package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.net.URL;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.netcool.O2GatewayWriterImpl;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;


/**
 * @author Adetola Ademiju
 * Writes events into Clients from CIC
 */
public class WriterPlugin extends NetcoolConnector implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(WriterPlugin.class);
    public static final String NAME = "WRITER"; 
    public static final String NETCOOL_WRITER_IOR= "o2gateway.netcool.writer.ior";
    public static final String CIC_FILTER="o2gateway.netcool.filter";
    public static final String CIC_POSTFILTER="o2gateway.netcool.post.filter";

    public static final String CIC_DUMMY_STRING_COLUMN_NAME = "o2gateway.cic.dummyStringColumnName"; 
    public static final String CIC_DUMMY_STRING_COLUMN_NAME_DEF = "DummyVarchar"; 
    
    public static final String CIC_DUMMY_STRING_VALUE = "o2gateway.cic.dummyStringValue"; 
    public static final String CIC_DUMMY_STRING_VALUE_DEF = "Osiris"; 
    
    public static final String CIC_DUMMY_INTEGER_COLUMN_NAME = "o2gateway.cic.dummyIntegerColumnName"; 
    public static final String CIC_DUMMY_INTEGER_COLUMN_NAME_DEF = "DummyInteger";
    
    public static final String CIC_DUMMY_INTEGER_VALUE = "o2gateway.cic.dummyIntegerValue";     
    public static final String CIC_DUMMY_INTEGER_VALUE_DEF = "0";  
    
    public static final String NETCOOL_WRITER_URL = "writer.url"; 
    public static final String NETCOOL_WRITER_URL_DEFAULT = "http://localhost:7008"; 
    private String netcoolWriterUrl;    
    private String dummyStringColumnName;
    private String dummyStringValue; 
    private String dummyIntegerColumnName;
    private int dummyIntegerValue;
    private O2GatewayWriterImpl WriterImpl;
    public URL netcoolGatewayWsdlUrl = null;
    
    /* Class constructor
     * 
     */
    public WriterPlugin()
    {
        super();
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
    	Vector ret = new Vector();
        
        ret.add(new VariableInfo(NETCOOL_WRITER_URL, NETCOOL_WRITER_URL_DEFAULT));   
    	ret.add(new VariableInfo(CIC_DUMMY_STRING_COLUMN_NAME, CIC_DUMMY_STRING_COLUMN_NAME_DEF)); 
        ret.add(new VariableInfo(CIC_DUMMY_STRING_VALUE, CIC_DUMMY_STRING_VALUE_DEF)); 
        ret.add(new VariableInfo(CIC_DUMMY_INTEGER_COLUMN_NAME, CIC_DUMMY_INTEGER_COLUMN_NAME_DEF)); 
        ret.add(new VariableInfo(CIC_DUMMY_INTEGER_VALUE, CIC_DUMMY_INTEGER_VALUE_DEF));  
        ret.add(new VariableInfo(CIC_FILTER, ""));
        ret.add(new VariableInfo(CIC_POSTFILTER, ""));
        ret.add(new VariableInfo(GATEWAY_POLL, SYS_POLL));
        //ret.add(new VariableInfo(NetcoolConnector.CIC_DATE_FORMAT,NetcoolConnector.CIC_DATE_FORMAT_DEF));
        
        //ret.add(new VariableInfo(THREAD_POOL_SIZE, THREAD_POOL_SIZE_DEF));
        return ret;
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        ret.add(SybTableLoadPlugin.NAME);
        ret.add(SocketProbePlugin.NAME);
        ret.add(CICGatewayPlugin.NAME);
        return ret;
    }
    
    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#preInit()
     */
    public void preInit(PluginManager mgr) throws PluginException
    {

        netcoolWriterUrl = mgr.getString(NETCOOL_WRITER_URL);       
        dummyStringColumnName = mgr.getString(CIC_DUMMY_STRING_COLUMN_NAME);
        dummyStringValue = mgr.getString(CIC_DUMMY_STRING_VALUE);
        dummyIntegerColumnName = mgr.getString(CIC_DUMMY_INTEGER_COLUMN_NAME);
        dummyIntegerValue = mgr.getInt(CIC_DUMMY_INTEGER_VALUE);
    	
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#postInit()
     */
    public void postInit(PluginManager mgr) throws PluginException
   	{
        	
       	try
   		{
       		WriterImpl=new O2GatewayWriterImpl(mgr);
       		
   		}
       	
       	catch (Exception e)
   		{
       		//e.printStackTrace();
       		throw new PluginException("Error initialising the plugin; details: ",e);
   		}   
   	}

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#shutdown()
     */
    public void shutdown() throws PluginException
    {
    	if(WriterImpl != null)
    		WriterImpl.shutdown();
    }

    public String getName()
    {
        return NAME;
    }

	public O2GatewayWriterImpl getWriterImpl() {
		return WriterImpl;
	}

	public void setWriterImpl(O2GatewayWriterImpl writerImpl) {
		WriterImpl = writerImpl;
	}
    
}
