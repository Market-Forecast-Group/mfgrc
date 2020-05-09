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

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.painters.AbstractBitmapMultiColorPainter;
import com.mfg.chart.layers.IndicatorLayer.ARCSettings;
import com.mfg.chart.model.IPivotCollection;
import com.mfg.chart.model.IPivotModel;

/**
 * @author arian
 * 
 */	
public class PivotLayer extends FinalScaleElementLayer {
	public static final String LAYER_NAME = "Pivots";
	public static final int MAX_ITEM_COUNT = 200;
	private final DatasetDelegate _pivotsDataset;
	private final IPivotModel model;

	public static class PivotPainter extends AbstractBitmapMultiColorPainter {

		@Override
		public BitmapData getBitmap(final IDataset ds, final int series,
				final int item) {
			final PivotDataset pds = (PivotDataset) ((DatasetDelegate) ds)
					.getBase();

			// TODO: remove the negative condition
			final boolean negative = true;

			final BitmapData upBmp = negative ? BITMAP_PIVOT_UP
					: BITMAP_PIVOT_RIGTH;
			final BitmapData downBmp = negative ? BITMAP_PIVOT_DOWN
					: BITMAP_PIVOT_LEFT;

			final boolean up = pds.isUp(series, item);

			return up ? upBmp : downBmp;
		}

		@Override
		public float[] getColor(final IDataset ds, final int series,
				final int item) {
			final PivotDataset pds = (PivotDataset) ((DatasetDelegate) ds)
					.getBase();
			boolean up = pds.isUp(series, item);
			return up ? COLOR_DARK_GREEN : COLOR_DARK_RED;

		}
	}

	public PivotLayer(final ScaleLayer scale) {
		super(LAYER_NAME, "M", scale, BITMAP_PIVOTS);
		model = _chart.getModel().getScaledIndicatorModel()
				.getPivotModel(getLevel());
		_pivotsDataset = new CompressedDataset(EMPTY_DATASET, MAX_ITEM_COUNT);
		_chart.addDataset(_pivotsDataset, new PivotPainter());
	}

	@Override
	public boolean isEnabled() {
		return getSettings().zzMarkersEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		getSettings().zzMarkersEnabled = enabled;
	}

	ARCSettings getSettings() {
		return _chart.getIndicatorLayer().getArcSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractScaleLayer#setEmptyDatasets()
	 */
	@Override
	public void clearDatasets() {
		_pivotsDataset.setBase(EMPTY_DATASET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractLayer#update()
	 */
	@Override
	public void updateDataset() {
		int dataLayer = _chart.getDataLayer();
		final PlotRange range = _chart.getXRange();
		IPivotCollection pivots = model.getNegPivots(dataLayer,
				(long) range.lower, (long) range.upper);

		final PivotDataset ds = new PivotDataset(pivots);
		_pivotsDataset.setBase(ds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractLayer#getAutorangeDataset()
	 */
	@Override
	public IDataset getAutorangeDataset() {
		return _pivotsDataset;
	}

	public DatasetDelegate getPivotsDataset() {
		return _pivotsDataset;
	}
}
