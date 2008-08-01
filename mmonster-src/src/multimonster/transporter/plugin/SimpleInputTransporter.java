/*
 * Created on 19.02.2004
 *
 */
package multimonster.transporter.plugin;

import org.apache.log4j.Logger;

import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;
import multimonster.transporter.TransporterPlugin;
import multimonster.transporter.exceptions.TransporterException;

/**
 * Takes data on the Pipe and passes the data to the converter.
 * No control actions are implemented.
 * 
 * @author Jörg Meier
 *
 */
public class SimpleInputTransporter extends TransporterPlugin {

	private Logger log;
	private Pipe pipeFromMediaProxy;
	private Pipe pipeToConverter;

	/**
	 * constructs a TestPlugIn
	 *
	 */
	public SimpleInputTransporter(){
		this.log = Logger.getLogger(this.getClass());
		log.debug("SimpleInputTransporter created.");
	}

	/* (non-Javadoc)
	 * @see multimonster.transporter.TransporterPlugin#serveRequest(multimonster.common.Pipe, multimonster.common.Pipe, multimonster.common.MOIdentifier, multimonster.common.Format)
	 */
	public void setPipes(Pipe pipeFromMediaProxy, Pipe pipeToConverter) throws TransporterException {
		
		String errorText = "";
		
		if (pipeFromMediaProxy == null) {
			errorText = "pipeFromMediaProxy is null - aborting.";
			log.error(errorText);
			throw new TransporterException(errorText);
		} else if (pipeToConverter == null) {
			errorText = "pipeToConverter is null - aborting.";
			log.error(errorText);
			throw new TransporterException(errorText);
		}
		this.pipeFromMediaProxy = pipeFromMediaProxy;
		this.pipeToConverter = pipeToConverter;		
		
		log.debug("Got Pipes for working.");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		int read_bytes_counter = 0;
		int write_bytes_counter = 0;
		byte[] buffer = null;
		//get the segment-size to read out of pipe
		int PIPE_SEGMENT_SIZE = Pipe.getPipeSegmentSize();

		log.debug("Starting work.");

		while (true) {
			
			// read data from proxy
			try {
				buffer = pipeFromMediaProxy.read(PIPE_SEGMENT_SIZE);
				read_bytes_counter += buffer.length;
				
			} catch (PipeClosedException e1) {
				log.debug("pipeFromMediaProxy closed, read " +write_bytes_counter +" bytes.");
				break;
			}

			// write data to converter
			try {
				pipeToConverter.write(buffer);
				write_bytes_counter += buffer.length;

			} catch (PipeClosedException e) {
				log.debug("pipeToConverter closed, wrote " + read_bytes_counter + " bytes.");
				break;
			}
		}
		
		log.debug("Transporting finished, wrote " +write_bytes_counter +" bytes, cleaning up...");
		
		pipeFromMediaProxy.close();
		pipeToConverter.close();
		
		return;
	}
	
	/* (non-Javadoc)
	 * @see multimonster.transporter.TransporterPlugin#disconnect()
	 */
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}


}
