package multimonster.common.setting;

import java.io.Serializable;

public class SettingID implements Serializable{
    private int id;
    private short prefix;
    
    public SettingID (int id) {
    	this.id = id;
    }
    
    public SettingID (short prefix, int id) {
    	this.id = id;
    	this.prefix = prefix;
    }
	/**
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	public short getPrefix() {
		return prefix;
	}

	/**
	 * @param s
	 */
	public void setPrefix(short s) {
		prefix = s;
	}

}
