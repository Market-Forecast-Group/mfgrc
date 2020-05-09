package com.mfg.dm;

import static com.mfg.utils.FinancialMath.getDeltaTicks;
import static com.mfg.utils.FinancialMath.get_delta_from_band;
import static com.mfg.utils.FinancialMath.is_contrary_price_to_band;

import java.util.ArrayList;

import com.mfg.common.Tick;

/**
 * This is the normal filter one tick.
 * 
 * @author Pasqualino
 * 
 */
public class FilterOneTick {

	private int tick; // the usual tick

	public FilterOneTick(int tick1) {
		this.tick = tick1;
	}

	/**
	 * The state of the filter functions
	 */
	public enum STATE {
		NO_TICKS, ONE_TICK, SIMPLE_BAND, COMPLETE_BAND
	}

	public STATE state = STATE.NO_TICKS;

	public long pv1;
	public long pv2;

	public Tick last_tick_2;

	/**
	 * This is not really a state... it is the object which is used to return
	 * the data filtered.
	 */
	public ArrayList<Tick> fot_return = new ArrayList<>();

	public ArrayList<Tick> filter(Tick tickIn) {

		this.fot_return.clear();
		// debug_var(768589, "fodstate ", this.state, " pv1 ", this.pv1 ,
		// " pv2 ",
		// this.pv2 , " tk " , tickIn.price , " ts " , tick_size);

		/* The filtering is NOT automatic unless requested */
		boolean filtered = false;

		switch (this.state) {
		case NO_TICKS:
			this.pv1 = tickIn.getPrice();
			this.state = FilterOneTick.STATE.ONE_TICK;
			break;
		case ONE_TICK:

			// get the distance
			int deltaTicks = getDeltaTicks(tickIn.getPrice(), this.pv1,
					this.tick);

			if (deltaTicks == 0) {
				filtered = true;
			} else if (deltaTicks >= 2) {
				this.pv1 = tickIn.getPrice();
				// this.state = FilterOneTick.STATE.NO_TICKS; //? Why? If the
				// distance is greater I start a new band... with the
				// new pivot!
			} else {
				// delta ticks == 1.
				this.pv2 = tickIn.getPrice();
				this.state = FilterOneTick.STATE.SIMPLE_BAND;
			}
			break;
		case SIMPLE_BAND:
			// OK, I have the simple band.
			if (tickIn.getPrice() == this.pv2) {
				// filter!
				filtered = true;
			} else if (tickIn.getPrice() == this.pv1) {
				// OK, I have a complete band! I don't filter it.
				this.state = FilterOneTick.STATE.COMPLETE_BAND;
			} else {
				// OK,2 I have a price not in the band, let's consider
				// the distance.
				deltaTicks = get_delta_from_band(this.pv1, this.pv2,
						tickIn.getPrice(), this.tick);

				if (deltaTicks == 1) {
					// no change in state
					if (!is_contrary_price_to_band(this.pv1, this.pv2,
							tickIn.getPrice())) {
						// I rotate the pivots
						this.pv1 = this.pv2;
						this.pv2 = tickIn.getPrice();
					} else {
						// I simply switch (there will be a gap!);
						this.pv2 = tickIn.getPrice();
					}
				} else {
					assert (deltaTicks >= 2);
					this.pv1 = tickIn.getPrice();
					this.state = FilterOneTick.STATE.ONE_TICK;
				}
			}
			break;

		case COMPLETE_BAND:

			// the most complex case, the complete band.
			if (this.pv1 == tickIn.getPrice()) {
				// no chance, I will throw it away.
				filtered = true;
			} else if (this.pv2 == tickIn.getPrice()) {
				filtered = true;
				this.last_tick_2 = tickIn;
			} else {
				// OK, Now I get the distance, In any case I will get
				// away from this state!
				deltaTicks = get_delta_from_band(this.pv1, this.pv2,
						tickIn.getPrice(), this.tick);

				if (deltaTicks == 1) {
					if (!is_contrary_price_to_band(this.pv1, this.pv2,
							tickIn.getPrice())) {
						this.pv1 = this.pv2;
						if (this.last_tick_2 != null) {
							this.fot_return.add(this.last_tick_2);
						}
					}
					this.pv2 = tickIn.getPrice();
					this.state = FilterOneTick.STATE.SIMPLE_BAND;
				} else {
					assert (deltaTicks >= 2);
					this.pv1 = tickIn.getPrice();
					this.state = FilterOneTick.STATE.ONE_TICK;
				}

				this.last_tick_2 = null;
			}
			break;
		}

		if (!filtered) {
			this.fot_return.add(tickIn);
		}
		// else{
		// debug_var(965337, "fot is filtered");
		// }

		return this.fot_return;

	}
}