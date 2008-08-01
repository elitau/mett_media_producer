package multimonster.converter;

import multimonster.common.*;
import multimonster.common.media.*;
import multimonster.common.pipe.*;
import multimonster.converter.exceptions.ConverterException;

/**
 * This is the interface of the facade of the conveter component.
 * The funktionality of the converter should only be accessed using this
 * interface.<br>
 * The converter component offers format-transparent access to the data of the
 * mediaaccess component. It offers this using the conversion funktionality
 * provided by plugins. The converter without plugins is only able to deliver
 * the data from the mediaaccess component. In this case no conversion is 
 * availible.<br>
 * The conversion possibilities are managed by the systemadministration
 * component.<br>
 * <br>
 * The converter component needs:<br>
 * - mediadata component<br>
 * - systemadministration<br>
 * 
 * @author Holger Velke (sihovelk)
 */
public interface ConverterFacade {
	
	/**
	 * This causes the converter to deliver mediainstance-data of the requested
	 * mediaobject in the requested format. If the converter is unable to 
	 * provide the potential conversion a <code>ConverterException</code> is 
	 * thrown. In normal cases a Pipe delivering the requested data is returned.
	 * 
	 * @param mOId The id of the mediaobject
	 * @param fId The id of the format
	 * @return a <code>Pipe</code> delivering the requested data
	 * @throws ConverterException If unable to do conversion or if an error 
	 * 	occours setting up the conversion.
	 */
	Pipe getMediaInstance(MOIdentifier mOId, FormatId fId)
		throws ConverterException;
	
	/**
	 * The converter returns a Pipe containing the source mediainstance-data
	 * of a media object. (The source mediainstance-data is the data of the
	 * mediainstance initially put in the mediaaccess) In this case the format
	 * of the deliverd data depends on the format of the mediainstance initially
	 * put in the system.
	 * 
	 * @param mOId The id of the mediaobject
	 * @return a <code>Pipe</code>
	 * @throws ConverterException If an erro occours when accessing mediaaccess
	 */
	Pipe getSourceMediaInstance(MOIdentifier mOId) throws ConverterException;
	
	/**
	 * Inserts the mediainstance data delivered by the <code>Pipe</code> and
	 * adds it as a mediainstance of the mediaobject specified. Additionally
	 * the Converter extracts meta-data of the mediainstance.
	 * 
	 * @param mOId The id of the mediaobject
	 * @param inputPipe the pipe delivering the mediainstance-data
	 * @throws ConverterException if an error occours setting up the add-process
	 */
	void addMediaObject(MOIdentifier mOId, Pipe inputPipe)
		throws ConverterException;
	
	/**
	 * This removes the specified mediainstance form the mediaaccess component.
	 * 
	 * @param mIId The id of the mediainstance to remove.
	 * @return
	 * @throws ConverterException
	 */
	boolean removeMediaInstance(MIIdentifier mIId) throws ConverterException;
}
