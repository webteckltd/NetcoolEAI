/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 				 1.0					 Base version
* 29-Oct-2010	 1.1	   KEANE		 Modified for DR 1434978                                                                                              
*/
package com.o2.techm.netcool.eai.o2gateway.troubleticket;

import com.o2.util.WorkQueue;;;

public interface TroubleTicketManager
{ 

   WorkQueue newTTList = new WorkQueue();
   WorkQueue upTTList = new WorkQueue();
   
   public void shutdown();

	public void initPlannedOutages() throws TroubleTicketException;

    public void createTroubleTicket(TroubleTicket troubleTicket)
        throws TroubleTicketException;
    // Start of Change for OSC 1434978
    // Changed the return variable as boolean 
    public boolean updateTroubleTicket(TroubleTicket troubleTicket) 
    	throws TroubleTicketException;     
    // End of Change for OSC 1434978
    public WorkQueue getCreateLinkList(); 
	public WorkQueue getUpdateLinkList();
	
	//Added for DR-16161094
	public boolean updateTroubleTicketTitle(TroubleTicket troubleTicket) 
	throws TroubleTicketException;  
	//End of Addition for DR-1616094	    
}