package com.mfg.strategy.manual;

import com.mfg.broker.IOrderMfg.ORDER_TYPE;
import com.mfg.strategy.ManualStrategySettings;

public class PositionCommand extends Command {

	private final ORDER_TYPE order;
	private final boolean open;

	public static PositionCommand createOpenLongCommand(
			ManualStrategySettings settings) {
		return new PositionCommand(Routing.LONG, true, settings);
	}

	public static PositionCommand createCloseLongCommand(
			ManualStrategySettings settings) {
		return new PositionCommand(Routing.LONG, false, settings);
	}

	public static PositionCommand createOpenShortCommand(
			ManualStrategySettings settings) {
		return new PositionCommand(Routing.SHORT, true, settings);
	}

	public static PositionCommand createCloseShortCommand(
			ManualStrategySettings settings) {
		return new PositionCommand(Routing.SHORT, false, settings);
	}

	public PositionCommand(Routing routing, boolean aOpen,
			ManualStrategySettings settings) {
		super(routing, settings);
		order = aOpen ? routing.getOrderToOpen() : routing.getOrderToClose();
		this.open = aOpen;
	}

	public ORDER_TYPE getOrder() {
		return order;
	}

	public boolean isForOpen() {
		return open;
	}
}
