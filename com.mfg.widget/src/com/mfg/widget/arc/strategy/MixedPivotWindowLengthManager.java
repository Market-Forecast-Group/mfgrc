package com.mfg.widget.arc.strategy;

/**
 * A mixed length manager which can make the channel shorter, when there is a
 * new pivot, but not shorter than the window.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public class MixedPivotWindowLengthManager extends ChannelLengthManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3242932694612390255L;

	MixedPivotWindowLengthManager(BaseScaleIndicator aInd) {
		super(aInd);

		/*
		 * I need to know the window, but the window is not known until the end
		 * of the warm up (in the naked case), so the only thing to do is to
		 * delay the rule creation, in any case when the indicator is naked the
		 * channel is not created.
		 */

	}

	@Override
	public boolean tryMakeChannelShorter(boolean forNegative) {
		if (!forNegative) {
			/*
			 * A positive channel is never shortened
			 */
			return false;
		}

		/*
		 * this is the minimum window which is used by the channel.
		 */
		int mininumWindow = ((IndicatorSingle) fInd).fIndicator
				.getMinimumWindow();

		/*
		 * Confirm time is the actual time. So maximum start point will be
		 * before the window length from the confirm time of the pivot.
		 */
		int maximumStartPoint = fInd.getLastPivot(0).fConfirmTime
				- mininumWindow;

		/*
		 * This is the start point candidate at maximum, because it counts the
		 * window and the last pivot.
		 */
		int candidateNewStartPoint = Math.min(maximumStartPoint,
				fInd.getLastPivot(0).fPivotTime);

		/*
		 * I cannot go backwards...
		 */
		int realNewStartPoint = Math.max(fInd.getStartPoint(),
				candidateNewStartPoint);

		// U.debug_var(382935, "pivot ", fInd.getLastPivot(0), " max sp ",
		// maximumStartPoint, " window ", mininumWindow, " candidate ",
		// candidateNewStartPoint, " choosen ", realNewStartPoint);

		/*
		 * As always moving a channel to the same start point (for a negative
		 * channel) is not an error.
		 */
		fInd.shortenChannel(realNewStartPoint);
		return true;
	}

}
