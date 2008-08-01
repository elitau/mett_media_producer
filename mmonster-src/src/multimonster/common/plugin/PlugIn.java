package multimonster.common.plugin;

/**
 * Each specific PlugIn in the system has to extend this
 * abstract class.
 *  
 * @author Holger Velke (sihovelk)
 */
public abstract class PlugIn {
    
    /**
     * The standard-constructor is needed by the PlugInFactory.
     * In noraml case a PlugIn is instanciated by it's 
     * standart-constructor and initialised afertwards.
     */
    public PlugIn(){
    }
    
}
