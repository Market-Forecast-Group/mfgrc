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
package com.mfg.symbols.configurations;

import com.mfg.dm.symbols.HistoricalDataInfo;
import com.mfg.dm.symbols.SymbolData2;
import com.mfg.interfaces.configurations.BaseConfigurationInfo;

/**
 * @author arian
 * 
 */
public abstract class SymbolConfigurationInfo<T extends SymbolData2> extends
		BaseConfigurationInfo {

	private static final String PROP_HISTORICAL_DATA_INFO = "historicalDataInfo";
	private static final String PROP_DATABASE_PATH = "databasePath";
	public static final String PROP_SYMBOL = "symbol";
	private static final String PROP_START_TRADING = "startTrading";
	private T symbol;
	private String databasePath;
	private boolean startTrading;
	private HistoricalDataInfo historicalDataInfo;

	/**
	 * See {@link #getSymbolUUID()}
	 */
	public SymbolConfigurationInfo() {
	}

	/**
	 * @return the historicalDataParams
	 */
	public HistoricalDataInfo getHistoricalDataInfo() {
		return historicalDataInfo;
	}

	/**
	 * @param aHistoricalDataInfo
	 *            the historicalDataParams to set
	 */
	public void setHistoricalDataInfo(HistoricalDataInfo aHistoricalDataInfo) {
		this.historicalDataInfo = aHistoricalDataInfo;
		firePropertyChange(PROP_HISTORICAL_DATA_INFO);
	}

	/**
	 * @return the startTrading
	 */
	public boolean isStartTrading() {
		return startTrading;
	}

	/**
	 * @param aStartTrading
	 *            the startTrading to set
	 */
	public void setStartTrading(boolean aStartTrading) {
		this.startTrading = aStartTrading;
		firePropertyChange(PROP_START_TRADING);
	}

	/**
	 * @return the databasePath
	 */
	public String getDatabasePath() {
		return databasePath;
	}

	/**
	 * @param aDatabasePath
	 *            the databasePath to set
	 */
	public void setDatabasePath(String aDatabasePath) {
		this.databasePath = aDatabasePath;
		firePropertyChange(PROP_DATABASE_PATH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.interfaces.configurations.BaseConfigurationInfo#getConfiguration
	 * ()
	 */
	@Override
	public SymbolConfiguration<?, ?> getConfiguration() {
		return (SymbolConfiguration<?, ?>) super.getConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.interfaces.symbols.ISymbolConfigurationInfo#getSymbol()
	 */
	public T getSymbol() {
		return symbol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.interfaces.symbols.ISymbolConfigurationInfo#setSymbolData(com
	 * .mfg.dm.symbols.SymbolData)
	 */
	public void setSymbol(T aSymbol) {
		this.symbol = aSymbol;
		firePropertyChange(PROP_SYMBOL);
	}
}
