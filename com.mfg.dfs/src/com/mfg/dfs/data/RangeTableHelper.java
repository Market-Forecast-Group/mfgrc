package com.mfg.dfs.data;

import com.mfg.common.DFSException;
import com.mfg.dfs.cache.ICache;
import com.mfg.dfs.misc.DfsBar;

/**
 * A helper class that contains only static methods to help compute some
 * quantities for the range tables (which are different).
 * 
 * <p>
 * This helper class is used because we do not have multiple inheritance in
 * Java.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
final class RangeTableHelper {

	static int getMaximumIndexOfBarWithin(ICache<DfsBar> _cache, long aTime)
			throws DFSException {
		/*
		 * The max function is translated to the raw floor function in cache.
		 * 
		 * * @return the floor index of this time, which is a valid index of
		 * this cache <code>[0, size()-1]</code> if (and only if) time is
		 * contained in the open interval [firstTime, +infinity), otherwise -1
		 * is returned
		 */
		int preAns = _cache.getFloorIndexForTime(aTime);

		/*
		 * now preAns points to a record whose key is not less than aTime. aTime
		 * is the ending time, so the starting time of the previous bar is
		 * certainly lower than that, at least of one millisecond, so the
		 * searched bar is one bar less, at least in the normal cases.
		 * 
		 * The fact is that the index must be then offset by one because the
		 * first record is not considered.
		 */

		if (preAns == 0) {
			/*
			 * this means that the time given is between the first and the
			 * second bar, this is a valid point, because the first bar is used
			 * only to signal the starting time of the second, so the second has
			 * a starting time after the first.
			 */
			return 1;
		}

		/*
		 * This strange computation is because the real record is one less, but
		 * the range table has the first bar unused, so I offset all indexes by
		 * one.
		 */
		return (preAns /*- 1 + 1*/);
	}

	public static int getMinimumIndexOfBarAfter(ICache<DfsBar> _cache,
			long aTime) throws DFSException {
		/*
		 * When we consider the starting time and the table stores the ending
		 * time the function is reversed, from ceil to floor and viceversa.
		 */

		// -1 because aTime is a valid end time.
		int preFloor = _cache.getFloorIndexForTime(aTime);

		/*
		 * Now floor points to the bar whose DURATION ends at aTime, or lower.
		 * If ends precisely at aTime the +1 bar is sufficient, otherwise we
		 * need the +2 bar
		 */
		if (preFloor < 0) {
			// all the keys in db are higher, 0 would be OK, BUT the range table
			// starts from 1, so I return 1, unless the cache is less than 2
			// bars wide
			if (_cache.size() < 2) {
				return -1;
			}
			return 1;
		}

		if (preFloor == _cache.size() - 1) {
			/*
			 * all the keys are lower, there is no physical bar which starts at
			 * aTime, it would be the next bar if aTime is equal to the last
			 * key, in any case it is now not existing.
			 */
			return (int) -_cache.size();
		}

		int checkIndex = (int) _cache.getIndexOfRecAt(aTime);

		if (checkIndex >= 0) {
			preFloor += 1;
		} else {
			preFloor += 2;
		}

		if (preFloor >= _cache.size()) {
			return (int) -_cache.size();
		}

		return preFloor;

	}

	public static int size(ICache<DfsBar> _cache) {
		return Math.max((int) _cache.size() - 1, 0);
	}

	public static int upLimit(ICache<DfsBar> _cache) {
		return _cache.size() != 0 ? size(_cache) : -1;
	}

}
