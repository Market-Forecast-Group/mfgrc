package com.mfg.symbols.trading.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.actions.OpenTradingViewAction;
import com.mfg.symbols.trading.ui.views.DashboardView;

public class OpenDashboardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		createAction((TradingConfiguration) sel.getFirstElement()).run();
		return null;
	}

	public static OpenTradingViewAction createAction(TradingConfiguration conf) {
		return new OpenTradingViewAction(conf, DashboardView.ID, "Dashboard",
				SymbolsPlugin.PLUGIN_ID, SymbolsPlugin.DASHBOARD_IMAGE_PATH);
	}

}
