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

import com.mfg.strategy.automatic.eventPatterns.EventAtomExitProb;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;

public class ExitProbEventModel extends ScaledEventModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5721332646191851598L;


	@Override
	public String getLabel() {
		return "ExitProb{scale=" + widgetScale + "}";
	}


	@Override
	public EventGeneral exportMe() {
		EventAtomExitProb res = new EventAtomExitProb();
		res.setWidgetScale(getWidgetScale());
		return res;
	}


	@Override
	public boolean isProbabilistic() {
		return true;
	}

}
