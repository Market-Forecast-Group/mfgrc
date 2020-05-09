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
 * represents a class containing logic that checks if the value of the current swing reaches an specific cut-point, computed using a percent of a
 * swing (swing<sub>0</sub>, swing<sub>-1</sub> or swing<sub>-2</sub>) and a start point (HHLL, P<sub>0</sub>, P<sub>-1</sub> or P<sub>-2</sub>).
 * 
 * @author gardero
 * 
 */
public class NewValueLevelTrigger extends ValueLevelTrigger {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private boolean updateOnHHLL;
	private boolean limitedToSwing0;
	private boolean updatingReferences;


	public NewValueLevelTrigger() {
		super();
		fWidgetScale = 3;
	}


	@Override
	public void init(IIndicator aWidget) {
		setTHS(aWidget);
		updateOnHHLL = getStartPoint() == StartPoint.HHLL || (getStartPoint() == StartPoint.P_m1 && getRefSwing() == 0);
		ll = Double.MAX_VALUE;
		hh = 0;
		super.init(aWidget);
	}


	@Override
	protected boolean internalIsActive() {
		if (!fWidget.isLevelInformationPresent(fWidgetScale))
			return false;
		if (shouldIUpdate()) {
			updateRef(getWidget().isThereANewPivot(fWidgetScale));
		}
		if (getTHS() + fRefSwing <= 0) {
			if (getWidget().isThereANewPivot(fWidgetScale))
				init(getWidget());
		}
		if (getTHS() + fRefSwing <= 0)
			return false;
		boolean activated = super.internalIsActive();
		return activated;
	}


	private boolean shouldIUpdate() {
		int thOffset = getOffset();
		if (thOffset < 1 && updateOnHHLL)
			return true;
		if (updatingReferences) {
			if (thOffset % 2 == 0 && getWidget().isThereANewPivot(fWidgetScale)) {
				if (getEventAtomTriger().isLimitToSwingZero())
					getEventAtomTriger().setTHS();
				setTHS(fWidget);
				return true;
			}
			return false;
		}
		return (thOffset == 0) && (getWidget().isThereANewPivot(fWidgetScale));
	}


	// getEventAtomTriger().setTHS();

	@Override
	protected void compute() {
		if (getTHS() + fRefSwing <= 0)
			return;
		super.compute();
	}


	@Override
	protected void updateRef(boolean log) {
		if (getTHS() + fRefSwing <= 0)
			return;
		super.updateRef(log);
	}


	/**
	 * @return the limitedToSwing0
	 */
	// @JSON
	public boolean isLimitedToSwing0() {
		return limitedToSwing0;
	}


	/**
	 * @param aLimitedToSwing0
	 *            the limitedToSwing0 to set
	 */
	public void setLimitedToSwing0(boolean aLimitedToSwing0) {
		limitedToSwing0 = aLimitedToSwing0;
	}


	// @JSON
	public boolean isUpdatingReferences() {
		return updatingReferences;
	}


	public void setUpdatingReferences(boolean aUpdatingReferences) {
		this.updatingReferences = aUpdatingReferences;
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		String s = updatingReferences ? ", updt ref." : "";
		return getValueType() + "=" + super.getHtmlBody(aUtil) + s;
	}

	private double ll = Double.MAX_VALUE;


	private double getMyLL(double v) {
		return ll = Math.min(v, ll);
	}

	private double hh = 0;


	private double getMyHH(double v) {
		return hh = Math.max(v, hh);
	}


	@Override
	protected double computeValue() {
		switch (fValueType) {
		case PRICE:
			if (getSign() > 0) {
				// return fWidget.getHHPrice(fWidgetScale);
				return getMyHH(fValueType.getCurrentValue(fWidget, fStartPoint, fWidgetScale));
			}
			// return fWidget.getLLPrice(fWidgetScale);
			return getMyLL(fValueType.getCurrentValue(fWidget, fStartPoint, fWidgetScale));
			//$CASES-OMITTED$
		default:
			return fValueType.getCurrentValue(fWidget, fStartPoint, fWidgetScale);
		}
	}

}
