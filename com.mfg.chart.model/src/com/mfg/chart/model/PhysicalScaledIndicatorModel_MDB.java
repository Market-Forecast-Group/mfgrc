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
package com.mfg.chart.model;

import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;

/**
 * @author arian
 * 
 */
public class PhysicalScaledIndicatorModel_MDB extends ScaledIndicatorModel_MDB {

	public PhysicalScaledIndicatorModel_MDB(IndicatorMDBSession session,
			ChartModel_MDB chartModel) {
		super(session, chartModel);
	}

	@Override
	protected BandsModel_MDB createBandsmodel(int level) {
		return new PhysicalBandsModel_MDB(_indicatorSession, level,
				getChartModel());
	}

	@Override
	protected IChannelModel createChannelModel(int level) {
		return new PhysicalChannelModel_MDB(_indicatorSession, level,
				getChartModel());
	}

	@Override
	protected PivotModel_MDB createPivotModel(int level) {
		return new PhysicalPivotModel_MDB(_indicatorSession, level,
				getChartModel());
	}
}
