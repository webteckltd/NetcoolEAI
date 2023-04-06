/* Modification  History :
 * Date          Version  Modified by     Brief Description of Modification
 * 12-Jun-2014            Indra               CD38583 - BMR                                                                                           
 */
package com.o2.techm.netcool.eai.manonsite.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	private static final String BUNDLE_NAME = "manonsite";

	public static final String DUMMY_STRING_COLUMN_NAME = "manonsite.cic.dummyStringColumnName";
	public static final String DUMMY_STRING_VALUE = "manonsite.cic.dummyStringValue";
	public static final String DUMMY_INTEGER_COLUMN_NAME = "manonsite.cic.dummyIntegerColumnName";
	public static final String DUMMY_INTEGER_VALUE = "manonsite.cic.dummyIntegerValue";

	public static final String SOCKET_PROBE_PORT = "manonsite.cic.socketprobe.port";
	public static final String SOCKET_PROBE_HOST = "manonsite.cic.socketprobe.host";

	public static final String REMEDY_USERNAME = "manonsite.remedy.login";
	public static final String REMEDY_PASSWORD = "manonsite.remedy.password";
	public static final String REMEDY_SERVER = "manonsite.remedy.server";
	public static final String REMEDY_LANGUAGE = "manonsite.remedy.language";

	public static final String CLARIFY_LOGIN = "manonsite.clarify.login";
	public static final String CLARIFY_PASSWORD = "manonsite.clarify.password";
	public static final String CLARIFY_URL = "manonsite.clarify.url";
	public static final String CLARIFY_PLANNED_OUTAGES_SLEEP = "manonsite.clarify.plannedoutages.sleep";
	public static final String CLARIFY_PLANNED_OUTAGES_LEAD_TIME = "manonsite.clarify.plannedoutages.leadtime";
	public static final String CLARIFY_PLANNED_OUTAGES_LAG_TIME = "manonsite.clarify.plannedoutages.lagtime";
	public static final String CLARIFY_DATE_FORMAT = "manonsite.clarify.dateFormat";

	public static final String MAPINFO_JDBC_URL = "manonsite.mapinfo.jdbc.url";
	public static final String MAPINFO_JDBC_LOGIN = "manonsite.mapinfo.jdbc.login";
	public static final String MAPINFO_JDBC_PASSWORD = "manonsite.mapinfo.jdbc.password";
	public static final String MAPINFO_JDBC_DRIVER = "manonsite.mapinfo.jdbc.driver";
	public static final String MAPINFO_DATE_FORMAT = "manonsite.mapinfo.dateFormat";

	public static final String BROKER_PASSWORD = "manonsite.broker.password";
	public static final String BROKER_CERTIFICATE = "manonsite.broker.certificate";
	public static final String BROKER_HOST = "manonsite.broker.host";
	public static final String BROKER_PORT = "manonsite.broker.port";
	public static final String BROKER_SCRIPT = "manonsite.broker.script";
	public static final String BROKER_MT_CODE = "manonsite.broker.mtcode";

	public static final String MOS_SERVLET_URL = "mos.sender.url";
	public static final String WINTEL_BROKER_PASSWORD = "mos.win.password";
	public static final String WINTEL_BROKER_USER = "mos.win.user";
	public static final String WINTEL_BROKER_URL = "mos.wintel.gateway.url";
	public static final String MRS_WSDL_PORT = "manonsite.mrs.port";
	public static final String MRS_HOSTS = "manonsite.mrs.hosts";
	public static final String O2BROKER = "O2XML";
	public static final String WINTELBROKER = "WINTEL";
	public static final String MOS_PROXY_PASSWORD = "manonsite.proxy.password";
	public static final String MOS_PROXY_USER = "manonsite.proxy.user";
	public static final String MOS_PROXY_PORT = "manonsite.proxy.port";
	public static final String MOS_PROXY_HOST = "manonsite.proxy.host";
	public static final String MOS_PROXY_HOST_BACKUP = "manonsite.proxy.host.backup";
	public static final int MOS_PROXY_PORT_DEF = 80;
	public static final String MOS_PROXY_PASSWORD_DEF = "default";
	public static final String MOS_PROXY_USER_DEF = "default";
	public static final String MOS_REMOTESERVICE_URL = "mos.remoteservice.url";
	public static final String GENERIC_RESPONSE_V1_DTD = "response.dtd";

	public static final String INMS_URL = "manonsite.inms.url";
	public static final String INMS_CERTIFICATE = "manonsite.inms.certificate";
	public static final String INMS_PASSWORD = "manonsite.inms.password";

	public static final String THREAD_POOL_SIZE = "manonsite.threadPoolSize";

	public static final String SERVER_ADMIN_URL = "manonsite.serveradmin.url";

	public static final String VANTIVE_API_LOGIN = "manonsite.vantive.api.login";
	public static final String VANTIVE_API_PASSWORD = "manonsite.vantive.api.password";
	public static final String VANTIVE_API_PORT = "manonsite.vantive.api.port";
	public static final String VANTIVE_API_HOST = "manonsite.vantive.api.host";
	// public static final String VANTIVE_JDBC_URL =
	// "manonsite.vantive.jdbc.url";
	// public static final String VANTIVE_JDBC_LOGIN =
	// "manonsite.vantive.jdbc.login";
	// public static final String VANTIVE_JDBC_PASSWORD =
	// "manonsite.vantive.jdbc.password";
	// public static final String VANTIVE_JDBC_DRIVER =
	// "manonsite.vantive.jdbc.driver";
	// public static final String VANTIVE_DATE_FORMAT =
	// "manonsite.vantive.dateFormat";
	public static final String ENRICHMENTDB_JDBC_URL = "manonsite.enrichmentdb.jdbc.url";
	public static final String ENRICHMENTDB_JDBC_LOGIN = "manonsite.enrichmentdb.jdbc.login";
	public static final String ENRICHMENTDB_JDBC_PASSWORD = "manonsite.enrichmentdb.jdbc.password";
	public static final String ENRICHMENTDB_JDBC_DRIVER = "manonsite.enrichmentdb.jdbc.driver";
	public static final String ENRICHMENTDB_DATE_FORMAT = "manonsite.enrichmentdb.dateFormat";
	public static final String ENRICHMENTDB_LOCALE = "manonsite.enrichmentdb.locale";
	public static final String TROUBLE_TICKET_MANAGER_CLASS_NAME = "manonsite.troubleTicketManager.className";
	public static final String CIC_DATE_FORMAT = "manonsite.cic.dateFormat";
	public static final String SYBASE_JDBC_URL = "manonsite.sybase.jdbc.url";
	public static final String SYBASE_JDBC_LOGIN = "manonsite.sybase.jdbc.login";
	public static final String SYBASE_JDBC_PASSWORD = "manonsite.sybase.jdbc.password";
	public static final String MANONSITE_MAX_RETRY = "manonsite.max.retry";

	private static final Logger log = LoggerFactory.getLogger(Configuration.class);

	static Hashtable variables = new Hashtable();;

	static {
		initialiseDefaults();
		readProperties();
	}

	private static void initialiseDefaults() {
		variables.put(TROUBLE_TICKET_MANAGER_CLASS_NAME, "");
		variables.put(DUMMY_STRING_COLUMN_NAME, "");
		variables.put(DUMMY_STRING_VALUE, "");
		variables.put(DUMMY_INTEGER_COLUMN_NAME, "");
		variables.put(DUMMY_INTEGER_VALUE, "");
		variables.put(CIC_DATE_FORMAT, "");
		variables.put(SERVER_ADMIN_URL, "http://localhost:7012");
		variables.put(SOCKET_PROBE_PORT, "8001");
		variables.put(SOCKET_PROBE_HOST, "localhost");
		variables.put(VANTIVE_API_LOGIN, "");
		variables.put(VANTIVE_API_PASSWORD, "");
		variables.put(VANTIVE_API_PORT, "");
		variables.put(VANTIVE_API_HOST, "");
		variables.put(ENRICHMENTDB_JDBC_URL, "");
		variables.put(ENRICHMENTDB_JDBC_LOGIN, "");
		variables.put(ENRICHMENTDB_JDBC_PASSWORD, "");
		variables.put(ENRICHMENTDB_JDBC_DRIVER, "oracle.jdbc.driver.OracleDriver");
		variables.put(ENRICHMENTDB_DATE_FORMAT, "");
		variables.put(ENRICHMENTDB_LOCALE, "2057");
		variables.put(REMEDY_USERNAME, "");
		variables.put(REMEDY_PASSWORD, "");
		variables.put(REMEDY_SERVER, "");
		variables.put(REMEDY_LANGUAGE, "");
		variables.put(CLARIFY_LOGIN, "");
		variables.put(CLARIFY_PASSWORD, "");
		variables.put(CLARIFY_URL, "");
		variables.put(CLARIFY_PLANNED_OUTAGES_LEAD_TIME, "900");
		variables.put(CLARIFY_PLANNED_OUTAGES_LAG_TIME, "900");
		variables.put(CLARIFY_PLANNED_OUTAGES_SLEEP, "900");
		variables.put(CLARIFY_DATE_FORMAT, "dd/MM/yyyy HH:mm:ss");
		variables.put(MAPINFO_JDBC_URL, "");
		variables.put(MAPINFO_JDBC_LOGIN, "");
		variables.put(MAPINFO_JDBC_PASSWORD, "");
		variables.put(MAPINFO_JDBC_DRIVER, "oracle.jdbc.driver.OracleDriver");
		variables.put(MAPINFO_DATE_FORMAT, "dd/MM/yyyy HH:mm:ss");
		variables.put(BROKER_PASSWORD, "");
		variables.put(BROKER_CERTIFICATE, "");
		variables.put(BROKER_HOST, "");
		variables.put(BROKER_PORT, "");
		variables.put(BROKER_SCRIPT, "");
		variables.put(BROKER_MT_CODE, "");
		variables.put(WINTEL_BROKER_PASSWORD, "");
		variables.put(WINTEL_BROKER_URL, "");
		variables.put(MOS_SERVLET_URL, "");
		variables.put(WINTEL_BROKER_USER, "");
		variables.put(MRS_WSDL_PORT, "7070");
		variables.put(INMS_URL, "");
		variables.put(INMS_CERTIFICATE, "");
		variables.put(INMS_PASSWORD, "");
		variables.put(THREAD_POOL_SIZE, "3");// OSC 459 before
												// variables.put(THREAD_POOL_SIZE,
												// "10")
		variables.put(SERVER_ADMIN_URL, "");
		variables.put(SYBASE_JDBC_URL, "");
		variables.put(SYBASE_JDBC_LOGIN, "");
		variables.put(SYBASE_JDBC_PASSWORD, "");
		variables.put(MOS_PROXY_PASSWORD, "");
		variables.put(MOS_PROXY_HOST, "");
		variables.put(MOS_PROXY_HOST_BACKUP, "");
		variables.put(MOS_PROXY_PORT, "");
		variables.put(MOS_PROXY_PASSWORD, "");
		variables.put(MANONSITE_MAX_RETRY, "");
	}

	private static void readProperties() {
		try {
          ResourceBundle resourceBundle = null; //ResourceBundle.getBundle(BUNDLE_NAME);
			
			try {
				resourceBundle = new PropertyResourceBundle(Files.newInputStream(Paths.get("conf/"+BUNDLE_NAME+".properties")));
			} catch (IOException e) {
				log.error("Not able to load configuration file conf/"+BUNDLE_NAME+".properties Failing early, failing loud.", e);
				e.printStackTrace();
				System.exit(1);
			}

			// Check that the peroperties file only contains variables we know
			// about.
			//
			Enumeration resourceKeys = resourceBundle.getKeys();
			while (resourceKeys.hasMoreElements()) {
				String key = (String) resourceKeys.nextElement();
				if (!variables.containsKey(key)) {
					log.error("Properties file '" + BUNDLE_NAME
							+ "' contains an unknown variable '" + key
							+ "'. Failing early, failing loud.");
					System.exit(1);
				}

			}

			// Now use the peroperties file to override the defaults.
			//
			Enumeration variableKeys = variables.keys();
			while (variableKeys.hasMoreElements()) {
				String variable = (String) variableKeys.nextElement();
				String value = null;
				try {
					value = resourceBundle.getString(variable);
					variables.put(variable, value);
					log.info(variable + " = '" + value
							+ "'  (Set from properties file '" + BUNDLE_NAME
							+ "')");
				} catch (java.util.MissingResourceException ex) {
					value = (String) variables.get(variable);
					log.info(variable + " = '" + value + "'  (Default)");
				}
			}
		} catch (RuntimeException e) {
			log.warn(e.getMessage());
			log.warn("Cannot find resource bundle '" + BUNDLE_NAME
					+ "'; using default configuration values.");

			Enumeration aenum = variables.keys();
			while (aenum.hasMoreElements()) {
				String variable = (String) aenum.nextElement();
				String value = (String) variables.get(variable);
				log.info(variable + " = '" + value + "'  (Default)");
			}
		}
	}

	public static String getValue(String variable) {
		String ret = (String) variables.get(variable);
		if (ret == null) {
			log.error("Request for non existant configuration variable '"
					+ variable + "'. Failing early, failing loud.");
			System.exit(1);
		}
		return ret;
	}
}
