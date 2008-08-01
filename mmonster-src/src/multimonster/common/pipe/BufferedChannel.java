package multimonster.common.pipe;

import org.apache.log4j.Logger;

/**
 *  A Channel to transfer data which uses a RingBuffer.
 * 
 * @author Jörg Meier
 */
public class BufferedChannel extends Channel {
	// TODO if reader and writer both are waiting -> Deadlock!! - This can
	// happen if there is not enough to read and not enough space to write

 /* Ideas for later enhancements
  *   
    private int percentageFilled;
    private int highLevelLimit;
    private int lowLevelLimit;
    private int channelCapacity;
*/
	/** the Pipe this Channel belongs to */
	private Pipe myPipe; 
	
	/**
	 * the default BufferSize for a BufferedChannel
	 */
	private static int defaultBufferSize = 10 * 128 * 1024;
    
	private RingBuffer buffer;
	private int bufferSize;
	private boolean isClosed;
	private boolean readerIsNotified = false;
	private boolean writerIsNotified = false;
	private static int TIMEOUT = 1000 * 60; //60s

	private Logger log;

	
	/**
	 * Constructs a Channel with default BufferSize.
	 *
	 */
	public BufferedChannel(Pipe p){
		this(p, defaultBufferSize);	
	}
	
	/**
	 * Constructs a Channel with the given BufferSize.
	 * 
	 * @param bufferSize
	 */
	public BufferedChannel(Pipe p, int bufferSize){		
		
		log = Logger.getLogger(this.getClass());

		this.myPipe = p;
		this.bufferSize = bufferSize; 
		this.buffer = new RingBuffer(this.bufferSize);
		this.isClosed = false;
		
	}
	
	public boolean isClosed() {
		return isClosed;
	}

	
	public synchronized void close() {

		//log.debug("close()");
		this.isClosed = true;
		readerIsNotified = true;
		writerIsNotified = true;
		this.notifyAll();

	}

	public synchronized byte[] read(int length) throws ChannelClosedException {
		byte[] buf = new byte[length];
		byte[] result = null;
		int nRead = 0;

		if (length > bufferSize) {
			throw new IllegalArgumentException(
				"length > bufferSize; bufferSize = " + bufferSize);
		}

		while (true) {

			if (isClosed && buffer.isEmpty()) {
				// channel is closed and has nothing to read
				throw new ChannelClosedException();
			} else if ((length > buffer.length()) && !isClosed) {
				// channel is open and has not enough to read
				try {
					//log.debug("sleep - read");
					readerIsNotified = false;
					this.wait(TIMEOUT);
					if (!readerIsNotified) {
						// a real timeout occured, so closing channel
						log.warn("channel-Timeout");
						close();
					}
					//log.debug("wake - read");
				} catch (InterruptedException e) {
					log.error(e);
				}
			} else
				break;
		}

		nRead = buffer.get(buf, length);
		if (nRead == length) {
			result = buf;
		} else {
			//log.debug("returning smaller buffer than ordered");
			result = new byte[nRead];
			for (int i = 0; i < result.length; i++)
				result[i] = buf[i];
		}

		writerIsNotified = true;
		this.notifyAll();

		return result;
	}

	public synchronized void write(byte[] in, int len)throws ChannelClosedException {

		if (len > in.length){
			throw new IllegalArgumentException(
					"len > in.length");
		}		
		if (len > bufferSize) {
			throw new IllegalArgumentException(
				"len > bufferSize; bufferSize = " + bufferSize);
		}

		// wait until Buffer is Free
		while (true) {
			if (isClosed) {
				throw new ChannelClosedException();
			}
			if (buffer.available() < len) {
				try {
					//log.debug("sleep - write");
					writerIsNotified = false;
					this.wait(TIMEOUT);
					if (!writerIsNotified) {
						// a real timeout occured, so closing channel
						log.warn("channel-Timeout");
						close();
					}
					//log.debug("wake - write");
				} catch (InterruptedException e1) {
					log.error(e1);
				}
			} else
				break;
		}

		try {
			buffer.add(in, len);
		} catch (Exception e) {
			// this should never be reached
			log.fatal("Buffer threw exception - this should never happen!");
		}

		readerIsNotified = true;
		this.notifyAll();
		
		
	}
	
	public synchronized void write(byte[] in) throws ChannelClosedException {

		if (in.length > bufferSize) {
			throw new IllegalArgumentException(
				"in.length > bufferSize; bufferSize = " + bufferSize);
		}
		
		write(in, in.length);
	}
	



}
