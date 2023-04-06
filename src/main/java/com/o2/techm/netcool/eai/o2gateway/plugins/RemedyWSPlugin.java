/* Modification  History :
 * Date          Version  Modified by     Brief Description of Modification
 * 12-Jun-2014   1.0      Indra               CD38583 - BMR                                                                                           
 */

package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.VariableInfo;
import com.o2.techm.netcool.eai.o2gateway.remedy.webservice.RemedyWSTroubleTicketManager;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicketException;

public class RemedyWSPlugin implements Plugin {

	private static final Logger log = LoggerFactory.getLogger(RemedyWSPlugin.class);

	public static final String NAME = "RemedyWS";
	public static final String REMEDY_WS_URL = "osiris.remedyws.url";
	public static final String REMEDY_WS_LOGIN = "osiris.remedyws.login";
	public static final String REMEDY_WS_PASSWORD = "osiris.remedyws.password";

	private String login = "";
	private String password = "";
	private String url = "";
	private String cic_date_format ="";

	public RemedyWSPlugin() {
		super();
	}

	public Vector declareVariables() throws PluginException {
		Vector ret = new Vector();
		ret.add(new VariableInfo(REMEDY_WS_URL, ""));
		ret.add(new VariableInfo(REMEDY_WS_LOGIN, ""));
		ret.add(new VariableInfo(REMEDY_WS_PASSWORD, ""));
		return ret;
	}

	public Vector declarePluginDependencies() throws PluginException {
		Vector ret = new Vector();
		ret.add(TroubleTicketPlugin.NAME);
		return ret;
	}

	public void preInit(PluginManager mgr) throws PluginException {
		url = mgr.getString(REMEDY_WS_URL);
		login = mgr.getString(REMEDY_WS_LOGIN);
		password = mgr.getString(REMEDY_WS_PASSWORD);
		cic_date_format = mgr.getString(CICCorbaGatewayPlugin.CIC_DATE_FORMAT);
		
		try {
			RemedyWSTroubleTicketManager remedyTroubleTicketManager = new RemedyWSTroubleTicketManager(
					login, password, url, cic_date_format);

			((TroubleTicketPlugin) mgr.getPlugins().get(
					TroubleTicketPlugin.NAME))
					.setTroubleTicketManager(remedyTroubleTicketManager);
log.debug("RemedyWSTroubleTicketManager created and configured sucessfully url = " + url +" login = " + login+ " password = " + password+"  cic_date_format = " + cic_date_format);
		} catch (TroubleTicketException e) {
			throw new PluginException(
					"Error initialising the RemedyWS plugin; details: "
							+ e.getMessage());
		}
	}

	public void postInit(PluginManager mgr) throws PluginException {
		// TODO Auto-generated method stub

	}

	public void shutdown() throws PluginException {
		// TODO Auto-generated method stub

	}

	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}

}
