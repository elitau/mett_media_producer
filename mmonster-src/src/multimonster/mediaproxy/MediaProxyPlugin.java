package multimonster.mediaproxy;

import java.net.URL;

import org.apache.log4j.Logger;

import multimonster.common.ConnectionAddress;
import multimonster.common.FormatId;
import multimonster.common.ProtocolId;
import multimonster.common.Session;
import multimonster.common.media.MOIdentifier;
import multimonster.common.plugin.PlugIn;
import multimonster.mediaproxy.exceptions.MediaProxyException;

/**
 * A Proxy that serves the data with a protocol.
 * It gets a request via connect(), does initialization and gets
 * the data pipe from the deeper layer.
 * It starts its work as a thread via run().
 * 
 * @author Jörg Meier
 */
abstract public class MediaProxyPlugin extends PlugIn implements Runnable {

    /** the ConnectionAddress for this plugin */
	protected ConnectionAddress caddr;
 
	/**
	 * Gets a connectionAddress to setup proxy and returns the
	 * URL where the proxy is accessible.
	 * 
	 * FormatId may be null, if it's insert.
	 * 
	 */
	public URL init(Session session, MOIdentifier mOId, ProtocolId protocolId, FormatId fId) throws MediaProxyException {
		
		Logger log = Logger.getLogger(this.getClass());
		URL url = null;
		String errorText = "";
		
		if (session == null){
			errorText = "";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else if (mOId == null){
			errorText = "";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		} else	if (protocolId == null){
			errorText = "";
			log.error(errorText);
			throw new MediaProxyException(errorText);
		}
		
		// save given parameters, then concrete PlugIn can access them
		caddr = new ConnectionAddress(session, mOId, fId, protocolId);
		
		// init in concrete Proxy, e. g. bind to socket
		url = initPlugIn();
		
		if (url == null) {
			errorText = "Concrete Proxy-Init-Method didn't return a URL.";
			log.error(errorText);
			throw new MediaProxyException(errorText);			
		}
		
		// save url the concrete PlugIn returned
		caddr.setUrl(url);
		
		log.debug("Got ConnectionAddress, initialization finished.");
		
		return url;	
	}

	public ConnectionAddress getConnectionAddress() {
		return caddr;
	}

	/**
	 * Should be implemented in every Proxy to get ready to serve
	 * the later incoming request.
	 * 
	 * @throws MediaProxyException
	 */
	protected abstract URL initPlugIn() throws MediaProxyException;
	
	public abstract void connect(ConnectionAddress p0);

    public abstract void disconnect();
}
