package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.mfg.opengl.chart.GLChart;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.HarmonicLinesTool;
import com.mfg.chart.ui.interactive.HarmonicLinesTool.VisibleLines;
import com.mfg.chart.ui.views.AbstractChartView;

public class HarmonyLinesChangeLevelHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		String cmdId = event.getCommand().getId();
		Chart chart = view.getChart();
		execute(cmdId, chart);
		return null;
	}

	public static void execute(String cmdId, Chart chart) {
		GLChart glChart = chart.getGLChart();
		HarmonicLinesTool tool = null;
		for (InteractiveTool tool2 : glChart.getTools()) {
			if (tool2 instanceof HarmonicLinesTool) {
				tool = (HarmonicLinesTool) tool2;
				break;
			}
		}
		if (tool != null) {

			boolean up = cmdId.endsWith("_up");

			VisibleLines v = tool.getVisibiles();

			if (up) {
				if (v.lines3) {
					v.lines4 = true;
				} else if (v.lines2) {
					v.lines3 = true;
				} else {
					v.lines2 = true;
				}
			} else {
				if (v.lines4) {
					v.lines4 = false;
				} else if (v.lines3) {
					v.lines3 = false;
				} else {
					v.lines2 = false;
				}
			}
			chart.syncRepaint();
		}
	}
}
