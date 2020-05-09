package com.mfg.chart.layers;

import java.util.List;

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.PlotRange;
import org.mfg.opengl.chart.SimplePainter;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.painters.AbstractBitmapMultiColorPainter;
import com.mfg.chart.backend.opengl.painters.BitmapPainter;
import com.mfg.chart.backend.opengl.painters.LineStripMultiColorPainter;
import com.mfg.chart.layers.IndicatorLayer.ARCSettings;
import com.mfg.chart.model.ISyntheticModel;
import com.mfg.chart.model.ISyntheticModel.PivotPoint;
import com.mfg.chart.model.ITimePriceCollection;
import com.mfg.chart.ui.IChartUtils;

public class SyntheticLayer extends FinalLayer {

	private static final int START_SCALE = 1;
	protected final ISyntheticModel _model;
	DatasetDelegate _zzDataset;
	private DatasetDelegate _priceDataset;

	public SyntheticLayer(Chart chart) {
		super("Synthetic Layer", chart);
		_model = chart.getModel().getSyntheticModel();
		_zzDataset = new DatasetDelegate(IChartUtils.EMPTY_DATASET);
		_priceDataset = new DatasetDelegate(IChartUtils.EMPTY_DATASET);
		// draw ZZ
		chart.addDataset(_zzDataset, new LineStripMultiColorPainter() {
			@Override
			public float[] getColor(int series, int item) {
				return scaleColor(series);
			}
		});

		// draw pivots
		chart.addDataset(_zzDataset, new AbstractBitmapMultiColorPainter() {
			@Override
			public BitmapData getBitmap(final IDataset ds, final int series,
					final int item) {
				final SynthDataset pds = (SynthDataset) ((DatasetDelegate) ds)
						.getBase();

				PivotPoint point = pds.getPoint(series, item);
				Boolean downSwing = point.downSwing;
				if (downSwing == null) {
					return null;
				}

				// TODO: remove the negative condition
				final boolean negative = true;

				final BitmapData upBmp = negative ? BITMAP_PIVOT_UP
						: BITMAP_PIVOT_RIGTH;
				final BitmapData downBmp = negative ? BITMAP_PIVOT_DOWN
						: BITMAP_PIVOT_LEFT;

				final boolean up = !downSwing.booleanValue();

				return up ? upBmp : downBmp;
			}

			@Override
			public float[] getColor(final IDataset ds, final int series,
					final int item) {
				final SynthDataset pds = (SynthDataset) ((DatasetDelegate) ds)
						.getBase();
				PivotPoint point = pds.getPoint(series, item);
				boolean up = !point.downSwing.booleanValue();
				return up ? COLOR_DARK_GREEN : COLOR_DARK_RED;
			}
		});

		// draw ZZ
		IDataset thDataset = new IDataset() {

			@Override
			public double getY(int series, int item) {
				SynthDataset ds = (SynthDataset) _zzDataset.getBase();
				return ds.getPoint(series, item).thY;
			}

			@Override
			public double getX(int series, int item) {
				SynthDataset ds = (SynthDataset) _zzDataset.getBase();
				return ds.getPoint(series, item).thX;
			}

			@Override
			public int getSeriesCount() {
				return _zzDataset.getSeriesCount();
			}

			@Override
			public int getItemCount(int series) {
				int count = _zzDataset.getItemCount(series);
				// ignore real-time swing
				return count == 0 ? 0 : count - 1;
			}
		};

		// draw TH
		chart.addDataset(thDataset, new BitmapPainter(this, BITMAP_BIG_DOT) {
			@Override
			protected BitmapData getBitmap() {
				ARCSettings s = _chart.getIndicatorLayer().getArcSettings();
				return SHAPES[s.thShapeWidth][s.thShapeType];
			}

			@Override
			protected float[] getColor(int series, int item) {
				return scaleColor(series);
			}
		});

		chart.addDataset(_priceDataset, new SimplePainter(COLOR_WHITE));
	}

	class SynthDataset implements IDataset {

		private List<List<PivotPoint>> _data;

		public SynthDataset(List<List<PivotPoint>> data) {
			_data = data;
		}

		@Override
		public int getSeriesCount() {
			return _data.size();
		}

		@Override
		public int getItemCount(int series) {
			return _data.get(series).size();
		}

		@Override
		public double getX(int series, int item) {
			return _data.get(series).get(item).x;
		}

		@Override
		public double getY(int series, int item) {
			return _data.get(series).get(item).y;
		}

		public PivotPoint getPoint(int series, int item) {
			return _data.get(series).get(item);
		}

	}

	@Override
	public void updateDataset() {
		if (isEnabled()) {
			{
				List<List<PivotPoint>> data = _model.getZigZagDataset();
				_zzDataset.setBase(new SynthDataset(data));
			}
			{
				ITimePriceCollection data = _model.getSecondScalePrices();
				_priceDataset.setBase(new TimePriceDataset(data));
			}
		}
	}

	@Override
	public IDataset getAutorangeDataset() {
		return _zzDataset;
	}

	@Override
	public float[] getDefaultLayerColor() {
		return COLOR_WHITE;
	}

	@Override
	public void clearDatasets() {
		_zzDataset.setBase(IChartUtils.EMPTY_DATASET);
	}

	@Override
	public int getDefaultLayerStippleFactor() {
		return STIPPLE_FACTOR_NULL;
	}

	public IDataset getCrossSnappingDataset() {
		return _zzDataset;
	}

	@Override
	public void autorange() {
		IChartUtils.autorangeAll(_chart, getAutorangeDataset());
		fixrange(_chart.getYRange(), 0.2);
		fixrange(_chart.getXRange(), 0.2);
	}

	float[] scaleColor(int series) {
		int scale = series + START_SCALE;
		return getScaleColor(scale);
	}

	public float[] getScaleColor(int scale) {
		float[][] colors = _chart.getIndicatorLayer().getScalesSettings().scalesColors;
		return colors[scale % colors.length];
	}

	private static void fixrange(PlotRange range, double factor) {
		double margin = range.getLength() * factor;
		range.lower -= margin;
		range.upper += margin;
	}

}
