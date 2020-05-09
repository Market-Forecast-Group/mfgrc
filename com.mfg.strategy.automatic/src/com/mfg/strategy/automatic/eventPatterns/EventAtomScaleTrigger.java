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
import com.mfg.strategy.automatic.triggers.ScaleSpecificTrigger;
import com.mfg.strategy.automatic.triggers.Trigger;
import com.mfg.utils.ui.HtmlUtils;

/**
 * represents an event based on a specific scale of the widget.
 * 
 * @author gardero
 * 
 */
public class EventAtomScaleTrigger extends EventAtomTrigger {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private boolean limitToSwingZero;
	private ScaleSpecificTrigger myTrigger;
	private int ths;


	public EventAtomScaleTrigger(ScaleSpecificTrigger aTrigger) {
		super(aTrigger);
		myTrigger = (ScaleSpecificTrigger) getTrigger();
	}


	@Override
	public void setTrigger(Trigger aTrigger) {
		super.setTrigger(aTrigger);
		myTrigger = (ScaleSpecificTrigger) getTrigger();
	}


	@Override
	public void init(EventsDealer aDealer) {
		setEventsDealer(aDealer);
		setTHS();
		myTrigger.setEventAtomTriger(this);
		super.init(aDealer);
	}


	public void setTHS() {
		if (getEventsDealer().getWidget().isLevelInformationPresent(myTrigger.getWidgetScale()))
			ths = getEventsDealer().getWidget().getCurrentPivotsCount(myTrigger.getWidgetScale());
	}


	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		boolean res = super.checkIFTriggered(aDealer);
		boolean sw0flg = isOnRightSwing0();
		setTriggered(isTriggered() && sw0flg);
		setActive(isActive() && sw0flg);
		setDiscarded(!sw0flg);
		return res;
	}


	public boolean isStrillOnSwing0() {
		return getEventsDealer().getWidget().isLevelInformationPresent(myTrigger.getWidgetScale()) && ths == getEventsDealer().getWidget().getCurrentPivotsCount(myTrigger.getWidgetScale());
	}


	public boolean isOnRightSwing0() {
		return !limitToSwingZero || isStrillOnSwing0();
	}


	// @JSON
	public boolean isLimitToSwingZero() {
		return limitToSwingZero;
	}


	public void setLimitToSwingZero(boolean aLimitToSwingZero) {
		this.limitToSwingZero = aLimitToSwingZero;
	}


	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return super.getHtmlBody(aUtil) + (limitToSwingZero ? ", On Sw0" : "");
	}


	@Override
	public int getBigEntryScale() {
		return myTrigger.getWidgetScale();
	}


	@Override
	public void setPresentScales(boolean[] scales) {
		scales[myTrigger.getWidgetScale()] = true;
	}

}
