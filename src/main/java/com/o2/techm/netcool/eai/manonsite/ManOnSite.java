package com.o2.techm.netcool.eai.manonsite;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.o2.techm.netcool.eai.manonsite.SocketProbe.SocketProbeClient;
import com.o2.techm.netcool.eai.manonsite.broker.BrokerConnection;
import com.o2.techm.netcool.eai.manonsite.broker.BrokerConnectionFactory;
import com.o2.techm.netcool.eai.manonsite.common.CommunicateWithTT;
import com.o2.techm.netcool.eai.manonsite.common.Configuration;
import com.o2.techm.netcool.eai.manonsite.common.Constants;
import com.o2.techm.netcool.eai.manonsite.jdbc.sybase.SyPoolConnectionManager;
import com.o2.techm.netcool.eai.manonsite.mapinfo.MapInfo;
import com.o2.techm.netcool.eai.manonsite.mrs.MRSRequestThreadPool;
import com.o2.techm.netcool.eai.manonsite.mrs.MRServiceRESTImp;
import com.o2.techm.netcool.eai.o2gateway.O2GatewayMgrPluginManager;
import com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin;
import com.o2.util.WorkQueue;

@Component
public class ManOnSite
{
	private static final Logger log = LoggerFactory.getLogger(ManOnSite.class);
    private static CommunicateWithTT ttServer = null;
    private static SocketProbeClient ss;
	private static MRSRequestThreadPool threadPool=null;
	private O2GatewayMgrPluginManager mgr;  
    private int threadPoolSize = 0;
  
	@Autowired
	private MRServiceRESTImp mrService;

    public void initManOnSiteApp()  
	{
    	log.info(" STARTING MAN ON SITE COMPONENT");
           try{
    			runAsServer();
    		}
    		catch (Exception e)
    		{
    			log.error("Error trying to run ManOnSite server; details: ",e);
    			System.exit(1); 
    		}
    		catch (Error e) {
    			log.error("Java language error: " ,e); 
    			System.exit(1); 
    		}
           
           log.info(" ALL MAN ON SITE COMPONENTS STARTED SUCESSFULLY");
    		
        } 
        
 
    

    private static void createCommTT() throws ManOnSiteException
	{
    	try
		{
    		ttServer = new CommunicateWithTT();
    		ttServer.init();
    	}
    	catch (Exception e)
		{
    		 log.debug("",e);
    		 throw new ManOnSiteException("initialising the trouble ticket manager " + e.getMessage());
    	}
   	}
	

    private  void runAsServer() throws Exception
    {
       log.debug("Creating WorkQueues. ");
        WorkQueue workQueue = new WorkQueue();
        WorkQueue workQueueCIC = new WorkQueue();
     
        
        try
        {
            threadPoolSize = Integer.parseInt(Configuration.getValue(Configuration.THREAD_POOL_SIZE));
        } catch (RuntimeException e1)
        {
            log.error("Variable '" + Configuration.THREAD_POOL_SIZE + "' should be an integer. Failing early, failing loud.",e1);
            System.exit(1); 
        }
                
        log.debug("Creating BrokerRequestThreadPool of size " + threadPoolSize + ". ");
        BrokerConnection brokerConnection=BrokerConnectionFactory.newInstance(getBrokerconn()); 
         
        log.info("Creating a pool of JDBC connections to Sybase ...");
		
    	String dbUrl = Configuration.getValue(Configuration.SYBASE_JDBC_URL);
    	String dbUsername = Configuration.getValue(Configuration.SYBASE_JDBC_LOGIN);
    	String dbPassword = Configuration.getValue(Configuration.SYBASE_JDBC_PASSWORD);
    	
        if(!SyPoolConnectionManager.isValid(Constants.IDUC_POOL)) 
        {
        	Properties props= new Properties(); 
			props.put( "password",  dbPassword );
			props.put( "user", dbUsername );
			props.put( "APPLICATIONNAME" , "MANONSITE" ); 
        	 SyPoolConnectionManager.createPool(
        			Constants.IDUC_POOL, Constants.JDBC_DRIVER,dbUrl,dbUsername,dbPassword,props);
        }
        threadPool = new MRSRequestThreadPool(
		                "MRSRequestThreadPool", threadPoolSize, workQueue, brokerConnection,workQueueCIC);
		
        log.debug("Starting the MRSRequestThreadPool...."); 
        threadPool.start();       
        
        try
        {
        	createCommTT(); 
        }catch(ManOnSiteException me)
        {
        	log.debug("Problem Creating Connection with Enrichment DB ",me);
        	/**
        	 * Ravi : uncomment this later post testing 
        	 */
        	//throw me;
        }
        
        mrService.setWorkQueue(workQueue);
        mrService.setWorkQueueCIC(workQueueCIC);
        mrService.setTtServer(ttServer);
	
        MapInfo mapinfo = null;
		try {
			mapinfo = new MapInfo();
		} catch (Exception e) {
			log.debug("Problem Creating Connection with MapInfo DB ",e);
			/**
        	 * Ravi : uncomment this later post testing 
        	 */
        	//throw e;
		}
        ss = new SocketProbeClient((SocketProbePlugin)(mgr.getPlugins()).get(SocketProbePlugin.NAME),workQueueCIC,mapinfo);
		ss.start();
        }

 
    /**
     * @return
     */
   
    
    private  String getBrokerconn()
    { 
         return Configuration.WINTELBROKER;
    }
    
    
    public String getStatusCheck(){
    	StringBuffer statusBuff  = new StringBuffer();
		if(threadPool.activeCount() <= 0)
			statusBuff.append("No MRSRequestThreads are active");
        else if(threadPool.activeCount() < threadPoolSize )
        {
        	statusBuff.append((threadPoolSize - threadPool.activeCount()) + " MRSRequestThreads have died");        	    	 
        }
        else{
        	statusBuff= new StringBuffer("All MRSRequestThreads are Alive");
        }
		
		return  statusBuff.toString();
    }
    
    
    public  void shutdown()
    { 
    	log.info("Invoking shutdown on Man On Site component ");
        if(threadPool != null)	threadPool.terminateAll(); 
        if(ss != null) ss.shutdown();
        if(ttServer != null) ttServer.shutdown();
    }

	public O2GatewayMgrPluginManager getMgr() {
		return mgr;
	}

	public void setMgr(O2GatewayMgrPluginManager mgr) {
		this.mgr = mgr;
	}
    
    
}
