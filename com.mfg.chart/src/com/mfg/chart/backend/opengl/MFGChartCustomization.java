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
package com.mfg.chart.backend.opengl;

import java.awt.geom.Point2D;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.media.opengl.GL2;

import org.mfg.opengl.chart.DefaultGLChartCustomization;
import org.mfg.opengl.chart.PlotRange;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.ISyntheticModel;
import com.mfg.chart.model.ITradingModel;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.model.ITemporalPricesModel;
import com.mfg.chart.model.PhysicalPriceModel_MDB;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.ChartType;

/**
 * @author arian
 * 
 */
public class MFGChartCustomization extends DefaultGLChartCustomization {

	private final Chart _chart;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss MMM-dd-yyyy");
	float[] _tickBg;
	private final float[] _tickFg;
	private float[] _lastPriceGridLineColor;
	GLUT glut = new GLUT();
	private SimpleDateFormat _highResolutionDateFormat;
	private SimpleDateFormat _lowResolutionDateFormat;

	public MFGChartCustomization(Chart chart) {
		this._chart = chart;
		_tickBg = COLOR_GREEN;
		_tickFg = COLOR_BLACK;
		_lastPriceGridLineColor = chart.getGLChart().getSettings()
				.getGridColor();

		if (isPhysicalChart(chart)) {
			_highResolutionDateFormat = new SimpleDateFormat(
					"HH:mm:ss MMM-dd-yyyy");
			_lowResolutionDateFormat = new SimpleDateFormat("MMM-dd-yyyy");
		}
	}

	private static boolean isPhysicalChart(Chart chart) {
		return chart.getType() == ChartType.SYNTHETIC
				|| chart.getModel().getPriceModel() instanceof PhysicalPriceModel_MDB;
	}

	@Override
	public String formatXTick(double tick) {
		int dataLayer = _chart.getDataLayer();
		Format fmt;
		long time = (long) tick;
		if (_highResolutionDateFormat == null) {
			// fake time model
			fmt = super.getFormatXValues();
		} else {
			if (TimeUnit.MILLISECONDS.toDays((long) _chart.getXRange()
					.getLength()) > 0) {
				fmt = _lowResolutionDateFormat;
			} else {
				fmt = _highResolutionDateFormat;
			}

			if (_chart.getType() == ChartType.EQUITY) {
				ITradingModel executionModel = _chart.getModel()
						.getTradingModel();
				time = executionModel.getEquityRealTime(dataLayer, (long) tick);
			} else if (_chart.getType() == ChartType.SYNTHETIC) {
				time = _chart.getModel().getSyntheticModel()
						.getRealDate((long) tick);
			} else {
				IPriceModel priceModel = _chart.getModel().getPriceModel();

				time = priceModel.getPhysicalTime_from_DisplayTime(dataLayer,
						(long) tick);
			}
		}
		return fmt.format(Long.valueOf(time));
	}

	public float[] getLastPriceGridLineColor() {
		return _lastPriceGridLineColor;
	}

	public void setLastPriceGridLineColor(float[] lastPriceGridLineColor) {
		this._lastPriceGridLineColor = lastPriceGridLineColor;
	}

	public float[] getLastPriceLabelColor() {
		return _tickBg;
	}

	/**
	 * @param tickBg
	 *            the tickBg to set
	 */
	public void setLastPriceLabelColor(float[] tickBg) {
		this._tickBg = tickBg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mfg.opengl.chart.DefaultGLChartCustomization#getYTickBackgroundColor
	 * (double)
	 */
	@Override
	public float[] getYTickBackgroundColor(double yTick) {
		Integer lastPrice = getLastPrice(_chart.getDataLayer());
		return lastPrice != null && yTick == lastPrice.doubleValue() ? _tickBg
				: null;
	}

	@Override
	public float[] getYTickGridLineColor(double yTick) {
		Integer lastPrice = getLastPrice(_chart.getDataLayer());
		return lastPrice != null && yTick == lastPrice.doubleValue() ? _lastPriceGridLineColor
				: null;
	}

	@Override
	public float[] getYTickForegroundColor(double yTick) {
		return _tickFg;
	}

	@Override
	public String getXTooltip(double crossPlotX, double crossPlotY) {
		int dataLayer = _chart.getDataLayer();
		String tooltip = null;
		long fakeTime = 0;
		long realTime = 0;
		ChartType type = _chart.getType();
		switch (type) {
		case EQUITY:
			ITradingModel executionModel = _chart.getModel().getTradingModel();
			fakeTime = executionModel.getEquityFakeTime((long) crossPlotX);
			realTime = executionModel.getEquityRealTime(dataLayer,
					(long) crossPlotX);
			break;
		case TRADING:
			tooltip = getOrderTooltip(crossPlotX, crossPlotY);
			if (tooltip != null) {
				tooltip = tooltip.split(";")[0];
			}
			break;
		case SYNTHETIC:
			ISyntheticModel synthModel = _chart.getModel().getSyntheticModel();
			realTime = synthModel.getRealDate(crossPlotX);
			tooltip = dateFormat.format(Long.valueOf(realTime));
			break;
		// $CASES-OMITTED$
		default:
			break;
		}

		if (tooltip == null) {
			IPriceModel priceModel = _chart.getModel().getPriceModel();
			fakeTime = priceModel.getFakeTime_from_DisplayTime(dataLayer,
					(long) crossPlotX);
			realTime = priceModel.getPhysicalTime_from_FakeTime(dataLayer,
					fakeTime);
			tooltip = "F" + fakeTime + " "
					+ dateFormat.format(Long.valueOf(realTime));
			if (_chart.getPriceLayer().getSettings().showVolume) {
				int vol = priceModel.getVolume_from_FakeTime(
						_chart.getDataLayer(), fakeTime);
				tooltip += " VOL=" + vol;
			}
		}

		return tooltip;
	}

	/**
	 * @param crossPlotX
	 * @param crossPlotY
	 * @return
	 */
	private String getOrderTooltip(double crossPlotX, double crossPlotY) {
		String tooltip;
		ITradingModel executionModel = _chart.getModel().getTradingModel();
		double factor = _chart.getPlotScreenWidth()
				/ _chart.getXRange().getLength();
		tooltip = executionModel.getStopLoss_TakeProfit_Tooltip(
				(long) crossPlotX, (long) crossPlotY, factor);
		return tooltip;
	}

	@Override
	public String getYTooltip(double crossPlotX, double crossPlotY) {
		String tooltip = null;
		if (_chart.getType() == ChartType.EQUITY) {
			tooltip = _chart.getModel().getTradingModel()
					.getEquityTooltip(crossPlotX);
		} else if (_chart.getType() == ChartType.TRADING) {
			tooltip = getOrderTooltip(crossPlotX, crossPlotY);
			if (tooltip != null) {
				tooltip = tooltip.split(";")[1];
			}
		}
		if (tooltip == null) {
			tooltip = super.getYTooltip(crossPlotX, crossPlotY);
		}
		return tooltip;
	}

	@Override
	public List<Double> computeExtraYTicks(PlotRange yrange) {
		Integer tick = getLastPrice(_chart.getDataLayer());
		return tick == null ? null : Arrays.asList(Double.valueOf(tick
				.doubleValue()));
	}

	private Integer getLastPrice(int dataLayer) {
		IChartModel model = _chart.getModel();
		ITemporalPricesModel tempModel = model.getTemporalPricesModel();
		Integer tick = null;
		Point2D point = tempModel.getTempTick(dataLayer);
		if (point == null) {
			tick = model.getPriceModel().getLastPrice(dataLayer);
		} else {
			tick = Integer.valueOf((int) point.getY());
		}
		return tick;
	}

	@Override
	public void paintExtraGrid(GL2 gl, int width, int height) {
		super.paintExtraGrid(gl, width, height);
		if (_chart.getModel().getDataLayerCount() > 0) {
			gl.glColor3fv(COLOR_DARK_RED, 0);
			gl.glRasterPos2i(_chart.glChart.xMargin + 5, height - 20);
			int dataLayer = _chart.getDataLayer();
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Layer "
					+ (dataLayer + 1));
		}
	}

	private static final String PREF_CUSTOM_LAST_PRICE_GRID_LINE_COLOR = "mfgchart.lasPriceGridLineColor";
	private static final String PREF_CUSTOM_LAST_PRICE_TICK_LABEL__COLOR = "mfgchart.lastPriceTickLabelColor";

	public void updateFromProfile(Profile p) {
		setLastPriceGridLineColor(p.getFloatArray(
				PREF_CUSTOM_LAST_PRICE_GRID_LINE_COLOR, _chart.getGLChart()
						.getSettings().getGridColor()));
		setLastPriceLabelColor(p.getFloatArray(
				PREF_CUSTOM_LAST_PRICE_TICK_LABEL__COLOR, COLOR_GREEN));
	}

	public void fillProfile(Profile p) {
		p.putFloatArray(PREF_CUSTOM_LAST_PRICE_GRID_LINE_COLOR,
				getLastPriceGridLineColor());
		p.putFloatArray(PREF_CUSTOM_LAST_PRICE_TICK_LABEL__COLOR,
				getLastPriceLabelColor());
	}
}
