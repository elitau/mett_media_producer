package multimonster.resourcemanager.exceptions;

/**
 * @author Holger Velke
 */
public class ManagementException extends ResourceManagerException {

	/**
	 * @param message
	 */
	public ManagementException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public ManagementException(String message, Throwable trbl) {
		super(message, trbl);
	}

	/**
	 * @param trbl
	 */
	public ManagementException(Throwable trbl) {
		super(trbl);
	}

}
