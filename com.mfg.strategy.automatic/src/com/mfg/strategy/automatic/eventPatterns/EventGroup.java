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
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.broker.orders.OrderImpl;
import com.mfg.strategy.automatic.EventsDealer;

public abstract class EventGroup extends EventGeneral {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private static final String EVENTS = null;
	protected ArrayList<EventGeneral> events;


	public EventGroup() {
		super();
		events = new ArrayList<>();
	}


	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		setParent(events);
		// System.out.println("setting parents for "+this);
	}


	@Override
	public void preinit(EventsDealer aDealer) {
		super.preinit(aDealer);
		for (EventGeneral e : events) {
			e.preinit(aDealer);
		}
	}


	@Override
	public void cancelThisEvent() {
		for (EventGeneral e : events) {
			e.cancelThisEvent();
		}
	}


	// @Override
	// public void addAllEventAtoms(EventsDealer aEventsDealer) {
	// for (EventGeneral e : events) {
	// e.addAllEventAtoms(aEventsDealer);
	// }
	// }

	@Override
	protected void collectMyFilledEntries(Hashtable<IOrderFilledListener, OrderImpl> table, List<OrderImpl> entries) {
		for (EventGeneral e : events) {
			e.collectMyFilledEntries(table, entries);
		}
	}


	public void addEvent(EventGeneral e) {
		events.add(e);
		e.setParentEvent(this);
	}


	/**
	 * @return the events
	 */
	// @JSON
	public ArrayList<EventGeneral> getEvents() {
		return events;
	}

	@Override
	public EventGeneral[] getChildren() {
		ArrayList<EventGeneral> list = getEvents();
		return list.toArray(new EventGeneral[list.size()]);
	}
	

	/**
	 * @param aEvents
	 *            the events to set
	 */
	public void setEvents(ArrayList<EventGeneral> aEvents) {
		events = aEvents;
		setParent(aEvents);
	}


	private void setParent(ArrayList<EventGeneral> aEvents) {
		for (EventGeneral eventGeneral : aEvents) {
			eventGeneral.setParentEvent(this);
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
		EventGroup other = (EventGroup) obj;
		if (events == null) {
			if (other.events != null)
				return false;
		} else if (!events.equals(other.events))
			return false;
		return true;
	}


	public void clearEvents() {
		getEvents().clear();
	}


	@Override
	public EventGroup clone() {
		EventGroup clone = (EventGroup) super.clone();
		clone.events = new ArrayList<>(events.size());
		for (EventGeneral e : events) {
			clone.events.add(e.clone());
		}
		clone.setParent(clone.events);
		return clone;
	}


	@Override
	public void getDelays(int[] delays) {
		for (EventGeneral e : events) {
			e.getDelays(delays);
		}
	}


	@Override
	public void setPresentScales(boolean[] scales) {
		for (EventGeneral e : events) {
			e.setPresentScales(scales);
		}
	}


	@Override
	public void turnAveragingOn(LSFilterType filter) {
		for (EventGeneral e : events) {
			e.turnAveragingOn(filter);
		}
	}


	@Override
	public void logDetails() {
		for (EventGeneral e : events) {
			e.logDetails();
		}
	}


	@Override
	public void getEntriesTo(EventGeneral aRequester, List<EventAtomEntry> aEntries, int[] IDs, boolean global) {
		for (EventGeneral e : events) {
			if (e != aRequester)
				e.getEntriesTo(this, aEntries, IDs, global);
		}
		if (getParentEvent() != null && getParentEvent() != aRequester)
			getParentEvent().getEntriesTo(this, aEntries, IDs, global);
	}


	@Override
	public int getScaleTo(EventGeneral aRequester) {
		for (int i = events.size() - 1; i >= 0; i--) {
			EventGeneral e = events.get(i);
			if (e != aRequester) {
				int s = e.getScaleTo(aRequester);
				if (s > -1)
					return s;
			}
		}
		return -1;
	}


	@Override
	public int getBigEntryScale() {
		int max = -1;
		for (int i = events.size() - 1; i >= 0; i--) {
			EventGeneral e = events.get(i);
			max = Math.max(max, e.getBigEntryScale());
		}
		return max;
	}


	@Override
	public boolean gotEntry() {
		for (int i = events.size() - 1; i >= 0; i--) {
			EventGeneral e = events.get(i);
			if (e.gotEntry())
				return true;
		}
		return false;
	}


	@Override
	public boolean needsToBeSplited() {
		for (EventGeneral e : events) {
			if (e.needsToBeSplited())
				return true;
		}
		return false;
	}


	@Override
	public void setBasedOn(LSFilterType filter) {
		for (EventGeneral e : events) {
			e.setBasedOn(filter);
		}
	}


	public void removeEvent(EventGeneral aMyEvent) {
		events.remove(aMyEvent);
	}


	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		stringer.key(EVENTS);
		stringer.array();
		for (int i = 0; i < events.size(); i++) {
			stringer.value(events.get(i));
		}
		stringer.endArray();
	}


	/**
	 * This method assumes that the object is already created and it assumes that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		JSONArray a = json.getJSONArray(EVENTS);
		events = new ArrayList<>(a.length());
		for (int i = 0; i < a.length(); i++) {
			events.add((EventGeneral) a.get(i));
		}
	}


	@Override
	public void suggestChildren(OrderImpl aEntry) {
		for (EventGeneral e : events) {
			e.suggestChildren(aEntry);
		}
	}
	
	@Override
	public boolean isPure(boolean entry){
		boolean res=true;
		for (EventGeneral e : events) {
			res &= e.isPure(entry);
			if (!res)
				break;
		}
		return res;
	}
}
