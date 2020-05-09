package com.mfg.utils;

import java.io.Serializable;

/**
 * A circular integer is a positive integer which can have only a range from
 * zero to some maximum.
 * 
 * @author Sergio
 * 
 */
public class CircularInteger implements Cloneable, Serializable {

	@Override
	protected CircularInteger clone() throws CloneNotSupportedException {
		// A simple bit copy is sufficient
		return (CircularInteger) super.clone();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7300411486668615543L;

	/**
	 * Builds a Circular integer. The start value is i and the maximum is
	 * 
	 * @param aValue
	 * @param aMaxValue
	 *            The maximum value
	 * 
	 * @throws IllegalArgumentException
	 *             if value < 0 or aMaxValue < 0 or value >= aMaxValue
	 */
	public CircularInteger(int aValue, int aMaxValue) {
		if (aValue < 0 || aMaxValue < 0 || aValue >= aMaxValue) {
			throw new IllegalArgumentException();
		}

		fInt = aValue;
	}

	/**
	 * This is the value of the integer
	 */
	private int fInt;

	/**
	 * returns the current value of this circular integer
	 * 
	 * @return
	 */
	public int get() {
		return fInt;
	}

	/**
	 * adds one to the circular integer, returning to zero if the integer
	 * touches the maxLength parameter
	 * 
	 * @param maxLength
	 *            the maximum length
	 */
	public void plusPlus(int maxLength) {
		assert (fInt < maxLength && maxLength > 0);
		if (++fInt == maxLength) {
			fInt = 0;
		}
	}

	/**
	 * Subtracts from the current integer the number aNumber. If the result is
	 * negative then it is returned positive adding the maximum length.
	 * 
	 * This number is not modified.
	 * 
	 * @param aNumber
	 * @param maxLength
	 * 
	 * @return this number - aNumber, added with maxLength if the result is
	 *         negative.
	 */
	public int subtract(int aNumber, int maxLength) {
		assert (aNumber >= 0 && aNumber < maxLength && maxLength > fInt);
		int res = fInt - aNumber;
		if (res < 0) {
			res += maxLength;
		}
		return res;
	}

	/**
	 * adds a number to this circular integer, wrapping around if the result
	 * will be over max length
	 * 
	 * This number is not modified.
	 * 
	 * @param i
	 * @param length
	 * @return this number plus i, wrapping around zero if the result is over
	 *         max
	 */
	public int add(int aNumber, int maxLength) {
		assert (aNumber >= 0 && aNumber < maxLength && maxLength > fInt) : " this "
				+ fInt + " anum " + aNumber + " max " + maxLength;
		int res = fInt + aNumber;
		if (res >= maxLength) {
			res -= maxLength;
		}
		return res;
	}

}
