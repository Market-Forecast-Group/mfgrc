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

import com.mfg.strategy.automatic.triggers.SCLevelTrigger;
import com.mfg.strategy.automatic.triggers.Trigger;
import com.mfg.utils.ui.HtmlUtils;

/**
 * The SC Event takes place when the price touches the SC line defined by the indicator as the Bottom Regression Line when channel is up and the Top
 * Regression Line when channel direction is down. The event is triggered every time the price touches the line.
 * 
 * @author gardero
 * 
 */
public class EventAtomSC extends EventAtomScaleTrigger {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private SCLevelTrigger fSCLevelTrigger;


	public EventAtomSC() {
		super(new SCLevelTrigger());
		fSCLevelTrigger = (SCLevelTrigger) getTrigger();
	}


	@Override
	public void setTrigger(Trigger aTrigger) {
		super.setTrigger(aTrigger);
		fSCLevelTrigger = (SCLevelTrigger) getTrigger();
	}


	/**
	 * @return the sCLevelTrigger
	 */
	// @Expand
	public SCLevelTrigger getSCLevelTrigger() {
		return fSCLevelTrigger;
	}


	/**
	 * @param aSCLevelTrigger
	 *            the sCLevelTrigger to set
	 */
	public void setSCLevelTrigger(SCLevelTrigger aSCLevelTrigger) {
		fSCLevelTrigger = aSCLevelTrigger;
	}


	@Override
	public String getLabel() {
		return getHtmlBody(HtmlUtils.Plain);
	}

}
