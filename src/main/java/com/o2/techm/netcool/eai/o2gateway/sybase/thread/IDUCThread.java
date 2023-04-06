package com.o2.techm.netcool.eai.o2gateway.sybase.thread;

//import java.sql.PreparedStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp; 
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.jdbc.PoolConnectionManager;
import com.o2.techm.netcool.eai.o2gateway.monitor.MonitorTask;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayEventType;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPair;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNVPairList;
import com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNetcoolEvent;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;
import com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelper;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelperException;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManager;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManagerException;
import com.o2.util.WorkQueue;
import com.sybase.jdbc2.jdbc.SybConnection;

public class IDUCThread extends Thread implements Terminatable
{
	private static final Logger log = LoggerFactory.getLogger(IDUCThread.class);
	
	private boolean terminate = false;
	private IDCUThreadPool parent = null;
	//private String opflag = null;
	int op_flags=0;
	//private String theQueue= null;
	
	private PluginManager mgr;
	public static int ACTION_INSERT = 1;
	public static int ACTION_UPDATE = 2;
	public static int ACTION_DELETE = 4;
	public static int ACTION_JOURNAL =8;
	private static final String IDUC_POOL = "IDUCPOOL"; 
	private static final String SQLSEP = " , ";
	private static final String SQLEND = " ;";
	
	private static final String SQLFROM = " from ";
	
	private static final String SQLWHERE = " where ";
	
	private static final String SQLOR = " or ";
	
	private static final String SQLSELECT = "select ";
	private static final String SQLAND = " and ";
	private static final String SQLEQUAL = " = ";
	private static final String SQLLIKE = " like ";
	private static final String SQLSQUOTE = "'";
	private static final String SQLNOTIFY = "set self notify ";
	private static final int BATCH_UP=100;
	
	
	IDUCInfo iducIns; 
	WorkQueue writerQueue;

	private SimpleDateFormat CICDateFormat;

	private String name;

	public IDUCThread(
			IDCUThreadPool parent,
			String name,IDUCInfo iducIns,PluginManager mgr,WorkQueue writerQueue)
	{
		super(parent, name);
		this.name =name;
		this.parent = parent; 
		this.iducIns = iducIns;	
		this.mgr = mgr;
		this.writerQueue=writerQueue;
		 
		try {
			CICDateFormat = mgr.getSimpleDateFormat(NetcoolConnector.CIC_DATE_FORMAT);
		} catch (PluginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean exceptionMsg (SQLException ex) throws SQLException
    {
        boolean rc = false;

        // If a SQLWarning object was given, display the warning messages.  
        // Note that there could be multiple warnings chained together
        log.debug("SQLException thrown:", ex);
        while (ex != null) 
        {
            rc = true;
            if (ex != null) 
            {
            	log.debug("Warning SQLState: " + ex.getSQLState () + " Message:  " + ex.getMessage () + "Vendor:   " + ex.getErrorCode () );
                ex = ex.getNextException();
            }
        }
        return rc;
    }
	/*
	 * 
	 * @param bitFlag
	 * @param serials
	 * @return
	 * @throws SQLException
	 */
	private boolean queryDB(String [] serials, SybConnection  reciever_con) throws SQLException
	{	 
		//log.debug(" ********* S T A R T Of Parsing filter scripts ********* "); 
		
		Hashtable filters = mgr.getFilters();
		Enumeration e2 = filters.keys();
		//log.debug("There are " + filters.size() +  " filter sets");
		int f_cnt=0;
		StringBuffer serialStr=new StringBuffer();
		for(int i=0; i < serials.length && !terminate;i++)
		{
			serialStr.append(serials[i]);
			if(i < serials.length -1)
				serialStr.append(",");
		}
		//log.debug("Executing all " + filters.size() + " filters");
		//log.debug(" ********* Get Reciever Connection *********");	 
		
		while(!terminate && e2.hasMoreElements())
		{
			StringBuffer fval =new StringBuffer(" ").append(SQLWHERE).append(" ").append("Serial in (").append(serialStr).append(")");
			String filtername = (String)e2.nextElement();
			Hashtable hh =((Hashtable)filters.get(filtername));
			String filterScript=null;
			filterScript = (String)hh.get("script");
			String filterPost =  (String)hh.get("post");
			//log.debug("filtername " + filtername);
			//log.debug("filterScript " + filterScript);
			//log.debug("filterPost " + filterPost);
				
			PreparedStatement stmt=null;
			PreparedStatement post_stmt=null;
			String qryStmt = null;			
			if(filterScript != null && iducIns.actionFlag != 8)			
				fval.append(SQLAND).append(filterScript);
			
			if(!fval.toString().endsWith(SQLEND.trim()))
				fval.append(SQLEND);
			 
			if(iducIns.query != null)
			{				 
				qryStmt = iducIns.query + fval.toString();
			}			
			  
			//log.debug("IDUC Fetch Query : " + qryStmt);
			stmt = reciever_con.prepareStatement(qryStmt);
			ResultSet rs = null;			
			if(stmt !=null)
			{
				f_cnt++;
				//log.debug("Executing filter No. " + f_cnt + "[ " + fval.toString() + " ]");			 
				rs = stmt.executeQuery ();
				 
				PreparedStatement pstmt=null;
				
				if(iducIns.actionFlag == 4)
				{
					log.debug(" Delete events , not processing filters");
					break;
				}
				else if(iducIns.actionFlag == 8)
				{
					log.debug(" Journal does not use filters");
					packEvent(rs);
					break;
				}				 
				else if (packEvent(rs)&& filterPost != null)
				{	
					StringBuffer fp = new StringBuffer(filterPost.trim().replace(';',' '));
					//log.debug("Executing Post filter.... [ " + fp + " ]");
					fp.append(SQLWHERE).append("Serial in (").append(serialStr).append(")");
					if(filterScript != null)			
						fp.append(SQLAND).append(filterScript);
					fp.append(SQLEND);
					//log.debug("Executing Post filter.... [ " + fp + " ]");
					
						
					if(fp.toString().toLowerCase().startsWith("update"))
					{
						StringBuffer stmp = new StringBuffer(SQLNOTIFY + " " + Boolean.FALSE + SQLEND).append(fp)
						.append(new StringBuffer(SQLNOTIFY + " " + Boolean.TRUE + SQLEND));
						pstmt = reciever_con.prepareStatement(/*fp.toString()*/stmp.toString());
						//log.debug("Executing Post filter.... [ " + stmp + " ]");
						int i = pstmt.executeUpdate();
						if(i > 0)
							log.debug("Updated " + i + " rows");
					}
					else	
					{
						ResultSet p_rs = null;	
						pstmt = reciever_con.prepareStatement(fp.toString());
						p_rs = pstmt.executeQuery ();
						if(p_rs != null)
							p_rs.close();
					}	
					
				}
				if(pstmt !=null)
					pstmt.close();						
			}
			if(rs != null)
				rs.close();
			if(stmt !=null) 
				stmt.close();			
		}
		
		//log.debug("Executed all " + filters.size() + " filters");
		//log.debug(" ********* E N D  Of Parsing filter scripts ********* ");
		
		return true;
	}
	void processQueue() throws InterruptedException, SQLException
	{
		SybConnection reciever_con = (SybConnection)PoolConnectionManager.requestConnection(IDUC_POOL); 
		if(reciever_con != null )
		{
			int sz = iducIns.iducQueue.size();
			//restricted to processing the value of 'BATCH_UP' at a time
			if(sz > BATCH_UP)
				sz=BATCH_UP;
			String [] tmp = new String [sz]; 
			for(int i=0; i < sz;i++)
			{
				tmp[i]=(String)iducIns.iducQueue.dequeueWork();
			}
			if(tmp != null)
				queryDB(tmp, reciever_con);
			PoolConnectionManager.returnConnection(IDUC_POOL, reciever_con);
		}		
	}
	public void run() 
	{
		// TODO Auto-generated method stub
		/*
		 * Use Batch Sql HERE: Could not use batching here;
		 * Netcool database does not have some required properties
		 */	    	
		 
		try {
			SybaseManager.iducthreadsStarted++;	 
			while(!terminate)
			{				
				if(iducIns.iducQueue.size() > 0)
				{
					try
					{
						processQueue();
					}
					catch (SQLException se) 
					{
						// TODO Auto-generated catch block 
						se.printStackTrace();
						exceptionMsg(se);
//						Send Critical Alarm to CIC
						String strMsg=se.getMessage();
						SocketProbePlugin socketProbePlugin =(SocketProbePlugin) mgr.getPlugins().get(SocketProbePlugin.NAME);
							Event event = socketProbePlugin.createEvent("1", MonitorTask.CLASS,   strMsg);
							try {
								SocketProbeHelper.raiseEvent(socketProbePlugin,event);
							} catch (SocketProbeHelperException e1) {
								// TODO Auto-generated catch block
								log.error("Socket Error",e1); 
							}
							
						
					} 
				}
				else
				{	 
					/**
					 * Not a good way of
					 * of utilizing the waiting capability in 
					 * dequeueWork() method
					 */
					log.debug(name + ": Waiting for work.... "); 
					Object tempobj = iducIns.iducQueue.dequeueWork();
					//log.debug("Putting it back again..") ;
					iducIns.iducQueue.enqueueWorkFirst(tempobj); 
				}
			}	
			log.debug(name + " terminating. Goodbye");
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error :",e);
		}			
		catch(Exception ee)
		{
			ee.printStackTrace(); 
			log.error("Error :",ee);
		}		
	}
	
	/**
	 * Packs event to send to clients
	 * @param bitFlag
	 * @param rs
	 * @return
	 */
	private boolean packEvent(ResultSet rs )
	{
		int i;
		StringBuffer SerialStr=new StringBuffer();
		try {			
			//      Display data, fetching until end of the result set 			
			while (rs.next ()) 
			{
				NetcoolGatewayNetcoolEvent netcoolevent = new com.o2.techm.netcool.eai.o2gateway.netcool.wsdl.NetcoolGatewayNetcoolEvent();
				
				netcoolevent.setEventId(Integer.toString(iducIns.actionFlag));
				if(iducIns.actionFlag == 1 )
				{				 
					netcoolevent.setType_event( NetcoolGatewayEventType.newET) ;
				}
				else if(iducIns.actionFlag == 2 )
				{ 
					netcoolevent.setType_event(NetcoolGatewayEventType.updateET);
				}
				else if(iducIns.actionFlag == 8 )
				{
					netcoolevent.setType_event( NetcoolGatewayEventType.journalET);
				}
				else if(iducIns.actionFlag == 4 )
				{
					netcoolevent.setType_event(NetcoolGatewayEventType.closeET);
				}  
				else
				{
					log.error("Invalid event (unknown type), skipping... " + iducIns.actionFlag);
					return false;
				}
				ResultSetMetaData rsmd = rs.getMetaData (); 
				int numCols = rsmd.getColumnCount ();
				NetcoolGatewayNVPairList anNVPairList = new NetcoolGatewayNVPairList();
				NetcoolGatewayNVPair anitem[] = new NetcoolGatewayNVPair[numCols];
				for (i=1; i<=numCols; i++) 
				{
					int j=i-1;
					String rsCol;					
					rsCol = rsmd.getColumnLabel(i).trim();	
					String foobar = rs.getString(i);
					//log.debug(" IDUC Print Result set - Column Name = "+ rsCol+" Column Value=" + foobar );
					for (int kk = 0; kk < SybaseManager.DATECOLUNMS.length; kk++) 
					{
						if(SybaseManager.DATECOLUNMS[kk].equals(rsCol) )
						{
							long ticks = new Timestamp(Long.parseLong(rs.getString(i))).getTime();
							foobar = CICDateFormat.format(new java.util.Date(ticks * 1000));
							break;
						}
					}
					if(SybaseManager.CONVCOLUNMS[0].equals(rsCol)) //OwnerUID
					{
						foobar=(String)SybaseManager.getSybaseManager(mgr).getUSER_VALUESStringDesp(rs.getString(i).trim());
					}
					else if(SybaseManager.CONVCOLUNMS[1].equals(rsCol )) //Severity
					{
						foobar=(String)SybaseManager.getSybaseManager(mgr).getSeverityStringDesp(rs.getString(i).trim());
					}
					if(rs.wasNull())
						foobar="";
					if(foobar == null)
						foobar = rs.getString(i);						
					
					if(rsCol.equals("ServerSerial"))
						SerialStr.append(foobar).append(',');
						
					anitem[j] = new NetcoolGatewayNVPair();
					anitem[j].setAName(rsCol.trim());
					anitem[j].setAValue(foobar.trim());
				}
				log.debug("Sending ServerSerial:" + SerialStr + " to Clients");
				anNVPairList.setItem(anitem);
				netcoolevent.setANVPairList(anNVPairList);
				
				writerQueue.enqueueWork(netcoolevent);
			}
			return true;			
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("SQL Error",e);
			e.printStackTrace();
		} catch (SybaseManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return boolean;
		return true;
	}
	/* (non-Javadoc)
	 * @see com.o2.o2gateway.sybase.thread.Terminatable#terminate()
	 */
	public void terminate() {
		// TODO Auto-generated method stub
		terminate = true;
	}
	
	
}
