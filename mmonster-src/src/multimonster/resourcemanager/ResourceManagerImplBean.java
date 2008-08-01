package multimonster.resourcemanager;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.log4j.Logger;

import multimonster.common.UserIdentifier;
import multimonster.common.resource.Costs;
import multimonster.common.resource.QueueTime;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.resource.ResourceWaiter;
import multimonster.resourcemanager.exceptions.ResourceManagerException;

/**
 * @ejb.bean name = "ResourceManagerImpl" 
 * 		display-name = "ResourceManagerFacade SessionBean"
 * 		description = "The Facade of the ResourceManager-Package of MultiMonster"
 * 		view-type = "remote" 
 * 		jndi-name = "multimonster/edit/ResourceManagerFacade"
 * 
 * @see multimonster.resourcemanager.ResourceManagerFacade
 * 
 * @author Holger Velke (sihovelk)
 * 
 */
public class ResourceManagerImplBean
	implements ResourceManagerFacade, SessionBean {

	private static Logger log = Logger.getLogger(ResourceManagerImplBean.class);
	
    /**
     * @clientCardinality 0..*
     * @supplierCardinality 0..1 
     */
	private ResourceManager manager;
	

	/**
	 * @see multimonster.resourcemanager.ResourceManagerFacade#requestResources(multimonster.common.UserIdentifier,
	 *      multimonster.common.Costs, multimonster.common.Duration)
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public ResourceRequestIdentifier requestResources(
		UserIdentifier uId,
		Costs costs,
		QueueTime maxQT,
		ResourceWaiter waiter) throws ResourceManagerException {

		log.debug("requestResource()");
		
		// check parameter
		
		if (uId == null){
			throw new ResourceManagerException("UserIdentifier is null");
		}
		if (costs == null){
			throw new ResourceManagerException("Costs is null");
		}
		if (maxQT == null){
			throw new ResourceManagerException("Duration is null");
		}
		if (waiter == null){
			throw new ResourceManagerException("ResourceWaiter is null");
		}

		if (manager == null) {
			this.manager = ResourceManager.getInstance();
		}		
		return this.manager.requestResources(
			uId,
			costs,
			maxQT,
			waiter);
	}

	/**
	 *@see multimonster.resourcemanager.ResourceManagerFacade#requestResources(multimonster.common.UserIdentifier,
	 *      multimonster.common.Costs)
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public ResourceRequestIdentifier requestResources(
		UserIdentifier uId,
		Costs costs) throws ResourceManagerException {

		log.debug("requestResource()");

		// check parameter
		
		if (uId == null){
			throw new ResourceManagerException("UserIdentifier is null");
		}
		if (costs == null){
			throw new ResourceManagerException("Costs is null");
		}
		
		if (manager == null) {
			this.manager = ResourceManager.getInstance();
		}
		return manager.requestResources(uId, costs);
	}

	/**
	 * @see multimonster.resourcemanager.ResourceManagerFacade#releaseResources(multimonster.common.ResourceRequestIdentifier)
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public Costs releaseResources(ResourceRequestIdentifier rrId) throws ResourceManagerException {

		log.debug("releaseResource()");
		
		// check parameter
		
		if (rrId == null){
			throw new ResourceManagerException("ResourceRequestIdentifier is null");
		}

		if (manager == null) {
			this.manager = ResourceManager.getInstance();
		}
		return manager.releaseResources(rrId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	public void ejbActivate() throws EJBException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	public void ejbPassivate() throws EJBException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext arg0)
		throws EJBException, RemoteException {
	}

	/**
	 * @ejb.create-method
	 */
	public void ejbCreate() {
	}
}
