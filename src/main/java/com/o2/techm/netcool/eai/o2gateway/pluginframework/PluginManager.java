package com.o2.techm.netcool.eai.o2gateway.pluginframework;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Abstract Class used by all plugins. Specifies all the plugins 
 * methods. Most of the methods are implemented
 * @author aademij1
 * 
 **/
public abstract class PluginManager
{
	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
    private static final String O2GW_DIR_PROPERTY = "o2gateway.dir";
    
    private Hashtable plugins;
    private Hashtable pluginClasses;
    private Hashtable variables;

    private ResourceBundle resourceBundle;
    private String resourceBundleName;
    boolean pluginCreationStageComplete;
	private Hashtable maps;
	private Hashtable filters;

	/**
	 * Class constructor spectifying the argument list and property file name
	 * @param args
	 * @param resourceBundleName
	 * @throws PluginException
	 */
    public PluginManager(String args[], String resourceBundleName)
    	throws PluginException
    {
    	log.debug(" in PluginManager() resourceBundleName = " + resourceBundleName);
    	this.resourceBundleName = resourceBundleName;
    	
    	System.out.println(this.resourceBundleName);
       // resourceBundle = ResourceBundle.getBundle(resourceBundleName);
        
        
    	try {
			resourceBundle = new PropertyResourceBundle(Files.newInputStream(Paths.get("conf/"+resourceBundleName+".properties")));
		} catch (IOException e) {
			log.error("Not able to load configuration file conf/"+resourceBundleName+".properties Failing early, failing loud.", e);
			e.printStackTrace();
			System.exit(1);
		}
        
        plugins = new Hashtable();
        pluginClasses = new Hashtable();
        variables = new Hashtable();
        pluginCreationStageComplete = false;
        variables.put("plugins", "");
        declareCoreVariables();
        registerPlugins();

		maps = new Hashtable();
		filters = new Hashtable();
    }

    public abstract void registerPlugins();
    public abstract void declareCoreVariables() throws PluginException;

    /**
     * Enables you to add variables
     * @param variableInfoVector
     * @throws PluginException
     */
    public void declareVariables(Vector variableInfoVector) throws PluginException
    {
        if (variableInfoVector != null)
        {
            Iterator iter = variableInfoVector.iterator();
            while (iter.hasNext())
            {
                VariableInfo info = (VariableInfo) iter.next();
                declareVariable(info.getName(), info.getDefaultValue());
            }
        }
    }

    /**
     * Overloaded method to add variables
     * @param name
     * @param defaultValue
     * @throws PluginException
     * @see declareVariables(Vector variableInfoVector)
     */

    public void declareVariable(String name, String defaultValue)
    throws PluginException
    {
        if (pluginCreationStageComplete) {
            throw new PluginException("Code is trying to declare variable *after* all plugins have been created.");
        }
        if (variables.containsKey(name)) {
        	log.error("VariableInfo '" + name + "' already declared.");
            //throw new PluginException("VariableInfo '" + name + "' already declared.");
        }
        variables.put(name, defaultValue);
    }

    /**
     * Method to register plugins
     * @param name
     * @param className
     */
    protected void registerPlugin(String name, String className)
    {
        pluginClasses.put(name, className);
    }

    /**
     * Stop all running plugins
     * @throws PluginException
     */
    public void shutdown()
    	throws PluginException
    {
        Enumeration lenum = plugins.elements();
        while (lenum.hasMoreElements()) {
            Plugin plugin = (Plugin) lenum.nextElement();
            log.debug("Shutting down plugin '" + plugin.getName() + "'");
            plugin.shutdown();
        }
    }

    /**
     * Load all required plugins
     * @throws PluginException
     */
    public void loadPlugins()
    	throws PluginException
    {
    	log.debug("in loadPlugins()");
    	String pluginList = resourceBundle.getString("plugins");
    	if (pluginList == null) {
    	    throw new PluginException("Error, the resource bundle does not contain a variable named \"plugins\"");
    	}

        StringTokenizer tokenizer = new StringTokenizer(pluginList, ", ");
        while (tokenizer.hasMoreTokens()) {
            String pluginName = tokenizer.nextToken();
            createPlugin(pluginName);
        }

        pluginCreationStageComplete = true;

        // update the variables from the resource bundle
        //
        readProperties();

        //
        // pass the variables to the plugins.
        Enumeration p_enum = plugins.elements();
        while (p_enum.hasMoreElements()) {
            ((Plugin) p_enum.nextElement()).preInit(this);
        }

        //
        // Second phase of initialisation.
        p_enum = plugins.elements();
        while (p_enum.hasMoreElements()) {
            ((Plugin) p_enum.nextElement()).postInit(this);
        }
        
        log.debug("loadPlugins() Completed Sucessfully");
    }

    /**
     * Enable other plugins before the calling Plugin 
     * @param pluginNames
     * @throws PluginException
     */
    public void declarePluginDependencies(Vector pluginNames) throws PluginException
    {
        if (pluginCreationStageComplete) {
            throw new PluginException("Code is trying to declare a plugin dependency *after* all plugins have been created.");
        }

        if (pluginNames != null)
        {
            Iterator iter = pluginNames.iterator();
            while (iter.hasNext())
            {
                createPlugin((String) iter.next());
            }
        }
    }

    /**
     *  Create a pluguin
     * @param name
     * @throws PluginException
     */
    private void createPlugin(String name)
    	throws PluginException
    {
        Plugin plugin = null;
        if (! plugins.containsKey(name)) {
            String className = (String) pluginClasses.get(name);
            if (className == null) {
                log.error("Unknown plugin '" + name + "'");
                throw new PluginException("Unknown plugin '" + name + "'");
            }

            try {
                //log.info("Creating plugin '" + name + "'");
                Class pluginClass = Class.forName(className);
                //log.info("Creating plugin '" + className + "'");
                log.info("Class Name:" + pluginClass.newInstance().getClass().getName());
                plugin = (Plugin) pluginClass.newInstance();
                plugins.put(name, plugin);
                declareVariables(plugin.declareVariables());
                declarePluginDependencies(plugin.declarePluginDependencies());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new PluginException(e);
            }
        }
        else {
            plugin = (Plugin) plugins.get(name);
        }
    }
    /**
     * Load Gateway Map configuration if any
     * @throws IOException
     * @see readProperties
     */
    private void readmap()throws IOException
	{
    	//InputStream s = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceBundleName + ".properties");
    	
        File file  = new File("conf/"+resourceBundleName + ".properties");
        FileInputStream  stream = new FileInputStream(file);
    	
    	BufferedReader in = new BufferedReader(new InputStreamReader(stream));
    	String text=null;
    	
    	log.debug("In readMap");
    	log.debug("*****************************");
    	//    	Create a pattern to match map 
    	Pattern	pmap = Pattern.compile("(o2gateway\\.)(netcool\\.)(map\\.)([a-zA-Z]*) *= *"); 
    	Pattern pmap_fields = Pattern.compile("'(\\w+)' *= *'(@*\\w+)',?");
    	
    	Pattern	pfilter = Pattern.compile("(o2gateway\\.)(netcool\\.)(\\w*\\.*)(filter) *= *");
    	Pattern pfilter_fields = Pattern.compile("'(\\w+)' *= *'(.*)'");
    	//Create a matcher with an input string
    	while((text = in.readLine()) != null)
	    {
	    	String mapname=null;
	    	String filtername=null;
	    	
	    	//log.debug("Read " + text);
	    	if(text.startsWith("#"))
	    		continue;
			Matcher m = pmap.matcher(text);
	    	Matcher m2 = pfilter.matcher(text);
		    if(m.find())
		    {
	    		mapname = m.group(4);
		    	Hashtable fields=new Hashtable();
				log.debug("mapname='" + mapname + "'");
		    			    	
		    	if(text.endsWith("{") || ((text = in.readLine()) != null && (text.startsWith("{")) ))
		    	{
		    		//log.debug("found {; so continue parsing");
		    		Hashtable result = new Hashtable ();

		    		while ((text = in.readLine()) != null)
					{		  
		    			if(text.trim().startsWith("#"))
		    	    		continue;
		    			if(text.endsWith("}") || (/*(text = in.readLine()) != null && */(text.startsWith("}")) ))
		    			{
		    				//log.debug("found closing }");
		    				maps.put(mapname,result);
		    				break;
		    			}
		    			else {
		    				//log.debug("Line Read :[" + text + "]");
		    				m = pmap_fields.matcher(text.trim());
		    				if(m.find())
		    			    {
		    					result.put( m.group(1),m.group(2));
		    					//log.debug(m.group(1)+" - "+m.group(2));
		    			    }
		    			}
			    	}
		    	}
		    	else
		    	{
		    		log.debug("Error, throw an exception");
		    	}
		    	
		    }
		    else if(m2.find())
		    {
		    	//log.debug("found filter entry");
		    	//boolean isPost=false;
		    	//if(m2.group(3).equalsIgnoreCase("post."))
		    	//{
		    	//	isPost=true;
		    	//}
		    	if(text.endsWith("{") || ((text = in.readLine()) != null && (text.startsWith("{")) ))
		    	{
		    		//log.debug("found {; so continue parsing filter");
		    		
		    		
		    		while ((text = in.readLine()) != null)
					{	
		    			Hashtable result = new Hashtable ();
		    			//log.debug("text" + text);
		    			if(text.trim().startsWith("#"))
		    			{
		    				log.debug("Skipping ....... text" + text);
		    				continue;
		    			}
		    	    		
						if(text.endsWith("}") || (text.startsWith("}")) )
		    			{
		    				//log.debug("found closing }");
		    				break;
		    			}
		    			else 
		    			{
		    				//log.debug("text" + text);
		    				m = pfilter_fields.matcher(text.trim());
		    				if(m.find())
		    			    {
		    					filtername=m.group(1);
			    				//log.debug("filtername=[" +filtername +"]");
			    			
			    				if(filters != null && filters.containsKey(filtername))
			    				{
			    					result = (Hashtable)filters.get(filtername);
			    					//log.debug("filter Already exist");
			    				}
			    				if(m2.group(3).equalsIgnoreCase("post."))
			    				{
			    					//log.debug("This is a post filter script");
			    					result.put("post",m.group(2));
		    					}
			    				else
			    				{
			    					//log.debug("This is a filter script");
			    					result.put( "script",m.group(2));
			    				}
			    				//log.debug("create new result table");
			    				filters.put(filtername,result);
			    		    }
		    			}
			    	}
		    	}
		    	
		    }
	    }
    	//DisplayHash(maps, "maps");
    	DisplayHash(filters, "filters");
    	log.debug("End of readMap");
	}
   
    /**
     * Display a Hashtable's content
     * @param mhash
     * @param mName
     */
    void DisplayHash(Hashtable mhash, String mName)
	{
    	log.debug("Hash " + mName);
    	log.debug("Keys	: " +  mhash.keySet());
    	
    	
    	Iterator iter = mhash.keySet().iterator();
        //Iterator iter = mhash.values().iterator();
        while (iter.hasNext()) 
        {
        	String ename = (String)iter.next();
        	log.debug("Name:[" + ename);
	    	Hashtable lhm = (Hashtable)mhash.get(ename);
	    	log.debug("Result Keys	: " +  lhm.keySet());
	    	Iterator iter2 = lhm.values().iterator();
	    	while (iter2.hasNext()) {
	    		
	    		Object sthing =iter2.next(); 
	    		try {
	    			if(sthing.getClass().isInstance(""))
	    			{
	    				log.debug("result Value	: " + (String)sthing);
	    			}
	    			else
	    			{
	    				if(sthing.getClass().isInstance(mhash))
	    				{
	    					log.debug("Calling DisplayHash Recursively");
	    					DisplayHash((Hashtable)sthing, mName);
	    				}
	    			}
	    		}
	    		
	    		catch (Exception ex) {
	    			log.error(" cannot create instance",ex);
	    				
	    		}    		
	    	}
	    }
	    
	}
    
    /**
     * Read property file
     *
     */
    private void readProperties()
    {
		try
		{
			// Check that the peroperties file only contains variables we know about.
			//
			Enumeration resourceKeys = resourceBundle.getKeys();
			log.info("Start of Configuration .............");
	    	
			while (resourceKeys.hasMoreElements())
			{
			    String key = (String) resourceKeys.nextElement();
			    if(key.startsWith("{") || key.endsWith("}") || (key.startsWith("'") && resourceBundle.getString(key).startsWith("'"))) 
			    {
			    	//ignore
			    }
			    else if (! variables.containsKey(key)) {
			        log.warn("Properties file '" + resourceBundleName + "' contains an unknown/unused variable '" + key + "'.");
			    }
			 }

			// Now use the properties file to override the defaults.
			//
			log.debug(" variables  =  " + variables.toString());
	        Enumeration variableKeys = variables.keys();
	        while (variableKeys.hasMoreElements()) {
	            String variable = (String) variableKeys.nextElement();
	            log.trace("variable =  " + variable);
	            String value = null;
	            try {
	                value = resourceBundle.getString(variable).trim();
	                variables.put(variable, value);
	            }
	            catch (java.util.MissingResourceException ex) {
	                // ignore!
	            }
	        }
	        readmap();
		}
		catch (RuntimeException e)
        {
            log.warn(e.getMessage());
            log.warn("Cannot find resource bundle '" + resourceBundleName + "'; using default configuration values.",e);
        }
		catch (Exception ex)
        {
			log.warn(ex.getMessage());
        }
        
        if (log.isDebugEnabled()) {
            Enumeration variableKeys = variables.keys();
	        List keys = new ArrayList();
	        while (variableKeys.hasMoreElements()) {
	            keys.add((String) variableKeys.nextElement());
	        }
	        java.util.Collections.sort(keys);
	        Iterator iter = keys.iterator();
	        while (iter.hasNext())  {
	            String name= (String) iter.next();
	            String value = (String) variables.get(name);
	            if(!name.startsWith("'") && !value.startsWith("'"))
	                log.debug(name + " = '" + value + "'");
	        }
        }
        log.info("End of Configuration .............");
    }

    public String getString(String name)throws PluginException
    {
        String ret = (String) variables.get(name);
        if (ret == null) {
        	log.error("Variable '" + name + "' unknown; returning a null value.");
        }
        //else
       // 	log.debug("ret='" + ret + "'");
        return ret;
    }

    public int getInt(String name) throws PluginException {
        try {
            return Integer.parseInt(getString(name));
        }
        catch (NumberFormatException e) {
            throw new PluginException("Variable '" + name + "' should be an integer value.");
        }
    }

    public SimpleDateFormat getSimpleDateFormat(String name) throws PluginException
    {
        try {
        	return new SimpleDateFormat(getString(name));
        }
        catch (IllegalArgumentException e) {
            throw new PluginException("Variable '" + name + "' cannot be parsed as a date format object.");
        }
    }

    public ResourceBundle getResourceBundle()
    {
        return resourceBundle;
    }


    public Hashtable getVariables()
    {
        return variables;
    }

    public Hashtable getPlugins()
    {
        return plugins;
    }
	/**
	 * @return Returns the filters.
	 */
	public Hashtable getFilters() {
		return filters;
	}
	/**
	 * @param filters The filters to set.
	 */
	public void setFilters(Hashtable filters) {
		this.filters = filters;
	}
	/**
	 * @return Returns the maps.
	 */
	public Hashtable getMaps() {
		return maps;
	}
	/**
	 * @param maps The maps to set.
	 */
	public void setMaps(Hashtable maps) {
		this.maps = maps;
	}
}
