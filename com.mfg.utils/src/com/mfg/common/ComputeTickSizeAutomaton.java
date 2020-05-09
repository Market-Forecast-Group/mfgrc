package com.mfg.common;



import java.math.BigDecimal;

import com.mfg.utils.FinancialMath;

/**
 * This is the compute tick size automaton. It simply is used to compute the
 * tick size.
 * 
 * 
 * @author Pasqualino
 * 
 */
public class ComputeTickSizeAutomaton {
	public static final int RANGES = 20;
	public static final int NUM_PRICES_TO_CONSIDER = 50;

	/**
	 * This stores some different ranges, used to compute the tick.
	 */
	public BigDecimal gaps[] = new BigDecimal[RANGES];

	/**
	 * Stores the computed tick
	 */
	public int computedTick = 1;

	/**
	 * Stores the computed scale.
	 */
	public int computedScale = 0;

	/**
	 * I have the previous price to compute the gap
	 */
	public BigDecimal previousPrice = null;

	/**
	 * How many prices I have still to consider.
	 */
	public int pricesToConsiderLeft = NUM_PRICES_TO_CONSIDER;

	/**
	 * this is the method which will consider the price... the automaton will
	 * then compute the tick size using the array of strings
	 * 
	 * @param open_s
	 */
	public void considerThePrice(String price) {

		if (this.pricesToConsiderLeft == 0) {
			return;
		}

		BigDecimal cur_price = new BigDecimal(price);
		this.computedScale = Math.max(this.computedScale, cur_price.scale());

		if (this.previousPrice != null) {

			// Ok, I have the previous price, I compute the diff
			BigDecimal diff = this.previousPrice.subtract(cur_price).abs();

			for (int i = 0; i < RANGES; ++i) {
				BigDecimal cur_i = this.gaps[i];
				if (cur_i != null) {
					if (cur_i.compareTo(diff) == 0) {
						break;
					}
				} else {
					System.out.println("[360293] Stored the gap " + diff);
					this.gaps[i] = diff;
					break;
				}
			}

		}
		this.previousPrice = cur_price;
		this.pricesToConsiderLeft--;

		if (this.pricesToConsiderLeft == 0) {
			this.computedTick = FinancialMath.compute_tick_size(this.gaps, this.computedScale);
		}
	}
}