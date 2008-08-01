package multimonster.converter.plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import multimonster.common.Codec;
import multimonster.common.Format;
import multimonster.common.Structure;
import multimonster.common.media.Duration;
import multimonster.common.media.MetaData;
import multimonster.converter.MetaDataExtractor;
import multimonster.converter.plugin.jmx.TCProbeCaller;

import org.apache.log4j.Logger;

/**
 * @author Holger Velke
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TCProbeExtractor extends MetaDataExtractor {

	static private Logger log = Logger.getLogger(TCProbeExtractor.class);

	// recomened for parse Binary
	static private int TC_PROBE_INFO_SIZE = 4 + TcProbeInfo.STRUCT_SIZE;

	static private TCProbeCaller caller = null;
	static private MBeanServer mBeanServer = null;

	private int tcProbeId = 0;
	private TcProbeInfo info = null;

	//state attribute
	private boolean parsed = false;
	private int counter = 0;

	public TCProbeExtractor() {

		// initialize
		if (caller == null) {
			caller = getTCProbeCaller();
		}
		this.tcProbeId = caller.startTCProbeProcess();

		// check if everything is ok		
		if (tcProbeId < 0) {
			// got no tcprobe process
			this.setReady(false);
		} else {
			this.setReady(true);
		}
	}

	static private MBeanServer getMBeanServer() {

		if (mBeanServer == null) {
			ArrayList mbeanServers = MBeanServerFactory.findMBeanServer(null);
			mBeanServer = (MBeanServer) mbeanServers.get(0);
		}

		return mBeanServer;
	}

	static private TCProbeCaller getTCProbeCaller() {
		if (mBeanServer == null)
			getMBeanServer();
		if (caller == null) {
			try {
				// for retrieval:
				ObjectName objName = new ObjectName(TCProbeCaller.JMX_NAME);
				// get the Object-reference (usable only if EJB in the same
				// JVM)
				caller =
					(TCProbeCaller) mBeanServer.invoke(
						objName,
						"returnThis",
						null,
						null);
			} catch (Exception e) {
				log.error("problem getting TCProbeCaller MBean", e);
			}
		}
		return caller;
	}

	/* (non-Javadoc)
	 * @see multimonster.converter.MetaDataExtractor#doWork(byte[])
	 */
	protected void doWork(byte[] buf) {

		// the meta-data-extraction might finish before the pipes are empty
		// then just read and write the data from input to output.
		if ((!isFinished()) && (isReady()) && (!isParsed())) {
			try {
				boolean moreData = false;
				moreData = caller.sendDataToTCProbe(tcProbeId, buf);
				counter += buf.length;
				if (!moreData)
					setFinished();
			} catch (IOException e) {
				log.error("problem sending data to tcprobe", e);
				log.debug("sent " + counter + " bytes to tcprobe");
				setFinished();
			}
		}

	}

	/* (non-Javadoc)
	 * @see multimonster.converter.MetaDataExtractor#finishWork()
	 */
	protected void finishWork() {

		if (!isFinished()) {
			caller.finishTCProbeProcess(tcProbeId);
			setFinished();
		}
	}

	/* (non-Javadoc)
	 * @see multimonster.converter.MetaDataExtractor#parseFormat()
	 */
	protected Format parseFormat() {

		if (!isParsed()) {
			parseTCProbeOutput();
		}

		return format;
	}

	/* (non-Javadoc)
	 * @see multimonster.converter.MetaDataExtractor#parseMetaData()
	 */
	protected MetaData parseMetaData() {

		if (!isParsed()) {
			parseTCProbeOutput();
		}

		return metaData;
	}

	/**
	 *  
	 */
	private void parseTCProbeOutput() {

		byte[] stdout = null;
		byte[] stderr = null;
		metaData = new MetaData();

		// read data from tcprobe process
		try {

			stdout = caller.getTCProbeStdout(tcProbeId);
			stderr = caller.getTCProbeStderr(tcProbeId);

		} catch (IOException e) {
			log.error("problem getting TCProbes output", e);
			return;
		} finally {
			// process not needed any more
			caller.removeTCProbe(tcProbeId);
			setParsed(true);
		}

		// parse the binary data
		info = parseBinary(stdout);

		if (info != null) {

			format = fillFormat(info);

			metaData.setDuration(new Duration(info.getTime()));
			metaData.setNumOfFrames((int) info.getFrames());

			log.debug(format.toString());
			
		} else {
			format = unknownFormat();
			log.info("useing UNKNOWN as format");
			log.error("tcprobes error message is: " + new String(stderr));
		}
	}

	/**
	 * parases the binary output of tcprobe. Option '-B' is required when calling tcprobe.
	 * 
	 * @param stdout the bynary output of tcprobe 
	 */
	private TcProbeInfo parseBinary(byte[] stdout) {

		byte[] dataBytes = null;
		ByteArrayInputStream stdoutStr = null;
		info = null;

		if (stdout.length != TC_PROBE_INFO_SIZE) {
			log.error("tcprobe output has wrong length: " + stdout.length);
		} else {
			try {
				stdoutStr = new ByteArrayInputStream(stdout);
				// first 4 bytes are crap - see transcode code for details
				stdoutStr.skip(4);
				dataBytes = new byte[stdoutStr.available()];
				stdoutStr.read(dataBytes);
				info = new TcProbeInfo(dataBytes);
			} catch (IOException e) {
				log.error("problem parsing tcprobe binary output", e);
			}
		}
		return info;
	}

	/**
	 * @return Returns the parsed.
	 */
	protected boolean isParsed() {
		return parsed;
	}

	/**
	 * @param parsed The parsed to set.
	 */
	protected void setParsed(boolean parsed) {
		this.parsed = parsed;
	}

	private Format fillFormat(TcProbeInfo info) {

		Format format = new Format(null);

		//resolution
		format.setResolutionVertical(info.getHeight());
		format.setResolutionHorizontal(info.getWidth());
		
		//frames per second
		format.setFps((int) info.getFps());
		
		//aspect_ratio - see src: tcprobe.c
		switch (info.getAsr()) {
			case 1 :
				format.setAspectRatio("1:1");
				break;
			case 2 :
			case 8 :
			case 12 :
				format.setAspectRatio("4:3");
				break;
			case 3 :
				format.setAspectRatio("16:9");
				break;
			case 4 :
				format.setAspectRatio("2.21:1");
				break;
			default :
				format.setAspectRatio("");
				break;
		}
		
		//codec
		format.setCodec(TranscodeMagic.getCodec(info.getCodec()));

		//file type
		format.setStructure(
			TranscodeMagic.getStructure(info.getMagic()));

		format.setBitrate((int) info.getBitrate());

		return format;
	}

	private Format unknownFormat() {

		Format format = new Format(null);

		//resolution
		format.setResolutionVertical(0);
		format.setResolutionHorizontal(0);
		
		//frames per second
		format.setFps(0);
		format.setAspectRatio("");
		
		//codec
		format.setCodec(new Codec(Codec.UNKNOWN));

		//file type
		format.setStructure(new Structure(Structure.UNKNOWN));
		format.setBitrate(0);

		return format;
	}
}
