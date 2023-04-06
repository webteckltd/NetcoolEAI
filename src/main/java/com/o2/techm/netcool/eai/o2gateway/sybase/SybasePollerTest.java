/*
 * Created on 21-Jun-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.techm.netcool.eai.o2gateway.sybase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException; 
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.jdbc.PoolConnectionManager;
import com.sybase.jdbc2.jdbc.SybConnection; 
import com.sybase.jdbc2.jdbc.SybStatement;
import com.sybase.jdbcx.EedInfo;

/**
 * @author AAdemij1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SybasePollerTest {	
	private static final Logger log = LoggerFactory.getLogger(SybasePollerTest.class);
	private static final String JDBC_DRIVER = "com.sybase.jdbc2.jdbc.SybDriver";
	private static  String DEFAPPNAME =	"GATEWAY";
    private static  String DEFUSERNAME ="gateway";
    private static  String DEFPASSWORD ="magic-cookie";
    
    //String dbUrl = "jdbc:sybase:Tds:isisdev:4201/CIS_ISIS2";
    String dbUrl="jdbc:sybase:Tds:isisdev:4100/CIS_ISIS";
    String dbUrlBackup ="jdbc:sybase:Tds:cnbrs-testtmr-01:4100/AGG_P";
    
    String currentUrl=null;
    String dbUsername = "cicadmin";
    String dbPassword = "passw0rd";
    protected static final String sybaseThrGrpStr = "SysGroup";
    private static ThreadGroup sysbaseThreadGroup = new ThreadGroup(sybaseThrGrpStr);   
    private static Thread PollThr = null;
    public static String PNAME = "Sybase Poller Thread";
    boolean terminate=false;
    private Socket pollerSocket;
    private int server_pid;
    private boolean pollerflag1 = true;
    private static final String IDUC_POOL = "IDUCPOOLTEST";
	private boolean isServerDead = true;
	private boolean isBackup = false;
	

	
    void terminateThread()
    {
        terminate = true;
    }
    
    
    /**
	 * 
	 */
    public SybasePollerTest() {     	
    	
    	setHostAndPortFromURL(dbUrl);    	
    	while(isServerDead)
    	{
    		try {    			
    			if(checkServer())
    			{
    				isServerDead=false;
    				log.info("Creating a pool of JDBC connections to Sybase ...");	
    				createConnectionPool();	
    				PollThr = (new Thread(sysbaseThreadGroup,new SybasePoller (), PNAME));
    				PollThr.start();
    			}    		 
    		} 
    		catch (SQLException ex) 
			{
    			while (ex != null) 
    			{  
    				if (ex != null) 
    				{
    					log.debug( "Warning SQLState: " + ex.getSQLState () + "Message:  " + ex.getMessage () + "Vendor:   " + ex.getErrorCode () );
    					ex = ex.getNextException();
    				}
    			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("ObjectServer " + serverName + " is not running",e);			
			}		
			finally
			{
				if(isServerDead)
				{
					if(currentUrl.equals(dbUrl))
					{
						log.info("Attempting to Connect to the Backup ObjectServer");
						setHostAndPortFromURL(dbUrlBackup);
						isBackup=true;
					}
					else
						setHostAndPortFromURL(dbUrl);
				}
			}
			
    	}
    }
    
	void createConnectionPool() throws SQLException
	{
		Properties props= new Properties();
		props.put( "password",  DEFPASSWORD );
		props.put( "user", DEFUSERNAME );
		props.put( "APPLICATIONNAME" , DEFAPPNAME );
		props.put( "REMOTEPWD" , DEFPASSWORD );
		if(!PoolConnectionManager.isValid(IDUC_POOL)) 
		{
			 PoolConnectionManager.createPool(
					IDUC_POOL, JDBC_DRIVER,currentUrl,DEFUSERNAME,DEFPASSWORD,props);			
		}
	}
	public static void main(String[] args) {
		
		new SybasePollerTest();
	}
	
	/**
	 * 
	 * @author aademij1
	 *
	 * Thread thats recieves updates information from Sybase Datadase (CIC
	 */
	class SybasePoller implements Runnable
	{
		private int serverPort = 0;
		private String serverHost = null;
		private SybConnection poller_con;
		

		SybasePoller()
		{
			try {				
				poller_con = getPollerConnection();
			}catch (SybaseManagerException se) 
			{						
				se.printStackTrace(); 				 
			} 
		}		
		
		SybConnection getPollerConnection() throws SybaseManagerException
		{			
			log.debug("********* Get Poller Connection *********");
			
			Properties props= new Properties();
			props.put( "password",  DEFPASSWORD );
			props.put( "user", DEFUSERNAME );
			props.put( "APPLICATIONNAME" , DEFAPPNAME );
			props.put( "REMOTEPWD" , DEFPASSWORD );
			SybConnection pcon=null;
			
			pcon = reconnectToSybase(props);
			
			return pcon;
		}
		public void run() 
	    {
	    	// TODO Auto-generated method stub	    	 
	    	 
			try 
			{				
				/* Get the Poller Channel attributes */
				while(!terminate && getPoller(poller_con))
				{
					log.debug(" ********* Retrieved  Port *********"); 
					PollerReader();
				}				 
			}	    
			catch (IOException ie) {
				// TODO Auto-generated catch block
				ie.printStackTrace();
			}			
			catch(Exception ee)
			{
				ee.printStackTrace();				
			}		
	    }		
		
		void flushIduc(SybConnection con) throws SQLException  
		{
			boolean status=false;
			SybStatement  st_iduc;
		 	st_iduc = (SybStatement)con.createStatement();			
			status =  st_iduc.execute("flush iduc;");			 
		}
		
		/*
		 * get bit type
		 */
		private int operationType(int i)
		{
			int typebit=0;
			
			if((i & 4) == 4)
			{ typebit= 4;}
			else if((i & 1) == 1)
			{typebit = 1;}
			else if((i & 2) != 2)
			{typebit = 0;}
			else
				typebit = 2; 	        
			
			return typebit;
		}
		/*
		 * Gets updates from CIC
		 */
		void PollerReader()  throws IOException
		{        
			BufferedReader pollerReaderBuf;				
			String rowLabels[] = null;
			int pollPause;
			try {
				pollPause = 5;
			} catch (NumberFormatException e1) {
				log.error("Number Expected for this property, defaulting",e1);
				pollPause = Integer.parseInt(NetcoolConnector.SYS_POLL); 
			} 
			if(pollPause != Integer.parseInt(NetcoolConnector.SYS_POLL))
			{
				log.debug("Setting Polling time");
				pollerSocket.setSoTimeout(pollPause * 1000);
			}
			pollerReaderBuf = new BufferedReader(new InputStreamReader(pollerSocket.getInputStream()));
			PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(pollerSocket.getOutputStream()));
			printwriter.println("" + server_pid);
			printwriter.flush();
			 	
			int l=0;
			
			try
			{ 
				String aline = null;
				boolean iscontinue=false;	 
				while( pollerflag1 && !terminate)
				{    
					iscontinue=false;
					try
					{   if(isServerDead)
					   {						
						log.debug("locate other and swap");
						try {
							if(currentUrl.equals(dbUrl))
							{
								log.info("Connect to Backup ObjectServer");
								setHostAndPortFromURL(dbUrlBackup);
								isBackup=true;
							}
							else
							{
								isBackup=false;
								setHostAndPortFromURL(dbUrl);
							}
							poller_con = getPollerConnection();
						}catch (SybaseManagerException se) 
						{						
							log.debug ("Error:   ",se);				 
						} 
					}
					if(pollPause != Integer.parseInt(NetcoolConnector.SYS_POLL))
					{
						//Need to flush IDUC early
						log.debug("Flushing in "  + pollPause + " seconds"); 
						Thread.sleep(pollPause * 1000);
						//log.debug("Flushing now ....");
						flushIduc(poller_con);							
					}
					aline = pollerReaderBuf.readLine(); 
					
					if(aline == null)
					{
						log.error("Error while reading "); 
						try {
							Thread.sleep( 5 * 1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						iscontinue=true;
						//setPollerflag1(false);
					}    
					}
					catch(SocketException se)
					{
						log.debug("[SE]Timeout during read" );
						iscontinue=true;
					}
					catch(SQLException sse)
					{
						log.debug("[SQLException]Sybase SQL Exception",sse ); 
						log.debug ("Error:   " + sse.getErrorCode());
						if(sse instanceof EedInfo)
						{
							// This SQLException contains additional Sybase Adaptive
							// Server error message info.
							
							EedInfo eed = (EedInfo) sse;
							log.debug("   Severity: " + eed.getSeverity());
							log.debug("   Line Number: " + eed.getLineNumber());
							log.debug("   Server Name: " + eed.getServerName());
							log.debug("   Error State: " + eed.getState());
							log.debug("Procedure Name: " + eed.getProcedureName());
							
						}
						log.debug ("SQLState: " + sse.getSQLState ());
						log.debug ("Message:  " + sse.getMessage());
						
						if(sse.getMessage().matches(".*SybConnectionDeadException.*"))
						{
							log.error("Sybase Connection Dead Exception recieved");
						}
						if(!poller_con.isClosed())
						{
							log.debug("[SQLException] Closing connection");
							shutdown(poller_con);
						}	
						log.debug("[SQLException] Re-creating poller connection");
						try
						{
							poller_con = getPollerConnection();
						}
						catch(SybaseManagerException se)
						{
							log.error("An Error has occurred:",se); 							
							log.error("Sybase Connection refused on Second Attempt");														
							
							try
							{
								checkServer(currentUrl);
							}
							catch(Exception e)
							{
								log.error("ObjectServer is dead; try other Server");
								isServerDead=true;
							}
						}
						iscontinue=true;
					}
					catch(InterruptedIOException interruptedioexception)
					{
						//log.debug("[IE]Timeout during read" ); 
						iscontinue=true;
					}
					catch(Exception e)
					{
						log.error("Error while reading");        				
						setPollerflag1(false);
					}
					finally
					{
						if (aline != null)   
							log.debug("IDUC Ready ..... ");
					}
					/*if(iscontinue == true) 
					 continue; 
					log.debug("iscontinue=" + iscontinue);
					*/
					if( !terminate && isPollerflag1() && aline != null && aline.indexOf("c:iduc") != -1)
					{
						Statement  st_iduc = poller_con.createStatement();
						ResultSet rs_iduc =  st_iduc.executeQuery("get iduc;");
						if(rowLabels == null)
						{
							ResultSetMetaData rsmd_iduc = rs_iduc.getMetaData();
							int j = rsmd_iduc.getColumnCount();
							rowLabels = new String[j];
							for(int i = 1; i <= j; i++)
							{
								rowLabels[i - 1] = rsmd_iduc.getColumnName(i);
							}    
						}       			
						int serial=0,bit_type=0,otype=0;
						StringBuffer pickedSerial=new StringBuffer();
						while(rs_iduc.next()) 
						{
							otype = rs_iduc.getInt(1);
							bit_type = operationType(otype);
							serial = rs_iduc.getInt(2);
							
							String s =Integer.toString(serial);
							pickedSerial.append(s).append(",");
							if(bit_type > 2)
								log.debug("bit_type :" + bit_type);
							/*else
								log.debug("poller got type: " + otype + " serial: " + serial + " bit: " + bit_type);*/
							
						
						}      
						log.debug("pickedSerial=" + pickedSerial);
						rs_iduc.close();
						st_iduc.close();				  			
					} 
					
					aline = null;
					
					/**Switch over if Primary Server is back  **/
					
					if(isBackup())
					{
						/*Checking if Primary server is up*/
						if(true/*checkServer(dbUrl)*/)		
						{
							log.info("Closing all connecton to " + serverName);
							setHostAndPortFromURL(dbUrl);							
							try {
								shutdown(poller_con);
				    			PoolConnectionManager.closeAllConnections(IDUC_POOL);				    			
				    			log.debug("Now Re-creating  connections");
				    			poller_con = getPollerConnection();
								createConnectionPool(); 	
								isBackup=false;
				    		} 
							catch(SQLException se)
							{
								log.error("An Error has occurred:",se);		 
							}	
							finally
							{
								break;
							}
						}
						else
							log.error("Primary Server is STILL not avaliable");						
					}
					
				}        	
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
				l++;
			}        
			
			log.debug("poller Connection to " + poller_con + " lost.");
			log.debug("poller Connection number of unexpected reads were " + l);
			
			if(pollerSocket != null)
			{
				try
				{
					pollerSocket.close();
				}
				catch(Exception exception1)
				{
					log.error("Could not close iduc socket - Socket could be already closed");
				}
			}
			
		}
		
		/*
		 * Get poller information
		 */
		private boolean getPoller(SybConnection con)  
		{
			log.debug("Subscribing to poller");
			boolean canPoll=false;
			try
			{
				Statement statement =  con.createStatement();
				
				boolean getResultSet = statement.execute("bind to iduc;"); 
				
				if (getResultSet) {
					
					ResultSet resultset = statement.getResultSet();
					
					while(resultset.next()) 
					{
						serverPort = resultset.getInt("Port");
						server_pid = resultset.getInt("SPID");
						serverHost = resultset.getString("Hostname");
					}
					log.debug("poller read on " + serverHost + " : " + serverPort + " with " + server_pid);
					canPoll=true;
					resultset.close();
					
					pollerSocket = new Socket(serverHost, serverPort);
				    pollerSocket.setSoTimeout(30000);
				}
				else
				{
					log.debug("No Result returned");							            	 
				}	
				statement.close();	
			}          
			catch(IOException ie)
			{
				log.error(" I/O error : Subscription failed for poller connection.",ie);
				ie.printStackTrace();
				//flag = true;
				System.out.println("Trying again");
			}
			catch(Exception e)
			{
				log.error("Subsciption failed for poller connection: " ,e);
				e.printStackTrace();               
			}           
			
			log.debug("Subscribe done");
			return canPoll;       
		}
	}
	
	/**
	 * Closes all the sybase connections
	 */
    private void shutdown(Connection con)
	{		
	        log.debug("Shutting down connection to Sybase.");
	        if (con != null)
    		{
    			try {
					con.close();
					con.clearWarnings();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					log.error("Shutdown error:",e);					
				}
    			
    		}	
	        con= null;			 
	}
     
//	Create a pattern to match map 
	Pattern	serverMap = Pattern.compile("(jdbc\\:)(sybase\\:)(Tds\\:)(.+)\\:([0-9]*)\\/(.+)");
	private int serverPort=-1; 
	private String serverHost = null;
	private String serverName = null;
	private boolean fired;	 
		 
		
		//	jdbc:sybase:Tds:isisdev:4100/CIS_ISIS
		private void setHostAndPortFromURL(String url)
		{
			setCurrentUrl(url); 
			Matcher m = serverMap.matcher(getCurrentUrl()); 
		    if(m.find())
		    {
		    	serverHost = m.group(4);
		    	serverPort = Integer.parseInt(m.group(5));
		    	serverName = m.group(6);
		    	log.debug("serverHost:" + serverHost + "    serverPort:" + serverPort + "    serverName:" + serverName);
		    }
		}
		
		/**
		 * @return Returns the serverHost.
		 */
		public String getServerHost() {
			return serverHost;
		}
		/**
		 * @return Returns the serverName.
		 */
		public String getServerName() {
			return serverName;
		}
		/**
		 * @return Returns the serverPort.
		 */
		public int getServerPort() {
			return serverPort;
		}		
		public boolean checkServer() throws Exception
		{
			boolean isConn= false;		
			if(serverPort != -1)
			{
				log.info("Checking " + serverName + " on " + serverHost + ":" + serverPort);
				Socket socket;				
				socket = new Socket(serverHost, serverPort);
				socket.close();			
			}
			isConn = true; 
			return isConn;
		}
		public boolean checkServer(String url)  
		{
			boolean isConn= false;	
			Matcher m = serverMap.matcher(url); 
			if(m.find())
			{
				String sHost = m.group(4);
				int sPort = Integer.parseInt(m.group(5));
				String sName = m.group(6);
				log.info("[" + url + "]Checking " + sName  + " on " + sHost + ":" + sPort);
				Socket socket;
				try {
					socket = new Socket(sHost, sPort);
					isConn = true; 
					socket.close();							
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					log.error("Don't know about host: " + sHost + ".");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error("Couldn't get I/O for the connection to: " + sHost+ ".");  
				}				
			}
			return isConn;
		}
		public boolean checkServer(SQLException sse) throws Exception
		{
			boolean isConn= false;
			
			log.debug("[SQLException]Sybase SQL Exception",sse ); 
			log.debug ("Error:   " + sse.getErrorCode());
            if(sse instanceof EedInfo)
            {
                // This SQLException contains additional Sybase Adaptive
                // Server error message info.

                EedInfo eed = (EedInfo) sse;
                log.debug("   Severity: " + eed.getSeverity());
                log.debug("   Line Number: " + eed.getLineNumber());
                log.debug("   Server Name: " + eed.getServerName());
                log.debug("   Error State: " + eed.getState());
                log.debug("Procedure Name: " + eed.getProcedureName());

            }
            log.debug ("SQLState: " + sse.getSQLState ());
			log.debug ("Message:  " + sse.getMessage());

            if(sse.getMessage().matches(".*SybConnectionDeadException.*"))
            {
            	log.error("Sybase Connection Dead Exception recieved");
            }
            try {
    			PoolConnectionManager.closeAllConnections(IDUC_POOL);
    		} catch (SQLException e) {
    			 
    		}	
			log.debug("[SQLException] Re-creating  connection");
			try
			{
				createConnectionPool();
				isConn = true; 
			}
			catch(SQLException se)
			{
				log.error("An Error has occurred:",se); 							
				log.error("Sybase Connection refused on Second Attempt");														
				
				try
				{
					isConn = checkServer();
				}
				catch(Exception e)
				{
					log.error("ObjectServer is dead; try other");
					isServerDead=true;
				}
			}			
			return isConn;
		}
		
		/**
		 * Overloaded method
		 * Get a Sybase connection for Poller Only
		 * @see reconnectToSybase()
		 */
		public SybConnection  reconnectToSybase(Properties props) throws SybaseManagerException
	    {
	    
		    SybConnection con=null;
	    	fired = true;	 
			 					
			log.info( ": Opening sybase connection ...... Calling Version 5 Sybase Drivers"); 
//			jdbc:sybase:Tds:isisdev:4100/CIS_ISIS
			 
				
			try {
				
				DriverManager.registerDriver((Driver)
		                Class.forName(JDBC_DRIVER).newInstance());
		        
		    	DriverManager.setLoginTimeout(30);
		    	con= null;
		    	try
				{	    		
		    		con = (SybConnection)DriverManager.getConnection(currentUrl,props);	    	
				}
		    	catch (SQLException ex) 
				{   
		    		log.debug("",ex);
		    		if (con != null)
		    		{
		    			con.close();
		    			con.clearWarnings();
		    		}	    		
				}
		    
		    	if(con == null)
		        {		    		 
					isServerDead=true;	
		    		log.info( "1. Connection is null");
		    		throw new SybaseManagerException("Connection could not be made; Check if Server is dead !!!");
		        }
				log.info( ":Openned connection successfully.");
				isServerDead=false;					 
			}
			catch (NumberFormatException e1) {
				// TODO Auto-generated catch block#
				log.error("cannot parse port number",e1);
				e1.printStackTrace();
				
			}
			catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				log.error("Class Not Found",e1);
				e1.printStackTrace();
			}
			catch (Exception e) 
			{ 
				throw new SybaseManagerException(e);
			}
			finally
			{
				fired=false;				
			}
			return con; 
	    }		
		
		/**
		 * @return Returns the pollerflag1.
		 */
		private boolean isPollerflag1() {
			return pollerflag1;
		}
		/**
		 * @param pollerflag1 The pollerflag1 to set.
		 */
		private void setPollerflag1(boolean pollerflag1) {
			this.pollerflag1 = pollerflag1;
		}
	/**
	 * @return Returns the currentUrl.
	 */
	public String getCurrentUrl() {
		return currentUrl;
	}
	/**
	 * @param currentUrl The currentUrl to set.
	 */
	public void setCurrentUrl(String currentUrl) { 
		this.currentUrl = currentUrl;
	}
	/**
	 * @return Returns the isBackup.
	 */
	public boolean isBackup() {
		return isBackup;
	}
}
