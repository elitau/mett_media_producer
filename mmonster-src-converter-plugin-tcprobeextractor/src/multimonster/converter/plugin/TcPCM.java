package multimonster.converter.plugin;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Holger Velke
 */
class TcPCM {

	public static final int STRUCT_SIZE = 44;

	// -- transcode structure --------------------------------------------
	private int samplerate = 0;
	private int chan = 0;
	private int bits = 0;
	private int bitrate = 0;
	private int padrate = 0; // byterate for zero padding

	private int format = 0;
	private int lang = 0;

	private int attribute = 0; // 0=subtitle,1=AC3,2=PCM
	private int tid = 0; // logical track id, in case of gaps

	private double pts_start = 0;
	// --------------------------------------------------------------------

	public TcPCM(byte[] dataBytes) throws IOException {

		DataInput data = null;

		//check dataBytes
		if (dataBytes.length != STRUCT_SIZE) {
			throw new IllegalArgumentException(
				"byte-arry has wrong lengh: " + dataBytes.length);
		}

		data = new DataInputStream(new ByteArrayInputStream(dataBytes));
		parseData(data);
	}

	/**
	 *  
	 */
	public TcPCM() {
	}

	public void parseData(DataInput data) throws IOException {
		this.samplerate = data.readInt();
		this.chan = data.readInt();
		this.bits = data.readInt();
		this.bitrate = data.readInt();
		this.padrate = data.readInt(); // byterate for zero padding

		this.format = data.readInt();
		this.lang = data.readInt();

		this.attribute = data.readInt(); // 0=subtitle,1=AC3,2=PCM
		this.tid = data.readInt(); // logical track id, in case of gaps

		this.pts_start = data.readDouble();
	}
	
	/**
	 * @return Returns the sTRUCT_SIZE.
	 */
	public static int getSTRUCT_SIZE() {
		return STRUCT_SIZE;
	}

	/**
	 * @return Returns the attribute.
	 */
	public int getAttribute() {
		return attribute;
	}

	/**
	 * @return Returns the bitrate.
	 */
	public int getBitrate() {
		return bitrate;
	}

	/**
	 * @return Returns the bits.
	 */
	public int getBits() {
		return bits;
	}

	/**
	 * @return Returns the chan.
	 */
	public int getChan() {
		return chan;
	}

	/**
	 * @return Returns the format.
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * @return Returns the lang.
	 */
	public int getLang() {
		return lang;
	}

	/**
	 * @return Returns the padrate.
	 */
	public int getPadrate() {
		return padrate;
	}

	/**
	 * @return Returns the pts_start.
	 */
	public double getPts_start() {
		return pts_start;
	}

	/**
	 * @return Returns the samplerate.
	 */
	public int getSamplerate() {
		return samplerate;
	}

	/**
	 * @return Returns the tid.
	 */
	public int getTid() {
		return tid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
	
		String info = "";
		
		info+="samplerate:'"+samplerate+"'";
		info+="chan:'"+chan+"'";
		info+="bits:'"+bits+"'";
		info+="bitrate:'"+bitrate+"'";
		info+="padrate:'"+padrate+"'";
		info+="format:'"+format+"'";
		info+="lang:'"+lang+"'";
		info+="attribute:'"+attribute+"'";
		info+="tid:'"+tid+"'";
		info+="pts_start:'"+pts_start+"'";
		
		return info;
	}

}