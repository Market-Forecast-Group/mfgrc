/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */
package com.mfg.utils;


/**
 * In the project mfg an Identifiable is simply an object which has:
 * 
 * <p>
 * 1. The possibility to stringify itself in a way to create an identical object
 * at a state zero. This stringification is done by JSON.
 * 
 * <p>
 * 2. The possibility to get a hash code of this stringification. This hash code
 * is simply done via the SHA1 sum of the stringification of the object.
 * 
 * <p>
 * 3. The possibility to clone itself. The general contract is that an
 * IIdentifiable cloned will have the same hashid as the original. The cloned
 * object will be at STATE ZERO.
 * 
 * <p>
 * So we must have <code>
IIdentifiable ident = ...;
assert(ident.clone().getHashId().compareTo(ident.getHashId()) == 0);
</code>
 */
public interface IIdentifiable extends Cloneable {

	/**
	 * This function returns the hash code of this object. This hash is a
	 * "secure" hash, that is it is very improbable that two different objects
	 * will have the same hash.
	 * 
	 * It is not like the hashCode of the Object class, which is instead a
	 * "weak" hash, used only to use it in a hash map or hash table.
	 * 
	 * @return the hash code of this object.
	 */
	public String getHashId();

	/**
	 * Serializes this object to a string.
	 * 
	 * @return the string serialization of this object.
	 */
	public String serializeToString();

}
