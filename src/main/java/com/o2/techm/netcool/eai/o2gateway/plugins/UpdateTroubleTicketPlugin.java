package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.text.SimpleDateFormat;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.netcool.UpdateTroubleTicket;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;

/**
 * @author trenaman
 */
public class UpdateTroubleTicketPlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(UpdateTroubleTicketPlugin.class);
    public static final String NAME = "updatetroubleticket"; 
    /**
     * 
     */
    public UpdateTroubleTicketPlugin()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        ret.add(TroubleTicketPlugin.NAME);
        return ret;  
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#preInit(com.o2.osiris.pluginframework.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#postInit(com.o2.osiris.pluginframework.PluginManager)
     */
    public void postInit(PluginManager mgr) throws PluginException
    {
        CICCorbaGatewayPlugin netcoolPlugin = (CICCorbaGatewayPlugin) mgr.getPlugins().get(
                CICCorbaGatewayPlugin.NAME);

        TroubleTicketPlugin troubleTicketPlugin = (TroubleTicketPlugin) mgr.getPlugins().get(
                TroubleTicketPlugin.NAME);

        SimpleDateFormat cicDateFormat = mgr.getSimpleDateFormat(CICCorbaGatewayPlugin.CIC_DATE_FORMAT);
        String dummyStringColumnName = mgr.getString(CICCorbaGatewayPlugin.CIC_DUMMY_STRING_COLUMN_NAME);
        String dummyStringValue = mgr.getString(CICCorbaGatewayPlugin.CIC_DUMMY_STRING_VALUE);
        String dummyIntegerColumnName = mgr.getString(CICCorbaGatewayPlugin.CIC_DUMMY_INTEGER_COLUMN_NAME);
        int dummyIntegerValue = mgr.getInt(CICCorbaGatewayPlugin.CIC_DUMMY_INTEGER_VALUE);

        UpdateTroubleTicket updateTroubleTicketHandler = new UpdateTroubleTicket(netcoolPlugin,
                troubleTicketPlugin, cicDateFormat, dummyStringColumnName, dummyStringValue, dummyIntegerColumnName,
                dummyIntegerValue);

        log.info("Adding handler to Netcool Listener.");
        netcoolPlugin.getNetcoolListener().addHandler(updateTroubleTicketHandler);        
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#shutdown()
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
