package multimonster.common.edit;

import java.io.Serializable;

import multimonster.common.UniqueIdentifier;

/**
 * Unique identifier of an EditJob. It is used to
 * reference an EditJob in the Edit component.
 * 
 * @author Holger Velke (sihovelk)
 */
public class EditJobIdentifier extends UniqueIdentifier 
	implements Serializable {
	
	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "EditJobIdentifier: "+this.getId();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.getId();
	}

}
