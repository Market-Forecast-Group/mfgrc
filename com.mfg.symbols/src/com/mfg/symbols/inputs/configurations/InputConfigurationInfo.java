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
package com.mfg.symbols.inputs.configurations;

import java.util.UUID;

import com.mfg.dm.symbols.HistoricalDataInfo;
import com.mfg.interfaces.configurations.BaseConfigurationInfo;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.probabilities.ProbabilitiesNames;

/**
 * @author arian
 * 
 */
public class InputConfigurationInfo extends BaseConfigurationInfo {
	private static final String PROP_HISTORICAL_DATA_INFO = "historicalDataInfo";
	private static final String PROP_DATABASE_PATH = "databasePath";
	public static final String PROP_USING_PROBABILITIES = "usingProbabilities";
	public static final String PROP_PROBABILITY_NAME = "probabilityName";
	public static final String PROP_HISTORICAL_DATA = "historicalData";
	public static final String PROP_SYMBOL_ID = "symbolId";

	private UUID symbolId;
	private IndicatorParamBean indicatorParams;
	private HistoricalDataInfo historicalDataInfo;

	private String probabilityName;
	private boolean usingProbabilities;

	private String databasePath;

	public InputConfigurationInfo() {
		indicatorParams = new IndicatorParamBean();
		probabilityName = ProbabilitiesNames.NO_PROBABILITY;
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

	public String getProbabilityName() {
		return probabilityName;
	}

	public void setProbabilityName(String aProbabilityName) {
		this.probabilityName = aProbabilityName;
		firePropertyChange(PROP_PROBABILITY_NAME);
	}

	public boolean isUsingProbabilities() {
		return usingProbabilities;
	}

	public void setUsingProbabilities(boolean aUsingProbabilities) {
		this.usingProbabilities = aUsingProbabilities;
		firePropertyChange(PROP_USING_PROBABILITIES);
	}

	/**
	 * @return the historicalDataInfo
	 */
	public HistoricalDataInfo getHistoricalDataInfo() {
		return historicalDataInfo;
	}

	/**
	 * @param aHistoricalDataInfo
	 *            the historicalDataInfo to set
	 */
	public void setHistoricalDataInfo(HistoricalDataInfo aHistoricalDataInfo) {
		this.historicalDataInfo = aHistoricalDataInfo;
		firePropertyChange(PROP_HISTORICAL_DATA_INFO);
	}

	/**
	 * @return the indicatorParams
	 */
	public IndicatorParamBean getIndicatorParams() {
		return indicatorParams;
	}

	/**
	 * @param aIndicatorParams
	 *            the indicatorParams to set
	 */
	public void setIndicatorParams(IndicatorParamBean aIndicatorParams) {
		this.indicatorParams = aIndicatorParams;
	}

	/**
	 * @return the symbolId
	 */
	public UUID getSymbolId() {
		return symbolId;
	}

	/**
	 * @param aSymbolId
	 *            the symbolId to set
	 */
	public void setSymbolId(UUID aSymbolId) {
		this.symbolId = aSymbolId;
		firePropertyChange(PROP_SYMBOL_ID);
	}
}
