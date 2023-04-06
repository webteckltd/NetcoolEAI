package com.o2.techm.netcool.eai.o2gateway.netcool;

import java.lang.String;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.monitor.MonitorTask;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNetcoolEvent;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayProcessEventFailure_Exception;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayReader;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.plugins.ReaderPlugin;
import com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelper;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelperException;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManager;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManagerException;
 
 
/**
 * com.o2.o2gateway.netcool.NetcoolGatewayReaderImpl
 */
public class O2GatewayReaderImpl implements NetcoolGatewayReader {

	private int hbrate;
	private int ebufSz;
	/*private LinkedList readerEventList;*/
	private static int currentReaderevts = 0;
	private Vector gwAuthors;
	boolean terminate=false;
	protected static final String readerThrGrpStr = "ReaderGroup";
	private static ThreadGroup readerThreadGroup   = new ThreadGroup(readerThrGrpStr);;
	private static Thread tpinger = null;
	
	/**
	 * Terminte all Reader threads
	 *
	 */	
	void terminateThreads()
    {
		terminate = true;
		try {
			if(tpinger != null && tpinger.getThreadGroup() != null) 
				tpinger.getThreadGroup().interrupt();
			SybaseManager.getSybaseManager(mgr).stopReaderThreads();
			
		} catch (SybaseManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }
	 /**
	 * @author aademij1
	 *
	 * This is a pinger nested java Class that  :-
	 * a) Sends an heartbeat alarm for the reader
	 * b) probably checks if the client is still there
	 * c)
	 */
	class Pinger implements Runnable
	{
		
		public void run()
		{
			try
			{
				threadsStarted++;
				do
				{
					log.debug(ReaderPlugin.NAME +  " I am alive");   
					Thread.sleep(Integer.parseInt(mgr.getString(NetcoolConnector.GATEWAY_HEARTBEAT)) * 1000);
				} while(!terminate && !tpinger.isInterrupted());
			}
			catch(InterruptedException ie)
			{
				//log.debug("READER Exception : " + ie);
				ie.printStackTrace();             
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PluginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Pinger construtor
		 */
		Pinger(){}
    }
	PluginManager mgr;
	private String jdbcUrl;
    private String jdbcLogin;
    private String jdbcPassword;
    private SimpleDateFormat sybaseDateFormat;
    private SimpleDateFormat cicDateFormat;
    private int locale;
    private static int threadsStarted=0;
	private SocketProbePlugin socketProbePlugin;
    //private SybaseManager sybaseManager;
    
    /**
     * Class constructor
     * @throws SybaseManagerException 
     */
	public O2GatewayReaderImpl(PluginManager mgr) throws SybaseManagerException
	{		
		this.mgr=mgr;
		try {
			hbrate =   Integer.parseInt(mgr.getString(NetcoolConnector.GATEWAY_HEARTBEAT));
			ebufSz =   Integer.parseInt(mgr.getString(NetcoolConnector.GATEWAY_EVENT_BUF_SZ));
			socketProbePlugin = (SocketProbePlugin) mgr.getPlugins().get(SocketProbePlugin.NAME);
		} 
		catch (NumberFormatException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PluginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		
		try {
			log.debug(" Starting Reader Threads ");
			SybaseManager.getSybaseManager(mgr).startReaderThreads();
			log.debug(ReaderPlugin.NAME + " Calling pinger Thread");
			tpinger = new Thread(readerThreadGroup,new Pinger(), "Reader Pinger Thread");
			tpinger.start();
		} catch (SybaseManagerException e1) {
			// TODO Auto-generated catch block
			log.error("ERROR:",e1);
			throw e1;
		}		
	}
	/**
	 * Checks Thread status
	 * @return
	 * @throws Exception
	 */
	public static String monitorME() throws Exception
    {
		StringBuffer str= null;
		if(readerThreadGroup.activeCount() <= 0)
			str = new StringBuffer("No Reader threads are active");
        else if(readerThreadGroup.activeCount() < threadsStarted )
        {
        	str = new StringBuffer((threadsStarted - Thread.activeCount()) + " Reader threads have died");        	 
        	if(tpinger != null && !tpinger.isAlive())
        		str.append(" :" + tpinger.getName() + " is dead");        	 
        }
        else
        	str= new StringBuffer("All Reader threads are Alive");
        return str.toString();
    }
	
	public void shutdown()
	{		
		terminateThreads();
	}
	private static final Logger log = LoggerFactory.getLogger(O2GatewayReaderImpl.class);
    /**
     * event
     * 
     * @param aEvent (NetcoolGatewayNetcoolEvent)
     * @return String
     */
	public String event(NetcoolGatewayNetcoolEvent aEvent) throws NetcoolGatewayProcessEventFailure_Exception, RemoteException {
		if(aEvent != null)
        {
            try {
				if(ebufSz != -1 && SybaseManager.getSybaseManager(mgr).getReaderQueue().size() >= ebufSz)
				{
					log.debug(ReaderPlugin.NAME + " Too many events ... event discarded"); 
					throw new NetcoolGatewayProcessEventFailure_Exception("Too many events ... event discarded",100);
				}			
                SybaseManager.getSybaseManager(mgr).getReaderQueue().enqueueWork(aEvent);
                currentReaderevts++;
            log.debug(ReaderPlugin.NAME + " Received an event from Client, Events: " + SybaseManager.getSybaseManager(mgr).getReaderQueue().size() + "(Total: " + currentReaderevts + ").");
            } catch (SybaseManagerException e) {
				// TODO Auto-generated catch block
				log.error("SybaseManager Exception:", e);
				String strMsg=e.getMessage();
				
					Event event = socketProbePlugin.createEvent("1", MonitorTask.CLASS,   strMsg);
					try {
						SocketProbeHelper.raiseEvent(socketProbePlugin,event);
					} catch (SocketProbeHelperException e1) {
						// TODO Auto-generated catch block
						log.error("Socket Error",e1); 
					}
					
				
			}
        }
        return "Success";
	}
	
    
}
