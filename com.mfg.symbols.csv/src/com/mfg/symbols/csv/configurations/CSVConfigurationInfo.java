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

import com.mfg.connector.csv.CSVHistoricalDataInfo;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;

/**
 * @author arian
 * 
 */
public class CSVConfigurationInfo extends
		SymbolConfigurationInfo<CSVSymbolData2> {

	/**
	 * 
	 */
	public CSVConfigurationInfo() {
		setHistoricalDataInfo(new CSVHistoricalDataInfo());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.SymbolConfigurationInfo#setSymbol(com.mfg.dm.symbols.
	 * SymbolData2)
	 */
	@Override
	public void setSymbol(CSVSymbolData2 symbol) {
		super.setSymbol(symbol);
		if (getConfiguration().getName() == null) {
			String fname = symbol.getFileName();
			String name = fname.substring(0, fname.length() - 4);
			getConfiguration().setName(name);
		}
	}

}
