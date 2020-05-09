package com.mfg.tea.db;

import org.mapdb.BTreeMap;

import com.mfg.tea.db.Db.OBJECTS;

/**
 * Just an utility class which is here to organize the naming conventions in the
 * database.
 * 
 * <p>
 * It has methods to store fields and arrays.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 *
 */
public class DbKeyFieldHelper {

	/**
	 * Creates a array index sequence for a field array in a particular object.
	 * 
	 * @param object
	 * @param objectId
	 * @param arrayName
	 */
	static void createArrayIndex(Db.OBJECTS object, int objectId,
			String arrayName) {
		//

		String arraySeqName = Db.ARRAY_INDEX_PREFIX + object.getPrefix()
				+ Integer.toString(objectId) + "." + arrayName;

		_instance._db.createAtomicInteger(arraySeqName, 0);
	}

	/**
	 * Deletes a given array index because probably the object is complete,
	 * immutable and does not need to have the index array any more.
	 */
	static void deleteArrayIndex(Db.OBJECTS object, int objectId,
			String arrayName) {
		String arraySeqName = Db.ARRAY_INDEX_PREFIX + object.getPrefix()
				+ Integer.toString(objectId) + "." + arrayName;
		_instance._db.delete(arraySeqName);
	}

	//

	/**
	 * 
	 * @param objectRoot
	 * @param objectId
	 * @param fieldName
	 * @return the field value, null if the given object has not this field.
	 */
	public static String getFieldValue(String objectRoot, int objectId,
			String fieldName) {
		return null;
	}

	static int getNewObjectId(Db.OBJECTS aObjectClass) {
		String sequenceKey = Db.INSTANCE_SEQUENCE_PREFIX + aObjectClass;
		return _instance._db.getAtomicInteger(sequenceKey).incrementAndGet();

	}

	/**
	 * @param aObjectClass
	 *            the class of the object you want to create.
	 */
	static long getNewObjectIdLong(Db.OBJECTS aObjectClass) {
		String sequenceKey = Db.INSTANCE_SEQUENCE_PREFIX + aObjectClass;
		return _instance._db.getAtomicLong(sequenceKey).incrementAndGet();
	}

	static int getNextArrayIndexValue(Db.OBJECTS object, int objectId,
			String arrayName) {
		String arraySeqName = Db.ARRAY_INDEX_PREFIX + object.getPrefix()
				+ Integer.toString(objectId) + "." + arrayName;

		return _instance._db.getAtomicInteger(arraySeqName).getAndIncrement();

	}

	static void initialize(Db aInstance) {
		_instance = aInstance;
		_teaMap = aInstance._teaMap;
	}

	/**
	 * Puts the given value in the given array field, incrementing the
	 * counter...
	 * 
	 * @param aObjectType
	 * @param aObjectId
	 * @param tradingSessionsArray
	 * @param string
	 */
	static void putArrayValue(OBJECTS aObjectType, int aObjectId,
			String arrayField, String aValue) {
		int arrayIndex = getNextArrayIndexValue(aObjectType, aObjectId,
				arrayField);
		// this is the array field name used to store the value
		String arrayFieldName = arrayField + "@" + Integer.toString(arrayIndex);

		putFieldValue(aObjectType, aObjectId, arrayFieldName, aValue);
	}

	/**
	 * Puts the given value to the given field in the object with a particular
	 * id.
	 * 
	 * @param aClass
	 * @param aObjectId
	 * @param fieldName
	 * @param value
	 */
	public static void putFieldValue(OBJECTS aClass, int aObjectId,
			String fieldName, String value) {
		String keyString = aClass.getPrefix() + Integer.toString(aObjectId)
				+ "." + fieldName;
		_teaMap.put(keyString, value);
	}

	static void putFieldValue(OBJECTS orderObject, long newId,
			String fieldName, int id) {
		putFieldValue(orderObject, newId, fieldName, Integer.toString(id));
	}

	static void putFieldValue(OBJECTS aClass, long newId, String fieldName,
			String value) {
		String keyString = aClass.getPrefix() + Long.toString(newId) + "."
				+ fieldName;
		_teaMap.put(keyString, value);
	}

	private static Db _instance;

	private static BTreeMap<String, String> _teaMap;

}
