package com.mfg.chart.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.IChartLayer;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.model.IScaledIndicatorModel;
import com.mfg.chart.ui.views.AbstractChartView;

public class ShowIndicatorLayerHandler extends ChartActionHandler {

	@Override
	protected void executeWhenNoActionRegistered(AbstractChartView view,
			ExecutionEvent event) {
		Chart chart = view.getChart();
		String id = event.getCommand().getId();
		execute(chart, id);
	}

	public static boolean execute(Chart chart, String id) {
		final IndicatorLayer indicatorLayer = chart.getIndicatorLayer();

		if (indicatorLayer != null) {

			final String[] split = id.split("\\.");
			final String layerName = split[split.length - 1];

			final Integer scale = indicatorLayer.getSelectedScale();

			IScaledIndicatorModel indicatorModel = chart.getModel()
					.getScaledIndicatorModel();
			if (scale == null
					|| scale.intValue() >= indicatorModel.getFirstScale()
					&& scale.intValue() <= indicatorModel.getScaleCount()) {
				IChartLayer layer;

				if (scale == null) {
					layer = indicatorLayer.getGlobalLayer().getLayer(layerName);
				} else {
					final ScaleLayer scaleLayer = indicatorLayer
							.getScaleLayer(scale.intValue());
					layer = scaleLayer.getLayer(layerName);
				}

				final boolean oldVisible = layer.isEnabled()
						&& layer.isVisible();
				final boolean newVisible = !oldVisible;

				layer.setEnabled(true);
				indicatorLayer.setVisibleByUser(layer, newVisible);

				indicatorLayer.setSelectedScale(null);

				chart.fireRangeChanged();

				return newVisible;
			}
		}
		return false;
	}
}
