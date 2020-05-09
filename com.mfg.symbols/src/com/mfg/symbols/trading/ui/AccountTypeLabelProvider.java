package com.mfg.symbols.trading.ui;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.strategy.logger.TradeMessageWrapper;

public class AccountTypeLabelProvider extends TypeLabelProvider {

	public AccountTypeLabelProvider() {
	}

	@Override
	public String getText(TradeMessageWrapper msg) {
		return (msg.getRoutedAccount() == EAccountRouting.LONG_ACCOUNT ? "Long"
				: "Short");
	}

}
