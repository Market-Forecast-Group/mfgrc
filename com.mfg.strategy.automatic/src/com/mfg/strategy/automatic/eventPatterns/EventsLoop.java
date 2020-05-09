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

public class EventsLoop extends EventDaemon {

	private static final long serialVersionUID = 1L;


	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		EventGeneral event = getEvent();
		boolean res = event.checkIFTriggered(aDealer);
		setTriggered(event.isTriggered());
		setActive(event.isActive());
		if (event.isTriggered()) {
			logDetails();
			EventsLoop newMe = this.clone();
			newMe.getEvent().preinit(aDealer);
			newMe.getEvent().init(aDealer);
			aDealer.addEventThread(newMe);
		}
		return res;
	}


	@Override
	public String getLabel() {
		return "Loop";
	}


	@Override
	public String getHtmlBody(HtmlUtils util) {
		return util.bold("Loop") + "{" + getEvent().getHtmlBody(util) + "}";
	}


	@Override
	public EventsLoop clone() {
		EventsLoop res = (EventsLoop) super.clone();
		res.setEvent(getEvent().clone());
		return res;
	}

}
