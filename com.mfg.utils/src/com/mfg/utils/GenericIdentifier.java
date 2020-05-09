package com.mfg.utils;

import static com.mfg.utils.Utils.catch_exception_and_continue;
import static com.mfg.utils.Utils.debug_var;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public abstract class GenericIdentifier extends BasicIdentifier implements
		IJSONIdentifiable {

	// private static Logger _log = Logger.getLogger(GenericIdentifier.class);
	private static final String TYPE_KEY = "type";

	/**
	 * The deserialization of a class can call other deserialization in chain.
	 * So the class loader is stored in this variable only for the purpose of
	 * deserialization
	 */
	private static ClassLoader _customClassLoaderForThisCreation;

	protected abstract void _toJsonEmbedded(JSONStringer stringer)
			throws JSONException;

	/**
	 * This method stringifies a collection to json using the array element in
	 * json
	 * 
	 * @param stringer
	 *            the name of the stringer which will accept this collection
	 * @param name
	 *            the name of the collection.
	 * @param col
	 *            the collection to be serialized
	 * @throws JSONException
	 */
	protected static void _collectionToJson(JSONStringer stringer, String name,
			Collection<?> col) throws JSONException {
		stringer.key(name);
		stringer.array();
		for (Object item : col) {
			stringer.value(item);
		}
		stringer.endArray();
	}

	/**
	 * This method assumes that the object is already created and it assumes
	 * that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 * 
	 * <p>
	 * The post condition of this method is that the object should be at state
	 * zero after this call.
	 */
	protected abstract void _updateFromJSON(JSONObject json)
			throws JSONException;

	/**
	 * This function is final, because the basic format of the object is fixed.
	 * Subclasses must override the _toJsonEmbedded function.
	 */
	@Override
	public final String toJSONString() {
		try {
			JSONStringer js = new JSONStringer();

			js.object();

			js.key(TYPE_KEY);
			js.value(this.getClass().getName());

			_toJsonEmbedded(js);

			js.endObject();

			return js.toString();

		} catch (JSONException e) {
			catch_exception_and_continue(e, true);
			return "{}";
		}

	}

	/**
	 * @param aClassLoader
	 */
	@SuppressWarnings("unchecked")
	public static GenericIdentifier createFromString(String json,
			ClassLoader aClassLoader) {
		if (aClassLoader != null) {
			_customClassLoaderForThisCreation = aClassLoader;
		}
		String className = "";
		try {
			// The null should be set from the serialization. The object was
			// null.
			if (json.compareTo("null") == 0) {
				return null;
			}
			JSONObject ob = new JSONObject(json);

			// the following assignment is valid, as long
			// as the json is correct
			Class<? extends GenericIdentifier> klass;
			className = (String) ob.get(TYPE_KEY);
			try {

				klass = (Class<? extends GenericIdentifier>) Class
						.forName((String) ob.get(TYPE_KEY));
			} catch (ClassNotFoundException e) {
				try {
					klass = (Class<? extends GenericIdentifier>) Thread
							.currentThread().getContextClassLoader()
							.loadClass((String) ob.get(TYPE_KEY));
				} catch (ClassNotFoundException e1) {
					/*
					 * 3rd try with the custom class loader, if present
					 */
					if (aClassLoader != null) {
						klass = (Class<? extends GenericIdentifier>) aClassLoader
								.loadClass((String) ob.get(TYPE_KEY));
					} else
						throw e1;
				}
			}

			Constructor<? extends GenericIdentifier> con = klass
					.getDeclaredConstructor();
			assert (con != null);
			con.setAccessible(true);
			GenericIdentifier m = con.newInstance();
			m._updateFromJSON(ob);

			// It is not important any more
			// _customClassLoaderForThisCreation = null;
			return m;

		} catch (NoSuchMethodException e) {
			throw new RuntimeException("The class " + className
					+ " has not an empty constructor!");
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot find the class " + className);
		}
	}

	/**
	 * This is the generic factory object for any object which is
	 * json-serializable in the system
	 */
	public static GenericIdentifier createFromString(String json) {
		return createFromString(json, _customClassLoaderForThisCreation);
	}

	/**
	 * Serializes safely the given object to the stringer.
	 * 
	 * <p>
	 * It handles gracefully the null object, serializing it to a "null" string,
	 * this is then used by the {@link #createFromString(String)} method to
	 * return a null object during desrialization.
	 * 
	 * 
	 * @param stringer
	 *            the stringer object used to serialize the object parameter.
	 * 
	 * @param key
	 *            the key used to serialize the object
	 * @param object
	 *            the object, can be null
	 * @throws JSONException
	 */
	protected final static void _serializeObjectSafe(JSONStringer stringer,
			String key, IJSONIdentifiable object) throws JSONException {
		stringer.key(key);
		stringer.value(object == null ? "null" : object.toJSONString());
	}

	@Override
	public final String serializeToString() {
		return toJSONString();
	}

	/**
	 * deserializes safely the object serialized inside the given json object
	 * with the corresponding key handling safely the case of null object
	 * 
	 * @param json
	 *            the json object
	 * @param key
	 *            the key inside the object
	 * @return the deserialized object, or null.
	 * @throws JSONException
	 */
	protected static Object _deserializeObjectSafe(JSONObject json, String key)
			throws JSONException {

		String jsonRep = json.getString(key);
		return createFromString(jsonRep);
	}

	/**
	 * Simple debug functions to check the json consistency
	 */
	protected boolean _cloneHasTheSameHash(GenericIdentifier clone) {
		clone._invalidateHash();
		this._invalidateHash();
		debug_var(399111, "this hash = " + this.getHashId());
		debug_var(281933, "clone hash = " + clone.getHashId());
		if ((this.getHashId().compareTo(clone.getHashId())) == 0) {
			return true;
		}
		debug_var(392911,
				"MISMATCH IN CLONED HASH this is ----------------------------------------");
		System.out.println(this.toJSONString());
		debug_var(939291, "clone is ----------------------------------------");
		System.out.println(clone.toJSONString());

		if (this.toJSONString().equals(clone.toJSONString()))
			System.out.println("2 equal JSON");
		else
			System.out.println("JSON different");

		if (this.equals(clone))
			System.out.println(">>> 2 equal objects");
		else
			System.out.println(">>> different objects");

		assert (this.toJSONString().compareTo(clone.toJSONString()) != 0) : "bad bad things";

		return false;
	}

}
