package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.ui.views.AbstractChartView;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.chart.ui.views.IChartView;

public class SwapFakePhysicalTimesHandler extends AbstractChartViewHanlder {
	public static final String CMD_ID = "com.mfg.chart.commands.swapPhysicalFakeTimes";

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		execute(view);
		return null;
	}

	public static void execute(IChartView view) {
		if (view instanceof ChartView) {
			final ChartView chartView = (ChartView) view;
			chartView.swapFakePhysicalTimes();
		}
	}
}
