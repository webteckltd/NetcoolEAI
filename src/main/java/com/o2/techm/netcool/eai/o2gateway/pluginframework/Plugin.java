/*
 * Created on May 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.techm.netcool.eai.o2gateway.pluginframework;

import java.util.Vector;


/**
 * @author trenaman
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Plugin
{
    public abstract Vector declareVariables() throws PluginException;
    public abstract Vector declarePluginDependencies() throws PluginException;
    public abstract void preInit(PluginManager mgr) throws PluginException;
    public abstract void postInit(PluginManager mgr) throws PluginException;
    public abstract void shutdown() throws PluginException;
    public abstract String getName();
}
