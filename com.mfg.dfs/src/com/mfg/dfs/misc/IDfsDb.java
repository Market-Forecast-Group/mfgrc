package com.mfg.dfs.misc;

import java.io.IOException;
import java.util.Arrays;

import org.mfg.mdb.runtime.IRandomCursor;
import org.mfg.mdb.runtime.IRecord;

//import com.mfg.dfs.serv.RangeBarsMDB.RandomCursor;

/**
 * 
 * 
 * @author Sergio
 * 
 */
public interface IDfsDb {

	/**
	 * returns the index of the record with exactly this time.
	 * 
	 * <p>
	 * It behaves exactly as the {@linkplain Arrays#binarySearch(long[], long)}
	 * method, in the sense that it returns the record, or, if not found, it
	 * returns <code>(-(insertion point) - 1)</code>
	 * 
	 * @param time
	 * @return
	 * @throws IOException
	 */
	public long indexOfPrimaryKey(IRandomCursor<? extends IRecord> rc, long time)
			throws IOException;

	/**
	 * 
	 * @return the first primary key in the database
	 * @throws IOException
	 */
	public long firstKey() throws IOException;

	/**
	 * 
	 * @return the last key in the database
	 * @throws IOException
	 */
	public long lastKey() throws IOException;

	/**
	 * gets the index of the primary key, and it does <b>not</b> complain if the
	 * time is out of bounds. It will simply return the first or last record.
	 * 
	 * <p>
	 * So it is guaranteed that, apart from exceptions, this method will simply
	 * return a valid index inside this database
	 * 
	 * 
	 * @param time
	 *            the time to be searched for
	 * 
	 * @return the index of the record with the primary key chosen. It will be
	 *         <b>always</b> be a valid index, the only exception is when we
	 *         have the
	 * @throws IOException
	 *             if something goes wrong in the database.
	 */
	public long indexOfPrimaryKey_lenient(IRandomCursor<? extends IRecord> rc,
			long time) throws IOException;
}
