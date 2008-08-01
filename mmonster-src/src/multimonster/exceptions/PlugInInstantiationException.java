/*
 * Created on 03.05.2004
 *
 */
package multimonster.exceptions;

/**
 * The Exception is thrown if the Insanciation of an PlugIn fails
 * 
 * @author Holger Velke (sihovelk)
 */
public class PlugInInstantiationException extends MultiMonsterException {

	/**
	 * 
	 */
	public PlugInInstantiationException() {
		super();
	}

	/**
	 * @param message
	 */
	public PlugInInstantiationException(String message) {
		super(message);
	}

	/**
	 * @param trbl
	 */
	public PlugInInstantiationException(Throwable trbl) {
		super(trbl);
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public PlugInInstantiationException(String message, Throwable trbl) {
		super(message, trbl);
	}

}
