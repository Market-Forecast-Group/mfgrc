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
package com.mfg.symbols.inputs.ui.views;

import java.util.LinkedList;

import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.ITimePriceCollection;
import com.mfg.chart.model.ITrendLinesModel;
import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.math.geom.Point;
import com.mfg.widget.arc.strategy.IIndicatorConsumer;
import com.mfg.widget.arc.strategy.IndicatorAdaptator;
import com.mfg.widget.arc.strategy.IndicatorConsumeArgs;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * @author arian
 * 
 */
public class TrendLinesModel implements ITrendLinesModel {
	final LinkedList<Pivot>[][] _dataUp;
	final LinkedList<Pivot>[][] _dataDown;
	final long[] _currentTime;
	protected final IChartModel _chartModel;
	private final LayeredIndicator _layeredIndicator;

	class DashedTrendCollection implements ITimePriceCollection {

		private final Pivot _p3;
		private final Point _lastPoint;
		private final int _dataLayer;

		public DashedTrendCollection(int dataLayer, Pivot p2, Pivot p3,
				Pivot p4, long currentTime) {
			_dataLayer = dataLayer;
			// m = (y2 - y1) / (x2 - x1);
			double p24_m = (p4.getPivotPrice() - p2.getPivotPrice())
					/ (double) (getDisplayTime(dataLayer, p4) - getDisplayTime(
							dataLayer, p2));

			// n = y - m * x;
			double n = p3.getPivotPrice() - p24_m
					* getDisplayTime(dataLayer, p3);
			long x = currentTime;
			double y = p24_m * x + n;

			_p3 = p3;
			_lastPoint = new Point(x, y);
		}

		@Override
		public double getPrice(int index) {
			return index == 0 ? _p3.getPivotPrice() : _lastPoint.getY();
		}

		@Override
		public long getTime(int index) {
			return (long) (index == 0 ? getDisplayTime(_dataLayer, _p3)
					: _lastPoint.getX());
		}

		@Override
		public int getSize() {
			return 2;
		}
	}

	class TrendCollection implements ITimePriceCollection {
		private final LinkedList<Pivot> _pivots;
		private final Point _lastPoint;
		private final int _dataLayer;

		public TrendCollection(int dataLayer, LinkedList<Pivot> pivots,
				long lastTime) {
			super();
			this._pivots = pivots;
			_lastPoint = getLastPoint(lastTime);
			_dataLayer = dataLayer;
		}

		private Point getLastPoint(long lastTime) {
			Pivot p1 = _pivots.get(0);
			Pivot p2 = _pivots.get(1);

			double y1 = p1.getPivotPrice();
			double y2 = p2.getPivotPrice();
			double x1 = getDisplayTime(_dataLayer, p1);
			double x2 = getDisplayTime(_dataLayer, p2);

			double m = (y2 - y1) / (x2 - x1);
			double n = y1 - m * x1;
			double y = m * lastTime + n;
			return new Point(lastTime, y);
		}

		@Override
		public double getPrice(int index) {
			return index < 2 ? _pivots.get(index).getPivotPrice() : _lastPoint
					.getPrice();
		}

		@Override
		public long getTime(int index) {
			return index < 2 ? getDisplayTime(_dataLayer, _pivots.get(index))
					: _lastPoint.getTime();
		}

		@Override
		public int getSize() {
			return 3;
		}

	}

	@SuppressWarnings("unchecked")
	public TrendLinesModel(final LayeredIndicator indicator,
			IChartModel chartModel) {
		this._chartModel = chartModel;
		this._layeredIndicator = indicator;

		int count = indicator.getLayers().size();

		_dataUp = new LinkedList[count][];
		_dataDown = new LinkedList[count][];
		_currentTime = new long[count];

		for (int i = 0; i < count; i++) {
			final int dataLayer = i;
			final MultiscaleIndicator layerInd = indicator.getLayers().get(
					dataLayer);

			int numScales = layerInd.getParamBean()
					.getIndicatorNumberOfScales();

			_dataUp[dataLayer] = new LinkedList[numScales + 1];
			_dataDown[dataLayer] = new LinkedList[numScales + 1];

			for (int level = 0; level <= numScales; level++) {
				_dataUp[dataLayer][level] = new LinkedList<>();
				_dataDown[dataLayer][level] = new LinkedList<>();
			}

			layerInd.addIndicatorListener(new IndicatorAdaptator() {
				@Override
				public void newPivot(Pivot pv) {
					LinkedList<Pivot>[][] data = pv.isStartingDownSwing() ? _dataUp
							: _dataDown;
					LinkedList<Pivot> list = data[dataLayer][pv.getLevel()];
					list.add(pv);
					if (list.size() > 2) {
						list.removeFirst();
					}
				}
			});
			layerInd.addIndicatorConsumer(new IIndicatorConsumer() {

				@Override
				public void stopped(IIndicator ind) {
					//
				}

				@Override
				public void consume(IndicatorConsumeArgs args) {
					QueueTick tick = args.getTick();
					_currentTime[dataLayer] = getDisplayTime(dataLayer, tick);
				}
			});
		}
	}

	/**
	 * @return the layeredIndicator
	 */
	public LayeredIndicator getLayeredIndicator() {
		return _layeredIndicator;
	}

	private ITimePriceCollection getLine(LinkedList<Pivot>[][] data,
			int dataLayer, int level) {
		LinkedList<Pivot> list = data[dataLayer][level];
		ITimePriceCollection col;
		if (list.size() > 1) {
			col = new TrendCollection(dataLayer, list, _currentTime[dataLayer]);
		} else {
			col = ITimePriceCollection.EMPTY;
		}
		return col;
	}

	@Override
	public ITimePriceCollection getDashedLine(int dataLayer, int level) {
		LinkedList<Pivot> lineUp = _dataUp[dataLayer][level];
		LinkedList<Pivot> lineDown = _dataDown[dataLayer][level];

		if (lineUp.size() < 2 || lineDown.size() < 2) {
			return ITimePriceCollection.EMPTY;
		}
		Pivot up = lineUp.getLast();
		Pivot down = lineDown.getLast();

		Pivot p2, p3, p4;

		if (up.getPivotTime() < down.getPivotTime()) {
			// up line start first
			p2 = lineDown.getFirst();
			p3 = lineUp.getLast();
			p4 = lineDown.getLast();
		} else {
			// down line start first
			p2 = lineUp.getFirst();
			p3 = lineDown.getLast();
			p4 = lineUp.getLast();
		}

		return new DashedTrendCollection(dataLayer, p2, p3, p4,
				_currentTime[dataLayer]);
	}

	@Override
	public ITimePriceCollection getUpLine(int dataLayer, int level) {
		return getLine(_dataUp, dataLayer, level);
	}

	@Override
	public ITimePriceCollection getDownLine(int dataLayer, int level) {
		return getLine(_dataDown, dataLayer, level);
	}

	/**
	 * 
	 * @param dataLayer
	 * @param tick
	 * @return
	 */
	@SuppressWarnings("static-method")
	// Used on inner classes.
	protected int getDisplayTime(int dataLayer, QueueTick tick) {
		return tick.getFakeTime();
	}

	/**
	 * 
	 * @param dataLayer
	 * @param pivot
	 * @return
	 */
	@SuppressWarnings("static-method")
	// Used on inner classes.
	protected long getDisplayTime(int dataLayer, Pivot pivot) {
		return pivot.getPivotTime();
	}

	/**
	 * 
	 * @param dataLayer
	 * @param pivot
	 * @return
	 */
	@SuppressWarnings("static-method")
	// Used on inner classes.
	protected long getDisplayTHTime(int dataLayer, Pivot pivot) {
		return pivot.getConfirmTime();
	}

}
