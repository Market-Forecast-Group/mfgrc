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

public class EventXORCollection extends EventLogicCollection {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int lastScale;

    public EventXORCollection() {
	super();
	setEventLogicCollectionOperator(EventLogicCollectionOperator.XOR);
    }
    
    @Override
    public void init(EventsDealer aDealer) {
        super.init(aDealer);
        lastScale = -1;
    }
    
    /**
     * checks the list of events when one of the events sets on the 
     * isMatchingEvents() flag it takes that one to be the main event of the 
     * check (it also sets its isMatchingEvents() flag on), and the other 
     * events won’t be checked anymore.  From that moment on the event is 
     * triggered if that chosen event is triggered, and it is discarded 
     * if that event is discarded. 
     * @param aDealer the events dealer.
     * @return {@code true} iff at least one of the events checked returned 
     * true as the check return. in case we are only checking the main 
     * event we return the value that the check if that event returns.
     */
    @Override
    public boolean checkIFTriggered(EventsDealer aDealer) {
	boolean signal = false, disc = true;
	if (winner == null) {
	    boolean done = false;
	    for (EventGeneral ev : events) {
		if (!done) {
                    signal |= checkEvent(ev, aDealer);
                    disc &= ev.isDiscarded();
		    if (ev.isMatchingEvents()) {  
			setMatchingEvents(true);
                        winner = ev;
			updateWinner();
			break;
		    }
		}
		done |= ev.isTriggered();
	    }
            setDiscarded(disc);
            if (disc)
                return signal;
	    setTriggered(done);
	} else {
	    signal = checkEvent(winner, aDealer);
	    updateWinner();
	}
	return signal || isTriggered();
    }

    private void updateWinner() {
	setTriggered(winner.isTriggered());
        setDiscarded(winner.isDiscarded());
	if (winner.isTriggered())
	    lastScale = winner.getScaleTo(this);
    }

    @Override
    public void reset() {
	super.reset();
	winner = null;
    }

    private EventGeneral winner;

    @Override
    public String getLabel() {
	return "XOR";
    }
    
    @Override
    public int getScaleTo(EventGeneral aRequester) {
        return lastScale;
    }
}
