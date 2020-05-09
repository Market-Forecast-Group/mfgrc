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

import com.mfg.strategy.automatic.triggers.RCLevelTrigger;
import com.mfg.strategy.automatic.triggers.Trigger;
import com.mfg.utils.ui.HtmlUtils;

/**
 * The RC Event takes place when the price touches the RC line defined by the indicator as the Bottom Regression Line when channel is down and the Top
 * Regression Line when channel direction is up. The event is triggered every time the price touches the line.
 * 
 * @author gardero
 * 
 */
public class EventAtomRC extends EventAtomScaleTrigger {

	private static final long serialVersionUID = 1L;

	private RCLevelTrigger fRCLevelTrigger;


	public EventAtomRC() {
		super(new RCLevelTrigger());
		fRCLevelTrigger = (RCLevelTrigger) getTrigger();
	}


	@Override
	public void setTrigger(Trigger aTrigger) {
		super.setTrigger(aTrigger);
		fRCLevelTrigger = (RCLevelTrigger) getTrigger();
	}


	/**
	 * @return the sCLevelTrigger
	 */
	// @Expand
	public RCLevelTrigger getRCLevelTrigger() {
		return fRCLevelTrigger;
	}


	/**
	 * @param aSCLevelTrigger
	 *            the sCLevelTrigger to set
	 */
	public void setRCLevelTrigger(RCLevelTrigger aRCLevelTrigger) {
		fRCLevelTrigger = aRCLevelTrigger;
	}


	@Override
	public String getLabel() {
		return getHtmlBody(HtmlUtils.Plain);
	}

}
