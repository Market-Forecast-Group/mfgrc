package com.mfg.symbols.inputs.ui.views;

import com.mfg.chart.model.IChartModel;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

public class PhysicalParallelRealTimeZZModel extends ParallelRealTimeZZModel {

	public PhysicalParallelRealTimeZZModel(LayeredIndicator layeredIndicator,
			int level, IChartModel chartModel) {
		super(layeredIndicator, level, chartModel);
	}

	@Override
	protected long getTime(int dataLayer, long fakeTime) {
		long lower = getPriceModel().getLowerDisplayTime(dataLayer);
		MultiscaleIndicator multiscaleIndicator = getIndicator().getLayers()
				.get(dataLayer);
		return multiscaleIndicator.getPhysicalTimeAt((int) fakeTime) - lower;
	}

}
