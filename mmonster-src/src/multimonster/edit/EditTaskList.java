package multimonster.edit;

import java.util.Enumeration;
import java.util.Vector;

import multimonster.common.edit.EditTaskIdentifier;

/**
 * @author Holger Velke
 */
class EditTaskList {
	
	private Vector tasks;

    /**
     * @supplierCardinality 0..*
     * @directed 
     */
    private EditTask lnkEditTask;
	
	public EditTaskList() {
		this.tasks = new Vector();
	}
	
	public void add(EditTask task) {
		tasks.add(task);
	}
	
	public boolean remove(EditTaskIdentifier taskId) {

		boolean removed = false;

		Enumeration enum = tasks.elements();
		for (; enum.hasMoreElements();) {
			EditTask task = (EditTask) enum.nextElement();
			if (taskId.toString().equals(task.getTaskId().toString())) {
				removed = tasks.remove(task);
				break;
			}
		}

		return removed;
	}
	
	public EditTask[] toArray() {
		EditTask[] taskArr = new EditTask[tasks.size()];

		for (int i = 0; i < taskArr.length; i++) {
			taskArr[i] = (EditTask) tasks.elementAt(i);
		}

		return taskArr;
	}

}
