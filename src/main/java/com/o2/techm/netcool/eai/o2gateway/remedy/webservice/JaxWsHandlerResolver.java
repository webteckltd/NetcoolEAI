/* Modification  History :
 * Date          Version  Modified by     Brief Description of Modification
 * 12-Jun-2014   1.0      Indra               CD38583 - BMR                                                                                           
 */

package com.o2.techm.netcool.eai.o2gateway.remedy.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

public class JaxWsHandlerResolver implements HandlerResolver {

	public List<Handler> getHandlerChain(PortInfo portInfo) {
		// TODO Auto-generated method stub
		List<Handler> hchain = new ArrayList<Handler>();
		hchain.add(new LoggingHandler());
		
		return hchain;
	}
	 


 
}
