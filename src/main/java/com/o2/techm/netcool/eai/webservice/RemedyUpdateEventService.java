package com.o2.techm.netcool.eai.webservice;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.o2.techm.netcool.eai.o2gateway.netcool.O2GatewayReaderImpl;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayEventType;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPair;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPairList;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNetcoolEvent;

import remedy.webservices.osiris.o2.com.Auth;

@Component
public class RemedyUpdateEventService extends HttpServlet{	

	private static final String PROPERTIES_FILE = "osiris-servlets";
	public static final String CIC_FAULT_STATUS_FIELD = "FaultStatus";
	public static final String CIC_TROUBLE_TICKET_FIELD = "TroubleTicket";
	public static final String CIC_FAULT_PRIORITY_FIELD = "FaultPriority";
	public static final String CIC_FAULT_QUEUE_FIELD = "FaultQueue";
	public static final String CIC_FAULT_OWNER_FIELD = "FaultOwner";
	public static final String CIC_FAULT_IMPACT_FIELD = "FaultImpact";
	public static final String CIC_URGENCY_FIELD = "Urgency";
	public static final String CIC_IDENTIFIER_FIELD = "Identifier";
	public static final String CIC_SERVER_SERIAL_FIELD = "ServerSerial";
	public static final String CIC_POLL_FIELD = "Poll";
	public static final String PO_POLL = "0"; 

	private static final int ERROR_OUTPUT = 1;
	private static final int SUCCESSFUL_OUTPUT = 0;

	private static final String WS_USER = "osiris.updateincident.user"; 
	private static final String WS_USER_DEFAULT = "remedy";    
	private static final String WS_PASS = "osiris.updateincident.pass"; 
	private static final String WS_PASS_DEFAULT = "WScic"; 
	private static final Logger log = LoggerFactory.getLogger(RemedyUpdateEventService.class);


	String sucessResposne = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><ns3:updateIncidentResponse xmlns:ns3=\"http://com.o2.osiris.webservices.remedy\"><UptResponse><errorCode>0</errorCode><errorMsg>Update Successful!!</errorMsg></UptResponse></ns3:updateIncidentResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
	String failedResposne = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><ns3:updateIncidentResponse xmlns:ns3=\"http://com.o2.osiris.webservices.remedy\"><UptResponse><errorCode>1</errorCode><errorMsg>Update Failed!!</errorMsg></UptResponse></ns3:updateIncidentResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";

	protected O2GatewayReaderImpl reader;
	private ResourceBundle osirisProperties;

	public void doPost( HttpServletRequest req, HttpServletResponse res ) 
			throws IOException, ServletException
	{
		try{
			StringBuffer sb = new StringBuffer();
			InputStream myInStream = req.getInputStream(); 
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();

			byte[] buffer = new byte[4096];
			int totalSizse = 0;
			int length;
			while ((length = myInStream.read(buffer)) > 0) {
				bOut.write(buffer, 0, length);
				totalSizse = totalSizse + length;
			}		
			String rawRequestPayload  =  bOut.toString();
			rawRequestPayload = rawRequestPayload.replace(System.getProperty("line.separator"), "");
			log.debug("UpdateIncidentServiceEndPoint :  in updateIncident()  Request Objet = " + rawRequestPayload);


			Auth authentication = extractAuthInfo(rawRequestPayload);
			log.debug("UpdateIncidentServiceEndPoint : UserName = " + authentication.getUsername() + "  Password  =  " + authentication.getPassword());    	

			if(authorizeOperation(authentication)){
				NetcoolGatewayNetcoolEvent event =null;
				if(!emptyRequiredFields(rawRequestPayload)){
					event = createUpdateEvent(rawRequestPayload);

					if (null != event)
					{
						log.debug("Invoking on netcool reader...");
						String result = null;

						log.info("sending event , value of event = "+ event.toString());
						result = reader.event(event);

						log.debug("Invoked on reader; got result '" + result + "'"); 
					}
					else
					{
						throw new Exception("Failed to send event to "
								+ "Netcool : Incorrect Message Format");
					}


				}else{
					log.warn("Empty fields received from Remedy");
					throw new Exception("Empty fields received from Remedy");

				}
			}else{
				log.warn("User unauthorized: "+authentication.getUsername()+"/"+authentication.getPassword());
				throw new Exception("User unauthorized: "+authentication.getUsername()+"/"+authentication.getPassword());
			}

			sendResponse(sucessResposne,res);
		}catch(Exception e){
			log.error("Exception thrown:" ,e); 
			sendResponse(failedResposne,res);
		}

	}



	private void sendResponse( String message , HttpServletResponse response ){
		FileInputStream in = null;
		try {	
			ServletOutputStream out = response.getOutputStream();
			response.setContentType("text/xml;charset=utf-8");
			response.setContentLength(message.length());

			out.write(message.getBytes());							
			out.flush();

		} catch (Exception e) {				
			e.printStackTrace();
		} finally{
			if(null != in ) {
				try {
					in.close();
				} catch (IOException e) {						
				}
			}
		}
	}

	private boolean emptyRequiredFields(String mesage){
		boolean emptyFields = false;
		String externalID = mesage.substring(mesage.indexOf("<externalID>") + "<externalID>".length(), mesage.indexOf("</externalID>"));
		String incidentID = mesage.substring(mesage.indexOf("<incidentID>") +"<incidentID>".length() , mesage.indexOf("</incidentID>"));

		if((null!= externalID && externalID.equals("") )||
				(null != externalID && externalID.equals("") )){
			emptyFields = true;
		}

		return emptyFields;
	}

	private Auth extractAuthInfo(String mesage){
		Auth authorization  = new Auth();
		try {

			String userName = "";
			String pass ="";

			try {
				userName =		mesage.substring(mesage.indexOf("<username>") + "<username>".length(), mesage.indexOf("</username>"));
			} catch (Exception e) {

			}

			try {
				pass = mesage.substring(mesage.indexOf("<password>") + "<password>".length(), mesage.indexOf("</password>"));
			} catch (Exception e) {

			}



			authorization.setUsername(userName);
			authorization.setPassword(pass);
		} catch (Exception e) {
			//e.printStackTrace();
		} 
		return authorization;

	}

	public NetcoolGatewayNVPair createNVPair(String name, String value) 
	{
		NetcoolGatewayNVPair ret = new NetcoolGatewayNVPair();
		ret.setAName(name);
		ret.setAValue(value);
		return ret; 
	}

	private NetcoolGatewayNetcoolEvent createUpdateEvent(String mesage)
	{
		NetcoolGatewayNetcoolEvent event = new NetcoolGatewayNetcoolEvent();
		NetcoolGatewayNVPairList attributes = new NetcoolGatewayNVPairList();
		NetcoolGatewayNVPair nvpair[] = new NetcoolGatewayNVPair[10];
		event.setType_event(NetcoolGatewayEventType.updateET);




		String asignee = "";
		String status ="";
		String externalID ="";
		String impact = "";
		String priority = "";
		String incidentID = "";
		String serverSerial ="";
		String suppOrg_grName ="";
		String urgency ="";


		try {
			asignee = mesage.substring(mesage.indexOf("<asignee>") + "<asignee>".length(), mesage.indexOf("</asignee>"));
		} catch (Exception e) {

		}


		try {
			status = mesage.substring(mesage.indexOf("<status>") + "<status>".length(), mesage.indexOf("</status>"));
		} catch (Exception e) {

		}
		try {
			externalID = mesage.substring(mesage.indexOf("<externalID>") + "<externalID>".length(), mesage.indexOf("</externalID>"));
		} catch (Exception e) {

		}


		try {
			impact = mesage.substring(mesage.indexOf("<impact>") + "<impact>".length(), mesage.indexOf("</impact>"));
		} catch (Exception e) {

		}

		try {
			priority = mesage.substring(mesage.indexOf("<priority>") + "<priority>".length(), mesage.indexOf("</priority>"));
		} catch (Exception e) {

		}

		try {
			incidentID = mesage.substring(mesage.indexOf("<incidentID>") +"<incidentID>".length() , mesage.indexOf("</incidentID>"));
		} catch (Exception e) {

		}

		try {
			serverSerial = mesage.substring(mesage.indexOf("<serverSerial>") + "<serverSerial>".length(), mesage.indexOf("</serverSerial>"));
		} catch (Exception e) {

		}


		try {
			suppOrg_grName = mesage.substring(mesage.indexOf("<suppOrg_grName>") + "<suppOrg_grName>".length(), mesage.indexOf("</suppOrg_grName>"));
		} catch (Exception e) {

		}
		try {
			urgency = mesage.substring(mesage.indexOf("<urgency>") + "<urgency>".length(), mesage.indexOf("</urgency>"));
		} catch (Exception e) {

		}



		nvpair[0] = createNVPair(CIC_FAULT_STATUS_FIELD, status);
		nvpair[1] = createNVPair(CIC_TROUBLE_TICKET_FIELD, incidentID);
		nvpair[2] = createNVPair(CIC_FAULT_PRIORITY_FIELD, priority);
		nvpair[3] = createNVPair(CIC_FAULT_QUEUE_FIELD, suppOrg_grName);
		nvpair[4] = createNVPair(CIC_FAULT_OWNER_FIELD, asignee);
		nvpair[5] = createNVPair(CIC_FAULT_IMPACT_FIELD, impact);        
		nvpair[6] = createNVPair(CIC_URGENCY_FIELD, urgency); 
		nvpair[7] = createNVPair(CIC_IDENTIFIER_FIELD, externalID);
		nvpair[8] = createNVPair(CIC_SERVER_SERIAL_FIELD, serverSerial);
		nvpair[9] = createNVPair(CIC_POLL_FIELD, PO_POLL);

		attributes.setItem(nvpair);   
		event.setANVPairList(attributes);

		event.setEventId(externalID);

		return event;
	}


	private boolean authorizeOperation(Auth auth){
		String user = "";
		String pass = "";

		user = getProperty(WS_USER, WS_USER_DEFAULT);
		pass = getProperty(WS_PASS, WS_PASS_DEFAULT);

		if(auth.getUsername().equals(user) &&
				auth.getPassword().equals(pass)){
			return true;
		}
		else{
			return false;
		}

	}



	public String getProperty(String name, String defaultValue)
	{
		String value = null;

		try { 
			value = osirisProperties.getString(name);
		}
		catch (MissingResourceException e) {
			log.error("Variable " + name + " not found in properties file; using the default value '" + defaultValue + "'");
			value = defaultValue;
		}
		return value;
	}

	public void readCfg()  throws Exception
	{ 

		// osirisProperties = ResourceBundle.getBundle(PROPERTIES_FILE); 
		try {
			osirisProperties = new PropertyResourceBundle(Files.newInputStream(Paths.get("conf/"+PROPERTIES_FILE+".properties")));
		} catch (IOException e) {
			log.error("Not able to load configuration file conf/"+PROPERTIES_FILE+".properties Failing early, failing loud.", e);
			e.printStackTrace();
			System.exit(1);
		}
	}



	public O2GatewayReaderImpl getReader() {
		return reader;
	}

	public void setReader(O2GatewayReaderImpl reader) {
		this.reader = reader;
	}

}
