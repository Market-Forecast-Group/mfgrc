package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.RangeDialog;
import com.mfg.chart.ui.views.AbstractChartView;

public class ChangeRangesHandler extends AbstractChartViewHanlder {

	public static final String CMD_ID = "com.mfg.chart.commands.changeRanges";

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();

		execute(chart);

		return null;
	}

	public static void execute(Chart chart) {
		Shell activeShell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		RangeDialog dlg = new RangeDialog(activeShell);
		dlg.setTimeRange(chart.getXRange());
		dlg.setPriceRange(chart.getYRange());
		dlg.setAutoRange(chart.isAutoRangeEnabled());

		if (dlg.open() == Window.OK) {
			chart.setAutoRangeEnabled(dlg.isAutoRange());
			chart.setYRange(dlg.getPriceRange());
			chart.setXRange(dlg.getTimeRange());
			chart.update(chart.isAutoRangeEnabled());
			chart.syncRepaint();
		}
	}

}
