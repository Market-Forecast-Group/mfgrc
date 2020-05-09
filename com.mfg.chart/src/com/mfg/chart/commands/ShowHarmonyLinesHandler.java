package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.mfg.opengl.chart.GLChart;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.HarmonicLinesTool;
import com.mfg.chart.ui.interactive.HarmonicLinesTool.Settings;
import com.mfg.chart.ui.views.AbstractChartView;

public class ShowHarmonyLinesHandler extends AbstractChartViewHanlder {
	public static String SHOW_HARMONIC_LINES_COMMAND = "com.mfg.chart.commands.showHarmonyLines";
	public static String SHOW_SECONDARY_HARMONIC_LINES_COMMAND = "com.mfg.chart.commands.showHarmonyLines_2";
	public static String SHOW_TERTIARY_HARMONIC_LINES_COMMAND = "com.mfg.chart.commands.showHarmonyLines_3";
	public static String SWAP_RATIO_HARMONIC_LINES_COMMAND = "com.mfg.chart.commands.showHarmonyLines_ratio";

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		execute(event.getCommand().getId(), chart);
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

			boolean line2 = cmdId.endsWith("2");
			boolean line3 = cmdId.endsWith("3");
			boolean ratio = cmdId.endsWith("ratio");

			if (line2 || line3 || ratio) {
				Settings settings = tool.getSettings();
				if (ratio) {
					settings.setPartition(settings.getPartition() == 2 ? 3 : 2);
				} else {
					settings.setPartition(line2 ? 3 : 2);
				}
			} else {
				tool.setAlwaysPaint(!tool.isAlwaysPaint());
			}
			chart.syncRepaint();
		}
	}

}
