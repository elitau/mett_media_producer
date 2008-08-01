package multimonster.resourcemanager;

import java.util.Vector;

import multimonster.common.resource.ResourceWaiter;
import multimonster.resourcemanager.exceptions.ResourceManagerException;

import org.apache.log4j.Logger;

/**
 * @author Holger Velke
 */
class RequestQueue implements Runnable {

	private static Logger log = Logger.getLogger(RequestQueue.class);

	private Vector queue = null;
	private ResourceManager manager = null;

    /**
     * @label stores
     * @labelDirection forward
     * @clientCardinality 1
     * @supplierCardinality 0..* 
     */
    private ResourceRequest lnkResourceRequest;
	private boolean running = false;

	/**
	 * 
	 */
	public RequestQueue(ResourceManager manager) {
		this.queue = new Vector();
		this.manager = manager;
	}

	public synchronized void add(ResourceRequest request) {

		queue.add(request);
		this.notify();
	}

	public synchronized void notifyFreeResources() {
		this.notify();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

//		log.debug("running");
		
		setRunning(true);

		while (true) {

			ResourceRequest request = null;
			boolean reserved = false;

			if (queue.isEmpty()) {
				setRunning(false);
//				log.debug("finished");
				return;
			}

			request = (ResourceRequest) queue.firstElement();

			reserved = manager.requestResources(request);
			if (reserved) {
				queue.remove(request);
				notifyResourceWaiter(request);
				log.info("grantResources: "+request.getRrId());
			} else {
				waitForResources();
			}
		}
	}

	/**
	 * @param request
	 */
	private void notifyResourceWaiter(ResourceRequest request) {

		ResourceWaiter waiter = null;

		waiter = request.getResourceWaiter();
		if (waiter != null) {
			waiter.grantResource(request.getRrId());
		} else {
			// no resourcewaiter - free resources - log error
			log.error(
				"No reference to ResourceWaiter for rrId:"
					+ request.getRrId()
					+ " - releasing resources.");
			try {
				manager.releaseResources(request.getRrId());
			} catch (ResourceManagerException e) {
				log.error("Problem freeing resources - I give up", e);
			}
			return;
		}
	}

	private synchronized void waitForResources() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			log.error("gotInterrupted while waitung for resources");
		}
	}

	/**
	 * @return Returns the running.
	 */
	public  synchronized boolean isRunning() {
		return running;
	}

	/**
	 * @param running The running to set.
	 */
	private synchronized void setRunning(boolean running) {
		this.running = running;
	}
}
