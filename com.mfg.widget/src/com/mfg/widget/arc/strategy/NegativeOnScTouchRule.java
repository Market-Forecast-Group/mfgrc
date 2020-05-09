package com.mfg.widget.arc.strategy;

import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.strategy.BaseScaleIndicator.EState;

/**
 * The negative on sc touch rule will create a negative channel if the indicator
 * has touched the SC line. The sc line is the top line if the swing is zag and
 * the bottom line if the swing is zig.
 * 
 * <p>
 * The touch will also have other constraints which are the price ratio and the
 * time ratio.
 * 
 * <p>
 * For now the two constraints are in AND but they could also be in OR (in later
 * versions).
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class NegativeOnScTouchRule extends PivotGeneratorRule {

	/**
	 * The current scale indicator
	 */
	private final IChannelHelper _indicator;
	private final BasePivotsGenerator _superScaleGen;
	private final double PRICE_S0_S0PRIME_RATIO;
	private final double TIME_S0_S0PRIME_RATIO;
	private final boolean PRICE_RATIO_ENABLED;
	private final boolean TIME_RATIO_ENABLED;
	private final boolean SELF_PIVOT_BREAKOUT;

	/**
	 * This flag signals that the rule must check the self pivot breakout
	 * because in this way it will revert the swing that has been created with
	 * the rule itself.
	 * 
	 * <p>
	 * The possibile states are check_up_break and check_down_break
	 */
	private boolean _selfCheckPivot;
	private int _lastSelfPivotFiredTime;

	/**
	 * This flag tells us to activate the rule only if the new pivot will start
	 * a swing with at least a certain threshold.
	 */
	private final boolean THRESHOLD_THRESHOLD_ENABLED;
	private double NEGATIVE_ON_SC_TOUCH_THRESHOLD_PERC;

	NegativeOnScTouchRule(PivotsGenerator aGen, BasePivotsGenerator superGen,
			IChannelHelper aIndicator) {
		super(aGen);
		_indicator = aIndicator;
		_superScaleGen = superGen;

		IndicatorParamBean bean = fGenerator.fInd._compositeIndicator.bean;

		PRICE_S0_S0PRIME_RATIO = bean.getNegativeOnSCTouch_S0Ratio() / 100.0;
		TIME_S0_S0PRIME_RATIO = bean.getNegativeOnSCTouch_S0TimeRatio() / 100.0;

		PRICE_RATIO_ENABLED = bean.isNegativeOnSCTouch_S0RatioEnabled();
		TIME_RATIO_ENABLED = bean.isNegativeOnSCTouch_S0TimeRatioEnabled();

		SELF_PIVOT_BREAKOUT = bean.isNegativeOnSCTouch_selfPivotBrakout();

		if (!PRICE_RATIO_ENABLED && !TIME_RATIO_ENABLED) {
			throw new IllegalArgumentException();
		}

		THRESHOLD_THRESHOLD_ENABLED = bean
				.isNegativeOnSCTouch_thPercentEnabled();

		NEGATIVE_ON_SC_TOUCH_THRESHOLD_PERC = bean
				.getNegativeOnSCTouch_thPercent();
	}

	@Override
	public void begin(int tick) {
		// nothing to do here.
	}

	@Override
	protected boolean tryToFire(QueueTick qt) {

		if (_selfCheckPivot) {
			return _selfCheckPivot(qt);
		}

		/*
		 * The precondition to fire is an sc touch
		 */
		if (!_indicator.isThereANewSc()) {
			return false;
		}
		// U.debug_var(283859, "lev ", fGenerator.fInd.level + 1,
		// " new sc touch @ ", qt);

		/*
		 * I am still not decided.
		 */
		if (fGenerator.fState != EState.ZAG_STATE
				&& fGenerator.fState != EState.ZIG_STATE) {
			return false;
		}

		/*
		 * The second condition is the reversal of the lower scale
		 */
		if (_superScaleGen.fState == fGenerator.fState) {
			return false;
		}
		// U.debug_var(283859, "lev ", fGenerator.fInd.level + 1,
		// " different state ", _superScaleGen.fState, " != ",
		// fGenerator.fState);

		if (_superScaleGen.fLastPivot.fConfirmTime == qt.getFakeTime()) {
			return false;
		}
		// U.debug_var(918385, "the super pivot was confirmed at ",
		// _superScaleGen.fLastPivot.fConfirmTime);

		boolean priceRatioOk;

		if (!PRICE_RATIO_ENABLED) {
			priceRatioOk = true;
		} else {
			/*
			 * Then we have the price condition, the ratio between S0 and S0'
			 */
			long S0Prime = Math.abs(fGenerator.fLastPivot.fConfirmPrice
					- fGenerator.fLastPivot.fPivotPrice);

			long S0 = Math.abs(fGenerator.fTentativePivot.getPrice()
					- fGenerator.fLastPivot.fPivotPrice);

			double ratio = S0 / (double) S0Prime;

			// U.debug_var(928953, "s0 price ratio ", ratio, " required ",
			// PRICE_S0_S0PRIME_RATIO);

			priceRatioOk = ratio >= PRICE_S0_S0PRIME_RATIO;
		}

		boolean timeRatioOk;

		if (!TIME_RATIO_ENABLED) {
			timeRatioOk = true;
		} else {
			/*
			 * now the time ratio *
			 */
			int SOPrimeTimeDelta = Math.abs(fGenerator.fLastPivot.fConfirmTime
					- fGenerator.fLastPivot.fPivotTime);

			int SOTimeDelta = Math.abs(qt.getFakeTime()
					- fGenerator.fLastPivot.fPivotTime);

			double timeRatio = (double) SOTimeDelta / (double) SOPrimeTimeDelta;

			// U.debug_var(128349, "time ratio ", timeRatio, " required ",
			// TIME_S0_S0PRIME_RATIO);

			timeRatioOk = timeRatio >= TIME_S0_S0PRIME_RATIO;
		}

		if (!(timeRatioOk || priceRatioOk)) {
			return false;
		}

		/*
		 * Last check, let's see if the new pivot would create a long enough
		 * swing.
		 */
		if (THRESHOLD_THRESHOLD_ENABLED) {
			double newSwing = Math.abs(qt.getPrice()
					- this.fGenerator.fTentativePivot.getPrice());
			double currentSwing = Math.abs(this.fGenerator.fTentativePivot
					.getPrice() - this.fGenerator.getPastPivot(0).fPivotPrice);
			double ratio = newSwing / currentSwing;
			// U.debug_var(291835, "new swing length ", newSwing, " cur swing ",
			// currentSwing, " ratio ", ratio, " required ",
			// NEGATIVE_ON_SC_TOUCH_THRESHOLD_PERC);
			if (ratio < NEGATIVE_ON_SC_TOUCH_THRESHOLD_PERC) {
				return false;
			}
		}

		// U.debug_var(299852, "lev ", fGenerator.fInd.level + 1,
		// " Confirm negative on sc touch pivot");

		/*
		 * Ok, I have created a new starting swing, I enter the state of self
		 * check pivot, if it is enabled.
		 */
		_selfCheckPivot = SELF_PIVOT_BREAKOUT;
		_lastSelfPivotFiredTime = qt.getFakeTime();
		return true;
	}

	/**
	 * This method will try to check if the current price breaks the last pivot
	 * created with this rule.
	 * 
	 * <p>
	 * Of course if another pivot arises from another rule the self check is
	 * aborted until the next swing.
	 * 
	 * 
	 * @param qt
	 * @return
	 */
	private boolean _selfCheckPivot(QueueTick qt) {
		Pivot lastPivot = this.fGenerator.fInd.getLastPivot(0);
		if (lastPivot.fConfirmTime > _lastSelfPivotFiredTime) {
			_selfCheckPivot = false;
			// U.debug_var(328329, "lev ", fGenerator.fInd.level + 1,
			// " another pivot came @ ", lastPivot.fConfirmTime,
			// " I return to normal");
		}

		/*
		 * Ok, Now the last pivot is the same
		 */
		if (lastPivot.isStartingDownSwing()) {
			/*
			 * up pivot, so let's see if the price goes up
			 */
			if (qt.getPrice() > lastPivot.fPivotPrice) {
				// U.debug_var(294200, "lev ", fGenerator.fInd.level + 1,
				// " self up pivot breakout, I return to ZIG!");
				// _selfCheckPivot = false;
				return true;
			}
		} else {
			/*
			 * down pivot, let's see if the price goes down
			 */
			if (qt.getPrice() < lastPivot.fPivotPrice) {
				// U.debug_var(294200, "lev ", fGenerator.fInd.level + 1,
				// " self up pivot breakout, I return to ZAG!");
				// _selfCheckPivot = false;
				return true;
			}
		}

		return false;

	}

}
