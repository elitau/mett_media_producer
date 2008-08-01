package multimonster.common.media;

import java.io.Serializable;

/**
 * encapsulates the identification of a MediaObject. 
 */
public class MOIdentifier implements Serializable{
    private int moNumber;
    
    public MOIdentifier (int number) {
    	this.moNumber = number;
    }
	/**
	 * @return
	 */
	public int getMoNumber() {
		return moNumber;
	}

}
