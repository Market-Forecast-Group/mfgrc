package com.mfg.dfs.misc;

import org.mfg.mdb.runtime.IRecord;

/**
 * extends the {@linkplain IRecord} interface with the possibility to know a
 * primary key.
 * 
 * <p>
 * In mdb the concept of a primary key is left to the user, records are simply
 * stored sequentially in the file
 * 
 * @author Pasqualino
 * 
 */
public interface IRecordWithKey extends IRecord {

	/**
	 * gets the primary key.
	 * @return the primary key as a long.
	 */
	public long getPrimaryKey();
}
