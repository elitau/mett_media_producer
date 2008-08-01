package multimonster.systemadministration;

import java.util.HashMap;

import org.apache.log4j.Logger;

import multimonster.common.BiHashMap;
import multimonster.common.setting.Setting;
import multimonster.common.setting.SettingID;
import multimonster.common.setting.SettingValue;
import multimonster.exceptions.MultiMonsterException;

/**
 * Dient als lokaler Proxy f�r Einstellungen die von der Systemadministration
 * zentral verwaltet werden.
 * 
 * @author Marc Iseler
 */
public class SettingProxy {

	
	/**
	 * Enth�lt ein Prefix, das jedes Setting von dieser Komponente
	 * als Anfang seiner SettingIDs erh�lt
	 * somit k�nnen Settings aus der DB einer Komponente zugeordnet werden
	 */
	private short localprefix = 0;
	
	/**
	 * lokaler Puffer f�r alle SettingValues 
	 * die �ber diesen Proxy eingef�gt wurden
	 * Key:		SettingName
	 * Wert: 	SettingValue
	 */
	private HashMap localValueBuffer = null;
	
	/**
	 * lokaler Puffer f�r die SettingIDs
	 * Key:		SettingID
	 * Wert:	SettingName
	 */
	private BiHashMap localKeyBuffer = null;
	
	
	private Logger log;
	
	/**
	 * h�lt alle existierenden SettingProxy-Instanzen
	 */
	private static HashMap allproxies = new HashMap();
	
	
	/**
	 * erzeugt einen Setting Proxy
	 * es muss die Klasse mitgegeben werden f�r die der Proxy erzeugt wird
	 */
	private SettingProxy(Class classname) throws MultiMonsterException{
		super();
		log = Logger.getLogger(this.getClass());
		localValueBuffer = new HashMap();
		localKeyBuffer = new BiHashMap();
		this.setUp(classname);
	}
	
	/**
	 * Muss anstatt des Konstruktors verwendet werden.
	 * F�r jeden Klassennamen wird eine Instanz des SettingProxy vorgehalten.
	 * 
	 * @param classname
	 * @throws MultiMonsterException
	 */
	public static SettingProxy getInstance(Class type) throws MultiMonsterException {
		SettingProxy retProxy = (SettingProxy) allproxies.get(type);
		
		if (retProxy == null) {
			// Proxy-Instanz f�r diese Klasse existiert noch nicht
			retProxy = new SettingProxy(type);
		}
		return retProxy;
	}
	
	/**
	 * �bernimmt das Setup f�r die Instanz des Proxies
	 * Er holt aus der DB f�r einen Klassennamen ein Prefix
	 * welches f�r die Komponente eindeutig ist.
	 * Desweiteren werden alle in der Datenbank sich befindlichen
	 * Settings f�r diesen Prefix wieder geholt
	 * 
	 * @param classname
	 */
	public void setUp(Class klasse) throws MultiMonsterException {
		if (klasse == null) {
			//TODO Change Exception Type
			throw new MultiMonsterException("Proxy set up failed for class = null!");
		}
		
		//log.debug("Klasse heisst: " + klasse.getName());
		
		//checken ob schon mal setUp durchgef�hrt wurde
		if (localprefix != 0) {
			return;
		}
		
		//f�r den Klassennamen den short-Wert aus der DB holen
		SettingAdministration setAdmin = new SettingAdministration();
		short tmpPrefix = setAdmin.getPrefix(klasse.getName());
		
		if (tmpPrefix != -1) {
			this.localprefix = tmpPrefix;
		} else {
			log.error("Proxy set up failed for classname: " + klasse.getName());
			throw new MultiMonsterException("Proxy set up failed!");
		}
		
		// den SettingProxy bei der SettingAdministration registrieren
		setAdmin.registerSettingProxy(this.localprefix, this);
		
		// alle schon existierenden Settings holen
		Setting[] allSettings = setAdmin.getAllSettings(this.localprefix);
		for(int i = 0; i < allSettings.length; i++) {
			String name = allSettings[i].getName();
			int setID = allSettings[i].getId().getId();
			SettingValue setValue = allSettings[i].getValue();
			
			this.localValueBuffer.put(name, setValue);
			this.localKeyBuffer.put(new Integer(setID), name);
		}
	}
	
	/**
	 * Meldet den Proxy von der SystemAdministration ab, ohne die SettingValues aus
	 * der DB zu l�schen.
	 * TODO
	 * Beim n�chsten anmelden erh�lt das Proxyobjekt die persistenten Werte aus der
	 * DB zugewiesen. 
	 *
	 */
	public void releaseProxy() {
	}

	/**
	 * Meldet den Proxy von der SystemAdministration ab und l�scht alle Werte
	 * aus der DB
	 *
	 */
	public void removeProxy() {
	}
	
	/**
	 * Registriert �bergebenes Setting in der DB.
	 * 
	 * SettingID kann gef�llt sein wird aber ignoriert.
	 * Setting-Name, Setting-Value, Setting-Domain, Setting-Description m�ssen gef�llt sein.
	 * Setting-Name muss zudem innerhalb der Komponente eindeutig sein.
	 * 
	 * @param setting
	 */
	public void registerSetting(Setting setting){
		SettingID setID = null;
		
		// pr�fen ob Setting mit diesem Namen schon existiert
		String setName = setting.getName();
		if (localValueBuffer.containsKey(setName)) {
			log.debug("Setting mit diesem Namen existiert bereits!");
			return;
		}
			
		//TODO Inhalt von Setting pr�fen
		
		SettingAdministration setAdmin = new SettingAdministration();
		
		// localPrefix in SettingID einf�gen
		setID = new SettingID(this.localprefix, 0);
		setting.setId(setID);
		
		// in die DB einbringen
		setID = setAdmin.registerSetting(setting);
		
		// in lokale Datenverwaltung einf�gen
		localValueBuffer.put(setting.getName(), setting.getValue());
		localKeyBuffer.put(new Integer(setID.getId()), setting.getName());
		
	}
	
	/**
	 * entfernt ein Setting aus dem Proxy und aus der Datenbank
	 * 
	 * @param name
	 */
	public void removeSetting(String name) {
		// aus der Datenbank entfernen
		SettingAdministration setAdmin = new SettingAdministration();
		SettingID setID = (SettingID) localKeyBuffer.getByName(name);
		setAdmin.removeSetting(setID);
		
		// aus den lokalen Puffern l�schen
		localValueBuffer.remove(name);
		localKeyBuffer.remove(setID);
	}
	
	/**
	 * Liefert aktuellen Wert dieses Settings.
	 * 
	 * Name referenziert Eindeutig ein Setting.
	 * 
	 * @param name
	 * @return
	 */
	public SettingValue getValue(String name) {
		SettingValue value = null;
		// aus lokalem Puffer holen
		value = (SettingValue) localValueBuffer.get(name);
		return value;
	}
	
	/**
	 * Aktualisiert den Wert eines Settings
	 * wird nur vom SettingChangeController aufgerufen
	 * @param setID
	 * @param value
	 */
	public void updateValue(SettingID setID, SettingValue value) {
		log.debug("updateValue called.");
		String name = null;
		
		// Pr�fen ob SettingID wirklich zu diesem SettingProxy geh�rt
		if (setID.getPrefix() != this.localprefix) {
			log.error("Prefix passt nicht zu diesem Proxy!");
		}
		
		// Aus bidirektionaler Hashmap �ber 
		// die SettingID den SettingName ermitteln
		name = (String) localKeyBuffer.getByKey(new Integer(setID.getId()));
		
		// den Wert updaten
		localValueBuffer.put(name, value);
		log.debug("New Value for Variable " + name + " received: " + value.getValueCont());
	}

}
