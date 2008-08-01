package multimonster.converter.plugin;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import com.mindprod.ledatastream.LEDataInputStream;

/**
 * @author Holger Velke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
class TcProbeInfo {

	public static final int STRUCT_SIZE = 1504;

	// transcode const - see tc_... for info
	private int TC_MAX_AUD_TRACKS = 32;

	// -- transcode structure --------------------------------------------
	private int width = 0; //frame size parameter
	private int height = 0; //frame size parameter

	private double fps = 0; //encoder fps

	private long codec = 0; //video codec
	private long magic = 0; //file type/magic
	private long magic_xml = 0;
	// specifies type/magic of the file content in xml file

	private int asr = 0; //aspect ratio code
	private int frc = 0; //frame rate code

	private int par_width = 0; // pixel aspect (== sample aspect ratio)
	private int par_height = 0;

	private int attributes = 0; //video attributes

	private int num_tracks = 0; //number of audio tracks

	private TcPCM track[] = new TcPCM[TC_MAX_AUD_TRACKS];
	//probe for TC_MAX_AUD_TRACKS tracks

	private long frames = 0; //total frames
	private long time = 0; //total time in secs

	private int unit_cnt = 0; //detected presentation units
	private double pts_start = 0; //video PTS start Presentation Time Stamp

	private long bitrate = 0; //video stream bitrate

	private int ext_attributes[] = new int[4]; //reserved for MPEG

	private int is_video = 0; //NTSC flag	
	// --------------------------------------------------------------------

	public TcProbeInfo(byte[] dataBytes) throws IOException {

		DataInput data = null;
		ByteArrayInputStream dataStr = null;

		//check dataBytes
		if (dataBytes.length != STRUCT_SIZE) {
			throw new IllegalArgumentException(
				"byte-arry has wrong lengh: " + dataBytes.length);
		}

		dataStr = new ByteArrayInputStream(dataBytes);

		if (dataStr.available() != STRUCT_SIZE)
			throw new IllegalArgumentException(
				"byte-arry has wrong lengh: " + dataStr.available());

		// Check native ByteOrder - internal ByteOrder is BIG_ENDIAN
		// create fitting DataInput
		if (ByteOrder.LITTLE_ENDIAN.equals(ByteOrder.nativeOrder())) {
			// Use special DataInput to convert from LITTLE_ENDIAN
			data = new LEDataInputStream(dataStr);
		} else {
			data = new DataInputStream(dataStr);
		}

		parseData(data, dataStr);
	}

	private void parseData(DataInput data, ByteArrayInputStream dataStr)
		throws IOException {

		// parse inputdata
		// transcode specific!!
		this.width = data.readInt(); //frame size parameter
		this.height = data.readInt(); //frame size parameter
		this.fps = data.readDouble(); //encoder fps
		this.codec = data.readInt(); //video codec
		this.magic = data.readInt(); //file type/magic
		this.magic_xml = data.readInt();
		// specifies type/magic of the file content in xml file
		this.asr = data.readInt(); //aspect ratio code
		this.frc = data.readInt(); //frame cate code
		this.par_width = data.readInt();
		// pixel aspect (== sample aspect ratio)
		this.par_height = data.readInt();
		this.attributes = data.readInt(); //video attributes
		this.num_tracks = data.readInt(); //number of audio tracks

		for (int i = 0; i < track.length; i++) {
			track[i] = new TcPCM();
			track[i].parseData(data);
		}

		this.frames = data.readInt(); //total frames
		this.time = data.readInt(); //total time in secs
		this.unit_cnt = data.readInt(); //detected presentation units
		this.pts_start = data.readDouble(); //video PTS start
		this.bitrate = data.readInt(); //video stream bitrate

		for (int i = 0; i < ext_attributes.length; i++) {
			ext_attributes[i] = data.readInt(); //reserved for MPEG
		}

		this.is_video = data.readInt(); //NTSC flag
	}

	/**
	 * @return Returns the sTRUCT_SIZE.
	 */
	public static int getSTRUCT_SIZE() {
		return STRUCT_SIZE;
	}

	/**
	 * @return Returns the asr.
	 */
	public int getAsr() {
		return asr;
	}

	/**
	 * @return Returns the attributes.
	 */
	public int getAttributes() {
		return attributes;
	}

	/**
	 * @return Returns the bitrate.
	 */
	public long getBitrate() {
		return bitrate;
	}

	/**
	 * @return Returns the codec.
	 */
	public long getCodec() {
		return codec;
	}

	/**
	 * @return Returns the ext_attributes.
	 */
	public int[] getExt_attributes() {
		return ext_attributes;
	}

	/**
	 * @return Returns the fps.
	 */
	public double getFps() {
		return fps;
	}

	/**
	 * @return Returns the frames.
	 */
	public long getFrames() {
		return frames;
	}

	/**
	 * @return Returns the frc.
	 */
	public int getFrc() {
		return frc;
	}

	/**
	 * @return Returns the height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return Returns the is_video.
	 */
	public int getIs_video() {
		return is_video;
	}

	/**
	 * @return Returns the magic.
	 */
	public long getMagic() {
		return magic;
	}

	/**
	 * @return Returns the magic_xml.
	 */
	public long getMagic_xml() {
		return magic_xml;
	}

	/**
	 * @return Returns the num_tracks.
	 */
	public int getNum_tracks() {
		return num_tracks;
	}

	/**
	 * @return Returns the par_height.
	 */
	public int getPar_height() {
		return par_height;
	}

	/**
	 * @return Returns the par_width.
	 */
	public int getPar_width() {
		return par_width;
	}

	/**
	 * @return Returns the pts_start.
	 */
	public double getPts_start() {
		return pts_start;
	}

	/**
	 * @return Returns the tC_MAX_AUD_TRACKS.
	 */
	public int getTC_MAX_AUD_TRACKS() {
		return TC_MAX_AUD_TRACKS;
	}

	/**
	 * @return Returns the time.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return Returns the track.
	 */
	public TcPCM[] getTrack() {
		return track;
	}

	/**
	 * @return Returns the unit_cnt.
	 */
	public int getUnit_cnt() {
		return unit_cnt;
	}

	/**
	 * @return Returns the width.
	 */
	public int getWidth() {
		return width;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		String info = "";

		info += "width='" + width + "' ";
		info += "height:'" + height + "' ";
		info += "fps:'" + fps + "' ";
		info += "codec:'" + codec + "' ";
		info += "magic:'" + magic + "' ";
		info += "magic_xml:'" + magic_xml + "' ";
		info += "asr:'" + asr + "' ";
		info += "frc:'" + frc + "' ";
		info += "par_width:'" + par_width + "' ";
		info += "par_height:'" + par_height + "' ";
		info += "attributes:'" + attributes + "' ";
		info += "num_tracks:'" + num_tracks + "' ";
		for (int i = 0; i < track.length; i++) {
			info += "track" + i + ":'" + track[i] + "' ";
		}
		info += "frames:'" + frames + "' ";
		info += "time:'" + time + "' ";
		info += "unit_cnt:'" + unit_cnt + "' ";
		info += "pts_start:'" + pts_start + "' ";
		info += "bitrate:'" + bitrate + "' ";
		for (int i = 0; i < ext_attributes.length; i++) {
			info += "ext_attribute" + i + ":'" + ext_attributes[i] + "' ";
		}
		info += "is_video:'"+is_video+"' ";

		return info;
	}

}
