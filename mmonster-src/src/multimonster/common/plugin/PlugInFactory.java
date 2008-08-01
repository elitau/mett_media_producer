package multimonster.common.plugin;

import multimonster.exceptions.PlugInInstantiationException;

import org.apache.log4j.Logger;

/**
 * The abstract PlugInFactory is the base for the component-specific
 * factories. The standart way to instanciate a PlugIn for a given
 * ID is immplemented and should be used by extending subclasses.
 * 
 * @author Holger Velke (sihovelk)
 */
abstract public class PlugInFactory {
	
	private Logger log;
	
	protected PlugInFactory() {
		this.log = Logger.getLogger(this.getClass());
	}

	/**
	 * Returns the PlugIn for a given PlugInIdentifier.
	 * PlugInIdentifier includes the classname!
	 * 
	 * @param plugInId
	 * @return returns the plugin specified by the <code>PlugInIdentifier</code>
	 * @throws PlugInInstantiationException
	 */
	protected PlugIn getPlugIn(PlugInIdentifier plugInId) throws PlugInInstantiationException {
		
		String className;		
		
		if ((className = plugInId.getClassName())!= null){
			try {
				return (PlugIn) Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new PlugInInstantiationException(e);
			}
		} else {
			String errorMsg ="PlugInIdentifier has no PlugIn-classname";
			errorMsg += " - unable to create PlugIn";
			throw new PlugInInstantiationException(errorMsg);
		}
	}

    /** @link dependency 
     * @stereotype instantiate
     * @clientRole Factory
     * @supplierRole Product*/
    /*# PlugIn lnkPlugIn; */
}
