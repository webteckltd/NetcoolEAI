package com.o2.techm.netcool.eai.manonsite.mrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.o2.techm.netcool.eai.manonsite.ProxyFactory;
import com.o2.techm.netcool.eai.o2gateway.sybase.thread.ThreadPool;
import com.o2.util.WorkQueue;
import com.o2.techm.netcool.eai.manonsite.broker.BrokerConnection;

public class MRSRequestThreadPool extends ThreadPool 
{
	private static final Logger log = LoggerFactory.getLogger(MRSRequestThreadPool.class); 
	
	private WorkQueue workQueue = null;
	private WorkQueue workQueueCIC = null;
	//private ProxyFactory cellSiteAlarmServiceFactory = null;
	private BrokerConnection brokerConnection; 

	public MRSRequestThreadPool(String name, 
                                   int poolSize,
								   WorkQueue workQueue,
								   BrokerConnection brokerConnection,WorkQueue workQueueCIC
	)
	{
        super(name);
        this.workQueue = workQueue;
        this.brokerConnection = brokerConnection;
        this.workQueueCIC = workQueueCIC;
        
        int i = 0;

        for (i = 0; i < poolSize; i++)
        {        	
                this.addThread(new MRSRequestThread(this,
                        "WorkerThread" + i, brokerConnection));
        }

        if (i > 0)
        {
            log.debug("A BrokerRequestThreadPool of " + i + " created");
        }
        else
        {
            log.error("BrokerRequestThreadPool creation failed");
        }

    }

	public WorkQueue getWorkQueue(){
		return this.workQueue;
		
	}
	public WorkQueue getWorkQueueCIC(){
		return this.workQueueCIC;
		
	}
}
