package multimonster.common;

import java.io.Serializable;
import java.util.Date;

import multimonster.exceptions.MultiMonsterException;

/**
 * Represents a User.
 * 
 * @author Jörg Meier
 *
 */
public class User implements Serializable {
    
    /** the identifier of the user <br> 
     *  (fix after construction of User) */
	private UserIdentifier id;

    /** the full username <br>
     * (fix after construction of User) */
	private String fullName;
    

    /** a info Text for the user */
    private String info;

    /** the date of birth */    
    private Date birthdate;

    // TODO implement Group-class and use it here
    /** the groups the user belongs to */
    private int[] groups;
    
    /**
     * Constructs a User with a give identifier and a username.
     * 
     * @param id
     * @param fullName
     * @throws MultiMonsterException if one of the parameters is null
     */    
    public User (UserIdentifier id, String fullName) throws MultiMonsterException {
    	if ((id != null) && (fullName != null)){
        	this.id = id;
        	this.fullName = fullName;
    		
    	} else {
    		throw new MultiMonsterException("A Parameter was null: couldn't create User");
    	}
    }

	/**
	 * @return Returns the birthdate.
	 */
	public Date getBirthdate() {
		return birthdate;
	}
	/**
	 * @param birthdate The birthdate to set.
	 */
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	/**
	 * @return Returns the info.
	 */
	public String getInfo() {
		return info;
	}
	/**
	 * @param info The info to set.
	 */
	public void setInfo(String info) {
		this.info = info;
	}
}
