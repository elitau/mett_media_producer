package multimonster.converter.exception;

import multimonster.converter.exceptions.ConverterException;

/**
 * @author Holger Velke
 */
public class TranscodeException extends ConverterException {

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TranscodeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public TranscodeException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public TranscodeException(Throwable arg0) {
		super(arg0);
	}

}
