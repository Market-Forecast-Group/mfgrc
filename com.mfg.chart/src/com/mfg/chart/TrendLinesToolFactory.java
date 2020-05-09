package com.mfg.chart;

import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.TrendLinesTool;
import com.mfg.chart.ui.osd.tools.InteractiveToolFactory;

public class TrendLinesToolFactory extends InteractiveToolFactory {

	public TrendLinesToolFactory() {
	}

	@Override
	public InteractiveTool createTool(Chart chart) {
		return new TrendLinesTool(chart);
	}

}
