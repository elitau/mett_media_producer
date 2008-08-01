package multimonster.edit;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multimonster.common.MMThread;
import multimonster.common.UserIdentifier;
import multimonster.common.edit.EditJobIdentifier;
import multimonster.common.edit.EditTaskIdentifier;
import multimonster.common.edit.FilterAction;
import multimonster.common.edit.FilterPlugInIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MediaObject;
import multimonster.common.media.MetaData;
import multimonster.common.resource.QueueTime;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.util.EjbCreator;
import multimonster.common.util.EjbHomeGetter;
import multimonster.edit.exceptions.EditException;
import multimonster.exceptions.MultiMonsterException;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster.systemadministration.interfaces.SystemAdministrationImplHome;

import org.apache.log4j.Logger;


/**
 * @ejb.bean name = "EditImpl" display-name = "EditFacade SessionBean"
 * description = "The Facade of the Edit-Package of MultiMonster" view-type =
 * "remote" jndi-name = "multimonster/edit/EditFacade"
 * 
 * @see multimonster.edit.EditFacade
 * 
 * @author Holger Velke (sihovelk)
 *
 */
public class EditImplBean implements EditFacade, SessionBean {

	private static Logger log = Logger.getLogger(EditImplBean.class);

	private Context context;
	
	private SystemAdministrationImplHome systemAdministrationHome;

	private EditJobList editJobList;


	/**
	 * @see multimonster.edit.EditFacade#getJob(multimonster.common.UserIdentifier,
	 *      multimonster.common.MOIdentifier)
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public EditJobIdentifier getJob(UserIdentifier uId, MOIdentifier mOId)
		throws EditException {

		log.debug("getJob()");

		// check parameters
		if (uId == null) {
			throw new EditException("UserIdentifier is null");
		}
		if (mOId == null) {
			throw new EditException("MOIdentifier is null");
		}

		// do work
		
		EditJob job = null;
		EditJobIdentifier jobId = null;

		log.debug("getJob()");

		// create new Edit Job
		job = new EditJob(uId, mOId);
		jobId = job.getId();

		// Add job to joblist
		editJobList.put(job);

		return jobId;
	}

	/**
	 * @see multimonster.edit.EditFacade#addTaskToJob(multimonster.common.EditJobIdentifier,
	 *      multimonster.common.FilterPlugInIdentifier,
	 *      multimonster.common.FilterAction)
	 * 
	 *  @ejb.interface-method view-type = "remote"
	 */
	public EditTaskIdentifier addTaskToJob(
		EditJobIdentifier jobId,
		FilterPlugInIdentifier filterId,
		FilterAction action)
		throws EditException {

		log.debug("addTaskToJob()");

		// check parameters
		if (jobId == null) {
			throw new EditException("EditJobIdentifier is null");
		}
		if (filterId == null) {
			throw new EditException("FilterPugInIdentifier is null");
		}
		if (action == null) {
			throw new EditException("FilterAction is null");
		}

		// do work

		EditTaskIdentifier taskId = null;
		EditJob job = null;
		EditTask task = null;

		// get Job for Id
		job = editJobList.get(jobId);
		if (job == null) {
			throw new EditException("no EditJob for this EditJobIdentifier");
		}

		//create new task for job with the instructions
		task = new EditTask(jobId, filterId, action);

		//add task to the job
		job.addEditTask(task);

		taskId = task.getTaskId();

		return taskId;
	}

	/** 
	 * @see multimonster.edit.EditFacade#finishJob(multimonster.common.EditJobIdentifier,
	 *      multimonster.common.MetaData, multimonster.common.Duration)
	 *
	 * @ejb.interface-method view-type = "remote"
	 */
	public MOIdentifier finishJob(
		EditJobIdentifier jobId,
		MetaData metaData,
		QueueTime maxQT)
		throws EditException {

		log.debug("finishJob()");

		// check parameters

		if (jobId == null) {
			throw new EditException("EditJobIdentifier is null");
		}
		if (metaData == null) {
			throw new EditException("MetaData is null");
		}

		// do work
		
		MOIdentifier mOId = null;
		MediaObject mO = null;
		EditJob job = null;
		EditHandler handler = null;
		UserIdentifier uId = null;
		SystemAdministrationImpl systemAdministration = null;

		job = editJobList.get(jobId);
		if (job == null) {
			throw new EditException("no EditJob for this EditJobIdentifier");
		}

		// set the max job Queue Time
		job.setMaxWait(maxQT);
		
		// create mediaobject in systemadministration
		mO = new MediaObject(metaData);
		uId = job.getEditor();
		try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
					systemAdministrationHome,
					context);
			mOId = systemAdministration.addMediaObject(mO, uId);
			systemAdministration.remove();
		} catch (MultiMonsterException e) {
			log.error(e);
			throw new EditException("unable to add new MediaObject", e);
		} catch (Exception e) {
			log.error(e);
			throw new EditException("unable to add new MediaObject", e);
		} 

		// start new edithandler
		if (mOId == null) {
			throw new EditException("got 'null' MOIdendtifier from SystemAdministration");
		}
		job.setNewMOId(mOId);

		handler = new EditHandler(jobId);
		(new MMThread(handler)).start();
		
		return mOId;
	}

	/**
	 * @see multimonster.edit.EditFacade#removeTask(multimonster.common.EditJobIdentifier,
	 *      multimonster.common.EditTaskIdentifier)
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public void removeTask(EditTaskIdentifier taskId) throws EditException {

		log.debug("removeTask()");

		// check parameter
		if (taskId == null) {
			throw new EditException("EditTaskIdentifier is null");
		}

		// do work

		EditJob job = null;

		// get job of the Task
		job = editJobList.get(taskId.getEditJob());
		if (job == null) {
			throw new EditException("no EditJob for this EditTaskIdentifier");
		}

		// remove the task form the job
		if (!job.removeEditTask(taskId)){
			throw new EditException("unknown EditTaskIdenitfier");
		}
	}

	/**
	 * 
	 * @see multimonster.edit.EditFacade#abortJob(multimonster.common.EditJobIdentifier)
	 * 
	 * @ejb.interface-method view-type = "remote"
	 */
	public void abortJob(EditJobIdentifier jobId) throws EditException {

		log.debug("abortJob()");

		// check parameter
		if (jobId == null) {
			throw new EditException("EditJobIdentifier is null");
		}

		// do work

		if (editJobList.remove(jobId) == null) {
			throw new EditException("no EditJob for this EditTaskIdentifier");
		}
	}

	/**
	 * @see multimonster.edit.EditFacade#getJobList(multimonster.common.UserIdentifier)
	 *
	 * @ejb.interface-method view-type = "remote"
	 */
	public EditJobIdentifier[] getJobList(UserIdentifier uId)
		throws EditException {

		log.debug("getJobList()");

		// check parameter
		if (uId == null) {
			throw new EditException("UserIdentifier is null");
		}

		// do work

		return editJobList.getUsersList(uId);
	}

	/**
	 * @see multimonster.common.ResourceWaiter#grantResource(multimonster.common.ResourceRequestIdentifier)
	 *	
	 * @ejb.interface-method view-type = "remote"
	 */
	public void grantResource(ResourceRequestIdentifier rrId) {
		
		log.debug("grantResource()");

		EditResourceWaiters.getInstance().getWaiter(rrId).grantResource(rrId);
		
	}

	/**
	 * @see multimonster.common.ResourceWaiter#denyResource(multimonster.common.ResourceRequestIdentifier)
	 *
	 * @ejb.interface-method view-type = "remote"
	 */	
	public void denyResource(ResourceRequestIdentifier rrId) {
		
		log.debug("denyResource()");
		
		EditResourceWaiters.getInstance().getWaiter(rrId).denyResource(rrId);
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

		this.editJobList = EditJobList.getInstance();

		try {
			context = new InitialContext();
			systemAdministrationHome =
				EjbHomeGetter.getSystemAdministrationHome(context);
		} catch (NamingException e) {
			log.error("unable to get SystemadministrationHome", e);
		}
		
		log.debug("CREATED");
	}

}
