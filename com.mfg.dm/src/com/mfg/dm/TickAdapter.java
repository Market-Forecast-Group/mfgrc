package com.mfg.dm;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.QueueTick;

/**
 * An abstract adapter class for receiving ticks events. The methods in this
 * class are empty. This class exists as convenience for creating listener
 * objects.
 * 
 * @author arian
 * 
 */
public abstract class TickAdapter implements ITickListener {

	@Override
	public void onNewTick(QueueTick qt) {
		// onNewTick
	}

	@Override
	public void onStarting(int tick, int scale) {
		// starting
	}

	@Override
	public void onStopping() {
		// onStopping
	}

	@Override
	public void onTemporaryTick(QueueTick qt) {
		// onTempTick
	}

	@Override
	public void onVolumeUpdate(int fakeTime, int volume) {
		// nothing

	}

	@Override
	public void onWarmUpFinished() {
		// nothing here

	}

	@Override
	public void preWarmUpFinishedEvent(IProgressMonitor aMonitor) {
		// nothing
	}

	@Override
	public void realTimeQueueAlertDown(int currentSize) {
		// nothing
	}

	@Override
	public void realTimeQueueAlertUp(int currentSize) {
		// nothing

	}

}
