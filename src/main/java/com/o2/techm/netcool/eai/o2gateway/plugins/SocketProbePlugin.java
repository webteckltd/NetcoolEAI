package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelper;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelperException;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;

/**
 * @author trenaman
 */
public class SocketProbePlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(SocketProbePlugin.class);
    
    public static final String NAME = "TCP"; 
    
    public static final String SOCKET_PROBE_PORT = "o2gateway.socketprobe.port"; 
    public static final String SOCKET_PROBE_PORT_DEF = "8001";
    
    public static final String SOCKET_PROBE_HOST = "o2gateway.socketprobe.host"; 
    public static final String SOCKET_PROBE_HOST_DEF = "localhost"; 
    
    private Socket socketProbe;
    private OutputStream socketOutput = null;
    
    private String port;
    private String host;


    /**
     * Class constructor
     */
    public SocketProbePlugin()
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
        ret.add(new VariableInfo(SOCKET_PROBE_PORT, SOCKET_PROBE_PORT_DEF));
        ret.add(new VariableInfo(SOCKET_PROBE_HOST, SOCKET_PROBE_HOST_DEF));
        return ret;
        }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        return ret;

    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#preInit(com.o2.osiris.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
        port = mgr.getString(SOCKET_PROBE_PORT); 
        host = mgr.getString(SOCKET_PROBE_HOST); 
    }
    
    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#postInit(com.o2.osiris.PluginManager)
     */
    public void postInit(PluginManager mgr) throws PluginException
    {
  
    	try {
			socketProbe = new Socket(host, Integer.parseInt(port));
			socketOutput  = socketProbe.getOutputStream();
			log.debug(" Socket connection established with  host =  " + host + " on port =  " + port + " Sucesfuly ");
		}
    	catch (Exception e)
        {
    		log.error( " Problem Establishing Socket connection  with  host =  " + host + " on port =  " + port,e);
            throw new PluginException(e);
        }
    }
    
    public void reConnect(){
    	if(null !=  socketProbe && socketProbe.isConnected() ){
    		try {
				socketProbe.close();
				socketProbe = new Socket(host, Integer.parseInt(port));
				socketOutput  = socketProbe.getOutputStream();
			}  catch (Exception e) {
				log.error( " Problem Re-Establishing Socket connection  with  host =  " + host + " on port =  " + port,e);
				e.printStackTrace();
			}
    	}else{
    		try {
    			socketProbe = new Socket(host, Integer.parseInt(port));
    			socketOutput  = socketProbe.getOutputStream();
    			log.debug(" Socket connection established with  host =  " + host + " on port =  " + port + " Sucesfuly ");
    		}
        	catch (Exception e)
            {
        		log.error( " Problem Establishing Socket connection  with  host =  " + host + " on port =  " + port,e);
                e.printStackTrace();
            }
    	}
    		
    	
    }
    
    
    /**
	 * Create CIC event and send to CIC
	 * @param strMsg
	 */
    long startTime;
    /**
	 * Create CIC event and send to CIC
	 * @param strMsg
	 */
    long currentTime;
    /**
	 * Create CIC event and send to CIC
	 * @param strMsg
	 */
    long elapsed;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	public Event  createEvent(String TYPE, String CLASS, String strMsg)
	{	
		Event event=new Event();	
		if(strMsg == null)
		{
			log.error("String value is null");
			return event;
		}
		/*else 
			log.debug("strMsg " + strMsg);*/
		
			
		
		try 
		{		
			String spMsg[] = strMsg.split("\\|");
			event.setLINE01(TYPE);
			event.setLINE02(CLASS);	
			if(spMsg.length >= 3)
			{
				event.setLINE03(spMsg[0]);  
				event.setLINE04(spMsg[1]); 
				event.setLINE05(spMsg[2]);
			}
			else
				event.setLINE03(strMsg);
			event.setLINE30("O2Gateway");
			startTime = System.currentTimeMillis();
			if (log.isDebugEnabled())
			{ 
				log.debug("Raising a CIC event with the socket probe.");
				log.debug("LINE01 = '" + event.getLINE01() + "'");
				log.debug("LINE02 = '" + event.getLINE02() + "'");
				log.debug("LINE03 = '" + event.getLINE03() + "'");
				log.debug("LINE04 = '" + event.getLINE04() + "'");
				log.debug("LINE05 = '" + event.getLINE05() + "'");
				log.debug("LINE06 = '" + event.getLINE06() + "'");
				log.debug("LINE07 = '" + event.getLINE07() + "'");
				log.debug("LINE08 = '" + event.getLINE08() + "'");
				log.debug("LINE09 = '" + event.getLINE09() + "'");
				log.debug("LINE10 = '" + event.getLINE10() + "'");			
			}
			
				SocketProbeHelper.raiseEvent(this,event);
						
		}
		catch (SocketProbeHelperException ex) 
		{		
			log.error("Error : ",ex);
			
		}
		finally
		{
			if(event != null)
			{
				currentTime = System.currentTimeMillis();
				elapsed = currentTime - startTime;
				log.debug("[" + event.getLINE01() + "] socketProbe.raiseEvent() took " + dateFormat.format(new Date(elapsed)));
				startTime = System.currentTimeMillis();		 			
				
			}
		}
		return event;
	}

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#shutdown()
     */
    public void shutdown() throws PluginException
    {
    }

    public Socket getSocketProbe()
    {
        return socketProbe;
    }
    
    public String getName()
    {
        return NAME;
    }

	public OutputStream getSocketOutput() {
		return socketOutput;
	}


    
    
}
