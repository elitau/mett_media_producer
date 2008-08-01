package multimonster.edit.plugin;

import org.apache.log4j.Logger;

import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;
import multimonster.edit.FilterPlugIn;

/**
 * @author Holger Velke
 */
public class MMThreadPlugIn extends FilterPlugIn {

	private static Logger log = Logger.getLogger(MMThreadPlugIn.class);
	
	private Pipe input = null;
	private Pipe output = null;
		
	public MMThreadPlugIn(){
		log.debug("CREATED");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {		
		byte[] buf = null;
		int readBufSize = Pipe.getPipeSegmentSize();
		int count = 0;

		log.debug("run()");
		
		// check preconditions
		if ((input == null)||(output == null)){
			log.error("Pipes missing - unable to do work");
		}

		while (true) {

			try {
				buf = input.read(readBufSize);
			} catch (PipeClosedException e) {
				log.debug("input pipe closed");
				break;
			}

			// real Work has to be done here

			try {
				output.write(buf);
			} catch (PipeClosedException e) {
				log.debug("output pipe closed");
				break;
			}
			count += buf.length;

		}

		log.debug(
				"Editing finished, wrote " + count + " bytes, cleaning up");
		input.close();
		output.close();		
		
		this.setFinished();
	}

	/**
	 * @param input The input to set.
	 */
	public void setInput(Pipe input) {
		this.input = input;
	}

	/**
	 * @param output The output to set.
	 */
	public void setOutput(Pipe output) {
		this.output = output;
	}

}
