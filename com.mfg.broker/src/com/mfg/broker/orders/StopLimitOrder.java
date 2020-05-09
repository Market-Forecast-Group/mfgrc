package com.mfg.broker.orders;

import com.mfg.broker.IOrderMfg;
import com.mfg.utils.MathUtils;
import com.mfg.utils.StepDefinition;

/**
 * The stop limit order is the combination of a stop and a limit order.
 * 
 * It has the "opening" price and a auxiliar price. The opening price is
 * referred to the stop and the auxiliar price is referred to the limit.
 */
public class StopLimitOrder extends OrderImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5180088317979531342L;

	/**
	 * Standard constructor; it has two prices, the stop and the limit price.
	 * 
	 * The stop price is used to wait until the condition is met. The limit
	 * price is used to limit the loss after the condition is met, but at the
	 * risk of not filling the order.
	 */
	public StopLimitOrder(ORDER_TYPE type, int quantity, long stopPrice,
			int limitPrice, int id) {
		this(null, type, quantity, stopPrice, limitPrice, id);
	}

	public StopLimitOrder(int aId, ORDER_TYPE type, int quantity,
			int stopPrice, int limitPrice) {
		this(null, type, quantity, stopPrice, limitPrice, aId);
	}

	public StopLimitOrder(IOrderMfg parent, ORDER_TYPE type, int quantity,
			long stopPrice, int limitPrice, int id) {
		super(parent, type, EXECUTION_TYPE.STOP_LIMIT, quantity, id);
		setLimitPrice(limitPrice);
		setAuxPrice(stopPrice);
	}

	public StopLimitOrder(int aId, IOrderMfg parent, ORDER_TYPE type,
			int quantity, long stopPrice, int limitPrice) {
		super(parent, type, EXECUTION_TYPE.STOP_LIMIT, quantity, aId);
		setLimitPrice(limitPrice);
		setAuxPrice(stopPrice);
	}

	@Override
	public int getOpeningPrice() {
		return getAuxPrice();
	}

	@Override
	public void turnIntoMarket(int currentPrice, int tickSize) {
		setLimitPrice(currentPrice);
		setAuxPrice(OrderUtils.someTicksBetter(getType(), currentPrice, 4,
				tickSize));
	}

	@Override
	public void setOpeningPrice(int op) {
		setLimitPrice(op);
		setAuxPrice(op);
	}

	@Override
	public String toString() {
		return super.toString() + " lim.Price " + getLimitPrice()
				+ " auxprice " + getAuxPrice();
	}

	@Override
	public String toString(StepDefinition tick) {
		return super.toString(tick) + " limPrice="
				+ MathUtils.getPriceFormat(tick.roundLong(getLimitPrice()))
				+ ", auxPrice="
				+ MathUtils.getPriceFormat(tick.roundLong(getAuxPrice()));
	}

}
