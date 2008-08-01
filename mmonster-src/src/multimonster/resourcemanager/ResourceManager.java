package multimonster.resourcemanager;

import multimonster.common.MMThread;
import multimonster.common.UserIdentifier;
import multimonster.common.resource.Costs;
import multimonster.common.resource.QueueTime;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.resource.ResourceWaiter;
import multimonster.resourcemanager.exceptions.ManagementException;
import multimonster.resourcemanager.exceptions.NoResourcesAvailable;
import multimonster.resourcemanager.exceptions.ResourceManagerException;

import org.apache.log4j.Logger;

/**
 * @author Holger Velke
 * 
 */
class ResourceManager {

	private static Logger log = Logger.getLogger(ResourceManager.class);

    /**
     * @label uses
     * @clientCardinality 1
     * @supplierCardinality 1 
     */
	private ManagementPlugIn management = null;
	private RequestQueue queue = null;

	/**
	 * @link
	 * @shapeType PatternLink
	 * @pattern Singleton
	 * @supplierRole Singleton factory 
	 */
	/*# private ResourceManager _resourceManager; */
	private static ResourceManager instance = null;

    /**
     * @label creates
     * @labelDirection forward 
     */
    private ResourceRequest lnkResourceRequest;

	/**
	 * @throws ResourceManagerException
	 *  
	 */
	private ResourceManager() throws ResourceManagerException {

		ResourceManagerPlugInFactory factory = null;
		
		factory = ResourceManagerPlugInFactory.getInstance();
		try {
			this.management = factory.getManagementPlugIn();
		} catch (ManagementException e) {
			throw new ResourceManagerException("No resource-management available.", e);
		}
		
	}

	/**
	 * @param uId
	 * @param costs
	 * @param maxQT
	 * @return
	 */
	public synchronized ResourceRequestIdentifier requestResources(
		UserIdentifier uId,
		Costs costs,
		QueueTime maxQT,
		ResourceWaiter waiter) {

		ResourceRequestIdentifier rrId = null;
		ResourceRequest request = null;

//		log.debug("requestResource()");

		request = new ResourceRequest(uId, costs, maxQT, waiter);

		if (queue == null)
			queue = getRequestQueue(request);
		else if (!queue.isRunning())
			queue = getRequestQueue(request);
		else
			queue.add(request);

		rrId = request.getRrId();

		return rrId;
	}

	/**
	 * @return
	 */
	private RequestQueue getRequestQueue(ResourceRequest request) {
		RequestQueue queue = new RequestQueue(this);
		queue.add(request);
		(new MMThread(queue)).start();
		return queue;
	}

	/**
	 * @param uId
	 * @param costs
	 * @return
	 */
	public synchronized ResourceRequestIdentifier requestResources(
		UserIdentifier uId,
		Costs costs) {

		ResourceRequestIdentifier rrId = null;
		ResourceRequest request = null;

//		log.debug("requestResource()");

		if (management == null) {
			log.error("no management plugin - unable to grant resources");
			// TODO better throw NoManagementPlugIn Exception
			return null;
		}

		try {
			request = new ResourceRequest(uId, costs);
			management.reserve(request);
			rrId = request.getRrId();
		} catch (NoResourcesAvailable e) {
			// TODO better throw an exception
			rrId = null;
		} catch (ManagementException e) {
			log.error("problem reserving resources - " + e.getMessage());
			// TODO better throw an exception
			rrId = null;
		}
		
		log.info("grantResources: "+rrId);

		return rrId;
	}

	/**
	 * @param rrId
	 * @return
	 */
	public synchronized Costs releaseResources(ResourceRequestIdentifier rrId)
		throws ResourceManagerException {
		Costs realCosts = null;

		log.info("releaseResources: "+rrId);

		realCosts = management.free(rrId);

		if ((queue != null)&&(queue.isRunning())){
			// queue is only running if someone is waiting for resources
			queue.notifyFreeResources();
		} 

		return realCosts;
	}

	/**
	 * @return the singleton <code>ResourceManager</code>
	 * @throws ResourceManagerException
	 */
	public static ResourceManager getInstance() throws ResourceManagerException {
		if (instance == null) {
			synchronized (multimonster.resourcemanager.ResourceManager.class) {
				if (instance == null) {
					instance =
						new multimonster.resourcemanager.ResourceManager();
				}
			}
		}
		return instance;
	}

	/**
	 * @param request
	 * @return <code>true</code> if resources are reserved <br> 
	 * 		   <code>false</code> if not
	 */
	public synchronized boolean requestResources(ResourceRequest request) {

		boolean result = false;

		try {
			management.reserve(request);
			result = true;
		} catch (NoResourcesAvailable e) {
			result = false;
		} catch (ManagementException e) {
			log.error("problem reserving resources - " + e.getMessage());
			result = false;
		}

		return result;
	}
}
