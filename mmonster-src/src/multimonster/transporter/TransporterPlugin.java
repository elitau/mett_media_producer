package multimonster.transporter;

import multimonster.common.pipe.Pipe;
import multimonster.common.plugin.PlugIn;
import multimonster.transporter.exceptions.TransporterException;

/**
 * A Plugin for the transporter.
 * External streaming server may be integrated here.
 * 
 * @author Jörg Meier
 *
 */
abstract public class TransporterPlugin extends PlugIn implements Runnable{

    /**
     * Sets the given pipes in the plugin.
     * 
     * @param pipeToMediaProxy
     * @param pipeFromConverter
     * @throws TransporterException
     */
	public abstract void setPipes(Pipe pipeToMediaProxy, Pipe pipeFromConverter) throws TransporterException;

    /**
     * Finishes a request.
     * NOT NECESSARY, JUST CLOSE THE PIPE!
     *
     */
	public abstract void disconnect();
}
