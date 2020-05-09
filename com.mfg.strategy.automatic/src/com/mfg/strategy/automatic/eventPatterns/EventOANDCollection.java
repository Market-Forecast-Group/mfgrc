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

/**
 *
 * @author gardero
 */
public class EventOANDCollection extends EventLogicCollection{
    
     /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int lastScale;

    public EventOANDCollection() {
        super();
        setEventLogicCollectionOperator(EventLogicCollectionOperator.OAND);
    }
    
    
    
    @Override
    public void init(EventsDealer aDealer) {
        super.init(aDealer);
        lastScale = super.getScaleTo(this);
    }

    /**
     * does a check that is very similar to the check we do in the OR event in 
     * {@link EventXORCollection.checkIfTriggered}, 
     * but when the event gets triggered, the non-triggered events of the list 
     * are added as Threads to the eventsDealer, so they can be checked on later 
     * ticks till the exit comes and they are removed.
     * @param aDealer the events dealer.
     * @return {@code true} iff at least one of the events checked returned 
     * true as the check return.
     */
    @Override
    public boolean checkIFTriggered(EventsDealer aDealer) {
	boolean counted = false;
	boolean triggered = false;
        boolean disc = true; 
	for (EventGeneral ev : events) {
	    counted |= checkEvent(ev, aDealer);
            setMatchingEvents(isMatchingEvents() || ev.isMatchingEvents());
	    triggered |= ev.isTriggered();
            disc &= ev.isDiscarded();
	    if (ev.isTriggered())
		lastScale = ev.getScaleTo(this);
	}
        setDiscarded(disc);
	setTriggered(triggered);
        if (triggered) {
            for (EventGeneral ev : events) {
                if (!ev.isTriggered())
                    aDealer.addEventThread(ev);
            }
        }
        return counted || triggered;
    }
    
    @Override
    public String getLabel() {
        return "OAND";
    }
    
    @Override
    public int getScaleTo(EventGeneral aRequester) {
	return lastScale;
    }

    
}
