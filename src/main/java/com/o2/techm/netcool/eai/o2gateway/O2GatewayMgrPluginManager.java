
package com.o2.techm.netcool.eai.o2gateway; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
 
/**
 * 
 * @author aademij1
 *
 * Class that loads and registers all the required plugins
 */
public class O2GatewayMgrPluginManager extends PluginManager
{
	private static final Logger log = LoggerFactory.getLogger(O2GatewayMgrPluginManager.class);

	/**
	 * @see com.o2.o2gateway.pluginframework#PluginManager(String args[], String resourceBundleName)
	 * @param args 
	 * @param resourceBundleName
	 * @throws PluginException
	 */
    public O2GatewayMgrPluginManager(String args[], String resourceBundleName) throws PluginException
    {
        super(args, resourceBundleName);        
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework#registerPlugins()
     */
    public void registerPlugins()
    {
    	log.debug("registering Plugins");
    	registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.CICGatewayPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.CICGatewayPlugin");
    	registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.SybTableLoadPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.SybTableLoadPlugin");
    	registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.ReaderPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.ReaderPlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.WriterPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.WriterPlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.MonitorPlugin.NAME,"com.o2.techm.netcool.eai.o2gateway.plugins.MonitorPlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.CICCorbaGatewayPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.CICCorbaGatewayPlugin");        
        
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.RecalcPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.RecalcPlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.TroubleTicketPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.TroubleTicketPlugin");
       
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.SMSBrokerPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.SMSBrokerPlugin");
       
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.PlannedOutagesPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.PlannedOutagesPlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.CreateTroubleTicketPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.CreateTroubleTicketPlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.UpdateTroubleTicketPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.UpdateTroubleTicketPlugin");
       
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.ArtixPingPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.ArtixPingPlugin");      
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.SendSMSPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.SendSMSPlugin");        
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.RemedyWSPlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.RemedyWSPlugin");
        registerPlugin(com.o2.techm.netcool.eai.o2gateway.plugins.EnrichmentDatabasePlugin.NAME, "com.o2.techm.netcool.eai.o2gateway.plugins.EnrichmentDatabasePlugin");
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework#declareCoreVariables()
     */
    public void declareCoreVariables() throws PluginException
    {
        // This is where the o2gateway core can declare it's own configuration variables. For example,
        // if programmatic logging is enabled, then the following variables must be declared.
        //
//        declareVariable(Log4jHelper.LOGGING_LEVEL, Log4jHelper.DEFAULT_LOGGING_LEVEL);
//        declareVariable(Log4jHelper.LOGGING_FILE_PATTERN, Log4jHelper.DEFAULT_LOG_PATTERN);
//        declareVariable(Log4jHelper.LOGGING_FILE_FILENAME, Log4jHelper.DEFAULT_LOG_FILE);
//        declareVariable(Log4jHelper.LOGGING_CONSOLE_PATTERN, Log4jHelper.DEFAULT_LOG_PATTERN);
    }

}
