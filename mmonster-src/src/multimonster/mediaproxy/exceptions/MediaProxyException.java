/*
 * Created on 21.02.2004
 *
 */
package multimonster.mediaproxy.exceptions;

import multimonster.exceptions.MultiMonsterException;

/**
 * @author Jörg Meier
 *
 */
public class MediaProxyException extends MultiMonsterException {

	/**
	 * 
	 */
	public MediaProxyException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MediaProxyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MediaProxyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MediaProxyException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
