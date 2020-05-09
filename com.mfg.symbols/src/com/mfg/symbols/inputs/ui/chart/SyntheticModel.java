package com.mfg.symbols.inputs.ui.chart;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mfg.chart.model.ChartModelException;
import com.mfg.chart.model.ISyntheticModel;
import com.mfg.chart.model.ITimePriceCollection;
import com.mfg.chart.model.Model_MDB;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.utils.collections.TimeMap;
import com.mfg.widget.arc.strategy.LayeredIndicator;

public class SyntheticModel implements ISyntheticModel {

	private static final int START_SCALE = 1;
	private int _numberOfSwings;
	private int _higherScale;
	private IIndicator _indicator;
	private PriceMDBSession _priceSession;
	private LayeredIndicator _layerdInd;
	protected double _startDate;
	private boolean _matchPivots;

	public SyntheticModel(PriceMDBSession priceSession, IIndicator indicator) {
		_priceSession = priceSession;
		_indicator = indicator;
		_layerdInd = (LayeredIndicator) (indicator instanceof LayeredIndicator ? indicator
				: null);
		_higherScale = Math.min(4, _indicator.getChscalelevels());
		_numberOfSwings = 3;
		_matchPivots = true;
	}

	@Override
	public int getScaleCount() {
		return _indicator.getChscalelevels();
	}

	@Override
	public int getZZSwings() {
		return _numberOfSwings;
	}

	@Override
	public void setZZSwings(int numberOfSwings) {
		_numberOfSwings = numberOfSwings;
	}

	@Override
	public int getHigherZZScale() {
		return _higherScale;
	}

	@Override
	public void setHigherZZScale(int numberOfScales) {
		_higherScale = numberOfScales;
	}

	@Override
	public int getDataLayer(int synthScale) {
		return _layerdInd.getLayerForScale(synthScale);
	}

	@Override
	public int getIndicatorScale(int synthScale) {
		return _layerdInd.getIndicatorScaleFromSynthScale(synthScale);
	}

	@Override
	public ITimePriceCollection getSecondScalePrices() {
		if (_layerdInd != null) {
			int scale = 2;
			int dataLayer = _layerdInd.getLayerForScale(scale);
			try {
				PriceMDB mdb = _priceSession.connectTo_PriceMDB(dataLayer);
				Pivot p = _indicator.getLastPivot(0, scale);
				if (p != null) {
					final Record[] data = mdb.select_sparse(
							mdb.thread_randomCursor(), mdb.thread_cursor(),
							p.getPivotTime(), mdb.size(),
							Model_MDB.getMaxNumberOfPointsToShow());
					return new ITimePriceCollection() {

						@Override
						public long getTime(int index) {
							return (long) (data[index].physicalTime - _startDate);
						}

						@Override
						public int getSize() {
							return data.length;
						}

						@Override
						public double getPrice(int index) {
							return data[index].price;
						}
					};
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new ChartModelException(e);
			}
		}
		return ITimePriceCollection.EMPTY;
	}

	@SuppressWarnings("null")
	@Override
	public List<List<PivotPoint>> getZigZagDataset() {
		List<List<PivotPoint>> data = new ArrayList<>();
		List<PivotPoint> lastScale = null;
		long minTime = -1;
		for (int scale = _higherScale; scale >= START_SCALE; scale--) {
			List<PivotPoint> scalePoints = new ArrayList<>();
			if (scale == _higherScale) {
				// the exact number of swings
				for (int swing = 0; swing < _numberOfSwings; swing++) {
					PivotPoint p = addPivot(scale, scalePoints, swing);
					if (p != null) {
						minTime = (long) p.x;
					}
				}
			} else {
				int swing = 0;
				int targetIndex = 0;
				// compute target
				PivotPoint target;
				if (lastScale == null || lastScale.size() < 2) {
					target = null;
				} else {
					targetIndex = lastScale.size() - 2;
					target = lastScale.get(targetIndex);
				}

				// find number of swings to show
				boolean equalTime = false;
				for (;;) {
					Pivot p = _indicator.getLastPivot(-swing, scale);
					if (p == null) {
						break;
					}

					long time = ptime(scale, p.getPivotTime());

					if (target != null) {
						// if pivot is the same of the target
						if (swing < 2 && p.getPivotPrice() == target.y
								&& time == target.x) {
							// get new target
							targetIndex--;
							if (targetIndex < lastScale.size()
									&& targetIndex >= 0) {
								target = lastScale.get(targetIndex);
							} else {
								target = null;
							}
							continue;
						}

						if (swing < 3 && time < target.x) {
							if (lastScale.size() < 2) {
								break;
							}
							target = lastScale.size() < 3 ? null : lastScale
									.get(lastScale.size() - 3);
						}
						if (swing > 2) {
							if (time < target.x) {
								break;
							}
							if (time == target.x) {
								equalTime = true;
								break;
							}
						}
					}

					if (swing > 2 && time < minTime) {
						break;
					}
					swing++;
				}

				if (!_matchPivots) {
					if (!equalTime) {
						swing++;
					}
				}

				for (int i = 0; i <= swing; i++) {
					addPivot(scale, scalePoints, i);
				}

				if (_matchPivots) {
					if (target != null) {
						int start = 0;
						int last = scalePoints.size() - 1;
						for (PivotPoint p : scalePoints) {
							if (p.y == target.y) {
								break;
							}
							if (start == last) {
								break;
							}
							start++;
						}
						scalePoints = scalePoints.subList(start, last + 1);
						scalePoints.get(0).x = target.x;
					}
				}
			}

			if (!scalePoints.isEmpty()) {
				Point pivot = _indicator.getCurrentTentativePivot(scale);

				long time = ptime(-1, (int) pivot.getX());

				// out.println("Tentative pivot (" + scale + "): " +
				// pivot.getX()
				// + " " + new Date(time));

				double price = pivot.getY();

				PivotPoint point = new PivotPoint(time, price, null, -1, -1);

				scalePoints.add(point);
			}
			if (_matchPivots) {
				adjustThreholds(scalePoints);
			}

			data.add(scalePoints);
			lastScale = scalePoints;
		}

		// reduce data numbers
		double min = Double.MAX_VALUE;
		for (List<PivotPoint> scale : data) {
			if (!scale.isEmpty()) {
				PivotPoint p = scale.get(0);
				if (p.x < min) {
					min = p.x;
				}
			}
		}

		for (List<PivotPoint> scale : data) {
			for (PivotPoint p : scale) {
				p.x -= min;
				p.thX -= min;
			}
		}

		_startDate = min;

		Collections.reverse(data);

		return data;
	}

	private static void adjustThreholds(List<PivotPoint> points) {
		for (int i = 1; i < points.size(); i++) {
			PivotPoint p1 = points.get(i - 1);
			PivotPoint p2 = points.get(i);
			double dx = p2.x - p1.x;
			double dy = p2.y - p1.y;
			double m = dy / dx;
			double n = p1.y - m * p1.x;
			double x = (p1.thY - n) / m;
			p1.thX = x;
		}
	}

	private PivotPoint addPivot(int scale, List<PivotPoint> scalePoints,
			int swing) {
		Pivot pivot = _indicator.getLastPivot(-swing, scale);
		if (pivot != null) {
			int pivotTime = pivot.getPivotTime();
			int pivotPrice = pivot.getPivotPrice();
			Boolean downSwing = Boolean.valueOf(pivot.isStartingDownSwing());
			long confirmTime = pivot.getConfirmTime();
			double confirmPrice = pivot.getConfirmPrice();

			long pPivotTime = ptime(scale, pivotTime);
			long pConfirmTime = ptime(scale, (int) confirmTime);

			PivotPoint point = new PivotPoint(pPivotTime, pivotPrice,
					downSwing, pConfirmTime, confirmPrice);
			scalePoints.add(0, point);
			return point;
		}
		return null;
	}

	@Override
	public long getRealDate(double displayTime) {
		return (long) (_startDate + displayTime);
	}

	private long ptime(int scale, int time) {
		int layer = scale == -1 ? 0 : (_layerdInd == null ? 0 : _layerdInd
				.getLayerForScale(scale));
		TimeMap map = _priceSession.getTimeMap(layer);
		return map.get(time);
	}

	@Override
	public void setMatchPivots(boolean matchPivots) {
		_matchPivots = matchPivots;
	}

	@Override
	public boolean isMatchPivots() {
		return _matchPivots;
	}
}
