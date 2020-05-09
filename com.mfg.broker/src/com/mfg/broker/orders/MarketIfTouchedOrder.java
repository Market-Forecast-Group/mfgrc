package com.mfg.broker.orders;

import com.mfg.broker.IOrderMfg;
import com.mfg.utils.MathUtils;
import com.mfg.utils.StepDefinition;

/**
 * This is an order specular to the stop: buy down and sell up with a market
 * order.
 * 
 * @author lino
 */
public class MarketIfTouchedOrder extends OrderImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -99786549958224936L;

	public MarketIfTouchedOrder(ORDER_TYPE type, int quantity, long stopPrice,
			int id) {
		this(null, type, quantity, stopPrice, id);
	}

	public MarketIfTouchedOrder(int aId, ORDER_TYPE type, int quantity,
			long stopPrice) {
		this(null, type, quantity, stopPrice, aId);
	}

	public MarketIfTouchedOrder(IOrderMfg parent, ORDER_TYPE type,
			int quantity, long stopPrice, int id) {
		super(parent, type, EXECUTION_TYPE.MIT, quantity, id);
		setAuxPrice(stopPrice);
	}

	public MarketIfTouchedOrder(int aId, IOrderMfg parent, ORDER_TYPE type,
			int quantity, long stopPrice) {
		super(parent, type, EXECUTION_TYPE.MIT, quantity, aId);
		setAuxPrice(stopPrice);
	}

	@Override
	public void turnIntoMarket(int currentPrice, int tickSize) {
		setAuxPrice(currentPrice);
	}

	@Override
	public int getOpeningPrice() {
		return getAuxPrice();
	}

	@Override
	public void setOpeningPrice(int op) {
		setAuxPrice(op);
	}

	@Override
	public String toString() {
		return super.toString() + " auxprice " + getAuxPrice();
	}

	@Override
	public String toString(StepDefinition tick) {
		return super.toString(tick) + " auxprice "
				+ MathUtils.getPriceFormat(tick.roundLong(getAuxPrice()));
	}

}
