package com.o2.techm.netcool.eai.o2gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/serviceCtr")	
public class StatusService {

	private static final Logger logger = LoggerFactory.getLogger(StatusService.class);
	@Autowired
	private O2GatewayMgr gatewayMgr;

	@GetMapping("/getStatus")
	public @ResponseBody String getServiceStatus(){
		return gatewayMgr.getStatusCheck();
	}
	
	@GetMapping("/stopservice")
	public @ResponseBody String stopService(){
		try {
			gatewayMgr.shutdown();
			return "Gracefull stop initiated sucesffully";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Problem while initiating Gracefull stop";
		}
		
	}
}
