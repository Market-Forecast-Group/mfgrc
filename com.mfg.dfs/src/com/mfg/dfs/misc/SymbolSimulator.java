package com.mfg.dfs.misc;

import java.util.Date;
import java.util.GregorianCalendar;

import com.mfg.common.Bar;
import com.mfg.common.BarAutomaton;
import com.mfg.common.BarType;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.common.Tick;
import com.mfg.common.UnparsedBar;
import com.mfg.common.UnparsedTick;
import com.mfg.dfs.data.IHistoryFeedListener;
import com.mfg.dfs.data.IHistoryFeedListener.EEosStatus;
import com.mfg.utils.IndexedRandomTickSource;
import com.mfg.utils.U;

/**
 * A simulator for a given symbol.
 * 
 * <p>
 * It has a seed, the tick and it can compute the maturities as a normal symbol.
 * 
 * <p>
 * It is used by the {@linkplain PseudoRandomDataFeed}.
 * 
 * 
 * some facts.
 * 
 * the maturity goes usually from 15 month before expiration to some days after
 * crossover
 * 
 * 
 * we have some minute data until 5 years
 * 
 * and daily bars until 1999 more or less
 * 
 * <p>
 * This class is thread safe: in particular it is safe to call the methods which
 * are used to request history and the method which should give to the caller a
 * mean to get the last real time (simulated) ticks.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class SymbolSimulator {

	/**
	 * This is the starting date for a symbol.
	 * <p>
	 * We could have different starting dates for different symbols. For
	 * simplicity now there is only one starting date for all.
	 */
	private static final long _startDateForSymbol;

	private static final int INDEX_FREQUENCY = 100_000;

	static {
		_startDateForSymbol = new GregorianCalendar(2005, 0, 1)
				.getTimeInMillis();
	}

	/**
	 * some test method calling.
	 * 
	 * @param args
	 */
	@SuppressWarnings("boxing")
	public static void main(String args[]) {
		SymbolSimulator ss = new SymbolSimulator("fakeES", 5);

		@SuppressWarnings("unused")
		IHistoryFeedListener listener = new IHistoryFeedListener() {

			@Override
			public void onEndOfStream(EEosStatus aStatus) {
				//
			}

			@Override
			public void onHistoricalTick(UnparsedTick ut) {
				//
			}

			@Override
			public void onNewCompleteBar(UnparsedBar ub) {
				U.debug_var(928911, "received the bar ", ub);
			}

			@Override
			public void onNewIncompleteBar(UnparsedBar ub) {
				//
			}
		};
		// Maturity mat = new Maturity(2009, (byte) 0);

		// U.debug_var(918739, "Getting all bars for ", mat);
		// ss.getAllAvailableData(listener, mat, BarType.DAILY);
		//
		// ss.getPartialData(listener, mat, BarType.RANGE,
		// System.currentTimeMillis() - Yadc.ONE_HOUR_MSEC);

		ss.watchOn(new Maturity());

		long now = System.currentTimeMillis();
		long fakeNow = now;
		// long then = now;
		Tick tk = new Tick();
		long nextDeltaWait = 300;
		double factor = 12.543;
		for (;;) {
			U.sleep((long) (nextDeltaWait / factor));
			now = System.currentTimeMillis();
			U.debug_var(761732, "Real clock is ", new Date(now),
					" fake clock is ", new Date(fakeNow));

			long ans = ss.getRealTimeTickUntil(fakeNow, tk);

			// I have 3 cases
			if (ans < 0) {
				// there is no tick, so I have to wait until then.
				U.debug_var(399910, "No tick @ ", new Date(now),
						" next tick will be in ", ans * (-1), " msecs");

			} else {
				U.debug_var(399193, "rtTick --> ", tk, " next will be @ ",
						new Date(fakeNow + ans), " after ", ans, " or ", ans
								/ factor, " corrected");

			}

			nextDeltaWait = Math.abs(ans);
			fakeNow += nextDeltaWait;

			// if (now - then > Yadc.ONE_MINUTE_MSEC) {
			// break;
			// }
		}
	}

	/**
	 * A simulated symbol is of course simulated, so there can be no problem for
	 * losing some ticks, the only thing we have to do is to store the last tick
	 * state for the random data source...
	 */
	private transient boolean _watched = false;

	private DfsSymbol _symbol;

	/**
	 * This is the data source which is used to create the stream of ticks for
	 * this data source.
	 * 
	 * <p>
	 * The data source here is the result of all the glue together, we can think
	 * of this data source as the data source of the continuous contract, even
	 * if we have the offset information in the {@linkplain SimulatedMaturity}
	 * class.
	 * 
	 * <p>
	 * The offset is a constant offset which is computed using a simple constant
	 * shift.
	 */
	private IndexedRandomTickSource _dataSource;

	private Maturity _watchedMaturity;

	/**
	 * 
	 */
	public SymbolSimulator(String prefix, int tick) {
		_symbol = new DfsSymbol(prefix, "simulated", tick, 0, tick);

		/* Create (and index) the data source. */
		U.debug_var(718293, "The data source for symbol ", prefix,
				" is being created, hold on");

		_dataSource = new IndexedRandomTickSource(88, tick, _startDateForSymbol);

		// Build the index may be optional
		_dataSource.buildIndexUpTo(System.currentTimeMillis(), INDEX_FREQUENCY);

	}

	@SuppressWarnings("boxing")
	private void _givePartialBars(SimulatedMaturity sm, long startDataTime,
			long endDataTime, IHistoryFeedListener listener, BarType aType) {

		U.debug_var(319391, _symbol.prefix, " mat ", sm.getMaturity(),
				" Giving bars from ", new Date(startDataTime), " to ",
				new Date(endDataTime), " type ", aType);

		BarAutomaton ba = aType.createBarAutomatonForYourType();

		Tick simTick = new Tick(startDataTime, 0); // just a fake price
		int completeBarsOrTicks = 0;
		while (true) {

			// let's take the tick
			_dataSource.putNextTick(simTick);

			if (simTick.getPhysicalTime() > endDataTime) {
				U.debug_var(193941, "Ended simulation @ ", simTick,
						" end time is ", new Date(endDataTime));
				break;
			}

			// I offset the price
			simTick.offsetPrice(sm.getAdjustedOffsetInTicks(simTick
					.getPhysicalTime()) * _symbol.tick);

			if (ba == null) {
				listener.onHistoricalTick(new UnparsedTick(simTick,
						_symbol.scale, simTick.getVolume()));
				completeBarsOrTicks++;
			} else {
				ba.accept(simTick);
				Bar completeBar = ba.getLastCompleteBar();

				if (completeBar != null) {
					// if the bar is a daily bar massage the volume
					if (aType == BarType.DAILY) {
						int vol = sm.getAdjustedDailyVolume(simTick
								.getPhysicalTime());
						completeBar.adjustVolume(vol);
					}
					listener.onNewCompleteBar(new UnparsedBar(completeBar,
							_symbol.scale));
					completeBarsOrTicks++;
				}
			}

		}

		listener.onEndOfStream(EEosStatus.ALL_OK); // this is the last call.
		U.debug_var(583913, _symbol.prefix, " mat ", sm.getMaturity(),
				" type ", aType, " given ", completeBarsOrTicks, " bars/ticks");

	}

	public synchronized void getAllAvailableData(IHistoryFeedListener listener,
			Maturity parsedMaturity, BarType aType) {

		SimulatedMaturity sm = new SimulatedMaturity(parsedMaturity);
		long startDataTime = sm.getStartDataTime(aType).getTime();

		if (startDataTime < _startDateForSymbol) {
			listener.onEndOfStream(EEosStatus.NO_DATA);
			return;
		}

		_dataSource.goTo(startDataTime);
		// Of course we cannot go in the future :)
		long endDataTime = Math.min(sm.getExpirationTime(),
				System.currentTimeMillis());

		_givePartialBars(sm, startDataTime, endDataTime, listener, aType);

	}

	/**
	 * the symbol simulator is a simulator for all the maturities. In reality we
	 * have the problem that we have several maturities and in theory every
	 * maturity is a symbol on its own.
	 * 
	 * <p>
	 * In this simulation a different route has been pursued, because the
	 * simulator is the same for all the maturities, and this limits to only one
	 * maturity watched for a given prefix.
	 * 
	 * @return
	 */
	public String getCompleteSymbolWatched() {
		return _symbol.prefix + _watchedMaturity.toDataProviderMediumString();
	}

	/**
	 * @param fromDate
	 *            the data from which you want the bar (the end is <b>always</b>
	 *            the real time mark).
	 */
	public synchronized void getPartialData(IHistoryFeedListener listener,
			Maturity parsedMaturity, BarType aType, long fromDate) {

		SimulatedMaturity sm = new SimulatedMaturity(parsedMaturity);

		long startDataTime = sm.getStartDataTime(aType).getTime();

		startDataTime = Math.max(fromDate, startDataTime);
		// startDataTime = Math.max(startDataTime, );

		if (startDataTime < parsedMaturity.getStartTradingData().getTime()) {
			listener.onEndOfStream(EEosStatus.NO_DATA);
			return;
		}

		_dataSource.goTo(startDataTime);
		// Of course we cannot go in the future :)
		long endDataTime = Math.min(sm.getExpirationTime(),
				System.currentTimeMillis());

		_givePartialBars(sm, startDataTime, endDataTime, listener, aType);
	}

	/**
	 * Gets the real time ticks which are "past" until the time given.
	 * 
	 * <p>
	 * The meaning of the return parameter is a bit convoluted, but this is only
	 * done because in this way we can have a "semi" real time behaviour with
	 * only one thread and, moreover, we can have a slower or faster simulation
	 * simply giving a "fake" time to the symbol simulator.
	 * 
	 * @param aInstant
	 *            the instant which we consider as "now". It can be
	 *            {@linkplain System#currentTimeMillis()} but it can be a time
	 *            in the future, just to have an accelerated simulation
	 * 
	 * @return a value less than zero means that there is no tick waiting and
	 *         the next tick will be with a time after the time given (with the
	 *         sign changed)
	 *         <p>
	 *         zero means that there is another tick waiting, before the instant
	 *         given, so call the same function asap.
	 *         <p>
	 *         A value different from zero means that the tick is valid and the
	 *         next tick will be after a certain delta from the instant given
	 */
	public synchronized long getRealTimeTickUntil(long aInstant, Tick aTick) {
		assert (_watched); // otherwise do not call me.
		long res = _dataSource.putNextRealTimeTick(aInstant, aTick);
		if (res >= 0) { // only here we have a valid tick
			SimulatedMaturity sm = new SimulatedMaturity(_watchedMaturity);
			aTick.offsetPrice(sm.getAdjustedOffsetInTicks(aTick
					.getPhysicalTime()) * _symbol.tick);
		}

		return res;
	}

	public DfsSymbol getSymbol() {
		return _symbol;
	}

	/**
	 * returns true if this symbol is watched.
	 * 
	 * @return
	 */
	public synchronized boolean isWatched() {
		return _watched;
	}

	/**
	 * un-watch the given symbol. It is safe to call it more than once.
	 * 
	 * The unwatched maturity should be the same of the watched maturity.
	 */
	public synchronized void watchOff(Maturity unwatchedMaturity) {
		if (_watched) {
			// if this fails you are mixing watched maturities, not good.
			assert (unwatchedMaturity.equals(_watchedMaturity)) : "watched "
					+ _watchedMaturity + " unwatched " + unwatchedMaturity;
			_dataSource.stopWatching();
		}
		_watched = false;
	}

	/**
	 * package private method to force the unwatch for this symbol, mainly used
	 * by the shutdown procedure
	 */
	void watchOffForced() {
		_dataSource.stopWatching();
		_watched = false;
	}

	/**
	 * enable watching on this simbol. The watch is not really push, like in the
	 * real market.
	 * 
	 * <p>
	 * There is a watch thread which will pool this symbol periodically.
	 * 
	 * <p>
	 * This pooling is independent from the request history, actually they are
	 * separated. In any case the symbol is synchronized with respect to the
	 * request history and the real time data.
	 * 
	 * <p>
	 * As the data is simulated we don't really need to store the real time
	 * ticks, because they can be replayed any time.
	 * 
	 * <p>
	 * Calling this function when there is already a subscription active is not
	 * harmful.
	 */
	public synchronized void watchOn(Maturity watchedMaturity) {
		if (!_watched) {
			_watchedMaturity = watchedMaturity;
			_dataSource.startWatching();
		}
		_watched = true;
	}

	/**
	 * Unconditionally steps the data source to the next real time tick.
	 * 
	 * 
	 * @param fakeInstant
	 * @param realTimeTick
	 */
	public synchronized void stepNextRealTime(long fakeInstantPar,
			Tick realTimeTick) {
		long fakeInstant = fakeInstantPar;
		long res = getRealTimeTickUntil(fakeInstant, realTimeTick);
		if (res < 0) {
			fakeInstant += Math.abs(res);
			res = getRealTimeTickUntil(fakeInstant, realTimeTick);
			assert (res >= 0); // something very wrong otherwise
		}
	}
}