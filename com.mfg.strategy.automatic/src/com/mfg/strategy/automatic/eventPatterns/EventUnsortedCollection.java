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

import java.util.Iterator;

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public class EventUnsortedCollection extends EventCollection {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private int lastScale;


	public EventUnsortedCollection() {
		super();
	}


	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		lastScale = -1;
	}


	/**
	 * we check all the events of the non-triggered events list and remove the triggered ones. Once that list is empty we can say that the event is
	 * triggered. If when we check an event it gets discarded then we can say that the UNSORTED event is discarded too. The flag isMatchingEvents() is
	 * set on when the one of the events turns its flag on.
	 * 
	 * @param aDealer
	 *            the events dealer.
	 * @return {@code true} iff at least one of the events checked returned true as the check return.
	 */
	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		boolean signal = false;
		for (Iterator<EventGeneral> it = nontriggeredEvents.iterator(); it.hasNext();) {
			EventGeneral eventGeneral = it.next();
			signal |= checkEvent(eventGeneral, aDealer);
			setMatchingEvents(isMatchingEvents() || eventGeneral.isMatchingEvents());
			if (eventGeneral.isTriggered()) {
				it.remove();
				lastScale = eventGeneral.getScaleTo(this);
			}
			if (eventGeneral.isDiscarded()) {
				setDiscarded(true);
				return signal;
			}
		}
		if (nontriggeredEvents.isEmpty()) {
			setTriggered(true);
		}
		return signal;
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		String res = "";
		res += aUtil.getHtmlBucketList(events);
		return res;
	}


	@Override
	public String getLabel() {
		return "UNSORTED";
	}


	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return lastScale;
	}

}
