package com.o2.techm.netcool.eai.manonsite.jdbc.oracle;

import java.sql.*;  
import com.o2.techm.netcool.eai.manonsite.jdbc.PoolConnectionManager;


public class OraPoolConnectionManager extends PoolConnectionManager
{
    /*private static Hashtable pool;
	private static final int POOL_INIT_LIMIT = 10;
	private static final int POOL_INCR_LIMIT = 10;
    
    static {
    	pool = null; 
    }; */
    
	public static void createPool(
		String poolName,
		String driverName,
		String dbUrl,
		String username,
		String password)
		throws SQLException
	{
		OraConnectionManager connectionManager = null;
		try
		{
			connectionManager =
				new OraConnectionManager(driverName, dbUrl, username, password);
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
		
		
}
