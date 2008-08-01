package multimonster.common.plugin;

import multimonster.common.setting.SettingListener;

/**
 * In normal cases the managing class of an component, like
 * resourcemanager or transporter, implements the <code>Pluggable</code>
 * interface, so the component is enabled to be extended with plugins
 * 
 * @author Holger Velke (sihovelk)
 */
public interface Pluggable extends SettingListener {
    
	/**
	 * addPlugin is called when an new PlugIn is inserted in
	 * the system or if a released plugin in reattached.
	 * 
	 * @param plugInInformation containing the plugin-specific
	 * 		information and the plugin-id
	 */
	void addPlugIn(PlugInInformation plugInInformation);

    /**
     * releasePlugIn is called if a PlugIn is removed but the settings
     * of the plugin are keept in the system.
     * 
     * @param plugInId the ID of the released plugin
     */
    void releasePlugIn(PlugInIdentifier plugInId);

    /**
     * removePlugIn is called if a plugin is removed and the settings
     * of the plugin are not keept in the system.
     * 
     * @param plugInId the ID of the removed plugin
     */
    void removePlugIn(PlugInIdentifier plugInId);
}
