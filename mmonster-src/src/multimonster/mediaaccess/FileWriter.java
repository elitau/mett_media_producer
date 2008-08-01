package multimonster.mediaaccess;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;

import org.apache.log4j.Logger;

/**
 * @author Holger Velke
 */
class FileWriter implements Runnable {

	private Logger log;
	private Pipe input;
	private FileOutputStream out;
	private int readBufSize = Pipe.getPipeSegmentSize();

	/**
	 * writes data receiving from inputPipe to a file with the specified filename  
	 */
	public FileWriter(Pipe inputPipe, String fileName)
		throws FileNotFoundException {
		super();
		this.log = Logger.getLogger(this.getClass());
		this.out = new FileOutputStream(fileName);
		this.input = inputPipe;

		log.debug("CREATED");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		int count = 0;
		byte[] buf = null;
		
		log.debug("starting");

		input.setupFinished();
		
		try {
			while (true) {
				buf = input.read(readBufSize);
				out.write(buf);
				count += buf.length;
			}
		} catch (PipeClosedException e) {
			log.debug("input pipe closed");
		} catch (IOException e) {
			log.error(e);
		} finally {
			input.close();
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				// DO nothing 
			}
		}
		log.debug("wrote: " + count + " bytes");
	}
}
