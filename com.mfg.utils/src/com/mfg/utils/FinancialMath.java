package com.mfg.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.mfg.common.DFSException;

public class FinancialMath {

	/**
	 * In our application we consider the prices as integers, without the
	 * decimal point.
	 * 
	 * <p>
	 * The scale is a property of the contract and it is a configuration item.
	 * 
	 * <p>
	 */
	private static final BigInteger MAX_PRICE_ALLOWED = new BigInteger(
			"999999999"); // they are 9 nines.

	/**
	 * Converts a string to a int, using a certain scale.
	 * 
	 * <p>
	 * This method makes also all the tests to ensure that the conversion goes
	 * well.
	 * 
	 * <p>
	 * Negative prices are not allowed.
	 * 
	 * @throws DFSException
	 *             if the price cannot be converted, there is probably a round
	 *             necessary, the scale or the price is invalid.
	 */
	public static int stringPriceToInt(String price_s, int scale)
			throws DFSException {

		BigDecimal bd = new BigDecimal(price_s);
		BigInteger bi;
		try {
			bi = bd.setScale(scale).unscaledValue();
		} catch (ArithmeticException e) {
			throw new DFSException("cannot convert " + price_s + " with scale "
					+ scale);
		}

		if (bi.signum() < 0) {
			throw new IllegalArgumentException(
					"negative prices are not allowed.");
		}
		if (bi.compareTo(MAX_PRICE_ALLOWED) > 0) {
			throw new IllegalArgumentException("Number too big : "
					+ bi.toString());
		}
		return bi.intValue();

	}

	public static int bigDecimalToIntCheck(BigDecimal bd, int scale,
			int tick_size) {
		BigInteger bi = bd.setScale(scale).unscaledValue();
		if (bi.signum() < 0) {
			throw new IllegalArgumentException(
					"negative prices are not allowed.");
		}
		if (bi.compareTo(MAX_PRICE_ALLOWED) > 0) {
			throw new IllegalArgumentException("Number too big : "
					+ bi.toString());
		}
		if (bi.mod(new BigInteger("" + tick_size)) != BigInteger.ZERO) {
			throw new IllegalArgumentException(
					"price not multiple of the tick " + bd);
		}
		return bi.intValue();
	}

	/**
	 * We have p1 + get_p1_to_p2_delta_ticks(p1,p2,tick) * tick == p2.
	 * 
	 * @return the relative distance between p1 to p2 in ticks.
	 */
	public static int get_p1_to_p2_delta_ticks(long p1, long p2, int tickSize) {
		return (int) ((p2 - p1) / tickSize);
	}

	/**
	 * @return Return the delta (minimum) of the price from the band. 0 if the
	 *         price is equal to one of the prices in the band.
	 */
	public static int get_delta_from_band(long p1, long p2, long pnow,
			int tick_size) {

		if ((p1 == pnow) || (p2 == pnow)) {
			return 0;
		}

		boolean contrary_band = is_contrary_price_to_band(p1, p2, pnow);

		long price_to_consider = contrary_band ? p1 : p2;

		int deltaTicks = getDeltaTicks(pnow, price_to_consider, tick_size);

		return deltaTicks;

	}

	/**
	 * @return true if pnow is contrary to the band defined by p1 (before) and
	 *         p2 (after).
	 */
	public static boolean is_contrary_price_to_band(long p1, long p2, long pnow) {
		boolean up_band = p1 < p2;
		boolean contrary_band = (up_band && (pnow < p1))
				|| (!up_band && (pnow > p1));

		return contrary_band;

	}

	/**
	 * This function returns the delta in ticks (absolute) between two prices.
	 */
	public static int getDeltaTicks(long p1, long p2, int tickSize) {
		return (int) Math.abs((p1 - p2) / tickSize);
	}

	/**
	 * returns the exact absolute difference between two prices in ticks.
	 * <p>
	 * It will burst if the difference is not an exact multiple of the ticksize
	 * 
	 * @param p1
	 *            first price
	 * @param p2
	 *            second price
	 * @param tickSize
	 *            the tick size
	 * @return the tick difference between two prices
	 * 
	 * @throws IllegalArgumentException
	 *             if the delta is not an exact tick multiplier
	 */
	public static int getExactDeltaTicks(int p1, int p2, int tickSize) {
		int delta = Math.abs(p1 - p2);
		if (delta % tickSize != 0) {
			throw new IllegalArgumentException();
		}
		return delta / tickSize;
	}

	public static int compute_tick_size(BigDecimal[] diffs, int computed_scale) {

		long ranges[] = new long[diffs.length];

		for (int i = 0; i < ranges.length; ++i) {
			ranges[i] = -1;
		}

		int i = 0;
		for (BigDecimal bd : diffs) {
			if (bd == null) {
				break;
			}

			BigDecimal bd_scaled = bd.setScale(computed_scale);
			ranges[i] = bd_scaled.unscaledValue().longValue();

			++i;
		}

		if (i == 1) {
			// only one range? I duplicate it
			ranges[i] = ranges[0];
		}

		return (int) gcd(ranges);

	}

	private static long gcd(long[] arr) {
		return gcd_rec(arr, 0);
	}

	private static long gcd_rec(long[] arr, int start_idx) {
		if (arr.length - start_idx == 2 || (arr[start_idx + 2] == -1)) {
			return gcd(arr[start_idx], arr[start_idx + 1]);
		}
		return gcd(arr[start_idx], gcd_rec(arr, start_idx + 1));
	}

	private static long gcd(long a, long b) {
		if (b == 0) {
			return a;
		}
		return gcd(b, a % b);
	}

	/**
	 * returns true if the price is inside the range (with extremes included).
	 * 
	 * <p>
	 * The low must be <= high, unreal results otherwise... (garbage in, garbage
	 * out)
	 * 
	 * @param price
	 *            the price you want to test
	 * @param low
	 *            the low price
	 * @param high
	 *            the high price (it can be equal to low)
	 * @return true if price is between low and high (inclusive)
	 */
	public static boolean isPriceInRange(long price, long low, long high) {
		if (price >= low && price <= high) {
			return true;
		}
		return false;
	}

}
