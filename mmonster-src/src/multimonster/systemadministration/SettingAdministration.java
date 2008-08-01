package multimonster.systemadministration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import multimonster.common.setting.Setting;
import multimonster.common.setting.SettingDomain;
import multimonster.common.setting.SettingID;
import multimonster.common.setting.SettingValue;
import multimonster.common.util.MDBSender;
import multimonster.exceptions.MultiMonsterException;
import multimonster.systemadministration.exceptions.SettingNotExistsException;
import multimonster.systemadministration.exceptions.SettingOutOfDomainException;

public class SettingAdministration {

	private Logger log;
	
	/**
	 * static Variable wird zur Datenhaltung während einer Systeminstanz (von Start bis Herunterfahren)
	 * verwendet
	 * reicht aus, da nur einmal gebraucht und auch nur hier
	 * daher DB überflüssig
	 */
	private static HashMap proxies = new HashMap();

	public SettingAdministration() {
		log = Logger.getLogger(this.getClass());
	}
	
	/**
	 * Registriert ein SettingProxyObjekt und stellt somit die Verbindung zwischen
	 * Prefix und ProxyObjekt her
	 * 
	 * @param prefix das zugeordnete Prefix zur Komponente
	 * @param proxy referenz auf SettingProxyObjekt
	 * @return void
	 */
	public void registerSettingProxy(short prefix, SettingProxy proxy) {
		Short key = new Short(prefix);
		SettingAdministration.proxies.put(key, proxy);
		
//		// QueryManager und Connection holen
//		QueryManager qmnr = new QueryManager();
//		int connNr = qmnr.reserveConnection();
//		
//		String query = "insert into settingProxy values(" + prefix + ", '" + JNDIname + "');";
//		
//		try {
//			qmnr.dbOpInsert(query, connNr);
//		} catch (MultiMonsterException e) {
//			// TODO better Exception handling
//			wasSuccessful = false;
//			e.printStackTrace();
//		}
//		
//		wasSuccessful = true;
//		qmnr.bringBackConn(connNr);
	}
	
	/**
	 * @see SettingAdministration#getAllSettings()
	 * @return
	 */
	public Setting[] getAllSettings() { 
		return getAllSettings((short)0);
	}
	
	/**
	 * Registriert ein SettingProxyObjekt und stellt somit die Verbindung zwischen
	 * Prefix und ProxyObjekt her
	 * 
	 * @param prefix das zugeordnete Prefix zur Komponente
	 * @param proxy referenz auf SettingProxyObjekt
	 * @return void
	 */
	public Setting[] getAllSettings(short prefix) {

		Setting[] allSettings = null;
		
		// QueryManager und Connection holen
		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();
		
		String query = "";
		if (prefix == 0) {
			// alle existierenden setting rausgeben
			query = "select * from setting;";
		} else {
			query = "select * from setting where prefix = " + prefix + ";";
		}
		
		
		ResultSet result = qmnr.dbOpExec(query, connNr);
		
		ArrayList list = new ArrayList();
		
		try {
			while (result.next()) {
				//log.debug("Rows found: " + result.getFetchSize());
				
				// Werte aus dem Resultset holen
				int setid = result.getInt("id");
				String setName = result.getString("name");
				Blob valueBlob = result.getBlob("value");
				SettingValue setValue = (SettingValue) deserialize(valueBlob.getBytes(1, (int) valueBlob.length()));
				Blob domainBlob = result.getBlob("domain");
				SettingDomain setDomain = (SettingDomain) deserialize(domainBlob.getBytes(1, (int) domainBlob.length()));
				String descr = result.getString("description");
				
				Setting setting = new Setting(setName, setValue, setDomain, descr);
				setting.setId(new SettingID(setid));
				list.add(setting);
			} 
		} catch (SQLException e) {
			log.error("Fehler bei getAllSettings");
		}
		
		allSettings = new Setting[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			allSettings[i] = (Setting) list.get(i);
		}
		return allSettings;

	}
	
	/**
	 * entfernt ein zuvor registriertes ProxyObjekt aus der DB
	 * 
	 * @param prefix eindeutiger Zugriff auf ein ProxyObjekt
	 * @return void
	 */
	public void removeSettingProxy(short prefix) {
		
		
		Short key = new Short(prefix);
		SettingAdministration.proxies.remove(key);
		
//		// 	QueryManager und Connection holen
//		QueryManager qmnr = new QueryManager();
//		int connNr = qmnr.reserveConnection();
//		
//		String query = "delete from settingProxy where prefix = " + prefix + ";";
//		
//		try {
//			qmnr.dbOpInsert(query, connNr);
//		} catch (MultiMonsterException e) {
//			// TODO better exception handling
//			e.printStackTrace();
//			wasSuccessful = false;
//		}
//		
//		wasSuccessful = true;
//		qmnr.bringBackConn(connNr);
	
	}
	
	/**
	 * Gibt zu einem spezifizierten prefix die Referenz auf das zugehörige ProxyObjekt zurück
	 * @param prefix
	 * @return
	 */
	public SettingProxy getSettingProxy(short prefix) {
		SettingProxy proxy = null;
		
		// aus dem Prefix den Key machen
		Short key = new Short(prefix);
		
		// ProxyReferenz explizit casten
		proxy = (SettingProxy) SettingAdministration.proxies.get(key);
		
		return proxy;
		
//		String JNDIname = null;
//		
//		//	QueryManager und Connection holen
//		QueryManager qmnr = new QueryManager();
//		int connNr = qmnr.reserveConnection();
//		
//		String query = "select JNDIname from settingProxy where prefix = " + prefix + ";";
//		
//		ResultSet result = null;
//		result = qmnr.dbOpExec(query, connNr);
//		
//		try {
//			if (result.next()) {
//				JNDIname = result.getString(1);
//			}
//		} catch (SQLException e1) {
//			log.error("Fehler beim Holen des JNDIname für prefix " + prefix);
//		}
//		
//		return JNDIname;
	}

	/**
	 * Fügt ein Setting in die DB ein. Vergibt für jedes neue Setting eine neue ID.
	 * Diese SettingID wird als Returnwert zurückgeliefert.
	 * 
	 * @param setting
	 * @return SettingID
	 */
	public SettingID registerSetting(Setting setting) {

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		// build statement
		String query = buildStatement_insertSetting(setting);

		// build BLOBs
		byte[] ba1 = serialize(setting.getValue());
		byte[] ba2 = serialize(setting.getDomain());

		// execute statement
		try {
			qmnr.dbOpInsertBLOB(query, ba1, ba2, connNr);
		} catch (MultiMonsterException e) {
			log.error("Fehler beim einfügen von Setting in DB!");
		}
		ResultSet result = null;
		result = qmnr.dbOpExec("SELECT LAST_INSERT_ID();", connNr);

		int settingID = 0;

		try {
			if (result.next()) {
				settingID = result.getInt(1);
				//log.debug("Setting ID vergeben: " + settingID);
			}
		} catch (SQLException e1) {
			log.error("Error getting last insert id!");
		}

		SettingID setID = new SettingID(settingID);

		log.debug("Setting inserted in DB with ID=" + settingID);
		qmnr.bringBackConn(connNr);

		return setID;
	}

	public void releaseSetting(SettingID settingId) {
	}
	
	public void removeSetting(SettingID settingID) {
	}

	/**
	 * Setzt den Wert eines Settings neu.
	 * 
	 * @param settingID
	 * @param value
	 */
	public void setValue(SettingID settingID, SettingValue value)
		throws SettingNotExistsException, SettingOutOfDomainException {

		if (settingID == null || value == null) {
			log.error("Einer der Parameter war null!");
			return;
		}

		//		Prüfen ob Setting überhaupt existiert
		if (checkSettingExistence(settingID) == false) {
			log.error("Setting mit dieser SettingID existiert nicht!");
			throw new SettingNotExistsException("SettingID existiert nicht!");
		}

		// Prüfen ob Value innerhalb der SettingDomain liegt
		if (checkValueDomain(settingID, value) == false) {
			log.error("SettingValue liegt ausserhalb des Domain-Bereichs!");
			throw new SettingOutOfDomainException();
		}

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		byte[] ba = serialize(value);

		// SettingId herausholen
		int setID = settingID.getId();

		String query = "update setting set value = ? where id = " + setID + ";";

		try {
			qmnr.dbOpUpdateBLOB(query, ba, connNr);
		} catch (MultiMonsterException e) {
			log.error("Fehler beim Update der Value mit id = " + setID);
		}

		qmnr.bringBackConn(connNr);

		//SettingChangeController asynchron aufrufen
		try {
			MDBSender.sendObjectMessage(
				SettingChangeControllerBean.JMS_QUEUE_NAME,
				settingID);
		} catch (NamingException e) {
			log.error(e);
		} catch (JMSException e) {
			log.error(e);
		}

	}

	/**
	 * Die Methode gibt den Wert zu einer SettingID heraus.
	 * 
	 * @param settingID
	 * @return SettingValue (Error => null) 
	 */
	public SettingValue getValue(SettingID settingID)
		throws SettingNotExistsException {

		if (settingID == null) {
			log.error("getValue mit Parameter = null aufgerufen!");
			return null;
		}

		//SettingValue val = null;
		Object obj = null;
		SettingValue setval = null;
		int setID = settingID.getId();

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		// build statement
		String query = "select value from setting where id = " + setID + ";";

		ResultSet result = qmnr.dbOpExec(query, connNr);

		try {
			if (result.next()) {
				Blob blob = result.getBlob(1);
				int bloblength = (int) blob.length();
				byte[] ba = blob.getBytes(1, bloblength);
				obj = deserialize(ba);
			} else {
				log.error("Setting mit dieser SettingID existiert nicht!");
				throw new SettingNotExistsException();
			}
		} catch (SQLException e) {
			log.error("Fehler bei getValue: Wert auslesen aus setting");
		}

		if (obj != null) {
			//log.debug("Try to cast to SettingValue");
			try {
				setval = (SettingValue) obj;
			} catch (ClassCastException cce) {
				log.error(
					"Fehler beim Lesen von SettingValue aus DB mit id="
						+ setID);
				return null;
			}
			//log.debug("Cast erfolgreich abgeschlossen.");
		} else {
			log.warn("Deserialisiertes Object aus DB ist null für id=" + setID);
			throw new SettingNotExistsException();
		}

		qmnr.bringBackConn(connNr);

		return setval;
	}

	/**
	 * returns the human readable description for the given settingID
	 * @param settingID
	 * @return
	 * @throws SettingNotExistsException
	 */
	public String getDescription(SettingID settingID)
		throws SettingNotExistsException {
		String descr = null;

		int setID = settingID.getId();

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		// build statement
		String query =
			"select description from setting where id = " + setID + ";";

		ResultSet result = qmnr.dbOpExec(query, connNr);

		try {
			if (result.next()) {
				descr = result.getString(1);
			} else {
				log.error("Setting mit id = " + setID + " existiert nicht!");
				throw new SettingNotExistsException(
					"Setting mit id = " + setID + " existiert nicht!");
			}
		} catch (SQLException e) {
			log.error("Fehler bei checkExistence: DB Zugriff!");
		}

		qmnr.bringBackConn(connNr);

		return descr;

	}

	/**
	 * Bildet ein Mapping zwischen Klassennamen und eindeutigen
	 * Prefixes für die Settings
	 * Jedes Setting bekommt ein sogenanntes Prefix zugewiesen,
	 * was die Zuordnung eines Settings zu einer Komponente erlaubt
	 * 
	 * @param classname
	 * @return
	 */
	public short getPrefix(String classname) {

		short prefix = -1;

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		// build statement
		String query =
			"select prefix from componentprefix where classname = '"
				+ classname
				+ "';";

		ResultSet result = qmnr.dbOpExec(query, connNr);

		if (result == null) {
			log.error("Fehlerhafte Query!!!");
			return -1;
		}

		try {
			if (result.next()) {
				prefix = result.getShort("prefix");
				//log.debug("getPrefix(): Prefix ist " + prefix);
			} else {
				// es gibt den Eintrag für diesen classname noch nicht
				// => neuen Vergeben und in die DB eintragen
				prefix = insertNewPrefix(classname);
				log.debug("getPrefix(): Neu vergebener Prefix ist " + prefix);
			}
		} catch (SQLException e) {
			log.error("Fehler bei getPrefix: lesen aus der DB!");
			return -1;
		} catch (MultiMonsterException e) {
			log.error("Fehler beim neu Eintragen in die DB!");
			return -1;
		}

		qmnr.bringBackConn(connNr);

		return prefix;
	}
	
	
	/**
	 * returns the appropriate prefix for a given SettingID 
	 * @param classname
	 * @return
	 */
	public short getPrefix(SettingID setID) {

		short prefix = 0;
		int settingID = 0;
		
		settingID = setID.getId();
		
		if (settingID == 0) {
			return prefix;
		}

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		// build statement
		String query =
			"select prefix from setting where id = '"
				+ settingID
				+ "';";

		ResultSet result = qmnr.dbOpExec(query, connNr);

		if (result == null) {
			log.error("Fehlerhafte Query!!!");
			return -1;
		}

		try {
			if (result.next()) {
				prefix = result.getShort("prefix");
				log.debug("getPrefix(): Prefix ist " + prefix);
			} else {
				return prefix;
			}
		} catch (SQLException e) {
			log.error("Fehler bei getPrefix: lesen aus der DB!");
			return -1;
		}

		qmnr.bringBackConn(connNr);

		return prefix;
	}

	/**
	 * return the associated Domain for a given setting
	 * @param settingID
	 * @return
	 * @throws SettingNotExistsException
	 */
	public SettingDomain getSettingDomain(SettingID settingID)
		throws SettingNotExistsException {
		SettingDomain domain = null;
		Object obj = null;

		int setID = settingID.getId();

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		// build statement
		String query = "select domain from setting where id = " + setID + ";";

		ResultSet result = qmnr.dbOpExec(query, connNr);

		try {
			if (result.next()) {
				Blob blob = result.getBlob(1);
				int bloblength = (int) blob.length();
				byte[] ba = blob.getBytes(1, bloblength);
				obj = deserialize(ba);
			} else {
				log.error("Setting mit dieser SettingID existiert nicht!");
				throw new SettingNotExistsException(
					"Setting mit ID = " + setID + " existiert nicht!");
			}
		} catch (SQLException e) {
			log.error("Fehler bei checkExistence: DB Zugriff!");
		}

		if (obj != null) {
			try {
				domain = (SettingDomain) obj;
			} catch (ClassCastException cce) {
				log.error("getSettingDomain(): Can't cast to SettingDomain");
			}
		} else {
			log.error("Deserialisieren des SettingDomain nicht möglich!");
		}

		qmnr.bringBackConn(connNr);

		return domain;

	}

	//******* Helper Methods ****************/

	private String buildStatement_insertSetting(Setting setting) {
		String query = null;

		query =
			"insert into setting values(null, " + setting.getId().getPrefix() + " , '"
				+ setting.getName()
				+ "', ?, ?, '"
				+ setting.getDescription()
				+ "');";

		//log.debug("Query for inserting setting: " + query);

		return query;
	}

	private byte[] serialize(Object object) {
		byte[] buf = null;
		String serObject = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput objout = new ObjectOutputStream(bos);
			//log.debug("serialize: Outputstreams are set up");
			objout.writeObject(object);
			//log.debug("serialize: Object was written into stream");
			objout.close();
			//log.debug("serialize: stream was closed");

			// write to byteArray
			buf = bos.toByteArray();
			//log.debug("serialized Object: " + buf);

			//			// bring byte[] to String to use it with Database
			//			StringBuffer intermediate = new StringBuffer();
			//			for (int i = 0; i < buf.length; i++) {
			//				intermediate.append((char) buf[i]);
			//			}
			//			serObject = intermediate.toString();
			//			log.debug("serialize(): serialized Object: " + serObject);

		} catch (IOException e) {
			log.error("Fehler beim serialisieren eines setting-Objekts!");
		}
		return buf;
	}

	private Object deserialize(byte[] buf) {
		Object result = null;
		//byte[] buf = null;

		try {
			//			// bring String to byte[] by copying char for char
			//			int bufferlength = serObject.length();
			//			buf = new byte[bufferlength];
			//			for (int j = 0; j < bufferlength; j++) {
			//				buf[j] = (byte) serObject.charAt(j);
			//			}

			ByteArrayInputStream byteIn = new ByteArrayInputStream(buf);
			//log.debug("deserialize: Byteinputstream was set up");

			ObjectInputStream objin = new ObjectInputStream(byteIn);
			//log.debug("deserialize: ObjectInputStream was set up");
			result = objin.readObject();
			//log.debug("deserialize(): Objekt ausgelesen!");
		} catch (IOException e) {
			log.error("deserialize(): Fehler beim Auslesen des Objekts");
		} catch (ClassNotFoundException e) {
			log.error("deserialize(): ClassnotFound Exception");
		}
		return result;
	}

	/**
	 * Vergibt für Klassennamen ein eindeutiges Prefix
	 * und legt es in der DB ab
	 * 
	 * @param classname
	 * @return
	 */
	private short insertNewPrefix(String classname)
		throws MultiMonsterException {

		short prefix = (new Integer(Math.abs(classname.hashCode()))).shortValue();
		
		log.debug("Klasse erzeugt folgenden Hash: " + classname + " " + prefix);

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		String query =
			"insert into componentprefix values('"
				+ classname
				+ "',"
				+ prefix
				+ " );";

		try {
			qmnr.dbOpInsert(query, connNr);
		} catch (MultiMonsterException e) {
			log.error("Fehler beim einfügen in componentprefix!");
			//TODO Change Exception type
			throw new MultiMonsterException("Error while inserting in componentprefix!");
		}

		return prefix;
	}

	/**
	 * Prüft ob ein Setting schon in der DB existiert
	 * 
	 * @param settingID
	 * @return
	 */
	private boolean checkSettingExistence(SettingID settingID) {
		boolean exists = false;

		int setID = settingID.getId();

		QueryManager qmnr = new QueryManager();
		int connNr = qmnr.reserveConnection();

		// build statement
		String query = "select * from setting where id = " + setID + ";";

		ResultSet result = qmnr.dbOpExec(query, connNr);

		try {
			if (result.next()) {
				exists = true;
			} else {
				exists = false;
			}
		} catch (SQLException e) {
			log.error("Fehler bei checkExistence: DB Zugriff!");
		}

		qmnr.bringBackConn(connNr);

		return exists;
	}

	/**
	 * Prüft ob der Wert im vorgegebenen Domainbereich liegt
	 * 
	 * @param settingID
	 * @param value
	 * @return
	 * @throws SettingNotExistsException
	 */
	private boolean checkValueDomain(SettingID settingID, SettingValue value)
		throws SettingNotExistsException {
		boolean isOk = false;
		SettingDomain domain = null;

		//zuerst Domain holen
		domain = getSettingDomain(settingID);

		if (domain.isContinuous() == true) {
			// Setting is continous data-type
			Comparable lower = domain.getLowerLimit();
			Comparable upper = domain.getUpperLimit();
			Comparable setVal = value.getValueCont();

			// Value must lie in between lower and upper limit
			if (lower.compareTo(setVal) <= 0 && upper.compareTo(setVal) >= 0) {
				isOk = true;
			}
		} else {
			// Setting is discrete data-type
			Object[] objects = domain.getDiscrete();
			Object setVal = value.getValueDiscr();

			for (int i = 0; i < objects.length; i++) {
				if (objects[i].equals(setVal) == true) {
					isOk = true;
				}
			}
		}

		return isOk;
	}

}
