package com.mfg.chart.model;

import com.mfg.utils.Utils;

public class ProbabilityPricesHelper {

	private double[] probabilities;
	private long[] prices;
	private int cursor;
	private int n;

	public int locatePrice(double probability) {
		cursor = binarySearch(probability, 0, cursor, n);
		return cursor;
	}

	private int binarySearch(double probability, int start, int mid, int end) {
		if (Math.abs(start - end) <= 1) {
			if (Math.abs(probability - probabilities[start]) < Math
					.abs(probability - probabilities[end]))
				return start;
			return end;
		}
		if (probability <= probabilities[mid]) {
			return binarySearch(probability, start, (start + mid) / 2, mid);
		}
		return binarySearch(probability, mid, (end + mid) / 2, end);
	}

	public long getLocatedPrice() {
		return prices[cursor];
	}

	public ProbabilityPricesHelper(double[] aProbabilities, long[] aPrices) {
		super();
		probabilities = aProbabilities;
		prices = aPrices;
		n = prices.length;
	}

	public double[] getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(double[] aProbabilities) {
		probabilities = aProbabilities;
	}

	public long[] getPrices() {
		return prices;
	}

	public void setPrices(long[] aPrices) {
		prices = aPrices;
	}

	public static void main(String[] args) {
		ProbabilityPricesHelper p = new ProbabilityPricesHelper(new double[] {
				0.2, 0.3, 0.6, 0.7, 0.9, 1 }, new long[] { 2, 3, 6, 7, 9, 10 });
		for (double i = 0; i <= 1; i += 0.1) {
			p.locatePrice(i);
			Utils.debug_var(12345,
					"prob=" + i + " with price " + p.getLocatedPrice());
		}
	}

}
