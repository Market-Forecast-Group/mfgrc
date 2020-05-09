package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.TimeLinesTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class TimeLineGeneralSettingsHandler extends AbstractChartViewHanlder {
	public static final String CMD_ID = "com.mfg.chart.commands.timeLinesGeneralSettings";

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		execute(chart);

		return null;
	}

	public static void execute(Chart chart) {
		TimeLinesTool tool = chart.getTool(TimeLinesTool.class);
		Shell activeShell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		chart.openSettingsWindow(activeShell, tool, null);
	}

}
