package com.mfg.strategy.manual.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.symbols.trading.configurations.TradingConfiguration;

public class OpenTradingConsoleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		TradingConfiguration conf = (TradingConfiguration) sel
				.getFirstElement();
		new OpenTradingConsoleAction(conf).run();
		return null;
	}

}
