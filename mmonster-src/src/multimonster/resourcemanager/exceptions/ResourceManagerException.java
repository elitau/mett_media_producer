package multimonster.resourcemanager.exceptions;

import multimonster.exceptions.MultiMonsterException;

/**
 * @author Holger Velke (sihovelk)
 */
public class ResourceManagerException extends MultiMonsterException {
	
	/**
	 * @param message
	 */
	public ResourceManagerException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param trbl
	 */
	public ResourceManagerException(String message, Throwable trbl) {
		super(message, trbl);
	}
	/**
	 * @param trbl
	 */
	public ResourceManagerException(Throwable trbl) {
		super(trbl);
	}
}
