package com.o2.techm.netcool.eai.manonsite.jdbc.sybase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.jdbc.ConnectionManager; 

public class SyConnectionManager extends ConnectionManager
{
	/*private String driverName;
	private String dbUrl;
	private String username;
	private String password;
	private Properties props;
	private Hashtable connections;
	private final int INITIAL_CONNECTIONS = 10;
	private final int ADDITIONAL_CONNECTIONS = 5;
	*/
	private static final Logger log = LoggerFactory.getLogger(SyConnectionManager.class);	

	public SyConnectionManager(
		String driverName,
		String dbUrl,
		String username,
		String password,Properties props)
		throws ClassNotFoundException, SQLException
	{
		super(driverName,dbUrl,username,password,props);
	}

	protected Connection createConnections(int i)
		throws SQLException
	{
		Connection con=null;
		Object obj = null;
		int j = -1;
		j = connections.size();
		for(int k = 0; k < i; k++)
			try
			{
				if(props != null){
					con  = DriverManager.getConnection(dbUrl, props);
					connections.put(con, new Boolean(false));
				}
				else{
					con  =  DriverManager.getConnection(dbUrl, username, password);
					connections.put(con, new Boolean(false));	
				}
			}
			catch(SQLException sqlexception)
			{
				log.error("Problemn while executing createConnections(int i) " , sqlexception);
				throw sqlexception;
			}
		return con;
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
        ex.printStackTrace();
        if (ex != null) 
        {
            mesg ="SQLState: " + ex.getSQLState () + "Message:  " + ex.getMessage () + "Vendor:   " + ex.getErrorCode () ;
            ex = ex.getNextException ();           
        }
        return mesg;
    }
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
		if(gotConnection != null && gotConnection.booleanValue())
		{
			try
			{
				createConnections(5);
			}
			catch(SQLException sqlexception)
			{
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
					connection  = createConnections(1);
				}
			}
			catch(SQLException sqlexception1)
			{
				log.error("whats going on here",sqlexception1);
				connections.remove(connection);
				connection = null;
				connection = createConnections(1);
			}
			finally
			{				
				return connection;
			}
		}
	}
}
