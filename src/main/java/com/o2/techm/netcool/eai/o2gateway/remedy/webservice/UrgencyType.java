
package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UrgencyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UrgencyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Critical"/>
 *     &lt;enumeration value="High"/>
 *     &lt;enumeration value="Medium"/>
 *     &lt;enumeration value="Low"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UrgencyType")
@XmlEnum
public enum UrgencyType {

    @XmlEnumValue("1-Critical")
    CRITICAL("1-Critical"),
    @XmlEnumValue("2-High")
    HIGH("2-High"),
    @XmlEnumValue("3-Medium")
    MEDIUM("3-Medium"),
    @XmlEnumValue("4-Low")
    LOW("4-Low");
    private final String value;

    UrgencyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UrgencyType fromValue(String v) {
        for (UrgencyType c: UrgencyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
