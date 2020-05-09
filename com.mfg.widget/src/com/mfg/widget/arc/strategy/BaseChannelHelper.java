package com.mfg.widget.arc.strategy;

import com.mfg.common.QueueTick;
import com.mfg.interfaces.symbols.AbstractIndicatorParamBean.TopBottomMaxDist;
import com.mfg.utils.MathUtils;
import com.mfg.widget.arc.gui.IndicatorParamBean;

/**
 * A basic class which is used to create the top and bottom indicators using the
 * center line value.
 * 
 * <p>
 * The architecture here is: this object receives from the outside a center
 * value and it computes the top and bottom distances.
 * 
 * <p>
 * Usually these top and bottom distances are independent, sometimes they are
 * fixed, because they have to follow a certain logic (maybe their sum must be
 * equal).
 * 
 * <p>
 * There are some variations of the max distance generator which are a mix
 * version of the fixed tick and the variable one, so we have to create the
 * distance together (this is the reason of the word "coupled").
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class BaseChannelHelper implements IIndicatorBlindParticipant {

	/**
	 * Top and bottom generator are only a suggestion. The <b>real</b> position
	 * of the channel is decided coupling the information between up and down.
	 */
	protected IMaxDistanceGenerator _topGenerator;
	protected IMaxDistanceGenerator _bottomGenerator;

	/**
	 * Usually a channel is free to move around, for example in the convex hull.
	 * There is however some other types of indicators which are not free, and
	 * in this case we have a <b>total</b> max distance.
	 */
	private double _totalMaximumWidth = Double.POSITIVE_INFINITY;

	/**
	 * Top and bottom distances are handled by this channel helper because they
	 * could be distribuited differently.
	 */
	private double _topDistance;

	private double _bottomDistance;

	public BaseChannelHelper(BaseScaleIndicator aInd) {
		IndicatorParamBean bean = aInd._compositeIndicator.bean;
		TopBottomMaxDist dist = bean.getIndicator_TopBottomMaxDist();

		switch (dist) {
		case BRUTE_FORCE:
		case CONVEX_HULL:
		case CONVEX_HULL_FIXED_TICK:
			boolean isThThresholdEnabled = bean
					.isThPercentForTopBottomMinDistanceEnabled();
			double thPercentage = -1;
			if (isThThresholdEnabled) {
				thPercentage = bean.getThPercentForTopBottomMinDistance();
			}

			boolean isBruteForceForced = dist == TopBottomMaxDist.BRUTE_FORCE;

			boolean isMaxScaleTBEnabled = bean.isNoTBMinFromScaleUpEnabled();

			int maxScaleEnabled = bean.getNoTBMinFromScaleUp();

			isThThresholdEnabled = isThThresholdEnabled
					&& (!isMaxScaleTBEnabled || (aInd.level < maxScaleEnabled));

			_topGenerator = new ConvexHullGenerator(aInd, true,
					isThThresholdEnabled, thPercentage, isBruteForceForced);
			_bottomGenerator = new ConvexHullGenerator(aInd, false,
					isThThresholdEnabled, thPercentage, isBruteForceForced);

			if (dist == TopBottomMaxDist.CONVEX_HULL_FIXED_TICK) {
				_totalMaximumWidth = FixedTickMaxDistanceGenerator
						.getMaxDistanceInTicksFromBeanInTicks(aInd);
			}

			break;
		case FIXED_TICK:
			_topGenerator = new FixedTickMaxDistanceGenerator(aInd);
			_bottomGenerator = new FixedTickMaxDistanceGenerator(aInd);
			break;
		case PERCENTAGE:
			_topGenerator = new PercentageMaxDistanceGenerator(aInd);
			_bottomGenerator = new PercentageMaxDistanceGenerator(aInd);
			break;
		default:
		case HALF_CONVEX_HULL:
			throw new UnsupportedOperationException();

		}
	}

	@Override
	public void begin(int tick) {
		_topGenerator.begin(tick);
		_bottomGenerator.begin(tick);

		/*
		 * If it was infinite it will remain infinite.
		 */
		_totalMaximumWidth *= tick;
	}

	@Override
	public void newTick(QueueTick qt) {
		_topGenerator.newTick(qt);
		_bottomGenerator.newTick(qt);
	}

	public void moveTo(int newSp) {
		_topGenerator.moveTo(newSp);
		_bottomGenerator.moveTo(newSp);
	}

	public double __tempGetRawMaxTop() {
		return _topDistance;
	}

	public double __tempGetRawMaxBottom() {
		return _bottomDistance;
	}

	/**
	 * This is a temporary method which is used by some kinds of channels to
	 * distribute the maximum distance between top and bottom, to make it
	 * coherent to the overall algorithm of the channel. For example if we have
	 * a convex/hull + fixed ticks than if the convex hull is larger than the
	 * fixed ticks the priority to the distance should be given to the part
	 * which touches the hull.
	 */
	public void updateTopBottomDistances() {
		double curDelta = _topGenerator.getMaxDistance()
				+ _bottomGenerator.getMaxDistance();

		_topDistance = _topGenerator.getMaxDistance();
		_bottomDistance = _bottomGenerator.getMaxDistance();
		if (curDelta > _totalMaximumWidth) {

			/*
			 * This is the difference which has to be taken from the current
			 * maximum distance.
			 */
			double diffDelta = curDelta - _totalMaximumWidth;

			/*
			 * Let's distribuite it between top and bottom distance, using the
			 * ratio between top and bottom as a reference
			 */
			if (_topDistance == 0) {
				_bottomDistance -= diffDelta;
			} else if (_bottomDistance == 0) {
				_topDistance -= diffDelta;
			} else {

				double topBottomRatio = _topDistance / _bottomDistance;

				/*
				 * I need a function which tends to 1 at x to infinity, and this
				 * is simply x + K / x, where K is any constant.
				 * 
				 * This function should return 0 for x = 0, 0.5 for x = 1 and 1
				 * for x = infinity.
				 * 
				 * I choose x / (x+1)
				 */

				double topRatio = topBottomRatio / (topBottomRatio + 1);
				assert (topRatio > 0);
				assert (topRatio < 1);

				double diffTop = topRatio * diffDelta;
				double diffBottom = diffDelta - diffTop;

				_topDistance -= diffTop;
				_bottomDistance -= diffBottom;

				assert (MathUtils.almost_equal(_topDistance + _bottomDistance,
						_totalMaximumWidth));

			}

		}

	}

	@Override
	public void hurryUp(int hurryUpQuota) {
		_topGenerator.hurryUp(hurryUpQuota);
		_bottomGenerator.calmDown(hurryUpQuota);
	}

	@Override
	public void calmDown(int calmDownQuota) {
		_topGenerator.hurryUp(calmDownQuota);
		_bottomGenerator.calmDown(calmDownQuota);
	}

}
