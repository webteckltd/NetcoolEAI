package com.o2.techm.netcool.eai.o2gateway.plugins;

import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.o2gateway.NetcoolConnector;
import com.o2.techm.netcool.eai.o2gateway.monitor.MonitorTask;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginException;
import com.o2.techm.netcool.eai.o2gateway.pluginframework.PluginManager;

/**
 * @author trenaman
 */
public class MonitorPlugin implements Plugin
{
	private static final Logger log = LoggerFactory.getLogger(MonitorPlugin.class);

    public static final String NAME = "MONITOR";    

    private int gwSleep; 
    private MonitorTask monitorTask;
    private Timer mTimer; 
    
    /**
     * 
     */
    public MonitorPlugin()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declareVariables()
     */
    public Vector declareVariables() throws PluginException
    {
        Vector ret = new Vector();         
        return ret;
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#declarePluginDependencies()
     */
    public Vector declarePluginDependencies() throws PluginException
    {
        Vector ret = new Vector(); 
        ret.add(SocketProbePlugin.NAME);
        return ret;
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#preInit(com.o2.osiris.PluginManager)
     */
    public void preInit(PluginManager mgr) throws PluginException
    {
        gwSleep = Integer.parseInt(mgr.getString(NetcoolConnector.GATEWAY_HEARTBEAT)); 
    }
    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#postInit(com.o2.osiris.PluginManager)
     */
    public void postInit(PluginManager mgr) throws PluginException
    {
        monitorTask = new MonitorTask(mgr);         
        mTimer = new Timer();         
        mTimer.schedule(monitorTask, 0, gwSleep * 1000);  
        log.debug("MonitorTask created and Scheduled Suvesffully");
    }

    /**
     * @see com.o2.techm.netcool.eai.o2gateway.pluginframework.Plugin#shutdown()
     */
    public void shutdown() throws PluginException
    {
        if (monitorTask != null) {
            monitorTask.shutdown();
            mTimer.cancel();
        }
    }
    public String getName()
    {
        return NAME;
    }
}
