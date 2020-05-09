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

import com.mfg.broker.IExecutionReport;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.broker.orders.LimitOrder;
import com.mfg.broker.orders.MarketOrder;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.broker.orders.OrderUtils;
import com.mfg.strategy.automatic.EventsDealer;
import com.mfg.utils.ui.HtmlUtils;

public class EventAtomCloseAll extends EventAtomCommand implements
		IOrderFilledListener {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private boolean marketFamily;
	private ArrayList<OrderImpl> entries;
	private int totalQ;

	public EventAtomCloseAll() {
		marketFamily = true;
		entries = new ArrayList<>();
	}

	@Override
	public void init(EventsDealer aDealer) {
		super.init(aDealer);
		totalQ = 0;
		closeEntries(aDealer);
	}

	private void closeEntries(EventsDealer aDealer) {
		for (OrderImpl entry : aDealer.getFilledEntries()) {
			entries.add(entry);
			closeEntry(entry, aDealer);
		}
		aDealer.clearFilledEntries();
	}

	private void closeEntry(IOrderMfg aEntry, EventsDealer aDealer) {
		int quantity = -aEntry.getQuantity();
		int currentPrice = aDealer.getWidget().getCurrentPrice();
		OrderImpl child = null;
		ArrayList<IOrderMfg> children = aEntry.getChildren();
		if (children.size() > 0) {
			for (IOrderMfg child1 : children) {
				if (OrderUtils.isMarketFamily(child1.getExecType()) == marketFamily) {
					child = (OrderImpl) child1;
					child.turnIntoMarket(currentPrice, aDealer.getTickSize());
					break;
				}
			}
		}
		if (child == null) {
			ORDER_TYPE type = OrderUtils.getOpposite(aEntry.getType());
			if (marketFamily) {
				child = new MarketOrder(aDealer.getNextOrderId(), type,
						quantity);
			} else {

				child = new LimitOrder(aDealer.getNextOrderId(), type,
						quantity, currentPrice);
			}
			child.setAccountRouting(aEntry.getAccountRouting());
		}
		totalQ += Math.abs(quantity);
		aDealer.addOrder(child, this);
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
		return "Close All{"
				+ HtmlUtils.getText(isMarketFamily(), "Market", "Limit") + "}";
	}

	@Override
	public boolean checkIFTriggered(EventsDealer aDealer) {
		return true;
	}

	public void addEntry(OrderImpl entry) {
		entries.add(entry);
	}

	@Override
	public void orderFilled(IExecutionReport aReport) {
		totalQ -= Math.abs(aReport.getQuantity());
		setTriggered(totalQ == 0);
	}

	@Override
	public boolean isTiedToCloseCommand() {
		return false;
	}

	@Override
	public String getLabel() {
		return "CLOSE ALL";
	}

	@Override
	public boolean ready2BChecked() {
		return true;
	}

	@Override
	public boolean isPure(boolean entry) {
		return !entry;
	}
}
