package com.mfg.widget.arc.strategy;

import java.io.Serializable;

import com.mfg.widget.arc.gui.IndicatorParamBean;

/**
 * A class which is used to make the crossing of indicator lines as smooth as
 * possible.
 * 
 * @author Sergio
 * 
 */
class IndicatorCrosser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8214817298546467588L;

	private enum ECrossState {
		/**
		 * In this state I have no memory of the past.
		 */
		VOID_STATE,
		/**
		 * This is the state of the crosser at the start of the channel. The
		 * smoothed version is stuck to the old channel (which was the new one)
		 */
		STARTING_NEW_CHANNEL,
		/**
		 * Here the crosser will do an average
		 */
		AVERAGING,
		/**
		 * I have crossed, so I can return the new indicator's value.
		 */
		ARRIVED_AT_NEW_CHANNEL
	}

	private ECrossState _state = ECrossState.VOID_STATE;

	public IndicatorCrosser(BaseScaleIndicator aInd) {

		IndicatorParamBean bean = aInd._compositeIndicator.bean;

		double testBoost = 1 + bean.getSmoothing_convergeBoost();
		if (testBoost < 1 || testBoost > 7) {
			throw new IllegalArgumentException("test boost is " + testBoost);
		}
		fBoostIndicator = testBoost;
	}

	final double fBoostIndicator;

	private double _prevResult;

	private double _prevCurInd;

	/**
	 * 
	 * 
	 */
	public double getCurIndicator(final double curInd) {

		double result;

		switch (_state) {
		case AVERAGING:

			if (Math.abs(_prevResult - curInd) < 1e-3) {
				_state = ECrossState.ARRIVED_AT_NEW_CHANNEL;
				result = curInd;
			} else {
				double prevResDistance = _prevCurInd - _prevResult;
				double curIndDistance = curInd - _prevResult;
				double curIndMovement = curInd - _prevCurInd;

				if (Math.signum(prevResDistance * curIndDistance) < 0) {
					/*
					 * The two indicators have crossed
					 */
					_state = ECrossState.ARRIVED_AT_NEW_CHANNEL;
					result = curInd;
				} else {
					/*
					 * the normal case: the indicators are not crossed and I am
					 * averaging. So, let's see if the indicator is moving
					 * towards us.
					 */

					if (Math.abs(curIndDistance) <= Math.abs(prevResDistance)) {
						/*
						 * stay here, the indicator is going toward us
						 */
						result = _prevResult;
					} else {
						/*
						 * the indicator is moving away, let's go towards it
						 */
						curIndMovement *= fBoostIndicator;
						curIndMovement = Math.min(Math.abs(curIndMovement),
								Math.abs(curIndDistance))
								* Math.signum(curIndMovement);
						result = _prevResult + curIndMovement;
					}

				}
			}

			if (Math.abs(result - curInd) < 1e-3) {
				result = curInd;
				_state = ECrossState.ARRIVED_AT_NEW_CHANNEL;
			}

			break;
		case STARTING_NEW_CHANNEL:
			result = _prevResult;
			_state = ECrossState.AVERAGING;
			break;
		case ARRIVED_AT_NEW_CHANNEL:
			result = curInd;
			break;
		case VOID_STATE:
			result = curInd;
			if (curInd != 0.0 && !Double.isNaN(curInd)) {
				_state = ECrossState.AVERAGING;
			}
			break;
		default:
			throw new IllegalStateException();
		}

		_prevCurInd = curInd;
		_prevResult = result;

		return result;
	}

	/**
	 * sets the creation of a new channel and this will allow the smoothing
	 * indicator to restart again.
	 */
	public void newChan() {
		if (_state == ECrossState.VOID_STATE) {
			return;
		}
		_state = ECrossState.STARTING_NEW_CHANNEL;
	}

}

public class NumericThIndicator {
	// unused.
}
