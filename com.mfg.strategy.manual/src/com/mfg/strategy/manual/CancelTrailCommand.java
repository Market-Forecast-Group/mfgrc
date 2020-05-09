package com.mfg.strategy.manual;

import com.mfg.strategy.ManualStrategySettings;


public class CancelTrailCommand extends Command {

	public CancelTrailCommand(Routing routing, ManualStrategySettings settings) {
		super(routing, settings);
	}

}
