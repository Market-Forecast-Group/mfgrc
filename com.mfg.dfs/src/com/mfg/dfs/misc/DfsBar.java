package com.mfg.dfs.misc;

import org.mfg.mdb.runtime.IRecord;

import com.mfg.common.Bar;
import com.mfg.common.DFSException;

/**
 * The DfsBar is the base class for all the bars inside the caches (we can have
 * either range or time bars, they are stored differently for efficiency
 * purposes).
 * 
 * <p>
 * The dfs bar is used as a simple base class for the cache; from the record it
 * adds the possibility to have a signature; the problem is that we need to fill
 * a cache without holes and we have to be prepared for the range bars, because
 * they could have the <i>same</i> time and <i>different</i> values.
 * 
 * <p>
 * The data provider usually do not give to us milliseconds information, so it
 * can happen to have two range bars with the same second but different, so we
 * have to distinguish them.
 * 
 * @author Sergio
 * 
 */
public interface DfsBar extends IRecord {

	/**
	 * Every bar in dfs has a signature which is simply the checksum of all the
	 * values in the bar itself.
	 * 
	 * <p>
	 * This is done because the bar is stored in the cache and we must make sure
	 * that even bars with the same timestamp are stored correctly.
	 * 
	 * @return the signature of this bar.
	 */
	public abstract long getSignature();

	/**
	 * returns the primary key for this bar.
	 * 
	 * @return
	 */
	public abstract long getPrimaryKey();

	/**
	 * This offsets the primary key by the given amount.
	 * <p>
	 * It is used by the cache to accommodate different bars at the same time
	 * (in seconds) splitting them in milliseconds information.
	 * 
	 * @param offset
	 *            the offset.
	 */
	public abstract void offsetPrimaryKey(long offset);

	public Bar decodeTo(int _tick) throws DFSException;

	/**
	 * returns true if the bar is equal to this except for the time
	 * @param checkBar
	 * @return
	 */
	public abstract boolean equalsNoTime(DfsBar checkBar);

}
