package com.mfg.broker.orders;

import com.mfg.broker.IOrderMfg;
import com.mfg.utils.MathUtils;
import com.mfg.utils.StepDefinition;

public class StopOrder extends OrderImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9123430930053818658L;

	public StopOrder(ORDER_TYPE type, int quantity, long stopPrice, int id) {
		this(null, type, quantity, stopPrice, id);
	}

	public StopOrder(int aId, ORDER_TYPE type, int quantity, long stopPrice) {
		this(null, type, quantity, stopPrice, aId);
	}

	public StopOrder(IOrderMfg parent, ORDER_TYPE type, int quantity,
			long stopPrice, int id) {
		super(parent, type, EXECUTION_TYPE.STOP, quantity, id);
		setAuxPrice(stopPrice);
	}

	public StopOrder(int aId, IOrderMfg parent, ORDER_TYPE type, int quantity,
			long stopPrice) {
		super(parent, type, EXECUTION_TYPE.STOP, quantity, aId);
		setAuxPrice(stopPrice);
	}

	@Override
	public void setOpeningPrice(int op) {
		setAuxPrice(op);
	}

	@Override
	public int getOpeningPrice() {
		return getAuxPrice();
	}

	@Override
	public void turnIntoMarket(int currentPrice, int tickSize) {
		setAuxPrice(OrderUtils.someTicksBetter(getType(), currentPrice, 4,
				tickSize));
	}

	@Override
	public String toString() {
		return super.toString() + " auxprice " + getAuxPrice();
	}

	@Override
	public String toString(StepDefinition tick) {
		return super.toString(tick) + " auxPrice="
				+ MathUtils.getPriceFormat(tick.roundLong(getAuxPrice()));
	}

}
