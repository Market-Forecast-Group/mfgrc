package com.mfg.chart.ui.interactive;

import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.osd.tools.InteractiveToolFactory;

public class ForecastingToolFactory extends InteractiveToolFactory {
	@Override
	public InteractiveTool createTool(Chart chart) {
		return new ForecastingTool(chart);
	}

}
