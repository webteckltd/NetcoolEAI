package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.netcool.ArtixPing;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;

/**
 * @author trenaman
 */
public class ArtixPingPlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(ArtixPingPlugin.class);
    public static final String NAME = "artixping"; 


    /**
     * 
     */
    public ArtixPingPlugin()
    {
        super();
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.pluginframework.Plugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector();
        ret.add(SocketProbePlugin.NAME);
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
        CICCorbaGatewayPlugin corbaGatewayPlugin = (CICCorbaGatewayPlugin) mgr.getPlugins().get(
                CICCorbaGatewayPlugin.NAME);
        
        SocketProbePlugin socketProbePlugin = (SocketProbePlugin) mgr.getPlugins().get(SocketProbePlugin.NAME);

        ArtixPing pingArtix = new ArtixPing(socketProbePlugin); 
        
        log.info("Adding handler to Netcool Listener");
        corbaGatewayPlugin.getNetcoolListener().addHandler(pingArtix);
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
