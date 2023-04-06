package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Vector;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicketManager;

/**
 * @author trenaman
 */
public class TroubleTicketPlugin implements Plugin
{
    private TroubleTicketManager troubleTicketManager;  
    
    public static final String NAME = "troubleticket"; 

    public static final String DATE_FORMAT = "osiris.troubleticketmanager.dateFormat"; 
    public static final String DATE_FORMAT_DEF = "dd/MM/yyyy HH:mm:ss"; 
    
    /**
     * 
     */
    public TroubleTicketPlugin()
    {
        super();
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
        Vector ret = new Vector();
        
        ret.add(new VariableInfo(DATE_FORMAT, DATE_FORMAT_DEF)); 
        
        return ret; 

    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        ret.add(CICCorbaGatewayPlugin.NAME);
        return ret;
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.SMSBrokerPlugin#preInit(com.o2.osiris.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
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

    public TroubleTicketManager getTroubleTicketManager()
    {
        return troubleTicketManager;
    }
    public void setTroubleTicketManager(TroubleTicketManager troubleTicketManager)
    {
        this.troubleTicketManager = troubleTicketManager;
    }
    
    public String getName()
    {
        return NAME;
    }
}
