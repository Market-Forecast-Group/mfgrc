package com.mfg.strategy.manual;

import com.mfg.strategy.ManualStrategySettings;


public class CloseAllCommand extends Command {

	public CloseAllCommand(Routing routing, ManualStrategySettings settings) {
		super(routing, settings);
	}

}
