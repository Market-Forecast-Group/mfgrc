package com.mfg.dfs.data;

import static com.mfg.utils.Utils.debug_var;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;

import com.mfg.common.BarType;
import com.mfg.common.DFSException;
import com.mfg.common.DfsSymbol;
import com.mfg.common.Maturity;
import com.mfg.common.RangeBar;
import com.mfg.common.RangeBarAutomaton;
import com.mfg.common.Tick;
import com.mfg.common.UnparsedBar;
import com.mfg.common.UnparsedTick;
import com.mfg.dfs.cache.ICache;
import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.misc.DfsBar;
import com.mfg.dfs.misc.DfsRangeBar;
import com.mfg.utils.U;
import com.mfg.utils.Yadc;

/**
 * The range history table is different, because it does not receive bars but
 * ticks.
 * 
 * <p>
 * The ticks are then stored into the mdb database compressed in the forms of
 * encoded range bars, ({@linkplain DfsRangeBar}).
 * 
 * 
 * @author Sergio
 * 
 */
public class RangeHistoryTable extends HistoryTable {

	/**
	 * This is the automaton used to check the consistency of the range table.
	 * 
	 * <p>
	 * It uses the glue index found by the {@linkplain GlueFinder} class and
	 * then it scans the table and exits with an error if there is an
	 * inconsistency.
	 * 
	 * @author Sergio
	 * 
	 */
	private final class ConsistencyAutomaton {

		/**
		 * This is the next index to check in the cache.
		 */
		private int _nextIndexToCheck;

		/**
		 * this is the flag that indicates if the consistency is OK or not
		 */
		private boolean _isOK = true;

		/**
		 * This is the first failed index;
		 */
		private int _failedBarIndex;

		private DfsBar _lastCheckedBar;

		/**
		 * It is true if the consistency automaton has reached the end of the
		 * table in the OK state and cannot check any more because there has
		 * been an overflow in the future.
		 */
		private boolean _overflowInTheFuture = false;

		/**
		 * creates the consistency automaton: it will start to check the range
		 * bars at the index given
		 * 
		 * @param indexToCheck
		 *            this is the index to check, the starting index in the
		 *            cache.
		 */
		@SuppressWarnings("boxing")
		public ConsistencyAutomaton(int indexToCheck) {
			_nextIndexToCheck = indexToCheck;
			if (_nextIndexToCheck >= _cache.size()) {
				_overflowInTheFuture = true;
			}
			debug_var(819393, getKey(),
					" created the consistency automaton with index ",
					_nextIndexToCheck, " overflow in future ",
					_overflowInTheFuture);
		}

		/**
		 * checks the current bar, the automaton.
		 * 
		 * @param aBar
		 * @return
		 * @throws DFSException
		 */
		@SuppressWarnings("boxing")
		public boolean acceptBar(DfsBar aBar) throws DFSException {

			assert (_isOK);

			if (_nextIndexToCheck >= _cache.size()) {
				/*
				 * Must not happen now, because you should ask the method
				 * #pointsToFuture before calling this one, because if the
				 * automaton is pointing to the future it has no meaning the
				 * consistency check.
				 */
				throw new IllegalStateException();
			}

			DfsBar checkBar = _cache.get(_nextIndexToCheck++);
			if (_nextIndexToCheck >= _cache.size()) {
				_overflowInTheFuture = true;
			}

			if (aBar.equals(checkBar)) {
				// debug_var(718939, getKey(), " CHECKED OK ",
				// _nextIndexToCheck - 1, " the bars are equal ", checkBar);
				_lastCheckedBar = checkBar;
				return true;
			}

			// now they are NOT equal..., but they may have a difference of only
			// one millisecond
			if ((_lastCheckedBar != null)
					&& (aBar.equalsNoTime(checkBar) && checkBar.getPrimaryKey()
							- _lastCheckedBar.getPrimaryKey() == 1)) {
				// debug_var(219304, getKey(),
				// " CHECKED OK, only one millisecond different from ",
				// aBar);
				_lastCheckedBar = checkBar;
				return true;
			}

			debug_var(718334, getKey(), " inconsistency @ ",
					_nextIndexToCheck - 1, " the bar is ", checkBar,
					" I wanted ", aBar);

			if (_isOK) { // I store the first ko index.
				_failedBarIndex = _nextIndexToCheck - 1;
				_isOK = false;
			}

			_consistencyErrorsCount++;
			return false;
		}

		public int getFailedBarIndex() {
			return _failedBarIndex;
		}

		/**
		 * return true if the consistency has failed.
		 * 
		 * @return
		 */
		public boolean isKaputt() {
			return !_isOK;
		}

		/**
		 * returns true if this automaton points to the future, this is used by
		 * the consistency algo to know if this bar should be unconditionally
		 * added to the table (as there is not a corresponding bar in the table
		 * it is OK by definition).
		 * 
		 * @return
		 */
		public boolean pointsToFuture() {
			return _overflowInTheFuture;
		}
	}

	/**
	 * This enumeration is used to know if we have the glue
	 * 
	 * @author Sergio
	 * 
	 */
	public enum EGlueAnswer {
		/**
		 * This answer means that the first bar given to the glue finder points
		 * after the last current bar of the table. This in "normal" filling
		 * could be also a false alarm, because the data feed could also not
		 * have the data to fill the table. In consistency check, however, this
		 * may mean that the table needs to be truncated because the data feed
		 * does not have data to fill the time hole. In that case the table will
		 * be truncated and filled with the new bars (the table will enter the
		 * LEFT_BAR_TRUNCATED state and it will try to recover the lost bars
		 * later...).
		 * 
		 */
		FIRST_BAR_IS_FUTURE, NEED_MORE_BARS, GLUE_FOUND, GLUE_FINDING_FAILED
	}

	/**
	 * This class is used to know how to glue the array of the bars to the
	 * 
	 * @author Sergio
	 * 
	 */
	private static final class GlueFinder {
		/**
		 * This is the enumeration which holds the states of the automaton.
		 * <p>
		 * this means that the enumeration will follow the automaton to have the
		 * states.
		 * 
		 * @author Sergio
		 * 
		 */
		private enum EState {
			BEFORE_FIRST_BAR, FILLING_VECTOR, FINAL_OK, FINAL_ERROR,
			/**
			 * This state is OK, but there has been a time hole, so the table
			 * should be truncated (if we are doing a consistency check).
			 */
			FINAL_OK_WITH_TIME_HOLE
		}

		private static final int MINIMUM_SIZE_TO_GLUE = 5;

		/**
		 * This vector holds the bars which are used to make a glue.
		 * 
		 * <p>
		 * I store in the
		 */
		private transient Deque<DfsRangeBar> _consistencyVector = new ArrayDeque<>();

		/**
		 * this is the cache used to perform the glue, the cache is stored here
		 * because this is a static class and we have not a reference to the
		 * enclosing class
		 */
		private final ICache<DfsBar> _cache;

		private EState _state = EState.BEFORE_FIRST_BAR;

		private int _glueIndex;

		private final boolean _isLenient;

		/**
		 * 
		 * @param aCache
		 *            the cache of this range table
		 * 
		 * @param isLenient
		 *            if this is true the glue finder will jump over the
		 *            inconsinstencies in the cache to find a glue, if false
		 *            then if the first try is failed it won't try any further
		 */
		public GlueFinder(ICache<DfsBar> aCache, boolean isLenient) {
			_cache = aCache;
			_isLenient = isLenient;
		}

		/**
		 * this method tries to find the glue; the glue is a series of identical
		 * consecutive bars.
		 * 
		 * @return
		 * @throws DFSException
		 */
		@SuppressWarnings("boxing")
		private boolean _foundGlue() throws DFSException {

			long checkIndex = -1;
			for (DfsBar bar : _consistencyVector) {
				long dbIndex = _cache.getIndexOfRecAt(bar.getPrimaryKey());
				debug_var(193892, "check consistency of bar ", bar,
						" its index is ", dbIndex, " check is ", checkIndex);
				if (checkIndex != -1) { // This is not the first bar
					if (dbIndex != checkIndex + 1) {
						debug_var(381934,
								"NOT FOUND glue, because db index is ",
								dbIndex, " I expected ", (checkIndex + 1));
						return false;
					}

					// Now the bars should be equal...
					DfsBar dbBar = _cache.get((int) dbIndex);
					if (!dbBar.equals(bar)) {
						debug_var(819134, "Glue failed, the db bar @ ",
								dbIndex, " is ", dbBar, " I expected ", bar);
						return false;
					}
				} else { // first bar
					if (dbIndex < 0) { // also the first bar is not present
						debug_var(738193, "glue failed in the first bar @ ",
								dbIndex, " bar is ", bar);
						return false;
					}
				}

				checkIndex = dbIndex;
			}
			_glueIndex = (int) (checkIndex + 1);
			debug_var(293013, "Found glue @ index ", _glueIndex,
					" the last checked bar was at index ", checkIndex);
			return true; // glue found!
		}

		/**
		 * accepts the given bar to find the glue.
		 * 
		 * 
		 * @param dfs
		 * @return
		 * @throws DFSException
		 */
		EGlueAnswer acceptBar(DfsRangeBar dfs) throws DFSException {

			switch (_state) {
			case BEFORE_FIRST_BAR:
				if (dfs.getPrimaryKey() > _cache.getLastKey()) {
					_state = EState.FINAL_OK_WITH_TIME_HOLE;
					return EGlueAnswer.FIRST_BAR_IS_FUTURE;
				}

				_consistencyVector.add(dfs);
				_state = EState.FILLING_VECTOR;
				return EGlueAnswer.NEED_MORE_BARS;
			case FILLING_VECTOR:

				if (dfs.timeStamp > _cache.getLastKey()) {
					// I cannot find the glue!!
					debug_var(718384, "Passed the end of the cache @ ",
							new Date(_cache.getLastKey()),
							" without glue, FAIL");
					_state = EState.FINAL_ERROR;
					return EGlueAnswer.GLUE_FINDING_FAILED;
				}

				// the glue must be found with the last bar too.
				_consistencyVector.add(dfs);
				if (_consistencyVector.size() >= MINIMUM_SIZE_TO_GLUE) {
					// let's try to find the glue.
					if (_foundGlue()) {
						_state = EState.FINAL_OK;
						return EGlueAnswer.GLUE_FOUND;
					}

					if (_isLenient) {
						// I remove the first and add the last.
						_consistencyVector.removeFirst();
						// the state remain the same.
						return EGlueAnswer.NEED_MORE_BARS;
					}
					_state = EState.FINAL_ERROR;
					return EGlueAnswer.GLUE_FINDING_FAILED;
				}

				return EGlueAnswer.NEED_MORE_BARS;
			case FINAL_ERROR:
				return EGlueAnswer.GLUE_FINDING_FAILED;
			case FINAL_OK:
				assert (false); // should not happen
				break;
			case FINAL_OK_WITH_TIME_HOLE:
				/*
				 * this could be OK, for example if the state is not consistency
				 * check
				 */
				return EGlueAnswer.FIRST_BAR_IS_FUTURE;
			default:
				throw new IllegalStateException(); // it's a bug!
			}

			assert (false); // unreachable.
			return null;
		}

		/**
		 * returns the index of the glue, if it has been found. The idea is that
		 * we have the possibility to use our automaton to create a new glue
		 * where we could in some way have the possibility to check the
		 * consistency of the range bars.
		 * 
		 * @return the glue index, it is the index of the bar <b>next</b> to the
		 *         glue.
		 */
		int getFoundGlueIndex() {
			return _glueIndex;
		}

		public boolean hasTimeHole() {
			return _state == EState.FINAL_OK_WITH_TIME_HOLE;
		}

		public boolean isGlueDone() {
			return _state == EState.FINAL_OK;
		}

		@Override
		public String toString() {
			return U.join("glue finder @ state ", _state);
		}
	}

	/**
	 * An enumeration which is used to list the sub states which are inside the
	 * catching up phase.
	 * 
	 * <p>
	 * The substates are used to make the glue in the ticks, and to make the
	 * table ready to get all the ticks without losing any one.
	 * 
	 * @author Sergio
	 * 
	 */
	private enum SubStates {

		BEFORE_FIRST_TICK, OVERLAPPING_TICK, GLUE_DONE_AND_FILLING
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4641699738329345527L;

	private static final int INITIAL_OVERLAP = 10;

	/**
	 * Just a constant, it is not really important the value, it is just a
	 * number to go back, because the table needs a glue: the glue is find with
	 * this overlap if the table has not sufficient ticks to glue or if the
	 * ticks to glue are not useful
	 */
	private static final int INCREMENT_TRANSIENT_OVERLAP = 7;

	/**
	 * To distinguish between a range bar and a minute bar I assume that a
	 * default range width is 45 seconds, this is also used by the
	 * {@link ContinuousTable} to give the starting time of its data for the
	 * first chunk
	 */
	// public static final long DEFAULT_RANGE_BAR_DURATION =
	// Yadc.ONE_SECOND_MSEC * 45;

	private transient RangeBarAutomaton _rangeAutomaton = null;
	private transient int _rangesCreated;

	/**
	 * This array stores the last ticks that have come from the server and which
	 * are incorporated in the last range bar which has not being completed yet.
	 * 
	 * <p>
	 * This array is persistent and it is used to determine the glue time when
	 * this history table wants to catch up the last range bars that have come
	 * from the system.
	 */
	private ArrayList<Tick> _lastTicks;

	private SubStates _substate;

	private int _indexToCheck; // this is the index in the glue ticks to be
								// checked

	/**
	 * the glue finder used when we want to check the consistency of the range
	 * history table.
	 */
	private transient GlueFinder _glueFinder = null;

	private transient ConsistencyAutomaton _consistencyAutomaton = null;

	/**
	 * The curent overlap is the amount of lookback used to glue the table. It
	 * is a value which is added when the check consistency is not able to find
	 * a glue (probably because the table is faulted). It is also used in
	 * "normal operation of the table but it should be noted that the two
	 * overlap are different, usually, because they serve for different
	 * purposes.
	 */
	private int _currentOverlap = INITIAL_OVERLAP;

	/**
	 * This overlap is not stored in cache and it is used only to catch up the
	 * range table in "normal" operation, when the table is not checking its
	 * consistency or doing the daily overwrite.
	 */
	private transient int _transientOverlap;

	public RangeHistoryTable(DfsSymbol symbol, Maturity aMaturity)
			throws IOException {
		super(symbol, aMaturity, BarType.RANGE);
	}

	/**
	 * checks the overlap tick, this is used to check when we have to overlap
	 * ticks and to find the glue point to create the new range bars.
	 * 
	 * <p>
	 * Also this method could be "lenient", because now I have found some
	 * "strange" ticks in the stream of prices.
	 * 
	 * @param tk
	 * @param isLenient
	 * @return
	 */
	@SuppressWarnings("boxing")
	private boolean _checkOverlapTick(Tick tk, boolean isLenient) {
		Tick tickToCheck = _lastTicks.get(_indexToCheck);
		if (tk.equals(tickToCheck)) {
			_indexToCheck++;
			if (_indexToCheck == _lastTicks.size()) {
				// finished checking
				debug_var(739183, getKey(), " // Finished checking @ ",
						_indexToCheck, " ALL OK!");
				_substate = SubStates.GLUE_DONE_AND_FILLING;
				_state = State.FILLING_HISTORICAL_DATA;
			}
			return true;
		}
		debug_var(381033, ">>>> Overlap fail <<<< @ ", _indexToCheck,
				" for tick ", tk, " it had to be ", tickToCheck);
		if (isLenient) {
			// Ok, I have to be lenient and in this case I will try to find
			// again the glue.
			if (tickToCheck.getPhysicalTime() == tk.getPhysicalTime()) {
				debug_var(381034,
						"Same time and different prices, I assume this is OK, but I stay here.");
				return true;
			} else if (tk.getPhysicalTime() < tickToCheck.getPhysicalTime()) {
				// the tick from the socket is in the past... I simply affirm
				// that this is OK, waiting to glue again
				debug_var(
						371393,
						"This tick is in the past, I wait to receive another tick without advancing the index");
				return true;
			} else {
				_indexToCheck++;
				if (_indexToCheck == _lastTicks.size()) {
					debug_var(381938,
							"Passed the end of the last ticks sizes @ tick", tk);
					_substate = SubStates.GLUE_DONE_AND_FILLING;
					_state = State.FILLING_HISTORICAL_DATA;
					return true; // going past one
				}
				return _checkOverlapTick(tk, true);
			}

		}
		_abortTable(293480392150128L); // This is really bad, because the
										// overlap has failed.
		return false;
	}

	@Override
	protected void _createCache() throws IOException {
		_cache = MfgMdbSession.getInstance().getRangeBarCache(_symbol.prefix,
				_maturity);
	}

	@SuppressWarnings({ "boxing" })
	@Override
	protected long _getOverlap() throws DFSException {
		/*
		 * If the state is check consistency I have to use another way to
		 * compute the overlap, this because the default (INITIAL_OVERLAP) is
		 * suitable only for the finding of the glue, instead when we want to
		 * check consistency we want to go back a little more, to "overwrite",
		 * if needed, a larger portion of the table.
		 */
		if (_state == State.CHECK_CONSISTENCY) {

			long delta = 0;
			int computedOverlap = _currentOverlap;
			do {

				/*
				 * It should be possible to call the getNearestIndex, but this
				 * sequential search is simpler and, besides, we may get some
				 * more overlap, which, for this particular case, it is good.
				 */
				// If the table is really small also the initial overlap can be
				// too big
				computedOverlap = Math
						.min(computedOverlap, (int) _cache.size());

				DfsBar barMinusOverlap = _cache
						.get((int) (_cache.size() - computedOverlap));
				try {
					delta = _getLastKey() - barMinusOverlap.getPrimaryKey();
					if (delta > Yadc.ONE_DAY_MSEC) {
						break;
					}
					computedOverlap += Math.max(1, _cache.size() / 100);
					if (computedOverlap >= _cache.size()) {
						return _getLastKey() - _getFirstKey();
					}
				} catch (DFSException e) {
					e.printStackTrace();
					debug_var(388193, "Aborted table for exception ", e);
					_abortTable(148190398403298402L);
				}
			} while (true);
			debug_var(617741, getKey(), " I have the computed overlap ",
					computedOverlap, " current ", _currentOverlap,
					" delta found (sec) ", delta / 1000.0);
			_currentOverlap = Math.max(_currentOverlap, computedOverlap);
		} else {
			// no state consistency state..., I use a transient overlap, which
			// is not stored
			if (_transientOverlap < INITIAL_OVERLAP) {
				_transientOverlap = INITIAL_OVERLAP;
			}
		}

		/*
		 * If the _lastTicks vector is empty then I cannot glue using the ticks
		 * but only using the GlueFinder, so I need a greater (usually) overlap.
		 * If the state is check consistency then, even if the vector is not
		 * empty, I will glue using the glue finder.
		 */
		if (_lastTicks.size() == 0 || _state == State.CHECK_CONSISTENCY) {

			int overlapToUse = _transientOverlap;
			if (_state == State.CHECK_CONSISTENCY) {
				overlapToUse = _currentOverlap;
			}

			try {
				if (_cache.size() < overlapToUse) {
					return _getLastKey() - _getFirstKey();
				}
				DfsBar barAtOverlap = _cache
						.get((int) (_cache.size() - overlapToUse));
				debug_var(729139, getKey(), " I will take the bar ",
						barAtOverlap, " as reference !, overlap current = ",
						overlapToUse);
				return _getLastKey() - barAtOverlap.getPrimaryKey();
			} catch (DFSException e) {
				e.printStackTrace();
				_abortTable(3892498324982389L);
			}
		}
		return super._getOverlap();
	}

	/**
	 * This method checks the validity of the last ticks; sometimes, expecially
	 * for the end of the maturity, we end up with a stream of ticks in the same
	 * second, sometimes also in the same milliseconds, and this makes the glue
	 * finding with the ticks a bit problematic.
	 * 
	 * <p>
	 * In this case it is preferable not to use the last ticks as glue, but the
	 * slower, but more robust, glue finder.
	 * 
	 * @return true if the last ticks are sane and can be used.
	 * @throws DFSException
	 */
	private boolean _lastTicksAreSane() throws DFSException {
		if (_lastTicks.size() == 0) {
			// empty list is sane
			return true;
		}

		long lastKey = _cache.getLastKey();
		long firstGlueKey = _lastTicks.get(0).getPhysicalTime();
		if (lastKey >= firstGlueKey) {
			debug_var(381039, "Cache incongruence, it ends @ ", new Date(
					lastKey), " while my first tick is @ ", _lastTicks.get(0),
					" I will use the finder.");
			return false;
		}

		if (_lastTicks.size() > 1) {
			long secondGlueKey = _lastTicks.get(1).getPhysicalTime();
			if (secondGlueKey - firstGlueKey < (Yadc.ONE_SECOND_MSEC / 10)) {
				debug_var(189393, " The second tick ", _lastTicks.get(1),
						" is too close to the first ", _lastTicks.get(0),
						" I prefer using the finder");
				return false; // at least I want some distance
			}
		}

		return true;
	}

	/**
	 * This method is called when a new historical tick comes when the table is
	 * in the check consistency state.
	 * 
	 * <p>
	 * This method could potentially truncate the table and/or append to the
	 * table some new bars. This may happen if the table was blocked prematurely
	 * and there were other bars on the right (future) which need to be appended
	 * in the end. The consistency automaton is used to make the glue, but this
	 * is also used to signal if a bar is in the future and needs to be
	 * appended.
	 * 
	 * @param tk
	 *            the historical tick which comes from the feed.
	 * @throws DFSException
	 */
	@SuppressWarnings("boxing")
	private void _onHistoricalTick_consistency(Tick tk) throws DFSException {

		if (_state != State.CHECK_CONSISTENCY) {
			return;
		}

		_rangeAutomaton.accept(tk);
		RangeBar bar = (RangeBar) _rangeAutomaton.getLastCompleteBar();

		if (bar == null) {
			return; // nothing to do
		}

		DfsRangeBar dfs = new DfsRangeBar(bar, _symbol.tick);

		if (_glueFinder.isGlueDone() || _glueFinder.hasTimeHole()) {
			/*
			 * Ok, now I have found the glue, so I have simply to start the
			 * consistency check
			 */
			if (_consistencyAutomaton.isKaputt()) {
				/*
				 * I have simply to append this bar..., because I have truncated
				 * the table so this is a good bar.
				 */
				try {
					_cache.addLastForce(dfs);
					_rangesCreated++;
				} catch (DFSException e) {
					_abortTable(38193891934381L);
					e.printStackTrace();
				}
			} else {
				/*
				 * There is the possibility that the automaton is good, in the
				 * sense that has found the glue, but the current bar is not
				 * present in the database because it is in the future. This
				 * means that the current table was declared BLOCKED too early,
				 * in any case these are good bars which should be put in
				 * database, and it is not advisable to truncate the table, as I
				 * am not sure wheter I could restore it later.
				 * 
				 * Of course it is a strange situation, but it may happen.
				 * Another solution would be to redeclare the table in the
				 * UPDATE state but this is very strange because it will have
				 * several side effects, probably the safest approach is to add
				 * the bar here.
				 */

				if (_consistencyAutomaton.pointsToFuture()) {
					U.debug_var(617282, getKey(),
							"Adding future bar unconditionally ", dfs);
					try {
						_cache.addLastForce(dfs);
					} catch (DFSException e) {
						e.printStackTrace();
						_abortTable(2782910934879218756L);
					}
				} else if (!_consistencyAutomaton.acceptBar(dfs)) {
					// Ok, I have found an error
					try {
						// _cache.backup();
						_cache.truncateAt(_consistencyAutomaton
								.getFailedBarIndex());
						_cache.addLastForce(dfs);
						_rangesCreated++;
					} catch (DFSException e) {
						e.printStackTrace();
						_abortTable(391374918713915315L);
					}

				} else { // this is OK, I go on
					_checkedBars++;
				}
			}

		} else {
			// find the glue!!
			try {
				EGlueAnswer ans = _glueFinder.acceptBar(dfs);
				switch (ans) {
				case GLUE_FINDING_FAILED:
					// nothing, I have failed to find the glue, I simply will
					// pass over all the ticks
					return;
				case GLUE_FOUND:
					_consistencyAutomaton = new ConsistencyAutomaton(
							_glueFinder.getFoundGlueIndex());
					debug_var(719302, getKey(), " found the glue with ", dfs,
							" , found glue @  ",
							_glueFinder.getFoundGlueIndex());
					break;
				case NEED_MORE_BARS:
					// nothing to do
					break;
				case FIRST_BAR_IS_FUTURE:
					/*
					 * In this case I truncate the table totally, and I create a
					 * consistency automaton which points to the future.
					 */
					_cache.truncateAt(0);
					_consistencyAutomaton = new ConsistencyAutomaton(0);
					_needsRecompute = true;
					// first bar unconditionally added.
					_cache.addLastForce(dfs);
					break;
				default:
					break;
				}
			} catch (DFSException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method is used to fill the table when the last tick size is zero.
	 * <p>
	 * This could happen in two different cases:
	 * 
	 * <li>If the last bar
	 * 
	 * @param tk
	 */
	@SuppressWarnings("boxing")
	private void _onHistoricalTick_withGlue(Tick tk) {

		_rangeAutomaton.accept(tk);
		RangeBar bar = (RangeBar) _rangeAutomaton.getLastCompleteBar();

		if (bar == null) {
			if (_glueFinder.isGlueDone()) {
				/**
				 * adding the tick because it is relative to the last forming
				 * bar.
				 */
				_lastTicks.add(tk);
			}
			return; // nothing to do
		}

		_lastTicks.clear(); // the ticks are used to complete the bar

		DfsRangeBar dfs = new DfsRangeBar(bar, _symbol.tick);

		if (_glueFinder.isGlueDone()) {

			// the glue is found, simply add the bar.
			try {
				_rangesCreated++;
				_cache.addLastForce(dfs);
			} catch (DFSException e1) {
				_abortTable(3893294373629423523L);
				e1.printStackTrace();
			}

		} else {
			// find the glue!!
			try {
				EGlueAnswer ans = _glueFinder.acceptBar(dfs);
				// debug_var(719302, getKey(), " try to find the glue with ",
				// dfs,
				// " the answer has been ", ans);
				switch (ans) {
				case GLUE_FINDING_FAILED:
				case FIRST_BAR_IS_FUTURE:
					/*
					 * nothing, I have failed to find the glue, I simply will
					 * pass over all the ticks
					 */
					return;
				case GLUE_FOUND:
					/*
					 * remember that size() here returns one less than the
					 * physical size of the cache! So it may happen that there
					 * is a mismatch, for example it could print truncate at 10
					 * and size it's 9. But it is not an error, because the real
					 * size is actually 10, as there is the first bar which is
					 * not counted.
					 */
					debug_var(837384, getKey(), " glue found I truncate @ ",
							_glueFinder.getFoundGlueIndex() /* + 1 */,
							" size now is ", size(),
							" (+ first bar!) the glue bar is ", dfs,
							" last is ", new Date(_getLastKey()));
					/*
					 * The found glue index is the new size of the cache. It may
					 * happen that the glue ends at the end of the cache and
					 * actually there is nothing to truncate, because the last
					 * bar of the glue is also the last bar of the cache.
					 */
					_cache.truncateAt(_glueFinder.getFoundGlueIndex() /* + 1 */);
					_state = State.FILLING_HISTORICAL_DATA;
					break;
				case NEED_MORE_BARS:
					// nothing to do
					break;
				default:
					break;
				}
			} catch (DFSException e) {
				_abortTable(78274937849290873L);
				e.printStackTrace();
			}
		}

	}

	/**
	 * overrides the default implementation in the
	 * {@linkplain HistoryTable#_truncateImpl(long)} because in this class we
	 * have also the range ticks which are to be deleted (and the glue will be
	 * done using the close of the bars)
	 */
	@Override
	protected void _truncateImpl(long truncateDate) throws DFSException {
		super._truncateImpl(truncateDate);
		_lastTicks.clear();
		debug_var(628293, getKey(), " Truncated @ ", new Date(truncateDate),
				" and also the ticks are truncated");
	}

	@Override
	protected DfsBar getConcreteBar(UnparsedBar ub, int scale)
			throws DFSException {
		return new DfsRangeBar(ub, scale);
	}

	@SuppressWarnings("boxing")
	@Override
	public void onEndOfStream(EEosStatus aStatus) {
		/*
		 * I store the fact that I was in state consistency because the super
		 * endOfStream rewrites the state.
		 */
		boolean wasConsistency = false;
		if (_state == State.CHECK_CONSISTENCY) {
			wasConsistency = true;
		}
		super.onEndOfStream(aStatus);

		if (_glueFinder != null) {

			if (!_glueFinder.isGlueDone() && !_glueFinder.hasTimeHole()) {
				if (wasConsistency) {
					_currentOverlap += Math.max(_cache.size() * 0.1, 10);
					_currentOverlap = Math.min((int) _cache.size(),
							_currentOverlap);
					debug_var(
							391939,
							getKey(),
							" could not find the glue, I pass the current overlap to ",
							_currentOverlap, " size is ", _cache.size());
				} else {
					_transientOverlap += INCREMENT_TRANSIENT_OVERLAP;
					_transientOverlap = Math.min((int) _cache.size(),
							_transientOverlap);
					debug_var(
							532622,
							getKey(),
							" could not find the glue, NOT CHECK CONSISTENCY , I pass the current overlap to ",
							_transientOverlap, " size is ", _cache.size());
				}

			} else { // Found the glue, so overlap return to default value

				if (wasConsistency) {
					_currentOverlap = INITIAL_OVERLAP;
					debug_var(652817, getKey(),
							" found glue, overlap returns to initial value ",
							_currentOverlap, " size is ", _cache.size());
				} else {
					_transientOverlap = INITIAL_OVERLAP;
					debug_var(
							652817,
							getKey(),
							" found glue,, NOT CHECK CONSISTENCY, transient overlap returns to initial value ",
							_transientOverlap, " size is ", _cache.size());
				}

			}

		}

		/*
		 * I do not put the current overlap to the initial, because I may use it
		 * for the table rewriting part (this method is called whenever we end
		 * the stream, either for normal update or for a rewrite (scheduled
		 * automatically or not).
		 */
		// else { // glue finder is null: I have glued with the last ticks
		// _currentOverlap = INITIAL_OVERLAP;
		// }

		debug_var(391993, "Created ", _rangesCreated,
				" # range bars, last ticks size ", _lastTicks.size(),
				" glue finder is ", _glueFinder, " overlap is ",
				_currentOverlap, " transient overlap is ", _transientOverlap);

		debug_var(391399, "last bar created ",
				_rangeAutomaton.getLastCompleteBar());
		debug_var(381939, "forming bar ", _rangeAutomaton.getFormingBar());

		if (_needsRecompute && (this._maturity.compareTo(new Maturity()) == 0)) {
			debug_var(
					381939,
					getKey(),
					" this table needs to be recomputed and it is current, I will try to refill it later.");
			_state = State.LEFT_BAR_TRUNCATED;
			_tentativesToExpand = 0;
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public void onHistoricalTick(UnparsedTick ut) {
		if (_state == State.ABORT) {
			return; // ignore.
		}

		if (_state != State.OVERLAPPING_FILL
				&& _state != State.FILLING_HISTORICAL_DATA
				&& _state != State.CHECK_CONSISTENCY) {
			_abortTable(3248102980251908L);
			U.debug_var(291051, " -> Illegal state 	" + _state + " for tick "
					+ ut); // what???
		}
		// Ok, first of all I get the tick
		Tick tk;
		try {
			tk = new Tick(ut.getTime(), _symbol.scale, ut.getPrice(),
					ut.getVolume());
		} catch (DFSException e1) {
			U.debug_var(103929, "could not parse the tick ", ut,
					" with scale ", _symbol.scale,
					" ignoring it....................");
			return;
		}

		if (_state == State.CHECK_CONSISTENCY) {
			try {
				_onHistoricalTick_consistency(tk);
			} catch (DFSException e) {
				e.printStackTrace();
				_abortTable(8927348971957917L);
			}
			return;
		}

		if (_glueFinder != null) {
			// finds the glue with the finder...
			_onHistoricalTick_withGlue(tk);
			return;
		}

		if (_state == State.OVERLAPPING_FILL) {
			switch (_substate) {
			case BEFORE_FIRST_TICK:
				Tick firstTick = _lastTicks.get(0);
				if (tk.getPhysicalTime() < firstTick.getPhysicalTime()) {
					// all ok, I am waiting for the first tick
					// debug_var(371890, getKey(),
					// " ignoring tick before glue ", tk);
					return; // do not feed anything.
				} else if (tk.getPhysicalTime() > firstTick.getPhysicalTime()) {
					// this is not ok!, I must have EXACT correspondence
					debug_var(842031, "Invalid tick received ", tk,
							" is AFTER the first tick saved ", firstTick,
							" creating ANOTHER TABLE...");
					try {
						// I create a backup... and I will create another table.
						// _cache.backup();
						_cache.truncateAt(0);
						assert (_cache.size() == 0);
						_needsRecompute = true;
					} catch (DFSException e) {
						debug_var(391034, getKey(),
								" aborted the table, because I could not have a backup!");
						_abortTable(3879139393992L);
						e.printStackTrace();
					}

					_substate = SubStates.GLUE_DONE_AND_FILLING;
					_state = State.FILLING_HISTORICAL_DATA;
					return;
				} else {
					debug_var(839199, "OK, I have found the glue @ ", tk);
					// Ok, I have to check the overlapping ticks.
					_substate = SubStates.OVERLAPPING_TICK;
					if (!_checkOverlapTick(tk, true)) {
						return;
					}
				}
				break;
			case GLUE_DONE_AND_FILLING:
				// All is OK, I simply have to feed the automaton
				break;
			case OVERLAPPING_TICK:
				if (!_checkOverlapTick(tk, true)) {
					return;
				}
				break;
			default:
				break;
			}
		}

		_rangeAutomaton.accept(tk); // this returns the forming bar,
									// which is not used.
		RangeBar bar = (RangeBar) _rangeAutomaton.getLastCompleteBar();

		if (bar != null) {

			if (_substate != SubStates.GLUE_DONE_AND_FILLING) {
				// I have created the bar before the glue done. This is a very
				// strange case,
				// because it means that the glue was not complete, or that
				// there has been some strange
				// tick meshing. This may happen with low traded contracts, in
				// this case I prefer
				// to add in any case the bar but to simulate the glue done
				debug_var(382193, "Created the bar ", bar,
						" WITHOUT finishing glue, I force it");
				_substate = SubStates.GLUE_DONE_AND_FILLING;
			}

			_rangesCreated++;
			if (_rangesCreated % 1000 == 0) {
				debug_var(492843, getKey(), " Created the [", _rangesCreated,
						"th]bar ", bar);
			}

			DfsRangeBar dfs = new DfsRangeBar(bar, _symbol.tick);
			try {
				DfsBar checkBar = _cache.getRecAt(dfs.timeStamp);

				if (checkBar != null) {
					// I am filling now the range history table, so it may be
					// possible to have
					// another bar with the same time. This would mean that
					// there is a second
					// bar with the same time, and I have not yet forced it.

					// Suppose a very long stream of ticks at the same
					// millisecond which forms 2 bars.
					// When I fill the first is OK, when I try to fill the other
					// it will come the bar
					// with the same time. This would abort the table, but it is
					// a normal situation instead.

					if (dfs.equals(checkBar)) {
						debug_var(482949, getKey(),
								" Skipped equal db bar (same msec) ", dfs);
						return; // In any case I skip this bar it is equal in
								// the same msec... should not happen, but I
								// skip
					}
				}

				_cache.addLastForce(dfs);

			} catch (DFSException e) {
				// OK, I abort the table...
				e.printStackTrace();
				_abortTable(80132358932L);
			}
			_lastTicks.clear();
		}

		// assert (_state != State.CHECK_CONSISTENCY);

		// I have to add the tick also if I redo the
		if (_state != State.OVERLAPPING_FILL
				|| _substate != SubStates.OVERLAPPING_TICK) {
			// Only in this state I add the tick, otherwise I repeat the same
			// ticks twice
			// in the buffer and this is, of course, not good.
			_lastTicks.add(tk);
		}

	}

	@SuppressWarnings("boxing")
	@Override
	protected void preHistoryRequestHook() throws DFSException {
		super.preHistoryRequestHook();
		// I create the range bar automaton, initializing it with the real tick.
		_rangeAutomaton = new RangeBarAutomaton(_symbol.tick, 1);
		_rangesCreated = 0;
		if (_state == State.FILLING_HISTORICAL_DATA) {
			_lastTicks = new ArrayList<>();
		} else if (_state == State.OVERLAPPING_FILL) {
			assert (_lastTicks != null); // If this fails than the vector has
											// not been deserialized from the
											// stream.
			// very strange state

			if (!_lastTicksAreSane()) {
				_lastTicks.clear();
			}

			if (_lastTicks.size() == 0) {
				debug_var(
						381094,
						"The last ticks size is zero, I will overlap using the close, (the glue finder)!!");
				_glueFinder = new GlueFinder(_cache, true);
				// _substate = SubStates.GLUE_DONE_AND_FILLING;
				// _state = State.FILLING_HISTORICAL_DATA;
			} else {
				debug_var(738933, "I have ", _lastTicks.size(),
						" ticks saved to check, from ", _lastTicks.get(0),
						" to ", _lastTicks.get(_lastTicks.size() - 1));

				/* Check coherence of the table */
				long lastKey = _cache.getLastKey();
				long firstGlueKey = _lastTicks.get(0).getPhysicalTime();

				if (lastKey >= firstGlueKey) {
					// this is a bad thing
					debug_var(381039, "Cache incongruence, it ends @ ",
							new Date(lastKey), " while my first tick is @ ",
							_lastTicks.get(0));
					// truncating...
					try {
						_cache.truncateFrom(firstGlueKey);
					} catch (DFSException e) {
						e.printStackTrace();
						debug_var(381038,
								"Could not truncate the table. Now it is really bad!");
						_abortTable(3891381093L);
					}
				}

				_substate = SubStates.BEFORE_FIRST_TICK;
			}
			_indexToCheck = 0;
		} else if (_state == State.CHECK_CONSISTENCY) {
			/*
			 * In reality I will make a glue, but not using the last ticks, but
			 * using some complete bars. The substate is not used, because we
			 * have the possibility to find the glue with the glue finder.
			 */
			_glueFinder = new GlueFinder(_cache, false);
		} else {
			throw new IllegalStateException(); // invalid state.
		}
	}
}
