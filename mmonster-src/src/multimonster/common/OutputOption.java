package multimonster.common;

import java.io.Serializable;

public class OutputOption  implements Serializable{
    private Format format;
    private Protocol protocol;
	
	public OutputOption(Format f, Protocol p){
		this.format = f;
		this.protocol = p;
	}

	/**
	 * @return
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * @return
	 */
	public Protocol getProtocol() {
		return protocol;
	}
	
	public String toString(){
		return "Format: " + this.format.getDescription() + " --- ProtocolName: " + this.protocol.getProtocolName(); 
	}

}
