package com.mfg.dfs.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.RequestParams;
import com.mfg.dfs.conn.IDatabaseChangeListener;
import com.mfg.dfs.data.ContinuousData;
import com.mfg.dfs.data.MaturityData;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.dfs.data.SingleWidthTable;
import com.mfg.dfs.data.SymbolData;
import com.mfg.dfs.misc.IDataFeed;

/**
 * a simple abstract class which is able to contains different tables.
 * 
 * <p>
 * The use of this class is used to make a common ancestor for the two classes
 * {@linkplain ContinuousData} and {@linkplain MaturityData}.
 * 
 * <p>
 * This class is then only an implementation inheritance.
 * 
 * @author Sergio
 * 
 */
public abstract class HistoryTablesContainer implements Serializable {

	protected HistoryTablesContainer() {
		// just to make sure that you don't create it from the outside.
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1453500447562206028L;

	/**
	 * I have a map of tables, every maturity has its own set of data (it may be
	 * blocked, running, waiting for data..., usually the maturity starts to
	 * have data one year and a quarter before the expiry date, but this is not
	 * fixed for all the symbols and it may change).
	 * 
	 * <p>
	 * The idea is that we have a different change for this kind of data,
	 * because only the data provider is able to give to us the real answer to
	 * this question.
	 * 
	 * 
	 */
	protected Map<BarType, SingleWidthTable> _tables = new Hashtable<>();

	/**
	 * 
	 */
	private transient IDatabaseChangeListener _listener;

	public IBarCache getCache(BarType aType, int nUnits) throws DFSException {
		return _tables.get(aType).getCache(nUnits);
	}

	public final IBarCache getCache(RequestParams aReq) throws DFSException {
		return _tables.get(aReq.getBarType()).getCache(aReq);
	}

	/**
	 * Every Maturity can have different states and it is also a FSM, but in
	 * some way different from. The {@linkplain MaturityData} is only used when
	 * the maturity is ready and has some data available.
	 * 
	 * @param aFeed
	 * @throws IOException
	 *             if something goes wrong.
	 * @throws DFSException
	 */
	public final void doOneStep(SymbolData aSymbolData, IDataFeed aFeed,
			boolean isFromScheduler) throws IOException, DFSException {
		boolean changedSomething = false;
		for (SingleWidthTable ht2 : _tables.values()) {
			changedSomething |= ht2.doOneStep(aSymbolData, aFeed,
					isFromScheduler);
		}

		if (_listener != null && changedSomething) {
			MaturityStats status = this.getStatus(false);

			// notify the listener
			_listener.onSymbolChanged(this.getCompleteSymbol(), status);
		}
	}

	protected abstract String getCompleteSymbol();

	public abstract MaturityStats getStatus(boolean forceCheck);

	public MaturityStats getStatus(Maturity aMaturity, boolean forceCheck) {
		MaturityStats ms = new MaturityStats(aMaturity);

		for (SingleWidthTable ht : _tables.values()) {
			ms._map.put(ht.getType(), ht.getStats(forceCheck));
		}

		return ms;
	}

	public int getBarsBetween(BarType aType, int barWidth, long startDate,
			long endDate) throws DFSException {
		return _tables.get(aType).getBarsBetween(barWidth, startDate, endDate);
	}

	public long getDateBeforeXBarsFrom(BarType aType, int barWidth,
			long endTime, int numBars) throws DFSException {
		return _tables.get(aType).getDateBeforeXBarsFrom(barWidth, endTime,
				numBars);
	}

	public long getDateAfterXBarsFrom(BarType aType, int barWidth,
			long startDate, int numBars) throws DFSException {
		return _tables.get(aType).getDateAfterXBarsFrom(barWidth, startDate,
				numBars);
	}

	public int getBarCount(BarType aType, int barWidth) throws DFSException {
		return _tables.get(aType).getBarCount(barWidth);
	}

	public abstract boolean needsRecompute();

	/**
	 * Watches this container. The listener will be notified when a change
	 * happens.
	 */
	public void watch(IDatabaseChangeListener aListener) {
		_listener = aListener;
	}

	public void unwatch() {
		_listener = null;
	}

}
