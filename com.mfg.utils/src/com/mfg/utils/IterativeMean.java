package com.mfg.utils;

import java.io.Serializable;

/**
 * A class to do an iterative mean; an iterative mean is computed adding the
 * values one by one.
 * 
 * <p>
 * The class is a companion of the MovingAverage, but the moving average has a
 * fixed buffer, instead the iterative mean has not (you have to provide the
 * buffer yourself).
 * 
 * @author Sergio
 * 
 */
public class IterativeMean implements Cloneable, Serializable {

	@Override
	public IterativeMean clone() {
		IterativeMean cloned;
		try {
			cloned = (IterativeMean) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4042678269315768385L;
	private double fSum = 0;
	private int fN = 0;

	public void add(double val) {
		fSum += val;
		fN++;
	}

	public double getAvg() {
		// debug_var(209101, "sum ", fSum, " n ", fN);
		if (fN < 2) {
			return Double.NaN; // not a mean!
		}
		return fSum / fN;
	}

	public void remove(double val) {
		fSum -= val;
		fN--;
	}

	public void clear() {
		fSum = 0;
		fN = 0;
	}

	public int getN() {
		return fN;
	}

}
