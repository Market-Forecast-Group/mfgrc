package com.mfg.chart.ui.settings.global;

import com.mfg.chart.backend.opengl.Chart;

public class ChartSettingsInput {
	private final Chart _chart;

	public ChartSettingsInput(Chart chart) {
		_chart = chart;
	}

	public Chart getChart() {
		return _chart;
	}
}
