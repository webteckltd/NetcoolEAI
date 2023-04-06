
package com.o2.techm.netcool.eai.manonsite.common;

import javax.xml.namespace.QName;

public class Constants
{
	
	public static final String MANONSITE_DIR_PROPERTY = "manonsite.dir";
	public static final String BROKER_PROPERTY = "broker.connection";
	
	public static final String MRS_PORT = "SOAPOverHTTP";
	public static final String MRS_SERVICE = "MRSService";
	//public static final String MRS_NAMESPACE = "http://ps.iona.com/artix/mrs.wsdl";
	public static final String MRS_NAMESPACE = "http://217.34.39.132:21/jspupload/upload/mrs.wsdl";
	public static String MRS_WSDL = "wsdl/MRSService.wsdl";
	public static String SOCKET_PROBE_WSDL = "wsdl/MOSSocketProbe.wsdl";
	
	public static final String SERVER_ADMIN_NAMESPACE = "http://ps.iona.com/artix/ServerAdmin.wsdl"; 
	public static final String SERVER_ADMIN_WSDL = "wsdl/ServerAdmin.wsdl"; 
	public static final String NETCOOL_GATEWAY_WRITER_PORT = "NetcoolGateway.WriterCORBAPort";
	public static final String NETCOOL_GATEWAY_WRITER_SERVICE = "NetcoolGateway.WriterCORBAService";
	public static final String NETCOOL_GATEWAY_READER_PORT = "NetcoolGateway.ReaderCORBAPort";
	public static final String NETCOOL_GATEWAY_READER_SERVICE = "NetcoolGateway.ReaderCORBAService";
	public static final String NETCOOL_GATEWAY_CLIENT_PORT = "NetcoolGateway.GatewayClientCORBAPort";
	public static final String NETCOOL_GATEWAY_CLIENT_SERVICE = "NetcoolGateway.GatewayClientCORBAService";
	public static final String NETCOOL_WSDL_NAMESPACE = "http://schemas.iona.com/idl/NetcoolGateway.idl";
	public static final String SOCKET_PROBE_PORT = "TaggedOverTCP";
	public static final QName SOCKET_PROBE_PORT_QNAME = new QName("", SOCKET_PROBE_PORT);
	public static final String SOCKET_PROBE_NAMESPACE = "http://ps.iona.com/artix/o2/MOSSocketProbe.wsdl"; 
	public static final String SOCKET_PROBE_SERVICE = "SocketProbe";
	public static final QName SOCKET_PROBE_SERVICE_QNAME = new QName("http://ps.iona.com/artix/o2/MOSSocketProbe.wsdl",
			SOCKET_PROBE_SERVICE);
	
	public static final String SERVER_ADMIN_WSDL_NAMESPACE = "http://ps.iona.com/artix/ServerAdmin.wsdl"; 
	public static final String SERVER_ADMIN_SERVICE = "ServerAdmin";
	public static final String SERVER_ADMIN_PORT = "SOAPOverHTTP";
	
	public static final String IDUC_POOL = "IDUCPOOL";
	public static final String JDBC_DRIVER = "com.sybase.jdbc2.jdbc.SybDriver";
	
	public static final int SECLEVEL_NONE = 0;
	public static final int SECLEVEL_FIRST = 1;
	public static final int SECLEVEL_SECOND = 2;
	public static final int SECLEVEL_THRID = 3;
	public static final String []CMDSTATUS = {"OK", "FAILED", "BLOCKED", "LIMITEXCEEDED","COMMANDFAILURE"};

	public static final String NETWORKID = "NETWORKID";
	public static final String SERVICEID = "SERVICEID";
	public static final String SMSTEXT = "TEXT";
	public static final String SHORTCODE = "62946";
	public static final String WINTEST_SHORTCODE = "62946";
	public static final String NetID_PROPERTIES_FILE = "networkID.properties";
	public static final String TP_XML = "TP_XML";
	public static final String USER_PARAM="USER";
	public static final String PASS_PARAM="Password";
	public static final String GENERIC_RESPONSE_V1_DTD_DEF="response_generic_v1.dtd";
	public static final String WIN_MESSAGE_DTD_DEF="winbound_messages_v1.dtd";
	public static final String STATUS_CODE = "statusCode";
	public static final String RtnCode_PROPERTIES_FILE = "returnCodes.properties";
}
