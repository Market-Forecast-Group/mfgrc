package com.mfg.strategy.manual.ui.commands;

import com.mfg.strategy.manual.ManualStrategyPlugin;
import com.mfg.strategy.manual.ui.views.TradingConsoleView2;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.actions.OpenTradingViewAction;

public class OpenTradingConsoleAction extends OpenTradingViewAction {

	public OpenTradingConsoleAction(TradingConfiguration configuration) {
		super(configuration, TradingConsoleView2.VIEW_ID, "Trading Console",
				ManualStrategyPlugin.PLUGIN_ID,
				ManualStrategyPlugin.TRADING_CONSOLE_IMAGE_PATH);
	}

}
