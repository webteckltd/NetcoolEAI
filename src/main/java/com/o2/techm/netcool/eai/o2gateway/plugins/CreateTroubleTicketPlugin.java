package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.text.SimpleDateFormat;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.netcool.CreateTroubleTicket;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;

/**
 * @author trenaman
 */
public class CreateTroubleTicketPlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(CreateTroubleTicketPlugin.class);
    public static final String NAME = "createtroubleticket"; 

    public CreateTroubleTicketPlugin()
    {
        super();
    }

    public Vector declareVariables() throws PluginException
    {
        return null;
    }

    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        ret.add(CICCorbaGatewayPlugin.NAME);
        return ret;
    }
    
    public void preInit(PluginManager mgr) throws PluginException
    {
    }

    public void postInit(PluginManager mgr) throws PluginException
    {
        CICCorbaGatewayPlugin corbaGatewayPlugin = (CICCorbaGatewayPlugin) mgr.getPlugins().get(
                CICCorbaGatewayPlugin.NAME);

        TroubleTicketPlugin troubleTicketPlugin = (TroubleTicketPlugin) mgr.getPlugins().get(
                TroubleTicketPlugin.NAME);

        SimpleDateFormat cicDateFormat = mgr.getSimpleDateFormat(CICCorbaGatewayPlugin.CIC_DATE_FORMAT);
        String dummyStringColumnName = mgr.getString(CICCorbaGatewayPlugin.CIC_DUMMY_STRING_COLUMN_NAME);
        String dummyStringValue = mgr.getString(CICCorbaGatewayPlugin.CIC_DUMMY_STRING_VALUE);
        String dummyIntegerColumnName = mgr.getString(CICCorbaGatewayPlugin.CIC_DUMMY_INTEGER_COLUMN_NAME);
        int dummyIntegerValue = mgr.getInt(CICCorbaGatewayPlugin.CIC_DUMMY_INTEGER_VALUE);

        CreateTroubleTicket createTroubleTicketHandler = new CreateTroubleTicket(corbaGatewayPlugin,
                troubleTicketPlugin, cicDateFormat, dummyStringColumnName, dummyStringValue, dummyIntegerColumnName,
                dummyIntegerValue);

        log.info("Adding handler to Netcool Listener");
        corbaGatewayPlugin.getNetcoolListener().addHandler(createTroubleTicketHandler);
    }

    public void shutdown() throws PluginException
    {
    }

    public String getName()
    {
        return NAME;
    }
}
