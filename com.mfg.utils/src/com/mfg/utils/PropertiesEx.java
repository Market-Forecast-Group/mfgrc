package com.mfg.utils;

import java.util.Properties;

/**
 * a PropertiesEx object is simply a Properties object with some
 * methods to query boolean,long and double values.
 * 
 * <p>This class is then used to store and retrieve meaningful properties
 * for an application.
 * 
 * @author Sergio
 *
 */
public class PropertiesEx extends Properties{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3569519428411797655L;
//

	public int getIntDef(String key, int def) {
		String value = getProperty(key);
		if (value == null){
			return def;
		}
		try{
			int val = Integer.parseInt(value);
			return val;
		} catch (NumberFormatException e){
			return def;
		}
		
	}

	public double getDoubleDef(String key, double def) {
		String value = getProperty(key);
		if (value == null){
			return def;
		}
		try{
			double val = Double.parseDouble(value);
			return val;
		} catch (NumberFormatException e){
			return def;
		}
	}

	public boolean getBooleanDef(String key, boolean defaultVal) {
		String value = getProperty(key, defaultVal ? "true"
				: "false");
		if (value.compareToIgnoreCase(defaultVal ? "true" : "false") == 0) {
			return defaultVal;
		}
		return !defaultVal; // the key is not found or it is different.
	}
}
