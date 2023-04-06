/*Modification  History :
* Date          Version Modified by     Brief Description of Modification
* 11-Jan-2011   1.10      Keane          Modified for OSC 1558511
*/
package com.o2.techm.netcool.eai.manonsite.mrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.sybase.thread.Terminatable;
//import com.o2.manonsite.WorkItem;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;
import com.o2.techm.netcool.eai.manonsite.SocketProbe.SocketProbeClient;
import com.o2.techm.netcool.eai.manonsite.broker.BrokerConnection;
import com.o2.techm.netcool.eai.manonsite.broker.BrokerException;
import com.o2.techm.netcool.eai.manonsite.broker.wintel.*;
import com.o2.techm.netcool.eai.manonsite.inms.INMSConnection;
import com.o2.techm.netcool.eai.manonsite.common.CommunicateWithTT;
import com.o2.techm.netcool.eai.manonsite.common.Constants;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.*;
import java.io.IOException;

public class MRSRequestThread extends Thread implements Terminatable
{
	private static final Logger log = LoggerFactory.getLogger(MRSRequestThread.class);
	
	private BrokerConnection brokerConnection;	
	private boolean terminate = false;
	private MRSRequestThreadPool parent = null;
	private Pattern psplit1 = Pattern.compile("[a-z][a-z]*=([^|])*");	
	public MRSRequestThread(
			MRSRequestThreadPool parent,
			String name,
			BrokerConnection brokerConnection)
	{
		super(parent, name);
		this.parent = parent;
		this.brokerConnection = brokerConnection;		
	}
	public void terminate (){
		terminate = true;
	}
	
	/** @deprecated */
	private String SplitMsg(String msidn,String msg, String dest_addr) throws BrokerException
	{
		String resOutput=null;
		if(msg.length() > 160)
		{
			int ff = Math.round(msg.length()/160);
			if (log.isDebugEnabled()) log.debug(">> Calling SplitMsg");
			String messages[] = new String [ff+1]; 
			StringBuffer buf = new StringBuffer(msg);
			int j=0;
			int start=0;
			int end=159;
			while(buf.length() > 0)
			{
				try
				{
					messages[j] = buf.substring(start,end);
					
					if (log.isDebugEnabled()) log.debug("Sending message[" + j + "] : " + messages[j]);
					String response =brokerConnection.sendSimpleSMS(msidn, messages[j]);
					
					buf = new StringBuffer(buf.substring(end));
					if(end > buf.length())
						end=buf.length();
					j++;
					Thread.sleep(2000); //To prevent sms msg from been out of order
				}
				catch(InterruptedException ie)
				{
					throw new BrokerException(ie.getMessage());
				}
				if (log.isDebugEnabled()) log.debug("buf :" + buf);
				if (log.isDebugEnabled()) log.debug("end :" + end);
				
			}
		}
		else
			brokerConnection.sendSimpleSMS(msidn, msg);
		return resOutput;
	}
	
	/** @deprecated */
	
	Event duplicateEvent(Event event,String emsg,String fStr)
	{
		
		Event tmpEvt = SocketProbeClient.cloneEvent(event);
		
		//errorcode=202|short note=dslkjfsljfkldsjfkdls|description=dsjhfjkhdshfds|msisdn=sdfklsdfjdsljf
		tmpEvt.setLINE18(fStr);
		tmpEvt.setLINE19(emsg);
		/*
		 Matcher m1 = psplit1.matcher(emsg);
		 while(m1.find())
		 {
		 String txt=m1.group();
		 if(txt.startsWith(brokerConnection.ERRORCODE))
		 tmpEvt.setLINE16((String)txt);
		 else if(txt.startsWith(brokerConnection.TRANSLATION))
		 tmpEvt.setLINE17((String)txt);
		 else if(txt.startsWith(brokerConnection.ETEXT))
		 tmpEvt.setLINE18((String)txt);
		 else if(txt.startsWith(brokerConnection.EMSISDN))
		 tmpEvt.setLINE19((String)txt);			
		 }
		 */
		return tmpEvt;
	}
	
	public static boolean  isParsableToInt(String i)
	{
		try
		{
			Integer.parseInt(i);
			return true;
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
	}	
	 
	public void run()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		long elapsed;
		
		while (!terminate){
			// Get the MessageRequest from the work queue.
			log.debug("Waiting for work.... ");	
			
			Event event = null;
			String smsBodies[] = null;
			try { 
				event = SocketProbeClient.cloneEvent((Event) parent.getWorkQueue().dequeueWork());
				long startTime = System.currentTimeMillis();
				log.info("Recieved Request at '" +  dateFormat.format(new Date(startTime)) + "'" + "Request Data  =  " + event.toString());
				if(event == null)
				{
					log.error(" Event is null; some problem here please investigate");
					continue;
				}
				boolean found=false;				
				String sitename=event.getLINE14();
				if(event.getLINE17() != null)
					found = Boolean.valueOf(event.getLINE17()).booleanValue();				
				log.debug("found :" + found); 
                
				//Security Level of 1 and above
				if((event.getLINE04() == null || event.getLINE04().length() == 0 ) || (found && (event.getLINE16() != null && event.getLINE16().equals(Constants.CMDSTATUS[4])))) //Please check incoming message, something is not right
				{ 						 
					log.warn("unsupported command/No Permission: " + event.getLINE04());
				}
				else if (sitename.indexOf(",SITENOTFOUND")>-1 )
				{
					// if Siteid does not exist.
					if (log.isInfoEnabled())
						{
						log.info("Siteid:" + sitename.substring(0,sitename.indexOf(",")) + " not found.");
						}
					event.setLINE14(sitename.substring(0,sitename.indexOf(",")));
				}
				else if (found && (event.getLINE05() != null && event.getLINE05().toLowerCase().startsWith("alarm") /*&& Integer.parseInt(event.getLINE15()) >= Constants.SECLEVEL_FIRST*/ ))
				{				
					//Invoke directly on INMS web server using pure HTTPS
					INMSConnection inmsConnection = new INMSConnection();
					
					log.info("[" + event.getLINE06() + "]Getting alarm info for SiteId '" + event.getLINE06() + "'");
					smsBodies = inmsConnection.getCellSiteAlarms(event.getLINE06());
					
				   log.info("SiteAlarms called successfully for SiteId '" + event.getLINE06() + "'");
					
					if(smsBodies == null || smsBodies.length == 0)
					{
						log.debug(">> No SMS alarms for Site " + event.getLINE06());
						smsBodies = new String[1];
						
						smsBodies[0] = "There are no current alarms on " + event.getLINE06() + " @ this instant"; 
						
					}
				}
				//start OCS 367 12/12/2011
				else if ((event.getLINE05() != null 
						&& event.getLINE05().toLowerCase().startsWith("ccrstart")))
				{		
//					Get ccrstart
					String  cid  = event.getLINE06();
					cid = Character.isLetter(cid.charAt(cid.length() - 1)) && isParsableToInt(cid.substring(0,cid.length() - 1)) ? cid.substring(0,cid.length() - 1) : cid;
					HashMap rOutput = CommunicateWithTT.CallMOSLocation(cid);
					
					if (log.isInfoEnabled()) log.info("Getting ccrstart for SiteId '" + event.getLINE06() + "'");
					//smsBodies = new String[1];
					//smsBodies[0] = (String)rOutput.get("CCRSTART");
					
					if (log.isInfoEnabled()) log.info("ccrstart called successfully for SiteId '" + event.getLINE06() + "'");
					
					if (Integer.parseInt((String)rOutput.get("ERRORMSG")) < 0)
					{
						if (log.isDebugEnabled()) log.debug(">> No  ccrstart available for Site " + event.getLINE06());
						//smsBodies = new String[1];
						//smsBodies[0] = "No site ccrstart available for Site " + event.getLINE06();
					}
					else
					{
						if (log.isDebugEnabled()) log.debug(">> Sending  messages for Site " + event.getLINE06());
					}
					
				}
				
				else if ((event.getLINE05() != null 
						&& event.getLINE05().toLowerCase().startsWith("ccrend")))
				{				
//					Get ccrend
					String  cid  = event.getLINE06();
					cid = Character.isLetter(cid.charAt(cid.length() - 1)) && isParsableToInt(cid.substring(0,cid.length() - 1)) ? cid.substring(0,cid.length() - 1) : cid;
					HashMap rOutput = CommunicateWithTT.CallMOSLocation(cid);
					
					if (log.isInfoEnabled()) log.info("Getting ccrend for SiteId '" + event.getLINE06() + "'");
					//smsBodies = new String[1];
					//smsBodies[0] = (String)rOutput.get("CCREND");
					
					if (log.isInfoEnabled()) log.info("ccrend called successfully for SiteId '" + event.getLINE06() + "'");
					
					if (Integer.parseInt((String)rOutput.get("ERRORMSG")) < 0)
					{
						if (log.isDebugEnabled()) log.debug(">> No  ccrend available for Site " + event.getLINE06());
						//smsBodies = new String[1];
						//smsBodies[0] = "No site ccrend available for Site " + event.getLINE06();
					}
					else
					{
						if (log.isDebugEnabled()) log.debug(">> Sending messages for Site " + event.getLINE06());
					}
				}
				
				//end OCS 367 12/12/2011
				else if (found && (event.getLINE05() != null && event.getLINE05().toLowerCase().startsWith("clear") /*&& Integer.parseInt(event.getLINE15()) >= Constants.SECLEVEL_FIRST*/))
				{//Security Level of 1 and above							
					//Invoke directly on INMS web server using pure HTTPS
					INMSConnection inmsConnection = new INMSConnection();
					
					if (log.isInfoEnabled()) log.info("Getting alarm info for SiteId '" + event.getLINE01() + "'");
					smsBodies = inmsConnection.getRequest(event.getLINE05(),event.getLINE06());
					
					if (log.isInfoEnabled()) log.info("SiteAlarms called successfully for SiteId '" + event.getLINE06() + "'");
					
					if(smsBodies == null || smsBodies.length == 0)
					{
						if (log.isDebugEnabled()) log.debug(">> No SMS alarms for Site " + event.getLINE06());
						smsBodies = new String[1];
						
						smsBodies[0] = "There are no current alarms on " + event.getLINE06() + " @ this instant"; 
					}
					
				}
				else if (found && (event.getLINE05() != null && event.getLINE05().toLowerCase().startsWith("audit")/*&& Integer.parseInt(event.getLINE15()) >= Constants.SECLEVEL_FIRST*/)) 
				{//Security Level of 1 and above 
					INMSConnection inmsConnection = new INMSConnection();
					
					if (log.isInfoEnabled()) log.info("Getting audit info for SiteId '" + event.getLINE06() + "'");
					smsBodies = inmsConnection.getRequest(event.getLINE05(),event.getLINE06());
					
					if (log.isInfoEnabled()) log.info("getCellSiteAudit called successfully for SiteId '" + event.getLINE06() + "'");
					
					if (smsBodies == null || smsBodies.length == 0)
					{
						if (log.isDebugEnabled()) log.debug(">> No SMS audit for Site " + event.getLINE06());
						smsBodies = new String[1];
						smsBodies[0] = "No SMS audit for Site " + event.getLINE06();
					}
					else
					{
						if (log.isDebugEnabled()) log.debug(">> Sending " + smsBodies.length + " messages for Site " + event.getLINE06());
					}
					
				}
				else if (found && (event.getLINE05() != null && event.getLINE05().toLowerCase().startsWith("history") /*&& Integer.parseInt(event.getLINE15()) >= Constants.SECLEVEL_FIRST*/)) 
				{//Security Level of 1 and above
					
					
					// Invoke directly on INMS web server using pure HTTPS
					INMSConnection inmsConnection = new INMSConnection();
					
					if (log.isInfoEnabled()) log.info("Getting history info for SiteId '" + event.getLINE06() + "'");
					smsBodies = inmsConnection.getRequest(event.getLINE05(),event.getLINE06());
					
					if (log.isInfoEnabled()) log.info("SiteHistory called successfully for SiteId '" + event.getLINE06() + "'");
					
					if (smsBodies == null || smsBodies.length == 0)
					{
						if (log.isDebugEnabled()) log.debug(">> No SMS history for Site " + event.getLINE06());
						smsBodies = new String[1];
						smsBodies[0] = "No SMS history for Site " + event.getLINE06();
					}
					else
					{
						if (log.isDebugEnabled()) log.debug(">> Sending " + smsBodies.length + " messages for Site " + event.getLINE06());
					}	
					
				}
				else if  (found && (event.getLINE05() != null && event.getLINE05().toLowerCase().startsWith("direct") /*&&  Integer.parseInt(event.getLINE15()) >= Constants.SECLEVEL_FIRST*/))
				{//Security Level of 1 and above
					
					// Get Site Location
					String  cid = event.getLINE06();
					cid = Character.isLetter(cid.charAt(cid.length() - 1)) && isParsableToInt(cid.substring(0,cid.length() - 1)) ? cid.substring(0,cid.length() - 1) : cid;
					HashMap rOutput = CommunicateWithTT.CallMOSLocation(cid);
					
					if (log.isInfoEnabled()) log.info("Getting directions for SiteId '" + event.getLINE06() + "'");
					
					smsBodies = new String[1];
					smsBodies[0] = (String)rOutput.get("DIRECTIONS");
					if (log.isInfoEnabled()) log.info("SiteLocation  called successfully for SiteId '" + event.getLINE06() + "'");
					
					if (Integer.parseInt((String)rOutput.get("ERRORMSG")) < 0 || smsBodies[0]== null)
					{
						if (log.isDebugEnabled()) log.debug(">> No directions available for Site " + event.getLINE06());
						smsBodies = new String[1];
						smsBodies[0] = "No directions available for Site " + event.getLINE06();
					}
					else
					{
						if (log.isDebugEnabled()) log.debug(">> Sending " + smsBodies.length + " messages for Site " + event.getLINE06());
					}	
					
				}	
				else if (found && (event.getLINE05() != null &&  event.getLINE05().toLowerCase().startsWith("safe") || event.getLINE05() != null && event.getLINE05().toLowerCase().equals("in"))) 
				{//Security Level of 1 and above
					if (event.getLINE05().toLowerCase().equals("in")) 
					{
						if (log.isInfoEnabled()) log.info("Engineer txting IN for SiteId '" + event.getLINE06() + "' Sending safety notes (if any) to Engineer");
					}
					//Get Safety
					/*if(event.getLINE05().toLowerCase().startsWith("safe") && Integer.parseInt(event.getLINE15()) < Constants.SECLEVEL_FIRST )
					{
						smsBodies = new String[1];
						smsBodies[0]= "You have not the correct security level to use this command. Please contact our NMC if you want this capability on 01753281000";
					}
					else*/
					{
						String cid = event.getLINE06(); 
						cid = Character.isLetter(cid.charAt(cid.length() - 1)) && isParsableToInt(cid.substring(0,cid.length() - 1)) ? cid.substring(0,cid.length() - 1) : cid;
						if (log.isInfoEnabled()) log.info("Getting safety info for SiteId '" + event.getLINE06() + "'");						
						HashMap rOutput = CommunicateWithTT.CallMOSLocation(cid);
						
						smsBodies = new String[1];
						smsBodies[0] = (String)rOutput.get("SAFETY");
						
						if (log.isInfoEnabled()) log.info("SiteSafetyNote  called successfully for SiteId '" + event.getLINE06() + "'");
						
						if (Integer.parseInt((String)rOutput.get("ERRORMSG")) < 0 || smsBodies[0]== null )
						{
							if (log.isDebugEnabled()) log.debug(">> No safety notes available for Site " + event.getLINE06());
							if(event.getLINE05().toLowerCase().startsWith("safe")) //Don't send no cellsite Msg
							{
								smsBodies = new String[1];
								smsBodies[0] = "No safety notes available for Site " + event.getLINE06();
							}
						}
						else
						{
							if (log.isDebugEnabled()) log.debug(">> Sending " + smsBodies.length + " messages for Site " + event.getLINE06());
						}	
						smsBodies[0] = "SAFETY INFO: " + smsBodies[0];
						
						if((String)rOutput.get("BEACONISED") != null){
							smsBodies[0] = smsBodies[0] + (String)rOutput.get("BEACONISED");
						}
					}
				}	
				else if (found && (event.getLINE05() != null && event.getLINE05().toLowerCase().startsWith("note") /*&& Integer.parseInt(event.getLINE15()) >= Constants.SECLEVEL_FIRST*/))
				{//Security Level of 1 and above
					
					
					//Get Site Note
					String  cid  = event.getLINE06();
					cid = Character.isLetter(cid.charAt(cid.length() - 1)) && isParsableToInt(cid.substring(0,cid.length() - 1)) ? cid.substring(0,cid.length() - 1) : cid;
					HashMap rOutput = CommunicateWithTT.CallMOSLocation(cid);
					
					if (log.isInfoEnabled()) log.info("Getting site notes for SiteId '" + event.getLINE06() + "'");
					smsBodies = new String[1];
					smsBodies[0] = (String)rOutput.get("SITENOTE");
					
					if (log.isInfoEnabled()) log.info("SiteNote  called successfully for SiteId '" + event.getLINE06() + "'");
					
					if (Integer.parseInt((String)rOutput.get("ERRORMSG")) < 0 || smsBodies[0]== null)
					{
						if (log.isDebugEnabled()) log.debug(">> No site notes available for Site " + event.getLINE06());
						smsBodies = new String[1];
						smsBodies[0] = "No site notes available for Site " + event.getLINE06();
					}
					else
					{
						if (log.isDebugEnabled()) log.debug(">> Sending " + smsBodies.length + " messages for Site " + event.getLINE06());
					}
					
				}	
				
//				Added for OSC 1558511 
				
				else if(found && (event.getLINE05() != null && 
					(event.getLINE05().equals("hopon") ||event.getLINE05().
					equals("hopoff") || event.getLINE05().equals("unlock")|| 
					event.getLINE05().equals("lock"))) && (event.getLINE04().
					toLowerCase().contains("trx")))
					/*&& Integer.parseInt(event.getLINE15()) 
					 * >= Constants.SECLEVEL_SECOND*/
				{
					log.debug("Inside block for BCF and TRX");
					// Security Level of 2 and above
					if (event.getLINE16().equals(Constants.CMDSTATUS[0])) {

						INMSConnection inmsConnection = new INMSConnection();
						// &BCF=5&TRX=1
						String hcmd = "&BCF=" + event.getLINE07() + "&TRX="
								+ event.getLINE08();
						if (log.isInfoEnabled())
							log.info("Request for " + event.getLINE05()
									+ "for Site '" + event.getLINE06() + "'");
						smsBodies = inmsConnection.getRequest(
								event.getLINE05(), event.getLINE06(), hcmd
										.trim());
						if (log.isInfoEnabled())
							log.info(event.getLINE05()
									+ " called successfully for TRX '"
									+ event.getLINE08() + "'");

						if (smsBodies == null || smsBodies.length == 0) {
							smsBodies = new String[1];
							smsBodies[0] = "No response for command '"
									+ event.getLINE05() + "' for TRX "
									+ event.getLINE08();
							if (log.isDebugEnabled())
								log.debug(">> " + smsBodies[0]);
						} else if (log.isDebugEnabled())
							log.debug(">> Sending " + smsBodies.length
									+ " messages for TRX " + event.getLINE08());
					}

				}				
			    
		   
				else if (found && (event.getLINE05() != null && 
					(event.getLINE04().toLowerCase().contains("bts")))) {
					log.debug("Inside BTS Block");
					if ((event.getLINE05().equalsIgnoreCase("unlock"))
						|| (event.getLINE05().equalsIgnoreCase("lock"))) {
						log.debug(" Processing BTS Command : "
								+ event.getLINE05());
						if (event.getLINE16().equals(Constants.CMDSTATUS[0])) {

							INMSConnection inmsConnection = new INMSConnection();
							// &BCF=5&TRX=1
							String hcmd = "&BTS=" + event.getLINE07();
							if (log.isInfoEnabled())
								log.info("Request for " + event.getLINE05()
								+ "for Site '" + event.getLINE06()+ "'");
							smsBodies = inmsConnection.getRequest(event
								.getLINE05(), event.getLINE06(), hcmd.trim());
							if (log.isInfoEnabled())
								log.info(event.getLINE05()
									+ " called successfully for BTS '"
									+ event.getLINE07() + "'");
							if (smsBodies == null || smsBodies.length == 0) {
								smsBodies = new String[1];
								smsBodies[0] = "No response for command '"
										+ event.getLINE05() + "' for TRX "
										+ ""; // event.getLINE08();
								if (log.isDebugEnabled())
									log.debug(">> " + smsBodies[0]);
							} else if (log.isDebugEnabled())
								log
										.debug(">> Sending " + smsBodies.length
												+ " messages for TRX " + "" 
												/* event.getLINE08() */);
						}
					}

				}
//	 checking for BCF
				else if (found && (event.getLINE05() != null
					&& !(event.getLINE04().toLowerCase().contains("trx")) 
					&& (event.getLINE04().toLowerCase().contains("bcf")))) {
					log.debug(" Inside BCF without TRX Block ");
					if ((event.getLINE05().equalsIgnoreCase("unlock"))
						|| (event.getLINE05().equalsIgnoreCase("lock"))) {
						if (event.getLINE16().equals(Constants.CMDSTATUS[0])) {
							INMSConnection inmsConnection = new INMSConnection();
							// &BCF=5&TRX=1
							String hcmd = "&BCF=" + event.getLINE07() + "&TRX="
								+ ""; // event.getLINE08();
							if (log.isInfoEnabled())
								log.info("Request for " + event.getLINE05()
								+ "for Site '" + event.getLINE06()+ "'");
							smsBodies = inmsConnection.getRequest(event
								.getLINE05(), event.getLINE06(), hcmd.trim());
							if (log.isInfoEnabled())
								log.info(event.getLINE05()
									+ " called successfully for BCF '"
									+ event.getLINE07() + "'");

							if (smsBodies == null || smsBodies.length == 0) {
								smsBodies = new String[1];
								smsBodies[0] = "No response for command '"
										+ event.getLINE05() + "' for TRX " + ""; // event.getLINE08();
								if (log.isDebugEnabled())
									log.debug(">> " + smsBodies[0]);
							} else if (log.isDebugEnabled())
								log.debug(">> Sending " + smsBodies.length
										+ " messages for TRX " + "" 
										/* event.getLINE08() */);
						}

					}
				}     					
				
				
				// End of Addition for OSC 1558511
				
			   else if(found && (event.getLINE05() != null && event.getLINE05().toLowerCase().startsWith("copy") /*&& Integer.parseInt(event.getLINE15()) >= Constants.SECLEVEL_FIRST  */)) 
				{//Security Level of 1 and above						 
					if (log.isInfoEnabled()) log.info("alarm copy for SiteId '" + event.getLINE06() + "'");						 
				}					
				else if (event.getLINE05() != null &&  event.getLINE05().toLowerCase().equals("in")) 
				{//Security Level of 0 and above
					if (log.isInfoEnabled()) log.info("Engineer txting IN for SiteId '" + event.getLINE06() + "'");
					
				}
				else if (event.getLINE05() != null &&  event.getLINE05().toLowerCase().equals("out")) 
				{//Security Level of 0 and above
					if (log.isInfoEnabled()) log.info("Engineer txting OUT for SiteId '" + event.getLINE06() + "'");
					
				}
				else if (event.getLINE05() != null && event.getLINE05().toLowerCase().equals("callme"))
				{//Security Level of 0 and above
					if (log.isInfoEnabled()) log.info("Engineer requesting CALLME for SiteId '" + event.getLINE06() + "'");
					
				}
				else if (event.getLINE05() != null && event.getLINE05().toLowerCase().equals("textin"))
				{//Security Level of 0 and above
					if (log.isInfoEnabled()) log.info("Engineer TEXTIN for SiteId '" + event.getLINE06() + "'");
					
				}				
				else
				{
					smsBodies = new String[1];
					smsBodies[0]= "You have not the correct security level to use this command. Please contact our NMC if you want this capability on 01753281000";
				}				
				long currentTime = System.currentTimeMillis();				
				elapsed= currentTime - startTime;				
				log.info("[" + (event.getLINE01() != null?event.getLINE01():"") + "] Request , " + event.getLINE05() + " took " + dateFormat.format(new Date(elapsed)));				
			}
			catch (InterruptedException ex) {
				log.debug("Got an interrupted exception."); 
				// We can ignore this; if we are interrupted because of a call to terminate() then
				// terminate will be set to true, so the thread will run to a graceful completion.
			}
			catch(IOException ex)
			{
				log.error("IO Exception thrown " ,ex); 
				event.setLINE18(INMSConnection.INMSFAILED);
				event.setLINE19(ex.getMessage());
			}
			catch(Exception ex)
			{
				log.error("MRSRequest Problem: " ,ex);
				//CommunicateWithTT.close(); 
				event.setLINE18(MRSRequestThread.class.getName() + " failed");
				event.setLINE19(ex.getMessage());
			}
			finally
			{	int nbdyCnt=1;
			try
			{
				for (int i = 0; smsBodies != null && i < smsBodies.length; i++) {
					if (log.isDebugEnabled()) log.debug(">> BODY [" +  i + "] " + smsBodies[i]);

					int positionOfTab = smsBodies[i].indexOf("\t");
					String smsString = smsBodies[i].substring(positionOfTab + 1, smsBodies[i].length());
					if(smsBodies[i].toUpperCase().startsWith("NCOBODY"))
					{	
						switch(nbdyCnt)
						{
						case 1:
							event.setLINE21((String)smsString);
							break;
						case 2:
							event.setLINE22((String)smsString);
							break;
						case 3:
							event.setLINE23((String)smsString);
							break;
						case 4:
							event.setLINE24((String)smsString);
							break;
						default:
						}
						nbdyCnt++;
					}					
					else if((smsBodies[i].toUpperCase().startsWith("STATUS")))
					{
						if (log.isDebugEnabled()) log.debug("Setting STATUS from INMS STATUS");   
						event.setLINE16(smsBodies[i].substring(smsBodies[i].indexOf(":") + 1, smsBodies[i].length()));
					}
					else if(smsBodies[i] != null)
					{				
						if (log.isDebugEnabled()) log.debug("Sending message[" + i + "]: '" + smsBodies[i] +"'"); 
						try
						{							
							/*if(!event.getLINE02().equals("VOD"))*/
								/**TODO: Need to CON to the ServiceID Value
								 * 1 - WINTESTSERVER
								 * 2 - ManOnsite
								 * 3 - OSIRIS
								 */
							//05.09.2012 OSC 652 MOS update to support the new PtW Long Code
								brokerConnection.sendSimpleSMS(event.getLINE01(), smsString,event.getLINE03()); 
							/*else 
								SplitMsg(event.getLINE01(),smsString,event.getLINE03());*/
						}
						catch (BrokerException e)
						{
							log.error("Unable to send SMS; details:" ,e);									 									 
							event.setLINE18(BrokerConnection.SMSFAILED);
							event.setLINE19(e.getMessage());
						}
					}
				}
			}
			catch(Exception e1)
			{
				log.error("ERROR:", e1);
			}
			if(event !=null)
				event.setLINE30("manonsite-processor");
			parent.getWorkQueueCIC().enqueueWork(event);				 
			}
		}
		
		if (log.isDebugEnabled()) log.debug(this.getName() + " terminated. ");
		
	}
	
}
