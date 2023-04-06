/*Modification  History :
* Date          Version Modified by     Brief Description of Modification
* 11-Jan-2011   1.10      Keane          Modified for OSC 1558511
*/
package com.o2.techm.netcool.eai.manonsite.sybase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import com.o2.techm.netcool.eai.manonsite.common.Constants;  
import com.o2.techm.netcool.eai.manonsite.jdbc.sybase.*;
import com.sybase.jdbc2.jdbc.SybConnection;

public class SybaseImpl  
{
	private static final Logger log = LoggerFactory.getLogger(SybaseImpl.class);

	private static final String SQLSEP = " , ";
	private static final String SQLEND = " ;";
	
	private static final String SQLFROM = " from ";
	
	private static final String SQLWHERE = " where ";
	
	private static final String SQLOR = " or ";
	
	private static final String SQLSELECT = "select ";
	private static final String SQLUPDATE = "update ";
	private static final String SQLAND = " and ";
	private static final String SQLEQUAL = " = ";
	private static final String SQLLIKE = " like ";
	private static final String SQLSQUOTE = "'";
	private static final String SQLNOTIFY = "set self notify ";
	private static final String TableName= "custom.mosAdmin" ;
	private static final String TableFields = "MSISDN,SecurityLevel,EnteredBy,ModifyDate,FName,SName,HoppingDailyLimit,HoppingCountDay,UnlockDailyLimit,UnlockCountDay";
	private SimpleDateFormat CICDateFormat;

	private String name;
	private static Hashtable COLUMNDESP = null;
	 
	//SybConnection con ;
	String qryStmt = SQLSELECT + TableFields + SQLFROM + TableName + SQLWHERE + 
	"MSISDN" +  SQLEQUAL + "?" + SQLEND;	
	
	public SybaseImpl()
	{

		try {
			GetColumnDesp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 /*
	 * Get Column information
	 *
	 */
	public void GetColumnDesp() throws Exception
	{ 
		Statement stmt = null;
		log.debug("Request for a Sybase connection");   
		SybConnection con = (SybConnection)SyPoolConnectionManager.requestConnection(Constants.IDUC_POOL);
		try {			
			if(COLUMNDESP == null)
			{				
				stmt = con.createStatement();
				ResultSet resultset1 = stmt.executeQuery("select TOP 1 * from " + TableName + ";" );
				if(resultset1.next())
				{					
					log.debug(" ********* Get ColumnTypes ********* ");
					ResultSetMetaData resultsetmetadata1 = resultset1.getMetaData();
					int i2 = resultsetmetadata1.getColumnCount();					
					COLUMNDESP = new Hashtable(i2);
					for(int j2 = 1; j2 <= i2; j2++)
					{	
						log.debug("ColumnName:" + resultsetmetadata1.getColumnName(j2));
						//log.debug("ColumnDisplaySize:" + resultsetmetadata1.getColumnDisplaySize(j2));
						//log.debug("ClassName:" + resultsetmetadata1.getColumnClassName(j2));						 
					COLUMNDESP.put(resultsetmetadata1.getColumnName(j2),new Integer(resultsetmetadata1.getColumnDisplaySize(j2)));												 
					}
					log.debug(" ********* Retrieved Column Description********* ");					
				}
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("SQL ERROR:",e);
			e.printStackTrace();
		}
		catch (Exception e) {
			log.error("Exception:" ,e);
			throw new Exception(e.getMessage());
		} finally {
			//Close statement
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException sqle) {
				log.warn("Could not close statment: " ,sqle);
			}
			SyPoolConnectionManager.returnConnection(Constants.IDUC_POOL,con);
			
		}
	} 
	public HashMap queryDB(String msisdn) throws SQLException  
	{	 
		
		//construct Query
		/* describe custom.mosAdmin table
		 * ColumnName            Type       
		 ---------------------- ------------
		 MSISDN                   2         
		 SecurityLevel            0         
		 EnteredBy                2         
		 ModifyDate               1         
		 FName                    2         
		 SName                    2         
		 HoppingDailyLimit        0         
		 HoppingCountDay          0         
		 UnlockDailyLimit         0         
		 UnlockCountDay           0   
		 LockDailyLimit           0         
		 LockCountDay             0         
		 */
		HashMap tmpMap=null;
		ResultSet rs=null;
		ResultSetMetaData rsmd = null;
		SybConnection con=null;
		PreparedStatement pstmt=null;
		try {
			con = (SybConnection) SyPoolConnectionManager.requestConnection(Constants.IDUC_POOL);			
			pstmt = con.prepareStatement(qryStmt.replaceFirst("\\?","'" + msisdn +"'"));
			//pstmt.setString(1,msisdn);
			rs = pstmt.executeQuery();
			rsmd = rs.getMetaData ();
			int colCnt = rsmd.getColumnCount ();
			//MSISDN,SecurityLevel,EnteredBy,ModifyDate,FName,SName,HoppingDailyLimit,HoppingCountDay,UnlockDailyLimit,UnlockCountDay
			if(pstmt !=null)
			{ 
				if (rs.next ()) 
				{
					tmpMap = new HashMap(colCnt);
					for (int i=1; i<=colCnt; i++) 
					{
						log.debug("getColumnName[" + i + "]:" + rs.getString(i));
						tmpMap.put(rsmd.getColumnName(i),rs.getString(i));
					}
				}
			}
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
			SyPoolConnectionManager.returnConnection(Constants.IDUC_POOL,con);
		}
		return tmpMap;
	}	 
	public void updateDB(String msisdn, int oldVal, String cmmd) throws SQLException
	{			 
		String afield=null;
		// Added lock command for OSC 1558511
		if(cmmd.equals("unlock") || cmmd.equals("lock"))
		//End of Addition for OSC 1558511
		{
			afield = "UnlockCountDay";
		}
		else if(cmmd.startsWith("hopo"))
		{
			afield="HoppingCountDay";
		}
			
		SybConnection con = (SybConnection) SyPoolConnectionManager.requestConnection(Constants.IDUC_POOL);				
		String updStmt =  SQLUPDATE + TableName + " set "  + afield + SQLEQUAL + (oldVal + 1) + SQLWHERE  + "MSISDN" +  SQLEQUAL + "?" + SQLEND;
		log.debug(updStmt.replaceFirst("\\?","'" + msisdn +"'"));
		PreparedStatement pstmt=con.prepareStatement(updStmt.replaceFirst("\\?","'" + msisdn +"'"));;
		//pstmt.setString(1,msisdn);
		
		pstmt.executeUpdate();
		 
		if(pstmt !=null)
		{ 
			pstmt.close();
		}
		SyPoolConnectionManager.returnConnection(Constants.IDUC_POOL,con);
	}

}
