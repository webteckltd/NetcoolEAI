
package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="New"/>
 *     &lt;enumeration value="Assigned"/>
 *     &lt;enumeration value="In Progress"/>
 *     &lt;enumeration value="Pending"/>
 *     &lt;enumeration value="Resolved"/>
 *     &lt;enumeration value="Closed"/>
 *     &lt;enumeration value="Cancelled"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StatusType")
@XmlEnum
public enum StatusType {

    @XmlEnumValue("New")
    NEW("New"),
    @XmlEnumValue("Assigned")
    ASSIGNED("Assigned"),
    @XmlEnumValue("In Progress")
    IN_PROGRESS("In Progress"),
    @XmlEnumValue("Pending")
    PENDING("Pending"),
    @XmlEnumValue("Resolved")
    RESOLVED("Resolved"),
    @XmlEnumValue("Closed")
    CLOSED("Closed"),
    @XmlEnumValue("Cancelled")
    CANCELLED("Cancelled");
    private final String value;

    StatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusType fromValue(String v) {
        for (StatusType c: StatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
