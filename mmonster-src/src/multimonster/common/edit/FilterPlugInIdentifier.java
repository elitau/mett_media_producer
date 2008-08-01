package multimonster.common.edit;

import multimonster.common.plugin.*;

/**
 * A FilterIdentifier is an explicit identifier for an Filter 
 * sub class. As every Filter is a PlugIn , a FilterIdntifier 
 * is a subclass of PlugInIdentifier
 * 
 * @author Holger Velke (sihovelk)
 */
public class FilterPlugInIdentifier extends PlugInIdentifier {
	
	/**
	 * @param className The className of the plugIn
	 */
	public FilterPlugInIdentifier(String className) {
		super(className);
	}
}
