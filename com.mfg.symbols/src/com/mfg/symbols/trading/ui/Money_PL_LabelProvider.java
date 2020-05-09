package com.mfg.symbols.trading.ui;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.strategy.logger.TradeMessageWrapper;

public class Money_PL_LabelProvider extends StrategyLogLabelProvider {

	public Money_PL_LabelProvider() {
	}

	@Override
	public String getText(TradeMessageWrapper msg) {
		return Double
				.toString(msg.getRoutedAccount() == EAccountRouting.LONG_ACCOUNT ? msg
						.getLongCapital() : msg.getShortCapital());
	}

}
