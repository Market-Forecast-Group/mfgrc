package com.mfg.dfs.misc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.mfg.common.DFSException;
import com.mfg.common.Maturity;
import com.mfg.common.Maturity.ParseMaturityAns;
import com.mfg.common.Tick;
import com.mfg.dfs.conn.IDataFeedController;
import com.mfg.dfs.data.AllAvailableHistoricalData;
import com.mfg.dfs.data.HistoryRequest;
import com.mfg.dfs.data.IHistoryFeedListener.EEosStatus;
import com.mfg.dfs.data.PartialHistoryRequest;
import com.mfg.utils.IMarketConnectionStatusListener.EConnectionStatus;
import com.mfg.utils.IMarketConnectionStatusListener.ETypeOfData;
import com.mfg.utils.PriceUtils;
import com.mfg.utils.U;

/**
 * This is an offline data feed which is able to actually give some data for a
 * particular symbol, a pseudo random symbol.
 * 
 * <p>
 * The data feed is controllable, using the {@linkplain IDataFeedController},
 * that interface may be used by a GUI creator to control the behavior of this
 * data feed.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class PseudoRandomDataFeed implements IDataFeed, IDataFeedController {

	/*
	 * I do not have the need to store a symbol simulator, here, because all the
	 * data is computed on the fly, I do not have a "real" state, well, yes, the
	 * indexes of the data source, so... yes, we have a certain map which will
	 * store the symbols.
	 * 
	 * The symbols can recover the indexed data source if they wish.
	 */

	private static final int DEFAULT_TICK_FOR_SYMBOLS = 5;

	private static final int FAKE_VOLUME = 33; // TODO

	private AtomicInteger _activeWatches = new AtomicInteger(0);

	private Thread _watchThread;

	private AtomicBoolean _endRequested = new AtomicBoolean(false);

	/**
	 * A lazy map for all the simulated symbols. The data feed will recreate the
	 * symbols, as needed.
	 */
	private HashMap<String, SymbolSimulator> _simulatedSymbols = new HashMap<>();

	/**
	 * This is the listener used to get the quotes from the pseudo random data
	 * feed.
	 */
	private IDataFeedListener _listener;

	private AtomicBoolean _connected = new AtomicBoolean(false);
	/**
	 * At first the real time factor is equal to the "normal" speed.
	 */
	private double _realTimeFactor = 1.0;

	private AtomicBoolean _pauseFlag = new AtomicBoolean(false);

	private AtomicBoolean _stepSignal = new AtomicBoolean(false);

	private AtomicBoolean _constantStepFlag = new AtomicBoolean(false);

	private long _intervalConstantStep = 1000L;

	public PseudoRandomDataFeed(MultiServer multiServer) {

		_listener = multiServer;
		connect();
	}

	/**
	 * The watch thread is only useful if we have at least one watch active.
	 */
	private void _createWatchThread() {
		_watchThread = new Thread(new Runnable() {

			@Override
			public void run() {
				_watchThread_main();
			}
		});

		_watchThread.setName("Simulator Real Time");
		_watchThread.start();
	}

	/**
	 * Returns the symbol simulator for the given symbol.
	 * 
	 * <p>
	 * If the symbol is not simulable then it returns null.
	 * 
	 * @param symbolPrefix
	 *            the simulator is one for every prefix, not for every maturity
	 *            (this is rather unfortunate, but for now it is in this way)
	 * @return the symbol simulator for this symbol, null if the symbol is not
	 *         simulable.
	 */
	private SymbolSimulator _getSymbolSimulatorFor(String symbolPrefix) {
		synchronized (_simulatedSymbols) {
			SymbolSimulator ss;
			if (!_simulatedSymbols.containsKey(symbolPrefix)) {
				ss = new SymbolSimulator(symbolPrefix, DEFAULT_TICK_FOR_SYMBOLS);
				_simulatedSymbols.put(symbolPrefix, ss);
			} else {
				ss = _simulatedSymbols.get(symbolPrefix);
			}
			return ss;
		}

	}

	/**
	 * Does one step for all the watched symbols
	 * 
	 * @param fakeInstant
	 * @param realTimeTick
	 * @return the minimum real time instant
	 */
	private long _stepTickForAll(long fakeInstant, Tick realTimeTick) {
		long newRealFakeNow = Long.MAX_VALUE;
		synchronized (_simulatedSymbols) {
			for (Entry<String, SymbolSimulator> entry : _simulatedSymbols
					.entrySet()) {
				if (entry.getValue().isWatched()) {
					entry.getValue()
							.stepNextRealTime(fakeInstant, realTimeTick);
					_listener.onNewQuote(entry.getValue()
							.getCompleteSymbolWatched(), realTimeTick
							.getPhysicalTime(), System.currentTimeMillis(),
							PriceUtils.longToString(realTimeTick.getPrice(),
									entry.getValue().getSymbol().scale),
							FAKE_VOLUME);
					newRealFakeNow = Math.min(newRealFakeNow,
							realTimeTick.getPhysicalTime());
				}
			}
		}
		return newRealFakeNow == Long.MAX_VALUE ? fakeInstant : newRealFakeNow;
	}

	private void _unwatchAllSymbols() {
		synchronized (_simulatedSymbols) {
			for (Entry<String, SymbolSimulator> entry : _simulatedSymbols
					.entrySet()) {
				entry.getValue().watchOffForced();
			}
		}

	}

	/**
	 * This is the watch thread. It remains active as soon as there is at least
	 * one watch active.
	 */
	@SuppressWarnings("boxing")
	protected void _watchThread_main() {
		try {
			Tick realTimeTick = new Tick();
			long nextWait = 0;
			long fakeNow = System.currentTimeMillis();
			while (true) {
				try {
					// connected? no connect no party
					while (!_connected.get()) {
						synchronized (_connected) {
							_connected.wait();
						}
					}

					while (_pauseFlag.get()) { // pause cycle
						while (!_stepSignal.compareAndSet(true, false)) {

							// waiting for the step
							synchronized (_stepSignal) {
								// this wait might be interrupted if
								// the user presses play.
								_stepSignal.wait();
							}

						}

						if (_activeWatches.get() != 0) {
							U.debug_var(
									381993,
									"Stepping for all, received the signal, fake now is ",
									new Date(fakeNow));

							// Ok, now I have the step signal, I can get a tick
							// from
							// all the symbols, the new fake now is the minimum
							// of
							// all the simulated tick times.
							long newFakeNow = _stepTickForAll(fakeNow,
									realTimeTick);

							// Update the fake now
							fakeNow = newFakeNow;
						} // no watch, no party

					} // end while (_pauseFlag.get())

					if (_constantStepFlag.get()) {
						Thread.sleep(_intervalConstantStep);
						fakeNow = _stepTickForAll(fakeNow, realTimeTick);
						continue;
					}

					// I am NOT pausing, so I have to wait the next time
					if (_activeWatches.get() != 0)
						U.debug_var(329109, "going to wait for ", nextWait,
								" real. ", nextWait / _realTimeFactor,
								" simulated");
					Thread.sleep((long) (nextWait / _realTimeFactor));
					fakeNow += nextWait;

					if (_realTimeFactor >= 1) {
						fakeNow = Math.max(fakeNow, System.currentTimeMillis());
					} else {
						fakeNow = Math.min(fakeNow, System.currentTimeMillis());
					}

					if (_activeWatches.get() != 0)
						U.debug_var(891831, "Current time is ", new Date(),
								" simulated time is ", new Date(fakeNow));

					nextWait = 20_000; // a simple default value
					if (_activeWatches.get() != 0) {

						synchronized (_simulatedSymbols) {
							for (Entry<String, SymbolSimulator> entry : _simulatedSymbols
									.entrySet()) {
								if (entry.getValue().isWatched()) {
									long ans = entry.getValue()
											.getRealTimeTickUntil(fakeNow,
													realTimeTick);

									if (ans < 0) {
										// there is no tick, so I have to wait
										// until then.
										U.debug_var(
												399910,
												"No tick @ ",
												new Date(fakeNow),
												" (simulated) next tick will be in ",
												ans * (-1), " msecs");

									} else {
										// there is a tick
										_listener
												.onNewQuote(
														entry.getValue()
																.getCompleteSymbolWatched(),
														realTimeTick
																.getPhysicalTime(),
														System.currentTimeMillis(),
														PriceUtils.longToString(
																realTimeTick
																		.getPrice(),
																entry.getValue()
																		.getSymbol().scale),
														FAKE_VOLUME);
									}
									nextWait = Math
											.min(nextWait, Math.abs(ans));
								}
							}
						}

					} // no watches?

					if (_endRequested.get()) {
						break;
					}

				} catch (InterruptedException e) {

					if (_endRequested.get()) {
						U.debug_var(319934,
								"Probably interrupted by the outside, I die, because the end is requested");
						break;
					}
					U.debug_var(
							637183,
							Thread.currentThread().getName(),
							" (?) Interrupted waiting, but not end requested, I continue, put next wait to zero");
					nextWait = 0;
					continue;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			U.debug_var(399349, "Ended the simulator watch thread");
		}
	}

	@Override
	public void connect() {
		_connected.set(true);
		synchronized (_connected) {
			_connected.notify();
		}
		_listener.onConnectionStatusUpdate(ETypeOfData.HISTORICAL,
				EConnectionStatus.CONNECTED);
		_listener.onConnectionStatusUpdate(ETypeOfData.REAL_TIME,
				EConnectionStatus.CONNECTED);
	}

	@Override
	public void disconnect() {
		_connected.set(false);
		_unwatchAllSymbols();
		_watchThread.interrupt(); // wait here, you are not connected any more

		_listener.onConnectionStatusUpdate(ETypeOfData.HISTORICAL,
				EConnectionStatus.DISCONNECTED);
		_listener.onConnectionStatusUpdate(ETypeOfData.REAL_TIME,
				EConnectionStatus.DISCONNECTED);

	}

	@Override
	public boolean isConnected() {
		return _connected.get();
	}

	@Override
	public void pause() {
		_pauseFlag.set(true);
		_watchThread.interrupt();
	}

	@Override
	public void play() {
		if (!_watchThread.isAlive()) {
			U.debug_var(293025, "Watch thread dead... I restart it");
			_watchThread = null;
			_createWatchThread();
		}
		_pauseFlag.set(false);
		_watchThread.interrupt();
	}

	@Override
	public void replayFactor(double aFactor) {
		if (aFactor <= 0) {
			throw new IllegalArgumentException();
		}
		_realTimeFactor = aFactor;
	}

	/**
	 * This is the request from the DFS system, which probably needs to fill the
	 * tables.
	 * 
	 * <p>
	 * In our case the request is really synchronous and this method is always
	 * blocking
	 */
	@Override
	public void requestHistory(HistoryRequest aRequest) throws DFSException {

		if (!_connected.get()) {
			aRequest.getListener().onEndOfStream(EEosStatus.NOT_CONNECTED);
		}

		/*
		 * ok, in this case I have to get all the data.
		 */
		ParseMaturityAns pma = Maturity.parseMaturity(aRequest._symbol);

		if (pma.parsedMaturity == null) {
			// you cannot ask the cont. contract, because it is simulated by
			// DFS
			throw new DFSException("you cannot ask the cont. contract here");
		}

		// Now I have the prefix and the parsed maturity...

		SymbolSimulator ss = _getSymbolSimulatorFor(pma.unparsedString);

		if (ss == null) {
			// no simulation possible for this symbol
			throw new DFSException("Simbol " + aRequest._symbol
					+ " is not simulable");
		}

		// No data for monthly maturities except for H, M, U, Z
		if (!pma.parsedMaturity.isAQuarterMaturity()) {
			aRequest.getListener().onEndOfStream(EEosStatus.NO_DATA);
			return;
		}

		if (aRequest instanceof AllAvailableHistoricalData) {

			ss.getAllAvailableData(aRequest.getListener(), pma.parsedMaturity,
					aRequest.getType());

		} else {

			PartialHistoryRequest phr = (PartialHistoryRequest) aRequest;

			ss.getPartialData(aRequest.getListener(), pma.parsedMaturity,
					aRequest.getType(), phr.getBeginDate());
		}
	}

	@Override
	public void playAtConstantInterval(long interval) {
		_intervalConstantStep = interval;
		if (interval == 0) {
			_constantStepFlag.set(false);
			// play();
		} else {
			// I simulate it using a pause
			_constantStepFlag.set(true);
			// pause();

		}

	}

	@Override
	public void start(String connectionString) {
		_createWatchThread();

	}

	@Override
	public void step() {
		if (!_pauseFlag.get()) {
			return;
		}

		// this is tricky... because the real time tick is in some way to be
		// "with one step" got.
		if (_stepSignal.compareAndSet(false, true)) {
			synchronized (_stepSignal) {
				_stepSignal.notify();
			}
		} else {
			U.debug_var(
					392934,
					"you want to step, but the step is already set, probably you should wait *or the thread is dead*, try play!");
		}

	}

	@Override
	public void stop() {
		// I have to stop the watch thread
		_endRequested.set(true);
		try {
			_watchThread.interrupt();
			_watchThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e); // should not happen
		}
	}

	@Override
	public void subscribeToSymbol(String symbol) {
		/*
		 * Subscribing when the feed is not connected is not an error, but a no
		 * op.
		 */
		if (!_connected.get()) {
			return;
		}
		ParseMaturityAns pma = null;
		try {
			pma = Maturity.parseMaturity(symbol);
		} catch (DFSException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		SymbolSimulator ss = _getSymbolSimulatorFor(pma.unparsedString);
		ss.watchOn(pma.parsedMaturity);
		_activeWatches.incrementAndGet();
		_watchThread.interrupt();
	}

	@Override
	public void unsubscribeSymbol(String symbol) {
		/*
		 * as the unsubscribe is a no op if the symbol is not subscribed I can
		 * avoid to make the test.
		 */
		ParseMaturityAns pma = null;
		try {
			pma = Maturity.parseMaturity(symbol);
		} catch (DFSException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		SymbolSimulator ss = _getSymbolSimulatorFor(pma.unparsedString);
		ss.watchOff(pma.parsedMaturity);
		_activeWatches.decrementAndGet();
	}

}
