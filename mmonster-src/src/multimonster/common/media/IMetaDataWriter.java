package multimonster.common.media;


/**
 * The IMetaDataWriter Interface allows setting a MetaData Property Object with a given key. 
 * The key is defined by the Multimonster Server
 * @author Frank Müller
 */
public interface IMetaDataWriter extends IMetaDataReader {
	/**
	 * Sets a MetaData Property Object for a key
	 * @param key the String that is identifing the property
	 * @param value the Object whose type is matching with the property 
	 */
	public void setValue(String key, Object value);
}