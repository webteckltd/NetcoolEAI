package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Vector;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicketException;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicketManager;

/**
 * @author trenaman
 */
public class PlannedOutagesPlugin implements Plugin
{
    public static final String NAME = "plannedoutages"; 
    
    public PlannedOutagesPlugin()
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
        ret.add(TroubleTicketPlugin.NAME); 
        return ret;
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#preInit(com.o2.osiris.pluginframework.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#postInit(com.o2.osiris.pluginframework.PluginManager)
     */
    public void postInit(PluginManager mgr) throws PluginException
    {
        TroubleTicketManager troubleTicketManager = 
            ((TroubleTicketPlugin) mgr.getPlugins().get(TroubleTicketPlugin.NAME)).getTroubleTicketManager();
        
        try
        {
            troubleTicketManager.initPlannedOutages();
        }
        catch (TroubleTicketException e)
        {
            throw new PluginException("Unable to initialise planned outages; details: " + e.getMessage());
        }
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
