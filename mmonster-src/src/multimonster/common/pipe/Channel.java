package multimonster.common.pipe;


/**
 * Represents a Channel for a Pipe to transfer data.
 * 
 * @author Jörg Meier
 *
 */
abstract public class Channel {

	/**
	 * Checks if the channel is closed.
	 * 
	 * @return
	 */
	public abstract boolean isClosed();
	
	/**
	 * Closes the channel.
	 *
	 */
	public abstract void close();
	
	/**
	 * @param length -
	 *            the number of bytes to read
	 * 
	 * Read Data out of the channel.
	 * 
	 * read() blocks when there is no more data in
	 * the channel and continues when there is data again to transfer.
	 * 
	 * returns a new byte[] with the length of read bytes
	 */
	public abstract byte[] read(int length) throws ChannelClosedException;
	
	/**
	 * Writes data in the channel to transfer it.
	 * 
	 * Blocks when the channel is full and
	 * continues when there is space again.
	 * 
	 * @param in the data
	 * @param len the amount to write to the channel
	 * @throws ChannelClosedException
	 */
	public abstract void write(byte[] in, int len)throws ChannelClosedException;
	
	/**
	 * Writes data in the channel to transfer it.
	 * 
	 * Blocks when the channel is full and
	 * continues when there is space again.
	 * 
	 * @param in the bytes to write in the channel
	 * @throws ChannelClosedException
	 */
	public abstract void write(byte[] in) throws ChannelClosedException;
	
}
