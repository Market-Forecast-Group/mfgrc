package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.layers.PriceLayer;
import com.mfg.chart.ui.views.AbstractChartView;

public class ShowPriceHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(final AbstractChartView view, final ExecutionEvent event) {
		final PriceLayer priceLayer = view.getChart().getPriceLayer();
		final boolean visible = !priceLayer.isVisible();
		priceLayer.setVisible(visible);
		view.getChart().update(view.getChart().isAutoRangeEnabled());
		return null;
	}

}
