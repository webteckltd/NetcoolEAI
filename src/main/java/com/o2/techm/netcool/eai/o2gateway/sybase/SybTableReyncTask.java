package com.o2.techm.netcool.eai.o2gateway.sybase;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManager;
import com.o2.util.Semaphore;

/**
 * 
 * @author aademij1
 *
 *Retrieves user information from CIC 
 */
public class SybTableReyncTask extends TimerTask
{
	private static final Logger log = LoggerFactory.getLogger(SybTableReyncTask.class);

    private boolean terminate = false; 
	private Semaphore taskSemaphore;	
	private boolean systemShuttingDown;
	private PluginManager mgr;

	/**
	 * Class constructor that creates the SybTableReyncTask Object
	 *  
	 */
	public SybTableReyncTask(PluginManager mgr )
    {  
	    systemShuttingDown = false;
        taskSemaphore = new Semaphore(1);
        this.mgr = mgr;
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
            log.info("Waiting for any SybTableReyncTask task to complete...");
            taskSemaphore.acquire();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            log.warn("Unexpected InterruptedException while waiting for SybTableReyncTask task to complete.");
        }
        
        systemShuttingDown = true;
        taskSemaphore.release();
    }
	 
	
	/**
	 * 
	 *Method that gets run by the TimerTask Schedule
	 */
	private void doWork()
	{
		log.info("SybTableReyncTask.run");
		 
		if (!terminate)
		{				 
			try {
				SybaseManager smgr = SybaseManager.getSybaseManager(mgr);
				
				if(smgr != null){
					
					log.debug(" calling .. smgr.ResynchUserTable() " );
					smgr.ResynchUserTable();
					log.debug(" smgr.ResynchUserTable()  called sucessfully " );
				}
			else{
				log.error("Sybase Manager has not been created as yet");
			}
			
			} catch (SybaseManagerException e) {
				// TODO Auto-generated catch block
				log.error("ERROR",e); 
				try {
					mgr.shutdown();
				} catch (PluginException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
				//Get User ID Conversion
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
