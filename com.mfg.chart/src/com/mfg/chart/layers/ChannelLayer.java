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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.layers.IndicatorLayer.ARCSettings;
import com.mfg.chart.model.IChannel2Collection;
import com.mfg.chart.model.IChannelModel;
import com.mfg.chart.model.IRealTimeChannelModel;
import com.mfg.chart.model.IScaledIndicatorModel;

public class ChannelLayer extends FinalScaleElementLayer {

	class PolylinePainter implements ISeriesPainter {

		public PolylinePainter() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.mfg.opengl.chart.ISeriesPainter#paint(javax.media.opengl.GL,
		 * org.mfg.opengl.chart.IDataset, org.mfg.opengl.chart.PlotRange,
		 * org.mfg.opengl.chart.PlotRange)
		 */
		@Override
		public void paint(final GL2 gl, IDataset ds, final PlotRange xrange,
				final PlotRange yrange) {

			int factor = STIPPLE_FACTOR_NULL;

			gl.glPushAttrib(GL2.GL_LINE_BIT);
			ARCSettings s = getSettings();
			factor = s.channelsType;
			gl.glLineWidth(s.channelsWidth);

			if (factor != STIPPLE_FACTOR_NULL) {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
			}

			int seriesCount = ds.getSeriesCount();

			for (int series = 0; series < seriesCount; series++) {

				gl.glBegin(GL.GL_LINE_STRIP);
				gl.glColor4fv(getLayerColor(), 0);

				int itemsCount = ds.getItemCount(series);

				for (int item = 0; item < itemsCount; item++) {
					final double x = ds.getX(series, item);
					final double y = ds.getY(series, item);

					gl.glVertex2d(x, y);
				}
				gl.glEnd();
			}

			gl.glPopAttrib();

			if (factor != STIPPLE_FACTOR_NULL) {
				gl.glDisable(GL2.GL_LINE_STIPPLE);
			}
		}
	}

	class PolylineDataset implements IDataset {
		private final IChannel2Collection _data;

		PolylineDataset(IChannel2Collection data) {
			super();
			_data = data;
		}

		@Override
		public int getSeriesCount() {
			return _data.getSize() * 3;
		}

		@Override
		public int getItemCount(int series) {
			return 100;
		}

		@Override
		public double getX(int series, int item) {
			int channel = series / 3;

			double end = _data.getEnd(channel);

			int count = getItemCount(series);

			if (item == count - 1) {
				return end;
			}

			double start = _data.getStart(channel);
			double len = end - start;
			double x = start + item * len / count;
			return x;
		}

		@Override
		public double getY(int series, int item) {
			int channel = series / 3;
			double x = getX(series, item);
			double y = _data.evaluateCentarLine(channel, x);
			double y2;
			int position = series % 3;
			switch (position) {
			case 0:
				y2 = y + _data.getTopDistance(channel);
				break;
			case 1:
				y2 = y;
				break;
			default:
				y2 = y - _data.getBottomDistance(channel);
				break;
			}
			return y2;
		}
	}

	/**
	 * 
	 */
	public static final String LAYER_NAME = "Channels";
	// private final DatasetDelegate _lineDataset;
	// private final DatasetDelegate _shapeDataset;
	private final DatasetDelegate _rtDataset;
	private final IChannelModel _model;
	final DatasetDelegate _polylineDataset;

	public ChannelLayer(final ScaleLayer scale) {
		super(LAYER_NAME, "C", scale, BITMAP_CHANNEL);
		IScaledIndicatorModel indicatorModel = _chart.getModel()
				.getScaledIndicatorModel();
		_model = indicatorModel.getChannelModel(getLevel());
		_rtDataset = new DatasetDelegate(EMPTY_DATASET);
		_polylineDataset = new DatasetDelegate(EMPTY_DATASET);

		_chart.addDataset(_polylineDataset, new PolylinePainter());
		_chart.addDataset(_rtDataset, new PolylinePainter());
	}

	@Override
	public boolean isEnabled() {
		return getSettings().channelsEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		getSettings().channelsEnabled = enabled;
	}

	ARCSettings getSettings() {
		return _chart.getIndicatorLayer().getArcSettings();
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		final PivotLayer pivotLayer = getScale().getPivotLayer();
		if (pivotLayer != null && isEnabled() && !pivotLayer.isEnabled()) {
			pivotLayer.setVisible(visible);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractScaleLayer#setEmptyDatasets()
	 */
	@Override
	public void clearDatasets() {
		_polylineDataset.setBase(EMPTY_DATASET);
		_rtDataset.setBase(EMPTY_DATASET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractLayer#update()
	 */
	@Override
	public void updateDataset() {
		int dataLayer = _chart.getDataLayer();

		final PlotRange r = _chart.getXRange();
		IChannel2Collection data2 = _model.getChannels2(dataLayer,
				(long) r.lower, (long) r.upper);
		_polylineDataset.setBase(new PolylineDataset(data2));

		IScaledIndicatorModel indModel = _chart.getModel()
				.getScaledIndicatorModel();
		IRealTimeChannelModel rtModel = indModel
				.getRealTimeChannelModel(getLevel());
		IChannel2Collection rtData = rtModel.getChannel(dataLayer);
		PolylineDataset rtBase = new PolylineDataset(rtData);
		_rtDataset.setBase(rtBase);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractLayer#getAutorangeDataset()
	 */
	@Override
	public IDataset getAutorangeDataset() {
		return _polylineDataset;
	}

}
