package multimonster.edit;

import multimonster.common.*;
import multimonster.common.edit.*;
import multimonster.common.media.*;
import multimonster.common.resource.*;
import multimonster.edit.exceptions.EditException;


/**
 * the edit component proviedes content-based modifications for mediadata.
 * It uses jobs to specifiy a number of tasks to modify the mediadata.
 * 
 * @author Holger Velke (sihovelk)
 */
public interface EditFacade extends ResourceWaiter{
	
	/**
	 * creats a new job for the specified user.
	 * Uses the specified mediaobject as source for the job.
	 * 
	 * @param uId the owner of the job
	 * @param mOId the source media object
	 * @return the id of the job
	 * @throws EditException
	 */
	EditJobIdentifier getJob(UserIdentifier uId, MOIdentifier mOId)
		throws EditException;
	
	
	/**
	 * creates a new task fo the specified filter snd the specified action and
	 * adds it to the job.
	 * 
	 * @param jobId the job to add the task
	 * @param filterId the filter to use
	 * @param action the parameters for the filter
	 * @return the id of the created task
	 * @throws EditException
	 */
	EditTaskIdentifier addTaskToJob(
		EditJobIdentifier jobId,
		FilterPlugInIdentifier filterId,
		FilterAction action)
		throws EditException;
	
	/**
	 * Causes the edit component to finish the job. No tasks can be added.
	 * The edit progress begins if resources are available.
	 * 
	 * @param jobId the id of the job to finish
	 * @param metaData the metadata for the new media object created by the job
	 * @param maxQT the maximal duration to wait for resources. if <code>null</code>
	 * 			it will try to start immediatelly, if there are no resources
	 * 			available it will give up
	 * @return	the mediaobjectid of the new created mediaobject
	 * @throws EditException
	 */
	MOIdentifier finishJob(
		EditJobIdentifier jobId,
		MetaData metaData,
		QueueTime maxQT)
		throws EditException;
	
	/**
	 * removes the spicified task from it's job
	 * 
	 * @param taskId the task to remove
	 * @throws EditException
	 */
	void removeTask(EditTaskIdentifier taskId) throws EditException;
	
	/**
	 * aborts the job specifed by the jobId. This is only possible until 
	 * <code>finishJob()</code> is call for the job.
	 * 
	 * @param jobId the id of the job to abort
	 * @throws EditException
	 */
	void abortJob(EditJobIdentifier jobId) throws EditException;
	
	/**
	 * @param uId the id of the user 
	 * @return all jobs of the specified user
	 * @throws EditException
	 */
	EditJobIdentifier[] getJobList(UserIdentifier uId) throws EditException;
}
