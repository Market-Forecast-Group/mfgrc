package com.mfg.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This is a simple class which defines some utility functions relative to
 * prices.
 */
public class PriceUtils {

	/**
	 * This class is not instantiable... just a container for helper functions.
	 */
	private PriceUtils() {
	}

	public static void main(String args[]) {
		String test = longToString(12345, 2);
		System.out.println("12345 2 is " + test);
		test = longToString(12, 3);
		System.out.println("12, 3 is " + test);
		
		long test1 = stringToLong("12.3", 4);
		System.out.println("12.3 scale 4 is " + test1);
		test1 = stringToLong("12.3", 2);
		System.out.println("12.3 scale 2  is " + test1);
		try{
			test1 = stringToLong("12.3", 0);
		} catch (ArithmeticException e){
			System.out.println("all ok. 12.3 needs rounding.");
		}
		
	}

	/**
	 * Scales back an integer price given a proper scale
	 * 
	 * @param aPrice
	 *            the unscaled price
	 * @param scale
	 *            the scale to be scaled
	 * 
	 * @return the string representation of this price, for example p=12345 s=2
	 *         return "123.45"
	 */
	public static String longToString(long aPrice, int scale) {
		BigDecimal bd = new BigDecimal(new BigInteger(
				new Long(aPrice).toString()), scale);
		return bd.toPlainString();
	}
	
	/**
	 * returns a price parsed with a predetermined scale.
	 * 
	 * @param aPrice
	 * @param scale
	 * 
	 * @return the long with trailing zeros as necessary. For example
	 * 8.9 with scale 2 is converted to 890.
	 * 
	 * @throws ArithmeticException if rounding is necessary, for example 12.3 with scale 0
	 * 
	 * @deprecated this does not take into consideration overflow.
	 */
	@Deprecated
	public static long stringToLong(String aPrice, int scale){
		BigDecimal bd = new BigDecimal(aPrice);
		bd = bd.setScale(scale);
		return bd.unscaledValue().longValue();
	}

	/**
	 * This function should simply return a price normalized used a defined
	 * tickSize The price is normalized using the minimum distance tick. For
	 * example a price of 833.23423 with a tick size of 0.25 becomes 83325.
	 * 
	 * The price is NOT scaled back. It returns a double without decimal
	 * fraction.
	 * 
	 * In the future it will return a simple long.
	 * 
	 * @param ticks
	 *            (for example 25)
	 * @param scale
	 *            (for example 100) it means a tick size of 0.25
	 * @return the price normalized (for example 34.25);
	 */
	public static double normalizePrice(double aPrice, int ticks, int scale) {
		// first of all I should have the tick size

		double price = aPrice;

		price *= scale;
		long np = Math.round(price);
		long rem = np % ticks;
		if (rem >= (ticks / 2.0)) {
			// round up
			np += (ticks - rem);
		} else {
			// round down
			np -= rem;
		}

		// the price is not scaled back
		return np;
	}

	public static double normalizeUsingStep(double e, int aStepSizeInt,
			int aStepSizeScale) {
		if (aStepSizeInt == 0 && e == 0) {
			return e;
		}
		return PriceUtils.normalizePrice(e, aStepSizeInt, aStepSizeScale);
	}

	public static double normalizeUpUsingStep(double e, int aStepSizeInt,
			int aStepSizeScale) {
		if (aStepSizeInt == 0 && e == 0) {
			return e;
		}
		return PriceUtils.normalizePriceUp(e, aStepSizeInt, aStepSizeScale);
	}

	public static double normalizeDownUsingStep(double e, int aStepSizeInt,
			int aStepSizeScale) {
		if (aStepSizeInt == 0 && e == 0) {
			return e;
		}
		return PriceUtils.normalizePriceDown(e, aStepSizeInt, aStepSizeScale);
	}

	/**
	 * This function does not take into account the scale, because it is always
	 * 1.
	 */
	public static double normalizePriceConservative(double price, int ticks) {
		// first of all I should have the tick size

		long np = -Math.round(-price);
		long rem = np % ticks;
		if (rem > (ticks / 2.0)) {
			// round up
			np += (ticks - rem);
		} else {
			// round down
			np -= rem;
		}

		return np;
	}

	/**
	 * This function returns the highest price normalized which is under the
	 * price. For example: normalizePriceDown(833,23, 0.25) becomes 83300
	 */
	public static double normalizePriceDown(double aPrice, int ticks, int scale) {
		double price = aPrice;
		price *= scale;
		long np = (long) Math.floor(price);
		long rem = np % ticks;
		np -= rem;
		return np;
	}

	/**
	 * This function normalize the price up. normalizePriceUp(833.02, 0.25)
	 * becomes 83325
	 */
	public static double normalizePriceUp(double aPrice, int ticks, int scale) {
		double price = aPrice;
		price *= scale;
		long np = (long) Math.ceil(price);
		long rem = np % ticks;
		if (rem != 0) {
			np += (ticks - rem);
		}
		return np;
	}

	public static double adjustwithtick(double v) {
		return PriceUtils.normalizePriceConservative(v, 25);
	}

	// public static double adjustwithtick(double v, int intValue/*, int
	// scale*/){
	// //The scale is always 1.
	// return PriceUtils.normalizePriceConservative(v, intValue, 1);
	// }

	public static int getDiffTicks(double price1, double price2) {
		return getStepDiffAbs(price1, price2, 25);
	}

	public static int getStepDiffAbs(double price1, double price2, double step) {
		return (int) Math.abs(Math.round((price1 - price2) / step));
	}

	public static int getStepDiff(double price1, double price2, double step) {
		return (int) Math.round((price1 - price2) / step);
	}

	/**
	 * This returns the threshold price, given the pivot price and the
	 * threshold. Valid only if the pivot has been computed with the logarithmic
	 * difference
	 * 
	 * @param price
	 *            the pivot price
	 * @param th
	 *            the threshold
	 * @param goingUp
	 *            true if we were going up.
	 * @return the last pivot
	 */
	public static double getLogPivot(double price, double th, boolean goingUp) {
		if (goingUp) {
			return Math.exp(th * -1.0) * price;
		}
		return Math.exp(th) * price;
	}

	/**
	 * This function returns the price which is at a specified (percentage)
	 * threshold from the given price.
	 */
	public static double getPercPrice(double price, double th, boolean goingUp) {

		double deltaPerc = price * th / 100.0;

		if (goingUp) {
			return price - deltaPerc;
		}
		return price + deltaPerc;
	}

	/**
	 * This returns the price which is at a specified (absolute) threshold from
	 * the given price.
	 */
	public static double getAbsolutePrice(double price, double th,
			boolean goingUp) {
		if (goingUp) {
			return price - th;
		}
		return price + th;
	}

	/**
	 * This returns the absolute difference in points between two prices.
	 * 
	 * @param p_old
	 *            a price
	 * @param p_new
	 *            another price
	 * @return the difference between the new and the old price
	 */
	public static double getAbsoluteDiffPoints(double p_old, double p_new) {
		return Math.abs(p_new - p_old);
	}

	/**
	 * This function returns the log difference between two prices.
	 */
	public static double get_log_difference(double p_new, double p_old) {
		return Math.abs(Math.log(p_new / p_old));
	}

	/**
	 * This function returns the % difference between two prices. The difference
	 * is computed with this formula
	 * 
	 * (abs(p_new - p_old) / p_old) * 100
	 * 
	 * @param p_old
	 *            the old price
	 * @param p_new
	 *            the new price
	 * @return the difference in %
	 */
	public static double getDiffPercentage(double p_old, double p_new) {

		return (((Math.abs(p_new - p_old)) / p_old) * 100.0);
	}

	public static double modulus(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}

}
