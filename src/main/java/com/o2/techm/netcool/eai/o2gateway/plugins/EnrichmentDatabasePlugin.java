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
import com.o2.techm.netcool.eai.o2gateway.persistence.EnrichmentDatabase;
import com.o2.techm.netcool.eai.o2gateway.persistence.EnrichmentDatabaseException;

public class EnrichmentDatabasePlugin implements Plugin {

	private static final Logger log = LoggerFactory.getLogger(EnrichmentDatabasePlugin.class);

	public static final String NAME = "enrichmentdb";

	public static final String ENRICHMENTDATABASE_JDBC_URL = "osiris.enrichmentdb.jdbc.url";
	public static final String ENRICHMENTDATABASE_JDBC_URL_JDBC_LOGIN = "osiris.enrichmentdb.jdbc.login";
	public static final String ENRICHMENTDATABASE_JDBC_URL_JDBC_PASSWORD = "osiris.enrichmentdb.jdbc.password";

	private EnrichmentDatabase persistence;

	private String url;
	private String login;
	private String password;

	public EnrichmentDatabasePlugin() {
		super();
	}

	public void preInit(PluginManager mgr) throws PluginException {
		url = mgr.getString(ENRICHMENTDATABASE_JDBC_URL);
		login = mgr.getString(ENRICHMENTDATABASE_JDBC_URL_JDBC_LOGIN);
		password = mgr.getString(ENRICHMENTDATABASE_JDBC_URL_JDBC_PASSWORD);
		try {
			setPersistence(new EnrichmentDatabase(url, login, password));
			log.debug("preInit for EnrichmentDatabase completed Sucessfully");
		} catch (EnrichmentDatabaseException e) {
			throw new PluginException(
					"Exception loading the Persistence plugin; perhaps you have not set the "
							+ "persistence variables correctly in the properties file? See the documentation for more "
							+ "information. Exception details: "
							+ e.getMessage(), e);
		}
	}

	public void postInit(PluginManager mgr) throws PluginException {
		// TODO Auto-generated method stub

	}

	public Vector declareVariables() throws PluginException {
		Vector ret = new Vector();
		ret.add(new VariableInfo(ENRICHMENTDATABASE_JDBC_URL, ""));
		ret.add(new VariableInfo(ENRICHMENTDATABASE_JDBC_URL_JDBC_LOGIN, ""));
		ret.add(new VariableInfo(ENRICHMENTDATABASE_JDBC_URL_JDBC_PASSWORD, ""));
		return ret;
	}

	public Vector declarePluginDependencies() throws PluginException {
		// TODO Auto-generated method stub
		return null;
	}

	public void shutdown() throws PluginException {
		// TODO Auto-generated method stub

	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}



	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public EnrichmentDatabase getPersistence() {
		return persistence;
	}

	public void setPersistence(EnrichmentDatabase persistence) {
		this.persistence = persistence;
	}

}
