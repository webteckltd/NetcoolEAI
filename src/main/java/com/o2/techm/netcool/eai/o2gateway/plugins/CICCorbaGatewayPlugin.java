package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.netcool.NetcoolGatewayClientImpl;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayReader;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayWriter;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;

/**
 * @author trenaman
 */
public class CICCorbaGatewayPlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(CICCorbaGatewayPlugin.class);

    public static final String NAME = "netcool"; 
       
    public static final String NETCOOL_WRITER_URL = "writer.url"; 
    public static final String NETCOOL_WRITER_URL_DEFAULT = "http://localhost:7008"; 
       
    public static final String NETCOOL_READER_URL = "reader.url"; 
    public static final String NETCOOL_READER_URL_DEFAULT = "http://localhost:7006"; 
        
    public static final String CIC_DUMMY_STRING_COLUMN_NAME = "osiris.cic.dummyStringColumnName"; 
    public static final String CIC_DUMMY_STRING_COLUMN_NAME_DEF = "DummyVarchar"; 
    
    public static final String CIC_DUMMY_STRING_VALUE = "osiris.cic.dummyStringValue"; 
    public static final String CIC_DUMMY_STRING_VALUE_DEF = "Osiris"; 
    
    public static final String CIC_DUMMY_INTEGER_COLUMN_NAME = "osiris.cic.dummyIntegerColumnName"; 
    public static final String CIC_DUMMY_INTEGER_COLUMN_NAME_DEF = "DummyInteger";
    
    public static final String CIC_DUMMY_INTEGER_VALUE = "osiris.cic.dummyIntegerValue";     
    public static final String CIC_DUMMY_INTEGER_VALUE_DEF = "0";
    
    public static final String CIC_DATE_FORMAT = "osiris.cic.dateFormat"; 
    public static final String CIC_DATE_FORMAT_DEF = "dd/MM/yyyy HH:mm:ss"; 
    
    private String netcoolReaderUrl;
    private String netcoolWriterUrl;

    private NetcoolGatewayReader netcoolGatewayReader;
    private NetcoolGatewayWriter netcoolGatewayWriter;
    private NetcoolGatewayClientImpl netcoolListener;

    public static final String WSDL_FILE = "wsdl/NetcoolGateway.wsdl";
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
    public CICCorbaGatewayPlugin()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
        Vector ret = new Vector();
        
        ret.add(new VariableInfo(NETCOOL_WRITER_URL, NETCOOL_WRITER_URL_DEFAULT)); 
        ret.add(new VariableInfo(NETCOOL_READER_URL, NETCOOL_READER_URL_DEFAULT)); 
        ret.add(new VariableInfo(CIC_DUMMY_STRING_COLUMN_NAME, "DummyVarchar")); 
        ret.add(new VariableInfo(CIC_DUMMY_STRING_VALUE, "Osiris")); 
        ret.add(new VariableInfo(CIC_DUMMY_INTEGER_COLUMN_NAME, "DummyInteger")); 
        ret.add(new VariableInfo(CIC_DUMMY_INTEGER_VALUE, "0")); 
        ret.add(new VariableInfo(CIC_DATE_FORMAT, "dd/MM/yyyy HH:mm:ss")); 
        
        return ret; 
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        ret.add(TroubleTicketPlugin.NAME);
        return ret;
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#initialise(com.o2.osiris.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
    	netcoolWriterUrl = mgr.getString(NETCOOL_WRITER_URL); 
        netcoolReaderUrl = mgr.getString(NETCOOL_READER_URL);
        mgr.getString(CIC_DUMMY_STRING_COLUMN_NAME);
        mgr.getString(CIC_DUMMY_STRING_VALUE);
        mgr.getString(CIC_DUMMY_INTEGER_COLUMN_NAME);
        mgr.getInt(CIC_DUMMY_INTEGER_VALUE);
        mgr.getSimpleDateFormat(CIC_DATE_FORMAT);
        
        
        netcoolListener = new NetcoolGatewayClientImpl();
        
    }
    
    public void postInit(PluginManager mgr) throws PluginException
    {

		try
        {
			ReaderPlugin readerPlugin = (ReaderPlugin) mgr.getPlugins().get(ReaderPlugin.NAME);
			netcoolGatewayReader =  readerPlugin.getReaderImpl();
			
			
			WriterPlugin writerPlugin = (WriterPlugin) mgr.getPlugins().get(WriterPlugin.NAME);
			netcoolGatewayWriter =  writerPlugin.getWriterImpl();

            
            log.debug("Registering callback with the Netcool Gateway Writer");

			boolean ret = netcoolGatewayWriter.netcool_connect(netcoolListener); 
            
        }         
        catch (Exception e)
        {
            throw new PluginException(e);
        }        
    }
    

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#shutdown()
     */
    public void shutdown() throws PluginException
	{
    	
    }

    
    public NetcoolGatewayReader getNetcoolGatewayReader()
    {
        return netcoolGatewayReader;
    }
    
    public NetcoolGatewayClientImpl getNetcoolListener()
    {
        return netcoolListener;
    }
    
    public String getName()
    {
        return NAME;
    }
        
	 
}
