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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.strategy.ProbabilitiesTD;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.strategy.automatic.probabilities.EventsPatternProbabilitiesStrategy;
import com.mfg.utils.ui.HtmlUtils;

/**
 * represents the probability event for exits(ExitProb). It monitors the SL and
 * TP of the best TD and in case it is reached or the direction of the winning
 * TD changes it is triggered.
 * <ol>
 * <li>The Widget scale parameter defines at which scale to consider TH.
 * </ol>
 * 
 * @author gardero
 */
public class EventAtomExitProb extends EventAtom {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static final String SCALE = "SCALE";

	private EventsPatternProbabilitiesStrategy strategy;

	//private boolean invert;

	private boolean longTradeDirInitial;

	public EventAtomExitProb() {
		super();
		fWidgetScale = 3;
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		strategy = (EventsPatternProbabilitiesStrategy) aDealer
				.getTheStrategy();
		getTDInfo(fWidgetScale);
		theTDInfo.isPositiveTradeDirection();
		longTradeDirInitial = getLongTradeDir();
		//invert = getNegativeTarget() > getPositiveTarget();
	}

	private boolean getLongTradeDir() {
		boolean res = !getEventsDealer().getWidget().isSwingDown(
				fWidgetScale);
		return theTDInfo.isPositiveTradeDirection() ? res : !res;
	}

	private int fWidgetScale;

	private ProbabilitiesTD theTDInfo;

	/**
	 * checks if it in a TH after skipping all the THs that it needs to skip, it
	 * will never be discarded because we always keep the hope to reach the n-th
	 * TH.
	 * 
	 * @param aDealer
	 *            the events dealer.
	 * @return {@code true} if there is a new pivot.
	 */
	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		IIndicator widget = aDealer.getWidget();
		if (!widget.isLevelInformationPresent(fWidgetScale))
			return false;
		getTDInfo(fWidgetScale);
		// boolean isNewPivot = widget.isThereANewPivot(fWidgetScale);
		// if (isNewPivot)
		// invert = !invert;
		double negativeTarget = getNegativeTarget();
		double positiveTarget = getPositiveTarget();
		double max = Math.max(negativeTarget, positiveTarget);
		double min = Math.min(negativeTarget, positiveTarget);
		double currentPrice = widget.getCurrentPrice();
		if (currentPrice >= max || currentPrice <= min)
			setTriggered(true);
		if (getLongTradeDir() != longTradeDirInitial)
			setTriggered(true);
		return isTriggered();
	}

	private double getPositiveTarget() {
		return theTDInfo.getBestPPrice();
	}

	private double getNegativeTarget() {
		return theTDInfo.getBestNPrice();
	}

	private double getTDInfo(int aWidgetScale) {
		theTDInfo = strategy.getProbabilitiesDealer().getTDInfo(aWidgetScale,
				true);
		return Math.max(theTDInfo.getBestNTD(), theTDInfo.getBestPTD());
	}

	/**
	 * @return the widgetScale
	 */
	// @JSON
	public int getWidgetScale() {
		return fWidgetScale;
	}

	/**
	 * @param aWidgetScale
	 *            the widgetScale to set
	 */
	public void setWidgetScale(int aWidgetScale) {
		fWidgetScale = aWidgetScale;
	}

	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		stringer.key(SCALE);
		stringer.value(getWidgetScale());
	}

	/**
	 * This method assumes that the object is already created and it assumes
	 * that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		setWidgetScale(json.getInt(SCALE));
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "ExitProb(scale=" + getWidgetScale() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fWidgetScale;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventAtomExitProb other = (EventAtomExitProb) obj;
		if (fWidgetScale != other.fWidgetScale)
			return false;
		return true;
	}

	@Override
	public String getLabel() {
		return getHtmlBody(HtmlUtils.Plain);
	}

	@Override
	public int getScaleTo(EventGeneral aRequester) {
		return fWidgetScale;
	}

	@Override
	public void setPresentScales(boolean[] scales) {
		scales[getWidgetScale()] = true;
	}

	@Override
	public int getBigEntryScale() {
		return fWidgetScale;
	}

}
