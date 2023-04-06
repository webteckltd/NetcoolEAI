package com.o2.techm.netcool.eai;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import com.o2.techm.netcool.eai.webservice.RemedyUpdateEventService;



@Configuration
public class WebServiceConfig implements ServletContextInitializer {
	private static final Logger logger = LoggerFactory.getLogger(WebServiceConfig.class);
	
	@Autowired
    private RemedyUpdateEventService updateIncSrv;
	
	public void onStartup(ServletContext servletContext) throws ServletException {
	      registerServlet(servletContext);
	  }

	  private void registerServlet(ServletContext servletContext) {
		  logger.debug("register Servlet");
	      ServletRegistration.Dynamic MosServlet = servletContext.addServlet("remedyServlet", updateIncSrv);

	      MosServlet.addMapping("/osiris/uptIncident");
	      MosServlet.setAsyncSupported(true);
	      MosServlet.setLoadOnStartup(2);  
	  }
}