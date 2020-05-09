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

import java.awt.Color;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.utils.ui.HtmlUtils;
import com.mfg.widget.priv.TRIGGER_TYPE;

/**
 * TODO add documentation
 * 
 * @author gardero
 * 
 */
@SuppressWarnings("serial")
public class NewTHRelationShipTrigger extends ValueScaleSpecificTrigger {

	protected RelationshipType fRelationshipType = RelationshipType.NonContrarian;
	protected THType fTHType = THType.TH0;

	protected double thRatioL;
	protected double thRatioU;
	private int ths;

	public NewTHRelationShipTrigger() {
		super();
		thRatioL = 0;
		thRatioU = 1;
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
		ths = aWidget.getCurrentPivotsCount(fWidgetScale);
	}

	/**
	 * checks the TH relationship trigger.
	 * 
	 * @return {@code true} if the TH relationship is OK to be triggered.
	 */
	protected boolean check_THRelationship() {
		if (isEnabled()) {
			if (ths - getDelay() <= 0) {
				if (getWidget().isThereANewPivot(fWidgetScale))
					init(getWidget());
			}
			if (ths - getDelay() <= 0)
				return false;
			double TH0 = fValueType.getTHValue(fWidget, fWidgetScale, 0);
			double THm1 = fValueType.getTHValue(fWidget, fWidgetScale, 0);
			if (!getRelationshipType().check(fWidget.isSwingDown(fWidgetScale),
					TH0, THm1))
				return false;
			double TH0ratio = getTHRatio(fValueType, fWidget, fWidgetScale, 0);
			double THm1ratio = getTHRatio(fValueType, fWidget, fWidgetScale, -1);
			if (getTHType() != THType.TH0) {
				if (!checkIn(THm1ratio))
					return false;
			}
			if (getTHType() != THType.TH_m1) {
				if (!checkIn(TH0ratio))
					return false;
			}
		}
		return true;
	}

	public static double getTHRatio(TRIGGER_TYPE aValueType, IIndicator widget,
			int widgetScale, int step) {
		double TH = aValueType.getTHSegment(widget, widgetScale, step);
		double delta = aValueType.getSwingValue(widget, widgetScale, -1);
		return TH / delta;
	}

	private boolean checkIn(double aRatio) {
		if (aRatio < thRatioL)
			return false;
		if (thRatioU < 1 && aRatio >= thRatioU)
			return false;
		return true;
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
		return check_THRelationship();
	}

	protected void compute() {
		// DO NOTHING
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
	 * gets the type of the TH relationship (C, NC, None see
	 * {@link RelationshipType})
	 * 
	 * @return the type of TH relationship being checked.
	 */
	// @JSON(index = 65)
	// @Label("TH Relationship Type")
	// @Param
	public RelationshipType getRelationshipType() {
		return fRelationshipType;
	}

	/**
	 * sets the type of the TH relationship (C, NC, None see
	 * {@link RelationshipType})
	 * 
	 * @param aRelationshipType
	 */
	public void setRelationshipType(RelationshipType aRelationshipType) {
		fRelationshipType = aRelationshipType;
	}

	/**
	 * gets which thresholds ratios are we checking (TH<sub>0</sub>,
	 * TH<sub>-1</sub> or Both) to in an specific range.
	 * 
	 * @return the checked value.
	 */
	// @JSON(index = 65)
	// @Label("TH Type")
	// @Param
	public THType getTHType() {
		return fTHType;
	}

	/**
	 * sets which thresholds ratios are we checking (TH<sub>0</sub>,
	 * TH<sub>-1</sub> or Both) to in an specific range.
	 * 
	 * @param aTHType
	 *            the new checked value.
	 */
	public void setTHType(THType aTHType) {
		fTHType = aTHType;
	}

	/**
	 * gets the lower bound for the TH ratio we are checking.
	 * 
	 * @return the lower bound.
	 */
	// @JSON(index = 100)
	// @Param
	public double getThRatioL() {
		return thRatioL;
	}

	/**
	 * gets the upper bound for the TH ratio we are checking.
	 * 
	 * @return the upper bound.
	 */
	// @JSON(index = 110)
	// @Param
	public double getThRatioU() {
		return thRatioU;
	}

	public void setThRatioL(double aThRatioL) {
		thRatioL = aThRatioL;
	}

	public void setThRatioU(double aThRatioU) {
		thRatioU = aThRatioU;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.marketforecastgroup.priv.strategy.triggers.Trigger#clone()
	 */
	@Override
	public NewTHRelationShipTrigger clone() {
		return (NewTHRelationShipTrigger) super.clone();
	}

	public int getDelay() {
		return fEnabled ? 1 : 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((fRelationshipType == null) ? 0 : fRelationshipType
						.hashCode());
		result = prime * result + ((fTHType == null) ? 0 : fTHType.hashCode());
		result = prime * result
				+ ((fValueType == null) ? 0 : fValueType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(thRatioL);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(thRatioU);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		NewTHRelationShipTrigger other = (NewTHRelationShipTrigger) obj;
		if (fRelationshipType != other.fRelationshipType)
			return false;
		if (fTHType != other.fTHType)
			return false;
		if (fValueType != other.fValueType)
			return false;
		if (Double.doubleToLongBits(thRatioL) != Double
				.doubleToLongBits(other.thRatioL))
			return false;
		if (Double.doubleToLongBits(thRatioU) != Double
				.doubleToLongBits(other.thRatioU))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getHtmlBody(HtmlUtils.Plain);

	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		String res = "THRealionship{" + super.getHtmlBody(aUtil);
		res = ("{THRelation=" + getRelationshipType())
				+ (", TH=" + getTHType())
				+ (", Range=["
						+ aUtil.color("" + thRatioL, Color.blue)
						+ ", "
						+ aUtil.color(
								""
										+ ((thRatioU == 1) ? "Inf" : Double
												.valueOf(thRatioU)), Color.red) + ")")
				+ "}";
		return res + "}";
	}

}
