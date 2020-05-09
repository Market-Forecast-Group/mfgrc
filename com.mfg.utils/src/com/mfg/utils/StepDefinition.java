package com.mfg.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * represents a definition of a double step with a (scale, integer)
 * representation used to do some calculations and avoid numerical errors.
 * <p>
 * The represented number is x = integer/scale, where integer is an integer
 * number and scale is equal to 10<sup>k</sup>
 * 
 * @author gardero
 * 
 */
public class StepDefinition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double stepDouble;
	private int stepScale;
	private int stepInteger;

	/**
	 * Used only by serialization
	 */
	public StepDefinition() {
	}

	public StepDefinition(double aStepDouble) {
		super();
		stepDouble = aStepDouble;
		importDouble(aStepDouble);
	}

	private void importDouble(double aStepDouble) {
		BigDecimal bd = new BigDecimal("" + stepDouble);
		stepScale = bd.scale();
		stepInteger = (int) (aStepDouble * getStep10Scale());
	}

	public StepDefinition(int aStepScale, int aStepInteger) {
		super();
		stepScale = aStepScale;
		stepInteger = aStepInteger;
		stepDouble = roundLong(aStepInteger);
	}

	public double getStepDouble() {
		return stepDouble;
	}

	public void setStepDouble(double aStepDouble) {
		stepDouble = aStepDouble;
		importDouble(aStepDouble);
	}

	public int getStepScale() {
		return stepScale;
	}

	public void setStepScale(int aStepScale) {
		stepScale = aStepScale;
	}

	public int getStepInteger() {
		return stepInteger;
	}

	public long getStep10Scale() {
		return (long) Math.pow(10, getStepScale());
	}

	public void setStepInteger(int aStepInteger) {
		stepInteger = aStepInteger;
	}

	public Double[] getElements(double aLower, double aLast) {
		return getElements(aLower, aLast, true);
	}

	@SuppressWarnings("boxing")
	public Double[] getElements(double aLower, double aLast,
			boolean includelower) {
		ArrayList<Double> res = new ArrayList<>();
		double e = round(aLower);
		if (!includelower)
			e = round(e + stepDouble);
		for (;; e = round(e + stepDouble)) {
			res.add(e);
			if (e >= aLast)
				break;
		}
		return res.toArray(new Double[] {});
	}

	public double getTimes(int ticks) {
		return roundLong(stepInteger * ticks);
	}

	public double round(double value) {
		return roundMore(value, 0);
	}

	public double roundUp(double value) {
		return roundUpMore(value, 0);
	}

	public double roundMore(double value, int scale) {
		return MathUtils.normalizeUsingStep(value, getStepInteger(),
				(long) Math.pow(10, getStepScale() + scale));
	}

	public double roundUpMore(double value, int scale) {
		return MathUtils.normalizeUpUsingStep(value, getStepInteger(),
				(long) Math.pow(10, getStepScale() + scale));
	}

	@SuppressWarnings("boxing")
	public double roundLong(long longValue) {
		return round(new Double(longValue) / new Double(getStep10Scale()));
	}

	public boolean isRoundMultiple(double t) {
		return MathUtils.isRoundStepDiffAbs(t, 0, getStepInteger(),
				getStep10Scale());
	}

	@SuppressWarnings("boxing")
	public Double[] getNElements(double aLower, int n, boolean includelower) {
		ArrayList<Double> res = new ArrayList<>();
		double e = round(aLower);
		if (!includelower)
			e = round(e + stepDouble);
		for (int i = 0; i < n; i++, e = round(e + stepDouble)) {
			res.add(e);
		}
		return res.toArray(new Double[] {});
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(stepDouble);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + stepInteger;
		result = prime * result + stepScale;
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
		StepDefinition other = (StepDefinition) obj;
		if (Double.doubleToLongBits(stepDouble) != Double
				.doubleToLongBits(other.stepDouble))
			return false;
		if (stepInteger != other.stepInteger)
			return false;
		if (stepScale != other.stepScale)
			return false;
		return true;
	}

	public int getTicksOn(double value) {
		return (int) ((Math.signum(value)) * MathUtils.getStepDiffAbs(0, value,
				stepInteger, getStep10Scale()));
	}

	@Override
	public String toString() {
		return "StepDefinition [stepDouble=" + stepDouble + ", stepScale="
				+ stepScale + ", stepInteger=" + stepInteger + "]";
	}

}
