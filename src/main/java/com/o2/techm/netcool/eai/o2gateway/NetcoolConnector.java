package com.o2.techm.netcool.eai.o2gateway;

import java.text.SimpleDateFormat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.sybase.*;
;


/**
 * @author  aademij1
 * Abstract Class for the Reader and Writer Plugins
 */
public abstract class NetcoolConnector 
{
    private Logger log = LoggerFactory.getLogger(NetcoolConnector.class);

    public final String WSDL_FILE = "wsdl/NetcoolGateway.wsdl";    
    public static final String CIC_DATE_FORMAT = "o2gateway.cic.dateFormat"; 
    public static final String CIC_DATE_FORMAT_DEF = "dd/MM/yyyy HH:mm:ss";
    public static final String SYBASE_JDBC_URL = "o2gateway.sybase.jdbc.url"; 
    public static final String SYBASE_JDBC_URL_BKUP = "o2gateway.sybase.jdbc.url.backup";
    public static final String SYBASE_JDBC_LOGIN = "o2gateway.sybase.jdbc.login"; 
    public static final String SYBASE_JDBC_PASSWORD = "o2gateway.sybase.jdbc.password"; 
    public static final String THREAD_POOL_SIZE ="o2gateway.iduc.threadPoolSize";
    public static final String THREAD_POOL_SIZE_DEF ="10";
    public static final String SYBASE_LOCALE = "o2gateway.sybase.locale"; 
    public static final String CIC_DUMMY_STRING_COLUMN_NAME = "o2gateway.cic.dummyStringColumnName"; 
    public static final String CIC_DUMMY_STRING_COLUMN_NAME_DEF = "DummyVarchar"; 
    public static final String CIC_DUMMY_STRING_VALUE = "o2gateway.cic.dummyStringValue"; 
    public static final String CIC_DUMMY_STRING_VALUE_DEF = "O2gateway"; 
    public static final String CIC_DUMMY_INTEGER_COLUMN_NAME = "o2gateway.cic.dummyIntegerColumnName"; 
    public static final String CIC_DUMMY_INTEGER_COLUMN_NAME_DEF = "DummyInteger";
    public static final String CIC_DUMMY_INTEGER_VALUE = "o2gateway.cic.dummyIntegerValue";     
    public static final String CIC_DUMMY_INTEGER_VALUE_DEF = "0";
    public static final String CIC_MAP="MAP";
	public static final String GATEWAY_HEARTBEAT = "o2gateway.hearbeat.interval";
	public static final String GATEWAY_POLL = "o2gateway.poll.interval";
	public static final String SYS_POLL = "60";
    public static final String GATEWAY_EVENT_BUF_SZ = "o2gateway.maxeventbuffersize";
    
    
    private String apiLogin;
    private String apiPassword;
    private String apiPort;
    private String apiHost;
    private String jdbcUrl;
    private String jdbcLogin;
    private String jdbcPassword;
    private SimpleDateFormat sybaseDateFormat;
    private SimpleDateFormat cicDateFormat;
    private int locale;

    private SybaseManager sybaseManager;
  
    /**
     * Class constructor
     */
    public NetcoolConnector()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     * @throws Exception
     */
    public void shutdown() throws Exception
    {
        // TODO Auto-generated method stub

    }

   
}
