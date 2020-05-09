package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.ui.views.AbstractChartView;

public class ContractRangeHandler extends AbstractChartViewHanlder{

	@Override
	protected Object execute(final AbstractChartView view, final ExecutionEvent event) {
		view.getChart().expandPriceRange(-0.25);
		return null;
	}

}
