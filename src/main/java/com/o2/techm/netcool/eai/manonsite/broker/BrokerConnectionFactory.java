package com.o2.techm.netcool.eai.manonsite.broker;

import com.o2.techm.netcool.eai.manonsite.broker.wintel.WintelBrokerConnection;

public class BrokerConnectionFactory {

	public static BrokerConnection newInstance(String os){

            return new WintelBrokerConnection();
       
    }	
}
