/*
 * Created on 18-Mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/* Modification  History :
 * Date          Version  Modified by     Brief Description of Modification
 * 12-Jun-2014            Indra               CD38583 - BMR                                                                                           
 */
package com.o2.techm.netcool.eai.manonsite.common;

import java.sql.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.common.Configuration;
import com.o2.techm.netcool.eai.manonsite.ManOnSite;
import com.o2.util.WorkQueue;
import com.o2.util.Semaphore;
import com.o2.techm.netcool.eai.manonsite.jdbc.oracle.OraPoolConnectionManager;
import com.o2.techm.netcool.eai.manonsite.common.troubleticket.TroubleTicketException;

/**
 * @author aademij1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommunicateWithTT {
	//String cellSiteId;
	
	String authReply;
	ManOnSite manonsite;
	private URL socketProbeWsdlUrl;

	private URL netcoolGatewayWsdlUrl;  
    private URL serverAdminWsdlUrl;
    private String dbUrl; 
	private String dbUsername;
	private String dbPassword;
	private String dbDriverName;
	
	private static final Logger log = LoggerFactory.getLogger(CommunicateWithTT.class);
	public static String JDBC_POOL = "JDBCPool";
	private static SimpleDateFormat dateFormat; 
	private Semaphore taskSemaphore;
	private WorkQueue workQueueCIC;
	private String genericQuery ;
    private CallableStatement contact_stmt=null;
    private static CallableStatement loc_stmt=null;
    private static CallableStatement sms_stmt=null;
    private static PreparedStatement pstmt=null;
    private static ResultSet rs=null;
    private int numCols;
   
    //private static Connection dbConnection = null;
    public static final String JDBCFAILED="JDBC FAILURE";
    static boolean jdbcinit=false;
    //static int JDBC_RETRY_CNT=3;
	/**
	 * 
	 */
    
    public static void main(String args[]) 
	{
   	 	String msisdn = args[0];
   	 	int k=Integer.parseInt(args[1]);
   	 	boolean found=false;
    	try
		{
    		int i=0;
    		
    		 CommunicateWithTT ttServer = new CommunicateWithTT();
    		 ttServer.init();
    		 do {
    		 	HashMap rOutput = ttServer.AuthenticateUser("+" + new String(msisdn));
 			
    		 	if(rOutput != null && new Integer((String)rOutput.get("ERRORMSG")).intValue() == 0)
    		 	{
    		 		found=true;
    		 	}
    		 	else
    		 		log.debug("[" + msisdn + "] Authentication failed ");
 			
    		 	log.debug("[" + msisdn + "] Sleeping for 120 Seconds");
    		 	Thread.sleep((2 * 60 * 1000));
    		}while(i++<k);
    	}
    	catch (Exception e)
		{
    		e.getMessage();
    	}
    	log.debug("[" + msisdn + "] Slept for 120 Seconds");
   	}
	public CommunicateWithTT()
	{
		super();
		
		dbUrl = Configuration.getValue(Configuration.ENRICHMENTDB_JDBC_URL);
		dbUsername = Configuration.getValue(Configuration.ENRICHMENTDB_JDBC_LOGIN);
		dbPassword = Configuration.getValue(Configuration.ENRICHMENTDB_JDBC_PASSWORD);
		dbDriverName = Configuration.getValue(Configuration.ENRICHMENTDB_JDBC_DRIVER);
		
		//this.socketProbe = getSocketProbe();
		
		dateFormat = new SimpleDateFormat(Configuration.getValue(Configuration.CIC_DATE_FORMAT));
		taskSemaphore = new Semaphore(1);
		//this.workQueueCIC = workQueueCIC;
	}
	
	
	public void init() throws TroubleTicketException
	{

		//TODO Auto-generated constructor stub
		
		log.info("Creating a pool of JDBC connections to Clarify...");
		
		if(!OraPoolConnectionManager.isValid(JDBC_POOL)) {
			try
			{
				
				OraPoolConnectionManager.createPool(
					JDBC_POOL,
					dbDriverName,
					dbUrl,
					dbUsername,
					dbPassword);			 
			}
			catch(SQLException ex)
			{
			   throw new TroubleTicketException("Error in creating the pool manager: " + ex.getMessage());
			}
			jdbcinit=true;
		}
		
	}

	static CallableStatement getCallableStmt(String statement,Connection dbConnection) throws java.sql.SQLException
	{
		CallableStatement lstmt=null;
		lstmt = dbConnection.prepareCall(statement);
		return lstmt;
		
	}
	
	HashMap CallMOSContact(String mobnumber) throws  java.sql.SQLException
     {
             HashMap rOutput = new HashMap(5); 
             
             Connection dbConnection = OraPoolConnectionManager.requestConnection(JDBC_POOL);              
             if(dbConnection == null)
             {
            	 if(jdbcinit)
             		throw new SQLException("db Connection could not be retrived. Please check if Oracle database up");
             	else
             		throw new SQLException("ManOnSite was not able to initiate connection to EnrichmentDB on StartUP; Please resolve before restarting");
        	  }
             
             if(contact_stmt == null){
             	//contact_stmt = getCallableStmt("{call CNSP_MANONSITE_INTERFACE_PKG.CNSP_MANONSITE_CONTACT(?, ?, ?, ?, ?, ?)}",dbConnection);
            	//contact_stmt = getCallableStmt("{call ARADMIN.CNSP_MANONSITE_CONTACT(?, ?, ?, ?, ?, ?)}",dbConnection);
            	 contact_stmt = getCallableStmt("{call CNSP_MANONSITE_CONTACT(?, ?, ?, ?, ?, ?)}",dbConnection);
             }

             if(contact_stmt != null)
             {
             	// set the in param
             	contact_stmt.setString(1, mobnumber);

	             // register the type of the out param
             	contact_stmt.registerOutParameter(2,java.sql.Types.CHAR );
             	contact_stmt.registerOutParameter(3,java.sql.Types.CHAR );
             	contact_stmt.registerOutParameter(4,java.sql.Types.CHAR );
             	contact_stmt.registerOutParameter(5,java.sql.Types.CHAR );
             	contact_stmt.registerOutParameter(6,java.sql.Types.NUMERIC );
	             // execute and retrieve the result set
             	contact_stmt.execute();
	              
	             String dirtn =contact_stmt.getString(2);
	             if( contact_stmt.getString(2) != null)
	             {
	             	log.debug("[" + mobnumber +"]FIRSTNAME\t:\t" + dirtn.trim());
	                rOutput.put("FIRSTNAME", dirtn.trim());
	             }
	             else
	             	log.debug("[" + mobnumber +"]FIRSTNAME is not set");
	             dirtn =contact_stmt.getString(3);
	             if( contact_stmt.getString(3) != null)
	             {
	             	log.debug("[" + mobnumber +"]LASTNAME\t:\t" + dirtn.trim());
	             	rOutput.put("LASTNAME", dirtn.trim());
	             }
	             else
	             	log.debug("[" + mobnumber +"]LASTNAME is not set");
	             
	            dirtn =contact_stmt.getString(4);
	            if( contact_stmt.getString(4) != null)
	            {
	            	log.debug("[" + mobnumber +"]COMPANY\t:\t" + dirtn.trim());
	                rOutput.put("COMPANY", dirtn.trim());
	            }
	            else
	             	log.debug("[" + mobnumber +"]COMPANY is not set");
	            
	            dirtn =contact_stmt.getString(5);
	            if( contact_stmt.getString(5) != null)
	            {
	            	log.debug("[" + mobnumber +"]EMAILADDRESS\t:\t" + dirtn.trim());
	                rOutput.put("EMAILADDRESS", dirtn.trim());
	            }
	            else
	             	log.debug("[" + mobnumber +"]EMAILADDRESS is not set");
	            
	            dirtn =contact_stmt.getString(6);
	            if( contact_stmt.getString(6) != null)
	            {
	            	log.debug("[" + mobnumber +"]ERRORMSG\t:\t" + dirtn.trim());
	                rOutput.put("ERRORMSG", dirtn.trim());
	            }
	            else
	             	log.debug("[" + mobnumber +"]ERRORMSG is not set");
	            
	            
            }
            OraPoolConnectionManager.returnConnection(JDBC_POOL, dbConnection);
		    log.debug("Connection returned");
            return rOutput;
     }

	/*
	public static HashMap CallSmsInterface(String alertid, String tdate) throws  java.sql.SQLException
	{
	 		HashMap rOutput = new HashMap(4);  
            Connection dbConnection = OraPoolConnectionManager.requestConnection(JDBC_POOL);
            
            if(dbConnection == null)
            {
            	//log.error("db Connection could not be retrived. Please check if Oracle database up");
            	if(jdbcinit)
            		throw new SQLException("db Connection could not be retrived. Please check if Oracle database up");
            	else
            		throw new SQLException("ManOnSite was not able to initiate connection to Amdocs on StartUP; Please resolve before restarting");
            }
						
			if(sms_stmt == null)
				sms_stmt = getCallableStmt("{call CNSP_SMS_INTERFACE_PKG.CNSP_SMS_INTERFACE(?, ?, ?, ?, ?, ?)}",dbConnection);

	        // set the in param
			if(sms_stmt != null)
			{
				sms_stmt.setBigDecimal(1, BigDecimal.valueOf(Long.parseLong(alertid)));
				sms_stmt.setString(2, tdate);
	
		        // register the type of the out param
				sms_stmt.registerOutParameter(3,java.sql.Types.VARCHAR );
				sms_stmt.registerOutParameter(4,java.sql.Types.CHAR );
				sms_stmt.registerOutParameter(5,java.sql.Types.VARCHAR );
				sms_stmt.registerOutParameter(6,java.sql.Types.VARCHAR );
		        
		        // execute and retrieve the result set
				sms_stmt.execute();
				int i=0;
				String dirtn =sms_stmt.getString(3);
				if( sms_stmt.getString(3) != null)
				{
					log.debug("[" + alertid +"]SMSID\t:\t" + dirtn.trim());
					rOutput.put("SMSID", dirtn.trim());
				}
				dirtn =sms_stmt.getString(4);
				if( sms_stmt.getString(4) != null)
				{
					log.debug("[" + alertid +"]SMSENABLED\t:\t" + dirtn.trim());
					rOutput.put("SMSENABLED", dirtn.trim());
				}
				dirtn =sms_stmt.getString(5);
				if( sms_stmt.getString(5) != null)
				{
					log.debug("[" + alertid +"]EMAILADDRESS\t:\t" + dirtn.trim());
					rOutput.put("EMAILADDRESS", dirtn.trim());
				}
				dirtn =sms_stmt.getString(6);
				if( sms_stmt.getString(6) != null)
				{
					log.debug("[" + alertid +"]ERRORMSG\t:\t" + dirtn.trim());
					rOutput.put("ERRORMSG", dirtn.trim());
				}
			}
			OraPoolConnectionManager.returnConnection(JDBC_POOL, dbConnection);
	        log.debug("Connection returned");
			return rOutput;
	 }
	 */
	
	public static HashMap CallMOSLocation(String csiteid)
	{
		HashMap rOutput  = new HashMap(5); 
		Connection dbConnection = null;
		try
		{

		     dbConnection = OraPoolConnectionManager.requestConnection(JDBC_POOL);;

			if(dbConnection == null)
			{ 
				if(jdbcinit)
					log.error("db Connection could not be retrived. Please check if Oracle database up");
            	else
            		log.error("ManOnSite was not able to initiate connection to EnrichmentDB on StartUP; Please resolve before restarting");
				rOutput.put("DIRECTIONS","Sorry, Can't retrive directions at this time");
				rOutput.put("SITENOTE","Sorry, Can't retrive sitenote at this time");
				rOutput.put("SAFETY","Sorry, Can't retrive safety info at this time");
				rOutput.put("ERRORMSG","0");
				return rOutput;
			}
			if(loc_stmt == null){
				//loc_stmt = getCallableStmt("{call CNSP_MANONSITE_INTERFACE_PKG.CNSP_MANONSITE_LOCATION(?, ?, ?, ?, ?)}",dbConnection);
				//loc_stmt = getCallableStmt("{call ARADMIN.CNSP_MANONSITE_LOCATION(?, ?, ?, ?, ?, ?)}",dbConnection);
				loc_stmt = getCallableStmt("{call CNSP_MANONSITE_LOCATION(?, ?, ?, ?, ?, ?)}",dbConnection);
			}
			// set the in param
			log.debug("["+ csiteid + "]CSR=" + csiteid + "\b");
			if(loc_stmt != null)
			{
				loc_stmt.setString(1, csiteid);

				// register the type of the out param 
				loc_stmt.registerOutParameter(2,java.sql.Types.CHAR );
				loc_stmt.registerOutParameter(3,java.sql.Types.CHAR );
				loc_stmt.registerOutParameter(4,java.sql.Types.CHAR );
				loc_stmt.registerOutParameter(5,java.sql.Types.CHAR );
				loc_stmt.registerOutParameter(6,java.sql.Types.CHAR );
				// execute and retrieve the result set
				loc_stmt.execute();
				//if(true)
				//	throw new java.sql.SQLException("User Invented Exception");

				String dirtn =loc_stmt.getString(2);
				if( loc_stmt.getString(2) != null)
				{
					log.debug("["+ csiteid + "]DIRECTIONS\t:\t" + dirtn.replaceAll("\\r|\\n", " ").trim());
					rOutput.put("DIRECTIONS",dirtn.replaceAll("\\r|\\n", " ").trim()); 	
				}
				else
					log.debug("[" + csiteid +"]DIRECTIONS is not set");
				dirtn =loc_stmt.getString(3);
				if( loc_stmt.getString(3) != null)
				{
					log.debug("["+ csiteid + "]SITENOTE\t:\t" + dirtn.replaceAll("\\r|\\n", " ").trim());
					rOutput.put("SITENOTE",dirtn.replaceAll("\\r|\\n", " ").trim()); 	
				}
				else
					log.debug("[" + csiteid +"]SITENOTE is not set");

				dirtn =loc_stmt.getString(4);
				if( loc_stmt.getString(4) != null)
				{
					log.debug("["+ csiteid + "]SAFETY\t:\t" + dirtn.replaceAll("\\r|\\n", " ").trim());
					rOutput.put("SAFETY",dirtn.replaceAll("\\r|\\n", " ").trim());
				}
				else
					log.debug("[" + csiteid +"]SATEFY is not set");

				dirtn =loc_stmt.getString(5);
				if( loc_stmt.getString(5) != null)
				{
					String str_beacon = dirtn.replaceAll("\\r|\\n", " ").trim();
					if(str_beacon.equals("0")){
						str_beacon = "Not Beaconised";
					}else if(str_beacon.equals("1")){
						str_beacon = "Fully Beaconised";
					}else if(str_beacon.equals("2")){
						str_beacon = "Partially Beaconised";
					}
					
					log.debug("["+ csiteid + "]BEACONISED\t:\t" + str_beacon);
					rOutput.put("BEACONISED", ". Beaconised: " + str_beacon); 	
				}
				else
					log.debug("[" + csiteid +"]BEACONISED is not set");
				
				dirtn =loc_stmt.getString(6);
				if( loc_stmt.getString(6) != null)
				{
					log.debug("["+ csiteid + "]ERRORMSG\t:\t" + dirtn.replaceAll("\\r|\\n", " ").trim());
					rOutput.put("ERRORMSG",dirtn.replaceAll("\\r|\\n", " ").trim()); 	
				}
				else
					log.debug("[" + csiteid +"]ERRORMSG is not set");
			}

		}
		catch(java.sql.SQLException sq)
		{
			log.error("Problem querying db using jdbc: " ,sq);
			try {
				loc_stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block 
			}
			loc_stmt=null;
			rOutput.put("DIRECTIONS","Sorry, Can't retrive directions at this time");
			rOutput.put("SITENOTE","Sorry, Can't retrive sitenote at this time");
			rOutput.put("SAFETY","Sorry, Can't retrive safety info at this time");
			rOutput.put("ERRORMSG","0");
		}
		finally
		{
			OraPoolConnectionManager.returnConnection(JDBC_POOL, dbConnection);
			log.debug("Connection returned");
		}
		return rOutput;
	}
	public HashMap AuthenticateUser(String cellSiteId) throws TroubleTicketException
	{
		HashMap rOutput=null;
		String cSId;
		
		cSId = new String(cellSiteId);
		log.info("Authenticating User '" + cSId + "'"); 
						
		try
		{
			rOutput = CallMOSContact(cSId);
		}
		catch(SQLException ex)
		{
			log.info("Problem querying db using jdbc: ",ex);
			try
			{
				if(contact_stmt != null)
				{
					contact_stmt.close();
					contact_stmt= null;
				}
			}
			catch(SQLException sx)
			{
				 log.error("ERROR:",sx);
			} 
			rOutput=null;
		}
		catch(Exception ex)
		{
			log.info("Cannot authenticate cellSiteID, Vantive probably Down",ex);
			rOutput=null;	 
		}
		
		return rOutput;
    }
	
	public String CallSiteName(String siteId) throws  java.sql.SQLException
	{
	 		
            Connection dbConnection = OraPoolConnectionManager.requestConnection(JDBC_POOL);
            
            String sitename="";
            if(dbConnection == null)
            {
            	//log.error("db Connection could not be retrived. Please check if Oracle database up");
            	if(jdbcinit)
            		throw new SQLException("db Connection could not be retrived. Please check if Oracle database up");
            	else
            		throw new SQLException("ManOnSite was not able to initiate connection to EnrichmentDB on StartUP; Please resolve before restarting");
            }
			
            try{
            	//OSC 349
            	log.info("[" + siteId + "] siteId ");
            	
            	
            	//String query = "SELECT NAME FROM ARADMIN.AST_PHYSICALLOCATION WHERE ASSET_ID_=UPPER('"+ siteId+"')";	
            	String query = "SELECT NAME FROM AST_PHYSICALLOCATION WHERE ASSET_ID_=UPPER('"+ siteId+"')";
            
            	//OSC 349
            	log.debug("[" + query + "] query ");
            	
            	
            	pstmt = dbConnection.prepareStatement(query);
    			
   			 		rs = pstmt.executeQuery();
   			 		while(rs.next())
   			 		{
   			 			sitename=rs.getString("NAME");
   			 		}
            }
            catch(SQLException ex)
    		{
    			log.info("Problem querying db using jdbc: ",ex);
    			try
    			{
    				if(pstmt != null)
    				{
    					pstmt.close();
    					pstmt= null;
    				}
    			}
    			catch(SQLException sx)
    			{
    				 log.error("ERROR:",sx);
    			} 
    			
    		}
                       	
			catch(Exception ex)
			{
					log.info("Cannot get Site name from EnrichmentDB database, Vantive probably Down",ex);
					sitename=siteId;	 
			}
			finally
			{
				try
				{
					if(rs != null)
						rs.close();
					if(pstmt != null)
						pstmt.close();
				}
				catch(SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			OraPoolConnectionManager.returnConnection(JDBC_POOL, dbConnection);
	        log.debug("Connection returned");
			}
			return sitename;
	 }	
	public static void close(Connection dbConnection)
	{
		try
		{
			if(sms_stmt != null)
				sms_stmt.close();
			if(loc_stmt != null)
				loc_stmt.close();
		}
		catch(SQLException sx)
		{
			try {dbConnection.close();}catch(SQLException sq){log.info("Caught an SQLException while try to close the DB connection",sq);}
		}
		
		OraPoolConnectionManager.returnConnection(JDBC_POOL, dbConnection);
		log.debug("Connection returned");
	}
    public void shutdown()
	{
    	//troubleTicketManager.shutdown();
		
	}
}
