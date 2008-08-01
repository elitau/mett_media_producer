package multimonster.resourcemanager.exceptions;

/**
 * @author Holger Velke
 */
public class UnknownRequest extends ManagementException {

	/**
	 * @param message
	 */
	public UnknownRequest(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public UnknownRequest(String message, Throwable trbl) {
		super(message, trbl);
	}

	/**
	 * @param trbl
	 */
	public UnknownRequest(Throwable trbl) {
		super(trbl);
	}

}
