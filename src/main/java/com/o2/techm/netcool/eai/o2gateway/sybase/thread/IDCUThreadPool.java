package com.o2.techm.netcool.eai.o2gateway.sybase.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.util.WorkQueue;

 

public class IDCUThreadPool extends ThreadPool 
{
	private static final Logger log = LoggerFactory.getLogger(IDCUThreadPool.class); 
	
	private WorkQueue workQueue = null;
	private WorkQueue workQueueCIC = null; 

	public IDCUThreadPool(String name, IDUCInfo[] iducIns,PluginManager mgr,WorkQueue writerQueue)
		{
		super(name);
		int poolSize = iducIns.length;       
        int i = 0;          
        for(i=0; i < poolSize; i++)
        {      	String thname = (String)iducIns[i].iname; 
                this.addThread(new IDUCThread(this,thname,iducIns[i],mgr,writerQueue)); 
        }

        if (i > 0)
        {
            log.debug("A IDCUThreadPool of " + i + " created");
        }
        else
        {
            log.error("IDUCThreadPool creation failed");
        }

    }

	public WorkQueue getWorkQueue(){
		return this.workQueue;
		
	}
	public WorkQueue getWorkQueueCIC(){
		return this.workQueueCIC;
		
	}
}
