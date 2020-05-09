package com.mfg.dfs.cache;

import com.mfg.common.DFSException;
import com.mfg.dfs.misc.DfsBar;

/**
 * Simple read cache, used to give to the GUI a read only view of the data
 * inside the database.
 * 
 * <P>
 * The cache is in reality a mdb file, but the gui is not aware of it.
 * 
 * <p>
 * All the methods in the cache are synchronous. We do not allow concurrent
 * access; it seems a bit too much, care must be taken to consider if this high
 * level lock is necessary.
 * 
 * 
 * @author Sergio
 * 
 */
public interface IReadCache<T extends DfsBar> {
	/**
	 * gets the bar at a certain index.
	 * <p>
	 * This method will create a new object and return the new object.
	 * 
	 * @param index
	 * @return
	 * @throws DFSException
	 */
	public T get(int index) throws DFSException;

	/**
	 * returns the first key in the store, the keys are in ascending order.
	 * <p>
	 * Usually, but not <b>always</b> the keys are positive.
	 * 
	 * @return the first key in the store, if the store is empty it returns
	 *         {@linkplain Long#MAX_VALUE}.
	 * @throws DFSException
	 */
	public long getFirstKey() throws DFSException;

	/**
	 * gets the index of a bar at a certain index., The index is absolute, from
	 * 0 to max.
	 * 
	 * @param time
	 *            The time requested.
	 * 
	 * @return the index of the record at the <b>exact</b> time given, -1 if it
	 *         is not found.
	 */
	public long getIndexOfRecAt(long time) throws DFSException;

	/**
	 * @return the last key in the store, if the store is empty it returns
	 *         {@link Long#MIN_VALUE}.
	 * @throws DFSException
	 * 
	 */
	public long getLastKey() throws DFSException;

	/**
	 * Gets the nearest index of a record with time.
	 * <p>
	 * This is inexact, in the sense that even if a record with the exact time
	 * does not exist it returns eventually something, also if the time is not
	 * contained in the cache the method returns the first or the last time of
	 * the cache
	 * 
	 * @param time
	 * @return the nearest absolute index (from 0 to size-1), as seen by the
	 *         {@link #getAbs(int)} methods
	 */
	public long getNearestIndexOfRecAt(long time) throws DFSException;

	/**
	 * gets the nearest index of a record with a certain time checking the first
	 * and the last time.
	 * 
	 * @param time
	 *            the time to search
	 * @return the index of the last record. It is guaranteed that the index
	 *         returned is the first or the last index <b>ONLY</b> if the time
	 *         is within bounds
	 * @throws DFSException
	 *             if the time is out of bounds for this cache.
	 */
	public long getNearestIndexOfRecAtBounded(long time) throws DFSException;

	/**
	 * Gets the record at a certain rowId.
	 * <p>
	 * The search is <b>exact</b>. If an inexact search is required, the method
	 * {@linkplain ICache#getNearestIndexOfRecAt(long)} should be used instead.
	 * 
	 * @param rowId
	 * @return the record at that time, null if it is not found.
	 */
	public T getRecAt(long rowId);

	/**
	 * gets a record within a range from time
	 * 
	 * 
	 * @param time
	 *            the time you want to find
	 * @param halfRange
	 *            the maximum distance that is permissible, in milliseconds.
	 * @return the record found, or null if nothing is found.
	 * @throws DFSException
	 *             if a problem occurs.
	 */
	public T getRecWithinRange(long time, long halfRange) throws DFSException;

	/**
	 * gets the size of this cache.
	 * <p>
	 * Valid indexes of the cache are from 0 to size()-1, as in any "normal"
	 * java container.
	 * 
	 * @return the cache size.
	 */
	public long size();
}
