package com.mfg.widget.priv;

import java.math.BigDecimal;

import com.mfg.utils.MathUtils;

/**
 * a discrete number interval, based on {@code Double} values.
 * <p>
 * For numerical and efficiency reasons we internally represent the stepSize
 * with 2 values:
 * <ul>
 * <li>the integer step value {@code _StepSizeInt}. In case our step size is
 * 0.25 the integer step value is 25.
 * <li>the scale step value {@code _StepSizeScale}. In case our step size is
 * 0.25 the step value scale is 100, what it takes to transform the step size
 * into an integer value through multiplications.
 * </ul>
 * they are used to round values to integer multiples the step size.
 * 
 * @author gardero
 * 
 */
public class DiscreteIntervalDomain extends IntervalDomain<Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static double DEFAULT_STEP = 0.1;
	public static int DEFAULT_STEP_int = 1;
	public static int DEFAULT_STEP_scale = 10;

	@SuppressWarnings("boxing")
	@Override
	public DiscreteIntervalDomain clone() {
		DiscreteIntervalDomain res = new DiscreteIntervalDomain(stepSize,
				minValue, maxValue, includingMin, includingMax);
		res.r = r;
		// assert (r!=null): "randomizer";
		res.universalMaxValue = universalMaxValue;
		res.universalMinValue = universalMinValue;
		return res;
	}

	public DiscreteIntervalDomain() {
		this(DEFAULT_STEP, 0, 1, true, true);
	}

	public DiscreteIntervalDomain(double aMinValue, double aMaxValue) {
		this(DEFAULT_STEP, aMinValue, aMaxValue, true, true);
	}

	public DiscreteIntervalDomain(double aMinValue, double aMaxValue,
			boolean aIncludingMin, boolean aIncludingMax) {
		this(DEFAULT_STEP, aMinValue, aMaxValue, aIncludingMin, aIncludingMax);
	}

	@SuppressWarnings("boxing")
	public DiscreteIntervalDomain(double aStepSize, double aMinValue,
			double aMaxValue, boolean aIncludingMin, boolean aIincludingMax) {
		super();
		this.stepSize = aStepSize;
		BigDecimal d = new BigDecimal("" + aStepSize);
		_StepSizeInt = d.unscaledValue().intValue();
		_StepSizeScale = (int) Math.pow(10, d.scale());
		this.minValue = this.universalMinValue = MathUtils
				.normalizeDownUsingStep(aMinValue, _StepSizeInt, _StepSizeScale);
		this.maxValue = this.universalMaxValue = MathUtils
				.normalizeUpUsingStep(aMaxValue, _StepSizeInt, _StepSizeScale);
		this.includingMax = aIincludingMax;
		this.includingMin = aIncludingMin;
		enumerateDomain();
	}

	@SuppressWarnings("boxing")
	@Override
	protected void enumerateDomain() {
		clear();
		double v = minValue;
		if (!includingMin)
			v += stepSize;
		for (; v < maxValue || (includingMax && v == maxValue); v += stepSize) {
			v = MathUtils.normalizeUsingStep(v, _StepSizeInt, _StepSizeScale);
			possibleValues.add(v);
		}
		resetRandomizer(r);
	}

	// public static double normalizeUsingStep(double value, int stepInt, int
	// stepScale) {
	// if (stepInt == 0 && stepScale == 0)
	// return value;
	// value *= stepScale;
	// long np = Math.round(value);
	// long rem = np % stepInt;
	// if (rem >= ((double) (stepInt) / 2.0)) {
	// // round up
	// np += (stepInt - rem);
	// } else {
	// // round down
	// np -= rem;
	// }
	// // the price is not scaled back
	// return (double) np;// /(double)scale;
	// }

	@SuppressWarnings("boxing")
	@Override
	public void add(Double cPar) {
		double c = cPar;
		c = MathUtils.normalizeUsingStep(c, _StepSizeInt, _StepSizeScale);
		super.add(c);
	}

	@SuppressWarnings("boxing")
	@Override
	public void remove(Double cPar) {
		double c = cPar;
		c = MathUtils.normalizeUsingStep(c, _StepSizeInt, _StepSizeScale);
		super.remove(c);
	}

	@Override
	public String toString() {
		return (includingMin ? "[" : "(") + minValue + ".." + maxValue
				+ (includingMax ? "]" : ")");
	}

	private int _StepSizeInt;
	private int _StepSizeScale;

	@Override
	public void setStepSize(Double aStepSize) {
		if (this.stepSize == null || !this.stepSize.equals(aStepSize)) {
			BigDecimal d = new BigDecimal("" + aStepSize);
			_StepSizeInt = d.unscaledValue().intValue();
			_StepSizeScale = (int) Math.pow(10, d.scale());
			setStepSize(aStepSize, _StepSizeInt, _StepSizeScale);
		}
	}

	@SuppressWarnings("boxing")
	public void setStepSize(Double aStepSize, int aStepSizeInt,
			int aStepSizeScale) {
		if (this.stepSize == null || !this.stepSize.equals(aStepSize)) {
			this.stepSize = aStepSize;
			_StepSizeInt = aStepSizeInt;
			_StepSizeScale = aStepSizeScale;
			setMinMaxValue(minValue, maxValue);
			universalMinValue = MathUtils.normalizeDownUsingStep(
					universalMinValue, _StepSizeInt, _StepSizeScale);
			universalMaxValue = MathUtils.normalizeUpUsingStep(
					universalMaxValue, _StepSizeInt, _StepSizeScale);
		}
	}

	@SuppressWarnings("boxing")
	@Override
	public void setMinMaxValue(Double aMinValue, Double aMaxValue) {
		this.minValue = MathUtils.normalizeDownUsingStep(aMinValue,
				_StepSizeInt, _StepSizeScale);
		this.maxValue = MathUtils.normalizeUpUsingStep(aMaxValue, _StepSizeInt,
				_StepSizeScale);
		enumerateDomain();
	}

}
