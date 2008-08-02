package mett.producer;

//import java.util.ArrayList;

//import javax.management.MBeanServer;
//import javax.management.MBeanServerFactory;
//import javax.management.ObjectName;

import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

//import multimonster.converter.ConverterImplBean;
//import multimonster.converter.exceptions.ConverterException;
import multimonster.common.AuthData;
import multimonster.common.ConnectionAddress;
import multimonster.common.Format;
import multimonster.common.FormatId;
import multimonster.common.OutputOption;
import multimonster.common.Protocol;
import multimonster.common.ProtocolId;
import multimonster.common.SearchCriteria;
import multimonster.common.SearchResult;
import multimonster.common.UserIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MediaObject;
import multimonster.controller.exceptions.ControllerException;
import multimonster.controller.exceptions.InvalidAuthDataException;
import multimonster.controller.interfaces.ControllerImpl;
import multimonster.controller.interfaces.ControllerImplHome;
import multimonster.converter.interfaces.ConverterImplHome;
import multimonster.mediaproxy.interfaces.MediaProxyImplHome;

import org.apache.log4j.Logger;

/**
 * This class is an adapter pattern realised for the Multimonster ControllerImplBean.
 * It authenticates the request, searches for a given key and returns an URI to the 
 * found media which can be streamed by the consumer.
 * 
 * @author elitau
 *
 */
public class MonsterAdapter {
	
	
	/**
	 * ATTRIBUTES
	 */	
	private static Logger log = Logger.getLogger(MonsterWebService.class);
	private ControllerImplHome monsterControllerHome;

	private MediaProxyImplHome proxyHome;
	
	private ControllerImpl monsterController = null;
	
	private String loginName	 = "ede";
	private String loginPassword = "ede";
	private Format format = null;
	private Protocol protocol = null;
	
	/**
	 * Constructor
	 * Instatiates the ConverterImplBean and authenticate the user.
	 */
	public MonsterAdapter(){
		// initialize
		if (monsterControllerHome == null) {
			instantiateControllerHome();
		}
		
		try {
			// wenn kein controller da, dann erzeugen und in Session ablegen:
			if (monsterController == null) {
				getMonsterControllerInstance();
			}
			
			if (authenticate()){
				log.info("Authentication successfull");
			} else{
				log.error("Could not authenticate!");
			}
		} catch (RemoteException e) {
			log.error("Error calling Controller: " + e.getMessage());

		}
		
	}
	
	
	/**
	 * PUBLIC
	 */
	public String getMedia(String key, Object metadata_Metadata) {
		MediaObject mediaObject = null;
		String mediaURI = null;
		try {
			log.info("Searching media for key: " + key);
			mediaObject = searchMedia(key);
			if (mediaObject != null) {
				mediaURI = prepareMediaOutput(mediaObject, null);
			} else {
				mediaURI = "Nothing found";
			}
		} catch (RemoteException e) {
			log.error("Error while getting Media");
			e.printStackTrace();
		}
		return mediaURI;
	}

	
	
	/**
	 * PRIVATE
	 */
	
	/**
	 * Searches with the given key for MediaObjects. Returns a MediaObject if found, else returns null;
	 * 
	 * @param String key - The search criteria
	 * @return {@link MediaObject} The first MedioObject that fits the search key. 
	 */
	private MediaObject searchMedia(String key) throws RemoteException {
		SearchResult[] result = null;
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setTitle(key);
		try {
			log.info("Searching for: " + searchCriteria.getTitle());
			result = monsterController.search(searchCriteria);
			log.info("Found: " + result.length + " results.");
		} catch (ControllerException e) {
			log.error("Error during searchMedia()");
			e.printStackTrace();
		}
		
		if (result.length > 0) {
			return result[0].getMediaObject();
		} else{
			return null;
		}
		
		
	}
	
	/**
	 * Authenticates and prepares the MediaOpbject for access. 
	 * Returns an URI to the prepared MediaObject for direct streaming/downloading.
	 *  
	 * @param mediaObject  
	 * @return URI to the given MediaObject
	 */
	private String prepareMediaOutput(MediaObject mediaObject, Object metadata) {
		OutputOption[] outputOptions = null;
		String uri = null;
		ConnectionAddress addr = null;
		OutputOption oo = null;
		MOIdentifier mOId = mediaObject.getMOId();
		String mediaTitle = mediaObject.getMetaData().getTitle();
		
		try {
			log.info("Getting OutputOptions for: " + mediaTitle);
			outputOptions = monsterController.getOutputOptions(mOId);
			if (outputOptions.length == 0) {
				return "No OutputOptions for " + mediaTitle + " found";
			}
			log.info("Possible OutputOptions("+outputOptions.length+"): " + formatOutputOptions(outputOptions));
			oo = outputOptions[0];
			addr = monsterController.prepareOutput(mOId, oo);
			uri = addr.getUrl().toString();
		} catch (ControllerException e) {
			log.error("Controller Exception while getting MediaObejt Identifier:");
			e.printStackTrace();
		} catch (RemoteException e) {
			log.error("Remote Exception while getting MediaObejt Identifier:");
			e.printStackTrace();
		}
		
		return uri;
	}
	
	
	private boolean authenticate() throws RemoteException{
//		TODO: Do not import AuthData
		UserIdentifier uid = new UserIdentifier(loginName);
		Boolean loggedIn = false;
		try {
			log.info("Trying to log in");
			loggedIn = monsterController.login(new AuthData(uid, (loginPassword)));
		} catch (InvalidAuthDataException e) {
			log.error("Could not login with: \nName: "+loginName + "Pass: " + loginPassword);
			e.printStackTrace();
		} catch (ControllerException e) {
			log.error("ControllerException with: ");
			e.printStackTrace();
		}
		
		return loggedIn;
	}
	
	private void getMonsterControllerInstance() throws RemoteException{
		try {
			monsterController = monsterControllerHome.create();
		} catch (CreateException e) {
			log.error("Error calling Controller: " + e.getMessage());
		}
	}

	private void instantiateControllerHome() {
		Context context;
		
		log = Logger.getLogger(this.getClass());
		log.debug("ControllerDispatcher-Servlet init...");
	
		try {
			context = new InitialContext();
	
			Object ref = context.lookup(ControllerImplHome.JNDI_NAME);
			log.error("Context lookup successful: "+ context.getNameInNamespace());
			monsterControllerHome =
				(ControllerImplHome) PortableRemoteObject.narrow(
					ref,
					ControllerImplHome.class);
	
			ref = context.lookup(MediaProxyImplHome.JNDI_NAME);
			proxyHome =
				(MediaProxyImplHome) PortableRemoteObject.narrow(
					ref,
					MediaProxyImplHome.class);
	
		} catch (NamingException e) {
			log.error("Couldn't get Controller: " + e.getMessage());
			e.printStackTrace();
	
		} catch (Exception e) {
			log.error("Couldn't get Controller: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
//	Helper:
	private String formatOutputOptions(OutputOption[] options){
		String output = "hmm";
		for (int i = 0; i < options.length; i++) {
			output = output.concat(options[i].toString());
		}
		return output;
	}
	
}