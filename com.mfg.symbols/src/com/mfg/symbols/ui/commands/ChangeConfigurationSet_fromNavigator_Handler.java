package com.mfg.symbols.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfigurationInfo;
import com.mfg.symbols.trading.persistence.TradingStorage;
import com.mfg.symbols.ui.ConfigurationSetLabelProvider;

public class ChangeConfigurationSet_fromNavigator_Handler extends
		AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection sel = (StructuredSelection) HandlerUtil
				.getCurrentSelection(event);
		Object obj = sel.getFirstElement();
		if (obj instanceof TradingConfiguration) {
			Shell shell = HandlerUtil.getActiveShell(event);
			Object input = SymbolsPlugin.getDefault().getSetsManager()
					.getSetList();
			ListDialog dialog = new ListDialog(shell);
			dialog.setTitle("Change Configuration Set");
			dialog.setMessage("Select a new set.");
			dialog.setContentProvider(new ArrayContentProvider());
			dialog.setLabelProvider(new ConfigurationSetLabelProvider());
			dialog.setInput(input);
			TradingConfiguration config = (TradingConfiguration) obj;
			TradingConfigurationInfo info = config.getInfo();
			dialog.setInitialSelections(new Object[] { Integer.valueOf(info
					.getConfigurationSet()) });

			if (dialog.open() == Window.OK) {
				Integer newSet = (Integer) dialog.getResult()[0];
				info.setConfigurationSet(newSet.intValue());
				TradingStorage storage = SymbolsPlugin.getDefault()
						.getTradingStorage();
				storage.fireConfigurationSetChanged(config);
			}
		}
		return null;
	}

}
