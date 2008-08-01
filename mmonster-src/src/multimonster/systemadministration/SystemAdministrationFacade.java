package multimonster.systemadministration;
import multimonster.common.*;
import multimonster.common.edit.*;
import multimonster.common.media.*;
import multimonster.common.plugin.*;
import multimonster.common.resource.*;
import multimonster.common.setting.*;
import multimonster.exceptions.DBNotAvailableException;
import multimonster.exceptions.MultiMonsterException;
import multimonster.systemadministration.exceptions.SettingNotExistsException;
import multimonster.systemadministration.exceptions.SettingOutOfDomainException;

/**
 * This is the interface for the System-component SystemAdministration
 * All other system-components should use only this interface when accessing Systemadministration
 * 
 * @author Marc Iseler
 */
public interface SystemAdministrationFacade {

	//Settings
	/**
	 * @param settingID
	 * Returns the human readable information what the effect of
	 * this setting is
	 * This should be used for listing all registered settings
	 */
	String getSettingDescription(SettingID settingID)
		throws SettingNotExistsException;

	/**
	 * Inserts a new setting into Systemadministration
	 * in order to do so, you have to deliver complete set of setting information
	 * @param setting
	 */
	void registerSetting(Setting setting);

	/**
	 * Removes a setting from Systemadministration
	 * @param settingId
	 */
	void releaseSetting(SettingID settingId);

	/**
	 * changes the value of a setting
	 * the new value must fit into the ValueDomain
	 * 
	 * @param settingID
	 * @param value
	 * @throws SettingNotExistsException
	 * @throws SettingOutOfDomainException
	 */
	void setSettingValue(SettingID settingID, SettingValue value)
		throws SettingNotExistsException, SettingOutOfDomainException;

	/**
	 * returns the current value of the specified setting
	 * 
	 * @param settingID
	 * @return
	 * @throws SettingNotExistsException
	 */
	SettingValue getSettingValue(SettingID settingID)
		throws SettingNotExistsException;
	
	/**
	 * returs all existing settings for administrativ GUI
	 * @return
	 */
	Setting[] getAllSettings();

	//MediaObject
	
	/**
	 * inserts a new MediaObject to the system
	 */
	MOIdentifier addMediaObject(MediaObject mediaObject, UserIdentifier user)
		throws MultiMonsterException;

	/**
	 * removes an existing MediaObject
	 * @param id
	 * @param user
	 * @throws MultiMonsterException
	 */
	void remMediaObject(MOIdentifier id, UserIdentifier user)
		throws MultiMonsterException;

	/**
	 * changes the metadata for the specified MediaObject
	 * doesn't provide a new MOIdentifier 
	 */
	void modifyMediaObject(MediaObject mediaObject, UserIdentifier user);

	/**
	 * returns a MediaInstance for a specified MediaObject and a specified format
	 * if such a MediaInstance exists
	 * @param mOId
	 * @param fId
	 * @return
	 * @throws MultiMonsterException
	 */
	MediaInstance getMediaInstance(MOIdentifier mOId, FormatId fId)
		throws MultiMonsterException;

	/**
	 * returns the initially inserted Mediainstance for a MediaObject
	 * @param mOId
	 * @return
	 * @throws MultiMonsterException
	 */
	MIIdentifier getSourceMediaInstance(MOIdentifier mOId)
		throws MultiMonsterException;

	/**
	 * adds a Mediainstance with certain format to a MediaObject
	 * @param instance
	 * @param meta
	 * @throws MultiMonsterException
	 */
	void addMediaInstance(MediaInstance instance, MetaData meta)
		throws MultiMonsterException;

	/**
	 * removes an existing MediaInstance
	 * @param id
	 */
	void remMediaInstance(MIIdentifier id);

	//Format and Meta
	
	
	/**
	 * returns a list of possible Input Options which are
	 * available for this user
	 */
	InputOption[] getInputOptions(UserIdentifier user);
	
	/**
	 * returns a list of possible Output Options for a specified user
	 * which the system can offer at that point of time
	 * 
	 * output options are tuples of protocol and format
	 */
	OutputOption[] getOutputOptions(
		UserIdentifier user,
		MOIdentifier mediaObject);

	/**
	 * resolves FormatID to complete specified Format-Object
	 * 
	 * @param fId
	 * @return
	 */
	Format getFormat(FormatId fId);

	/**
	 * retruns a list of possible Filters that can be used  by the
	 * specified user manipulating the specified MediaObject
	 * 
	 * returns An Arry of filterDetails describing the Filters
	 */
	FilterDetail[] getFilterOptions(UserIdentifier uId, MOIdentifier mOId);

	/**
	 * returns all available metadata for a specified MediaObject
	 * 
	 * @param mOId
	 * @return
	 */
	MetaDataAccess getMetaData(MOIdentifier mOId);

	/**
	 * inserts a new Plugin into the system
	 * all necessary information must be contained in reginfo
	 * @param regInfo
	 */
	void registerPlugin(PlugInInformation regInfo);

	/**
	 * removes Plugin from the system, but doesn't remove the registered settings for this plugin
	 * @param pluginID
	 */
	void releasePlugin(PlugInIdentifier pluginID);

	/**
	 * removes the specified plugin with all its settings
	 * @param pluginID
	 */
	void removePlugin(PlugInIdentifier pluginID);


	//Search
	
	/**
	 * provides search within all MediaObjects 
	 */
	SearchResult[] search(SearchCriteria criteria)
		throws DBNotAvailableException;

	//RessourceControl
	/**
	 * calculates estimated resource consumption for an editing job
	 * for a specified Mediaobject and a specified filter-queue
	 */
	Costs calculateCosts(MOIdentifier id, FilterAction[] actions);
	
	/**
	 * 
	 * @param mOId
	 * @param format
	 * @param protocol
	 * @param action
	 * @return
	 */
	Costs calculateCosts(MOIdentifier mOId, FormatId formatId, ProtocolId protocolId, Action action);

	/**
	 * 
	 * @param mOId
	 * @param protocol
	 * @param action
	 * @return
	 */
	Costs calculateCosts(MOIdentifier mOId, ProtocolId protocolId, Action action);
	
	/**
	 * for improvement of estimated ressource consumption after an editing job
	 * the measured actual ressource consumption of this job is handed in after the job has been finished
	 * @param id
	 * @param actions
	 * @param realcosts
	 */
	void realCosts(MOIdentifier id, FilterAction[] actions, Costs realcosts);

	/**
	 * identifies the appropriate ConverterPlugin for given input and output Format 
	 * @param input
	 * @param output
	 * @return
	 */
	PlugInIdentifier getConverterPlugInId(Format input, Format output);
	
	/**
	 * identifies the appropriate ProxyPlugin for the specified ProtocolID and in dependency
	 * whether its for output or input
	 * @param protID
	 * @param isInput
	 * @return
	 */
	PlugInIdentifier getProxyPlugInId(ProtocolId protID, boolean isInput);
	
	/**
	 * identifies the appropriate TransporterPlugin taking respect of protocol and
	 * input or output
	 * @param protID
	 * @param isInput
	 * @return
	 */
	PlugInIdentifier getTransporterPlugInId(ProtocolId protID, boolean isInput);
}
