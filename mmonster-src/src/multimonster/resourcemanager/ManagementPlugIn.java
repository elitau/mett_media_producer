package multimonster.resourcemanager;

import multimonster.common.resource.Costs;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.resourcemanager.exceptions.ManagementException;

/**
 * @author Holger Velke
 */
abstract public class ManagementPlugIn extends ResourceManagerPlugIn {

	/**
	 * @param request
	 */
	abstract public void reserve(ResourceRequest request) throws ManagementException;

	/**
	 * @param rrId
	 * @return
	 */
	abstract public Costs free(ResourceRequestIdentifier rrId)  throws ManagementException;

    /**
     * @label uses
     * @directed
     * @clientCardinality 1
     * @supplierCardinality 0..* 
     */
    private MeasuringPlugIn lnkMeasuringPlugIn;
}
