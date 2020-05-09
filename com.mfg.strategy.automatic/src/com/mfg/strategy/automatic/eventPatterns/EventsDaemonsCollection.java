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

import java.util.ArrayList;

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public class EventsDaemonsCollection extends EventGroup {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		myInit();
		for (EventGeneral ev : events) {
			ev.init(aDealer);
		}
	}

	@Override
	protected void setTriggered(boolean aTriggered) {
		boolean prev = isDoneChecking();
		super.setTriggered(aTriggered);
		setDoneChecking(prev);
	}

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		boolean res = false;
		ArrayList<EventGeneral> ne = new ArrayList<>(events.size());
		ArrayList<EventGeneral> ns = new ArrayList<>(events.size());
		for (EventGeneral ev : events) {
			ev.checkIFTriggered(aDealer);
			if (ev.isTriggered()) {
				logDetails();
				ne.add(ev.clone());
			} else
				ns.add(ev);
		}
		events.clear();
		events.addAll(ns);
		events.addAll(ne);
		for (EventGeneral ev : ne) {
			ev.preinit(aDealer);
			ev.init(aDealer);
		}
		return res;
	}

	private void myInit() {
		//
	}

	public EventsDaemonsCollection() {
		super();
	}

	// @Override
	// protected void setDiscarded(boolean aDiscarded) {
	// if (aDiscarded)
	// init(null);
	// super.setDiscarded(aDiscarded);
	// }

	@Override
	public void reset() {
		super.reset();
		myInit();
		for (EventGeneral ev : events) {
			ev.reset();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((events == null) ? 0 : events.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventsDaemonsCollection other = (EventsDaemonsCollection) obj;
		if (events == null) {
			if (other.events != null)
				return false;
		} else if (!events.equals(other.events))
			return false;
		return true;
	}

	@Override
	public String getLabel() {
		return "BUCLE";
	}

	@Override
	public String getHtmlBody(HtmlUtils util) {
		return "Bucle" + util.getHtmlBucketList(events);
	}

}
