package multimonster.converter;

import javax.naming.Context;

import multimonster.common.Format;
import multimonster.common.media.MIIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MediaInstance;
import multimonster.common.media.MetaData;
import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;
import multimonster.common.util.EjbCreator;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster
	.systemadministration
	.interfaces
	.SystemAdministrationImplHome;

import org.apache.log4j.Logger;


/**
 * TODO Create Java-Doc
 * 
 * @author Holger Velke (sihovelk)
 */
public abstract class MetaDataExtractor implements Runnable {

	private static Logger log = Logger.getLogger(MetaDataExtractor.class);

	// TODO use administrative setting here
	private static String EXTRACTOR_TO_USE =
		"multimonster.converter.plugin.TCProbeExtractor";
	
	private SystemAdministrationImplHome systemAdministrationHome;
	private Context context;

	private MOIdentifier mOId = null;
	private MIIdentifier mIId = null;

	private Pipe input = null;
	private Pipe output = null;
	protected Format format = null;
	protected MetaData metaData = null;

	// state attibutes
	private boolean finished = false;
	private boolean ready = false;


	/**
	 * @param ready
	 *            The ready to set.
	 */
	protected void setReady(boolean ready) {
		this.ready = ready;
	}

	/**
	 *  
	 */
	protected void setFinished() {
		finished = true;
	}
	
	/**
	 * @return <code>true</code> if the extration is finished.
	 */
	protected boolean isFinished() {
		return this.finished;
	}

	/**
	 * @return
	 */
	protected boolean isReady() {
		return this.ready;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		byte[] buf = null;
		int count = 0;
		int readBufSize = Pipe.getPipeSegmentSize();

		// check preconditions
		if ((input == null) || (output == null)) {
			log.error("Pipes missing - unable to do work");
		}
		
		try {
			output.waitForPipeSetup();
		} catch (PipeClosedException e){
			log.warn("pipe was not setup - unable to do work");
			input.close();
			return;
		}		
		input.setupFinished();
		
		while (true) {

			try {
				buf = input.read(readBufSize);
			} catch (PipeClosedException e) {
				break;
			}

			doWork(buf);

			try {
				output.write(buf);
			} catch (PipeClosedException e) {
				break;
			}
			count += buf.length;
		}
		input.close();
		output.close();
		finishWork();

		addMediaInstance();
	}

	private void addMediaInstance() {

		SystemAdministrationImpl systemAdministration = null;
		MediaInstance mI = null;

		this.format = parseFormat();
		this.metaData = parseMetaData();

		mI = new MediaInstance(mIId, mOId, this.format);

		try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
					systemAdministrationHome,
					context);
			systemAdministration.addMediaInstance(mI, this.metaData);
			systemAdministration.remove();
		} catch (Exception e) {
			log.error(
				"problem adding MediaInstance to SystemAdministration: "
					+ e.getMessage());
		}
	}

	public static MetaDataExtractor getInstance() {

		MetaDataExtractor instance = null;

		try {
			instance =
				(MetaDataExtractor) Class
					.forName(EXTRACTOR_TO_USE)
					.newInstance();
		} catch (ClassNotFoundException e) {
			log.error(
				"unable to instanciate MetaDataExtractor, class'"
					+ EXTRACTOR_TO_USE
					+ "' not found");
		} catch (Exception e) {
			log.error("unable to instanciate MetaDataExtractor", e);
		}
		return instance;
	}

	/**
	 * @param inputPipe
	 */
	public void setInput(Pipe inputPipe) {
		this.input = inputPipe;
	}

	/**
	 * @param outputPipe
	 */
	public void setOutput(Pipe outputPipe) {
		this.output = outputPipe;
	}

	/**
	 * @param id
	 *            The mIId to set.
	 */
	public void setMIId(MIIdentifier id) {
		mIId = id;
	}

	/**
	 * @param id
	 *            The mOId to set.
	 */
	public void setMOId(MOIdentifier id) {
		mOId = id;
	}

	/**
	 * @param context
	 *            The context to set.
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @param systemAdministrationHome
	 *            The systemAdministrationHome to set.
	 */
	public void setSystemAdministrationHome(SystemAdministrationImplHome systemAdministrationHome) {
		this.systemAdministrationHome = systemAdministrationHome;
	}
	
	/**
	 * @param buf
	 */
	abstract protected void doWork(byte[] buf);

	/**
	 *  
	 */
	abstract protected Format parseFormat();

	/**
	 *  
	 */
	abstract protected void finishWork();

	/**
	 * @return
	 */
	abstract protected MetaData parseMetaData();
}
