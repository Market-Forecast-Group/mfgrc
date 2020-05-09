package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.interactive.LineTool;
import com.mfg.chart.ui.views.AbstractChartView;

public class LinesGeneralSettingsHandler extends AbstractChartViewHanlder {

	public static final String CMD_ID = "com.mfg.chart.commands.linesGeneralSettings";

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		execute(chart);
		return null;
	}

	public static void execute(Chart chart) {
		Shell activeShell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		chart.openSettingsWindow(activeShell, chart.getTool(LineTool.class),
				null);
	}

}
