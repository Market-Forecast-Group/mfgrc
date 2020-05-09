package com.mfg.common;

import com.mfg.utils.FinancialMath;

/**
 * an automaton to create range bars.
 * 
 * <p>
 * This is the companion of the {@linkplain TimeBarAutomaton} class. This class
 * instead creates range bars. These bars are created using a
 * 
 * 
 * @author Sergio
 * 
 */
public class RangeBarAutomaton extends BarAutomaton {

	private final int _tick;
	private final int _range;

	/**
	 * creates a Range Bar Automaton which is able to generate range bars.
	 * 
	 * <p>
	 * To create a range bars the automaton needs the tick size and the number
	 * of range ticks used to create the range bar.
	 * 
	 * @param tickSize
	 *            the tick size, in integer units (the {@linkplain Tick} gives
	 *            always prices in units.
	 * 
	 * @param rangeTicks
	 *            the automaton will create X ranges wide bars. This number must
	 *            be positive and usually a small positive (1 or 2, most often
	 *            1, to create one range bars).
	 */
	public RangeBarAutomaton(int tickSize, int rangeTicks) {
		if (tickSize <= 0 || rangeTicks <= 0) {
			throw new IllegalArgumentException();
		}

		_tick = tickSize;
		_range = rangeTicks;
	}

	private enum State {
		/**
		 * This is the intial state, when the automaton has not received any
		 * prices yet.
		 */
		BEFORE_FIRST_OPEN,

		/** This is the state of the bar which is being completed. */
		COMPLETING_THE_BAR,

		/**
		 * This is the state when the band is not completed and we wait ticks to
		 * fill the range. In this state the distance between high and low is
		 * less than the range of the range bar. As we use only (for now) range
		 * bars of one range... this state is only valid when we have a bar with
		 * 4 equal prices.
		 */
		WAITING_RANGE
	}

	private State _state = State.BEFORE_FIRST_OPEN;

	@Override
	public Bar accept(Tick tk) {
		_lastCompleteBar = null; // I forget the last completed bar.
		int curRange = 0;
		if (_formingBar != null) {
			curRange = FinancialMath.getDeltaTicks(_formingBar.getHigh(),
					_formingBar.getLow(), _tick);
		}

		switch (_state) {
		case BEFORE_FIRST_OPEN:

			_createNewBar(tk);
			_state = State.WAITING_RANGE;
			return this._formingBar;
		case COMPLETING_THE_BAR:

			assert (curRange == _range); // this is the invariant in this state

			// let's see if the price is range
			if (FinancialMath.isPriceInRange(tk.getPrice(),
					_formingBar.getLow(), _formingBar.getHigh())) {
				// ok the price is in range, so the forming bar is not changed,
				// apart from the close and the time...
				_formingBar.setClose(tk.getPrice());
				_formingBar.setTime(tk.getPhysicalTime()); // I update the time
				_formingBar.accumulateVolume(tk);
				return null; // No new bar created and the state remains the
								// same
			}
			// the price is outside range, so by definition we have created
			// another bar
			this._lastCompleteBar = this._formingBar;

			_createNewBar(tk);
			_state = State.WAITING_RANGE;
			return this._formingBar;
		case WAITING_RANGE:
			/*
			 * In this state by definition the distance between high and low is
			 * less than the number of ticks in the range bar
			 */
			assert (curRange < _range);

			// Ok, now I should know whether the new tick arrived is inside the
			// band.
			if (FinancialMath.isPriceInRange(tk.getPrice(),
					_formingBar.getLow(), _formingBar.getHigh())) {
				// ok the price is in range, so the forming bar is not changed,
				// apart from the close and the time...
				_formingBar.setClose(tk.getPrice());
				_formingBar.setTime(tk.getPhysicalTime()); // I update the time
				_formingBar.accumulateVolume(tk);
				return null; // No new bar createdm and the state remains the
								// same

				// also the state is the same
			}

			// If the price is not in range then I have to know the distance in
			// ticks from the band.
			int deltaTicks = FinancialMath.get_delta_from_band(
					_formingBar.getLow(), _formingBar.getHigh(), tk.getPrice(),
					_tick);

			if (curRange + deltaTicks > _range) {
				// I have broken the range, so...I have to publish an incomplete
				// bar, because I cannot
				// have a gap
				this._lastCompleteBar = this._formingBar;

				_createNewBar(tk);
				return this._formingBar; // I return the new bar!

			} else if (curRange + deltaTicks == _range) {
				// the range is complete, I have only to update high or low
				_updateHighLow(tk);
				// and I change the state!
				_state = State.COMPLETING_THE_BAR;
			} else {
				// the range is still not complete, I have only to update high
				// or low; state remains the same!
				_updateHighLow(tk);
			}

			// in any case I update the close and the last time and I return
			// null, because the bar is still the same
			_formingBar.setClose(tk.getPrice());
			_formingBar.setTime(tk.getPhysicalTime());
			_formingBar.accumulateVolume(tk);
			return null;
		}

		assert (false);
		throw new IllegalStateException(); // should not happen
	}

	/**
	 * Accepts a new tick and tries to create a new Range bar.
	 * 
	 * <p>
	 * returns a bar if a new bar has been created, null otherwise (the most
	 * recent created bar is modified IN PLACE).
	 * 
	 * <p>
	 * users of this class should store the last reference of the bar in order
	 * to look at the difference.
	 * 
	 * @param tk
	 *            The tick to be accepted.
	 * 
	 * @return a bar if it has been formed, null otherwise.
	 * @deprecated use the {@link #accept(Tick)} instead
	 */
	@Deprecated
	public Bar acceptNewTick(Tick tk) {
		return accept(tk);

	}

	private void _updateHighLow(Tick tk) {
		if (tk.getPrice() > _formingBar.getHigh()) {
			_formingBar.setHigh(tk.getPrice());
		} else if (tk.getPrice() < _formingBar.getLow()) {
			_formingBar.setLow(tk.getPrice());
		}

	}

	@Override
	protected void _createNewBar(Tick tk) {
		this._formingBar = new RangeBar();
		_formingBar.setTime(tk.getPhysicalTime());
		// the starting candle has all the prices equals.
		this._formingBar.setOpen(tk.getPrice());
		this._formingBar.setLow(tk.getPrice());
		this._formingBar.setHigh(tk.getPrice());
		this._formingBar.setClose(tk.getPrice());
		this._formingBar.setInitialVolume(tk.getVolume());
	}

}
