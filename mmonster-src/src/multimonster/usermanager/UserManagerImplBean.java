package multimonster.usermanager;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import multimonster.common.Action;
import multimonster.common.AuthData;
import multimonster.common.User;
import multimonster.common.UserGroup;
import multimonster.common.UserIdentifier;
import multimonster.common.media.MOGroup;
import multimonster.common.media.MOIdentifier;
import multimonster.exceptions.MultiMonsterException;
import multimonster.systemadministration.QueryManager;

import org.apache.log4j.Logger;
/**
 * 
 * @author Marc Iseler
 * 
 * @ejb.bean	name = "UserManagerImpl"
 * 				display-name = "UserManagerFacade SessionBean"
 * 				description = "The Facade of the UserManager-Package of MultiMonster"
 * 				view-type = "remote"
 * 				jndi-name = "multimonster/edit/UserManagerFacade"
 * 
 */
public class UserManagerImplBean implements UserManagerFacade, SessionBean {

	private SessionContext ctx;
	private Logger log;
	private RightsManager rightMan;
	private Authenticator authcator;
	private GroupManager groupMan;

	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean checkUserAuth(AuthData authData) {
		return authcator.checkUserAuth(authData);
	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#logout(multimonster.common.UserIdentifier)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void logout(UserIdentifier uid) {
		log.debug("logout() - " + uid.getUid());

	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#createUser()
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void createUser(UserIdentifier UserID, Object sharedSecret) {
		log.debug("createUser()");

		String uid = UserID.getUid();
		String passw = sharedSecret.toString();
		
		QueryManager query = new QueryManager();
		int connNr = query.reserveConnection();
		
		try {
			query.dbOpInsert(
					"insert into mmUser (username, passwd) values ('" + uid + "', '" + passw + "');",
					connNr);
		} catch (MultiMonsterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		query.bringBackConn(connNr);

		

	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#remUser()
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean remUser(UserIdentifier UserID) {
		log.debug("remUser()");
		
		String uid = UserID.getUid();
		
		QueryManager query = new QueryManager();
		int connNr = query.reserveConnection();
		
		try {
			query.dbOpInsert(
					"delete mmUser where username = '" + uid + "';",
					connNr);
		} catch (MultiMonsterException e) {
			log.error("Was not able to remove User " + uid );
			return false;
		}
		query.bringBackConn(connNr);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#createUsergroup()
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public int createUsergroup() {
		log.debug("createUsergroup()");
		return groupMan.createUsergroup();
	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#remUsergroup(int)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void remUsergroup(int groupID) {
		log.debug("remUsergroup()");
		groupMan.remUsergroup(groupID);

	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#addUserToGroup(multimonster.common.UserIdentifier, int)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void addUserToGroup(UserIdentifier user, int group) {
		log.debug("addUserToGroup()");

	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#remUserFromGroup(multimonster.common.UserIdentifier, int)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void remUserFromGroup(UserIdentifier user, int group) {
		log.debug("remUserFromGroup()");

	}
	
	/**
	 * @see UserManagerFacade#getAllUsergroups()
	 * @ejb.interface-method view-type = "remote"
	 */
	public UserGroup[] getAllUsergroups() {
		GroupManager grpMan = new GroupManager();
		return grpMan.getAllUsergroups();
	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#allUserInGroup(int)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public User[] allUserInGroup(int groupID) {
		log.debug("allUserInGroup()");
		return groupMan.allUserInGroup(groupID);
	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#isUserInGroup(multimonster.common.UserIdentifier, int)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean isUserInGroup(UserIdentifier user, int group) {
		log.debug("isUserInGroup()");
		return false;
	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#changeUser(multimonster.common.User)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void changeUser(User user) {
		log.debug("changeUser()");

	}

	/* (non-Javadoc)
	 * @see multimonster.usermanager.UserManagerFacade#login(multimonster.common.UserIdentifier, multimonster.common.AuthData)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean login(AuthData authData) {
		log.debug("login()");
		Authenticator auth = new Authenticator();
		return auth.checkUserAuth(authData);
	}

	/**
	 * @ejb.interface-method view-type = "remote"
	*/
	public boolean isActionAllowed(
		UserIdentifier user,
		MOIdentifier mediaObject,
		Action action) {
		log.debug("isActionAllowed() - delegate to Subcomponent");
		return rightMan.isActionAllowed(user, mediaObject, action);
	}

	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void grantRight(
		int usergroup,
		MOGroup mediaObjectGroup,
		Action action) {
		log.debug("grantRight() - delegate to Subcomponent");
		rightMan.grantRight(usergroup, mediaObjectGroup, action);
	}

	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void revokeRight(int usergroup, MOGroup moGroup, Action newAction) {
		log.debug("revokeRight() - delegate to Subcomponent");
		rightMan.revokeRight(usergroup, moGroup, newAction);
	}

	/* (non-Javadoc)
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	public void ejbActivate() throws EJBException, RemoteException {

	}

	/* (non-Javadoc)
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.ejb.SessionBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext context)
		throws EJBException, RemoteException {
		ctx = context;

	}

	/**
	 * @ejb.create-method 
	 */
	public void ejbCreate() {
		log = Logger.getLogger(this.getClass());
		// RightsManager
		rightMan = new RightsManager();
		// Authenticator
		authcator = new Authenticator();
		// GroupManager
		groupMan = new GroupManager();
	}
}
