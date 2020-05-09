package com.mfg.strategy.manual.interfaces;

import com.mfg.strategy.IManualStrategy;
import com.mfg.strategy.manual.Command;
import com.mfg.strategy.manual.ManualStrategyAlgorithm;

/**
 * Interface of the manual strategy. Manual strategies implementation can use
 * the {@link ManualStrategyAlgorithm} class to execute the commands.
 * 
 * @author arian
 * 
 */
public interface IManualStrategy2 extends IManualStrategy {
	/**
	 * Execute a command. Usually this method will be called by the strategy
	 * GUI. Implementations of this methods can use the
	 * {@link ManualStrategyAlgorithm}.
	 * 
	 * @param command
	 */
	public void executeCommand(Command command);
}
