package com.mfg.symbols;

import org.eclipse.ui.IStartup;

public class SymbolsStartup implements IStartup {

	@Override
	public void earlyStartup() {
		SymbolsPlugin.deleteTemporalDatabases();
	}

}
