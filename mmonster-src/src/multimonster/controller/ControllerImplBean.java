package multimonster.controller;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multimonster.common.Action;
import multimonster.common.AdminOperation;
import multimonster.common.AdminResult;
import multimonster.common.AuthData;
import multimonster.common.ConnectionAddress;
import multimonster.common.FormatId;
import multimonster.common.InputOption;
import multimonster.common.OutputOption;
import multimonster.common.ProtocolId;
import multimonster.common.SearchCriteria;
import multimonster.common.SearchResult;
import multimonster.common.Session;
import multimonster.common.UserIdentifier;
import multimonster.common.edit.EditJobIdentifier;
import multimonster.common.edit.EditTaskIdentifier;
import multimonster.common.edit.FilterAction;
import multimonster.common.edit.FilterDetail;
import multimonster.common.edit.FilterPlugInIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MediaObject;
import multimonster.common.media.MetaData;
import multimonster.common.media.MetaDataAccess;
import multimonster.common.resource.Costs;
import multimonster.common.resource.QueueTime;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.util.EjbCreator;
import multimonster.common.util.EjbHomeGetter;
import multimonster.controller.exceptions.ControllerException;
import multimonster.controller.exceptions.InvalidAuthDataException;
import multimonster.edit.exceptions.EditException;
import multimonster.edit.interfaces.EditImpl;
import multimonster.edit.interfaces.EditImplHome;
import multimonster.exceptions.MultiMonsterException;
import multimonster.mediaproxy.exceptions.MediaProxyException;
import multimonster.mediaproxy.interfaces.MediaProxyImpl;
import multimonster.mediaproxy.interfaces.MediaProxyImplHome;
import multimonster.resourcemanager.exceptions.ResourceManagerException;
import multimonster.resourcemanager.interfaces.ResourceManagerImpl;
import multimonster.resourcemanager.interfaces.ResourceManagerImplHome;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster.systemadministration.interfaces.SystemAdministrationImplHome;
import multimonster.usermanager.interfaces.UserManagerImpl;
import multimonster.usermanager.interfaces.UserManagerImplHome;

import org.apache.log4j.Logger;

/**
 * @author Jörg Meier
 * 
 * @ejb.bean	name = "ControllerImpl"
 * 				display-name = "ControllerFacade SessionBean"
 * 				description = "The Facade of the Controller-Package of MultiMonster"
 * 				view-type = "remote"
 * 				jndi-name = "multimonster/controller/ControllerFacade"
 * 				type="Stateful"
 */
public class ControllerImplBean implements SessionBean, ControllerFacade {
    
    /* THIS IS A STATEFUL SESSION BEAN, SO
     * all fields must be serializable or of type
     * javax.ejb.SessionContext, javax.ejb.EJBHome, javax.ejb.EJBObject, 
     * javax.jta.UserTransaction, javax.naming.Context 
     */
	
	private SessionContext ctx;
    private Logger log;
    private Context context;
    private MediaProxyImplHome mediaProxyHome;
    private SystemAdministrationImplHome sysadminHome;
    private UserManagerImplHome usermngHome;
    private ResourceManagerImplHome resMngHome;
    private EditImplHome editHome;
    
    /** the multimonster-Session */
    private Session session;

    
    
    public void setSessionContext(SessionContext context) throws RemoteException, EJBException {
        ctx = context;
    }

    public void ejbActivate() throws EJBException {
    	//reinit all nonserializable fields
    	log = Logger.getLogger(this.getClass());
    	try {
			context = new InitialContext();
		} catch (NamingException e) {
			log.error("Couldn't reinit context while activating EJB.");
		}
    	
    	String sid = "[null]";
    	if (session != null){
    		sid = session.getId();
    	}
    	
    	log.debug("Controller for Session (" +sid +") was activated.");
    }

    public void ejbPassivate() throws EJBException {
       	String sid = "[null]";
    	if (session != null){
    		sid = session.getId();
    	}
    	log.debug("Controller for Session (" +sid +") will be passivated.");
    	
    	//set all  nonserializable fields to null
    	log = null;
    	context = null;
    	
     }

    public void ejbRemove() throws EJBException {
    	log.debug(this.getClass().getName() + " EJB removed.");
    }

    /**
     * @ejb.create-method 
     * 
     * @throws CreateException
     * @throws EJBException
     */
	public void ejbCreate() throws CreateException, EJBException {
		log = Logger.getLogger(this.getClass());
		log.debug("EJB created.");
		
		try {
			context = new InitialContext();
			
			mediaProxyHome = EjbHomeGetter.getMediaProxyHome(context);
			sysadminHome = EjbHomeGetter.getSystemAdministrationHome(context);
			usermngHome = EjbHomeGetter.getUserManagerHome(context);
			resMngHome = EjbHomeGetter.getResourceManagerHome(context);
			editHome = EjbHomeGetter.getEditHome(context);

		} catch (Exception e) {
			log.error(
				"Couldn't get referenced EJB-Home-Interfaces: "
					+ e.getMessage());
		}
	}
	
	/**
	 * checks if the session in this stateful EJB is valid.
	 * 
	 * @return
	 */
	private boolean isSessionValid(){
		if (session != null){
			if (session.isValid()){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean login(AuthData auth) throws ControllerException, InvalidAuthDataException{
		
		boolean successfulAuthenticated = false;
		
		UserIdentifier uid = auth.getUid();
		
		UserManagerImpl usermng = null;
		
		String sessionId = "";
		
		String errorText;
		
		// check if Session is already valid
		if (isSessionValid()) {
			errorText = "You cannot login more than once.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}
		

		log.debug(
			"login() called (user: "
				+ uid.getUid()
				+ ").");
		
		/* trying to get UserManager */
		try {
			usermng = EjbCreator.createUserManager(usermngHome, context);			
						
		} catch (Exception e) {
			errorText = "Couldn't get UserManager:" +e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}

		/* check if User is valid by calling UserManager */
		try {
			if ( usermng.login(auth) == true ) {
				/* user valid */
				//create new Session with uid as Session-ID

				sessionId = String.valueOf( this.hashCode() ) + "_" +auth.getUid().getUid();
				
				// save the MultiMonster-Session in this stateful session-bean				
				session = new Session(sessionId,  uid);
				log.debug("Created a new Session with ID: " +sessionId);
				
				successfulAuthenticated = true;

			} else {
				/* user invalid */
				errorText = "User (" +auth.getUid().getUid() +") invalid."; 
				log.info(errorText);
				throw new InvalidAuthDataException(errorText);
				
			}
			
			usermng.remove();
			
		} catch (RemoteException e1) {
			log.error(e1.getMessage());
			
		} catch (RemoveException e) {
			log.error(e.getMessage());
			
		} catch (MultiMonsterException e) {
			log.error(e.getMessage());
		
		}
				
		return successfulAuthenticated;
	}

	/**
	 * @throws ControllerException
	 * @ejb.interface-method view-type = "remote"
	 */
	public void logout() throws ControllerException {
		String errorText;
		log.debug("logout() called.");
		
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}

		UserIdentifier uid = null;
		UserManagerImpl usermng = null;

		uid = session.getUid();
		
		/* trying to get UserManager */
		try {
			usermng = EjbCreator.createUserManager(usermngHome, context);			
			
		} catch (Exception e) {
			log.error("Couldn't get UserManager, returning null." +e.getMessage());
			return;
		}

		/* inform usermanager */
		try {
			usermng.logout(uid);
			
			usermng.remove();
			
		} catch (RemoteException e1) {
			log.error(e1.getMessage());
			
		} catch (RemoveException e) {
			log.error(e.getMessage());
			
		}

		session.inValidate();
		
		// remove stateful session bean
		this.ejbRemove();
		
		return;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#authorize(multimonster.controller.Session, multimonster.common.MediaObject, multimonster.common.Format, multimonster.common.Action)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean authorize(Session s, MOIdentifier mo, Action action) throws ControllerException{
		log.debug("authorize() called.");
		boolean isAllowed = false;
		String errorText;
		
		UserManagerImpl usermng = null;
		
		// parameter check
		if (mo == null){
			errorText = "mo is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);	
		}
		if (action == null){
			errorText = "action is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);	
		}		
		if (s == null){
			errorText = "session is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);	
		}
		
		session = s;

		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}
		
		/* trying to get UserManager */
		try {
			usermng = EjbCreator.createUserManager(usermngHome, context);			
			
		} catch (Exception e) {
			log.error("Couldn't get UserManager, returning null." +e.getMessage());
			return isAllowed;
		}

		/* ask usermanager */
		try {
			/*
			 * the Format (fId) is actually ignored;
			 * the User gets only Formats that he is allowed to play.  
			 */
			
			isAllowed = usermng.isActionAllowed(session.getUid(), mo, action);
			
			usermng.remove();
			
		} catch (RemoteException e1) {
			log.error(e1.getMessage());
			
		} catch (RemoveException e) {
			log.error(e.getMessage());
			
		}
		
		return isAllowed;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#getInputOptions()
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public InputOption[] getInputOptions() throws ControllerException {
		log.debug("getInputOptions() called.");
		
		InputOption[] inputOptions = null;
		UserIdentifier uId = null;
		SystemAdministrationImpl sysadmin = null;
		String errorText = "";
				
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}

		uId = session.getUid();
		
		// trying to get SysAdminEJB
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}

		try {
			/* ask Sysadmin to get possible inputOptions for user */
			inputOptions = sysadmin.getInputOptions(uId);
			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		if (inputOptions == null){
			errorText = "SysAdmin didn't return any inputOptions.";
			log.error(errorText);
			throw new ControllerException(errorText);			
		} else if (inputOptions.length < 1){
			errorText = "No inputOptions available.";
			log.info(errorText);
		} else if (inputOptions[0].getProtocol() == null){
			errorText = "No inputOptions available.";
			log.info(errorText);
		}	
		
		return inputOptions;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#getOutputOptions(multimonster.common.MediaObject)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public OutputOption[] getOutputOptions(MOIdentifier mOId) throws ControllerException {
		
		log.debug("getOutputOptions() called.");
		
		OutputOption[] outputOptions = null;
		UserIdentifier uId = null;	
		SystemAdministrationImpl sysadmin = null;
		String errorText = "";
		
		// parameter check
		if (mOId == null){
			errorText = "mOId is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);			
		} else {
			log.debug("parameter ok.");
		}

		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}
		
		uId = session.getUid();
		
		// trying to get SysAdminEJB
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}

		try {
			/* ask Sysadmin to get possible OutputOptions for mOId and user */
			outputOptions = sysadmin.getOutputOptions(uId, mOId);
			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		if (outputOptions == null){
			errorText = "SysAdmin didn't return any outputOptions.";
			log.error(errorText);
			throw new ControllerException(errorText);			
		} else if (outputOptions.length < 1){
			errorText = "No outputOptions available.";
			log.info(errorText);
		}
		
		return outputOptions;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#prepareInsert(multimonster.common.Protocol, multimonster.common.MetaData)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public ConnectionAddress prepareInsert(ProtocolId protocolId, MetaData metaData) throws ControllerException{
		log.debug("prepareInsert() called.");
		ConnectionAddress addr = null;	

		SystemAdministrationImpl sysadmin = null;
		MediaObject mo = null;
		UserIdentifier uid = null;
		MOIdentifier mOId = null;
		MediaProxyImpl proxy = null;	
		String errorText = "";		

		// parameter check
		if (protocolId == null) {
			errorText = "protocol is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);
		} else if (metaData == null) {
			errorText = "metaData is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);			
		} else {
			 log.debug("parameter ok.");
		}
		
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}
		uid = session.getUid();
		
		mo = new MediaObject(metaData);

		// trying to get SysAdminEJB
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);		
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		try {			
			/* calling SysAdmin to search and get the result */
			mOId = sysadmin.addMediaObject(mo, uid);
			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (MultiMonsterException e){
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		if (mOId == null){
			errorText = "Got no mOId from Sysadmin.";
			log.error(errorText);
			throw new ControllerException(errorText);			
		} else {
			log.debug("Got mOId " +mOId.getMoNumber());
		}
		
		
		// trying to get MediaProxyEJB
		try {
			proxy = EjbCreator.createMediaProxy(mediaProxyHome, context);					
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);			
		}

		try {
			/* call MediaProxy to prepare for input and get ConnectionAddress */
			addr = proxy.getInputProxy(session, mOId, protocolId);
			
			proxy.remove();		
			
		} catch (RemoteException e) {
			errorText = "Error calling remote Object: " +e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);			
		} catch (RemoveException e) {
			errorText = "Couldn't remove MediaProxy: " +e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);			
		} catch (MediaProxyException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);			
		}
		
		if (addr == null){
			errorText = "Got no ConnectionAdress from MediaProxy";
			log.error(errorText);
			throw new ControllerException(errorText);						
		}

		return addr;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#prepareOutput(multimonster.common.MediaObject, multimonster.common.OutputOption)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public ConnectionAddress prepareOutput(MOIdentifier mOId, OutputOption oo)  throws ControllerException {
		log.debug("prepareOutput() called.");
		
		ConnectionAddress addr = null;	
		MediaProxyImpl proxy = null;	
		FormatId fId = null;
		ProtocolId protocolId = null;
		String errorText = "";		

		// parameter check
		if (mOId == null) {
			errorText = "mOId is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);
		} else if (oo == null) {
			errorText = "outputOption is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);			
		} if ((fId = oo.getFormat().getFormatId()) == null) {
			errorText = "outputOption doesn't contain format - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);						 
		} else if ((oo.getProtocol() == null) || (protocolId = oo.getProtocol().getProtocolID()) == null) {
			errorText = "outputOption doesn't contain protocol - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);			
		} else {
			 log.debug("parameter ok.");
		}

		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}		
		
		// trying to get MediaProxyEJB
		try {
			proxy = EjbCreator.createMediaProxy(mediaProxyHome, context);					
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);			
		}

		try {
			/* call MediaProxy to prepare for output and get ConnectionAddress */
			addr = proxy.getOutputProxy(session, mOId, fId, protocolId);
			
			proxy.remove();		
			
		} catch (RemoteException e) {
			errorText = "Error calling remote Object: " +e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);			
		} catch (RemoveException e) {
			errorText = "Couldn't remove MediaProxy: " +e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);			
		} catch (MediaProxyException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);			
		}
		
		if (addr == null){
			errorText = "Got no ConnectionAdress from MediaProxy";
			log.error(errorText);
			throw new ControllerException(errorText);						
		}
		
		return addr;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#setMetaData(multimonster.common.MediaObject, multimonster.common.MetaData)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void setMetaData(MediaObject mo) throws ControllerException{
		log.debug("setMetaData() called.");
		String errorText;
		SystemAdministrationImpl sysadmin;
		UserIdentifier uid;
		
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}		

		uid = session.getUid();
		
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);		
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		try {			
			/* calling SysAdmin to set metadata */
			sysadmin.modifyMediaObject(mo, uid);
			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} 		
		
		return;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#getMetaData(multimonster.common.MOIdentifier)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public MetaDataAccess getMetaData(MOIdentifier mOId) throws ControllerException {
		log.debug("getMetaData() called.");
		
		MetaDataAccess metaData;
		String errorText;
		SystemAdministrationImpl sysadmin;

		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}		
		
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);		
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		try {			
			/* calling SysAdmin  */
			metaData = sysadmin.getMetaData(mOId);
			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} 		
		
		return metaData;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#deleteMediaObject(multimonster.common.MediaObject)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean deleteMediaObject(MOIdentifier mOId) throws ControllerException {
		log.debug("deleteMediaObject() called.");
		
		boolean ret = false;
		String errorText;
		SystemAdministrationImpl sysadmin;
		UserIdentifier uid;
		
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}		

		uid = session.getUid();
		
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);		
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		try {			
			/* calling SysAdmin */
			sysadmin.remMediaObject(mOId, uid);
			ret = true;
			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (MultiMonsterException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} 		
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#search()
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public SearchResult[] search(SearchCriteria search) throws ControllerException {
		log.debug("search() called.");
		
		SearchResult[] result = null;
		SystemAdministrationImpl sysadmin;
		String errorText = "";
		
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}		

		// parameter check
		if (search == null){
			errorText = "search is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);
		} else {
			log.debug("parameter ok.");
		}
		
		// trying to get SysAdminEJB
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);		
						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		try {			
			/* calling SysAdmin to search and get the result */
			result = sysadmin.search(search);
			
			sysadmin.remove();
			
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (MultiMonsterException e){
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		if (result == null){
			errorText = "Searchresult from SysAdmin is null.";
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		return result;		
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#administration(multimonster.common.AdminOperation)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public AdminResult administration(AdminOperation operation) throws ControllerException {
		log.debug("administration() called.");	
		AdminResult result = null;
		SystemAdministrationImpl sysadmin;
		UserIdentifier uid;
		String errorText = "";

		if (operation == null){
			errorText = "operation is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);			
		}
		
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}		
		
		uid = session.getUid();

		//TODO Implement admin-operations
		//dispatch the admin-operation
		if (operation.getOperationID() == AdminOperation.SHUTDOWN_SERVER){
			log.info("shutdown of server is requested, don't know how to do that - won't do anything!");
			
		} else {
			errorText = "unknown command: " +operation.getOperationID()
						+" (" +operation.getDescription() +").";
			log.error(errorText);
			throw new ControllerException(errorText);						
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#getFilterOptions(multimonster.common.MOIdentifier)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public FilterDetail[] getFilterOptions(MOIdentifier mOId) {
		log.warn(this.getClass().getName() + "getFilterOptions() called (NOT IMPLEMENTED).");
		
		FilterDetail[] filterInfo = null;
		
		// TODO implement getFilterOptions
		return filterInfo;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#getEditJob(multimonster.common.MOIdentifier)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public EditJobIdentifier getEditJob(MOIdentifier mo) throws ControllerException{
		
		String errorText;
		EditJobIdentifier ejid = null;
		EditImpl editFacade = null;
		UserIdentifier uid = null;
		
		log.debug("getEditJob() called.");

		if (mo == null){
			errorText = "mo is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);						
		}
		
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}		
		
		uid = session.getUid();
			
		/* trying to get EditFacade */
		try {
			editFacade = EjbCreator.createEdit(editHome, context);						
		} catch (Exception e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		try {
			ejid = editFacade.getJob(uid, mo);	
			
			editFacade.remove();
			
		} catch (RemoteException e){
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (EditException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			throw new ControllerException(errorText);
		}
		
		return ejid;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#finishEditJob(multimonster.common.EditJobIdentifier, multimonster.common.MetaData, multimonster.common.Duration)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public MediaObject finishEditJob(EditJobIdentifier job, MetaData metaData, QueueTime maxQT) {
		log.warn("finishEditJob() called (NOT IMPLEMENTED).");
		
		MediaObject mo = null;
		// TODO implement finishEditJob
		
		return mo;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#getEditJobList()
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public EditJobIdentifier[] getEditJobList() {
		log.warn("getEditJobList() called (NOT IMPLEMENTED).");
		
		EditJobIdentifier[] ejids = null;
		// TODO implement getEditJobList
		
		return ejids;
	}
	
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
    public EditTaskIdentifier addTaskToEditJob(EditJobIdentifier jobId, FilterPlugInIdentifier filterId, FilterAction action) {
		log.warn("addTaskToEditJob() called (NOT IMPLEMENTED).");
		
		EditTaskIdentifier etid = null;
		// TODO implement addTaskToEditJob
		
		return etid;
    }

	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void removeEditTask(
		EditJobIdentifier job,
		EditTaskIdentifier task) {
			log.warn("removeEditTask() called (NOT IMPLEMENTED).");
		
		// TODO implement removeEditTask
		
		return;
	}

	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void abortEditJob(EditJobIdentifier job) {
		log.warn("abortEditJob() called (NOT IMPLEMENTED).");
		
		// TODO implement abortEditJob
			
		return;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#requestResources(multimonster.controller.Session, multimonster.common.MediaObject, multimonster.common.Format, multimonster.common.Protocol, multimonster.common.Action)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public ResourceRequestIdentifier requestResources(Session s, MOIdentifier moid, FormatId fId, ProtocolId protocolId, Action action) throws ControllerException{
		log.debug("requestResources() called.");
		
		ResourceRequestIdentifier rrid = null;
		String errorText;
		UserIdentifier uid;
		Costs costs = null;
		SystemAdministrationImpl sysadmin = null;
		ResourceManagerImpl resMng = null;
		
		// TODO add parameter-check
		if (s == null){
			errorText = "session is null - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);	
		}
		
		session = s;
		
		
		// check if Session is still valid
		if (!isSessionValid()) {
			errorText = "Session in this EJB is invalid - aborting.";
			log.error(errorText);
			throw new ControllerException(errorText);				
		}		
		
		uid = session.getUid();

		/* trying to get SysAdminEJB */
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);		
						
		} catch (Exception e) {
			log.error(e.getMessage());
			return rrid;
		}

		/* trying to get ResourceManagerEJB */
		try {
			resMng = EjbCreator.createResourceManager(resMngHome, context);		
						
		} catch (Exception e) {
			log.error(e.getMessage());
			return rrid;
		}

		try {
				
			/* calling SysAdmin to get Costs */
			if (fId != null){
				costs = sysadmin.calculateCosts(moid, fId, protocolId, action);
			} else {
				//e. g. for input formatid is null
				costs = sysadmin.calculateCosts(moid, protocolId, action);
			}
			
			sysadmin.remove();		
			
			/* calling the ResourceManager to get a ResourceRequestIdentifier */
			rrid = resMng.requestResources(uid, costs);		
			
			resMng.remove();	
			
		} catch (RemoteException e) {
			log.error(e.getMessage());
			
		} catch (RemoveException e) {
			log.error(e.getMessage());
			
		} catch (ResourceManagerException e) {
			log.error(e.getMessage());
		}		

		return rrid;
	}

	/* (non-Javadoc)
	 * @see multimonster.controller.ControllerFacade#releaseResource(multimonster.common.ResourceRequestIdentifier)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void releaseResource(ResourceRequestIdentifier rrId) throws ControllerException{
		log.debug("releaseResource() called.");
		SystemAdministrationImpl sysadmin = null;
		ResourceManagerImpl resMng = null;
		Costs realCosts = null;
		
		// check if Session is still valid NOT NECCESSARY HERE
		
		/* trying to get SysAdminEJB */
		try {
			sysadmin = EjbCreator.createSystemAdministration(sysadminHome, context);		
			
		} catch (Exception e) {
			log.error(e.getMessage());
			return;
		}

		/* trying to get ResourceManagerEJB */
		try {
			resMng = EjbCreator.createResourceManager(resMngHome, context);		
			
		} catch (Exception e) {
			log.error(e.getMessage());
			return;
		}
		
		try {
			
			realCosts = resMng.releaseResources(rrId);			
			// TODO add interface to SysAdmin, call method
			//sysadmin.realCosts()		
			
		} catch (ResourceManagerException e) {
			log.error("problem releasing resources: " +e);
		} catch (RemoteException e) {
			log.error(e.getMessage());
		}
		
		return;		
	}


}
