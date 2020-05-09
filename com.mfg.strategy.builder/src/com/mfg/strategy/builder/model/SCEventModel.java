/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder.model;

import com.mfg.strategy.automatic.eventPatterns.EventAtomSC;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.triggers.SCLevelTrigger;

public class SCEventModel extends ScaledEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3944366727902425667L;


	@Override
	public String getLabel() {
		return "SC{scale=" + widgetScale + ((!isLimitedToSwing0()) ? "" : (", on Sw0 ")) + "}";
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomSC res = new EventAtomSC();
		res.setLimitToSwingZero(isLimitedToSwing0());
		SCLevelTrigger par = new SCLevelTrigger();
		par.setWidgetScale(getWidgetScale());
		res.setTrigger(par);
		return res;
	}

}
