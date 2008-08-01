package multimonster.common;

import java.io.Serializable;

/**
 * Hash is used to offer an serializable hash
 * 
 * @author Holger Velke (sihovelk)
 */
public class Hash implements Serializable{

	private int hash;
	
	public Hash(int hash) {
		this.hash = hash;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {		
		return hash;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (new Integer(hash)).toString();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {

		if (obj instanceof Hash){
			Hash aHash = (Hash) obj;
			return this.hash == aHash.hash;
		}
		return false;
	}

}
