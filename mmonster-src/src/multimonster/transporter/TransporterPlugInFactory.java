package multimonster.transporter;

import java.rmi.RemoteException;

import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multimonster.common.ProtocolId;
import multimonster.common.plugin.PlugInFactory;
import multimonster.common.plugin.PlugInIdentifier;
import multimonster.common.util.EjbCreator;
import multimonster.common.util.EjbHomeGetter;
import multimonster.exceptions.PlugInInstantiationException;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster.systemadministration.interfaces.SystemAdministrationImplHome;
import multimonster.transporter.exceptions.TransporterException;

import org.apache.log4j.Logger;

/**
 * Creates TransporterPlugIns that serve a given protocol.
 * 
 * @author Jörg Meier
 */
public class TransporterPlugInFactory extends PlugInFactory {

	private Logger log;
    private Context context;
    private SystemAdministrationImplHome sysadminHome;
	private static TransporterPlugInFactory instance = null;

	/**
	 * protected constructor,
	 * this is a factory which is created by
	 * getInsance()
	 */
	protected TransporterPlugInFactory() {
		this.log = Logger.getLogger(this.getClass());

		try {
			context = new InitialContext();
			sysadminHome = EjbHomeGetter.getSystemAdministrationHome(context);
			
		} catch (NamingException e) {
			log.error("Couldn't reinit context while activating EJB.");
		}		

	}

	/**
	 * returns a TransporterPlugInFactory for 
	 * "production" of TransporterPlugIns
	 * 
	 * @return
	 */
	public static TransporterPlugInFactory getInstance() {
		
		if (instance == null) {
			synchronized (TransporterPlugInFactory.class) {
				if (instance == null) {
					instance = new TransporterPlugInFactory();
				}
			}
		}
		return instance;
	}

	/**
	 * Creates a Transporter for a given protocol.
	 * 
	 * @param p0
	 * @return
	 */
	public TransporterPlugin getTransporter(ProtocolId protocolId, boolean isInput) throws TransporterException {
		
		log.debug("getTransporter() called.");
		
		TransporterPlugin plugin = null;
		PlugInIdentifier plugInId = null;
		String errorText = "";
		
		if (protocolId == null) {
			errorText = "Protocol is null - aborting.";
			log.error(errorText);						
			throw new TransporterException(errorText);
		}
		
		//log.debug("Have to look for a transporter for protocol " +protocolId.getId());
		plugInId = getPlugInId(protocolId, isInput);
		
		// return PlugIn with investigated plugInId:
		try {
			plugin = (TransporterPlugin) this.getPlugIn(plugInId);
		} catch (PlugInInstantiationException e) {
			errorText = "Couldn't get Plugin for plugInId='" +plugInId.getClassName() +"'.";
			log.error(errorText);
			throw new TransporterException(errorText, e);
		}
		
		return plugin;
	}
	
	private PlugInIdentifier getPlugInId(ProtocolId protocolId, boolean isInput) throws TransporterException{
    	//ProtocolId.pId_mmSimple = multimonster.transporter.plugin.SimpleInputTransporter
    	//ProtocolId.pId_mmSimple = multimonster.transporter.plugin.SimpleTransporter
    	
		SystemAdministrationImpl sysadmin = null;
    	PlugInIdentifier plugInId;
		String errorText = "";

		// trying to get SysAdminEJB
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new TransporterException(errorText);
		}

		try {
			// ask sysadmin to get a pluginid
			plugInId = sysadmin.getTransporterPlugInId(protocolId, isInput);			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new TransporterException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new TransporterException(errorText);
		}

		if (protocolId == null) {
			errorText = "Didn't get a Transporter for Protocol "
					+ protocolId.getId() + ".";
			log.error(errorText);
			throw new TransporterException(errorText);
		}
		
		return plugInId;

    }	
}
