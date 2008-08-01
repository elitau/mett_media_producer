package multimonster.common.resource;

/**
 * A component which wants to reqeust resources
 * in a asynchronous whay needs to implement this
 * interface. It's used to inform the requesting
 * component wether and when the request was 
 * successfull.
 * 
 * @author Holger Velke (sihovelk)
 */
public interface ResourceWaiter {
	
    /**
     * called if the resources are granted within the maximum wait time
     * @param rrId the id of the request 
     */
    void grantResource(ResourceRequestIdentifier rrId);
    /**
     * called if no resources could be granted within the maximum wait time
     * @param rrId the id of the request 
     */
    void denyResource(ResourceRequestIdentifier rrId);
    
}
