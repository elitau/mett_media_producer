package multimonster.common;

import java.io.Serializable;

/**
 * Internal representation of multimedia containers
 * 
 * @author Holger Velke (sihovelk)
 */
public class Structure implements Serializable {

	public static final String UNKNOWN = "UNKNOWN";

	public static final String RAW = "RAW";

	public static final String WAV = "WAV";
	public static final String AVI = "AVI";
	public static final String ASF = "ASF";
	public static final String MOV = "MOV";
	public static final String LAV = "LAV";
	public static final String OGG = "OGG";
	public static final String AF6 = "AF6";
	public static final String VNC = "VNC";
	public static final String MXF = "MXF";

	public static final String VOB = "VOB";

	//raw streams concatenated frames:  - see transcode sources: "./import/magic.h"

	public static final String M2V = "M2V";
	public static final String PICEXT = "PICEXT";
	public static final String MPEG = "MPEG";
	public static final String TS = "TS";
	public static final String YUV4MPEG = "YUV4MPEG";
	public static final String DV_PAL = "DV_PAL";
	public static final String DV_NTSC = "DV_NTSC";
	public static final String AC3 = "AC3";
	public static final String LPCM = "LPCM";
	public static final String MP3 = "MP3";
	public static final String MP2_FC = "MP2_FC";
	public static final String MP2 = "MP2";
	public static final String MP3_2_5 = "MP3_2_5";
	public static final String MP3_2 = "MP3_2";
	public static final String NUV = "NUV";

	//movie types:  - see transcode sources: "./import/magic.h"
	public static final String PAL = "PAL";
	public static final String NTSC = "NTSC";
	public static final String MPG = "MPG";
	public static final String RMF = "RMF";
	
	
	private String structureId = null;
	
	public Structure (String id) {
		this.structureId = id;
	}
	
	public String toString(){
		return this.structureId;
	}
}
