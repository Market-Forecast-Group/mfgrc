package com.mfg.chart.ui.interactive;

import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.osd.tools.InteractiveToolFactory;

public class TimeLinesToolFactory extends InteractiveToolFactory {

	@Override
	public InteractiveTool createTool(Chart chart) {
		if (chart.getType().hasChannels()) {
			return new TimeLinesTool(chart);
		}
		return null;
	}

}
