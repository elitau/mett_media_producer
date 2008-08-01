/*
 * Created on 22.02.2004
 *
 */
package multimonster.controller.exceptions;

import multimonster.exceptions.MultiMonsterException;

/**
 * @author Jörg Meier
 *
 */
public class ControllerException extends MultiMonsterException {

	/**
	 * 
	 */
	public ControllerException() {
		super();
	}

	/**
	 * @param message
	 */
	public ControllerException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ControllerException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ControllerException(Throwable cause) {
		super(cause);
	}

}
