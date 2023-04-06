package com.o2.techm.netcool.eai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class NetcoolEAIApplication {

	private static String[] args ;
	
	public static void main(String[] args) {
		setArgs(args);
		SpringApplication application = new SpringApplication(NetcoolEAIApplication.class);
		application.addListeners(new ApplicationPidFileWriter("NetcoolEAIApplication.pid"));
		application.run();	
	}

	public static String[] getArgs() {
		return args;
	}

	public static void setArgs(String[] args) {
		NetcoolEAIApplication.args = args;
	}
	

}
