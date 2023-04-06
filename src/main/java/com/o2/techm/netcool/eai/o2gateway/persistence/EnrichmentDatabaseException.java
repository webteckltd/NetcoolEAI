/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 12-Jun-2014   1.0              Indra               CD38583 - BMR                                                                                           
*/
package com.o2.techm.netcool.eai.o2gateway.persistence;

public class EnrichmentDatabaseException extends Exception {
	/**
	 * default constructor. Calls the superclass (OSSException) constructor.
	 */
	public EnrichmentDatabaseException() {
		super();
	}

	/**
	 * Calls the superclass (OSSException) constructor and passes the message to
	 * it.
	 */
	public EnrichmentDatabaseException(String message) {
		super(message);
	}

	/**
	 * Calls the superclass (OSSException) constructor and passes the Throwable
	 * object to it.
	 */
	public EnrichmentDatabaseException(Throwable cause) {
		super(cause);
	}

	public EnrichmentDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public EnrichmentDatabaseException(Throwable cause, String message) {
		super(message, cause);
	}

}