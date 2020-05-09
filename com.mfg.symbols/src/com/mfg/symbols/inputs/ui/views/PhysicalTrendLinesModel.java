/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.inputs.ui.views;

import com.mfg.chart.model.IChartModel;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * @author arian
 * 
 */
public class PhysicalTrendLinesModel extends TrendLinesModel {

	public PhysicalTrendLinesModel(LayeredIndicator indicator,
			IChartModel chartModel) {
		super(indicator, chartModel);
	}

	@Override
	protected int getDisplayTime(int dataLayer, QueueTick tick) {
		long lower = _chartModel.getPriceModel().getLowerDisplayTime(dataLayer);
		return (int) (tick.getPhysicalTime() - lower);
	}

	@Override
	protected long getDisplayTime(int dataLayer, Pivot pivot) {
		long time = pivot.getPivotTime();
		return getDisplayTime(dataLayer, time);
	}

	@Override
	protected long getDisplayTHTime(int dataLayer, Pivot pivot) {
		return getDisplayTime(dataLayer, pivot.getConfirmTime());
	}

	private long getDisplayTime(int dataLayer, long time) {
		long lower = _chartModel.getPriceModel().getLowerDisplayTime(dataLayer);
		int layer = _chartModel.getDataLayerModel().getDataLayer();
		MultiscaleIndicator indLayer = getLayeredIndicator().getLayers().get(
				layer);

		long date = indLayer.getPhysicalTimeAt((int) time);
		return (int) (date - lower);
	}

}
