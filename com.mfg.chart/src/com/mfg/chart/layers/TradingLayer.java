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

import static com.mfg.chart.ui.IChartUtils.EMPTY_DATASET;
import static javax.media.opengl.GL.GL_LINE_STRIP;

import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL2;

import org.eclipse.jface.preference.IPreferenceStore;
import org.mfg.opengl.BitmapData;
import org.mfg.opengl.IGLConstants;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.painters.AbstractBitmapMultiColorPainter;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IPositionCollection;
import com.mfg.chart.model.ITimePriceCollection;
import com.mfg.chart.model.ITradeCollection;
import com.mfg.chart.model.ITradingModel;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.settings.ProfiledObject;

public class TradingLayer extends FinalLayer {
	final DatasetDelegate tradeDataset;
	private final ITradingModel model;
	private int maxNumberToShow;
	private DatasetDelegate pendingOrdersDataset;
	private IChartModel chartModel;
	protected TradingSettings _s;
	private ProfiledObject _profiledObject;

	public static class TradingSettings {
		private static final String K_SHORT_PENDING_SHAPE_WIDTH2 = "short_Pending_shape_width";
		private static final String K_SHORT_PENDING_SHAPE2 = "short_Pending_shape";
		private static final String K_SHORT_PENDING_COLOR2 = "short_Pending_color";
		private static final String K_LONG_PENDING_SHAPE_WIDTH2 = "long_Pending_shape_width";
		private static final String K_LONG_PENDING_SHAPE2 = "long_Pending_shape";
		private static final String K_LONG_PENDING_COLOR2 = "long_Pending_color";
		private static final String K_SHORT_CLOSE_LOSE_SHAPE_WIDTH = "short_Close_Lose_shapeWidth";
		private static final String K_SHORT_CLOSE_LOSE_SHAPE2 = "short_Close_Lose_shape";
		private static final String K_SHORT_CLOSE_LOSE_COLOR2 = "short_Close_Lose_color";
		private static final String K_SHORT_CLOSE_GAIN_SHAPE_WIDTH = "short_Close_Gain_shapeWidth";
		private static final String K_SHORT_CLOSE_GAIN_SHAPE2 = "short_Close_Gain_shape";
		private static final String K_SHORT_CLOSE_GAIN_COLOR2 = "short_Close_Gain_color";
		private static final String K_SHORT_OPEN_LOSE_SHAPE_WIDTH = "short_Open_Lose_shapeWidth";
		private static final String K_SHORT_OPEN_LOSE_SHAPE2 = "short_Open_Lose_shape";
		private static final String K_SHORT_OPEN_LOSE_COLOR2 = "short_Open_Lose_color";
		private static final String K_SHORT_OPEN_GAIN_SHAPE_WIDTH = "short_Open_Gain_shapeWidth";
		private static final String K_SHORT_OPEN_GAIN_SHAPE2 = "short_Open_Gain_shape";
		private static final String K_SHORT_OPEN_GAIN_COLOR2 = "short_Open_Gain_color";
		private static final String K_LONG_CLOSE_LOSE_SHAPE_WIDTH = "long_Close_Lose_shapeWidth";
		private static final String K_LONG_CLOSE_LOSE_SHAPE2 = "long_Close_Lose_shape";
		private static final String K_LONG_CLOSE_LOSE_COLOR2 = "long_Close_Lose_color";
		private static final String K_LONG_CLOSE_GAIN_SHAPE_WIDTH = "long_Close_Gain_shapeWidth";
		private static final String K_LONG_CLOSE_GAIN_SHAPE2 = "long_Close_Gain_shape";
		private static final String K_LONG_CLOSE_GAIN_COLOR2 = "long_Close_Gain_color";
		private static final String K_LONG_OPEN_LOSE_SHAPE_WIDTH = "long_Open_Lose_shapeWidth";
		private static final String K_LONG_OPEN_LOSE_SHAPE2 = "long_Open_Lose_shape";
		private static final String K_LONG_OPEN_LOSE_COLOR2 = "long_Open_Lose_color";
		private static final String K_LONG_OPEN_GAIN_SHAPE_WIDTH = "long_Open_Gain_shapeWidth";
		private static final String K_LONG_OPEN_GAIN_SHAPE2 = "long_Open_Gain_shape";
		private static final String K_LONG_OPEN_GAIN_COLOR2 = "long_Open_Gain_color";
		private static final String K_SHOW_CLOSED_POSITIONS = "showClosedPositions";
		public boolean showClosedPosition = false;

		public int long_Pending_shape = 0;
		public int long_Pending_shape_width = 0;
		public float[] long_Pending_color = COLOR_CYAN;

		public int short_Pending_shape = 0;
		public int short_Pending_shape_width = 0;
		public float[] short_Pending_color = COLOR_PINK;

		// open
		public int long_Open_Gain_shape = 0;
		public int long_Open_Gain_shapeWidth = 0;
		public float[] long_Open_Gain_color = COLOR_BLUE;

		public int short_Open_Gain_shape = 0;
		public int short_Open_Gain_shapeWidth = 0;
		public float[] short_Open_Gain_color = COLOR_RED;

		public int long_Open_Lose_shape = 0;
		public int long_Open_Lose_shapeWidth = 0;
		public float[] long_Open_Lose_color = COLOR_BLUE;

		public int short_Open_Lose_shape = 0;
		public int short_Open_Lose_shapeWidth = 0;
		public float[] short_Open_Lose_color = COLOR_RED;

		// close

		public int long_Close_Gain_shape = 3;
		public int long_Close_Gain_shapeWidth = 0;
		public float[] long_Close_Gain_color = COLOR_GREEN;

		public int short_Close_Gain_shape = 3;
		public int short_Close_Gain_shapeWidth = 0;
		public float[] short_Close_Gain_color = COLOR_YELLOW;

		public int long_Close_Lose_shape = 1;
		public int long_Close_Lose_shapeWidth = 0;
		public float[] long_Close_Lose_color = COLOR_GREEN;

		public int short_Close_Lose_shape = 1;
		public int short_Close_Lose_shapeWidth = 0;
		public float[] short_Close_Lose_color = COLOR_YELLOW;

		public TradingSettings() {
		}

		public void fillProfile(Profile p) {
			p.putBoolean(K_SHOW_CLOSED_POSITIONS, showClosedPosition);

			p.putFloatArray(K_LONG_PENDING_COLOR2, long_Pending_color);
			p.putInt(K_LONG_PENDING_SHAPE2, long_Pending_shape);
			p.putInt(K_LONG_PENDING_SHAPE_WIDTH2, long_Pending_shape_width);

			p.putFloatArray(K_SHORT_PENDING_COLOR2, short_Pending_color);
			p.putInt(K_SHORT_PENDING_SHAPE2, short_Pending_shape);
			p.putInt(K_SHORT_PENDING_SHAPE_WIDTH2, short_Pending_shape_width);

			p.putFloatArray(K_LONG_OPEN_GAIN_COLOR2, long_Open_Gain_color);
			p.putInt(K_LONG_OPEN_GAIN_SHAPE2, long_Open_Gain_shape);
			p.putInt(K_LONG_OPEN_GAIN_SHAPE_WIDTH, long_Open_Gain_shapeWidth);

			p.putFloatArray(K_LONG_OPEN_LOSE_COLOR2, long_Open_Lose_color);
			p.putInt(K_LONG_OPEN_LOSE_SHAPE2, long_Open_Lose_shape);
			p.putInt(K_LONG_OPEN_LOSE_SHAPE_WIDTH, long_Open_Lose_shapeWidth);

			p.putFloatArray(K_LONG_CLOSE_GAIN_COLOR2, long_Close_Gain_color);
			p.putInt(K_LONG_CLOSE_GAIN_SHAPE2, long_Close_Gain_shape);
			p.putInt(K_LONG_CLOSE_GAIN_SHAPE_WIDTH, long_Close_Gain_shapeWidth);

			p.putFloatArray(K_LONG_CLOSE_LOSE_COLOR2, long_Close_Lose_color);
			p.putInt(K_LONG_CLOSE_LOSE_SHAPE2, long_Close_Lose_shape);
			p.putInt(K_LONG_CLOSE_LOSE_SHAPE_WIDTH, long_Close_Lose_shapeWidth);

			p.putFloatArray(K_SHORT_OPEN_GAIN_COLOR2, short_Open_Gain_color);
			p.putInt(K_SHORT_OPEN_GAIN_SHAPE2, short_Open_Gain_shape);
			p.putInt(K_SHORT_OPEN_GAIN_SHAPE_WIDTH, short_Open_Gain_shapeWidth);

			p.putFloatArray(K_SHORT_OPEN_LOSE_COLOR2, short_Open_Lose_color);
			p.putInt(K_SHORT_OPEN_LOSE_SHAPE2, short_Open_Lose_shape);
			p.putInt(K_SHORT_OPEN_LOSE_SHAPE_WIDTH, short_Open_Lose_shapeWidth);

			p.putFloatArray(K_SHORT_CLOSE_GAIN_COLOR2, short_Close_Gain_color);
			p.putInt(K_SHORT_CLOSE_GAIN_SHAPE2, short_Close_Gain_shape);
			p.putInt(K_SHORT_CLOSE_GAIN_SHAPE_WIDTH,
					short_Close_Gain_shapeWidth);

			p.putFloatArray(K_SHORT_CLOSE_LOSE_COLOR2, short_Close_Lose_color);
			p.putInt(K_SHORT_CLOSE_LOSE_SHAPE2, short_Close_Lose_shape);
			p.putInt(K_SHORT_CLOSE_LOSE_SHAPE_WIDTH,
					short_Close_Lose_shapeWidth);

		}

		public void updateFromProfile(Profile p) {
			TradingSettings s = new TradingSettings();

			showClosedPosition = p.getBoolean(K_SHOW_CLOSED_POSITIONS,
					s.showClosedPosition);

			long_Pending_color = p.getFloatArray(K_LONG_PENDING_COLOR2,
					s.long_Pending_color);
			long_Pending_shape = p.getInt(K_LONG_PENDING_SHAPE2,
					s.long_Pending_shape);
			long_Pending_shape_width = p.getInt(K_LONG_CLOSE_GAIN_SHAPE_WIDTH,
					s.long_Pending_shape_width);

			short_Pending_color = p.getFloatArray(K_SHORT_PENDING_COLOR2,
					s.short_Pending_color);
			short_Pending_shape = p.getInt(K_SHORT_PENDING_SHAPE2,
					s.short_Pending_shape);
			short_Pending_shape_width = p
					.getInt(K_SHORT_CLOSE_GAIN_SHAPE_WIDTH,
							s.short_Pending_shape_width);

			long_Open_Gain_color = p.getFloatArray(K_LONG_OPEN_GAIN_COLOR2,
					s.long_Open_Gain_color);
			long_Open_Gain_shape = p.getInt(K_LONG_OPEN_GAIN_SHAPE2,
					s.long_Open_Gain_shape);
			long_Open_Gain_shapeWidth = p.getInt(K_LONG_OPEN_GAIN_SHAPE_WIDTH,
					s.long_Open_Gain_shapeWidth);

			long_Open_Lose_color = p.getFloatArray(K_LONG_OPEN_LOSE_COLOR2,
					s.long_Open_Lose_color);
			long_Open_Lose_shape = p.getInt(K_LONG_OPEN_LOSE_SHAPE2,
					s.long_Open_Lose_shape);
			long_Open_Lose_shapeWidth = p.getInt(K_LONG_OPEN_LOSE_SHAPE_WIDTH,
					s.long_Open_Lose_shapeWidth);

			long_Close_Gain_color = p.getFloatArray(K_LONG_CLOSE_GAIN_COLOR2,
					s.long_Close_Gain_color);
			long_Close_Gain_shape = p.getInt(K_LONG_CLOSE_GAIN_SHAPE2,
					s.long_Close_Gain_shape);
			long_Close_Gain_shapeWidth = p
					.getInt(K_LONG_CLOSE_GAIN_SHAPE_WIDTH,
							s.long_Close_Gain_shapeWidth);

			long_Close_Lose_color = p.getFloatArray(K_LONG_CLOSE_LOSE_COLOR2,
					s.long_Close_Lose_color);
			long_Close_Lose_shape = p.getInt(K_LONG_CLOSE_LOSE_SHAPE2,
					s.long_Close_Lose_shape);
			long_Close_Lose_shapeWidth = p
					.getInt(K_LONG_CLOSE_LOSE_SHAPE_WIDTH,
							s.long_Close_Lose_shapeWidth);

			short_Open_Gain_color = p.getFloatArray(K_SHORT_OPEN_GAIN_COLOR2,
					s.short_Open_Gain_color);
			short_Open_Gain_shape = p.getInt(K_SHORT_OPEN_GAIN_SHAPE2,
					s.short_Open_Gain_shape);
			short_Open_Gain_shapeWidth = p
					.getInt(K_SHORT_OPEN_GAIN_SHAPE_WIDTH,
							s.short_Open_Gain_shapeWidth);

			short_Open_Lose_color = p.getFloatArray(K_SHORT_OPEN_LOSE_COLOR2,
					s.short_Open_Lose_color);
			short_Open_Lose_shape = p.getInt(K_SHORT_OPEN_LOSE_SHAPE2,
					s.short_Open_Lose_shape);
			short_Open_Lose_shapeWidth = p
					.getInt(K_SHORT_OPEN_LOSE_SHAPE_WIDTH,
							s.short_Open_Lose_shapeWidth);

			short_Close_Gain_color = p.getFloatArray(K_SHORT_CLOSE_GAIN_COLOR2,
					s.short_Close_Gain_color);
			short_Close_Gain_shape = p.getInt(K_SHORT_CLOSE_GAIN_SHAPE2,
					s.short_Close_Gain_shape);
			short_Close_Gain_shapeWidth = p.getInt(
					K_SHORT_CLOSE_GAIN_SHAPE_WIDTH,
					s.short_Close_Gain_shapeWidth);

			short_Close_Lose_color = p.getFloatArray(K_SHORT_CLOSE_LOSE_COLOR2,
					s.short_Close_Lose_color);
			short_Close_Lose_shape = p.getInt(K_SHORT_CLOSE_LOSE_SHAPE2,
					s.short_Close_Lose_shape);
			short_Close_Lose_shapeWidth = p.getInt(
					K_SHORT_CLOSE_LOSE_SHAPE_WIDTH,
					s.short_Close_Lose_shapeWidth);
		}
	}

	class TradeDataset implements IDataset {
		final ITradeCollection col;

		public TradeDataset(ITradeCollection aCol) {
			this.col = aCol;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.mfg.opengl.chart.IDataset#getSeriesCount()
		 */
		@Override
		public int getSeriesCount() {
			return 1;
		}

		@Override
		public int getItemCount(int series) {
			return col.getSize();
		}

		@Override
		public double getX(int series, int item) {
			return 0;
		}

		@Override
		public double getY(int series, int item) {
			return 0;
		}
	}

	/**
	 * @param name
	 * @param chart
	 */
	public TradingLayer(final Chart chart) {
		super("Execution", chart);
		_s = new TradingSettings();
		_profiledObject = new ProfiledObject() {
			@Override
			protected List<Profile> createProfilePresets() {
				Profile p = new Profile("Profile 1");
				new TradingSettings().fillProfile(p);
				return Arrays.asList(p);
			}
			@Override
			public void setProfile(Profile profile) {
				super.setProfile(profile);
			}

			@Override
			public String getProfileKeySet() {
				return "tradingLayer";
			}
		};
		_s.updateFromProfile(_profiledObject.getDefault());

		model = chart.getModel().getTradingModel();
		chartModel = chart.getModel();

		tradeDataset = new DatasetDelegate(EMPTY_DATASET);
		pendingOrdersDataset = new DatasetDelegate(EMPTY_DATASET);

		// final BitmapData openBmp = new BitmapData(BITMAP_OPEN_POSITION,
		// BITMAP_OPEN_POSITION_WIDTH, BITMAP_OPEN_POSITION_HEIGHT);

		chart.addDataset(pendingOrdersDataset,
				new AbstractBitmapMultiColorPainter() {

					@Override
					public float[] getColor(IDataset ds, int series, int item) {
						return isLongPosition(ds, item) ? _s.long_Pending_color
								: _s.short_Pending_color;
					}

					@Override
					public BitmapData getBitmap(IDataset ds, int series,
							int item) {
						BitmapData long_ = SHAPES[_s.long_Pending_shape_width][_s.long_Pending_shape];
						BitmapData short_ = SHAPES[_s.short_Pending_shape_width][_s.short_Pending_shape];

						return isLongPosition(ds, item) ? long_ : short_;
					}
				});

		chart.addDataset(tradeDataset, new ISeriesPainter() {

			@Override
			public void paint(GL2 gl, IDataset ds, PlotRange xrange,
					PlotRange yrange) {
				if (ds.getItemCount(0) > 0) {
					ITradeCollection col = ((TradeDataset) tradeDataset
							.getBase()).col;
					for (int item = 0; item < col.getSize(); item++) {
						paintOpenMarker(gl, col, item);
						if (col.isClosed(item)) {
							paintCloseMarkers(gl, col, item);
						}
					}
				}
			}

			private void paintCloseMarkers(GL2 gl, ITradeCollection col,
					int item) {
				// draw close marker
				boolean isLong = col.isLong(item);
				boolean isGain = col.isGain(item);

				float[] long_color = isGain ? _s.long_Close_Gain_color
						: _s.long_Close_Lose_color;
				float[] short_color = isGain ? _s.short_Close_Gain_color
						: _s.short_Close_Lose_color;
				float[] color = isLong ? long_color : short_color;

				gl.glColor4fv(color, 0);

				double openX = col.getOpenTime(item);
				double openY = col.getOpenPrice(item);
				long closeX = col.getCloseTime(item);
				double closeY = col.getClosePrice(item);

				BitmapData long_bmp = isGain ? SHAPES[_s.long_Close_Gain_shapeWidth][_s.long_Close_Gain_shape]
						: SHAPES[_s.long_Close_Lose_shapeWidth][_s.long_Close_Lose_shape];
				BitmapData short_bmp = isGain ? SHAPES[_s.short_Close_Gain_shapeWidth][_s.short_Close_Gain_shape]
						: SHAPES[_s.short_Close_Lose_shapeWidth][_s.short_Close_Lose_shape];

				BitmapData bmp = isLong ? long_bmp : short_bmp;
				gl.glRasterPos2d(closeX, closeY);
				gl.glBitmap(bmp.width, bmp.height, bmp.x, bmp.y, 0, 0,
						bmp.bitmap, 0);

				// draw open-close line
				gl.glColor4fv(COLOR_WHITE, 0);
				gl.glPushAttrib(GL2.GL_LINE_BIT);
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(getLayerStippleFactor(), STIPPLE_PATTERN);
				gl.glColor4fv(COLOR_WHITE, 0);
				gl.glBegin(GL_LINE_STRIP);
				gl.glVertex2d(openX, openY);
				gl.glVertex2d(closeX, closeY);
				gl.glEnd();
				gl.glPopAttrib();
				gl.glDisable(GL2.GL_LINE_STIPPLE);
			}

			private void paintOpenMarker(GL2 gl, ITradeCollection col, int item) {
				boolean isLong = col.isLong(item);
				boolean isGain = col.isGain(item);

				float[] long_color = isGain ? _s.long_Open_Gain_color
						: _s.long_Open_Lose_color;
				float[] short_color = isGain ? _s.short_Open_Gain_color
						: _s.short_Open_Lose_color;
				float[] color = isLong ? long_color : short_color;

				gl.glColor4fv(color, 0);
				double openX = col.getOpenTime(item);
				double openY = col.getOpenPrice(item);
				gl.glRasterPos2d(openX, openY);

				BitmapData long_bmp = isGain ? SHAPES[_s.long_Open_Gain_shapeWidth][_s.long_Open_Gain_shape]
						: SHAPES[_s.long_Open_Lose_shapeWidth][_s.long_Open_Lose_shape];
				BitmapData short_bmp = isGain ? SHAPES[_s.short_Open_Gain_shapeWidth][_s.short_Open_Gain_shape]
						: SHAPES[_s.short_Open_Lose_shapeWidth][_s.short_Open_Lose_shape];

				BitmapData bmp = isLong ? long_bmp : short_bmp;

				gl.glBitmap(bmp.width, bmp.height, bmp.x, bmp.y, 0, 0,
						bmp.bitmap, 0);

				gl.glColor4fv(COLOR_WHITE, 0);
				for (long opening : col.getOpenings(item)) {
					gl.glRasterPos2d(openX, opening);
					gl.glBitmap(bmp.width, bmp.height, bmp.x, bmp.y, 0, 0,
							bmp.bitmap, 0);
				}
			}
		});
	}

	public ProfiledObject getProfiledObject() {
		return _profiledObject;
	}

	public TradingSettings getSettings() {
		return _s;
	}

	public void setSettings(TradingSettings setings) {
		_s = setings;
	}

	@Override
	public int getDefaultLayerStippleFactor() {
		return STIPPLE_FACTOR_3;
	}

	static boolean isLongPosition(final IDataset ds, final int item) {
		final TimePriceDataset timeDs = (TimePriceDataset) ((DatasetDelegate) ds)
				.getBase();
		final IPositionCollection col = (IPositionCollection) timeDs
				.getCollection();
		return col.isLongPosition(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IColoredLayer#getLayerColor()
	 */
	@Override
	public float[] getLayerColor() {
		return IGLConstants.COLOR_BLUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#getDefaultLayerColor()
	 */
	@Override
	public float[] getDefaultLayerColor() {
		return IGLConstants.COLOR_BLUE;
	}

	@Override
	public void updateDataset() {
		if (isEnabled()) {
			final PlotRange range = _chart.getXRange();

			int count = model.getOpenPositionCount((long) range.lower,
					(long) range.upper);

			if (count < maxNumberToShow) {
				ITradeCollection tradeList = model.getTrade((long) range.lower,
						(long) range.upper, _s.showClosedPosition);
				tradeDataset.setBase(new TradeDataset(tradeList));

				ITimePriceCollection pendingOrdersList = chartModel
						.getPendingOrdersModel();
				pendingOrdersDataset.setBase(new TimePriceDataset(
						pendingOrdersList));
			} else {
				setVisible(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#getAutorangeDataset()
	 */
	@Override
	public IDataset getAutorangeDataset() {
		return tradeDataset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#clearDatasets()
	 */
	@Override
	public void clearDatasets() {
		tradeDataset.setBase(EMPTY_DATASET);
		pendingOrdersDataset.setBase(EMPTY_DATASET);
	}

	@Override
	public void updatePreferences(IPreferenceStore store) {
		maxNumberToShow = store
				.getInt(ChartPlugin.PREFERENCES_MAX_NUMBER_OF_POSITIONS_TO_SHOW);
	}
}
