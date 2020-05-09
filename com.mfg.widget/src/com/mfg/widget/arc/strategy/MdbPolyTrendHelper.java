package com.mfg.widget.arc.strategy;

import java.io.IOException;
import java.util.Arrays;

import com.mfg.common.QueueTick;
import com.mfg.inputdb.prices.mdb.PriceMDB.RandomCursor;
import com.mfg.utils.U;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.math.geom.JamaPolyTrendLine;
import com.mfg.widget.arc.math.geom.PolyEvaluator;

/**
 * A trend line computer which uses MDB as the back end for the prices.
 * 
 * <p>
 * The interface is similar to {@link TrendLine}
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class MdbPolyTrendHelper {

	private static final int INITIAL_WINDOW = 500;

	/**
	 * maximum number of points to be used to compute the trendline
	 */
	private final int _maxPointsUsed;

	private final boolean _filterPoly;

	/**
	 * This is the indicator which is watched. When there is a new pivot in the
	 * indicator watched than the computation of the poly line is done.
	 */
	private final BaseScaleIndicator _indicatorToWatch;

	/**
	 * The composite indicator used to get the price list in mdb format
	 */
	private final ChannelIndicator _compositeIndicator;

	/**
	 * A generic object which is used to compute actually the trend line.
	 */
	private final JamaPolyTrendLine _trendLine;

	/**
	 * The x model is simply the list of the fake times which constitute this
	 * channel.
	 */
	private double _xModel[];

	/**
	 * The y model is simply the list of the prices corresponding to the fake
	 * times in the channel
	 */
	private double _yModel[];

	/**
	 * The last position in the
	 */
	private int _lastUsedSize;

	/**
	 * The old polynomial which is simply used to make a smoothed version of the
	 * indicator in the same channel during warm up.
	 */
	private double[] _oldPoly;

	private double _percentageOfNew;

	/**
	 * The idea for this field is to count how many prices the trend line is
	 * skipping and in this way hte smoothing is adaptative, based on how many
	 * points i have skipped in the past channel.
	 */
	private int _lastSkippedPoints;

	/**
	 * The value with which I increment the percentage to smooth the new
	 * polyline with the old.
	 */
	private double _percentageIncrementor;

	/**
	 * A flag that says that the indicator must unconditionally hurry up,
	 * probably because there is a fast market condition. This will usually
	 * never be set in warm up (for now I can enforce this, but later this
	 * condition may be relaxed).
	 */
	private boolean _unconditionallyFilterPoly;

	private RandomCursor _cursor;

	/**
	 * This is a testing flag to reproduce the testing, because it waits for the
	 * lower scale indicator to compute itself.
	 * 
	 * <p>
	 * It should be left as false in normal runs.
	 */
	public static boolean TESTING_SO_SERIALIZE;

	/**
	 * Builds a trend helper without the filter and a fixed window size.
	 * 
	 * <p>
	 * The fixed window size is not enforced, for now, in the case of a variable
	 * window simply pass -1 to the constructor, and it will adapt its window to
	 * the channel length
	 * 
	 * @param aDegree
	 *            the degree of the polyline. It must be 2 or greater.
	 * 
	 * @param fixWindow
	 *            the fix window size. If this has not a fixed window than give
	 *            to it -1.
	 * @param baseIndicator
	 *            the base indicator is used to access the parameters and the
	 *            lower scales.
	 */
	@SuppressWarnings("boxing")
	public MdbPolyTrendHelper(int aDegree, int fixWindow,
			BaseScaleIndicator baseIndicator) {

		_compositeIndicator = baseIndicator._compositeIndicator;

		boolean useFilter = false;
		int maxPointsUsed = -1;
		if (useFilter) {
			if (maxPointsUsed < aDegree + 1) {
				throw new IllegalArgumentException();
			}
			_maxPointsUsed = maxPointsUsed;
		} else {
			_maxPointsUsed = Integer.MAX_VALUE;
		}

		_trendLine = new JamaPolyTrendLine(aDegree);

		int window = fixWindow;
		window = Math.min(window, _maxPointsUsed);

		if (fixWindow < 0) {
			window = Math.min(INITIAL_WINDOW, _maxPointsUsed);
			/*
			 * _lastUsedSize is zero by default, and this is fine
			 */
		} else {
			/*
			 * The last size used is always the window, as this is a fixed
			 * window computation.
			 */
			_lastUsedSize = window;
		}

		_xModel = new double[window];
		_yModel = new double[window];

		IndicatorParamBean bean = baseIndicator._compositeIndicator.bean;

		_filterPoly = bean.isMaxPricesForPolylinesEnabled();

		BaseScaleIndicator prevIndicator = null;

		// if (_filterPoly) {

		int scaleToSee = bean.getMaxPricesForPolylines();
		U.debug_var(819331, "The scale to see is ", scaleToSee);

		if (scaleToSee < 1) {
			throw new IllegalArgumentException("Cannot see pivots of scale "
					+ scaleToSee + " LS");
		}

		prevIndicator = baseIndicator;
		for (int i = 1; i <= scaleToSee; ++i) {
			/*
			 * This is a hard limiter for the scale to see because it means that
			 * lower indicators at most compute the pivots at scale zero, if
			 * there are no lower scales available.
			 */
			if (prevIndicator.prevInd == null) {
				break;
			}
			prevIndicator = prevIndicator.prevInd;
		}
		// }

		_indicatorToWatch = prevIndicator;

		try {
			_cursor = baseIndicator._compositeIndicator.getMdbDatabase()
					.randomCursor();
			_cursor.getMDB().getSession().defer(_cursor);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param calmDownQuota
	 */
	public void calmDown(int calmDownQuota) {
		_unconditionallyFilterPoly = false;
	}

	public void computeTrendLine(int fX1, int fX2) {

		this.computeTrendLine(fX1, fX2, _compositeIndicator.lastTick);
	}

	/**
	 * actually computes the trend line.
	 * 
	 * <p>
	 * This is used by the
	 * 
	 * 
	 * @param fX1
	 * @param fX2
	 * @param lastTick
	 */
	private void computeTrendLine(int fX1, int fX2, QueueTick lastTick) {

		int curWindow = fX2 - fX1 + 1;
		_lastUsedSize = curWindow;

		if (curWindow <= _trendLine.getDegree()) {
			/*
			 * not enough points to compute the trend line.
			 */
			return;
		}

		if (_unconditionallyFilterPoly
				|| (_filterPoly && _compositeIndicator.isInWarmUp())) {

			/*
			 * Here I watch for the lowest indicator, but I have to wait for it
			 * if the flag is true.
			 * 
			 * This flag is really useful only in debug, because in this way we
			 * have predictable behavior, but in normal runs this is not really
			 * important, we may skip some updates, but we are in warm up or in
			 * a fast market so it is important to be as quick as possible.
			 * 
			 * Maybe I could put a hard limit on the number of points skipped,
			 * in the remote situation where the multithread is so "strange"
			 * that the lower scale indicator has not any chance to compute the
			 * pivot...
			 */
			if (TESTING_SO_SERIALIZE)
				while (true) {
					long val = _compositeIndicator._scaleCounter.get();
					if ((val & _indicatorToWatch.MY_BIT) != 0) {
						break;
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			// ////////////////////////////////////////////////////

			/*
			 * This in multithread will have unpredictable behavior, because the
			 * indicator to watch may have finished to compute the pivots before
			 * or after this call.
			 * 
			 * Maybe it is necessary to put a hard limit on the number of
			 * skipped points, just to be sure that the indicator is not
			 * extrapolating too much
			 */
			if (!_indicatorToWatch.isThereANewPivot()) {
				_lastSkippedPoints++;
				return;
			}
			/*
			 * I need to compute the polyline, but I save the old coefficients
			 */
			double[] oldCoeff = getCoeff();
			if (oldCoeff != null) {
				_oldPoly = Arrays.copyOf(oldCoeff, oldCoeff.length);
			}
			_percentageOfNew = 0.0;
			_percentageIncrementor = 1.0 / _lastSkippedPoints;
			_lastSkippedPoints = 1;
		}

		/*
		 * I could compute the trend line, now, but is the trend line too long?
		 * and if yes I should compute the step, because not all the points will
		 * be considered.
		 * 
		 * This is an integer division, so the step at least is one
		 */
		int step = Math.max(curWindow / _maxPointsUsed, 1);
		curWindow = Math.min(curWindow, _maxPointsUsed);

		if (curWindow > _xModel.length) {
			int newLength = (int) (curWindow * 1.5);
			newLength = Math.min(_maxPointsUsed, newLength);
			_xModel = new double[newLength];
			_yModel = new double[newLength];
		}

		int ind = 0;
		int i;
		for (i = fX1; i < lastTick.getFakeTime(); i += step) {
			// com.mfg.inputdb.prices.mdb.PriceMDB.Record rec =
			// ranMDBList.get(i);
			try {
				_cursor.seek(i);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			_xModel[ind] = i;
			_yModel[ind] = _cursor.price;
			ind++;
			if (ind == _xModel.length - 1) {
				// System.out.println(")))");
				break;
			}
		}

		/*
		 * if this fail you are messing with the indexes.
		 */
		if (ind == curWindow - 1) {
			_xModel[ind] = lastTick.getFakeTime();
			_yModel[ind] = lastTick.getPrice();
		}

		/*
		 * Ok I have the model, I feed it to the interpolator
		 */
		_trendLine.setValues(_yModel, _xModel, curWindow);

	}

	public double[] getCoeff() {
		return _trendLine.getCoeff();
	}

	/**
	 * @param hurryUpQuota
	 */
	public void hurryUp(int hurryUpQuota) {
		_unconditionallyFilterPoly = true;
	}

	public double predict(int fX2) {
		double newVal = _trendLine.predict(fX2);
		if (_oldPoly != null && _percentageOfNew < 1.0) {
			double oldValue = PolyEvaluator.evaluate(_oldPoly, fX2);
			_percentageOfNew += _percentageIncrementor;
			return (oldValue * (1 - _percentageOfNew) + newVal
					* _percentageOfNew);
		}
		return newVal;

	}

	public int size() {
		return _lastUsedSize;
	}
}
