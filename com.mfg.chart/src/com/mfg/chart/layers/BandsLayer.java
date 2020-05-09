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
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.painters.LineStripPainter;
import com.mfg.chart.layers.IndicatorLayer.ARCSettings;
import com.mfg.chart.model.IBandsCollection;
import com.mfg.chart.model.IBandsModel;
import com.mfg.chart.ui.IChartUtils;

class BandsDataset implements IDataset {

	private final IBandsCollection _list;

	public BandsDataset(final IBandsCollection list) {
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
			return _list.getTopPrice(item);
		case 1:
			return _list.getCenterPrice(item);
		case 2:
			return _list.getBottomPrice(item);
		}
		assert false;
		return 0;
	}
}

public class BandsLayer extends FinalScaleElementLayer {

	/**
	 * 
	 */
	public static final String LAYER_NAME = "Bands";

	private final IBandsModel model;
	private final DatasetDelegate dataset;

	public BandsLayer(final ScaleLayer scale) {
		this(LAYER_NAME, scale);
	}

	public BandsLayer(String name, final ScaleLayer scale) {
		super(name, "B", scale, BITMAP_BANDS);
		model = _chart.getModel().getScaledIndicatorModel()
				.getBandsModel(getLevel());
		dataset = new DatasetDelegate(IChartUtils.EMPTY_DATASET);

		_chart.addDataset(dataset, new LineStripPainter(this) {
			@Override
			public int getFactor(int series) {
				if (series == 1) {
					return _chart.getIndicatorLayer().getArcSettings().bandsCenterType;
				}
				return _chart.getIndicatorLayer().getArcSettings().bandsTopBottomType;
			}

			@Override
			public float getWidth(int series) {
				if (series == 1) {
					return _chart.getIndicatorLayer().getArcSettings().bandsCenterWidth;
				}
				return _chart.getIndicatorLayer().getArcSettings().bandsTopBottomWidth;
			}
		});
	}

	@Override
	public boolean isEnabled() {
		return getSettings().bandsEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		getSettings().bandsEnabled = enabled;
	}

	private ARCSettings getSettings() {
		return _chart.getIndicatorLayer().getArcSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#getDefaultLayerWidth()
	 */
	@Override
	public float getDefaultLayerWidth() {
		return 1f;
	}

	@Override
	public void updateDataset() {
		int dataLayer = _chart.getDataLayer();
		final PlotRange range = _chart.getXRange();
		final IBandsCollection list = model.getBands(dataLayer,
				(long) range.lower, (long) range.upper);
		dataset.setBase(createBandsDataset(list));
	}

	public boolean containsInRange() {
		int dataLayer = _chart.getDataLayer();
		PlotRange range = _chart.getXRange();
		boolean result = model.containsDataIn(dataLayer, (long) range.lower,
				(long) range.upper);
		return result;
	}

	@SuppressWarnings("static-method")
	// Used on inner classes
	protected IDataset createBandsDataset(final IBandsCollection list) {
		return new BandsDataset(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractScaleLayer#setEmptyDatasets()
	 */
	@Override
	public void clearDatasets() {
		dataset.setBase(IChartUtils.EMPTY_DATASET);
	}

	@Override
	public IDataset getAutorangeDataset() {
		return dataset;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("static-method")
	// Used on inner classes
	public float getDefaultCenterLineWidth() {
		return 2;
	}
}
