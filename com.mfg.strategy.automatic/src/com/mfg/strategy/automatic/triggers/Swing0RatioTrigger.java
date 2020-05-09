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
import com.mfg.utils.ui.HtmlUtils;

/**
 * TODO add documentation
 * 
 * @author gardero
 * 
 */
@SuppressWarnings("serial")
public class Swing0RatioTrigger extends ValueScaleSpecificTrigger {

	protected Swing0RatioRefSwing refSwing = Swing0RatioRefSwing.SwingM1;
	protected DoubleInterval fInterval;


	public Swing0RatioTrigger() {
		super();
		fInterval = new DoubleInterval();
		fInterval.setEnabledPositiveInfinite(true);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.marketforecastgroup.priv.strategy.triggers.Trigger#init(com. marketforecastgroup.priv.indicator.PivotsIndicatorWidget)
	 */
	@Override
	public void init(IIndicator aWidget) {
		super.init(aWidget);
		compute();
	}


	/**
	 * checks the swing<sub>0</sub> ratio trigger.
	 * 
	 * @return {@code true} if the swing<sub>0</sub> ratio is OK to be triggered.
	 */
	protected boolean check_Swing0Ratio() {
		double TH0 = fValueType.getDistanceToP0(fWidget, fWidgetScale);
		double ref = refSwing.getSwing(fWidget, fWidgetScale, fValueType);
		double Swing0ratio = TH0 / ref;
		return checkIn(Swing0ratio);
	}


	private boolean checkIn(double aRatio) {
		return fInterval.contains(aRatio);
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
		return check_Swing0Ratio();
	}


	protected void compute() {
		//DO NOTHING
	}


	/**
	 * @return the refSwing
	 */
	// @JSON(index = 230)
	// @Param
	// @Label("Ref Swing")
	public Swing0RatioRefSwing getRefSwing() {
		return refSwing;
	}


	/**
	 * @param aRefSwing
	 *            the refSwing to set
	 */
	public void setRefSwing(Swing0RatioRefSwing aRefSwing) {
		refSwing = aRefSwing;
	}


	/**
	 * @return the interval
	 */
	// @JSON(index = 250)
	// @Param(primitive = false)
	// @Expand(prefix = "Interval")
	public DoubleInterval getInterval() {
		return fInterval;
	}


	/**
	 * @param aInterval
	 *            the interval to set
	 */
	public void setInterval(DoubleInterval aInterval) {
		fInterval = aInterval;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#clone()
	 */
	@Override
	public Swing0RatioTrigger clone() {
		Swing0RatioTrigger res = (Swing0RatioTrigger) super.clone();
		res.fInterval = fInterval.clone();
		return res;
	}


	public int getDelay() {
		return fEnabled ? refSwing.getDelay() : 0;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fInterval == null) ? 0 : fInterval.hashCode());
		result = prime * result + ((fValueType == null) ? 0 : fValueType.hashCode());
		result = prime * result + ((refSwing == null) ? 0 : refSwing.hashCode());
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Swing0RatioTrigger other = (Swing0RatioTrigger) obj;
		if (fInterval == null) {
			if (other.fInterval != null)
				return false;
		} else if (!fInterval.equals(other.fInterval))
			return false;
		if (fValueType != other.fValueType)
			return false;
		if (refSwing != other.refSwing)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return getHtmlBody(HtmlUtils.Plain);

	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "SW0 Ratio{" + super.getHtmlBody(aUtil) + ", Swing0/" + refSwing + (" in " + fInterval.getHtmlBody(aUtil)) + "}";
	}
}
