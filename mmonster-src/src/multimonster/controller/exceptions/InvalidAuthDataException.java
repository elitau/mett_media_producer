/*
 * Created on 19.09.2004
 *
 */
package multimonster.controller.exceptions;

/**
 * @author Jörg Meier
 *
 */
public class InvalidAuthDataException extends Exception {

	/**
	 * 
	 */
	public InvalidAuthDataException() {
		super();
	}

	/**
	 * @param message
	 */
	public InvalidAuthDataException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidAuthDataException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public InvalidAuthDataException(Throwable cause) {
		super(cause);
	}

}
