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
 * this trigger will be triggered when price crosses the Central Regression Line. The event can occur contrarian to the direction of the swing, or
 * non-contrarian to it. For example if we are in a down swing, and price is below the Central Line, at the moment it goes above the central Line, we
 * will have a CL Xover contrarian to the direction of the swing.
 * 
 * @author gardero
 */
public class TrendXoverTrigger extends ScaleSpecificTrigger {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean prevXoverEval = true;
	private boolean contrarian;
	private double prevDiff;


	public TrendXoverTrigger() {
		fWidgetScale = 3;
	}


	@Override
	public void init(IIndicator aWidget) {
		super.init(aWidget);
		prevXoverEval = true;
		prevDiff = getDiff();
	}


	@Override
	protected boolean internalIsActive() {
		if (!fWidget.isLevelInformationPresent(fWidgetScale))
			return false;
		double diff = getDiff();
		boolean down = fWidget.isSwingDown(fWidgetScale);
		boolean currentEval = (diff != 0) && (diff > 0 == (down != contrarian));
		boolean res = (currentEval && prevDiff * diff <= 0);
		prevDiff = diff;
		prevXoverEval = currentEval;
		return res;
	}


	protected double getDiff() {
		if (!fWidget.isLevelInformationPresent(fWidgetScale))
			return 0;
		double cl1 = fWidget.getCurrentCenterRegressionPrice(fWidgetScale);
		double price = fWidget.getCurrentPrice();
		double diff = cl1 - price;
		return diff;
	}


	// @JSON
	public boolean isContrarian() {
		return contrarian;
	}


	public void setContrarian(boolean aContrarian) {
		this.contrarian = aContrarian;
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "TREND XOVER{S=" + getWidgetScale() + ", DIR=" + (contrarian ? "Contr" : "NonContr") + "}";
	}
}
