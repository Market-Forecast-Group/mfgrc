package com.mfg.broker.events;

/**
 * Interface of the broker events. The broker events will be showed in a User
 * Log.
 * 
 * @author arian
 * 
 */
public interface ITradeMessage {
	public TradeMessageType getType();

	public String getEvent();

	public void setTickScale(int scale);

	public int getTickScale();

	public String getSource();
}
