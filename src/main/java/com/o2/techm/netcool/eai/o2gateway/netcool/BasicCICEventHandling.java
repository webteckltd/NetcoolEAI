/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 				 1.0					 Base version
* 18-Oct-2010    1.1       KEANE         Modified for DR 1616094
* 29-Oct-2010	 1.2	   KEANE		 Modified for DR 1434978        
* 12-Jun-2014              Indra         CD38583 - BMR                                                                                       
*/
package com.o2.techm.netcool.eai.o2gateway.netcool;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayEventType;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPair;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPairList;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNetcoolEvent;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayProcessEventFailure_Exception;
import com.o2.techm.netcool.eai.o2gateway.plugins.CICCorbaGatewayPlugin;

/**
 * @author trenaman
 */
public abstract class BasicCICEventHandling
{
	private static final Logger log = LoggerFactory.getLogger(BasicCICEventHandling.class);

    public static final String TTFLAG_EMPTY = "0"; 
    public static final String TTFLAG_RAISE = "1"; 
    public static final String TTFLAG_NETWORK_CLEAR = "2"; 
    public static final String TTFLAG_MANUAL_CLEAR = "3"; 
    public static final String TTFLAG_RAISE_AND_CLOSE = "4"; 
    public static final String TTFLAG_ADD_NOTE = "5"; 
        
    public static final String TTFLAG_SMS="767"; //spells out SMS on mobile keypad
    public static final String TTFLAG_PING="764"; //spells out PING on mobile keypad    
    
    public static final String TTFLAG_STATUS_PREFIX_IN_PROCESS = "5"; 
    public static final String TTFLAG_STATUS_PREFIX_SUCCESS = "10"; 
    public static final String TTFLAG_STATUS_PREFIX_FAILED = "20"; 
    
    public static final String NOTIFICATION_FLAG_EMPTY = "0";
    public static final String NOTIFICATION_FLAG_NOTIFY = "1";
    public static final String NOTIFICATION_FLAG_GIS = "2";
    
    public static final String ALERT_KEY_PING_ARTIX = "ARTIX"; 
    public static final String ALERT_KEY_PING_SMS = "SMS"; 

    public static final String PLANNED_OUTAGE_POLL = "0"; 
    
    //Added for DR-1616094 
    public static final String TTFLAG_UPDATE_TTTITLE		= "6";
    //End of Addition for DR-1616094
    //  Added for DR-1434978 
    public static final String TTFLAG_RESOLVE_CASE = "7";
    public static final String TTFLAG_RESOLVE_CLOSE_CASE = "8";
    public static final String TTFLAG_UNRESOLVE_CASE="9";
    //End of Addition for DR-1434978 
    /**
     * @param troubleTicketManager
     * @param netcoolGatewayReader
     * @param dateFormat
     * @param dummyStringColumnName
     * @param dummyStringValue
     * @param dummyIntegerColumnName
     * @param dummyIntegerValue
     */
    public BasicCICEventHandling(
        CICCorbaGatewayPlugin netcoolPlugin,
        SimpleDateFormat cicDateFormat,
        String dummyStringColumnName,
        String dummyStringValue,
        String dummyIntegerColumnName,
        int dummyIntegerValue)
    {
        super();
        this.netcoolPlugin = netcoolPlugin;
        this.cicDateFormat = cicDateFormat;
        this.dummyStringColumnName = dummyStringColumnName;
        this.dummyStringValue = dummyStringValue;
        this.dummyIntegerColumnName = dummyIntegerColumnName;
        this.dummyIntegerValue = "" + dummyIntegerValue;
    }
    
    protected CICCorbaGatewayPlugin netcoolPlugin;   
    protected SimpleDateFormat cicDateFormat;	
    protected String dummyStringColumnName;
    protected String dummyStringValue;
    protected String dummyIntegerColumnName;
    protected String dummyIntegerValue; 
    
    
    protected void updateTTNoteInCIC(String identifier, String TTFlag, String TTNote)
    {
        final int nvPairLength = 8;
        
        log.debug("identifier = '" + identifier + "'");        
        
        if(TTNote.length() >= 250)
        {
        	log.info("TTNote is too long, truncating");
        	TTNote = TTNote.substring(0,250);
        }
        log.debug("TTNote = '" + TTNote + "'");
        log.debug("TTFlag = '" + TTFlag + "'");
        log.debug("dummyStringColumnName = '" + dummyStringColumnName + "'");
        log.debug("dummyStringValue = '" + dummyStringValue + "'");
        log.debug("dummyIntegerColumnName = '" + dummyIntegerColumnName + "'");
        log.debug("dummyIntegerValue = '" + dummyIntegerValue + "'");
        
        NetcoolGatewayNetcoolEvent event = new NetcoolGatewayNetcoolEvent();
        NetcoolGatewayNVPairList nvPairList = new NetcoolGatewayNVPairList(); 
        NetcoolGatewayNVPair[] nvPair = new NetcoolGatewayNVPair[nvPairLength];
        for (int i = 0; i < nvPairLength; i++) {
            nvPair[i] = new NetcoolGatewayNVPair(); 
        }
            
        nvPair[0].setAName("Identifier"); 
        nvPair[0].setAValue(identifier);

        nvPair[1].setAName("TTFlag"); 
        nvPair[1].setAValue(TTFlag); 
        
        nvPair[2].setAName("TTNote"); 
        nvPair[2].setAValue(TTNote); 
 
        nvPair[3].setAName(dummyStringColumnName); 
        nvPair[3].setAValue(dummyStringValue); 

        nvPair[4].setAName(dummyStringColumnName); 
        nvPair[4].setAValue(dummyStringValue); 

        nvPair[5].setAName(dummyIntegerColumnName); 
        nvPair[5].setAValue(dummyIntegerValue); 

        nvPair[6].setAName(dummyStringColumnName); 
        nvPair[6].setAValue(dummyStringValue); 

        nvPair[7].setAName(dummyIntegerColumnName); 
        nvPair[7].setAValue(dummyIntegerValue); 


        nvPairList.setItem(nvPair);
        
        event.setEventId(identifier);
        event.setType_event(NetcoolGatewayEventType.updateET);
        event.setANVPairList(nvPairList); 
        
        try
        {
            netcoolPlugin.getNetcoolGatewayReader().event(event);
        }
        catch (RemoteException e)
        {
            log.error("Communication failure while trying to contact the CORBA gateway; could not update TTNote for event with identifier='"
                    + identifier + "' to '" + TTNote + "'. Exception details: \n" , e);
        }
        catch (NetcoolGatewayProcessEventFailure_Exception e)
        {
            log.error("NetcoolGatewayProcessEventFailureException while trying to contact the CORBA gateway; could not update TTNote for event with identifier='"
                    + identifier + "' to '" + TTNote + "'. Exception details: \n" ,e);
        } 
        catch (Exception e) {
            log.error("Exception: " ,e);
        }
    }
    protected void updateTTFlagInCIC(String identifier, String TTFlag)
    {
        final int nvPairLength = 8;
        
        log.debug("identifier = '" + identifier + "'");
        log.debug("TTFlag = '" + TTFlag + "'"); 
        log.debug("dummyStringColumnName = '" + dummyStringColumnName + "'");
        log.debug("dummyStringValue = '" + dummyStringValue + "'");
        log.debug("dummyIntegerColumnName = '" + dummyIntegerColumnName + "'");
        log.debug("dummyIntegerValue = '" + dummyIntegerValue + "'");
        
        NetcoolGatewayNetcoolEvent event = new NetcoolGatewayNetcoolEvent();
        NetcoolGatewayNVPairList nvPairList = new NetcoolGatewayNVPairList(); 
        NetcoolGatewayNVPair[] nvPair = new NetcoolGatewayNVPair[nvPairLength];
        for (int i = 0; i < nvPairLength; i++) {
            nvPair[i] = new NetcoolGatewayNVPair(); 
        }
        nvPair[0].setAName("Identifier"); 
        nvPair[0].setAValue(identifier);
        
        nvPair[1].setAName("TTFlag"); 
        nvPair[1].setAValue(TTFlag); 

        nvPair[2].setAName(dummyStringColumnName); 
        nvPair[2].setAValue(dummyStringValue); 

        nvPair[3].setAName(dummyStringColumnName); 
        nvPair[3].setAValue(dummyStringValue); 

        nvPair[4].setAName(dummyStringColumnName); 
        nvPair[4].setAValue(dummyStringValue); 

        nvPair[5].setAName(dummyIntegerColumnName); 
        nvPair[5].setAValue(dummyIntegerValue); 

        nvPair[6].setAName(dummyStringColumnName); 
        nvPair[6].setAValue(dummyStringValue); 

        nvPair[7].setAName(dummyIntegerColumnName); 
        nvPair[7].setAValue(dummyIntegerValue); 


        nvPairList.setItem(nvPair);
        
        event.setEventId(identifier);
        event.setType_event(NetcoolGatewayEventType.updateET);
        event.setANVPairList(nvPairList); 
        
        try
        {
            netcoolPlugin.getNetcoolGatewayReader().event(event);
        }
        catch (RemoteException e)
        {
            log.error("Communication failure while trying to contact the CORBA gateway; could not update TTFlag for event with identifier='"
                    + identifier + "' to '" + TTFlag + "'. Exception details: \n" + e.getMessage());
        }
        catch (NetcoolGatewayProcessEventFailure_Exception e)
        {
            log.error("NetcoolGatewayProcessEventFailureException while trying to contact the CORBA gateway; could not update TTFlag for event with identifier='"
                    + identifier + "' to '" + TTFlag + "'. Exception details: \n" + e.getMessage());
        } 
        catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
    }
    
    
	protected void updateTroubleTicketInCIC(
			String identifier,
			String TTFlag,
			String faultStatus,
			String faultPriority,
			String troubleTicket,
			String faultQueue
		)
		{	
	        log.info("updateTroubleTicket() -  identifer: " + identifier
	                + " faultStatus: " + faultStatus 
	                + " faultQueue: " + faultQueue 
	                + " Priority: " + faultPriority 
	                + " TTFlag: " + TTFlag 
	                + " TicketId: " + troubleTicket);
	        
	        if (identifier == null || faultStatus == null || faultPriority == null ||
	            TTFlag == null || troubleTicket == null || faultQueue == null)
	        {

	            log.error("updateTroubleTicket() identifer: " + identifier
	                    + " faultStatus: " + faultStatus
	                    + " faultQueue: " + faultQueue 
	                    + " Priority: " + faultPriority 
	                    + " TTFlag: " + TTFlag 
	                    + " TicketId: " + troubleTicket);

	            log.error("No Null values for the identifier or "
	                    + "faultImpact or faultPriority or faultQueue or"
	                    + "ttFlag or troubleTicketID cannot be "
	                    + "passed into updateTroubleTicket()");
	            
	            return; 
	        }	         
	        if (!TTFlag.equals(TTFLAG_STATUS_PREFIX_SUCCESS + TTFLAG_RAISE)
	                && !TTFlag.equals(TTFLAG_STATUS_PREFIX_SUCCESS + TTFLAG_RAISE_AND_CLOSE))
	        {
	            log.error("The following TTFlag cannot be passed into "
	                    + "updateTroubleTicket(): " + TTFlag);
	            return;

	        }
	        
	            
	        final int nvPairLength = 7;
	        
	        NetcoolGatewayNetcoolEvent event = new NetcoolGatewayNetcoolEvent();
	        NetcoolGatewayNVPairList nvPairList = new NetcoolGatewayNVPairList(); 
	        NetcoolGatewayNVPair[] nvPair = new NetcoolGatewayNVPair[nvPairLength];
	        for (int i = 0; i < nvPairLength; i++) {
	            nvPair[i] = new NetcoolGatewayNVPair(); 
	        }
	      
	        nvPair[0].setAName("Identifier"); 
	        nvPair[0].setAValue(identifier);
	        
	        nvPair[1].setAName("TTFlag"); 
	        nvPair[1].setAValue(TTFlag); 

	        nvPair[2].setAName("FaultStatus"); 
	        nvPair[2].setAValue(faultStatus); 

	        nvPair[3].setAName("FaultPriority"); 
	        nvPair[3].setAValue(faultPriority); 

	        nvPair[4].setAName("TroubleTicket"); 
	        nvPair[4].setAValue(troubleTicket); 

	        nvPair[5].setAName("Poll"); 
	        nvPair[5].setAValue(PLANNED_OUTAGE_POLL); 

	        nvPair[6].setAName("FaultQueue"); 
	        nvPair[6].setAValue(faultQueue); 

	        nvPairList.setItem(nvPair);
	        
	        event.setEventId(identifier);
	        event.setType_event(NetcoolGatewayEventType.updateET);
	        event.setANVPairList(nvPairList); 
	        
	        try
	        {
	        	log.info("Sending event to Netcool Gateway Reader...");
	        	String response = netcoolPlugin.getNetcoolGatewayReader().event(event);
	        	log.info("Sent event successfully, response = '" + response + "'");
	        }
	        catch (RemoteException e)
	        {
	            log.error("Communication failure while trying to contact the CORBA gateway; could not update TTFlag for event with identifier='"
	                    + identifier + "' to '" + TTFlag + "'. Exception details: \n" + e.getMessage());
	        }
	        catch (NetcoolGatewayProcessEventFailure_Exception e)
	        {
	            log.error("NetcoolGatewayProcessEventFailure_Exception while trying to contact the CORBA gateway; could not update TTFlag for event with identifier='"
	                    + identifier + "' to '" + TTFlag + "'. Exception details: \n" + e.getMessage());
	        } 
	        catch (Exception e) {
	            log.error("Exception: " + e.getMessage());
	        }    
	    }    
	
	protected void updateFlagInCIC(String identifier, String FlagDesp,String Flag)
    {
        final int nvPairLength = 8;
        
        log.debug("identifier = '" + identifier + "'");
        log.debug("FlagDesp = '" + FlagDesp + "'");
        log.debug("Flag = '" + Flag + "'"); 
        log.debug("dummyStringColumnName = '" + dummyStringColumnName + "'");
        log.debug("dummyStringValue = '" + dummyStringValue + "'");
        log.debug("dummyIntegerColumnName = '" + dummyIntegerColumnName + "'");
        log.debug("dummyIntegerValue = '" + dummyIntegerValue + "'");
        
        NetcoolGatewayNetcoolEvent event = new NetcoolGatewayNetcoolEvent();
        NetcoolGatewayNVPairList nvPairList = new NetcoolGatewayNVPairList(); 
        NetcoolGatewayNVPair[] nvPair = new NetcoolGatewayNVPair[nvPairLength];
        for (int i = 0; i < nvPairLength; i++) {
            nvPair[i] = new NetcoolGatewayNVPair(); 
        }
        nvPair[0].setAName("Identifier"); 
        nvPair[0].setAValue(identifier);
        
        nvPair[1].setAName(FlagDesp); 
        nvPair[1].setAValue(Flag); 

        nvPair[2].setAName(dummyStringColumnName); 
        nvPair[2].setAValue(dummyStringValue); 

        nvPair[3].setAName(dummyStringColumnName); 
        nvPair[3].setAValue(dummyStringValue); 

        nvPair[4].setAName(dummyStringColumnName); 
        nvPair[4].setAValue(dummyStringValue); 

        nvPair[5].setAName(dummyIntegerColumnName); 
        nvPair[5].setAValue(dummyIntegerValue); 

        nvPair[6].setAName(dummyStringColumnName); 
        nvPair[6].setAValue(dummyStringValue); 

        nvPair[7].setAName(dummyIntegerColumnName); 
        nvPair[7].setAValue(dummyIntegerValue); 


        nvPairList.setItem(nvPair);
        
        event.setEventId(identifier);
        event.setType_event(NetcoolGatewayEventType.updateET);
        event.setANVPairList(nvPairList); 
        
        try
        {
            netcoolPlugin.getNetcoolGatewayReader().event(event);
        }
        catch (RemoteException e)
        {
            log.error("Communication failure while trying to contact the CORBA gateway; could not update TTFlag for event with identifier='"
                    + identifier + "' to '" + Flag + "'. Exception details: \n" + e.getMessage());
        }
        catch (NetcoolGatewayProcessEventFailure_Exception e)
        {
            log.error("NetcoolGatewayProcessEventFailure_Exception while trying to contact the CORBA gateway; could not update TTFlag for event with identifier='"
                    + identifier + "' to '" + Flag + "'. Exception details: \n" + e.getMessage());
        } 
        catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
    }
}
