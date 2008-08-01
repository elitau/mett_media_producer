package multimonster.usermanager;

import multimonster.common.Action;
import multimonster.common.AuthData;
import multimonster.common.UserGroup;
import multimonster.common.UserIdentifier;
import multimonster.common.User;
import multimonster.common.media.MOGroup;
import multimonster.common.media.MOIdentifier;

public interface UserManagerFacade {
	
	/**
	 * checks users authentication data
	 * without writing login protocol entry
	 * 
	 * @param authData
	 * @param user
	 * @return
	 */
    boolean checkUserAuth(AuthData authData);

    /**
     * destroys user session
     * and writes protocol entry
     * @param uid
     */
    void logout(UserIdentifier uid);

    /**
     * add a new user to the system
     * when using external authentication method, user must exist in external system
     */
    void createUser(UserIdentifier UserID, Object sharedSecret);

    /**
     * removes user from system
     * @return
     */
    boolean remUser(UserIdentifier UserID);

    /**
     * creates a usergroup where users can get categorized
     * @return
     */
    int createUsergroup();

    /**
     * deletes a specified usergroup
     * group must be empty to delete it
     * @param groupID
     */
    void remUsergroup(int groupID);

    /**
     * add a user to a existing usergroup
     * @param user
     * @param group
     */
    void addUserToGroup(UserIdentifier user, int group);

    /**
     * removes a user from the specified group
     * @param user
     * @param group
     */
    void remUserFromGroup(UserIdentifier user, int group);
    
    /**
     * returns all existing UserGroups
     * @return
     */
    public UserGroup[] getAllUsergroups();

    /**
     * returns all users which have been added to usergroup
     * @param groupID
     * @return
     */
    User[] allUserInGroup(int groupID);

    /**
     * checks whether a user is contained in the specified usergroup
     * @param user
     * @param group
     * @return
     */
    boolean isUserInGroup(UserIdentifier user, int group);

    /**
     * changes user properties
     * @param user
     */
    void changeUser(User user);
    
    /**
     * checks whether a specified action is allowed for this user on that mediaobject
     * @param user
     * @param mediaObject
     * @param action
     * @return
     */
	boolean isActionAllowed(
		UserIdentifier user,
		MOIdentifier mediaObject,
		Action action);

	/**
	 * grants right to a usergroup to execute a specified action on elements which are contained in the specified mediaObjectGroup
	 * @param usergroup
	 * @param mediaObjectGroup
	 * @param action
	 */
	void grantRight(int usergroup, MOGroup mediaObjectGroup, Action action);

	/**
	 * revokes a previously granted right
	 * @param usergroup
	 * @param moGroup
	 * @param newAction
	 */
	void revokeRight(int usergroup, MOGroup moGroup, Action newAction);

	/**
	 * checks user authentication data and writes a protocol entry
	 * 
	 * @param authData
	 * @return
	 */
    boolean login(AuthData authData);
}
