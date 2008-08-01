package multimonster.mediaaccess;


import multimonster.common.media.MIIdentifier;
import multimonster.common.pipe.Pipe;

public interface MediaAccessFacade {
	
	/**
	 * removes a existing Mediainstance from Mediadata
	 * 
	 * @param mIId Mediainstanceindentifier
	 * @return boolean 
	 */
    boolean remMediaInstance(MIIdentifier mIId);

    /**
     * returns pipe object where the mediainstance can be read from
     * @param mIId MediaInstanceIdentifier 
     * @return Pipe Data can be read from here
     */
    Pipe getMediaInstanceData(MIIdentifier mIId);
    
    /**
     * inserts a new mediainstance to Mediadata <br>
     * data is read from the given pipe until its closed
     * 
     * @param inputPipe where Data is read from
     * @return MIIdentifier a new Identifier, which has been created
     */
    MIIdentifier newMediaInstanceData(Pipe inputPipe);
    
}
