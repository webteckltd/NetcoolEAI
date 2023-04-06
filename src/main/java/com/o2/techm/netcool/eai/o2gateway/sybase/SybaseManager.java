/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 				 1.0					 Base version
* 25-Oct-2010    1.1       KEANE         Modified for DR 1434952   
* 01-Sep-2014    1.2       Indra         OSC 1054 - Improvements to PTW                                                                                                       
* 02-Sep-2014    1.3       Indra         CD38583 - BMR
*/
package com.o2.techm.netcool.eai.o2gateway.sybase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.jdbc.PoolConnectionManager;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPair;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNetcoolEvent;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.sybase.thread.IDCUThreadPool;
import com.o2.techm.netcool.eai.o2gateway.sybase.thread.IDUCInfo;
import com.o2.util.WorkQueue;
import com.sybase.jdbc2.jdbc.SybConnection;
import com.sybase.jdbc2.jdbc.SybSQLException;
import com.sybase.jdbc2.jdbc.SybStatement;

/**
 * Class that encapsulates all sybase functionality
 * @author aademij1
 *
 *  
 */
/**
 * @author aademij1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SybaseManager extends NetcoolConnector
{
	private static final Logger log = LoggerFactory.getLogger(SybaseManager.class);
	public static final String SYSBASE_TABLE_TIMER = "o2gateway.table.resync.timer"; 
    public static final String SYSBASE_TABLE_TIMER_DEF = "3600";      
   
 	public static String NAME = "SybaseManager";
	public static String GNAME = "Sybase Reciever Thread";
	public static String PNAME = "Sybase Poller Thread";
	public static String UNAME = "Sybase Pusher Thread";
	private static final String JDBC_DRIVER = "com.sybase.jdbc2.jdbc.SybDriver";
	
	private static final String DEFAULT_UID = "65534";
	private static final String DEFAULT_USER = "NETCOOL_OSS";
	
	private static final String OPEN="open";
	private static final String CLOSE="close";
	private static final String UPDATE="update";
	private static final String JOURNAL="journal";
	private static WorkQueue updatev = new WorkQueue();
	private static WorkQueue insertv = new WorkQueue();
	private static WorkQueue deletev = new WorkQueue();
	private static WorkQueue journalv = new WorkQueue();
	private boolean usePoller = true;
	int threadPoolSize = 0;
	/**
	 * Sybase database URL
	 */
	private String dbUrl=null; 
	private String dbUrlBackup=null;
	
	/**
	 * Login name to access Sybase Database
	 */
	private String dbUsername;
	/**
	 * Password to access Sybase Database </code>
	 */
	private String dbPassword;
	 
	protected static final String sybaseThrGrpStr = "SysGroup";
	public  static final int PAUSE=5;
    private static ThreadGroup sysbaseThreadGroup = new ThreadGroup(sybaseThrGrpStr); 
	private static IDCUThreadPool iducThreadpool; 
	private IDUCInfo iducinfos[];
    private	SybConnection /*sender_con,reciever_con,*/poller_con; 
	
	public static final String [] SQLOPERATORS = {"+","-","*","/","+","-","||","=","!=","<",">","<=",">=","IS NULL","LIKE","BETWEEN","IN","NOT","AND","OR"}; 
	
	public static int ACTION_INSERT = 1;
	public static int ACTION_UPDATE = 2;
	public static int ACTION_DELETE = 4;
	public static int ACTION_JOURNAL =8;
	// ;
	//
	
	public static final int OPERATION_ANDER=15;
	public static final int MAX_NO_OF_UNEXPEXTED_READS=5;
	public static final int MAX_MILLISECS_OF_UNEXPEXTED_READS=30000;
	PluginManager mgr;
	/**
	    * Static instance of SybaseManager
	    */
	public static SybaseManager sybaseMgr = null;
	private Hashtable maps;
	private static Hashtable COLUMNDESP=null;
	
	private String queryOpen = null;
	private String queryJournal= null;
	private String queryClose= null;
	private String queryUpd= null;
	
	private static  String DEFAPPNAME =	"GATEWAY";
    private static  String DEFUSERNAME ="gateway";
    private static  String DEFPASSWORD ="magic-cookie";
	
    public static final String [] DATECOLUNMS = {"FirstOccurrence","StateChange","LastOccurrence"};
    public static final String [] CONVCOLUNMS = {"OwnerUID","Severity"};
	 
	private WorkQueue writerQueue = new WorkQueue();	
	private WorkQueue readerQueue = new WorkQueue();

	private static final String SQLSEP = " , ";
	private static final String SQLEND = " ;";

	private static final String SQLFROM = " from ";

	private static final String SQLWHERE = " where ";

	private static final String SQLSELECT = "select ";
	private static final String SQLEQUAL = " = ";
	private static final String SQLSQUOTE = "'";
	private static Thread InsThr = null ;
	private static Thread PollThr = null ;	
	private static int threadsStarted=0;
	public static int iducthreadsStarted=0;
	int op_flags=0;
	
	//private String[][] actionArr = new String [4][];
	 
	private Socket pollerSocket;
	private int server_pid;

	private boolean pollerflag1 = true; 
	boolean terminate=false;
	private static final String IDUC_POOL = "IDUCPOOL";
	private static final String APPNAME = "O2GATEWAY";
	private String currentUrl=null;	
	boolean isServerDead=false; 
	
	void terminateThread()
    {
        terminate = true;
    }
	
	
	/**
	 * 
	 * @author aademij1
	 *
	 * Thread thats recieves updates information from Sybase Database (CIC
	 */
	class SybasePoller implements Runnable
	{
		private int serverPort = 0;
		private String serverHost = null;
		private BufferedReader pollerReaderBuf;
		private int pollPause;
		long leventTime=System.currentTimeMillis();//Last event time
		SybasePoller()
		{
			try {
				poller_con = getPollerConnection(currentUrl);
			}catch (SybaseManagerException se) 
			{						
				se.printStackTrace();
				shutdown();
				try {
					mgr.shutdown();
				} catch (PluginException e1) { 
					e1.printStackTrace();					
				}
			} 
		}		

		SybConnection getPollerConnection(String cUrl) throws SybaseManagerException
		{			
			log.debug("********* Get Poller Connection for " + cUrl + " *********");

			Properties props= new Properties();
			props.put( "password",  DEFPASSWORD );
			props.put( "user", DEFUSERNAME );
			props.put( "APPLICATIONNAME" , DEFAPPNAME );
			props.put( "REMOTEPWD" , DEFPASSWORD );
			SybConnection pcon=null;

			pcon = reconnectToSybase(props, cUrl);

			if(getPoller(pcon))
			{
				try {
					pollPause = Integer.parseInt(mgr.getString(NetcoolConnector.GATEWAY_POLL));
				} catch (NumberFormatException e1) {
					log.error("Number Expected for this property, defaulting",e1);
					pollPause = Integer.parseInt(NetcoolConnector.SYS_POLL); 
				} catch (PluginException e1) { 
					log.error("Cannot retrieve property, defaulting",e1);
					pollPause = Integer.parseInt(NetcoolConnector.SYS_POLL); 
				}
				try {
					if(pollPause != Integer.parseInt(NetcoolConnector.SYS_POLL))
					{
						log.debug("Setting Polling time");

						pollerSocket.setSoTimeout(pollPause * 1000);
					}
					pollerReaderBuf = new BufferedReader(new InputStreamReader(pollerSocket.getInputStream()));
					PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(pollerSocket.getOutputStream()));
					printwriter.println("" + server_pid);
					printwriter.flush();
				} catch (SocketException e) {
					log.error("SocketException: ",e);
				} catch (IOException e) {
					log.error("IOException: ",e);
				} 	
			}
			return pcon;
		}
		public void run() 
	    {
	    	try 
			{				
				/* Get the Poller Channel attributes */
				if(!terminate && usePoller)
				{
					log.debug(" ********* Retrieved  Port *********");
					threadsStarted++;
					PollerReader();
				}				 
			}	    
			catch (IOException ie) { 
				log.error("IOException: ", ie); 
			}			
			catch(Exception ee)
			{
				ee.printStackTrace();				
			}		
	    }
		
		
		void flushIduc(SybConnection con) throws SQLException
		{
			SybStatement  st_iduc;
			try {
				st_iduc = (SybStatement)con.createStatement();	
				st_iduc.setQueryTimeout(30);
				st_iduc.execute("flush iduc;");		
			}
			catch (SybSQLException e) {
				 log.error("Error :",e); 
				throw e;
			} 
			catch (SQLException e) {
				log.error("Error :",e);
				if(e.getMessage().matches(".*SybConnectionDeadException.*"))
				{
					log.error("Sybase Connection Dead Exception recieved");
					setServerDead(true);
				}
				throw e;
			}
			finally
			{
				con.clearWarnings();
			}
		}
		
		/*
		 * Gets updates from CIC
		 */
		void PollerReader()  throws IOException
		{        
			String rowLabels[] = null;

			int l=0;
			String aline = null;

			while( pollerflag1 && !terminate)
			{        			
				try
				{   /*Checking if Primary server is up*/
					if(currentUrl.equals(dbUrlBackup))		
					{
						log.debug("Checking if Primary server is up");
						if(checkServer(dbUrl))
						{
							log.info("Closing all connecton to " + dbUrlBackup);
							setHostAndPortFromURL(dbUrl);							
							try {
								shutdown(poller_con);
								PoolConnectionManager.closeAllConnections(IDUC_POOL);	

								log.debug("Now Re-creating  connections");
								poller_con = getPollerConnection(dbUrl);
								createConnectionPool(); 	 
							}				    			
							catch(SQLException se)
							{
								log.error("An Error has occurred:",se);								
							} 
						}
						else
							log.error("Primary Server is STILL not avaliable");
					}

					/*if(pollPause != Integer.parseInt(NetcoolConnector.SYS_POLL))
					{
						//Need to flush IDUC early
						log.debug("Flushing in "  + pollPause + " seconds"); 
						Thread.sleep(pollPause * 1000);
						log.debug("Flushing now ....");
						flushIduc(poller_con);							
					}*/
					
					aline = pollerReaderBuf.readLine(); 

					if(aline == null)
					{
						log.debug("Error while reading [" + aline + "]");
						if((System.currentTimeMillis() - leventTime) >= MAX_MILLISECS_OF_UNEXPEXTED_READS)
						{
							setServerDead(true);
							throw new SQLException("No Lines read in " +  (System.currentTimeMillis() - leventTime) + " seconds");

						}
						else
						{
							try {
								Thread.sleep( PAUSE * 1000);
							} catch (InterruptedException e) { 
								e.printStackTrace();
							}
							continue;
						}
						//setPollerflag1(false);
					}   
					else					   
						log.debug("IDUC Ready ..... Line Read:[" + aline + "]");

					if( !terminate && isPollerflag1() && aline != null && aline.indexOf("c:iduc") != -1)
					{
						leventTime = System.currentTimeMillis();
						l=0;//re-initailise unexpected reads to '0' once a valid read has occurred
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


						while(rs_iduc.next()) 
						{
							otype = rs_iduc.getInt(1);
							bit_type = operationType(otype);
							serial = rs_iduc.getInt(2);

							String s =Integer.toString(serial);
							/*
							if(bit_type > 2)
								log.debug("bit_type :" + bit_type);
							else
								log.debug("poller got type: " + otype + " serial: " + serial + " bit: " + bit_type);*/

							if(bit_type == 1 && (op_flags & ACTION_INSERT) == ACTION_INSERT && queryOpen != null)
							{		
								updatev.enqueueWork(s);
							}
							else if(bit_type == 2 && (op_flags & ACTION_UPDATE) == ACTION_UPDATE && queryUpd != null)
							{ 
								insertv.enqueueWork(s);
							}
							else if(bit_type == 8 && (op_flags & ACTION_JOURNAL) == ACTION_JOURNAL && queryJournal != null)
							{ 
								journalv.enqueueWork(s);
							}

							if(bit_type == 4 && (op_flags & ACTION_DELETE)  == ACTION_DELETE)
							{   
								//for(int i1 = 3; i1 <= rowLabels.length; i1++)
								//{
								/*Object delrow = rs_iduc.getObject(i1);
									if(delrow instanceof String)
									{
										deletev.enqueueWork(s);
									}*/
								//log.debug("Deletes are not been sent across for now : serial no: " + s);

								//}
							}        			     
						}      
						rs_iduc.close();
						st_iduc.close();				  			
					} 
					else
					{
						l++;
						log.debug("poller Connection number of unexpected reads were " + l + " in " + (System.currentTimeMillis() - leventTime) + " seconds.");
						if((System.currentTimeMillis() - leventTime) >= MAX_MILLISECS_OF_UNEXPEXTED_READS && l >= MAX_NO_OF_UNEXPEXTED_READS)
						{
							if(!poller_con.isClosed())
							{
								log.debug("Closing Poller connection");
								shutdown(poller_con);
							}
							poller_con = getPollerConnection(currentUrl);
						}
					}
					aline = null;
				}
				catch(SocketException se)
				{
					log.debug("[SE]Timeout during read" ); 
				}
				catch(SQLException sse)
				{
					urlTester(currentUrl, sse);	 
				}
				catch(SybaseManagerException sse)
				{
					log.debug("SybaseManagerException",sse );	 
				}
				catch(InterruptedIOException interruptedioexception)
				{
					log.debug("[IE]Timeout during read" );
					try {
						if(pollPause != Integer.parseInt(NetcoolConnector.SYS_POLL))
						{
							//Need to flush IDUC early
							log.debug("Flushing in "  + pollPause + " seconds"); 
							Thread.sleep(pollPause * 1000);
							log.debug("Flushing now ....");
							flushIduc(poller_con);							
						}
					} catch (InterruptedException e) {
						log.debug("InterruptedException",e );
					} catch (SQLException e) {
						log.error("SQLException",e );
						urlTester(currentUrl, null);
					}
				}
				catch(Exception e)
				{
					log.error("Unknown Exception thrown",e);        				
					urlTester(currentUrl, null);	 
				}
			}
			log.warn("poller Connection to " + poller_con + " lost.");
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
		
		public boolean urlTester(String cUrl,SQLException sse)
		{
			boolean isConn= false; 
			
			int cnt=0;
			if( sse != null)
			{
				//log.debug("[SybSQLException]Sybase SQL Exception",sse ); 
				log.debug ("Error:   " + sse.getErrorCode ()+ "\n");
				log.debug ("Message:  " + sse.getMessage () + "\n");
//				Send Critical Alarm to CIC
				String strMsg=sse.getMessage();
				log.error(strMsg);
				/*synchronized (socketProbePlugin.getSocketProbe()) {
					Event event = socketProbePlugin.createEvent("Poller", MonitorTask.CLASS,   strMsg);
					try {
						SocketProbeHelper.raiseEvent(socketProbePlugin.getSocketProbe(),event);
					} catch (SocketProbeHelperException e1) {
						// TODO Auto-generated catch block
						log.error("Socket Error",e1); 
					}
				}	*/			 
			}

			do
			{
				isConn = checkServer(currentUrl);
			}while(++cnt < 3 && !isConn);

			log.error("isServerDead " + isServerDead + " isConn " + isConn); 
			while(!isConn)
			{
				log.error("ObjectServer is dead; might need to try another Server"); 
				if(currentUrl.equals(dbUrl))
				{
					log.info("Checking if Secondary ObjectServer is running");
					if(checkServer(dbUrlBackup))
					{
						log.info("Connecting to Secondary ObjectServer");
						setHostAndPortFromURL(dbUrlBackup);
						isConn = true;
					} 
				}
				else
				{
					log.info("Checking if Primary ObjectServer is running");
					if(checkServer(dbUrl))
					{
						log.info("Connecting to Primary ObjectServer");
						setHostAndPortFromURL(dbUrl);
						isConn = true;
					} 
				} 
				if(!isConn)
				{	
					log.error("It seems both server are down");
					try {
						Thread.sleep( PAUSE * 1000);
					} catch (InterruptedException e) {
						log.error("InterruptedException:",e);
					}
					continue;
				}	
				 
			}
			log.error("isServerDead " + isServerDead + " isConn " + isConn);  			
			if(isServerDead)
			{
				try {
					if(!poller_con.isClosed())
					{
						log.debug("Closing Poller connection");
						shutdown(poller_con);
					}

					PoolConnectionManager.closeAllConnections(IDUC_POOL);  

					log.debug("[SybSQLException] Re-creating poller connection");	
					poller_con = getPollerConnection(currentUrl);	
					log.debug("[SybSQLException] Re-creating JDBC Pool");
					createConnectionPool();
					isServerDead=false;	
				} catch (Exception e) {
					log.error("Error:",e);
				}  					 	
		    }	 
			return isConn;
		}
		/*
		 * Get poller information
		 */
		private boolean getPoller(SybConnection poller_con)  
		{
			log.debug("Subscribing to poller");
			boolean canPoll=false;
			try
			{
				Statement statement = poller_con.createStatement();
				
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
			}
			catch(Exception e)
			{
				log.error("Subsciption failed for poller connection: " ,e); 
			}           
			
			log.debug("Subscribe done");
			return canPoll;       
		}
	}
	
	 
	/**
	 * 
	 * @author aademij1
	 *
	 * Thread that sends event into CIC
	 */
	/**
	 * @author aademij1
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
	class SybaseInserter implements Runnable
	{
		private boolean useSp;
		private String procNm="O2GatewayTTFlagUpdate";
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		SybaseInserter()
		{
			try {				
				//isSP 
				setUseSp(checkForSP()); 
			} catch (SQLException e) {
				log.error("SQLERROR: ",e);
				shutdown();
				try {
					mgr.shutdown();
				} catch (PluginException e1) {
					log.error("PluginException: ",e1);					
				}
			}
		}
		
		   /**
	     * Process a ResultSet displaying all of the rows and columns.  Also
	     * use ResultSetMetaData to obtain the column headers<br>
	     * @param rs  the current result set<p>
	     * @exception SQLException .
	     */
	    public void dispResultSet (ResultSet rs) throws SQLException
	    {
	        int i;

	        // Get the ResultSetMetaData.  This will be used for the column headings

	        ResultSetMetaData rsmd = rs.getMetaData ();

	        //  Get the number of columns in the result set

	        int numCols = rsmd.getColumnCount ();

	        // Display column headings

	        for (i=1; i<=numCols; i++) 
	        {
	            if (i > 1)  
	            	log.debug("\t\t");
	            log.debug(rsmd.getColumnLabel(i));
	        }
	        log.debug("\n");

	        // Display data, fetching until end of the result set

	        while (rs.next ()) 
	        {
	            // Loop through each column, getting the column data and displaying
	            for (i=1; i<=numCols; i++) 
	            {
	                if (i > 1) 
	                	log.debug("\t\t");

	                String foobar = rs.getString(i);
	                if(rs.wasNull())
	                	log.debug("NULL");
	                else
	                	log.debug(foobar);
	            }
	            log.debug("\n");

	            // Fetch the next result set row

	        }
	    }
		/**
		 * Checks if the Required Stored Procedure is implemented on the ObjectServer.
		 * And can be used by the Gateway 
		 * @return
		 * @throws SQLException
		 */
		public boolean checkForSP() throws SQLException
		{	
			boolean st=false;
			String tquery = "SELECT * from persist.procedures where ProcedureName = '" + procNm +"';";
			
			ResultSet p_rs = null;	
			PreparedStatement pstmt;
			SybConnection con=null;
			try {
				con= (SybConnection)PoolConnectionManager.requestConnection(IDUC_POOL);
				pstmt = con.prepareStatement(tquery);
				//sp_procxmode <procedure_name>,"anymode" 
				 
				//sender_con.createStatement().execute("sp_procxmode " + procNm + ",\"anymode\"");
				//dispResultSet(sender_con.createStatement().executeQuery("sp_procxmode "));
				p_rs = pstmt.executeQuery ();
				if(p_rs.next())
				{
					/*DatabaseMetaData dmet = sender_con.getMetaData();
					log.debug("dmet.getCatalogTerm() : " +  dmet.getCatalogTerm());
					dispResultSet(dmet.getProcedures(null,null,procNm));*/
					log.debug("Stored Procedure exist, READER will Use that ....");
					st=true;
				}
				else
					log.debug("Using SQL Statements");
				if(p_rs != null)
					p_rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("Error caught in checkForSp",e); 
				throw new SQLException(e.getMessage());			
			}	
			finally
			{
				PoolConnectionManager.returnConnection(IDUC_POOL,con);
				p_rs=null;
			}
			return st;
		}
		public void run() {
			// TODO Auto-generated method stub
			/*
			 * Use Transactions HERE ?
			 */
			try
			{	
				WorkQueue readerQ = SybaseManager.getSybaseManager(mgr).readerQueue; 
				threadsStarted++;
				while(!terminate)
				{			 
					log.debug("Waiting for Work ......");
					NetcoolGatewayNetcoolEvent  nevent = (NetcoolGatewayNetcoolEvent)readerQ.dequeueWork();	
					log.debug("Recieved Work from a Client ......");
					SybConnection con=null;
					boolean isSuccess=false;
					try {
						con= (SybConnection)PoolConnectionManager.requestConnection(IDUC_POOL); 
						if(con !=null)
							isSuccess = (isUseSp())? CallUpdate(nevent, con):unpackEvent(nevent, con);	
					}
					catch(SybSQLException se)
					{
						log.error("An Error has occurred during insertion into the ObjectServer",se);				    		 
					}
					catch(Exception e)
					{
						log.error("some other kind of error:",e);
					}
					finally
					{ 
						if(!isSuccess)
						{
						 log.debug("Putting it back again..") ;
					 	 readerQ.enqueueWorkFirst(nevent);
						}
						PoolConnectionManager.returnConnection(IDUC_POOL,con);						 
					}													
				}				
			}
			catch(SybaseManagerException se)
			{
				log.error("An Error has occurred:",se); 
				terminateThread();
			}	    	
			catch(Exception e)
			{
				log.error("An Error has occurred: " + e.getMessage()); 
			}
		}
		/**
		 * @return Returns the useSp.
		 */
		public boolean isUseSp() {
			return useSp;
		}
		/**
		 * @param useSp The useSp to set.
		 */
		public void setUseSp(boolean useSp) {
			this.useSp = useSp;
		}
				
		/**
		 * unpacks event and sent to CIC
		 * @param con  
		 * @param boolean
		 * @return
		 */
		private boolean unpackEvent(NetcoolGatewayNetcoolEvent netcoolevent, SybConnection con )
		{
			String alertupdateStmt = "update alerts.status set ";
			StringBuffer updateFld = new StringBuffer(alertupdateStmt);
			try {		 
				NetcoolGatewayNVPair item [] = netcoolevent.getANVPairList().getItem() ;
				for(int i=1; i < item.length; i++) 
				{ 
					//update alerts.status set Gateway = 2  where Serial in (9,16,19,46,2251,2270,7916);
					String aname=item[i].getAName();
					if(aname.equals(CIC_DUMMY_INTEGER_COLUMN_NAME_DEF) || aname.equals(CIC_DUMMY_STRING_COLUMN_NAME_DEF))
						continue;
					//To put a quotation mark back in you will need four backslashes to get one to appear in the output,
					//plus one more to escape the quotation mark. 
					if(COLUMNDESP != null &&((String)COLUMNDESP.get(aname)).endsWith("String"))
					{
						if(item[i].getAValue().matches(".*'.*"))
						{
							log.debug("Quoted String exist in this value");
							log.debug("Replaced :" + item[0].getAValue().replaceAll("\'","\\\\\'"));
						}
						
						updateFld.append(aname).append(SQLEQUAL).append(SQLSQUOTE).append(item[i].getAValue().replaceAll("\'","\\\\\'")).append(SQLSQUOTE);
					}
					else
					{
						updateFld.append(aname).append(SQLEQUAL).append(item[i].getAValue());
					}
					if(i < item.length - 1)
						updateFld.append(SQLSEP);				  
				}
				//log.debug("updateFld=" + updateFld);
				int f =  updateFld.toString().trim().length()- 1;
				//log.debug("f=" + f + "updateFld.charAt(f)=" + updateFld.charAt(f) + "SQLSEP=" + SQLSEP.trim().charAt(0));
				if(updateFld.charAt(f) ==  SQLSEP.trim().charAt(0))
				{
					updateFld.deleteCharAt(f);
				}
				//?HUH ...........
				
				updateFld.append(SQLWHERE).append(item[0].getAName())
				.append(SQLEQUAL).append(SQLSQUOTE).append(item[0].getAValue().replaceAll("\'","\\\\\'"))
				.append(SQLSQUOTE).append(SQLEND);	
				log.debug("Sending Statment: " + updateFld);
				if(updateFld != null)
				{					
					 PreparedStatement stmt = con.prepareStatement(updateFld.toString());
					try {
						stmt.executeUpdate();
						if(stmt != null)
							stmt.close();
						stmt=null;
					}
					catch(SybSQLException se)
					{
						log.error("An Error has occurred during insertion into the ObjectServer",se);				    		 
					}
					
				}	
				
			}
			catch (Exception e) {
				log.error("An Error occurred ",e);
				e.printStackTrace();
			}
			return true;
			//return updateFld.toString();
		}
		

		/**
		 * @param dbConnection
		 * @param statement
		 * @return
		 * @throws java.sql.SQLException
		 */
		private CallableStatement getCallableStmt(Connection dbConnection,String statement) throws java.sql.SQLException
		{
			CallableStatement lstmt=null;
			if(dbConnection != null)
				lstmt = dbConnection.prepareCall(statement);
			return lstmt;		
		}
		
		/**
		 * @param netcoolevent
		 * @param con 
		 * @return boolean
		 * @throws java.sql.SQLException
		 */
		public boolean CallUpdate(NetcoolGatewayNetcoolEvent netcoolevent, SybConnection con) throws java.sql.SQLException
		{
			// set the in param
			/*
			 * ttflag Integer
			 * faultimpact char 50
			 * faultpriority char 50
			 * troubleticket char 100
			 * poll integer
			 * ttnote char 255
			 * identifier char 255
			 * faultqueue char 50
			 * faultowner char 40
			 * faultstatus char 50
			 * urgency char 20
			 */
			NetcoolGatewayNVPair item [] = netcoolevent.getANVPairList().getItem() ;
			// Start of Changes for OSC-1434952
			String cicFields[] = { "TTFlag", "FaultImpact", "FaultPriority", "TroubleTicket", 
								   "Poll", "TTNote", "Identifier", "FaultQueue", 
								   "FaultOwner", "FaultStatus", "Urgency" };
			// End of changes for OSC-1434952
			String fndFields [] = new String [cicFields.length];

			//CallableStatement cstmt = getCallableStmt(sender_con,"call " + procNm + " (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			String iname=null;
			String ival=null; 
			for(int i=0; i < netcoolevent.getANVPairList().getItem().length; i++)
			{
				iname = item[i].getAName();
				//log.debug("item name:" + iname);
				ival=item[i].getAValue(); 
				for(int j=0; j < cicFields.length; j++)
				{
					if(iname != null && iname.equals(cicFields[j]))
					{		
						log.debug("SQL Field[" + (j+1) + "]" + ":" + iname + "@" + ival + "@" + cicFields[j]);
						if(ival.matches(".*'.*"))
						{
							log.debug("Quoted String exist in this value");
							ival = ival.replaceAll("\'","\\\\\'");
							log.debug("Replaced :" + ival);
						}
						fndFields [j] = ival;	
						break;
					}
				}
			} 
			StringBuffer sp_parameter=new StringBuffer();

			for(int i=0; i < fndFields.length;i++)
			{ 				
				if(i != 0 && i != 4)
					sp_parameter.append('\'');
				// Start of Changes for OSC 1434952
				if(fndFields [i] != null && fndFields [i].trim().length() != 0)
				// End of Changes for OSC 1434952	
				{
					//cstmt.setObject((i+1), ival); 
					sp_parameter.append(fndFields[i]);					
				}
				else
				{
					//cstmt.setString((i+1), null); 
					sp_parameter.append("0");					
				}
				
				if(i != 0 && i != 4)
					sp_parameter.append("\'");
				sp_parameter.append(",");
			}
			sp_parameter.deleteCharAt(sp_parameter.length()- 1);
			log.debug("sp_parameter:" + sp_parameter);
			
			CallableStatement cstmt = null; 
			boolean procExecSuccess = false;
			int maxNumberRetries = 3;
			int numberAttempts = 1;
			int retryTimeInterval = 1500; //milliseconds
			
			do{
				try {				 
					cstmt = getCallableStmt(con,"call " + procNm + " (" + sp_parameter +")");				
					cstmt.execute(); 
					
					procExecSuccess = true;
				}
				catch(SybSQLException se)
				{
					log.error("An Error has occurred during insertion into the ObjectServer...attempt-"+String.valueOf(numberAttempts)+":",se);
					
					try {
						Thread.sleep(retryTimeInterval);
					} catch (InterruptedException e_t) {
						log.warn("The thread cant sleep...attempt-"+String.valueOf(numberAttempts)+":", e_t);
					}
					numberAttempts++;
				}
				catch(Exception e)
				{
					log.error("some other kind of error...attempt-"+String.valueOf(numberAttempts)+":",e);
					
					try {
						Thread.sleep(retryTimeInterval);
					} catch (InterruptedException e_t) {
						log.warn("The thread cant sleep...attempt-"+String.valueOf(numberAttempts)+":", e_t);
					}
					numberAttempts++;
				}
				finally
				{		 
					if(cstmt != null)
					{
						try{
							cstmt.close();
						}catch(Exception exc_cl){
							log.warn("An error has occurred during closing the CallableStatement object...attempt-"+String.valueOf(numberAttempts)+":", exc_cl);
						}
					}
					cstmt=null;		 
				}
			}while(!procExecSuccess && numberAttempts <= maxNumberRetries);
			
			return true;
		}
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
	 *  validate filters
	 *
	 */
	private boolean TestFiltersSyntax() throws SybaseManagerException
	{ 
		boolean bsyn=true;
		SybConnection con=null;
		Hashtable filters = mgr.getFilters();
		String exMsg = null;
		if(filters == null)
		{
			log.warn("No Filters Appenders to test");
			return bsyn;
		}
		Enumeration e2 = filters.keys();
		while (e2.hasMoreElements())
		{
			try {
				String filtername = (String)e2.nextElement();
				if(filtername == null)
				{
					exMsg="Can't create a blank filter set; Please provide a filtername and Script or port script";
					log.error(exMsg);
					return false;
				}			
				Hashtable hh =((Hashtable)filters.get(filtername));
				if(hh == null)
				{
					exMsg="No Script/Port property set for filter '" + filtername + "'. Please provide or remove entry entirely";
					log.warn(exMsg);
					return false;
				}
				String filterScript =  prepareSQLStmts((String)hh.get("script"));
				if(filterScript == null)
				{
					exMsg="No Script property set for filter '" + filtername+ "'. Please provide or remove entry entirely";
					log.warn(exMsg);
					return false;
				}
				Statement  statement1;
				
				con = (SybConnection)PoolConnectionManager.requestConnection(IDUC_POOL);/*reconnectToSybase()*/;
				statement1 = con.createStatement();
				statement1.executeQuery("select TOP 1 * from alerts.status where " + filterScript +";" );
				statement1.close();
				statement1=null;
			}
			catch (SQLException e){
				bsyn=false;
				try {
					logExceptionMesg(e);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				log.error("SQL Error:",e);			 
				if(exMsg !=null)
					throw new SybaseManagerException(exMsg,e);
				else
					throw new SybaseManagerException(e);
			}
			finally
			{
				PoolConnectionManager.returnConnection(IDUC_POOL,con);
			}
		}
		return bsyn;
	}
	
	public void GetColumnName()
	{ 
		SybConnection con=null;
		ResultSet resultset1 = null;
		Statement  statement1=null;
		try {
			con = (SybConnection)PoolConnectionManager.requestConnection(IDUC_POOL);/*reconnectToSybase()*/;
			statement1 = con.createStatement();
			
			resultset1 = statement1.executeQuery("select TOP 1 * from alerts.status;");
			if(resultset1.next())
			{
				if(COLUMNDESP == null)
				{
					log.debug(" ********* Get ColunmTypes ********* ");
					ResultSetMetaData resultsetmetadata1 = resultset1.getMetaData();
					
					int i2 = resultsetmetadata1.getColumnCount();
					
					COLUMNDESP = new Hashtable(i2);
					for(int j2 = 1; j2 <= i2; j2++)
					{	//log.debug("getColumnName:" + resultsetmetadata1.getColumnClassName(j2));
						//resultsetmetadata1.getColumnDisplaySize(j2);
						COLUMNDESP.put(resultsetmetadata1.getColumnName(j2),resultsetmetadata1.getColumnClassName(j2));						 
					}
					log.debug(" ********* Retrieved ColunmTypes ********* ");
				}
			}
			if(resultset1 != null)
				resultset1.close();
			if(statement1 != null)
				statement1.close();	 
		}
		catch (SQLException e){
			log.error("SQL Error: : ",e);
			try {
				logExceptionMesg(e);
			} catch (SQLException e1) {
				log.error("SQL Error; ",e);
			}
			e.printStackTrace();
		}
		finally
		{			 
			PoolConnectionManager.returnConnection(IDUC_POOL,con);	
			statement1=null;
			resultset1=null;
		}
	}
	/**
	 * Class constructor
	 * @param mgr
	 * @throws SybaseManagerException
	 */
	public SybaseManager(PluginManager mgr) 
		throws SybaseManagerException
	{	
		try {
			this.mgr=mgr;
			mgr.getSimpleDateFormat(NetcoolConnector.CIC_DATE_FORMAT);
			dbUrl = mgr.getString(NetcoolConnector.SYBASE_JDBC_URL); 
			dbUrlBackup = mgr.getString(NetcoolConnector.SYBASE_JDBC_URL_BKUP);
			dbUsername = mgr.getString(NetcoolConnector.SYBASE_JDBC_LOGIN);
			dbPassword = mgr.getString(NetcoolConnector.SYBASE_JDBC_PASSWORD);
			//threadPoolSize = mgr.getInt(NetcoolConnector.THREAD_POOL_SIZE);
			
			Properties props= new Properties(); 
			props.put( "password",  dbPassword );
			props.put( "user", dbUsername );
			props.put( "APPLICATIONNAME" , APPNAME ); 
			 
			if(checkServer(dbUrl))
			{
				log.info("Connecting to Primary ObjectServer");
				setHostAndPortFromURL(dbUrl);
			}
			else if(checkServer(dbUrlBackup))
			{ 				 
				log.info("Can't connect to Primary ObjectServer, Connecting to Secondary ObjectServer");	
				setHostAndPortFromURL(dbUrlBackup);
			} 
			else
			{
				log.error("Can't connect to either Primary/Backup ObjectServers");	
				throw new SybaseManagerException("Can't connect to either Primary/Backup ObjectServers");				
			}
			if(!PoolConnectionManager.isValid(IDUC_POOL)) 
			{
				try
				{
					log.info("Creating a pool of JDBC connections to Sybase ...");
					PoolConnectionManager.createPool(
							IDUC_POOL, JDBC_DRIVER,currentUrl,dbUsername,dbPassword,props);
				}
				catch(SQLException ex)
				{
					try {
						logExceptionMesg(ex);
					} catch (SQLException e1) {
						log.error("Error in creating the pool manager: ",e1); 
					} 
				}

			}
 			
			maps = mgr.getMaps();
			iducinfos = new IDUCInfo[maps.size()];
			Enumeration e = maps.keys();
					
			StringBuffer val =null;
			
			GetColumnName();
			
			log.debug(" ********* S T A R T Of Parsing Map Name ********* ");
			int mapCnt=0;
			while(!terminate && e.hasMoreElements())
			{
				String mapname = (String)e.nextElement();
				log.debug("mapname=" + mapname);
				Hashtable ht =((Hashtable)maps.get(mapname));
				Collection cc = ht.values();
				
				Iterator ccIter = cc.iterator();
				val=new StringBuffer("");
				while(ccIter.hasNext())
				{
					val.append((String)ccIter.next());
					if(ccIter.hasNext())
						val.append(SQLSEP);
					else
						val.append(SQLFROM);
					
				}				
				if(mapname.equals(JOURNAL))
					val.append(" alerts.journal ");
				else
					val.append(" alerts.status "); 
				
				if(mapname.equals(OPEN))
				{
					op_flags = op_flags | ACTION_INSERT;
					queryOpen = new String(SQLSELECT + val);
					iducinfos[mapCnt] = new IDUCInfo("Sybase INSERT Thread","INSERT",ACTION_INSERT,queryOpen,insertv);
					//log.debug("Executing: " + queryOpen + "\n");
				}  
				else if(mapname.equals(UPDATE))
				{
					op_flags = op_flags | ACTION_UPDATE;
					queryUpd = new String(SQLSELECT + val);
					iducinfos[mapCnt] = new IDUCInfo("Sybase UPDATE Thread","UPDATE",ACTION_UPDATE,queryUpd,updatev);
					//log.debug("Executing: " + queryUpd + "\n");
				}
				else if(mapname.equals(CLOSE))
				{
					op_flags = op_flags | ACTION_DELETE;
					queryClose = new String(SQLSELECT + val);
					iducinfos[mapCnt] = new IDUCInfo("Sybase DELETE Thread","DELETE",ACTION_DELETE,queryClose,deletev);
					log.info("Deletes are not been sent across for now");
				}
				else if(mapname.equals(JOURNAL))
				{
					op_flags = op_flags | ACTION_JOURNAL;
					queryJournal = new String(SQLSELECT + val);
					iducinfos[mapCnt] = new IDUCInfo("Sybase JOURNAL Thread","JOURNAL",ACTION_JOURNAL,queryJournal,journalv);
					//log.debug("Executing: " + queryJournal + "\n");
				}			
				mapCnt++;
			}
			//log.debug("op_flags: " + op_flags + "\n");
			log.debug(" ********* E N D Of Parsing Map Name ********* ");
			
		} catch (PluginException e) {
			// TODO Auto-generated catch block
			log.error("Java Error Occurred ",e); 
		}		 	    
	}
	
	/**
	 * Stops Reader Threads
	 *
	 */
	public void stopReaderThreads()
	{
		if(InsThr != null && InsThr.getThreadGroup() != null) 
			InsThr.getThreadGroup().interrupt();
		  
		try {
			PoolConnectionManager.closeAllConnections(IDUC_POOL);
		} 
		catch (SQLException e) {
			 
		}
		
	}
	
	/**
	 * Stops Writer threads
	 *
	 */
	public void stopWriterThreads()
	{		 
		if(iducThreadpool != null)
		{
			log.info("Shutting down thread pool.");
			iducThreadpool.terminateAll(); 
		}
		else if(PollThr != null)
			PollThr.getThreadGroup().interrupt();
		
		shutdown(poller_con); 
		try {
			PoolConnectionManager.closeAllConnections(IDUC_POOL);
		} catch (SQLException e) {
			 
		}
	}
	/**
	 * Start Reader threads
	 *
	 */
	public void startReaderThreads()
	{
		if(InsThr == null || InsThr != null && !InsThr.isAlive())
		{
			InsThr = (new Thread(sysbaseThreadGroup,new SybaseInserter(), UNAME));
	        InsThr.start();
		}
	}
	
	/**
	 * Starts writer threads
	 * @throws 
	 * @throws SybaseManagerException
	 */
	public void startWriterThreads() throws SybaseManagerException
	{ 
		log.debug(" ********* Checking Filter Format ********* ");
		if(!TestFiltersSyntax())
		{
			log.error("Filter syntax error please check format");
			throw new SybaseManagerException("Filter syntax error please check format");
		}
		log.debug(" ********* Filter Format Valid ********* ");
		if(iducThreadpool == null)
		{
			iducThreadpool = new IDCUThreadPool("IDCUThreadPool", iducinfos,mgr,writerQueue);
			iducThreadpool.start();
		}
		if(PollThr == null || PollThr != null &&  !PollThr.isAlive())
		{
			PollThr = (new Thread(sysbaseThreadGroup,new SybasePoller (), PNAME));
			PollThr.start();
		}
		
	}
 
	/**
	 * Gathers threads status information
	 */
	public static String monitorME() throws Exception
	{     
		StringBuffer str= null;
		if(Thread.activeCount() <= 0)
			str = new StringBuffer("No SybaseManager threads are active");
		else if(sysbaseThreadGroup.activeCount() < threadsStarted )
		{        	
			str= new StringBuffer((threadsStarted - sysbaseThreadGroup.activeCount()) + " SybaseManager threads have died");
			sysbaseThreadGroup.list();
			/*if(RecvThr != null && !RecvThr.isAlive())
			 str.append(" : " + RecvThr.getName() + " is dead");*/
			if(InsThr != null && !InsThr.isAlive())
				str.append(" : " + InsThr.getName() + " is dead");
			if(PollThr != null && !PollThr.isAlive())
				str.append(" : " + PollThr.getName() + " is dead");
		}
		else 
			str= new StringBuffer("All " + sybaseThrGrpStr + " are Alive");
		
		if(iducThreadpool == null )
		{
			log.debug("Iduc Thread Pool is not active Yet");
			str.append(" : Iduc Thread Pool is not active Yet");
		}
		else 
		{    
			StringBuffer strTmp=new StringBuffer();
			if(iducThreadpool.activeCount() < iducthreadsStarted )
			{ 
				strTmp.append(iducthreadsStarted - iducThreadpool.activeCount()).append(" Iduc Threads have died");
				iducThreadpool.list();        	
			}	
			strTmp.append(iducThreadpool.monitorAll());
			log.debug(strTmp.toString());
			str.append(strTmp);
		}
		return str.toString();
		
	}
//	Create a pattern to match map 
Pattern	serverMap = Pattern.compile("(jdbc\\:)(sybase\\:)(Tds\\:)(.*)\\:([0-9]*)\\/([a-zA-Z]*)");
private int serverPort=-1; 
private String serverHost = null;
private String serverName = null; 
	 
	
	//	jdbc:sybase:Tds:isisdev:4100/CIS_ISIS
	private void setHostAndPortFromURL(String url)
	{
		currentUrl=url;
		Matcher m = serverMap.matcher(url); 
	    if(m.find())
	    {
	    	serverHost = m.group(4);
	    	serverPort = Integer.parseInt(m.group(5));
	    	serverName = m.group(6);
	    	log.debug("serverHost:" + serverHost + " serverPort:" + serverPort + " serverName:" + serverName);
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
		 
	public boolean checkServer(String url)  
	{
		boolean isConn= false;	 
		Matcher m = serverMap.matcher(url); 
		if(m.find())
		{
			String sHost = m.group(4);
			int sPort = Integer.parseInt(m.group(5));
			String sName = m.group(6);
			
			Socket socket;
			try {
				log.debug("[" + url + "]Checking " + sName  + " on " + sHost + ":" + sPort);
				socket = new Socket(sHost, sPort);
				isConn = true; 
				socket.close();		
				log.info("[" + url + "]Connection to " + sName  + " on " + sHost + ":" + sPort + " is made!!! ");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				log.error("Don't know about host: " + sHost + ".");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Couldn't get I/O for the connection to: " + sHost+ ".");  
			}	
			 catch (Exception e) {
					// TODO Auto-generated catch block
					log.error("Error: " ,e);  
			}	
		}
		return isConn;
	}
	
	private void createConnectionPool() throws SQLException
	{
		PoolConnectionManager.removeConnectionManager(IDUC_POOL);
			PoolConnectionManager.createPool(
					IDUC_POOL, JDBC_DRIVER,currentUrl,dbUsername,dbPassword,null);		 
	}

	/**
	 * Overloaded method
	 * Get a Sybase connection for Poller Only
	 * @param cUrl TODO
	 * @see reconnectToSybase()
	 */
	public SybConnection  reconnectToSybase(Properties props, String cUrl) throws SybaseManagerException
    {
    
	    SybConnection con=null;
    	log.info(NAME + ": Opening sybase connection to " + cUrl); 
//		jdbc:sybase:Tds:isisdev:4100/CIS_ISIS
		 
			
		try {
			
			DriverManager.registerDriver((Driver)
	                Class.forName(JDBC_DRIVER).newInstance());
	        
	    	DriverManager.setLoginTimeout(30);
	    	con= null;
	    	try
			{	    		
	    		con = (SybConnection)DriverManager.getConnection(cUrl,props);	    	
			}
	    	catch (SQLException ex) 
			{   
	    		try {
	    			logExceptionMesg(ex);
	    		} catch (SQLException e1) {
	    			log.error("ERROR : ",e1);
	    			e1.printStackTrace();
	    		}
	    		if (con != null && !con.isClosed())
	    		{
	    			con.close();
	    			con.clearWarnings();
	    		}	    		
			}
	    
	    	if(con == null)
	        {
	    		//log.info(NAME + " :1. Connection is null");
	    		throw new SybaseManagerException("Connection could not be made; Check if Server is dead !!!");
	        }
			log.info(NAME + ":Openned connection successfully.");
			
				 
		}
		catch (NumberFormatException e1) {
			// TODO Auto-generated catch block#
			log.error("cannot parse port number",e1); 
			
		}
		catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			log.error("Class Not Found",e1); 
		}
		catch (Exception e) {
				String msg = "Caught exception while trying to open connection to Sybase database.";
				log.error(msg,e);
				throw new SybaseManagerException(msg);
		}
		finally
		{				
		}
		return con; 
    }
	
	/**
	 * Get a sybase Manager Object
	 * @param mgr
	 * @return
	 * @throws SybaseManagerException
	 */
	synchronized public static SybaseManager getSybaseManager(PluginManager mgr
	   ) throws SybaseManagerException
	   {
	       
          if (sybaseMgr == null )
	      {
	         log.debug(NAME + ": create new Sybase connection ");
	         sybaseMgr = new SybaseManager(mgr);
	      }
	      
	      if (sybaseMgr == null)
	      {
	         log.error(NAME + " : connection not be established correctly");
	         throw new SybaseManagerException(NAME + ": Connection not be established correctly");
	      }	      
	      return sybaseMgr;
	   }
	
	/**
	 *  (non-Javadoc)
	 * @see com.o2.techm.netcool.eai.o2gateway.NetcoolConnector#shutdown()
	 */
	public void shutdown()
	{ 
		log.debug("Interrupting "  + sybaseThrGrpStr + " Thread ...");
		setPollerflag1(false);		
		stopReaderThreads();
		stopWriterThreads();		
	}
	/**
	 * Closes all the sybase connections
	 */
    private void shutdown(Connection con)
	{		
	        log.info("Shutting down connection to Sybase.");
	        try {
				if (con != null && !con.isClosed())
				{
					try {
						con.close();
						con.clearWarnings();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						log.error("Shutdown error:",e);
						try {
							logExceptionMesg(e);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	        con= null;			 
	}
     	
	private static final Hashtable SEV_VALUES= new  Hashtable();
	private static final Hashtable USER_VALUES= new  Hashtable();
	public static final String SERVER_SERIAL = "ServerSerial";
	public static final String TTFLAG = "TTFlag";
	public static final String IDENTIFIER = "Identifier";	
		
	static
	{
		SEV_VALUES.put("5","Critical");
		SEV_VALUES.put("4","Major");
		SEV_VALUES.put("3","Minor");
		SEV_VALUES.put("2","Warning");
		SEV_VALUES.put("1","Indeterminate");
		SEV_VALUES.put("0","Clear");
	}
	/**
	 * @return Returns the String representation of the OwnerUID.
	 */
	public   String getUSER_VALUESStringDesp(String strVal) {
		if(strVal.equals(DEFAULT_UID)){
			return DEFAULT_USER;
		}
		else{
			return (String)USER_VALUES.get(strVal);
		}
	}
	
	/**
	 * @return Returns the String representation of the Serverity
	 */
	public String getSeverityStringDesp(String strVal)
	{
		return (String)SEV_VALUES.get(strVal);
	}
	/**
	 * Connects to the ObjectServer and get all user id information
	 * @return
	 */
	boolean ResynchUserTable()
	{
		SybConnection con=null;
		boolean status=true;
		PreparedStatement stmt = null;	
		ResultSet rs = null;
		try {
			//con = reconnectToSybase();
			con = (SybConnection)PoolConnectionManager.requestConnection(IDUC_POOL);			
			String squery="select Value,Conversion from alerts.conversions where Colname = 'OwnerUID';";		 
			stmt = con.prepareStatement(squery);
			
			if(stmt !=null)
			{				
				rs = stmt.executeQuery ();				
				while (rs.next ()) 
				{
					
					if(!USER_VALUES.containsKey(rs.getString(1)) || 
							(USER_VALUES.containsKey(rs.getString(1)) && 
									(!((String)USER_VALUES.get(rs.getString(1))).equals(rs.getString(2)))))
									USER_VALUES.put(rs.getString(1).trim(),rs.getString(2).trim());
				}
			}
			log.debug("Connection returned Current user converstion map = " + USER_VALUES.toString());
			log.debug("There are now " + USER_VALUES.size() + " entries in the userid pool");
			if(stmt != null)
				stmt.close();				
			if(rs != null)
				rs.close();	
		} 
		catch (SQLException e) {
			log.error("SQL Exception: " ,e);
			try {
				logExceptionMesg(e);
			} catch (SQLException e1) {
				log.error("SQL Exception: " ,e);
			}
			log.error("SQL Exception: " ,e);
			status=false;
		
		}
		finally
		{
			PoolConnectionManager.returnConnection(IDUC_POOL, con);			
		}
    	return status;
    }
    /**
     * Checks for and displays warnings.  Returns true if a warning
     * was found<pollerBuf>
     * @param ex SQLWarning object
     * @exception SQLException .<p>
     */
    public boolean logExceptionMesg (SQLException ex) throws SQLException
    {
        boolean rc = false;

        // If a SQLWarning object was given, display the warning messages.  
        // Note that there could be multiple warnings chained together

        while (ex != null) 
        {
            rc = true;
            if (ex != null) 
            {
            	log.debug(NAME + "Warning SQLState: " + ex.getSQLState () + "Message:  " + ex.getMessage () + "Vendor:   " + ex.getErrorCode () );
                ex = ex.getNextException();
            }
        }
        return rc;
    }   
    
    public void execDDL(Connection con,String cmd) throws SQLException
    {
        Statement stmt = con.createStatement();
        log.error("Executing: " + cmd +"\n");
        stmt.executeUpdate(cmd);
        logExceptionMesg(stmt.getWarnings());
        stmt.close();
    }    	
	
	/**
	 * @return Returns the readerQueue.
	 */
	public WorkQueue getReaderQueue() {
		return readerQueue;
	}
	/**
	 * @param readerQueue The readerQueue to set.
	 */
	public void setReaderQueue(WorkQueue readerQueue) {
		this.readerQueue = readerQueue;
	}
	/**
	 * @return Returns the writerQueue.
	 */
	public WorkQueue getWriterQueue() {
		return writerQueue;
	}
	/**
	 * @param writerQueue The writerQueue to set.
	 */
	public void setWriterQueue(WorkQueue writerQueue) {
		this.writerQueue = writerQueue;
	}
	
	/*
	 * Not implemented Yet
	 * This function is create a preparsed and compiled SQL stmt
	 */
	public String prepareSQLStmts(String filterScript) throws SybaseManagerException 
	{
		if(filterScript == null)
			throw new  SybaseManagerException("Filter script does not exist");
		//StringBuffer preparedString = new StringBuffer(filterScript);
		// '(( Gateway =  1 ) AND (( TTFlag not in  ( 764, 767 )  ) AND ( NOT(( Class =  7037 ) AND(( Poll < 100 ) AND ( AlertKey =  \'ALARMS\' ))))));'	
		// '((( Gateway =  1 ) AND ( Class in  ( 7037, 8767 )  )) AND ( TTFlag in  ( 764, 767 )  ))'

		log.debug(" ********** preparedString **************");
		
		return filterScript/*preparedString.toString()*/;
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
	 * @return Returns the sybaseMgr.
	 */
	public static SybaseManager getSybaseMgr() {
		return sybaseMgr;
	}

	/**
	 * @return the pollThr
	 */
	public static Thread getPollThr() {
		return PollThr;
	}

	/**
	 * @param pollThr the pollThr to set
	 */
	public static void setPollThr(Thread pollThr) {
		PollThr = pollThr;
	}

	/**
	 * @return the isServerDead
	 */
	public boolean isServerDead() {
		return isServerDead;
	}

	/**
	 * @param isServerDead the isServerDead to set
	 */
	public void setServerDead(boolean isServerDead) {
		this.isServerDead = isServerDead;
	}

}

