package com.mfg.common;

/**
 * This is a finite state machine used to create time bars from a stream of
 * ticks.
 * 
 * <p>
 * It will create bars using the request parameters and the tick by tick data
 * 
 * @author Pasqualino
 */
public class TimeBarAutomaton extends BarAutomaton {

	public static enum STATE {
		BEFORE_FIRST_OPEN, BEFORE_OPEN, COMPLETING_THE_BAR, BAR_COMPLETED
	}

	/**
	 * This is the working memory of the automaton which creates time
	 * candles/bar.
	 */
	private final class time_bar_automaton_wm {

		/**
		 * Create a time_bar_automaton.
		 * 
		 * @param dur
		 *            (in milliseconds: the duration of the bar)
		 */
		public time_bar_automaton_wm(long dur) {
			duration = dur;
		}

		/**
		 * This is the duration of the candle, the unique parameter for the
		 * automaton
		 */
		public final long duration;

		/**
		 * This stores the next open. It is used when the automaton receives a
		 * tick which is outside the current duration range.
		 */
		public int next_open;
		public long next_start_candle;

		public STATE state = STATE.BEFORE_FIRST_OPEN;

		/**
		 * Takes a real time tick and returns null if a new bar has not been
		 * created. Returns null (but MODIFIES in place) the bar which has been
		 * given before.
		 * 
		 * @param tk
		 * @return
		 */
		public Bar accept_new_tick(Tick tk) {
			// debug_var(382219, "auto state " , this.state, " forming bar ",
			// this.formingBar);
			_lastCompleteBar = null; // when I enter a new tick the new bar
										// is void.
			switch (this.state) {
			case BEFORE_OPEN:
			case BEFORE_FIRST_OPEN:

				_createNewBar(tk);

				_formingBar.setTime(_normalize_open_time(tk.getPhysicalTime(),
						this.duration, _useEndTime));
				this.state = STATE.COMPLETING_THE_BAR;
				return _formingBar;

			case COMPLETING_THE_BAR:

				return _normal_bar_processing(tk);

			case BAR_COMPLETED:

				this.state = STATE.COMPLETING_THE_BAR;

				return _normal_bar_processing(tk);

			default:
				assert (false) : "unknown state";
			}

			throw new IllegalStateException(); // unreacheable
		}

		private Bar _normal_bar_processing(Tick tk) {
			// I must know if the bar is completed or not.
			// the getTime is the END of the period!
			long delta = tk.getPhysicalTime() - _formingBar.getTime();

			if (!_useEndTime) {
				delta -= tbaw.duration;
				++delta; // the end of the period is belonging to the new bar.
			}

			// debug_var(726373, "start bar ", new
			// Date(this.formingBar.getTime()), " delta ", delta/1000);
			if (delta > 0) {
				// complete bar with modulus

				// if delta is equal to duration in any case we form a new bar!

				// The clone of the bar is the candidate clone
				// The new next open will be this tick
				this.next_open = tk.getPrice();
				this.next_start_candle = _normalize_open_time(
						tk.getPhysicalTime(), this.duration, _useEndTime);

				_lastCompleteBar = _formingBar;
				_formingBar = new Bar();
				_formingBar.setOpen(this.next_open);
				_formingBar.setClose(this.next_open);
				_formingBar.setLow(this.next_open);
				_formingBar.setHigh(this.next_open);
				_formingBar.setTime(this.next_start_candle);
				_formingBar.setInitialVolume(tk.getVolume());

				// debug_var(829151, "The next open will be ", tk.getPrice(),
				// " next start ", new Date(this.next_start_candle));

				this.state = STATE.BAR_COMPLETED;

				// Bar formedBar = this.formingBar;

				return _formingBar;
			}

			// not complete the bar, let's try to update the low/high
			_tbaw_update_low_high(tk);

			// the new tick will in any case be the candidate close
			_formingBar.setClose(tk.getPrice());
			_formingBar.accumulateVolume(tk);

			// debug_var(562722, "NO NEW BAR: forming bar ", this.formingBar);
			return null;
		}

		private void _tbaw_update_low_high(Tick tk) {
			if (tk.getPrice() > _formingBar.getHigh()) {
				_formingBar.setHigh(tk.getPrice());
			} else if (tk.getPrice() < _formingBar.getLow()) {
				_formingBar.setLow(tk.getPrice());
			}
		}

	}

	/**
	 * return the <b>END</b> time of the bar, which is the contrary which
	 * happened in eSignal. This because iqFeed has a different concept
	 * regarding the time bars. The time bar of today has the date of the end of
	 * the period.
	 * 
	 * <p>
	 * For example a 10 minute bar, if now it is 14.33 in eSignal will have a
	 * time of 14.30, in iqFeed it has the time of 14.40, so the bar will end at
	 * 14.40.00 and the new bar will start @ 14.40.01
	 * 
	 * @param time
	 * @param duration
	 * @param useEnd
	 *            if true the normalization is towards the end of the period,
	 *            otherwise to the start
	 * @return
	 */
	static long _normalize_open_time(long time, long duration, boolean useEnd) {
		long res = time - (time % duration);
		if (useEnd) {
			res += duration;
		}
		return res;
	}

	time_bar_automaton_wm tbaw;
	final boolean _useEndTime;

	/**
	 * This will create the automaton from the data request. It will brought the
	 * automaton to the zero state.
	 * 
	 * 
	 * @param barDuration
	 *            The duration of the bar, in milliseconds.
	 * @param useEndTime
	 *            if true the time bar automaton will give to the bar the ending
	 *            time of the period, otherwise it will give it the starting
	 *            time (useful for daily bars)
	 */
	public TimeBarAutomaton(long barDuration, boolean useEndTime) {
		_useEndTime = useEndTime;
		this.tbaw = new time_bar_automaton_wm(barDuration);
	}

	/**
	 * 
	 * @param tk
	 * @return
	 * 
	 * @deprecated use the {@link #accept(Tick)} instead.
	 */
	@Deprecated
	public Bar newPrice(Tick tk) {
		return this.tbaw.accept_new_tick(tk);
	}

	@Override
	public Bar accept(Tick tk) {
		return this.tbaw.accept_new_tick(tk);
	}

	public long getDuration() {
		return this.tbaw.duration;
	}

	// public Bar getLastCompleteBar() {
	// return this._lastCompleteBar;
	// }

}
