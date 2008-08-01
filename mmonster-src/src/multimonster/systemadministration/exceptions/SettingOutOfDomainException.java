package multimonster.systemadministration.exceptions;

import multimonster.exceptions.MultiMonsterException;

/**
 * @author Marc Iseler
 */
public class SettingOutOfDomainException extends MultiMonsterException {

	/**
	 * 
	 */
	public SettingOutOfDomainException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SettingOutOfDomainException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param trbl
	 */
	public SettingOutOfDomainException(Throwable trbl) {
		super(trbl);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param trbl
	 */
	public SettingOutOfDomainException(String message, Throwable trbl) {
		super(message, trbl);
		// TODO Auto-generated constructor stub
	}

}
