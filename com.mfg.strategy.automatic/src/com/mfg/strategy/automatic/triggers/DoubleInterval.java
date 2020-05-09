
package com.mfg.strategy.automatic.triggers;

import java.awt.Color;
import java.io.Serializable;

import com.mfg.utils.ui.HtmlUtils;
import com.mfg.utils.ui.HtmlUtils.IHtmlStringProvider;

public class DoubleInterval implements Cloneable, Serializable, IHtmlStringProvider {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected double fLowerBound = 0, fUpperBound = 1, fPositiveInfinite = 1, fNegativeInfinite = 0;
	protected boolean fIncludingLower = true, fIncludingUpper = false, fEnabledPositiveInfinite = false, fEnabledNegativeInfinite = false;


	public DoubleInterval(double aLowerBound, double aUpperBound, boolean aIncludingLower, boolean aIncludingUpper) {
		super();
		fLowerBound = aLowerBound;
		fUpperBound = aUpperBound;
		fIncludingLower = aIncludingLower;
		fIncludingUpper = aIncludingUpper;
	}


	public DoubleInterval(double aLowerBound, double aUpperBound) {
		super();
		fLowerBound = aLowerBound;
		fUpperBound = aUpperBound;
	}


	public DoubleInterval() {
		this(0, 1);
	}


	// @Param
	// @JSON(index = 10)
	public double getLowerBound() {
		return fLowerBound;
	}


	public void setLowerBound(double aLowerBound) {
		fLowerBound = aLowerBound;
	}


	// @Param
	// @JSON(index = 10)
	public double getUpperBound() {
		return fUpperBound;
	}


	public void setUpperBound(double aUpperBound) {
		fUpperBound = aUpperBound;
	}


	// @Param
	// @JSON(index = 10)
	public double getPositiveInfinite() {
		return fPositiveInfinite;
	}


	public void setPositiveInfinite(double aFPositiveInfinite) {
		this.fPositiveInfinite = aFPositiveInfinite;
	}


	// @Param
	// @JSON(index = 10)
	public double getNegativeInfinite() {
		return fNegativeInfinite;
	}


	public void setNegativeInfinite(double aNegativeInfinite) {
		fNegativeInfinite = aNegativeInfinite;
	}


	// @Param
	// @JSON(index = 10)
	public boolean isIncludingLower() {
		return fIncludingLower;
	}


	public void setIncludingLower(boolean aIncludingLower) {
		fIncludingLower = aIncludingLower;
	}


	// @Param
	// @JSON(index = 10)
	public boolean isIncludingUpper() {
		return fIncludingUpper;
	}


	public void setIncludingUpper(boolean aIncludingUpper) {
		fIncludingUpper = aIncludingUpper;
	}


	// @Param
	// @JSON(index = 10)
	public boolean isEnabledPositiveInfinite() {
		return fEnabledPositiveInfinite;
	}


	public void setEnabledPositiveInfinite(boolean aEnabledPositiveInfinite) {
		fEnabledPositiveInfinite = aEnabledPositiveInfinite;
	}


	// @Param
	// @JSON(index = 10)
	public boolean isEnabledNegativeInfinite() {
		return fEnabledNegativeInfinite;
	}


	public void setEnabledNegativeInfinite(boolean aEnabledNegativeInfinite) {
		fEnabledNegativeInfinite = aEnabledNegativeInfinite;
	}


	public boolean contains(double value) {
		return (value == fLowerBound && fIncludingLower) || (value == fUpperBound && fIncludingUpper) || ((value > fLowerBound || (fEnabledNegativeInfinite && fLowerBound <= fNegativeInfinite)) && (value < fUpperBound || (fEnabledPositiveInfinite && fUpperBound >= fPositiveInfinite)));
	}


	@Override
	public DoubleInterval clone() {
		try {
			return (DoubleInterval) super.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
		}
		return null;
	}


	@Override
	public String toString() {
		return getHtmlBody(HtmlUtils.Plain);

	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		String res = "";
		if (isIncludingLower())
			res += "[";
		else
			res += "(";
		if (fEnabledNegativeInfinite && fLowerBound <= fNegativeInfinite)
			res += (aUtil.color("-Inf", Color.blue));
		else
			res += (aUtil.color("" + fLowerBound, Color.blue) + ", ");
		if (fEnabledPositiveInfinite && fUpperBound >= fPositiveInfinite)
			res += (aUtil.color("+Inf", Color.RED));
		else
			res += (aUtil.color("" + fUpperBound, Color.RED));
		if (isIncludingUpper())
			res += "]";
		else
			res += ")";
		return res;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (fEnabledNegativeInfinite ? 1231 : 1237);
		result = prime * result + (fEnabledPositiveInfinite ? 1231 : 1237);
		result = prime * result + (fIncludingLower ? 1231 : 1237);
		result = prime * result + (fIncludingUpper ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(fLowerBound);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fNegativeInfinite);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fPositiveInfinite);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fUpperBound);
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
		DoubleInterval other = (DoubleInterval) obj;
		if (fEnabledNegativeInfinite != other.fEnabledNegativeInfinite)
			return false;
		if (fEnabledPositiveInfinite != other.fEnabledPositiveInfinite)
			return false;
		if (fIncludingLower != other.fIncludingLower)
			return false;
		if (fIncludingUpper != other.fIncludingUpper)
			return false;
		if (Double.doubleToLongBits(fLowerBound) != Double.doubleToLongBits(other.fLowerBound))
			return false;
		if (Double.doubleToLongBits(fNegativeInfinite) != Double.doubleToLongBits(other.fNegativeInfinite))
			return false;
		if (Double.doubleToLongBits(fPositiveInfinite) != Double.doubleToLongBits(other.fPositiveInfinite))
			return false;
		if (Double.doubleToLongBits(fUpperBound) != Double.doubleToLongBits(other.fUpperBound))
			return false;
		return true;
	}

}
