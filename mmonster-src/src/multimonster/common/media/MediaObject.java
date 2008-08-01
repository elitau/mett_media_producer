package multimonster.common.media;

import java.io.Serializable;

/**
 * Represents a media-object. 
 */
public class MediaObject  implements Serializable{
    private MetaData metaData;
    private MOIdentifier mOId;
    
    public MediaObject(){
    	this.metaData =null;
    	this.mOId = null;
    }
    
    public MediaObject(MetaData metaData){
    	this.metaData = metaData;
    	this.mOId = null;
	}    
    
	/**
	 * @param mOId
	 * @param metaData
	 */
	public MediaObject(MOIdentifier mOId, MetaData metaData) {
		this.mOId = mOId;
		this.metaData = metaData;
	}

	/**
	 * @return Returns the metaData.
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	/**
	 * @return Returns the mOId.
	 */
	public MOIdentifier getMOId() {
		return mOId;
	}

	/**
	 * @param data
	 */
	public void setMetaData(MetaData data) {
		metaData = data;
	}

	/**
	 * @param identifier
	 */
	public void setMOId(MOIdentifier identifier) {
		mOId = identifier;
	}

}
