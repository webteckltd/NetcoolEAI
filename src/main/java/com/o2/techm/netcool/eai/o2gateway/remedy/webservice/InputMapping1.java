
package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for InputMapping1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputMapping1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Action" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Status" type="{urn:TGTUK_NETCOOL_HPD_WS}StatusType" minOccurs="0"/>
 *         &lt;element name="Company" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Customer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Summary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Service" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Impact" type="{urn:TGTUK_NETCOOL_HPD_WS}ImpactType" minOccurs="0"/>
 *         &lt;element name="Urgency" type="{urn:TGTUK_NETCOOL_HPD_WS}UrgencyType" minOccurs="0"/>
 *         &lt;element name="Incident_Type" type="{urn:TGTUK_NETCOOL_HPD_WS}Incident_TypeType" minOccurs="0"/>
 *         &lt;element name="Operational_Categorization_Tier_1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Operational_Categorization_Tier_2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Operational_Categorization_Tier_3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Resolution_Categorization_Tier_1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Resolution_Categorization_Tier_2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Resolution_Categorization_Tier_3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CI_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CI_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Product_Categorization_Tier_3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Contact_Method" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Contact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="External_Ticket_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServerSerial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Alarm_First_Occurence" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Incident_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Work_Info_Summary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Work_Info_Note" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Work_Info_Type" type="{urn:TGTUK_NETCOOL_HPD_WS}Work_Info_TypeType" minOccurs="0"/>
 *         &lt;element name="Locked" type="{urn:TGTUK_NETCOOL_HPD_WS}LockedType" minOccurs="0"/>
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
@XmlType(name = "InputMapping1", propOrder = {
    "action",
    "status",
    "company",
    "customer",
    "summary",
    "notes",
    "service",
    "impact",
    "urgency",
    "incidentType",
    "operationalCategorizationTier1",
    "operationalCategorizationTier2",
    "operationalCategorizationTier3",
    "resolutionCategorizationTier1",
    "resolutionCategorizationTier2",
    "resolutionCategorizationTier3",
    "ciName",
    "ciType",
    "productCategorizationTier3",
    "contactMethod",
    "contact",
    "externalTicketID",
    "serverSerial",
    "alarmFirstOccurence",
    "incidentID",
    "workInfoSummary",
    "workInfoNote",
    "workInfoType",
    "locked",
    "assignedGroup"
})
public class InputMapping1 {

    @XmlElement(name = "Action")
    protected String action;
    @XmlElementRef(name = "Status", namespace = "urn:TGTUK_NETCOOL_HPD_WS", type = JAXBElement.class, required = false)
    protected JAXBElement<StatusType> status;
    @XmlElement(name = "Company")
    protected String company;
    @XmlElement(name = "Customer")
    protected String customer;
    @XmlElement(name = "Summary")
    protected String summary;
    @XmlElement(name = "Notes")
    protected String notes;
    @XmlElement(name = "Service")
    protected String service;
    @XmlElementRef(name = "Impact", namespace = "urn:TGTUK_NETCOOL_HPD_WS", type = JAXBElement.class, required = false)
    protected JAXBElement<ImpactType> impact;
    @XmlElementRef(name = "Urgency", namespace = "urn:TGTUK_NETCOOL_HPD_WS", type = JAXBElement.class, required = false)
    protected JAXBElement<UrgencyType> urgency;
    @XmlElementRef(name = "Incident_Type", namespace = "urn:TGTUK_NETCOOL_HPD_WS", type = JAXBElement.class, required = false)
    protected JAXBElement<IncidentTypeType> incidentType;
    @XmlElement(name = "Operational_Categorization_Tier_1")
    protected String operationalCategorizationTier1;
    @XmlElement(name = "Operational_Categorization_Tier_2")
    protected String operationalCategorizationTier2;
    @XmlElement(name = "Operational_Categorization_Tier_3")
    protected String operationalCategorizationTier3;
    @XmlElement(name = "Resolution_Categorization_Tier_1")
    protected String resolutionCategorizationTier1;
    @XmlElement(name = "Resolution_Categorization_Tier_2")
    protected String resolutionCategorizationTier2;
    @XmlElement(name = "Resolution_Categorization_Tier_3")
    protected String resolutionCategorizationTier3;
    @XmlElement(name = "CI_Name")
    protected String ciName;
    @XmlElement(name = "CI_Type")
    protected String ciType;
    @XmlElement(name = "Product_Categorization_Tier_3")
    protected String productCategorizationTier3;
    @XmlElement(name = "Contact_Method")
    protected String contactMethod;
    @XmlElement(name = "Contact")
    protected String contact;
    @XmlElement(name = "External_Ticket_ID")
    protected String externalTicketID;
    @XmlElement(name = "ServerSerial")
    protected String serverSerial;
    @XmlElement(name = "Alarm_First_Occurence")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar alarmFirstOccurence;
    @XmlElement(name = "Incident_ID")
    protected String incidentID;
    @XmlElement(name = "Work_Info_Summary")
    protected String workInfoSummary;
    @XmlElement(name = "Work_Info_Note")
    protected String workInfoNote;
    @XmlElementRef(name = "Work_Info_Type", namespace = "urn:TGTUK_NETCOOL_HPD_WS", type = JAXBElement.class, required = false)
    protected JAXBElement<WorkInfoTypeType> workInfoType;
    @XmlElementRef(name = "Locked", namespace = "urn:TGTUK_NETCOOL_HPD_WS", type = JAXBElement.class, required = false)
    protected JAXBElement<LockedType> locked;
    @XmlElement(name = "Assigned_Group")
    protected String assignedGroup;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
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
     * Gets the value of the company property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompany() {
        return company;
    }

    /**
     * Sets the value of the company property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompany(String value) {
        this.company = value;
    }

    /**
     * Gets the value of the customer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Sets the value of the customer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomer(String value) {
        this.customer = value;
    }

    /**
     * Gets the value of the summary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets the value of the summary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSummary(String value) {
        this.summary = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Gets the value of the impact property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ImpactType }{@code >}
     *     
     */
    public JAXBElement<ImpactType> getImpact() {
        return impact;
    }

    /**
     * Sets the value of the impact property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ImpactType }{@code >}
     *     
     */
    public void setImpact(JAXBElement<ImpactType> value) {
        this.impact = value;
    }

    /**
     * Gets the value of the urgency property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UrgencyType }{@code >}
     *     
     */
    public JAXBElement<UrgencyType> getUrgency() {
        return urgency;
    }

    /**
     * Sets the value of the urgency property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UrgencyType }{@code >}
     *     
     */
    public void setUrgency(JAXBElement<UrgencyType> value) {
        this.urgency = value;
    }

    /**
     * Gets the value of the incidentType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link IncidentTypeType }{@code >}
     *     
     */
    public JAXBElement<IncidentTypeType> getIncidentType() {
        return incidentType;
    }

    /**
     * Sets the value of the incidentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link IncidentTypeType }{@code >}
     *     
     */
    public void setIncidentType(JAXBElement<IncidentTypeType> value) {
        this.incidentType = value;
    }

    /**
     * Gets the value of the operationalCategorizationTier1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationalCategorizationTier1() {
        return operationalCategorizationTier1;
    }

    /**
     * Sets the value of the operationalCategorizationTier1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationalCategorizationTier1(String value) {
        this.operationalCategorizationTier1 = value;
    }

    /**
     * Gets the value of the operationalCategorizationTier2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationalCategorizationTier2() {
        return operationalCategorizationTier2;
    }

    /**
     * Sets the value of the operationalCategorizationTier2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationalCategorizationTier2(String value) {
        this.operationalCategorizationTier2 = value;
    }

    /**
     * Gets the value of the operationalCategorizationTier3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationalCategorizationTier3() {
        return operationalCategorizationTier3;
    }

    /**
     * Sets the value of the operationalCategorizationTier3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationalCategorizationTier3(String value) {
        this.operationalCategorizationTier3 = value;
    }

    /**
     * Gets the value of the resolutionCategorizationTier1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolutionCategorizationTier1() {
        return resolutionCategorizationTier1;
    }

    /**
     * Sets the value of the resolutionCategorizationTier1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolutionCategorizationTier1(String value) {
        this.resolutionCategorizationTier1 = value;
    }

    /**
     * Gets the value of the resolutionCategorizationTier2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolutionCategorizationTier2() {
        return resolutionCategorizationTier2;
    }

    /**
     * Sets the value of the resolutionCategorizationTier2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolutionCategorizationTier2(String value) {
        this.resolutionCategorizationTier2 = value;
    }

    /**
     * Gets the value of the resolutionCategorizationTier3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolutionCategorizationTier3() {
        return resolutionCategorizationTier3;
    }

    /**
     * Sets the value of the resolutionCategorizationTier3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolutionCategorizationTier3(String value) {
        this.resolutionCategorizationTier3 = value;
    }

    /**
     * Gets the value of the ciName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCIName() {
        return ciName;
    }

    /**
     * Sets the value of the ciName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCIName(String value) {
        this.ciName = value;
    }

    /**
     * Gets the value of the ciType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCIType() {
        return ciType;
    }

    /**
     * Sets the value of the ciType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCIType(String value) {
        this.ciType = value;
    }

    /**
     * Gets the value of the productCategorizationTier3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductCategorizationTier3() {
        return productCategorizationTier3;
    }

    /**
     * Sets the value of the productCategorizationTier3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductCategorizationTier3(String value) {
        this.productCategorizationTier3 = value;
    }

    /**
     * Gets the value of the contactMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactMethod() {
        return contactMethod;
    }

    /**
     * Sets the value of the contactMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactMethod(String value) {
        this.contactMethod = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContact(String value) {
        this.contact = value;
    }

    /**
     * Gets the value of the externalTicketID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalTicketID() {
        return externalTicketID;
    }

    /**
     * Sets the value of the externalTicketID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalTicketID(String value) {
        this.externalTicketID = value;
    }

    /**
     * Gets the value of the serverSerial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerSerial() {
        return serverSerial;
    }

    /**
     * Sets the value of the serverSerial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerSerial(String value) {
        this.serverSerial = value;
    }

    /**
     * Gets the value of the alarmFirstOccurence property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAlarmFirstOccurence() {
        return alarmFirstOccurence;
    }

    /**
     * Sets the value of the alarmFirstOccurence property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAlarmFirstOccurence(XMLGregorianCalendar value) {
        this.alarmFirstOccurence = value;
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
     * Gets the value of the workInfoSummary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkInfoSummary() {
        return workInfoSummary;
    }

    /**
     * Sets the value of the workInfoSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkInfoSummary(String value) {
        this.workInfoSummary = value;
    }

    /**
     * Gets the value of the workInfoNote property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkInfoNote() {
        return workInfoNote;
    }

    /**
     * Sets the value of the workInfoNote property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkInfoNote(String value) {
        this.workInfoNote = value;
    }

    /**
     * Gets the value of the workInfoType property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link WorkInfoTypeType }{@code >}
     *     
     */
    public JAXBElement<WorkInfoTypeType> getWorkInfoType() {
        return workInfoType;
    }

    /**
     * Sets the value of the workInfoType property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link WorkInfoTypeType }{@code >}
     *     
     */
    public void setWorkInfoType(JAXBElement<WorkInfoTypeType> value) {
        this.workInfoType = value;
    }

    /**
     * Gets the value of the locked property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LockedType }{@code >}
     *     
     */
    public JAXBElement<LockedType> getLocked() {
        return locked;
    }

    /**
     * Sets the value of the locked property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LockedType }{@code >}
     *     
     */
    public void setLocked(JAXBElement<LockedType> value) {
        this.locked = value;
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
