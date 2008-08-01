package multimonster.mediaaccess;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import multimonster.common.MMThread;
import multimonster.common.media.MIIdentifier;
import multimonster.common.pipe.Pipe;

/**
 * @author Marc Iseler
 * @ejb.bean name = "MediaAccessImpl" display-name = "MediaAccessFacade
 *           SessionBean" description = "The Facade of the MediaAccess-Package
 *           of MultiMonster" view-type = "remote" jndi-name =
 *           "multimonster/mediaaccess/MediaAccessFacade"
 * @ejb.env-entry name = "filepath" type = "java.lang.String" value =
 *                "/multimonster/video/mmInstance_" description = "Place where
 *                the mediafiles are stored in the filesystem"
 *  
 */
public class MediaAccessFacadeImplBean implements MediaAccessFacade,
		SessionBean {

	private SessionContext ctx;

	private Logger log;
	
	private String video_instance_path = "/tmp/mmonster/";

	/**
	 * @see MediaAccessFacade#newMediaInstanceData(Pipe)
	 * @ejb.interface-method view-type = "remote"
	 */
	public MIIdentifier newMediaInstanceData(Pipe inputPipe) {

		String fileName = null;
		FileWriter writer = null;
		MIIdentifier mIId = null;

		log.debug(".newMediaInstance()");

		// TODO better filename generation
		try {
			Context context = new InitialContext();
			log.debug("Filename from context: "
					+ context.lookup("java:/comp/env/filepath"));
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fileName = video_instance_path + "mmInstance_" + System.currentTimeMillis();

		try {
			writer = new FileWriter(inputPipe, fileName);
			(new MMThread(writer)).start();
		} catch (FileNotFoundException e) {
			// TODO handle exception properly
			log.error(e);
		}

		mIId = new MIIdentifier(fileName);

		return mIId;
	}

	/**
	 * @see MediaAccessFacade#getMediaInstanceData(MIIdentifier)
	 * @ejb.interface-method view-type = "remote"
	 */
	public Pipe getMediaInstanceData(MIIdentifier mIId) {

		String fileName = null;
		FileReader reader = null;
		Pipe output = null;

		log.debug(".getMediaInstance()");

		fileName = mIId.getLocation();

		output = new Pipe();
		try {
			reader = new FileReader(output, fileName);
			(new MMThread(reader)).start();
		} catch (FileNotFoundException e) {
			// TODO handle exception properly
			log.error(e);
			return null;
		}

		return output;
	}

	/**
	 * @see MediaAccessFacade#remMediaInstance(MIIdentifier)
	 * @ejb.interface-method view-type = "remote"
	 */
	public boolean remMediaInstance(MIIdentifier p0) {
		log.debug(".remMediaInstance()");
		// TODO not implemented
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	public void ejbActivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException, RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext context) throws EJBException,
			RemoteException {
		ctx = context;

	}

	/**
	 * @ejb.create-method
	 */
	public void ejbCreate() {
		this.log = Logger.getLogger(this.getClass());

		log.debug("CREATED");
	}
}