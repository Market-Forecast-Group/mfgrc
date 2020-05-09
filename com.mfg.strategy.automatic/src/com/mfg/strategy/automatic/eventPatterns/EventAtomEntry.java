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
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.orders.LimitOrder;
import com.mfg.broker.orders.MarketOrder;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.broker.orders.StopLimitOrder;
import com.mfg.broker.orders.StopOrder;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.strategy.automatic.probabilities.EventsPatternProbabilitiesStrategy;
import com.mfg.utils.MathUtils;
import com.mfg.utils.ui.HtmlUtils;

public class EventAtomEntry extends EventAtomOrder {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private static final String ID = "EntryID";
	private static final String MARKET = "EntryMarket";
	private static final String QUANTITY = "EntryQ";
	private static final String MULENTRIES = "EntryMultEntr";
	private static final String SINGLEENTRIES = "SinglEntries";
	private static final String SLSIMPLEPROTECT = "SP";
	private static final String LIMITCHILD = "Limit";
	private static final String SLSIMPLEPROTECTSCALE = "SPScale";
	private boolean fContrarian;
	private int fQuantity;
	private double Qfactor;
	private int maxQ = 1000;
	private LSFilterType factorFilter;
	private boolean mixingLS;
	private boolean reEnteringInTheSameDir;
	private boolean fMarketFamily;
	private int fWidgetScale;
	private ORDER_TYPE fEntryType;
	private boolean usingSLSimpleProtection;
	private int simpleProtectionScale;
	private boolean includingLimitChild;
	private boolean internalIncludingLimitChild;
	private boolean multipleEntries;
	private int[] singleEntriesScales;
	private boolean filled;
	private boolean probabilistic = false;

	private int fID;

	public EventAtomEntry() {
		// super(null, true);
		fQuantity = 1;
		fWidgetScale = 3;
		simpleProtectionScale = 5;
		usingSLSimpleProtection = true;
		fMarketFamily = true;
		setID(0);
		reEnteringInTheSameDir = true;
		factorFilter = LSFilterType.Auto;
		Qfactor = 1;
		singleEntriesScales = new int[0];
		fContrarian = true;
	}

	public EventAtomEntry(int aID) {
		this();
		setID(aID);
	}

	@Override
	public void cancelThisEvent() {
		if (isTriggered() && !isFilled())
			cancelOrder();
	}

	public EventAtomEntry(int aQuantity, boolean aMarketFamily, int aWidgetScale) {
		// super(null, true);
		fQuantity = aQuantity;
		fMarketFamily = aMarketFamily;
		fWidgetScale = aWidgetScale;
	}

	public EventAtomEntry(boolean aContrarian, int aQuantity,
			boolean aMarketFamily, int aWidgetScale) {
		// super(null, true);
		fContrarian = aContrarian;
		fQuantity = aQuantity;
		fMarketFamily = aMarketFamily;
		fWidgetScale = aWidgetScale;
	}

	@Override
	public void preinit(EventsDealer aDealer) {
		super.preinit(aDealer);
		computeScale();
	}

	@Override
	public void init(EventsDealer aDealer) {
		if (aDealer != null) {
			filled = false;
			closed = false;
			computeScale();
			logScale();
			boolean preconditions = (!usingSLSimpleProtection && !includeLimitChild())
					|| aDealer.getTHCount(simpleProtectionScale) > 1;
			if (preconditions) {
				super.init(aDealer);
				getEventsDealer().trackEntryAttempt(this);
				setTHS();
			} else
				setDiscarded(true);
		}
	}

	protected int geteCount(EventsDealer aDealer, ORDER_TYPE type) {
		switch (factorFilter) {
		case Auto:
			if (mixingLS)
				return aDealer.getLongOpened() + aDealer.getShortOpened();
			return aDealer.getOpenedEntriesCount(type);
			// $CASES-OMITTED$
		default:
			if (factorFilter.matchEntry(type)) {
				return aDealer.getOpenedEntriesCount(type);
			}
			return 0;
		}
	}

	private void logScale() {
		// not used
		// IExecutionLog llogger = getEventsDealer().getLogger();
		// if (llogger != null) {
		// // if (llogger.isEnabled(EMessageType.Comment)) {
		// // PatternStrategyMessage m = new
		// // PatternStrategyMessage(getBirthID(),
		// // StrategyMessageType.HTMLComment, "Assuming basescale=" +
		// // fWidgetScale + " for " + this, "Automatic");
		// // llogger.log(m);
		// // }
		// }
	}

	@Override
	protected void sendOrder(EventsDealer aDealer) {
		aDealer.addEntryOrder(getOrder(), this);
	}

	public void computeScale() {
		if (isLinkedToEntry()) {
			EventAtomEntry linkedEntry = getLinkedEntry();
			if (linkedEntry != null)
				fWidgetScale = linkedEntry.getWidgetScale();
		} else
			fWidgetScale = getParentEvent().getScaleTo(this);
	}

	@Override
	protected OrderImpl getAnOrder(EventsDealer aDealer) {
		OrderImpl res = null;
		ORDER_TYPE type = getAType(aDealer);
		int theQ = getQuantity();
		theQ = computeQ(theQ, type) * OrderUtils.getSign(type);
		int currentPrice = aDealer.getWidget().getCurrentPrice();
		if (isMarketFamily())
			res = new MarketOrder(aDealer.getNextOrderId(), type, theQ);
		else
			res = new LimitOrder(aDealer.getNextOrderId(), type, theQ,
					currentPrice);
		if (type == ORDER_TYPE.BUY) {
			res.setAccountRouting(EAccountRouting.LONG_ACCOUNT);
		} else {
			res.setAccountRouting(EAccountRouting.SHORT_ACCOUNT);
		}
		if (usingSLSimpleProtection || includeLimitChild()) {
			double linearSwing = aDealer.getWidget().getLastPivot(0,
					simpleProtectionScale).fLinearSwing;
			long slPrice = OrderUtils.moveToNonExecutingDirection(
					EXECUTION_TYPE.STOP, OrderUtils.getOpposite(type),
					currentPrice, linearSwing * 4);
			int tickSize = getEventsDealer().getTickSize();
			if (usingSLSimpleProtection) {
				if (slPrice > currentPrice) {
					slPrice = (long) MathUtils.normalizeDownUsingStep(slPrice,
							tickSize, 1);
				} else {
					slPrice = (long) MathUtils.normalizeUpUsingStep(slPrice,
							tickSize, 1);
				}
				StopOrder child = new StopOrder(aDealer.getNextOrderId(),
						OrderUtils.getOpposite(type), -theQ, slPrice);
				// child.setChildType(OrderChildType.STOP_LOSS);
				res.setStopLoss(child);
			}
			if (includeLimitChild()) {
				int tpPrice = OrderUtils.moveToNonExecutingDirection(
						EXECUTION_TYPE.STOP, OrderUtils.getOpposite(type),
						slPrice, 8 * tickSize);
				StopLimitOrder child = new StopLimitOrder(
						aDealer.getNextOrderId(), OrderUtils.getOpposite(type),
						-theQ, tpPrice, tpPrice);
				// child.setChildType(OrderChildType.TAKE_PROFIT);
				res.setTakeProfit(child);
			}
		}
		getParentEvent().suggestChildren(res);
		return res;
	}

	protected boolean includeLimitChild() {
		return includingLimitChild || internalIncludingLimitChild;
	}

	public ORDER_TYPE getAType(EventsDealer aDealer) {
		if (isLinkedToEntry()) {
			EventAtomEntry e = getLinkedEntry();
			fEntryType = e.getEntryType();
			if (isContrarian())
				fEntryType = OrderUtils.getOpposite(fEntryType);
		} else {
			fEntryType = !aDealer.getWidget().isSwingDown(fWidgetScale) ? ORDER_TYPE.BUY
					: ORDER_TYPE.SELL;
			if (isProbabilistic()) {
				EventsPatternProbabilitiesStrategy theStrategy = (EventsPatternProbabilitiesStrategy) getEventsDealer()
						.getTheStrategy();
				if (!theStrategy.getProbabilitiesDealer()
						.getTDInfo(fWidgetScale, true)
						.isPositiveTradeDirection())
					fEntryType = OrderUtils.getOpposite(fEntryType);
				// theStrategy.logBestTD(fWidgetScale);
			} else {
				if (isContrarian())
					fEntryType = OrderUtils.getOpposite(fEntryType);
			}
		}
		return fEntryType;
	}

	private EventAtomEntry getLinkedEntry() {
		ArrayList<EventAtomEntry> list = collectEntries(false,
				new int[] { getEntryLinkID() });
		if (list == null || list.size() == 0)
			return null;
		EventAtomEntry e = list.get(0);
		return e;
	}

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		setTriggered(true);
		return super.checkIFTriggered(aDealer);
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

	public boolean isStrillOnSwing0() {
		return ths == getEventsDealer().getWidget().getCurrentPivotsCount(
				getWidgetScale());
	}

	private boolean limitToSwingZero;
	private int ths;

	public void setTHS() {
		ths = getEventsDealer().getWidget().getCurrentPivotsCount(
				getWidgetScale());
	}

	@Override
	public void orderFilled(IExecutionReport aReport) {
		super.orderFilled(aReport);
		setFilled(true);
		getEventsDealer().trackEntry(this);
		logEntry();
	}

	@Override
	public boolean ready2BChecked() {
		computeScale();
		if (isLinkedToEntry()) {
			EventAtomEntry linkedEntry = getLinkedEntry();
			return linkedEntry != null && linkedEntry.isFilled();
		}
		EventsDealer dealer = getEventsDealer();
		getAType(dealer);
		return (multipleEntries || dealer.isClear(this))
				&& (reEnteringInTheSameDir || dealer.isFree(fEntryType))
				&& (dealer.isClear(this, singleEntriesScales))
				&& !dealer.isSendingBlocked() && !dealer.isWarmingUp();
	}

	private boolean closed;
	private boolean linkedToEntry;
	private int entryLinkID;

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean aClosed) {
		this.closed = aClosed;
	}

	public boolean isFilled() {
		return filled;
	}

	public void setFilled(boolean aFilled) {
		this.filled = aFilled;
	}

	/**
	 * @return the entryType
	 */
	public ORDER_TYPE getEntryType() {
		return fEntryType;
	}

	/**
	 * @return the contrarian
	 */
	// @JSON
	public boolean isContrarian() {
		return fContrarian;
	}

	/**
	 * @param aContrarian
	 *            the contrarian to set
	 */
	public void setContrarian(boolean aContrarian) {
		fContrarian = aContrarian;
	}

	/**
	 * @return the quantity
	 */
	// @JSON
	public int getQuantity() {
		return fQuantity;
	}

	/**
	 * @param aQuantity
	 *            the quantity to set
	 */
	public void setQuantity(int aQuantity) {
		fQuantity = aQuantity;
	}

	/**
	 * @return the marketFamily
	 */
	// @JSON
	public boolean isMarketFamily() {
		return fMarketFamily;
	}

	/**
	 * @param aMarketFamily
	 *            the marketFamily to set
	 */
	public void setMarketFamily(boolean aMarketFamily) {
		fMarketFamily = aMarketFamily;
	}

	/**
	 * @return the widgetScale
	 */
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

	public void setLinkedToEntry(boolean aLinkedToEntry) {
		this.linkedToEntry = aLinkedToEntry;
	}

	public void setEntryLinkID(int aEntryLinkID) {
		this.entryLinkID = aEntryLinkID;
	}

	public boolean isLinkedToEntry() {
		return linkedToEntry;
	}

	public int getEntryLinkID() {
		return entryLinkID;
	}

	/**
	 * @return the usingSLSimpleProtection
	 */
	// @JSON
	public boolean isUsingSLSimpleProtection() {
		return usingSLSimpleProtection;
	}

	/**
	 * @param aUsingSLSimpleProtection
	 *            the usingSLSimpleProtection to set
	 */
	public void setUsingSLSimpleProtection(boolean aUsingSLSimpleProtection) {
		usingSLSimpleProtection = aUsingSLSimpleProtection;
	}

	/**
	 * @return the simpleProtectionScale
	 */
	// @JSON
	public int getSimpleProtectionScale() {
		return simpleProtectionScale;
	}

	/**
	 * @param aSimpleProtectionScale
	 *            the simpleProtectionScale to set
	 */
	public void setSimpleProtectionScale(int aSimpleProtectionScale) {
		simpleProtectionScale = aSimpleProtectionScale;
	}

	/**
	 * @return the includingLimitChild
	 */
	// @JSON
	public boolean isIncludingLimitChild() {
		return includingLimitChild;
	}

	/**
	 * @param aIncludingLimitChild
	 *            the includingLimitChild to set
	 */
	public void setIncludingLimitChild(boolean aIncludingLimitChild) {
		includingLimitChild = aIncludingLimitChild;
	}

	public boolean isInternalIncludingLimitChild() {
		return internalIncludingLimitChild;
	}

	public void setInternalIncludingLimitChild(
			boolean aInternalIncludingLimitChild) {
		this.internalIncludingLimitChild = aInternalIncludingLimitChild;
	}

	// @JSON
	public double getQfactor() {
		return Qfactor;
	}

	public void setQfactor(double aQfactor) {
		this.Qfactor = aQfactor;
	}

	// @JSON
	public int getMaxQ() {
		return maxQ;
	}

	public void setMaxQr(int aMaxQ) {
		this.maxQ = aMaxQ;
	}

	// @JSON
	public LSFilterType getFactorFilter() {
		return factorFilter;
	}

	public void setFactorFilter(LSFilterType aFactorFilter) {
		this.factorFilter = aFactorFilter;
	}

	// @JSON
	public boolean isReEnteringInTheSameDir() {
		return reEnteringInTheSameDir;
	}

	public void setReEnteringInTheSameDir(boolean aReEnteringInTheSameDir) {
		this.reEnteringInTheSameDir = aReEnteringInTheSameDir;
	}

	public boolean isOnly1TradeInTheSameDir() {
		return !isReEnteringInTheSameDir();
	}

	public void setOnly1TradeInTheSameDir(boolean Only1TradeInTheSameDir) {
		setReEnteringInTheSameDir(!Only1TradeInTheSameDir);
	}

	// @JSON
	public boolean isMixingLS() {
		return mixingLS;
	}

	public void setMixingLS(boolean aMixingLS) {
		this.mixingLS = aMixingLS;
	}

	// @JSON
	public int[] getSingleEntriesScales() {
		return singleEntriesScales;
	}

	public void setSingleEntriesScales(int[] aSingleEntriesScales) {
		this.singleEntriesScales = aSingleEntriesScales;
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return "Entry("
				+ getID()
				+ "){"
				+ HtmlUtils.getText(fContrarian, "Contr, ", "")
				+ "Q="
				+ getQuantity()
				+ ", "
				+ HtmlUtils.getText(fMarketFamily, "Market", "Limit")
				+ (multipleEntries ? (", Multiple Entries") : "")
				+ (usingSLSimpleProtection ? (", SimpleProtection SL("
						+ simpleProtectionScale + ")") : "")
				+ (includingLimitChild ? (", Limit child") : "")
				+ (limitToSwingZero ? ", On Sw0" : "")
				+ ((singleEntriesScales != null && singleEntriesScales.length > 0) ? (", Single Entries on: " + Arrays
						.toString(singleEntriesScales)) : "") + "}";
	}

	@Override
	public String getLabel() {
		return "ENTRY " + fID;
	}

	@Override
	public void getEntriesTo(EventGeneral aRequester,
			List<EventAtomEntry> aEntries, int[] IDs, boolean global) {
		if (IDs.length == 0 || Arrays.binarySearch(IDs, fID) >= 0) {
			List<EventAtomEntry> list = getEventsDealer().getEntriesIDTable()
					.get(Integer.valueOf(fID));
			if (list == null)
				aEntries.add(this);
			else {
				if (global) {
					aEntries.addAll(list);
				} else {
					for (EventAtomEntry eventAtomEntry : list) {
						if (sameGeneration(this, eventAtomEntry)) {
							aEntries.add(eventAtomEntry);
						}
					}
				}
			}
		}
	}

	private static boolean sameGeneration(EventGeneral eventGeneral,
			EventGeneral ev) {
		return eventGeneral.getRoot() == ev.getRoot();
	}

	public double getGain() {
		if (!isTriggered())
			return 0;
		return -OrderUtils.getSign(fEntryType)
				* getQuantity()
				* (getExecutionPrice() - getEventsDealer().getWidget()
						.getCurrentPrice());
	}

	public double getPlainGain() {
		if (!isTriggered())
			return 0;
		return -OrderUtils.getSign(fEntryType)
				* (getExecutionPrice() - getEventsDealer().getWidget()
						.getCurrentPrice());
	}

	/**
	 * @return the iD
	 */
	// @JSON
	public int getID() {
		return fID;
	}

	/**
	 * @param aID
	 *            the iD to set
	 */
	public void setID(int aID) {
		fID = aID;
	}

	// @JSON
	public boolean isMultipleEntries() {
		return multipleEntries;
	}

	public void setMultipleEntries(boolean aMultipleEntries) {
		this.multipleEntries = aMultipleEntries;
	}

	public boolean isProbabilistic() {
		return probabilistic;
	}

	public void setProbabilistic(boolean aProbabilistic) {
		probabilistic = aProbabilistic;
	}

	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		stringer.key(ID);
		stringer.value(getID());
		stringer.key(MARKET);
		stringer.value(isMarketFamily());
		stringer.key(QUANTITY);
		stringer.value(getQuantity());
		stringer.key(SINGLEENTRIES);
		stringer.array();
		for (int i = 0; i < singleEntriesScales.length; i++) {
			stringer.value(singleEntriesScales[i]);
		}
		stringer.endArray();
		stringer.key(MULENTRIES);
		stringer.value(isMultipleEntries());
		stringer.key(SLSIMPLEPROTECT);
		stringer.value(isUsingSLSimpleProtection());
		stringer.key(SLSIMPLEPROTECTSCALE);
		stringer.value(getSimpleProtectionScale());
		stringer.key(LIMITCHILD);
		stringer.value(isIncludingLimitChild());
	}

	/**
	 * This method assumes that the object is already created and it assumes
	 * that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		JSONArray a = json.getJSONArray(SINGLEENTRIES);
		singleEntriesScales = new int[a.length()];
		for (int i = 0; i < a.length(); i++) {
			singleEntriesScales[i] = Integer.parseInt(a.get(i).toString());
		}
		setID(json.getInt(ID));
		setMarketFamily(json.getBoolean(MARKET));
		setQuantity(json.getInt(QUANTITY));
		setMultipleEntries(json.getBoolean(MULENTRIES));
		setUsingSLSimpleProtection(json.getBoolean(SLSIMPLEPROTECT));
		setSimpleProtectionScale(json.getInt(SLSIMPLEPROTECTSCALE));
		setIncludingLimitChild(json.getBoolean(LIMITCHILD));
	}

	@Override
	public void getDelays(int[] delays) {
		if (isIncludingLimitChild() || isUsingSLSimpleProtection())
			delays[simpleProtectionScale] = Math.max(
					delays[simpleProtectionScale], 2);
	}

	@Override
	public void setPresentScales(boolean[] scales) {
		if (getWidgetScale() > -1)
			scales[getWidgetScale()] = true;
		if (usingSLSimpleProtection)
			scales[getSimpleProtectionScale()] = true;
		for (int b : singleEntriesScales) {
			scales[b] = true;
		}
	}

	private void logEntry() {
		// not used
		// EventCommandContainer cc = (EventCommandContainer) getParentEvent();
		// IExecutionLog log = getEventsDealer().getLogger();
		// // if (log.isEnabled(EMessageType.Comment)) {
		// // StrategyMessage m = new
		// StrategyMessage(StrategyMessageType.Comment,
		// // "<html><body>(PatID=" + getBirthID() + ") Entry because of [" +
		// // getID() + "]:</br>" + cc.getHtmlBody(new HtmlUtils(true, true)) +
		// // "</body></html>", "Automatic");
		// // log.log(m);
		// // }
	}

	@Override
	public boolean gotEntry() {
		return isTriggered();
	}

	@Override
	public EventAtomCommand clone() {
		EventAtomEntry e = (EventAtomEntry) super.clone();
		if (singleEntriesScales != null) {
			e.singleEntriesScales = singleEntriesScales.clone();
		} else {
			e.singleEntriesScales = new int[0];
			singleEntriesScales = new int[0];
		}
		return e;
	}

	private int computeQ(int theQ, ORDER_TYPE oRDER_TYPE) {
		double res = theQ;
		for (int i = 0; i < geteCount(getEventsDealer(), oRDER_TYPE); i++) {
			res = res * Qfactor;
			if (res > maxQ) {
				return maxQ;
			}
		}
		return (int) res;
	}

	@Override
	public boolean isPure(boolean entry) {
		return entry;
	}
}
