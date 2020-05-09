/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.triggers;

import com.mfg.interfaces.indicator.IIndicator;

/**
 * this class represents Trigger with a value that reaches an specific cut point to activate it.
 * 
 * @author gardero
 * 
 */
@SuppressWarnings("serial")
public class ValueLevelCutPointTrigger extends ValueScaleSpecificTrigger {

	protected double cutPointValue;
	private double fSign;


	public ValueLevelCutPointTrigger() {
		super();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#init(com. marketforecastgroup.priv.indicator.PivotsIndicatorWidget)
	 */
	@Override
	public void init(IIndicator aWidget) {
		super.init(aWidget);

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#isActive()
	 */
	@Override
	protected boolean internalIsActive() {
		if (!fWidget.isLevelInformationPresent(fWidgetScale))
			return false;
		double dv = fValueType.getCurrentValue(fWidget, null, fWidgetScale) - getCutPointValue();
		return dv * fSign >= 0;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#getWidget()
	 */
	@Override
	public IIndicator getWidget() {
		return super.getWidget();
	}


	public double getSign() {
		return fSign;
	}


	public void setCutPointValue(double aCutPointValue) {
		cutPointValue = aCutPointValue;
	}


	public void setSign(double aSign) {
		fSign = aSign;
	}


	public double getCutPointValue() {
		return cutPointValue;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#clone()
	 */
	@Override
	public ValueLevelCutPointTrigger clone() {
		return (ValueLevelCutPointTrigger) super.clone();
	}


	public static int getDelay() {
		return 0;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(cutPointValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(fSign);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((fValueType == null) ? 0 : fValueType.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueLevelCutPointTrigger other = (ValueLevelCutPointTrigger) obj;
		if (Double.doubleToLongBits(cutPointValue) != Double.doubleToLongBits(other.cutPointValue))
			return false;
		if (Double.doubleToLongBits(fSign) != Double.doubleToLongBits(other.fSign))
			return false;
		if (fValueType != other.fValueType)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "ValueLevelCutPointTrigger [fValueType=" + fValueType + ", cutPointValue=" + cutPointValue + ", fSign=" + fSign + "]";
	}

}
