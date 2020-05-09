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

public class CompressedDataset extends DatasetDelegate {
	//private static final long serialVersionUID = 1L;

	private final int max;
	private double factor;


	public CompressedDataset(final IDataset realDataset, final int maxNumber) {
		super(realDataset);
		max = maxNumber;
	}


	@Override
	public double getX(final int series, final int item) {
		return super.getX(series, (int) (item * factor));
	}


	@Override
	public double getY(final int series, final int item) {
		return super.getY(series, (int) (item * factor));
	}


	@Override
	public int getItemCount(final int series) {
		int count = getBase().getItemCount(series);

		if (count > max) {
			factor = count / (double) max;
			count = max;
		} else {
			factor = 1;
		}

		return count;
	}

}
