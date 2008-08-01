package multimonster.common;

import java.io.Serializable;

/**
 * unique Identification for streaming protocol. both protocolID and protocolName are unique. All available protocols are strored in the SystemadministrationDB. 
 */
public class Protocol  implements Serializable{
    private ProtocolId protocolID;
    private String protocolName;

    /**
     * Network-Layer to which the protocol belongs. 
     */
    private int ProtocolLayer;

    /**
     * Wheather it's realtime or not. 
     */
    private boolean realtime;

	public Protocol(ProtocolId id){
		this.protocolID = id;
		this.protocolName = "";
	}

	public Protocol(ProtocolId id, String protocolName){
		this.protocolID = id;
		this.protocolName = protocolName;
	}

	/**
	 * @return
	 */
	public int getProtocolLayer() {
		return ProtocolLayer;
	}

	/**
	 * @return
	 */
	public String getProtocolName() {
		return protocolName;
	}

	/**
	 * @return
	 */
	public boolean isRealtime() {
		return realtime;
	}

	/**
	 * @param i
	 */
	public void setProtocolLayer(int i) {
		ProtocolLayer = i;
	}

	/**
	 * @param string
	 */
	public void setProtocolName(String string) {
		protocolName = string;
	}

	/**
	 * @param b
	 */
	public void setRealtime(boolean b) {
		realtime = b;
	}

	/**
	 * @return
	 */
	public ProtocolId getProtocolID() {
		return protocolID;
	}

}
