package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.layers.PriceLayer;
import com.mfg.chart.layers.PriceLayer.PriceSettings;
import com.mfg.chart.ui.views.AbstractChartView;

public class ShowAllPricesHandle extends AbstractChartViewHanlder {

	@Override
	protected Object execute(final AbstractChartView view,
			final ExecutionEvent event) {
		final PriceLayer priceLayer = view.getChart().getPriceLayer();
		PriceSettings s = priceLayer.getSettings();
		s.zzCompression = !s.zzCompression;
		view.getChart().update(view.getChart().isAutoRangeEnabled());
		return null;
	}

}
