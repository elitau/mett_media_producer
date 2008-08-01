package multimonster.common;

import java.io.Serializable;

/**
 * Identifies a User.
 * 
 * @author Jörg Meier
 *
 */
public class UserIdentifier implements Serializable {
    
	/** simple solution: just a String for a username */
	private String uid;
    
    /**
     * constructs a UserIdentifier with a given username.
     * 
     * @param uid
     */
	public UserIdentifier(String uid) {
    	this.uid = uid;
    }
    
	/**
	 * @return the actual uid
	 */
	public String getUid() {
		return uid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/**
	 * compares the given UserIdentifier with that object.
	 */
	public boolean equals(Object obj) {

		if (obj instanceof UserIdentifier){
			UserIdentifier aUId = (UserIdentifier) obj;
			return this.uid.equals(aUId.getUid());
		}
		
		return false;
	}
}
