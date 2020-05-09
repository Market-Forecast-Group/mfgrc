package com.mfg.dfs.data;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.marketforescastgroup.logger.LogManager;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.IBarCache;
import com.mfg.common.Maturity;
import com.mfg.common.RequestParams;
import com.mfg.dfs.conn.DfsCacheRepo;
import com.mfg.dfs.conn.DfsCacheRepo.GetSymbolDataAns;
import com.mfg.dfs.conn.DfsSchedulingTimes;
import com.mfg.dfs.conn.DfsSymbolList;
import com.mfg.dfs.conn.IDatabaseChangeListener;
import com.mfg.dfs.misc.IDataFeed;
import com.mfg.utils.U;

/**
 * This will be the data model for all the tables in the system.
 * 
 * <p>
 * This may be viewed as the "controller" part of the application, because it
 * holds the data model thread which is responsible to update the tables also
 * without a user intervention
 * 
 * 
 * 
 * @author Sergio
 * 
 */
public class DataModel {

	/**
	 * The data model has the cache. This is the short term storage where the
	 * data is stored.
	 * 
	 * for now there exist only the short term storage, the history tables are
	 * stored here.
	 */
	private DfsCacheRepo _cache;

	private IDataFeed _dataFeed;

	/**
	 * This is the data model thread.
	 * <p>
	 * There are two main threads in the DFS system: this one (which is simply
	 * the shared thread of all the FSMs in the system) and the DataFeed thread
	 * (which can also have the possibility to interact with the FSM), but from
	 * another point of view.
	 * 
	 * <p>
	 * This thread is the exact equivalent of the same thread in the
	 * HistoriesManager class in C#, in the bridge (_historyUpdateThread)
	 */
	private Thread _dmThread;

	private AtomicBoolean _endRequested = new AtomicBoolean();

	private AtomicInteger _lastFinishedCycle = new AtomicInteger();

	/**
	 * This lists holds the symbols that I will be forced to update in a cycle.
	 */
	private ArrayList<String> _symbolsToForcefullyUpdate = new ArrayList<>();

	// private AtomicBoolean _atLeastOneDmCycle = new AtomicBoolean();

	public DataModel(IDataFeed aDataFeed) throws IOException,
			ClassNotFoundException, DFSException {
		_dataFeed = aDataFeed;
		_cache = new DfsCacheRepo(_dataFeed);

		_dmThread = new Thread(new Runnable() {
			@Override
			public void run() {
				dmThread();
			}
		});

		_dmThread.setName("DFS data model thread");

		debug_var(391034, "Starting the data model thread");
		_dmThread.start();
	}

	/**
	 * This method is called by the data model thread.
	 * <P>
	 * It call the update to the cache. The cache will update the tables AND the
	 * views.
	 * 
	 * @param symbolToUpdateList
	 * 
	 * @throws IOException
	 * @throws DFSException
	 */
	private void _updateHistoryTables(ArrayList<String> symbolToUpdateList)
			throws DFSException {
		_cache.update(symbolToUpdateList);
	}

	/**
	 * This is the main function of the thread.
	 * <p>
	 * It simply cycles forever until a stop is requested
	 * <p>
	 * It periodically gives to the history tables the heart beat to check if
	 * the data has come, change state...
	 * 
	 * @throws InterruptedException
	 */
	void dmThread() {
		boolean firstPass = true;
		for (;;) {

			try {
				synchronized (_symbolsToForcefullyUpdate) {
					_updateHistoryTables(_symbolsToForcefullyUpdate);
					_symbolsToForcefullyUpdate.clear();
					if (firstPass) {
						LogManager.getInstance().INFO(
								"Finished first update, DFS ready");
						firstPass = false;
					}
					_lastFinishedCycle.incrementAndGet();
				}

				synchronized (_lastFinishedCycle) {
					_lastFinishedCycle.notify();
				}

				if (_checkEnd()) {
					return;
				}

			} catch (DFSException e) {
				e.printStackTrace();
				debug_var(391013, "got exception, I will try to redo it again.");
				try {
					if (_checkEnd()) {
						return;
					}
				} catch (InterruptedException e1) {
					debug_var(391013,
							"Ending history thread for interrupted exception, probably the app is closing.");
					break;
				}
			} catch (InterruptedException e) {
				debug_var(391013,
						"Ending history thread for interrupted exception, probably the app is closing.");
			}
		}
	}

	private boolean _checkEnd() throws InterruptedException {
		if (!_endRequested.get()) {
			synchronized (_endRequested) {
				_endRequested.wait(15_000);
			}
		}

		// I double the check because the flag might have been set
		// during the wait :)
		if (_endRequested.get()) {
			return true; // end of thread.
		}
		return false;
	}

	public int getBarCount(String symbol, BarType aType, int barWidth)
			throws DFSException {
		return _cache.getBarCount(symbol, aType, barWidth);
	}

	public int getBarsBetween(String symbol, BarType aType, int barWidth,
			long startDate, long endDate) throws DFSException {
		return _cache.getBarsBetween(symbol, aType, barWidth, startDate,
				endDate);
	}

	public DfsCacheRepo getCache() {
		return _cache;
	}

	public IBarCache getCache(RequestParams aReq) throws DFSException {
		// _waitOneDmCycle();
		return _cache.returnCache(aReq);
	}

	public IBarCache getCache(String prefixSymbol, Maturity aMaturity,
			BarType aType, int nUnits) throws DFSException {
		// _waitOneDmCycle();
		return _cache.returnCache(prefixSymbol, aMaturity, aType, nUnits);
	}

	public long getDateAfterXBarsFrom(String symbol, BarType aType,
			int barWidth, long startDate, int numBars) throws DFSException {
		return _cache.getDateAfterXBarsFrom(symbol, aType, barWidth, startDate,
				numBars);
	}

	public long getDateBeforeXBarsFrom(String symbol, BarType aType,
			int barWidth, long endTime, int numBars) throws DFSException {
		return _cache.getDateBeforeXBarsFrom(symbol, aType, barWidth, endTime,
				numBars);
	}

	public int getScaleForSymbol(String symbol) throws DFSException {
		return _cache.getScaleForSymbol(symbol);
	}

	public DfsSchedulingTimes getSchedulingTimes() {
		return _cache.getSchedulingTimes();
	}

	/**
	 * @param symbol
	 *            the symbol which you want to query.
	 * @throws DFSException
	 */
	public DfsSymbolStatus getStatusForSymbol(String symbol, boolean forceCheck)
			throws DFSException {
		_waitOneDmCycle();
		return _cache.getStatusForSymbol(symbol, forceCheck);
	}

	private void _waitOneDmCycle() throws DFSException {
		_waitForUpdateCycleAtLeast(1);
	}

	/**
	 * Waits for ever until the update cycle is equal (or greater) than the
	 * parameter. This method is used for the forcing update of one symbol.
	 * 
	 * @param targetCycle
	 * @throws DFSException
	 */
	private void _waitForUpdateCycleAtLeast(int targetCycle)
			throws DFSException {

		while (_lastFinishedCycle.get() < targetCycle) {
			synchronized (_lastFinishedCycle) {
				try {
					_lastFinishedCycle.wait();
				} catch (InterruptedException e) {
					throw new DFSException(e);
				}
			}
		}
	}

	public DfsSymbolList getSymbolsList() {
		return _cache.getSymbolsList();
	}

	// public BigDecimal getTickSizeForSymbol(String symbol) {
	// BigDecimal bd = _cache.getTickSizeForSymbol(symbol);
	// return bd;
	// }

	public void manualScheduling() {
		_cache.manualScheduling();
	}

	public void setSchedulingTimes(DfsSchedulingTimes aSchedulingTimes)
			throws DFSException {
		_cache.setSchedulingTimes(aSchedulingTimes);
	}

	public void stop(boolean isOffline) throws IOException {

		_endRequested.set(true);
		synchronized (_endRequested) {
			_endRequested.notify();
		}
		/*
		 * This interrupt is dangerous, because if the data model thread is in
		 * some I/O, the thread dies badly with a
		 * java.nio.channels.ClosedByInterruptException which is tricky to
		 * handle, in any case we notify the endRequested, so at least the wait
		 * is interrupted.
		 */
		// _dmThread.interrupt();
		try {
			_dmThread.join();
		} catch (InterruptedException e) {
			debug_var(399193,
					"Interrupted while waiting for data model thread to finish");
		}

		_cache.stop(isOffline);
		debug_var(729839, "Done stopping the cache!");
	}

	/**
	 * translates the given mfg symbol between the mfg standard and the iqfeed.
	 * 
	 * <p>
	 * For now the translation is 1:1, identical, except for the continuous
	 * symbol which is different.
	 * 
	 * 
	 * @param mfgSymbol
	 *            the symbol, could also be a continuous symbol
	 * 
	 * @return the translated (in iqFeed terms) symbol
	 * @throws DFSException
	 * 
	 */
	public String translateMfgSymbol(String mfgSymbol) throws DFSException {
		return _cache.translateMfgSymbol(mfgSymbol);
	}

	public void truncateMaturity(String aSymbol, BarType aType,
			long truncateLength) throws DFSException {
		_cache.truncateMaturity(aSymbol, aType, truncateLength);
	}

	public void refreshSynchSymbol(String symbol) throws DFSException {

		GetSymbolDataAns symbolDataSafe = _cache.getSymbolDataSafe(symbol);

		int currentCycle;
		synchronized (_symbolsToForcefullyUpdate) {
			currentCycle = _lastFinishedCycle.get();
			/*
			 * The list can be also greater than one, but each one will have its
			 * own flag.
			 */
			// if (_symbolsToForcefullyUpdate.size() != 0) {
			// /*
			// * Only one thread at a time can forcefully refresh the data
			// * model, and the list is cleared after a cycle, so it has only
			// * size zero or one.
			// */
			// throw new IllegalStateException();
			// }
			_symbolsToForcefullyUpdate
					.add(symbolDataSafe.f2.getSymbol().prefix);
		}

		U.debug_var(191856, "Please waiting while updating the symbol ",
				symbolDataSafe.f2.getSymbol().prefix);
		synchronized (_endRequested) {
			_endRequested.notify();
		}
		_waitForUpdateCycleAtLeast(currentCycle + 1);

	}

	public void watchSymbol(String symbol, IDatabaseChangeListener aListener)
			throws DFSException {
		_cache.watchSymbol(symbol, aListener);
	}

	public void unwatchSymbol(String aSymbol) throws DFSException {
		_cache.unwatchSymbol(aSymbol);
	}
}
