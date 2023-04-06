/* Modification  History :

* Date          Version  Modified by     Brief Description of Modification

* 12-Jun-2014   1.0              Indra               CD38583 - BMR                                                                                           

*/

package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

public class RemedyWSConfiguration {

	public static String getLogin() {
		return login;
	}

	public static void setLogin(String login) {
		RemedyWSConfiguration.login = login;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		RemedyWSConfiguration.password = password;
	}

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		RemedyWSConfiguration.url = url;
	}

	public static String getCic_date_format() {
		return cic_date_format;
	}

	public static void setCic_date_format(String cic_date_format) {
		RemedyWSConfiguration.cic_date_format = cic_date_format;
	}

	private static String login;
	private static String password;
	private static String url;
	private static String cic_date_format;

}
