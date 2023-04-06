/*
 * Created on 27-Apr-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.techm.netcool.eai.manonsite.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AAdemij1
 *
 * This should really be an abstract Class but cannot make it so because of the need for Static
 * methods. Also note with the final modifier used with the methods means you cannot override them 
 */
public  class PoolConnectionManager {
	protected static Hashtable pool;
	protected static final int POOL_INIT_LIMIT = 3; //OSC 459 before POOL_INIT_LIMIT = 10
	protected static final int POOL_INCR_LIMIT = 3; //OSC 459 before POOL_INCR_LIMIT = 10
	protected static int JDBC_RETRY_CNT=3;
	static {
		pool = null;
	};

	private static final Logger log = LoggerFactory.getLogger(PoolConnectionManager.class);
	protected static final void storeConnectionManager(String poolName, ConnectionManager connectionManager)
	{
		if(pool == null)
		{
			pool = new Hashtable(POOL_INIT_LIMIT, POOL_INCR_LIMIT);
		}
		if(!pool.containsKey(poolName))
		{
			pool.put(poolName, connectionManager);
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
	
	public static final Connection requestConnection(String poolName)
	throws SQLException
	{
		Object obj = null;
		Connection connection = null;

		int i = 0;
		if(pool != null && pool.containsKey(poolName))
		{
			ConnectionManager connectionManager = (ConnectionManager) pool.get(poolName);			
			//while(connection == null && i++ < 3)
			do
			{
				try
				{
					log.debug("OSC 459 connection attempt: " + i);
					connection = connectionManager.getConnectionSingleton();
				}
				catch(SQLException sqlexception)
				{
					
				}
			}while(connection == null && JDBC_RETRY_CNT > i++ );
			
			if(connection == null)
			{
				log.debug("Cannot get a connection handle");
				throw new SQLException("Cannot get a connection handle");
			}
		}

		return connection;
	}	

	
	public static int returnConnectionSize(String poolName)
	{
		ConnectionManager connectionManager = (ConnectionManager) pool.get(poolName);
		return connectionManager.getConnections().size();
	}
	public static final void returnConnection(String poolName, Connection connection)
	{
		Object obj = null;
		if(pool != null && pool.containsKey(poolName))
		{
			ConnectionManager connectionManager = (ConnectionManager) pool.get(poolName);
			connectionManager.returnConnectionSingleton(connection);
		}
	}
}


