package com.mfg.broker.orders;

import com.mfg.utils.MathUtils;
import com.mfg.utils.StepDefinition;

public class LimitOrder extends OrderImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5271345749471233813L;

	public LimitOrder(int aId, ORDER_TYPE type, int quantity, int limitPrice) {
		super(null, type, EXECUTION_TYPE.LIMIT, quantity, aId);
		setLimitPrice(limitPrice);
	}

	@Override
	public void setOpeningPrice(int op) {
		setLimitPrice(op);
	}

	@Override
	public void turnIntoMarket(int currentPrice, int tickSize) {
		setLimitPrice(currentPrice);
	}

	@Override
	public int getOpeningPrice() {
		return getLimitPrice();
	}

	@Override
	public String toString() {
		return super.toString() + " lim.Price " + getLimitPrice();
	}

	@Override
	public String toString(StepDefinition tick) {
		return super.toString(tick) + " lim.Price "
				+ MathUtils.getPriceFormat(tick.roundLong(getLimitPrice()));
	}

}
