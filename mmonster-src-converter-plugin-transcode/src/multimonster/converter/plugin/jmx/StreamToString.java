package multimonster.converter.plugin.jmx;

import java.io.InputStream;

/**
 * @author Holger Velke
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class StreamToString implements Runnable {

	private InputStream in;
	private String string = null;
	private boolean finished = false;

	public StreamToString(InputStream in) {
		
		if (in != null){
			this.in = in;
		} else {
			throw new IllegalArgumentException("InputStream is null");
		}
		this.string = "";
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		byte[] buf = new byte[1024];
		
		try {
			int read = 0;
			while (true) {
				read = in.read(buf);
				if (-1 == read)
					break;
				string += new String(buf, 0, read);
			}
		} catch (Exception e) {
		}

		finished = true;
	}

	/**
	 * @return Returns the finished.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @return Returns the string.
	 */
	public String getString() {
		return string;
	}

}
