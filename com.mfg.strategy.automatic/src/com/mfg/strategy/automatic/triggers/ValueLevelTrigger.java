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
import com.mfg.widget.priv.StartPoint;

/**
 * this class represents Trigger with a value that reaches an specific cut point
 * to activate it.
 * 
 * @author gardero
 * 
 */
@SuppressWarnings("serial")
public class ValueLevelTrigger extends ValueScaleSpecificTrigger {

	// private static Logger _log = Logger.getLogger(ValueLevelTrigger.class);

	protected double fPercent = 1;
	protected int fRefSwing = -1;
	protected StartPoint fStartPoint = StartPoint.P_0;
	private double cutPointValue;
	private double fSign;
	private int ths;

	public ValueLevelTrigger() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.marketforecastgroup.priv.strategy.triggers.Trigger#init(com.
	 * marketforecastgroup.priv.indicator.PivotsIndicatorWidget)
	 */
	@Override
	public void init(IIndicator aWidget) {
		super.init(aWidget);
		compute();
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
		double currentValue = computeValue();
		double dv = currentValue - getCutPointValue();
		boolean res = dv * fSign >= 0;
		// if (_log.isDebugEnabled()) {
		// if (res) {
		// if (fSign == 1) {
		// _log.debug(currentValue + ">=" + getCutPointValue() + " " + res);
		// } else {
		// _log.debug(currentValue + "<=" + getCutPointValue() + " " + res);
		// }
		// }
		// }
		return res;
	}

	protected double computeValue() {
		return fValueType.getCurrentValue(fWidget, fStartPoint, fWidgetScale);
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

	/**
	 * gets the start point, used as reference to compute the cut point value.
	 * 
	 * @return the Start Point
	 */
	// @JSON(index = 210)
	// @Param
	// @Label("Start Point")
	public StartPoint getStartPoint() {
		return fStartPoint;
	}

	/**
	 * sets the start point, used as reference to compute the cut point value.
	 * 
	 * @param aStartPoint
	 *            the new Start Point to set.
	 */
	public void setStartPoint(StartPoint aStartPoint) {
		fStartPoint = aStartPoint;
	}

	/**
	 * gets the reference Swing.
	 * 
	 * @return the reference Swing.
	 */
	// @JSON(index = 220)
	// @Param
	// @Label("Ref Swing")
	public int getRefSwing() {
		return fRefSwing;
	}

	/**
	 * sets the reference Swing.
	 * 
	 * @param aRefSwing
	 *            the new reference Swing to set.
	 */
	public void setRefSwing(int aRefSwing) {
		fRefSwing = aRefSwing;
	}

	/**
	 * gets the percent of the reference swing we add to the start point to
	 * compute the cut point value.
	 * 
	 * @return the Percent.
	 */
	// @JSON(index = 230)
	// @Param
	// @Label("Percent")
	public double getPercent() {
		return fPercent;
	}

	/**
	 * sets the percent of the reference swing we add to the start point to
	 * compute the cut point value.
	 * 
	 * @param aPercent
	 *            the new Percent to set
	 */
	public void setPercent(double aPercent) {
		fPercent = aPercent;
	}

	public double getCutPointValue() {
		return cutPointValue;
	}

	protected void compute() {
		fSign = fStartPoint.getSign(fWidget, fWidgetScale, fValueType);
		updateRef(true);
	}

	protected void updateRef(boolean log) {
		double startPoint = fStartPoint.getStartPoint(fWidget, fWidgetScale,
				fValueType);
		double swingValue = fValueType.getSwingValue(fWidget, fWidgetScale,
				fRefSwing);
		if (fStartPoint == StartPoint.HHLL && getOffset() > 0) {
			startPoint = StartPoint.P_0.getStartPoint(fWidget, fWidgetScale,
					fValueType);
			swingValue = fValueType.getSwingValue(fWidget, fWidgetScale,
					fRefSwing - 1);
		}
		cutPointValue = fSign * swingValue * fPercent + startPoint;
		if (log)
			logUpdate(startPoint, swingValue);
	}

	/**
	 * @param startPoint  
	 * @param swingValue 
	 */
	protected void logUpdate(double startPoint, double swingValue) {
		// not used
		// IExecutionLog llogger = getLogger();
		// if (llogger != null) {
		// // if (llogger.isEnabled(EMessageType.Comment)) {
		// // PatternStrategyMessage m = new
		// PatternStrategyMessage(getEventAtomTriger().getBirthID(),
		// StrategyMessageType.HTMLComment,
		// // "Updating cp=" + cutPointValue + "=" + startPoint + (fSign > 0 ?
		// "+" : "-") + fPercent + "*" + swingValue, "Automatic");
		// // llogger.log(m);
		// // }
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#clone()
	 */
	@Override
	public ValueLevelTrigger clone() {
		return (ValueLevelTrigger) super.clone();
	}

	public int getDelay() {
		return Math.max(fStartPoint.getDelay(), -fRefSwing);
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
		long temp;
		temp = Double.doubleToLongBits(fPercent);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + fRefSwing;
		result = prime * result
				+ ((fStartPoint == null) ? 0 : fStartPoint.hashCode());
		result = prime * result
				+ ((fValueType == null) ? 0 : fValueType.hashCode());
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
		ValueLevelTrigger other = (ValueLevelTrigger) obj;
		if (Double.doubleToLongBits(fPercent) != Double
				.doubleToLongBits(other.fPercent))
			return false;
		if (fRefSwing != other.fRefSwing)
			return false;
		if (fStartPoint != other.fStartPoint)
			return false;
		if (fValueType != other.fValueType)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getHtmlBody(HtmlUtils.Plain);
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return super.getHtmlBody(aUtil) + " , " + fStartPoint
				+ (fSign > 0 ? "+" : "-") + fPercent + "*swing"
				+ aUtil.sub("(" + fRefSwing + ")");
	}

	protected double getSign() {
		return fSign;
	}

	protected int getTHS() {
		return ths;
	}

	protected void setTHS(IIndicator aWidget) {
		if (aWidget.isLevelInformationPresent(fWidgetScale))
			ths = aWidget.getCurrentPivotsCount(fWidgetScale);
	}

	protected int getOffset() {
		return fWidget.getCurrentPivotsCount(fWidgetScale) - getTHS();
	}

}
