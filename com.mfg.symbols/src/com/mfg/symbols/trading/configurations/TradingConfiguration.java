/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.trading.configurations;

import com.mfg.interfaces.configurations.BaseConfiguration;
import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.SymbolsPlugin;

/**
 * @author arian
 * 
 */
public class TradingConfiguration extends
		BaseConfiguration<TradingConfigurationInfo> {

	/**
	 * 
	 */
	public TradingConfiguration() {
		setInfo(new TradingConfigurationInfo());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IStorageObject#getStorage()
	 */
	@Override
	public SimpleStorage<TradingConfiguration> getStorage() {
		return SymbolsPlugin.getDefault().getTradingStorage();
	}

}
