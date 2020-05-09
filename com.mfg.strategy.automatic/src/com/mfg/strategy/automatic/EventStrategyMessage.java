package com.mfg.strategy.automatic;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.broker.events.TradeMessageType;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.logger.StrategyMessage;

public class EventStrategyMessage extends StrategyMessage {

	private EventGeneral _patternEvent;

	public EventStrategyMessage(TradeMessageType type, String aSource,
			String aEvent, EventGeneral pattern, EAccountRouting accountRouting) {
		super(type, aSource, aEvent, accountRouting);
		_patternEvent = pattern;
	}

	public EventGeneral getPatternEvent() {
		return _patternEvent;
	}
}
