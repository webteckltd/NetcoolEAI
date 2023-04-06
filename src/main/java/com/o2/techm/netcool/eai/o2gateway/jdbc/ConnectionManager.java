package com.o2.techm.netcool.eai.o2gateway.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManager;

public class ConnectionManager
{
	private String driverName;
	private String dbUrl;
	private String username;
	private String password;
	private Properties props;
	private Hashtable connections;
	private static final Logger log = LoggerFactory.getLogger(SybaseManager.class);

	 
	
	private final int INITIAL_CONNECTIONS = 10;
	private final int ADDITIONAL_CONNECTIONS = 5;

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
	    	log.debug(" Creating pooled  connections for dbUrl= " + dbUrl );
			createConnections(INITIAL_CONNECTIONS,dbUrl);
			log.debug("  pooled  connections for dbUrl= " + dbUrl + "  Created Sucessfully" );
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

	private Connection createConnections(int i)
		throws SQLException
	{
		Connection con=null;
		int j = -1;
		j = connections.size();
		for(int k = 0; k < i; k++)
			try
			{
				
				if(props != null) 
					con = DriverManager.getConnection(dbUrl, props); 
				else 
					con = DriverManager.getConnection(dbUrl, username, password); 
				if(con !=null)
						connections.put(con, new Boolean(false));					
			}
			catch(SQLException sqlexception)
			{
				throw sqlexception;
			}
		return con;
	}
	
	private void createConnections(int i,String ldbUrl)
			throws SQLException
	{
		int j = -1;
		j = connections.size();
		for(int k = 0; k < i; k++){
			try
			{
				Connection con=null;
				if(props != null) 
					con = DriverManager.getConnection(ldbUrl, props); 
				else 
					con = DriverManager.getConnection(ldbUrl, username, password); 

				if(con !=null){	
					connections.put(con, new Boolean(false));
				}else{
					log.error("DB connection for URL = " +ldbUrl + " found Null" );
				}
			}
			
			catch(SQLException sqlexception)
			{
				throw sqlexception;
			} 
		}
		
		log.error("Number of Pooled connection  for URL " +ldbUrl + " are = " + connections.size() );
	}
	 /**
     *  A SQLException was generated.  Catch it and
     *  display the error information.  Note that there
     *  could be multiple error objects chained
     *  together.<pollerBuf>
     *  @param ex  info will be displayed for this exception <p>
     */
    public String  displaySQLEx(SQLException ex)
    {
    	String mesg=null;
        //ex.printStackTrace();
        if (ex != null) 
        {
            mesg ="SQLState: " + ex.getSQLState () + "Message:  " + ex.getMessage () + "Vendor:   " + ex.getErrorCode () ;
            ex = ex.getNextException ();           
        }
        return mesg;
    }
	/*void closeConnections()
	throws SQLException
{
		Connection connection = null;
		java.util.Enumeration enumeration = null;

		Boolean gotConnection = null;
		if(connections == null)
		{
			return;
		}
		enumeration = connections.keys();
		synchronized(enumeration)
		{
			do
			{
				connection = (Connection)enumeration.nextElement();
				gotConnection = (Boolean)connections.get(connection);
				if(gotConnection != null)
				{
					try {
						if(!connection.isClosed())
						{
							connection.close();
							connection.clearWarnings();
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						String mesg = displaySQLEx(e);
						if(mesg != null)
							throw new SQLException(mesg);
						else
							throw new SQLException();
					} 
					connections.remove(connection);
				}
			} while(enumeration.hasMoreElements());
		}
}*/
	void closeConnections()
	throws SQLException
{
		Connection connection = null;
		java.util.Enumeration enumeration = null;

		Boolean gotConnection = null;
		if(connections == null)
		{
			return;
		}
		enumeration = connections.keys();
		synchronized(enumeration)
		{
			int c_cnt=0;
			while(enumeration.hasMoreElements())
			{
				connection = (Connection)enumeration.nextElement();
				synchronized(connection)
				{
					gotConnection = (Boolean)connections.get(connection);
					c_cnt++;
					if(gotConnection != null)
					{
						
						if(gotConnection.booleanValue() == true )
						{														
							log.info("[" + c_cnt + "This connection is still use");
							try {Thread.sleep(5 * 1000);} catch (InterruptedException e) {}
							if(((Boolean)connections.get(connection)).booleanValue() == true )
							{
								log.info("[" + c_cnt + "This connection is still use; removing anyway");
							}
						}
						connections.remove(connection);					 
						try {
							if(!connection.isClosed())
							{
								connection.close();
								connection.clearWarnings();
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							String mesg = displaySQLEx(e);
							if(mesg != null)
								throw new SQLException(mesg);
							else
								throw new SQLException();
						}						
					}
				}
			} 
		}
}
	public Connection getConnectionSingleton()
		throws SQLException
	{
		Connection connection = null;
		java.util.Enumeration enumeration = null;

		Boolean gotConnection = null;
		boolean isEmpty=false;
		if(connections == null)
		{
			log.error("Pooledd connection table for  Connection Manager with URL  = "+ this.dbUrl + " founds null .. returning Null connectiins ");
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
					//log.debug(" gotConnection.booleanValue() = " + gotConnection.booleanValue() +"for  Connection Manager with URL  = "+ this.dbUrl + "connection = " + connection);
					if(gotConnection != null && !gotConnection.booleanValue())
					{
						connections.put(connection, new Boolean(true));
					  //  log.debug(" Found connection for  Connection Manager with URL  = "+ this.dbUrl);
					    break ; 
					}
				} while(gotConnection != null && gotConnection.booleanValue() && enumeration.hasMoreElements());
			}
			catch(NoSuchElementException nsee)
			{
				log.debug("It seems the Connection pool  is empty for URL  = "+ this.dbUrl, nsee);
				isEmpty=true;
			}
		}
		if(gotConnection != null && gotConnection.booleanValue() || isEmpty)
		{
			log.debug("Increasing Connections in  pool for  URL  = "+ this.dbUrl);
			try
			{
				synchronized(connections)
				{
					if(connections.size() == 0)
						createConnections(INITIAL_CONNECTIONS);
					else
						createConnections(ADDITIONAL_CONNECTIONS);
				}
			}
			catch(SQLException sqlexception)
			{
				log.error("Exception while Increasing Connections in  pool for  URL  = "+ this.dbUrl , sqlexception);
				throw sqlexception;
			}
			finally
			{
				log.error("Returning null connection for this URL  = "+ this.dbUrl);
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
					connection  = createConnections(1);
					connections.put(connection, new Boolean(true));
				} else
				{
					Statement statement = connection.createStatement();
					ResultSet resultset = statement.executeQuery("show props");
					resultset.close();
					statement.close();
					statement = null;
					resultset = null;
				}
			}
			catch(SQLException sqlexception1)
			{
				log.error("Exception while fetching Connection form  pool for  URL  = "+ this.dbUrl,sqlexception1 );
				connections.remove(connection);
				connection  = createConnections(1);
				connections.put(connection, new Boolean(true));
			}
			finally
			{
				return connection;
			}
		}
	}

	public void returnConnectionSingleton(Connection connection)
	{
		synchronized(connections)
		{
			connections.put(connection, new Boolean(false));
		}
	}

	/**
	 * @param dbUrl the dbUrl to set
	 */
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}	
}
