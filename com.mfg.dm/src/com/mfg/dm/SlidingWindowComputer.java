package com.mfg.dm;

import java.util.Random;

/**
 * This object is able to compute a statistic on a series to know how many
 * prices are used to fill a gap. This object is used during warm up and also in
 * real time to have the possibility to fill the gaps with a markov process.
 * 
 * This is a simple finite state machine which is used to compute the amount of
 * prices needed for each gap.
 * 
 * @author Sergio
 * 
 */
public class SlidingWindowComputer {

	public SlidingWindowComputer(long seed) {
		this.rnd = new Random(seed);
	}

	/**
	 * These are final variables that are used by the computer.
	 */
	private static final int N_WINDOWS = 15;
	private static final int MAX_TRIES = 10;
	private static final int MINIMUM_WINDOWS = 7;

	enum SLIDE_WINDOW_ANSWER {
		CROSSED, NOT_CROSSED, GAP_CROSSED
	}

	private boolean first_price = true;
	private int start_price;
	private int previous_price;
	private int prices_accumulator = 0;
	private Random rnd;

	private boolean fInvalid = false;

	/**
	 * Resets the computer to its initial state
	 */
	private void reset() {
		fInvalid = false;
		first_price = true;
		prices_accumulator = 0;
	}

	/**
	 * This is the update state function. You cannot call this function when the
	 * Finite state machine is in an invalid state
	 * 
	 * @param price
	 *            the price is an integer, it is the number of ticks
	 * @param aDelta
	 * @return
	 */
	private SLIDE_WINDOW_ANSWER accept(int price, int aDelta) {
		if (fInvalid) {
			throw new IllegalStateException(
					"Cannot accept a price in an invalid state!");
		}

		if (this.first_price) {
			this.first_price = false;
			this.start_price = price;
			this.previous_price = price;
			return SLIDE_WINDOW_ANSWER.NOT_CROSSED;
		}

		short cur_delta = (short) Math.abs(price - this.previous_price);

		if (cur_delta > 1) {
			// debug_var(295267, "Gap crossed @ ", price, " previous ",
			// this.previous_price);
			fInvalid = true;
			return SLIDE_WINDOW_ANSWER.GAP_CROSSED;
		}

		this.prices_accumulator += cur_delta;
		this.previous_price = price;

		short delta_from_first = (short) Math.abs(price - this.start_price);
		// debug_var(926926, "Accepted the price ", price,
		// " delta from first ", delta_from_first,
		// " range total ", ranges_accumulator);

		if (delta_from_first >= aDelta) {
			// debug_var(252676, "Crossed the delta @ ", price );
			return SLIDE_WINDOW_ANSWER.CROSSED;
		}
		return SLIDE_WINDOW_ANSWER.NOT_CROSSED;
	}

	/**
	 * Estimates the number of prices needed to fill a gap.
	 * 
	 * <p>
	 * This of course depends on the price series given (which is not altered).
	 * If the price sequence does not have the gap requested a minor statistic
	 * is done with a lower gap, until a suitable stat is found.
	 * 
	 * <p>
	 * If the algorithm does not find a statistic it throws an exception.
	 * 
	 * @param aPriceArr
	 *            the array of prices, they should be in order.
	 * @param gap
	 * @return
	 * @throws Exception
	 *             if the gap is not found
	 * @throws IllegalArgumentException
	 *             if the gap is less then 2 or if the array length is zero.
	 */
	public double estimateGap(int[] aPriceArr, final int gap) throws Exception {
		if (gap < 2 || aPriceArr == null || aPriceArr.length == 0) {
			throw new IllegalArgumentException();
		}

		reset();
		int gapFound = gap;
		for (;;) {
			double ans = estimateGapImpl(aPriceArr, gapFound);
			if (ans > 0) {
				double ansNew = ans * ((double) gap / (double) gapFound);
				// debug_var(239103, "filled the gap ", gapFound, " with ", ans,
				// " prices, requested gap ", gap, " answer becomes: ",
				// ansNew);
				return ansNew;
			}
			gapFound--;
			if (gapFound < 2) {
				throw new Exception("cannot find a suitable gap");
			}
		}
	}

	/**
	 * @return the number of prices needed to fill this gap, given the array of
	 *         prices. -1 if it could not find a suitable statistics for the
	 *         computing the gap.
	 * 
	 */
	private double estimateGapImpl(int[] aPriceArr, final int gap) {
		int total_prices_accumulated = 0;
		int cur_win;

		GIVING_UP: for (cur_win = 0; cur_win < N_WINDOWS; ++cur_win) {
			int n_tries = 0;
			boolean found_window = false;

			while (!found_window) {
				int starting_index = (int) (this.rnd.nextDouble() * aPriceArr.length);
				reset();
				for (int i = starting_index; i < aPriceArr.length; ++i) {

					SLIDE_WINDOW_ANSWER res = this.accept(aPriceArr[i], gap);

					if (res == SLIDE_WINDOW_ANSWER.GAP_CROSSED) {
						break;
					} else if (res == SLIDE_WINDOW_ANSWER.CROSSED) {
						found_window = true;
						total_prices_accumulated += prices_accumulator;
						continue GIVING_UP;
					}
				}

				n_tries++;
				if (n_tries > MAX_TRIES) {
					break GIVING_UP;
				}
			}
		}

		if (cur_win < MINIMUM_WINDOWS) {
			return -1; // cannot find an exact number of windows.
		}
		double avg_range = (double) total_prices_accumulated / (double) cur_win;
		return avg_range;
	}

}
