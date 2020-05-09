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

import com.mfg.strategy.automatic.triggers.THRelationShipTrigger;
import com.mfg.strategy.automatic.triggers.Trigger;

public class EventAtomTHRelationShip extends EventAtomScaleTrigger {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private THRelationShipTrigger fTHRelationShipTrigger;


	public EventAtomTHRelationShip() {
		super(new THRelationShipTrigger());
		fTHRelationShipTrigger = (THRelationShipTrigger) getTrigger();
		fTHRelationShipTrigger.setWidgetScale(3);
	}


	@Override
	public void setTrigger(Trigger aTrigger) {
		super.setTrigger(aTrigger);
		fTHRelationShipTrigger = (THRelationShipTrigger) getTrigger();
	}


	/**
	 * @return the tHRelationShipTrigger
	 */
	// @Expand
	public THRelationShipTrigger getTHRelationShipTrigger() {
		return fTHRelationShipTrigger;
	}


	/**
	 * @param aTHRelationShipTrigger
	 *            the tHRelationShipTrigger to set
	 */
	public void setTHRelationShipTrigger(THRelationShipTrigger aTHRelationShipTrigger) {
		fTHRelationShipTrigger = aTHRelationShipTrigger;
	}


	@Override
	public String getLabel() {
		return "TH REL (scale=" + fTHRelationShipTrigger.getWidgetScale() + ")";
	}

}
