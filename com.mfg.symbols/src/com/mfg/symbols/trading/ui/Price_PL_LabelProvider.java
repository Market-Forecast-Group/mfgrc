package com.mfg.symbols.trading.ui;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.strategy.logger.TradeMessageWrapper;

public class Price_PL_LabelProvider extends StrategyLogLabelProvider {

	public Price_PL_LabelProvider() {
	}

	@Override
	public String getText(TradeMessageWrapper msg) {
		long price = msg.getRoutedAccount() == EAccountRouting.LONG_ACCOUNT ? msg
				.getLongPricePL() : msg.getShortPricePL();
		return Long.toString(price);
	}

}
