package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.views.AbstractChartView;

public class SelectScaleHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(final AbstractChartView view,
			final ExecutionEvent event) {
		final Chart chart = view.getChart();
		IndicatorLayer indicatorLayer = chart.getIndicatorLayer();
		if (indicatorLayer != null) {
			final String id = event.getCommand().getId();
			final String strNum = id.split("_")[1];
			Integer scale = Integer.valueOf(Integer.parseInt(strNum));
			indicatorLayer.setSelectedScale(scale);
			chart.update(chart.isAutoRangeEnabled());
		}
		return null;
	}
}
