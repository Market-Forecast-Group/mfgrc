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

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.views.AbstractChartView;

/**
 * @author arian
 * 
 */
public class RefreshChartHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		chart.reloadDefaultProfile();
		chart.update(chart.isAutoRangeEnabled());
		return null;
	}

}
