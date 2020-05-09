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
package com.mfg.symbols.trading.ui.adapters;

import org.eclipse.swt.graphics.Image;

import com.mfg.chart.ui.ChartType;
import com.mfg.symbols.trading.configurations.TradingConfiguration;

/**
 * @author arian
 * 
 */
public class EquityChartAdapter extends TradingChartAdapater {

	/**
	 * @param configuration
	 */
	public EquityChartAdapter(TradingConfiguration configuration) {
		super(configuration, ChartType.EQUITY);
	}

	@Override
	public Image getChartIcon() {
		return null;
	}

	@Override
	public String getChartName() {
		return super.getChartName() + " - Equity";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.ChartContentAdapter#getLastTime()
	 */
	@Override
	protected long getLastTime() {
		return _chart.getModel().getTradingModel().getEquityUpperTime();
	}
}
