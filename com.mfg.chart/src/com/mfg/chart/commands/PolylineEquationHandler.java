package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.ui.interactive.PolylineTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class PolylineEquationHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		String[] split = event.getCommand().getId().split("_");
		int n = Integer.parseInt(split[split.length - 1]);
		selectPolylineCombination(view, n);
		return null;
	}

	public static void selectPolylineCombination(AbstractChartView view,
			int combination) {

		PolylineTool tool = (PolylineTool) view.getChart().getGLChart()
				.getSelectedTool();
		tool.setCombination(combination);
		tool.repaint();
	}

}
