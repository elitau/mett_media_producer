/*
 * Created on 22.04.2004
 * 
*/
package multimonster.mediaproxy.plugin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import multimonster.common.Action;
import multimonster.common.ConnectionAddress;
import multimonster.common.FormatId;
import multimonster.common.ProtocolId;
import multimonster.common.Session;
import multimonster.common.media.MOIdentifier;
import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.common.util.EjbCreator;
import multimonster.common.util.EjbHomeGetter;
import multimonster.mediaproxy.MediaProxyPlugin;
import multimonster.mediaproxy.ProxyInitObjects;
import multimonster.mediaproxy.exceptions.MediaProxyException;
import multimonster.mediaproxy.interfaces.MediaProxyImpl;
import multimonster.mediaproxy.interfaces.MediaProxyImplHome;

import org.apache.log4j.Logger;

/**
 * The helper class for the HttpProxy
 */
public class HttpProxyHandler implements Runnable{

	private Logger log = Logger.getLogger(this.getClass());

	private Socket socket;
	private MediaProxyPlugin parent;
	private ConnectionAddress ca = null;

	/**
	 *  
	 */
	public HttpProxyHandler(Socket socket, MediaProxyPlugin parent, ConnectionAddress ca) {
		super();
		this.socket = socket;
		this.parent = parent;
		this.ca = ca;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		InputStream in = null;
		OutputStream out = null;
		InputStreamReader inStream = null;
		DataOutputStream outStream = null;
		BufferedReader inBufferedReader = null;
		Context context = null;
		MediaProxyImplHome mediaProxyHome = null;
		MediaProxyImpl mediaProxyFacade = null;
		ProxyInitObjects pio = null;
		Pipe pipeToTransporter = null;
		ResourceRequestIdentifier rrId = null;
		Session session = null;
		MOIdentifier mOId = null;
		FormatId fId = null;
		ProtocolId protocolId = null;
		Action action = null;
		String errorText = "";

		log.debug("thread is running, waiting for connection...");

		try {
			in = socket.getInputStream();
			inStream = new InputStreamReader(in);
			inBufferedReader = new BufferedReader(inStream);

		} catch (IOException e3) {
			errorText =
				"Couldn't get InputStream of socket: " + e3.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}

		try {
			out = socket.getOutputStream();
			outStream = new DataOutputStream(out);

		} catch (IOException e4) {
			errorText = "Couldn't OutputStream of socket: " + e4.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}

		InetAddress remoteIp = socket.getInetAddress();
		if (remoteIp == null) {
			errorText = "No remote-IP, socket isn't connected.";
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}

		// log.debug("Remote IP=" + remoteIp.getHostAddress());

		/* parse request to check session, mOId, format */
		try {
			parseClientRequest(inBufferedReader);

		} catch (MediaProxyException e5) {
			errorText = e5.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}
		// now the values are check by the parser, I can use them:
		session = ca.getSession();
		mOId = ca.getMOId();
		fId = ca.getFormatId();
		
		/* mapping to internal protocol between proxy and transporter
		 * this proxy needs a transporter which speaks "mmSimple"!
		 */
		protocolId = new ProtocolId(ProtocolId.pId_mmSimple);
		
		action = new Action(Action.A_USE);

		/* init */
		// trying to get MediaProxyEJB
		try {

			context = new InitialContext();
			mediaProxyHome = EjbHomeGetter.getMediaProxyHome(context);

			mediaProxyFacade =
				EjbCreator.createMediaProxy(mediaProxyHome, context);

		} catch (NamingException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		} catch (CreateException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}

		try {
			/*
			 * call MediaProxyFacade to prepare for output and get Pipe from
			 * Transporter and ResourceRequestId
			 */
			pio =
				mediaProxyFacade.initWork(
					session,
					mOId,
					fId,
					protocolId,
					action,
					false);

			mediaProxyFacade.remove();

		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		} catch (MediaProxyException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}

		if (pio == null) {
			errorText = "Didn't get InitObjects to do my work.";
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}
		if ((rrId = pio.getRrId()) == null) {
			errorText = "Didn't get ResourceRequestIdentifier.";
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}
		log.debug("rrID is ok: " +rrId);
		
		if ((pipeToTransporter = pio.getPipeToTransporter()) == null) {
			errorText = "Didn't get pipeToTransporter.";
			log.error(errorText);
			cleanup(
				inBufferedReader,
				outStream,
				socket,
				pipeToTransporter,
				mediaProxyFacade,
				rrId,
				true);
			return;
		}

		/* do data exchange */

		transmitOutputData(pipeToTransporter, inBufferedReader, outStream);
		

		cleanup(
			inBufferedReader,
			outStream,
			socket,
			pipeToTransporter,
			mediaProxyFacade,
			rrId,
			false);
	}
	
	/**
	 * Cleansup every established connection, either to the client or to the
	 * transporter.
	 * 
	 * @param in
	 * @param out
	 * @param socket
	 * @param pipeToTransporter
	 * @param mediaProxyFacade
	 */
	private void cleanup(
			BufferedReader in,
			DataOutputStream out,
			Socket socket,
			Pipe pipeToTransporter,
			MediaProxyImpl mediaProxyFacade,
			ResourceRequestIdentifier rrId,
			boolean errorOccured) {
		String http_error_msg = "HTTP/1.1 400 Bad Request\r\n\r\nERROR";
		String errorText = "";

		log.debug("finished, closing socket, pipe and cleaning up.");

		try {
			if (out != null) {
				if (errorOccured) {
					// try to inform client, about the error
					out.writeBytes(http_error_msg);
				}
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e1) {
			errorText = "Error on cleaning up socket: " + e1.getMessage();
			log.error(errorText);
		} finally {
			if (pipeToTransporter != null) {
				pipeToTransporter.close();
			}

			try {
				/* call MediaProxyFacade to indicate the finished request */
				if (mediaProxyFacade != null) {
					if (rrId != null){
						mediaProxyFacade.requestFinished(rrId);
					}
					mediaProxyFacade.remove();
				}

			} catch (RemoteException e) {
				errorText = "Error calling remote object: " + e.getMessage();
				log.error(errorText);
				return;
			} catch (RemoveException e) {
				errorText = "Couldn't remove MediaProxy: " + e.getMessage();
				log.error(errorText);
				return;
			} catch (MediaProxyException e) {
				errorText = e.getMessage();
				log.error(errorText);
				return;
			}

		}
	}

	/**
	 * Parses the client request and checks if it's equal to the expected
	 * request as it is stored in the ConnectionAddress.
	 * 
	 * @param in
	 * @param mOId
	 * @param format
	 * @param protocol
	 * @param session
	 * @param action
	 */
	private void parseClientRequest(BufferedReader in)
	throws MediaProxyException {

		// get the request string:
		//				
		String request = "";
		String parsed_sessionId = "";
		int parsed_mOId = 0;
		String parsed_formatId = "";
		String errorText = "";

		try {
			// now it's a buffered reader:
			request = in.readLine();

		} catch (IOException e5) {
			log.info("Could not read anymore from client.");
		}
		if (request == null) {
			errorText = "Client-request was null";
			throw new MediaProxyException(errorText);
		}
		log.debug("read request: " +request);
		
		StringTokenizer to_parse = new StringTokenizer(request, "/");

		try {
			to_parse.nextToken(); // "GET "
			parsed_sessionId = to_parse.nextToken();
			// sessionID
			parsed_mOId = Integer.parseInt(to_parse.nextToken()); // mOId
			parsed_formatId = to_parse.nextToken(); // format
			to_parse.nextToken(); // filename

		} catch (Exception e) {
			errorText = "Couldn't parse request string: " + e.getMessage();
			throw new MediaProxyException(errorText);
		}
		/* check if parsed request is ok */
		if (parent
				.getConnectionAddress()
				.getSession()
				.getId()
				.compareTo(parsed_sessionId)
				!= 0) {
			errorText =
				"Expected different sessionId ("
				+ parent.getConnectionAddress().getSession().getId()
				+ "), but got "
				+ parsed_sessionId
				+ ".";
			throw new MediaProxyException(errorText);
		} else if (
				parent.getConnectionAddress().getMOId().getMoNumber()
				!= parsed_mOId) {
			errorText =
				"Expected different mOId ("
				+ parent.getConnectionAddress().getMOId().getMoNumber()
				+ "), but got "
				+ parsed_mOId
				+ ".";
			throw new MediaProxyException(errorText);
		} else if (
				parent
				.getConnectionAddress()
				.getFormatId()
				.getId()
				.compareTo(
						parsed_formatId)
				!= 0) {
			errorText =
				"Expected different formatNumber ("
				+ parent
				.getConnectionAddress()
				.getFormatId()
				.getId()
				+ "), but got "
				+ parsed_formatId
				+ ".";
			throw new MediaProxyException(errorText);
		}

		log.debug(
				"Request parsed, is ok, as expected: sessionID="
				+ parsed_sessionId
				+ ", moid="
				+ parsed_mOId
				+ ", format="
				+ parsed_formatId);

		return;
	}

	/**
	 * Does the actual data transmit from transporter to client.
	 * 
	 * @param pipeToTransporter
	 * @param in
	 * @param out
	 */
	private void transmitOutputData(
			Pipe pipeToTransporter,
			BufferedReader in,
			DataOutputStream out) {

		String CRLF = "\r\n";

		
		byte[] bytesOut;
		int bytesOutCounter = 0;
		boolean proxying = true;
		String clientRequest = "";

		//get the segment-size to read out of pipe
		int PIPE_SEGMENT_SIZE = Pipe.getPipeSegmentSize();

		try {

			String httpHeader = "";
			String httpHeader_statusLine = "";
			String httpHeader_contentType = "";
			
			
			httpHeader_statusLine = "HTTP/1.1 200 OK";
			//httpHeader_contentType = "Content-Type: application/octet-stream";
			httpHeader_contentType = "Content-Type: video/mpeg";
			
			httpHeader =  httpHeader_statusLine +CRLF
						 +httpHeader_contentType +CRLF
						 +CRLF; 			
			
			// writing HTTP header to client
			out.write(httpHeader.getBytes());
				
			
			bytesOutCounter += httpHeader.length();
			log.debug("HTTP header: " +httpHeader.length() +" bytes.");


			while (proxying) {

				// read data from transporter
				bytesOut = pipeToTransporter.read(PIPE_SEGMENT_SIZE);

				// write it to the client
				out.write(bytesOut);

				// count written bytes
				bytesOutCounter += bytesOut.length;

				if (bytesOut.length < PIPE_SEGMENT_SIZE) {

					// this was the rest of data in the pipe, now we're
					// finished
					proxying = false;
				}

				// get controlling info from client
				if (in.ready()) {
					clientRequest = in.readLine();
//					log.debug(
//							"Got controlling-client-request: " + clientRequest);

					if (clientRequest.endsWith("stop")) {
						/* abort transmission */
						log.info("client wants to abort transmission.");
						proxying = false;
					} else {

					}
				}

			}

		} catch (PipeClosedException e6) {
			log.info(
					"Pipe from Transporter was closed, wrote "
					+ bytesOutCounter
					+ " bytes.");
		} catch (IOException e) {
			log.info(
					"Couldn't write to client anymore, wrote "
					+ bytesOutCounter
					+ " bytes.");
		}
		log.debug(
				"Transmit finished, " + bytesOutCounter + " wrote to client.");
		return;
	}
	
}
