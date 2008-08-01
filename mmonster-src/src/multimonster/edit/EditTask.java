package multimonster.edit;

import multimonster.common.edit.EditJobIdentifier;
import multimonster.common.edit.EditTaskIdentifier;
import multimonster.common.edit.FilterAction;
import multimonster.common.edit.FilterPlugInIdentifier;

/**
 * @author Holger Velke (sihovelk)
 */
class EditTask {

	private EditTaskIdentifier taskId;
	
	private FilterPlugInIdentifier filterId;
	private FilterAction action;
	

	/**
	 * @param jobId
	 * @param filterId
	 * @param action
	 */
	public EditTask(
		EditJobIdentifier jobId,
		FilterPlugInIdentifier filterId,
		FilterAction action) {

		this.filterId = filterId;
		this.action = action;
		
		this.taskId = new EditTaskIdentifier(jobId);
	}

	/**
	 * @return Returns the action.
	 */
	public FilterAction getAction() {
		return action;
	}

	/**
	 * @return Returns the filterId.
	 */
	public FilterPlugInIdentifier getFilterId() {
		return filterId;
	}

	/**
	 * @return Returns the taskId.
	 */
	public EditTaskIdentifier getTaskId() {
		return taskId;
	}

}
