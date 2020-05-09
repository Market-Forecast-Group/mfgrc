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
package com.mfg.chart.ui.views;

import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.ScrollingMode;

/**
 * @author arian
 * 
 */
public class ChartConfig {
	private PlotRange _range;
	private int _dataLayer;
	private ScrollingMode _scrollingMode;
	private boolean _autoDataLayer;
	private ChartType _chartType;

	public ChartConfig(PlotRange range, int dataLayer, ScrollingMode scrolling,
			boolean autoDataLayer, ChartType chartType) {
		this._range = range;
		this._dataLayer = dataLayer;
		this._scrollingMode = scrolling;
		this._autoDataLayer = autoDataLayer;
		_chartType = chartType;
	}

	public ChartType getChartType() {
		return _chartType;
	}

	public void setChartType(ChartType chartType) {
		_chartType = chartType;
	}

	public boolean isAutoDataLayer() {
		return _autoDataLayer;
	}

	public void setAutoDataLayer(boolean autoDataLayer) {
		this._autoDataLayer = autoDataLayer;
	}

	public PlotRange getRange() {
		return _range;
	}

	public void setRange(PlotRange range) {
		this._range = range;
	}

	public int getDataLayer() {
		return _dataLayer;
	}

	public void setDataLayer(int dataLayer) {
		this._dataLayer = dataLayer;
	}

	public ScrollingMode getScrollingMode() {
		return _scrollingMode;
	}

	public void setScrollingMode(ScrollingMode scrollingMode) {
		this._scrollingMode = scrollingMode;
	}

}
