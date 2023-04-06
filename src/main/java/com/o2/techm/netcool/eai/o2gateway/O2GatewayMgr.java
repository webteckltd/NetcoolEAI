package com.o2.techm.netcool.eai.o2gateway;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.o2.techm.netcool.eai.NetcoolEAIApplication;
import com.o2.techm.netcool.eai.manonsite.ManOnSite;
import com.o2.techm.netcool.eai.o2gateway.netcool.O2GatewayReaderImpl;
import com.o2.techm.netcool.eai.o2gateway.netcool.O2GatewayWriterImpl;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.plugins.ReaderPlugin;
import com.o2.techm.netcool.eai.o2gateway.sybase.SybaseManager;
import com.o2.techm.netcool.eai.webservice.RemedyUpdateEventService;

@Component
public class O2GatewayMgr
{
    private static String resourceBundleName = "O2GatewayMgr";
    private static final Logger log = LoggerFactory.getLogger(O2GatewayMgr.class);
    private O2GatewayMgrPluginManager mgr=null;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private ManOnSite mosApp;
    
    @Autowired
    private RemedyUpdateEventService updateIncSrv;
    
    /**
     * Main Program entry point
     * @param args
     */
    
    @PostConstruct
    public void init() {
    	log.info(" STARTING GATEWAY MANAGER COMPONENT");          
        try
        {
            mgr  = new O2GatewayMgrPluginManager(NetcoolEAIApplication.getArgs(), resourceBundleName);
            mgr.loadPlugins();
            
            ReaderPlugin readerPlugin = (ReaderPlugin) mgr.getPlugins().get(ReaderPlugin.NAME);
            O2GatewayReaderImpl netcoolGatewayReader =  readerPlugin.getReaderImpl();
            updateIncSrv.setReader(netcoolGatewayReader);
            updateIncSrv.readCfg();
            
            mosApp.setMgr(mgr);
            mosApp.initManOnSiteApp();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("Error",e);
            System.exit(1);
        }         
        log.info(" GATEWAY MANAGER COMPONENT STARTED SUCESSFULLY ");      
    }

    
    
    public String  getStatusCheck(){
     try {
		 StringBuffer  buff  = new StringBuffer();
		 buff.append(SybaseManager.monitorME());
		 buff.append(System.lineSeparator());
		 
		 buff.append(O2GatewayReaderImpl.monitorME());
		 buff.append(System.lineSeparator());
		 
		 buff.append(O2GatewayWriterImpl.monitorME());
		 buff.append(System.lineSeparator());
		 
		 buff.append(mosApp.getStatusCheck());	 
		 return buff.toString();
		 
	} catch (Exception e) {
		e.printStackTrace();
		return " Error File Fetching Threads and Pollor status ";
	}
    }
    
    
    
    public  void shutdown()
    { 
    	log.info("Invoking shutdown on Gateway Manager component ");
    	if(mgr != null){
    		try {
				mgr.shutdown();
			} catch (PluginException e) {
				e.printStackTrace();
				log.error("problem while shutting down ", e );
			}
    	}
    	
    	if (null != mosApp){
    		mosApp.shutdown();
    	}
    }
 
}
