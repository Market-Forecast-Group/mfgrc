/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package org.mfg.opengl.chart;

public class PlotRange {
	public double lower;
	public double upper;

	@SuppressWarnings("hiding")
	public PlotRange(final double lower, final double upper) {
		super();
		this.lower = lower;
		this.upper = upper;
	}

	public PlotRange(PlotRange range) {
		super();
		this.lower = range.lower;
		this.upper = range.upper;
	}

	/**
	 * @param time
	 * @return
	 */
	public PlotRange getMovedTo(double value) {
		double half = (upper - lower) / 2;
		return new PlotRange(value - half, value + half);
	}

	public boolean contains(final double value) {
		return value >= lower && value <= upper;
	}

	public double getLength() {
		return upper - lower;
	}

	public double getMiddle() {
		return lower + getLength() / 2;
	}

	public int screenValue(final double plotValue, final int screenLen) {
		return screenWidth(plotValue - lower, screenLen);
	}

	public int screenWidth(final double plotValue, final int screenLen) {
		return (int) (plotValue / (upper - lower) * screenLen);
	}

	public double plotWidth(final int screenValue, final int screenLen) {
		return (double) screenValue / (double) screenLen * (upper - lower);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lower);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(upper);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlotRange other = (PlotRange) obj;
		if (Double.doubleToLongBits(lower) != Double
				.doubleToLongBits(other.lower))
			return false;
		if (Double.doubleToLongBits(upper) != Double
				.doubleToLongBits(other.upper))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlotRange [lower=" + lower + ", upper=" + upper + "]";
	}

	public void add(PlotRange range) {
		lower = range.lower < lower ? range.lower : lower;
		upper = range.upper > upper ? range.upper : upper;
	}
}
