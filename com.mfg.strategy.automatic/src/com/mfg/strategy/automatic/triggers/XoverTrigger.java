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
 * represents the trigger that is activated when we have a crossover between the center regression lines of two consecutive scales.
 * 
 * @author gardero
 */
public class XoverTrigger extends ScaleSpecificTrigger {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	protected ComparationOperator xoverOperator = ComparationOperator.G;
	protected boolean prevXoverEval = true;


	public XoverTrigger() {
		fWidgetScale = 3;
	}


	@Override
	public void init(IIndicator aWidget) {
		super.init(aWidget);
		prevXoverEval = true;
	}


	@Override
	protected boolean internalIsActive() {
		if (!fWidget.isLevelInformationPresent(fWidgetScale))
			return false;
		double cl1 = fWidget.getCurrentCenterRegressionPrice(fWidgetScale);
		double cl2 = fWidget.getCurrentCenterRegressionPrice(fWidgetScale + 1);
		boolean currentEval = xoverOperator.compare(cl1, cl2);
		boolean res = (currentEval && !prevXoverEval);
		prevXoverEval = currentEval;
		return res;
	}


	// @JSON
	public ComparationOperator getXoverOperator() {
		return xoverOperator;
	}


	public void setXoverOperator(ComparationOperator aXoverOperator) {
		this.xoverOperator = aXoverOperator;
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "XOVER{S=" + getWidgetScale() + ", OP=" + xoverOperator.toHtmlString() + "}";
	}
}
 