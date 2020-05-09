package com.mfg.chart.ui.interactive;

import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.osd.tools.InteractiveToolFactory;

public class LineToolFactory extends InteractiveToolFactory {

	public LineToolFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public InteractiveTool createTool(Chart chart) {
		return new LineTool(chart);
	}

}
