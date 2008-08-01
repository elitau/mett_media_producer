package multimonster.common.plugin;

import java.io.Serializable;

import multimonster.common.setting.Setting;


/**
 * The <code>PlugInInformation</code> class contains the information needed
 * by the component. This information is additional infomation that has to
 * be profided by the plugin when it is added to the system.
 * 
 * @author Holger Velke (sihovelk)
 */
public class PlugInInformation implements Serializable{

	private PlugInIdentifier id;
	private String name;
	private String description;
	private Setting [] settings;

    /** 
     * The class can only be instaciated if all required information is available.
     * @link dependency 
     * @stereotype information
     * */
    /*# PlugIn lnkPlugIn; */
	
	public PlugInInformation(PlugInIdentifier id, String name, String description, Setting [] settings){
		this.id = id;
		this.name = name;
		this.description = description;
		this.settings = settings;
	}
		
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the id.
	 */
	public PlugInIdentifier getId() {
		return id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the settings.
	 */
	public Setting[] getSettings() {
		return settings;
	}
}
