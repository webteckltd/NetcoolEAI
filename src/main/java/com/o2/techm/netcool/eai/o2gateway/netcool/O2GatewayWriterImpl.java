package com.o2.techm.netcool.eai.o2gateway.netcool;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayGatewayClient;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPair;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPairList;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNetcoolEvent;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayProcessEventFailure_Exception;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayWriter;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.plugins.WriterPlugin;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManager;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManagerException;
import com.o2.util.WorkQueue;


/**
 * com.o2.o2gateway.netcool.NetcoolGatewayWriterImpl
 */
public class O2GatewayWriterImpl implements NetcoolGatewayWriter{

	private static final String CALLBACK = "CALLBACK";
	private static final String CLIENTRETRIES = "RETRIES";
	private static final Logger log = LoggerFactory.getLogger(O2GatewayWriterImpl.class);
	protected static final String writerThrGrpStr = "WriterGroup";
	private static ThreadGroup writerThreadGroup = new ThreadGroup(writerThrGrpStr);
	private static int threadsStarted=0;
	boolean terminate=false;
	private List gatewayClients=new LinkedList();
	ListIterator itr = null;
	
	
	/**
	 * @author aademij1
	 *
	 * Flag to terminate all writer threads
	 */
	void terminateThread()
    {
        terminate = true;
    }
	/**
	 * @author aademij1
	 *
	 * This is a pinger nested java Class that  :-
	 * a) Sends an heartbeat alarm for the writer
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
	         	//NetcoolGatewayNetcoolEvent anOtherEvent = null;
	         	int oldNum, oldTotal, cnter=0;
	         	do
	         	{         		
	         		log.debug(WriterPlugin.NAME +": I am alive");
	         		
	         		oldNum = SybaseManager.getSybaseManager(mgr).getWriterQueue().size() ;
	         		oldTotal=total_writer_evt;
	         		
	         		log.debug("no of " + WriterPlugin.NAME + " clients connected : " +   getGatewayClients().size() + " , no of events in Writer Queue: " + SybaseManager.getSybaseManager(mgr).getWriterQueue().size() + ", Total Writer events sent to clients " + total_writer_evt);
	         		Thread.sleep(Integer.parseInt(mgr.getString(NetcoolConnector.GATEWAY_HEARTBEAT)) * 1000);
	         		
	         		if(oldNum > 0 && oldTotal == total_writer_evt && oldNum > SybaseManager.getSybaseManager(mgr).getWriterQueue().size())	         			
	         		{
	         			cnter+=Integer.parseInt(mgr.getString(NetcoolConnector.GATEWAY_HEARTBEAT));
	         			String strMsg = "In the last " + cnter + " seconds the total number of events sent to client(s) has remained the same AND " + 
     					"the number of events in the writer queue has also increased please investigate. This might indicate events are NOT been sent to OSIRIS";
	         			log.warn(strMsg);	         			 		
	         		}
	         		else
	         			cnter=0;
	         	} while(!terminate);
	         }
	         catch(InterruptedException ie)
	         {
	         	//log.debug(WriterPlugin.NAME + ie);
	            ie.printStackTrace();             
	         } catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PluginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SybaseManagerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       }
	
	      Pinger(){}
	    }
	 
	PluginManager mgr;
	Vector clients;
	private static Thread WriMain = null;
	private static Thread WriPing = null;
	/*private LinkedList writerEventList;*/
	private int total_writer_evt=0; //Total Number of Alarms already Sent	 
	
	/**
	 * Checks Thread status
	 * @return
	 * @throws Exception
	 */
	public static String monitorME() throws Exception
    {		
		StringBuffer str= null;
		if(Thread.activeCount() <= 0)
			str = new StringBuffer("No Writer threads are active");		
        else if(writerThreadGroup.activeCount() < threadsStarted )
		{
			str= new StringBuffer((threadsStarted - writerThreadGroup.activeCount()) + " Writer threads have died");
			writerThreadGroup.list();
			if(WriPing != null && !WriPing.isAlive())
				str.append(": " + WriPing.getName() + " is dead");
			
			if(WriMain != null && !WriMain.isAlive())
				str.append(": " + WriMain.getName() + " is dead"); 
		}
        else
        	str= new StringBuffer("All Writer threads are Alive");
		return str.toString();
    }
	
	 /**
     * Class constructor
	 * @throws PluginException
     */
	public O2GatewayWriterImpl(PluginManager mgr) throws PluginException, SybaseManagerException
	{
		boolean testfilter=false;
		this.mgr=mgr;
		synchronized(gatewayClients) 
    	{
			itr = getGatewayClients().listIterator();
    	}
	   /*writerEventList = new LinkedList();*/
		writerThreadGroup = new ThreadGroup(writerThrGrpStr);
		if(mgr == null)
			log.debug("******** mgr is null NOW! ************");

		log.debug("Calling pinger Thread");
		WriPing = new Thread(writerThreadGroup,new Pinger(), "Writer Pinger Thread");
		WriPing.start();
		 
		try {
			SybaseManager.getSybaseManager(mgr).startWriterThreads();
			testfilter=true;
			log.debug("Calling writerMain");
			WriMain = new Thread(writerThreadGroup,new writerMain(), "Writer Impler Thread");
			WriMain.start();
		} catch (SybaseManagerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			if(!testfilter)
				throw new PluginException(e1);
			else
				throw e1;
		}
		
	}
	/**
	 * 
	 * @author aademij1
	 *
	 * Thread that sends events to client(s)
	 */
	class writerMain implements Runnable
	{

		private static final String FAILURETIMESTAMP = "FAILURETIMESTAMP";
		NetcoolGatewayNetcoolEventComparator evtComparator = new NetcoolGatewayNetcoolEventComparator();
		String startDate=null;
		writerMain()		
		{			 
			startDate=new SimpleDateFormat(NetcoolConnector.CIC_DATE_FORMAT_DEF).
			      format(new java.util.Date(System.currentTimeMillis()));			
		}
		public void run()
		{
			try
			{
				threadsStarted++;
				NetcoolGatewayNetcoolEvent anOtherEvent = null;
				WorkQueue writerQ =  SybaseManager.getSybaseManager(mgr).getWriterQueue(); 				 
				int clientSize=0,wsize=0;
				
				while(!terminate)
				{		
					 
					clientSize = getGatewayClients().size();
					if(clientSize <= 0)
					{
						if(writerQ.size() > 0)
							log.error("There are " + writerQ.size() + " event(s) in the Queue; but no client connection");
//						log.debug("Sleep for 5 seconds");
						Thread.sleep(SybaseManager.PAUSE * 1000);            		 
						continue;
					}
//					Queue is not empty and atleast one client 
					do
					{
						//Get event from Netcool
						anOtherEvent = (NetcoolGatewayNetcoolEvent)writerQ.dequeueWork();
						wsize = writerQ.size(); 
						itr = getGatewayClients().listIterator();
					    while(itr.hasNext())
						{	
							Properties clientProps = (Properties)itr.next();
							NetcoolGatewayGatewayClient callback = (NetcoolGatewayGatewayClient)clientProps.get(CALLBACK);
							try
							{								
								 if(anOtherEvent != null)
								 {									 
									callback.takeEvents(anOtherEvent );
									if(!itr.hasNext())
										total_writer_evt++;
									clientProps.remove(writerMain.FAILURETIMESTAMP);
									clientProps.setProperty(CLIENTRETRIES,  "0"); 
								 }
								else
								{
								    log.error("Dequeued Event is null");
								    break;
								}
							}   
							catch(RemoteException re)
							{
								//re.printStackTrace(); 
								int retryPerClient =Integer.parseInt((String)clientProps.getProperty(CLIENTRETRIES));
								clientProps.setProperty(CLIENTRETRIES,  ++retryPerClient + ""); 
								log.error("Since " + startDate + " there has been "  + retryPerClient+ " Remote Exception ",re);
								try
								{
									String causeStr =re.getCause().getMessage();
									
									if(causeStr.matches(".*Could not connect.*") ||causeStr.matches(".*Connection refused.*"))
									{
										/**
										 *  For a "ReceiveReply Receive timeout" Exception It seems that the OSIRIS receives the case in the first place 
										 *  regardless but only in 80% of the time. The short term solution is 
										 *  to remove the buffering of alarms all together 
										 *  because this is also causing headache for NMC will the creation of multiple tickets.
										 *  Long Term Solution would be to implement so king of Cache mechanism when
										 *  Then allows us to compare the case in the cache with response from  the Reader
										 *  within a specified time period.
										 */
										
										if(writerQ.binarySearch(anOtherEvent, evtComparator) < 0)
											writerQ.enqueueWorkFirst(anOtherEvent);
										
										log.error("Connection Refusted, removing Client" + retryPerClient + " times",re);										
										itr.remove();
										clientSize = getGatewayClients().size(); 
									}
									else
									{										 	
										String failtime = clientProps.getProperty(writerMain.FAILURETIMESTAMP);
										if(failtime == null)
										{
											failtime = Long.toString(System.currentTimeMillis());
										}
										clientProps.setProperty(writerMain.FAILURETIMESTAMP,failtime  );
										log.info("Pausing for 5 seconds");										
										Thread.sleep(5 * 1000);
									}
								}
								catch(Exception e)
								{
									log.error("Error:",e);
								}								
								/*else
									log.warn("But event is still sent to the Clients, just did not recieve an acknowledgemet from Client");*/
								//Cheating a bit as the k-- will happen once it returns to the for .. loop								
							}
							catch(Exception e)
							{
								log.error(" Client callback issue",e); 
							}
						}
						 
						if(terminate)
							log.warn("Shutdown signal recieved, " + wsize + " left to progress");
					}while(wsize > 0 &&  clientSize > 0);				
				}  
			}
			catch(InterruptedException ie)
			{
				//log.debug(WriterPlugin.NAME + ie);
				//ie.printStackTrace();             
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (SybaseManagerException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}

    /**
     * netcool_connect
     * 
     * @param: aClient (com.iona.schemas.references.Reference)
     * @return: boolean
     */
    public boolean netcool_connect(NetcoolGatewayGatewayClient callback)  {
    	// User code goes in here.
    	log.debug("WriterImpl::netcool_connect invoked");
    	boolean isable=false;        
        try
        {   
        	Properties aProp = new Properties();
        	aProp.put(O2GatewayWriterImpl.CALLBACK,callback);
        	aProp.setProperty(O2GatewayWriterImpl.CLIENTRETRIES, "0");          
        	synchronized(gatewayClients) 
        	{
        		itr.add(aProp);
        	}
        	isable=true; 
        }
        catch (Exception ex)
        {
        	log.error("Exception",ex);
        } 
        finally
        {
        	log.info("There are now " + gatewayClients.size() + " clients in the list" );
        }
        return isable;
    }
    
    public void shutdown()
	{ 
		log.warn("Interrupting "  + writerThrGrpStr + " Thread ...");
		if(WriPing != null && WriPing.getThreadGroup() != null)WriPing.getThreadGroup().interrupt();
		else if(WriMain != null) WriMain.getThreadGroup().interrupt();		 
		terminateThread();
		try {
			SybaseManager.getSybaseManager(mgr).stopWriterThreads();
		} catch (SybaseManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    /**
     * sendEvent
     * 
     * @param: aEvent (NetcoolGatewayNetcoolEvent)
     * @return: boolean
     */
    public boolean sendEvent(NetcoolGatewayNetcoolEvent aEvent) throws NetcoolGatewayProcessEventFailure_Exception {
    	boolean status= false;
    	log.debug("send Event");
    	try {
			SybaseManager.getSybaseManager(mgr).getReaderQueue().enqueueWork(aEvent);
			status=true;
		} catch (SybaseManagerException e) {
			// TODO Auto-generated catch block
			throw new NetcoolGatewayProcessEventFailure_Exception(e.getMessage(),100);
		}
        return status;
    }
    /**
     * deregister
     * 
     * @param: aClient (com.iona.schemas.references.Reference)
     */
    public boolean deregister(NetcoolGatewayGatewayClient aClient) throws NetcoolGatewayProcessEventFailure_Exception {
    	log.debug("Removing clients from Servant List");
    	return true ;
    }
	
	/**
	 * @return Returns the clients.
	 */
	/*public Vector getClients() {
		return clients;
	}*/
	/**
	 * @param clients The clients to set.
	 */
	/*public void setClients(Vector clients) {
		this.clients = clients;
	}*/

	/**
	 * @return the gatewayClients
	 */
	private List getGatewayClients() {
		return Collections.synchronizedList(gatewayClients);
	}

	 
}
class NetcoolGatewayNetcoolEventComparator implements Comparator {
	private static final Logger log = LoggerFactory.getLogger(NetcoolGatewayNetcoolEventComparator.class);
	
	public int compare(Object obj1, Object obj2) {
		NetcoolGatewayNetcoolEvent o1 = (NetcoolGatewayNetcoolEvent) obj1;
		NetcoolGatewayNetcoolEvent o2 = (NetcoolGatewayNetcoolEvent) obj2;

		if (o1 == null && o2 == null) return 0; 
		// assuming you want null values shown last 
		if (o1 != null && o2 == null) return -1; 
		if (o1 == null && o2 != null) return 1; 
		if (!(o1 instanceof NetcoolGatewayNetcoolEvent) || 
				!(o2 instanceof NetcoolGatewayNetcoolEvent)) 
		{ 
			throw new IllegalArgumentException ("..."); 
		} 
		if(o1 == o2) return 0;
		
		NetcoolGatewayNVPairList o1List = o1.getANVPairList();
		NetcoolGatewayNVPairList o2List = o2.getANVPairList();
		if (o1List == null && o2List == null) return 0; 
		// assuming you want null values shown last 
		if (o1List != null && o2List == null) return -1; 
		if (o1List == null && o2List != null) return 1;

		NetcoolGatewayNVPair[] item1 = o1List.getItem(); 
		NetcoolGatewayNVPair[] item2 = o2List.getItem(); 
		if (item1 == null && item2 == null) return 0;  
		if (item1 != null && item2 == null) return -1; 
		if (item1 == null && item2 != null) return 1;

		for(int i=1; i < item1.length && i < item2.length; i++) 
		{
			String aname=item1[i].getAName();
			if(aname != null && (aname.equals(SybaseManager.SERVER_SERIAL) || aname.equals(SybaseManager.TTFLAG) || aname.equals(SybaseManager.IDENTIFIER)))
			{
				log.debug("aname=" + aname);
				String first1 = item1[i].getAValue();
				String first2 = item2[i].getAValue();
				if (first1 == null && first2 == null) return 0;  
				if (first1 != null && first2 == null) return -1; 
				if (first1 == null && first2 != null) return 1;

				if(aname.equals(SybaseManager.SERVER_SERIAL) && !first1.equals(first2))
				{
					log.debug("serials are not equal");
					return new Integer(first1).compareTo (new Integer(first2));
				}
				if(aname.equals(SybaseManager.IDENTIFIER) && !first1.equals(first2))
				{
					log.debug("identifiers:" + first1 + "is not equal to " + first2);
					return  first1.compareTo(first2);
				}
				if(aname.equals(SybaseManager.TTFLAG) && !first1.equals(first2))
				{
					log.debug("TTFlags are not equal");
					return new Integer(first1).compareTo (new Integer(first2));
				}
			}	    	 
		}		     
		return 0;
	}

	public boolean equals(Object obj)
	{
		return true;
	}

}
