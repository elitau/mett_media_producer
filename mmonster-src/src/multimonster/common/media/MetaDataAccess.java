package multimonster.common.media;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import multimonster.common.media.Duration;
import multimonster.common.media.MetaData;

/**
 * A MetaDataAccess is concrete implementation of the IMetaData interface. It
 * combines IMetaData interfaces. In Order to add a new MetaDataProperty to this
 * class simply add a private static field like : private static Object[]
 * titleProperty = new Object[] { "Title", String.class }; to this class . A
 * Reflection based algorithm checks all fiels in an static initializer.
 * Therefor the property is available in the getKeys() and getValueTypes()
 * methods;
 * 
 * 
 * 
 * @author Frank Müller
 *  
 */

public class MetaDataAccess implements IMetaData {
	private static Object[] titleProperty = new Object[] { "Title",
			String.class };

	private static Object[] languageProperty = new Object[] { "Language",
			String.class };

	private static Object[] durationProperty = new Object[] { "Duration",
			Duration.class };

	private static Object[] dateProperty = new Object[] { "DateOfRelease",
			Date.class };

	private static Object[] outlineProperty = new Object[] { "Outline",
			String.class };

	private static Object[] coloredProperty = new Object[] { "Colored",
			Boolean.class };

	private static Object[] ageRestrictionProperty = new Object[] {
			"AgeRestriction", Integer.class };

	private static Object[] numOfFramesProperty = new Object[] {
			"NumberOfFrames", Integer.class };

	private static String[] keys;

	private static Class[] types;

	private Hashtable values = new Hashtable();

	/**
	 * initializes the keys and types fields with MetaDataProperties using
	 * reflection
	 */
	static {
		ArrayList tempkeys = new ArrayList();
		ArrayList temptypes = new ArrayList();

		Class c = MetaDataAccess.class;
		Field[] fields = c.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (Modifier.isStatic(fields[i].getModifiers())) {
				Class typeClass = fields[i].getType();
				if (typeClass == Object[].class) {
					try {
						Object[] property = (Object[]) fields[i]
								.get(MetaDataAccess.class);
						tempkeys.add(property[0]);
						temptypes.add(property[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		keys = new String[tempkeys.size()];
		types = new Class[tempkeys.size()];

		for (int lfv = 0; lfv < tempkeys.size(); lfv++) {
			keys[lfv] = (String) tempkeys.get(lfv);
			types[lfv] = (Class) temptypes.get(lfv);
		}

	}

	/**
	 * Wraps a MetaDataObject in an MetaDataAccess Object
	 * 
	 * @param metaData
	 */
	public MetaDataAccess(MetaData metaData) {
		//TODO remove Constructor
		this.setValue((String) titleProperty[0], metaData.getTitle());
		this.setValue((String) languageProperty[0], metaData.getLanguage());
		this.setValue((String) durationProperty[0], metaData.getDuration());
		this.setValue((String) dateProperty[0], metaData.getDateOfRelease());
		this.setValue((String) outlineProperty[0], metaData.getOutline());
		this.setValue((String) coloredProperty[0], new Boolean(metaData
				.isColored()));
		this.setValue((String) ageRestrictionProperty[0], new Integer(metaData
				.getAgeRestriction()));
		this.setValue((String) numOfFramesProperty[0], new Integer(metaData
				.getNumOfFrames()));
	}
	
	

	/**
	 * Constuct a empty MetaDataObject
	 */

	public MetaDataAccess() {

	}

	/**
	 * @inheritDoc
	 */
	public String[] getKeys() {
		return keys;
	}

	/**
	 * @inheritDoc
	 */
	public Class[] getValueTypes() {
		return types;
	}

	/**
	 * @inheritDoc
	 */

	public Object getValue(String key) {
		boolean keyfound = false;
		for (int lfv = 0; lfv < keys.length; lfv++) {
			if (keys[lfv].equals(key)) {
				keyfound = true;
				break;
			}
		}

		if (!keyfound) {
			throw new IllegalStateException("Key " + key + " not allowed");
		}

	
		return values.get(key);
	}

	/**
	 * @inheritDoc
	 */
	public void setValue(String key, Object value) {
		boolean keyfound = false;
		int index = 0;
		for (int lfv = 0; lfv < keys.length; lfv++) {
			if (keys[lfv].equals(key)) {
				keyfound = true;
				index = lfv;
				break;
			}
		}

		if (!keyfound) {
			throw new IllegalStateException("Key " + key + " not allowed");
		}
		if (value.getClass().isInstance((Class) types[index])) {
			throw new IllegalStateException("Value type " + value.getClass()
					+ " not allowed. Must be " + types[index]);
		}

		this.values.put(key, value);
	}

}