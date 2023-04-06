
package com.o2.techm.netcool.eai.manonsite.broker.wintel;
 
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.broker.BrokerConnection;
import com.o2.techm.netcool.eai.manonsite.broker.BrokerException;
import com.o2.techm.netcool.eai.manonsite.common.Configuration;
import com.o2.techm.netcool.eai.manonsite.common.Constants;

public class WintelBrokerConnection implements BrokerConnection 
{	
	public  static  final String MAXIM_SERVID="11";
	private static  final String MAXIM_CostID="2";
	private static  final String WINTEST_CostID="1";
	private static  final String LONGCODE_CostID="3";
	private static  final String WINTEST_SERVID="4";
	private static  final int typeID=2;
	public  static  final int WINTRANID_LIMIT = 10000000;
	public  static  final String[][] ServiceDesp= {{"1","WIN Test Service","WINTEST"},{"4","ManOnsite","62946"},{"4","Osiris","62946"}};
	private static  final int TEXTSIZE=160;
	private static final Logger log = LoggerFactory.getLogger(WintelBrokerConnection.class);
	  
	private static String username;
	private static String password;	  	    
	static final String testKeyword = "WINO2MANST";	 
	public static final String SMSFAILED="SMS SEND FAILURE";
	private static final int MAX_XML = 5; 
	private static int maxRetry;
	Properties systemProperties=System.getProperties();;   
	static String proxyIP = "";
	static int proxyPort = 80;
	static boolean autoDetectProxy = false;  // If SSL version, assume autoDetect, else do not
	static boolean useProxy = false;
	
	public static final String ENC = "UTF-8";
	public static Random generator =new Random();
	private String urlStr;
	public WintelBrokerConnection()
	{
		
		WintelBrokerConnection.password = Configuration.getValue(Configuration.WINTEL_BROKER_PASSWORD);
		WintelBrokerConnection.username = Configuration.getValue(Configuration.WINTEL_BROKER_USER); 
		urlStr = Configuration.getValue(Configuration.MOS_SERVLET_URL);
		log.debug("Url :" + urlStr);
		WintelBrokerConnection.maxRetry = Integer.parseInt(Configuration.getValue(Configuration.MANONSITE_MAX_RETRY));
		log.debug("Max Retry :" + WintelBrokerConnection.maxRetry);
	}

	public WintelBrokerConnection(String password, String username, String urlStr)
	{
		WintelBrokerConnection.password = password;
		WintelBrokerConnection.username = username;
		this.urlStr = urlStr; 
		log.debug("Url :" + urlStr);
	}

	//connectProxyServer
	//Initialises Global variable useProxy
	Integer lockObj = new Integer(0);

	
	public String sendApacheCommonsHTTPClientReq(String request)throws BrokerException{
		log.debug("Creating the HTTP connection for URL  = " + urlStr +" with Payload  = " + request); 
		StringBuffer uCred=new StringBuffer(request); ;
		String httpResponse= null; 	
		try {
			log.debug(" RAW request XM to MOS ProxyL = " + uCred.toString());
			if(!URLDecoder.decode(request,WintelBrokerConnection.ENC).toLowerCase().matches("user=.*&password=.*&requestid=.*"))
			{
				uCred= new StringBuffer("User=").append(username).append("&Password=").append(password)
						.append("&RequestID=").append(Math.abs(generator.nextLong() % WintelBrokerConnection.WINTRANID_LIMIT))
						.append("&WIN_XML=").append(URLEncoder.encode(request,WintelBrokerConnection.ENC));
			}				 
			log.debug("Encoded SMS Request  Message  to MOS Proxy:" +  uCred);
		} catch (UnsupportedEncodingException e) {
			log.error("problem while Encoading MOS sender request",e);
			e.printStackTrace();
			throw new BrokerException(e);
		}

		boolean isSuccess=true;
		int retry=0;
		do
		{
			isSuccess=true;
			try
			{   

				HttpClient client = new HttpClient();
				PostMethod httppost = new PostMethod(this.urlStr);
				httppost.setRequestBody(uCred.toString());
			//	httppost.addRequestHeader("Content-type", "application/x-www-form-urlencoded");			
				httppost.addRequestHeader("Content-type", "text/html");
				
				int statusCode = client.executeMethod(httppost);

				if (statusCode != HttpStatus.SC_OK) {
					log.debug("Call to  Mos sender was unsecssfull: " + httppost.getStatusLine());
				}else{
					
					byte[] responseBody = httppost.getResponseBody();
					httpResponse = new String(responseBody) ;
					log.debug("Response from MOS Proxy  recived  = " + httpResponse);
					isSuccess = true ;
					break;
				}
			}catch (Exception e)
			{        

				httpResponse=e.getMessage();
				log.error("Exception caught : " + httpResponse);
				isSuccess=false;				
				retry++;
			}
		}while(!isSuccess && retry < maxRetry);

		if(!isSuccess && retry >= maxRetry)
		{
			log.error(" Mos senedr con't be reached after " + maxRetry +" re tries ");
			throw new BrokerException(" Mos senedr con't be reached after " + maxRetry +" re tries "); 
		}
		return httpResponse;
	}

		 

	
	static String addPlus(String phoneNumber)
	{
		if(!phoneNumber.startsWith("+"))
			phoneNumber  = "+" + phoneNumber;
		return phoneNumber;
	}
	
	static String getSourceAddr(String source_addr)
	{
		String SourceAddr = source_addr;//= WintelBrokerConnection.ServiceDesp[1][2];
		if( source_addr != null && source_addr.equalsIgnoreCase(WintelBrokerConnection.ServiceDesp[2][1]))
		{
			log.debug("This should be OSIRIS Service");  
			SourceAddr= WintelBrokerConnection.ServiceDesp[2][2];			 
		}
		else if(source_addr != null && source_addr != null && source_addr.equalsIgnoreCase(WintelBrokerConnection.ServiceDesp[0][1]))
		{
			log.debug("This should be WINTEST Service");  
			SourceAddr= WintelBrokerConnection.ServiceDesp[0][2];			 
		}
		else if(source_addr != null && source_addr != null && source_addr.equalsIgnoreCase(WintelBrokerConnection.ServiceDesp[1][1]))
		{
			log.debug("This should be MANONSITE Service");  
			SourceAddr= WintelBrokerConnection.ServiceDesp[1][2];;			 
		}
		log.debug("SourceAddr:" + SourceAddr);
		return /*WintelBrokerConnection.ServiceDesp[0][2]*/ SourceAddr;
	}
	private static String createSendSimpleMessageXMLRequest( 
			String[] phoneNumbers,
			String[] messages,
			String source_addr,
			String costId,
			String servId,
			long transID)
	{		 
		StringBuffer outXml = new StringBuffer("<?xml version=\"1.0\" standalone=\"no\"?>" 
				+ "<!DOCTYPE SMSRESPONSE SYSTEM \"" + Constants.WIN_MESSAGE_DTD_DEF + "\">"
				+ "<WIN_DELIVERY_2_SMS>") ;
		// Add message
		int r = Math.abs(generator.nextInt(Integer.MAX_VALUE) % 10000);  

		outXml.append("<SMSMESSAGE>");
		for (int i= 0; i < phoneNumbers.length; i++) 
		{				 
			outXml.append("<DESTINATION_ADDR>" + addPlus(phoneNumbers[i]) + "</DESTINATION_ADDR>");	  
		}
		for (int j = 0; j < messages.length; j++) 
		{
			outXml.append("<TEXT>" + messages[j] + "</TEXT>");
		}
		int start_text=1000;
		//outXml.append("<TRANSACTIONID>" + r + "</TRANSACTIONID>")
		outXml.append("<TRANSACTIONID>").
		append(start_text + 1).append(":").append(start_text + messages.length).append("-").append(r).
		append("</TRANSACTIONID>")
		.append("<TYPEID>" + typeID + "</TYPEID>") 
		.append("<SERVICEID>" + servId + "</SERVICEID>")
		.append("<COSTID>" + costId+ "</COSTID>")
		//+ "<DELIVERYRECEIPT>" + 13 + "</DELIVERYRECEIPT>");	
		.append("<WINTRANSACTIONID>" + transID + "</WINTRANSACTIONID>")
		.append("<SOURCE_ADDR>" + getSourceAddr(source_addr) + "</SOURCE_ADDR>")
		.append("</SMSMESSAGE>"); 

		outXml.append("</WIN_DELIVERY_2_SMS>");     
		return outXml.toString();
	}    

	private static String createSendSimpleMessageXMLRequest(String phoneNumber, String msg, String source_addr,String costId, String servId, long winTransID)
	throws BrokerException
	{		 
		String outXmlStr=null;
		if(!msg.matches(".*\n.*"))		
		{
			int r = Math.abs(generator.nextInt(Integer.MAX_VALUE) % 10000);		 
			StringBuffer outXml = new StringBuffer("<?xml version=\"1.0\" standalone=\"no\"?>");
			outXml.append("<!DOCTYPE WIN_DELIVERY_2_SMS SYSTEM \"").append(Constants.WIN_MESSAGE_DTD_DEF + "\">")
			.append("<WIN_DELIVERY_2_SMS> <SMSMESSAGE>")
			.append("<DESTINATION_ADDR>").append(addPlus(phoneNumber))
			.append("</DESTINATION_ADDR>");    

			if(msg.length() > 160)
			{
				int ff = Math.round(msg.length()/160);
				String messages[] = new String [ff+1]; 
				StringBuffer buf = new StringBuffer(msg);
				int j=0;
				int start=0; 
				int start_text=1000;
				if (log.isDebugEnabled()) log.debug("buf length:" + buf.length()); 				
				while(buf.length() > 0)
				{						 
					messages[j] = buf.length() > TEXTSIZE ? buf.substring(start,TEXTSIZE -1) : buf.substring(start);						
					if (log.isDebugEnabled()) log.debug("Sending message[" + j + "] : " + messages[j]); 
					outXml.append("<TEXT><![CDATA[").append(messages[j]).append("]]></TEXT>");
					buf = new StringBuffer(buf.substring(messages[j].length()));	
					if (log.isDebugEnabled()) log.debug("buf length:" + buf.length()); 
					j++;					
				}
				outXml.append("<TRANSACTIONID>").
				append(start_text + 1).append(":").append(start_text + j).append("-").append(r).
				append("</TRANSACTIONID>");
			}
			else
			{
				outXml.append("<TEXT><![CDATA[").append(msg).append("]]></TEXT>").
				append("<TRANSACTIONID>").append(r).append("</TRANSACTIONID>");
			}

			outXml.append("<TYPEID>").append(typeID).append("</TYPEID>").
			append("<SERVICEID>").append(servId).append("</SERVICEID>").	
			append("<COSTID>").append(costId).append("</COSTID>").	
			//+ "<DELIVERYRECEIPT>" + 13 + "</DELIVERYRECEIPT>"	
			append("<WINTRANSACTIONID>").append(winTransID).append("</WINTRANSACTIONID>").	
			append("<SOURCE_ADDR>").append(getSourceAddr(source_addr)).append("</SOURCE_ADDR>").
			append("</SMSMESSAGE></WIN_DELIVERY_2_SMS>"); 	
			outXmlStr=outXml.toString();

		}
		else
		{
			log.debug("Text Message has new line");
			String[] msgs = msg.split("\n");
			String [] phoneNumbers = new String[1];
			phoneNumbers[0]=phoneNumber;
			outXmlStr=createSendSimpleMessageXMLRequest(phoneNumbers, msgs,source_addr, costId,servId,winTransID); 
		}		
		return outXmlStr;
	}
	 
	public String sendSimpleSMS(String phoneNumber, String message)
	throws BrokerException {
		// TODO Auto-generated method stub 
		int winTransID=generator.nextInt(Integer.MAX_VALUE) % WintelBrokerConnection.WINTRANID_LIMIT;
		//log.debug("Using WINTEST  costid and  servid values");		
		//return simpleSMS(phoneNumber, message,ServiceDesp[1][1], WINTEST_SERVID,WINTEST_CostID, winTransID);
		
			return simpleSMS(phoneNumber, message,WINTEST_SERVID,winTransID);	
	}

	public String sendSimpleSMS(String phoneNumber, String message, String shortcode)
	throws BrokerException {		 
		// TODO Auto-generated method stub
		String request=null;
		int winTransID=generator.nextInt(Integer.MAX_VALUE) % WintelBrokerConnection.WINTRANID_LIMIT;
		
		log.info("winTransID = " + winTransID);	
		log.info("Shortcode = " + shortcode);
		if(!shortcode.equals("CON"))
		{
			try
			{
				
				if ((shortcode.equals("+447860002789")) || (shortcode.equals("00447860002789")) || (shortcode.equals("447860002789")))
				{
					log.info("phoneNumber = " + phoneNumber);
					log.info("message = " + message);
					log.info("Using LONGCODE  costid and  servid values");
					request=simpleSMS(phoneNumber, message,shortcode, MAXIM_SERVID,LONGCODE_CostID,/*MAXIM_SERVID,LONGCODE_CostID,*/ winTransID);
					
				}
				
				else if(Integer.parseInt(shortcode) == 62946)
				//if(shortcode.equals("62946"))
				{
					log.info("Using WINTEST  costid and  servid values");
					request=simpleSMS(phoneNumber, message,ServiceDesp[1][1], WINTEST_SERVID,WINTEST_CostID, winTransID);
					log.debug("sendSimpleSMS  resposne  =  " + request  );
				}			
			}
			catch(NumberFormatException nfe)
			{	
				//if (Integer.parseInt(shortcode) == 62946){
				//log.debug("Using WINTEST  costid and  servid values");
				//request=simpleSMS(phoneNumber, message,shortcode, WINTEST_SERVID,WINTEST_CostID, winTransID);
				//}  Abi(String phoneNumber, String message,String serviceid, long winTransID, String shortcode)
				//else{ (String phoneNumber, String message,String source_addr,String servId, String costId, long winTransID)
			//	log.debug("Using WINTEST costid and  servid values");
				request=simpleSMS(phoneNumber, message,winTransID, shortcode);
				//}
			}
			
		}
		else	
			request=simpleSMS(phoneNumber, message,MAXIM_SERVID,winTransID);		
		return request;
	}

	public String sendSimpleSMS(String[] phoneNumbers, String[] messages,
			String costId,String servId) throws BrokerException {
		// TODO Auto-generated method stub
		String response  = null;
	    long transID=generator.nextInt(Integer.MAX_VALUE) % WintelBrokerConnection.WINTRANID_LIMIT;
		int s=0; 		
		do
		{ 			
			String msgs [] = messages;
			if(messages.length > MAX_XML)
			{		
				int arr_size = messages.length - s <= MAX_XML? messages.length - s : MAX_XML;
				System.arraycopy(messages, s, msgs,0, arr_size);
				log.debug("s" + s + " , msgs[" + s + " ]= " + messages[s] + " arr_size: " + arr_size + " messages.length: " + messages.length);
				s+=msgs.length;	
				log.debug("Sending the next" + s + "SMS messages at a time");				
			}		
			
			
			String request = createSendSimpleMessageXMLRequest(phoneNumbers, msgs, ServiceDesp[1][1],costId != null? costId : MAXIM_CostID, servId != null? servId : MAXIM_SERVID, transID);
			if (log.isDebugEnabled()) log.debug("request\n" + request + "\n");
			//response =SendHttpRequest(request);
			response = sendApacheCommonsHTTPClientReq(request);
		}while(messages.length - s > 0);
		return response;
	}
	 
	public String simpleSMS(String phoneNumber, String message,String shortcode, long winTransID) throws BrokerException
	{		
		String request = createSendSimpleMessageXMLRequest(phoneNumber, message, ServiceDesp[1][1], MAXIM_CostID,shortcode,winTransID); 
		log.info("request1\n" + request + "\n"); 
		if (log.isDebugEnabled()) log.debug("request\n" + request + "\n");         
		//return SendHttpRequest(request);		
		return sendApacheCommonsHTTPClientReq(request);
	}
	
	public String simpleSMS(String phoneNumber, String message, long winTransID, String shortcode) throws BrokerException
	{		
		log.info("Estoy en el simpleSMS de Abi");
		if ((shortcode.equals("+447860002789")) || (shortcode.equals("00447860002789")) || (shortcode.equals("447860002789")))
		{
		String request = createSendSimpleMessageXMLRequest(phoneNumber, message, shortcode, LONGCODE_CostID, MAXIM_SERVID,winTransID); 
		if (log.isDebugEnabled()) log.debug("request\n" + request + "\n");         
		//return SendHttpRequest(request);
		return sendApacheCommonsHTTPClientReq(request);
		}
		else{
			String request = createSendSimpleMessageXMLRequest(phoneNumber, message, shortcode, WINTEST_CostID, WINTEST_SERVID,winTransID); 
			if (log.isDebugEnabled()) log.debug("request\n" + request + "\n");   
			//return SendHttpRequest(request);
			return sendApacheCommonsHTTPClientReq(request);
		}
	}

	public String simpleSMS(String phoneNumber, String message,String source_addr,String servId, String costId, long winTransID) throws BrokerException
	{		
		   
		String request = createSendSimpleMessageXMLRequest(phoneNumber, message,source_addr, costId,servId,winTransID); 
		log.info("Request XML for Mos Sender = " + request );          
		//return SendHttpRequest(request);	
		return  sendApacheCommonsHTTPClientReq(request);
	}
	
	
}
