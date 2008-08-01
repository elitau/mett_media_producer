package multimonster.usermanager;

import multimonster.common.User;
import multimonster.common.UserGroup;

import org.apache.log4j.Logger;

/**
 * @author Marc Iseler
 */
public class GroupManager {


	private Logger log;
	
	/**
	 * 
	 */
	public GroupManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
		 * @see multimonster.usermanager.UserManagerFacade#createUsergroup()
		 */
		public int createUsergroup() {
			log.debug("createUsergroup()");
			return 0;
		}

		/* (non-Javadoc)
		 * @see multimonster.usermanager.UserManagerFacade#remUsergroup(int)
		 */
		public void remUsergroup(int groupID) {
			log.debug("remUsergroup()");

		}
		
	/* (non-Javadoc)
		 * @see multimonster.usermanager.UserManagerFacade#allUserInGroup(int)
		 */
		public User[] allUserInGroup(int groupID) {
			log.debug("allUserInGroup()");
			return null;
		}
		
		/**
		 * @see UserManagerFacade#getAllUsergroups()
		 * @return
		 */
		public UserGroup[] getAllUsergroups() {
			log.debug("getAllUserGroups()");
			return null;
		}

}
