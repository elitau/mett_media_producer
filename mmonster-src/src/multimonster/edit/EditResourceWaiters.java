package multimonster.edit;

import java.util.Hashtable;

import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.resource.ResourceWaiter;

/**
 * @author Holger Velke
 */
class EditResourceWaiters {

	private Hashtable waiters;
	
	/** @link
	 * @shapeType PatternLink
	 * @pattern Singleton
	 * @supplierRole Singleton factory
	 */
	/* # private EditResourceWaiters _editResourceWaiters; */
	private static EditResourceWaiters instance = null;

    /**
     * @supplierCardinality 0..*
     * @directed 
     */
    private EditHandler lnkEditHandler;
	
	protected EditResourceWaiters(){
		this.waiters = new Hashtable();
	}
	
	public static EditResourceWaiters getInstance(){
		if (instance == null) {
			synchronized(multimonster.edit.EditResourceWaiters.class) {
				if (instance == null) {
					instance = new multimonster.edit.EditResourceWaiters();
				}
			}
		}
		return instance;
	}
	
	public void addWaiter(ResourceRequestIdentifier rrId, ResourceWaiter waiter){
		waiters.put(rrId, waiter);
	}
	
	public ResourceWaiter getWaiter(ResourceRequestIdentifier rrId){
		
		ResourceWaiter waiter = (ResourceWaiter) waiters.get(rrId); 
		
		if (waiter != null) {
			waiters.remove(rrId);
		}
		
		return waiter;
	}
	
}
