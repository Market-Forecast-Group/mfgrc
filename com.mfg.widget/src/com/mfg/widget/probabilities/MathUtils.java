package com.mfg.widget.probabilities;

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

	/**
	 * This function should simply return a price normalized used a defined
	 * tickSize The price is normalized using the minimum distance tick. For
	 * example a price of 833.23423 with a tick size of 0.25 becomes 833.25.
	 * 
	 * In the future it will return a simple long.
	 * 
	 * @param stepInt
	 *            (for example 25)
	 * @param scale
	 *            (for example 100) it means a tick size of 0.25
	 * @return the price normalized (for example 34.25);
	 */
	public static double normalizeValue(double valuePar, long stepInt,
			long scale) {
		// first of all I should have the tick size
		double value = valuePar;

		value *= scale;
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
		return (double) np / (double) scale;
	}

	public static double normalizeUsingStep(double e, long aStepSizeInt,
			long aStepSizeScale) {
		if (aStepSizeInt == 0 && e == 0) {
			return e;
		}
		return MathUtils.normalizeValue(e, aStepSizeInt, aStepSizeScale);
	}

	public static double normalizeUpUsingStep(double e, int aStepSizeInt,
			int aStepSizeScale) {
		if (aStepSizeInt == 0 && e == 0) {
			return e;
		}
		return MathUtils.normalizeValueUp(e, aStepSizeInt, aStepSizeScale);
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
	public static double normalizeValueDown(double pricePar, int stepInt,
			int scale) {
		double price = pricePar;
		price *= scale;
		long np = (long) Math.floor(price);
		long rem = np % stepInt;
		np -= rem;
		return (double) np / (double) scale;
	}

	/**
	 * This function normalize the price up. normalizePriceUp(833.02, 0.25)
	 * becomes 833.25
	 */
	public static double normalizeValueUp(double pricePar, int stepInt,
			int scale) {
		double price = pricePar;
		price *= scale;
		long np = (long) Math.ceil(price);
		long rem = np % stepInt;
		if (rem != 0) {
			np += (stepInt - rem);
		}
		return (double) np / (double) scale;
	}

	public static double modulus(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}

	// public static int getStepDiffAbs(double price1, double price2, double
	// step) {
	// return (int) Math.abs(Math.round((price1 - price2) / step));
	// }

	// public static int getStepDiff(double price1, double price2, double step)
	// {
	// return (int) Math.round((price1 - price2) / step);
	// }

	public static int getStepDiffAbs(double a, double b, int aInteger, long l) {
		int aa = (int) (a * l);
		int bb = (int) (b * l);
		return Math.abs(aa - bb) / aInteger;
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

}
