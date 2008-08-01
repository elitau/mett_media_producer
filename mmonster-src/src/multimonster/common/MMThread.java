package multimonster.common;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import multimonster.common.util.MDBSender;

/**
 * MMThread is used to create a common way for asynchron execution like it is
 * provieded by a Thread but without violation of the J2EE specification.
 * 
 * @author Holger Velke
 */
public class MMThread {

	private static Hashtable instances = new Hashtable();
	private static Logger log = Logger.getLogger(MMThread.class);

	private Runnable runnable = null;
	private boolean stared = false;

    /**
     * @directed
     * @label uses 
     */
    private MMThreadBean lnkMMThreadBean;

	/**
	 * Allocates a new MMThread object so that it has <code>target</code> as
	 * its run object. A MMThreadBean (MessageDrivenBean) is used to do the
	 * asychron execution.
	 * 
	 * @param target
	 *            the object whose run method is called.
	 */
	public MMThread(Runnable target) {

		Hash key = null;

		this.runnable = target;
		key = new Hash(this.hashCode());

		synchronized (instances) {
			instances.put(key, this);
		}

		try {
			MDBSender.sendObjectMessage(MMThreadBean.JMS_QUEUE_NAME, key);
		} catch (NamingException e) {
			log.error(e);
		} catch (JMSException e) {
			log.error(e);
		}

//		log.debug("CREATED");
	}

	/**
	 * Strats the execution. It is <b>not</b> assured that the execution starts
	 * immediately.
	 */
	public synchronized void start() {
		this.stared = true;
		notifyAll();
	}

	/**
	 * gets an existing instance referenced by <code>key</code>.
	 * This is used by the MMThreadBean to get the reference of its caller in order to get it's runnable.
	 * 
	 * @param key
	 *            Hash referencing a instance of MMThread
	 * @return - The instance referenced by <code>key</code>. The returned
	 *         instance is deleted from the internal list.
	 *         <p>-<code>null</code> if there is no insance referenced by
	 *         Hash.
	 */
	static protected MMThread getInstance(Hash key) {
		Object instance = null;
		synchronized (instances) {
			instance = instances.remove(key);
		}
		return (MMThread) instance;
	}

	/**
	 * gets the target object which run method should be called.
	 * This is used by the MMThreadBean to get it's runnable.
	 * 
	 * @return Returns the Runable of the MMThread.
	 */
	public synchronized Runnable getRunnable() {
		while (!stared) {
			try {
				wait();
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
		return runnable;
	}
}
