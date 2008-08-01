package multimonster.common;

import javax.ejb.MessageDrivenBean;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;


/* container-configuration - max number of instances
* in 'standardjboss.xml' improofe <MaximumSize> of the
* <proxy-factory-confg> of the 'message-driven-bean' <invoker-proxy-binding>
* default is "15"
*/ 
/**
 * MDB that is used by the MMThread class to do the asynchron execution of the
 * run method of its target.
 * 
 * @see multimonster.common.MMTheard
 * @author Holger Velke (sihovelk)
 * 
 * @ejb.bean name="MMThread"
 * 			 jndi-name="multimonster/coommon/MMThread" type="MDB"
 * 			 destination-type="javax.jms.Queue"
 * 
 * @jboss.destination-jndi-name name="queue/multimonster/common/MMThread" 
 */
public class MMThreadBean implements MessageDrivenBean, MessageListener {

	/**
	 * the name of the JMS queue the Bean listens on
	 */
	static public final String JMS_QUEUE_NAME =
		"queue/multimonster/common/MMThread";
	
	/** 
	 * The context for the message-driven bean, set by the EJB container.
	 */
	private javax.ejb.MessageDrivenContext messageContext = null;
	
	
	private Logger log = null;
	
	private Runnable run = null;

	
	/** Required method for container to set context. */
	public void setMessageDrivenContext(
		javax.ejb.MessageDrivenContext messageContext)
		throws javax.ejb.EJBException {
		this.messageContext = messageContext;
	}

	/**
	 * Required creation method for message-driven beans.
	 * 
	 * @ejb.create-method
	 */
	public void ejbCreate() {
		this.log = Logger.getLogger(this.getClass());
//		log.debug("CREATED");
	}

	/** Required removal method for message-driven beans. */
	public void ejbRemove() {
		messageContext = null;
	}

	/** 
	 * Required method that is called when a <code>message</code> is received
	 * 
	 * @param message
	 *  the message received form the queue
	 */
	public void onMessage(javax.jms.Message message) {

		Object msgObj = null;
		Hash key = null;
		
//		log.debug("onMessage()");

		// check and read message
		try {
			msgObj = ((ObjectMessage) message).getObject();
		} catch (JMSException e) {
			log.error("problem getting the Object from the message", e);
			log.error("unable to doWork");
			return;
		} catch (ClassCastException e) {
			log.error("message is no ObjectMessage", e);
			log.error("unable to doWork");
			return;
		} 

		try {
			key = (Hash) msgObj;
		} catch (ClassCastException e) {
			log.error("Object in message has wrong class", e);
			log.error("unable to doWork");
			return;
		}

		// initialize
		init(key);

		// do work
		if (run != null){
		 run.run();
		} else {
			log.error("Runnable is null - unable to doWork");
		}
	}

	private void init(Hash key) {
		MMThread caller = null;
		
		caller = MMThread.getInstance(key);

		if (caller == null) {
			log.error("unable to get my caller.");
			return;
		}

		run = caller.getRunnable();
	}	
}