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

import org.eclipse.ui.IMemento;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.ChartType;

/**
 * @author arian
 * 
 */
public interface IChartContentAdapter {
	public void init(boolean usePhysicalTime, IChartContentAdapter oldAdapter);

	public Chart getChart();

	public void configure(IChartView chartView, ChartConfig chartConfig);

	public void dispose(IChartView chartView);

	public String getChartName();

	public void scrollChart();

	public void shuttingDown();

	public ChartType getType();

	public void saveState(IMemento memento);

	public void initState(IMemento initState);
}
