package com.o2.techm.netcool.eai.manonsite.jdbc.sybase;

import java.sql.*;
import java.util.Hashtable;
import java.util.Properties;

import com.o2.techm.netcool.eai.manonsite.jdbc.PoolConnectionManager;



public class SyPoolConnectionManager extends PoolConnectionManager
{
	private static Hashtable pool;
	private static final int POOL_INIT_LIMIT = 10;
	private static final int POOL_INCR_LIMIT = 10;
	
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
		SyConnectionManager connectionManager = null;
		try
		{
			connectionManager =
				new SyConnectionManager(driverName, dbUrl, username, password,props);
		}
		catch (ClassNotFoundException classnotfoundexception)
		{
			throw new SQLException("Driver class '" + driverName + "' not found.");
		}
		catch (SQLException sqlexception)
		{
			throw sqlexception;
		}
		storeConnectionManager(poolName, connectionManager);
	}
	
	
	
	public static final boolean closeAllConnections(String poolName)
	throws SQLException
	{ 
		
		
		int i = 0;
		if(pool != null && pool.containsKey(poolName))
		{
			SyConnectionManager connectionManager = (SyConnectionManager) pool.get(poolName);
			
			try
			{
				connectionManager.closeConnections();
			}
			catch(SQLException sqlexception)
			{
				throw sqlexception;
			}
		}
		
		return true;
	}
	
}
