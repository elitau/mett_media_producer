package multimonster.common;

import java.io.Serializable;

/**
 * @author Jörg Meier
 *
 */
public class ProtocolId  implements Serializable{

	/**
	 * Known protocols
	 * TODO complete protocol list 
	 */
	public static String pId_HTTP = "HTTP";
	public static String pId_RTSP = "RTSP";
	public static String pId_RAW_SOCKET = "RAW_SOCKET";
	
	/** 
	 * Internes Standard-Protokoll zwischen Proxy und Transporter 
	 */
	public static String pId_mmSimple = "MM_SIMPLE";

	private String id;
	/**
	 * 
	 */
	public ProtocolId(String id) {
		this.id = id.toUpperCase();
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

}
