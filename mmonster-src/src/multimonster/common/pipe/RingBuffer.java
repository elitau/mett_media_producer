package multimonster.common.pipe;

/**
 * Implementation of a RingBuffer.
 * 
 * @author Holger Velke (sihovelk)
 */
class RingBuffer {
/*
 * Let 'size' be the size of the buffer B in bytes.
 * 
 * Let 'inIndex' be an index into the buffer where the producer will store the
 * next new byte of data.
 * 
 * Let 'outIndex' be the index of the next byte that the consumer should remove
 * from the buffer.
 * 
 * Let 'used' be the number of unconsumed bytes in the buffer. Define % as the
 * modulus operator.
 * 
 * Initially, I = O = N = 0.
 * 
 * The buffer is full (has no room for new data) when N == S. The available
 * space (for new data) A = S - N
 */

	private int size; // size of B
	private int inIndex; // input index
	private int outIndex; // output ndex
	private int avalbl; // available (free) space
	private int used; // number of bytes in B

	private byte[] buffer; // the buffer

	/**
	 * Allocate a ring buffer of size s
	 */
	public RingBuffer(int size) {
		this.size = size;
		this.avalbl = size;
		this.used = 0;
		this.inIndex = 0;
		this.outIndex = 0;
		buffer = new byte[size];
	}

	/**
	 * Add m bytes of data from buffer D to the buffer B
	 */
	public void add(byte[] D, int m) throws Exception {

		// Check that m <= A (if not an error has occurred)
		if (m > avalbl)
			throw new Exception("ringBuffer.add(): insufficient room in buffer");

		int j = inIndex;
		inIndex = (inIndex + m) % size;
		used += m;

		for (int q = 0; q < m; q++)
			buffer[(j + q) % size] = D[q];

		// update available space
		avalbl = size - used;

	}

	/**
	 * Get n bytes from the buffer B to buffer 'buf' 
	 */
	public int get(byte[] buf, int n) {

		if (used == 0)
			return 0;

		// Check that r <= N. If not, adjust r (r = N)
		int result = n;
		if (n > used)
			result = used;

		int j = outIndex;
		outIndex = (outIndex + result) % size;
		used -= result;

		try {
			for (int q = 0; q < result; q++)
				buf[q] = buffer[(j + q) % size];
		} catch (Exception x) {
			System.out.println("ringBuffer " + x.toString());
		}

		// update available space
		avalbl = size - used;

		return result;
	}

	/**
	 * Return the number of bytes in the buffer.
	 */
	public int length() {
		return used;
	}
	
	/**
	 * Checks if buffer is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty(){
		if (length() == 0) {
			return true;
		} else { 
			return false;
		}
	}

	/**
	 * @return
	 */
	public int available() {		
		return avalbl;
	}

}
