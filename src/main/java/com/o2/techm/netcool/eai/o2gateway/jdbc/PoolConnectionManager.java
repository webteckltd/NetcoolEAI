package com.o2.techm.netcool.eai.o2gateway.jdbc;

import java.sql.*;
import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;



public class PoolConnectionManager
{
    private static Hashtable pool;
	private static final int POOL_INIT_LIMIT = 10;
	private static final int POOL_INCR_LIMIT = 10;
	protected static int JDBC_RETRY_CNT=3;
	private static final Logger log = LoggerFactory.getLogger(PoolConnectionManager.class);
    static {
    	pool = null;
    };

	public static final void createPool(
		String poolName,
		String driverName,
		String dbUrl,
		String username,
		String password,Properties props)
		throws SQLException
	{
		ConnectionManager connectionManager = null;
		try
		{
			//log.debug("Creating connection manager for dbUrl = " + dbUrl + " poolName =  " + poolName);
			connectionManager =
				new ConnectionManager(driverName, dbUrl, username, password,props);
			//log.debug("connection manager for dbUrl = " + dbUrl + " poolName =  " + poolName  + " Created sucessfully");
		}
		catch (ClassNotFoundException classnotfoundexception)
		{
			log.error(" Problem Creating connection manager for dbUrl = " + dbUrl + " poolName =  " + poolName , classnotfoundexception);
			throw new SQLException("Driver class '" + driverName + "' not found.");
		}
		catch (SQLException sqlexception)
		{
			log.error(" Problem Creating connection manager for dbUrl = " + dbUrl + " poolName =  " + poolName , sqlexception);
			throw sqlexception;
		}
		storeConnectionManager(poolName, connectionManager);
	}

	private static final void storeConnectionManager(String poolName, ConnectionManager connectionManager)
	{
		if(pool == null)
		{
			pool = new Hashtable(POOL_INIT_LIMIT, POOL_INCR_LIMIT);
		}
		if(!pool.containsKey(poolName))
		{
			pool.put(poolName, connectionManager);
		}
		log.debug(" Entries in Connection Pool = " + pool.toString());
	}

	public static final void removeConnectionManager(String poolName)
	{
		 
		if(pool.containsKey(poolName))
		{
			pool.remove(poolName);
		}
	}
	public static final boolean isValid(String poolName)
	{
		if (pool == null)
		{
			return false;
		}
		else
		{
			return pool.containsKey(poolName);
		}
	}

	public static final void setConnectionUrl(String poolName, String dbUrl)
	throws SQLException
{
		if(pool != null && pool.containsKey(poolName))
		{
			ConnectionManager connectionManager = (ConnectionManager) pool.get(poolName);
			connectionManager.setDbUrl(dbUrl);
		}
}
	public static final Connection requestConnection(String poolName)
		throws SQLException
	{ 
		Connection connection = null;

		int i = 0;
		if(pool != null && pool.containsKey(poolName))
		{
			ConnectionManager connectionManager = (ConnectionManager) pool.get(poolName);
			//log.debug("found connection manager for pool  = " +poolName );
			do
			{
				try
				{
					//log.debug("Trying to get connection for Connection Pool   = " +poolName );
					connection = connectionManager.getConnectionSingleton();
					//log.debug("recived connection for Connection Pool   = " +poolName  + " is " + connection);
				}
				catch(SQLException sqlexception)
				{
					try {
						if(i == JDBC_RETRY_CNT)
							Thread.sleep(30 * 1000);
					} catch (InterruptedException e) {}
				}
			}while(connection == null && JDBC_RETRY_CNT > ++i );
			
			if(connection == null)
			{
				log.debug("Cannot get a connection handle");
				throw new SQLException("Cannot get a connection handle");
			}
		}

		return connection;
	}

	public static final boolean closeAllConnections(String poolName)
	throws SQLException
{ 
	 

	int i = 0;
	if(pool != null && pool.containsKey(poolName))
	{
		ConnectionManager connectionManager = (ConnectionManager) pool.get(poolName);
		 
			try
			{
				connectionManager.closeConnections();
			}
			catch(SQLException sqlexception)
			{
				 
			}
	}

	return true;
}
	public static final void returnConnection(String poolName, Connection connection)
	{
		Object obj = null;
		if(pool != null && pool.containsKey(poolName) && connection != null)
		{
			ConnectionManager connectionManager = (ConnectionManager) pool.get(poolName);
			connectionManager.returnConnectionSingleton(connection);
		}
	}
}
