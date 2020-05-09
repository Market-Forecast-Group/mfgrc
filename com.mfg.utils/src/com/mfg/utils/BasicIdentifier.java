package com.mfg.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The BasicIdentifier is an object that can serialize to a string without
 * giving a fixed format to the string itself.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class BasicIdentifier implements IIdentifiable {

	/**
	 * This is used to invalidate the hash, when the object is modified.
	 */
	protected void _invalidateHash() {
		_hash = null;
	}

	/**
	 * This is used whenever I modify the object from the outside. This method
	 * is temporary.
	 */
	public void _temp_setModified() {
		_invalidateHash();
	}

	/**
	 * This is precomputed, cached
	 */
	protected String _hash = null;

	@Override
	public final String getHashId() {
		if (_hash == null) {
			_hash = computeHash(this.serializeToString());
		}
		return _hash;
	}

	public static String computeHash(String message) {
		try {
			String hash;
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] dig = md.digest(message.getBytes());
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < dig.length; i++) {
				hexString.append(Integer.toHexString(0xFF & dig[i]));
			}
			hash = new String(hexString);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
