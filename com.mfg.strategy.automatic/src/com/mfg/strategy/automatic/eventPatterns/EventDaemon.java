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

import java.util.List;

import com.mfg.broker.orders.OrderImpl;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.strategy.automatic.EventsPatternStrategy;

/**
 * 
 * @author gardero
 */
public abstract class EventDaemon extends EventGeneral {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private EventGeneral event;

	@Override
	public EventGeneral[] getChildren() {
		return new EventGeneral[] { event };
	}

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		event.checkIFTriggered(aDealer);
		setTriggered(event.isTriggered());
		setActive(event.isActive());
		return isTriggered();
	}

	@Override
	public void getDelays(int[] delays) {
		event.getDelays(delays);
	}

	@Override
	public void setPresentScales(boolean[] scales) {
		event.setPresentScales(scales);
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		event.init(aDealer);
	}

	@Override
	public void preinit(EventsDealer aDealer) {
		super.preinit(aDealer);
		event.preinit(aDealer);
	}

	@Override
	public void cancelThisEvent() {
		getEvent().cancelThisEvent();
	}

	@Override
	public void getEntriesTo(EventGeneral aRequester,
			List<EventAtomEntry> aEntries, int[] IDs, boolean global) {
		if (event != aRequester)
			event.getEntriesTo(this, aEntries, IDs, global);
		if (getParentEvent() != null && getParentEvent() != aRequester)
			getParentEvent().getEntriesTo(this, aEntries, IDs, global);
	}

	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return event.getScaleTo(aRequester);
	}

	@Override
	public int getBigEntryScale() {
		return event.getBigEntryScale();
	}

	@Override
	public boolean gotEntry() {
		return event.gotEntry();
	}

	// @JSON
	// @Expand
	public EventGeneral getEvent() {
		return event;
	}

	public void setEvent(EventGeneral aEvent) {
		this.event = aEvent;
		aEvent.setParentEvent(this);
	}

	@Override
	public EventDaemon clone() {
		EventDaemon res = (EventDaemon) super.clone();
		res.setEvent(event.clone());
		return res;
	}

	@Override
	public boolean needsToBeSplited() {
		return event.needsToBeSplited();
	}

	@Override
	public void setBasedOn(LSFilterType filter) {
		event.setBasedOn(filter);
	}

	public void addMe(EventsPatternStrategy p) {
		p.addDaemon(this);
	}

	@Override
	public void suggestChildren(OrderImpl aEntry) {
		event.suggestChildren(aEntry);
	}

}
