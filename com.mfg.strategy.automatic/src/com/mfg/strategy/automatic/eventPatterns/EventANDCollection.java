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


public class EventANDCollection extends EventLogicCollection {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int depth;
    private int oldDepth;
 

    public EventANDCollection() {
	super();
	this.setEventLogicCollectionOperator(EventLogicCollectionOperator.AND);
    }

    @Override
    public void init(EventsDealer aDealer) {
        super.init(aDealer);
        depth = 0;
        oldDepth = 0;
    }

    /**
     * we keep a field named oldDepth that tells us how many events did we got 
     * (the last time we checked) triggered before getting a non-triggered event. 
     * And in each check if the number of events we are checking is greater 
     * than the oldDepth value it initializes that event before checking the status.  
     * If during the check all events got triggered (this is the logic that 
     * will be implemented soon, currently it is based on the isActive flag 
     * that will be removed) we can say that the AND event is triggered, 
     * if one of them got discarded we can say that it is discarded. 
     * Before returning we update the oldDepth value to the number of 
     * events we got triggered before the first non-triggered event so next 
     * time we check we have the value we got on this check. 
     * The flag isMatchingEvents() is set on when the one of the events turns its flag on.
     * @param aDealer the events dealer.
     * @return {@code true} iff at least one of the events checked returned 
     * true as the check return.
     */
    @Override
    public boolean checkIFTriggered(EventsDealer aDealer) {
	boolean counted = false, allTriggered = true;
        depth = 0;
	for (EventGeneral ev : events) {
            if (oldDepth<depth){
                ev.init(aDealer);
                oldDepth = depth;
            }
	    counted |= checkEvent(ev, aDealer);
            setMatchingEvents(isMatchingEvents() || ev.isMatchingEvents());
	    allTriggered &= ev.isTriggered();
            if (ev.isDiscarded()){
                setDiscarded(true);
                return counted;
            }
            if (!allTriggered){
                oldDepth = depth;
                return counted;
            }
            depth++;
	}
        oldDepth = depth;
	setTriggered(allTriggered);
	return counted;
    }

    
    @Override
    public String getLabel() {
        return "AND";
    }
}
