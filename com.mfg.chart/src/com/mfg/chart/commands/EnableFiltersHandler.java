package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.Bands2Layer;
import com.mfg.chart.layers.ChannelLayer;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.PriceLayer;
import com.mfg.chart.ui.osd.GlobalScaleElementLayer;
import com.mfg.chart.ui.views.AbstractChartView;

public class EnableFiltersHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		Chart chart = view.getChart();
		IndicatorLayer indLayer = chart.getIndicatorLayer();
		if (indLayer != null) {
			for (GlobalScaleElementLayer layer : indLayer.getGlobalLayer()
					.getLayers()) {
				if (!layer.getName().equals(ChannelLayer.LAYER_NAME)
						&& !layer.getName().equals(Bands2Layer.LAYER_NAME_2)) {
					layer.setEnabled(true);
				}
			}
			indLayer.setFiltersEnabled(true);
		}

		PriceLayer priceLayer = chart.getPriceLayer();
		if (priceLayer != null) {
			priceLayer.getSettings().zzCompression = true;
		}

		chart.setAutoDataLayer(true);
		chart.fireRangeChanged();
		return null;
	}

}
