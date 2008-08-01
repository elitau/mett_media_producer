package multimonster.common;

import java.io.Serializable;

/**
 * Specifies a codec. It includes a system-specific codec-id
 * and the name of the codec.
 * 
 * @author Holger Velke (sihovelk)
 */
public class Codec implements Serializable{

	public final static String UNKNOWN = "UNKNOWN";
	
	public final static String MPEG1 = "MPEG-1";
	public final static String MPEG2 = "MPEG-2";
	public final static String DIVX4 = "DIVX";
	public final static String WMV9P = "WMV9P";
	public final static String MJPG = "MJPG";
	public final static String LZO1 = "LZO1";
	public final static String RV10 = "RV10 Real Video";
	public final static String DIVX3 = "DivX;-)";
	public final static String MP42 = "MSMPEG4_V2";
	public final static String MP43 = "MSMPEG4_V3";
	public final static String DIVX5 = "DivX5";
	public final static String XVID = "XviD";
	public final static String MPEG = "MPEG";
	public final static String DV = "Digital Video";
	public final static String YV12 = "YV12/I420";
	public final static String YUV2 = "YUV2";
	public final static String NUV = "RTjpeg";
	public final static String RGB = "RGB/BGR";
	public final static String LAV = "LAV";
	public final static String PCM = "PCM";
	public final static String RAW = "RAW";
	public final static String AC3 = "AC3";
	public final static String A52 = "A52";
	public final static String UYVY = "UYVY";
	public final static String YUY2 = "YUY2";
	public final static String M2V = "M2V";
	public final static String MP3 = "MP3";
	public final static String PS1 = "PS1";
	public final static String PS2 = "PS2";
	public final static String SUB = "SUB";
	public final static String THEORA = "THEORA";
	public final static String VORBIS = "VORBIS";
	public final static String SVQ1 = "SVQ1";
	public final static String SVQ3 = "SVQ3";
	public final static String VP3 = "VP3";
	public final static String _4XM = "4XM";
	public final static String WMV1 = "WMV1";
	public final static String WMV2 = "WMV2";
	public final static String HFYU = "HFYU";
	public final static String INDEO3 = "INDEO3";
	public final static String H263P = "H263P";
	public final static String H263I = "H263I";
	public final static String LZO2 ="LZO2";
	public final static String FRAPS = "FRAPS";
	public final static String FFV1 = "FFV1";
	public final static String ASV1 = "ASV1";
	public final static String ASV2 = "ASV2";
	
    private String codecID;
    
    public Codec(String codecID) {
    	this.codecID = codecID;
    }
    	
	public String toString() {
		return codecID;
	}

}
