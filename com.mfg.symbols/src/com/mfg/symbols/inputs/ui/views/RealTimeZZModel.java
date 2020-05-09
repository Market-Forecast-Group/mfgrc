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
package com.mfg.symbols.inputs.ui.views;

import java.awt.Point;

import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IRealTimeZZModel;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * @author arian
 * 
 */
public class RealTimeZZModel implements IRealTimeZZModel {
	private final LayeredIndicator _layeredIndicator;
	private final int _level;
	protected final IChartModel _chartModel;

	public RealTimeZZModel(LayeredIndicator layeredIndicator, int level,
			IChartModel chartModel) {
		super();
		this._chartModel = chartModel;
		this._layeredIndicator = layeredIndicator;
		this._level = level;
	}

	public MultiscaleIndicator getIndicator(int dataLayer) {
		return _layeredIndicator.getLayers().get(dataLayer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeZZModel#isCompleted()
	 */
	@Override
	public boolean isCompleted(int dataLayer) {
		try {
			MultiscaleIndicator indicator = getIndicator(dataLayer);
			Pivot lastPivot = indicator.getLastPivot(0, _level);
			Point tentativePivot = indicator.getCurrentTentativePivot(_level);
			return lastPivot != null && tentativePivot != null;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeZZModel#getTime1()
	 */
	@Override
	public long getTime1(int dataLayer) {
		MultiscaleIndicator indicator = getIndicator(dataLayer);
		return indicator.getLastPivot(0, _level).getPivotTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeZZModel#getTime2()
	 */
	@Override
	public long getTime2(int dataLayer) {
		MultiscaleIndicator indicator = getIndicator(dataLayer);
		return (long) indicator.getCurrentTentativePivot(_level).getX();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeZZModel#getPrice1()
	 */
	@Override
	public double getPrice1(int dataLayer) {
		MultiscaleIndicator indicator = getIndicator(dataLayer);
		return indicator.getLastPivot(0, _level).getPivotPrice();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.model.IRealTimeZZModel#getPrice2()
	 */
	@Override
	public double getPrice2(int dataLayer) {
		MultiscaleIndicator indicator = getIndicator(dataLayer);
		return indicator.getCurrentTentativePivot(_level).getY();
	}

	@Override
	public double getTHPrice(int dataLayer) {
		MultiscaleIndicator indicator = getIndicator(dataLayer);
		return indicator.getConfirmThreshold(_level);
	}

	@Override
	public double getTHTime(int dataLayer) {
		return getTime2(dataLayer);
	}

	// layer x scale x percentile
	private double[][][] _statsMap = null;

	@Override
	public double[] getPercentilStatistics(int dataLayer, int scale) {
		MultiscaleIndicator indicator = getIndicator(dataLayer);
		double[] stats = indicator.getStatsForLevel(scale);

//		out.println(scale + ": stats " + Arrays.toString(stats));

		if (stats == null) {
			return null;
		}

		// out.println("stats scale " + scale + " layer " + dataLayer +
		// " array "
		// + Arrays.toString(stats));

		if (_statsMap == null) {
			_statsMap = new double[3][indicator.getChscalelevels() + 1][4 + 4];
		}

		double[] stats2 = _statsMap[dataLayer][scale];
		Pivot pivot = indicator.getLastPivot(0, scale);
		if (pivot == null) {
			return null;
		}
		double th = pivot.getConfirmPrice();
		double delta = th - pivot.getPivotPrice();
		for (int i = 0; i < stats.length; i++) {
			double p = stats[i];
			double value = th + delta * p;
			stats2[i] = value;
		}

		th = getTHPrice(dataLayer);
		delta = th - getPrice2(dataLayer);
		for (int i = 0; i < stats.length; i++) {
			double p = stats[i];
			double value = th + delta * p;
			stats2[4 + i] = value;
		}

		return stats2;
	}

}
