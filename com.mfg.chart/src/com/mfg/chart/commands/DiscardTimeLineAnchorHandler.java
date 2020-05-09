package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.mfg.opengl.chart.GLChart;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.TimeLinesTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class DiscardTimeLineAnchorHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		GLChart glChart = chart.getGLChart();
		TimeLinesTool tool = (TimeLinesTool) glChart.getSelectedTool();
		tool.discardFirstAnchor();
		return null;
	}

}
