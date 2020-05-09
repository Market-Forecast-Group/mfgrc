package com.mfg.widget.arc.strategy;

import com.mfg.common.QueueTick;
import com.mfg.utils.U;
import com.mfg.widget.arc.strategy.BaseScaleIndicator.EState;

/**
 * This models a channel which is a mix of a polyline and a linear regression.
 * 
 * <p>
 * The switch between the two is done by a check on the crossing of the linear
 * indicator with respect to the poly indicator.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class MixedPolyLinearFixedChannel extends FixedStartPointChannel {

	@Override
	public void hurryUp(int hurryUpQuota) {
		_polyChan.hurryUp(hurryUpQuota);
	}

	@Override
	public void calmDown(int calmDownQuota) {
		_polyChan.calmDown(calmDownQuota);
	}

	private final LinearRegressionComponent _linearChan;
	private final FixedPolyIndicatorComponent _polyChan;
	private BaseChannel _activeChan;

	/**
	 * The mixed state has two states for every swing. The before crossing state
	 * in which the real indicator is the linear indicator and then the crossing
	 * state when the real indicator is the poly
	 * 
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private enum MixedEState {
		BEFORE_CROSSING, AFTER_CROSSING
	}

	private MixedEState _state = MixedEState.BEFORE_CROSSING;
	private double[] _bufferCoeff;

	protected MixedPolyLinearFixedChannel(IndicatorSingle aInd,
			int aMinimumWindow, int aDegree) {
		super(aInd, aMinimumWindow);

		_linearChan = new LinearRegressionComponent(aInd, aMinimumWindow);
		_polyChan = new FixedPolyIndicatorComponent(aInd, aMinimumWindow,
				aDegree);

		/*
		 * At first the active chan is simply the linear channel.
		 */
		_activeChan = _linearChan;
		fCenterY1isComputed = true;

		_bufferCoeff = new double[3];

	}

	@Override
	protected void _moveToFixedSpChan_base(int newSp) {
		_linearChan.moveTo(newSp);
		_polyChan.moveTo(newSp);
	}

	@Override
	public double getSlope() {
		return _activeChan.getSlope();
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
		return _activeChan._getN();
	}

	@Override
	protected double getRealCenterY2() {
		return _activeChan.getRealCenterY2();
	}

	@SuppressWarnings("boxing")
	@Override
	protected void indicator_new_tick(QueueTick qt) {
		_linearChan.newTick(qt);
		_polyChan.newTick(qt);

		switch (_state) {
		case AFTER_CROSSING:
			/*
			 * I am after crossing, so I have to check if the state has changed.
			 * 
			 * I cannot use the isThereANewPivot method, because the qt is
			 * already changed, this is rather weird, but the indicator is fed
			 * before the pivot generator, so the last pivot has a confirm time
			 * which is minus one the time (fake) of the new tick.
			 */
			if (fInd.fPivotsGenerator.fPivots.size() != 0
					&& (fInd.fPivotsGenerator.getPastPivot(0).fConfirmTime == qt
							.getFakeTime() - 1)) {
				_state = MixedEState.BEFORE_CROSSING;
				_activeChan = _linearChan;

				if (fInd.level == 4) {
					U.debug_var(283438, "returning to before crossing @ ",
							qt.getFakeTime());
				}
			}

			break;
		case BEFORE_CROSSING:

			if (fInd.fPivotsGenerator.fState == EState.ZIG_STATE) {
				/*
				 * I am in an up swing, the crossing is when the parabola goes
				 * down
				 */
				if (_polyChan.getRealCenterY2() < _linearChan.getRealCenterY2()) {
					_activeChan = _polyChan;
					_state = MixedEState.AFTER_CROSSING;

					if (fInd.level == 4) {
						U.debug_var(283438, "ZIG goto aftercrossing @ ",
								qt.getFakeTime(), " poly ",
								_polyChan.getRealCenterY2(), " lin ",
								_linearChan.getRealCenterY2());
					}
				}
			} else {
				/*
				 * Down swing the crossing is when the parabola goes up
				 */
				if (_polyChan.getRealCenterY2() > _linearChan.getRealCenterY2()) {
					_activeChan = _polyChan;
					_state = MixedEState.AFTER_CROSSING;

					if (fInd.level == 4) {
						U.debug_var(283438, "ZAG goto aftercrossing @ ",
								qt.getFakeTime(), " poly ",
								_polyChan.getRealCenterY2(), " lin ",
								_linearChan.getRealCenterY2());
					}
				}
			}
			break;
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public boolean supportsSlope() {
		return _activeChan.supportsSlope();
	}

	@Override
	public double[] getChannelCoefficients() {
		if (_activeChan instanceof FixedPolyIndicatorComponent) {
			return _activeChan.getChannelCoefficients();
		}
		System.arraycopy(_activeChan.getChannelCoefficients(), 0, _bufferCoeff,
				0, 2);
		return _bufferCoeff;
	}

	@Override
	protected double getRealCenterY1() {
		return _activeChan.getRealCenterY1();
	}

}
