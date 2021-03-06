/* Generated by Together */

package multimonster.common;

import java.io.Serializable;

import multimonster.common.media.*;

public class SearchResult implements Serializable{
    private MediaObject mediaObject;
    private Action right;
    
    public SearchResult(MediaObject mo) {
    	this.mediaObject = mo;
    }
    
    public SearchResult(){
    }
    
	/**
	 * @return
	 */
	public MediaObject getMediaObject() {
		return mediaObject;
	}

	/**
	 * @return
	 */
	public Action getRight() {
		return right;
	}

	/**
	 * @param object
	 */
	public void setMediaObject(MediaObject object) {
		this.mediaObject = object;
	}

	/**
	 * @param action
	 */
	public void setRight(Action action) {
		right = action;
	}

}
