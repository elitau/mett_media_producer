package multimonster.systemadministration.exceptions;

import multimonster.exceptions.MultiMonsterException;

/**
 * @author Marc Iseler
 */
public class SettingNotExistsException extends MultiMonsterException {

	/**
	 * 
	 */
	public SettingNotExistsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SettingNotExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param trbl
	 */
	public SettingNotExistsException(Throwable trbl) {
		super(trbl);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public SettingNotExistsException(String message, Throwable trbl) {
		super(message, trbl);
		// TODO Auto-generated constructor stub
	}

}
