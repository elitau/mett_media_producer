package multimonster.edit;

import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multimonster.common.MMThread;
import multimonster.common.UserIdentifier;
import multimonster.common.edit.EditJobIdentifier;
import multimonster.common.edit.FilterAction;
import multimonster.common.edit.FilterPlugInIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.pipe.Pipe;
import multimonster.common.resource.Costs;
import multimonster.common.resource.QueueTime;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.resource.ResourceWaiter;
import multimonster.common.util.EjbCreator;
import multimonster.common.util.EjbHomeGetter;
import multimonster.converter.exceptions.ConverterException;
import multimonster.converter.interfaces.ConverterImpl;
import multimonster.converter.interfaces.ConverterImplHome;
import multimonster.edit.exceptions.EditException;
import multimonster.resourcemanager.exceptions.ResourceManagerException;
import multimonster.resourcemanager.interfaces.ResourceManagerImpl;
import multimonster.resourcemanager.interfaces.ResourceManagerImplHome;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster
	.systemadministration
	.interfaces
	.SystemAdministrationImplHome;

import org.apache.log4j.Logger;

/**
 * @author Holger Velke
 * 
 * @ejb.bean name="EditHandler"
 * jndi-name="multimonster/edit/plugin/EditHandler" type="MDB"
 * destination-type="javax.jms.Queue"
 * 
 * @jboss.destination-jndi-name
 * name="queue/multimonster/edit/plugin/EditHandler"
 *  
 */
public class EditHandler implements Runnable, ResourceWaiter {

	private Context context;

	private EditJob job;
	private ResourceManagerImplHome resourceManagerHome;
	private ResourceRequestIdentifier rrId;
	private SystemAdministrationImplHome systemAdministrationHome;
	private ConverterImplHome converterHome;
	private FilterPlugInFactory filterFactory;
	private Logger log;
	private Boolean resourcesGranted = null;

	public EditHandler(EditJobIdentifier jobId) {
		context = null;

		this.log = Logger.getLogger(this.getClass());
		this.filterFactory = FilterPlugInFactory.getInstance();

		//initialize - get job from list
		this.job = EditJobList.getInstance().get(jobId);

		try {
			context = new InitialContext();
		} catch (NamingException e) {
			log.error(e);
			return;
		}
		try {
			systemAdministrationHome =
				EjbHomeGetter.getSystemAdministrationHome(context);
		} catch (NamingException e) {
			log.error(e);
		}
		try {
			resourceManagerHome = EjbHomeGetter.getResourceManagerHome(context);
		} catch (NamingException e) {
			log.error(e);
		}
		try {
			converterHome = EjbHomeGetter.getConverterHome(context);
		} catch (NamingException e) {
			log.error(e);
		}
		log.debug("CREATED");
	}

	public void run() {

		log.debug("run()");

		try {
			getResources();

			waitForResources();

			if (resourcesGranted.booleanValue()) {
				startEditing();
				finishedEditing();
			} else {
				log.debug("got no Resources - job aborted.");
			}
		} catch (Exception e) {
			log.error("caught: ", e);
		}
	}

	private void getResources() throws EditException, InterruptedException {

		FilterAction[] actions = null;
		MOIdentifier mOId = null;
		Costs costs = null;
		UserIdentifier editor = null;
		QueueTime maxQT = null;

		log.debug("requestResources()");

		// get cost estimation
		actions = job.getActions();
		mOId = job.getSourceMOId();
		editor = job.getEditor();
		maxQT = job.getMaxQueueTime();

		costs = getCosts(mOId, actions);

		// request resources
		if (maxQT == null) { //ResourceRequest without waiting

			rrId = requestResources(editor, costs);

			if (rrId == null) { // no Resources
				log.debug(" - resources denyed!");
				this.resourcesGranted = Boolean.FALSE;
			} else { // Resources granted
				log.debug(" - resources granted");
				this.resourcesGranted = Boolean.TRUE;
			}
		} else { //ResourceResquest with maxWaitTime

			rrId = requestResources(editor, costs, maxQT);

			// Wait for resources
			if (rrId == null) {
				throw new EditException("null RequestResourceIdentifier returned");
			}
		}
	}

	private void waitForResources() throws InterruptedException {
		log.debug("test weather to wait.");
		while (true) {
			synchronized (this) {
				if (this.resourcesGranted == null) {
					log.debug("start waiting.");
					wait();
				} else {
					log.debug("don't wait.");
					break;
				}
			}
		}
	}

	private void startEditing() throws EditException {

		MOIdentifier sourceMOId = null;
		MOIdentifier newMOId = null;
		EditTask[] editTasks = null;
		Pipe sourcePipe = null;
		Pipe currentPipe = null;
		Pipe resultPipe = null;
		FilterPlugIn lastFilter = null;

		log.debug("startEditing()");

		if (job != null) {
			sourceMOId = job.getSourceMOId();
			newMOId = job.getNewMOId();
			editTasks = job.getEditTasks();
		}

		// get Pipe form converter
		sourcePipe = getSourceMediaData(sourceMOId);

		// plug all filters in series
		currentPipe = sourcePipe;
		if (editTasks != null) {

			for (int i = 0; i < editTasks.length; i++) {

				FilterAction action = null;
				FilterPlugInIdentifier filterId = null;
				FilterPlugIn filter = null;

				action = editTasks[i].getAction();
				filterId = editTasks[i].getFilterId();

				// create filter
				filter = filterFactory.getFilterPlugIn(filterId, action);
				// init filter
				filter.setInput(currentPipe);
				currentPipe = new Pipe();
				filter.setOutput(currentPipe);

				// calls an MDB
				 (new MMThread(filter)).start();

				lastFilter = filter;
			}
			log.debug("connected " + editTasks.length + " Filters");
		}
		resultPipe = currentPipe;

		// plug resultpipe to converter
		addMediaData(newMOId, resultPipe);

		log.debug("waiting for PlugIns to finish their work");
		try {
			lastFilter.waitForFinishing();
		} catch (InterruptedException e) {
			log.error("Got interupted while waiting for the filter-chain", e);
		}
		log.debug("PlugIns have finished");

	}

	private void finishedEditing() throws EditException {

		Costs realCosts = null;
		MOIdentifier mOId = null;
		FilterAction[] actions = null;

		log.debug("finishEditing()");

		if (job != null) {
			mOId = job.getSourceMOId();
			actions = job.getActions();
		} else {
			throw new EditException("job to finish is 'null'");
		}

		realCosts = releaseResources(rrId);

		realCostsToSytemAdministration(mOId, actions, realCosts);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.common.ResourceWaiter#grantResource(multimonster.common.ResourceRequestIdentifier)
	 */
	public void grantResource(ResourceRequestIdentifier rrId) {

		log.debug("grantResource()");

		synchronized (this) {
			this.resourcesGranted = Boolean.TRUE;
			this.notify();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.common.ResourceWaiter#denyResource(multimonster.common.ResourceRequestIdentifier)
	 */
	public void denyResource(ResourceRequestIdentifier rrId) {

		log.debug("denyResource()");

		synchronized (this) {
			this.resourcesGranted = Boolean.FALSE;
			this.notify();
		}
	}

	/**
	 *  
	 */
	private Costs getCosts(MOIdentifier mOId, FilterAction[] actions)
		throws EditException {

		Costs costs = null;
		SystemAdministrationImpl systemAdministration = null;

		try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
					systemAdministrationHome,
					context);
			costs = systemAdministration.calculateCosts(mOId, actions);
			systemAdministration.remove();
		} catch (RemoveException e) {
			log.error("problem removeing SystemAdministration", e);
		} catch (Exception e) {
			throw new EditException("problem getting Costs", e);
		}

		return costs;
	}

	private ResourceRequestIdentifier requestResources(
		UserIdentifier editor,
		Costs costs)
		throws EditException {

		ResourceRequestIdentifier rrId = null;
		ResourceManagerImpl resourceManager = null;

		try {
			resourceManager =
				EjbCreator.createResourceManager(resourceManagerHome, context);
			rrId = resourceManager.requestResources(editor, costs);
			resourceManager.remove();
		} catch (RemoveException e) {
			log.error("problem removeing ResourceManager", e);
		} catch (ResourceManagerException e) {
			throw new EditException("problem at ResourceManager", e);
		} catch (Exception e) {
			throw new EditException("problem requesting resources", e);
		}
		
		return rrId;
	}

	private ResourceRequestIdentifier requestResources(
		UserIdentifier editor,
		Costs costs,
		QueueTime maxQT)
		throws EditException {

		ResourceRequestIdentifier rrId = null;
		ResourceManagerImpl resourceManager = null;

		try {
			resourceManager =
				EjbCreator.createResourceManager(resourceManagerHome, context);
			// TODO create indirection for calling resourcewaiter
			rrId = resourceManager.requestResources(editor, costs, maxQT, this);
			resourceManager.remove();
		} catch (RemoveException e) {
			log.error("problem removeing ResourceManager", e);
		} catch (ResourceManagerException e) {
			throw new EditException("problem at ResourceManager", e);
		} catch (Exception e) {
			throw new EditException("problem requesting resources", e);
		}

		return rrId;
	}

	private Pipe getSourceMediaData(MOIdentifier mOId) throws EditException {

		Pipe source = null;
		ConverterImpl converter = null;

		try {
			converter = EjbCreator.createConverter(converterHome, context);
			source = converter.getSourceMediaInstance(mOId);
			converter.remove();
		} catch (RemoveException e) {
			log.error("Problem removeing Conveter", e);
		} catch (ConverterException e) {
			throw new EditException("error at Converter", e);
		} catch (Exception e) {
			throw new EditException("problem getting sourceMedia", e);
		}

		return source;
	}

	private void addMediaData(MOIdentifier mOId, Pipe pipe)
		throws EditException {

		ConverterImpl converter = null;

		try {
			converter = EjbCreator.createConverter(converterHome, context);
			converter.addMediaObject(mOId, pipe);
			converter.remove();
		} catch (RemoveException e) {
			log.error("problem removeing converter", e);
		} catch (ConverterException e) {
			throw new EditException("problem at Converter", e);
		} catch (Exception e) {
			throw new EditException("problem adding MediaInstance", e);
		}
	}

	private Costs releaseResources(ResourceRequestIdentifier rrId)
		throws EditException {

		ResourceManagerImpl resourceManager = null;
		Costs realCosts = null;

		try {
			resourceManager =
				EjbCreator.createResourceManager(resourceManagerHome, context);
			realCosts = resourceManager.releaseResources(rrId);
			resourceManager.remove();
		} catch (ResourceManagerException e) {
			throw new EditException(
				"tried to release resources, problem at ResourceManager",
				e);
		} catch (RemoveException e) {
			log.warn("problem removeing ResourceManager");
		} catch (Exception e) {
			throw new EditException("problem releasing resources", e);
		}

		return realCosts;
	}

	private void realCostsToSytemAdministration(
		MOIdentifier mOId,
		FilterAction[] actions,
		Costs realCosts)
		throws EditException {

		SystemAdministrationImpl systemAdministration = null;

		try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
					systemAdministrationHome,
					context);
			systemAdministration.realCosts(mOId, actions, realCosts);
			systemAdministration.remove();
		} catch (RemoveException e) {
			log.error("problem removeing SystemAdministration", e);
		} catch (Exception e) {
			throw new EditException("problem calling SystemAdministration", e);
		}
	}
}
