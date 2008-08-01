package multimonster.common.media;

import java.io.Serializable;

/**
 * The IMetaData interface combines read and write access for MetaData
 * Properties. 
 * 
 * @author Frank Müller
 *  
 */
public interface IMetaData extends IMetaDataReader, IMetaDataWriter,
		Serializable {
	/**
	 * Gets all available MetaData Property keys
	 * @return the MetaDataProperty keys
	 */
	public String[] getKeys();
	
	/**
	 * Gets the values types which are used for the keys
	 * @return the key types
	 */

	public Class[] getValueTypes();

}