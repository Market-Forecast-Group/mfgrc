package com.mfg.dm.filters;

import java.util.ArrayList;
import java.util.List;

import com.mfg.common.RealTick;

/**
 * In reality this is a one bar filter, but when the bar is ready it serializes
 * giving precedence to the real ticks in this bar, the difficulty which was in
 * the previous filter is that we do not know until the end if this is a 2 or 3
 * price bar, because we do not know how the price will exit its range, so the
 * safest way is to keep 3 prices until the end (but maybe we give to the
 * outside only 2).
 * 
 * <p>
 * so for example if we have 10,11,10 -> 9 we will give 10,11,10, but if we have
 * <p>
 * 10,11,10,11,12
 * 
 * We will output only 10,11 (the 12 is NOT sent to the outside because we may
 * have a 12 later real...).
 * 
 * 
 * <p>
 * This filter makes some delay but it is used only in warm up, for the real
 * time we use the {@link FinalNotFinalClassificator}.
 * 
 * <p>
 * This filter is in fact used only in historical warm up, because it has this
 * delay; it outputs only when the bar is completed.
 * 
 * <p>
 * This filter does not emit the open price, because it may be overwritten
 * later, in case it is fake.
 * 
 * <p>
 * This filter is able to do the volume computation.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class OneBarHistoricalFilter extends OneTickAscendingFilter {

	private enum EState {
		/**
		 * This is the initial state when I do not have received any price yet.
		 */
		INITIAL_STATE,

		/**
		 * This is the state of the initial bar, when I have only one price,
		 * after that I can go only to a state with two prices....
		 * 
		 * <p>
		 * Regarding reality. If this price is real this is the open, period. If
		 * this is fake I have to store it, but it may be not definitive.
		 */
		AFTER_OPEN,

		/**
		 * a state when I am at the low point of a bar when the bar was high, if
		 * I exit from this bar at low I will emit 2 prices, if I receive a high
		 * I will be at the state {@link #AT_HIGH_T_BAR}.
		 * 
		 * <p>
		 * Regarding the reality. If I receive a real price this real price
		 * always updates the close
		 * 
		 */
		AT_LOW_LOW_BAR,

		/**
		 * I have received the high price, of a bar, which is equal to the open,
		 * but I have received also the low (I have been in the state
		 * {@link #AT_LOW_LOW_BAR}, if I receive a low price I will return to
		 * that state, if I receive a high I will exit, emitting 3 prices.
		 */
		AT_HIGH_T_BAR,

		AT_HIGH_HIGH_BAR,

		/**
		 * I have received the low, which is equal to the open, but I have had
		 * also received the high, so if I exit with a low I will emit 3 price,
		 * using an inverted T shape.
		 */
		AT_LOW_INVERTED_T_BAR,

	}

	private EState _state = EState.INITIAL_STATE;

	/**
	 * I need to have always the possible expansion of the bar, when I pass from
	 * a 3 to a 2 prices bar I have to discard a price, in that case I always
	 * discard the middle price, because the expansion will be done using the
	 * 1st and the 3rd tick.
	 * 
	 * <p>
	 * So I have at most 3 prices to store, which is the current expansion of
	 * the bar.
	 */
	private RealTick _currentExpansion[] = new RealTick[3];

	/**
	 * this array is given to the outside. Clients are able to peek in it. It is
	 * an array list, and not simply an array, because we can have two or three
	 * prices expanded.
	 */
	private ArrayList<RealTick> _lastCompletedBar = new ArrayList<>();

	/**
	 * Used to park a candidate real open, if the current open is fake.
	 */
	private RealTick _candidateRealOpen = null;

	private RealTick _candidateRealClose = null;

	/**
	 * This is a conservation mass field. All the ticks that enter in this
	 * filter, even if filtered, contribute to the total mass of the bar.
	 */
	private int _currentBarMass = 0;

	private final ArrayList<RealTick> _tracedTicks = new ArrayList<>();

	/**
	 * Builds a fake compressor, using the tick size (to ensure the precondition
	 * of the {@link OneTickAscendingFilter}).
	 * 
	 * @param aTick
	 *            a tick size.
	 */
	protected OneBarHistoricalFilter(int aTick) {
		super(aTick);
		_resetCurrentExpansion();
	}

	/**
	 * When I receive a tick after open I create a range bar, either low or
	 * high, depening on the direction of this tick.
	 * 
	 * <p>
	 * If I create a two price bar I also create a low (or high) and a close.
	 * 
	 * <p>
	 * I do not store the high or low, because for now I use only two prices.
	 * 
	 * @param aTick
	 * @param tickClassification
	 * @return true if I have a new bar
	 */
	private boolean _acceptTickAfterOpen(RealTick aTick,
			EInputSymbol tickClassification) {

		switch (tickClassification) {
		case TICK_DOWN:
			// I create a down bar
			_state = EState.AT_LOW_LOW_BAR;
			break;
		case TICK_UP:
			_state = EState.AT_HIGH_HIGH_BAR;
			break;
		case UNDECIDED:
		default:
			throw new IllegalStateException();

		}

		_currentExpansion[2] = aTick.clone();
		// I do not have a bar, yet.
		return false;
	}

	private boolean _acceptTickHighHighBar(RealTick aTick,
			EInputSymbol tickClassification) {
		// I may create an inverted T bar

		switch (tickClassification) {
		case TICK_UP:
			_emitTwoTicksBar();
			_createNewBar(aTick);
			return true;
		case TICK_DOWN:
			_processTorInvertedT(aTick);
			_state = EState.AT_LOW_INVERTED_T_BAR;
			return false;
		case UNDECIDED:
		default:
			throw new IllegalStateException();
		}
	}

	private boolean _acceptTickHighTBar(RealTick aTick,
			EInputSymbol tickClassification) {

		switch (tickClassification) {
		case TICK_DOWN:
			_processTickForaT(aTick);
			_state = EState.AT_LOW_LOW_BAR;
			return false;
		case TICK_UP:
			// exited the range!
			_emitThreeTicksBar();
			_createNewBar(aTick);
			return true;
		case UNDECIDED:
		default:
			throw new IllegalStateException();

		}
	}

	/**
	 * This state is only at the beginning, after that, when a new bar is
	 * created the state goes to {@link EState#AFTER_OPEN}.
	 * 
	 * <p>
	 * This state makes only the kick off of the filtering stage.
	 * 
	 * @param aTick
	 *            the initial tick
	 * @param tickClassification
	 * @return
	 */
	private boolean _acceptTickInitialState(RealTick aTick,
			EInputSymbol tickClassification) {
		// If this fails you are using a not initialized filter.
		assert (tickClassification == EInputSymbol.UNDECIDED);
		// This tick is a candidate to be a new open.
		_currentExpansion[0] = aTick.clone();
		_state = EState.AFTER_OPEN;
		return false;
	}

	private boolean _acceptTickInvertedTBar(RealTick aTick,
			EInputSymbol tickClassification) {

		// I am in an inverted t, so if I go down I exit the bar

		switch (tickClassification) {
		case TICK_UP:
			_processTickForaT(aTick);
			_state = EState.AT_HIGH_HIGH_BAR;
			return false;
		case TICK_DOWN:
			// exited the range!
			_emitThreeTicksBar();
			_createNewBar(aTick);
			return true;
		case UNDECIDED:
		default:
			throw new IllegalStateException();

		}
	}

	/**
	 * In this state I can either:
	 * 
	 * <p>
	 * <li>create a 2 prices bar
	 * <li>return to a 3 prices bar
	 * 
	 * @param aTick
	 * @param tickClassification
	 * @return
	 */
	private boolean _acceptTickLowLowBar(RealTick aTick,
			EInputSymbol tickClassification) {

		// I may exit the range or return (or create) a T bar.

		switch (tickClassification) {
		case TICK_DOWN:
			_emitTwoTicksBar();
			_createNewBar(aTick);
			return true;
		case TICK_UP:
			_processTorInvertedT(aTick);
			_state = EState.AT_HIGH_T_BAR;
			return false;
		case UNDECIDED:
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * This method simply tries to check the coherence in the automaton, after
	 * each tick.
	 * 
	 * <p>
	 * It is not strictly necessary and may be removed later.
	 * <p>
	 * This method will also check the total current mass of the current bar,
	 */
	private void _checkCoherenceInAutomaton() {
		switch (_state) {
		case AFTER_OPEN:
		case INITIAL_STATE:
		default:
			break;
		case AT_HIGH_HIGH_BAR:
		case AT_LOW_LOW_BAR:
			_checkCoherenceOfTwoTicksBar();
			break;
		case AT_HIGH_T_BAR:
		case AT_LOW_INVERTED_T_BAR:
			_checkCoherenceOfThreeTicksBar();
			break;
		}

		if (_currentBarMass != getAccumulatedVolume()) {
			throw new IllegalStateException("Received " + _currentBarMass
					+ " units, the cur bar has " + getAccumulatedVolume());
		}
	}

	private void _checkCoherenceOfThreeTicksBar() {
		_checkOneRangeMovementBetween(0, 1);
		_checkOneRangeMovementBetween(1, 2);
	}

	/**
	 * the current bar is made of two prices, I have to check its coherence.
	 */
	private void _checkCoherenceOfTwoTicksBar() {
		_checkOneRangeMovementBetween(0, 2);
	}

	/**
	 * Simple helper method that ensures that:
	 * 
	 * <p>
	 * <li>The future tick happens in the future
	 * <li>The distance in ticks exact from past to future is one.
	 * 
	 * @param pastTick
	 * @param futureTick
	 */
	private void _checkOneRangeMovementBetween(int pastTick, int futureTick) {
		if (!_currentExpansion[futureTick]
				.happensAfter(_currentExpansion[pastTick])) {
			throw new IllegalStateException();
		}

		if (_currentExpansion[pastTick].getExactDeltaTicksFrom(
				_currentExpansion[futureTick], _tick) != 1) {
			throw new IllegalStateException();
		}

	}

	private void _createNewBar(RealTick aTick) {
		_resetCurrentExpansion();
		_currentExpansion[0] = aTick.clone();
		_state = EState.AFTER_OPEN;
		_candidateRealOpen = null;
		_candidateRealClose = null;
		_currentBarMass = 0;
		_tracedTicks.clear();
	}

	private void _emitThreeTicksBar() {
		this._lastCompletedBar.clear();
		this._lastCompletedBar.add(_currentExpansion[0]);
		this._lastCompletedBar.add(_currentExpansion[1]);
		this._lastCompletedBar.add(_currentExpansion[2]);
	}

	/**
	 * helper method to emit a bar of two ticks, reference are copied, because
	 * they were cloned before, and I won't use them any more, it is safe to
	 * share them
	 */
	private void _emitTwoTicksBar() {
		this._lastCompletedBar.clear();
		this._lastCompletedBar.add(_currentExpansion[0]);
		this._lastCompletedBar.add(_currentExpansion[2]);
	}

	/**
	 * In this method I will return to a two ticks bar. I have to stay careful
	 * about the mass, the close which will be overwritten will have to transfer
	 * the mass to the open.
	 * 
	 * <p>
	 * We must take care of adding volumes with the same prices, otherwise we
	 * mix up the bar.
	 * 
	 * @param aTick
	 *            the tick which will be the new close of the newly 2-ticks bar.
	 */
	private void _processTickForaT(RealTick aTick) {
		// the current tick is at the low (or high, depending if this is a t
		// or inversed t) level
		if (aTick.getPrice() != _currentExpansion[1].getPrice()) {
			throw new IllegalStateException();
		}

		if (_candidateRealOpen != null) {
			// I come after the candidate real open, so I can overwrite it, but
			// I must conserve the mass.
			int savedVoume = _currentExpansion[0].getVolume();
			_currentExpansion[0] = _candidateRealOpen;
			_currentExpansion[0].accumulateVolume(savedVoume);
			// I return to be a 2 prices bar, so this is the new close,
			// this cloning is unconditionally, BUT I may return to be a 3
			// prices bar... so if the close was good (real) I should save
			// it.
			_currentExpansion[2] = aTick.clone();
			_candidateRealOpen = null;
		} else {
			if (_currentExpansion[2].getReal()) {
				// I save the close if I would return to a three price bar
				_candidateRealClose = _currentExpansion[2];
			}
			_currentExpansion[0].accumulateVolume(_currentExpansion[2]
					.getVolume());
			_currentExpansion[2] = aTick.clone();
		}

		// In case this becomes a new T bar I overwrite also the low, if it
		// was fake
		if (!_currentExpansion[1].getReal()) {
			_substituteAndAccumulateVolume(1, aTick);
			_currentExpansion[2].setVolume(_currentExpansion[1].getVolume());
		} else {
			// the 1 tick is real, I have to accumulate the volume not,
			// substituting it.
			_currentExpansion[2].accumulateVolume(_currentExpansion[1]
					.getVolume());
		}

	}

	private void _processTorInvertedT(RealTick aTick) {
		// the current price is equal to the open
		if (aTick.getPrice() != _currentExpansion[0].getPrice()) {
			throw new IllegalStateException();
		}

		/*
		 * I am building a t or inverted t. If the open was fake, the new DOWN
		 * could be the new open
		 */
		if (aTick.getReal() && !_currentExpansion[0].getReal()) {
			// this could be a new open, if I will receive a new low
			_candidateRealOpen = aTick.clone();
		}
		/*
		 * The past close could be the new high, the chronological order is
		 * respected. But this extreme (low or high) could have stayed behind
		 * this tick..., so this is the reason for the (happens after) code, the
		 * low(high) could have stayed before the open, if the open was
		 * substituted with the candidate.
		 */
		if (!_currentExpansion[1].getReal()
				|| !_currentExpansion[1].happensAfter(_currentExpansion[0])) {
			_currentExpansion[1] = _currentExpansion[2];
		} else {
			// not happens after, I may simply update the volume!
			_currentExpansion[1].setVolume(_currentExpansion[2].getVolume());
		}

		// I must always save the close here, because they are different!
		if (aTick.equalsPrice(_currentExpansion[2])) {
			throw new IllegalStateException();
		}

		// The problem here is that I could have a real close waiting
		if (_candidateRealClose != null) {
			if (!_candidateRealClose.equalsPrice(aTick)) {
				throw new IllegalStateException();
			}
			int savedVolume = _currentExpansion[2].getVolume();
			if (aTick.getReal()) {
				/*
				 * I am building a 3 ticks bar, this means that I have to save
				 * this volume, before overwriting it.
				 */
				_currentExpansion[2] = aTick.clone();
			} else {
				_currentExpansion[2] = _candidateRealClose;
				_currentExpansion[2].setVolume(aTick.getVolume());
				_candidateRealClose = null;
			}
			_currentExpansion[1].setVolume(savedVolume);
		} else {
			_currentExpansion[2] = aTick.clone();
		}

	}

	private void _resetCurrentExpansion() {
		for (int i = 0; i < _currentExpansion.length; ++i) {
			_currentExpansion[i] = new RealTick(0, 0, false);
		}

	}

	/*
	 * Substitutes the tick to the current expansion but also accumulating the
	 * volume.
	 */
	private void _substituteAndAccumulateVolume(int aIndex, RealTick aTick) {
		int savedValume = 0;
		if (_currentExpansion[aIndex] == null) {
			return;
		}
		if (_currentExpansion[aIndex].getPrice() == aTick.getPrice()) {
			savedValume = _currentExpansion[aIndex].getVolume();
		}
		_currentExpansion[aIndex] = aTick.clone();
		_currentExpansion[aIndex].accumulateVolume(savedValume);
	}

	/**
	 * Accepts the tick and complains first of all if it does not fulfill the
	 * {@link OneTickAscendingFilter} condition.
	 * 
	 * <p>
	 * Then it analyzes the tick and finds if it can be put to the outside or
	 * not.
	 * 
	 * <p>
	 * It is safe to reuse the tick given to this filter, as the tick is cloned,
	 * if needed.
	 * 
	 * @param aTick
	 * @return a list if this tick has created a new bar, null otherwise. The
	 *         ticks are in order.
	 */
	public List<RealTick> acceptTick(RealTick aTick) {

		// U.debug_var(993911, "HIST_FILTER[", this.hashCode(),
		// "] accepting tick ", aTick, " in state ", _state);

		EInputSymbol tickClassification = this._classifyInputTick(aTick);

		boolean res;

		switch (_state) {
		case AFTER_OPEN:
			res = _acceptTickAfterOpen(aTick, tickClassification);
			break;
		case AT_HIGH_HIGH_BAR:
			res = _acceptTickHighHighBar(aTick, tickClassification);
			break;
		case AT_HIGH_T_BAR:
			res = _acceptTickHighTBar(aTick, tickClassification);
			break;
		case AT_LOW_INVERTED_T_BAR:
			res = _acceptTickInvertedTBar(aTick, tickClassification);
			break;
		case AT_LOW_LOW_BAR:
			res = _acceptTickLowLowBar(aTick, tickClassification);
			break;
		case INITIAL_STATE:
			res = _acceptTickInitialState(aTick, tickClassification);
			break;
		default:
			throw new IllegalStateException();
		}

		/*
		 * I have the mass in input, it has to correspond to the mass in output,
		 * only after I have made the computation I update the current bar mass,
		 * because I may have created a new bar.
		 */
		_currentBarMass += aTick.getVolume();
		_tracedTicks.add(aTick.clone());

		_checkCoherenceInAutomaton();

		// U.debug_var(993911, "HIST_FILTER[", this.hashCode(),
		// "] I exit with res 	", res, " in state ", _state);

		if (!res) {
			return null;
		}
		return getCompletedBar();

	}

	/**
	 * Returns the volume accumulated so far in the filter. Used to check the
	 * conservation of mass.
	 * 
	 * @return the accumulated volume of the bar that would be returned if we
	 *         called the {@link #getIncompleteExpansion()}.
	 */
	private int getAccumulatedVolume() {
		int volume = 0;
		switch (_state) {
		case AFTER_OPEN:
			// I have a degenerate bar with only the open
			volume = _currentExpansion[0].getVolume();
			break;
		case AT_HIGH_HIGH_BAR:
		case AT_LOW_LOW_BAR:
			volume += _currentExpansion[0].getVolume();
			volume += _currentExpansion[2].getVolume();
			break;
		case AT_HIGH_T_BAR:
		case AT_LOW_INVERTED_T_BAR:
			volume += _currentExpansion[0].getVolume();
			volume += _currentExpansion[1].getVolume();
			volume += _currentExpansion[2].getVolume();
			break;
		case INITIAL_STATE:
		default:
		}

		return volume;

	}

	/**
	 * returns the complete bar. This
	 * 
	 * @return
	 */
	private List<RealTick> getCompletedBar() {
		if (_state != EState.AFTER_OPEN) {
			throw new IllegalStateException();
		}
		return java.util.Collections.unmodifiableList(_lastCompletedBar);
	}

	/**
	 * returns the incomplete expansion of this filter, used in the transition
	 * between historical and real time
	 * 
	 * @return the ticks which are stored in this filter, but that do not yet
	 *         constitute a bar
	 */
	public List<RealTick> getIncompleteExpansion() {
		switch (_state) {
		case AFTER_OPEN:
			// I have a degenerate bar with only the open
			_lastCompletedBar.clear();
			_lastCompletedBar.add(_currentExpansion[0]);
			break;
		case AT_HIGH_HIGH_BAR:
		case AT_LOW_LOW_BAR:
			_emitTwoTicksBar();
			break;
		case AT_HIGH_T_BAR:
		case AT_LOW_INVERTED_T_BAR:
			_emitThreeTicksBar();
			break;
		case INITIAL_STATE:
		default:
			_lastCompletedBar.clear();
		}

		return java.util.Collections.unmodifiableList(_lastCompletedBar);
	}

}
