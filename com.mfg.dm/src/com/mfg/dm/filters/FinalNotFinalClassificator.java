package com.mfg.dm.filters;

import com.mfg.common.RealTick;
import com.mfg.dm.FillGapsMachine;
import com.mfg.utils.FinancialMath;
import com.mfg.utils.U;

/**
 * This class is able to get in input a stream of real time ticks and it returns
 * zero, one or two ticks, depending on the relationship between this tick and
 * the past N ticks (where N is usually 4).
 * 
 * <p>
 * Think of it as a real time version of the filter 1p, where the filtering is
 * done but the filtered ticks is nevertheless given to the outside, but with a
 * flag that tells us if this tick is final or not.
 * 
 * <p>
 * Ticks that come from the gap filling algorithm are always final, but they may
 * be fake.
 * 
 * <p>
 * The filter takes into consideration a stream of four REAL prices.
 * 
 * <p>
 * A stream of 3 or less REAL prices are always final, so only in a series of 4
 * (or more) real prices we make the filter active.
 * 
 * <p>
 * This classificator needs prices which are at most one tick apart and in
 * strict chronological order as in the {@link OneTickAscendingFilter}
 * precondition
 * 
 * <p>
 * 
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class FinalNotFinalClassificator extends OneTickAscendingFilter {

	/**
	 * These are the possible answers from the filter.
	 * 
	 * <p>
	 * When only one tick is as the answer this tick is not copied to the
	 * outside.
	 * 
	 * <p>
	 * For caller convenience the filter stores the last tick given in a slot
	 * 
	 * <p>
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	public enum EAnswer {
		/**
		 * This answer means that the given tick (to the
		 * {@link FinalNotFinalClassificator#acceptTick(RealTick)} is final
		 */
		THIS_TICK_IS_FINAL,
		/**
		 * This answer means that the given tick is not final, but it may become
		 * final in the next state.
		 * <p>
		 * Clients are not required to store the tick somewhere, because the
		 * filter stores it for them.
		 * 
		 * <p>
		 * A not final tick is <b>always</b> real.
		 */
		THIS_TICK_IS_NOT_FINAL,
		/**
		 * This means that this and the previous ticks are final. When this
		 * answer happens it means that these last two ticks are real.
		 */
		PREVIOUS_AND_THIS_ARE_FINAL
	}

	/**
	 * This state machine has some states relative to the number of real prices
	 * that this machine has received in a row.
	 * 
	 * We start to filter real prices when we have received 4 (or more) prices
	 * in a row AND they must for a V or a ^,
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private enum State {
		INITIAL_STATE, PREVIOUS_FAKE_PRICE, ONE_REAL_PRICE, TWO_REAL_PRICES,
		/**
		 * This is the state when we start to filter the real price, in this
		 * state we filter all prices that are
		 * 
		 * - real - equal to one or the other price of the bar
		 */
		V_STATE
	}

	/**
	 * The classificator needs the volume dispenser to help it to share the
	 * different volumes.
	 */
	// VolumeDispenserFilter _vdf;

	/**
	 * All the filters which use this base are in reality a variation of a
	 * "one-tick" filter. This means that they have the memory of (at most) the
	 * last v (or ^), which is a complete bar (one range).
	 */
	protected RealTick _vBuffer[] = new RealTick[3];

	/**
	 * The starting state of the automaton.
	 */
	private State _state = State.INITIAL_STATE;

	private int _sharedVolume;

	// private int _newPrice;
	//
	// @SuppressWarnings("unused")
	// private int _newVolume;

	/**
	 * creates the classificator, this, as usual, wants the tick as a parameter.
	 * 
	 * @param aTick
	 */
	public FinalNotFinalClassificator(int aTick) {
		super(aTick);
		// _tick = aTick;

		// _vdf = new VolumeDispenserFilter();
	}

	private EAnswer _accept_previous_fake_price(RealTick aTick) {
		if (aTick.getReal()) {
			_vBuffer[0] = aTick.clone();
			_state = State.ONE_REAL_PRICE;
		} else {
			// fake? I remain in this state and I do not copy it
		}

		// In any case this tick is final
		return EAnswer.THIS_TICK_IS_FINAL;
	}

	/**
	 * This is the initial state, so I have nothing to compare against
	 * 
	 * @param aTick
	 * @return
	 */
	private EAnswer _accept_tick_initial(RealTick aTick) {
		// If this fail the machine is in a bad state, because I have to be here
		// only once.
		// assert (_lastTick != null);
		// assert (_lastTick.equals(aTick));

		if (aTick.getReal()) {
			// this may be the first tick of a V, so I store it
			_vBuffer[0] = aTick.clone();
			_state = State.ONE_REAL_PRICE;
		} else {
			_state = State.PREVIOUS_FAKE_PRICE;
		}
		return EAnswer.THIS_TICK_IS_FINAL;
	}

	private EAnswer _accept_tick_one_real(RealTick aTick) {
		if (aTick.getReal()) {
			// this tick is real, so I simply store it, it may be a Up or a
			// Down, but it does not matter, for us, because
			// in any case the final condition is to have the 3rd price equal to
			// the 1st.
			_vBuffer[1] = aTick.clone();
			_state = State.TWO_REAL_PRICES;

		} else { // no real? no party, I will restart from square 1
			_state = State.PREVIOUS_FAKE_PRICE;
		}

		return EAnswer.THIS_TICK_IS_FINAL;
	}

	private EAnswer _accept_two_real_prices(RealTick aTick) {
		if (aTick.getReal()) {
			// I have to know if this is the starting of a V or a ^, (v upward).
			/*
			 * The condition price == price_0 is sufficient because we have
			 * ensured at the starting of the tick, that this is a filter that
			 * accepts the ticks in one tick order.
			 */
			if (aTick.getPrice() == _vBuffer[0].getPrice()) {
				// ok, this is the starting of the V
				_vBuffer[2] = aTick.clone();
				_state = State.V_STATE;
			} else {
				// I remain in this state but I shift the ticks.
				_vBuffer[0] = _vBuffer[1];
				_vBuffer[1] = aTick.clone();
			}
		} else {
			// fake price, I return to square one
			_state = State.PREVIOUS_FAKE_PRICE;
		}

		// In any case this is a final price.
		return EAnswer.THIS_TICK_IS_FINAL;
	}

	/**
	 * @param aTick
	 */
	private EAnswer _accept_v_state(RealTick aTick) {
		/*
		 * This is the most complex state. In any case if I get a fake price I
		 * return to square zero, as always
		 */
		if (aTick.getReal()) {
			if ((aTick.getPrice() == _vBuffer[1].getPrice())
					|| (aTick.getPrice() == _vBuffer[2].getPrice())) {
				// Ok, this is a NOT FINAL TICK, the v remains of the same
				// shape, but I store the last final tick as a reference
				// (because when I exit the range I will need it to form
				// the other part of the V

				if (aTick.getPrice() == _vBuffer[1].getPrice()) {
					// the tick is equal to the apex (or bottom) or the v
					int oldVolume = _vBuffer[1].getVolume();
					_vBuffer[1] = aTick.clone();
					_vBuffer[1].accumulateVolume(oldVolume);
				} else {
					int oldVolume = _vBuffer[2].getVolume();
					_vBuffer[2] = aTick.clone();
					_vBuffer[2].accumulateVolume(oldVolume);
				}

				return EAnswer.THIS_TICK_IS_NOT_FINAL;
			}

			/*
			 * The shift is tricky, because I must shift remembering on which
			 * side of the V (or ^) I have exited the range.
			 */
			RealTick save1 = _vBuffer[1]; // to save it
			_vBuffer[1] = aTick.clone(); // this is fixed in all case
			_state = State.TWO_REAL_PRICES; // also this is fixed

			if (FinancialMath.getExactDeltaTicks(aTick.getPrice(),
					save1.getPrice(), _tick) == 1) {
				// exited from the apex, I have 2 final prices!

				/*
				 * This is the total volume that needs to be splitted and I save
				 * it.
				 */
				_sharedVolume = _vBuffer[0].getVolume();

				_vBuffer[0] = save1;
				return EAnswer.PREVIOUS_AND_THIS_ARE_FINAL;
			}
			// exited from the leg of the v, this only is final
			// there is no volume to be splitted.
			_vBuffer[0] = _vBuffer[2];
			return EAnswer.THIS_TICK_IS_FINAL;
		}
		// not real.
		_state = State.PREVIOUS_FAKE_PRICE;

		/*
		 * I have to know if I am exiting the V leaving a not final price
		 * behind. The following condition is used to know if I am exiting the V
		 * from the apex. If I do, then there MUST be a not-final tick saved
		 * which I must send to the outside
		 */
		if (aTick.getExactDeltaTicksFrom(this._vBuffer[1], _tick) == 1) {
			_sharedVolume = _vBuffer[0].getVolume();
			_vBuffer[0] = _vBuffer[1];
			return EAnswer.PREVIOUS_AND_THIS_ARE_FINAL;
		}

		// a fake price is always final
		return EAnswer.THIS_TICK_IS_FINAL;
	}

	/**
	 * accepts a tick as come from the {@link FillGapsMachine}.
	 * 
	 * <p>
	 * A fake tick is <b>always</b> final, because it comes from the fill gaps
	 * and has been already filtered, in any case it is not in real time (if
	 * there has been a gap the fill gap has filtered it and built a series of
	 * fake ticks in the past, which I want to pass)
	 * 
	 * @param aTick
	 * @return {@link EAnswer} indicating the result of this classification.
	 * 
	 * @throws IllegalArgumentException
	 *             If the tick you supply does not happens after and it is not
	 *             one tick different from the last.
	 */
	public EAnswer acceptTick(RealTick aTick) {

		this._classifyInputTick(aTick);

		EAnswer res;
		switch (_state) {
		case INITIAL_STATE:
			res = _accept_tick_initial(aTick);
			break;
		case ONE_REAL_PRICE:
			res = _accept_tick_one_real(aTick);
			break;
		case PREVIOUS_FAKE_PRICE:
			res = _accept_previous_fake_price(aTick);
			break;
		case TWO_REAL_PRICES:
			res = _accept_two_real_prices(aTick);
			break;
		case V_STATE:
			res = _accept_v_state(aTick);
			break;
		default:
			throw new IllegalStateException();
		}

		return res;

	}

	/**
	 * This method is used to get the previous final tick in case the previous
	 * answer has been {@linkplain EAnswer#PREVIOUS_AND_THIS_ARE_FINAL}
	 * 
	 * @return the previous final tick.
	 */
	public RealTick getPreviousFinalTick() {
		assert (_state == State.TWO_REAL_PRICES || _state == State.PREVIOUS_FAKE_PRICE);
		return _vBuffer[0];
	}

	/**
	 * gets the shared volume, the volume which has to be divided after the
	 * classificator returns the {@link EAnswer#PREVIOUS_AND_THIS_ARE_FINAL}
	 * answer.
	 * 
	 * @return the shared volume, of course it may be an odd number.
	 */
	public int getSharedVolume() {
		return _sharedVolume;
	}

	/**
	 * checks whether an external price is coherent to the last price seen by
	 * this filter.
	 * 
	 * @param aPrice
	 * @return true if aPrice is equal to the last price seen by this filter
	 */
	public boolean isCoherent(int aPrice) {
		return super._isCoherent(aPrice);
	}

	public int onRealTimeQuoteVolumeUpdate(int price, int volume) {

		switch (_state) {
		case INITIAL_STATE:
			return _accumulateVolume(0, price, volume);
		case ONE_REAL_PRICE:
			return _accumulateVolume(0, price, volume);
		case PREVIOUS_FAKE_PRICE:
			/*
			 * This should not happen, because a fake price does not have a
			 * volume update.
			 */
			throw new IllegalStateException();
		case TWO_REAL_PRICES:
			return _accumulateVolume(1, price, volume);
		case V_STATE:
			if (price == _vBuffer[1].getPrice()) {
				return _accumulateVolume(1, price, volume);
			}
			return _accumulateVolume(2, price, volume);
		default:
			throw new IllegalStateException();

		}

	}

	/**
	 * Helper method which is used to accumulate the volume of a particular
	 * price inside the final buffer.
	 * 
	 * <p>
	 * There is a consistency check based on the price.
	 * 
	 * @return
	 */
	@SuppressWarnings("boxing")
	private int _accumulateVolume(int index, int price, int volume) {
		if (_vBuffer[index].getPrice() != price) {
			U.debug_var(426167, "You want to update ", price, " but @ ", index,
					" I have ", _vBuffer[index].getPrice(), " state ", _state);
			return -1;
		}

		int res = _vBuffer[index].accumulateVolume(volume);
		U.debug_var(293051, "volume update @ ", _state, " p ",
				_vBuffer[index].getPrice(), " delta ", volume, " vol ",
				_vBuffer[index].getVolume());
		return res;

	}

	/**
	 * gets the last not final volume: this is the last volume
	 * 
	 * @return
	 */
	public int getVolumeLastNotFinal(int price) {
		if (price == _vBuffer[1].getPrice()) {
			return _vBuffer[1].getVolume();
		}
		return _vBuffer[2].getVolume();
	}
}
