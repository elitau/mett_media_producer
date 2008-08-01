package multimonster.edit;

import multimonster.common.UserIdentifier;
import multimonster.common.edit.EditJobIdentifier;
import multimonster.common.edit.EditTaskIdentifier;
import multimonster.common.edit.FilterAction;
import multimonster.common.media.MOIdentifier;
import multimonster.common.resource.QueueTime;

class EditJob {
	
    private EditJobIdentifier id;
    // TODO create status concept 
    private int status;
    private EditTaskList editTasks;
    private UserIdentifier editor;
    private MOIdentifier newMOId;
	private MOIdentifier sourceMOId;
	private QueueTime maxQT = null;
    
    public EditJob(UserIdentifier editor, MOIdentifier mOId){
    	this.id = new EditJobIdentifier();
    	this.status = 0;
    	this.editTasks = new EditTaskList();
    	this.editor= editor;
    	this.newMOId = null;
    	this.sourceMOId = mOId;    	
    }
    
	/**
	 * @return Returns the newMOId.
	 */
	public MOIdentifier getNewMOId() {
		return newMOId;
	}

	/**
	 * @param newMOId The newMOId to set.
	 */
	public void setNewMOId(MOIdentifier newMOId) {
		this.newMOId = newMOId;
	}

	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return Returns the editor.
	 */
	public UserIdentifier getEditor() {
		return editor;
	}

	/**
	 * @return Returns the editTasks.
	 */
	public EditTask[] getEditTasks() {
		return editTasks.toArray();
	}

	/**
	 * @return Returns the id.
	 */
	public EditJobIdentifier getId() {
		return id;
	}

	public void addEditTask(EditTask editTask){
		editTasks.add(editTask);	
	}
	
	public FilterAction[] getActions(){
		EditTask[] tasks = editTasks.toArray();
		FilterAction[] actions = new FilterAction[tasks.length];
		for (int i = 0; i < tasks.length; i++){
			actions[i] = tasks[i].getAction();
		}
		return actions;
	}

	/**
	 * @param taskId
	 */
	public boolean removeEditTask(EditTaskIdentifier taskId) {		
		return editTasks.remove(taskId);
	}
	/**
	 * @return Returns the sourceMOId.
	 */
	public MOIdentifier getSourceMOId() {
		return sourceMOId;
	}

	/**
	 * @return Returns the maxQT.
	 */
	public QueueTime getMaxQueueTime() {
		return maxQT;
	}

	/**
	 * @param maxQT The maximum QueueTime to set.
	 */
	public void setMaxWait(QueueTime maxQT) {
		this.maxQT = maxQT;
	}

}
