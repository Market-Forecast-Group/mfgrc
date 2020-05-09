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

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.strategy.automatic.triggers.RatioTrigger;
import com.mfg.strategy.automatic.triggers.Trigger;
import com.mfg.widget.priv.TRIGGER_TYPE;

/**
 * looks for a relationship between swings. Focus mainly on past swings starting from swing<sub>-1</sub> and older. It is basically a filter and will
 * enable some other events which can generate an entry only if a certain ratio between Swing<sub>-1</sub>/Swing<sub>-2</sub> (or more swings) is met.
 * 
 * @author gardero
 * 
 */
public class EventAtomTriggerRatio extends EventAtomScaleTrigger {
	private static final long serialVersionUID = 1L;

	private RatioTrigger ratioTrigger;


	public EventAtomTriggerRatio() {
		super(new RatioTrigger(TRIGGER_TYPE.PRICE));
		ratioTrigger = (RatioTrigger) getTrigger();
		ratioTrigger.setWidgetScale(3);
		ratioTrigger.setDimensions(2);
		ratioTrigger.getLowerBounds()[1] = 1;
		ratioTrigger.getUpperBounds()[0] = 10;
		ratioTrigger.getUpperBounds()[1] = 10;
	}


	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		super.checkIFTriggered(aDealer);
		return isTriggered() && aDealer.getWidget().isThereANewPivot(ratioTrigger.getWidgetScale());
	}


	@Override
	public void setTrigger(Trigger aTrigger) {
		super.setTrigger(aTrigger);
		ratioTrigger = (RatioTrigger) getTrigger();
	}


	/**
	 * @return the ratioTrigger
	 */
	// @Expand
	public RatioTrigger getRatioTrigger() {
		return ratioTrigger;
	}


	/**
	 * @param aRatioTrigger
	 *            the ratioTrigger to set
	 */
	public void setRatioTrigger(RatioTrigger aRatioTrigger) {
		ratioTrigger = aRatioTrigger;
	}


	@Override
	public String getLabel() {
		return "SW RATIO (scale=" + ratioTrigger.getWidgetScale() + ")";
	}

}
