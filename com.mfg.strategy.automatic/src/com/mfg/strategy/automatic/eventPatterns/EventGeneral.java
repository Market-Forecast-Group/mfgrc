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

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.GenericIdentifier;
import com.mfg.utils.ui.HtmlUtils;
import com.mfg.utils.ui.HtmlUtils.IHtmlStringProvider;

public abstract class EventGeneral extends GenericIdentifier implements IHtmlStringProvider, Serializable {

	// private static Logger _log = Logger.getLogger(EventGeneral.class);

	private EAccountRouting _accountRouting;
	
	public EventGeneral() {
		_accountRouting = null;
	}
	
	public EAccountRouting getAccountRouting() {
		return _accountRouting;
	}
	
	public void setAccountRouting(EAccountRouting accountRouting) {
		_accountRouting = accountRouting;
	}
	
	@Override
    protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    protected void _updateFromJSON(JSONObject json) throws JSONException
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * 
     */
	private static final long serialVersionUID = 1L;

	private transient EventGeneral parentEvent;
	private int _nodeid;
	private int birthID;

	public int getNodeID() {
		return _nodeid;
	}
	
	public void setNodeID(int nodeid) {
		_nodeid = nodeid;
	}
	
	public abstract EventGeneral[] getChildren();

	/**
	 * @return the parentEvent
	 */
	public EventGeneral getParentEvent() {
		return parentEvent;
	}


	/**
	 * @param aParentEvent
	 *            the parentEvent to set
	 */
	public void setParentEvent(EventGeneral aParentEvent) {
		parentEvent = aParentEvent;
	}

	private transient EventsDealer fEventsDealer;


	/**
	 * @return the eventsDealer
	 */
	public EventsDealer getEventsDealer() {
		return fEventsDealer;
	}


	/**
	 * @param aEventsDealer
	 *            the eventsDealer to set
	 */
	public void setEventsDealer(EventsDealer aEventsDealer) {
		fEventsDealer = aEventsDealer;
	}


	/**
	 * sets the false value for status properties ({@code isTriggered()}, {@code isMatchingEvents()} and {@code isDiscarded()})
	 * 
	 * @param aDealer
	 *            the dealer to be used by this event.
	 */
	public void init(EventsDealer aDealer) {
		setTriggered(false);
		setMatchingEvents(false);
		setDiscarded(false);
		if (aDealer != null) {
			setEventsDealer(aDealer);
		}
		// if (_log.isDebugEnabled()) {
		// _log.debug("----Init " + this);
		// }
	}


	public void preinit(EventsDealer aDealer) {
		setEventsDealer(aDealer);
		birthID = aDealer.getThisBirthID();
		setTriggered(false);
		setMatchingEvents(false);
		_accountRouting = null;
	}


	/**
	 * checks if the event is triggered.
	 * 
	 * @param aDealer
	 *            the events dealer.
	 * @return if this event accepted the input in the current time.
	 */
	public abstract boolean checkIFTriggered(EventsDealer aDealer);


	public abstract void suggestChildren(OrderImpl entry);

	private transient boolean discarded;


	/**
	 * @return the discarded
	 */
	public boolean isDiscarded() {
		return discarded;
	}


	/**
	 * @param aDiscarded
	 *            the discarded to set
	 */
	protected void setDiscarded(boolean aDiscarded) {
		// if (aDiscarded)
		// init(null);
		discarded = aDiscarded;
	}

	private transient boolean active;

	private transient boolean triggered;


	/**
	 * @param aTriggered
	 *            the triggered to set
	 */
	protected void setTriggered(boolean aTriggered) {
		triggered = aTriggered;
		if (aTriggered) {
			setDoneChecking(true);
		}
		// if (aTriggered) {
		// if (_log.isDebugEnabled()) {
		// _log.debug("****Trigered " + this);
		// }
		// }
	}

	private boolean matchingEvents;


	public boolean isMatchingEvents() {
		return matchingEvents;
	}


	public void setMatchingEvents(boolean aMatchingEvents) {
		this.matchingEvents = aMatchingEvents;
	}


	/**
	 * @return the triggered
	 */
	public boolean isTriggered() {
		return triggered;
	}


	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}


	/**
	 * @param aActive
	 *            the active to set
	 */
	public void setActive(boolean aActive) {
		active = aActive;
	}

	private transient boolean doneChecking;


	/**
	 * @return the done checking
	 */
	public boolean isDoneChecking() {
		return doneChecking;
	}


	/**
	 * @param aDoneChecking
	 *            the done checking to set
	 */
	public void setDoneChecking(boolean aDoneChecking) {
		doneChecking = aDoneChecking;
	}


	// public void addAllEventAtoms(EventsDealer aEventsDealer) {
	// }

	public void reset() {
		setTriggered(false);
		setDiscarded(false);
		setDoneChecking(false);
		init(fEventsDealer);
	}


	@Override
	public EventGeneral clone() {
		EventGeneral clone = null;
		try {
			clone = (EventGeneral) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}


	protected static boolean checkEvent(EventGeneral e, EventsDealer aDealer) {
		boolean counted = false;
		counted = e.checkIFTriggered(aDealer);
		return counted;
	}


	/**
	 * @param table  
	 * @param entries 
	 */
	protected void collectMyFilledEntries(Hashtable<IOrderFilledListener, OrderImpl> table, List<OrderImpl> entries) {
		//DO NOTHING
	}

	private static HtmlUtils hutil = HtmlUtils.Plain;


	@Override
	public String toString() {
		return getHtmlBody(hutil);
	}


	public abstract String getLabel();


	public abstract void getEntriesTo(EventGeneral requester, List<EventAtomEntry> entries, int[] IDs, boolean global);


	public abstract int getScaleTo(EventGeneral requester);


	public abstract int getBigEntryScale();


	public abstract boolean gotEntry();


	public abstract void cancelThisEvent();


	/**
	 * @param delays  
	 */
	public void getDelays(int[] delays) {
		//DO NOTHING
	}


	/**
	 * @param scales  
	 */
	public void setPresentScales(boolean[] scales) {
		//DO NOTHING
	}


	/**
	 * @param filter  
	 */
	public void turnAveragingOn(LSFilterType filter) {
		//DO NOTHING
	}


	public EventGeneral getRoot() {
		if (getParentEvent() == null)
			return this;
		return getParentEvent().getRoot();
	}


	public int getBirthID() {
		return birthID;
	}


	/**
	 * prints to the log the details of the event activation;
	 */
	public void logDetails() {
		//DO NOTHING
	}


	public <T> boolean inside(Class<T> t) {
		EventGeneral p = this.getParentEvent();
		while (p != null) {
			if (p.getClass() == t)
				return true;
			p = p.getParentEvent();
		}
		return false;
	}


	@SuppressWarnings("static-method")// Overloaded on inner classes.
	public boolean needsToBeSplited() {
		return false;
	}


	/**
	 * @param filter  
	 */
	public void setBasedOn(LSFilterType filter) {
		//DO NOTHING
	}
	
	/**
	 * @param entry  
	 */
	@SuppressWarnings("static-method")// Used on inner classes.
	public boolean isPure(boolean entry){
		return false;
	}
	
	public boolean isNotPure(){
		return !isPure(true) && !isPure(false);
	}

}
