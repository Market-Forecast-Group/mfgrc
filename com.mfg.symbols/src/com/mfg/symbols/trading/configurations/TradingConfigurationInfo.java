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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mfg.interfaces.configurations.BaseConfigurationInfo;
import com.mfg.strategy.IStrategyFactory;
import com.mfg.strategy.IStrategySettings;
import com.mfg.symbols.SymbolsPlugin;

/**
 * @author arian
 * 
 */
public class TradingConfigurationInfo extends BaseConfigurationInfo {
	public static final String PROP_DO_PAPER_TRADING = "doPaperTrading";
	/**
	 * 
	 */
	private static final String PROP_DATABASE_PATH = "databasePath";
	public static final String PROP_CONFIGURATION_SET = "configurationSet";
	public static final String PROP_STRATEGY_FACTORY_ID = "strategyFactoryId";
	public static final String PROP_USE_ORDER_CONFIRMATION = "useOrderConfirmation";
	public static final String PROP_COLOR = "color";
	public static final String PROP_INPUT_CONFIGURATION_ID = "inputConfiguratioId";

	private UUID inputConfiguratioId;
	@Deprecated
	private int[] color;
	private boolean useOrderConfirmation;
	private String strategyFactoryId;
	private final Map<String, IStrategySettings> strategySettingsMap;
	private int configurationSet;
	private boolean _doPaperTrading;

	private String databasePath;

	public TradingConfigurationInfo() {
		color = new int[] { (int) (Math.random() * 250),
				(int) (Math.random() * 250), (int) (Math.random() * 250) };
		useOrderConfirmation = false;
		strategySettingsMap = new HashMap<>();
		configurationSet = 0;
		_doPaperTrading = true;
	}

	public boolean isDoPaperTrading() {
		return _doPaperTrading;
	}

	public void setDoPaperTrading(boolean doPaperTrading) {
		_doPaperTrading = doPaperTrading;
		firePropertyChange(PROP_DO_PAPER_TRADING);
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

	public int getConfigurationSet() {
		return configurationSet;
	}

	public void setConfigurationSet(int aConfigurationSet) {
		this.configurationSet = aConfigurationSet;
		firePropertyChange(PROP_CONFIGURATION_SET);
	}

	public IStrategySettings getStrategySettings(String strategyId) {
		if (!strategySettingsMap.containsKey(strategyId)) {
			IStrategyFactory factory = SymbolsPlugin.getDefault()
					.getStrategyFactory(strategyId);
			strategySettingsMap
					.put(strategyId, factory.createDefaultSettings());
		}
		return strategySettingsMap.get(strategyId);
	}

	/**
	 * @return the strategyFactoryId
	 */
	public String getStrategyFactoryId() {
		return strategyFactoryId;
	}

	/**
	 * @param aStrategyFactoryId
	 *            the strategyFactoryId to set
	 */
	public void setStrategyFactoryId(String aStrategyFactoryId) {
		this.strategyFactoryId = aStrategyFactoryId;
		firePropertyChange(PROP_STRATEGY_FACTORY_ID);
	}

	/**
	 * @return the useOrderConfirmation
	 */
	public boolean isUseOrderConfirmation() {
		return useOrderConfirmation;
	}

	/**
	 * @param aUseOrderConfirmation
	 *            the useOrderConfirmation to set
	 */
	public void setUseOrderConfirmation(boolean aUseOrderConfirmation) {
		this.useOrderConfirmation = aUseOrderConfirmation;
		firePropertyChange(PROP_USE_ORDER_CONFIRMATION);
	}

	public UUID getInputConfiguratioId() {
		return inputConfiguratioId;
	}

	public void setInputConfiguratioId(UUID aInputConfiguratioId) {
		this.inputConfiguratioId = aInputConfiguratioId;
		firePropertyChange(PROP_INPUT_CONFIGURATION_ID);
	}

	public int[] getColor() {
		return color;
	}

	public void setColor(int[] aColor) {
		this.color = aColor;
		firePropertyChange(PROP_COLOR);
	}

}
