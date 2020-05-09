package com.mfg.broker.events;

/**
 * Remaining orders event.
 * 
 * @author arian
 * 
 */
public class RemainingOrdersMessage extends TradeMessage {

	private long numberOrders;

	/**
	 * @param time
	 * @param price
	 */
	public RemainingOrdersMessage(String aSource, long time, long price,
			long numberOrders1) {
		super(TradeMessage.WHITE_COMMENT, aSource);
		this.numberOrders = numberOrders1;
	}

	public long getNumberOrders() {
		return numberOrders;
	}

	@Override
	public String getEvent() {
		return "Active orders " + getNumberOrders();
	}

}
