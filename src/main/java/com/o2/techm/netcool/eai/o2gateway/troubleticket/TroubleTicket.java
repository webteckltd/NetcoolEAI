/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 12-Jun-2014            Indra               CD38583 - BMR                                                                                           
*/


package com.o2.techm.netcool.eai.o2gateway.troubleticket;

import com.o2.techm.netcool.eai.o2gateway.netcool.BasicCICEventHandling;

public class TroubleTicket {

	public TroubleTicket() {
	}

	/**
	 * @param troubleticket
	 **/
	public TroubleTicket(TroubleTicket troubleticket) {
		Urgency = troubleticket.Urgency;
		TTService = troubleticket.TTService;
		TTOpCat1 = troubleticket.TTOpCat1;
		TTOpCat2 = troubleticket.TTOpCat2;
		TTOpCat3 = troubleticket.TTOpCat3;
		TTOpCatRes1 = troubleticket.TTOpCatRes1;
		TTOpCatRes2 = troubleticket.TTOpCatRes2;
		TTOpCatRes3 = troubleticket.TTOpCatRes3;
		TTRemedyCI = troubleticket.TTRemedyCI;
		TTRemedyCIType = troubleticket.TTRemedyCIType;
		TTProdCat3 = troubleticket.TTProdCat3;
		Details = troubleticket.Details;
		Identifier = troubleticket.Identifier;
		FaultImpact = troubleticket.FaultImpact;
		FaultPriority = troubleticket.FaultPriority;
		FaultStatus = troubleticket.FaultStatus;
		FaultQueue = troubleticket.FaultQueue;
		ServerSerial = troubleticket.ServerSerial;
		TTFlag = troubleticket.TTFlag;
		TTNote = troubleticket.TTNote;
		TTDescription = troubleticket.TTDescription;
		OwnerUID = troubleticket.OwnerUID;
		TroubleTicket = troubleticket.TroubleTicket;
		
		CDS_ID = troubleticket.CDS_ID;
		AlertKey = troubleticket.AlertKey;
		NEName = troubleticket.NEName;
		AlarmImpact = troubleticket.AlarmImpact;
		CDS_Type = troubleticket.CDS_Type;
		CommitmentPoints = troubleticket.CommitmentPoints;
		CommitmentTotal = troubleticket.CommitmentTotal;
		FirstOccurrence = troubleticket.FirstOccurrence;
		LastOccurrence = troubleticket.LastOccurrence;
		Manager = troubleticket.Manager;
		NotificationFlag = troubleticket.NotificationFlag;
		ObjectType = troubleticket.ObjectType;
		OutageDuration = troubleticket.OutageDuration;
		OutageEnd = troubleticket.OutageEnd;
		OutageStart = troubleticket.OutageStart;
		OutageType = troubleticket.OutageType;
		Poll = troubleticket.Poll;
		ServerName = troubleticket.ServerName;
		ServiceGroup = troubleticket.ServiceGroup;
		Severity = troubleticket.Severity;
		StateChange = troubleticket.StateChange;
		Tally = troubleticket.Tally;
		TimeToFix = troubleticket.TimeToFix;
		TimeToFixUnits = troubleticket.TimeToFixUnits;
		TTField1 = troubleticket.TTField1;
		TTField2 = troubleticket.TTField2;
		TTField3 = troubleticket.TTField3;
		TTField4 = troubleticket.TTField4;
		TTElementID = troubleticket.TTElementID;
		TTCaseType = troubleticket.TTCaseType;
		TTCategory = troubleticket.TTCategory;
		TTItem = troubleticket.TTItem;
		// Added newly OSC :1435000;
		TTTitle = troubleticket.TTTitle;
		// 19/09/2011 Top25 Add variable
		// TTIncidentGroup = troubleticket.TTIncidentGroup;
		// 04/09/2012 OSC 652 Add variable
		TEXT02 = troubleticket.TEXT02;
		Class_ID = troubleticket.Class_ID;
		Class_Name = troubleticket.Class_Name;
		Alarm_Key = troubleticket.Alarm_Key;
		Alarm_Severity = troubleticket.Alarm_Severity;
		if (TEXT02 == null)
			TEXT02 = "62946";
		else if (TEXT02.length() < 4)
			TEXT02 = "62946"; 
	}

	public String getAlarmImpact() {
		return AlarmImpact;
	}

	public String getAlertKey() {
		return AlertKey;
	}

	public String getCDS_ID() {
		return CDS_ID;
	}

	public String getCDS_Type() {
		return CDS_Type;
	}

	public String getCommitmentPoints() {
		return CommitmentPoints;
	}

	public String getCommitmentTotal() {
		return CommitmentTotal;
	}

	public String getFaultImpact() {
		return FaultImpact;
	}

	public String getFaultPriority() {
		return FaultPriority;
	}

	public String getFaultStatus() {
		return FaultStatus;
	}

	public String getFirstOccurrence() {
		return FirstOccurrence;
	}

	public String getLastOccurrence() {
		return LastOccurrence;
	}

	public String getIdentifier() {
		return Identifier;
	}

	public String getManager() {
		return Manager;
	}

	public String getNEName() {
		return NEName;
	}

	public String getNotificationFlag() {
		return NotificationFlag;
	}

	public String getObjectType() {
		return ObjectType;
	}

	public String getOutageDuration() {
		return OutageDuration;
	}

	public String getOutageEnd() {
		return OutageEnd;
	}

	public String getOutageStart() {
		return OutageStart;
	}

	public String getOutageType() {
		return OutageType;
	}

	public String getOwnerUID() {
		return OwnerUID;
	}

	public String getPoll() {
		return Poll;
	}

	public String getServerName() {
		return ServerName;
	}

	public String getServerSerial() {
		return ServerSerial;
	}

	public String getServiceGroup() {
		return ServiceGroup;
	}

	public String getSeverity() {
		return Severity;
	}

	public String getStateChange() {
		return StateChange;
	}

	public String getTally() {
		return Tally;
	}

	public String getTimeToFix() {
		return TimeToFix;
	}

	public String getTimeToFixUnits() {
		return TimeToFixUnits;
	}

	public String getTroubleTicket() {
		return TroubleTicket;
	}

	public String getTTFlag() {
		return TTFlag;
	}

	public String getTTNote() {
		return TTNote;
	}

	public String getTTField1() {
		return TTField1;
	}

	public String getTTField2() {
		return TTField2;
	}

	public String getTTField3() {
		return TTField3;
	}

	public String getTTField4() {
		return TTField4;
	}

	public String getTTDescription() {
		return TTDescription;
	}

	public String getTTElementID() {
		return TTElementID;
	}

	public String getClass_ID() {
		return Class_ID;
	}
	
	public String getClass_Name() {
		return Class_Name;
	}
	
	public String getAlarm_Key() {
		return Alarm_Key;
	}
	
	public String getAlarm_Severity() {
		return Alarm_Severity;
	}
	
	public void setAlarmImpact(String alarmImpact) {
		AlarmImpact = alarmImpact;
	}

	public void setAlertKey(String alertKey) {
		AlertKey = alertKey;
	}

	public void setCDS_ID(String cds_id) {
		CDS_ID = cds_id;
	}

	public void setCDS_Type(String type) {
		CDS_Type = type;
	}

	public void setCommitmentPoints(String commitmentPoints) {
		CommitmentPoints = commitmentPoints;
	}

	public void setCommitmentTotal(String commitmentTotal) {
		CommitmentTotal = commitmentTotal;
	}

	public void setFaultImpact(String faultImpact) {
		FaultImpact = faultImpact;
	}

	public void setFaultPriority(String faultPriority) {
		FaultPriority = faultPriority;
	}

	public void setFaultStatus(String faultStatus) {
		FaultStatus = faultStatus;
	}

	public void setFirstOccurrence(String firstOccurrence) {
		FirstOccurrence = firstOccurrence;
	}

	public void setLastOccurrence(String lastOccurrence) {
		LastOccurrence = lastOccurrence;
	}

	public void setIdentifier(String identifier) {
		Identifier = identifier;
	}

	public void setManager(String manager) {
		Manager = manager;
	}

	public void setNEName(String name) {
		NEName = name;
	}

	public void setNotificationFlag(String notificationFlag) {
		NotificationFlag = notificationFlag == null ? BasicCICEventHandling.NOTIFICATION_FLAG_EMPTY
				: notificationFlag;
	}

	public void setObjectType(String objectType) {
		ObjectType = objectType;
	}

	public void setOutageDuration(String outageDuration) {
		OutageDuration = outageDuration;
	}

	public void setOutageEnd(String outageEnd) {
		OutageEnd = outageEnd;
	}

	public void setOutageStart(String outageStart) {
		OutageStart = outageStart;
	}



	public void setOutageType(String outageType) {
		OutageType = outageType;
	}

	public void setOwnerUID(String ownerUID) {
		OwnerUID = ownerUID;
	}

	public void setPoll(String poll) {
		Poll = poll;
	}

	public void setServerName(String serverName) {
		ServerName = serverName;
	}

	public void setServerSerial(String serverSerial) {
		ServerSerial = serverSerial;
	}

	public void setServiceGroup(String serviceGroup) {
		ServiceGroup = serviceGroup;
	}

	public void setSeverity(String severity) {
		Severity = severity;
	}

	public void setStateChange(String stateChange) {
		StateChange = stateChange;
	}

	public void setTally(String tally) {
		Tally = tally;
	}

	public void setTimeToFix(String timeToFix) {
		TimeToFix = timeToFix;
	}

	public void setTimeToFixUnits(String timeToFixUnits) {
		TimeToFixUnits = timeToFixUnits;
	}

	public void setTroubleTicket(String troubleTicket) {
		TroubleTicket = troubleTicket;
	}

	public void setTTFlag(String flag) {
		TTFlag = flag;
	}

	public void setTTNote(String note) {
		TTNote = note;
	}

	public void setTTField1(String vantive1) {
		TTField1 = vantive1;
	}

	public void setTTField2(String vantive2) {
		TTField2 = vantive2;
	}

	public void setTTField3(String vantive3) {
		TTField3 = vantive3;
	}

	public void setTTField4(String vantive4) {
		TTField4 = vantive4;
	}

	public void setTTDescription(String vantive5) {
		TTDescription = vantive5;
	}

	public void setTTElementID(String vantiveRef) {
		TTElementID = vantiveRef;
	}
	
	public void setClass_ID(String class_ID) {
		Class_ID = class_ID;
	}
	
	public void setClass_Name(String class_Name) {
		Class_Name = class_Name;
	}

	public void setAlarm_Key(String alarm_Key) {
		Alarm_Key = alarm_Key;
	}
	
	public void setAlarm_Severity(String alarm_Severity) {
		Alarm_Severity = alarm_Severity;
	}
	
	public String getSummary() {
		return Summary;
	}

	public void setSummary(String summary) {
		Summary = summary;
	}

	public String getManufacturer() {
		return Manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		Manufacturer = manufacturer;
	}

	/**
	 * @return the tTCaseType
	 */
	public String getTTCaseType() {
		return TTCaseType;
	}

	/**
	 * @param caseType
	 *            the tTCaseType to set
	 */
	public void setTTCaseType(String caseType) {
		TTCaseType = caseType;
	}

	/**
	 * @return the tTCategory
	 */
	public String getTTCategory() {
		return TTCategory;
	}

	/**
	 * @param category
	 *            the tTCategory to set
	 */
	public void setTTCategory(String category) {
		TTCategory = category;
	}

	/**
	 * @return the tTItem
	 */
	public String getTTItem() {
		return TTItem;
	}

	// Added newly OSC :1435000;
	/**
	 * @return the tTTTitle
	 */

	public String getTTTitle() {
		return TTTitle;
	}

	/**
	 * @param category
	 *            the tTTitle to set
	 */
	// Added newly OSC :1435000;
	public void setTTTitle(String title) {
		TTTitle = title;
	}

	// 19/09/2011 Top25 Add two variables GET/SET methods
	/**
	 * @param incidentGroup
	 *            the TTIncidentGroup to set
	 */
	/*
	 * public void setTTIncidentGroup(String incidentGroup) { TTIncidentGroup =
	 * incidentGroup; } /**
	 * 
	 * @return the TTIncidentGroup
	 */
	/*
	 * public String getTTIncidentGroup() { return TTIncidentGroup; }
	 */

	// 04/09/2012 OSC 652 Add two variables GET/SET methods
	public String getTEXT02() {
		return TEXT02;
	}

	public void setTEXT02(String shortLongCode) {
		TEXT02 = shortLongCode;
	}

	public String getFaultQueue() {
		return FaultQueue;
	}

	public void setFaultQueue(String faultQueue) {
		FaultQueue = faultQueue;
	}

	public String getUrgency() {
		return Urgency;
	}

	public void setUrgency(String urgency) {
		this.Urgency = urgency;
	}

	public String getTTService() {
		return TTService;
	}

	public void setTTService(String tTService) {
		TTService = tTService;
	}

	public String getTTOpCat1() {
		return TTOpCat1;
	}

	public void setTTOpCat1(String tTOpCat1) {
		TTOpCat1 = tTOpCat1;
	}

	public String getTTOpCat2() {
		return TTOpCat2;
	}

	public void setTTOpCat2(String tTOpCat2) {
		TTOpCat2 = tTOpCat2;
	}

	public String getTTOpCat3() {
		return TTOpCat3;
	}

	public void setTTOpCat3(String tTOpCat3) {
		TTOpCat3 = tTOpCat3;
	}

	public String getTTOpCatRes1() {
		return TTOpCatRes1;
	}

	public void setTTOpCatRes1(String tTOpCatRes1) {
		TTOpCatRes1 = tTOpCatRes1;
	}

	public String getTTOpCatRes2() {
		return TTOpCatRes2;
	}

	public void setTTOpCatRes2(String tTOpCatRes2) {
		TTOpCatRes2 = tTOpCatRes2;
	}

	public String getTTOpCatRes3() {
		return TTOpCatRes3;
	}

	public void setTTOpCatRes3(String tTOpCatRes3) {
		TTOpCatRes3 = tTOpCatRes3;
	}

	public String getTTRemedyCI() {
		return TTRemedyCI;
	}

	public void setTTRemedyCI(String tTRemedyCI) {
		TTRemedyCI = tTRemedyCI;
	}

	public String getTTRemedyCIType() {
		return TTRemedyCIType;
	}

	public void setTTRemedyCIType(String tTRemedyCIType) {
		TTRemedyCIType = tTRemedyCIType;
	}

	public String getTTProdCat3() {
		return TTProdCat3;
	}

	public void setTTProdCat3(String tTProdCat3) {
		TTProdCat3 = tTProdCat3;
	}

	public String getDetails() {
		return Details;
	}

	public void setDetails(String details) {
		Details = details;
	}

	private String Urgency;
	private String TTService;
	private String TTOpCat1;
	private String TTOpCat2;
	private String TTOpCat3;
	private String TTOpCatRes1;
	private String TTOpCatRes2;
	private String TTOpCatRes3;
	private String TTRemedyCI;
	private String TTRemedyCIType;
	private String TTProdCat3;
	private String Details;

	private String Identifier;
	private String CDS_ID;
	private String AlertKey;
	private String NEName;
	private String AlarmImpact;
	private String CDS_Type;
	private String CommitmentPoints;
	private String CommitmentTotal;
	private String FaultImpact;
	private String FaultPriority;
	private String FaultStatus;
	private String FaultQueue;
	private String FirstOccurrence;
	private String LastOccurrence;
	private String Manager;
	private String Manufacturer;
	private String NotificationFlag;
	private String ObjectType;
	private String OutageDuration;
	private String OutageEnd;
	private String OutageStart;
	private String OutageType;
	private String OwnerUID;
	private String Poll;
	private String ServerName;
	private String ServerSerial;
	private String ServiceGroup;
	private String Severity;
	private String StateChange;
	private String Summary;
	private String Tally;
	private String TimeToFix;
	private String TimeToFixUnits;
	private String TroubleTicket;
	private String TTFlag;
	private String TTNote;
	private String TTField1;
	private String TTField2;
	private String TTField3;
	private String TTField4;
	private String TTDescription;
	private String TTElementID;
	private String TTCaseType;
	private String TTCategory;
	private String TTItem;
	// Added newly OSC :1435000;
	private String TTTitle;
	// 19/09/2011 Top25 Add variable
	// private String TTIncidentGroup;
	// 04/09/2012 OSC 652 Add variable
	private String TEXT02;
	//OSC1216
	private String Class_ID;
	private String Class_Name;
	private String Alarm_Key;
	private String Alarm_Severity;
}
