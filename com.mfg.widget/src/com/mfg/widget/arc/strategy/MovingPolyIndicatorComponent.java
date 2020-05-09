package com.mfg.widget.arc.strategy;

import com.mfg.common.QueueTick;

/**
 * This is a moving channel based on a polyline of a certain degree.
 * 
 * <p>
 * The degree must be 2 or above. A degree one is simply the
 * {@link MovingLinearRegressionComponent}, which has been already made.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class MovingPolyIndicatorComponent extends MovingChannel {

	@Override
	public void hurryUp(int hurryUpQuota) {
		super.hurryUp(hurryUpQuota);
		_trend.hurryUp(hurryUpQuota);
	}

	@Override
	public void calmDown(int calmDownQuota) {
		super.calmDown(calmDownQuota);
		_trend.calmDown(calmDownQuota);
	}

	/**
	 * The common class which is used to help to compute the trend line. This
	 * object is shared by this class and the
	 * {@link FixedPolyIndicatorComponent}, because in reality the two
	 * computations are the same, as the trend line is not able to compute
	 * itself iteratively
	 */
	private final MdbPolyTrendHelper _trend;

	/**
	 * @param aDegree
	 *            the degree of the polynomial, it must be greater than one. If
	 *            you want a linear regression use the
	 *            {@linkplain MovingLinearRegressionComponent}
	 * @param useFilter
	 * @param maxPoints
	 */
	protected MovingPolyIndicatorComponent(BaseScaleIndicator aInd, int window,
			int aDegree) {
		super(aInd, window);
		_trend = new MdbPolyTrendHelper(aDegree, window, aInd);
		fCenterY1isComputed = true;
	}

	@Override
	protected int _getN() {
		return _trend.size();
	}

	@Override
	protected void _moving_channel_add_tick(QueueTick qtPar) {
		/*
		 * nothing... the data is always computed entirely.
		 */
	}

	@Override
	protected void _moving_channel_remove_tick(QueueTick qtPar) {

		if (_indicatorDisabled) {
			return;
		}
		/*
		 * When this method is called qtPar is the last not inclusive tick of
		 * the channel, fX1 and fX2 are good.
		 */

		/*
		 * I have to compute all ticks from x1 to x2
		 */

		if (fX1 < 0) {
			// nothing to do, the window is not ready.
			return;
		}

		/*
		 * If this fail you are not using a fixed window.
		 */
		assert (fX2 - fX1 + 1 == _trend.size()) : " fx2 " + fX2 + " fx1 " + fX1
				+ " length " + _trend.size();

		_trend.computeTrendLine(fX1, fX2);

	}

	@Override
	public double[] getChannelCoefficients() {
		return _trend.getCoeff();
	}

	@Override
	protected double getRealCenterY1() {
		double realCY1 = _trend.predict(fX1);
		return realCY1;
	}

	@Override
	protected double getRealCenterY2() {
		if (_indicatorDisabled || fX2 < fStorage.getWindow()) {
			return Double.NaN;
		}
		// return _trendLine.predict(fX2);
		return _trend.predict(fX2);
	}

	@Override
	public boolean supportsSlope() {
		return false;
	}

}
