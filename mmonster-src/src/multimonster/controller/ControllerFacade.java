package multimonster.controller;
import multimonster.common.Action;
import multimonster.common.AdminOperation;
import multimonster.common.AdminResult;
import multimonster.common.AuthData;
import multimonster.common.ConnectionAddress;
import multimonster.common.FormatId;
import multimonster.common.InputOption;
import multimonster.common.OutputOption;
import multimonster.common.ProtocolId;
import multimonster.common.SearchCriteria;
import multimonster.common.SearchResult;
import multimonster.common.Session;
import multimonster.common.edit.EditJobIdentifier;
import multimonster.common.edit.EditTaskIdentifier;
import multimonster.common.edit.FilterAction;
import multimonster.common.edit.FilterDetail;
import multimonster.common.edit.FilterPlugInIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MediaObject;
import multimonster.common.media.MetaData;
import multimonster.common.media.MetaDataAccess;
import multimonster.common.resource.QueueTime;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.controller.exceptions.ControllerException;
import multimonster.controller.exceptions.InvalidAuthDataException;

/**
 * The Controller is the client-interface for interaction with
 * the system. <br>
 * It dispatches the incoming request to the suitable components.
 * <p>
 * Controller is realized as a stateful session bean.
 * 
 */
public interface ControllerFacade {
	
	/**
	 * Starts a Session by logging a user in.
	 * Checks the given AuthData, if valid, returns a true.
	 * If AuthData is invalid returns false.
	 * 
	 * @param auth
	 * @return true if login was successful
	 * @throws InvalidAuthDataException if AuthData is invalid
	 * 
	 */	
	public boolean login(AuthData auth) throws ControllerException, InvalidAuthDataException;

    /**
     * Logs the user out, invalidates the Session.
     * 
     * @param session
     */
	public void logout() throws ControllerException;

    /**
     * MUST NOT BE CALLED FROM THE CLIENT
     * (is called by the MediaProxy).<br>
     * 
     * Checks if request is allowed by asking the UserManager.
     * 
     * @param session
     * @param mOId
     * @param fId
     * @param action
     * @return true if request specified by the given parameters is allowed
     * @throws ControllerException if a parameter is null
     */
	public boolean authorize(Session session, MOIdentifier mOId, Action action) throws ControllerException;

    /**
     * Returns the InputOptions the user has to insert mediadata. 
     * 
     * @return
     * @throws ControllerException
     */
	public InputOption [] getInputOptions() throws ControllerException;

    /**
     * Returns the OutputOptions for the user for the given moID.
     * 
     * @param mo
     * @return
     * @throws ControllerException
     */
	public OutputOption [] getOutputOptions(MOIdentifier mo) throws ControllerException;

    /**
     * Prepares the system for the input of media-data. <br>
     * After creating a new media-instance with the given metaData,<br>
     * a proxy is started to serve the coming input request.<br>
     * The ConnectionAddress of the proxy which will accept the data is returned.  
     * 
     * @param protocol
     * @param metaData
     * @return
     * @throws ControllerException
     */
	public ConnectionAddress  prepareInsert(ProtocolId protocolId, MetaData metaData) throws ControllerException;

    /**
     * Prepares the system to output media-data. <br>
     * The ConnectionAddress of the proxy which will deliver the <br>
     * requested data is returned.
     * 
     * @param mo
     * @param oo
     * @return
     * @throws ControllerException
     */
	public ConnectionAddress prepareOutput(MOIdentifier mo, OutputOption oo) throws ControllerException;

	/**
	 * Sets metadata for a media-object.
	 * The metadata is included in the MediaObject.
	 * 
	 * @param mo
	 */
	public void setMetaData(MediaObject mo) throws ControllerException;

    /**
     * Returns MetaData to the given moid.
     * 
     * @param mOId
     * @return
     * @throws ControllerException
     */
	public MetaDataAccess getMetaData(MOIdentifier mOId) throws ControllerException;

    /**
     * Deletes a media-object.
     * 
     * @param mOId
     * @return
     * @throws ControllerException
     */
	public boolean deleteMediaObject(MOIdentifier mOId) throws ControllerException;

    /**
     * Searches in the DB for the given SearchCriteria.
     *  
     * @param search
     * @return
     * @throws ControllerException
     */
	public SearchResult[] search(SearchCriteria search) throws ControllerException;

    /**
     * Executes a administrative command which is given as an AdminOperation.
     * 
     * @param operation
     * @return
     * @throws ControllerException if the command is unknown or an error occured
     */
	public AdminResult administration(AdminOperation operation) throws ControllerException;

    /**
     * Get information about the available filters for the given media-object. <br>
     * NOT IMPLEMENTET
     *   
     * @param mOId
     * @return
     */
	public FilterDetail [] getFilterOptions(MOIdentifier mOId);

    /**
     * Get a EditJob for the given media-object.
     * 
     * @param mo
     * @return
     * @throws ControllerException
     */
    public EditJobIdentifier getEditJob(MOIdentifier mo) throws ControllerException;

    /**
     * Adds a task to an EditJob.<br>
     * NOT IMPLEMENTED
     * 
     * @param jobId
     * @param filterId
     * @param action
     * @return
     */
    public EditTaskIdentifier addTaskToEditJob(EditJobIdentifier jobId, FilterPlugInIdentifier filterId, FilterAction action);

	/**
	 * Removes a task fom an EditJob.<br>
     * NOT IMPLEMENTED
     * 
	 * @param job
	 * @param task
	 */
    public void removeEditTask(EditJobIdentifier job, EditTaskIdentifier task);

	/**
	 * Aborts a complete EditJob.<br>
     * NOT IMPLEMENTED
     * 
	 * @param job
	 */
    public void abortEditJob(EditJobIdentifier job);

	/**
	 * Finishes a queued EditJob.<br>
     * NOT IMPLEMENTED
     * 
	 * @param job
	 * @param metaData
	 * @param maxQT
	 * @return
	 */
    public MediaObject finishEditJob(EditJobIdentifier job, MetaData metaData, QueueTime maxQT);

    /**
     * Gets the complete EditJob-List.<br>
     * NOT IMPLEMENTED
     * 
     * @return
     */
    public EditJobIdentifier [] getEditJobList();

    /**
     * MUST NOT BE CALLED FROM THE CLIENT
     * (is called by the MediaProxy).<br>
     * 
     * Reserves resources for a request.
     * The costs for the request represented by the given parameters are
     * calculated and the ResourceManager is asked to reserve resources.
     * A id for that request is returned to release it later.
     * 
     * @param session
     * @param moid
     * @param protocol
     * @param action
     * @return
     * @throws ControllerException
     */
    ResourceRequestIdentifier requestResources(Session session, MOIdentifier moid, FormatId fId, ProtocolId protocolId, Action action)throws ControllerException;

    /**
     * MUST NOT BE CALLED FROM THE CLIENT
     * (is called by the MediaProxy).<br>
     * 
     * Releases the reserved resources for the given ResourceRequestIdentifier.
     * Frees system resources.
     * 
     * @param request
     */
    void releaseResource(ResourceRequestIdentifier request) throws ControllerException;
 }
