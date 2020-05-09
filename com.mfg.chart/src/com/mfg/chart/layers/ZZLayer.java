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

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.painters.BitmapPainter;
import com.mfg.chart.backend.opengl.painters.LineStripPainter;
import com.mfg.chart.layers.IndicatorLayer.ARCSettings;
import com.mfg.chart.model.IParallelRealTimeZZModel;
import com.mfg.chart.model.IPivotCollection;
import com.mfg.chart.model.IPivotModel;
import com.mfg.chart.model.IRealTimeZZModel;
import com.mfg.chart.model.IScaledIndicatorModel;
import com.mfg.chart.ui.IChartUtils;

class PivotDataset implements IDataset {
	private final IPivotCollection _data;

	public PivotDataset(final IPivotCollection data) {
		this._data = data;
	}

	@Override
	public int getSeriesCount() {
		return 1;
	}

	@Override
	public int getItemCount(final int series) {
		return _data.getSize();
	}

	@Override
	public double getX(final int series, final int item) {
		return _data.getTime(item);
	}

	@Override
	public double getY(final int series, final int item) {
		return _data.getPrice(item);
	}

	/**
	 * 
	 * @param series
	 * @param item
	 * @return
	 */
	public boolean isUp(final int series, final int item) {
		return _data.isUp(item);
	}

}

class THDataset implements IDataset {

	private final IPivotCollection _data;

	public THDataset(final IPivotCollection data) {
		this._data = data;
	}

	@Override
	public int getSeriesCount() {
		return 1;
	}

	@Override
	public int getItemCount(final int series) {
		return _data.getSize();
	}

	@Override
	public double getX(final int series, final int item) {
		return _data.getTHTime(item);
	}

	@Override
	public double getY(final int series, final int item) {
		return _data.getTHPrice(item);
	}

}

class RTTHDataset implements IDataset {
	private IRealTimeZZModel _model;
	private int _dataLayer;

	public RTTHDataset(int dataLayer, IRealTimeZZModel zzModel) {
		_model = zzModel;
		_dataLayer = dataLayer;
	}

	@Override
	public int getSeriesCount() {
		return _model.isCompleted(_dataLayer) ? 1 : 0;
	}

	@Override
	public int getItemCount(int series) {
		return 1;
	}

	@Override
	public double getX(int series, int item) {
		return _model.getTHTime(_dataLayer);
	}

	@Override
	public double getY(int series, int item) {
		return _model.getTHPrice(_dataLayer);
	}

}

public class ZZLayer extends FinalScaleElementLayer {

	public static final String LAYER_NAME = "Zz";

	private final DatasetDelegate _pivotDataset;
	private final IPivotModel _model;
	private final DatasetDelegate _thDataset;
	private final DatasetDelegate _rtTHDataset;
	private final DatasetDelegate _realTimeZZDataset;

	private int lastCountObjects;

	public ZZLayer(final ScaleLayer scale) {
		super(LAYER_NAME, "Z", scale, BITMAP_ZZ);
		_model = _chart.getModel().getScaledIndicatorModel()
				.getPivotModel(getLevel());

		_pivotDataset = new DatasetDelegate(IChartUtils.EMPTY_DATASET);
		final LineStripPainter painter = new LineStripPainter(this) {
			@Override
			public float getWidth(int series) {
				return getSettings().zzWidth;
			}

			@Override
			public int getFactor(int series) {
				return getSettings().zzType;
			}
		};
		_chart.addDataset(_pivotDataset, painter);

		_realTimeZZDataset = new DatasetDelegate(EMPTY_DATASET);

		_chart.addDataset(_realTimeZZDataset, painter);

		BitmapPainter thPainter = new BitmapPainter(this, BITMAP_BIG_DOT) {
			@Override
			protected BitmapData getBitmap() {
				ARCSettings s = getSettings();
				return SHAPES[s.thShapeWidth][s.thShapeType];
			}
		};
		_thDataset = new DatasetDelegate(EMPTY_DATASET);
		_chart.addDataset(_thDataset, thPainter);

		_rtTHDataset = new DatasetDelegate(EMPTY_DATASET);
		_chart.addDataset(_rtTHDataset, thPainter);

	}

	@Override
	public boolean isEnabled() {
		return getSettings().zzEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		getSettings().zzEnabled = enabled;
	}

	ARCSettings getSettings() {
		return _chart.getIndicatorLayer().getArcSettings();
	}

	@Override
	public void updateDataset() {
		int dataLayer = _chart.getDataLayer();
		final PlotRange range = _chart.getXRange();

		// zz pivots
		final IPivotCollection data = _model.getNegPivots(dataLayer,
				(long) range.lower, (long) range.upper);
		_pivotDataset.setBase(new PivotDataset(data));
		_thDataset.setBase(new THDataset(data));

		// real-time zz
		IScaledIndicatorModel indModel = _chart.getModel()
				.getScaledIndicatorModel();
		IDataset zzDs;
		IRealTimeZZModel zzModel = indModel.getRealTimeZZModel(getLevel());
		if (isZZParallel()) {
			IParallelRealTimeZZModel zzParallelModel = indModel
					.getParallelRealTimeZZModel(getLevel());
			zzDs = new ParallelRealTimeZZDataset(
					zzParallelModel.getRealtimeZZ(dataLayer));
		} else {
			zzDs = new RealTimeZZDataset(dataLayer, zzModel);
		}
		_realTimeZZDataset.setBase(zzDs);
		_rtTHDataset.setBase(new RTTHDataset(dataLayer, zzModel));

	}

	private boolean isZZParallel() {
		return getSettings().zzParallel;
	}

	/**
	 * @return the objectsCount
	 */
	public int countObjects() {
		int dataLayer = _chart.getDataLayer();
		final PlotRange range = _chart.getXRange();
		lastCountObjects = _model.countNegPivots(dataLayer, (long) range.lower,
				(long) range.upper);
		return lastCountObjects;
	}

	/**
	 * @return the lastCountObjects
	 */
	public int getLastCountObjects() {
		return lastCountObjects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractScaleLayer#setEmptyDatasets()
	 */
	@Override
	public void clearDatasets() {
		_pivotDataset.setBase(IChartUtils.EMPTY_DATASET);
		_thDataset.setBase(IChartUtils.EMPTY_DATASET);
		_rtTHDataset.setBase(EMPTY_DATASET);
		_realTimeZZDataset.setBase(IChartUtils.EMPTY_DATASET);
	}

	@Override
	public IDataset getAutorangeDataset() {
		return _pivotDataset;
	}

	public DatasetDelegate getPivotDataset() {
		return _pivotDataset;
	}
}
