package com.mfg.strategy.manual;

import com.mfg.strategy.ManualStrategySettings;

public class SARCommand extends Command {

	public static Command createLongCommand(ManualStrategySettings settings) {
		return new SARCommand(Routing.LONG, settings);
	}

	public static Command createShortCommand(ManualStrategySettings settings) {
		return new SARCommand(Routing.SHORT, settings);
	}

	public SARCommand(Routing routing, ManualStrategySettings settings) {
		super(routing, settings);
	}
}
