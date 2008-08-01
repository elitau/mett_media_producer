package multimonster.converter.plugin.jmx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;

/**
 * Used to call the native process tcprobe which is used to extract metadata.
 * It is used by the <code>MetaDataExtractor</code>.
 * 
 * @author Holger Velke
 * 
 * @jmx.mbean name="multimonster:service=TCProbeCaller"
 *    extends="org.jboss.system.ServiceMBean"
 * @jboss.service servicefile="jboss"
 */
public class TCProbeCaller extends ServiceMBeanSupport implements TCProbeCallerMBean{
	
	static private Logger log = Logger.getLogger(TCProbeCaller.class);
	
	// TODO externalize in setting
	private String TCPROBE_PATH = "tcprobe";
	private String TCPROBE_ARGS = "-B";
	
	//TODO use other container for storing the processes
	private Vector processes = null;
	
	/**
	 * the jmx object name of the MBean
	 */
	static final public String JMX_NAME = "multimonster:service=TCProbeCaller";

	/**
	 * @return Returns the tCPROBE_ARGS.
	 * 
	 * @jmx.managed-attribute 
	 */
	public String getTCPROBE_ARGS() {
		return TCPROBE_ARGS;
	}

	/**
	 * @param tcprobe_args The tCPROBE_ARGS to set.
	 * 
	 * @jmx.managed-attribute 
	 */
	public void setTCPROBE_ARGS(String tcprobe_args) {
		TCPROBE_ARGS = tcprobe_args;
	}

	/**
	 * @return Returns the tCPROBE_PATH.
	 * 
	 * @jmx.managed-attribute 
	 */
	public String getTCPROBE_PATH() {
		return TCPROBE_PATH;
	}

	/**
	 * @param tcprobe_path The tCPROBE_PATH to set.
	 * 
	 * @jmx.managed-attribute 
	 */
	public void setTCPROBE_PATH(String tcprobe_path) {
		TCPROBE_PATH = tcprobe_path;
	}

	/**
	 * this can only be used within the same VM.
	 * 
	 * @jmx.managed-operation 
	 */
	public TCProbeCaller returnThis() {
		
		log.debug("returnThis()");
		
		return this;
	}
	
	//------------------------------------------------------------------------
	
	/**
	 * 
	 * @return the <code>id</code> of the process started. The <code>id</code> is needed for the following methods.  
	 */
	public int startTCProbeProcess(){
		
		Process tcprobe = null;
		int id = 0;
		
		if (processes == null){
			processes = new Vector();
		}
	
		try {
			tcprobe = Runtime.getRuntime().exec(TCPROBE_PATH+" "+TCPROBE_ARGS);
		} catch (IOException e){
			log.error("problem starting tcprobe process - "+ e.getMessage());
			return -1;
		}
		
		processes.add(tcprobe);		
		id = processes.indexOf(tcprobe);
		
		return id;
	}
	
	/**
	 * sends data to the specified transcode process.
	 * 
	 * 
	 * @param id
	 * 		the id of the process the data should be sent to. 
	 * @param data
	 * 		the data to send to the process 
	 * @return
	 * 		<code>true</code> if more data is needed by transcode. <p>
	 * 		<code>false</code> if transcode has finished extraction and needs no more data.
	 * @throws IOException if there is an unsolvable proble while writing data to tcprobe
	 */
	public boolean sendDataToTCProbe(int id, byte[] data) throws IOException{
		Process tcprobe = null;
		
		tcprobe = getTCProbe(id);
		
		try {
			tcprobe.getOutputStream().write(data);
		} catch (IOException e) {
			
			try {
				tcprobe.waitFor();
			} catch (InterruptedException e1) {
				log.error("error while waiting for tcprobe to finish", e);
				return false;
			}
			
			if (tcprobe.exitValue() == 0){
				return false;
			} else {
				log.error("tcprobe finished with exit value "+tcprobe.exitValue());
				return false;
			}
		}		
		return true;
	}
	
	public void finishTCProbeProcess(int id){
		Process tcprobe = null;
		
		tcprobe = getTCProbe(id);
		
		try {
			tcprobe.getOutputStream().flush();
			tcprobe.getOutputStream().close();			
		} catch (IOException e) {
			log.error("problem closing tcprobe's stdin", e);
		}
		
		try {
			tcprobe.waitFor();
		} catch (InterruptedException e) {
			log.error("problem waitng for tcprobe: "+e.getMessage());
			tcprobe.destroy();
		}
		
		if (tcprobe.exitValue() != 0){
			log.warn("tcprobe exited with errorcode "+tcprobe.exitValue());
		}					
	}
	
	/**
	 * get data written to the stdout by the tcprobe process
	 * 
	 * @param id the id of the process
	 * @return the data written to stdout
	 * @throws IOException if an unsolvable problem occours while reading the data  
	 */
	public byte[] getTCProbeStdout(int id) throws IOException{
		byte[] result = null;
		Process tcprobe = null;
		InputStream tcprobeStdout = null;
		
		tcprobe = getTCProbe(id);
		tcprobeStdout = tcprobe.getInputStream();
		
		result = new byte[tcprobeStdout.available()];
		tcprobeStdout.read(result);
		
		return result;
	}


	/**
	 * get data written to the stderr by the tcprobe process
	 * 
	 * @param id the id of the process
	 * @return the data written to stdout
	 * @throws IOException if an unsolvable problem occours while reading the data  
	 */
	public byte[] getTCProbeStderr(int id) throws IOException{
		byte[] result = null;
		Process tcprobe = null;
		InputStream tcprobeStderr = null;
		
		tcprobe = getTCProbe(id);
		tcprobeStderr = tcprobe.getErrorStream();
		
		result = new byte[tcprobeStderr.available()];
		tcprobeStderr.read(result);
		
		return result;
	}
	
	
	/**
	 * removes the process form the internal list.
	 * destroys the process.
	 * 
	 * @param id the id of the tcprobe process
	 */
	public void removeTCProbe(int id){
		Process transcode = null;
		
		transcode = (Process) processes.remove(id);
		transcode.destroy();
	}
	
	/**
	 * gets the process from the internal list
	 * 
	 * @param id the id of the process
	 * @return the tcprobe process
	 */
	private Process getTCProbe(int id){
		
		Process tcprobe = null;		
		try {
			tcprobe = (Process) processes.get(id);
		} catch (ArrayIndexOutOfBoundsException e){
			throw new IllegalArgumentException("unknown id = "+id);
		}
		if (tcprobe == null){
			throw new IllegalArgumentException("unknown id = "+id);
		}		
		return tcprobe;
	}
}
