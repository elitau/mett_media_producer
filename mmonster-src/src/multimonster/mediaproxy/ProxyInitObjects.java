package multimonster.mediaproxy;
import multimonster.common.pipe.Pipe;
import multimonster.common.resource.ResourceRequestIdentifier;


/**
 * A container used by the MediaProxy.
 * 
 * @author Jörg Meier
 */
public class ProxyInitObjects {
    private Pipe pipeToTransporter;
    private ResourceRequestIdentifier rrId;

	public ProxyInitObjects(Pipe p, ResourceRequestIdentifier rrId){
		this.pipeToTransporter = p;
		this.rrId = rrId;
	}
	
	/**
	 * @return
	 */
	public Pipe getPipeToTransporter() {
		return pipeToTransporter;
	}

	/**
	 * @return
	 */
	public ResourceRequestIdentifier getRrId() {
		return rrId;
	}

}
