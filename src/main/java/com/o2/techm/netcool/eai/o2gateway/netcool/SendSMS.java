package com.o2.techm.netcool.eai.o2gateway.netcool;

import java.text.SimpleDateFormat; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.plugins.CICCorbaGatewayPlugin;
import com.o2.techm.netcool.eai.o2gateway.plugins.SMSBrokerPlugin;
import com.o2.techm.netcool.eai.manonsite.broker.BrokerException;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicket;


/**
 * @author trenaman
 */
public class SendSMS extends BasicCICEventHandling implements GatewayEventHandler
{
	private static final Logger log = LoggerFactory.getLogger(SendSMS.class);
    private SMSBrokerPlugin smsBrokerPlugin; 
    public static String OSIRIS="OSIRIS";
    
    /**
     *  
     */
    public SendSMS(
            SMSBrokerPlugin smsBrokerPlugin,
            CICCorbaGatewayPlugin netcoolPlugin,
            SimpleDateFormat cicDateFormat,
            String dummyStringColumnName,
            String dummyStringValue,
            String dummyIntegerColumnName,
            int dummyIntegerValue)
    {
        super(netcoolPlugin,
                 cicDateFormat,
                 dummyStringColumnName,
                 dummyStringValue,
                 dummyIntegerColumnName,
                 dummyIntegerValue);
        
        this.smsBrokerPlugin = smsBrokerPlugin;
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.netcool.GatewayEventHandler#handleEvent(com.o2.osiris.troubleticket.TroubleTicket)
     */
    public boolean handleEvent(TroubleTicket event) throws GatewayEventHandlingException
    {
        if (event.getTTFlag().equals(TTFLAG_SMS))
        {
        	log.debug("llamando TTFLAG_STATUS_PREFIX_IN_PROCESS");
            
        	log.debug("Updating the TTFlag to "
                    + TTFLAG_STATUS_PREFIX_IN_PROCESS
                    + event.getTTFlag() + "...");
            updateTTFlagInCIC(event.getIdentifier(),
                    TTFLAG_STATUS_PREFIX_IN_PROCESS + event.getTTFlag());
            
            String smsAddress = event.getTTField4();
            String smsMessage = event.getTTDescription();
            String smsresponse;
            String serviceId =event.getTEXT02();
            
            if (serviceId==null)
            	serviceId="62946";
            else if (serviceId.length()<4)
            	serviceId="62946";
            

            try {
            	
            	log.debug("Valor de smsAddress,"+smsAddress);
                log.debug("Valor de smsMessage,"+smsMessage);
                log.debug("Valor de serviceId,"+serviceId);
            	
            	smsresponse = smsBrokerPlugin.getSmsBroker().sendSimpleSMS(smsAddress, smsMessage,serviceId);
            	

                  
        		log.debug("Updating the TTFlag to "
                        + TTFLAG_STATUS_PREFIX_SUCCESS
                        + event.getTTFlag() + "...");
                updateTTFlagInCIC(event.getIdentifier(),
                        TTFLAG_STATUS_PREFIX_SUCCESS + event.getTTFlag());
            }
            catch (BrokerException be) 
			{
            	//Update the status of this trouble ticket.
            	// 
            	log.error("Exception caught :" ,be);
            	log.info("Updating the TTFlag/TTNote to "
                    + TTFLAG_STATUS_PREFIX_FAILED
                    + event.getTTFlag() + "...");
            	updateTTNoteInCIC(event.getIdentifier(),TTFLAG_STATUS_PREFIX_FAILED + event.getTTFlag(), "SMS FAILURE:" + be.getMessage().split("\n")[0]);
			} 
            catch (Exception e)
            {
                log.error("Exception caught :", e);
            }
            return true;
        }
        else {
            return false;
        }
    }

}
