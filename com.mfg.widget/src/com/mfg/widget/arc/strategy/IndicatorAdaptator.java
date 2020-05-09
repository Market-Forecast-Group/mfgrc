package com.mfg.widget.arc.strategy;

import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.data.PointRegressionLine;
import com.mfg.widget.arc.math.geom.Channel;
import com.mfg.widget.arc.math.geom.Line;

/**
 * an adaptor to the indicator interface, in this way you are not obliged to
 * override all the indicator's methods subscribing to the interface.
 * 
 * @author Sergio
 * 
 */
public class IndicatorAdaptator implements IIndicatorListener {

	@Override
	public void newTentativePivot(int level, Line aLine) {
		// nothing
	}

	@Override
	public void onNewTouch(boolean isSupportTouch, int count, int level) {
		// empty
	}

	@Override
	public void newRealTimeChannel(Channel newChannel) {
		// empty
	}

	@Override
	public void newPointRegressionLine(PointRegressionLine prl) {
		// empty
	}

	@Override
	public void newPivot(Pivot pv) {
		// empty
	}

	@Override
	public void newStartedChannel(Channel ch) {
		// empty
	}

}
