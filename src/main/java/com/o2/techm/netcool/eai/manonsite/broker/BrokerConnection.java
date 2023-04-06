package com.o2.techm.netcool.eai.manonsite.broker;

public interface BrokerConnection {

	String SMSFAILED = "SMS FAILURE";
	public abstract String sendSimpleSMS(String phoneNumber, String message)
			throws BrokerException;

	public abstract String sendSimpleSMS(String phoneNumber, String message,
			String Type) throws BrokerException;
	public abstract String sendSimpleSMS(String phoneNumbers[], String[] messages,
			String costId,String servId) throws BrokerException;

}