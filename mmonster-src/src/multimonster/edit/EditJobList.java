package multimonster.edit;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import multimonster.common.UserIdentifier;
import multimonster.common.edit.EditJobIdentifier;

/**
 * The EditJobList is a Singleton that stores the EditJobs in a not persistant
 * way.
 * @author Holger Velke (sihovelk)
 */
class EditJobList {

	private Hashtable table;

	/**
	 * @link
	 * @shapeType PatternLink
	 * @pattern Singleton
	 * @supplierRole Singleton factory
	 */
	/* # private EditJobList _editJobList; */
	private static EditJobList instance = null;

	/**
	 * @supplierCardinality 0..*
	 * @directed
	 */
	private EditJob lnkEditJob;

	protected EditJobList() {
		table = new Hashtable();
	}

	/**
	 * Stores the job in the JobList.
	 * 
	 * @param job 
	 */
	synchronized void put(EditJob job) {

		if (job == null)
			throw new IllegalArgumentException("job should not be null");

		table.put(job.getId(), job);
	}

	/**
	 * Gets the job spezified by jobId or null if there is no job for the jobId
	 * 
	 * @param jobId
	 * @return the EditJob spezified by jobId
	 */
	synchronized EditJob get(EditJobIdentifier jobId)
		throws IllegalArgumentException {

		if (jobId == null)
			throw new IllegalArgumentException("jobId should not be null");

		return (EditJob) table.get(jobId);
	}

	/**
	 * removes the job spezified by jobId from the list.
	 * returns the job spezified by jobId or null if there is no job for 
	 * the jobId
	 * 
	 * @param jobId
	 * @return
	 */
	synchronized EditJob remove(EditJobIdentifier jobId) {

		if (jobId == null)
			throw new IllegalArgumentException("jobId should not be null");

		return (EditJob) table.remove(jobId);

	}

	/**
	 * @param uId
	 * @return
	 */
	synchronized EditJobIdentifier[] getUsersList(UserIdentifier uId) {

		Object[] jobIdObjs = null;
		EditJobIdentifier[] jobIds = null;
		Vector jobIdsVect = new Vector();

		if (uId == null)
			throw new IllegalArgumentException("uId shloud not be null");

		if (table.size() == 0)
			return null;

		Enumeration enum = table.elements();
		for (; enum.hasMoreElements();) {
			EditJob job = (EditJob) enum.nextElement();
			if (job.getEditor().equals(uId)) {
				jobIdsVect.add(job.getId());
			}
		}

		if (jobIdsVect.isEmpty()) //User has no EditJobs
			//return empty list
			return new EditJobIdentifier[0];

		jobIdObjs = jobIdsVect.toArray();
		jobIds = new EditJobIdentifier[jobIdObjs.length];

		for (int i = 0; i < jobIds.length; i++) {
			jobIds[i] = (EditJobIdentifier) jobIdObjs[i];
		}

		return jobIds;
	}

	public static EditJobList getInstance() {
		if (instance == null) {
			synchronized (multimonster.edit.EditJobList.class) {
				if (instance == null) {
					instance = new multimonster.edit.EditJobList();
				}
			}
		}
		return instance;
	}
}
