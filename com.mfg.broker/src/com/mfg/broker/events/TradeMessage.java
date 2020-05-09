package com.mfg.broker.events;

import com.mfg.utils.MathUtils;

/**
 * Base class for {@link ITradeMessage}.
 * 
 * @author arian
 * 
 */
public abstract class TradeMessage implements ITradeMessage {

	public static final TradeMessageType COMMENT = new TradeMessageType(
			"Comment");

	public static final TradeMessageType NEW_TH = new TradeMessageType("NEW_TH");
	/**
	 * White comments are used by the log view to show a blank row but with the
	 * Event and Source columns.
	 */
	public static final TradeMessageType WHITE_COMMENT = new TradeMessageType(
			"WhiteComment");
	public static final TradeMessageType CANCELED = new TradeMessageType(
			"Canceled");
	public static final TradeMessageType RECEIVED = new TradeMessageType(
			"Received");
	public static final TradeMessageType ADDED = new TradeMessageType("Added");
	public static final TradeMessageType MODIFIED = new TradeMessageType(
			"Modified");
	public static final TradeMessageType EXECUTED = new TradeMessageType(
			"Executed");

	private TradeMessageType type;
	private int tickScale;
	private final String source;

	public TradeMessage(TradeMessageType type1, String aSource) {
		super();
		this.type = type1;
		tickScale = 0;
		this.source = aSource;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public TradeMessageType getType() {
		return type;
	}

	public void setType(TradeMessageType type1) {
		this.type = type1;
	}

	@Override
	public int getTickScale() {
		return tickScale;
	}

	@Override
	public void setTickScale(int tickScale1) {
		this.tickScale = tickScale1;
	}

	public String formatPriceWithScale(double price) {
		return formatPriceWithScale(price, getTickScale());
	}

	public static String formatPriceWithScale(double price, int tickScale) {
		return MathUtils.getPriceFormat(price/Math.pow(10, tickScale));
//		return String.format("%." + tickScale + "f", tickScale == 0 ? price
//				: price / Math.pow(10, tickScale));
	}
}
