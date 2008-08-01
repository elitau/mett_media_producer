package mett.producer;

import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.Context;

import multimonster.converter.ConverterImplBean;
import multimonster.converter.plugin.jmx.TCProbeCaller;

import org.apache.log4j.Logger;

/**
 * This class is an adapter pattern realised for the Multimonster ControllerImplBean.
 * It authenticates the request, searches for a given key and returns an URI to the 
 * found media which can be streamed by the consumer.
 * 
 * @author elitau
 *
 */
public class MonsterAdapter {
	
	
	/**
	 * ATTRIBUTES
	 */	
//	private Context context;
	private static Logger log = Logger.getLogger(MonsterWebService.class);
	private static ConverterImplBean monsterConverter = null;
	private static MBeanServer mBeanServer = null;
	private static final String MULTIMONSTER_CONTROLLER_JMX_NAME = "multimonster/controller/ControllerFacade || multimonster:service=TCProbeCaller";
	
	
	
	/*
	 * Constructor
	 * Instatiates the ConverterImplBean and authenticate the user.
	 */
	public MonsterAdapter(){
//	TODO: get an instance of the ConverterImplBean
		// initialize
		if (caller == null) {
			caller = getTCProbeCaller();
		}
//	TODO: authenticate
	}
	public MonsterAdapter MonsterAdapter(String userName, String userPassword){
		
		return this;
	}
	
	/**
	 * PUBLIC
	 */
	
//	The JMX name to locate the java bean with the JavabeanContainer.
	private String MULITMONSTER_CONTROLLER_JMX_NAME;
	private mett.producer.MonsterWebService unnamed_MonsterWebService_;

	public void getMedia(Object key_String, Object metadata_Metadata) {
		throw new UnsupportedOperationException();
	}

	private static String getMonsterConverterInstance() {
		throw new UnsupportedOperationException();
	}

	private void searchMedia_key_String_() {
		throw new UnsupportedOperationException();
	}

	
	
	/**
	 * PRIVATE
	 */
	private boolean authenticate() {
		throw new UnsupportedOperationException();
	}

	private void getLogger() {
		throw new UnsupportedOperationException();
	}
	
	static private ConverterImplBean getConverterImplBean() {
		if (mBeanServer == null)
			getMBeanServer();
		if (monsterConverter == null) {
			try {
				// for retrieval:
				ObjectName objName = new ObjectName(MULTIMONSTER_CONTROLLER_JMX_NAME);
				// get the Object-reference (usable only if EJB in the same
				// JVM)
				monsterConverter =
					(TCProbeCaller) mBeanServer.invoke(
						objName,
						"returnThis",
						null,
						null);
			} catch (Exception e) {
				log.error("problem getting TCProbeCaller MBean", e);
			}
		}
		return monsterConverter;
	}
	
	static private MBeanServer getMBeanServer() {

		if (mBeanServer == null) {
			ArrayList mbeanServers = MBeanServerFactory.findMBeanServer(null);
			mBeanServer = (MBeanServer) mbeanServers.get(0);
		}

		return mBeanServer;
	}
}