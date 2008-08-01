package multimonster.common;

import java.io.Serializable;

import multimonster.exceptions.MultiMonsterException;

/**
 * Container to authorize a User. <br>
 * Contains a UserIdentifier and a corresponding sharedSecret to
 * validate the User.
 * 
 * @author Jörg Meier
 *
 */
public class AuthData implements Serializable{
    
    /** identifies a User */
	private UserIdentifier uid;
    
	/** the secret that authorizes the user 
	 * (e. g. a password) */
	private Object sharedSecret;

	/**
	 * Constructs AuthData for a given UserIdentifier and the corresponding
	 * sharedSecret to that uid.
	 * 
	 * @param uid
	 * @param sharedSecret
	 * @throws MultiMonsterException if one of the parameters is null
	 */
	public AuthData(UserIdentifier uid, Object sharedSecret){ // throws MultiMonsterException{
		
		if ((uid != null) && (sharedSecret != null)){
			this.uid = uid;
			this.sharedSecret = sharedSecret;
			
		} else {
			throw new IllegalArgumentException("A Parameter was null: couldn't create AuthData");
	   		//throw new MultiMonsterException("A Parameter was null: couldn't create AuthData");		
		}
		
	}
	
	/**
	 * @return
	 */
	public UserIdentifier getUid() {
		return uid;
	}
	
	/**
	 * Checks if given secret equals the AuthData-sharedSecret.
	 * 
	 * @param secretToCompare
	 * @return true if given secret equals the one in that class.
	 */
	public boolean check(Object secretToCompare){
		if (secretToCompare != null){
			if (sharedSecret.equals(secretToCompare)) {
				return true;
			} else {
				return false;
			}
		
		} else {
			return false;
		}
	}




}
