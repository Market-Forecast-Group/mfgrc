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
import com.mfg.strategy.automatic.EventsPatternStrategy;
import com.mfg.utils.ui.HtmlUtils;

/**
 * 
 * @author gardero
 */
public class GlobalAveragingRule extends EventDaemon {

	private static final long serialVersionUID = 1L;


	@Override
	public String getLabel() {
		return "AVG RULE";
	}


	@Override
	public String getHtmlBody(HtmlUtils util) {
		return "AVG{" + getEvent().getHtmlBody(util) + "}";
	}


	@Override
	public void preinit(EventsDealer aDealer) {
		super.preinit(aDealer);
	}


	@Override
	public void addMe(EventsPatternStrategy p) {
		if (needsToBeSplited()) {
			EventDaemon d = clone();
			d.setBasedOn(LSFilterType.Long);
			p.addDaemon(d);
			d = clone();
			d.setBasedOn(LSFilterType.Short);
			p.addDaemon(d);
		} else
			p.addDaemon(this);
	}

}
