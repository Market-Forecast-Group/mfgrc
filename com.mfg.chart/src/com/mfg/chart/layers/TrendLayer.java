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

import com.mfg.chart.backend.opengl.painters.LineStripPainter;
import com.mfg.chart.layers.IndicatorLayer.AdditionalSettings;
import com.mfg.chart.model.ITrendLinesModel;

/**
 * @author arian
 * 
 */
public class TrendLayer extends FinalScaleElementLayer {

	public static final String LAYER_NAME = "AutoTrendLines";
	private final DatasetDelegate _trendUpDataset;
	private final DatasetDelegate _trendDownDataset;
	private final DatasetDelegate _trendDashedDataset;
	private final ITrendLinesModel trendModel;

	class Painter extends LineStripPainter {
		public Painter(FinalLayer layer1) {
			super(layer1);
		}

		@Override
		public float[] getColor() {
			return _chart.getIndicatorLayer().getScalesSettings().scalesColors[getLevel()];
		}

		@Override
		public int getFactor(int series) {
			return _chart.getIndicatorLayer().getAdditionalSettings().autoTrendLinesLineType;
		}

		@Override
		public float getWidth(int series) {
			return _chart.getIndicatorLayer().getAdditionalSettings().autoTrendLinesLineWidth;
		}
	}

	/**
	 * @param name
	 * @param scale
	 */
	public TrendLayer(ScaleLayer scale) {
		super(LAYER_NAME, "L", scale, BITMAP_TREND_ICON);
		trendModel = _chart.getModel().getScaledIndicatorModel()
				.getTrendLinesModel();

		_trendUpDataset = new DatasetDelegate(EMPTY_DATASET);
		_trendDownDataset = new DatasetDelegate(EMPTY_DATASET);
		_trendDashedDataset = new DatasetDelegate(EMPTY_DATASET);

		LineStripPainter painter = new Painter(this);
		_chart.addDataset(_trendUpDataset, painter);
		_chart.addDataset(_trendDownDataset, painter);
		_chart.addDataset(_trendDashedDataset, new Painter(this) {
			@Override
			public int getFactor(int series) {
				return STIPPLE_FACTOR_4;
			}
		});
	}

	@Override
	public boolean isEnabled() {
		return getSettings().autoTrendLinesEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		getSettings().autoTrendLinesEnabled = enabled;
	}

	AdditionalSettings getSettings() {
		return _chart.getIndicatorLayer().getAdditionalSettings();
	}

	@Override
	public void updateDataset() {
		int dataLayer = getChart().getDataLayer();
		int level = getLevel();
		_trendUpDataset.setBase(new TimePriceDataset(trendModel.getUpLine(
				dataLayer, level)));
		_trendDownDataset.setBase(new TimePriceDataset(trendModel.getDownLine(
				dataLayer, level)));
		_trendDashedDataset.setBase(new TimePriceDataset(trendModel
				.getDashedLine(dataLayer, level)));
	}

	@Override
	public IDataset getAutorangeDataset() {
		return _trendUpDataset;
	}

	@Override
	public void clearDatasets() {
		_trendUpDataset.setBase(EMPTY_DATASET);
		_trendDownDataset.setBase(EMPTY_DATASET);
		_trendDashedDataset.setBase(EMPTY_DATASET);
	}

}
