package com.mfg.widget.probabilities;

import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.indicator.Pivot;

public class WidgetSwingInfo extends SwingInfo {

	private double slen;
	private double pslen;
	private double thlen;

	public WidgetSwingInfo(IIndicator w, int scale) {
		slen = w.getLastPivot(0, scale).fLinearSwing;
		Pivot lastPivot = w.getLastPivot(-1, scale);
		thlen = Math.abs(lastPivot.fPivotPrice - lastPivot.fConfirmPrice);
		pslen = lastPivot.fLinearSwing;
	}

	@Override
	public double getSwingLength() {
		return slen;
	}

	@Override
	public double getPreviousSwingLength() {
		return pslen;
	}

	@Override
	public double getTargetLength() {
		// TODO Auto-generated method stub
		return slen;
	}

	@Override
	public double getTHLength() {
		return thlen;
	}

	@Deprecated
	@Override
	public double getTargetPrice() {
		return 0;
	}

}
