package com.mfg.dfs.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.RequestParams;
import com.mfg.dfs.misc.IDataFeed;
import com.mfg.utils.U;

/**
 * The single width table is a table which is used to store the single width
 * bars.
 * 
 * <p>
 * It has also the possibility to store the multiple bars tables which are in
 * reality views on the single width table.
 * 
 * <p>
 * The table in reality can be also a "multiple" table, in the sense that this
 * table is actually composed of different bars with a multiple duration, they
 * come from a CSV external data source, or by other means and they are not
 * updatable.
 * 
 * @author Sergio
 * 
 */
public abstract class SingleWidthTable extends BaseTable {

	protected transient boolean _needsRecompute = false;

	/**
	 * This stores all the caches which have been given to the system; the map
	 * will be empty if the table has no more active references
	 * 
	 * <p>
	 * The key of the map is the cache, the value is a boolean which tells us if
	 * the cache is active or not.
	 * 
	 * <p>
	 * It would have been possible also to store <b>only</b> the active caches
	 * in the map, but maybe this solution has the advantage of giving a clear
	 * idea of the "load" of the table.
	 * 
	 */
	protected transient HashMap<IBarCache, Boolean> _cacheMap = new HashMap<>();

	/**
	 * This maps stores the multiple history table views, that is the tables
	 * which are used to store the multiple bars (for example the 5 minutes
	 * tables, and so on).
	 */
	protected transient HashMap<Integer, IHistoryTable> _multipleTables = new HashMap<>();

	/**
	 * 
	 */
	private static final long serialVersionUID = -7166355038634232051L;

	private static final int MAX_UNITS = 360;

	/**
	 * The symbol of this table.
	 */
	protected DfsSymbol _symbol;

	/**
	 * The type for this table.
	 */
	protected final BarType _type;

	/**
	 * The base width of this table.
	 */
	private int _baseWidth;

	/**
	 * protected constructor used to initialize the final fields.
	 * 
	 * @param aSymbol
	 * @param aType
	 * 
	 * @param aBaseWidth
	 *            it indicates the base width for this table, which is the
	 *            minimum unit of measure for it. For example if this is a
	 *            MINUTE table with a baseWidth of 5 it means that it is
	 *            composed of 5-minutes bars.
	 * 
	 *            <p>
	 *            That means also that this table can give to the user a 10
	 *            minutes view, or a 15 minutes view, but <b>not</b> a 1 minute
	 *            view.
	 */
	protected SingleWidthTable(DfsSymbol aSymbol, BarType aType, int aBaseWidth) {
		if (aType == BarType.RANGE && aBaseWidth != 1) {
			throw new IllegalArgumentException(
					"Cannot have a width != 1 with a range table.");
		}

		_symbol = aSymbol;
		_type = aType;
		_baseWidth = aBaseWidth;
	}

	@SuppressWarnings("boxing")
	protected boolean _atLeastOneViewIsOpen() {
		synchronized (_cacheMap) {
			for (Entry<IBarCache, Boolean> entry : _cacheMap.entrySet()) {
				if (entry.getValue()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * helper method used only to check coherence in the multiple tables.
	 * 
	 * <p>
	 * It iterates and builds all the multiple tables until a certain length and
	 * each time it will check their coherence
	 * 
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	protected void _checkCoherenceOfMultipleTable() throws DFSException {
		if (_type == BarType.RANGE) {
			U.debug_var(910391, getKey(),
					" Cannot have multiple bars for this type");
			return;
		}

		int endingWidth;
		int step;
		int startingWidth;

		switch (_type) {
		case DAILY:
			startingWidth = 2;
			endingWidth = 30;
			step = 1;
			break;
		case MINUTE:
			startingWidth = 5;
			endingWidth = 180;
			step = 13;
			break;
		case RANGE:
		default:
			throw new IllegalStateException();
		}

		for (int i = startingWidth; i < endingWidth; i += step) {
			IHistoryTable mTable = _getMultipleTable(i);

			int size = mTable.size();
			if (size == 0) {
				U.debug_var(190301, mTable.getKey(),
						" is empty so it is coherent, ending cycle");
				break;
			}
			long startingTime = mTable.getStartingTime();
			long endingTime = mTable.getEndingTime();

			U.debug_var(192993, mTable.getKey(), " has ", size, " bars from ",
					new Date(startingTime), " to ", new Date(endingTime));
			((BaseTable) mTable)._checkStatCoherence(size, startingTime,
					endingTime);
		}
	}

	/**
	 * Simple helper method to create multiple table.
	 * 
	 * @param nUnits
	 * @return
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	private final IHistoryTable _getMultipleTable(int nUnitsPar)
			throws DFSException {

		int nUnits = nUnitsPar;

		if (nUnits % _baseWidth != 0) {
			throw new DFSException("Cannot have width " + nUnits
					+ " because my base is " + _baseWidth);
		}

		nUnits /= _baseWidth;

		if (nUnits == 1) {
			/*
			 * this is a abstract class. The concrete classes do implement the
			 * interface
			 */
			return this;
		}

		if (_multipleTables.get(nUnits) == null) {
			if (_type == BarType.RANGE) {
				throw new DFSException("cannot do multiple ranges, for now");
			}
			if (nUnits < 2 || nUnits > MAX_UNITS) {
				throw new DFSException("Cannot create a multiple table of "
						+ nUnits + " units");
			}
			_multipleTables.put(nUnits, new MultipleTimeTable(this, nUnits));
		}
		return _multipleTables.get(nUnits);
	}

	protected abstract void _truncateImpl(long truncateDate)
			throws DFSException;

	/**
	 * protected method to update the multiple tables, if there are any
	 * 
	 * <p>
	 * The multiple tables are table which are composed of bars of width greater
	 * than one. These bars are not real, in the sense that they are used to
	 * 
	 * @param isFromScheduler
	 * @throws DFSException
	 * 
	 */
	protected void _updateMultipleTables() throws DFSException {
		for (IHistoryTable hit : _multipleTables.values()) {
			/*
			 * The multiple table does not need the information whether it is
			 * from scheduler or not.
			 */
			hit.doOneStep(null, null, false);
		}
	}

	public abstract void close();

	@Override
	public final void closeCache(IBarCache aCache) {
		Boolean oldVal = _cacheMap.remove(aCache);
		// If this fails then there is some strange mapping in the map
		assert (oldVal != null);
	}

	@Override
	public abstract boolean doOneStep(SymbolData aSymbolData, IDataFeed aFeed,
			boolean isFromScheduler) throws DFSException;

	public abstract void forceUpdate();

	@SuppressWarnings("boxing")
	public final int getBarCount(int barWidth) throws DFSException {

		if (barWidth == 1) {
			return size();
		}

		if (_multipleTables.get(barWidth) == null) {
			throw new DFSException("bar witdth " + barWidth + " not present");
		}

		return _multipleTables.get(barWidth).size();
	}

	public final int getBarsBetween(int barWidth, long startDate, long endDate)
			throws DFSException {

		IHistoryTable table = _getMultipleTable(barWidth);
		return table.getBarsBetween(startDate, endDate);
	}

	public int getBaseWidth() {
		return _baseWidth;
	}

	@SuppressWarnings("boxing")
	public final IBarCache getCache(int nUnits) throws DFSException {
		IBarCache cache;

		IHistoryTable table = _getMultipleTable(nUnits);
		cache = new HistoryTableSlicer(table);

		/*
		 * the complete cache is always a closed request
		 */
		_cacheMap.put(cache, false);
		return cache;
	}

	@SuppressWarnings("boxing")
	public IBarCache getCache(RequestParams aReq) throws DFSException {
		IBarCache cache;
		int nUnits = aReq.getBarWidth();

		IHistoryTable table = _getMultipleTable(nUnits);
		cache = new HistoryTableSlicer(table, aReq);

		_cacheMap.put(cache, aReq.isOpenRequest());
		return cache;
	}

	public long getDateAfterXBarsFrom(int barWidth, long startDate, int numBars)
			throws DFSException {
		IHistoryTable table = _getMultipleTable(barWidth);
		return table.getDateAfterXBarsFrom(startDate, numBars);
	}

	public long getDateBeforeXBarsFrom(int barWidth, long endTime, int numBars)
			throws DFSException {
		IHistoryTable table = _getMultipleTable(barWidth);
		return table.getDateBeforeXBarsFrom(endTime, numBars);
	}

	/**
	 * gets the maturity of this table.
	 * 
	 * @return
	 */
	@Override
	public abstract Maturity getMaturity();

	@Override
	public final DfsSymbol getSymbol() {
		return _symbol;
	}

	@Override
	public BarType getType() {
		return _type;
	}

	@Override
	public abstract boolean isEmpty();

	@SuppressWarnings("static-method")
	protected boolean isLeftTruncated() {
		/*
		 * it is overridden in HistoryTable.
		 */
		return false;
	}

	public abstract boolean isReady();

	/**
	 * returns the recompute flag.
	 * <p>
	 * This flag is on when we the continous table has found that it could
	 * expand its view.
	 * 
	 * @return
	 */
	public final boolean needsRecompute() {
		boolean res = _needsRecompute;
		_needsRecompute = false;
		return res;
	}

	/**
	 * This is called <b>only</b> for the continuous table, because Java
	 * serialization will call only one "readResolve" per object read from the
	 * stream.
	 * 
	 * <p>
	 * This is why you have two readResolve in the project, because the other is
	 * about the {@linkplain HistoryTable} branch.
	 * 
	 * @return this object with the transient maps created.
	 * @throws DFSException
	 */
	protected Object readResolve() throws DFSException {
		_cacheMap = new HashMap<>();
		_multipleTables = new HashMap<>();
		if (_baseWidth == 0) {
			// this happens for old tables in old caches.
			_baseWidth = 1;
		}
		return this;
	}

	public void truncate(long truncateDate) throws DFSException {
		_truncateImpl(truncateDate);
		_needsRecompute = true;
	}

}
