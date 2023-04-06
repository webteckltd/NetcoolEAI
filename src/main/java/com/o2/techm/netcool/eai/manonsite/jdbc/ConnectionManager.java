package com.o2.techm.netcool.eai.manonsite.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager; 
import java.sql.SQLException; 
import java.util.Hashtable;
import java.util.Properties;

abstract public class ConnectionManager
{
	protected String driverName;
	protected String dbUrl;
	protected String username; 
	protected String password;
	protected Hashtable connections;
	protected Properties props;
	
	protected final int INITIAL_CONNECTIONS = 3; //osc 459 before INITIAL_CONNECTIONS = 10
	protected final int ADDITIONAL_CONNECTIONS = 1;//osc 459 before ADDITIONAL_CONNECTIONS = 5

	public ConnectionManager(
		String driverName,
		String dbUrl,
		String username,
		String password)
		throws ClassNotFoundException, SQLException
	{
		this.driverName = driverName;
		this.dbUrl = dbUrl;
		this.username = username;
		this.password = password;
		connections = new Hashtable(INITIAL_CONNECTIONS, ADDITIONAL_CONNECTIONS);
		
		try
		{
			Class.forName(driverName);
			createConnections(INITIAL_CONNECTIONS); 
		}
		catch(ClassNotFoundException ex)
		{
			throw ex;
		}
		catch(SQLException ex)
		{
			throw ex;
		}	
	}

	public ConnectionManager(
			String driverName,
			String dbUrl,
			String username,
			String password,Properties props)
			throws ClassNotFoundException, SQLException
		{
			this.driverName = driverName;
			this.dbUrl = dbUrl;
			this.username = username;
			this.password = password;
			this.props = props;
			connections = new Hashtable(INITIAL_CONNECTIONS, ADDITIONAL_CONNECTIONS);

			try
			{
				//Class.forName(driverName);
				DriverManager.registerDriver((Driver)
		                Class.forName(driverName).newInstance());
		        
		    	DriverManager.setLoginTimeout(30);
				createConnections(INITIAL_CONNECTIONS);
			}
			catch(ClassNotFoundException ex)
			{
				throw ex;
			}
			catch(SQLException ex)
			{
				throw ex;
			}
			catch (Exception ex) 
			{
				throw new SQLException();  
		   }
		}
	protected abstract Connection createConnections(int i) throws SQLException;
		
	public abstract Connection getConnectionSingleton() throws SQLException;

	public void returnConnectionSingleton(Connection connection)
	{
		synchronized(connections)
		{
			connections.put(connection, new Boolean(false));
		}
	}

	/**
	 * @return the connections
	 */
	public Hashtable getConnections() {
		return connections;
	}	
}
