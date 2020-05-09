package com.mfg.chart.ui.interactive;

import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.osd.tools.InteractiveToolFactory;

public class HarmonicLinesToolFactory extends InteractiveToolFactory {

	@Override
	public InteractiveTool createTool(Chart chart) {
		return new HarmonicLinesTool(chart);
	}

}
