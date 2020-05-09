package com.mfg.symbols.trading.persistence;

import com.mfg.persist.interfaces.IWorkspaceStorageReference;
import com.mfg.symbols.SymbolsPlugin;

public class TradingWorkspaceStorageReference implements
		IWorkspaceStorageReference {

	@Override
	public TradingStorage getStorage() {
		return SymbolsPlugin.getDefault().getTradingStorage();
	}

	@Override
	public String getStorageId() {
		return TradingStorage.class.getCanonicalName();
	}
}
