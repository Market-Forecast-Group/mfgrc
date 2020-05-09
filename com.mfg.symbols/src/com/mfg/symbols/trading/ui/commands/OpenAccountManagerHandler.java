package com.mfg.symbols.trading.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.actions.OpenTradingViewAction;
import com.mfg.symbols.trading.ui.views.AccountManagerView2;

public class OpenAccountManagerHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		new OpenTradingViewAction((TradingConfiguration) sel.getFirstElement(),
				AccountManagerView2.VIEW_ID, "Account Manager",
				SymbolsPlugin.PLUGIN_ID, SymbolsPlugin.STRATEGY_LOG_IMAGE_PATH)
				.run();
		return null;
	}

}
