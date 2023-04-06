package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImpactType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ImpactType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Extensive/Widespread"/>
 *     &lt;enumeration value="Significant/Large"/>
 *     &lt;enumeration value="Moderate/Limited"/>
 *     &lt;enumeration value="Minor/Localized"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ImpactType")
@XmlEnum
public enum ImpactType {

    @XmlEnumValue("1-Extensive/Widespread")
    EXTENSIVE_WIDESPREAD("1-Extensive/Widespread"),
    @XmlEnumValue("2-Significant/Large")
    SIGNIFICANT_LARGE("2-Significant/Large"),
    @XmlEnumValue("3-Moderate/Limited")
    MODERATE_LIMITED("3-Moderate/Limited"),
    @XmlEnumValue("4-Minor/Localized")
    MINOR_LOCALIZED("4-Minor/Localized");
    private final String value;

    ImpactType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ImpactType fromValue(String v) {
        for (ImpactType c: ImpactType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
