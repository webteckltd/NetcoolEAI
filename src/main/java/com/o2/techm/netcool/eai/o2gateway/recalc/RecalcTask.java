/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 12-Jun-2014            Indra               CD38583 - BMR                                                                                           
*/
package com.o2.techm.netcool.eai.o2gateway.recalc;

import java.util.Iterator;
import java.util.TimerTask;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.plugins.EnrichmentDatabasePlugin;
import com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin;
import com.o2.techm.netcool.eai.o2gateway.serviceview.Recalc;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelper;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelperException;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.GateWaySocketEvent;
import com.o2.util.Semaphore;
import com.o2.techm.netcool.eai.o2gateway.persistence.EnrichmentDatabaseException;

public class RecalcTask extends TimerTask {
	private static final String TYPE = "1";
	private static final String CLASS = "8002";
	private static final String SEVERITY = "0";
	private static final String RECALC_POLL = "5";
	private static final String RECALC_POLL_ZERO = "0";
	private static final String IDENTIFIER_SUFFIX = "_RECALC";

	private static final Logger log = LoggerFactory.getLogger(RecalcTask.class);

	// private ServiceViewPlugin serviceViewPlugin;
	private EnrichmentDatabasePlugin enrichmentPlugin;
	private SocketProbePlugin socketProbePlugin;
	private Semaphore taskSemaphore;
	private boolean systemShuttingDown;

	public RecalcTask(EnrichmentDatabasePlugin enrichmentPlugin,
			SocketProbePlugin socketProbePlugin) {
		this.enrichmentPlugin = enrichmentPlugin;
		this.socketProbePlugin = socketProbePlugin;

		systemShuttingDown = false;
		taskSemaphore = new Semaphore(1);
	}

	@Override
	public void run() {
		try {
			taskSemaphore.acquire();
		} catch (InterruptedException e) {
			log.error("Unexpected InterruptedException while trying to get the bus semaphore.");
		}

		if (!systemShuttingDown) {
			doWork();
		} else {
			cancel();
		}

		taskSemaphore.release();

	}

	public void shutdown() {
		try {
			log.info("Waiting for any recalc task to complete...");
			taskSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.warn("Unexpected InterruptedException while waiting for recalc task to complete.");
		}

		systemShuttingDown = true;
		taskSemaphore.release();
	}
	
	
	private  String createSocketProbePayLoad(GateWaySocketEvent event){
	 	   StringBuffer sb  =  new StringBuffer();
	 	   sb.append("<BeginEvent>" + System.lineSeparator());
	 	   sb.append("IDENTIFIER "+ event.getIDENTIFIER().trim() + System.lineSeparator());
	 	   sb.append("CDS_ID "+ event.getCDS_ID()+ System.lineSeparator());
	 	   sb.append("POLL "+ event.getPOLL()+ System.lineSeparator());
	 	   sb.append("SEVERITY "+ event.getSEVERITY()+ System.lineSeparator()); 	
	 	   sb.append("CLASS "+ event.getCLASS()+ System.lineSeparator());
	 	   sb.append("TYPE "+ event.getTYPE()+ System.lineSeparator());
	 	   sb.append("OUTAGE_TYPE "+ event.getOBJECT_TYPE()+ System.lineSeparator());
	 	   sb.append("<EndEvent>" + System.lineSeparator());
	 	   return sb.toString();	   
	   }
	

	private void doWork() {
		log.info("RecalcTask.run");
		Vector recalcs = null;
		// TODO: Siva code starts

		GateWaySocketEvent event1 = new GateWaySocketEvent();

		event1.setIDENTIFIER("Node" + IDENTIFIER_SUFFIX);
		event1.setCDS_ID("Node");
		event1.setPOLL(RECALC_POLL);
		event1.setSEVERITY(SEVERITY);
		event1.setCLASS(CLASS);
		event1.setTYPE(TYPE);
		event1.setOBJECT_TYPE("100");

		log.debug(" ------ Raising recalcevent, details:");
		log.debug("IDENTIFIER = '" + event1.getIDENTIFIER() + "'");
		log.debug("CDS_ID = '" + event1.getCDS_ID() + "'");
		log.debug("POLL = '" + event1.getPOLL() + "'");
		log.debug("SEVERITY = '" + event1.getSEVERITY() + "'");
		log.debug("CLASS = '" + event1.getCLASS() + "'");
		log.debug("TYPE = '" + event1.getTYPE() + "'");
		log.debug("OBJECT_TYPE = '" + event1.getOBJECT_TYPE() + "'");

		try {
			log.info(" ------ Raising recalc event.");
			SocketProbeHelper.raiseEvent(socketProbePlugin,createSocketProbePayLoad(event1));

			log.debug(" ------ socketProbe.raiseEvent() complete.");

		} catch (SocketProbeHelperException ex) {
			log.error(" ------- Received exception while trying to send recalc data to the socket probe; details: "
					+ ex.getMessage());
		}
		// TODO: Siva code ends

		try {
			log.info("Getting recalc data...");
			recalcs = enrichmentPlugin.getPersistence().getRecalcData();
			Iterator iter = recalcs.iterator();
			boolean recalcDataAvailable = iter.hasNext();
			while (iter.hasNext()) {
				/**
				 * Raise an event for the recalc data.
				 */
				Recalc recalc = (Recalc) iter.next();

				GateWaySocketEvent event = new GateWaySocketEvent();
				event.setIDENTIFIER(recalc.getCDSId() + IDENTIFIER_SUFFIX);
				event.setCDS_ID(recalc.getCDSId());
				event.setPOLL(recalc.getObjectType() > 99 ? RECALC_POLL_ZERO
						: RECALC_POLL);
				event.setSEVERITY(SEVERITY);
				event.setCLASS(CLASS);
				event.setTYPE(TYPE);
				event.setOBJECT_TYPE("" + recalc.getObjectType());

				log.debug("Raising recalcevent, details:");
				log.debug("IDENTIFIER = '" + event.getIDENTIFIER() + "'");
				log.debug("CDS_ID = '" + event.getCDS_ID() + "'");
				log.debug("POLL = '" + event.getPOLL() + "'");
				log.debug("SEVERITY = '" + event.getSEVERITY() + "'");
				log.debug("CLASS = '" + event.getCLASS() + "'");
				log.debug("TYPE = '" + event.getTYPE() + "'");
				log.debug("OBJECT_TYPE = '" + event.getOBJECT_TYPE() + "'");

				try {
					log.info("Raising recalc event.");
					SocketProbeHelper.raiseEvent(socketProbePlugin,createSocketProbePayLoad(event));

					log.debug("socketProbe.raiseEvent() complete.");

					/*
					 * Now delete the recalc data from the database
					 */
					enrichmentPlugin.getPersistence().deleteRecalcData(
							recalc.getCDSId(), recalc.getObjectType() + "");

					// serviceViewPlugin.getServiceView().deleteRecalcData(recalc.getCDSId(),
					// recalc.getObjectType() + "");
					log.debug("Deleted recalc data.");

				} catch (SocketProbeHelperException ex) {
					log.error("Received exception while trying to send recalc data to the socket probe; details: "
							+ ex.getMessage());
				} catch (EnrichmentDatabaseException e) {
					log.error("Error trying to get recalc date from EnrichmentDatabase; details: "
							+ e.getMessage());
				}
			}

			if (!recalcDataAvailable) {
				log.debug("There was no recalc data available.");
			}
		} catch (EnrichmentDatabaseException e) {
			log.error("Error trying to get recalc date from EnrichmentDatabase; details: "
					+ e.getMessage());
		}

	}

	public boolean isSystemShuttingDown() {
		return systemShuttingDown;
	}

	public void setSystemShuttingDown(boolean busHasShutDown) {
		this.systemShuttingDown = busHasShutDown;
	}

	public Semaphore getTaskSemaphore() {
		return taskSemaphore;
	}

	public void setTaskSemaphore(Semaphore busSemaphore) {
		this.taskSemaphore = busSemaphore;
	}
}
