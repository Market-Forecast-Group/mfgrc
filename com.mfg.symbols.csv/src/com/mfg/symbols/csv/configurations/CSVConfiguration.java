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
package com.mfg.symbols.csv.configurations;

import com.mfg.persist.interfaces.SimpleStorage;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.csv.CSVSymbolPlugin;

/**
 * @author arian
 * 
 */
public class CSVConfiguration extends
		SymbolConfiguration<CSVSymbolData2, CSVConfigurationInfo> {
	/**
	 * 
	 */
	public CSVConfiguration() {
		setInfo(new CSVConfigurationInfo());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.persist.interfaces.IStorageObject#getStorage()
	 */
	@Override
	public SimpleStorage<?> getStorage() {
		return CSVSymbolPlugin.getDefault().getCSVStorage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.configurations.SymbolConfiguration#toContract()
	 */
//	@Override
//	public IContract toContract() {
//		return new CSVContractAdapter(this);
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.configurations.SymbolConfiguration#getFullName()
	 */
	@Override
	public String getFullName() {
		return getName();
	}
}
