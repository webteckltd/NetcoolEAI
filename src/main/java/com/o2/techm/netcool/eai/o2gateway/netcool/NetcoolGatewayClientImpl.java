/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification      
* 12-Jun-2014              Indra         CD38583 - BMR                                                                                       
*/
package com.o2.techm.netcool.eai.o2gateway.netcool;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayGatewayClient;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPairList;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNetcoolEvent;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicket;

public class NetcoolGatewayClientImpl implements NetcoolGatewayGatewayClient
{
	private static final Logger log = LoggerFactory.getLogger(NetcoolGatewayClientImpl.class);

	//private NetcoolGatewayReader netcoolGatewayReader;
	private Vector handlers;	
	
    public NetcoolGatewayClientImpl() {
        handlers = new Vector();
    }
    
    public void addHandler(GatewayEventHandler handler) {
        handlers.add(handler);
    }
	   
    public void takeEvents(NetcoolGatewayNetcoolEvent event)
        throws RemoteException
    {
        log.info("Received event...");       
        
        Hashtable params = new Hashtable(); 

        String eventId = event.getEventId();
        String type = event.getType_event().toString();
        log.info("Event ID = '" + eventId + "', type = '" + type + "'"); 
        
        NetcoolGatewayNVPairList nvPairList = event.getANVPairList();
        for (int i = 0; i < nvPairList.getItem().length; i++) 
        {
            log.debug("param[" + i + "] = (" +  nvPairList.getItem()[i].getAName() + ", " + nvPairList.getItem()[i].getAValue() + ")" );
            params.put(nvPairList.getItem()[i].getAName(), nvPairList.getItem()[i].getAValue()); 
        }
        
        TroubleTicket troubleTicket = new TroubleTicket(); 
        troubleTicket.setAlertKey(getValue(params, "AlertKey"));
        troubleTicket.setDetails(getValue(params, "Details"));
        troubleTicket.setFaultImpact(getValue(params, "FaultImpact"));
        troubleTicket.setFaultPriority(getValue(params, "FaultPriority"));
        troubleTicket.setFaultStatus(getValue(params, "FaultStatus"));
        troubleTicket.setFirstOccurrence(getValue(params, "FirstOccurrence"));
        troubleTicket.setIdentifier(getValue(params, "Identifier"));
        troubleTicket.setOwnerUID(getValue(params, "OwnerUID"));
        troubleTicket.setPoll(getValue(params, "Poll"));
        troubleTicket.setServerSerial(getValue(params, "ServerSerial"));
        troubleTicket.setSeverity(getValue(params, "Severity"));
        troubleTicket.setTroubleTicket(getValue(params, "TroubleTicket"));
        troubleTicket.setTTFlag(getValue(params, "TTFlag"));
        troubleTicket.setTTNote(getValue(params, "TTNote"));
        troubleTicket.setTTField1(getValue(params, "TTField1", "Vantive1"));        
        troubleTicket.setTTField2(getValue(params, "TTField2", "Vantive2"));
        troubleTicket.setTTField3(getValue(params, "TTField3", "Vantive3"));
        troubleTicket.setTTField4(getValue(params, "TTField4", "Vantive4"));
        troubleTicket.setTTDescription(getValue(params, "TTDescription", "Vantive5"));
        troubleTicket.setTTOpCat1(getValue(params, "TTOpCat1"));        
        troubleTicket.setTTOpCat2(getValue(params, "TTOpCat2"));
        troubleTicket.setTTOpCat3(getValue(params, "TTOpCat3"));
        troubleTicket.setTTOpCatRes1(getValue(params, "TTOpCatRes1"));        
        troubleTicket.setTTOpCatRes2(getValue(params, "TTOpCatRes2"));
        troubleTicket.setTTOpCatRes3(getValue(params, "TTOpCatRes3"));
        troubleTicket.setTTProdCat3(getValue(params, "TTProdCat3"));
        troubleTicket.setTTRemedyCI(getValue(params, "TTRemedyCI"));
        troubleTicket.setTTRemedyCIType(getValue(params, "TTRemedyCIType"));
        troubleTicket.setTTService(getValue(params, "TTService"));
        troubleTicket.setTTTitle(getValue(params, "TTTitle"));
        troubleTicket.setTEXT02(getValue(params, "ShortLongCode","TEXT02"));
        troubleTicket.setUrgency(getValue(params, "Urgency"));
        
        troubleTicket.setCDS_ID(getValue(params, "CDS_ID"));
        troubleTicket.setNEName(getValue(params, "NEName"));
        troubleTicket.setAlarmImpact(getValue(params, "AlarmImpact"));
        troubleTicket.setCDS_Type(getValue(params, "CDS_Type"));
        troubleTicket.setCommitmentPoints(getValue(params, "CommitmentPoints"));        
        troubleTicket.setLastOccurrence(getValue(params, "LastOccurrence"));
        troubleTicket.setManager(getValue(params, "Manager"));
        troubleTicket.setManufacturer(getValue(params, "Manufacturer"));
        troubleTicket.setNotificationFlag(getValue(params, "NotificationFlag"));
        troubleTicket.setObjectType(getValue(params, "ObjectType"));
        troubleTicket.setOutageDuration(getValue(params, "OutageDuration"));
        troubleTicket.setOutageEnd(getValue(params, "OutageEnd"));
        troubleTicket.setOutageStart(getValue(params, "OutageStart"));
        troubleTicket.setOutageType(getValue(params, "OutageType"));
        troubleTicket.setServerName(getValue(params, "ServerName"));
        troubleTicket.setServiceGroup(getValue(params, "ServiceGroup"));
        troubleTicket.setStateChange(getValue(params, "StateChange"));
        troubleTicket.setSummary(getValue(params, "Summary"));        
        troubleTicket.setTally(getValue(params, "Tally"));
        troubleTicket.setTimeToFix(getValue(params, "TimeToFix"));
        troubleTicket.setTimeToFixUnits(getValue(params, "TimeToFixUnits"));
        troubleTicket.setTTElementID(getValue(params, "TTElementID", "VantiveRef"));
        troubleTicket.setClass_ID(getValue(params, "Class"));
        //troubleTicket.setClass_ID(Integer.valueOf("Class_ID"));
        troubleTicket.setClass_Name(getValue(params, "AstroClassText"));
        troubleTicket.setAlarm_Key(getValue(params, "AlarmKey"));
        troubleTicket.setAlarm_Severity(getValue(params, "Severity"));
        //troubleTicket.setAlarm_Severity(Integer.valueOf("Alarm_Severity"));
        
        // 19/09/2011  Top25 Add new parameter
        //troubleTicket.setTTIncidentGroup(getValue(params, "TTIncidentGroup"));
        
//        try
//        {
//            eventProcessor.handle(troubleTicket);
//        }
//        catch (GatewayEventHandlingException e)
//        {
//            // TODO Auto-generated catch block: should log the error! 
//            e.printStackTrace();
//        } 
        
        
        try
        {
            boolean done = false;
            Iterator iter = handlers.iterator();
            while (iter.hasNext() && !done) {
                GatewayEventHandler handler = (GatewayEventHandler) iter.next();
                done = handler.handleEvent(troubleTicket);
            }
            
            if (! done) {
                log.warn("None of the installed handlers processed the trouble ticket!"); 
            }
        }
        catch (GatewayEventHandlingException e)
        {
            log.error("Error handling trouble ticket; details: " + e.getMessage());
        }
        
        
    }
    
    /*
     * Tries to get the string value from mthe params hashtable using the key supplied. If 
     * no value is registered under the key then the value null is returned.
     * 
     */
    private String getValue(Hashtable params, String key) {
        String value = (String) params.get(key);
        //if (value == null) {
        //    log.warn("The field '" + key + "' was not present in the incoming message. Using a null value for this field internally."); 
        //}
        return value;
    }

    /*
     * Tries to get the string value from the params Hashtable using the key supplied. If 
     * no value is registered under the key, the alternative key is tried. 
     * 
     * This method is used to provide backward compabilitiy from deprecated fields names in CIC as 
     * follows: 
     * 
     * Old Name                  New Name
     * --------                  --------
     * Vantive1       ->         TTField1
     * Vantive2       ->         TTField2
     * Vantive3       ->         TTField3
     * Vantive4       ->         TTField4
     * Vantive5       ->         TTDescription
     * VantiveRef     ->         TTElementID
     * 
     */
    private String getValue(Hashtable params, String key, String alternativeKey)
    {
        String value = null;
        value = (String) params.get(key); 
        if (value == null) {
            value = (String) params.get(alternativeKey); 
        }
        
        /*if (value == null) 
        {
            log.warn("Tried to get the CIC parameter " + key + " using field names '" + key + "' and '" +
                    alternativeKey + "', but couldn't find it. It looks like there's something wrong with the " 
                    + "mapping between CIC fields and those sent on the wire to Osiris. Leaving the value set to" 
                    + " null for now.");
        }*/
        
        return value; 
    }
}
