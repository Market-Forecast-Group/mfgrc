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

import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.widget.arc.strategy.LayeredIndicator;

/**
 * @author arian
 * 
 */
public class PhyisicalRealTimeChannelModel extends RealTimeChannelModel {

	public PhyisicalRealTimeChannelModel(LayeredIndicator layeredIndicator,
			int level, ChartModel_MDB chartModel) {
		super(layeredIndicator, level, chartModel);
	}

	@Override
	public long getStartTime(int dataLayer) {
		long lower = _chartModel.getPriceModel().getLowerDisplayTime(dataLayer);
		return getCurrentIndicatorLayer().getPhysicalTimeAt(
				(int) super.getStartTime(dataLayer))
				- lower;
	}

	@Override
	public long getEndTime(int dataLayer) {
		long lower = _chartModel.getPriceModel().getLowerDisplayTime(dataLayer);
		return getCurrentIndicatorLayer().getPhysicalTimeAt(
				(int) super.getEndTime(dataLayer))
				- lower;
	}
}
