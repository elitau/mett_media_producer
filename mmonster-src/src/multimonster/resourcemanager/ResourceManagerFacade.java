package multimonster.resourcemanager;

import multimonster.common.UserIdentifier;
import multimonster.common.resource.Costs;
import multimonster.common.resource.QueueTime;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.resource.ResourceWaiter;
import multimonster.resourcemanager.exceptions.ResourceManagerException;

/**
 * The ResourceManager manages the resources of the system. The ResourceManager
 * does not prohibit using resources without requesting. So you don't have to
 * request resources a the ResourceManager but you should.
 * The ResourceManager is only able to keep the quality of service if every
 * resource consuming action requests the resources befor starting.
 * 
 * @author Holger Velke (sihovelk)
 */
public interface ResourceManagerFacade {
	
	/**
	 * Requests resources. The request stays pending until the
	 * <code>maxQT</code> expires. If the requested are available the 
	 * <code>waiter</code> is notified.
	 * 
	 * @param uId
	 * 			The Id of the user who requests the resources
	 * @param costs
	 * 			The expected resource-costs
	 * @param maxQT
	 * 			The maximal time to wait.
	 * @param waiter
	 * 			The requestand waiting for resources
	 * @return
	 * 			 a <code>ResourceRequestIdentifier</code> 
	 * @throws ResourceManagerException
	 */
	ResourceRequestIdentifier requestResources(
		UserIdentifier uId,
		Costs costs,
		QueueTime maxQT,
		ResourceWaiter waiter)
		throws ResourceManagerException;

	/**
	 * Requests resources that have to be immediately available. The <code>ResourceRequestIdentifier</code>
	 * returned is needed to release the requested an reserved resources.
	 * 
	 * @param uId
	 *            The Id of the user who requests the resources
	 * @param costs
	 *            The expected resource-costs
	 * @return If requested resources are granted a ResourceRequestIdentifier
	 *         is returend.
	 *         <p>
	 *         If not <code>null</code> is returned.
	 * @throws ResourceManagerException
	 */
	ResourceRequestIdentifier requestResources(UserIdentifier uId, Costs costs)
		throws ResourceManagerException;
	
	/**
	 * Releases the resources specified by the
	 * <code>ResourceRequestIdentifier</code> and returns the real costs of the
	 * request. 
	 * 
	 * @param rrId The id of the resources to release
	 * @return the real costs of the request
	 * @throws ResourceManagerException
	 */
	Costs releaseResources(ResourceRequestIdentifier rrId)
		throws ResourceManagerException;
}
