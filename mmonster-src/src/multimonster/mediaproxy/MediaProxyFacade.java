package multimonster.mediaproxy;
import multimonster.common.ConnectionAddress;
import multimonster.common.FormatId;
import multimonster.common.ProtocolId;
import multimonster.common.Session;
import multimonster.common.Action;
import multimonster.common.media.MOIdentifier;
import multimonster.common.resource.ResourceRequestIdentifier;
import multimonster.mediaproxy.exceptions.MediaProxyException;

/**
 * The interface for the MediaProxy-component.
 * Represents the user-interface for media in- and output.
 * Manages the different proxies, distributes the requestes to running instances. 
 * 
 * @author Jörg Meier
 */
public interface MediaProxyFacade {


	/**
     * Returns an address where media data of a certain protocol and format
     * can be fetched.
     * 
	 * @param session
	 * @param mOId
	 * @param fId
	 * @param protocol
	 * @return
	 * @throws MediaProxyException
	 */
    ConnectionAddress getOutputProxy(Session session, MOIdentifier mOId, FormatId fId, ProtocolId protocolId) throws MediaProxyException;

    /**
     * Returns an address where media data has to be transferred.
     * 
     * @param session
     * @param mOId
     * @param fId
     * @param protocol
     * @return
     * @throws MediaProxyException
     */
    ConnectionAddress getInputProxy(Session session, MOIdentifier mOId, ProtocolId protocolId) throws MediaProxyException;

	/**
     * Does the initialization for an output or input request.
     * calls ControllerFacade:authorize()
     * 		 ControllerFacade:requestResources()
     * 		 TransporterFacade:output()
     * and returns
     * 		the Pipe from the Transporter,
     * 		the ResourceRequestIdentifier.
	 * 
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 * @param isInput
	 * @return
	 * @throws MediaProxyException
	 */
    ProxyInitObjects initWork(Session p0, MOIdentifier p1, FormatId p2, ProtocolId p3, Action p4, boolean isInput) throws MediaProxyException;


	/**
     * Cleans up, after a request is finished.
     * calls ControllerFacade:releaseResource()
	 * 
	 * @param rrId
	 * @throws MediaProxyException
	 */
    void requestFinished(ResourceRequestIdentifier rrId) throws MediaProxyException ;
    
    /**
     * For direct insert of a media file without any ProxyPlugin.
     * For test and demo.
     * 
     * @param file the media file
     * @param link the link that would normally be used to insert via a proxy-plugin
     */
    void directInput(byte[] file, String link) ;
}
