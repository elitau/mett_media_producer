package multimonster.common;

import java.io.Serializable;

public class InputOption implements Serializable{

    private Protocol protocol;
    
	public InputOption(Protocol p){
		this.protocol = p;
	}

	/**
	 * @return
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
}
