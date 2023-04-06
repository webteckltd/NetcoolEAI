package com.o2.techm.netcool.eai.o2gateway.monitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.netcool.O2GatewayReaderImpl;
import com.o2.techm.netcool.eai.o2gateway.netcool.O2GatewayWriterImpl;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelper;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelperException;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManager;
import com.o2.util.Semaphore;

/**
 * 
 * @author aademij1
 *
 * Class that sends thread status information to CIC
 */
public class MonitorTask extends TimerTask
{
    private static final String TYPE = "5";
    public static final String CLASS = "705";
     
    
    private static final Logger log = LoggerFactory.getLogger(MonitorTask.class);

    private boolean terminate = false; 
    private SocketProbePlugin socketProbePlugin; 
	private Semaphore taskSemaphore;	
	private boolean systemShuttingDown; 
	long startTime;
	long currentTime;
	long elapsed;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	PluginManager mgr;
	/**
	 * Class constructor that initalise the socket
	 * @param socketProbePlugin
	 */
	public MonitorTask(PluginManager mgr )
    {
		this.mgr=mgr;
		SocketProbePlugin socketProbePlugin =(SocketProbePlugin) mgr.getPlugins().get(SocketProbePlugin.NAME);
	    this.socketProbePlugin = socketProbePlugin;
	    
        systemShuttingDown = false;
        taskSemaphore = new Semaphore(1);
    }
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
	    try
        {
            taskSemaphore.acquire();
        }
        catch (InterruptedException e)
        {
            log.error("Unexpected InterruptedException while trying to get the bus semaphore.");
        }
        
        if (!systemShuttingDown)
        {
            doWork(); 
        }
        else {
            cancel();
        }

        taskSemaphore.release();
		
	}
	/**
	 * shutdown plugins
	 *
	 */
	public void shutdown()
    {
        try
        {
            log.info("Waiting for any Monitor task to complete...");
            taskSemaphore.acquire();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            log.warn("Unexpected InterruptedException while waiting for Monitor task to complete.");
        }
        
        systemShuttingDown = true;
        taskSemaphore.release();
    }
	/**
	 * Create CIC event and send to CIC
	 * @param strMsg
	 */
	public void createEvent(String strMsg)
	{	
		if(strMsg == null)
		{
			log.error("String value is null");
			return;
		}
		else 
			log.debug("Monitor Task Message = " + strMsg);
		
		Event event=new Event();		
		
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
			
				SocketProbeHelper.raiseEvent(socketProbePlugin,event);
					
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
	}
	
	/**
	 * 
	 *
	 */
	private void doWork()
	{
		log.info("MonitorTask.run");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		if (!terminate)
		{				 
			try {
				createEvent(SybaseManager.monitorME() + "|" + O2GatewayReaderImpl.monitorME() + "|" + O2GatewayWriterImpl.monitorME());
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
		}
			
	}
	
		
    public boolean isSystemShuttingDown()
    {
        return systemShuttingDown;
    }
    public void setSystemShuttingDown(boolean busHasShutDown)
    {
        this.systemShuttingDown = busHasShutDown;
    }
    public Semaphore getTaskSemaphore()
    {
        return taskSemaphore;
    }
    public void setTaskSemaphore(Semaphore busSemaphore)
    {
        this.taskSemaphore = busSemaphore;
    }
}
