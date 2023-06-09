
package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Incident_TypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Incident_TypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="User Service Restoration"/>
 *     &lt;enumeration value="User Service Request"/>
 *     &lt;enumeration value="Infrastructure Restoration"/>
 *     &lt;enumeration value="Infrastructure Event"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Incident_TypeType")
@XmlEnum
public enum IncidentTypeType {

    @XmlEnumValue("User Service Restoration")
    USER_SERVICE_RESTORATION("User Service Restoration"),
    @XmlEnumValue("User Service Request")
    USER_SERVICE_REQUEST("User Service Request"),
    @XmlEnumValue("Infrastructure Restoration")
    INFRASTRUCTURE_RESTORATION("Infrastructure Restoration"),
    @XmlEnumValue("Infrastructure Event")
    INFRASTRUCTURE_EVENT("Infrastructure Event");
    private final String value;

    IncidentTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static IncidentTypeType fromValue(String v) {
        for (IncidentTypeType c: IncidentTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
