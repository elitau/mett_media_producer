/* Generated by Together */

package multimonster.common.media;

import java.io.Serializable;

public class MIIdentifier implements Serializable {
	private int mINumber;
	private String location;
	/**
	 * @param fileName
	 */
	public MIIdentifier(String location) {
		this.location = location;
	}

	/**
	 * @param fileName
	 */
	public MIIdentifier(String location, int number) {
		this.location = location;
		this.mINumber = number;
	}

	/**
	 * this constructor shall not be used 
	 */
	public MIIdentifier() {
		//by Holger
		mINumber = -1;
		location = null;
		// TODO this shall not be used
	}

	/**
	 * @return
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return
	 */
	public int getMINumber() {
		return mINumber;
	}

	/**
	 * @param string
	 */
	public void setLocation(String string) {
		location = string;
	}

	/**
	 * @param i
	 */
	public void setMINumber(int i) {
		mINumber = i;
	}

}
