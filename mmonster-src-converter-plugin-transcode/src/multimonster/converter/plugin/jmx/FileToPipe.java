package multimonster.converter.plugin.jmx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

import multimonster.common.pipe.Pipe;
import multimonster.common.pipe.PipeClosedException;

/**
 * @author Holger Velke
 */
public class FileToPipe implements Runnable {

	private static Logger log = Logger.getLogger(FileToPipe.class);

	private static int RETRY_MAX = 20;
	private static long RETRY_DELAY = 250; //ms
	private int MAX_BUF_SIZE = Pipe.getPipeSegmentSize(); //8 kB

	private String inputFileName = null;
	private Pipe output = null;
	private Process transcode = null;
	private boolean transcodeKilled = false;

	private boolean inputFileGrowing = true;
	private int count = 0;

	private static final long WAIT_TIME = 40;

	/**
	 * @param from_tc_fileName
	 * @param output
	 */
	public FileToPipe(String inputFileName, Pipe output, Process transcode) {
		if (inputFileName == null)
			throw new IllegalArgumentException("inputFileName is 'null'");
		this.inputFileName = inputFileName;
		if (output == null)
			throw new IllegalArgumentException("output Pipe is 'null'");
		this.output = output;
		if (transcode == null)
			throw new IllegalArgumentException("reference to transcode is 'null'");
		this.transcode = transcode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		FileInputStream input = null;

		input = getTranscodeOutput();

		if (input == null) {
			output.close();
			log.error("unable to find transcodes output file: "+inputFileName);
			return;
		}

		
		try {
			try {

				byte[] buf = new byte[MAX_BUF_SIZE];
				int readSize = 0;

				while (true) {

					readSize = getNextReadSize(input);
					
					if (readSize != 0) {
						int read = 0;
						read = input.read(buf,0,readSize);
						if (-1 == read)
							break;
						output.write(buf, read);
						count += buf.length;
					} else if (output.isClosed()){
						killTranscode();
						break;
					}
				}
				input.close();
				output.close();
			} catch (PipeClosedException e) {
				// output-pipe closed, no need to do more conversion-work
				input.close();
				killTranscode();
			}
		} catch (IOException e) {
			log.error(e);
		}
		log.debug("finished " + count + " bytes");
	}

	/**
	 * @param inputFileGrowing The inputFileGrowing to set.
	 */
	public void setInputFileGrowing(boolean inputFileGrowing) {
		this.inputFileGrowing = inputFileGrowing;
	}

	/**
	 * @return Returns the transcodeKilled.
	 */
	public boolean isTranscodeKilled() {
		return transcodeKilled;
	}
	
	private FileInputStream getTranscodeOutput(){
		
		FileInputStream input = null;
		
		for (int i = 0; i < RETRY_MAX; i++) {
			try {
				input = new FileInputStream(inputFileName);
			} catch (FileNotFoundException e) {
				input = null;
			}
			if (input != null)
				break;
			
			synchronized (this) {
				try {
					wait(RETRY_DELAY);
				} catch (InterruptedException e) {
				}
			}
		}
		
		return input;
	}

	private int getNextReadSize(FileInputStream input) throws IOException{
		int avail = 0;
		
		if (inputFileGrowing) {
			avail = input.available();
			if (avail == 0) {
				synchronized (this) {
					try {
						wait(WAIT_TIME);
					} catch (InterruptedException e) {
					}
				}
			} else if (avail < MAX_BUF_SIZE) {
				//buf = new byte[avail];
				return avail;
			} else {
				//buf = new byte[MAX_BUF_SIZE];
				return MAX_BUF_SIZE;
			}
		} else {
			//buf = new byte[MAX_BUF_SIZE];
			return MAX_BUF_SIZE;
		}

		return 0;
	}

	private void killTranscode(){
		if (transcode != null){
			transcodeKilled = true;
			transcode.destroy();
		}
		log.info("Output-Pipe closed, killing transcode-process");
	}
}
