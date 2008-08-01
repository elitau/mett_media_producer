package multimonster.transporter.plugin;

import org.apache.log4j.Logger;

import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;
import multimonster.transporter.TransporterPlugin;
import multimonster.transporter.exceptions.TransporterException;

/**
 * Takes data from the converter and passes it to the proxy.
 * No control-operations are implemented.
 * @author Jörg Meier
 */
public class SimpleTransporter extends TransporterPlugin {

	private Logger log;
	private Pipe pipeToMediaProxy;
	private Pipe pipeFromConverter;

	/**
	 * constructs a TestPlugIn
	 *
	 */
	public SimpleTransporter(){
		this.log = Logger.getLogger(this.getClass());
		log.debug("SimpleTransporter created.");
	}

	/* (non-Javadoc)
	 * @see multimonster.transporter.TransporterPlugin#serveRequest(multimonster.common.Pipe, multimonster.common.Pipe, multimonster.common.MOIdentifier, multimonster.common.Format)
	 */
	public void setPipes(Pipe pipeToMediaProxy, Pipe pipeFromConverter) throws TransporterException {
		
		String errorText = "";
		
		if (pipeToMediaProxy == null) {
			errorText = "pipeToMediaProxy is null - aborting.";
			log.error(errorText);
			throw new TransporterException(errorText);
		} else if (pipeFromConverter == null) {
			errorText = "pipeFromConverter is null - aborting.";
			log.error(errorText);
			throw new TransporterException(errorText);
		}
		this.pipeToMediaProxy = pipeToMediaProxy;
		this.pipeFromConverter = pipeFromConverter;		
		
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

		log.debug("Thread started running.");
		
		try {
			pipeToMediaProxy.waitForPipeSetup();

		} catch (PipeClosedException e3) {
			
			// pipe was closed, without ever been setuped
			log.warn("Pipe was not setup, closing pipe from Converter.");
			pipeFromConverter.close();
			
		}
		pipeFromConverter.setupFinished();
		
		log.debug("Starting work.");

		while (true) {
			
			// read data from converter
			try {
				buffer = pipeFromConverter.read(PIPE_SEGMENT_SIZE);
				read_bytes_counter += buffer.length;

			} catch (PipeClosedException e) {
				log.debug("pipeFromConverter closed, " + read_bytes_counter + " bytes read.");
				break;
			}
						
			// write data to proxy
			try {
				pipeToMediaProxy.write(buffer);
				write_bytes_counter += buffer.length;
				
			} catch (PipeClosedException e1) {
				log.debug("pipeToMediaProxy closed, wrote " +write_bytes_counter +" bytes.");
				break;
			}

		}
		
		log.debug("Transporting finished, read "+read_bytes_counter
				+" wrote "+write_bytes_counter +" bytes, cleaning up...");
		
		pipeToMediaProxy.close();
		pipeFromConverter.close();
		
		return;
	}
	
	/* (non-Javadoc)
	 * @see multimonster.transporter.TransporterPlugin#disconnect()
	 */
	public void disconnect() {
		log.warn("disconnect() not implemented - just close the pipes!");
		
	}


}
