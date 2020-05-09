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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public class EventAtomExit extends EventAtomCommand implements
		IOrderFilledListener {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private static final String ENTRIES = "Entries";
	private static final String FILTER = "Filter";
	private static final String MARKET = "Market";
	private boolean marketFamily;
	private ArrayList<OrderImpl> entries;
	private int[] fEntries;
	private LSFilterType filterType = LSFilterType.Auto;
	private LSFilterType filterAVGRule = LSFilterType.None;
	private boolean global;
	private boolean containsAutoEntries;
	private boolean containsManualEntries;

	public EventAtomExit() {
		marketFamily = true;
		entries = new ArrayList<>();
		fEntries = new int[0];
	}

	public EventAtomExit(boolean aMarketFamily, boolean aGlobal,
			LSFilterType aFilterType) {
		this();
		this.marketFamily = aMarketFamily;
		this.global = aGlobal;
		this.filterType = aFilterType;
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		closeEntries(aDealer);
		aDealer.clearMyEventThreads(this.getParentEvent());
	}

	protected int geteCount(EventsDealer aDealer, ORDER_TYPE type) {
		switch (filterAVGRule) {
		case Auto:
			return aDealer.getLongOpened() + aDealer.getShortOpened();
			// else
			// return aDealer.getOpenedEntriesCount(type);
			// $CASES-OMITTED$
		default:
			if (filterAVGRule.matchEntry(type)) {
				return aDealer.getOpenedEntriesCount(type);
			}
			return 0;
		}
	}

	public boolean hasAveragingLock() {
		ArrayList<EventAtomEntry> _entries = collectEntries(global, fEntries);
		for (EventAtomEntry e : _entries) {
			ORDER_TYPE type = e.getOrder().getType();
			if (e.isTriggered() && getFilterType().matchEntry(type)) {
				if (e.isFilled()
						&& getEventsDealer().getFilledEntries().contains(
								e.getOrder())) {
					if (geteCount(getEventsDealer(), type) > 1
							&& filterAVGRule.matchEntry(type))
						return true;
				} else {
					// DO NOTHING
				}
			}
		}
		return false;
	}

	private void closeEntries(EventsDealer aDealer) {
		LSFilterType currentFilter = getFilterType();
		if (containsManualEntries) {
			aDealer.closeManualPosition(global, marketFamily, this);
		}
		if (containsAutoEntries) {
			if (currentFilter == LSFilterType.Auto) {
				ArrayList<EventAtomEntry> _entries = collectEntries(false,
						fEntries);
				if (_entries.size() > 0) {
					EventAtomEntry myEntry = _entries.get(0);
					OrderImpl order = myEntry.getOrder();
					if (order == null)
						return;
					if (order.isLong())
						currentFilter = LSFilterType.Long;
					else
						currentFilter = LSFilterType.Short;
				}
			}
			ArrayList<EventAtomEntry> _entries = collectEntries(global,
					fEntries);
			for (EventAtomEntry e : _entries) {
				if (e.isTriggered()
						&& currentFilter.matchEntry(e.getOrder().getType())) {
					if (e.isFilled()
							&& aDealer.getFilledEntries()
									.contains(e.getOrder())) {
						proccessEntry(aDealer, e.getOrder());
					} else {
						if (!e.isFilled()) {
							PortfolioStrategy portfolio = getEventsDealer()
									.getTheStrategy().getPortfolio();
							if (portfolio.getConfirmOrder() == null) {
								aDealer.logDiscarding(e.getParentEvent(),
										"exit arrived before entering");
								getEventsDealer().cancelOrCloseIfFilled(e);
							} else {
								portfolio.discardOrderConfirmation();
							}
						}
					}
				}
			}
		}
	}

	private boolean checkContainsEntries() {
		containsAutoEntries = false;
		containsManualEntries = false;
		if (fEntries.length == 0) {
			containsAutoEntries = true;
			containsManualEntries = true;
		}
		for (int i = 0; i < fEntries.length; i++) {
			int e = fEntries[i];
			if (e > 0)
				containsAutoEntries = true;
			if (e == 0)
				containsManualEntries = true;
		}
		return false;
	}

	@Override
	public void preinit(EventsDealer aDealer) {
		super.preinit(aDealer);
		Arrays.sort(this.fEntries);
		ArrayList<EventAtomEntry> _entries = collectEntries(global, fEntries);
		for (EventAtomEntry e : _entries) {
			e.setInternalIncludingLimitChild(e.isInternalIncludingLimitChild()
					|| !marketFamily);
		}
	}

	private void proccessEntry(EventsDealer aDealer, OrderImpl entry) {
		entries.add(entry);
		closeEntry(entry, aDealer);
	}

	private void closeEntry(IOrderMfg aEntry, EventsDealer aDealer) {
		aDealer.closePosition(aEntry, marketFamily, this);
		Math.abs(aEntry.getQuantity());
	}

	/**
	 * @return the marketFamily
	 */
	// @JSON
	public boolean isMarketFamily() {
		return marketFamily;
	}

	/**
	 * @param aMarketFamily
	 *            the marketFamily to set
	 */
	public void setMarketFamily(boolean aMarketFamily) {
		marketFamily = aMarketFamily;
	}

	@Override
	public String getHtmlBody(HtmlUtils aUtil) {
		return (global ? "Global " : "") + "Exit{"
				+ HtmlUtils.getText(isMarketFamily(), "Market", "Limit") + "}"
				+ getRest();
	}

	private String getRest() {
		return (fEntries.length == 0 ? "*" : (" of " + Arrays
				.toString(fEntries)))
				+ (getFilterType() == LSFilterType.Auto ? ""
						: (" " + getFilterType()));
	}

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		setTriggered(true);
		return true;
	}

	public void addEntry(OrderImpl entry) {
		entries.add(entry);
	}

	@Override
	public void orderFilled(IExecutionReport aReport) {
		Math.abs(aReport.getQuantity());
		// setTriggered(totalQ == 0);
		IOrderMfg parent = aReport.getOrder().getParent();
		getEventsDealer().removeFilledEntry(parent);
		// getEventsDealer().getFilledEntries().remove(parent);
		logExit();
	}

	private void logExit() {
		// not used
		// EventCommandContainer cc = (EventCommandContainer) getParentEvent();
		// IExecutionLog log = getEventsDealer().getLogger();
		// // if (log.isEnabled(EMessageType.Comment)) {
		// // StrategyMessage m = new
		// StrategyMessage(StrategyMessageType.Comment, "<html><body>(PatID=" +
		// getBirthID() + ") Exit because of:</br>" +
		// // cc.getHtmlBody(new HtmlUtils(true, true)) + "</body></html>",
		// "Automatic");
		// // log.log(m);
		// // }
	}

	@Override
	public boolean isTiedToCloseCommand() {
		return false;
	}

	@Override
	public String getLabel() {
		return "EXIT" + getRest();
	}

	/**
	 * @return the entries
	 */
	// @JSON
	public int[] getEntries() {
		return fEntries;
	}

	/**
	 * @param aEntries
	 *            the entries to set
	 */
	public void setEntries(int[] aEntries) {
		fEntries = aEntries;
		checkContainsEntries();
	}

	// @JSON
	public LSFilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(LSFilterType aFilterType) {
		this.filterType = aFilterType;
	}

	// @JSON
	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean aGlobal) {
		this.global = aGlobal;
	}

	@Override
	public boolean ready2BChecked() {
		return !isExitAveraging || !hasAveragingLock();
	}

	private boolean isExitAveraging;

	@Override
	public void turnAveragingOn(LSFilterType filter) {
		isExitAveraging = true;
		filterAVGRule = filterAVGRule.joinFilter(filter);
	}

	// @Override
	// public boolean isBasedOnBoth(){
	// return filterType==LSFilterType.Both;
	// }

	@Override
	public void setBasedOn(LSFilterType filter) {
		this.setFilterType(filter);
	}

	@Override
	protected void _toJsonEmbedded(JSONStringer stringer) throws JSONException {
		stringer.key(ENTRIES);
		stringer.array();
		for (int i = 0; i < fEntries.length; i++) {
			stringer.value(fEntries[i]);
		}
		stringer.endArray();
		stringer.key(FILTER);
		stringer.value(getFilterType());
		stringer.key(MARKET);
		stringer.value(isMarketFamily());
	}

	/**
	 * This method assumes that the object is already created and it assumes
	 * that the fields must be updated. This method will be called during
	 * deserialization of the object.
	 */
	@Override
	protected void _updateFromJSON(JSONObject json) throws JSONException {
		JSONArray a = json.getJSONArray(ENTRIES);
		fEntries = new int[a.length()];
		for (int i = 0; i < a.length(); i++) {
			fEntries[i] = Integer.parseInt(a.get(i).toString());
		}
		setFilterType((LSFilterType) json.get(FILTER));
		setMarketFamily(json.getBoolean(MARKET));
	}

	@Override
	public boolean isPure(boolean entry) {
		return !entry;
	}

	private boolean _requiresConfirmation = false;
	private boolean _playSound = false;

	public boolean isRequiresConfirmation() {
		return _requiresConfirmation;
	}

	public void setRequiresConfirmation(boolean requiresConfirmation) {
		_requiresConfirmation = requiresConfirmation;
	}

	public boolean isPlaySound() {
		return _playSound;
	}

	public void setPlaySound(boolean playSound) {
		_playSound = playSound;
	}
}
