/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */
/**
 *
 */
package com.mfg.chart.layers;

import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.model.ITimePriceSegmentCollection;

/**
 * @author arian
 *
 */
public class TimePriceSegmentsDataset implements IDataset {

	private final ITimePriceSegmentCollection collection;

	public TimePriceSegmentsDataset(final ITimePriceSegmentCollection collection1) {
		this.collection = collection1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.IDataset#getSeriesCount()
	 */
	@Override
	public int getSeriesCount() {
		return collection.getSize();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.IDataset#getItemCount(int)
	 */
	@Override
	public int getItemCount(final int series) {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.IDataset#getX(int, int)
	 */
	@Override
	public double getX(final int series, final int item) {
		switch (item) {
		case 0:
			return collection.getTime0(series);
		case 1:
			return collection.getTime1(series);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.IDataset#getY(int, int)
	 */
	@Override
	public double getY(final int series, final int item) {
		switch (item) {
		case 0:
			return collection.getPrice0(series);
		case 1:
			return collection.getPrice1(series);
		}
		return 0;
	}

}
