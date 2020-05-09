package com.mfg.broker.events;

/**
 * Broker event type.
 * 
 * @author arian
 * 
 */
public final class TradeMessageType {
	private String name;

	public TradeMessageType(String name1) {
		this.name = name1;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

}
