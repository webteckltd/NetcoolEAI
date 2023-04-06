package com.o2.techm.netcool.eai.o2gateway.netcool.wsdl;


/**
 * com.o2.o2gateway.netcool.wsdl.NetcoolGatewayReaderImpl
 */
public class NetcoolGatewayReaderImpl implements NetcoolGatewayReader {   //changed from java.rmi.Remote to NetcoolGatewayReader by sravan

    /**
     * event.
     * 
     * @param aEvent (NetcoolGatewayNetcoolEvent)
     * @return String
     * @throws NetcoolGatewayProcessEventFailure_Exception  (NetcoolGatewayProcessEventFailure_Exception)
     */
    public String event(NetcoolGatewayNetcoolEvent aEvent) throws NetcoolGatewayProcessEventFailure_Exception {
        // User code goes in here.
        return "success";
    }
}
