/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.mfg.broker.BrokerException;
import com.mfg.broker.IMarketSimulatorListener.EOrderStatus;
import com.mfg.broker.IOrderExec;
import com.mfg.broker.IOrderMfg;
import com.mfg.broker.IOrderStatus;
import com.mfg.broker.MarketSimulator;
import com.mfg.broker.orders.OrderImpl;
import com.mfg.strategy.logger.TradeMessageWrapper;
import com.mfg.utils.Utils;

/**
 * @author arian
 * 
 */
public abstract class FinalStrategy extends AbstractStrategy {

	private static final class StatusOrder {
		@Override
		public String toString() {
			return "[ " + order + " st. " + lastStatus + " ]";
		}

		IOrderMfg order;

		EOrderStatus lastStatus;

		public StatusOrder(IOrderMfg aOrder) {
			order = aOrder;
			lastStatus = EOrderStatus.STILL_IN_APP;
		}
	}

	/**
	 * This map holds all the orders which are sent by the strategy, with their
	 * internal id. The orders are all mixed, there is not a LONG or SHORT
	 * division.
	 */
	private final HashMap<Integer, StatusOrder> ordersMap;

	/**
	 * This value is used to build a new id for every order
	 * <p>
	 * This value was static in the {@link OrderImpl} class, but that meant that
	 * the id was different from every strategy.
	 * <p>
	 * That was a good thing because that id was shared and the market simulator
	 * needed to distinguish different orders, but now we have different
	 * {@link MarketSimulator} objects for different strategies and moreover
	 * {@link TEAOrder} has different means to distinguish an order from the
	 * other, so, for practical purposes, we may have a relative id used only by
	 * the strategy.
	 */
	protected AtomicInteger _nextId = new AtomicInteger(1);

	public FinalStrategy() {
		ordersMap = new HashMap<>();
	}

	@SuppressWarnings("boxing")
	public final void addOrder(IOrderMfg order) {
		if (!isWarmingUp()) {
			StatusOrder so = new StatusOrder(order);

			ordersMap.put(order.getId(), so);

			for (IOrderMfg child : order.getChildren()) {
				so = new StatusOrder(child);
				ordersMap.put(child.getId(), so);
			}
			try {
				((OrderImpl) order).setStrategyId(this.getStrategyName());
				_shell.addOrder(this, order);
			} catch (BrokerException e) {
				e.printStackTrace();
			}
		} else
			Utils.debug_var(452626, "........trade attempt while warming up");
	}

	@Override
	public void begin(int tickSize1) {
		super.begin(tickSize1);
		_nextId.set(1);
	}

	public List<IOrderMfg> getLongOpenedOrders() {
		return _getOpenedOrdersList(true);
	}

	private List<IOrderMfg> _getOpenedOrdersList(boolean isLong) {
		ArrayList<IOrderMfg> res = new ArrayList<>();

		for (StatusOrder so : ordersMap.values()) {

			if (so.order.isChild())
				continue;

			if (isLong ^ so.order.isSentToLongAccount())
				continue;

			if (so.lastStatus == EOrderStatus.TOTAL_FILLED) {
				res.add(so.order);
			}
		}

		return res;
	}

	public int getNextOrderId() {
		return _nextId.getAndIncrement();
	}

	public HashMap<Integer, StatusOrder> getOrdersMap() {
		return ordersMap;
	}

	/**
	 * @return the portfolio
	 */
	public PortfolioStrategy getPortfolio() {
		return (PortfolioStrategy) _shell;
	}

	public List<IOrderMfg> getShortOpenedOrders() {
		return _getOpenedOrdersList(false);
	}

	/**
	 * returns true if this is an closed position, that is an execution that
	 * closes a position.
	 * 
	 * <p>
	 * This is a temporary method mainly used by the {@link PortfolioStrategy}
	 * to generate the entry exit markers.
	 * 
	 * @param anExec
	 * @return
	 */
	@SuppressWarnings("static-method")
	// Maybe used on inner classes.
	public boolean isAClosedPosition(IOrderExec anExec) {
		return false;
	}

	@Override
	public void log(TradeMessageWrapper msg) {
		msg.setStrategyName(getStrategyName());
		((PortfolioStrategy) _shell).log(msg);
	}

	@SuppressWarnings("boxing")
	@Override
	public void newExecution(IOrderExec anExec) {
		ordersMap.get(anExec.getOrderId()).lastStatus = EOrderStatus.TOTAL_FILLED;
	}

	@Override
	public void orderConfirmedByUser(IOrderMfg order) {
		// do nothing
	}

	@SuppressWarnings("boxing")
	@Override
	public void orderStatus(IOrderStatus aStatus) {
		super.orderStatus(aStatus);
		ordersMap.get(aStatus.getOrderId()).lastStatus = aStatus.getStatus();
	}
}
