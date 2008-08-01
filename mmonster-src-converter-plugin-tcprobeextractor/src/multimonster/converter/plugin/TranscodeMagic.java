package multimonster.converter.plugin;

import java.lang.reflect.Field;

import multimonster.common.Codec;
import multimonster.common.Structure;

/**
 * @author Holger Velke
 */
public class TranscodeMagic {

	//FILE TYPES - see transcode sources: "./import/magic.h"
	public static final long TC_MAGIC_ERROR = 0xFFFFFFFF;
	public static final long TC_MAGIC_UNKNOWN = 0x00000000;
	public static final long TC_MAGIC_PIPE = 0x0000FFFF;
	public static final long TC_MAGIC_DIR = 0x000000FF;
	public static final long TC_MAGIC_RAW = 0x00000001;

	public static final long TC_MAGIC_WAV = 0x00000016;
	public static final long TC_MAGIC_AVI = 0x00000017;
	public static final long TC_MAGIC_ASF = 0x00000018;
	public static final long TC_MAGIC_MOV = 0x00000019;
	public static final long TC_MAGIC_CDXA = 0x00000020;
	public static final long TC_MAGIC_VDR = 0x00000021;
	public static final long TC_MAGIC_XML = 0x00000022;
	public static final long TC_MAGIC_LAV = 0x00000023;
	public static final long TC_MAGIC_OGG = 0x00000024;
	public static final long TC_MAGIC_AF6 = 0x00000025;
	public static final long TC_MAGIC_VNC = 0x00000026;
	public static final long TC_MAGIC_MXF = 0x00000027;

	public static final long TC_MAGIC_VOB = 0x000001ba;
	public static final long TC_MAGIC_SOCKET = 0xFF00FF00;
	public static final long TC_MAGIC_DVD = 0xF0F0F0F0;
	public static final long TC_MAGIC_DVD_PAL = 0xF0F0F0F1;
	public static final long TC_MAGIC_DVD_NTSC = 0xF0F0F0F2;

	public static final long TC_MAGIC_V4L_VIDEO = 0xF0F0F0F3;
	public static final long TC_MAGIC_V4L_AUDIO = 0xF0F0F0F4;
	public static final long TC_MAGIC_V4L2_VIDEO = 0xF0F0F0F5;
	public static final long TC_MAGIC_V4L2_AUDIO = 0xF0F0F0F6;

	//raw streams concatenated frames:  - see transcode sources: "./import/magic.h"

	public static final long TC_MAGIC_M2V = 0x000001b3;
	public static final long TC_MAGIC_PICEXT = 0x000001b5;
	public static final long TC_MAGIC_MPEG = 0x000001e0;
	public static final long TC_MAGIC_TS = 0x00000047;
	public static final long TC_MAGIC_YUV4MPEG = 0x00000300;
	public static final long TC_MAGIC_DV_PAL = 0x1f0700bf;
	public static final long TC_MAGIC_DV_NTSC = 0x1f07003f;
	public static final long TC_MAGIC_AC3 = 0x00000b77;
	public static final long TC_MAGIC_LPCM = 0x00000180;
	public static final long TC_MAGIC_MP3 = 0x0000FFFB;
	public static final long TC_MAGIC_MP2_FC = 0x0000FFFC;
	public static final long TC_MAGIC_MP2 = 0x0000FFFD;
	public static final long TC_MAGIC_MP3_2_5 = 0x0000FFE3;
	public static final long TC_MAGIC_MP3_2 = 0x0000FFF3;
	public static final long TC_MAGIC_NUV = 0x4e757070;
	public static final long TC_MAGIC_TIFF1 = 0x00004D4D;
	public static final long TC_MAGIC_TIFF2 = 0x00004949;
	public static final long TC_MAGIC_JPEG = 0xFFD8FFE0;
	public static final long TC_MAGIC_BMP = 0x0000424D;
	public static final long TC_MAGIC_SGI = 0x000001DA;
	public static final long TC_MAGIC_PNG = 0x89504e47;
	public static final long TC_MAGIC_GIF = 0x00474946;
	public static final long TC_MAGIC_PPM = 0x00005036;
	public static final long TC_MAGIC_PGM = 0x00005035;
	public static final long TC_MAGIC_ID3 = 0x49443303;

	//movie types:  - see transcode sources: "./import/magic.h"

	public static final long TC_MAGIC_PAL = 0x000000F1;
	public static final long TC_MAGIC_NTSC = 0x000000F2;
	public static final long TC_MAGIC_MPG = 0x000000F3;
	public static final long TC_MAGIC_RMF = 0x000000F4;

	// CODECS  - see transcode sources: "./import/magic.h"
	public static final long TC_CODEC_ERROR = 0xFFFFFFFF;
	public static final long TC_CODEC_UNKNOWN = 0x00000000;
	public static final long TC_CODEC_RAW = 0xFEFEFEFE;
	public static final long TC_CODEC_PCM = 0x00000001;
	public static final long TC_CODEC_RGB = 0x00000024;
	public static final long TC_CODEC_AC3 = 0x00002000;
	public static final long TC_CODEC_A52 = 0x00002001;
	public static final long TC_CODEC_YV12 = 0x32315659;
	public static final long TC_CODEC_UYVY = 0x59565955;
	public static final long TC_CODEC_YUV2 = 0x32565559;
	public static final long TC_CODEC_YUY2 = 0x32595559;
	public static final long TC_CODEC_M2V = 0x000001b3;
	public static final long TC_CODEC_MPEG = 0x01000000;
	public static final long TC_CODEC_MPEG1 = 0x00100000;
	public static final long TC_CODEC_MPEG2 = 0x00010000;
	public static final long TC_CODEC_DV = 0x00001000;
	public static final long TC_CODEC_MP3 = 0x00000055;
	public static final long TC_CODEC_NUV = 0x4e757070;
	public static final long TC_CODEC_PS1 = 0x00007001;
	public static final long TC_CODEC_PS2 = 0x00007002;
	public static final long TC_CODEC_DIVX3 = 0x000031B3;
	public static final long TC_CODEC_MP42 = 0x000031B4;
	public static final long TC_CODEC_MP43 = 0x000031B5;
	public static final long TC_CODEC_DIVX4 = 0x000041B6;
	public static final long TC_CODEC_DIVX5 = 0x000051B6;
	public static final long TC_CODEC_XVID = 0x58766944;
	public static final long TC_CODEC_MJPG = 0xA0000010;
	public static final long TC_CODEC_MPG1 = 0xA0000012;
	public static final long TC_CODEC_SUB = 0xA0000011;
	public static final long TC_CODEC_LAV = 0xFFFF0023;
	public static final long TC_CODEC_THEORA = 0x00001234;
	public static final long TC_CODEC_VORBIS = 0x0000FFFE;
	public static final long TC_CODEC_LZO1 = 0x0001FFFE;
	public static final long TC_CODEC_RV10 = 0x0002FFFE;
	public static final long TC_CODEC_SVQ1 = 0x0003FFFE;
	public static final long TC_CODEC_SVQ3 = 0x0004FFFE;
	public static final long TC_CODEC_VP3 = 0x0005FFFE;
	public static final long TC_CODEC_4XM = 0x0006FFFE;
	public static final long TC_CODEC_WMV1 = 0x0007FFFE;
	public static final long TC_CODEC_WMV2 = 0x0008FFFE;
	public static final long TC_CODEC_HFYU = 0x0009FFFE;
	public static final long TC_CODEC_INDEO3 = 0x000AFFFE;
	public static final long TC_CODEC_H263P = 0x000BFFFE;
	public static final long TC_CODEC_H263I = 0x000CFFFE;
	public static final long TC_CODEC_LZO2 = 0x000DFFFE;
	public static final long TC_CODEC_FRAPS = 0x000EFFFE;
	public static final long TC_CODEC_FFV1 = 0x000FFFFE;
	public static final long TC_CODEC_ASV1 = 0x0010FFFE;
	public static final long TC_CODEC_ASV2 = 0x0011FFFE;

	private static String getCodecName(long codec) {

		String name = "UNKOWN";
		Field[] fields = null;

		fields = TranscodeMagic.class.getFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				if (codec == fields[i].getLong(TranscodeMagic.class)) {
					name = fields[i].getName();
					if (name.startsWith("TC_CODEC_")) {
						name = name.substring(9); //remove 'TC_CODEC_'
						break;
					}
				}
			}
		} catch (Exception e) {
		}

		return name;
	}

	public static Structure getStructure(long codec) {

		String name = Structure.UNKNOWN;
		Field[] fields = null;

		fields = TranscodeMagic.class.getFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				if (codec == fields[i].getLong(TranscodeMagic.class)) {
					name = fields[i].getName();
					if (name.startsWith("TC_MAGIC_")) {
						name = name.substring(9); //remove 'TC_MAGIC_'
						break;
					}
				}
			}
		} catch (Exception e) {
		}

		return new Structure(name);
	}
	
	/*
	 * see transcode sources: "./src/probe.c - char *codec2str(int f)"  
	 */
	/**
	 * Makes the mapping between Transcode codec-number and 
	 * MultiMonster-Codec 
	 * 
	 * @param codecNum
	 * @return the Codec spezified by codecNum
	 */
	public static Codec getCodec(long codecNum) {
		switch ((int) codecNum) {

			case (int) TC_CODEC_MPEG2 :
				return new Codec(Codec.MPEG2);

			case (int) TC_CODEC_MJPG :
				return new Codec(Codec.MJPG);

			case (int) TC_CODEC_MPG1 :
				return new Codec(Codec.MPEG1);

			case (int) TC_CODEC_LZO1 :
				return new Codec(Codec.LZO1);

			case (int) TC_CODEC_RV10 :
				return new Codec(Codec.RV10);

			case (int) TC_CODEC_DIVX3 :
				return new Codec(Codec.DIVX3);

			case (int) TC_CODEC_MP42 :
				return new Codec(Codec.MP42);

			case (int) TC_CODEC_MP43 :
				return new Codec(Codec.MP43);

			case (int) TC_CODEC_DIVX4 :
				return new Codec(Codec.DIVX4);

			case (int) TC_CODEC_DIVX5 :
				return new Codec(Codec.DIVX5);

			case (int) TC_CODEC_XVID :
				return new Codec(Codec.XVID);

			case (int) TC_CODEC_MPEG1 :
				return new Codec(Codec.MPEG1);

			case (int) TC_CODEC_MPEG :
				return new Codec(Codec.MPEG);

			case (int) TC_CODEC_DV :
				return new Codec(Codec.DV);

			case (int) TC_CODEC_YV12 :
				return new Codec(Codec.YV12);

			case (int) TC_CODEC_YUV2 :
				return new Codec(Codec.YUV2);

			case (int) TC_CODEC_NUV :
				return new Codec(Codec.NUV);

			case (int) TC_CODEC_RGB :
				return new Codec(Codec.RGB);

			case (int) TC_CODEC_LAV :
				return new Codec(Codec.LAV);

			case (int) TC_CODEC_PCM :
				return new Codec(Codec.PCM);
			
			case (int) TC_CODEC_4XM :
				return new Codec(Codec._4XM);

			default :
				return new Codec(getCodecName(codecNum));
		}
	}
}
