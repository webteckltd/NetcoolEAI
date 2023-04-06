
package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.o2.osiris.remedy.webservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InputMapping1IncidentType_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Incident_Type");
    private final static QName _InputMapping1Status_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Status");
    private final static QName _InputMapping1Impact_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Impact");
    private final static QName _InputMapping1WorkInfoType_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Work_Info_Type");
    private final static QName _InputMapping1Urgency_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Urgency");
    private final static QName _InputMapping1Locked_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Locked");
    private final static QName _IncidentSubmitAddWIService_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Incident_SubmitAddWI_Service");
    private final static QName _IncidentSubmitAddWIServiceResponse_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Incident_SubmitAddWI_ServiceResponse");
    private final static QName _AuthenticationInfo_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "AuthenticationInfo");
    private final static QName _OutputMapping1Priority_QNAME = new QName("urn:TGTUK_NETCOOL_HPD_WS", "Priority");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.o2.osiris.remedy.webservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AuthenticationInfo }
     * 
     */
    public AuthenticationInfo createAuthenticationInfo() {
        return new AuthenticationInfo();
    }

    /**
     * Create an instance of {@link OutputMapping1 }
     * 
     */
    public OutputMapping1 createOutputMapping1() {
        return new OutputMapping1();
    }

    /**
     * Create an instance of {@link InputMapping1 }
     * 
     */
    public InputMapping1 createInputMapping1() {
        return new InputMapping1();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IncidentTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Incident_Type", scope = InputMapping1 .class)
    public JAXBElement<IncidentTypeType> createInputMapping1IncidentType(IncidentTypeType value) {
        return new JAXBElement<IncidentTypeType>(_InputMapping1IncidentType_QNAME, IncidentTypeType.class, InputMapping1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Status", scope = InputMapping1 .class)
    public JAXBElement<StatusType> createInputMapping1Status(StatusType value) {
        return new JAXBElement<StatusType>(_InputMapping1Status_QNAME, StatusType.class, InputMapping1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImpactType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Impact", scope = InputMapping1 .class)
    public JAXBElement<ImpactType> createInputMapping1Impact(ImpactType value) {
        return new JAXBElement<ImpactType>(_InputMapping1Impact_QNAME, ImpactType.class, InputMapping1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WorkInfoTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Work_Info_Type", scope = InputMapping1 .class)
    public JAXBElement<WorkInfoTypeType> createInputMapping1WorkInfoType(WorkInfoTypeType value) {
        return new JAXBElement<WorkInfoTypeType>(_InputMapping1WorkInfoType_QNAME, WorkInfoTypeType.class, InputMapping1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UrgencyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Urgency", scope = InputMapping1 .class)
    public JAXBElement<UrgencyType> createInputMapping1Urgency(UrgencyType value) {
        return new JAXBElement<UrgencyType>(_InputMapping1Urgency_QNAME, UrgencyType.class, InputMapping1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LockedType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Locked", scope = InputMapping1 .class)
    public JAXBElement<LockedType> createInputMapping1Locked(LockedType value) {
        return new JAXBElement<LockedType>(_InputMapping1Locked_QNAME, LockedType.class, InputMapping1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InputMapping1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Incident_SubmitAddWI_Service")
    public JAXBElement<InputMapping1> createIncidentSubmitAddWIService(InputMapping1 value) {
        return new JAXBElement<InputMapping1>(_IncidentSubmitAddWIService_QNAME, InputMapping1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OutputMapping1 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Incident_SubmitAddWI_ServiceResponse")
    public JAXBElement<OutputMapping1> createIncidentSubmitAddWIServiceResponse(OutputMapping1 value) {
        return new JAXBElement<OutputMapping1>(_IncidentSubmitAddWIServiceResponse_QNAME, OutputMapping1 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthenticationInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "AuthenticationInfo")
    public JAXBElement<AuthenticationInfo> createAuthenticationInfo(AuthenticationInfo value) {
        return new JAXBElement<AuthenticationInfo>(_AuthenticationInfo_QNAME, AuthenticationInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Status", scope = OutputMapping1 .class)
    public JAXBElement<StatusType> createOutputMapping1Status(StatusType value) {
        return new JAXBElement<StatusType>(_InputMapping1Status_QNAME, StatusType.class, OutputMapping1 .class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PriorityType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:TGTUK_NETCOOL_HPD_WS", name = "Priority", scope = OutputMapping1 .class)
    public JAXBElement<PriorityType> createOutputMapping1Priority(PriorityType value) {
        return new JAXBElement<PriorityType>(_OutputMapping1Priority_QNAME, PriorityType.class, OutputMapping1 .class, value);
    }

}
