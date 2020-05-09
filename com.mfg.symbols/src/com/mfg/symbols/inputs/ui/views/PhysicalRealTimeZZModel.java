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
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * @author arian
 * 
 */
public class PhysicalRealTimeZZModel extends RealTimeZZModel {

	public PhysicalRealTimeZZModel(LayeredIndicator layeredIndicator,
			int level, IChartModel chartModel) {
		super(layeredIndicator, level, chartModel);
	}

	@Override
	public long getTime1(int dataLayer) {
		long lower = _chartModel.getPriceModel().getLowerDisplayTime(dataLayer);
		MultiscaleIndicator ind = getIndicator(dataLayer);
		long ptime = ind.getPhysicalTimeAt((int) super.getTime1(dataLayer));
		return ptime - lower;
	}

	@Override
	public long getTime2(int dataLayer) {
		long lower = _chartModel.getPriceModel().getLowerDisplayTime(dataLayer);
		return getIndicator(dataLayer).getPhysicalTimeAt(
				(int) super.getTime2(dataLayer))
				- lower;
	}

}
