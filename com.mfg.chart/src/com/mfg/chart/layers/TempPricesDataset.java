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

import java.awt.geom.Point2D;

import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.model.ITemporalPricesModel;

/**
 * @author arian
 * 
 */
public class TempPricesDataset implements IDataset {
	private final ITemporalPricesModel _model;
	private final int _dataLayer;

	public TempPricesDataset(int dataLayer, ITemporalPricesModel model) {
		super();
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
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getItemCount(int)
	 */
	@Override
	public int getItemCount(int series) {
		int res = _model.getTempTick(_dataLayer) == null ? 0 : 2;
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getX(int, int)
	 */
	@Override
	public double getX(int series, int item) {
		Point2D finalTick = _model.getLastFinalTick(_dataLayer);
		Point2D tmpTick = _model.getTempTick(_dataLayer);
		if (tmpTick == null || finalTick == null)
			return 0;
		return item == 0 ? finalTick.getX() : tmpTick.getX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getY(int, int)
	 */
	@Override
	public double getY(int series, int item) {
		Point2D finalTick = _model.getLastFinalTick(_dataLayer);
		Point2D tmpTick = _model.getTempTick(_dataLayer);
		if (tmpTick == null || finalTick == null)
			return 0;
		return item == 0 ? finalTick.getY() : tmpTick.getY();
	}

}
