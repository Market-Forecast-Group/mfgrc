package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.HarmonicLinesTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class ShowHarmonicLinesSettingsHandler extends AbstractChartViewHanlder {
	public final static String CMD_ID = "com.mfg.chart.commands.showHarmonyLines_settings";

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		execute(chart);
		return null;
	}

	public static void execute(Chart chart) {
		Shell activeShell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		chart.openSettingsWindow(activeShell,
				chart.getTool(HarmonicLinesTool.class), null);
	}

}
