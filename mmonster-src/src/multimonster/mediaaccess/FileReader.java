package multimonster.mediaaccess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;

import org.apache.log4j.Logger;

/**
 * @author Holger Velke (sihovelk)
 */
class FileReader implements Runnable {

	private Pipe pipe;
	private Logger log;
	private FileInputStream in;
	private int readBufSize = 128 * 1024;

	/**
	 * Reads data from the file with specified filename and writes it into the given Pipe-Object
	 * 
	 * @param pipe -
	 *            the Pipe to write the data in
	 * @param fileName -
	 *            the absolute Filepath of the file
	 */
	public FileReader(Pipe pipe, String fileName) throws FileNotFoundException {

		this.log = Logger.getLogger(this.getClass());
		this.in = null;
		this.pipe = pipe;

		in = new FileInputStream(fileName);

		log.debug("CREATED");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		byte[] buf = new byte[readBufSize];
		int n = 0;
		int count = 0;

		log.debug("strating");
		
		try {
			pipe.waitForPipeSetup();
		} catch (PipeClosedException e){
			log.warn("pipe was not setup - unable to do work");
			try {
				in.close();
			} catch (IOException ie) {
			}
			return;
		}

		while (true) {

			try {
				n = in.read(buf);
			} catch (IOException e) {
				log.error(e);
				break;
			}

			if (n == -1){
				//EOF
				break;
			}
			
			if (n < readBufSize){
				byte[] temp = new byte[n];
				for (int i = 0 ; i<temp.length; i++)
					temp[i] = buf[i];
				
				buf = temp;
				try {
					pipe.write(buf);
				} catch (PipeClosedException pe) {
					log.debug("pipe closed");
					break;
				}
				count += n;
				break;
			}
			
			try {
				pipe.write(buf);
			} catch (PipeClosedException pe) {
				log.debug("pipe closed");
				break;
			}

			count += n;

		}
		
		try {
			in.close();
		} catch (IOException e) {
			log.error(e);
		}
		
		pipe.close();
		log.debug("finished reading. read " + count + " bytes.");
	}
}
