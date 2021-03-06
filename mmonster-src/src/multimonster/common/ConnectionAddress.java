/* Generated by Together */

package multimonster.common;
import java.io.Serializable;
import java.net.URL;

import multimonster.common.media.*;

public class ConnectionAddress  implements Serializable{

	private MOIdentifier mOId;
	private FormatId fId;
	private ProtocolId protocolId;
	private Session session;

	/* completed after construction */
    private URL url;
    
    public ConnectionAddress(Session session, MOIdentifier mOId, FormatId fId, ProtocolId protocolId) {
		this.session = session;
		this.mOId = mOId;
		this.fId = fId;
		this.protocolId = protocolId;
    }
    
	/**
	 * @return
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @return
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @param session
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @param string
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * @return
	 */
	public FormatId getFormatId() {
		return fId;
	}

	/**
	 * @return
	 */
	public MOIdentifier getMOId() {
		return mOId;
	}

	/**
	 * @return
	 */
	public ProtocolId getProtocolId() {
		return protocolId;
	}

}
