package com.o2.techm.netcool.eai.manonsite.mapinfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Hashtable;

import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;
import com.o2.techm.netcool.eai.manonsite.mrs.MRSRequestThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.common.Configuration;
import com.o2.techm.netcool.eai.manonsite.jdbc.oracle.OraPoolConnectionManager;
/**
 * Class used to query data that is persistant and stored in either an Oracle or
 * a Dimension database. The data is accessed through views.
 */
public class MapInfo
{
	private static final Logger log = LoggerFactory.getLogger(MapInfo.class);
    public static final String MAPINFOFAILED = "MAPINFO FAILURE";
    private static String MAPINFO_CONNECTION_POOL = "MapInfoConnectionPool";
    private String driverName;
    private String url;
    private String login;
    private String password;
    Hashtable COLUMNDESP=null;
    boolean isMapInfoBad=false;

    /**
     * default constructor based on OSS structure, loads connection parameters
     * from file
     */
    public MapInfo() throws Exception
    {
    	createConnectionPool();
    }

    boolean createConnectionPool()
    {
    	driverName = Configuration.getValue(Configuration.MAPINFO_JDBC_DRIVER);
        url = Configuration.getValue(Configuration.MAPINFO_JDBC_URL);
        login = Configuration.getValue(Configuration.MAPINFO_JDBC_LOGIN);
        password = Configuration.getValue(Configuration.MAPINFO_JDBC_PASSWORD);

        if (!OraPoolConnectionManager.isValid(MAPINFO_CONNECTION_POOL)) 
        {
        	try 
			{
                log.debug("Create a MAPINFO pool in the Oracle PoolConnectionManager");
                OraPoolConnectionManager.createPool(MAPINFO_CONNECTION_POOL, driverName, url, login, password);
                GetColumnDesp();
                
			} catch (Exception sqle) {
				log.error("Cannot create a MAPINFO pool with the Oracle PoolConnectionManager",sqle);
				isMapInfoBad=true;
				/*throw new Exception(sqle);*/
			}
        }
		return isMapInfoBad;      
    }
    /*
	 * Get Column information
	 *
	 */
	public void GetColumnDesp() throws MapInfoException
	{ 
		Connection connection = null;        
		Statement stmt = null;
		try {
			 
			
			log.debug("Request for a MAPINFO connection");
            connection = OraPoolConnectionManager.requestConnection(MAPINFO_CONNECTION_POOL);
            stmt = connection.createStatement();
            //OSC 458, Removal of database synonyms from MOS SQL  16/11/2011
			ResultSet resultset1 = stmt.executeQuery("select MOS_CSR,MOS_SWITCH_SITE,MOS_CLI,MOS_DATESTAMP,MOS_SMS_ACTION,MOS_SMS_TEXT,MOS_ENGINEER_NAME,MOS_COMPANY_NAME from MapInfo_ManOnSite where ROWNUM = 1");
			//
			if(resultset1.next())
			{
				
				if(COLUMNDESP == null)
				{
					log.debug(" ********* Get ColumnTypes ********* ");
					ResultSetMetaData resultsetmetadata1 = resultset1.getMetaData();
					int i2 = resultsetmetadata1.getColumnCount();					
					COLUMNDESP = new Hashtable(i2);
					for(int j2 = 1; j2 <= i2; j2++)
					{	/*log.debug("ColumnName:" + resultsetmetadata1.getColumnName(j2));
						log.debug("ColumnDisplaySize:" + resultsetmetadata1.getColumnDisplaySize(j2));
						log.debug("ClassName:" + resultsetmetadata1.getColumnClassName(j2));*/
						if(resultsetmetadata1.getColumnClassName(j2).endsWith("String"))
							COLUMNDESP.put(resultsetmetadata1.getColumnName(j2),new Integer(resultsetmetadata1.getColumnDisplaySize(j2)));												 
					}
					log.debug(" ********* Retrieved Column Description********* ");
				}
			}
		}
		catch (Exception e) {
        	log.error("MapInfoException:" ,e);
             throw new MapInfoException(e.getMessage());
        } finally {
            //Close statement
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException sqle) {
                log.warn("Could not close statment: " ,sqle);
            }
        }
        OraPoolConnectionManager.returnConnection(MAPINFO_CONNECTION_POOL, connection);
        log.debug("MAPINFO Connection returned");
        
	}
	
	private String truncateString(String aStr, int aSize)
	{		 
		if(aStr != null && aStr.length() > aSize)
        {
        	log.error(aStr + " value is too long, truncating ");
        	aStr =aStr.substring(0,aSize);
        }		
		return aStr;
	}
    public void addEvent(Event newevent) throws MapInfoException
    {
        Connection connection = null;        
        PreparedStatement pstmt = null;

        try {
            log.debug("[" + newevent.getLINE01() + "] Request for a MAPINFO connection");
            connection = OraPoolConnectionManager.requestConnection(MAPINFO_CONNECTION_POOL);
            log.debug("MAPINFO Connection established");
            // OSC 458, Removal of database synonyms from MOS SQL  16/11/2011
            String strSQL = "INSERT INTO MapInfo_ManOnSite (";
            String theStr=null;             
            
    		try
	        {
    			try {
    				char c = newevent.getLINE06().charAt(newevent.getLINE06().length() - 1);    				
    				if(Character.isLetter(c) && 
    						MRSRequestThread.isParsableToInt(newevent.getLINE06().substring(0,newevent.getLINE06().length() - 1)))
    				{						 
    					if((newevent.getLINE05().startsWith("ala") || newevent.getLINE05().startsWith("his")) && (Character.toUpperCase(c) == 'B'|| Character.toUpperCase(c) == 'G'))
    					{
    						log.debug("This is the expecter haviour; continue processing");
    					}
    					else
    					{   
    						throw new NumberFormatException();
    					}					
    				}
    				else
    					Integer.parseInt(newevent.getLINE06());
    			}
    			catch (StringIndexOutOfBoundsException e0)
				{ 
    				Integer.parseInt(newevent.getLINE06());
				} 
	            strSQL += "MOS_CSR"; 
	            theStr = truncateString(newevent.getLINE06(), ((Integer)COLUMNDESP.get("MOS_CSR")).intValue()); 
	        }
	        catch (NumberFormatException ex)
	        {
	        	//DO nothing assuming that this is a SSR
	        	strSQL += "MOS_SWITCH_SITE"; 
	        	theStr = truncateString(newevent.getLINE06(), ((Integer)COLUMNDESP.get("MOS_SWITCH_SITE")).intValue()); 
	        }
            strSQL += ",MOS_CLI,MOS_DATESTAMP,";
            strSQL += "MOS_SMS_ACTION,MOS_SMS_TEXT,MOS_ENGINEER_NAME,MOS_COMPANY_NAME) Values ";
            strSQL += "( ?, ?, ?, ?, ?,?,?)";
            
            pstmt = connection.prepareStatement(strSQL);
       
            pstmt.setString(1, theStr);
            pstmt.setString(2, truncateString(newevent.getLINE01(), ((Integer)COLUMNDESP.get("MOS_CLI")).intValue()));
            Timestamp tp = new Timestamp(System.currentTimeMillis());
    		pstmt.setTimestamp(3,tp);		 
    		pstmt.setString(4, truncateString(newevent.getLINE05(), ((Integer)COLUMNDESP.get("MOS_SMS_ACTION")).intValue()).toUpperCase());
    		pstmt.setString(5, truncateString(newevent.getLINE04(), ((Integer)COLUMNDESP.get("MOS_SMS_TEXT")).intValue()));
            pstmt.setString(6,truncateString(newevent.getLINE10() + " " + newevent.getLINE11(), ((Integer)COLUMNDESP.get("MOS_ENGINEER_NAME")).intValue()));
            pstmt.setString(7, truncateString(newevent.getLINE12(), ((Integer)COLUMNDESP.get("MOS_COMPANY_NAME")).intValue()));
            
            pstmt.executeUpdate();
        } catch (Exception e) {
        	log.error("MapInfoException:" ,e);
            throw new MapInfoException(e.getMessage());
        } finally {
            //Close statement
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException sqle) {
                log.warn("Could not close statment: " ,sqle);
            }
        }
        OraPoolConnectionManager.returnConnection(MAPINFO_CONNECTION_POOL, connection);
        log.debug("[" + newevent.getLINE01() + "]MAPINFO Connection returned");        
    }

	/**
	 * @return the isMapInfoBad
	 * @Comment : (15/10/08) This is a fixed after noticing that the MAPinfo does not try a reconnect after a failure.  
	 */
	public boolean isMapInfoBad() {
		if(isMapInfoBad)
		{
			isMapInfoBad=createConnectionPool();
			if(isMapInfoBad) log.error("Cannot connect to MAPINFO Database at this time see previous error messages for details");
		}		
		return isMapInfoBad;
	}
}