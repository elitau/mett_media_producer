package multimonster.converter.plugin.jmx;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;

/**
 * @author Holger Velke
 */
public class PipeToFile implements Runnable {

	private static Logger log = Logger.getLogger(PipeToFile.class);

	private static int BUF_SIZE = Pipe.getPipeSegmentSize();

	private Pipe input = null;
	private FileOutputStream output = null;
	
	private boolean finished = false;

	public PipeToFile(Pipe input, String outputFileName) throws FileNotFoundException {
		this.input = input;
		this.output = new FileOutputStream(outputFileName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		byte[] buf = null;
		int count = 0;

		try {
			try {
				while (true) {

					buf = input.read(BUF_SIZE);
					output.write(buf);
					
					count += buf.length;

				}
			} catch (PipeClosedException e) {
				output.close();
			}
		} catch (IOException e) {
			log.error(e);
		}
		
		finished = true;
		log.debug("finished "+count+" bytes");
	}

	/**
	 * @return Returns the finished.
	 */
	public boolean isFinished() {
		return finished;
	}

}
