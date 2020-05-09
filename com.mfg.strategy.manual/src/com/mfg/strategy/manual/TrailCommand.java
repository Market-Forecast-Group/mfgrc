package com.mfg.strategy.manual;

import com.mfg.strategy.ManualStrategySettings;

public class TrailCommand extends Command {
	private final Trailing trail;

	public TrailCommand(Routing routing, ManualStrategySettings settings,
			Trailing aTrail) {
		super(routing, settings);
		this.trail = aTrail;
	}

	public Trailing getTrail() {
		return trail;
	}

}
