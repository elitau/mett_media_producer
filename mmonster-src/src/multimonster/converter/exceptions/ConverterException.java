package multimonster.converter.exceptions;

import multimonster.exceptions.MultiMonsterException;

/**
 * @author Holger Velke
 */
public class ConverterException extends MultiMonsterException {

	/**
	 * @param message
	 */
	public ConverterException(String message) {
		super(message);
	}

	/**
	 * @param trbl
	 */
	public ConverterException(Throwable trbl) {
		super(trbl);
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public ConverterException(String message, Throwable trbl) {
		super(message, trbl);
	}

}
