package multimonster.resourcemanager;

import org.apache.log4j.Logger;

import multimonster.common.plugin.PlugInFactory;
import multimonster.common.plugin.PlugInIdentifier;
import multimonster.exceptions.PlugInInstantiationException;
import multimonster.resourcemanager.exceptions.ManagementException;

class ResourceManagerPlugInFactory extends PlugInFactory {

	private static Logger log =
		Logger.getLogger(ResourceManagerPlugInFactory.class);

	// TODO use administrative setting for this.
	private static final String MANAGEMENT_PLUGIN_NAME =
		"multimonster.resourcemanager.plugin.NRequestsOnly";

	protected ResourceManagerPlugInFactory() {
	}

	public static ResourceManagerPlugInFactory getInstance() {
		if (instance == null) {
			synchronized (ResourceManagerPlugInFactory.class) {
				if (instance == null) {
					instance = new ResourceManagerPlugInFactory();
				}
			}
		}
		return instance;
	}

	/**
	 * @link
	 * @shapeType PatternLink
	 * @pattern Singleton
	 * @supplierRole Singleton factory 
	 */
	/*# private ResourceManagerPlugInFactory _resourceManagerPlugInFactory; */
	private static ResourceManagerPlugInFactory instance = null;

	/** @link dependency 
	 * @clientRole Factory
	 * @supplierRole Product
	 * @stereotype instantiate*/
	/*# ResourceManagerPlugIn lnkResourceManagerPlugIn; */

	/**
	 * @return the current to use ManagemantPlugIn
	 * @throws ManagementException
	 */
	public ManagementPlugIn getManagementPlugIn() throws ManagementException {

		ManagementPlugIn management = null;

		log.debug("getManagementStrategyPlugIn()");

		try {
		management =
			(ManagementPlugIn) getPlugIn(
				new PlugInIdentifier(MANAGEMENT_PLUGIN_NAME));
		} catch (PlugInInstantiationException e) {
			String errorMsg = "unable to get the management-plugin '"+MANAGEMENT_PLUGIN_NAME+"'."; 
			throw new ManagementException(errorMsg, e);
		}
		
		return management;
	}
}
