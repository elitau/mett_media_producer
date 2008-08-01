package multimonster.common;

import java.util.HashMap;

/**
 * Offers a bijective projection of value pairs
 * 
 * @author Marc Iseler
 */
public class BiHashMap {

	HashMap keyToName;
	HashMap nameToKey;
	
	/**
	 * 
	 */
	public BiHashMap() {
		super();
		keyToName = new HashMap();
		nameToKey = new HashMap();
	}
	
	/**
	 * inserts a new key-name pair
	 * @param key
	 * @param name
	 */
	public void put(Object key, Object name) {
		this.keyToName.put(key, name);
		this.nameToKey.put(name, key);
	}
	
	/**
	 * removes the pair with the given key
	 * @param key
	 */
	public void remove(Object key) {
		this.keyToName.remove(key);
	}
	
	/**
	 * returns the name by the given key
	 * @param key
	 * @return
	 */
	public Object getByKey(Object key) {
		return this.keyToName.get(key);
	}
	
	/**
	 * returns the key by the given name
	 * @param name
	 * @return
	 */
	public Object getByName(Object name) {
		return this.nameToKey.get(name);
	}

}
