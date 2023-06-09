package com.o2.techm.netcool.eai.manonsite.SocketProbe;

import java.text.SimpleDateFormat;
 
import java.util.Date;
 

import com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;
import com.o2.techm.netcool.eai.o2gateway.sybase.thread.Terminatable;
import com.o2.util.WorkQueue;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.common.Configuration;
import com.o2.techm.netcool.eai.manonsite.mapinfo.*;
import com.o2.util.Semaphore;
import java.util.LinkedList;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelper;

public class SocketProbeClient extends Thread implements Terminatable
{
	private static final Logger log = LoggerFactory.getLogger(SocketProbeClient.class);

	private SocketProbePlugin socketProbe; 
	
	private Semaphore taskSemaphore;	
	//private boolean systemShuttingDown; 
    private static SimpleDateFormat dateFormat;  
    private WorkQueue workQueueCIC;
    private boolean terminate = false;
    private static LinkedList almList = new LinkedList();
    MapInfo mapinfo;
	long startTime,currentTime, elapsed;
    private static final String TYPE = "1";
    private static final String CLASS = "704";
	
	public SocketProbeClient(SocketProbePlugin socketProbe,WorkQueue workQueueCIC,MapInfo mapinfo)
	{
		this.socketProbe = socketProbe;
		this.workQueueCIC = workQueueCIC;
		this.mapinfo = mapinfo;
		
		dateFormat = new SimpleDateFormat(Configuration.getValue(Configuration.CIC_DATE_FORMAT));
		taskSemaphore = new Semaphore(1); 
	}
	
	public void terminate (){
		terminate = true;
	}
	
	
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
        
        if (!terminate)
        {
            doWork(); 
        }

        taskSemaphore.release();
	}

    public void shutdown()
	{
            log.info("Waiting for any check events task to complete...");
            terminate();  
            this.interrupt();
            log.debug("Socket termination complete");
	}

 
	private void doWork()
	{
		
		SimpleDateFormat dateFormat =  new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		

		log.info("SocketProbeClient.run"); 
	
		
	 	while (!terminate)
	 	{
				// Get the Event from the work queue.
				log.debug("Waiting for work.... ");	
				Event event = null; 
				try 
				{
				    event= (Event) workQueueCIC.dequeueWork();				   
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
				        log.debug("LINE11 = '" + event.getLINE11() + "'");
				        log.debug("LINE12 = '" + event.getLINE12() + "'");
				        log.debug("LINE13 = '" + event.getLINE13() + "'");
				        log.debug("LINE14 = '" + event.getLINE14() + "'");
				        log.debug("LINE15 = '" + event.getLINE15() + "'");
				        log.debug("LINE16 = '" + event.getLINE16() + "'");
				        log.debug("LINE17 = '" + event.getLINE17() + "'");
				        log.debug("LINE18 = '" + event.getLINE18() + "'");
				        log.debug("LINE19 = '" + event.getLINE19() + "'");
				        log.debug("LINE20 = '" + event.getLINE20() + "'");
				        log.debug("LINE21 = '" + event.getLINE21() + "'");
				        log.debug("LINE22 = '" + event.getLINE22() + "'");
				        log.debug("LINE23 = '" + event.getLINE23() + "'");
				        log.debug("LINE24 = '" + event.getLINE24() + "'");
				        log.debug("LINE25 = '" + event.getLINE25() + "'");
				        log.debug("LINE26 = '" + event.getLINE26() + "'");
				        log.debug("LINE27 = '" + event.getLINE27() + "'");
				        log.debug("LINE28 = '" + event.getLINE28() + "'");
				        log.debug("LINE29 = '" + event.getLINE29() + "'");
				        log.debug("LINE30 = '" + event.getLINE30() + "'");
				    }
				   
				        SocketProbeHelper.raiseEvent(socketProbe,event);
				  
				   while(!almList.isEmpty())
				   {
						Event tmpevent = (Event)almList.getFirst();
						log.warn("[" + tmpevent.getLINE01() + "] Sending a buffered event to Socket probe ");
						SocketProbeHelper.raiseEvent(socketProbe,tmpevent);
						almList.remove(tmpevent);
					}				 
     			}
				catch (InterruptedException ex) 
				{
					log.warn("Got an interrupted exception."); 
				}
				
			 	catch (Exception ex) 
				{
			 		try
					{
			 			log.warn("[" + event.getLINE01() + "]Will Try and reconnect",ex);
						log.warn("[" + event.getLINE01() + "]Got an SocketProbeHelper exception.",ex);						 
						log.warn("[" + event.getLINE01() + "] Adding event to list; Sent to CIC when possible");
						almList.addLast(cloneEvent(event));
						if(almList.size() > 0) {log.warn("[" + event.getLINE01() + "]" +  almList.size() + " event(s) in List");}
					}
			 		catch(Exception e)
					{
						log.error("Error : ",e);
					}
				}
			 	finally
				{
			 		if(event != null && !mapinfo.isMapInfoBad())
			 		{
			 			currentTime = System.currentTimeMillis();
			 			elapsed = currentTime - startTime;
			 			log.debug("[" + event.getLINE01() + "] socketProbe.raiseEvent() took " + dateFormat.format(new Date(elapsed)));
 
			 			log.debug("[" + event.getLINE01() + "] about to insert record into mapinfo");
			 			startTime = System.currentTimeMillis();
			 	
			 			try
						{
			 				 mapinfo.addEvent(cloneEvent(event)); 
						}
			 			catch (MapInfoException ex) 
						{
			 				log.warn("[" + event.getLINE01() + "]Got an MapInfo exception.");
			 				Event mevent = new Event();
			 				mevent.setLINE01(TYPE);
			 				mevent.setLINE02(CLASS);
			            	mevent.setLINE03(MapInfo.MAPINFOFAILED);
			            	mevent.setLINE04(event.getLINE01()); 
			            	mevent.setLINE30("manonsite-mapinfo"); 
			            	
						        try {
									SocketProbeHelper.raiseEvent(socketProbe,mevent);
								} catch (Exception e) 
						        {
									
									log.error("[" + event.getLINE01() + "]Got an Exception, re-Initializing ...",e);
						 			
								}
						   
						}					
			 			log.debug("[" + event.getLINE01() + "] MapInfoRecordInsertion took " + dateFormat.format(new Date(elapsed)));
			 			currentTime = System.currentTimeMillis();
			 			elapsed = currentTime - startTime;
			 		}
				}
			}
	}
	
	public static Event cloneEvent(Event event)
	{
		Event newEvent = new Event();
		newEvent.setLINE01(event.getLINE01() != null ? event.getLINE01():"");
		newEvent.setLINE02(event.getLINE02() != null ? event.getLINE02():"");
		newEvent.setLINE03(event.getLINE03() != null ? event.getLINE03():"");
		newEvent.setLINE04(event.getLINE04() != null ? event.getLINE04():"");
		newEvent.setLINE05(event.getLINE05() != null ? event.getLINE05():"");
		newEvent.setLINE06(event.getLINE06() != null ? event.getLINE06():"");
		newEvent.setLINE07(event.getLINE07() != null ? event.getLINE07():"");
		newEvent.setLINE08(event.getLINE08() != null ? event.getLINE08():"");
		newEvent.setLINE09(event.getLINE09() != null ? event.getLINE09():"");
		newEvent.setLINE10(event.getLINE10() != null ? event.getLINE10():"");
		newEvent.setLINE11(event.getLINE11() != null ? event.getLINE11():"");
		newEvent.setLINE12(event.getLINE12() != null ? event.getLINE12():"");
		newEvent.setLINE13(event.getLINE13() != null ? event.getLINE13():"");
		newEvent.setLINE14(event.getLINE14() != null ? event.getLINE14():"");
		newEvent.setLINE15(event.getLINE15() != null ? event.getLINE15():"");
		newEvent.setLINE16(event.getLINE16() != null ? event.getLINE16():"");
		newEvent.setLINE17(event.getLINE17() != null ? event.getLINE17():"");
		newEvent.setLINE18(event.getLINE18() != null ? event.getLINE18():"");
		newEvent.setLINE19(event.getLINE19() != null ? event.getLINE19():"");
		newEvent.setLINE20(event.getLINE20() != null ? event.getLINE20():"");
		newEvent.setLINE21(event.getLINE21() != null ? event.getLINE21():"");
		newEvent.setLINE22(event.getLINE22() != null ? event.getLINE22():"");
		newEvent.setLINE23(event.getLINE23() != null ? event.getLINE23():"");
		newEvent.setLINE24(event.getLINE24() != null ? event.getLINE24():"");
		newEvent.setLINE25(event.getLINE25() != null ? event.getLINE25():"");
		newEvent.setLINE26(event.getLINE26() != null ? event.getLINE26():"");
		newEvent.setLINE27(event.getLINE27() != null ? event.getLINE27():"");
		newEvent.setLINE28(event.getLINE28() != null ? event.getLINE28():"");
		newEvent.setLINE29(event.getLINE29() != null ? event.getLINE29():"");
		newEvent.setLINE30(event.getLINE30() != null ? event.getLINE30():"");
		return newEvent;
	}
	public boolean isSystemShuttingDown()
    {
        return terminate;
    }
    public void setSystemShuttingDown(boolean systemShuttingDown)
    {
        this.terminate = systemShuttingDown;
    }
    public Semaphore getTaskSemaphore()
    {
        return taskSemaphore;
    }
    public void setTaskSemaphore(Semaphore taskSemaphore)
    {
        this.taskSemaphore = taskSemaphore;
    }
}
