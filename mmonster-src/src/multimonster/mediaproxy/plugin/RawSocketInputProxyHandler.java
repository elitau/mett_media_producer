package multimonster.mediaproxy.plugin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;

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
 * The helper class of the RawSocketInputProxy.
 */
public class RawSocketInputProxyHandler implements Runnable {

	private Logger log = Logger.getLogger(this.getClass());

	private Socket socket;

	private MediaProxyPlugin parent;

	ConnectionAddress ca = null;

	/**
	 *  
	 */
	public RawSocketInputProxyHandler(Socket socket, MediaProxyPlugin parent,
			ConnectionAddress ca) {
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
		DataOutputStream outStream = null;
		BufferedReader inBufferedReader = null;
		MediaProxyImpl mediaProxyFacade = null;
		Pipe pipeToTransporter = null;
		ResourceRequestIdentifier rrId = null;
		String errorText = "";

		log.debug("thread is running, waiting for connection...");

		try {
			in = socket.getInputStream();

		} catch (IOException e3) {
			errorText = "Couldn't get InputStream of socket: "
					+ e3.getMessage();
			log.error(errorText);
			cleanup(inBufferedReader, outStream, socket, pipeToTransporter,
					mediaProxyFacade, rrId, true);
			return;
		}

		try {
			out = socket.getOutputStream();
			outStream = new DataOutputStream(out);

		} catch (IOException e4) {
			errorText = "Couldn't OutputStream of socket: " + e4.getMessage();
			log.error(errorText);
			cleanup(inBufferedReader, outStream, socket, pipeToTransporter,
					mediaProxyFacade, rrId, true);
			return;
		}

		InetAddress remoteIp = socket.getInetAddress();
		if (remoteIp == null) {
			errorText = "No remote-IP, socket isn't connected.";
			log.error(errorText);
			cleanup(inBufferedReader, outStream, socket, pipeToTransporter,
					mediaProxyFacade, rrId, true);
			return;
		}

		doWork(in, outStream);

		cleanup(inBufferedReader, outStream, socket, pipeToTransporter,
				mediaProxyFacade, rrId, false);
	}

	/**
	 * Runs the Proxy.
	 * 
	 * @param file
	 * @param link
	 */
	public void doWork(InputStream inStream, DataOutputStream out) {

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
		int transferredBytes = 0;
		String errorText = "";

		log.debug("InputProxy starts work...");

		// now the values are check by the parser, I can use them:
		if (ca == null) {
			errorText = "Connection Adress is null, should be set at init, creating it from the parsed link...";
			log.warn(errorText);
		}

		session = ca.getSession();
		mOId = ca.getMOId();
		fId = ca.getFormatId();

		/*
		 * mapping to internal protocol between proxy and transporter this proxy
		 * needs a transporter which speaks "mmSimple"!
		 */
		protocolId = new ProtocolId(ProtocolId.pId_mmSimple);

		action = new Action(Action.A_USE);

		/* init */
		// trying to get MediaProxyEJB
		try {

			context = new InitialContext();
			mediaProxyHome = EjbHomeGetter.getMediaProxyHome(context);

			mediaProxyFacade = EjbCreator.createMediaProxy(mediaProxyHome,
					context);

		} catch (NamingException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		} catch (CreateException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		}

		try {
			/*
			 * call MediaProxyFacade to prepare for output and get Pipe from
			 * Transporter and ResourceRequestId
			 */
			pio = mediaProxyFacade.initWork(session, mOId, fId, protocolId,
					action, true);

			mediaProxyFacade.remove();

		} catch (RemoteException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		} catch (RemoveException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		} catch (MediaProxyException e) {
			errorText = e.getMessage();
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		}

		if (pio == null) {
			errorText = "Didn't get InitObjects to do my work.";
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		}
		if ((rrId = pio.getRrId()) == null) {
			errorText = "Didn't get ResourceRequestIdentifier.";
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		}
		if ((pipeToTransporter = pio.getPipeToTransporter()) == null) {
			errorText = "Didn't get pipeToTransporter.";
			log.error(errorText);
			cleanup(pipeToTransporter, mediaProxyFacade, rrId, true);
			return;
		}

		log
				.debug("Initialization finshed, got Pipe from Transporter and ResourceRequestIdentifier.");

		/* do data exchange */
		log.debug("starting work.");

		transferredBytes = transmitInputData(pipeToTransporter, inStream);

		try {
			out.writeUTF("successfully transferred " + transferredBytes
					+ " bytes.");

		} catch (IOException e1) {
			log.error("Couldn't write to client through socket.");
		}

		cleanup(pipeToTransporter, mediaProxyFacade, rrId, false);

		return;
	}

	private void cleanup(Pipe pipeToTransporter,
			MediaProxyImpl mediaProxyFacade, ResourceRequestIdentifier rrId,
			boolean errorOccured) {
		cleanup(null, null, null, pipeToTransporter, mediaProxyFacade, rrId,
				errorOccured);
	}

	/**
	 * Cleans up every established connection, either to the client or to the
	 * transporter.
	 * 
	 * @param in
	 * @param out
	 * @param socket
	 * @param pipeToTransporter
	 * @param mediaProxyFacade
	 */
	private void cleanup(BufferedReader in, DataOutputStream out,
			Socket socket, Pipe pipeToTransporter,
			MediaProxyImpl mediaProxyFacade, ResourceRequestIdentifier rrId,
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
					mediaProxyFacade.requestFinished(rrId);
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
	 * Does the actual data transmit from client to transporter.
	 * 
	 * @param pipeToTransporter
	 * @param in
	 * @param out
	 */
	private int transmitInputData(Pipe pipeToTransporter, InputStream inStream) {

		int bytesInCounter = 0;
		int bytesOutCounter = 0;

		//get the segment-size to read out of pipe
		int PIPE_SEGMENT_SIZE = Pipe.getPipeSegmentSize();

		log.debug("transmitInputData called, starting reading HTTP-Stream...");

		try {

			//transfer data

			byte[] data = new byte[PIPE_SEGMENT_SIZE];
			int read = 1;
			while ((read = inStream.read(data)) > 0) {
				bytesInCounter += read;
				pipeToTransporter.write(data);
				bytesOutCounter += data.length;
			}

				/*
				 * int nextBlockSize = Math.min(PIPE_SEGMENT_SIZE,
				 * inStream.available());
				 * 
				 * while (nextBlockSize > 0){ byte[] data = new
				 * byte[nextBlockSize];
				 * 
				 * bytesInCounter += inStream.read(data, 0, nextBlockSize);
				 * 
				 * pipeToTransporter.write(data); bytesOutCounter +=
				 * data.length;
				 * 
				 * nextBlockSize = Math.min(512, inStream.available());
				   
				 

			}
			*/

			//			byte[] bytesIn = new byte[PIPE_SEGMENT_SIZE];
			//			byte[] tmpBuff;
			//				
			//			while (!socket.isInputShutdown()) {
			//
			//				bytesInCounter = inStream.read(bytesIn);
			//				
			//				if (bytesInCounter == -1){
			//					//EOF is reached
			//					break;
			//				}
			//				
			//				if (bytesInCounter < bytesIn.length){
			//					
			//					tmpBuff = new byte[bytesInCounter];
			//					
			//					for (int i=0; i<bytesInCounter; i++){
			//						tmpBuff[i] = bytesIn[i];
			//					}
			//					bytesIn = tmpBuff;
			//					
			//				}
			//												
			//				// write data to transporter
			//				pipeToTransporter.write(bytesIn);
			//				
			//				// count written bytes
			//				bytesOutCounter += bytesIn.length;
			//				//log.debug("Wrote " +bytesOutCounter +" bytes to Transporter.");
			//			}

		} catch (PipeClosedException e6) {
			log.info("Pipe to Transporter was closed, wrote " + bytesOutCounter
					+ " bytes.");
		} catch (IOException e6) {
			log.info("File was closed, wrote " + bytesOutCounter + " bytes.");
		}

		log.debug("Transmit finished, " + bytesOutCounter
				+ " wrote to Transporter.");
		return bytesOutCounter;
	}
}