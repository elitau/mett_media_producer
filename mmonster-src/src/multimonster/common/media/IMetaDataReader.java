package multimonster.common.media;

/**
 * The IMetaDataReader allows retrieving a MetaData Property Object with a given key.
 * 
 * @author Frank Müller
 */

public interface IMetaDataReader {
	/**
	 * Gets a MetaData Property Object for a key
	 * 
	 * @param key
	 *            the String that is identifing the property
	 * @return value the Object whose type is matching with the property
	 */
	public Object getValue(String key);
}