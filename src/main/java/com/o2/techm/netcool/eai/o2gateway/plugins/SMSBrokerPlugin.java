package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.broker.BrokerConnection;
import com.o2.techm.netcool.eai.manonsite.broker.wintel.WintelBrokerConnection;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;

/**
 * @author trenaman
 */
public class SMSBrokerPlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(SMSBrokerPlugin.class);
    
    public static final String NAME = "smsbroker"; 
    
    public static final String BROKER_PASSWORD = "osiris.broker.password";
    public static final String BROKER_USER = "osiris.broker.user";
    public static final String BROKER_CERTIFICATE = "osiris.broker.certificate";
    public static final String BROKER_HOST = "osiris.broker.host" ;
    public static final String BROKER_PORT = "osiris.broker.port";
    public static final String BROKER_SCRIPT = "osiris.broker.script";
    public static final String BROKER_MT_CODE = "osiris.broker.mtcode";
	public static final String BROKER_PROPERTY = "broker.connection"; 
    
    
    
    private BrokerConnection smsBroker; 
    
    /**
     * 
     */
    public SMSBrokerPlugin()
    {
        super();
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
        Vector ret = new Vector();
        
        ret.add(new VariableInfo(BROKER_PASSWORD, ""));
        ret.add(new VariableInfo(BROKER_USER, ""));
        ret.add(new VariableInfo(BROKER_CERTIFICATE, ""));
        ret.add(new VariableInfo(BROKER_HOST, ""));
        ret.add(new VariableInfo(BROKER_PORT, ""));
        ret.add(new VariableInfo(BROKER_SCRIPT, ""));
        ret.add(new VariableInfo(BROKER_MT_CODE, ""));   
        
        return ret; 
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        return ret;
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#preInit(com.o2.osiris.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
    	String password = mgr.getString(BROKER_PASSWORD);
    	String host = mgr.getString(BROKER_HOST);
    	String username=mgr.getString(BROKER_USER);
    	smsBroker = new WintelBrokerConnection(password,username,host);
    	log.debug("WintelBrokerConnection Obtained host =  " + host + " username = " + username +"  password = "+ password );
    }    
    

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#postInit(com.o2.osiris.PluginManager)
     */
    public void postInit(PluginManager mgr) throws PluginException
    {
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#shutdown()
     */
    public void shutdown() throws PluginException
    {
        // TODO Auto-generated method stub

    }

    public BrokerConnection getSmsBroker()
    {
        return smsBroker;
    }
    
    public String getName()
    {
        return NAME;
    }
}
