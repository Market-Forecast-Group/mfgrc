package com.mfg.strategy.logger;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.events.TradeMessage;
import com.mfg.broker.events.TradeMessageType;

public class StrategyMessage extends TradeMessage {
	public static final TradeMessageType SENT = new TradeMessageType("Sent");
	public static final TradeMessageType PTPA = new TradeMessageType("+TPA");
	public static final TradeMessageType NTPA = new TradeMessageType("-TPA");
	public static final TradeMessageType PIV = new TradeMessageType("+PIV");
	public static final TradeMessageType NIV = new TradeMessageType("-NIV");
	public static final TradeMessageType PTD = new TradeMessageType("+PTD");
	public static final TradeMessageType NTD = new TradeMessageType("-NTD");

	private String event;
	private EAccountRouting _accountRouting;

	public StrategyMessage(TradeMessageType type, String aSource,
			String aEvent, EAccountRouting accountRouting) {
		super(type, aSource);
		this.event = aEvent;
		_accountRouting = accountRouting;
	}

	@Override
	public String getEvent() {
		return event;
	}

	public EAccountRouting getAccountRouting() {
		return _accountRouting;
	}
}
