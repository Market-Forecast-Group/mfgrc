package com.mfg.symbols.dfs.configurations;

import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.persistence.DFSStorage;

public class DFSConfiguration extends
		SymbolConfiguration<DFSSymbolData, DFSConfigurationInfo> {

	public DFSConfiguration() {
		setInfo(new DFSConfigurationInfo());
		setName("ES");
	}

	@Override
	public String getFullName() {
		return getName();
	}

	@Override
	public DFSStorage getStorage() {
		return DFSSymbolsPlugin.getDefault().getDFSStorage();
	}

	@Override
	public boolean allowRename() {
		return !getInfo().getSymbol().getLocalSymbol().endsWith("#mfg");
	}
}
