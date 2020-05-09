package com.mfg.dfs.data;

import static com.mfg.utils.Utils.debug_var;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import com.mfg.common.Bar;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsInvalidRangeException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.TimeBarIntegrator;
import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.misc.IDataFeed;
import com.mfg.utils.IntArray;
import com.mfg.utils.U;
import com.mfg.utils.Yadc;

/**
 * this class should simply take a {@linkplain TimeHistoryTable} which is a
 * table of time bars and builds a multiple view of it, in which each bar is a
 * multiple of the real bars.
 * 
 * @author Sergio
 * 
 */
public class MultipleTimeTable extends BaseTable {

	private final SingleWidthTable _realTable;
	private final int _nUnits;

	/**
	 * These are the bars, at least the initial start index.
	 */
	private IntArray _bars;
	private final long _singleDuration;

	/**
	 * This is the index in the real table from which the last incomplete bar
	 * starts.
	 */
	private int _lastIncompleteBarStart;

	/**
	 * This is the physical time of the first real bar.
	 */
	private long _firstRealBarPhysicalTime;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8110942006774329625L;

	public MultipleTimeTable(SingleWidthTable abstractTable, int nUnits)
			throws DFSException {
		_realTable = abstractTable;
		_nUnits = nUnits;

		if (_realTable.getType() == BarType.DAILY) {
			_singleDuration = Yadc.ONE_DAY_MSEC;
		} else if (_realTable.getType() == BarType.MINUTE) {
			_singleDuration = Yadc.ONE_MINUTE_MSEC;
		} else {
			throw new DFSException("cannot have a range bar here");
		}
		_buildMultipleView();
	}

	/**
	 * helper method to initialize the multiple view which is used to build the
	 * multiple size vector.
	 * 
	 * @throws DFSException
	 */
	@SuppressWarnings({ "boxing" })
	private void _buildMultipleView() throws DFSException {

		debug_var(381933, "Building chunks for table ", getKey(),
				" and duration ", this._nUnits);

		// OK, Let's start to have the file name
		_realTable.getSymbol();
		_realTable.getMaturity();
		_realTable.getType();

		File cacheFile = MfgMdbSession.getInstance().getMultipleCacheKeyFile(
				_realTable.getSymbol().prefix, _realTable.getMaturity(),
				_realTable.getType(), _nUnits);
		debug_var(910392, "Searching for file ", cacheFile.getAbsolutePath());
		if (!cacheFile.exists() || !_loadCacheFile(cacheFile)) {
			_bars = new IntArray();
			_lastIncompleteBarStart = 0;
			_firstRealBarPhysicalTime = _realTable.size() == 0 ? Long.MIN_VALUE
					: _realTable.getStartingTime();
		}

		int oldSize = _bars.size();
		_updateMultipleArray();

		if (oldSize != _bars.size()) {
			_saveCacheFile(cacheFile);
		}
	}

	@SuppressWarnings({ "boxing" })
	private boolean _loadCacheFile(File cacheFile) throws DFSException {

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				cacheFile));) {
			_firstRealBarPhysicalTime = ois.readLong();
			_bars = (IntArray) ois.readObject();
			_lastIncompleteBarStart = ois.readInt();

			// check if the table has been truncated or has been invalidated...
			if (_firstRealBarPhysicalTime != _realTable.getStartingTime()) {
				debug_var(
						399404,
						getKey(),
						" the cache file is referring to another table... I expected ",
						new Date(_realTable.getStartingTime()), " I got ",
						new Date(_firstRealBarPhysicalTime));
				return false;
				// this is not really an exception, but a "normal" condition
			}

			if (_lastIncompleteBarStart > _realTable.size()) {
				debug_var(837193, getKey(), " the table was pointing at ",
						_lastIncompleteBarStart, " now the table is ",
						_realTable.size(), " so I have to truncate it");
				return false;
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new DFSException(e); // something is bad, the cache file is
										// corrupt
		}

		return true;

	}

	private void _saveCacheFile(File cacheFile) {
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(cacheFile))) {

			oos.writeLong(_firstRealBarPhysicalTime);
			oos.writeObject(_bars);
			oos.writeInt(_lastIncompleteBarStart);
			debug_var(381994, getKey(), " Saved the cache file");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * helper method to fill the array of the multiple indeces using a starting
	 * point.
	 * <p>
	 * This method is used during the initial phase of the table and after, when
	 * the table wants to fill the real time bars.
	 * 
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	private void _updateMultipleArray() throws DFSException {
		int size = _realTable.size();
		int i = _lastIncompleteBarStart;

		TimeBarIntegrator tbi = new TimeBarIntegrator(_singleDuration,
				this._nUnits, _realTable.getType() == BarType.DAILY);

		int virtualIndex = _bars.size();
		// int startNextBarReal = 0;
		int previousStartReal = _lastIncompleteBarStart;

		int progress = i + ((size - _lastIncompleteBarStart) / 20);

		for (; i < size; ++i) {

			if (i > progress) {
				debug_var(192923, getKey(), " processed ", progress,
						" bars out of ", size);
				progress += ((size - _lastIncompleteBarStart) / 20);
			}

			Bar bar = _realTable.getBarAbs(i);
			// debug_var(718381, "[", i, "] give: ", bar);
			Bar formingBar = tbi.acceptBar(bar);
			Bar completeBar = tbi.getLastCompleBar();

			if (completeBar != null) {

				int realEnd;
				// if the forming bar is null, then this ends the period,
				// otherwise
				// this index is the first of the next bar.
				if (formingBar != null) {
					realEnd = i - 1;
				} else {
					realEnd = i;
				}

				_bars.add(previousStartReal);

				// startNextBarReal = i;

				if (tbi.isLastCompletedBarFull()) {
					if (realEnd - previousStartReal + 1 != _nUnits) {
						assert (false);
					}
				} else {
					if (realEnd - previousStartReal + 1 >= _nUnits) {
						assert (false);
					}
				}

				previousStartReal = realEnd + 1;
				// In any case I advance
				virtualIndex++;
			}
			// }

		}

		_lastIncompleteBarStart = previousStartReal;

		if (_bars.size() == 0) {
			U.debug_var(191019, getKey(), " not one bar created");
			return;
		}

		debug_var(183913, "I have ", virtualIndex, " bars, size real ", size,
				" last complete bar starts @ ", _bars.get(virtualIndex - 1),
				" last incomplete @ ", _lastIncompleteBarStart);
	}

	@Override
	public void closeCache(IBarCache aCache) {
		// I simply pass the method to the real table
		_realTable.closeCache(aCache);
	}

	@Override
	public boolean doOneStep(SymbolData aSymbolData, IDataFeed aFeed,
			boolean isFromScheduler) throws DFSException {

		/*
		 * Simple parameter check, they are phony
		 */
		assert (aSymbolData == null);
		assert (aFeed == null);
		assert (isFromScheduler == false);

		// I have to know if I have new bar(s) here.
		debug_var(879823, getKey(), " multiple ****************** do one step!");

		int oldSize = _bars.size();
		_updateMultipleArray();

		int newSize = _bars.size();
		for (int i = oldSize; i < newSize; ++i) {
			Bar bar = getBarAbs(i);
			debug_var(829301, getKey(), " created bar ", bar);
		}

		return false;
	}

	@Override
	public Bar getBarAbs(int aIdx) throws DFSException {
		TimeBarIntegrator tbi = new TimeBarIntegrator(_singleDuration, _nUnits,
				_realTable.getType() == BarType.DAILY);
		Bar completeBar = null;

		/*
		 * there is no condition in the for loop, because it is exited when a
		 * new bar is created
		 */
		for (int i = _bars.get(aIdx); /* i < _nUnits */; i++) {
			Bar realBar = _realTable.getBarAbs(i);
			tbi.acceptBar(realBar);
			completeBar = tbi.getLastCompleBar();
			if (completeBar != null) {
				break;
			}
		}

		return completeBar;
	}

	@Override
	public int getBarsBetween(long startDate, long endDate) throws DFSException {
		// Ok, to get the bars between I have first to know the virtual index of
		// the bar with a certain time

		if (startDate > endDate) {
			throw new DFSException(getKey() + " out of order");
		}

		if (startDate < getStartingTime() || endDate > getEndingTime()) {
			throw new DFSException(getKey() + " out of range");
		}

		/*
		 * even if start and end date are safely inside the bounds of the
		 * multiple table they may not be inside the bounds for the real table
		 * underneath, this because the multiple table may have a starting time
		 * lower than the starting time of the real table (for example in daily
		 * bars, if the first bar of the real table is at the end of a period),
		 * so I must clamp the start and real dates with the real values.
		 */

		long startDateAdj = Math.max(startDate, _realTable.getStartingTime());
		long endDateAdj = Math.min(endDate, _realTable.getEndingTime());

		int startIndex = _realTable.getMinimumIndexOfBarAfter(startDateAdj);
		int endIndex = _realTable.getMaximumIndexOfBarWithin(endDateAdj);

		/*
		 * This may happen because the real table is really small, in tests this
		 * could happen for example if the real table is composed of one bar
		 * only.
		 */
		if (endIndex < 0 || endIndex < startIndex) {
			return 0;
		}

		int virtualIndex = _bars.binarySearch(startIndex);
		// clamp the starting to the real starting of the bar
		long multipleBarStartingTime = getStartingTimeOfBarAt(virtualIndex);
		if (multipleBarStartingTime < startDate) {
			virtualIndex++; // take the next
		}

		int endVirtual = _bars.binarySearch(endIndex);
		long multipleBarEndingTime = getEndingTimeOfBarAt(endVirtual);
		if (multipleBarEndingTime > endDate) {
			endVirtual--; // this is not contained
		}

		int res = endVirtual - virtualIndex + 1;
		/*
		 * I could have returned a negative index; this may happen if the two
		 * indexes are too near, and this multiple table is referring to a very
		 * small table.
		 */
		return Math.max(res, 0);
	}

	@Override
	public long getDateAfterXBarsFrom(long startDate, int numBars)
			throws DFSException {

		if (startDate < getStartingTime()) {
			throw new DfsInvalidRangeException(getKey()
					+ " out of range start: " + new Date(getStartingTime())
					+ " end: " + new Date(getEndingTime()) + " you req from: "
					+ new Date(startDate));
		}

		long startDateAdj = Math.max(startDate, _realTable.getStartingTime());
		int firstIndex = getMinimumIndexOfBarAfter(startDateAdj);
		int lastIndex = firstIndex + numBars - 1;

		if (lastIndex >= size()) {
			throw new DfsInvalidRangeException(getKey()
					+ "the request goes outside range max index "
					+ (upLimit() - firstIndex));
		}

		long answer = getEndingTimeOfBarAt(lastIndex);
		return answer;
	}

	@Override
	public long getDateBeforeXBarsFrom(long endTime, int numBars)
			throws DFSException {
		if (endTime > getEndingTime()) {
			throw new DfsInvalidRangeException(getKey()
					+ " out of range start: " + new Date(getStartingTime())
					+ " end: " + new Date(getEndingTime())
					+ " you req before: " + new Date(endTime));
		}

		// int firstIndex = getMinimumIndexOfBarAfter(startDatePar);

		long endDateAdj = Math.min(endTime, _realTable.getEndingTime());
		int lastIndex = getMaximumIndexOfBarWithin(endDateAdj);

		int firstIndex = lastIndex - numBars + 1;

		if (firstIndex < 0) {
			throw new DfsInvalidRangeException(getKey()
					+ " date Before index: " + (firstIndex) + " nb " + numBars
					+ " size " + size() + " endTime " + new Date(endTime)
					+ " cache ends " + new Date(_realTable.getEndingTime()));
		}

		long startingTime = getStartingTimeOfBarAt(firstIndex);
		return startingTime;

	}

	@Override
	public long getEndingTime() throws DFSException {
		return getEndingTimeOfBarAt(upLimit());
	}

	@Override
	public String getKey() {
		return _realTable.getKey() + "_" + _nUnits;
	}

	@Override
	public Maturity getMaturity() {
		return _realTable.getMaturity();
	}

	@Override
	public int getMaximumIndexOfBarWithin(long cDate) throws DFSException {

		int realMaximum = _realTable.getMaximumIndexOfBarWithin(cDate);

		int virtualMaximum = _bars.binarySearchPure(realMaximum);

		if (virtualMaximum >= 0) {
			return virtualMaximum;
		}

		if (virtualMaximum == -1) {
			// before the minimum, all the bars are higher
			return -1;
		}

		// all the bars are lower
		return _bars.size() - 1;
	}

	@Override
	public int getMinimumIndexOfBarAfter(long aTime) throws DFSException {

		/*
		 * the minimum index after a certain time is a bar that has a <starting>
		 * time above the time. Then we have to search for the multiple bar
		 * whose starting time is above that time, this means that the exact
		 * match must be found in the int array or the next one, because I have
		 * to be sure that the bar will start after the time given.
		 */

		int realMinimumIndex = _realTable.getMinimumIndexOfBarAfter(aTime);
		int virtualIndex = _bars.binarySearchPure(realMinimumIndex);

		if (virtualIndex >= 0) {
			return virtualIndex; // this is it
		}

		// virtual index is -(insertion point) -1
		int insertionPoint = -virtualIndex - 1;

		if (insertionPoint == _bars.size()) {
			return -insertionPoint;
		}

		return insertionPoint;
	}

	@Override
	public long getStartingTime() throws DFSException {
		/*
		 * The starting time is simply the starting time of the first bar
		 */
		return getStartingTimeOfBarAt(0);
	}

	@Override
	public DfsSymbol getSymbol() {
		return _realTable.getSymbol();
	}

	@Override
	public BarType getType() {
		return _realTable.getType();
	}

	@Override
	public boolean isEmpty() {
		return _realTable.isEmpty();
	}

	@Override
	public int size() {
		return _bars.size();
	}

	@Override
	public int upLimit() {
		return _bars.size() - 1;
	}

	@Override
	public long getEndingTimeOfBarAt(int aIndex) throws DFSException {
		if (size() == 0) {
			throw new DFSException(getKey() + " empty");
		}
		TimeBarIntegrator tbi = new TimeBarIntegrator(_singleDuration, _nUnits,
				_realTable.getType() == BarType.DAILY);
		int idx = _bars.get(aIndex);
		Bar realBar = _realTable.getBarAbs(idx);
		Bar formingBar = tbi.acceptBar(realBar);

		/*
		 * forming bar can be null, because the first is also the last bar
		 */
		if (formingBar == null) {
			formingBar = tbi.getLastCompleBar();
		}

		switch (_realTable.getType()) {
		case DAILY:
			return formingBar.getTime() + tbi.getPeriodLength();
		case MINUTE:
			return formingBar.getTime();
		case RANGE:
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public long getStartingTimeOfBarAt(int aIndex) throws DFSException {
		if (size() == 0) {
			throw new DFSException(getKey() + " empty");
		}
		TimeBarIntegrator tbi = new TimeBarIntegrator(_singleDuration, _nUnits,
				_realTable.getType() == BarType.DAILY);
		int idx = _bars.get(aIndex);
		Bar realBar = _realTable.getBarAbs(idx);
		Bar formingBar = tbi.acceptBar(realBar);

		/*
		 * forming bar can be null, because the first is also the last bar
		 */
		if (formingBar == null) {
			formingBar = tbi.getLastCompleBar();
		}

		switch (_realTable.getType()) {
		case DAILY:
			return formingBar.getTime();
		case MINUTE:
			return formingBar.getTime() - tbi.getPeriodLength();
		case RANGE:
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public Bar getBarStartingAtTime(long time) throws DFSException {
		throw new UnsupportedOperationException();
	}

	@Override
	public DfsIntervalStats getStats(boolean forceCheck) {
		throw new UnsupportedOperationException();
	}
}
