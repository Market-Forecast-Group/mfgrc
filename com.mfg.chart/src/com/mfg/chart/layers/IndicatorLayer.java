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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.IScaledIndicatorModel;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.IChartUtils;
import com.mfg.chart.ui.interactive.TimeLinesTool.TimeLines.RatioInfo;
import com.mfg.chart.ui.osd.GlobalScaleElementLayer;
import com.mfg.chart.ui.settings.ProfiledObject;

public class IndicatorLayer extends MergedLayer<ScaleLayer> {
	public static final float[][] DEFAULT_COLORS = new float[][] { COLOR_CYAN,
			COLOR_CYAN, COLOR_ORANGE, COLOR_GREEN, COLOR_BLUE, COLOR_RED,
			COLOR_PURPLE, COLOR_CYAN, COLOR_ORANGE, COLOR_GREEN, COLOR_BLUE,
			COLOR_RED, COLOR_PURPLE, COLOR_CYAN, COLOR_ORANGE, COLOR_GREEN,
			COLOR_BLUE, COLOR_RED, COLOR_PURPLE, COLOR_GREEN, COLOR_BLUE,
			COLOR_RED, COLOR_PURPLE, COLOR_CYAN, COLOR_ORANGE, COLOR_GREEN,
			COLOR_BLUE, COLOR_RED, COLOR_PURPLE, COLOR_CYAN, COLOR_ORANGE };

	private final IScaledIndicatorModel _model;
	private final MergedLayer<GlobalScaleElementLayer> _globalLayers;
	private Integer _selectedScale = null;
	private final int _firstLevel;
	private final LinkedList<ScaleLayer> _reversedScales;
	private boolean _filtersEnabled;
	private ARCSettings _arcSettings;
	private ProfiledObject _arcProfiledObject;
	private IndicatorScalesSettings _scalesSettings;
	private ProfiledObject _scalesProfiledObject;
	private AdditionalSettings _additionalSettings;
	private ProfiledObject _additionalProfileObject;
	private ATLSettings _atlSettings;
	private ProfiledObject _atlProfiledObject;

	public static class ATLSettings {
		private static final String K_ENABLED = "enabled";
		public RatioInfo[] ratios;
		public float[] anchorsColor;
		public boolean enabled;

		public ATLSettings() {
			anchorsColor = COLOR_WHITE;
			ratios = new RatioInfo[10];
			for (int i = 0; i < 10; i++) {
				RatioInfo r = new RatioInfo(1, COLOR_BLUE, i == 0, 1, 0);
				ratios[i] = r;
			}

			ratios[0].setColor(COLOR_BLUE);
			ratios[0].setRatio(1);
			ratios[0].setSelected(true);

			ratios[1].setColor(COLOR_GREEN);
			ratios[1].setRatio(0.618f);
			ratios[1].setSelected(true);

			ratios[2].setColor(COLOR_BLUE);
			ratios[2].setRatio(0.5f);
			ratios[2].setSelected(true);

			ratios[3].setColor(COLOR_BLUE);
			ratios[3].setRatio(2);
			ratios[3].setSelected(true);

			ratios[4].setColor(COLOR_GREEN);
			ratios[4].setRatio(1.618f);
			ratios[4].setSelected(true);

			ratios[5].setColor(COLOR_CYAN);
			ratios[5].setRatio(1.8f);
			ratios[5].setSelected(true);

			ratios[6].setColor(COLOR_CYAN);
			ratios[6].setRatio(0.559f);
			ratios[6].setSelected(true);

			enabled = false;
		}

		public void updateFromProfile(Profile p) {
			enabled = p.getBoolean(K_ENABLED, false);
			anchorsColor = p.getFloatArray("anchorsColor", COLOR_WHITE);
			for (int i = 0; i < ratios.length; i++) {
				RatioInfo r = ratios[i];
				String key = "ratio" + i + ".";
				r.setColor(p.getFloatArray(key + "color", COLOR_BLUE));
				r.setLineType(p.getInt(key + "type", 0));
				r.setLineWidth(p.getInt(key + "width", 1));
				r.setRatio(p.getFloat(key + "ratio", 1));
				r.setSelected(p.getBoolean(key + "selected", i == 0));
			}
		}

		public void fillProfile(Profile p) {
			p.putFloatArray("anchorsColor", anchorsColor);
			p.putBoolean(K_ENABLED, enabled);
			for (int i = 0; i < ratios.length; i++) {
				RatioInfo r = ratios[i];
				String key = "ratio" + i + ".";
				p.putFloatArray(key + "color", r.getColor());
				p.putInt(key + "type", r.getLineType());
				p.putInt(key + "width", r.getLineWidth());
				p.putFloat(key + "ratio", r.getRatio());
				p.putBoolean(key + "selected", r.isSelected());
			}
		}
	}

	public static class AdditionalSettings {
		private static final String K_AUTO_TREND_LINES_LINE_TYPE = "autoTrendLinesLineType";
		private static final String K_PROBS_ENABLED = "probsEnabled";
		private static final String K_PROBS_PROFIT_LINE_TYPE = "probsProfitLineType";
		private static final String K_PROBS_PROFIT_LINE_WIDTH = "probsProfitLineWidth";
		private static final String K_PROBS_LOSS_LINE_WIDTH = "probsLossLineWidth";
		private static final String K_PROBS_LOSS_LINE_TYPE = "probsLossLineType";
		private static final String K_AUTO_TREND_LINES_LINE_WIDTH = "autoTrendLinesLineWidth";
		private static final String K_AUTO_TREND_LINES_ENABLED = "autoTrendLinesEnabled";
		public boolean probsEnabled;
		public int probsProfitLineType;
		public int probsProfitLineWidth;
		public int probsLossLineType;
		public int probsLossLineWidth;

		public boolean autoTrendLinesEnabled;
		public int autoTrendLinesLineWidth;
		public int autoTrendLinesLineType;

		public AdditionalSettings() {
			probsEnabled = true;
			probsLossLineType = STIPPLE_FACTOR_3;
			probsLossLineWidth = 1;
			probsProfitLineType = 0;
			probsProfitLineWidth = 1;
			autoTrendLinesEnabled = false;
			autoTrendLinesLineType = 0;
			autoTrendLinesLineWidth = 1;
		}

		public void fillProfile(Profile p) {
			p.putBoolean(K_PROBS_ENABLED, probsEnabled);
			p.putInt(K_PROBS_PROFIT_LINE_TYPE, probsProfitLineType);
			p.putInt(K_PROBS_PROFIT_LINE_WIDTH, probsProfitLineWidth);
			p.putInt(K_PROBS_LOSS_LINE_TYPE, probsLossLineType);
			p.putInt(K_PROBS_LOSS_LINE_WIDTH, probsLossLineWidth);

			p.putBoolean(K_AUTO_TREND_LINES_ENABLED, autoTrendLinesEnabled);
			p.putInt(K_AUTO_TREND_LINES_LINE_WIDTH, autoTrendLinesLineWidth);
			p.putInt(K_AUTO_TREND_LINES_LINE_TYPE, autoTrendLinesLineType);
		}

		public void updateFromProfile(Profile p) {
			probsEnabled = p.getBoolean(K_PROBS_ENABLED, true);
			probsLossLineType = p.getInt(K_PROBS_LOSS_LINE_TYPE,
					STIPPLE_FACTOR_3);
			probsLossLineWidth = p.getInt(K_PROBS_LOSS_LINE_WIDTH, 1);
			probsProfitLineType = p.getInt(K_PROBS_PROFIT_LINE_TYPE, 0);
			probsProfitLineWidth = p.getInt(K_PROBS_PROFIT_LINE_WIDTH, 1);

			autoTrendLinesEnabled = p.getBoolean(K_AUTO_TREND_LINES_ENABLED,
					false);
			autoTrendLinesLineType = p.getInt(K_AUTO_TREND_LINES_LINE_TYPE, 0);
			autoTrendLinesLineWidth = p
					.getInt(K_AUTO_TREND_LINES_LINE_WIDTH, 1);
		}
	}

	public static class IndicatorScalesSettings {
		private static final String K_MAX_VISIBLE_BANDS = "maxVisibleBands";
		private static final String K_MAX_VISIBLE_SCALES = "maxVisibleScales";
		private static final String K_FILTER_ENABLED = "filterEnabled";

		public float[][] scalesColors;
		public boolean[] scalesVisible;
		public boolean filterEnabled;
		public int maxVisibleScales;
		public int maxVisibleBands;

		public IndicatorScalesSettings() {
			scalesColors = Arrays.copyOf(DEFAULT_COLORS, DEFAULT_COLORS.length);

			scalesVisible = new boolean[scalesColors.length];
			for (int i = 0; i < scalesVisible.length; i++) {
				scalesVisible[i] = true;
			}
			filterEnabled = true;
			maxVisibleScales = 3;
			maxVisibleBands = 1;
		}

		public void fillProfile(Profile p) {
			p.putBoolean(K_FILTER_ENABLED, filterEnabled);
			for (int i = 0; i < scalesVisible.length; i++) {
				p.putBoolean("scale" + i + ".visible", scalesVisible[i]);
				p.putFloatArray("scale" + i + ".color", scalesColors[i]);
			}
			p.putInt(K_MAX_VISIBLE_SCALES, maxVisibleScales);
			p.putInt(K_MAX_VISIBLE_BANDS, maxVisibleBands);
		}

		public void updateFromProfile(Profile p) {
			filterEnabled = p.getBoolean(K_FILTER_ENABLED, true);
			for (int i = 0; i < DEFAULT_COLORS.length; i++) {
				scalesVisible[i] = p.getBoolean("scale" + i + ".visible", true);
				scalesColors[i] = p.getFloatArray("scale" + i + ".color",
						DEFAULT_COLORS[i]);
			}
			maxVisibleBands = p.getInt(K_MAX_VISIBLE_BANDS, 1);
			maxVisibleScales = p.getInt(K_MAX_VISIBLE_SCALES, 3);
		}
	}

	public static class ARCSettings {
		private static final String K_TH_SHAPE_WIDTH = "thShapeWidth";
		private static final String K_TH_SHAPE_TYPE = "thShapeType";
		private static final String K_ZZ_PARALLEL = "zzParallel";
		private static final String K_ZZ_MARKERS_ENABLED = "zzMarkersEnabled";
		private static final String K_CHANNELS_ENABLED = "channelsEnabled";
		private static final String K_CHANNELS_WIDTH = "channelsWidth";
		private static final String K_CHANNELS_TYPE = "channelsType";
		private static final String K_BANDS_ENABLED = "bandsEnabled";
		private static final String K_BANDS_CENTER_WIDTH = "bandsCenterWidth";
		private static final String K_BANDS_CENTER_TYPE = "bandsCenterType";
		private static final String K_BANDS_TOP_BOTTOM_WIDTH = "bandsTopBottomWidth";
		private static final String K_BANDS_TOP_BOTTOM_TYPE = "bandsTopBottomType";
		private static final String K_ZZ_ENABLED = "zzEnabled";
		private static final String K_ZZ_TYPE = "zzType";
		private static final String K_ZZ_WIDTH = "zzWidth";

		public int zzWidth;
		public int zzType;
		public boolean zzEnabled;
		public boolean zzMarkersEnabled;
		public boolean zzParallel;
		public int thShapeType;
		public int thShapeWidth;

		public int bandsTopBottomWidth;
		public int bandsTopBottomType;
		public int bandsCenterWidth;
		public int bandsCenterType;
		public boolean bandsEnabled;

		public int channelsWidth;
		public int channelsType;
		public boolean channelsEnabled;

		public ARCSettings() {
			zzWidth = 1;
			zzType = 0;
			zzEnabled = true;
			zzMarkersEnabled = false;
			zzParallel = false;
			thShapeType = 1;
			thShapeWidth = 0;

			bandsTopBottomType = 0;
			bandsTopBottomWidth = 1;
			bandsCenterType = 0;
			bandsCenterWidth = 2;
			bandsEnabled = true;

			channelsType = 0;
			channelsWidth = 1;
			channelsEnabled = false;
		}

		public void fillProfile(Profile p) {
			p.putInt(K_ZZ_WIDTH, zzWidth);
			p.putInt(K_ZZ_TYPE, zzType);
			p.putBoolean(K_ZZ_ENABLED, zzEnabled);
			p.putBoolean(K_ZZ_MARKERS_ENABLED, zzMarkersEnabled);
			p.putBoolean(K_ZZ_PARALLEL, zzParallel);
			p.putInt(K_TH_SHAPE_TYPE, thShapeType);
			p.putInt(K_TH_SHAPE_WIDTH, thShapeWidth);

			p.putInt(K_BANDS_TOP_BOTTOM_TYPE, bandsTopBottomType);
			p.putInt(K_BANDS_TOP_BOTTOM_WIDTH, bandsTopBottomWidth);
			p.putInt(K_BANDS_CENTER_TYPE, bandsCenterType);
			p.putInt(K_BANDS_CENTER_WIDTH, bandsCenterWidth);
			p.putBoolean(K_BANDS_ENABLED, bandsEnabled);

			p.putInt(K_CHANNELS_TYPE, channelsType);
			p.putInt(K_CHANNELS_WIDTH, channelsWidth);
			p.putBoolean(K_CHANNELS_ENABLED, channelsEnabled);
		}

		public void updateFromProfile(Profile p) {
			zzWidth = p.getInt(K_ZZ_WIDTH, 1);
			zzType = p.getInt(K_ZZ_TYPE, 0);
			zzEnabled = p.getBoolean(K_ZZ_ENABLED, true);
			zzMarkersEnabled = p.getBoolean(K_ZZ_MARKERS_ENABLED, false);
			zzParallel = p.getBoolean(K_ZZ_PARALLEL, false);
			thShapeType = p.getInt(K_TH_SHAPE_TYPE, 1);
			thShapeWidth = p.getInt(K_TH_SHAPE_WIDTH, 0);

			bandsCenterType = p.getInt(K_BANDS_CENTER_TYPE, 0);
			bandsCenterWidth = p.getInt(K_BANDS_CENTER_WIDTH, 2);
			bandsTopBottomType = p.getInt(K_BANDS_TOP_BOTTOM_TYPE, 0);
			bandsTopBottomWidth = p.getInt(K_BANDS_TOP_BOTTOM_WIDTH, 1);
			bandsEnabled = p.getBoolean(K_BANDS_ENABLED, true);

			channelsType = p.getInt(K_CHANNELS_TYPE, 0);
			channelsWidth = p.getInt(K_CHANNELS_WIDTH, 1);
			channelsEnabled = p.getBoolean(K_CHANNELS_ENABLED, false);
		}
	}

	public IndicatorLayer(final Chart chart, int firstLevel) {
		super("Scales", chart);
		this._firstLevel = firstLevel;
		// TODO: Get it from preferences
		_filtersEnabled = true;

		initSettings();

		_model = chart.getModel().getScaledIndicatorModel();

		final int scaleCount = _model.getScaleCount();

		_globalLayers = new MergedLayer<>("All", chart);

		for (int level = firstLevel; level <= scaleCount; level++) {
			final ScaleLayer scale = new ScaleLayer("Scale " + level, this,
					level);
			addLayer(scale);

			for (final IElementScaleLayer layer : scale.getLayers()) {
				final String name = layer.getName();
				if (!_globalLayers.containsLayer(name)) {
					final GlobalScaleElementLayer elementLayer = new GlobalScaleElementLayer(
							name, chart);
					_globalLayers.addLayer(elementLayer);
				}
				final GlobalScaleElementLayer elementLayer = _globalLayers
						.getLayer(name);
				elementLayer.addLayer(layer);
			}
		}

		_reversedScales = new LinkedList<>();
		for (ScaleLayer layer : getScales()) {
			_reversedScales.addFirst(layer);
		}
	}

	public ProfiledObject getAdditionalProfileObject() {
		return _additionalProfileObject;
	}

	public AdditionalSettings getAdditionalSettings() {
		return _additionalSettings;
	}

	public void setAdditionalSettings(AdditionalSettings additionalSettings) {
		_additionalSettings = additionalSettings;
	}

	void initSettings() {
		{
			_arcSettings = new ARCSettings();
			_arcProfiledObject = new ProfiledObject() {

				@Override
				protected List<Profile> createProfilePresets() {
					Profile p = new Profile("Profile 1");
					ARCSettings s = new ARCSettings();
					s.fillProfile(p);
					return Arrays.asList(p);
				}

				@Override
				public String getProfileKeySet() {
					return "indicatorARCSettings";
				}
			};
			_arcSettings.updateFromProfile(_arcProfiledObject.getProfile());
		}
		{
			Profile p = _arcProfiledObject.getProfilesManager().getDefault(
					_arcProfiledObject.getProfileKeySet());
			_arcSettings.updateFromProfile(p);

			_scalesSettings = new IndicatorScalesSettings();
			_scalesProfiledObject = new ProfiledObject() {
				@Override
				protected List<Profile> createProfilePresets() {
					Profile p2 = new Profile("Profile 1");
					IndicatorScalesSettings s = new IndicatorScalesSettings();
					s.fillProfile(p2);
					return Arrays.asList(p2);
				}

				@Override
				public String getProfileKeySet() {
					return "indicatorScales";
				}
			};
			_scalesSettings.updateFromProfile(_scalesProfiledObject
					.getProfile());
			applyScalesSettings();
		}

		{
			_additionalSettings = new AdditionalSettings();
			_additionalProfileObject = new ProfiledObject() {

				@Override
				protected List<Profile> createProfilePresets() {
					Profile p2 = new Profile("Profile 1");
					AdditionalSettings s = new AdditionalSettings();
					s.fillProfile(p2);
					return Arrays.asList(p2);
				}

				@Override
				public String getProfileKeySet() {
					return "additionalIndicatorSettings";
				}
			};
			_additionalSettings.updateFromProfile(_additionalProfileObject
					.getProfile());
		}
		{
			_atlSettings = new ATLSettings();
			_atlProfiledObject = new ProfiledObject() {

				@Override
				protected List<Profile> createProfilePresets() {
					Profile p2 = new Profile("Profile 1");
					ATLSettings s = new ATLSettings();
					s.fillProfile(p2);
					return Arrays.asList(p2);
				}

				@Override
				public String getProfileKeySet() {
					return "indicatorAutoTimeLines";
				}
			};
			_atlSettings.updateFromProfile(_atlProfiledObject.getProfile());
		}
	}

	public ATLSettings getAtlSettings() {
		return _atlSettings;
	}

	public void setAtlSettings(ATLSettings atlSettings) {
		_atlSettings = atlSettings;
	}

	public ProfiledObject getAtlProfiledObject() {
		return _atlProfiledObject;
	}

	public void setAtlProfiledObject(ProfiledObject atlProfiledObject) {
		_atlProfiledObject = atlProfiledObject;
	}

	public void applyScalesSettings() {
		setFiltersEnabled(_scalesSettings.filterEnabled);
		for (ScaleLayer layer : getScales()) {
			layer.setVisible(_scalesSettings.scalesVisible[layer.getLevel()]);
		}
	}

	public ProfiledObject getScalesProfiledObject() {
		return _scalesProfiledObject;
	}

	public IndicatorScalesSettings getScalesSettings() {
		return _scalesSettings;
	}

	public void setScalesSettings(IndicatorScalesSettings scalesSettings) {
		_scalesSettings = scalesSettings;
	}

	public ProfiledObject getArcProfiledObject() {
		return _arcProfiledObject;
	}

	public ARCSettings getArcSettings() {
		return _arcSettings;
	}

	public void setArcSettings(ARCSettings arcSettings) {
		_arcSettings = arcSettings;
	}

	/**
	 * @return the filtersEnabled
	 */
	public boolean isFiltersEnabled() {
		return _filtersEnabled;
	}

	/**
	 * @param filtersEnabled
	 *            the filtersEnabled to set
	 */
	public void setFiltersEnabled(boolean filtersEnabled) {
		this._filtersEnabled = filtersEnabled;
		if (filtersEnabled) {
			getChart().setAutoRangeEnabled(true);
		}
	}

	public void setSelectedScale(final Integer selectedScale) {
		// if (selectedScale == this.selectedScale) {
		// this.selectedScale = null;
		// } else {
		// this.selectedScale = selectedScale;
		// }
		this._selectedScale = selectedScale;
	}

	public Integer getSelectedScale() {
		return _selectedScale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.MergedLayer#updateDataset()
	 */
	@Override
	public void updateDataset() {
		if (isFiltersEnabled()) {
			// update non bands layers visivility
			int maxVisible = getMaxNumberOfVisibleScales();
			int countVisible = 0;
			for (final ScaleLayer scaleLayer : getLayers()) {
				scaleLayer.clearDatasets();
				if (scaleLayer.isEnabled()) {
					// bands has other visibility rules, see below.
					if (countVisible < maxVisible) {
						scaleLayer.updateDataset(false);
						if (scaleLayer.computeFilterVisible()) {
							countVisible++;
						}
					} else {
						for (IElementScaleLayer elementLayer : scaleLayer
								.getLayers()) {
							elementLayer.setVisible(false);
						}
					}
				}
			}

			// update bands visibility

			int max = getMaxNumberOfVisibleBands();
			int count = 0;

			for (ScaleLayer layer : _reversedScales) {
				if (layer.getZzLayer().isVisible()) {
					boolean visible = count < max;
					layer.getBandsLayer().setVisible(visible);
					layer.getBands2Layer().setVisible(visible);
					if (layer.getBandsLayer().containsInRange()) {
						count++;
					}
				} else {
					layer.getBandsLayer().setVisible(false);
					layer.getBands2Layer().setVisible(false);
				}
			}
		} else {
			for (ScaleLayer layer : layers) {
				layer.clearDatasets();
				layer.updateDataset();
			}
		}
	}

	/**
	 * @return the maxNumberOfVisibleScales
	 */
	public int getMaxNumberOfVisibleScales() {
		return _scalesSettings.maxVisibleScales;
	}

	/**
	 * @return the maxNumberOfVisibleBands
	 */
	public int getMaxNumberOfVisibleBands() {
		return _scalesSettings.maxVisibleBands;
	}

	@Override
	public IDataset getAutorangeDataset() {
		return getLayers().getLast().getAutorangeDataset();
	}

	/**
	 * Alias of {@link #getLayers()}
	 * 
	 * @return the scales
	 */
	public LinkedList<ScaleLayer> getScales() {
		return getLayers();
	}

	public LinkedList<ScaleLayer> getReversedScales() {
		return _reversedScales;
	}

	public ScaleLayer getScaleLayer(final int scale) {
		return getLayers().get(scale - _firstLevel);
	}

	/**
	 * @return the firstLevel
	 */
	public int getFirstLevel() {
		return _firstLevel;
	}

	/**
	 * @return
	 */
	public MergedLayer<GlobalScaleElementLayer> getGlobalLayer() {
		return _globalLayers;
	}

	/**
	 *
	 */
	public void swapZZWithChannels() {
		for (final ScaleLayer scaleLayer : getScales()) {
			final ZZLayer zzLayer = scaleLayer.getZzLayer();
			final ChannelLayer channelLayer = scaleLayer.getChannelLayer();
			final PivotLayer pivotLayer = scaleLayer.getPivotLayer();

			if (zzLayer.isEnabled() != channelLayer.isEnabled()) {
				final boolean zzEnabled = zzLayer.isEnabled();
				zzLayer.setEnabled(!zzEnabled);
				channelLayer.setEnabled(zzEnabled);
				pivotLayer.setVisible(channelLayer.isVisible());
			}
		}
	}

	public void setVisibleByUser(IChartLayer layer, boolean visible) {
		if (layer instanceof GlobalScaleElementLayer) {
			layer.setEnabled(true);
			layer.setVisible(visible);
			layer.setEnabled(visible);
			if (!visible) {
				layer.clearDatasets();
			}
		} else {
			setFiltersEnabled(false);
			if (layer instanceof PivotLayer || layer instanceof ChannelLayer) {
				layer.setEnabled(visible);
				layer.setVisible(visible);
			} else {
				layer.setVisible(visible);
			}
		}
	}

	public static class PivotReference {
		private final long _time;
		private final long _price;
		private final int _level;

		public static PivotReference fromPoint(Point2D point, int level) {
			return new PivotReference((long) point.getX(), (long) point.getY(),
					level);
		}

		public PivotReference(long time, long price, int level) {
			_time = time;
			_price = price;
			_level = level;
		}

		public long getTime() {
			return _time;
		}

		public long getPrice() {
			return _price;
		}

		public int getLevel() {
			return _level;
		}

		public boolean samePosition(PivotReference other) {
			if (other == null) {
				return false;
			}
			if (_price != other._price)
				return false;
			if (_time != other._time)
				return false;
			return true;
		}

		public String key() {
			return _time + "x" + _price;
		}

		@Override
		public String toString() {
			return key() + " level " + _level;
		}
	}

	/**
	 * Find visible pivots on ZZ or Pivot layers.
	 * 
	 * @param time
	 * @param price
	 * @param level
	 *            Set it to -1 if you want to look in higher levels.
	 * @return
	 */
	public PivotReference findVisiblePivot(long time, long price) {
		Point2D found = null;
		double dist = Double.MAX_VALUE;
		int selLevel = 0;

		for (ScaleLayer scale : getReversedScales()) {
			if (scale.isVisible()) {
				IDataset ds = scale.getZzLayer().getPivotDataset();
				PlotRange xr = getChart().getXRange();
				PlotRange yr = getChart().getYRange();
				Point2D point = IChartUtils.findDatasetPoint(ds, xr, yr, time,
						price);
				if (point != null) {
					double d = Point2D.distance(point.getX(), point.getY(),
							time, price);
					if (d < dist) {
						found = point;
						dist = d;
						selLevel = scale.getLevel();
					}
				}
			}
		}

		double len = getChart().getXRange().getLength();
		if (found != null && dist / len * 100 < 10) {
			return new PivotReference((long) found.getX(), (long) found.getY(),
					selLevel);
		}
		return null;
	}

	public List<PivotReference> findMatchingPivots(long time, long price) {
		List<PivotReference> list = new ArrayList<>();
		for (ScaleLayer scale : getScales()) {
			if (scale.isVisible()) {
				IDataset ds = scale.getZzLayer().getPivotDataset();
				PlotRange xr = getChart().getXRange();
				PlotRange yr = getChart().getYRange();
				Point2D point = IChartUtils.findDatasetPoint(ds, xr, yr, time,
						price);
				if (point != null) {
					if (point.getX() == time && point.getY() == price) {
						list.add(PivotReference.fromPoint(point,
								scale.getLevel()));
					}
				}
			}
		}
		return list;
	}

	public void reloadDefaultProfile() {
		_scalesSettings.updateFromProfile(_scalesProfiledObject.getDefault());
		_additionalSettings.updateFromProfile(_additionalProfileObject
				.getDefault());
		_arcSettings.updateFromProfile(_arcProfiledObject.getDefault());
		_atlSettings.updateFromProfile(_atlProfiledObject.getDefault());
		applyScalesSettings();
	}
}
