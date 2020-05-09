package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.ui.views.AbstractChartView;

public class ScrollLittleRightHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(final AbstractChartView view, final ExecutionEvent event) {
		view.getChart().shift(0.9 / 2);
		return null;
	}

}
