package com.mfg.utils;

import java.util.Hashtable;

/**
 * This is the fingerprint detail, used to have the possibility to know which
 * leaf of the fingertip has been changed.
 * 
 * @author Sergio
 * 
 */
public final class i_fp {

	private i_fp() {
	}

	public static class Field_fp {
		public long fingerprint;
		/**
		 * The value of the field, in a human readable way
		 */
		public String val;
		public Hashtable<String, Field_fp> detail = new Hashtable<>();
	}

}