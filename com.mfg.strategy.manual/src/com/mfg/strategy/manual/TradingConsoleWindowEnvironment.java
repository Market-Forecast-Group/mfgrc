package com.mfg.strategy.manual;

import com.mfg.strategy.ManualStrategySettings;
import com.mfg.utils.ListenerSupport;

public class TradingConsoleWindowEnvironment {

	private final ManualStrategySettings settings;

	private final ListenerSupport support;
	private final ManualStrategy strategy;

	public TradingConsoleWindowEnvironment(ManualStrategy aStrategy,
			ManualStrategySettings aSettings) {
		this.strategy = aStrategy;
		this.settings = aSettings;
		support = new ListenerSupport();
	}

	public void addStateListener(Runnable listener) {
		support.addListener(listener);
	}

	public void removeStateListener(Runnable listener) {
		support.removeListener(listener);
	}

	/**
	 * Notify to the Manual Trading Window that his environment state changed.
	 * Because to know if the environment changed or not is too complicated,
	 * this method is called when a possible change was performed. For example,
	 * it is called when a new tick or when a command is executed. This must to
	 * me improved in the future.
	 * 
	 */
	// TODO: This must to be improved in the future. In the sense that just must
	// to be called only when the state changed.
	public void fireStateChanged() {
		support.fire();
	}

	public void executeCommand(WindowCommand windowCommand,
			ManualStrategySettings aSettings) {
		getStrategy().executeCommand(
				Command.createCommand(windowCommand, aSettings));
		fireStateChanged();
	}

	public ManualStrategy getStrategy() {
		return strategy;
	}

	public ManualStrategySettings getSettings() {
		return settings;
	}

}
