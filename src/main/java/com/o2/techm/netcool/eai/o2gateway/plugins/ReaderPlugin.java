package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.io.File;
import java.net.URL; 
import java.util.Vector;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.O2GatewayMgrPluginManager;
import com.o2.techm.netcool.eai.o2gateway.netcool.O2GatewayReaderImpl;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;



/**
 * Class that Initialises and starts the Reader Servant
 * @author aademij1
 */
public class ReaderPlugin extends NetcoolConnector implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(ReaderPlugin.class);
    
    public static final String NAME = "READER"; 
    public static final String NETCOOL_READER_URL = "reader.url"; 
    public static final String NETCOOL_READER_URL_DEFAULT = "http://localhost:7006"; 
    public static final String NETCOOL_READER_MAP_OPEN="o2gateway.netcool.map.open";
    public static final String NETCOOL_READER_MAP_UPDATE="o2gateway.netcool.map.update";
    public static final String NETCOOL_READER_MAP_CLOSE="o2gateway.netcool.map.close";
    public static final String NETCOOL_READER_MAP_JOURNAL="o2gateway.netcool.map.journal";
    public static final String NETCOOL_READER_FILTER="o2gateway.netcool.filter";
    public static final String NETCOOL_READER_FILTER_POST="o2gateway.netcool.post.filter";
  
    public static final String GATEWAY_EVENT_BUF_SZ = "o2gateway.maxeventbuffersize"; 
        
    public static final String GATEWAY_EVENT_BUF_SZ_DEF = "-1";
    
    private O2GatewayReaderImpl ReaderImpl;
    private String netcoolReaderUrl;
    public URL netcoolGatewayWsdlUrl = null;

    /*
     * Class constructor
     *
     */
    public ReaderPlugin()
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
        
        ret.add(new VariableInfo(NETCOOL_READER_URL, NETCOOL_READER_URL_DEFAULT));         
        ret.add(new VariableInfo(NETCOOL_READER_MAP_OPEN,""));
        ret.add(new VariableInfo(NETCOOL_READER_MAP_UPDATE,""));
        ret.add(new VariableInfo(NETCOOL_READER_MAP_CLOSE,""));
        ret.add(new VariableInfo(NETCOOL_READER_MAP_JOURNAL,""));
        ret.add(new VariableInfo(GATEWAY_EVENT_BUF_SZ,GATEWAY_EVENT_BUF_SZ_DEF));
        ret.add(new VariableInfo(NetcoolConnector.CIC_DATE_FORMAT,NetcoolConnector.CIC_DATE_FORMAT_DEF));
        
        ret.add(new VariableInfo(NETCOOL_READER_FILTER,""));
        ret.add(new VariableInfo(NETCOOL_READER_FILTER_POST,""));        
        return ret;
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        ret.add(CICGatewayPlugin.NAME);
        ret.add(SocketProbePlugin.NAME);
        return null;
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#preInit()
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
    	 netcoolReaderUrl = mgr.getString(NETCOOL_READER_URL);
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#postInit()
     */
    public void postInit(PluginManager mgr) throws PluginException
    {
    	 try
         {
         	ReaderImpl = new O2GatewayReaderImpl(mgr);
         	log.debug("Gateway Reader Created sucessfully ");
         }catch (Exception e)
         {
             throw new PluginException("Error initialising the plugin; details: " + e.getMessage());
         }   
       
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#shutdown()
     */
    public void shutdown() throws PluginException
    {
        // TODO Auto-generated method stub
    	if(ReaderImpl != null)
    		ReaderImpl.shutdown();
    }

    public String getName()
    {
        return NAME;
    }

	public O2GatewayReaderImpl getReaderImpl() {
		return ReaderImpl;
	}

	public void setReaderImpl(O2GatewayReaderImpl readerImpl) {
		ReaderImpl = readerImpl;
	}
    
    
}
