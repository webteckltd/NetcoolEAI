/* Generated by WSDL Java Compiler. Please do not edit this file. NetcoolGatewayReader.java Version : 5.1 */
package com.o2.techm.netcool.eai.o2gateway.netcool.wsdl;

import java.rmi.RemoteException;


/**
 * com.o2.o2gateway.netcool.wsdl.NetcoolGatewayReader
 */
public interface NetcoolGatewayReader extends java.rmi.Remote {

    /**
     * event.
     * 
     * @param aEvent (NetcoolGatewayNetcoolEvent)
     * @return String
     * @throws NetcoolGatewayProcessEventFailure_Exception  (NetcoolGatewayProcessEventFailure_Exception)
     */
    public String event(NetcoolGatewayNetcoolEvent aEvent) throws NetcoolGatewayProcessEventFailure_Exception, RemoteException;

}
