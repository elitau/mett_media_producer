package multimonster.edit.exceptions;

import multimonster.exceptions.MultiMonsterException;

/**
 * @author Holger Velke
 */
public class EditException extends MultiMonsterException {

	/**
	 * @param message
	 */
	public EditException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param trbl
	 */
	public EditException(String message, Throwable trbl) {
		super(message,trbl);
	}
	/**
	 * @param trbl
	 */
	public EditException(Throwable trbl) {
		super(trbl);
	}
}
