package mett.producer;

//import java.util.ArrayList;

//import javax.management.MBeanServer;
//import javax.management.MBeanServerFactory;
//import javax.management.ObjectName;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

//import multimonster.converter.ConverterImplBean;
//import multimonster.converter.exceptions.ConverterException;
import multimonster.common.AuthData;
import multimonster.common.SearchCriteria;
import multimonster.common.SearchResult;
import multimonster.common.UserIdentifier;
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
	private static ConverterImplHome monsterConverter = null;
	private ControllerImplHome controllerHome;

	private MediaProxyImplHome proxyHome;
	
	private String httpSessionID;
	
	private ControllerImpl controller = null;
	
	private String loginName	 = "ede";
	private String loginPassword = "ede";
//	private static MBeanServer mBeanServer = null;
//	private static final String MULTIMONSTER_CONVERTER_JMX_NAME = "multimonster/controller/ControllerFacade || multimonster:service=TCProbeCaller";
	
	/*
	 * Constructor
	 * Instatiates the ConverterImplBean and authenticate the user.
	 */
	public MonsterAdapter(){
//	TODO: get an instance of the ConverterImplBean
		// initialize
		if (controllerHome == null) {
			instantiateControllerHome();
		}

		// wenn kein controller da, dann erzeugen und in Session ablegen:
		try {
			controller = controllerHome.create();
			if (authenticate()){
				log.info("Authentication successfull");
			} else{
				log.error("Could not authenticate!");
			}

		} catch (RemoteException e) {
			log.error("Error calling Controller: " + e.getMessage());

		} catch (CreateException e) {
			log.error("Error calling Controller: " + e.getMessage());
		}


		
	}
	
	/**
	 * PUBLIC
	 */
	
//	The JMX name to locate the java bean with the JavabeanContainer.

	public String getMedia(String key, Object metadata_Metadata) {
		try {
			log.info("Getting media for key: " + key);
			return searchMedia(key);
		} catch (RemoteException e) {
			log.error("Error while getting Media");
			e.printStackTrace();
		}
		return null;
	}

	private static String getMonsterConverterInstance() {
		throw new UnsupportedOperationException();
	}

	private String searchMedia(String key) throws RemoteException {
		SearchResult[] result = null;
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setTitle(key);
		try {
			log.info("Searching for: " + searchCriteria.getTitle());
			result = controller.search(searchCriteria);
		} catch (ControllerException e) {
			log.error("Error during searchMedia()");
			e.printStackTrace();
		}
		return "Found " + ((Integer) result.length).toString() + 
			   " Media for '" +	key + "'." + 
			   "\nFirst Media Title: " + result[0].getMediaObject().getMetaData().getTitle();
		
	}

	
	
	/**
	 * PRIVATE
	 */
	private boolean authenticate() throws RemoteException{
//		TODO: Do not import AuthData
		UserIdentifier uid = new UserIdentifier(loginName);
		Boolean loggedIn = false;
		try {
			log.info("Trying to log in");
			loggedIn = controller.login(new AuthData(uid, (loginPassword)));
		} catch (InvalidAuthDataException e) {
			log.error("Could not login with: \nName: "+loginName + "Pass: " + loginPassword);
			e.printStackTrace();
		} catch (ControllerException e) {
			log.error("ControllerException with: ");
			e.printStackTrace();
		}
		
		return loggedIn;
	}

	public void instantiateControllerHome() {

		ConverterImplHome converterHome = null;
		Context context;
		
		log = Logger.getLogger(this.getClass());
		log.debug("ControllerDispatcher-Servlet init...");
	
		try {
			context = new InitialContext();
//			title = (String) context.lookup("java:/comp/env/Title");
	
			Object ref = context.lookup(ControllerImplHome.JNDI_NAME);
			log.error("Context lookup successful: "+ context.getNameInNamespace());
			controllerHome =
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
		
		
		
		
		
//		try {
//			/* get ConverterHome */
//			context = new InitialContext();
//			
//			log.error("ConverterImplHome.JNDI_NAME: " + ConverterImplHome.JNDI_NAME);
//			Object ref = context.lookup(ConverterImplHome.COMP_NAME);
//			
//			converterHome =	(ConverterImplHome) PortableRemoteObject.narrow(ref,
//					ConverterImplHome.class);
//		} 
//		catch (Exception e) {
//			log.error("Error while getting ConverterImplHome: " + e.getMessage() + " StackTrace: \n");
//			e.printStackTrace();
//		}
//		return converterHome;
	}
	
//	static private MBeanServer getMBeanServer() {
//
//		if (mBeanServer == null) {
//			ArrayList mbeanServers = MBeanServerFactory.findMBeanServer(null);
//			mBeanServer = (MBeanServer) mbeanServers.get(0);
//		}
//
//		return mBeanServer;
//	}
}