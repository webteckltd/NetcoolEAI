package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Timer;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybTableReyncTask;
  
/**
 * @author AAdemij1
 * 
 */
public class SybTableLoadPlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(SybTableLoadPlugin.class);
    
    public static final String NAME = "SYB_RESYNC"; 
    
    public static final String SYSBASE_TABLE_TIMER = "o2gateway.table.resync.timer"; 
    public static final String SYSBASE_TABLE_TIMER_DEF = "3600";      
     
    private int resyncSleep; 
    private SybTableReyncTask resyncTask;
    private Timer sTimer;
    /**
     * Class constructor
     */
    public SybTableLoadPlugin()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
        Vector ret = new Vector();
        ret.add(new VariableInfo(SYSBASE_TABLE_TIMER, SYSBASE_TABLE_TIMER_DEF)); 
        return ret;
        }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        //ret.add(ArtixPlugin.NAME);
        return ret;

    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#preInit(com.o2.o2gateway.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
    	resyncSleep = Integer.parseInt(mgr.getString(SYSBASE_TABLE_TIMER)); 
    }
    
    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#postInit(com.o2.o2gateway.PluginManager)
     */
    public void postInit(PluginManager mgr) throws PluginException
    {
        resyncTask= new SybTableReyncTask(mgr); 
        
    	sTimer = new Timer();         
    	sTimer.schedule(resyncTask, 0, resyncSleep * 1000);        
    }
    

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#shutdown()
     */
    public void shutdown() throws PluginException
    {
    }
     
    
    public String getName()
    {
        return NAME;
    }
}
