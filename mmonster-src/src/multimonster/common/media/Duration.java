/* Generated by Together */

package multimonster.common.media;

import java.io.Serializable;

public class Duration  implements Serializable{
	private int seconds;
	private int minutes;
	private int hours;
	
	
	public Duration(long seconds){
		this.hours = (int) seconds / 3600;
		this.minutes = (int) (seconds % 3600) / 60;
		this.seconds = (int) ((seconds % 3600) % 60);
	}
	
	public Duration(int hours, int minutes, int seconds) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}
	
	public Duration(String hhmmss) {
		this.hours = Integer.parseInt(hhmmss.substring(0,2));
		this.minutes = Integer.parseInt(hhmmss.substring(3,5));
		this.seconds = Integer.parseInt(hhmmss.substring(6,8));
	}
	
	public String getDuration() {
		String ret = hours + ":" + minutes + ":" + seconds;
		return ret;
	}
	/**
	 * @return
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * @return
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * @return
	 */
	public int getSeconds() {
		return seconds;
	}

}