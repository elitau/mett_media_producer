package multimonster.common.util;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Holger Velke
 */
public class MDBSender {

	static public void sendObjectMessage(String queueName, Serializable obj) throws NamingException, JMSException {

		Context context = null;
		QueueConnectionFactory queueFact = null;
		Queue queue = null;
		QueueConnection connection = null;
		QueueSession session = null;
		ObjectMessage message = null;

		context = new InitialContext();

		queueFact =
			(QueueConnectionFactory) context.lookup("ConnectionFactory");
		queue = (Queue) context.lookup(queueName);
		connection = queueFact.createQueueConnection();
		session = connection.createQueueSession(true, 1);
		message = session.createObjectMessage();

		message.setObject(obj);

		session.createSender(queue).send(message);
		session.commit();
		session.close();
		connection.close();

	}

}
