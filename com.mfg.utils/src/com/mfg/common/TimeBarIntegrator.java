package com.mfg.common;

/**
 * The TimeBarIntegrator integrates different time bars to create a time bar
 * which is a "multiple" of the time bars given.
 * 
 * <p>
 * This means that the object will sum correctly bars and discard the bars which
 * are outside the time range
 * 
 * <p>
 * The difference between this and the {@link TimeBarAutomaton} is that the
 * automaton builds time bar using ticks, this instead integrates time bars with
 * more resolution to create time bar with less resolution.
 * 
 * 
 * @author P. Ferrentino
 * 
 */
public class TimeBarIntegrator {

	/**
	 * This enumeration will serve to
	 * 
	 * @author Sergio
	 * 
	 */
	private enum EState {
		BLANK, FILLING_PERIOD,
	}

	/**
	 * This is the incomplete time bar which is used to integrate the higher
	 * resolution bars which are given by the outside.
	 * */
	private Bar _formingBar = null;

	/**
	 * This is the bar which is the latest completed
	 */
	private Bar _lastCompletedBar = null;

	private final long _singleBarDuration;

	private final int _nUnits;

	/**
	 * If true it means that <b>both</b> the input bars and the output bars will
	 * have a time which is the starting time of their period length. By
	 * convention the starting of the period is inclusive, that is if a bar
	 * starts at midnight 000, the 000 is the first instant of the new period.
	 */
	private final boolean _useStartingTime;

	private EState _state = EState.BLANK;

	/**
	 * This is the end of the period, normalized to the bar duration of the
	 * integrator. For example if bar duration is 15 minutes, the period start
	 * must be a multiple of the bar duration, for example 9.15, 9.30 so on.
	 * 
	 * <p>
	 * The period is "end" because iqfeed has the convention to have the time of
	 * the bars at the end of the period, not the start.
	 * 
	 * <P>
	 * This also for daily bars, which have the convention of being referred to
	 * the day to which they belong
	 */
	private long _periodEnd;

	private final long _periodLength;

	private int _curUnits;

	private int _lastNUnits;

	/**
	 * creates an integrator. To create it we should know the duration of the
	 * single bar and then the number of units of the integrator.
	 * 
	 * <p>
	 * The length of the final bar will be (singleBarDuration * nUnits).
	 * 
	 * <p>
	 * TODO This integrator must take into consideration the possibility to have
	 * a bar time equal to the starting period and not the ending period
	 * 
	 * 
	 * @param singleBarDuration
	 *            the duration of the single bar
	 * 
	 * @param nUnits
	 *            how many units we want, for example 5 could mean 5 minutes, 5
	 *            days, 5 "whatever" is the <b>singleBarDuration</b>.
	 * @param useStartingTime
	 *            if true this integrator will consider as the time of the
	 *            multiple bar the starting time of the period, and not the
	 *            ending time
	 */
	public TimeBarIntegrator(long singleBarDuration, int nUnits,
			boolean useStartingTime) {
		_singleBarDuration = singleBarDuration;
		_nUnits = nUnits;
		_useStartingTime = useStartingTime;
		_periodLength = _nUnits * _singleBarDuration;
	}

	private void _createNewPeriod(Bar aBar) {
		// Ok, I do not have anything, let's normalize the open time
		_periodEnd = _getNormalizedTimeOfBar(aBar);
		// debug_var(839130, "the period end is ", _periodEnd, " which is ",
		// new Date(_periodEnd));
		_curUnits = 1; // I create a new period and this is the first bar.

		_formingBar = new Bar(_useStartingTime ? _periodEnd - _periodLength
				: _periodEnd, aBar.getOpen(), aBar.getHigh(), aBar.getLow(),
				aBar.getClose(), aBar.getVolume());
		// debug_var(103913, "The forming bar is ", _formingBar);

		_state = EState.FILLING_PERIOD;

	}

	private long _getNormalizedTimeOfBar(Bar aBar) {
		// The time of the bar is the END of the period, so I simply have to
		// get this period and subtract to it the duration of the longer period.

		// but I have to be careful, because it may be a time which is not a
		// multiple of the longer

		long time = aBar.getTime();

		/**
		 * This modulus is usually zero, because the bar that comes from the
		 * feed is exactly posed at the end or at the start of the single
		 * duration interval, for example a 1- minute bar is exactly posed at
		 * the 00 second, either of the starting or the ending minute.
		 * 
		 * But for daily bars this is not usually the case, because, for time
		 * zone considerations, the real offset inside the day can be anywhere
		 * in the cycle, this can affect the computation about the daily
		 * multiple table because also it should have the same offset.
		 * 
		 * There is a further complication if, during the multiple bar interval,
		 * the time zone switches due to DST. This is for now not handled.
		 */
		long modulus = time % _singleBarDuration;
		time -= modulus;

		if (_useStartingTime) {
			time += _singleBarDuration;
		}

		// 9.01 is for example the first bar of the next period.

		if ((time - _singleBarDuration) % (_periodLength) == 0) {
			// this is the FIRST bar of the next period, no adjustment is needed
			// debug_var(391034, "This is the first bar of the next period ",
			// aBar);
			return time + (_singleBarDuration * (_nUnits - 1)) + modulus;
		}

		// 9.05 is the last bar of the previous period (5 minutes period)
		if (time % (_periodLength) == 0) {
			// debug_var(381934, "This is the last bar of the current period ",
			// aBar);
			return time + modulus;
		}

		return time - (time % (_periodLength)) + (_periodLength) + modulus;
	}

	/**
	 * returns true if this bar is inside the period, so the bar contributes to
	 * the forming bar.
	 * 
	 * @param aBar
	 * @return
	 */
	private boolean _isBarInsidePeriod(Bar aBar) {

		// the bar is inside of period if the time is before the period end

		long timeToConsider = aBar.getTime();
		if (_useStartingTime) {
			timeToConsider += _singleBarDuration;
		}
		if (timeToConsider <= _periodEnd) {
			return true;
		}

		return false;
	}

	private boolean _isLastBarOfPeriod(Bar aBar) {
		return _useStartingTime ? aBar.getTime() + _singleBarDuration == _periodEnd
				: aBar.getTime() == _periodEnd;
	}

	private void _updatePeriod(Bar aBar) {
		// the update period is simple, just update high/low, if necessary, and
		// close (absolutely).

		_formingBar.setClose(aBar.getClose());
		_formingBar.accumulateVolume(aBar.getVolume());

		if (aBar.getHigh() > _formingBar.getHigh()) {
			_formingBar.setHigh(aBar.getHigh());
		}

		if (aBar.getLow() < _formingBar.getLow()) {
			_formingBar.setLow(aBar.getLow());
		}

		// I have integrated another bar.
		_curUnits++;

	}

	/**
	 * accepts a new bar, returns a bar if a new bar is formed.
	 * 
	 * <p>
	 * the bar <b>MUST</b> be a time bar of the same duration and normalized, in
	 * the sense that the start time <b>must</b> already be
	 * 
	 * @param aBar
	 *            the bar to accept. It <b>must</b> be of the duration which has
	 *            been used in the constructor (but it cannot be enforced).
	 *            Garbage in, garbage out.
	 * 
	 * @return null if this bar is "engulfed" inside the new bar, otherwise it
	 *         returns a new bar
	 */
	public Bar acceptBar(Bar aBar) {
		_lastCompletedBar = null;

		switch (_state) {
		case BLANK:
			// I unconditionally create a new period
			_createNewPeriod(aBar);

			// aBar.getTime() == _periodEnd
			if (_isLastBarOfPeriod(aBar)) {
				// this is the first bar and it is also the last
				_lastCompletedBar = _formingBar;
				_lastNUnits = _curUnits;
				_formingBar = null;
				_state = EState.BLANK;
				return null; // no forming bar.
			}

			return _formingBar; // I have create a new bar

		case FILLING_PERIOD:

			// In the filling period I have to check if the bar is inside the
			// filling period, but if it is the LAST bar of the corresponding
			// period
			// then I can simply finish the period.

			if (_isBarInsidePeriod(aBar)) {
				_updatePeriod(aBar);
				if (aBar.getTime() != _periodEnd) {
					return null; // I have not finished the period
				}
			}
			_lastCompletedBar = _formingBar;
			_lastNUnits = _curUnits;

			// Ok, if this is the last bar of the period I start a new (blank)
			// period
			if (_isLastBarOfPeriod(aBar)) {
				_formingBar = null;
				_state = EState.BLANK;
				return null; // no forming bar.
			}

			_createNewPeriod(aBar);
			return _formingBar;

		default:
			throw new IllegalStateException(); // invalid state.

		}
	}

	public Bar getFormingBar() {
		return _formingBar;
	}

	public Bar getLastCompleBar() {
		return _lastCompletedBar;
	}

	/**
	 * returns the length of the period for this particular integrator.
	 * 
	 * @return
	 */
	public long getPeriodLength() {
		return _periodLength;
	}

	public boolean isLastCompletedBarFull() {
		if (_lastNUnits > _nUnits) {
			throw new IllegalStateException();
		}
		return _lastNUnits == _nUnits;
	}

}
