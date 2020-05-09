package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.mfg.opengl.chart.GLChart;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.PolylineTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class DiscardPolylineFirstAnchorHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		GLChart glChart = chart.getGLChart();
		PolylineTool tool = (PolylineTool) glChart.getSelectedTool();
		tool.discardFirstAnchor();
		return null;
	}

}
