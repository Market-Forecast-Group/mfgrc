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

import com.mfg.chart.model.IBandsCollection;

class Bands2Dataset implements IDataset {

	private final IBandsCollection _list;

	public Bands2Dataset(final IBandsCollection list) {
		this._list = list;
	}

	@Override
	public int getSeriesCount() {
		return 3;
	}

	@Override
	public int getItemCount(final int series) {
		return _list.getSize();
	}

	@Override
	public double getX(final int series, final int item) {
		return _list.getTime(item);
	}

	@Override
	public double getY(final int series, final int item) {
		switch (series) {
		case 0:
			return _list.getTopRaw(item);
		case 1:
			return _list.getCenterRaw(item);
		case 2:
			return _list.getBottomRaw(item);
		}
		assert false;
		return 0;
	}
}

public class Bands2Layer extends BandsLayer {
	public static final String LAYER_NAME_2 = "Bands2";

	public Bands2Layer(ScaleLayer scale) {
		super(LAYER_NAME_2, scale);
	}

	@Override
	public boolean isEnabled() {
		// TODO: put it in the ARC settings
		return false;
	}

	@Override
	protected IDataset createBandsDataset(IBandsCollection list) {
		return new Bands2Dataset(list);
	}

}
