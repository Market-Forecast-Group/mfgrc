/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gadero@gmail.com">Enrique Matos Alfonso</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.automatic.eventPatterns;

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public class EventSortedCollection extends EventCollection {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;


	public EventSortedCollection() {
		super();
	}


	/**
	 * it checks the first element of the non-triggered events and if it is triggered it removes from the list of non-triggered events and initializes
	 * the next event that will be checked on the next tick. Once the list of non-triggered events is empty we can say that the event is triggered. If
	 * when we check an event it gets discarded then we can say that the SORTED event is discarded too. The flag isMatchingEvents() is set on when the
	 * first event turns its flag on.
	 * 
	 * @param aDealer
	 *            the events dealer.
	 * @return the result of the check done to the first element of the non-triggered events.
	 */
	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		if (!nontriggeredEvents.isEmpty()) {
			currentevent = nontriggeredEvents.get(0);
			boolean signal = checkEvent(currentevent, aDealer);
			setMatchingEvents(isMatchingEvents() || currentevent.isMatchingEvents());
			if (currentevent.isDiscarded()) {
				setDiscarded(true);
			}
			if (currentevent.isTriggered()) {
				moveToEvent();
			}
			if (nontriggeredEvents.isEmpty()) {
				setTriggered(true);
			}
			return signal;
		}
		return false;
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		String res = "";
		if (getParentEvent() != null)
			res += aUtil.getHtmlSortedList(events, true, true);
		else {
			if (aUtil.isOn())
			res += "<table border=\"1\"><tbody><tr>";
			for (EventGeneral eventGeneral : events) {
				res += ("<td>" + eventGeneral.getHtmlBody(aUtil) + "</td>");
			}
			if (aUtil.isOn())
			res += "</tr></tbody></table>";
		}
		return res;
	}


	@Override
	public String getLabel() {
		return "SORTED";
	}

}
