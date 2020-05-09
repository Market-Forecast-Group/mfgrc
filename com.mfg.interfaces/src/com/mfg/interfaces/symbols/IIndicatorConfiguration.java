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

package com.mfg.interfaces.symbols;

import java.beans.PropertyChangeListener;
import java.util.UUID;

import com.mfg.chart.model.mdb.ChartMDBSession;
import com.mfg.dm.symbols.CSVSymbolData;
import com.mfg.interfaces.trading.Configuration;

/**
 * Indicator configuration for statistics.
 * 
 * @author arian
 * 
 */
public interface IIndicatorConfiguration {
	public UUID getUUID();

	public void setUUID(UUID uuid);

	public AbstractIndicatorParamBean getIndicatorSettings();

	public void setIndicatorSettings(AbstractIndicatorParamBean settings);

	public CSVSymbolData getSymbol();

	public void setSymbol(CSVSymbolData symbol);

	public ChartMDBSession getChartDBSession();

	public String getName();

	public void setName(String name);

	public String getProbabilityName();

	public void setProbabilityName(String pname);

	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l);

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l);

	public void firePropertyChange(String property);

	public Configuration getProbabilitiesSettings();

	public void setProbabilitiesSettings(Configuration aConfiguration);
}
