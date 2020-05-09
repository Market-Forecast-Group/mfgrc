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

package com.mfg.chart.layers;

import org.mfg.opengl.chart.IDataset;

public class DatasetDelegate implements IDataset {

	private IDataset base;


	public DatasetDelegate(final IDataset realDataset) {
		base = realDataset;
	}


	@Override
	public int getSeriesCount() {
		return base.getSeriesCount();
	}


	@Override
	public int getItemCount(final int series) {
		return base.getItemCount(series);
	}


	@Override
	public double getX(final int series, final int item) {
		return base.getX(series, item);
	}


	@Override
	public double getY(final int series, final int item) {
		return base.getY(series, item);
	}


	public IDataset getBase() {
		return base;
	}


	public void setBase(final IDataset base1) {
		this.base = base1;
	}
}
