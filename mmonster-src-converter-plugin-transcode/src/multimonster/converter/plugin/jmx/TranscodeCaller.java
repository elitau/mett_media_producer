package multimonster.converter.plugin.jmx;

import java.io.File;
import java.io.IOException;

import multimonster.common.pipe.Pipe;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;

/**
 * @author Holger Velke
 * 
 * @jmx.mbean name="multimonster:service=TranscodeCaller"
 *    extends="org.jboss.system.ServiceMBean"
 * @jboss.service servicefile="jboss"
 */
public class TranscodeCaller
	extends ServiceMBeanSupport
	implements TranscodeCallerMBean {

	static private Logger log = Logger.getLogger(TranscodeCaller.class);

	/**
	 * the jmx object name of the MBean
	 */
	static final public String JMX_NAME =
		"multimonster:service=TranscodeCaller";
	
	private String TRANSCODE_PATH = "transcode";

    /**
     * @label uses
     * @directed 
     */
    private FileToPipe lnkFileToPipe;

    /**
     * @directed
     * @label uses*/
    private PipeToFile lnkPipeToFile;
	
	/**
	 * @return Returns the TRANSCODE_PATH.
	 * 
	 * @jmx.managed-attribute 
	 */
	public String getTRANSCODE_PATH() {
		return TRANSCODE_PATH;
	}
	/**
	 * @param transcode_path The TRANSCODE_PATH to set.
	 * 
	 * @jmx.managed-attribute 
	 */
	public void setTRANSCODE_PATH(String transcode_path) {
		TRANSCODE_PATH = transcode_path;
	}
	/**
	 * this can only be used within the same VM.
	 * 
	 * @jmx.managed-operation 
	 */
	public TranscodeCaller returnThis() {
		
		log.debug("returnThis()");

		return this;
	}

	public void doWork(Pipe input, Pipe output, String transcodeParameters, String to_tc_fileName, String from_tc_fileName) {
		
		Process transcode = null;
		FileToPipe in = null;
		Thread tIn = null;
		PipeToFile out = null;
		Thread tOut = null;

		String transcodeCommand = "";
		StreamToString transcodeStderr = null;
		StreamToString transcodeStdout = null;	
		// build transcodeCommand
		
		transcodeCommand = TRANSCODE_PATH + " " + transcodeParameters;
		
		log.debug("XXX: transcodeCommand: " + transcodeCommand);
		
		try {
			out = new PipeToFile(input, to_tc_fileName);
		} catch (IOException e) {
			log.error(
				"unable to create tmp-file for transcode communication - closing pipes, I give up");
			input.close();
			output.close();
			return;
		}
		

		// start real work
		try {
			// start writng transcodes input file
			tOut = new Thread(out);
			tOut.start();
		
			Thread.sleep(1000); //sleep 1 secon before starting transcode
			
			// start transcode
			transcode = Runtime.getRuntime().exec(transcodeCommand);
			log.debug("started transcode: "+transcode);
			
			// read transcodes out err Streams
			transcodeStderr = new StreamToString(transcode.getErrorStream());
			transcodeStdout = new StreamToString(transcode.getInputStream());
			(new Thread(transcodeStderr)).start();
			(new Thread(transcodeStdout)).start();
			
			// read transcodes output file
			in = new FileToPipe(from_tc_fileName, output, transcode);
			tIn = new Thread(in);
			tIn.start();

			log.debug("waiting for transcode");
			transcode.waitFor();
			log.debug("transcode is finished");
			// transcode in finished
			in.setInputFileGrowing(false);
			if (!out.isFinished()&&!in.isTranscodeKilled()) {
				log.error(
					"transcode exited before all data was written to communication file"+
					" exitvalue is:"+transcode.exitValue());
			}
			if ((transcode.exitValue()!= 0)&&!in.isTranscodeKilled()) {
				log.error(
					"transcode exited with exitvalue " + transcode.exitValue());
				log.error("stdout: " + transcodeStdout.getString());
				log.error("stderr: " + transcodeStderr.getString());
			}
			// wait until all data is written to the output pipe
			tIn.join();

		} catch (IOException e) {
			log.error(e);
		} catch (InterruptedException e) {
			log.error(e);
		} catch (IllegalThreadStateException e){
			log.error(e);
		} finally{
			
			try {
				log.debug("stoped transcode: "+transcode);
				transcode.destroy();
			} catch (Exception e) {
				log.error(e);
			}
			
			input.close();
			output.close();
			
			(new File(to_tc_fileName)).delete();
			(new File(from_tc_fileName)).delete();
			
			log.debug("finished");			
		}
	}
}
