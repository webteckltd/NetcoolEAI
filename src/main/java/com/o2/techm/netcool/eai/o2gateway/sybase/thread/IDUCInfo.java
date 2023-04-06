/*
 * Created on 22-Mar-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.techm.netcool.eai.o2gateway.sybase.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.util.WorkQueue;

/**
 * @author aademij1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IDUCInfo {
	private static final Logger log = LoggerFactory.getLogger(IDUCInfo.class); 
		String iname;
	    String query;	  
	    String action;
	    int	actionFlag; 
	    WorkQueue iducQueue;
	/**
	 * 
	 */
	public IDUCInfo(String iname,String action,int actionFlag,String query,WorkQueue iducQueue) {
		this.iname= iname;
		this.action=action;
		this.actionFlag = actionFlag;
		this.query = query;	 
		this.iducQueue =iducQueue;		 
	}
	
}
