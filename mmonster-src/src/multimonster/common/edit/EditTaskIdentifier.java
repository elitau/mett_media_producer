package multimonster.common.edit;

import java.io.Serializable;

import multimonster.common.UniqueIdentifier;

/**
 * Unique identifier of an EditTask as part of an EditJob.
 * 
 * @author Holger Velke (sihovelk)
 */
public class EditTaskIdentifier extends UniqueIdentifier
	implements Serializable {

	/**
	 * specifies the job the task belongs to.
	 */
	private EditJobIdentifier editJob;
	
	/**
	 * @param the id of the job the task should belong to.
	 */
	public EditTaskIdentifier(EditJobIdentifier editJob) {
		super();
		this.editJob = editJob;
	}

	/**
	 * @return Returns the editJob to witch the task belongs.
	 */
	public EditJobIdentifier getEditJob() {
		return editJob;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "EditTaskIdentifier: "+this.getId();
	}
}
