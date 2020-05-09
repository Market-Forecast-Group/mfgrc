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

import com.mfg.chart.model.IRealTimeChannelModel;

/**
 * @author arian
 * 
 */
public class RealTimeChannelDataset implements IDataset {

	private final IRealTimeChannelModel _model;
	private final int _dataLayer;

	public RealTimeChannelDataset(int dataLayer, IRealTimeChannelModel model) {
		super();
		this._model = model;
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
		return _model.isComputed(_dataLayer) ? 6 : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getX(int, int)
	 */
	@Override
	public double getX(int series, int item) {
		switch (item) {
		case 0:
		case 2:
		case 4:
			return _model.getStartTime(_dataLayer);
		default:
			return _model.getEndTime(_dataLayer);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.IDataset#getY(int, int)
	 */
	@Override
	public double getY(int series, int item) {
		switch (item) {
		case 0:
			return _model.getStartTopPrice(_dataLayer);
		case 1:
			return _model.getEndTopPrice(_dataLayer);
		case 2:
			return _model.getStartCenterPrice(_dataLayer);
		case 3:
			return _model.getEndCenterPrice(_dataLayer);
		case 4:
			return _model.getStartBottomPrice(_dataLayer);
		case 5:
			return _model.getEndBottomPrice(_dataLayer);
		}

		return 0;
	}
}
