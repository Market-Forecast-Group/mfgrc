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

package com.mfg.strategy.automatic.eventPatterns;

import com.mfg.strategy.automatic.triggers.CLXoverTrigger;
import com.mfg.strategy.automatic.triggers.TrendXoverTrigger;
import com.mfg.strategy.automatic.triggers.Trigger;

/**
 * is the event triggered when price crosses the Central Regression Line. The event can occur contrarian to the direction of the swing, or
 * non-contrarian to it. For example if we are in a down swing, and price is below the Central Line, at the moment it goes above the central Line, we
 * will have a CL Xover contrarian to the direction of the swing.
 * 
 * @author gardero
 */
public class EventAtomTrendXover extends EventAtomScaleTrigger {

	private static final long serialVersionUID = 1L;

	private TrendXoverTrigger xoverTrigger;


	public EventAtomTrendXover() {
		super(new CLXoverTrigger());
		xoverTrigger = (TrendXoverTrigger) getTrigger();
	}


	@Override
	public void setTrigger(Trigger aTrigger) {
		super.setTrigger(aTrigger);
		xoverTrigger = (TrendXoverTrigger) getTrigger();
	}


	// @Expand
	public TrendXoverTrigger getXoverTrigger() {
		return xoverTrigger;
	}


	public void setXoverTrigger(TrendXoverTrigger aXoverTrigger) {
		this.xoverTrigger = aXoverTrigger;
	}


	@Override
	public String getLabel() {
		return "TREND XOVER (scale=" + xoverTrigger.getWidgetScale() + ")";
	}

}
