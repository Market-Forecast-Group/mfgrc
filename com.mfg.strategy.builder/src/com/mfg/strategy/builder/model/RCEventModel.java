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

import com.mfg.strategy.automatic.eventPatterns.EventAtomRC;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.triggers.RCLevelTrigger;

public class RCEventModel extends ScaledEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 94798307491043462L;


	@Override
	public String getLabel() {
		return "RC{scale=" + widgetScale + ((!isLimitedToSwing0()) ? "" : (", on Sw0 ")) + "}";
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomRC res = new EventAtomRC();
		res.setLimitToSwingZero(isLimitedToSwing0());
		RCLevelTrigger par = new RCLevelTrigger();
		par.setWidgetScale(getWidgetScale());
		res.setTrigger(par);
		return res;
	}

}
