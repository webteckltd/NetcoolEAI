package com.o2.techm.netcool.eai.o2gateway.socketprobe;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.plugins.SocketProbePlugin;
import com.o2.techm.netcool.eai.o2gateway.socketprobe.wsdl.Event;
import com.o2.util.Semaphore;


public class SocketProbeHelper
{
	private static final Logger log = LoggerFactory.getLogger(SocketProbeHelper.class);
	private static Semaphore syncObj =  new Semaphore();

	private static String createSocketProbePayLoad(Event event){
		StringBuffer sb  =  new StringBuffer();
		sb.append("<BeginEvent>" + System.lineSeparator());
		sb.append("LINE01 "+ event.getLINE01() + System.lineSeparator());
		sb.append("LINE02 "+ event.getLINE02()+ System.lineSeparator());
		sb.append("LINE03 "+ event.getLINE03()+ System.lineSeparator());
		sb.append("LINE04 "+ event.getLINE04()+ System.lineSeparator());
		sb.append("LINE05 "+ event.getLINE05()+ System.lineSeparator());
		sb.append("LINE06 "+ event.getLINE06()+ System.lineSeparator());
		sb.append("LINE07 "+ event.getLINE07()+ System.lineSeparator());
		sb.append("LINE08 "+ event.getLINE08()+ System.lineSeparator());
		sb.append("LINE09 "+ event.getLINE09()+ System.lineSeparator());
		sb.append("LINE10 "+ event.getLINE10()+ System.lineSeparator());
		sb.append("LINE11 "+ event.getLINE11()+ System.lineSeparator());
		sb.append("LINE12 "+ event.getLINE12()+ System.lineSeparator());
		sb.append("LINE13 "+ event.getLINE13()+ System.lineSeparator());
		sb.append("LINE14 "+ event.getLINE14()+ System.lineSeparator());
		sb.append("LINE15 "+ event.getLINE15()+ System.lineSeparator());
		sb.append("LINE16 "+ event.getLINE16()+ System.lineSeparator());
		sb.append("LINE17 "+ event.getLINE17()+ System.lineSeparator());
		sb.append("LINE18 "+ event.getLINE18()+ System.lineSeparator());
		sb.append("LINE19 "+ event.getLINE19()+ System.lineSeparator());
		sb.append("LINE20 "+ event.getLINE20()+ System.lineSeparator());
		sb.append("LINE21 "+ event.getLINE21()+ System.lineSeparator());
		sb.append("LINE22 "+ event.getLINE22()+ System.lineSeparator());
		sb.append("LINE23 "+ event.getLINE23()+ System.lineSeparator());
		sb.append("LINE24 "+ event.getLINE24()+ System.lineSeparator());
		sb.append("LINE25 "+ event.getLINE25()+ System.lineSeparator());
		sb.append("LINE26 "+ event.getLINE26()+ System.lineSeparator());
		sb.append("LINE27 "+ event.getLINE27()+ System.lineSeparator());
		sb.append("LINE28 "+ event.getLINE28()+ System.lineSeparator());
		sb.append("LINE29 "+ event.getLINE29()+ System.lineSeparator());
		sb.append("LINE30 "+ event.getLINE30()+ System.lineSeparator());
		sb.append("<EndEvent>" + System.lineSeparator());
		return sb.toString();

	}






	public static void raiseEvent(SocketProbePlugin plugin , String message) throws SocketProbeHelperException {

		if (plugin.getSocketProbe() == null) {
			String warning  = "Socketprobe is null; perhaps it has not been initalised fully yet"; 
			log.warn(warning);
			throw new SocketProbeHelperException(warning);  
		}

		log.debug("Synchronising on socketProbe..."); 
		synchronized (syncObj) {    	
			try {
				(plugin.getSocketOutput()).write(message.getBytes());
				(plugin.getSocketOutput()).flush();

				log.debug("finished invocation on socket  probe. ");
			} catch (Exception e) {
				log.error(" Problem while writing to Socket Probe .. re-establishing connection ", e);
				plugin.reConnect();
				try {
					(plugin.getSocketOutput()).write(message.getBytes());
					(plugin.getSocketOutput()).flush();
				} catch (IOException e1) {
					log.error(" tried re-establishing connection  with Socket Proble  .. But its still failing ", e);
					throw new SocketProbeHelperException("tried re-establishing connection  with Socket Proble  .. But its still failing ");
				}
			} 
		}
	}


	public static void raiseEvent(SocketProbePlugin plugin , Event event) throws SocketProbeHelperException {
		if (plugin.getSocketProbe() == null) {
			String warning  = "Socketprobe is null; perhaps it has not been initalised fully yet"; 
			log.warn(warning);
			throw new SocketProbeHelperException(warning);  
		}

		log.debug("Synchronising on socketProbe..."); 
		synchronized (syncObj) { 
			String message  = createSocketProbePayLoad(event);

			try {
				(plugin.getSocketOutput()).write(message.getBytes());
				(plugin.getSocketOutput()).flush();

				log.debug("finished invocation on socket  probe. ");
			} catch (Exception e) {
				log.error(" Problem while writing to Socket Probe .. re-establishing connection ", e);
				plugin.reConnect();
				try {
					(plugin.getSocketOutput()).write(message.getBytes());
					(plugin.getSocketOutput()).flush();
				} catch (IOException e1) {
					log.error(" tried re-establishing connection  with Socket Proble  .. But its still failing ", e);
					throw new SocketProbeHelperException("tried re-establishing connection  with Socket Proble  .. But its still failing ");
				}
			} 
		}
	}


}
