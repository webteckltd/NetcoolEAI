package com.o2.techm.netcool.eai.manonsite.jdbc.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.jdbc.ConnectionManager;

public class OraConnectionManager extends ConnectionManager
{
	/*private String driverName;
	private String dbUrl;
	private String username; 
	private String password;
	private Hashtable connections;
	
	private final int INITIAL_CONNECTIONS = 10;
	private final int ADDITIONAL_CONNECTIONS = 5;*/
	private static final Logger log = LoggerFactory.getLogger(OraConnectionManager.class);
	public OraConnectionManager(
		String driverName,
		String dbUrl,
		String username,
		String password)
		throws ClassNotFoundException, SQLException
	{
		super(driverName,dbUrl,username,password);	
	}

	
	protected Connection createConnections(int i) throws SQLException
	{
		Connection con=null;
		Object obj = null;
		int j = -1;
		j = connections.size();
		for(int k = 0; k < i; k++)
			try
			{
				log.debug("OSC 459 connection attempt: " + k);
				con  = DriverManager.getConnection(dbUrl, username, password);
				connections.put(con, new Boolean(false));
			}
			catch(SQLException sqlexception)
			{
				log.error("Cannot sucessfully recreate another connection" ,sqlexception );
				throw sqlexception;
			}
		
		return con;
	}
	
	public Connection getConnectionSingleton()
		throws SQLException
	{
		Connection connection = null;
		java.util.Enumeration enumeration = null;
		
		Boolean gotConnection = null;
		if(connections == null)
		{
			return connection;
		}
		
		enumeration = connections.keys();
		synchronized(enumeration)
		{
			try
			{
				do
				{
					connection = (Connection)enumeration.nextElement();
					gotConnection = (Boolean)connections.get(connection);
					if(gotConnection != null && !gotConnection.booleanValue())
					{
						connections.put(connection, new Boolean(true));
					}
				} while(gotConnection != null && gotConnection.booleanValue() && enumeration.hasMoreElements());
			}
			catch(NoSuchElementException nsee)
			{
				log.debug("It seems the Connection is empty. Creating a new Pool");
				try
				{
					createConnections(INITIAL_CONNECTIONS);
				}
				catch(SQLException sqlexception)
				{
					log.error("Trying to create a new set of connections but cannot please check previous error logs" );
					throw sqlexception;
				}
				finally
				{
					return null;
				}
			}
		}
		if(gotConnection != null && gotConnection.booleanValue())
		{
			try
			{
				
				createConnections(5);
			}
			catch(SQLException sqlexception)
			{
				log.error("Trying to create a new set of connections but cannot please check previous error logs" );
				throw sqlexception;
			}
			finally
			{
				return null;
			}
		} 
		else
		{
			try
			{
				if(connection.isClosed())
				{
					connections.remove(connection);
					connection = null;
					log.info("Recreating another connection ");
					connection  = 	createConnections(1);
				} else
				{
					Statement statement = connection.createStatement();
					ResultSet resultset = statement.executeQuery("SELECT 'Connection Test' FROM DUAL");
					resultset.close();
					statement.close();
					statement = null;
					resultset = null;
				}
			}
			catch(SQLException sqlexception1)
			{				
				connections.remove(connection);
				connection = null;
				connection  =   createConnections(1);
			}
			finally
			{
				return connection;
			}
		}
	}

}
