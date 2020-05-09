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
import com.mfg.strategy.automatic.triggers.ScaleSpecificTrigger;
import com.mfg.strategy.automatic.triggers.Trigger;
import com.mfg.utils.ui.HtmlUtils;

/**
 * represents an event that contains a trigger inside to determine when it is
 * triggered.
 * 
 * @author gardero
 */
public abstract class EventAtomTrigger extends EventAtom {

	/**
	 * Generated type to avoid warning.
	 */
	private static final long serialVersionUID = 1L;

	public EventAtomTrigger(Trigger aTrigger) {
		this.trigger = aTrigger;
	}

	private Trigger trigger;

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		boolean triggered = trigger.isActive() && trigger.isTriggered();
		setTriggered(triggered);
		return triggered;
	}

	@Override
	public EventAtomTrigger clone() {
		EventAtomTrigger res = null;
		res = (EventAtomTrigger) super.clone();
		res.setTrigger(trigger.clone());
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EventAtomTrigger other = (EventAtomTrigger) obj;
		if (trigger == null) {
			if (other.trigger != null) {
				return false;
			}
		} else if (!trigger.equals(other.trigger)) {
			return false;
		}
		return true;
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return trigger.getHtmlBody(aUtil);
	}

	@Override
	public String getLabel() {
		return "TRIGGER";
	}

	@Override
	public int getScaleTo(EventGeneral aRequester) {
		if (trigger instanceof ScaleSpecificTrigger) {
			return ((ScaleSpecificTrigger) trigger).getWidgetScale();
		}
		return -1;
	}

	/**
	 * @return the trigger
	 */
	// @JSON
	// @Expand
	// @Label(value = "Trigger")
	public Trigger getTrigger() {
		return trigger;
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
		result = prime * result + ((trigger == null) ? 0 : trigger.hashCode());
		return result;
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		if (aDealer != null) {
			// trigger.setLogger(getEventsDealer().getLogger());
			trigger.init(aDealer.getWidget());
		}
	}

	/**
	 * @param aTrigger
	 *            the trigger to set
	 */
	public void setTrigger(Trigger aTrigger) {
		trigger = aTrigger;
	}

}
