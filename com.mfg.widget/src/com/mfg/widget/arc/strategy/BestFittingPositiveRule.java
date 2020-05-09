package com.mfg.widget.arc.strategy;

import java.util.ArrayList;

import com.mfg.common.QueueTick;
import com.mfg.interfaces.indicator.Pivot;

/**
 * This rule tries to make a new positive channel when the new channel best fits
 * the prices. The rule is that the new channel will model the 1ls pivots better
 * (with less distance).
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class BestFittingPositiveRule extends PositiveChannelCreationRule {

	private final double _fittingPar;
	private BaseScaleIndicator _indicator;

	public BestFittingPositiveRule(PositiveChannelsGenerator pcg,
			double fittingPar) {
		super(pcg);
		_fittingPar = fittingPar;
		_indicator = pcg.fInd;
	}

	@Override
	public void begin(int tick) {
		// nothing
	}

	@Override
	protected boolean tryToFire(QueueTick qt) {

		if (!_indicator.prevInd.prevInd.isThereANewPivot()) {
			return false;
		}

		/*
		 * Ok, I have a pivot at 1ls scale. I have to find the pivot to which
		 * the top or bottom parts of the channel touch.
		 */

		/*
		 * Then I have to check the channel value at the pivot next, with the
		 * same orientation (up for top, down for bottom)
		 */

		IChannel curChannel = _indicator.getCurrentChannel();

		Pivot pv = _getNextTopInferiorPivot();

		/*
		 * Now I have to get the value of the channel at that point
		 */
		if (pv != null) {
			double valChannel = curChannel.eval(pv.getPivotTime());
			if (!Double.isNaN(valChannel)) {
				double topDistance = curChannel.getTopDistance();

				double ratio = (pv.getPivotPrice() - valChannel)
						/ (topDistance);

				// assert (ratio > 0);

				if (ratio > 0 && ratio < _fittingPar) {
					return true;
				}

			}
		}

		pv = _getNextBottomInferiorPivot();

		/*
		 * Now I have to get the value of the channel at that point
		 */
		if (pv != null) {
			double valChannel = curChannel.eval(pv.getPivotTime());
			if (!Double.isNaN(valChannel)) {
				double bottomDistance = curChannel.getBottomDistance();

				double ratio = (valChannel - pv.getPivotPrice())
						/ (bottomDistance);

				// assert (ratio > 0);

				if (ratio > 0 && ratio < _fittingPar) {
					return true;
				}
			}
		}

		return false;
	}

	private Pivot _getNextBottomInferiorPivot() {
		return _getNextExtremeInferiorPivot(false);
	}

	/**
	 * Returns the pivot at the lower scale next (in the future) to the top
	 * pivot which is contained in this channel.
	 * 
	 * @return the next pivot.
	 */
	private Pivot _getNextTopInferiorPivot() {
		return _getNextExtremeInferiorPivot(true);
	}

	private Pivot _getNextExtremeInferiorPivot(boolean searchingForTop) {
		long extremePrice = searchingForTop ? Long.MIN_VALUE : Long.MAX_VALUE;

		/*
		 * I have to get the top pivot of the inferior scale which is in the
		 * range of this channel.
		 */

		ArrayList<Pivot> list1ls = this.fPositiveGenerator.fInd.prevInd
				.getPivotsList();

		if (list1ls.size() < 2) {
			return null;
		}

		// Pivot rightToTheMaxPivot = null;
		Pivot secondRightToTheMaxPivot = null;
		Pivot curRightPivot = null;
		Pivot curSecondToRightPivot = null;
		for (int i = list1ls.size() - 1; i >= 0; --i) {
			Pivot pv = list1ls.get(i);

			if (pv.getPivotTime() < fPositiveGenerator.fInd.getStartPoint()) {
				// finished searching
				break;
			}

			if (pv.isStartingDownSwing() && searchingForTop) {
				if (pv.getPivotPrice() > extremePrice) {
					extremePrice = pv.getPivotPrice();
					secondRightToTheMaxPivot = curSecondToRightPivot;
					// rightToTheMaxPivot = curRightPivot;
				}
			} else if (!pv.isStartingDownSwing() && !searchingForTop) {
				if (pv.getPivotPrice() < extremePrice) {
					extremePrice = pv.getPivotPrice();
					secondRightToTheMaxPivot = curSecondToRightPivot;
					// rightToTheMaxPivot = curRightPivot;
				}
			}

			curSecondToRightPivot = curRightPivot;
			curRightPivot = pv;
		}

		return secondRightToTheMaxPivot;
	}

}
