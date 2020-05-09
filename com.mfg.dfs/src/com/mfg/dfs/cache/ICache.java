/**
 * 
 * (C) Copyright 2011-2013 - MFG <http://www.marketforecastgroup.com/>
 * 
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 */
package com.mfg.dfs.cache;

import java.io.IOException;

import com.mfg.common.DFSException;
import com.mfg.dfs.misc.DfsBar;

/**
 * a cache which has methods only to append in the end the data.
 * 
 * <p>
 * In these days (July 2013) we are changing the data provider from eSignal to
 * iqFeed.
 * 
 * 
 * @author Sergio
 * 
 */
public interface ICache<T extends DfsBar> extends IReadCache<DfsBar> {

	/**
	 * pushes a bar at the end of the cache.
	 * 
	 * <p>
	 * It will add the record <b>only</b> if the record is after the last row in
	 * the cache.
	 * <p>
	 * It will <b>not</b> try to alter the record in any way.
	 * 
	 * @param aRec
	 *            the record which is pushed at the last index
	 * 
	 * @return true if the push has been successful
	 * @throws DFSException
	 *             if something wrong happens
	 */
	public boolean addLast(T aRec) throws DFSException;

	/**
	 * adds a bar at the end of the cache <i>forcing</i> it to be the last bar.
	 * 
	 * <p>
	 * This means that the bar, if it has a time less then the last it will be
	 * "massaged" to be the last time.
	 * 
	 * <p>
	 * This is only really useful when the client knows perfectly what it is
	 * doing, because in this case we are really forcing the history.
	 * 
	 * @param aRec
	 *            the record (it may be changed!)
	 * @throws DFSException
	 *             if something goes wrong.
	 */
	public void addLastForce(T aRec) throws DFSException;

	/**
	 * builds a backup of this cache and at the same time clears the cache, this
	 * is done atomically in the file system renaming the file and creating
	 * another empty file.
	 * 
	 * <p>
	 * There is only one level of backup, if you call this method twice you lose
	 * the backup, so be careful.
	 * 
	 * 
	 * @throws DFSException
	 */
	public void backup() throws DFSException;

	/**
	 * Clears the cache.
	 * 
	 * <p>
	 * <b>WARNING</b>. This method will <b>destroy</b> all data in the cache.
	 * The cache is now implemented with a mdb database and we don't have the
	 * possibility to recover...
	 * 
	 * @throws DFSException
	 *             if something goes wrong.
	 */
	public void clear() throws DFSException;

	/**
	 * Compacts the cache
	 * <p>
	 * This could be a no-op for certain store, for example h2 already does a
	 * the compact (but it could mean to do a commit).
	 * 
	 * @param tryToFlush
	 *            true if you want to try to flush the cache (using the last
	 *            time stored in the forward buffer, if the cache is implemented
	 *            using two buffers).
	 * 
	 */
	public void compact(boolean tryToFlush);

	/**
	 * This method deletes the cache physically from disk.
	 * <p>
	 * as a safety measure it refuses to delete a cache that has some data
	 * inside, first of all you have to call clear()
	 * 
	 * @throws DFSException
	 */
	public void delete() throws DFSException;

	/**
	 * returns the minimum index of the record which, in this cache, has a key
	 * strictly higher than the time given.
	 * 
	 * <p>
	 * The algorithm can also return a not existing record, off by one, that is
	 * the corresponding end iterator, which in this case it is simply defined
	 * as size(), this special not existing record is returned if the key
	 * supplied is equal to the last key in the database.
	 * 
	 * <p>
	 * When the client receives this return value it means that there is not a
	 * physical record corresponding to the ceiling.
	 * 
	 * 
	 * 
	 * The behavior in the corner case is:
	 * 
	 * <p>
	 * <li>
	 * the cache is empty, it returns -1.
	 * <p>
	 * <li>
	 * time equal to the first record time: the method returns 1, which is the
	 * second record, if the cache has only one record it returns -1 again, this
	 * case is the same as the next, where the time is equal to the last record
	 * time
	 * <p>
	 * <li>
	 * time equal to the last record time: ceiling the next not existent record,
	 * it returns size()
	 * <p>
	 * <li>
	 * time less than the first record: ceiling is equal to the first record, it
	 * returns 0. (if the cache is empty this is already covered in the first
	 * case)
	 * <p>
	 * <li>
	 * time greater than the last record, ceiling not existent, so it returns
	 * -size()
	 * 
	 * @param aTime
	 * @return a number between 0 and size(), the first index, 0 can be a
	 *         ceiling in the particular case of time less than the first,
	 *         because if time is equal to the first, it returns the second.
	 * @throws DFSException
	 */
	public int getCeilingIndexForTime(long aTime) throws DFSException;

	/**
	 * gets the maximum index of a record whose time is not higher than the
	 * given time.
	 * 
	 * <p>
	 * In other words in this cache, for all the indexes minor than the index
	 * returned the time of the record is strictly less than aTime, and for all
	 * the others the time is equal (if the floor is exact) and greater than the
	 * time given.
	 * 
	 * <p>
	 * The behavior for the corner cases is:
	 * 
	 * 
	 * <p>
	 * <li>
	 * a floor of an empty cache is -1 always.
	 * 
	 * <p>
	 * <li>
	 * If aTime is the time of the first record this is the floor, so it returns
	 * 0
	 * 
	 * <p>
	 * <li>
	 * If aTime is the time of the last record this is again the floor, so it
	 * returns size()-1
	 * 
	 * <p>
	 * <li>
	 * If aTime is less than the time of the first record it returns -1
	 * 
	 * <p>
	 * <li>
	 * If aTime is greater than the time of the last record it returns size() -
	 * 1, to make it congruent with the case of the ceiling with a time less
	 * than the first key.
	 * 
	 * <p>
	 * throws exception only if there is a problem.
	 * 
	 * @param aTime
	 * @return the floor index of this time, which is a valid index of this
	 *         cache <code>[0, size()-1]</code> if (and only if) time is
	 *         contained in the open interval [firstTime, +infinity), otherwise
	 *         -1 is returned
	 * @throws IOException
	 * @throws DFSException
	 */
	public int getFloorIndexForTime(long aTime) throws DFSException;

	/**
	 * restores the preceding backup in this file, the backup itself is NOT
	 * removed, so it is save to restore it multiple times.
	 * 
	 * <p>
	 * The <b>current</b> state of the cache, however, is not preserved.
	 * 
	 * @throws DFSException
	 */
	public void restore() throws DFSException;

	/**
	 * truncates at a particular index the database.
	 * 
	 * <p>
	 * this action is destructive of the db, be careful!
	 * 
	 * <p>
	 * if there are not errors the new size of the db will be <code>index</code>
	 * , so index will not be a valid index any more, the last index will be
	 * <code>index-1</code>
	 * 
	 * @param index
	 *            the index used to truncate the database, it must be
	 *            <code> 0 < index < {@link #size()} </code>
	 * @throws DFSException
	 */
	public void truncateAt(int index) throws DFSException;

	/**
	 * Truncates the cache.
	 * 
	 * <p>
	 * <b>WARNING</b>. This method will <b>destroy</b> all data of the cache
	 * since a particular time. If the time is before the first time of the
	 * cache this method is equivalent to the {@link #clear()} method.
	 * 
	 * <p>
	 * This method is useful DFS only because sometime there is a mismatch in
	 * the table from the database and the data that come from the data feed. As
	 * the cache does not allow overwriting (basically because it is not a real
	 * db) we have to truncate and rewrite the offending part.
	 * 
	 * <p>
	 * After the call of this method the call to {@link #getLastKey()} will
	 * return a time less then the time given.
	 * 
	 * @param time
	 *            the time from which you want to truncate the data (inclusive)
	 * 
	 * @throws DFSException
	 */
	public void truncateFrom(long time) throws DFSException;

}
