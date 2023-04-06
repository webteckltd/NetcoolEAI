package com.o2.techm.netcool.eai.manonsite.mrs;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.o2.techm.netcool.eai.manonsite.common.CommunicateWithTT;
import com.o2.techm.netcool.eai.manonsite.common.Constants;
import com.o2.techm.netcool.eai.manonsite.sybase.SybaseImpl;
import com.o2.util.WorkQueue;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;

@RestController
@RequestMapping("/mrservice")	
public class MRServiceRESTImp {

	private static final Logger logger = LoggerFactory.getLogger(MRServiceRESTImp.class);
	
    private WorkQueue workQueue;
    private WorkQueue workQueueCIC;
    private CommunicateWithTT ttServer;
    private boolean cmmdHaveBG = false; // FIX October release 18/10/2011 
	private Hashtable requestTable = new Hashtable();
	
	public static String MTCODE = "manonsite.co.uk/default"; // Got this from MRS support. See Peter Duguid 
	public static String NORMAL_SMS_MESSAGE = "NOR";  
	public static String FLASH_MESSAGE = "FLA"; 
	
	@PostMapping("/getContent")
	public @ResponseBody RequestContent getContent(@RequestBody RequestContent request){
				
		logger.info("Received call for getContent.");
		populateRequestTable(request);
        String incomingTextMessage = (String)requestTable.get("MSG"); 
        String replyTextMessage = null;
        Event event =null;
        boolean found = false;
        HashMap rOutput=null;
        boolean cont=true;
        boolean flag4G=true;
        cmmdHaveBG = false; // FIX October release 18/10/2011 
        try {
        	
        	long startTime = System.currentTimeMillis();
        	        	
        	String msisdn = (String)requestTable.get("MSISDN");
        	if(msisdn.startsWith("+"))
        		msisdn =new StringBuffer(msisdn).deleteCharAt(0).toString();
        	logger.debug("MSISDN 	:" + msisdn );
    		String scode = (String)requestTable.get("SHORTCODE");
    		logger.debug("SHORTCODE 	:" + scode);
    		String serviceID = (String)requestTable.get("SERVICEID");
    		logger.debug("SERVICEID 	:" + serviceID);    
    		String nwork = (String)requestTable.get("NETWORK");
    		logger.debug("NETWORK 	:" + nwork);    		
    		logger.debug("incomingTextMessage : " + incomingTextMessage);
        	String sid = (String)requestTable.get( "SERVICEID");
        	logger.debug("SERVICEID 	:" + sid);
        	event = new Event();
           	event.setLINE01(msisdn);
        	event.setLINE02(nwork);
        	event.setLINE03(scode);
        	event.setLINE04(incomingTextMessage);
        	event.setLINE09(sid);
		
        	//Parse Incoming message
        	MRSRequestInfo requestInfo = new MRSRequestInfo(incomingTextMessage);        	
        	String cmmd = requestInfo.getCommand().toLowerCase();
        	logger.debug("The Command is :" + cmmd);
        	StringTokenizer stk = new StringTokenizer(requestInfo.getCommand().toLowerCase());
        	//log.debug("NumOfToken = " + stk.countTokens());
        	if(stk.countTokens() > 1)
        	      	cmmd =stk.nextToken();
        	        logger.debug(" After Tokenizer, the command is : " + cmmd);
        	        event.setLINE05(cmmd);  
        	        logger.debug(" The LINE05 is : " + event.getLINE05());
        	
            
        	if(incomingTextMessage.toLowerCase().matches(".* WINO2MANST .*" ))
    		{
        		logger.debug("Recieved a WINTEL Test message");
    			replyTextMessage="Recieved a WINTEL Message to test connection so no action will be taken";
    		}
        	else if(cmmd.toLowerCase().equals("heartbeat"))
    		{
        		logger.debug("Recieved an heartbeat");
    		}
    		else
    		{   
    			//Authenticate User Here, not for PTW OSC 367
    			rOutput = ttServer.AuthenticateUser(new String(msisdn));    
    			if(rOutput == null || new Integer((String)rOutput.get("ERRORMSG")).intValue() != 0)
    			{
    				rOutput = ttServer.AuthenticateUser("+" + new String(msisdn));
    			}
    			 
    			if(rOutput != null && new Integer((String)rOutput.get("ERRORMSG")).intValue() == 0)
    				found=true;
    			else
    				logger.debug("[" + msisdn + "] Authentication failed ");
    	    	event.setLINE17(new Boolean(found).toString());
    	    	
    			long currentTime = System.currentTimeMillis(); 
    			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    	    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    	long elapsed = currentTime - startTime;

    	    	logger.debug("[" + msisdn + "] Authenticating took " + dateFormat.format(new Date(elapsed)));    	    	 
    	    	String siteName = null; 
    	    	String padCellSite="";
    	    	String padCellSiteAux=""; //FIX OSC 349 MARCH
    	    	boolean siteId=true;
				try
				{

					//OSC 367, 
					if(!cmmd.startsWith("ccrstart") && !cmmd.startsWith("ccrend"))
					{
						padCellSite = padStr(requestInfo.getCellSiteId(),6);					
					} else {
						//OSC 367 ccrreference 
						padCellSite = requestInfo.getCellSiteId();
						cont = false; //OSC 367 esto es para que no compruebe el siteID
						siteId = true; //OSC 367 esto es para que no responda como sms corrupto
						siteName = requestInfo.getCellSiteId(); //OSC 367, evita el null en el sms de respuesta						
					}
									
						//OSC 349
					logger.info("[" + padCellSite + "] padCellSite before check");
						
						// FIX MARCH OSC 349
						if(cont) {						
								// Si es un comando aurora
								if((cmmd.startsWith("ala") || cmmd.startsWith("his") || cmmd.startsWith("alarm") || cmmd.startsWith("history")
							    			|| cmmd.startsWith("aud") || cmmd.startsWith("hopon") || cmmd.startsWith("hopof") 
							    			|| cmmd.startsWith("unl") || cmmd.startsWith("lock"))) //OCS 367 12/12/2011
								 {
									 //Buscamos el siteName							 
									 siteName = ttServer.CallSiteName(padCellSite);
									 logger.info("AU SiteInfo called successfully for SiteId '" + requestInfo.getCellSiteId() + "'");
									 logger.debug("AU siteName = " + siteName);
									 
									 //Si no se encuentra...
									 if ((siteName== null) || (siteName.trim().length() < 1))
										{
										  	//...intentamos de nuevo quitando la ultima letra del site
										 	char chr1 = padCellSite.toUpperCase().charAt(padCellSite.length() - 1);		
								    	 	if ((Character.toUpperCase(chr1) == 'B' || Character.toUpperCase(chr1) == 'G')) {							    		
								    	 		padCellSiteAux = padCellSite.substring(0,(padCellSite.length() - 1));						    	 		
								    	 		siteName = ttServer.CallSiteName(padCellSiteAux);
								    	 		logger.debug("AUr siteName 2 try = " + siteName);
								    	 		 if ((siteName== null) || (siteName.trim().length() < 1)) {
								    	 			 
								    	 			 //hacemos una comprobaci�n extra, ya que se pueden aceptar el cellsite GB, BG y LG y LB
								    	 			 //y los comandos tendran que ser los comunes a 3G y 4G (alarm, history, audit, lock & unlock)
								    	 			char chr2 = padCellSite.toUpperCase().charAt(padCellSite.length() - 2);
								    	 			if (( (Character.toUpperCase(chr1) == 'B' && Character.toUpperCase(chr2) == 'G' )
								    	 			    ||(Character.toUpperCase(chr1) == 'G' && Character.toUpperCase(chr2) == 'B')
								    	 				||(Character.toUpperCase(chr1) == 'G' && Character.toUpperCase(chr2) == 'L') 
								    	 				||(Character.toUpperCase(chr1) == 'B' && Character.toUpperCase(chr2) == 'L'))
								    	 				   && (!cmmd.startsWith("hopon") && !cmmd.startsWith("hopof") )  ){
								    	 				padCellSiteAux = padCellSite.substring(0,(padCellSite.length() - 2));						    	 		
										    	 		siteName = ttServer.CallSiteName(padCellSiteAux);
										    	 		logger.debug("siteName 2 try 2G,3G,4G  = " + siteName);
										    	 		if ((siteName== null) || (siteName.trim().length() < 1)) {
										    	 			logger.debug(">>G No Site name for  " + requestInfo.getCellSiteId());
															siteId = false;
															siteName = requestInfo.getCellSiteId();
															cont=false;	
										    	 		}		
								    	 			}
								    	 			else{
								    	 				logger.debug(">>AU No Site name for  " + requestInfo.getCellSiteId());
													siteId = false;
													siteName = requestInfo.getCellSiteId();
													cont=false;
								    	 			}
								    	 		 }
								    	 	}
								    	 	else if (Character.toUpperCase(chr1) == 'L'){
								    	 		//hay que hacer esto ya que algun comando aurora tb es un comando valido para 4G
								    	 		//(alarm, history, audit, lock and unlock )
								    	 		siteId = false;
												flag4G = false;
												cont=false;
								    	 	}
										}
								 } 
								 	else // No es un comando aurora
								 {						 
									siteName = ttServer.CallSiteName(padCellSite);
									logger.info("SiteInfo called successfully for SiteId '" + requestInfo.getCellSiteId() + "'");							
									logger.debug("siteName = " + siteName);
									
									if ((siteName== null) || (siteName.trim().length() < 1))
									{
										logger.debug(">> No Site name for  " + requestInfo.getCellSiteId());
										siteId = false;
										flag4G = false;
										siteName = requestInfo.getCellSiteId();
										cont=false;
									}
								 }			
						}// end if(cont)
						
				//buscamos a ver si trae cellsite + L (4G)
			    //si flag4G esta a false, es que no ha encontrado el siteName
				if(flag4G==false){
					// Si es un comando valido para  4G (safe, status, notes, clear, alarm, history, audit, lock, unlock)
					if((cmmd.startsWith("ala") || cmmd.startsWith("his") || cmmd.startsWith("alarm") || cmmd.startsWith("history")
				    			|| cmmd.startsWith("aud") || cmmd.startsWith("note") || cmmd.startsWith("status") 
				    			||(cmmd.startsWith("cle") || cmmd.startsWith("clr")) 
				    			|| (cmmd.startsWith("safe")||cmmd.startsWith("saf"))
				    			|| cmmd.startsWith("unl") || cmmd.startsWith("lock"))) 
					 {
						
					  	// quitamos la ultima letra del site a ver si es una L
					 	char chr1 = padCellSite.toUpperCase().charAt(padCellSite.length() - 1);		
			    	 	if ((Character.toUpperCase(chr1) == 'L')) {							    		
			    	 		padCellSiteAux = padCellSite.substring(0,(padCellSite.length() - 1));						    	 		
			    	 		siteName = ttServer.CallSiteName(padCellSiteAux);
			    	 		logger.debug("4G siteName 2 try = " + siteName);
			    	 		 if ((siteName== null) || (siteName.trim().length() < 1)) {
			    	 			 //comprobaremos si llega un GL o BL y un comando compatible con ambos 
			    	 			char chr2 = padCellSite.toUpperCase().charAt(padCellSite.length() - 2);
			    	 			if (( (Character.toUpperCase(chr2) == 'G')||(Character.toUpperCase(chr2) == 'B'))
				    	 		    && (!cmmd.startsWith("note") && !cmmd.startsWith("status") &&
				    	 		    	!cmmd.startsWith("cle")  && !cmmd.startsWith("clr")    &&
				    	 				!cmmd.startsWith("safe") && !cmmd.startsWith("saf") )  ){
			    	 				
			    	 				padCellSiteAux = padCellSite.substring(0,(padCellSite.length() - 2));						    	 		
					    	 		siteName = ttServer.CallSiteName(padCellSiteAux);
					    	 		logger.debug("siteName 2 try 4G,2G,3G  = " + siteName);
					    	 		if ((siteName== null) || (siteName.trim().length() < 1)) {
					    	 			logger.debug(">>G No Site name for  " + requestInfo.getCellSiteId());
										siteId = false;
										siteName = requestInfo.getCellSiteId();
										cont=false;	
					    	 		}
					    	 		else{
					    	 			 //hay que poner siteId y cont a true ya que anteriormente se habra puesto a false
					    	 			 // al llegar un cellsite con una letra que no es B o G sino L, o si no es un comando Aurora
					    	 			siteId = true;
										cont=true;
					    	 			
					    	 		}
			    	 						
			    	 			}
			    	 			else{
			    	 				logger.debug(">>4G No Site name for  " + requestInfo.getCellSiteId());
								siteId = false;
								siteName = requestInfo.getCellSiteId();
								cont=false;
			    	 			}
			    	 		 }
			    	 		 else{
			    	 			 //hay que poner siteId y cont a true ya que anteriormente se habra puesto a false
			    	 			 // al llegar un cellsite con una letra que no es B o G sino L, o si no es un comando Aurora
			    	 			siteId = true;
								cont=true;
			    	 			 
			    	 		 }
			    	 	}
						
					 }
					
				}
						
				}
					catch (StringIndexOutOfBoundsException e0)
				{ 
					siteName=requestInfo.getCellSiteId();
					event.setLINE18("MRSServiceImpl.class");
					event.setLINE19(e0.getMessage());							
				} 
		
				event.setLINE01(msisdn);
				event.setLINE02(nwork);
				event.setLINE03(scode);
				event.setLINE04(incomingTextMessage);    			
				event.setLINE06(padCellSite);
				event.setLINE09(sid);

				event.setLINE14(siteName);
				if(rOutput == null)
				{
					event.setLINE18(CommunicateWithTT.JDBCFAILED);
					event.setLINE19("Cannot authenticate userID, CMDB probably Down");    					 
					rOutput = new HashMap(5);
				}
				if(rOutput.get("FIRSTNAME") == null || rOutput.get("LASTNAME") == null || rOutput.get("COMPANY") == null)
					logger.warn("Although an attempt was made to authenticate , some/all of this user's infomation was not present");
								 	 
				event.setLINE10(rOutput.get("FIRSTNAME") == null ? "":(String)rOutput.get("FIRSTNAME"));
				event.setLINE11(rOutput.get("LASTNAME") == null ? "": (String)rOutput.get("LASTNAME"));
				event.setLINE12(rOutput.get("COMPANY") == null ? "" :(String)rOutput.get("COMPANY"));
				event.setLINE13(rOutput.get("EMAILADDRESS") == null? "":(String)rOutput.get("EMAILADDRESS"));
				//11/07/2011 OSC 349
				//22/12/2011 OSC 367: && (!cmmd.startsWith("ccrstart") && !cmmd.startsWith("ccrend"))
				if(cont == false && (!cmmd.startsWith("ccrstart") && !cmmd.startsWith("ccrend")))				
    			{    				  				
					logger.debug("Only 'ALARM' and 'HISTORY' requests can have a postfix of either 'B' or 'G'");
    				//replyTextMessage = "Only 'ALARM' and 'HISTORY' requests can have a postfix of either 'B' or 'G'";
    				replyTextMessage = "Your message appears corrupt.  Please phone the O2 - NMC for guidance";
    				event.setLINE16(Constants.CMDSTATUS[4]);
    			}
    			else if(!found && !cmmd.startsWith("cal") && !cmmd.startsWith("in") 
    					&& !cmmd.startsWith("out") && !cmmd.startsWith("txt") && !cmmd.startsWith("text")
    					&& !cmmd.startsWith("ccrstart") && !cmmd.startsWith("ccrend")) { //09/01/2012 OSC 367, add && !cmmd.startsWith("ccrstart") && !cmmd.startsWith("ccrend")
    				logger.warn("[" + msisdn + "]Cannot authenticate " + requestInfo.getCellSiteId() + " Request terminated"); 
    				replyTextMessage = "This phone is not allowed to do this command.  If you register with the NMC, O2 may allow you to do more functions.";
    				//replyTextMessage = "Warning you are not a known field engineer. Contact the O2 NOC on 01234 67 8899 immediately";    		    	 
    			}
                //  If Site does not exist 
    			else if(siteId == false)
				{
    				String smsg = "Hello " + (String)rOutput.get("FIRSTNAME") + " " + (String)rOutput.get("LASTNAME") + " of " + (String)rOutput.get("COMPANY") + " ";
    				
    				logger.info("The SiteId: "+siteName+" not found.");
    				
    				replyTextMessage = smsg + ". Your message appears corrupt. Please phone the O2 NMC for guidance.";
    				siteName = requestInfo.getCellSiteId()+","+"SITENOTFOUND";
    				event.setLINE14(siteName);
				}
    			    			
    			else  
    			{    				
    				String smsg="";
    				//FIX MARCH OSC 349
    				String emsg=" ";
    				if(padCellSiteAux.equals("")){
    					emsg += padCellSite;
    				} else if (!padCellSiteAux.equals("") && cont == true) {
    					emsg += padCellSiteAux;
    				} else {
    					emsg += padCellSite;
    				}
    				//--end FIX MARCH OSC 349
    				
    				if((cmmd.startsWith("out") || cmmd.startsWith("in") || cmmd.startsWith("cal") || cmmd.startsWith("txt") || cmmd.startsWith("text") ) && !found)
    				{
    					if(cmmd.startsWith("out"))
    					{smsg = "You have just txt out of ";}
    					else if (cmmd.startsWith("in"))
    					{smsg = "Your presence is txt-in @ " ;emsg +=". If you register with the NMC, you can do more functions."  ;}
    					else if (cmmd.startsWith("cal"))
    					{smsg = "";}
    					else if (cmmd.startsWith("txt")||cmmd.startsWith("text") )
    					{smsg ="";} 
    				}
    				//13/01/2012 OSC 367 FIX user autenticate
    				else if((cmmd.startsWith("ccrstart") || cmmd.startsWith("ccrend")) && rOutput.get("FIRSTNAME") == null) {
    					smsg ="";
    				}
    				else
    				{    					
    					smsg = "Hello " + (String)rOutput.get("FIRSTNAME") + " " + (String)rOutput.get("LASTNAME") + " of " + (String)rOutput.get("COMPANY") + " ";
    					
    					if(cmmd.startsWith("in"))
    						{ smsg += " We have you @ ";}
    					if(cmmd.startsWith("out"))
    					{ smsg = "You have just txt out of ";}
    					if(!requestInfo.getCellSiteId().equals(siteName))
						{ emsg += " - " + siteName ;}
    					
    				}
 		 
    				logger.debug("SCODE: " + scode);
    				if (scode.trim().equals("62946"))
    					  emsg += ". Thx 4 using Maxim.";
    				else
    					  emsg += ". Thx 4 using PTW.";

    				if (cmmd.startsWith("ala")||cmmd.startsWith("alarm")) //11/07/2011 OSC 349
    				{
    					cmmd = "alarm";
    					replyTextMessage = smsg + ". Processing your request for alarms for "  + emsg; 
    				}
    				else if (cmmd.startsWith("aud")) 
    				{
    					cmmd = "audit";
         	       		replyTextMessage = smsg +". Processing your request for audit for " + emsg; 
    				}
    				else if (cmmd.startsWith("cle") || cmmd.startsWith("clr")) 
    				{
    					cmmd = "clear";
         	       		replyTextMessage = smsg +". Processing your request for clear for " + emsg; 
    				}
    				else if (cmmd.startsWith("dir") || cmmd.startsWith("loca") || cmmd.startsWith("access")) 
    				{ 
    					cmmd = "direct";
    					replyTextMessage = smsg +". Processing your request for directions for " + emsg; 
    				}
    				else if (cmmd.startsWith("safe")||cmmd.startsWith("saf")) //11/07/2011 OSC 349
    				{ 
    					cmmd = "safe";
    					replyTextMessage = smsg +". Processing your request for safety information for " + emsg; 
    				}
    				else if (cmmd.startsWith("note")) 
    				{ 
    					cmmd = "note";
    					replyTextMessage = smsg +". Processing your request for site notes for " + emsg; 
    				}
    				else if (cmmd.startsWith("cop")) 
    				{ 
    					int cpySub=1; 
    					Pattern	cpMap = Pattern.compile("copy?? *([0-9]+)");
    					Matcher m = cpMap.matcher(requestInfo.getCommand().toLowerCase()); 
    					if(m.find())
    					{ 
    						try
							{
    							cpySub = Integer.parseInt(m.group(1));  
							}
    						catch(NumberFormatException ne)
							{
    							logger.error("Copy value not recongnised;setting to default ",ne);
    							cpySub=1;
							}
    						logger.debug("copy Subcrition no :" + cpySub );
    					}
    					String cpStateVal=" disable ";
    					if(cpySub > 0)
    						cpStateVal = " enable ";
    					cmmd ="copy";            		 	
    					replyTextMessage = smsg +". Processing your request to " + cpStateVal + "alarm diverts for " + emsg; 
    				}
    				else if (cmmd.startsWith("in")) 
    				{ 
    					cmmd ="in";
    					replyTextMessage = smsg + emsg; 
    				}
    				else if (cmmd.startsWith("out")) 
    				{ 
    					cmmd = "out";
    					replyTextMessage = smsg + emsg ; 
    				}
    				else if (cmmd.startsWith("cal")) 
    				{ 
    					cmmd = "callme"; 
    					replyTextMessage = smsg +". We are processing a CALLME request for you @ " + emsg;
    				}
    				else if (cmmd.startsWith("txt") || cmmd.startsWith("text") ) 
    				{ 
    					cmmd = "textin";
    					replyTextMessage = smsg +". Processing your request 'TextIn' for " + emsg; 
    				}
    				else if(cmmd.startsWith("his")||cmmd.startsWith("history")) //11/07/2011 OSC 349
    				{
    					cmmd = "history";
    					replyTextMessage = smsg +". Processing your request 'History' for " + emsg;    					
    				}

    				//Added lock command for OSC 1558511
    				else if(cmmd.startsWith("hopon") || cmmd.startsWith("hopof") || cmmd.startsWith("unl")|| cmmd.startsWith("lock"))
//    					End of changes done for OSC 1558511
    				{
//    					Retrieve Usage Quota
    					HashMap tmpMap = null;
    					int dlimit=0;
    					int cday =0;
    					SybaseImpl sysImpl = new SybaseImpl();
    					try {
    						tmpMap = sysImpl.queryDB(event.getLINE01());
    						if(tmpMap == null)
    						{
    							logger.warn(" No Privledge record found for MSISDN " + event.getLINE01());
    							event.setLINE15("0"); 						
    						}						 
    						else
    						{
    							event.setLINE15((String)tmpMap.get("SecurityLevel"));
//    							Added lock command for OSC 1558511
    							if(event.getLINE05().equals("unlock") || event.getLINE05().equals("lock"))
//    							End of changes done for OSC 1558511
    							{
    								dlimit = Integer.parseInt((String)tmpMap.get("UnlockDailyLimit"));
    								cday =Integer.parseInt((String)tmpMap.get("UnlockCountDay"));							
    							}
    							else if(event.getLINE05().startsWith("hopo"))
    							{
    								dlimit = Integer.parseInt((String)tmpMap.get("HoppingDailyLimit"));
    								cday = Integer.parseInt((String)tmpMap.get("HoppingCountDay"));
    							}    							
    						}
    					} catch (SQLException e) {
    						// TODO Auto-generated catch block
    						logger.warn(" Could not retrieve user priviledges for MSISDN " + event.getLINE01() + " at this time",e);
    						event.setLINE15("0");
    						e.printStackTrace(); 
    					} 
    					
    					if( event.getLINE04().toLowerCase().contains("bts")){
    						event.setLINE07(padStr(requestInfo.getBts(),4));    						
    					}
    					else { 
    						event.setLINE07(padStr(requestInfo.getBCF(),4));
    					}
    					if(cmmd.startsWith("hopon") || cmmd.startsWith("hopof"))
    							{
    					event.setLINE08(padStr2(requestInfo.getTRX(),4));
    							}else
    					event.setLINE08(padStr(requestInfo.getTRX(),4));
    	    						
    					cmmd=cmmd.trim().toLowerCase();
//    					Added lock command for OSC 1558511
    					if(cmmd.startsWith("lock"))
    						cmmd="lock";
//						End of changes done for OSC 1558511
    					else if(cmmd.startsWith("unl"))
    						cmmd="unlock"; 
    					else if(cmmd.startsWith("hopof"))
    						cmmd="hopoff";    					
    					if(Integer.parseInt(event.getLINE15()) < Constants.SECLEVEL_SECOND)
    					{    						 
    						replyTextMessage =smsg + "You are not authorised to issue this command. Contact the O2 NMC for this capability on 01753281000." /*+ emsg*/;
    						event.setLINE16(Constants.CMDSTATUS[2]);
    					}
    					else if((event.getLINE07() == null || event.getLINE07().equals("")) && (event.getLINE08() == null || event.getLINE08().equals("")))
    					{
    						replyTextMessage=smsg +". Your message appears corrupt.  Please phone the O2 - NMC for guidance " + emsg;
    						event.setLINE16(Constants.CMDSTATUS[1]);
    					}
    					else if(cday >= dlimit)
						{							 
    						replyTextMessage=  smsg +". Your personal daily limit has been reached for this command. Contact the O2 NMC on 01753281000." /*+ emsg*/;
							event.setLINE16(Constants.CMDSTATUS[3]);
						}
    					else
    					{
    						sysImpl.updateDB(event.getLINE01(),cday,event.getLINE05());
    						event.setLINE16(Constants.CMDSTATUS[0]);
    						replyTextMessage = smsg +". Processing your request for " + cmmd + " on " + padCellSite + "-" + siteName + ", an AUDIT will be sent to confirm any changes";
    					}
    				}
    				//start OCS 367 12/12/2011
    				else if(cmmd.startsWith("ccrstart"))
    				{
    					cmmd = "ccrstart";
    					replyTextMessage = smsg + "Processing your request for ccrstart for "  + emsg;
    				}
    				else if(cmmd.startsWith("ccrend"))
    				{
    					cmmd = "ccrend";
    					replyTextMessage = smsg + "Processing your request for ccrend for "  + emsg;
    				}
    				//end OCS 367 12/12/2011
    				else 
    				{    
    					event.setLINE16(Constants.CMDSTATUS[4]);    					
    					replyTextMessage = "Your message appears corrupt.  Please phone the O2 - NMC for guidance";
    				}
    				event.setLINE05(cmmd);//overwrite command and it would have be modified for some commands
				 				
    				logger.info("Creating work item for Site " + siteName);	
    			}
	     	} 
        }
        
 		catch (MRSRequestInfoException ex) {
 		   replyTextMessage="Incoming text messsage was formatted incorrectly.";
 		  logger.warn(replyTextMessage,ex);  
 		  if(event != null)
		    {
		       event.setLINE18("MRSRequestInfoException");
		       event.setLINE19("Exception while trying to process request");
		    }
 		}
 		catch (Exception ex) {
 		    replyTextMessage = "Exception while trying to process request!"; 
 		   logger.error(replyTextMessage,ex);
 		    if(event != null)
 		    {
 		       event.setLINE18("MRSServiceImpl.class");
		       event.setLINE19("Exception while trying to process request");
 		    }
 		}
 		catch (java.lang.Error e) {
 		    replyTextMessage = "Exception while trying to process request!"; 
 		   logger.error(replyTextMessage,e);
 		   	System.exit(1); 		    
 		}
 		finally
		{
 			if(event.getLINE17() == null)
 				event.setLINE17(new Boolean(false).toString());	
 			    /*event.setLINE09(new Boolean(cont).toString());*/
 		    	workQueue.enqueueWork(event);
		}
        logger.info("Sending response '" + replyTextMessage + "'");        
 		return createResponse(NORMAL_SMS_MESSAGE,MTCODE, replyTextMessage);
 		
    }       

		
	

    private RequestContent createResponse(String messageType, String MTCode, String messageText){
    	RequestContent  res = new RequestContent();
		Item resItem  =  new Item();
		resItem.setKey("Type");
		resItem.setValue(messageType);
		res.addItem(resItem);
		
		resItem  =  new Item();
		resItem.setKey("MTCode");
		resItem.setValue(MTCode);
		res.addItem(resItem);
		
		resItem  =  new Item();
		resItem.setKey("Message");
		resItem.setValue(messageText);
		res.addItem(resItem);
		
		return res;		
    }

            /**
        	 * @param inStr
        	 * @param maxSize
        	 * @return String
        	 * Pad cellsiteid with leading Zeros if less than maxSize digits  
        	 */
        	public String padStr(String inStr, int maxSize)
        	{	
        		if(inStr == null || inStr.length() == 0)
        			 return inStr;
        		String outStr = new String(inStr);
        		char chr = inStr.toUpperCase().charAt(inStr.length() - 1);	
        		char chr2 = inStr.toUpperCase().charAt(inStr.length() - 2);
        		if(Character.isLetter(chr))
        			inStr =inStr.substring(0,(inStr.length() - 1)); 
        		if(Character.isLetter(chr2))
        			inStr =inStr.substring(0,(inStr.length() - 1)); 
        		
        		StringBuffer cid = new StringBuffer(inStr);
        		try
        		{			
        			Integer.parseInt(inStr);
        			if(cid.length() < maxSize)
        			{
        				int pp=0;
        				//need to pad the cellsiteId to 6 digits
        				pp = maxSize - cid.length();
        				char cc[] = new char[pp];
        				for(int i=0; i < pp; i++)
        				{
        					cc[i] = '0';
        				}
        				logger.info("padding  + '" + inStr + "' by " + pp + " Zeros" );
        				cid.insert(0,cc);
        				//log.info("CellSite() is now  " + cid );
        			}
        			else if(cid.length() > maxSize)
        			{
        				logger.info("[before]cellsiteid " +  inStr + " is more than "+ maxSize +" digits");
        				logger.info("Removing digits if padded with too many");
        				while(cid.length() >  maxSize )
        				{      			  
        					cid.deleteCharAt(0);
        				}
        				logger.info("[after]cellsiteid " +  inStr + " is now "+ maxSize +" digits");
        			}
        			
        			if(Character.isLetter(chr2))
        				cid.append(chr2);
        			if(Character.isLetter(chr) /*&& (chr == 'B' || chr == 'G')*/) //Add the letter back
        				cid.append(chr);
        		}
        		catch (NumberFormatException ex)
        		{
        			//DO nothing assuming that this is a SSR
        			cid = new StringBuffer(outStr.toUpperCase());
        		} 
        		return cid.toString();
        	}
        	public String padStr2(String inStr, int maxSize)
        	{	
        		if(inStr == null || inStr.length() == 0)
        			 return inStr;
        		String outStr = new String(inStr);
        		char chr = inStr.toUpperCase().charAt(inStr.length() - 1);	
        		if(Character.isLetter(chr))
        			inStr =inStr.substring(0,(inStr.length() - 1)); 
        		
        		StringBuffer cid = new StringBuffer(inStr);
        		try
        		{			
        			Integer.parseInt(inStr);
        			if(cid.length() < maxSize)
        			{
        				int pp=0;
        				//need to pad the cellsiteId to 6 digits
        				pp = maxSize - cid.length();
        				char cc[] = new char[pp];
        				for(int i=0; i < pp; i++)
        				{
        					cc[i] = '0';
        				}
        				logger.info("padding  + '" + inStr + "' by " + pp + " Zeros" );
        				cid.insert(0,cc);
        				//log.info("CellSite() is now  " + cid );
        			}
        			else if(cid.length() > maxSize)
        			{
        				logger.info("[before]cellsiteid " +  inStr + " is more than "+ maxSize +" digits");
        				logger.info("Removing digits if padded with too many");
        				while(cid.length() >  maxSize )
        				{      			  
        					cid.deleteCharAt(0);
        				}
        				logger.info("[after]cellsiteid " +  inStr + " is now "+ maxSize +" digits");
        			}
        			if(Character.isLetter(chr) /*&& (chr == 'B' || chr == 'G')*/) //Add the letter back
        				cid.append(chr);
        		}
        		catch (NumberFormatException ex)
        		{
        			//DO nothing assuming that this is a SSR
        			cid = new StringBuffer(outStr.toUpperCase());
        		} 
        		return cid.toString();
        	}    	
        	
        	
	private void populateRequestTable(RequestContent request){
		
		requestTable.clear();
		List<Item> item  = request.getItem();
		for (Iterator iterator = item.iterator(); iterator.hasNext();) {
			Item item2 = (Item) iterator.next();
			requestTable.put(item2.getKey().toUpperCase(), item2.getValue());
			logger.trace(item2.toString());
		}	
	}

	public WorkQueue getWorkQueue() {
		return workQueue;
	}


	public void setWorkQueue(WorkQueue workQueue) {
		this.workQueue = workQueue;
	}


	public WorkQueue getWorkQueueCIC() {
		return workQueueCIC;
	}


	public void setWorkQueueCIC(WorkQueue workQueueCIC) {
		this.workQueueCIC = workQueueCIC;
	}


	public CommunicateWithTT getTtServer() {
		return ttServer;
	}


	public void setTtServer(CommunicateWithTT ttServer) {
		this.ttServer = ttServer;
	}
	
}