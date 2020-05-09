package com.mfg.widget.arc.strategy;

import com.mfg.common.QueueTick;

/**
 * Base class for the indicator component.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class FixedPolyIndicatorComponent extends FixedStartPointChannel {

	@Override
	public void hurryUp(int hurryUpQuota) {
		super.hurryUp(hurryUpQuota);
		_trendLine.hurryUp(hurryUpQuota);
	}

	@Override
	public void calmDown(int calmDownQuota) {
		super.calmDown(calmDownQuota);
		_trendLine.calmDown(calmDownQuota);
	}

	@Override
	public boolean supportsSlope() {
		return false;
	}

	private final MdbPolyTrendHelper _trendLine;

	@Override
	public double[] getChannelCoefficients() {
		return _trendLine.getCoeff();
	}

	/**
	 * override the default implementation because the moving is not done step
	 * by step but all at once.
	 * 
	 * <p>
	 * The computation of the new channel is done once.
	 */
	@Override
	protected void _moveToFixedSpChan_base(int newSp) {
		if (_indicatorDisabled) {
			return;
		}
		/*
		 * newSp is the new fX1, fX2 is already updated
		 */
		_trendLine.computeTrendLine(newSp, fX2);
	}

	protected FixedPolyIndicatorComponent(BaseScaleIndicator aInd,
			int minimumWindow, int aDegree) {
		super(aInd, minimumWindow);
		_trendLine = new MdbPolyTrendHelper(aDegree, -1, aInd);
		fCenterY1isComputed = true;
	}

	@Override
	public double getSlope() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void _clearAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void _remove_one_tick(QueueTick queueTick) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int _getN() {
		if (_indicatorDisabled) {
			/*
			 * pre computed size
			 */
			return fX2 - fX1 + 1;
		}
		return _trendLine.size();
	}

	@Override
	protected double getRealCenterY2() {
		if (_indicatorDisabled || fX2 < fStorage.getWindow()) {
			return Double.NaN;
		}
		return _trendLine.predict(fX2);
	}

	@Override
	protected void indicator_new_tick(QueueTick qt) {
		if (_indicatorDisabled) {
			return;
		}
		/*
		 * Each time I recompute all the indicator
		 */
		_trendLine.computeTrendLine(fX1, fX2);
	}

	@Override
	protected double getRealCenterY1() {
		double realCY1 = _trendLine.predict(fX1);
		return realCY1;
	}

}
