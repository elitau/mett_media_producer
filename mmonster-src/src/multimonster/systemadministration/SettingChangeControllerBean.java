package multimonster.systemadministration;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import multimonster.common.setting.SettingID;
import multimonster.common.setting.SettingValue;
import multimonster.systemadministration.exceptions.SettingNotExistsException;

/**
 * Receives Message when SettingValue was changed.
 * Distributes ChangeEvent to SettingProxies
 * 
 * @ejb.bean
 *     name="SettingChangeController"
 *     type="MDB"
 *     acknowledge-mode="Auto-acknowledge"
 *     destination-type="javax.jms.Queue"
 *     subscription-durability="NonDurable"
 *     jndi-name = "multimonster/systemadministration/SettingChangeController"
 * 
 * @jboss.destination-jndi-name
 *     name="queue/multimonster/systemadministration/SettingQueue"
 *
 */
public class SettingChangeControllerBean
	implements MessageDrivenBean, MessageListener {

	MessageDrivenContext ctx = null;
	Logger log = null;

	public final static String JMS_QUEUE_NAME =
		"queue/multimonster/systemadministration/SettingQueue";

	public static String TEST = "TEST1";

	private void publishSettingChange(
		SettingID settingID,
		SettingValue value) {
		try {
			Context ctx = new InitialContext();

			QueueConnectionFactory queueConnectionFactory =
				(QueueConnectionFactory) ctx.lookup("QueueConnectionFactory");

			TopicConnectionFactory topicConnectionFactory =
				(TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
		 * Required creation method for message-driven beans.
		 * 
		 * @ejb.create-method
		 */
	public void ejbCreate() {
		this.log = Logger.getLogger(this.getClass());
		log.debug("SettingChangeController was created!");
	}

	/* (non-Javadoc)
	 * @see javax.ejb.MessageDrivenBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.ejb.MessageDrivenBean#setMessageDrivenContext(javax.ejb.MessageDrivenContext)
	 */
	public void setMessageDrivenContext(MessageDrivenContext arg0)
		throws EJBException {
		this.ctx = arg0;
		this.log = Logger.getLogger(this.getClass());
		log.debug("SettingChangeController was instanciated!");

	}

	/* (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 * wird beim Erhalt einer Message aufgerufen und leitet die Wertänderung
	 * an den entsprechenden SettingProxy weiter
	 */
	public void onMessage(Message msg) {
		log.info("Message received!");
		Object obj = null;
		SettingID settingID = extractID(msg);
		short prefix = 0;
		SettingValue setValue = null;
		SettingProxy proxy = null;

		if (settingID != null) {
			// Den zugehörigen SettingProxy benachrichtigen
			
			SettingAdministration settingAdmin = new SettingAdministration();
			
			// zu dem Setting das Prefix aus der DB holen ...
			prefix = settingAdmin.getPrefix(settingID);
			log.debug("Prefix ist: " + prefix);
			
			// ... und dem SettingID-Object das Prefix zuweisen
			settingID.setPrefix(prefix);
			
			try {
				// Wert aus DB holen
				setValue = settingAdmin.getValue(settingID);
				log.debug("Neues Setting Value ist: " + setValue.getValueCont().toString());
			} catch (SettingNotExistsException e2) {
				log.error("Verwendetes Setting existiert nicht!");
			} 
			
			// Referenz auf zugehörigen SettingProxy holen
			proxy = settingAdmin.getSettingProxy(prefix);
			
			// Wertänderung dem Proxy bekannt geben
			if (proxy != null) {
				proxy.updateValue(settingID, setValue);
			} else {
				log.error("SettingProxy-Referenz ist null!");
			}
		}

//		try {
//			String classname = (new MOSearch()).getClass().getName();
//			String fieldname = "TEXT";
//			log.info("Klassenname lautet: " + classname);
//			
//			
//			//Class searchClass = (new MOSearch()).getClass();
//			Class searchClass = null;
//			
//			searchClass = Class.forName(classname);
//			
//			Field test = searchClass.getField(fieldname);
//			test.set(null, "Der ersetzte Text");
//
//			//			log.info("Typ des Felds: " + test.getType());
//			//			log.info("Name des Felds: " + test.getName());
//			//			log.info("Name der deklarierenden Klasse: " + test.getDeclaringClass());
//		} catch (SecurityException e1) {
//			log.error("SecurityException:" + e1);
//		} catch (NoSuchFieldException e) {
//			log.error("NoSuchFieldException" + e);
//		} catch (IllegalArgumentException e) {
//			log.error(e);
//		} catch (IllegalAccessException e) {
//			log.error(e);
//		} catch (ClassNotFoundException e) {
//			log.error(e);
//		}

	}

	//**************** Helper Methods ********************

	private SettingID extractID(Message msg) {
		Object obj = null;
		SettingID settingID = null;

		try {
			obj = ((ObjectMessage) msg).getObject();
		} catch (JMSException e) {
			log.error(e);
			return null;
		} catch (ClassCastException e) {
			log.error(e);
			return null;
		}

		if (obj != null) {
			try {
				settingID = (SettingID) obj;
			} catch (ClassCastException e) {
				log.error(e);
				return null;
			}
		}

		log.debug("SettingID is " + settingID.getId());
		log.debug("SettingPrefix is " + settingID.getPrefix());

		return settingID;
	}
	
}
