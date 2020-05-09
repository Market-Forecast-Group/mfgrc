package com.mfg.strategy.manual;

import com.mfg.strategy.ManualStrategySettings;

public class AvgCommand extends PositionCommand {

	public AvgCommand(Routing routing, ManualStrategySettings settings) {
		super(routing, true, settings);
	}

}
