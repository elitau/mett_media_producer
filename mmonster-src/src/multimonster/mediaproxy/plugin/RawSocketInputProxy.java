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
import multimonster.mediaproxy.MediaProxyPlugin;
import multimonster.mediaproxy.exceptions.MediaProxyException;
import multimonster.mediaproxy.exceptions.MediaProxyPlugInException;

import org.apache.log4j.Logger;


/**
 * A input proxy that takes data on a socket.
 * 
 * @author Jörg Meier
 */
public class RawSocketInputProxy extends MediaProxyPlugin {

	private Logger log = Logger.getLogger(this.getClass());
	private ServerSocket ssocket = null;

	/* (non-Javadoc)
	 * @see multimonster.mediaproxy.MediaProxyPlugin#init()
	 */
	protected URL initPlugIn() throws MediaProxyException {
		
		log.debug("initPlugIn() called.");
				
		int startPort = 0;
		int number_of_retry = 50;
		boolean successful = false;	
		InetAddress ip = null;
		String host = "";
		int port = 0;
		MMThread mmThread = null;
		URL actualUrl = null;
		String errorText = "";

		// TODO use Setting for startPort
		startPort = 10080;

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

		port = startPort;
		// trys to bind to IP and port in some iterations:
		while (number_of_retry > 0 && !successful) {
			try {
				// create socket on port and ip
				ssocket = new ServerSocket(port, 0, ip);

				actualUrl =
					new URL(
						"http",
						host,
						port,
						"uploadFile");
				
				successful = true;
				
			} catch (IOException e) {
				errorText =
					"Couldn't create socket on "
						+ ip.getHostAddress()
						+ ":"
						+ port
						+ " "
						+ e.getMessage();
				
				// try next port
				port++;
				number_of_retry--;
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

	/* (non-Javadoc)
	 * @see multimonster.mediaproxy.MediaProxyPlugin#connect(multimonster.common.ConnectionAddress)
	 */
	public void connect(ConnectionAddress p0) {
		
		log.warn("Not implemented, connection is accepted only on the socket.");
		return;		
	}

	/* (non-Javadoc)
	 * @see multimonster.mediaproxy.MediaProxyPlugin#disconnect()
	 */
	public void disconnect() {

		log.warn("Not implemented, connection is controlled only on the socket.");
		return;		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		Socket socket = null;
		String errorText = "";

		try {
			ssocket.setSoTimeout(60 * 1000); // 60sec
		}  catch (SocketException e1) {
			log.error("problem setting timeout.");			
		}
		
		while (true) {

			// wait for connection, then create new socket:
			try {
				socket = ssocket.accept();

			} catch (SocketTimeoutException e){
				// log.debug("socket-timeout");				
				break;
			} catch (IOException e2) {
				errorText = "Couldn't accept connection: " + e2.getMessage();
				log.error(errorText);
				break;
			}

			(new MMThread(new RawSocketInputProxyHandler(socket, this, caddr))).start();
		}		
		
		if (ssocket != null) {
			try {
				ssocket.close();
			} catch (IOException e) {

			}
		}

		
	}


}
