package com.o2.techm.netcool.eai.o2gateway.netcool;

import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicket;

public interface GatewayEventHandler
{
    /*
     * Handle the trouble ticket. Return true if no subsequent handlers in the chain
     * should be processed, false otherwise. 
     */
    boolean handleEvent(TroubleTicket event) throws GatewayEventHandlingException;
}
