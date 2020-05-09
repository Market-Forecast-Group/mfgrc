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

package com.mfg.chart.layers;

import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.model.IRealTimeZZModel;

/**
 * @author arian
 * 
 */
public class RealTimeZZDataset implements IDataset {

	private final IRealTimeZZModel _model;
	private final int _dataLayer;

	/**
	 * @param model
	 */
	public RealTimeZZDataset(int dataLayer, final IRealTimeZZModel model) {
		_model = model;
		_dataLayer = dataLayer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getSeriesCount()
	 */
	@Override
	public int getSeriesCount() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public int getItemCount(final int series) {
		return _model.isCompleted(_dataLayer) ? 2 : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getX(int, int)
	 */
	@Override
	public double getX(final int series, final int item) {
		return item == 0 ? _model.getTime1(_dataLayer) : _model
				.getTime2(_dataLayer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getY(int, int)
	 */
	@Override
	public double getY(final int series, final int item) {
		return item == 0 ? _model.getPrice1(_dataLayer) : _model
				.getPrice2(_dataLayer);
	}
}
