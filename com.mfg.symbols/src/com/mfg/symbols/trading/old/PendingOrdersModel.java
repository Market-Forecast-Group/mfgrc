/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */
/**
 * 
 */
package com.mfg.symbols.trading.old;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.chart.model.IPositionCollection;
import com.mfg.strategy.PendingOrderInfo;
import com.mfg.strategy.PortfolioStrategy;

/**
 * @author arian
 * 
 */
public class PendingOrdersModel implements IPositionCollection {

	private final PortfolioStrategy strategy;
	private PendingOrderInfo[] pendingOrders;

	public PendingOrdersModel(PortfolioStrategy aStrategy) {
		this.strategy = aStrategy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITimePriceCollection#getSize()
	 */
	@Override
	public int getSize() {
		// we should consider the case the strategy is null, for example, when
		// the trading was stopped but the symbol job (and charts) still
		// running. Basically, if the strategy is null, it is because there is
		// not any pending order.
		if (strategy == null) {
			return 0;
		}
		pendingOrders = strategy.getPendingOrders();
		return pendingOrders.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITimePriceCollection#getTime(int)
	 */
	@Override
	public long getTime(int index) {
		PendingOrderInfo[] infos = pendingOrders;
		PendingOrderInfo info = infos[index];
		return info.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.ITimePriceCollection#getPrice(int)
	 */
	@Override
	public double getPrice(int index) {
		PendingOrderInfo[] infos = pendingOrders;
		PendingOrderInfo info = infos[index];
		return info.getOrder().getOpeningPrice();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IPositionCollection#isLongPosition(int)
	 */
	@Override
	public boolean isLongPosition(int index) {
		PendingOrderInfo[] infos = pendingOrders;
		PendingOrderInfo info = infos[index];
		return info.getOrder().getAccountRouting() == EAccountRouting.LONG_ACCOUNT;
	}

}
