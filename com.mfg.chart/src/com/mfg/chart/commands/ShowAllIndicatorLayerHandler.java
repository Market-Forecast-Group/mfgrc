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
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.ui.views.AbstractChartView;

/**
 * @author arian
 * 
 */
public class ShowAllIndicatorLayerHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		IndicatorLayer indicatorLayer = chart.getIndicatorLayer();
		if (indicatorLayer != null) {
			Integer scale = indicatorLayer.getSelectedScale();
			if (scale == null) {
				if (indicatorLayer.isVisible()) {
					indicatorLayer.setVisibleByUser(indicatorLayer, false);
				} else {
					indicatorLayer.reloadDefaultProfile();
				}
			} else {
				ScaleLayer scaleLayer = indicatorLayer.getScaleLayer(scale
						.intValue());
				indicatorLayer.setVisibleByUser(scaleLayer,
						!scaleLayer.isVisible());
			}

			indicatorLayer.setSelectedScale(null);

			chart.update(true);
		}
		return null;
	}
}
