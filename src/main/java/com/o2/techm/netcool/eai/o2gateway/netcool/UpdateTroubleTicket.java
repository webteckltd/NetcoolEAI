/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 				 1.0					 Base version
* 18-Oct-2010    1.1       KEANE         Modified for DR 1616094
* 29-Oct-2010	 1.2	   KEANE		 Modified for DR 1434978 
* 12-Jun-2014              Indra         CD38583 - BMR                                                                                                 
*/
package com.o2.techm.netcool.eai.o2gateway.netcool;

import java.text.SimpleDateFormat;
import com.o2.util.WorkQueue;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicket;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.o2.techm.netcool.eai.o2gateway.plugins.CICCorbaGatewayPlugin;
import com.o2.techm.netcool.eai.o2gateway.plugins.TroubleTicketPlugin;
import java.util.NoSuchElementException;

/**
 * @author trenaman
 */
public class UpdateTroubleTicket extends BasicCICEventHandling implements GatewayEventHandler
{
	private static final Logger log = LoggerFactory.getLogger(UpdateTroubleTicket.class);

	private TroubleTicketPlugin troubleTicketPlugin;
	
    /**
     *  
     */
    public UpdateTroubleTicket(
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

    /* (non-Javadoc)
     * @see com.o2.osiris.netcool.BasicCICEventHandling#handleEvent(com.o2.osiris.troubleticket.TroubleTicket)
     */
    public boolean handleEvent(TroubleTicket troubleTicket) throws GatewayEventHandlingException
    {
        String TTFlag = troubleTicket.getTTFlag();
        if (TTFlag.equals(TTFLAG_ADD_NOTE))
        {
        	do
        	{	
	            log.debug("Start of resolveTroubleTicket or updateTroubleTicket process.");
	            try
	            {
	                log.debug("Updating the TTFlag to "
	                        + TTFLAG_STATUS_PREFIX_IN_PROCESS
	                        + troubleTicket.getTTFlag() + "...");
	                updateTTFlagInCIC(troubleTicket.getIdentifier(),
	                    TTFLAG_STATUS_PREFIX_IN_PROCESS + troubleTicket.
	                    getTTFlag());
	
	                // Update the trouble ticket.
	                log.info("Updating trouble ticket...");
	                
                	//End of Addition for DR-1616094
                	final boolean bolIsUpdateTroubleTicketSuccess = 
                			troubleTicketPlugin.getTroubleTicketManager().
                				updateTroubleTicket(troubleTicket);
                	
					// Addition for OSC 1434978
					if (!bolIsUpdateTroubleTicketSuccess) 
					{
						log.error("Trouble ticket update functional error: "+troubleTicket.getTTNote());
						
						updateTTNoteInCIC(
								troubleTicket.getIdentifier(),
								TTFLAG_STATUS_PREFIX_FAILED	+ troubleTicket.getTTFlag(), 
								troubleTicket.getTTNote());
					} else {
						// Now update the trouble ticket in the InfoServer
						//
						log.info("Update successful; updating TTFlag information in CIC...");
						updateTTFlagInCIC(
								troubleTicket.getIdentifier(),
								TTFLAG_STATUS_PREFIX_SUCCESS + troubleTicket.getTTFlag());
						log.debug("Trouble ticket updated successfully.");
					}
	            }
	            catch (TroubleTicketException ex)
	            {
	                // Update the status of this trouble ticket.
	                //	                
	                ex.printStackTrace();
	            	log.error("Received a TroubleTicket exception while trying to resolve or update the trouble ticket."); 
	                String Tstr = ex.getMessage();
	          
	                //updating TTNote in Netcool
	                //
	                updateTTNoteInCIC(troubleTicket.getIdentifier(),TTFLAG_STATUS_PREFIX_FAILED + troubleTicket.getTTFlag(), Tstr.split("\n")[0]);
	                return false;
	            }
	            catch (Exception ex) 
	    		{
	            	log.error("Received an unexpected exception while trying to resolve or update the trouble ticket."); 
	            	log.error(ex.getMessage()); 
	            	ex.printStackTrace();
	            	
	            	updateTTNoteInCIC(troubleTicket.getIdentifier(),TTFLAG_STATUS_PREFIX_FAILED + troubleTicket.getTTFlag(), "Unexpected Exception, contact with system administrator");
	            	return false;
	    		}
	            WorkQueue upList = troubleTicketPlugin.getTroubleTicketManager().getUpdateLinkList();
	            
	            if (upList.size() == 0)
	            	troubleTicket=null;
	            else
	            {
	            	try
					{
	            		troubleTicket = (TroubleTicket)upList.dequeueWork();
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
        }        
        // TODO Auto-generated method stub
        return false;
    }

}

