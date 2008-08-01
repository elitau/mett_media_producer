/**
 * 
 */
package mett;

/**
 * @author elitau
 *
 */

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.Context;
import multimonster.converter.*;
import multimonster.converter.plugin.jmx.TCProbeCaller;

import org.apache.log4j.Logger;

/**
 * Annotations:
 * This is a Tutorial from http://www.javabeat.net/articles/40-creating-webservice-using-jboss-and-eclipse-europa-2.html 
 */

/**
 * This is a webservice class exposing a method called hello which takes a
 * input parameter and greets the parameter with hello.
 *
 * @author dhanago and ede
 * http://www.javabeat.net/articles/40-creating-webservice-using-jboss-and-eclipse-europa-1.html
 */

/*
 * @WebService indicates that this is webservice interface and the name
 * indicates the webservice name.
 */

@WebService(name = "MonsterWebService")

/*
 * @SOAPBinding indicates binding information of soap messages. Here we have
 * document-literal style of webservice and the parameter style is wrapped.
 */
@SOAPBinding
   (
         style = SOAPBinding.Style.DOCUMENT,
         use = SOAPBinding.Use.LITERAL,
         parameterStyle = SOAPBinding.ParameterStyle.WRAPPED
    )


public class MonsterWebService {
	
	
	private Context context;
	private static Logger log = Logger.getLogger(MonsterWebService.class);
	private static String TRANSPORTER_TO_USE =
		"multimonster.transporter.TCProbeExtractor";
	static private TCProbeCaller caller = null;
	static private MBeanServer mBeanServer = null;
	static final public String MULTIMONSTER_CONTROLLER_JMX_NAME = "multimonster/controller/ControllerFacade || multimonster:service=TCProbeCaller";
	public MonsterWebService(){
		// initialize
		if (caller == null) {
			caller = getTCProbeCaller();
		}
//		this.tcProbeId = caller.startTCProbeProcess();
	}
	
	/**
	 * This method accepts a string and prepends it with "Hello ".
	 * @param name
	 * @return String
	 * 
	 */
	@WebMethod
	public String getMedia( @WebParam(name = "key") String key ){
		return "Hello " + key + ", my caller ID: " + caller.getName();
	}
	
	static private TCProbeCaller getTCProbeCaller() {
		if (mBeanServer == null)
			getMBeanServer();
		if (caller == null) {
			try {
				// for retrieval:
				ObjectName objName = new ObjectName(TCProbeCaller.JMX_NAME);
				// get the Object-reference (usable only if EJB in the same
				// JVM)
				caller =
					(TCProbeCaller) mBeanServer.invoke(
						objName,
						"returnThis",
						null,
						null);
			} catch (Exception e) {
				log.error("problem getting TCProbeCaller MBean", e);
			}
		}
		return caller;
	}
	
	static private MBeanServer getMBeanServer() {

		if (mBeanServer == null) {
			ArrayList mbeanServers = MBeanServerFactory.findMBeanServer(null);
			mBeanServer = (MBeanServer) mbeanServers.get(0);
		}

		return mBeanServer;
	}
	
}
