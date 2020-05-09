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

import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public abstract class EventLogicCollection extends EventGroup {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private EventLogicCollectionOperator fEventLogicCollectionOperator;

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return fEventLogicCollectionOperator + "("
				+ aUtil.getHtmlBucketList(getEvents()) + ")";
	}

	/**
	 * @return the eventLogicCollectionOperator
	 */
	public EventLogicCollectionOperator getEventLogicCollectionOperator() {
		return fEventLogicCollectionOperator;
	}

	/**
	 * @param aEventLogicCollectionOperator
	 *            the eventLogicCollectionOperator to set
	 */
	public void setEventLogicCollectionOperator(
			EventLogicCollectionOperator aEventLogicCollectionOperator) {
		fEventLogicCollectionOperator = aEventLogicCollectionOperator;
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		myInit();
		for (EventGeneral ev : events) {
			ev.init(aDealer);
		}
	}

	protected void myInit() {
		// TODO Auto-generated method stub

	}

	public EventLogicCollection() {
		super();
		fEventLogicCollectionOperator = EventLogicCollectionOperator.OR;
	}

	@Override
	public void reset() {
		super.reset();
		myInit();
		for (EventGeneral ev : events) {
			ev.reset();
		}
	}

	// @Override
	// protected void setDiscarded(boolean aDiscarded) {
	// if (aDiscarded)
	// init(null);
	// super.setDiscarded(aDiscarded);
	// }

}
