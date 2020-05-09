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

package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.mfg.opengl.chart.GLChart;
import org.mfg.opengl.chart.Settings;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.views.AbstractChartView;

/**
 * @author arian
 * 
 */
public class SnappingOverPricesHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(final AbstractChartView view,
			final ExecutionEvent event) {
		final GLChart chart = view.getChart().getGLChart();
		Settings settings = chart.getSettings();
		settings.setSnappingMode(settings.getSnappingMode().next());
		view.getChart();
		view.getChart()
				.getFeedbackMessages()
				.showMessage(
						Chart.getSnappingName(settings
								.getSnappingMode()));
		view.getChart().update(view.getChart().isAutoRangeEnabled());
		return null;
	}
}
