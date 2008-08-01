package multimonster.common;

import java.io.Serializable;


/**
 * The server-specific identifier of a format.
 * 
 * @author Holger Velke (sihovelk)
 */
public class FormatId implements Serializable{

	/**
	 * Known formats
	 */
	public static final String fId_MPEG_1_LOW = "MPEG-1-LOW";
	public static final String fId_MPEG_1_MID = "MPEG-1-MID";
	public static final String fId_MPEG_1_HI = "MPEG-1-HI";
	public static final String fId_MPEG_2_LOW = "MPEG-2-LOW";
	public static final String fId_MPEG_2_MID = "MPEG-2-MID";
	public static final String fId_MPEG_2_HI = "MPEG-2-HI";
	public static final String fId_DIVX4_LOW = "DIVX4-LOW";
	public static final String fId_DIVX4_MID = "DIVX4-MID";
	public static final String fId_DIVX4_HI = "DIVX4-HI";

	/**
	 * The server-specific format-string
	 */
	private String id;
	
	/**
	 * @param id The server-specific format-string
	 */
	public FormatId(String id) {
		
		if (id == null){
			throw new IllegalArgumentException("null not allowed");
		}
		this.id = id.toUpperCase();
	}

	/**
	 * @return The server-specific format-string
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		
		if (obj instanceof FormatId){
			FormatId fId = (FormatId) obj;
			return fId.id.equals(this.id);
		} else if (obj instanceof String){
			String idString = (String) obj;
			return  this.id.equals(idString.toUpperCase());
		} 		
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getId();
	}

}
