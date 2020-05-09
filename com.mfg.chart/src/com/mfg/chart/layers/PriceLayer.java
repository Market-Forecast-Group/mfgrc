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

import static com.mfg.chart.ui.IChartUtils.EMPTY_DATASET;
import static javax.media.opengl.GL.GL_LINE_STRIP;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL2;

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;
import org.mfg.opengl.chart.SimplePainter;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.Chart.TimeOfTheDayLabelMode;
import com.mfg.chart.backend.opengl.Chart.TimeOfTheDaySettings;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IDataLayerModel;
import com.mfg.chart.model.IPivotCollection;
import com.mfg.chart.model.IPivotModel;
import com.mfg.chart.model.IPriceCollection;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.model.IRealTimeZZModel;
import com.mfg.chart.model.IScaledIndicatorModel;
import com.mfg.chart.model.ITemporalPricesModel;
import com.mfg.chart.model.ITimePriceCollection;
import com.mfg.chart.model.ITimesOfTheDayCollection;
import com.mfg.chart.model.Model_MDB;
import com.mfg.chart.model.PriceModel_MDB.VolumeCollection;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.IChartUtils;
import com.mfg.chart.ui.TimeOfTheDay;
import com.mfg.chart.ui.settings.ProfiledObject;

public class PriceLayer extends FinalLayer {
	public static final int MAX_ITEM_COUNT = 300;
	final DatasetDelegate _pricesDataset;
	private final IPriceModel _priceModel;
	private final IDataset _autoRangeDataset;
	private final IScaledIndicatorModel _indicatorModel;
	IPivotModel _compressionModel;
	IRealTimeZZModel _compressionRTModel;
	private final int _firstLevel;
	private final ITemporalPricesModel _tempPriceModel;
	private final DatasetDelegate _tempPricesDataset;
	private int _maxNumberOfPricesToShowAsZZ1;
	float[] _tempPricesColor;
	private DatasetDelegate _timeOfDayDataset;
	private DatasetDelegate _startRTLinesDataset;
	private DatasetDelegate _edgesDataset;

	static class LayerEdgesLineDataset implements IDataset {
		private final IChartModel _model;
		private final int _dataLayer;

		public LayerEdgesLineDataset(int dataLayer, IChartModel model) {
			super();
			this._model = model;
			_dataLayer = dataLayer;
		}

		@Override
		public int getSeriesCount() {
			return 1;
		}

		@Override
		public int getItemCount(int series) {
			IDataLayerModel dataLayerModel = _model.getDataLayerModel();
			if (dataLayerModel == null) {
				return 0;
			}
			return _dataLayer;
		}

		@Override
		public double getX(int series, int item) {
			IPriceModel priceModel = _model.getPriceModel();
			int layer = item;
			if (priceModel.getDataLayerPricesCount(layer) == 0) {
				return -1;
			}
			long lowerDisplayTime = priceModel
					.getDataLayerLowerDisplayTime(layer);
			long date = priceModel.getPhysicalTime_from_DisplayTime(layer,
					lowerDisplayTime);
			long x = priceModel.getDisplayTime_from_PhysicalTime(_dataLayer,
					date);
			return x;
		}

		@Override
		public double getY(int series, int item) {
			return 0;
		}

	}

	class LayerEdgesLinePainter implements ISeriesPainter {
		private final GLUT glut = new GLUT();

		@SuppressWarnings("static-access")
		// Used on inner classes
		@Override
		public void paint(GL2 gl, IDataset ds, PlotRange xrange,
				PlotRange yrange) {
			if (ds.getSeriesCount() > 0) {
				int screenHeight = _chart.getPlotScreenHeight();

				for (int item = 0; item < ds.getItemCount(0); item++) {
					double x = ds.getX(0, item);
					if (x >= 0) {
						gl.glPushAttrib(GL2.GL_LINE_BIT);
						gl.glEnable(GL2.GL_LINE_STIPPLE);
						gl.glLineStipple(STIPPLE_FACTOR_1, STIPPLE_PATTERN);

						gl.glColor3fv(COLOR_RED, 0);

						gl.glBegin(GL2.GL_LINES);

						gl.glVertex2d(x, yrange.lower);
						gl.glVertex2d(x, yrange.upper);
						gl.glEnd();
						gl.glPopAttrib();

						String label = "Layer " + (item + 1);

						double y = yrange.upper
								- yrange.plotWidth(36, screenHeight)
								- yrange.plotWidth(20, screenHeight) * item;

						gl.glRasterPos2d(x, y);
						glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "  "
								+ label);
						gl.glFlush();
					}
				}
			}
		}

	}

	class StartRealtimeLinesDataset implements IDataset {
		private final Long _time;

		public StartRealtimeLinesDataset(Long time) {
			this._time = time;
		}

		@Override
		public int getSeriesCount() {
			return _time == null ? 0 : 1;
		}

		@Override
		public int getItemCount(int series) {
			return 1;
		}

		@Override
		public double getX(int series, int item) {
			return _time.doubleValue();
		}

		@Override
		public double getY(int series, int item) {
			return 0;
		}

	}

	class StartRealtimeLinesPainter implements ISeriesPainter {
		private final GLUT glut = new GLUT();

		@SuppressWarnings("static-access")
		// Used on inner classes
		@Override
		public void paint(GL2 gl, IDataset ds, PlotRange xrange,
				PlotRange yrange) {
			int screenHeight = _chart.getPlotScreenHeight();
			if (ds.getSeriesCount() > 0) {
				double x = ds.getX(0, 0);
				if (x >= 0) {
					gl.glPushAttrib(GL2.GL_LINE_BIT);
					gl.glEnable(GL2.GL_LINE_STIPPLE);
					gl.glLineStipple(STIPPLE_FACTOR_1, STIPPLE_PATTERN);

					gl.glColor3fv(COLOR_BLUE, 0);

					gl.glBegin(GL2.GL_LINES);

					gl.glVertex2d(x, yrange.lower);
					gl.glVertex2d(x, yrange.upper);
					gl.glEnd();
					gl.glPopAttrib();

					String label = "Start RT " + (_chart.getDataLayer() + 1);

					double y = yrange.upper
							- yrange.plotWidth(36, screenHeight)
							- yrange.plotWidth(20, screenHeight);

					gl.glRasterPos2d(x, y);
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "  "
							+ label);
					gl.glFlush();
				}
			}
		}

	}

	class TimeOfDayDataset implements IDataset {

		private final List<ITimesOfTheDayCollection> list;
		private final List<float[]> colors;

		public TimeOfDayDataset() {
			super();
			list = new ArrayList<>();
			colors = new ArrayList<>();
		}

		@Override
		public int getSeriesCount() {
			return list.size();
		}

		@Override
		public int getItemCount(int series) {
			return list.get(series).getSize();
		}

		@Override
		public double getX(int series, int item) {
			return list.get(series).getTime(item);
		}

		@Override
		public double getY(int series, int item) {
			return 0;
		}

		public String getLabel(int series, int item) {
			return list.get(series).getLabel(item);
		}

		public float[] getColor(int series) {
			return colors.get(series);
		}

		public void add(ITimesOfTheDayCollection col, float[] color) {
			list.add(col);
			colors.add(color);
		}
	}

	class TimeOfDayPainter implements ISeriesPainter {

		private static final int TIME_OF_THE_DAY_AND_CROSSHAIR_AREA = 200;

		@SuppressWarnings("static-access")
		// Used on inner classes
		@Override
		public void paint(GL2 gl, IDataset ds, PlotRange xrange,
				PlotRange yrange) {
			TimeOfTheDaySettings settings = _chart.getTimeOfTheDaySettings();
			if (ds.getSeriesCount() > 0 && settings.isVisible()) {
				for (int series = 0; series < ds.getSeriesCount(); series++) {
					TimeOfDayDataset ds2 = (TimeOfDayDataset) ((DatasetDelegate) ds)
							.getBase();

					gl.glPushAttrib(GL2.GL_LINE_BIT);
					gl.glEnable(GL2.GL_LINE_STIPPLE);
					gl.glLineStipple(STIPPLE_FACTOR_1, STIPPLE_PATTERN);

					gl.glBegin(GL2.GL_LINES);
					gl.glColor4fv(ds2.getColor(series), 0);
					for (int i = 0; i < ds2.getItemCount(series); i++) {
						double x = ds2.getX(series, i);
						gl.glVertex2d(x, yrange.lower);
						gl.glVertex2d(x, yrange.upper);
					}
					gl.glEnd();

					TimeOfTheDayLabelMode labelMode = settings
							.getTimeOfTheDayLabelMode();
					int crossX_screen = _chart.getGLChart().crossScreenX;

					GLUT glut = new GLUT();
					for (int i = 0; i < ds2.getItemCount(series); i++) {
						double x = ds2.getX(series, i);

						boolean showLabel = true;

						switch (labelMode) {
						case ALWAYS_SHOW_LABELS:
							break;
						case NEVER_SHOW_LABELS:
							showLabel = false;
							break;
						case ONLY_SHOW_LABELS_CLOSE_TO_THE_CROSSHAIR:
							int x_screen = _chart.getPlotLeftMargin()
									+ xrange.screenValue(x,
											_chart.getPlotScreenWidth());
							showLabel = Math.abs(x_screen - crossX_screen) < TIME_OF_THE_DAY_AND_CROSSHAIR_AREA;
							break;
						}
						if (showLabel) {
							double y = yrange.upper
									- yrange.plotWidth(36,
											_chart.getPlotScreenHeight());
							String label = ds2.getLabel(series, i);

							gl.glRasterPos2d(x, y);
							glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10,
									"  " + label);
							gl.glFlush();
						}
					}

					gl.glPopAttrib();
				}
			}
		}
	}

	class PricePainter implements ISeriesPainter {
		private final BitmapData shape = BITMAP_DOT;

		/**
		 * 
		 * @param series
		 * @param item
		 * @return
		 */
		public float[] getColor(final int series, final int item) {
			final TimePriceDataset ds = (TimePriceDataset) _pricesDataset
					.getBase();
			final ITimePriceCollection col = ds.getCollection();
			final boolean red = col instanceof IPriceCollection
					&& !((IPriceCollection) col).isReal(item);
			return red ? COLOR_RED : _settings.color;
		}

		@Override
		public void paint(final GL2 gl, final IDataset ds,
				final PlotRange xrange, final PlotRange yrange) {

			gl.glPushAttrib(GL2.GL_LINE_BIT);
			gl.glLineWidth(_settings.lineWidth);

			int stippleFactor = _settings.lineType;
			if (stippleFactor != STIPPLE_FACTOR_NULL) {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(stippleFactor, STIPPLE_PATTERN);
			}

			gl.glColor4fv(_settings.color, 0);

			for (int series = 0; series < ds.getSeriesCount(); series++) {

				gl.glBegin(GL_LINE_STRIP);

				for (int item = 0; item < ds.getItemCount(series); item++) {

					gl.glColor4fv(getColor(series, item), 0);

					final double x = ds.getX(series, item);
					final double y = ds.getY(series, item);

					gl.glVertex2d(x, y);
				}

				gl.glEnd();

				if (_compressionModel == null) {
					final double shapeW = xrange.plotWidth(shape.width,
							getChart().getPlotScreenWidth()
									- getChart().getPlotLeftMargin()) / 2;
					final double shapeH = yrange.plotWidth(shape.height,
							getChart().getPlotScreenHeight()
									- getChart().getPlotBottomMargin()) / 2;

					for (int item = 0; item < ds.getItemCount(series); item++) {
						final double x = ds.getX(series, item);
						final double y = ds.getY(series, item);

						gl.glColor4fv(getColor(series, item), 0);

						gl.glRasterPos2d(x - shapeW, y - shapeH);
						gl.glBitmap(shape.width, shape.height, 0, 0, 0, 0,
								shape.bitmap, 0);
					}
				}
			}

			gl.glPopAttrib();
			if (stippleFactor != STIPPLE_FACTOR_NULL) {
				gl.glDisable(GL2.GL_LINE_STIPPLE);
			}
		}
	}

	public static class PriceSettings {
		private static final String K_VOLUME_TYPE = "volumeType";
		private static final String K_VOLUME_WIDTH = "volumeWidth";
		private static final String K_VOLUME_COLOR = "volumeColor";
		private static final String K_SHOW_VOLUME = "showVolume";
		private static final String K_ENABLED = "enabled";
		private static final String K_ZZ_COMPRESSION = "zzCompression";
		private static final String K_LINE_TYPE = "lineType";
		private static final String K_LINE_WIDTH = "lineWidth";
		private static final String K_COLOR = "color";
		public float[] color;
		public int lineWidth;
		public int lineType;
		public boolean zzCompression;
		public boolean enabled;
		public boolean showVolume;
		public float[] volumeColor;
		public int volumeWidth;
		public int volumeType;

		public PriceSettings() {
			color = COLOR_WHITE;
			lineWidth = 1;
			lineType = 0;
			zzCompression = true;
			enabled = true;
			showVolume = false;
			volumeColor = COLOR_BLUE;
			volumeWidth = 1;
			volumeType = 0;
		}

		public void fillProfile(Profile p) {
			p.putFloatArray(K_COLOR, color);
			p.putInt(K_LINE_WIDTH, lineWidth);
			p.putInt(K_LINE_TYPE, lineType);
			p.putBoolean(K_ZZ_COMPRESSION, zzCompression);
			p.putBoolean(K_ENABLED, enabled);
			p.putBoolean(K_SHOW_VOLUME, showVolume);
			p.putFloatArray(K_VOLUME_COLOR, volumeColor);
			p.putInt(K_VOLUME_WIDTH, volumeWidth);
			p.putInt(K_VOLUME_TYPE, volumeType);
		}

		public void updateFromProfile(Profile p) {
			color = p.getFloatArray(K_COLOR, COLOR_WHITE);
			lineWidth = p.getInt(K_LINE_WIDTH, 1);
			lineType = p.getInt(K_LINE_TYPE, 0);
			zzCompression = p.getBoolean(K_ZZ_COMPRESSION, true);
			enabled = p.getBoolean(K_ENABLED, true);
			showVolume = p.getBoolean(K_SHOW_VOLUME, false);
			volumeColor = p.getFloatArray(K_VOLUME_COLOR, COLOR_BLUE);
			volumeWidth = p.getInt(K_VOLUME_WIDTH, 1);
			volumeType = p.getInt(K_VOLUME_TYPE, 0);
		}
	}

	protected PriceSettings _settings;
	private ProfiledObject _profiledObject;
	private DatasetDelegate _volumeDataset;
	private DatasetDelegate _rtDataset;

	public PriceLayer(final Chart chart) {
		super("Price", chart, BITMAP_PRICES.bitmap);

		setLayerColor(getDefaultLayerColor());
		_maxNumberOfPricesToShowAsZZ1 = 300;
		_tempPricesColor = COLOR_CYAN;

		_priceModel = chart.getModel().getPriceModel();
		_tempPriceModel = chart.getModel().getTemporalPricesModel();
		_indicatorModel = chart.getModel().getScaledIndicatorModel();
		_firstLevel = _indicatorModel.getFirstScale();

		_pricesDataset = new DatasetDelegate(EMPTY_DATASET);
		_rtDataset = new DatasetDelegate(EMPTY_DATASET);
		_tempPricesDataset = new DatasetDelegate(EMPTY_DATASET);
		_timeOfDayDataset = new DatasetDelegate(EMPTY_DATASET);

		_startRTLinesDataset = new DatasetDelegate(EMPTY_DATASET);

		_autoRangeDataset = _pricesDataset;

		_edgesDataset = new DatasetDelegate(EMPTY_DATASET);

		_volumeDataset = new DatasetDelegate(EMPTY_DATASET);

		chart.addDataset(_pricesDataset, new PricePainter());
		chart.addDataset(_rtDataset, new PricePainter());
		chart.addDataset(_volumeDataset, new SimplePainter(COLOR_BLUE) {
			@Override
			public float[] getColor(int series) {
				return _settings.volumeColor;
			}

			@Override
			public int getStippleFactor() {
				return _settings.volumeType;
			}

			@Override
			public float getLineWidth() {
				return _settings.volumeWidth;
			}

		});
		chart.addDataset(_tempPricesDataset,
				new SimplePainter(_tempPricesColor));
		chart.addDataset(_timeOfDayDataset, new TimeOfDayPainter());
		chart.addDataset(_edgesDataset, new LayerEdgesLinePainter());
		chart.addDataset(_startRTLinesDataset, new StartRealtimeLinesPainter());

		_settings = new PriceSettings();
		_profiledObject = new ProfiledObject() {
			@Override
			protected List<Profile> createProfilePresets() {
				PriceSettings s = new PriceSettings();
				Profile p = new Profile("Profile 1");
				s.fillProfile(p);
				return Arrays.asList(p);
			}

			@Override
			public String getProfileKeySet() {
				return "priceSettings";
			}
		};
		_settings.updateFromProfile(_profiledObject.getProfile());
	}

	@Override
	public boolean isEnabled() {
		return _settings.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		_settings.enabled = enabled;
	}

	public ProfiledObject getProfiledObject() {
		return _profiledObject;
	}

	public PriceSettings getSettings() {
		return _settings;
	}

	public void setSettings(PriceSettings settings) {
		_settings = settings;
	}

	/**
	 * @return the tempPricesColor
	 */
	public float[] getTempPricesColor() {
		return _tempPricesColor;
	}

	/**
	 * @param tempPricesColor
	 *            the tempPricesColor to set
	 */
	public void setTempPricesColor(float[] tempPricesColor) {
		this._tempPricesColor = tempPricesColor;
	}

	@Override
	public float[] getDefaultLayerColor() {
		return COLOR_WHITE;
	}

	@Override
	public int getDefaultLayerStippleFactor() {
		return STIPPLE_FACTOR_NULL;
	}

	@Override
	public void updateDataset() {
		if (isEnabled()) {
			final PlotRange xrange = _chart.getXRange();

			final long lower = (long) xrange.lower;
			final long upper = (long) xrange.upper;

			_compressionModel = null;
			_compressionRTModel = null;

			int dataLayer = _chart.getDataLayer();
			if (_settings.zzCompression && _chart.getType().hasChannels()) {
				long count = _priceModel.getPricesDistance(dataLayer, lower,
						upper);
				// out.println("count " + count);
				if (count > getMaxNumberOfPricesToShowAsZZ1()) {
					// out.println("look for scale");
					// try to find a ZZ good for prices
					int minDiff = Integer.MAX_VALUE;

					final int scaleCount = _indicatorModel.getScaleCount();

					int firstVisibleScale = scaleCount;
					for (ScaleLayer scale : _chart.getIndicatorLayer()
							.getLayers()) {
						if (scale.getZzLayer().isVisible()) {
							firstVisibleScale = scale.getLevel();
							break;
						}
					}

					for (int level = _firstLevel; level < firstVisibleScale; level++) {
						final IPivotModel pivotModel = _indicatorModel
								.getPivotModel(level);
						final IRealTimeZZModel rtModel = _indicatorModel
								.getRealTimeZZModel(level);

						final int pivotsCount = pivotModel.countNegPivots(
								dataLayer, lower, upper);

						final int diff = Math.abs(MAX_ITEM_COUNT - pivotsCount);
						if (diff < minDiff && pivotsCount >= 5) {
							minDiff = diff;
							_compressionModel = pivotModel;
							_compressionRTModel = rtModel;
						}
					}
				}
			}

			IDataset basePriceDataset;
			IDataset baseTempPriceDataset;

			if (_compressionModel == null) {
				final IPriceCollection data = _priceModel
						.getPrices(
								dataLayer,
								lower,
								upper,
								_settings.zzCompression ? getMaxNumberOfPricesToShowAsZZ1()
										: Model_MDB
												.getMaxNumberOfPointsToShow());
				basePriceDataset = new TimePriceDataset(data);
				baseTempPriceDataset = new TempPricesDataset(dataLayer,
						_tempPriceModel);

				_rtDataset.setBase(EMPTY_DATASET);
			} else {
				final IPivotCollection data = _compressionModel.getNegPivots(
						dataLayer, lower, upper);
				basePriceDataset = new TimePriceDataset(data);
				baseTempPriceDataset = EMPTY_DATASET;

				_rtDataset.setBase(new RealTimeZZDataset(dataLayer,
						_compressionRTModel));
			}

			_pricesDataset.setBase(basePriceDataset);
			_tempPricesDataset.setBase(baseTempPriceDataset);

			if (_settings.showVolume) {
				PlotRange yrange = _chart.getYRange();
				IPriceCollection col = _priceModel.getVolumes(dataLayer, lower,
						upper, (long) yrange.lower, (long) yrange.upper);
				_volumeDataset.setBase(new TimePriceDataset(col));
			} else {
				_volumeDataset.setBase(EMPTY_DATASET);
			}

			TimeOfDayDataset ds = new TimeOfDayDataset();
			_timeOfDayDataset.setBase(ds);

			TimeOfTheDay[] timesOfTheDay = _chart.getTimeOfTheDaySettings()
					.getTimesOfTheDay();
			for (TimeOfTheDay time : timesOfTheDay) {
				ITimesOfTheDayCollection col = _priceModel
						.getTimeOfTheDayCollection(dataLayer, time.getHour(),
								time.getMinutes(), _chart
										.getTimeOfTheDaySettings()
										.getMaxNumberOfTimesOfTheDay(), lower,
								upper);
				ds.add(col, time.getColor());
			}

			_startRTLinesDataset.setBase(new StartRealtimeLinesDataset(
					_priceModel.getStartRealtime(dataLayer)));

			_edgesDataset.setBase(new LayerEdgesLineDataset(dataLayer, _chart
					.getModel()));
		} else {
			clearDatasets();
		}
	}

	@Override
	public IDataset getAutorangeDataset() {
		return _autoRangeDataset;
	}

	@Override
	public void autorange() {
		// super.autorange();
		IChartUtils.autorange(_chart, getAutorangeDataset(), _rtDataset);

		IDataset base = _volumeDataset.getBase();
		if (base != EMPTY_DATASET) {
			VolumeCollection col = (VolumeCollection) ((TimePriceDataset) base)
					.getCollection();
			PlotRange yrange = _chart.getYRange();
			PlotRange xrange = _chart.getXRange();
			col.computeRange((long) xrange.lower, (long) xrange.upper,
					(long) yrange.lower, (long) yrange.upper);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.FinalLayer#setEmptyDatasets()
	 */
	@Override
	public void clearDatasets() {
		_pricesDataset.setBase(EMPTY_DATASET);
	}

	/**
	 * @return the showAll
	 */
	public boolean isZZCompressed() {
		return _settings.zzCompression;
	}

	/**
	 * @param zzCompressed
	 *            the showAll to set
	 */
	@Deprecated
	public void setZZCompressed(final boolean zzCompressed) {
		_settings.zzCompression = zzCompressed;
	}

	/**
	 * @return
	 */
	public IDataset getCrossSnappingDataset() {
		return _pricesDataset;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxNumberOfPricesToShowAsZZ1() {
		return _maxNumberOfPricesToShowAsZZ1;
	}

	/**
	 * @param filterNumber
	 */
	public void setMaxNumberOfPricesToShowAsZZ1(int filterNumber) {
		this._maxNumberOfPricesToShowAsZZ1 = filterNumber;
	}

	public Double findPoint(long x, long y) {
		return IChartUtils.findDatasetPoint(_pricesDataset, getChart()
				.getXRange(), getChart().getYRange(), x, y);
	}

	public void reloadDefaultProfile() {
		_settings.updateFromProfile(_profiledObject.getDefault());
	}
}
