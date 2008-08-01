package multimonster.converter.plugin;

import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import multimonster.common.Format;
import multimonster.common.FormatId;
import multimonster.common.pipe.PipeClosedException;
import multimonster.converter.ConverterPlugIn;
import multimonster.converter.exception.TranscodeException;
import multimonster.converter.exceptions.ConverterException;
import multimonster.converter.plugin.jmx.TranscodeCaller;

/**
 * @author Holger Velke
 */
public class TranscodeWrapper extends ConverterPlugIn {

	private static Logger log = Logger.getLogger(TranscodeWrapper.class);

	private static MBeanServer mBeanServer = null;

    /**
     * @label uses 
     */
	private static TranscodeCaller caller = null;

	private String VIDEO_EXPORT_MODULE = "mpeg";
	private String AUDIO_EXPORT_MODULE = "null";
	private String CODEC_STRING = "";
	private String STANDARD_PARAMETER = "-V";
	private String WIDTH_HEIGHT = "";
	private String BITRATE = "";
//	private String TRANSCODE_TEMP_PATH = "/multimonster/transcode/";
	private String TRANSCODE_TEMP_PATH = "/tmp/mmonster/";
//	private String VIDEO_INSTANCE_PATH = "/Users/elitau/Documents/Master/06_MIAV/jboss-4.2.2.GA/server/default/mm_video/";
	/**
	 *  
	 */
	public TranscodeWrapper() {
		caller = getTranscodeCaller();
	}

	static private MBeanServer getMBeanServer() {

		if (mBeanServer == null) {
			ArrayList mbeanServers = MBeanServerFactory.findMBeanServer(null);
			mBeanServer = (MBeanServer) mbeanServers.get(0);
		}

		return mBeanServer;
	}

	private static TranscodeCaller getTranscodeCaller() {
		if (mBeanServer == null)
			getMBeanServer();
		if (caller == null) {
			try {
				// for retrieval:
				ObjectName objName = new ObjectName(TranscodeCaller.JMX_NAME);
				// get the Object-reference (usable only if EJB in the same
				// JVM)
				log.debug("try to get the Caller");
				caller =
					(TranscodeCaller) mBeanServer.invoke(
						objName,
						"returnThis",
						null,
						null);
			} catch (Exception e) {
				log.error("problem getting TranscodeCaller MBean", e);
			}
		}
		return caller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see multimonster.converter.ConverterPlugIn#init(
	 * 		multimonster.common.Format, multimonster.common.Format)
	 */
	public void init(Format input, Format output) throws ConverterException {

		FormatId outputId = null;

		if (input == null) {
			throw new TranscodeException(
					"unable to init with 'null' input format");
		}
		if (output == null) {
			throw new TranscodeException(
					"unable to init with 'null' output format");
		}

		int oldHeight = input.getResolutionVertical();
		int oldWidth = input.getResolutionHorizontal();
		int newHeight = output.getResolutionVertical();
		int newWidth = output.getResolutionHorizontal();
		
		log.debug("oldHeight="+oldHeight+" oldWidth="+oldWidth+
				" newHeight="+newHeight+" newWidth="+newWidth);
		
		// set resolution
		if (newHeight > 0 && ((oldHeight > 0 &&	oldWidth > 0))){
			// calculate new width to avoid killing the aspectratio.
			newWidth = (oldWidth*newHeight/oldHeight);
			WIDTH_HEIGHT = newWidth + "x" + newHeight + ",fast ";
		} else if (oldWidth <= 0 | oldHeight<= 0) {
			if (newWidth > 0 && newHeight > 0) {
				WIDTH_HEIGHT = newWidth + "x" + newHeight + ",fast ";
			} else {
				WIDTH_HEIGHT = "x" + newHeight + ",fast ";
				log.warn("RESIZING - maybe killing aspect ratio!");
			}
		} else {
			log.info("NO RESIZE!");
		}
		
		// set bitrate
		if (output.getBitrate() > 0) {
			BITRATE = "" + output.getBitrate();
		}
		
		// set export module
		outputId = output.getFormatId();
		if ((outputId.equals(FormatId.fId_MPEG_1_LOW))|
			(outputId.equals(FormatId.fId_MPEG_1_MID))|
			(outputId.equals(FormatId.fId_MPEG_1_HI))) {
			CODEC_STRING = "mpeg1";
			VIDEO_EXPORT_MODULE = "ffmpeg";
		} else if ((outputId.equals(FormatId.fId_MPEG_2_LOW))|
				(outputId.equals(FormatId.fId_MPEG_2_MID))|
				(outputId.equals(FormatId.fId_MPEG_2_HI))) {
			CODEC_STRING = "mpeg2";
			VIDEO_EXPORT_MODULE = "ffmpeg";
		} else if ((outputId.equals(FormatId.fId_DIVX4_LOW))|
				(outputId.equals(FormatId.fId_DIVX4_MID))|
				(outputId.equals(FormatId.fId_DIVX4_HI)))  {
			CODEC_STRING = "mpeg4";
			VIDEO_EXPORT_MODULE = "ffmpeg";
		} else {
			throw new TranscodeException(
				"unsupported format - FormatId='" + outputId + "'");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		String transcodeParameters = null;
		String baseFileName = null;
		String to_tc_fileName = null;
		String from_tc_fileName = null;

		log.debug("run()");

		// bulid filename of communication files
		baseFileName = String.valueOf(System.currentTimeMillis());
		to_tc_fileName = TRANSCODE_TEMP_PATH + "~to" + baseFileName;
		from_tc_fileName = TRANSCODE_TEMP_PATH + "~from" + baseFileName;

		transcodeParameters =
			buildTranscodeParameters(to_tc_fileName, from_tc_fileName);
		
		// create 'real' output-filename
		// transcode does not use to given name but atatches a suffix
//		if (VIDEO_EXPORT_MODULE == "mpeg")
//			from_tc_fileName += ".m1v";
//		else if (VIDEO_EXPORT_MODULE == "ffmpeg") {
//			if (CODEC_STRING == "mpeg1") {
//				from_tc_fileName += ".m1v";
//			} else if (CODEC_STRING == "mpeg2") {
//				from_tc_fileName += ".m2v";
//			}
//		}

		try {
			output.waitForPipeSetup();
		} catch (PipeClosedException e) {
			log.warn("pipe was not setup - unable to do work");
			input.close();
			return;
		}

		input.setupFinished();

		caller.doWork(
			input,
			output,
			transcodeParameters,
			to_tc_fileName,
			from_tc_fileName);
	}

	private String buildTranscodeParameters(
		String to_tc_fileName,
		String from_tc_fileName) {

		String transcodeParameters = "";

		transcodeParameters += " " + STANDARD_PARAMETER + " yuv420p";
		transcodeParameters += " -i " + to_tc_fileName; // transcode input file
		transcodeParameters += " -o " + from_tc_fileName;
		// transcode output file
		transcodeParameters += " -y "
			+ VIDEO_EXPORT_MODULE
			+ ","
			+ AUDIO_EXPORT_MODULE;

		if (!CODEC_STRING.equals(""))
			transcodeParameters += " -F " + CODEC_STRING;

		if (!WIDTH_HEIGHT.equals(""))
			transcodeParameters += " -Z " + WIDTH_HEIGHT;

		if (!BITRATE.equals(""))
			transcodeParameters += " -w " + BITRATE;

		log.debug("Transcode-Parameters: " + transcodeParameters);

		return transcodeParameters;
	}
}
