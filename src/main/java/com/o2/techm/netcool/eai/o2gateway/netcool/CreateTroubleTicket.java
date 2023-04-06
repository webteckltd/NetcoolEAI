/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 12-Jun-2014              Indra         CD38583 - BMR                                                                                                 
*/

package com.o2.techm.netcool.eai.o2gateway.netcool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.plugins.CICCorbaGatewayPlugin;
import com.o2.techm.netcool.eai.o2gateway.plugins.TroubleTicketPlugin;
import com.o2.util.WorkQueue;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicket;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicketException;

/**
 * @author trenaman
 */
public class CreateTroubleTicket extends BasicCICEventHandling implements GatewayEventHandler
{
	private static final Logger log = LoggerFactory.getLogger(CreateTroubleTicket.class);

	private TroubleTicketPlugin troubleTicketPlugin;
	
    /**
     *  
     */
    public CreateTroubleTicket(
            CICCorbaGatewayPlugin netcoolPlugin,
            TroubleTicketPlugin troubleTicketPlugin,
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
        
        this.troubleTicketPlugin = troubleTicketPlugin;
    }

    public boolean handleEvent(TroubleTicket troubleTicket) 
    	throws GatewayEventHandlingException
    {
    	String TTFlag = troubleTicket.getTTFlag();
        
    	if (TTFlag.equals(BasicCICEventHandling.TTFLAG_RAISE) || TTFlag.equals(BasicCICEventHandling.TTFLAG_RAISE_AND_CLOSE)) 
	    {
	        	do
	        	{
	        
	            // Update the trouble ticket flag on the InfoServer
	            // 
	            try
	            {
	                log.debug("Updating the TTFlag to "
	                        + TTFLAG_STATUS_PREFIX_IN_PROCESS
	                        + troubleTicket.getTTFlag() + "...");
	                updateTTFlagInCIC(troubleTicket.getIdentifier(),
	                        TTFLAG_STATUS_PREFIX_IN_PROCESS + troubleTicket.getTTFlag());
	
	                // Create the trouble ticket.
	                log.info("Creating trouble ticket...");
	                if(troubleTicketPlugin.getTroubleTicketManager() == null)
	                {
	                	log.debug("troubleTicketPlugin.getTroubleTicketManager() not initialized");
	                }
	                
	                troubleTicketPlugin.getTroubleTicketManager().createTroubleTicket(troubleTicket);
	
	                log.info("About the to update the trouble ticket, using identifier: " + troubleTicket.getIdentifier()); 
	                updateTroubleTicketInCIC(
	                		troubleTicket.getIdentifier(),
	                        TTFLAG_STATUS_PREFIX_SUCCESS + troubleTicket.getTTFlag(),
	                        troubleTicket.getFaultStatus(), 
	                        troubleTicket.getFaultPriority(), 
	                        troubleTicket.getTroubleTicket(), 
	                        troubleTicket.getFaultQueue());
	
	                log.debug("Trouble ticket created successfully.");
	            }
	            catch (TroubleTicketException ex)
	            {
	                // Update the status of this trouble ticket.
	                //	                
	                ex.printStackTrace();
	                log.error("Received a TroubleTicket exception while trying to create or close the trouble ticket.");
	                String Tstr=ex.getMessage().split("\n")[0];
	                
	                //updateTTFlagInCIC(troubleTicket.getIdentifier(),TTFLAG_STATUS_PREFIX_FAILED + troubleTicket.getTTFlag());
	                updateTTNoteInCIC(troubleTicket.getIdentifier(),TTFLAG_STATUS_PREFIX_FAILED + troubleTicket.getTTFlag(), Tstr);
	                
	                return true;
	            }
	            catch (Exception ex) 
	    		{
	            	log.error("Received an unexpected exception while trying to create the trouble ticket.",ex); 
	                updateTTNoteInCIC(troubleTicket.getIdentifier(),TTFLAG_STATUS_PREFIX_FAILED + troubleTicket.getTTFlag(), "Unexpected Exception, contact with system administrator");
	            }
	            WorkQueue newList = troubleTicketPlugin.getTroubleTicketManager().getCreateLinkList();
	        	
	        	if (newList.size() == 0)
	            	troubleTicket=null;
	            else
	            {
	            	try
					{
	            		troubleTicket = (TroubleTicket)newList.dequeueWork();
					}
	            	catch(NoSuchElementException ex)
					{
	            		log.error("No more TTs");
	            		troubleTicket = null;
					}
	            	catch(InterruptedException ex)
					{
	            		log.error("Process have been interrupted");
	            		break;
					}
	            }
	    	}
			while(troubleTicket != null);
	        log.debug("End of createTroubleTicket process.");
	    	return true;
	    }
	    else 
	    {
	    	return false;
	    }
	        
    }
     
}
