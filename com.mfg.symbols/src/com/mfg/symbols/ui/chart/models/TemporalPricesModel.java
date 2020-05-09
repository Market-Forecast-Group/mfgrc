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
package com.mfg.symbols.ui.chart.models;

import java.awt.geom.Point2D;

import com.mfg.chart.model.ITemporalPricesModel;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.common.QueueTick;

/**
 * @author arian
 * 
 */
public class TemporalPricesModel implements ITemporalPricesModel {
	protected QueueTick[] _lastFinalTick;
	protected QueueTick[] _tempTick;
	private Chart _chart;
	private int _token;

	/**
	 * 
	 */
	public TemporalPricesModel(int layerCount) {
		_lastFinalTick = new QueueTick[layerCount];
		_tempTick = new QueueTick[layerCount];
		_token = 0;
	}

	public Chart getChart() {
		return _chart;
	}

	public void setChart(Chart chart) {
		_chart = chart;
	}

	@Override
	public Point2D getTempTick(int dataLayer) {
		Point2D finalTick = getLastFinalTick(dataLayer);
		if (finalTick != null) {
			QueueTick tmpTick = _tempTick[dataLayer];
			if (tmpTick != null) {
				return new Point2D.Double(finalTick.getX() + 1,
						tmpTick.getPrice());
			}
		}
		return null;
	}

	@Override
	public Point2D getLastFinalTick(int dataLayer) {
		QueueTick tick = _lastFinalTick[dataLayer];
		if (tick != null) {
			return new Point2D.Double(tick.getFakeTime(), tick.getPrice());
		}
		return null;
	}

	public final void onNewTick(int layer, QueueTick qt) {
		_lastFinalTick[layer] = qt;
		_tempTick[layer] = null;
		_token++;
	}

	public final void onTemporaryTick(int layer, QueueTick qt) {
		_tempTick[layer] = qt;
		_token++;
	}

	@Override
	public int getModificationToken() {
		return _token;
	}
}
