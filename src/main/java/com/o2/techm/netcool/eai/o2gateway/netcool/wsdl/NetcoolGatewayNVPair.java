/* Generated by WSDL Java Compiler. Please do not edit this file. NetcoolGatewayNVPair.java Version : 5.1 */
package com.o2.techm.netcool.eai.o2gateway.netcool.wsdl;



/**
 * com.o2.o2gateway.netcool.wsdl.NetcoolGatewayNVPair.
 */
public class NetcoolGatewayNVPair {

    /**
     * Target namespace of Schema in which this type is defined.
     */
    public static final String TARGET_NAMESPACE = "http://ps.iona.com/artix/o2/NetcoolGateway.wsdl";

    /**
     * QName of the type.
     */
    public static final javax.xml.namespace.QName QNAME = new javax.xml.namespace.QName("http://ps.iona.com/artix/o2/NetcoolGateway.wsdl", "NetcoolGateway.NVPair");


    private String aName;
    private String aValue;


    /**
     * getAName.
     * 
     * @return String
     */
    public String getAName() {
        return aName;
    }

    /**
     * setAName.
     * 
     * @param val (String)
     */
    public void setAName(String val) {
        this.aName = val;
    }

    /**
     * getAValue.
     * 
     * @return String
     */
    public String getAValue() {
        return aValue;
    }

    /**
     * setAValue.
     * 
     * @param val (String)
     */
    public void setAValue(String val) {
        this.aValue = val;
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
        if (aName != null) {
            buffer.append("aName : " + aName + "\n");
        }
        if (aValue != null) {
            buffer.append("aValue : " + aValue + "\n");
        }
        return buffer.toString();
    }
}