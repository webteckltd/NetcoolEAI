/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 12-Jun-2014   1.0      Indra               CD38583 - BMR                                                                                           
*/
package com.o2.techm.netcool.eai.o2gateway.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.jdbc.oracle.OraPoolConnectionManager;
//import com.o2.techm.netcool.eai.o2gateway.jdbc.PoolConnectionManager;
import com.o2.techm.netcool.eai.o2gateway.serviceview.Recalc;

public class EnrichmentDatabase {

	private static final Logger log = LoggerFactory.getLogger(EnrichmentDatabase.class);

	private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";

	private static String ORACLE_POOL_NAME = "OracleEDBPool";

	private final int m_allServiceGroups = 4; // This represents all the service
												// groups e.g. GSM, GPRS

	public EnrichmentDatabase(String url, String login, String password)
			throws EnrichmentDatabaseException {
		if (!OraPoolConnectionManager.isValid(ORACLE_POOL_NAME)) {
			try {
				log.info("Msg(9) EnrichmentDatabase.EnrichmentDatabase() - Try to create a pool in the PoolConnectionMgr");
				OraPoolConnectionManager.createPool(ORACLE_POOL_NAME, JDBC_DRIVER,
						url, login, password);
			} catch (SQLException sqle) {
				log.error("Msg(10) EnrichmentDatabase.EnrichmentDatabase() - Cannot create a pool in the PoolConnectionMgr");
				throw new EnrichmentDatabaseException(sqle,
						"Error in creating the pool manager");
			}
		}
	}
	

	public Vector getRecalcData() throws EnrichmentDatabaseException {
		// check login name and password
		Connection connection = null;
		String sQuery = "";
		ResultSet o_ResultSet = null;
		Statement o_Statement = null;
		Vector o_Vector = new Vector();

		try {
			log.debug("Msg() EnrichmentDatabase.getRecalcData() - Request for a connection");
			connection = OraPoolConnectionManager
					.requestConnection(ORACLE_POOL_NAME);
			log.debug("Msg() EnrichmentDatabase.getRecalcData() - Connection established");

			if (connection != null) {

				// query
				sQuery = "SELECT CDS_ID, OBJECT_TYPE FROM SV_RECALC ";

				log.debug("Msg()  Query: " + sQuery);

				// run the query
				try {
					o_Statement = connection.createStatement();
					o_ResultSet = o_Statement.executeQuery(sQuery);
					while (o_ResultSet.next() != false) {
						Recalc isis = new Recalc();
						isis.setCDSId(o_ResultSet.getString("CDS_ID"));
						isis.setObjectType(o_ResultSet.getInt("OBJECT_TYPE"));
						o_Vector.add(isis);
					}

				} catch (SQLException e) {
					log.debug("Msg() EnrichmentDatabase.getRecalcData() - an SQLException is thrown"
							+ e);
					throw new EnrichmentDatabaseException(
							"SQLException Error occured in getRecalcData "
									+ e.getMessage());
				}
			} else
				log.debug("Msg() EnrichmentDatabase.getRecalcData() - Connection null");
		} catch (SQLException e) {
			log.debug("Msg() EnrichmentDatabase.getRecalcData() - an SQLException is thrown"
					+ e);
			throw new EnrichmentDatabaseException(
					"SQLException Error occured in getRecalcData "
							+ e.getMessage());
		} finally {
			// Close resultset
			try {
				if (o_ResultSet != null) {
					o_ResultSet.close();
				}

			} catch (SQLException sqle) {
				throw new EnrichmentDatabaseException(
						"Could not close resultset: " + sqle.getMessage());
			}

			// Close statement
			try {
				if (o_Statement != null) {
					o_Statement.close();
				}
			} catch (SQLException sqle) {
				throw new EnrichmentDatabaseException(
						"Could not close statment: " + sqle.getMessage());
			}

			OraPoolConnectionManager
					.returnConnection(ORACLE_POOL_NAME, connection);
			log.debug("Msg() EnrichmentDatabase.getRecalcData() - Connection returned");
		}
		return o_Vector;
	}

	public void deleteRecalcData(String cdsId, String objectType)
			throws EnrichmentDatabaseException {
		// check login name and password
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			log.debug("Msg() EnrichmentDatabase.deleteRecalcData() - Request for a connection");
			connection = OraPoolConnectionManager
					.requestConnection(ORACLE_POOL_NAME);
			log.debug("Msg() EnrichmentDatabase.deleteRecalcData() - Connection established");

			if (connection != null) {

				preparedStatement = connection
						.prepareStatement("DELETE FROM SV_RECALC WHERE CDS_ID = ? AND OBJECT_TYPE = ?");
				preparedStatement.setString(1, cdsId);
				preparedStatement.setInt(2, Integer.parseInt(objectType));
				preparedStatement.executeUpdate();

			} else
				log.debug("Msg() EnrichmentDatabase.deleteRecalcData() - Connection null");
		} catch (SQLException e) {
			log.debug("Msg() EnrichmentDatabase.deleteRecalcData() - an SQLException is thrown"
					+ e);
			throw new EnrichmentDatabaseException(
					"SQLException Error occured in getRecalcData "
							+ e.getMessage());
		} finally {

			// Close statement
			try {

				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException sqle) {
				throw new EnrichmentDatabaseException(
						"Could not close statment: " + sqle.getMessage());
			}

			OraPoolConnectionManager
					.returnConnection(ORACLE_POOL_NAME, connection);
			log.debug("Msg() EnrichmentDatabase.deleteRecalcData() - Connection returned");
		}
	}

}
