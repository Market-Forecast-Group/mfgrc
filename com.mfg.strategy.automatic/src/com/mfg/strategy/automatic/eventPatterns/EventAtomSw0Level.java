/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gadero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.eventPatterns;

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.strategy.automatic.triggers.NewValueLevelTrigger;
import com.mfg.strategy.automatic.triggers.Trigger;

/**
 * represents an event that is triggered when the value of the current swing reaches an specific cut-point, computed using a percent of a swing
 * (swing<sub>0</sub>, swing<sub>-1</sub> or swing<sub>-2</sub>) and a start point (HHLL, P<sub>0</sub>, P<sub>-1</sub> or P<sub>-2</sub>).
 * 
 * @author gardero
 * 
 */
public class EventAtomSw0Level extends EventAtomScaleTrigger {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private NewValueLevelTrigger fNewValueLevelTrigger;


	public EventAtomSw0Level() {
		super(new NewValueLevelTrigger());
		fNewValueLevelTrigger = (NewValueLevelTrigger) getTrigger();
		setLimitToSwingZero(true);
	}


	/**
	 * computes a cutpoint based on the references parameters and once the HH/LL (computed from the moment we start checking) and it will be triggered
	 * once it reaches that cutpoint level in the direction of the initial swing. After that moment, if we ask again to that event, it will not be
	 * triggered but active.
	 * 
	 * @param aDealer
	 *            the events dealer.
	 * @return {@code true} iff we are on a new threshold. We create a new check of this event at each new TH of the scale it is located in, because
	 *         that is the place where references change and it will determine a new cutpoint value to be reached.
	 */
	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		super.checkIFTriggered(aDealer);
		return aDealer.getWidget().isLevelInformationPresent(fNewValueLevelTrigger.getWidgetScale()) && aDealer.getWidget().isThereANewPivot(fNewValueLevelTrigger.getWidgetScale());
	}


	@Override
	public void setTrigger(Trigger aTrigger) {
		super.setTrigger(aTrigger);
		fNewValueLevelTrigger = (NewValueLevelTrigger) getTrigger();
	}


	/**
	 * @return the newValueLevelTrigger
	 */
	// @Expand
	public NewValueLevelTrigger getNewValueLevelTrigger() {
		return fNewValueLevelTrigger;
	}


	/**
	 * @param aNewValueLevelTrigger
	 *            the newValueLevelTrigger to set
	 */
	public void setNewValueLevelTrigger(NewValueLevelTrigger aNewValueLevelTrigger) {
		fNewValueLevelTrigger = aNewValueLevelTrigger;
	}


	@Override
	public String getLabel() {
		return "SW0 LEVEL (scale=" + fNewValueLevelTrigger.getWidgetScale() + ")";
	}

}
