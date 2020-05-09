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

import com.mfg.broker.orders.LimitOrder;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.broker.orders.StopOrder;
import com.mfg.interfaces.trading.Configuration;
import com.mfg.strategy.ProbabilitiesTD;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.strategy.automatic.probabilities.EventsPatternProbabilitiesStrategy;
import com.mfg.utils.MathUtils;
import com.mfg.utils.ui.HtmlUtils;

/**
 * represents the probability TD event (ProbTD). It occurs when the probability
 * TD reaches an specific level. It also places a TP and a SL according to the
 * most probable targets in positive and negative directions.
 * <ol>
 * <li>The Widget scale parameter defines at which scale to consider TH.
 * </ol>
 * 
 * @author gardero
 */
public class EventAtomProbTD extends EventAtom {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private static final String SCALE = "SCALE";

	private static final String TDTH = "TDTH";

	private double fTDTH;

	private EventsPatternProbabilitiesStrategy strategy;

	public EventAtomProbTD() {
		super();
		fWidgetScale = 3;
	}

	@Override
	public void preinit(EventsDealer aDealer) {
		super.preinit(aDealer);
		strategy = (EventsPatternProbabilitiesStrategy) aDealer
				.getTheStrategy();
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
	}

	private int fWidgetScale;

	private ProbabilitiesTD theTDInfo;

	private boolean logged = false;

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
		if (!aDealer.getWidget().isLevelInformationPresent(fWidgetScale))
			return false;
		if (((EventsPatternProbabilitiesStrategy) getEventsDealer()
				.getTheStrategy()).getProbabilitiesDealer().getElement()[fWidgetScale] == null)
			return false;
		double tdInfo = getTDInfo(fWidgetScale);
		if (fTDTH < 0) {
			setTriggered(true);
		} else {
			setTriggered(tdInfo >= fTDTH);
		}
		if (isTriggered() && !logged) {
			logged = true;
			((EventsPatternProbabilitiesStrategy) getEventsDealer()
					.getTheStrategy()).getProbabilitiesDealer().logInfo(
					fWidgetScale);
		}
		return isTriggered();
	}

	@Override
	public void getDelays(int[] aDelays) {
		Configuration configuration = ((EventsPatternProbabilitiesStrategy) getEventsDealer()
				.getTheStrategy()).getDistribution().getConfiguration();
		aDelays[fWidgetScale] = Math.max(configuration.getMaxRatioLevel() + 3,
				aDelays[fWidgetScale]);
	}

	@Override
	public void suggestChildren(OrderImpl aEntry) {
		int tpprice;
		long slprice;
		if (theTDInfo.isPositiveTradeDirection()) {
			tpprice = (int) theTDInfo.getBestPPrice();
			slprice = (long) theTDInfo.getBestNPrice();
		} else {
			tpprice = (int) theTDInfo.getBestNPrice();
			slprice = (long) theTDInfo.getBestPPrice();
		}
		int ts = getEventsDealer().getTickSize();
		if (tpprice > slprice) {
			tpprice = (int) MathUtils.normalizeDownUsingStep(tpprice, ts, 1);
			slprice = (long) MathUtils.normalizeUpUsingStep(slprice, ts, 1);
		} else {
			tpprice = (int) MathUtils.normalizeUpUsingStep(tpprice, ts, 1);
			slprice = (long) MathUtils.normalizeDownUsingStep(slprice, ts, 1);
		}
		LimitOrder tp = new LimitOrder(strategy.getNextOrderId(),
				OrderUtils.getOpposite(aEntry.getType()),
				-aEntry.getQuantity(), tpprice);
		StopOrder sl = new StopOrder(strategy.getNextOrderId(),
				OrderUtils.getOpposite(aEntry.getType()),
				-aEntry.getQuantity(), slprice);
		aEntry.setTakeProfit(tp);
		aEntry.setStopLoss(sl);
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

	public double getTDTH() {
		return fTDTH;
	}

	public void setTDTH(double aTDTH) {
		fTDTH = aTDTH;
	}

	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		stringer.key(SCALE);
		stringer.value(getWidgetScale());
		stringer.key(TDTH);
		stringer.value(getTDTH());
	}

	/**
	 * This method assumes that the object is already created and it assumes
	 * that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		setWidgetScale(json.getInt(SCALE));
		setTDTH(json.getInt(TDTH));
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "ProbTD(scale=" + getWidgetScale() + thTDText() + ")";
	}

	private String thTDText() {
		if (fTDTH >= 0)
			return ", TD >=" + fTDTH;
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(fTDTH);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		EventAtomProbTD other = (EventAtomProbTD) obj;
		if (Double.doubleToLongBits(fTDTH) != Double
				.doubleToLongBits(other.fTDTH))
			return false;
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
