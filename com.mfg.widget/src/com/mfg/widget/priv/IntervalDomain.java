
package com.mfg.widget.priv;

import java.util.ArrayList;

/**
 * domain that represents an interval of numbers. the extreme values {@code minValue} and {@code maxValue} can be included or not.
 * <p>
 * Elements in this domain are equally spaced by a {@code stepSize}
 * </p>
 * 
 * @author gardero
 * 
 * @param <T>
 *            a number class, for example {@code Integer}, {@code Double},...
 */
public abstract class IntervalDomain<T extends Number> extends SetDomain<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected T stepSize;
	protected T minValue;
	protected T maxValue;
	protected T universalMinValue;
	protected T universalMaxValue;
	protected boolean includingMin;
	protected boolean includingMax;


	public IntervalDomain() {
		super();
	}


	/**
	 * gets a list of possible in the interval.
	 */
	// @JSON(include = false)
	@Override
	public ArrayList<T> getPossibleValues() {
		return possibleValues;
	}


	/**
	 * fills the list of possible values in the interval.
	 */
	protected abstract void enumerateDomain();


	/**
	 * gets the lower value of the interval
	 * 
	 * @return the minimum value in the interval.
	 */
	// @JSON(include = true, index = 10)
	public T getMinValue() {
		return minValue;
	}


	/**
	 * gets the upper value of the interval
	 * 
	 * @return the maximum value in the interval.
	 */
	// @JSON(include = true, index = 20)
	public T getMaxValue() {
		return maxValue;
	}


	/**
	 * gets if the lower bound of the interval is included in this interval.
	 * 
	 * @return true of it is included.
	 */
	// @JSON(include = true, index = 30)
	public boolean isIncludingMin() {
		return includingMin;
	}


	/**
	 * gets if the upper bound of the interval is included in this interval.
	 * 
	 * @return true of it is included.
	 */
	// @JSON(include = true, index = 40)
	public boolean isIncludingMax() {
		return includingMax;
	}


	/**
	 * gets the step size of the elements in the interval.
	 * 
	 * @return the step size value.
	 */
	// @JSON(include = true, index = 0)
	public T getStepSize() {
		return stepSize;
	}


	/**
	 * sets the step size in the interval.
	 * <p>
	 * Notice that all elements in the interval, including the {@code minValue} and {@code maxValue} should be integer multiples of this
	 * {@code stepSize}.
	 * 
	 * @param AstepSize
	 */
	public void setStepSize(T AstepSize) {
		if (!this.stepSize.equals(AstepSize)) {
			this.stepSize = AstepSize;
			enumerateDomain();
		}
	}


	/**
	 * sets if the lower bound is included in this interval or not.
	 * 
	 * @param AIncludingMin
	 *            {@code true} iff the lower bound is in this interval.
	 */
	public void setIncludingMin(boolean AIncludingMin) {
		this.includingMin = AIncludingMin;
	}


	/**
	 * sets if the upper bound is included in this interval or not.
	 * 
	 * @param aIncludingMax
	 *            {@code true} iff the upper bound is in this interval.
	 */
	public void setIncludingMax(boolean aIncludingMax) {
		this.includingMax = aIncludingMax;
	}


	/**
	 * sets the lower value for this interval.
	 * 
	 * @param aMinValue
	 *            the minimum value.
	 */
	public void setMinValue(T aMinValue) {
		setMinMaxValue(aMinValue, this.maxValue);
	}


	/**
	 * sets the lower value for this interval.
	 * 
	 * @param aMinValue
	 *            the minimum value.
	 * @param setUniversal
	 *            true if this method will also reset the universal minimum value
	 */
	public void setMinValue(T aMinValue, boolean setUniversal) {
		setMinMaxValue(aMinValue, this.maxValue);
		if (setUniversal)
			setUniversalMinValue(aMinValue);
	}


	/**
	 * sets the upper value for this interval.
	 * 
	 * @param aMaxValue
	 *            the maximum value.
	 */
	public void setMaxValue(T aMaxValue) {
		setMinMaxValue(this.minValue, aMaxValue);
	}


	/**
	 * sets the upper value for this interval.
	 * 
	 * @param aMaxValue
	 *            the maximum value.
	 * @param setUniversal
	 *            true if this method will also reset the universal maximum value
	 */
	public void setMaxValue(T aMaxValue, boolean setUniversal) {
		setMinMaxValue(this.minValue, aMaxValue);
		if (setUniversal)
			setUniversalMinValue(aMaxValue);
	}


	/**
	 * sets both lower and upper bounds for this interval.
	 * 
	 * @param aMminValue
	 *            lower bound.
	 * @param aMaxValue
	 *            upper bound.
	 */
	public void setMinMaxValue(T aMminValue, T aMaxValue) {
		this.minValue = aMminValue;
		this.maxValue = aMaxValue;
		enumerateDomain();
	}


	/**
	 * gets the universal lower bound.
	 * <p>
	 * this value is used to memorize a minimum value reference.
	 * 
	 * @return the universal lower bound.
	 */
	// @JSON(include = true, index = 60)
	public T getUniversalMinValue() {
		return universalMinValue;
	}


	/**
	 * sets the universal lower bound.
	 * 
	 * @param aUniversalMinValue
	 *            the new value for the universal lower bound.
	 */
	public void setUniversalMinValue(T aUniversalMinValue) {
		universalMinValue = aUniversalMinValue;
	}


	/**
	 * gets the universal upper bound.
	 * <p>
	 * this value is used to memorize a maximum value reference.
	 * 
	 * @return the universal upper bound.
	 */
	// @JSON(include = true, index = 70)
	public T getUniversalMaxValue() {
		return universalMaxValue;
	}


	/**
	 * sets the universal upper bound.
	 * 
	 * @param aUniversalMaxValue
	 *            the new value for the universal upper bound.
	 */
	public void setUniversalMaxValue(T aUniversalMaxValue) {
		universalMaxValue = aUniversalMaxValue;
	}

}
