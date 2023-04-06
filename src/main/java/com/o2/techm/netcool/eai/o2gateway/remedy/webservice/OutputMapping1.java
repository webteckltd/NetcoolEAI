
package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OutputMapping1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutputMapping1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errorMsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Incident_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Priority" type="{urn:TGTUK_NETCOOL_HPD_WS}PriorityType" minOccurs="0"/>
 *         &lt;element name="Status" type="{urn:TGTUK_NETCOOL_HPD_WS}StatusType" minOccurs="0"/>
 *         &lt;element name="Assigned_Group" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputMapping1", propOrder = {
    "errorCode",
    "errorMsg",
    "incidentID",
    "priority",
    "status",
    "assignedGroup"
})
public class OutputMapping1 {

    protected String errorCode;
    protected String errorMsg;
    @XmlElement(name = "Incident_ID", defaultValue = "Incident_ID")
    protected String incidentID;
    @XmlElementRef(name = "Priority", namespace = "urn:TGTUK_NETCOOL_HPD_WS", type = JAXBElement.class, required = false)
    protected JAXBElement<PriorityType> priority;
    @XmlElementRef(name = "Status", namespace = "urn:TGTUK_NETCOOL_HPD_WS", type = JAXBElement.class, required = false)
    protected JAXBElement<StatusType> status;
    @XmlElement(name = "Assigned_Group")
    protected String assignedGroup;

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorCode(String value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the errorMsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * Sets the value of the errorMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMsg(String value) {
        this.errorMsg = value;
    }

    /**
     * Gets the value of the incidentID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncidentID() {
        return incidentID;
    }

    /**
     * Sets the value of the incidentID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncidentID(String value) {
        this.incidentID = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PriorityType }{@code >}
     *     
     */
    public JAXBElement<PriorityType> getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PriorityType }{@code >}
     *     
     */
    public void setPriority(JAXBElement<PriorityType> value) {
        this.priority = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link StatusType }{@code >}
     *     
     */
    public JAXBElement<StatusType> getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link StatusType }{@code >}
     *     
     */
    public void setStatus(JAXBElement<StatusType> value) {
        this.status = value;
    }

    /**
     * Gets the value of the assignedGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssignedGroup() {
        return assignedGroup;
    }

    /**
     * Sets the value of the assignedGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssignedGroup(String value) {
        this.assignedGroup = value;
    }

}
