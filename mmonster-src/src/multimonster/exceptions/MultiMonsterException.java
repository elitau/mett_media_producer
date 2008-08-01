package multimonster.exceptions;

/**
 * @author Holger Velke (sihovelk)
 */
public class MultiMonsterException extends Exception {

	/**
	 * 
	 */
	public MultiMonsterException() {
		super();
	}

	/**
	 * @param message
	 */
	public MultiMonsterException(String message) {
		super(message);
	}

	/**
	 * @param trbl
	 */
	public MultiMonsterException(Throwable trbl) {
		super(trbl);
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public MultiMonsterException(String message, Throwable trbl) {
		super(message, trbl);
	}

}
