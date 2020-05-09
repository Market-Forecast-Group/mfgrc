package com.mfg.symbols.trading.ui;

import java.util.List;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.ui.views.CommonNavigatorContentProvider;

public class TradingContentProvider extends CommonNavigatorContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof InputConfiguration) {
			return SymbolsPlugin.getDefault().getTradingStorage()
					.findByInput((InputConfiguration) parentElement).toArray();
		}
		return null;
	}

	@Override
	protected void registerStorages(List<IWorkspaceStorage> storages) {
		storages.add(SymbolsPlugin.getDefault().getTradingStorage());
	}

}
