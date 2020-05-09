package com.mfg.symbols.trading.ui;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.strategy.logger.TradeMessageWrapper;

public class QuantityLabelProvider extends StrategyLogLabelProvider {
	public QuantityLabelProvider() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.trading.logger.ui.StrategyLogLabelProvider#getText(com
	 * .mfg.strategy .logger.TradeMessageWrapper)
	 */
	@Override
	public String getText(TradeMessageWrapper msg) {
		return Integer
				.toString(msg.getRoutedAccount() == EAccountRouting.LONG_ACCOUNT ? msg
						.getLongQuantity() : msg.getShortQuantity());
	}
}
