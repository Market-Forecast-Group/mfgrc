package com.mfg.dfs.data;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.marketforescastgroup.logger.LogManager;
import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.common.UnparsedBar;
import com.mfg.common.UnparsedTick;
import com.mfg.dfs.data.DfsIntervalStats.EVisibleState;
import com.mfg.dfs.misc.DfsBar;
import com.mfg.dfs.misc.IDataFeed;
import com.mfg.utils.U;
import com.mfg.utils.Yadc;

/**
 * This is the second version of the HistoryTable class.
 * 
 * <p>
 * This new version should be simpler because it does not have the "fill left"
 * mechanism which was for the other part.
 * 
 * <p>
 * The HistoryTable is contained in a {@linkplain MaturityData} object. The
 * object is responsible to allocate the history tables needed by a particular
 * maturity.
 * 
 * <p>
 * The table is synchronous: this means that all the historical requests are
 * done in the same thread and they are then used to change the state.
 * 
 * <p>
 * The table will not serve any client when it is filling the historical
 * request.
 * 
 * <p>
 * The table is also part of a set of tables, and probably also of a maturity.
 * In this case the table is also used to store the last index of the bar which
 * is used in the continous contract.
 * 
 * <p>
 * This table is actually a "table with state", because it will update. There
 * are table without state and they are imported from other sources. They are
 * actually immutable.
 * 
 * @author Sergio
 * 
 */
public abstract class HistoryTable extends CachedTable implements
		IHistoryFeedListener {

	protected enum State {
		/**
		 * This is the state of the table when it is created. It is different
		 * from a blocked table, a blocked table cannot grow any more and it
		 * here just for convenience
		 */
		BLANK,
		/**
		 * a BLOCKED table is in a state where temporary has no data, but it may
		 * grow in the future. The blocked state is very similar to the
		 * UP_TO_DATE state the only difference is that the last bar is old.
		 * 
		 */
		BLOCKED,
		/**
		 * a COMPLETE table has all the data and it cannot grow any more, the
		 * contract is over and the data provider does not give to us any more
		 * data. This is a "maintenance" state, the table is not more active,
		 * like a devitalized tooth.
		 */
		COMPLETE,
		/**
		 * In this state the table will fill the historical data. The historical
		 * data is fed in one-way only;
		 * <p>
		 * Some symbols have ONLY historical data. In a certain sense this is
		 * the <b>only</b> state of the table, because the historical data is
		 * the <b>only</b> way in which we fill the table.
		 * 
		 * <p>
		 * Contrary to the {@linkplain HistoryTable} class we don't have here
		 * incomplete bars, but only complete. This is a <i>real</i> history
		 * table.
		 */
		FILLING_HISTORICAL_DATA,

		/**
		 * The table is up to date and it is "alive". That means that the table
		 * is in a cycle of updating itself (usually only if there is a slicer -
		 * view - upon it)
		 */
		UP_TO_DATE,
		/**
		 * This is the "classic" abort table state which is only used to abort
		 * the table.
		 * <p>
		 * Usually this state is "in-only" like a black hole. Now some limited
		 * recover capability is added, but it is most a work in progress.
		 * Usually it is safe to consider this state a bad final state.
		 */
		ABORT,

		/**
		 * This was called the "check bar" state. It means that the table will
		 * wait for the first bar from the outside and it checks that it has no
		 * holes, that in other words has been already stored in the database,
		 * exactly as this.
		 */
		OVERLAPPING_FILL,

		/**
		 * a temporary state used to check if the data in the feed is coherent
		 * in the data which is stored here in MDB. Not all data is checked,
		 * only the most recent window which may be 24 hours, usually, for range
		 * and minute bars, and some days for the daily bar.
		 * <p>
		 */
		CHECK_CONSISTENCY,

		/**
		 * This is a temporary state. The table in this state (the table of the
		 * current maturity) has been truncated to avoid an hole, but the app
		 * will try to fill this hole asap, because the feed will likely give
		 * the historical ticks missing when the markets are closed.
		 */
		LEFT_BAR_TRUNCATED,
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4924706097805767561L;

	private static final long THREE_MINUTES_MSEC = Yadc.ONE_MINUTE_MSEC * 3;

	/**
	 * Constant used to truncate the table if it is too short, because for
	 * example for range table we don't have enough material to do the glue
	 */
	private static final int MINIMUM_SIZE = 8;

	private static final int MAX_TENTATIVES_TO_EXPAND_LEFT_BAR = 8;

	/**
	 * this is the default number of hours to overlap, used to know how much to
	 * overlap for minute and range bars.
	 */
	private static long _hoursToOverlap = 20;

	public static void setHoursToOverlap(int hoursToOverlap) {
		_hoursToOverlap = hoursToOverlap;
	}

	/**
	 * The number of times I have tried to expand the left side of the table.
	 */
	protected int _tentativesToExpand = 0;

	/**
	 * This is the default number of days to overlap, used when I have to check
	 * the consistency of the table
	 * 
	 */
	private static long _daysToOverlap = 7;

	public static void setDaysToOverlap(int daysToOverlap) {
		_daysToOverlap = daysToOverlap;
	}

	protected final Maturity _maturity;

	private long _abortReason;

	/**
	 * This records when we have updated
	 */
	private long _lastUpdateTime;

	protected transient int _checkedBars;

	/**
	 * at first the history table is blank.
	 */
	protected State _state = State.BLANK;
	/**
	 * This boolean is used to store the update booking made by the continous
	 * table.
	 */
	private transient boolean _forceUpdate;
	private transient boolean _unserialized;
	private transient State _beforeConsistencyState;

	protected transient int _consistencyErrorsCount;

	protected HistoryTable(DfsSymbol symbol, Maturity maturity, BarType aType)
			throws IOException {
		super(symbol, aType, 1);
		_maturity = maturity;
		_createCache();
	}

	protected void _abortTable(long abortReason) {
		_abortReason = abortReason;
		_state = State.ABORT;

	}

	/**
	 * Does the blocking state computation, this was done in the normal cycle,
	 * but now it is done only when the scheduler fires.
	 * 
	 * @param aFeed
	 *            the feed used to do the real time update
	 */
	@SuppressWarnings("boxing")
	private void _doBlockedState(IDataFeed aFeed) {
		/*
		 * The if is now useless, because the blocked state is done only when
		 * the scheduler is run.
		 */
		// if (_unserialized || Yadc.isOneCalendarDayPassed(_lastUpdateTime)) {
		// I should try to re-get the data, if the maturity is in the
		// future or if it is the current maturity
		Maturity curMaturity = new Maturity();
		if ((_maturity.compareTo(curMaturity) == 0)
				|| (_maturity.getStartDate().getTime() > System
						.currentTimeMillis())) {
			debug_var(
					719301,
					getKey(),
					" ***************I try to make the table revive*************** cur size ",
					size());
			try {
				if (size() < MINIMUM_SIZE) {
					_fillFirstRequest(aFeed);
				} else {
					/*
					 * I try to catch up, but if the table is range I may have
					 * passed the feed's retaining period, so I end up with a
					 * table which cannot be glued together, as it is too old.
					 * This may be a problem is the table is on the current
					 * maturity. In that case the only meaningful solution is to
					 * refill completely the table and try to fill the left bar
					 * in another time
					 */
					long lastKeyBefore = _cache.getLastKey();
					_doCatchUp(aFeed);
					long lastKeyAfter = _cache.getLastKey();

					if (lastKeyAfter > lastKeyBefore) {

						U.debug_var(892745, getKey(),
								" table was blocked but I went from ",
								new Date(lastKeyBefore), " to ", new Date(
										lastKeyAfter));
						_state = State.UP_TO_DATE;
						return;

					} else if (lastKeyAfter == lastKeyBefore
							&& _type == BarType.RANGE
							&& _maturity.compareTo(curMaturity) == 0) {
						long firstKeyBefore = _cache.getFirstKey();
						U.debug_var(
								163751,
								getKey(),
								" table has not been updated, it was blocked, I will STAY HERE, first bar is ",
								new Date(firstKeyBefore));
						/*
						 * this is dangerous, because there is no backup. So I
						 * may end up with a table with an hole The fact is that
						 * I have to check the current maturity not based with
						 * the calendar but based on the real maturity of that
						 * symbol. The normal maturity has a fixed cut off,
						 * instead the normal symbol may have a different cut
						 * off, so the last if may say that we are in the
						 * current maturity BUT the real symbol has already
						 * switched the maturity.
						 * 
						 * The correct way to do it is to continue to catch up
						 * until the table is declared complete.
						 * 
						 * Another thing may be to check the real maturity, not
						 * based on the calendar, but based on the real symbol
						 * 
						 * Another thing may be to check the first bar from the
						 * feed without refilling the table, that would be a
						 * sure way to know if the feed has bars or not.
						 */

						// _fillFirstRequest(aFeed);
						// _needsRecompute = true;
						// if (_cache.size() != 0) {
						// long firstKeyAfter = _cache.getFirstKey();
						// if (firstKeyAfter > firstKeyBefore) {
						// U.debug_var(
						// 293195,
						// getKey(),
						// " there is an hole, now the first key is ",
						// new Date(firstKeyAfter),
						// " the table is left truncated");
						// _state = State.LEFT_BAR_TRUNCATED;
						// }
						// }
					}
				}

				// At the end of the catch up I am still up to date.
				// Remember: the table is synchronous, so all the
				// changing state happens in this thread.
			} catch (DFSException e) {
				e.printStackTrace();
				_abortTable(32897239847L);
			}
		} else if (curMaturity.getPreviousAsQuarter().compareTo(_maturity) <= 0) {

			debug_var(637183, getKey(),
					" BLOCKED *recent* MATURITY. doing nothing (for now).....");

			/*
			 * This code was dangerous because the table is backupped and then
			 * refilled from the start, but this can lead to loss of data if the
			 * program crashes, because the old table is not automatically
			 * restored from the backup. The data is not really lost, but it is
			 * not restored, and if the user is not warned about the crash she
			 * is not able to know where the fault is.
			 * 
			 * so it is better to have the blocked table really blocked and it
			 * will be complete after a certain time.
			 */

			// if (_type != BarType.RANGE) {
			// debug_var(319394, getKey(),
			// " is blocked but it is not range, I continue");
			// } else {
			// debug_var(637183, getKey(),
			// " BLOCKED *recent* MATURITY, I WILL TRY TO FILL BACKWARD.....");
			//
			// if (!_tryToExpandLeftBar(aFeed)) {
			// U.debug_var(928421, getKey(),
			// " this table is complete, now");
			// _state = State.COMPLETE;
			// }
			// }
		}
		// }
	}

	/**
	 * this protected method will try to get the bars which are missing from the
	 * last update time.
	 * 
	 * <p>
	 * Different table types will do different catch ups, because for example
	 * the range table must get the same tick sequence, otherwise it is not
	 * used.
	 * 
	 * @throws DFSException
	 * 
	 * 
	 */
	private final void _doCatchUp(IDataFeed aFeed) throws DFSException {
		LogManager.getInstance().INFO(
				getKey() + " doing catch up; the last key in cache is "
						+ new Date(_cache.getLastKey()) + " current size "
						+ size());

		_state = State.OVERLAPPING_FILL;

		/*
		 * It is important that the hook is called before the build of the
		 * history request, as the hook may alter the cache (truncating it).
		 */
		preHistoryRequestHook();

		_doPartialHistoryRequestWithOverlap(aFeed, _getOverlap());

	}

	@SuppressWarnings("boxing")
	private void _doConsistencyCheckAndRepair(IDataFeed aFeed)
			throws DFSException {
		if (_state == State.COMPLETE) {
			// debug_var(291044, getKey(),
			// " nothing to do for a complete table");
			return;
		}

		if (size() == 0) {
			debug_var(839133, getKey(),
					" Cannot check consistency of an empty table");

			if (System.currentTimeMillis() - _maturity.getStartDate().getTime() > (Yadc.ONE_YEAR_MSEC * _getLimitEmptyTableInYears())) {
				debug_var(536173, getKey(),
						" Empty table very old, I declare it complete");
				_state = State.COMPLETE;
			}

			return;
		}

		int beforeSize = size();

		// ok, the update request has been from the scheduler.
		_beforeConsistencyState = _state;
		_consistencyErrorsCount = 0;
		_checkedBars = 0;
		_state = State.CHECK_CONSISTENCY;

		preHistoryRequestHook(); // this because the range history table may
		// need to create the bar automaton!
		_doPartialHistoryRequestWithOverlap(aFeed, _getOverlap());

		if (_consistencyErrorsCount != 0 && _type != BarType.RANGE) {
			// repair coherency... to do...

			debug_var(839105, getKey(), " There are ", _consistencyErrorsCount,
					"consistency errors in table, now last key is ", new Date(
							_getLastKey()));
			// _abortTable(7382934798274923L);
			// return;
		} else if (_type == BarType.RANGE) {
			debug_var(728293, getKey(), " the table has ",
					_consistencyErrorsCount, " errors");
		}

		int newSize = size();

		if (_consistencyErrorsCount == 0 && beforeSize != newSize) {
			debug_var(738293, getKey(), " no errors size went from 	",
					beforeSize, " to ", newSize, " state is ", _state);
		}

		debug_var(728383, getKey(), " IS OK! I have checked ", _checkedBars,
				" bars");

		if (System.currentTimeMillis() - _maturity.getStartDate().getTime() > Yadc.ONE_YEAR_MSEC) {
			debug_var(382941, getKey(),
					" one year has passed, I declare it complete");
			_state = State.COMPLETE;
		} else {
			debug_var(183944, getKey(), " the table returns to ",
					_beforeConsistencyState);
			_state = _beforeConsistencyState;
		}

	}

	/**
	 * partial history request.
	 * 
	 * @param aFeed
	 * @param overlap
	 * @throws DFSException
	 */
	private void _doPartialHistoryRequestWithOverlap(IDataFeed aFeed,
			long overlap) throws DFSException {
		PartialHistoryRequest phr = new PartialHistoryRequest(this,
				_symbol.prefix + _maturity.toDataProviderMediumString(), _type,
				_cache.getLastKey() - overlap);

		aFeed.requestHistory(phr);

		if (_state != State.UP_TO_DATE && _state != State.BLOCKED
				&& _state != State.LEFT_BAR_TRUNCATED && _state != State.BLANK) {
			throw new IllegalStateException(); // onEndOfStream was not called.
		}
	}

	/**
	 * Does the scheduler routines.
	 * 
	 * <p>
	 * The scheduler routines are the same for all the states. Only the
	 * {@link State#BLOCKED} and {@link State#LEFT_BAR_TRUNCATED} are different,
	 * because they try to fill the left bar
	 * 
	 * @param aFeed
	 * @throws DFSException
	 */
	private void _doScheduler(IDataFeed aFeed) throws DFSException {
		if (_state == State.BLANK) {
			U.debug_var(726735, getKey(), " filling first request");
			_fillFirstRequest(aFeed);
			return;
		} else if (_state == State.BLOCKED) {
			_doBlockedState(aFeed);
		} else if (_state == State.LEFT_BAR_TRUNCATED) {
			if (!_tryToExpandLeftBar(aFeed)) {
				debug_var(837882, getKey(),
						" I give up to restore the table..., I reset up to date");
				_state = State.UP_TO_DATE;
			} else {
				_state = State.LEFT_BAR_TRUNCATED;
			}
		}
		_doConsistencyCheckAndRepair(aFeed);

		LogManager.getInstance().INFO(
				"Table " + getKey() + " after scheduling my size is " + size()
						+ " state " + _state);

	}

	@SuppressWarnings({ "boxing", "unused" })
	private void _doSequentialTest(IDataFeed aFeed) throws DFSException {
		int max = size();
		DfsBar lastBar = null;
		for (int i = 0; i < max; ++i) {
			DfsBar bar = _cache.get(i);
			if (lastBar != null) {
				if (lastBar.getPrimaryKey() >= bar.getPrimaryKey()) {
					U.debug_var(921035, getKey(), " backward bar ", bar,
							" at ", i, " last was ", lastBar);
					/*
					 * so the database should be truncated here, but for certain
					 * types of bars (minutes and daily) it has no sense to
					 * repair, the most sensible thing is to redo the filling.
					 */
					int currentYear = Calendar.getInstance().get(Calendar.YEAR);
					if (_type != BarType.RANGE
							&& (_maturity.getYear() - currentYear < 3)) {
						U.debug_var(193910, getKey(), " refilling the table.");
						try {
							_cache.truncateAt(0);
							_state = State.BLANK;
							_fillFirstRequest(aFeed);
						} catch (DFSException e) {
							e.printStackTrace();
							_abortTable(1981083717918374L);
						}

					}

				}
			}
			lastBar = bar;
		}

	}

	/**
	 * The fill first request clears the cache and does the filling.
	 * 
	 * <p>
	 * The filling is synchronous, so the change state is done rightly after the
	 * 
	 * @throws DFSException
	 * 
	 */
	private final void _fillFirstRequest(IDataFeed aFeed) throws DFSException {

		// this will ensure that, if the table is aborted for some reasons
		// we try to refill all the data.
		_cache.clear();

		_state = State.FILLING_HISTORICAL_DATA;

		preHistoryRequestHook();

		// This request is blocking!
		aFeed.requestHistory(new AllAvailableHistoricalData(this,
				_symbol.prefix + _maturity.toDataProviderMediumString(), _type));

		// If I have finished you should have called the #onEndOfStream
		// method before
		if (_state != State.UP_TO_DATE && _state != State.BLOCKED) {
			throw new IllegalStateException(); // onEndOfStream was not
												// called.
		}
	}

	/**
	 * gets the limit in years of an empty table. If there is an empty table
	 * which is older than the said number of years then it will be declared
	 * complete, even if empty, and the system will not try to update it any
	 * more.
	 * 
	 * @return the number of years used to declare it completed.
	 */
	private long _getLimitEmptyTableInYears() {
		switch (_type) {
		case DAILY:
			return 10;
		case MINUTE:
			return 5;
		case RANGE:
			return 1;
		default:
			break;

		}
		throw new IllegalStateException();
	}

	/**
	 * returns the overlap (in milliseconds) for the request history command.
	 * <p>
	 * The fact is that there are actually only two types of requests: full and
	 * partial, this is the method that gives how many milliseconds, from now,
	 * you have to go back.
	 * 
	 * @return how many milliseconds from now to go back.
	 * @throws DFSException
	 */
	protected long _getOverlap() throws DFSException {

		if (_state == State.OVERLAPPING_FILL) {
			if (_type == BarType.DAILY) {
				return Yadc.ONE_DAY_MSEC * _daysToOverlap;
			}
			return THREE_MINUTES_MSEC;
		}

		switch (_type) {
		case DAILY:
			return _daysToOverlap * Yadc.ONE_DAY_MSEC;
		case MINUTE:
			return _hoursToOverlap * Yadc.ONE_HOUR_MSEC;
		case RANGE:
			return _hoursToOverlap * Yadc.ONE_HOUR_MSEC;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * method used to update the table when it is time to (if there are not
	 * active slices than this method is not called).
	 * 
	 * <p>
	 * This method is also called when the left bar is floating <b>but</b> we
	 * are not called from the scheduler.
	 * 
	 * @param aFeed
	 * @param isFromScheduler
	 */
	private void _simpleUpdate(IDataFeed aFeed) {
		if (_unserialized) {
			try {
				if (size() < MINIMUM_SIZE) {
					debug_var(819393, getKey(),
							" is under minimum size, I will rewrite it all. ");
					_fillFirstRequest(aFeed);
				} else {
					_doCatchUp(aFeed);
				}
				// At the end of the catch up I am still up to date.
				// Remember: the table is synchronous, so all the changing
				// state happens in this thread.
			} catch (DFSException e) {
				e.printStackTrace();
				_abortTable(832939029949L);
			}
		}

		long now = System.currentTimeMillis();
		try {
			if (size() == 0) {
				debug_var(356153, getKey(),
						"Strange case, size null, probably the table is been removed!");
				_fillFirstRequest(aFeed);
			} else if ((now - _getLastKey()) > getIntervalForUpdating()) {
				try {
					debug_var(728039, getKey(),
							" I will have to update the table, ");
					_doCatchUp(aFeed);
					_updateMultipleTables();
				} catch (DFSException e) {
					e.printStackTrace();
					_abortTable(7392839341038103L);
				}
			}
		} catch (DFSException e) {

			e.printStackTrace();
			_abortTable(2394732918494L);
		}
	}

	@Override
	protected void _truncateImpl(long truncateDate) throws DFSException {
		if (size() == 0 || truncateDate > _getLastKey()) {
			// this is a no operation
			return;
		}
		_cache.truncateFrom(truncateDate);
		_state = State.BLOCKED;

		// to force the revival of the table.
		_lastUpdateTime = System.currentTimeMillis() - Yadc.ONE_MONTH_MSEC;

	}

	/**
	 * returns true if the left bar of this table (the first) has changed
	 * <b>or</b> if it needs some more tries.
	 * 
	 * <p>
	 * it returns false only when the algorithm gives up.
	 * 
	 * <p>
	 * The table tries for several times to expand the left bar, but it is a
	 * time consuming operation and it is done only when there is the scheduler
	 * active.
	 * 
	 * @param aFeed
	 * @return
	 */
	private boolean _tryToExpandLeftBar(IDataFeed aFeed) {
		long oldFirstKey;
		try {
			oldFirstKey = _cache.getFirstKey();
			// _cache.backup();
			_cache.truncateAt(0);
			_state = State.BLANK;
			_fillFirstRequest(aFeed);
			long newFirstKey = _cache.getFirstKey();

			debug_var(291930, getKey(),
					" after the second fill the new start is ", new Date(
							newFirstKey), " before was ", new Date(oldFirstKey));

			if (oldFirstKey > newFirstKey) {
				/*
				 * OK... I now have to recompute the range continuous data, this
				 * is done by the continuous table, by the way, because it
				 * recognizes that this maturity has been filled. But I set the
				 * needs recompute flag because the continuous table may give to
				 * the client some incorrect results, because if this is the
				 * first maturity of the continuous contract the first bar will
				 * be erroneously shifted, instead we have to recompute the cut
				 * off indexes
				 */
				_needsRecompute = true;
				/*
				 * I reset the counter.
				 */
				_tentativesToExpand = 0;
				return true;
			} else if (oldFirstKey == newFirstKey) {
				debug_var(391939, getKey(),
						" I have not gained anything, but neither lost, so I remain here");

			} else {
				debug_var(
						891934,
						getKey(),
						" I restore the old table, because the new first key is ",
						new Date(newFirstKey), " after the old.");
				// _cache.restore();
				// _tentativesToExpand++;
			}

		} catch (DFSException e) {
			e.printStackTrace();
			_abortTable(3247239487329832020L);
		}
		_tentativesToExpand++;
		if (_tentativesToExpand > MAX_TENTATIVES_TO_EXPAND_LEFT_BAR) {
			// I give up
			debug_var(391939, getKey(),
					" run out of tries to expand the table.");
			return false;
		}
		return true;
	}

	private void _tryToFill(DfsBar dfs) throws DFSException {
		if (!_cache.addLast(dfs)) {
			debug_var(399391, "table ", getKey(),
					" aborting table because I have received ", dfs);
			_abortTable(381939103L);
		}
	}

	/**
	 * does one step for the history table.
	 * 
	 * @param aFeed
	 * 
	 * @return true if there are some data active.
	 * @throws IOException
	 *             if something goes wrong with the cache
	 * @throws DFSException
	 */
	@Override
	@SuppressWarnings("boxing")
	public boolean doOneStep(SymbolData aSymbolData, IDataFeed aFeed,
			boolean isFromScheduler) throws DFSException {

		if (_unserialized) {
			if (_cache == null)
				try {
					_createCache();
					// debug_var(391934, getKey(), " size: ", _cache.size(),
					// " state ", _state, " from: ",
					// new Date(_cache.getFirstKey()), " to: ", new Date(
					// _cache.getLastKey()));
				} catch (IOException e) {
					throw new DFSException(e);
				}

		}

		// boolean sequentialTest = false;
		// if (sequentialTest) {
		// _doSequentialTest(aFeed);
		// return;
		// }

		// boolean isMultipleCheck = false;
		if (CHECK_MULTIPLE_TABLES) {
			_checkCoherenceOfMultipleTable();
		}

		if (!_unserialized && !_forceUpdate && !_atLeastOneViewIsOpen()
				&& !isFromScheduler) {
			/*
			 * Even if we don't have "views" any more, we have slicers which may
			 * be open or not. This alters the behavior of the table, because if
			 * it has open slicers it will update nevertheless of the scheduling
			 * time
			 */
			return false;
		}

		if (!aFeed.isConnected()) {
			return false;
		}

		if (isFromScheduler) {
			if (!_atLeastOneViewIsOpen()) {
				_doScheduler(aFeed);
			} else {
				LogManager
						.getInstance()
						.INFO(getKey()
								+ " the table has views opened, so it misses the scheduler. Size: "
								+ _cacheMap.size());
			}
			return false;
		}

		// /*
		// * the consistency check could truncate the table and this is not
		// * advisable if there are views opened on this table
		// */
		// if ((_state == State.BLOCKED || _state == State.UP_TO_DATE)
		// && isFromScheduler && !_atLeastOneViewIsOpen()) {
		// if (_state == State.BLOCKED) {
		// _doBlockedState(aFeed);
		// }
		// _doConsistencyCheckAndRepair(aFeed);
		// return;
		// }

		/*
		 * it is important that this switch does not return, because at the end
		 * we set some important flags, so please if you add some code do not
		 * return abruptly.
		 */
		int oldSize = size();
		switch (_state) {
		case BLANK:
			_fillFirstRequest(aFeed);
			break;
		case BLOCKED:
			// blocked state, normal cycle, do nothing
			break;
		case FILLING_HISTORICAL_DATA:
			U.debug_var(219103, getKey(), " inconsistent state ", _state,
					" I return to blocked.");
			_state = State.BLOCKED;
			_doConsistencyCheckAndRepair(aFeed);
			return false;
			// throw new IllegalStateException(); // what are you doing here?
		case ABORT:
			debug_var(302015, " key ", getKey(), " table is aborted reason 	",
					_abortReason);

			/*
			 * I plan to recover only if the table has been unserialized.
			 */
			if (_unserialized) {
				// Limited recover possibility.
				if (_abortReason == 2839252093L || _abortReason == 381939103L) {
					_state = State.BLANK;
				} else if (_abortReason == 7392839341038103L) {
					// I reset to up to date
					_state = State.UP_TO_DATE;
				} else if (_abortReason == 32897239847L) {
					_state = State.BLOCKED;
				} else if (_abortReason == 67261739348120341L) {
					_state = State.UP_TO_DATE;
				} else if (_abortReason == 2394732918494L) {
					_state = State.UP_TO_DATE;
				}

				debug_var(361516, " key ", getKey(), " I restore the state ",
						_state);
			}

			break;
		case UP_TO_DATE:
			_simpleUpdate(aFeed);
			break;
		case OVERLAPPING_FILL:
			// this REALLY must not happen, but if it happens it does mean that
			// the cache was saved when
			// the table was trying to do the catch up, I simply declare it
			// blocked
			debug_var(
					391930,
					getKey(),
					" The table is being revived in a strange state, I block it to be safe..., last update ",
					new Date(_lastUpdateTime));
			_state = State.BLOCKED;
			break;
		case COMPLETE:
			// nothing to do.
			break;
		case CHECK_CONSISTENCY:
			/*
			 * This could happen for example if the application was closed
			 * during the check consistency and repair status. This is not
			 * really a bug, but a condition that may happen.
			 */
			U.debug_var(
					939913,
					getKey(),
					" the table is in state check consistency: return it to blocked, unserialize is ",
					_unserialized);
			_state = State.BLOCKED;
			break;
		case LEFT_BAR_TRUNCATED:
			// if (isFromScheduler) {
			// if (!_tryToExpandLeftBar(aFeed)) {
			// debug_var(837882, getKey(),
			// " I give up to restore the table..., I reset up to date");
			// _state = State.UP_TO_DATE;
			// } else {
			// _state = State.LEFT_BAR_TRUNCATED;
			// }
			// } else
			//
			// {
			_simpleUpdate(aFeed);
			_state = State.LEFT_BAR_TRUNCATED; // I remain in this state,
												// waiting to fill the left
												// bar.
			// }
			assert (_state == State.LEFT_BAR_TRUNCATED || _state == State.UP_TO_DATE);
			break;
		default:
			throw new IllegalStateException("should not happen, unknown state!");
		}

		_forceUpdate = false;
		_unserialized = false;

		// Notifies?
		return size() != oldSize;
	}

	@Override
	public void forceUpdate() {
		_forceUpdate = true;
	}

	protected abstract DfsBar getConcreteBar(UnparsedBar ub, int scale)
			throws DFSException;

	/**
	 * returns a interval (in milliseconds) used to update the catch up;
	 * 
	 * <p>
	 * This could be also a virtual method or a method of the enumeration. For
	 * now the easiest implementation is to make it here, in a simple method.
	 * 
	 * @return
	 */
	private long getIntervalForUpdating() {
		switch (_type) {
		case DAILY:
			return Yadc.ONE_DAY_MSEC / 2;
		case MINUTE:
			return Yadc.ONE_MINUTE_MSEC / 2;
		case RANGE:
			return Yadc.ONE_MINUTE_MSEC / 2;
		}
		throw new IllegalStateException();
	}

	@Override
	public String getKey() {
		return "[" + _symbol.prefix + ":" + _maturity + ":" + _type + "]";
	}

	@Override
	public Maturity getMaturity() {
		return _maturity;
	}

	@Override
	public final DfsIntervalStats getStats(boolean forceCheck) {
		EVisibleState state = _state == State.LEFT_BAR_TRUNCATED ? EVisibleState.TRUNCATED
				: _state == State.COMPLETE ? EVisibleState.COMPLETE
						: EVisibleState.UP_TO_DATE;

		return super._getStatsState(state, forceCheck);

	}

	@Override
	public boolean isLeftTruncated() {
		return _state == State.LEFT_BAR_TRUNCATED;
	}

	@Override
	public boolean isReady() {
		switch (_state) {
		case ABORT:
			return false;
		case BLANK:
			return false;
		case BLOCKED:
			return true;
		case FILLING_HISTORICAL_DATA:
			throw new IllegalStateException(); // this must not happen! (the
												// state is synchronous)
		case UP_TO_DATE:
			return true;
		case OVERLAPPING_FILL:
			throw new IllegalStateException(); // this must not happen!
		case COMPLETE:
			return true;
		case CHECK_CONSISTENCY:
			throw new IllegalStateException(); // this must not happen!
		case LEFT_BAR_TRUNCATED:
			return true;
		default:
			break;
		}
		throw new IllegalStateException();
	}

	@SuppressWarnings("boxing")
	@Override
	public void onEndOfStream(EEosStatus aStatus) {

		/*
		 * First of all I check the status code
		 */
		switch (aStatus) {
		case ALL_OK:
			break;
		case INVALID_DATE:
			if (_type != BarType.RANGE) {
				U.debug_var(
						283495,
						getKey(),
						" invalid date, probably the table is corrupted, its size was ",
						size(), " I blank it");
				try {
					truncate(0);
				} catch (DFSException e) {
					U.debug_var(919355, "Really bad, I have an exception ");
					_abortTable(89792837492749L);
				}
				_state = State.BLANK;
				return;
			}
			break;
		case INVALID_SYMBOL:
			break;
		case NOT_CONNECTED:
			break;
		case NO_DATA:
			break;
		case UNKNOWN_ERROR:
			break;
		case GENERIC_ERROR:
			break;
		default:
			break;

		}

		switch (this._state) {
		case ABORT:
			debug_var(839190, getKey(), " table is aborted ", _abortReason,
					" but it may be OK, for now pass");
			break;
		case BLANK:
		case BLOCKED:
		case UP_TO_DATE:
		case COMPLETE:
		case LEFT_BAR_TRUNCATED:
			throw new IllegalStateException(); // why_??

		case FILLING_HISTORICAL_DATA:
			LogManager.getInstance().INFO(
					getKey() + " filling ended, the new table size is: "
							+ _cache.size());
			break;

		case OVERLAPPING_FILL:
			try {
				debug_var(
						839933,
						getKey(),
						" not a bar added to the table, last bar remains: ",
						_cache.size() > 0 ? _cache.get((int) (_cache.size() - 1))
								: " empty! ");
			} catch (DFSException e1) {
				U.debug_var(452542,
						"Really bad, I have an exception printing the debug statement.");
				_abortTable(7185918275982715L);
			}
			break;
		case CHECK_CONSISTENCY:
			_state = State.BLOCKED;
			/*
			 * this is a phony state, because it will be changed after the check
			 * is over with the real state which was stored
			 */
			return; // nothing to do about it.

		default:
			break;
		}

		if (_cache.size() == 0) {
			if (_state == State.OVERLAPPING_FILL) {
				throw new IllegalStateException(); // if I am overlapping then
													// the cache must not be
													// empty.
			}

			debug_var(381039,
					"The table is empty, so it is blocked by definition!");
			_state = State.BLOCKED;
		} else {
			/*
			 * When the last bar is at least one week old I can declare this
			 * maturity blocked
			 */
			long lastTime = -1;
			try {
				lastTime = _cache.getLastKey();
			} catch (DFSException e) {
				e.printStackTrace();
				_abortTable(3029423085320L);
			}
			long now = System.currentTimeMillis();

			if (now - lastTime > Yadc.ONE_WEEK_MSEC) {
				debug_var(732004,
						"I declare the table as BLOCKED! time difference ",
						(now - lastTime));
				_state = State.BLOCKED;
			} else {
				_state = State.UP_TO_DATE;
			}
		}
		_lastUpdateTime = System.currentTimeMillis();
	}

	@Override
	public void onHistoricalTick(UnparsedTick ut) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onNewCompleteBar(UnparsedBar ub) {
		if (_state != State.FILLING_HISTORICAL_DATA
				&& _state != State.OVERLAPPING_FILL
				&& _state != State.CHECK_CONSISTENCY) {
			throw new IllegalStateException(); // what are you doing here?
		}

		DfsBar dfs;
		try {
			dfs = getConcreteBar(ub, _symbol.scale);
		} catch (DFSException e1) {
			/*
			 * This silenced exception has been put because in the BD symbols,
			 * sometimes, there is a very srange bar, for example:
			 * 
			 * HIT,BDH14,60,20140127 162800,,,,,1,HIT$5
			 * 
			 * HIT$5,2014-01-27 16:35:00,0.6403,0.6403,0.6403,0.6403,555182,0,
			 * 
			 * HIT$5,2014-01-28 02:02:00,142.46,142.43,142.43,142.45,2348,2348,
			 * 
			 * The first bar is unparsable with the normal tick. Until I have
			 * found the reason of this strange bar I ignore it.
			 */
			U.debug_var(918394, "*********   IGNORING MALFORMED BAR ", ub,
					" ********************* reason: ");
			e1.printStackTrace();
			return;
		}

		// Ok, now I can put the bar inside the cache
		try {

			if (_state == State.OVERLAPPING_FILL) {
				DfsBar checkBar = _cache.getRecAt(dfs.getPrimaryKey());
				if (checkBar == null) {
					debug_var(381935, "Check bar not present...", dfs,
							" I will try to fill it to the cache!");

					// this maybe means that we are starting to fill the table
					_state = State.FILLING_HISTORICAL_DATA;
					_tryToFill(dfs); // this will burst if it cannot fill.
				} else if (!checkBar.equals(dfs)) {
					debug_var(873980, "Difference between ", dfs,
							" and the db bar ", checkBar,
							" I truncate the table!");

					// OK, I take the new bar
					_cache.truncateFrom(dfs.getPrimaryKey());
					if (_cache.getLastKey() >= dfs.getPrimaryKey()) {
						debug_var(392355, getKey(),
								"truncate is not successfull ");
						_abortTable(398472398479284L);
					} else {
						debug_var(393423, getKey(),
								" truncate success! Now last key is ",
								new Date(_cache.getLastKey()));
					}
					_cache.addLastForce(dfs);

					// very bad situation!
					// _abortTable(348923825L);
				}
			} else if (_state == State.CHECK_CONSISTENCY) {
				_checkedBars++;
				DfsBar checkBar = _cache.getRecAt(dfs.getPrimaryKey());
				if (checkBar == null) {
					// the check bar is not present, so I suppose this is past
					// over the last bar
					if (_getLastKey() > dfs.getPrimaryKey()) {
						// the database is newer than this, but this is not
						// present, it is a consistency error
						_consistencyErrorsCount++;
						debug_var(183943, getKey(), "not found the bar ", dfs,
								" this is an error because last key is ",
								new Date(_getLastKey()));

						// I truncate from the bar which is not present, and I
						// add it.
						_cache.truncateFrom(dfs.getPrimaryKey());
					}
					// in any case save this bar...
					_cache.addLastForce(dfs);
				} else {
					if (!checkBar.equals(dfs)) {
						debug_var(831934, getKey(), " inconsistency required ",
								dfs, " I got ", checkBar);
						_cache.truncateFrom(checkBar.getPrimaryKey());
						_cache.addLastForce(dfs);

						_consistencyErrorsCount++;
						// debug_var(389390, getKey(), " new bar ", dfs,
						// " not equal to ", checkBar);
						// LogManager.getInstance().ERROR("");
					}
				}
			} else {
				// the state is filling
				_tryToFill(dfs);
			}
		} catch (DFSException e) {
			e.printStackTrace();
			_abortTable(839238520);
		}

	}

	/**
	 * as the data provider is not in real time (we have one historical request
	 * which is only able to arrive to the last point requested... BUT it won't
	 * go further) we can have ONLY one incomplete bar per request.
	 * 
	 * <p>
	 * Giving this incomplete bar we can have the possibility to store it,
	 * because this actually <b>could</b> be complete if the time of the bar is
	 * greater than the interval of the bar itself.
	 * 
	 * <p>
	 * This of course is only an assumption but it is reasonable, at least for
	 * the minute bars (the daily bars are different, as they are always
	 * complete!).
	 */
	@Override
	public void onNewIncompleteBar(UnparsedBar ub) {
		debug_var(493021, getKey(), " the last incomplete bar is ", ub);
	}

	/**
	 * called before the request is sent to the data feed.
	 * 
	 * <p>
	 * I can do two requests: one is the initial request, done to fill the data,
	 * the other is the secondary request, done when we want to catch up.
	 * 
	 * <p>
	 * The particular tables could differentiate the two cases, but these two
	 * cases are alredy differentiated because the method is called with a
	 * different state: CATCHING_UP in the first case and OVERLAPPING_FILL in
	 * the second.
	 * 
	 * @throws DFSException
	 * 
	 * 
	 */
	protected void preHistoryRequestHook() throws DFSException {
		//
	}

	/**
	 * it will read the object and create the transient data.
	 * 
	 * @return
	 * @throws DFSException
	 */
	@Override
	protected Object readResolve() throws DFSException {
		if (super.readResolve() != this)
			throw new IllegalStateException(
					"? the contract is violated, I need myself here.");
		_unserialized = true;
		return this;
	}

	public void removeFromDisk() throws DFSException {
		_cache.delete();
	}

	@Override
	public String toString() {
		return getKey() + " in " + this._state + " size: " + size();
	}

}
