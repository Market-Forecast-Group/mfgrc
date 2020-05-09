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

public class EventORCollection extends EventLogicCollection {

   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int lastScale;

    public EventORCollection() {
        super();
        setEventLogicCollectionOperator(EventLogicCollectionOperator.OR);
    }
    
    
    
    @Override
    public void init(EventsDealer aDealer) {
        super.init(aDealer);
        lastScale = -1;
    }

   /**
     * checks the list of events it has and when one of them is triggered we 
     * can say this event will be triggered, if instead all of the events of 
     * the list are discarded we can say that this event will be discarded too. 
     * The flag isMatchingEvents() is set on when the one of the events turns its flag on.
     * @param aDealer the events dealer.
     * @return {@code true} iff at least one of the events checked returned 
     * true as the check return.
     */
    @Override
    public boolean checkIFTriggered(EventsDealer aDealer) {
	boolean signal = false;
	boolean triggered = false;
        boolean disc = true;
	for (EventGeneral ev : events) {
	    signal |= checkEvent(ev, aDealer);
            setMatchingEvents(isMatchingEvents() || ev.isMatchingEvents());
	    triggered |= ev.isTriggered();
            disc &= ev.isDiscarded();
	    if (ev.isTriggered())
		lastScale = ev.getScaleTo(this);
	}
        setDiscarded(disc);
	setTriggered(triggered);
	return signal;
    }
    
    @Override
    public String getLabel() {
        return "OR";
    }
    
    @Override
    public int getScaleTo(EventGeneral aRequester) {
	return lastScale;
    }

}
