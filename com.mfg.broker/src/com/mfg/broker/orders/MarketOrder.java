package com.mfg.broker.orders;

import com.mfg.broker.IOrderMfg;

public class MarketOrder extends OrderImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5132490184411461178L;

	public MarketOrder(int aId, IOrderMfg.ORDER_TYPE type, int quantity) {
		super(type, IOrderMfg.EXECUTION_TYPE.MARKET, quantity, aId);
	}

}
