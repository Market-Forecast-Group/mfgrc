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

public abstract class EventAtom extends EventGeneral {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;


	// @Override
	// public void addAllEventAtoms(EventsDealer aEventsDealer) {
	// aEventsDealer.addEvent(this.clone());
	// }

	@Override
	public EventAtom clone() {
		return (EventAtom) super.clone();
	}

	@Override
	public EventGeneral[] getChildren() {
		return new EventGeneral[0];
	}
	
	@Override
	public void getEntriesTo(EventGeneral aRequester, List<EventAtomEntry> aEntries, int[] IDs, boolean global) {
		//DO NOTHING
	}


	@Override
	public boolean gotEntry() {
		return false;
	}


	@Override
	public void cancelThisEvent() {
		//DO NOTHING
	}


	@Override
	protected void setTriggered(boolean aTriggered) {
		super.setTriggered(aTriggered);
		setMatchingEvents(aTriggered);
	}


	@Override
	public void suggestChildren(OrderImpl aEntry) {
		// to be redefined when needed.
	}
	
	@Override
	public boolean isPure(boolean entry){
		return true;
	}

}
