package com.mfg.utils;

import java.text.DecimalFormat;

import com.mfg.common.DFSException;

/**
 * This is a simple class which defines some utility functions relative to
 * prices.
 */
public class MathUtils {

	/**
	 * This class is not instantiable... just a container for helper functions.
	 */
	private MathUtils() {
	}

	// /////////////////
	// safe integer down-casts.
	// /////////////////

	/**
	 * Safely casts from a long value to an int value.
	 * 
	 * @param val
	 *            the long value to be converted.
	 * @return the integer which has the same arithmetic value of the long
	 * 
	 * @throws ArithmeticException
	 *             if the cast cannot be done.
	 */
	public static int longToIntSafe(long val) {
		if (val < Integer.MIN_VALUE || val > Integer.MAX_VALUE) {
			throw new ArithmeticException("Cannot downcast " + val);
		}
		return (int) val;
	}

	/**
	 * safely casts an integer to a short value. It guarantees that the short
	 * returned has the same arithmetic value of the integer.
	 * 
	 * @param val
	 *            the int value to be casted.
	 * @return the val converted to a short.
	 */
	public static short intToShortSafe(int val) {
		if (val < Short.MIN_VALUE || val > Short.MAX_VALUE) {
			throw new ArithmeticException("Cannot downcast " + val);
		}
		return (short) val;
	}

	/**
	 * safely converts a long to a short value, checking that the cast does not
	 * throw away information.
	 * 
	 * @param val
	 *            the long value to be casted.
	 * @return the val converted to a short.
	 */
	public static short longToShortSafe(long val) {
		if (val < Short.MIN_VALUE || val > Short.MAX_VALUE) {
			throw new ArithmeticException("Cannot downcast " + val);
		}
		return (short) val;
	}

	/**
	 * returns true if two doubles are almost equals.
	 * 
	 * @param aD1
	 * @param aD2
	 * @return
	 */
	public static boolean almost_equal(double aD1, double aD2) {

		if (Double.isNaN(aD1)) {
			return Double.isNaN(aD2);
		}

		final double EPSILON = 1E-7;
		if (Math.abs(aD1) <= EPSILON) {
			return Math.abs(aD2) <= EPSILON;
		}
		// if (aD2 == 0){
		// return Math.abs(aD1) <= EPSILON;
		// }

		return (Math.abs(aD1 - aD2) / Math.max(Math.abs(aD1), Math.abs(aD2))) <= EPSILON;
	}

	public static boolean almost_equalEps(double aD1, double aD2, double epsilon) {

		if (Double.isNaN(aD1)) {
			return Double.isNaN(aD2);
		}

		// final double EPSILON = 1E-7;
		if (Math.abs(aD1) <= epsilon) {
			return Math.abs(aD2) <= epsilon;
		}
		// if (aD2 == 0){
		// return Math.abs(aD1) <= EPSILON;
		// }

		return (Math.abs(aD1 - aD2) / Math.max(Math.abs(aD1), Math.abs(aD2))) <= epsilon;
	}

	/**
	 * This function should simply return a price normalized used a defined
	 * tickSize The price is normalized using the minimum distance tick. For
	 * example a price of 833.23423 with a tick size of 0.25 becomes 833.25.
	 * 
	 * In the future it will return a simple long.
	 * 
	 * @param stepInt
	 *            (for example 25)
	 * @param aStepSizeScale
	 *            (for example 100) it means a tick size of 0.25
	 * @return the price normalized (for example 34.25);
	 */
	public static double normalizeValue(double aValue, int stepInt,
			long aStepSizeScale) {
		// first of all I should have the tick size

		double value = aValue * aStepSizeScale;
		long np = Math.round(value);
		long rem = np % stepInt;
		if (rem >= ((stepInt) / 2.0)) {
			// round up
			np += (stepInt - rem);
		} else {
			// round down
			np -= rem;
		}

		// the price is not scaled back
		return (double) np / (double) aStepSizeScale;
	}

	public static double normalizeUsingStep(double e, int aStepSizeInt,
			long aStepSizeScale) {
		if (aStepSizeInt == 0 && e == 0) {
			return e;
		}
		return MathUtils.normalizeValue(e, aStepSizeInt, aStepSizeScale);
	}

	public static double normalizeUpUsingStep(double e, int aStepSizeInt, long l) {
		if (aStepSizeInt == 0 && e == 0) {
			return e;
		}
		return MathUtils.normalizeValueUp(e, aStepSizeInt, l);
	}

	public static double normalizeDownUsingStep(double e, int aStepSizeInt,
			int aStepSizeScale) {
		if (aStepSizeInt == 0 && e == 0) {
			return e;
		}
		return MathUtils.normalizeValueDown(e, aStepSizeInt, aStepSizeScale);
	}

	/**
	 * This function returns the highest price normalized which is under the
	 * price. For example: normalizePriceDown(833,23, 0.25) becomes 833.00
	 */
	public static double normalizeValueDown(double aPrice, int stepInt,
			int scale) {
		double price = aPrice * scale;
		long np = (long) Math.floor(price);
		long rem = np % stepInt;
		np -= rem;
		return (double) np / (double) scale;
	}

	/**
	 * This function normalize the price up. normalizePriceUp(833.02, 0.25)
	 * becomes 833.25
	 */
	public static double normalizeValueUp(double aPrice, int stepInt, long l) {
		double price = aPrice * l;
		long np = (long) Math.ceil(price);
		long rem = np % stepInt;
		if (rem != 0) {
			np += (stepInt - rem);
		}
		return (double) np / (double) l;
	}

	public static double modulus(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}

	public static int getStepDiffAbs(double a, double b, int aIntegerValue,
			long aScale) {
		int aa = (int) (a * aScale);
		int bb = (int) (b * aScale);
		return Math.abs(aa - bb) / aIntegerValue;
	}

	public static int getClosestStepDiffAbs(double a, double b, int aInteger,
			int aScale) {
		double abs = Math.abs(a - b) * aScale;
		int res = (int) (Math.round(abs) / aInteger);
		return res;
	}

	public static boolean isRoundStepDiffAbs(double a, double b,
			int aStepInteger, long l) {
		int aa = (int) (a * l);
		if (aa - a * l != 0)
			return false;
		int bb = (int) (b * l);
		if (bb - b * l != 0)
			return false;
		return Math.abs(aa - bb) % aStepInteger == 0;
	}

	static DecimalFormat myFormatter = new DecimalFormat("###,###.###");

	public static String getPriceFormat(double value) {
		String output = myFormatter.format(value);
		return output;
	}

	/**
	 * performs a safe integer division between the price and the tick.
	 * 
	 * <p>
	 * Safe means that it ensures that the tick is contained an integer number
	 * of times in the price, that is the division is without modulus.
	 * 
	 * @param aPrice
	 *            the price which you want to convert in ticks
	 * @param aTickSize
	 *            the tick size
	 * @return how many ticks are stored in this price
	 * 
	 * @throws DFSException
	 *             if the price is not an integer multiple of tick size
	 */
	public static long safeIntDivision(long aPrice, int aTickSize)
			throws DFSException {
		if (aPrice % aTickSize != 0) {
			throw new DFSException("Price " + aPrice
					+ " is not compatbile with tick size " + aTickSize);
		}
		return aPrice / aTickSize;
	}

}
