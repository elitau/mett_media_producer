package multimonster.common;

import java.io.Serializable;

import multimonster.exceptions.MultiMonsterException;

/**
 * Represents a User-Session. 
 * @author Jörg Meier
 *
 */
public class Session implements Serializable{

    /** the id of the session; should be unique */
	private String id;
    
	/** the uid of the user to whom the Session belongs */
	private UserIdentifier uid;
	
    /** indicates if the Session is valid */
    private boolean isValid;

	/**
	 * Creates a Session with the given ID.
	 * 
	 * @param sessionId
	 * @param uid
	 * @throws MultiMonsterException if a paramter is null
	 */
	public Session(String sessionId, UserIdentifier uid) throws MultiMonsterException {
		
		if ((sessionId != null) && (uid != null)) {
		
			this.id = sessionId;
			this.uid = uid;
			
			isValid = true;
		} else {
			throw new MultiMonsterException("one parameter was null: couldn't create Session."); 
		}
	}

	/**
	 * @return the Session-ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return Returns the isValid.
	 */
	public boolean isValid() {
		return isValid;
	}
	
	/**
	 * Invalidates the Session.
	 *
	 */
	public void inValidate(){
		isValid = false;
	}
	/**
	 * @return Returns the uid.
	 */
	public UserIdentifier getUid() {
		return uid;
	}
}
