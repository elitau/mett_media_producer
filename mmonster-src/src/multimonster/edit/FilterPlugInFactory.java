package multimonster.edit;

import multimonster.common.edit.FilterAction;
import multimonster.common.edit.FilterPlugInIdentifier;
import multimonster.common.plugin.PlugInFactory;
import multimonster.edit.exceptions.EditException;
import multimonster.exceptions.PlugInInstantiationException;

import org.apache.log4j.Logger;

class FilterPlugInFactory extends PlugInFactory {

	private Logger log;

	protected FilterPlugInFactory() {
		this.log = Logger.getLogger(this.getClass());
	}

	public FilterPlugIn getFilterPlugIn(
		FilterPlugInIdentifier filterId,
		FilterAction action) throws EditException {
		
		FilterPlugIn filter = null;
		
		log.debug("getFilterPlugIn()");
		try {
			filter = (FilterPlugIn) this.getPlugIn(filterId);
		} catch (ClassCastException e){
    		String errorMsg = "unable to cast " + filterId.getClassName();
    		errorMsg += " as multimonster.edit.FilterPlugIn";
    		errorMsg += " - filter is not available!";
    		throw new EditException(errorMsg, e);
    	} catch (PlugInInstantiationException e) {
    		String errorMsg = "unable to instanciate " + filterId.getClassName();
    		errorMsg += " - filter is not available!";
    		throw new EditException(errorMsg, e);
    	}
		
		return filter;
	}

	public static FilterPlugInFactory getInstance() {

		Logger.getLogger(FilterPlugInFactory.class).debug("getInstance()");

		if (instance == null) {
			synchronized (multimonster.edit.FilterPlugInFactory.class) {
				if (instance == null) {
					instance = new multimonster.edit.FilterPlugInFactory();
				}
			}
		}
		return instance;
	}

	/**
	 * @link dependency 
	 * @stereotype instantiate
	 * @clientRole Factory
	 * @supplierRole Product
	 */
	private FilterPlugIn lnkFilterPlugIn;

	/**
	 * @link 
	 * @shapeType PatternLink 
	 * @pattern Singleton 
	 * @supplierRole Singleton
	 * factory
	 */
	/* # private FilterPlugInFactory _filterPlugInFactory; */
	private static FilterPlugInFactory instance = null;
}
