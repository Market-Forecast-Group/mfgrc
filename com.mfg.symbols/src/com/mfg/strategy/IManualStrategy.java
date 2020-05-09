package com.mfg.strategy;

import com.mfg.interfaces.trading.IStrategy;

public interface IManualStrategy extends IStrategy {
	public ManualStrategySettings getManualStrategySettings();

	public void setManualStrategySettings(ManualStrategySettings settings);
}
