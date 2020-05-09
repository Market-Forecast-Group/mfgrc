package com.mfg.dfs.data;

import java.io.IOException;
import java.util.Date;

import com.mfg.common.Bar;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsEmptyDatabaseException;
import com.mfg.common.DfsInvalidRangeException;
import com.mfg.common.DfsSymbol;
import com.mfg.dfs.cache.ICache;
import com.mfg.dfs.data.DfsIntervalStats.EVisibleState;
import com.mfg.dfs.misc.DfsBar;
import com.mfg.utils.Yadc;

/**
 * A cached table is a table that back up the data using a MDB cache.
 * 
 * <p>
 * The table does not automatically support updating. That is done in the
 * {@link HistoryTable} class.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class CachedTable extends SingleWidthTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1069432107663660434L;

	/**
	 * The cache is transient because it is a file (a mdb file).
	 */
	protected transient ICache<DfsBar> _cache;

	protected CachedTable(DfsSymbol aSymbol, BarType aType, int aBaseWidth) {
		super(aSymbol, aType, aBaseWidth);

	}

	final long _getLastKey() throws DFSException {
		return getBarAbs(upLimit()).getTime();
	}

	@Override
	public final synchronized Bar getBarAbs(int i) throws DFSException {
		DfsBar bar = _cache.get(i);
		Bar ub = bar.decodeTo(_symbol.tick);
		return ub;
	}

	@Override
	public final int getBarsBetween(long startDatePar, long endDatePar)
			throws DFSException {

		long startDate = startDatePar;
		long endDate = endDatePar;

		if (size() == 0) {
			throw new DfsEmptyDatabaseException(getKey() + " is empty");
		}

		if (endDate < startDate) {
			throw new IllegalArgumentException("out of order arguments");
		}

		if (startDate < getStartingTime() || endDate > getEndingTime()) {
			throw new DfsInvalidRangeException(getKey()
					+ " out of range start: " + new Date(getStartingTime())
					+ " end: " + new Date(getEndingTime()) + " you req: "
					+ new Date(startDate) + " to: " + new Date(endDate));
		}

		int firstIndex = getMinimumIndexOfBarAfter(startDatePar);
		int lastIndex = getMaximumIndexOfBarWithin(endDatePar);

		if (firstIndex < 0 || lastIndex < 0) {
			/*
			 * This may happen, also if we have checked for starting and ending
			 * time bounds, because the methods will check the bar duration
			 * which is different from the simple start and end time.
			 */
			return 0;
		}

		int betweenBars = lastIndex - firstIndex + 1;
		return betweenBars;
	}

	@Override
	public final Bar getBarStartingAtTime(long timeInMillis)
			throws DFSException {

		long timeAdj = timeInMillis;
		switch (_type) {
		case DAILY:
			break;
		case MINUTE:
			timeAdj -= Yadc.ONE_MINUTE_MSEC;
			break;
		case RANGE: // override if you want the range
		default:
			throw new UnsupportedOperationException();

		}
		DfsBar barcache = _cache.getRecAt(timeAdj);
		if (barcache == null)
			return null;
		Bar ub = barcache.decodeTo(_symbol.tick);
		return ub;
	}

	@Override
	public final int getMinimumIndexOfBarAfter(long aTime) throws DFSException {
		/*
		 * We are searching for the minimum bar with the starting time entirely
		 * after aTime, so we are asking the floor.
		 */
		int answer;
		switch (_type) {
		case DAILY:
			answer = _cache.getCeilingIndexForTime(aTime - 1);
			break;
		case MINUTE:
			// the minimum here is the ceiling, because I store the ending
			// times, I must get the ending time up aTime.
			answer = _cache.getCeilingIndexForTime(aTime + Yadc.ONE_MINUTE_MSEC
					- 1);

			break;
		case RANGE:
			return RangeTableHelper.getMinimumIndexOfBarAfter(_cache, aTime);
		default:
			throw new IllegalStateException();

		}

		if (answer == size()) {
			return -size();
		}

		// answer is correct, 0 is a normal case
		return answer;
	}

	@Override
	public final long getStartingTime() throws DFSException {
		switch (_type) {
		case DAILY:
			return _getFirstKey();
		case MINUTE:
			return _getFirstKey() - Yadc.ONE_MINUTE_MSEC;
		case RANGE:
			/*
			 * In the case of range table the starting time is not really
			 * defined, at least in this version of the database. The safest
			 * idea is to have that the first range bar is not valid, and use
			 * its ending time as the starting time of the second bar, which in
			 * this way will be the first.
			 */
			if (size() < 1) { // size is overridden
				throw new DFSException(getKey()
						+ "cannot have starting time for size equal to zero");
			}
			/*
			 * Ok, the starting time of the table is simply the ending time of
			 * the first bar, which is not considered. I add 1 because this
			 * physically means that the first tick of the new bar is one
			 * instant after the ending time of the first bar
			 */
			return _getFirstKey();
		default:
			throw new IllegalStateException();
		}

	}

	/**
	 * Gets the starting time of the bar at a particular index.
	 * <p>
	 * Care is taken of the RANGE case, where the bar has a not definite
	 * duration.
	 * 
	 * @param aIndex
	 * @return
	 * @throws DFSException
	 */
	@Override
	public final long getStartingTimeOfBarAt(int aIndex) throws DFSException {
		DfsBar bar;
		switch (_type) {
		case DAILY:
			bar = _cache.get(aIndex);
			return bar.getPrimaryKey();
		case MINUTE:
			bar = _cache.get(aIndex);
			return bar.getPrimaryKey() - Yadc.ONE_MINUTE_MSEC;
		case RANGE:
			if (aIndex == 0) {
				throw new IllegalArgumentException(getKey()
						+ "Cannot have the starting index of the first bar");
			}
			bar = _cache.get(aIndex - 1);
			return bar.getPrimaryKey();
		default:
			throw new IllegalStateException();

		}

	}

	@Override
	public final int size() {
		if (_type == BarType.RANGE) {
			return RangeTableHelper.size(_cache);
		}
		return (int) _cache.size();
	}

	@Override
	public final void close() {
		_cache.compact(true);
	}

	/**
	 * returns true if the cache inside is empty, false otherwise.
	 * 
	 * @return true if the cache is empty.
	 */
	@Override
	public final boolean isEmpty() {
		return (_cache.size() == 0);
	}

	/**
	 * creates the cache
	 * 
	 * 
	 * @throws IOException
	 */
	protected abstract void _createCache() throws IOException;

	@Override
	public final int upLimit() {
		if (_cache == null) {
			try {
				_createCache();
			} catch (IOException e) {
				return 0;
			}
		}
		if (_type == BarType.RANGE) {
			return RangeTableHelper.upLimit(_cache);
		}
		return (int) (_cache.size() - 1);
	}

	public final long _getFirstKey() throws DFSException {
		if (_cache.size() == 0) {
			throw new IllegalStateException();
		}
		return _cache.getFirstKey();
	}

	@Override
	public final long getDateAfterXBarsFrom(long startDate, int numBars)
			throws DFSException {

		if (startDate < getStartingTime()) {
			throw new DfsInvalidRangeException(getKey()
					+ " out of range start: " + new Date(getStartingTime())
					+ " end: " + new Date(getEndingTime()) + " you req from: "
					+ new Date(startDate));
		}

		int firstIndex = getMinimumIndexOfBarAfter(startDate);
		int lastIndex = firstIndex + numBars - 1;

		if (lastIndex >= _cache.size()) {
			throw new DfsInvalidRangeException(getKey()
					+ "the request goes outside range max index "
					+ (upLimit() - firstIndex));
		}

		long answer = getEndingTimeOfBarAt(lastIndex);
		return answer;
	}

	@Override
	public final long getDateBeforeXBarsFrom(long endTime, int numBars)
			throws DFSException {

		if (endTime > getEndingTime()) {
			throw new DfsInvalidRangeException(getKey()
					+ " out of range start: " + new Date(getStartingTime())
					+ " end: " + new Date(getEndingTime())
					+ " you req before: " + new Date(endTime));
		}

		// int firstIndex = getMinimumIndexOfBarAfter(startDatePar);
		int lastIndex = getMaximumIndexOfBarWithin(endTime);

		int firstIndex = lastIndex - numBars + 1;

		if (firstIndex < 0) {
			throw new DfsInvalidRangeException(getKey()
					+ " date Before index: " + (firstIndex) + " nb " + numBars
					+ " size " + _cache.size() + " endTime "
					+ new Date(endTime) + " cache ends "
					+ new Date(_cache.getLastKey()));
		}

		long startingTime = getStartingTimeOfBarAt(firstIndex);
		return startingTime;
	}

	@Override
	public final long getEndingTime() throws DFSException {
		switch (_type) {
		case DAILY:
			return _getLastKey() + Yadc.ONE_DAY_MSEC;
		case MINUTE:
		case RANGE:
			return _getLastKey();
		default:
			throw new IllegalStateException();
		}

	}

	/**
	 * returns the ending time of the bar at a particular index.
	 * 
	 * @param aIndex
	 * @return the ending time
	 * @throws DFSException
	 */
	@Override
	public final long getEndingTimeOfBarAt(int aIndex) throws DFSException {
		DfsBar bar = _cache.get(aIndex);
		switch (_type) {
		case DAILY:
			return bar.getPrimaryKey() + Yadc.ONE_DAY_MSEC;
		case RANGE:
			if (aIndex == 0) {
				throw new IllegalArgumentException(
						"cannot give the ending time of the first bar");
			}
			//$FALL-THROUGH$ do not worry
		case MINUTE:
			return bar.getPrimaryKey();
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public final int getMaximumIndexOfBarWithin(long aTime) throws DFSException {
		/*
		 * maximum should be translated to floor function.
		 */

		int preFloor;
		switch (_type) {
		case DAILY:
			/*
			 * The daily table stores the initial time of the bars, so the
			 * maximum index of the bar within a time is simply the maximum bar
			 * whose start time is below the time offset by one day duration.
			 * 
			 * for example if you want to know the maximum bar which ends before
			 * July 2nd 06:00:00.000 my time you are searching for the bar (at
			 * most) with starting time July 1st 06:00:00.000, 24 hours before.
			 */
			preFloor = _cache.getFloorIndexForTime(aTime - Yadc.ONE_DAY_MSEC);
			break;
		case MINUTE:
			/*
			 * The minute bars stores the ending time of the minute considered
			 * so the the preFloor points to a bar whose ending time is already
			 * contained in the date
			 */
			preFloor = _cache.getFloorIndexForTime(aTime);
			break;
		case RANGE:
			return RangeTableHelper.getMaximumIndexOfBarWithin(_cache, aTime);
		default:
			throw new IllegalStateException();
		}

		return preFloor;

	}

	protected final DfsIntervalStats _getStatsState(EVisibleState state,
			boolean forceCheck) {
		int numBars;
		long startDate;
		long endDate;

		if (_cache == null) {
			// this may happen in the case of adding a symbol and not waiting
			// for the cache to appear
			numBars = 0;
			startDate = -1;
			endDate = -1;

		} else {
			// important, size is overridden in RangeHistoryTable
			numBars = size();
			if (numBars == 0) {
				startDate = -1;
				endDate = -1;
			} else {
				try {
					startDate = getStartingTime();
					endDate = getEndingTime();

					// this will check that the statistics to the outside are
					// good.
					if (forceCheck) {
						_checkStatCoherence(numBars, startDate, endDate);
					}

				} catch (DFSException e) {
					e.printStackTrace();
					startDate = -1;
					endDate = -1;
				}
			}
		}
		DfsIntervalStats dis = new DfsIntervalStats(state, numBars, startDate,
				endDate);
		return dis;
	}

}
