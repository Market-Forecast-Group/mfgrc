package com.mfg.dfs.data;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.mfg.common.Bar;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.RequestParams;
import com.mfg.utils.Utils;
import com.mfg.utils.Yadc;

/**
 * A simple read only wrapper to a history cache.
 * 
 * <p>
 * This slicer substitues what we were calling a "view" for the table. The view
 * has disappeared because the slicer can handle all the cases for a request.
 * 
 * <p>
 * The slicer is independent on the actual type for the table. It can "slice"
 * every {@link IHistoryTable} object.
 * 
 * 
 * @author Ferrentino
 * 
 */
public class HistoryTableSlicer implements IBarCache {

	/**
	 * A class that allows the iteration over the bars in a slicer.
	 * 
	 * <p>
	 * It has the possibility to start at a particular index in the sequence.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private final class IteratorImplementation implements Iterator<Bar> {
		private int _index = 0;

		public IteratorImplementation() {
			// simple constructor that points to the beginning of cache.
		}

		/**
		 * builds an interator pointing to a determined position in the
		 * sequence. It is an error to point to past the end of the sequence.
		 * 
		 * @param startingIndex
		 *            , must be greater or equal to zero and less or equal than
		 *            the size of the sequence.
		 */
		public IteratorImplementation(int startingIndex) {
			if (_index < 0 || _index > size()) {
				throw new IllegalArgumentException();
			}
			_index = startingIndex;
		}

		@Override
		public boolean hasNext() {
			return _index < size();
		}

		@Override
		public Bar next() {
			try {
				return getBar(_index++);
			} catch (DFSException e) {
				e.printStackTrace();
				throw new NoSuchElementException(e.toString());
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private IHistoryTable _table;

	private final int _lowIdx;

	/**
	 * The higher index of the table. It could be -1 in this case it is
	 * variable, and it is equal to size()-1. Where the size of the table could
	 * grow.
	 */
	private final int _highIdx;

	public HistoryTableSlicer(IHistoryTable aCC) {
		_table = aCC;
		_lowIdx = 0;
		_highIdx = aCC.size() - 1;
	}

	public HistoryTableSlicer(IHistoryTable aTable, RequestParams aReq)
			throws DFSException {
		_table = aTable;
		switch (aReq.getReqType()) {
		case ALL_BARS:
			_lowIdx = 0;
			_highIdx = -1;
			break;
		case BETWEEN_TIME_X_Y:
			_lowIdx = _getLowBound(aReq.getStartTime());
			_highIdx = _getHighBound(aReq.getEndTime());
			break;
		case NUM_BARS:
			_highIdx = -1; // I am not fixed
			_lowIdx = Math.max(0, _table.size() - aReq.getNumBarsOrDays());
			break;
		case NUM_DAYS:
			_highIdx = -1; // not fixed, return the maximum
			long timeBefore = Yadc.getNCalendarDaysBeforeNow(aReq
					.getNumBarsOrDays());
			_lowIdx = _getLowBound(timeBefore);
			break;
		case X_BARS_AFTER_Y:
			_lowIdx = _getLowBound(aReq.getStartTime());
			_highIdx = Math.min(_table.size() - 1,
					_lowIdx + aReq.getNumBarsOrDays());
			if (_highIdx > _table.upLimit()) {
				throw new DFSException("cannot have more than "
						+ (_table.upLimit() - _lowIdx) + " bars after "
						+ new Date(aReq.getStartTime()));
			}

			break;
		case X_BARS_BEFORE_Y:
			_highIdx = _getHighBound(aReq.getEndTime());
			_lowIdx = Math.max(0, _highIdx - aReq.getNumBarsOrDays());
			break;
		default:
			throw new IllegalStateException();
		}
	}

	private int _getHighBound(long endTime) throws DFSException {
		int res = _table.getMaximumIndexOfBarWithin(endTime);
		if (res < 0) {
			throw new DFSException(_table.getKey() + "cannot have bars before "
					+ new Date(endTime) + " its first is "
					+ new Date(_table.getStartingTime()));
		}
		return res;
	}

	private int _getLowBound(long timeBefore) throws DFSException {
		int res = _table.getMinimumIndexOfBarAfter(timeBefore);
		if (res < 0) {
			throw new DFSException(_table.getKey()
					+ " cannot have a bar after " + new Date(timeBefore)
					+ " its last is " + new Date(_table.getEndingTime()));
		}
		return res;
	}

	@Override
	public void close() {
		if (_table == null) {
			return;
		}
		Utils.debug_var(939103, "Closing of slicer ", this,
				" referring on table ", _table);
		_table.closeCache(this);
		_table = null; // after the close this view is invalid
	}

	@Override
	public Bar getBar(int index) throws DFSException {
		return _table.getBarAbs(index + _lowIdx);
	}

	@Override
	public Maturity getMaturity() {
		return _table.getMaturity();
	}

	@Override
	public DfsSymbol getSymbol() {
		return _table.getSymbol();
	}

	@Override
	public Iterator<Bar> iterator() {
		/**
		 * I simply return a fresh copy of a local iterator.
		 * 
		 * <p>
		 * This iterator does not alter the cache in any way, it is a simple
		 * read forward iterator.
		 */
		return new IteratorImplementation();
	}

	/**
	 * Returns an iterator pointing to the index given.
	 * 
	 * <p>
	 * The index is the next-to-be taken element. For example if you pass 1, the
	 * {@link Iterator#next()} element will return the second element.
	 * 
	 * @param startingIndex
	 * @return a newly created iterator.
	 */
	public Iterator<Bar> iterator(int startingIndex) {
		return new IteratorImplementation(startingIndex);
	}

	@Override
	public void purgeOldBars(int lastAffirmedSize) {
		// here it is a no-op
	}

	@Override
	public int size() {
		if (_highIdx > 0) {
			return _highIdx - _lowIdx + 1;
		}
		return _table.size() - _lowIdx; // in this case the +1 is not needed.
	}

	@Override
	public String toString() {
		return "From " + this._lowIdx + " to " + this._highIdx + " / ["
				+ this.size() + "] " + this._table.toString();
	}

}
