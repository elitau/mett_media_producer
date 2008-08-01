package multimonster.resourcemanager;

import multimonster.common.UserIdentifier;
import multimonster.common.resource.Costs;
import multimonster.common.resource.QueueTime;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.resource.ResourceWaiter;

public class ResourceRequest {
	
    private ResourceRequestIdentifier rrId;
    private UserIdentifier uId;
    private Costs estimatedCosts;
    private QueueTime maxQT;
    private ResourceWaiter waiter;
    
    public ResourceRequest(UserIdentifier uId, Costs costs){
    	this.uId =uId;
    	this.estimatedCosts = costs;
    	this.maxQT = null;
    	this.waiter = null;
    	this.rrId = new ResourceRequestIdentifier();
    }
    
    public ResourceRequest(UserIdentifier uId, Costs costs, QueueTime maxQT, ResourceWaiter waiter){
    	this.uId =uId;
    	this.estimatedCosts = costs;
    	this.maxQT = maxQT;
    	this.waiter = waiter;
    	this.rrId = new ResourceRequestIdentifier();
    }
    
	/**
	 * @return Returns the estimatedCosts.
	 */
	public Costs getEstimatedCosts() {
		return estimatedCosts;
	}

	/**
	 * @return Returns the rrId.
	 */
	public ResourceRequestIdentifier getRrId() {
		return rrId;
	}

	/**
	 * @return Returns the uId.
	 */
	public UserIdentifier getUId() {
		return uId;
	}

	/**
	 * @return Returns the maxQT.
	 */
	public QueueTime getMaxQT() {
		return maxQT;
	}

	/**
	 * @return Returns the resourceWaiterJNDI_NAME.
	 */
	public ResourceWaiter getResourceWaiter() {
		return waiter;
	}

}
