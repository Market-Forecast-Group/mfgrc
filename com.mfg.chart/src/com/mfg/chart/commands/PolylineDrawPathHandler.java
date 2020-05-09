package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.PolylineTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class PolylineDrawPathHandler extends AbstractChartViewHanlder {

	public static final String CMD_ID = "com.mfg.chart.commands.polylinesDrawPath";

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		execute(chart);
		return null;
	}

	public static void execute(Chart chart) {
		for (InteractiveTool t : chart.getTools()) {
			if (t instanceof PolylineTool) {
				PolylineTool t2 = (PolylineTool) t;
				t2.setDrawPath(!t2.isDrawPath());
				t2.repaint();
				break;
			}
		}
	}

}
