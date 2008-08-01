package multimonster.mediaproxy.plugin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import multimonster.common.ConnectionAddress;
import multimonster.common.MMThread;
import multimonster.common.setting.Setting;
import multimonster.common.setting.SettingDomain;
import multimonster.common.setting.SettingValue;
import multimonster.mediaproxy.MediaProxyPlugin;
import multimonster.mediaproxy.exceptions.MediaProxyException;
import multimonster.mediaproxy.exceptions.MediaProxyPlugInException;
import multimonster.systemadministration.SettingProxy;

import org.apache.log4j.Logger;

/**
 * A output proxy that is able to handle http-requests.
 * A copy of the HttpProxy.
 * 
 * Just for proove of concept to choose between different Plugins.
 * 
 * @author Jörg Meier
 */
public class Http2Proxy extends MediaProxyPlugin {

	private static Logger log = null;
	private static SettingProxy sproxy = null;

	static {
		log = Logger.getLogger(Http2Proxy.class);
		
		try {
			sproxy = SettingProxy.getInstance(Http2Proxy.class);		
			Setting mySetting;
			
			mySetting = new Setting("StartPort", new SettingValue(10080), new SettingDomain(1025, 65000), "the port the proxy begins to bind to");
			sproxy.registerSetting(mySetting);
			
			mySetting = new Setting("EndPort", new SettingValue(10580), new SettingDomain(1025, 65000), "the highest port the proxy tries to bind to");
			sproxy.registerSetting(mySetting);

			mySetting = new Setting("NumberOfRetry", new SettingValue(10), new SettingDomain(1, 1000), "how many times the proxy tries to get a port");
			sproxy.registerSetting(mySetting);

		} catch (Exception e1) {
			log.error("Error registering setting.");
		}
	}
	
	private ServerSocket ssocket = null;

	private static int START_PORT = 10080;
	private static int END_PORT = 10580;
	/** the next port to use */
	private static int currentPort = 10080;
	private static int numberOfRetry = 10;

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.mediaproxy.MediaProxyPlugin#init()
	 */
	protected URL initPlugIn() throws MediaProxyException {

		InetAddress ip = null;
		String host = "";
		int port = 0;
		
		MMThread mmThread = null;
		boolean successful = false;
		String fileWithPath = "";
		URL actualUrl = null;
		String errorText = "";

		initPortCycle();
		
		try {
			//getHostName of this machine
			host = InetAddress.getLocalHost().getHostName();
			
		} catch (UnknownHostException e1) {
			log.error("Couldn't get Hostname of the system: "
							+ e1.getMessage());
		}
		// get IP of given hostname
		try {
			ip = InetAddress.getByName(host);
			
		} catch (UnknownHostException e1) {
			errorText = "Couldn't get IP of host '" + host + "'.";
			log.error(errorText);
			throw new MediaProxyPlugInException(errorText);
		}
		
		fileWithPath = 	 "/" +caddr.getSession().getId()
						+"/" +caddr.getMOId().getMoNumber() 
						+"/" +caddr.getFormatId().getId()
						+"/" +"httpDownloadedMediaFile_" 
							 +System.currentTimeMillis() +".mpg";
						
			
		// trys to bind to IP and port in some iterations:
		while (numberOfRetry > 0 && !successful) {
			try {
				port = getNextPort();
				
				// create socket on port and ip
				ssocket = new ServerSocket(port, 0, ip);

				actualUrl =
					new URL(
						"http",
						ip.getCanonicalHostName(),
						port,
						fileWithPath);						
				
				successful = true;
				
			} catch (IOException e) {
				errorText =
					"Couldn't create socket on "
						+ ip.getHostAddress()
						+ ":"
						+ port
						+ " "
						+ e.getMessage();
				//log.debug(errorText);
				// try next port
				numberOfRetry--;

			}
		}
		if (!successful) {
			// all trys didn't succeed, new ServerSocket() wasn't successful
			throw new MediaProxyPlugInException(errorText);
		}

		log.debug(
			"Created Serversocket, waiting for connection on IP "
				+ ip.getHostAddress()
				+ " and port "
				+ port
				+ " in a seperate thread - init finished.");

		// starting thread:
		mmThread = new MMThread(this);
		mmThread.start();

		return actualUrl;
	}

	private void initPortCycle(){
		//get Value from Setting
		if (sproxy != null && sproxy.getValue("NumberOfRetry") != null){
			numberOfRetry = ((Integer)(sproxy.getValue("NumberOfRetry")).getValueCont()).intValue();
			START_PORT = ((Integer)(sproxy.getValue("StartPort")).getValueCont()).intValue();
			END_PORT = ((Integer)(sproxy.getValue("EndPort")).getValueCont()).intValue();
	
			if (START_PORT > END_PORT){
				log.warn("START_PORT > END_PORT, will use END_PORT=START_PORT=" +END_PORT);
				START_PORT = END_PORT; 
			}
			
			if (currentPort < START_PORT || currentPort > END_PORT){
				//currentPort is out of range
				currentPort = START_PORT;
			}
		} else {
			log.warn("Setting doesn't work, taking default values.");
		}
	}
	
	/**
	 * @return the next port to use
	 */
	private int getNextPort() {
		if (currentPort <= END_PORT){
			return currentPort++;
		} else {
			currentPort = START_PORT;
			return currentPort;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.mediaproxy.MediaProxyPlugin#connect(multimonster.common.ConnectionAddress)
	 */
	public void connect(ConnectionAddress p0) {

		log.warn("Not implemented, connection is accepted only on the socket.");
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.mediaproxy.MediaProxyPlugin#disconnect()
	 */
	public void disconnect() {

		log.warn(
			"Not implemented, connection is controlled only on the socket.");
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		Socket socket = null;
		int waitTimeForRequests = 0;
		String errorText = "";

		//TODO use Setting for waitTimeForRequests
		waitTimeForRequests = 60 * 1000; // 60sec
		
		try {
			ssocket.setSoTimeout(waitTimeForRequests); 
		}  catch (SocketException e1) {
			log.error("problem setting timeout.");			
		}
		
		while (true) {

			// wait for connection, then create new socket:
			try {
				socket = ssocket.accept();

			} catch (SocketTimeoutException e){
				//socket timed out, now no new request are possible
				// log.debug("socket-timeout");				
				break;
			} catch (IOException e2) {
				errorText = "Couldn't accept connection: " + e2.getMessage();
				log.error(errorText);
				break;
			}

			(new MMThread(new HttpProxyHandler(socket, this, caddr))).start();
		}		
		
		if (ssocket != null) {
			try {
				ssocket.close();
			} catch (IOException e) {

			}
		}
	}
}
	