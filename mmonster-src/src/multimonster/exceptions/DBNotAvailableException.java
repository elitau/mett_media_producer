package multimonster.exceptions;

/**
 * @author Marc Iseler
 */
public class DBNotAvailableException extends MultiMonsterException {

	/**
	 * 
	 */
	public DBNotAvailableException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DBNotAvailableException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param trbl
	 */
	public DBNotAvailableException(Throwable trbl) {
		super(trbl);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public DBNotAvailableException(String message, Throwable trbl) {
		super(message, trbl);
		// TODO Auto-generated constructor stub
	}

}
