package com.mfg.symbols.trading.ui;

import com.mfg.strategy.logger.TradeMessageWrapper;

public class TimeLabelProvider extends StrategyLogLabelProvider {

	public TimeLabelProvider() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.strategy.logger.ui.StrategyLogLabelProvider#getText(com.mfg.strategy
	 * .logger.TradeMessageWrapper)
	 */
	@Override
	public String getText(TradeMessageWrapper msg) {
		return "" + msg.getFakeTime();
	}

}
