/* Generated by WSDL Java Compiler. Please do not edit this file. NetcoolGatewayReaderEventResult.java Version : 5.1 */
package com.o2.techm.netcool.eai.o2gateway.netcool.wsdl;



/**
 * com.o2.o2gateway.netcool.wsdl.NetcoolGatewayReaderEventResult.
 */
public class NetcoolGatewayReaderEventResult {

    /**
     * Target namespace of Schema in which this type is defined.
     */
    public static final String TARGET_NAMESPACE = "http://ps.iona.com/artix/o2/NetcoolGateway.wsdl";

    /**
     * QName of the type.
     */
    public static final javax.xml.namespace.QName QNAME = null;


    private String _return;


    /**
     * get_return.
     * 
     * @return String
     */
    public String get_return() {
        return _return;
    }

    /**
     * set_return.
     * 
     * @param val (String)
     */
    public void set_return(String val) {
        this._return = val;
    }

    /**
     * Returns the qname of the object.
     * @return javax.xml.namespace.QName the qname of the object.
     */
    public javax.xml.namespace.QName _getQName() {
        return QNAME;
    }

    /**
     * Returns a string representation of the object.
     * @return String a string representation of the object.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (_return != null) {
            buffer.append("_return : " + _return + "\n");
        }
        return buffer.toString();
    }
}
