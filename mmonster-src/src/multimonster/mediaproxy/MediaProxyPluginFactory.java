/* Generated by Together */

package multimonster.mediaproxy;

import java.rmi.RemoteException;

import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multimonster.common.FormatId;
import multimonster.common.ProtocolId;
import multimonster.common.Session;
import multimonster.common.media.MOIdentifier;
import multimonster.common.plugin.PlugInFactory;
import multimonster.common.plugin.PlugInIdentifier;
import multimonster.common.util.EjbCreator;
import multimonster.common.util.EjbHomeGetter;
import multimonster.exceptions.PlugInInstantiationException;
import multimonster.mediaproxy.exceptions.MediaProxyException;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster.systemadministration.interfaces.SystemAdministrationImplHome;

import org.apache.log4j.Logger;

public class MediaProxyPluginFactory extends PlugInFactory {
    
	private Logger log;
    private Context context;
    private SystemAdministrationImplHome sysadminHome;
	private static MediaProxyPluginFactory instance = null;

	/**
	 * protected constructor,
	 * this is a factory which is created by
	 * getInsance()
	 */
	protected MediaProxyPluginFactory() {
		this.log = Logger.getLogger(this.getClass());

		try {
			context = new InitialContext();
			sysadminHome = EjbHomeGetter.getSystemAdministrationHome(context);
			
		} catch (NamingException e) {
			log.error("Couldn't reinit context while activating EJB.");
		}		
	}

	/**
	 * returns a MediaProxyPluginFactory for 
	 * "production" of MediaProxyPlugins
	 * 
	 * @return
	 */
	public static MediaProxyPluginFactory getInstance() {
		
		if (instance == null) {
			synchronized (MediaProxyPluginFactory.class) {
				if (instance == null) {
					instance = new MediaProxyPluginFactory();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Creates a InputProxy for a given protocol.
	 * 
	 * @param p0
	 * @return
	 * @throws MediaProxyException
	 */
    public MediaProxyPlugin getInputProxy(Session session, MOIdentifier mOId, ProtocolId protocolId) throws MediaProxyException {
    	String errorText = "";
		PlugInIdentifier plugInId = null;
		MediaProxyPlugin plugin = null;
   	
    	// check parameter
		if (session == null) {
			errorText = "session is null - aborting.";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else if (mOId == null) {
			errorText = "mOId is null - aborting.";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else if (protocolId == null) {
			errorText = "protocolId is null - aborting.";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else {
			log.debug("Parameter ok.");
		}

		//get a pluginId for the requested protocol
		plugInId = getPlugInId(protocolId, true);
		
		// get plugin, identified by id
		try {
			plugin = (MediaProxyPlugin) getPlugIn(plugInId);
			
		} catch (PlugInInstantiationException e) {
			errorText = "Couldn't get Plugin for plugInId='"
					+ plugInId.getClassName() + "'.";
			log.error(errorText);
			throw new MediaProxyException(errorText, e);
		}

		//initialize the plugin
		plugin.init(session, mOId, protocolId, null);		
		
    	return plugin;	
	}
    
    /**
     * Creates a OutputProxy for a given protocol.
     * 
     * @param session
     * @param mOId
     * @param protocolId
     * @param formatId
     * @return
     * @throws MediaProxyException
     */
    public MediaProxyPlugin getOutputProxy(Session session, MOIdentifier mOId, ProtocolId protocolId, FormatId fId) throws MediaProxyException {
		String errorText = "";
		PlugInIdentifier plugInId = null;
		MediaProxyPlugin plugin = null;

		// check parameter
		if (session == null) {
			errorText = "session is null - aborting.";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else if (mOId == null) {
			errorText = "mOId is null - aborting.";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else if (protocolId == null) {
			errorText = "protocolId is null - aborting.";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else if (fId == null) {
			errorText = "fd is null - aborting.";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else {
			log.debug("Parameter ok.");
		}
		
		//get a pluginId for the requested protocol
		plugInId = getPlugInId(protocolId, false);
		
		// get plugin, identified by id
		try {
			plugin = (MediaProxyPlugin) getPlugIn(plugInId);
			
		} catch (PlugInInstantiationException e) {
			errorText = "Couldn't get Plugin for plugInId='"
					+ plugInId.getClassName() + "'.";
			log.error(errorText);
			throw new MediaProxyException(errorText, e);
		}

		//initialize the plugin
		plugin.init(session, mOId, protocolId, fId);		
		
    	return plugin;	
	}
 
   
    private PlugInIdentifier getPlugInId(ProtocolId protocolId, boolean isInput) throws MediaProxyException{
    	//ProtocolId.pId_RAW_SOCKET = multimonster.mediaproxy.plugin.RawSocketInputProxy
    	//ProtocolId.pId_HTTP = multimonster.mediaproxy.plugin.HttpProxy
    	
		SystemAdministrationImpl sysadmin = null;
    	PlugInIdentifier plugInId;
		String errorText = "";

		// trying to get SysAdminEJB
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new MediaProxyException(errorText);
		}

		try {
			// ask sysadmin to get a pluginid
			plugInId = sysadmin.getProxyPlugInId(protocolId, isInput);			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new MediaProxyException(errorText);
		}

		if (protocolId == null) {
			errorText = "Didn't get a Proxy for Protocol "
					+ protocolId.getId() + ".";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		}
		
		return plugInId;

    }
}
