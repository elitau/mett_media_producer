package multimonster.converter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import multimonster.common.Format;
import multimonster.common.plugin.PlugInFactory;
import multimonster.common.plugin.PlugInIdentifier;
import multimonster.common.util.EjbCreator;
import multimonster.common.util.EjbHomeGetter;
import multimonster.converter.exceptions.ConverterException;
import multimonster.exceptions.PlugInInstantiationException;
import multimonster.systemadministration.interfaces.SystemAdministrationImpl;
import multimonster.systemadministration.interfaces.SystemAdministrationImplHome;

/**
 * The ConverterPlugIn Factory instanciates the fitting converter plugins for a
 * specific conversion.
 * 
 * @author Holger Velke (sihovelk)
 */
class ConverterPlugInFactory extends PlugInFactory {

	private Logger log;
	
	private SystemAdministrationImplHome systemAdministrationHome;
	private Context context;
	
	protected ConverterPlugInFactory(){
		this.log = Logger.getLogger(this.getClass());
		
		try {
			this.context = new InitialContext();
			systemAdministrationHome =
				EjbHomeGetter.getSystemAdministrationHome(context);
		} catch (NamingException e) {
			log.error(e);
		}
	}

    /**
     * converter plugin factory is a singleton, use this to get the instance.
     * 
     * @return the converter plugin factory
     */
    public static ConverterPlugInFactory getInstance(){
            if (instance == null) {
                synchronized(ConverterPlugInFactory.class) {
                    if (instance == null) {
                        instance = new ConverterPlugInFactory();
                    }
                }
            }
            return instance;
        }

    /**
     * @param input the input format
     * @param output the output format
     * @return a <code>ConverterPlugIn</code> the is able to convert form
     * <code>input</code> to <code>output</code>
     * @throws ConverterException 
     */
    public ConverterPlugIn getConverterPlugIn(Format input, Format output) throws ConverterException {
    	
    	PlugInIdentifier plugInId = null;
    	ConverterPlugIn converterPlugIn = null;
    	SystemAdministrationImpl systemAdministration = null;
    
    	log.debug("getConverterPlugIn()");
    	
    	try {
			systemAdministration =
				EjbCreator.createSystemAdministration(
					systemAdministrationHome,
					context);
			plugInId = systemAdministration.getConverterPlugInId(input, output);
			systemAdministration.remove();
		} catch (Exception e) {
			throw new ConverterException(
				"problem getting the PlugInIdentifier from SystemAdministration",
				e);
		}
    	
    	try {
    		converterPlugIn =(ConverterPlugIn) this.getPlugIn(plugInId);
    	} catch (ClassCastException e){
    		String errorMsg = "unable to cast " + plugInId.getClassName();
    		errorMsg += " as multimonster.converter.ConverterPlugIn";
    		errorMsg += " - no conversion available!";
    		throw new ConverterException(errorMsg, e);
    	} catch (PlugInInstantiationException e) {
    		String errorMsg = "unable to instanciate " + plugInId.getClassName();
    		errorMsg += " - no conversion available!";
    		throw new ConverterException(errorMsg, e);
    	}
    	
    	converterPlugIn.init(input, output);
    	
		return converterPlugIn;
    }

    /**
     * @link
     * @shapeType PatternLink
     * @pattern Singleton
     * @supplierRole Singleton factory 
     */
    /*# private ConverterPlugInFactory _converterPlugInFactory; */
    private static ConverterPlugInFactory instance = null;

    /** @link dependency 
     * @clientRole Factory
     * @supplierRole Product
     * @stereotype instantiate*/
    /*# ConverterPlugIn lnkConverterPlugIn; */
}
