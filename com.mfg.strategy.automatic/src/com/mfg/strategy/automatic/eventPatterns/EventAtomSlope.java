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

import com.mfg.utils.ui.HtmlUtils;

public class EventAtomSlope extends EventAtomScaleTrigger {

	private static final long serialVersionUID = 1L;


	public EventAtomSlope() {
		super(null);
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "SLOPE";
	}


	@Override
	public String getLabel() {
		return "SLOPE";
	}
}
