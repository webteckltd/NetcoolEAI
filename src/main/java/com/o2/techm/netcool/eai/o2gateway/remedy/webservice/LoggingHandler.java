/* Modification  History :
 * Date          Version  Modified by     Brief Description of Modification
 * 12-Jun-2014   1.0      Indra               CD38583 - BMR                                                                                           
 */
package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import java.io.StringWriter;
import java.util.Set;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

	// Initialize OtputStream (fos) etc. ...
	// OutputStream out;
	private static final Logger log = LoggerFactory.getLogger(LoggingHandler.class);

	public LoggingHandler() {
		super();
	}

	public boolean handleMessage(SOAPMessageContext smc) {
		SOAPMessage message = smc.getMessage();
		
		SOAPMessageContext messageContext = (SOAPMessageContext) smc;
		
		boolean isOutboundMessage = (Boolean) smc
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (isOutboundMessage) {
			log.debug("OUTBOUND MESSAGE");

		} else {
			log.debug("INBOUND MESSAGE");
		}

		// Set the output for the transformation
		StringWriter writer = new StringWriter();

		StreamResult result = new StreamResult(writer);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(message.getSOAPPart().getContent(), result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug((writer.getBuffer()).toString());

		return true;
	}

	public boolean handleFault(SOAPMessageContext smc) {
		SOAPMessage message = smc.getMessage();

		// Set the output for the transformation
		StringWriter writer = new StringWriter();

		StreamResult result = new StreamResult(writer);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(message.getSOAPPart().getContent(), result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug((writer.getBuffer()).toString());

		return true;
	}

	public Set getHeaders() {
		// Not required for logging
		return null;
	}

	public void close(MessageContext context) {
		// TODO Auto-generated method stub

	}


}