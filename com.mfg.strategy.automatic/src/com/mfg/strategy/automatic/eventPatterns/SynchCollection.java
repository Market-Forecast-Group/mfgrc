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
import com.mfg.utils.ui.HtmlUtils;

public class SynchCollection extends EventLogicCollection {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;


	public SynchCollection() {
		super();
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		String res = "Shych";
		res += aUtil.getHtmlBucketList(events);
		return res;
	}


	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		boolean triggered = true;
		for (EventGeneral ev : events) {
			checkEvent(ev, aDealer);
			triggered &= ev.isTriggered();
		}
		setTriggered(triggered);
		return triggered;
	}


	@Override
	public String getLabel() {
		return "SYNCH";
	}

}
