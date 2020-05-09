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

public abstract class EventCollection extends EventGroup {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected transient ArrayList<EventGeneral> nontriggeredEvents;
    private transient ArrayList<EventGeneral> trigeredEvents;
    protected EventGeneral currentevent;

    @Override
    public void init(EventsDealer aDealer) {
	super.init(aDealer);
	myInit();
	for (EventGeneral ev : events) {
	    ev.init(aDealer);
	}
    }

    @SuppressWarnings("unchecked")
	private void myInit() {
	nontriggeredEvents = (ArrayList<EventGeneral>) events.clone();
	if (trigeredEvents==null)
	    trigeredEvents = new ArrayList<>();
	else
	    trigeredEvents.clear();
    }

    public EventCollection() {
	super();
	nontriggeredEvents = new ArrayList<>();
	trigeredEvents = new ArrayList<>();
    }

    @Override
    protected void setDiscarded(boolean aDiscarded) {
//	if (aDiscarded)
//	    init(null);
	super.setDiscarded(aDiscarded);
    }

    @Override
	public void reset() {
	super.reset();
	myInit();
	for (EventGeneral ev : events) {
	    ev.reset();
	}
    }


    protected void moveToEvent() {
	EventGeneral t = nontriggeredEvents.remove(0);
	if (nontriggeredEvents.size()>0){
	    nontriggeredEvents.get(0).init(getEventsDealer());
	}
        getEventsDealer().logActivation(t);
	trigeredEvents.add(t);
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
	EventCollection other = (EventCollection) obj;
	if (events == null) {
	    if (other.events != null)
		return false;
	} else if (!events.equals(other.events))
	    return false;
	return true;
    }
     

}
