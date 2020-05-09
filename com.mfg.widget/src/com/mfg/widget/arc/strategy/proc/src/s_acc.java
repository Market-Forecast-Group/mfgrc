package com.mfg.widget.arc.strategy.proc.src;

import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;

/**
 * Module which holds some functions to do the "accumulation" of the channel
 * widget (and other dependant objects) to a long.
 */
public final class s_acc {

	/* don't create me */
	private s_acc() {
	}

	/**
	 * This is the function which accumulates the visible channel state into an
	 * accumulator, in order to test the coherence of the widget itself.
	 * 
	 * @return a long representing the long representation of the current widget
	 *         _visible_ state.
	 */
	public static long wid_acc(MultiscaleIndicator chw) {
		long current = 0;

		int scales = chw.getParamBean().getIndicatorNumberOfScales();

		for (int i = 1; i <= scales; ++i) {
			boolean b = chw.isThereANewPivot(i);
			current += boolean_to_long(b);

			if (b) {
				Pivot pv = chw.getLastPivot(0, i);
				current += pv_acc(pv);
			}

			current += double_to_long(chw.getCurrentTopRegressionPrice(i));
			current += double_to_long(chw.getCurrentCenterRegressionPrice(i));
			current += double_to_long(chw.getCurrentBottomRegressionPrice(i));

			current += double_to_long(chw.getCurrentRawTopRegressionPrice(i));
			current += double_to_long(chw.getCurrentRawCenterRegressionPrice(i));
			current += double_to_long(chw.getCurrentRawBottomRegressionPrice(i));

			current += chw.getHHPrice(i);
			current += chw.getHHTime(i);
			current += chw.getLLPrice(i);
			current += chw.getLLTime(i);
		}

		return current;
	}

	/**
	 * @return a long representation of the current pivot
	 */
	public static long pv_acc(Pivot pv) {
		long rep = 0;

		rep += pv.fPivotTime;
		rep += pv.fConfirmTime;

		rep += pv.fPivotPrice;
		rep += pv.fConfirmPrice;

		rep += boolean_to_long(pv.isStartingDownSwing());

		return rep;
	}

	/**
	 * @return 1 if b is true, 0 otherwise
	 */
	public static long boolean_to_long(boolean b) {
		return b ? 1L : 0L;
	}

	/**
	 * Converts a double to a long, the conversion is done on the 3 decimal
	 * places as rounding errors may percolate towards the end giving inaccurate
	 * results.
	 */
	public static long double_to_long(double d) {
		long ld = (long) (d * 1000); // truncate decimal part
		return ld;
	}

}