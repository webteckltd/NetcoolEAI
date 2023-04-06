/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 12-Jun-2014            Indra               CD38583 - BMR                                                                                           
*/
package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;
import com.o2.techm.netcool.eai.o2gateway.recalc.RecalcTask;

/**
 * @author trenaman
 */
public class RecalcPlugin implements Plugin {
	private static final Logger log = LoggerFactory.getLogger(RecalcPlugin.class);

	public static final String NAME = "recalc";
	public static final String RECALC_SLEEP = "osiris.recalc.sleep";

	private int recalcSleep;
	private RecalcTask recalcTask;
	private Timer recalcTimer;

	/**
     * 
     */
	public RecalcPlugin() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.o2.osiris.SMSBrokerPlugin#declareVariables()
	 */
	public Vector declareVariables() throws PluginException {
		Vector ret = new Vector();
		ret.add(new VariableInfo(RECALC_SLEEP, "60"));
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.o2.osiris.SMSBrokerPlugin#declarePluginDependencies()
	 */
	public Vector declarePluginDependencies() throws PluginException {
		Vector ret = new Vector();
		ret.add(SocketProbePlugin.NAME);
		ret.add(EnrichmentDatabasePlugin.NAME);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.o2.osiris.SMSBrokerPlugin#initialise(com.o2.osiris.PluginManager)
	 */
	public void preInit(PluginManager mgr) throws PluginException {
		recalcSleep = mgr.getInt(RECALC_SLEEP);
	}

	public void postInit(PluginManager mgr) throws PluginException {
		recalcTask = new RecalcTask(
				(EnrichmentDatabasePlugin) mgr.getPlugins().get(EnrichmentDatabasePlugin.NAME), 
				(SocketProbePlugin) mgr.getPlugins().get(SocketProbePlugin.NAME));
		recalcTimer = new Timer();

		recalcTimer.schedule(recalcTask, 0, recalcSleep * 1000);
		log.debug("RecalcTask Created and scheduled sucessfully ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.o2.osiris.SMSBrokerPlugin#shutdown()
	 */
	public void shutdown() throws PluginException {
		if (recalcTask != null) {
			recalcTask.shutdown();
			recalcTimer.cancel();
		}
	}

	public String getName() {
		return NAME;
	}
}
