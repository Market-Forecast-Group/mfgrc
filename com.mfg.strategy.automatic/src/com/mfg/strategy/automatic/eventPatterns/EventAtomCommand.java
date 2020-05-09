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

public abstract class EventAtomCommand extends EventAtom {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private EventCommandContainer fCommandContainer;


	// public void addAllEventAtoms(EventsDealer aEventsDealer) {
	// }

	@Override
	public EventAtomCommand clone() {
		return (EventAtomCommand) super.clone();
	}

	
	/**
	 * @return the commandContainer
	 */
	public EventCommandContainer getCommandContainer() {
		return fCommandContainer;
	}


	/**
	 * @param aCommandContainer
	 *            the commandContainer to set
	 */
	public void setCommandContainer(EventCommandContainer aCommandContainer) {
		fCommandContainer = aCommandContainer;
	}


	@Override
	public void reset() {
		setTriggered(false);
		setDiscarded(false);
		setDoneChecking(false);
	}


	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return -1;
	}


	@Override
	public int getBigEntryScale() {
		return -1;
	}


	public abstract boolean ready2BChecked();
	
	protected ArrayList<EventAtomEntry> collectEntries(boolean aGlobal, int[] fEntries) {
		ArrayList<EventAtomEntry> _entries = new ArrayList<>();
		getParentEvent().getEntriesTo(this, _entries, fEntries, aGlobal);
		return _entries;
	}


}
