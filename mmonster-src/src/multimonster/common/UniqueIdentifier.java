package multimonster.common;

import java.util.Random;

/**
 * This is a common unque identifier. It can be used 
 * in order to creat unique ids.
 * 
 * @author Holger Velke (sihovelk)
 */
public class UniqueIdentifier {

	/** 
	 * Comment for <code>rnd</code>
	 * 
	 * rnd is used to generate a unique Id for each UniqueIdentifier
	 */
	private static Random rnd = new Random();
	
	/**
	 * The unique id.
	 */
	private int id;
	
	/**
	 * This constructor has to be called.
	 */
	public UniqueIdentifier(){
		this.id = rnd.nextInt();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {

		if (obj instanceof UniqueIdentifier){
			UniqueIdentifier aJobId = (UniqueIdentifier) obj;
			return aJobId.id == this.id;
		}
		
		return false;
	}
	
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	
	public String toString() {
		return this.getClass().getName()+":"+id;
	}
}
