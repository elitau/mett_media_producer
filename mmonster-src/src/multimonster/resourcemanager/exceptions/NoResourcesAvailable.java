package multimonster.resourcemanager.exceptions;

/**
 * @author Holger Velke
 */
public class NoResourcesAvailable extends ManagementException {

	/**
	 * @param message
	 */
	public NoResourcesAvailable(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public NoResourcesAvailable(String message, Throwable trbl) {
		super(message, trbl);
	}

	/**
	 * @param trbl
	 */
	public NoResourcesAvailable(Throwable trbl) {
		super(trbl);
	}

}
