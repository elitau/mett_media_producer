package multimonster.converter;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multimonster.common.Format;
import multimonster.common.FormatId;
import multimonster.common.MMThread;
import multimonster.common.media.MIIdentifier;
import multimonster.common.media.MOIdentifier;
import multimonster.common.media.MediaInstance;
import multimonster.common.media.MetaData;
import multimonster.common.pipe.Pipe;
import multimonster.common.util.EjbCreator;
import multimonster.common.util.EjbHomeGetter;
import multimonster.converter.exceptions.ConverterException;
import multimonster.mediaaccess.interfaces.MediaAccessImpl;
import multimonster.mediaaccess.interfaces.MediaAccessImplHome;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster.systemadministration.interfaces.SystemAdministrationImplHome;

import org.apache.log4j.Logger;

/**
 * @author Holger Velke
 * 
 * @ejb.bean name = "ConverterImpl"
 * 		display-name = "ConverterFacade SessionBean"
 * 		description = "The Facade of the Converter-Pacage of MultiMonster"
 * 		view-type = "remote" 
 * 		jndi-name = "multimonster/converter/ConverterFacade"
 */
public class ConverterImplBean implements ConverterFacade, SessionBean {

	private ConverterPlugInFactory converterFactory;
	private MediaAccessImplHome mediaDataHome;
	private SystemAdministrationImplHome systemAdministrationHome;
	private Context context;
	private Logger log;

    /**
     * @label uses
     * @directed 
     */
    private MetaDataExtractor lnkMetaDataExtractor;

	/**
	 *  
	 */
	public ConverterImplBean() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.converter.ConverterFacade#getMediaInstance
	 * 	(multimonster.common.MOIdentifier, multimonster.common.Format)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public Pipe getMediaInstance(MOIdentifier mOId, FormatId fId)
		throws ConverterException {

		log.debug("getMediaInstance()");

		// check parameters
		if (mOId == null) {
			throw new ConverterException("MOIdentifier is null");
		}
		if (fId == null) {
			throw new ConverterException("FormatID is null");
		}

		MediaInstance mI = null;
		MIIdentifier mIId = null;
		ConverterPlugIn converter = null;
		Pipe output = null;

		mI = getMediaInstanceFromSystemAdministration(mOId, fId);
		mIId = mI.getIdentifier();

		if (!fId.equals(mI.getFormat().getFormatId())) {
			log.debug("sourceFormat != outputFormat");
			
			Format format = getFormatFromSystemAdministration(fId);
			
			converter =
				converterFactory.getConverterPlugIn(mI.getFormat(), format);
			
			converter.setInput(getMediaPipeFromMediaAccess(mIId));
			output = new Pipe();
			converter.setOutput(output);

			(new MMThread(converter)).start();

		} else {
			log.debug("sourceFormat == outputFormat");
			output = getMediaPipeFromMediaAccess(mIId);
		}

		return output;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.converter.ConverterFacade#getSourceMediaInstance
	 * 	(multimonster.common.MOIdentifier)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public Pipe getSourceMediaInstance(MOIdentifier mOId)
		throws ConverterException {

		log.debug("getSourceMediaInstance()");

		// check parameter

		if (mOId == null) {
			throw new ConverterException("MOIdentifier is null");
		}

		Pipe output = null;
		MIIdentifier mIId = null;


		mIId = getSourceMediaInstanceFromSystemAdministration(mOId);
		output = getMediaPipeFromMediaAccess(mIId);

		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.converter.ConverterFacade#addMediaObject
	 * 	(multimonster.common.MOIdentifier,
	 *      multimonster.common.Pipe)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public void addMediaObject(MOIdentifier mOId, Pipe input)
		throws ConverterException {

		log.debug("addMediaObject()");

		// check parameters

		if (mOId == null) {
			throw new ConverterException("MOIdentifier is null");
		}
		if (input == null) {
			throw new ConverterException("Pipe is null");
		}

		MIIdentifier mIId = null;
		MetaDataExtractor extractor = null;		
		Pipe output = null;

		// create a meta-data-extractor and start it.
		extractor = MetaDataExtractor.getInstance();
		if (extractor != null) {
			output = new Pipe();
			extractor.setInput(input);
			extractor.setOutput(output);
			(new MMThread(extractor)).start();
		} else {
			log.warn("no metadata-extraction available");
			output = input;
		}

		
		mIId = newMediaInstaceAtMediaAccess(output);
		
		if (extractor != null) {
			// extractor adds instace to systemAdministration
			extractor.setMIId(mIId);
			extractor.setMOId(mOId);
			extractor.setSystemAdministrationHome(systemAdministrationHome);
			extractor.setContext(context);
		} else {
			// direct add instance to systemAdministration
			MediaInstance mI = new MediaInstance(mIId, mOId, new Format(null));
			addMediaInstanceToSystemAdministration(mI, new MetaData());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.converter.ConverterFacade#removeMediaInstance
	 * 	(multimonster.common.MIIdentifier)
	 */
	/**
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean removeMediaInstance(MIIdentifier mIId)
		throws ConverterException {

		MediaAccessImpl mediaData = null;
		boolean result = false;

		log.debug("removeMediaInstance()");

		// check parameters
		if (mIId == null) {
			throw new ConverterException("MIIdentifier is null");
		}

		try {
			mediaData = EjbCreator.createMediaAccess(mediaDataHome, context);
			result = mediaData.remMediaInstance(mIId);
			mediaData.remove();
		} catch (RemoteException e) {
			log.error(e);
		} catch (CreateException e) {
			log.error(e);
		} catch (NamingException e) {
			log.error(e);
		} catch (RemoveException e) {
			log.error(e);
		}

		return result;
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

		this.log = Logger.getLogger(this.getClass());

		this.converterFactory = ConverterPlugInFactory.getInstance();

		// get MediaDataHome
		try {
			this.context = new InitialContext();
			mediaDataHome = EjbHomeGetter.getMediaAccessHome(context);
		} catch (NamingException e) {
			log.error(e);
		}
		// get SystemAdministrationHome
		try {
			systemAdministrationHome =
				EjbHomeGetter.getSystemAdministrationHome(context);
		} catch (NamingException e) {
			log.error(e);
		}

		log.debug("CREATED");
	}

	
	/**
	 * gets the media-instance-identifier of the source media-instance from the
	 * system administration
	 * 
	 * @param mOId the media object id
	 * @return the media-instance-identifier of the source media-instance
	 * @throws ConverterException
	 */
	private MIIdentifier getSourceMediaInstanceFromSystemAdministration
		(MOIdentifier mOId)
		throws ConverterException {

		MIIdentifier mIId = null;
		SystemAdministrationImpl systemAdministration = null;

		try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
					systemAdministrationHome,
					context);
			mIId = systemAdministration.getSourceMediaInstance(mOId);
			systemAdministration.remove();
		} catch (Exception e) {
			String etxt = "problem getting the MIIdentifier from" +
					" SystemAdministration";
			log.error(etxt, e);
			throw new ConverterException(etxt, e);
		}

		if (mIId == null) {
			throw new ConverterException("got 'null' MIIdentifier form " +
					"SystemAdministration");
		}
		
		return mIId;
	}

	/**
	 * @param mOId the media object id
	 * @param format
	 * @return
	 * @throws ConverterException
	 */
	private MediaInstance getMediaInstanceFromSystemAdministration(
		MOIdentifier mOId,
		FormatId fId)
		throws ConverterException {

		MediaInstance mI = null;
		SystemAdministrationImpl systemAdministration = null;

		try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
					systemAdministrationHome,
					context);
			mI = systemAdministration.getMediaInstance(mOId, fId);
			systemAdministration.remove();
		} catch (Exception e) {
			String etxt = "problem getting the MediaInstance from " +
					"SystemAdministration";
			log.error(etxt,	e);
			throw new ConverterException(etxt, e);
		}

		if (mI == null) {
			throw new ConverterException("got 'null' MediaInstance form " +
					"SystemAdministration");
		}

		if (mI.getMIId() == null) {
			throw new ConverterException(
				"got MediaInstance from SystemAdministration"
					+ " with 'null' MIIdentifier");
		}

		if (mI.getFormat() == null) {
			throw new ConverterException(
				"got MediaInstance from SystemAdministration with 'null' " +
				"Format");
		}

		return mI;
	}


	/**
	 * Gets the foramt identified by a specific foramt idnetifier
	 * 
	 * @param id
	 * @return
	 */
	private Format getFormatFromSystemAdministration(FormatId fId)
		throws ConverterException {

		Format format = null;
		SystemAdministrationImpl systemAdministration = null;

		try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
						systemAdministrationHome,
						context);
			format = systemAdministration.getFormat(fId);
		} catch (Exception e) {
			throw new ConverterException(
					"problem getting the Format from SystemAdministration",
					e);
		}
		
		if (format == null) {
			throw new ConverterException("got 'null' format from" +
					"SystemAdministration");
		}
		
		return format;
	}

	/**
	 * gets a pipe containing the mediadata of the media instance
	 * 
	 * @param mIId
	 * @return
	 * @throws ConverterException
	 */
	private Pipe getMediaPipeFromMediaAccess(MIIdentifier mIId)
		throws ConverterException {

		Pipe media = null;
		MediaAccessImpl mediaAccess = null;

		try {
			mediaAccess = EjbCreator.createMediaAccess(mediaDataHome, context);
			media = mediaAccess.getMediaInstanceData(mIId);
			mediaAccess.remove();
		} catch (Exception e) {
			String etxt = "problem getting mediaData";
			log.error(etxt, e);
			throw new ConverterException(etxt, e);
		} 

		if (media == null) {
			throw new ConverterException("got no Pipe form MediaData");
		}

		return media;
	}
	
	/**
	 * adds a mediainstance with it's specific metadata to the system
	 * administration
	 * 
	 * @param mI
	 * @param metaData
	 */
	private void addMediaInstanceToSystemAdministration
		(MediaInstance mI, MetaData metaData) {
		
		SystemAdministrationImpl systemAdministration = null;
		
		try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
						systemAdministrationHome,
						context);
			systemAdministration.addMediaInstance(mI, metaData);
			systemAdministration.remove();
		} catch (Exception e) {
			log.error(
					"problem adding MediaInstance to SystemAdministration: "
					+ e.getMessage());
		}		
	}

	/**
	 * @param output pipe containing the media data
	 * @return the media instance identifier of the new instance
	 */
	private MIIdentifier newMediaInstaceAtMediaAccess(Pipe output)
		throws ConverterException {
		
		MIIdentifier mIId = null;
		MediaAccessImpl mediaAccess = null;
		
		try {
			mediaAccess = EjbCreator.createMediaAccess(mediaDataHome, context);
			mIId = mediaAccess.newMediaInstanceData(output);
			mediaAccess.remove();
		} catch (Exception e) {
			String etxt = "problem adding new media instance to MediaData";
			log.error(etxt, e);
			throw new ConverterException(etxt, e);
		}
		if (mIId == null) {
			throw new ConverterException("got 'null' mIId form MediaData");
		}
		
		return mIId;
	}

}
