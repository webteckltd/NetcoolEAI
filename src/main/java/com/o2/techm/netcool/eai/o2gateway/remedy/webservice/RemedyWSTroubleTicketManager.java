/* Modification  History :
 * Date          Version  Modified by     Brief Description of Modification
 * 12-Jun-2014   1.0      Indra               CD38583 - BMR                                                                                           
 */

package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.jws.HandlerChain;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.netcool.BasicCICEventHandling;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicket;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicketException;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicketManager;
import com.o2.util.WorkQueue;

@HandlerChain(file = "/data/artix/logs/osiris/LoggingHandler.xml")
public class RemedyWSTroubleTicketManager implements TroubleTicketManager {

	private static final Logger log = LoggerFactory.getLogger(RemedyWSTroubleTicketManager.class);

	// Update TT Action constants
	private String ACTION_CREATE = "CREATE";
	private String ACTION_UPDATE = "ADDWI";
	//private String COMPANY = "TELEFONICA UK";
	private String COMPANY = "VMO2";
	private String WORK_INFO_SUMMARY = "Netcool Note";
	private String alarmSeverity = null;

	//private String alarmSeverity = null;
	
	private String RESPONSE_SUCCESS = "0";

	public RemedyWSTroubleTicketManager(String login, String password,
			String url, String cic_date_format) throws TroubleTicketException {

		log.debug("Initialising connection to Remedy... ");
		RemedyWSConfiguration.setLogin(login);
		RemedyWSConfiguration.setPassword(password);
		RemedyWSConfiguration.setUrl(url);
		RemedyWSConfiguration.setCic_date_format(cic_date_format);
	}

	public void createTroubleTicket(TroubleTicket troubleTicket)
			throws TroubleTicketException {
		// Output parameter
		Holder<String> assigned_Group = new Holder<String>();
		Holder<String> incidentID = new Holder<String>();
		Holder<String> errorMsg = new Holder<String>();
		Holder<String> errorCode = new Holder<String>();

		// Alarm_Severity
		if (troubleTicket.getAlarm_Severity().equals("Clear")) {
			alarmSeverity = "0";
		} else if (troubleTicket.getAlarm_Severity().equals("Indeterminate")) {
			alarmSeverity = "1";
		} else if (troubleTicket.getAlarm_Severity().equals("Warning")) {
			alarmSeverity = "2";
		} else if (troubleTicket.getAlarm_Severity().equals("Minor")) {
			alarmSeverity = "3";
		} else if (troubleTicket.getAlarm_Severity().equals("Major")) {
			alarmSeverity = "4";
		} else if (troubleTicket.getAlarm_Severity().equals("Critical")) {
			alarmSeverity = "5";
		}
		// Priority
		Holder<PriorityType> priority = new Holder<PriorityType>();
		/*
		try {
			priority = new Holder<PriorityType>(
					PriorityType.fromValue(troubleTicket.getFaultPriority()));
			//priority = new Holder<PriorityType>(PriorityType.LOW);

		} catch (Exception e) {
			log.error("(RAISE/CLOSE WS) Problem with Priority parameter: "	+ troubleTicket.getFaultPriority()
					+". NetcoolID: " + troubleTicket.getIdentifier());
			throw new TroubleTicketException("Priority "
					+ troubleTicket.getFaultPriority() + " wrong");
		}
		*/

		// First_Ocurrence Check
		XMLGregorianCalendar firstOcurrrence = null;
		try {
			firstOcurrrence = stringToXMLGregorianCalendar(troubleTicket
					.getFirstOccurrence());
		} catch (Exception e) {
			log.error("(RAISE/CLOSE WS) FirstOcurrrence can't be empty. NetcoolID: " 
					+ troubleTicket.getIdentifier(), e);
			throw new TroubleTicketException("FirstOcurrrence can't be empty"
					+ e.getMessage());
		}

		// Indicate Status
		Holder<StatusType> status = new Holder<StatusType>();

		if (BasicCICEventHandling.TTFLAG_RAISE
				.equals(troubleTicket.getTTFlag())) {
			status = new Holder<StatusType>(StatusType.NEW);

		} else if (BasicCICEventHandling.TTFLAG_RAISE_AND_CLOSE
				.equals(troubleTicket.getTTFlag())) {
			status = new Holder<StatusType>(StatusType.CLOSED);
		}

		// ImpacType
		ImpactType impactType = null;
		if(troubleTicket.getFaultImpact()!=null && !troubleTicket.getFaultImpact().equals("")){
			try {
				impactType =
						ImpactType.fromValue(troubleTicket.getFaultImpact());
				//impactType = ImpactType.MODERATE_LIMITED;
			} catch (Exception e) {
				log.error("(RAISE/CLOSE WS) ImpactType doesn't match. NetcoolID: " 
							+ troubleTicket.getIdentifier(), e);
				throw new TroubleTicketException("ImpactType doesn't match : "
						+ e.getMessage());
			}
		}

		// Urgency
		UrgencyType urgency = null;
		if(troubleTicket.getUrgency()!=null && !troubleTicket.getUrgency().equals("")){
			try {
				// urgency = UrgencyType.MEDIUM;
				urgency = UrgencyType.fromValue(troubleTicket.getUrgency());
			} catch (Exception e) {
				log.error("(RAISE/CLOSE WS) Urgency doesn't match. NetcoolID: " 
							+ troubleTicket.getIdentifier(), e);
				throw new TroubleTicketException("Urgency doesn't match : "
						+ e.getMessage());
			}
		}
		
		AuthenticationInfo parameterAuthentication = new AuthenticationInfo();
		try {
			parameterAuthentication.setUserName(RemedyWSConfiguration
					.getLogin());
			parameterAuthentication.setPassword(RemedyWSConfiguration
					.getPassword());
			parameterAuthentication.setLocale("en_GB");

		} catch (Exception e) {
			log.error("(RAISE/CLOSE WS) Problem with Authentication parameter, "
					+ "Login:  " + RemedyWSConfiguration.getLogin() 
					+ "Pass: " + RemedyWSConfiguration.getPassword()
					+ ". NetcoolID: " + troubleTicket.getIdentifier());
			throw new TroubleTicketException(
					"Authentication parameter can't be empty");
		}

		String severity = "Severity: " + troubleTicket.getSeverity() + "\n"
				+ troubleTicket.getDetails();	
		
		try {
			TGTUKNETCOOLHPDWSService service = new TGTUKNETCOOLHPDWSService();

			// Loggin request
			service.setHandlerResolver(new JaxWsHandlerResolver());

			HPDIncidentInterfaceNETCOOLWSPortTypePortType port = service
					.getHPDIncidentInterfaceNETCOOLWSPortTypeSoap();

			BindingProvider bProvider = (BindingProvider) port;

			bProvider.getRequestContext().put(
					BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					RemedyWSConfiguration.getUrl());

			
			/******************************************* TIMEOUT REMEDY*************************************/
			//Set timeout until a connection is established
		/*	bProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", "6000");
			//Set timeout until the response is received
			bProvider.getRequestContext().put("javax.xml.ws.client.receiveTimeout", "1000");
			bProvider.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, "1000");
			bProvider.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, "1000");*/
			/******************************************* TIMEOUT REMEDY*************************************/
			
			Binding binding = bProvider.getBinding();
			
			try{
				String strStatus = (status!=null && status.value!=null)?status.value.value():"null";
				String strPriority = (priority!=null && priority.value!=null)?priority.value.value():"null";
				String strImpact = (impactType!=null)?impactType.value():"null";
				String strUrgency = (urgency!=null)?urgency.value():"null";
				
				log.debug(("SEND: " + "ACTION: " + ACTION_CREATE + ";" + " STATUS: "+ strStatus + ";" + 
						" CLASS: " + troubleTicket.getOutageType()  + ";" + " CONTACT: " + troubleTicket.getOwnerUID() + ";" +
						" ALERTKEY: " + troubleTicket.getAlertKey() + ";" + " SERVERSERIAL: " + troubleTicket.getServerSerial() + ";" + 
						" CI_NAME: " + troubleTicket.getTTRemedyCI() + ";" + " CI_TYPE: " + troubleTicket.getTTCaseType() + ";" + 
						" TTITLE: "	+ troubleTicket.getTTTitle() + ";" + " TTDESCRIPTION: "	+ troubleTicket.getTTDescription() + ";" +
						" IMPACT: "	+ strImpact + ";" + " URGENCY: " + strUrgency	+ ";" + " PRIORITY: " + strPriority	+ ";" +
						" OPCATS1: " + troubleTicket.getTTOpCat1() + ";" + " OPCATS2: " + troubleTicket.getTTOpCat2() + ";"	+ 
						" OPCATS3: " + troubleTicket.getTTOpCat3() + ";" + " OPCATSRESOLUTION1: " + troubleTicket.getTTOpCatRes1() + ";" +
						" OPCATSRESOLUTION2: " + troubleTicket.getTTOpCatRes2() + ";" + " OPCATSRESOLUTION3: " + troubleTicket.getTTOpCatRes3()	+ ";" + 
						" CLASS_ID: " + troubleTicket.getClass_ID()	+ ";" + "	CLASS_NAME: " + troubleTicket.getClass_Name() + ";" +
						" ALARM_KEY: " + troubleTicket.getAlarm_Key()	+ ";" + " ALARM_SEVERITY: " + alarmSeverity	+ ";" + 
						" PRODCAT3: " + troubleTicket.getTTProdCat3() + ";").replaceAll("\\r|\\n", " "));
			}catch(Exception e0){
				log.warn("(RAISE/CLOSE WS) Error tracing the WS message: ", e0);
			}

		
			// Company: TELEFONICA UK
			// Customer: NETCOOL_OSS
			// Service:Mobile Data 3G
			// Impact:3-Moderate/Limited
			// Urgency:3-Medium
			// Incident_Type:Infrastructure Event
			// Operational_Categorization_Tier_1:Performance
			// Operational_Categorization_Tier_2:Connectivity
			// Operational_Categorization_Tier_3:Intermittent
			// Resolution_Categorization_Tier_1:Failure
			// Resolution_Categorization_Tier_2:Performance
			// Resolution_Categorization_Tier_3:Repair
			// CI_Name:BSC849 (Newcastle)
			// CI_Type:BMC_COMPUTERSYSTEM
			// Product_Categorization_Tier_3:Just a random value
			// Contact_Method:Netcool
			// Contact:NETCOOL_OSS
			// External_Ticket_ID:<Input your own value here>
			// ServerSerial:<Same as above>
			// Alarm_First_Occurence:<Same as above>
			// Incident_ID:
			// Work_Info_Summary:Netcool Note
			// Work_Info_Note:<Same as above>
			// Work_Info_Type:General Information
			// Locked:Yes
			
			port.incidentSubmitAddWIService(
					ACTION_CREATE,// Action
					status, // Status
					COMPANY, // Company
					"NETCOOL_OSS", //Customer
					troubleTicket.getTTTitle(),// Summary
					troubleTicket.getTTDescription(),// Notes
					troubleTicket.getTTService(), // Service
					impactType, // Impact
					urgency, // Urgency
					IncidentTypeType.INFRASTRUCTURE_EVENT, // Incident_type
					troubleTicket.getTTOpCat1(), // Operational_Categorization_Tier_1
					troubleTicket.getTTOpCat2(), // Operational_Categorization_Tier_2
					troubleTicket.getTTOpCat3(), // Operational_Categorization_Tier_3
					troubleTicket.getTTOpCatRes1(), // Resolution_Categorization_Tier_1
					troubleTicket.getTTOpCatRes2(), // Resolution_Categorization_Tier_2
					troubleTicket.getTTOpCatRes3(), // Resolution_Categorization_Tier_3
					troubleTicket.getTTRemedyCI(), // CI_Name
					troubleTicket.getTTRemedyCIType(), // CI_Type
					troubleTicket.getTTProdCat3(), //Product_Categorization_Tier_3
					"Netcool", // Contact_Method
				    troubleTicket.getOwnerUID(), //contact
					troubleTicket.getIdentifier(), // External_Ticket_ID
					troubleTicket.getServerSerial(), // serverSerial
					firstOcurrrence, // Alarm_First_Occurrence
					incidentID, // Incident_ID
					WORK_INFO_SUMMARY, // Work_Info_Summary
					severity, // Work_Info_Note
					WorkInfoTypeType.GENERAL_INFORMATION, // Work_Info_Type, 
					LockedType.YES, // Locked
					assigned_Group, 
					Integer.parseInt(troubleTicket.getClass_ID()), //Class_ID
					troubleTicket.getClass_Name(), //Class_Name
					troubleTicket.getAlarm_Key(), //Alarm_Key
					Integer.parseInt(alarmSeverity), //Alarm_Severity
					errorCode, errorMsg, priority,
					parameterAuthentication);
			
			try{
				String strStatus = (status!=null && status.value!=null)?status.value.value():"null";
				String strPriority = (priority!=null && priority.value!=null)?priority.value.value():"null";
				String strImpact = (impactType!=null)?impactType.value():"null";
				String strUrgency = (urgency!=null)?urgency.value():"null";
				
				log.debug(("RESPONSE: " + " ACTION: " + ACTION_CREATE + ";" + "	STATUS: " + strStatus + ";" +
						" CLASS: " + troubleTicket.getOutageType()  + ";" + " ALERTKEY: " + troubleTicket.getAlertKey() + ";" + 
						" SERVERSERIAL: "	+ troubleTicket.getServerSerial() + ";" + " CI_NAME: "	+ troubleTicket.getTTRemedyCI() + ";" + 
						" CI_TYPE: " + troubleTicket.getTTCaseType() + ";" + "	TTITLE: " + troubleTicket.getTTTitle() + ";" + 
						" TTDESCRIPTION: " + troubleTicket.getTTDescription() + ";" + " IMPACT: " + strImpact + ";" + 
						" URGENCY: " + strUrgency + ";" + " PRIORITY: " + strPriority	+ ";" + " OPCATS1: " + troubleTicket.getTTOpCat1() + ";"	+ 
						" OPCATS2: " + troubleTicket.getTTOpCat2() + ";" + " OPCATS3: " + troubleTicket.getTTOpCat3() + ";"	+ 
						" OPCATSRESOLUTION1: " + troubleTicket.getTTOpCatRes1()	+ ";" + " OPCATSRESOLUTION2: " + troubleTicket.getTTOpCatRes2() + ";" + 
						" OPCATSRESOLUTION3: " + troubleTicket.getTTOpCatRes3()	+ ";" + "	PRODCAT3: " + troubleTicket.getTTProdCat3() + ";" + 
						" ERRORCODE: " + errorCode.value + ";" + " ERRORMSG: " + errorMsg.value + ";" + 
						" INCIDENTID: " + incidentID.value + ";" + " PRIORITY: " + strPriority + ";"	+ 
						" SUPPORT ORGANIZATION / GROUP NAME: " + assigned_Group.value + ";").replaceAll("\\r|\\n", " "));
			}catch(Exception e0){
				log.warn("(RAISE/CLOSE WS) Error tracing the WS message: ", e0);
			}
			
		} catch (Exception e1) {
			log.error("(RAISE/CLOSE WS) Problem Remedy WebService: NetcoolID: "
					+ troubleTicket.getIdentifier(),e1);

			throw new TroubleTicketException("Problem Remedy WebService:  "
					+ e1.getMessage());
		}

		if (RESPONSE_SUCCESS.equals(errorCode.value)) {
			
			
			troubleTicket.setTTNote(errorMsg.value);
			troubleTicket.setTroubleTicket(incidentID.value);
			troubleTicket.setFaultPriority(priority.value.value());
			troubleTicket.setFaultStatus(status.value.value());
			troubleTicket.setFaultQueue(assigned_Group.value);
			
			
			log.debug("(RAISE/CLOSE WS) Response succesful NetcoolID: "
					+ troubleTicket.getIdentifier());
		} else {
			troubleTicket.setTTNote("Error Code " + errorCode.value+ " : " + errorMsg.value);
			log.error("(RAISE/CLOSE WS) Functional error calling Remedy WebService:  NetcoolID:  "
					+ troubleTicket.getIdentifier() + "; ERROR: " + "["+errorCode.value+"] " + errorMsg.value);

			throw new TroubleTicketException("["+errorCode.value+"] " + errorMsg.value);
		}

		log.debug("Create incident - incident ticket created");
	}

	public XMLGregorianCalendar stringToXMLGregorianCalendar(String s)
			throws ParseException, DatatypeConfigurationException {
		XMLGregorianCalendar result = null;
		Date date;
		SimpleDateFormat simpleDateFormat;
		GregorianCalendar gregorianCalendar;

		simpleDateFormat = new SimpleDateFormat(
				RemedyWSConfiguration.getCic_date_format());
		date = simpleDateFormat.parse(s);
		gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.setTime(date);
		result = DatatypeFactory.newInstance().newXMLGregorianCalendar(
				gregorianCalendar);
		return result;
	}

	public boolean updateTroubleTicket(TroubleTicket troubleTicket)
			throws TroubleTicketException {

		log.debug("Updating ticket....");
		// Output parameter
		Holder<String> errorMsg = new Holder<String>();
		Holder<String> errorCode = new Holder<String>();
		boolean updated = false;
		// Input paramater
		Holder<String> incidentID;
		try {
			incidentID = new Holder<String>(troubleTicket.getTroubleTicket());
		} catch (Exception e) {
			log.error("(ADDWI WS) Problem with incidentID parameter: " + troubleTicket.getTroubleTicket()
					+". NetcoolID: " + troubleTicket.getIdentifier());
			throw new TroubleTicketException(
					"IncidentID parameter can't be empty");
		}

		AuthenticationInfo parameterAuthentication;
		try {
			parameterAuthentication = new AuthenticationInfo();
			parameterAuthentication.setUserName(RemedyWSConfiguration
					.getLogin());
			parameterAuthentication.setPassword(RemedyWSConfiguration
					.getPassword());

		} catch (Exception e) {
			log.error("(ADDWI WS) Problem with Authentication parameter, "
					+ "Login: " + RemedyWSConfiguration.getLogin() 
					+ "Pass: " + RemedyWSConfiguration.getPassword()
					+". NetcoolID: " + troubleTicket.getIdentifier());
			throw new TroubleTicketException(
					"Authentication parameter can't be empty");
		}

		Holder<StatusType> status = new Holder<StatusType>();
		if (BasicCICEventHandling.TTFLAG_ADD_NOTE.equals(troubleTicket
				.getTTFlag())) {
			status = null;
		}

		try {

			TGTUKNETCOOLHPDWSService service = new TGTUKNETCOOLHPDWSService();

			// Loggin request
			service.setHandlerResolver(new JaxWsHandlerResolver());
			HPDIncidentInterfaceNETCOOLWSPortTypePortType port = service
					.getHPDIncidentInterfaceNETCOOLWSPortTypeSoap();
			BindingProvider bProvider = (BindingProvider) port;
			bProvider.getRequestContext().put(
					BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					RemedyWSConfiguration.getUrl());
			Binding binding = bProvider.getBinding();

			try{
				log.debug(("SEND:    " + " ACTION: " + ACTION_UPDATE + ";" + " CLASS: "	+ troubleTicket.getOutageType()  + ";" + 
						" ALERTKEY: " + troubleTicket.getAlertKey() + ";" + " INCIDENTID: "	+ incidentID + ";").replaceAll("\\r|\\n", " "));
			}catch(Exception e0){
				log.warn("(ADDWI WS) Error tracing the WS message: ", e0);
			}

			port.incidentSubmitAddWIService(ACTION_UPDATE, // Action
					status, // Status
					null, // Company
					null, // Customer
					null, // Summary
					null, // Notes
					null, // Service
					null, // Impact
					null, // Urgency
					null, // Incident_type
					null, // Operational_Categorization_Tier_1
					null, // Operational_Categorization_Tier_2
					null, // Operational_Categorization_Tier_3
					null, // Resolution_Categorization_Tier_1
					null, // Resolution_Categorization_Tier_2
					null, // Resolution_Categorization_Tier_3
					null, // CI_Name
					null, // CI_Type
					null, // Product_Categorization_Tier_3
					null, // Contact_Method
					null, // Contact
					null, // External_Ticket_ID
					null, // Needs_Csutomization
					null, // Alarm_First_Occurrence
					incidentID, // IncidentID
					WORK_INFO_SUMMARY, // Work_Info_Summary
					troubleTicket.getTTNote(), // Work_Info_Note
					WorkInfoTypeType.GENERAL_INFORMATION, // Work_Info_Type
					LockedType.YES, // Locked
					null, null, null, null, null, errorCode, errorMsg, null, parameterAuthentication);

			try{
				log.debug(("RESPONSE: " + "ACTION: " + ACTION_UPDATE + ";" + " CLASS: " + troubleTicket.getOutageType() + ";" + 
						" ALERTKEY: " + troubleTicket.getAlertKey() + ";" + " INCIDENTID " + incidentID.value + ";" + 
						" ERRORCODE: " + errorCode.value + ";" + " ERRORMSG: " + errorMsg.value + ";" + 
						" INCIDENTID: " + incidentID.value + ";").replaceAll("\\r|\\n", " "));
			}catch(Exception e0){
				log.warn("(ADDWI WS) Error tracing the WS message: ", e0);
			}

		} catch (Exception e1) {
			log.error("(ADDWI WS) Problem Remedy WebService (ADDWI): NetcoolID: "
					+ troubleTicket.getIdentifier() + "; ERROR: " + e1.getMessage());
			throw new TroubleTicketException(
					"Problem Remedy WebService");
		}

		if (RESPONSE_SUCCESS.equals(errorCode.value)) {
			updated = true;

			log.debug("(ADDWI WS) Response succesful NetcoolID: "
					+ troubleTicket.getIdentifier());
		} else {
			updated = false;
			troubleTicket.setTTNote("Error Code " + errorCode.value+ " : " + errorMsg.value);
			log.error("(ADDWI WS) Functional error calling Remedy WebService:  NetcoolID:  "
					+ troubleTicket.getIdentifier() + "; ERROR: " + "["+errorCode.value+"] " + errorMsg.value);
		}

		return updated;
	}

	public WorkQueue getCreateLinkList() {
		log.warn(newTTList.size() + " events in List");
		return newTTList;
	}

	public WorkQueue getUpdateLinkList() {
		log.warn(upTTList.size() + " events in List");
		return upTTList;
	}

	public boolean updateTroubleTicketTitle(TroubleTicket troubleTicket)
			throws TroubleTicketException {
		return false;
	}



	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public void initPlannedOutages() throws TroubleTicketException {
		// TODO Auto-generated method stub

	}

}
