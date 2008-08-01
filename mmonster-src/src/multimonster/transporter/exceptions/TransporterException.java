/*
 * Created on 21.02.2004
 *
 */
package multimonster.transporter.exceptions;

import multimonster.exceptions.MultiMonsterException;

/**
 * @author Jörg Meier
 *
 */
public class TransporterException extends MultiMonsterException {

	/**
	 * 
	 */
	public TransporterException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public TransporterException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TransporterException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public TransporterException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
