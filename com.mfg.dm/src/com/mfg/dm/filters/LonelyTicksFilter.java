package com.mfg.dm.filters;

import com.mfg.common.Tick;
import com.mfg.utils.U;

/**
 * A filter for outlier ticks, that is ticks which are too far away from the
 * previous and the next tick.
 * 
 * <P>
 * Something like: 10, 11, 20, 10... the "20" tick is an outlier, because it is
 * preceded and followed by a tick more than some ticks away (the number of
 * ticks is configurable)
 * 
 * <p>
 * The ticks must come in ascending time, but they do not need to have one tick
 * distance, of course.
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class LonelyTicksFilter {

	private final int _tick;
	private final int _minimumGap;

	public LonelyTicksFilter(int aTick, int aMinimumGap) {
		_tick = aTick;
		_minimumGap = aMinimumGap;
	}

	/**
	 * These are the possible answer from the filter.
	 * 
	 * <p>
	 * They are only 3 answers:
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	public enum EAnswer {
		/**
		 * The last tick given to filter is OK.
		 * 
		 * <P>
		 * if the previous answer was IN JAIL, this means that the previous tick
		 * is discarded
		 */
		THIS_TICK_IS_OK,
		/**
		 * the last tick given to filter has been put in jail, do not pass it
		 */
		THIS_TICK_IS_IN_JAIL,
		/**
		 * this tick and the previous one are OK
		 */
		THIS_AND_PREVIOUS_TICKS_ARE_OK
	}

	private enum EState {
		/**
		 * I am blank, no ticks are arrived yet
		 */
		BLANK,
		/**
		 * The previous tick was OK
		 */
		NO_TICK_IN_JAIL,
		/**
		 * The previous tick has been put in jail, I have to decide wheter to
		 * free it or not
		 */
		TICK_IN_JAIL,
		/**
		 * When the tick is disabled it becomes a no-op, all ticks are OK.
		 */
		DISABLED
	}

	private EState _state = EState.BLANK;
	private Tick _prevTick;
	private Tick _freedomTick;
	private Tick _prevOkTick;

	// private boolean _enabled = true;

	/**
	 * Accepts the tick from the outside and it decides to filter it or not
	 * based on the state
	 * 
	 * @param aTick
	 *            the tick to accept, can be reused (the filter makes a copy)
	 * @return the answer after receiving this tick
	 */
	public EAnswer acceptTick(Tick aTickPar) {
		/*
		 * I clone it at the beginning of the method because the tick is always
		 * saved.
		 */
		Tick inTickCopy = aTickPar.clone();

		// ascending time and 0P tests
		if (_prevTick != null) {
			if (!inTickCopy.happensAfter(_prevTick)) {
				throw new IllegalArgumentException();
			}
			if (inTickCopy.equalsPrice(_prevTick)) {
				U.debug_var(981939, "tick in ", inTickCopy,
						" equals (in price) to ", _prevTick);
				throw new IllegalArgumentException();
			}
		}

		EAnswer res;
		switch (_state) {
		case DISABLED:
			res = EAnswer.THIS_TICK_IS_OK;
			break;
		case BLANK:
			_state = EState.NO_TICK_IN_JAIL;
			res = EAnswer.THIS_TICK_IS_OK;
			break;
		case NO_TICK_IN_JAIL:
			/*
			 * By definition of the state, if the filter is here the previous
			 * tick was OK
			 */
			int delta = _prevTick.getExactDeltaTicksFrom(inTickCopy, _tick);
			if (delta > _minimumGap) {
				/* this tick go in jail, just in case */
				_state = EState.TICK_IN_JAIL;
				res = EAnswer.THIS_TICK_IS_IN_JAIL;
				_prevOkTick = _prevTick;
			} else {
				res = EAnswer.THIS_TICK_IS_OK;
			}
			break;
		case TICK_IN_JAIL:
			delta = _prevTick.getExactDeltaTicksFrom(inTickCopy, _tick);
			if (delta > _minimumGap) {
				/*
				 * the previous tick was in jail and it is filtered this one is
				 * still in jail (because has a gap greater than the minimum)
				 */
				res = EAnswer.THIS_TICK_IS_IN_JAIL;
			} else {
				/*
				 * the previous tick can be freed, if it is not equal to the
				 * last given to the outside
				 */
				if (_prevTick.equalsPrice(_prevOkTick)) {
					res = EAnswer.THIS_TICK_IS_OK;
				} else {
					_freedomTick = _prevTick; // no need to clone
					res = EAnswer.THIS_AND_PREVIOUS_TICKS_ARE_OK;
				}
				_state = EState.NO_TICK_IN_JAIL;
			}

			break;

		default:
			throw new IllegalStateException();

		}
		// in any case this tick is saved
		_prevTick = inTickCopy;
		return res;
	}

	/**
	 * return the liberated tick, which was held waiting for the next
	 * 
	 * @return
	 */
	public Tick getPreviousTick() {
		return _freedomTick;
	}

	/**
	 * This call for coherence makes the filter coherent to the last time and
	 * price, if they are different.
	 * 
	 * <p>
	 * If the filter needs to change the price and or time it will return to a
	 * stable state, without ticks in jail
	 * 
	 * @param _lastTime
	 * @param _lastPrice
	 */
	@SuppressWarnings("boxing")
	public void makeCoherentTo(long _lastTime, int _lastPrice) {
		if (_prevTick != null && (_prevTick.getPrice() != _lastPrice)) {
			U.debug_var(819390, "Reverting my filter to ", _lastPrice,
					" and time ", _lastTime, " my real prev was ", _prevTick);
			_prevTick.setPhysicalTime(_lastTime);
			_prevTick.setPrice(_lastPrice);
			_state = EState.NO_TICK_IN_JAIL;
		}
	}

	/**
	 * Enables the filter.
	 * 
	 * <p>
	 * the first state after the enabling will be {@link EState#NO_TICK_IN_JAIL}.
	 */
	public void enableOn() {
		if (_prevTick != null) {
			_state = EState.NO_TICK_IN_JAIL;
		} else {
			_state = EState.BLANK;
		}
	}

	/**
	 * Disables the filter. When the filter is reenabled it will restart from
	 * the state {@link EState#NO_TICK_IN_JAIL}, with the last ok tick set
	 * correctly.
	 */
	public void enableOff() {
		_state = EState.DISABLED;
	}

}
