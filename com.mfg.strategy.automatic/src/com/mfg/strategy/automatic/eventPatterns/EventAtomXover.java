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

import com.mfg.strategy.automatic.triggers.Trigger;
import com.mfg.strategy.automatic.triggers.XoverTrigger;

/**
 * @deprecated
 * @author gardero
 */
@Deprecated
public class EventAtomXover extends EventAtomScaleTrigger {

	private static final long serialVersionUID = 1L;

	private XoverTrigger xoverTrigger;


	public EventAtomXover() {
		super(new XoverTrigger());
		xoverTrigger = (XoverTrigger) getTrigger();
	}


	@Override
	public void setTrigger(Trigger aTrigger) {
		super.setTrigger(aTrigger);
		xoverTrigger = (XoverTrigger) getTrigger();
	}


	// @Expand
	public XoverTrigger getXoverTrigger() {
		return xoverTrigger;
	}


	public void setXoverTrigger(XoverTrigger aXoverTrigger) {
		this.xoverTrigger = aXoverTrigger;
	}


	@Override
	public String getLabel() {
		return "XOVER";
	}

}
