package com.o2.techm.netcool.eai.o2gateway.netcool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelper;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.SocketProbeHelperException;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.GateWaySocketEvent;
import com.o2.techm.netcool.eai.o2gateway.troubleticket.TroubleTicket;

/**
 * @author trenaman
 */
public class ArtixPing implements GatewayEventHandler
{
	public static final String OSIRIS = "OSIRIS";
	private static final Logger log = LoggerFactory.getLogger(ArtixPing.class);
	private SocketProbePlugin socketProbePlugin;
	
    public ArtixPing(SocketProbePlugin socketProbePlugin)
    {
        this.socketProbePlugin = socketProbePlugin;
    }

    /* (non-Javadoc)
     * @see com.o2.osiris.netcool.BasicCICEventHandling#handleEvent(com.o2.osiris.troubleticket.TroubleTicket)
     */
    public boolean handleEvent(TroubleTicket troubleTicket) throws GatewayEventHandlingException
    {
        if (troubleTicket.getTTFlag().equals(BasicCICEventHandling.TTFLAG_PING) 
                || troubleTicket.getNotificationFlag().equals(BasicCICEventHandling.NOTIFICATION_FLAG_EMPTY)
                        && troubleTicket.getTTFlag().equals(BasicCICEventHandling.TTFLAG_EMPTY)
                        && troubleTicket.getAlertKey().equals(BasicCICEventHandling.ALERT_KEY_PING_ARTIX))
        {
        	log.info("HeartBeat.run");
            
            try
            {
            	GateWaySocketEvent event = new GateWaySocketEvent();
                    event.setIDENTIFIER("OSIRIS.heartbeat.ping.response");
                    event.setCDS_ID(ArtixPing.OSIRIS);
                    event.setADDITIONAL_INFO("OSIRIS service heartbeat recently recieved");
    				event.setSEVERITY("0");
                    event.setTYPE("5");
                    event.setCLASS("8001");
                   
                                	                      
                    log.debug("Raising ping response event, details:");
                    log.debug("IDENTIFIER = '" + event.getIDENTIFIER() + "'");
                    log.debug("CDS_ID = '" + event.getCDS_ID() + "'");
                    

                    SocketProbeHelper.raiseEvent(socketProbePlugin, createSocketProbePayLoad(event));
                    log.debug("socketProbe.raiseEvent() complete.");
            }
            catch (SocketProbeHelperException e)
            {
                log.error("Error trying to send Artix Ping to CIC." +  e.getMessage());                
            }            
            return true;
        }
        else { 
            return false;
        }
    }
    
    
    
    
    private  String createSocketProbePayLoad(GateWaySocketEvent event){
 	   StringBuffer sb  =  new StringBuffer();
 	   sb.append("<BeginEvent>" + System.lineSeparator());
 	   sb.append("IDENTIFIER "+ event.getIDENTIFIER().trim() + System.lineSeparator());
 	   sb.append("ADDITIONAL_INFO "+ event.getADDITIONAL_INFO()+ System.lineSeparator());
 	   sb.append("SEVERITY "+ event.getSEVERITY()+ System.lineSeparator());
 	   sb.append("CDS_ID "+ event.getCDS_ID()+ System.lineSeparator());
 	   sb.append("CLASS "+ event.getCLASS()+ System.lineSeparator());
 	   sb.append("TYPE "+ event.getTYPE()+ System.lineSeparator());
 	   sb.append("<EndEvent>" + System.lineSeparator());
 	   return sb.toString();	   
   }

}
