package multimonster.common.plugin;

import java.io.Serializable;

/**
 * A PlugInIdentifier is an explicit identifier for a PlugInSubClass 
 */
public class PlugInIdentifier implements Serializable{
    
	private String className;
		
	/**
	 * The PlugInIdentifier uses the classname to
	 * exactly identify a specific PlugIn 
	 * 
	 * @param className The classname of the plugin.
	 */
	public PlugInIdentifier(String className){
		this.className = className;
	}
    
	/**
	 * @return the classname of the plugin
	 */
	public String getClassName(){
		return this.className;
	}
	
    /** @link dependency 
     * @clientRole unique Class-Idetifier*/
    /*# PlugIn lnkPlugIn; */
}
