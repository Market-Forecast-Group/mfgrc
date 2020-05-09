package com.mfg.widget.probabilities;

import com.mfg.common.QueueTick;
import com.mfg.dm.ITickListener;
import com.mfg.dm.TickAdapter;
import com.mfg.interfaces.indicator.IIndicator;

public class SimpleTickListener extends TickAdapter implements IIndicatorRunner {

	private ITickListener ts;
	private IIndicator indicator;
	private boolean stopped;

	public SimpleTickListener(ITickListener ts1) {
		super();
		this.ts = ts1;
		stopped = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IIndicatorRunner#getIndicator()
	 */
	@Override
	public IIndicator getIndicator() {
		return indicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.widget.probabilities.IIndicatorRunner#setIndicator(com.mfg.interfaces
	 * .indicator.IIndicator)
	 */
	@Override
	public void setIndicator(IIndicator indicator2) {
		indicator = indicator2;
	}

	@Override
	public void onStarting(int tick, int scale) {
		ts.onStarting(tick, scale);
		indicator.onStarting(tick, scale);
	}

	@Override
	public void onNewTick(QueueTick aQt) {
		ts.onNewTick(aQt);
	}

	@Override
	public void onStopping() {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IIndicatorRunner#stop()
	 */
	@Override
	public void stop() {
		stopped = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IIndicatorRunner#isStopped()
	 */
	@Override
	public boolean isStopped() {
		return stopped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.widget.probabilities.IIndicatorRunner#setStopped(boolean)
	 */
	@Override
	public void setStopped(boolean aStopped) {
		stopped = aStopped;
	}

}
