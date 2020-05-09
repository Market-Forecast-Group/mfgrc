package com.mfg.widget.arc.strategy;

import com.mfg.interfaces.indicator.Pivot;
import com.mfg.widget.arc.data.PointRegressionLine;
import com.mfg.widget.arc.math.geom.Channel;
import com.mfg.widget.arc.math.geom.Line;

public interface IIndicatorListener {

	/**
	 * Invoked when a new tentative pivot is ready for a certain level. The
	 * tentative pivot is the other end of the "zigzag", the starting point is
	 * the last confirmed pivot of the same level.
	 * 
	 * @param level
	 *            the level in which the tentative pivot is created.
	 * @param aLine
	 *            the line formed by two points: on the left we have the last
	 *            confirmed pivot for the level. On the right we have the
	 *            tentative pivot.
	 */
	void newTentativePivot(int level, Line aLine);

	/**
	 * Called when there is available a new real time channel.
	 * 
	 * @param newChannel
	 */
	void newRealTimeChannel(Channel newChannel);

	/**
	 * Called when a new regression point is available
	 * 
	 * @param prl
	 */
	void newPointRegressionLine(PointRegressionLine prl);

	/**
	 * Called when there is a new pivot available
	 * 
	 * @param pv
	 */
	void newPivot(Pivot pv);

	/**
	 * Called when a new channel starts.
	 * 
	 * @param ch
	 */
	void newStartedChannel(Channel ch);
	
	/**
	 * Called when there is a new touch, either support or resistance
	 * 
	 * @param isSupportTouch true if this has been a support touch, false
	 * if it has been a resistance touch.
	 * 
	 * @param count the current count of the indicator
	 * 
	 * @param level the level where has happened the touch.
	 */
	void onNewTouch(boolean isSupportTouch, int count, int level);
}
