package com.mfg.chart.ui.interactive;

import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL2;

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.PlotRange;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.IGLConstantsMFG;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.model.IRealTimeZZModel;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.IChartUtils;

public class ForecastingTool extends InteractiveTool {

	public static class Settings implements Cloneable {
		private static final String K_SHAPE_TYPE = "shapeType";
		private static final String K_SHAPE_SIZE = "shapeSize";
		public int shapeSize;
		public int shapeType;

		public void fillProfile(Profile p) {
			p.putInt(K_SHAPE_SIZE, shapeSize);
			p.putInt(K_SHAPE_TYPE, shapeType);
		}

		public void updateFromProfile(Profile p) {
			shapeSize = p.getInt(K_SHAPE_SIZE, 0);
			shapeType = p.getInt(K_SHAPE_TYPE, 0);
		}

		@Override
		public Settings clone() {
			Profile p = new Profile();
			fillProfile(p);
			Settings s = new Settings();
			s.updateFromProfile(p);
			return s;
		}
	}

	private static final String PROFILE_KEY_SET = "ForecastingTool";
	private Settings _settings;

	public ForecastingTool(Chart chart) {
		super("Forecasting", chart, null);
		setAlwaysPaint(false);
		_settings = new Settings();
		Profile p = getProfilesManager().getDefault(PROFILE_KEY_SET);
		_settings.updateFromProfile(p);
	}

	@Override
	protected List<Profile> createProfilePresets() {
		Profile p = new Profile("Profile 1");
		Settings s = new Settings();
		s.fillProfile(p);
		return Arrays.asList(p);
	}

	public Settings getSettings() {
		return _settings;
	}

	public void setSettings(Settings settings) {
		_settings = settings;
	}

	@Override
	public String getKeywords() {
		return "forecasting";
	}

	@Override
	public String getProfileKeySet() {
		return PROFILE_KEY_SET;
	}

	@Override
	public void paintOnPlotMatrix(GL2 gl, int w, int h) {
		Chart chart = getChart();
		for (ScaleLayer scaleLayer : chart.getIndicatorLayer().getScales()) {
			if (scaleLayer.getZzLayer().isVisible()) {
				paintPercentiles(gl, scaleLayer.getLevel(),
						scaleLayer.getLayerColor());
			}
		}
	}

	private void paintPercentiles(GL2 gl, int scale, float[] color) {
		Chart chart = getChart();
		int dataLayer = chart.getDataLayer();
		IRealTimeZZModel zzModel = chart.getModel().getScaledIndicatorModel()
				.getRealTimeZZModel(scale);
		double[] stats = zzModel.getPercentilStatistics(dataLayer, scale);

		if (stats != null) {
			long x = zzModel.getTime2(dataLayer);
			IPriceModel priceModel = chart.getModel().getPriceModel();
			long tick = (long) (priceModel.getTickSize() * Math.pow(10,
					priceModel.getTickScale()));

			BitmapData data = IGLConstantsMFG.SHAPES[_settings.shapeSize][_settings.shapeType];

			for (double y : stats) {
				double low = y / tick * tick;
				double up = (y / tick + 1) * tick;
				if (y - low < up - y) {
					y = low;
				} else {
					y = up;
				}
				gl.glColor4fv(color, 0);
				gl.glRasterPos2d(x, y);
				gl.glBitmap(data.width, data.height, data.x, data.y, 0, 0,
						data.bitmap, 0);
			}
		}
	}

	@Override
	public void autorange() {
		Chart chart = getChart();

		PlotRange xrange = chart.glChart.plot.xrange;
		PlotRange yrange = chart.glChart.plot.yrange;

		double min = yrange.lower;
		double max = yrange.upper;

		IPriceModel priceModel = chart.getModel().getPriceModel();
		long tick = (long) (priceModel.getTickSize() * Math.pow(10,
				priceModel.getTickScale()));

		for (ScaleLayer scaleLayer : chart.getIndicatorLayer().getScales()) {
			if (scaleLayer.getZzLayer().isVisible()) {
				int scale = scaleLayer.getLevel();
				int dataLayer = chart.getDataLayer();
				IRealTimeZZModel zzModel = chart.getModel()
						.getScaledIndicatorModel().getRealTimeZZModel(scale);
				long x = zzModel.getTime2(dataLayer);

				if (!xrange.contains(x)) {
					continue;
				}

				double[] stats = zzModel.getPercentilStatistics(dataLayer,
						scale);

				if (stats != null) {
					for (int i = 0; i < stats.length; i++) {
						if (i == 3 || i == 7)
							continue;

						double y = stats[i];
						double low = y / tick * tick;
						double up = (y / tick + 1) * tick;
						if (y - low < up - y) {
							y = low;
						} else {
							y = up;
						}

						if (y < min) {
							min = y;
						}

						if (y > max) {
							max = y;
						}
					}
				}
			}
		}

		IChartUtils.fixAutorange(chart, min, max);
	}
}
