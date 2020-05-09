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

import org.eclipse.ui.IViewPart;

import com.mfg.chart.ui.views.IChartView;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.views.ITradingView;

/**
 * @author arian
 * 
 */
public class ChartViewToITradingViewAdapter implements ITradingView {
	private static final String KEY_TRADING_VIEW_CONFIGURATION_SET = "ITradingView.configurationSet";
	private final IChartView view;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.trading.ui.views.ITradingView#getPart()
	 */
	@Override
	public IViewPart getPart() {
		return (IViewPart) view;
	}

	public static Integer getChartViewConfigurationSet(IChartView view) {
		String configSetValue = view
				.getPartProperty(KEY_TRADING_VIEW_CONFIGURATION_SET);
		return configSetValue == null ? null : Integer.valueOf(Integer
				.parseInt(configSetValue));
	}

	public static void setChartViewConfigurationSet(IChartView view, Integer set) {
		view.setPartProperty(KEY_TRADING_VIEW_CONFIGURATION_SET,
				set == null ? null : set.toString());
	}

	public ChartViewToITradingViewAdapter(IChartView aView) {
		this.view = aView;
		setConfigurationSet(((TradingConfiguration) aView.getContent())
				.getInfo().getConfigurationSet());
	}

	@Override
	public void setConfiguration(TradingConfiguration configuration) {
		view.setContent(configuration);
		if (configuration != null) {
			setConfigurationSet(configuration.getInfo().getConfigurationSet());
		}
	}

	@Override
	public TradingConfiguration getConfiguration() {
		return (TradingConfiguration) view.getContent();
	}

	@Override
	public int getConfigurationSet() {
		if (getConfiguration() != null) {
			setChartViewConfigurationSet(view,
					Integer.valueOf(getConfiguration().getInfo()
							.getConfigurationSet()));
		}
		Integer confSet = getChartViewConfigurationSet(view);
		return confSet.intValue();
	}

	@Override
	public void setConfigurationSet(int configurationSet) {
		setChartViewConfigurationSet(view, Integer.valueOf(configurationSet));
	}

}
