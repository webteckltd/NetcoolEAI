package com.o2.techm.netcool.eai.o2gateway.plugins;
 
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManager;
 
/**
 * @author aademij1
 */
public class CICGatewayPlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(CICGatewayPlugin.class);

    public static final String NAME = "GatewayServant"; 
       
    private SimpleDateFormat cicDateFormat; 
    private SimpleDateFormat serviceViewDateFormat; 
    private SybaseManager sybaseManager;

    public static final String NAMESPACE = "http://ps.iona.com/artix/o2/NetcoolGateway.wsdl"; 

    public static final String WRITER_PORT_NAME = "NetcoolGateway.WriterPort";
    public static final String WRITER_SERVICE_NAME = "NetcoolGateway.WriterService";

    public static final String READER_PORT_NAME = "NetcoolGateway.ReaderServicePort";
    public static final String READER_SERVICE_NAME = "NetcoolGateway.ReaderService";

    public static final String CLIENT_PORT_NAME = "NetcoolGateway.GatewayClientPort";
    public static final String CLIENT_SERVICE_NAME = "NetcoolGateway.GatewayClientService";
    
    /**
     * 
     */
    public CICGatewayPlugin()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
        Vector ret = new Vector();     
        ret.add(new VariableInfo(NetcoolConnector.SYBASE_JDBC_URL, "")); 
        ret.add(new VariableInfo(NetcoolConnector.SYBASE_JDBC_URL_BKUP, ""));   
        ret.add(new VariableInfo(NetcoolConnector.SYBASE_JDBC_LOGIN, "")); 
        ret.add(new VariableInfo(NetcoolConnector.SYBASE_JDBC_PASSWORD, "")); 
        
        
        ret.add(new VariableInfo(NetcoolConnector.GATEWAY_HEARTBEAT, "")); 
        ret.add(new VariableInfo(NetcoolConnector.GATEWAY_POLL, ""));   
        ret.add(new VariableInfo(NetcoolConnector.SYS_POLL, "")); 
        ret.add(new VariableInfo(NetcoolConnector.GATEWAY_EVENT_BUF_SZ, "")); 
        
       return ret; 
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        return ret;
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#preInit(com.o2.o2gateway.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
        //cicDateFormat = mgr.getSimpleDateFormat(NetcoolConnector.CIC_DATE_FORMAT);  
    }
    
    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#initialise(com.o2.o2gateway.PluginManager)
     */
    public void postInit(PluginManager mgr) throws PluginException
    {
   
       
    }
    

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#shutdown()
     */
    public void shutdown() throws PluginException
    {
        // TODO Auto-generated method stub

    }
   
    public String getName()
    {
        return NAME;
    }
        
}
