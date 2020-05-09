package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.mfg.opengl.chart.GLChart;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.IAnchorTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class DiscardLineFirstAnchorHandler extends AbstractChartViewHanlder {

	public static final String CMD_ID = "com.mfg.chart.commands.discardFirstAnchor";
	
	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		execute(chart);
		return null;
	}

	public static void execute(Chart chart) {
		GLChart glChart = chart.getGLChart();
		InteractiveTool tool = glChart.getSelectedTool();
		if (tool != null && tool instanceof IAnchorTool) {
			((IAnchorTool) tool).discardFirstAnchor();
		}
	}

}
