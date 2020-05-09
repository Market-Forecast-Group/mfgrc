package com.mfg.dm;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.QueueTick;

/**
 * This interface models all the tick listeners in the system. A Tick listener
 * is an object that is able to consume ticks. Usually these ticks are already
 * filtered and come from a particular source (either historical or real time).
 * 
 * <p>
 * The tick listener does not know the symbol of this source, this should be
 * accessible from the parameters of the data source configuration (or the trade
 * configuration if this tick listener is a participant in the trade).
 * 
 * @author Sergio
 * 
 */
public interface ITickListener {

	/**
	 * called when a new (post processed tick) is created
	 * 
	 * @param qt
	 *            the post processed tick
	 * 
	 */
	public void onNewTick(QueueTick qt);

	/**
	 * Called when the tick data source is going to start
	 * 
	 * @param tick
	 *            the computed tick (or the manual/forced tick) of this data
	 *            source
	 * @param scale
	 *            the computed scale (or the manual/forced scale) of this data
	 *            source
	 */
	public void onStarting(int tick, int scale);

	/**
	 * called when the subscription is going to be stopped. Listeners are able
	 * in this case to do some clean up.
	 */
	public void onStopping();

	/**
	 * invoked when a new <i>temporary</i> (or <b>not final</b>) tick is
	 * created.
	 * 
	 * <p>
	 * The temporary tick is a tick which is not final, because it is inside a
	 * range which has already being covered by final ticks.
	 * 
	 * @param qt
	 *            the temporary tick which has been created.
	 * 
	 */
	public void onTemporaryTick(QueueTick qt);

	/**
	 * Updates the volume for the tick at the corresponding fakeTime. The
	 * faketime parameter is referring to an existing fake time, always.
	 * 
	 * <p>
	 * The volume update may regard the tick -2, that is the tick which is 2
	 * ticks before the current tick.
	 * 
	 * <p>
	 * This will happen when two final ticks are sent at the same time. In that
	 * case the data source will send the two final ticks, the first with a
	 * final volume and the second with a temporary volume. And then it will
	 * send the update for the tick at the same price as the last -1 tick.
	 * 
	 * <p>
	 * At the present moment I do not really know if the volume update will be
	 * sent before or after the two consecutive ticks.
	 * 
	 * @param fakeTime
	 * @param volume
	 */
	public void onVolumeUpdate(int fakeTime, int volume);

	/**
	 * invoked when the warm up is finished, and real time begins.
	 * 
	 * <p>
	 * The warm up can be simulated. For example the csv files have a simulated
	 * warm up which is simulated by giving a certain number of tick.
	 */
	public void onWarmUpFinished();

	/**
	 * invoked before the {@link #onWarmUpFinished()} event to enable some
	 * objects, mainly the indicator, to do some lenght computations before the
	 * end of warm up is given to the outside.
	 * 
	 * <p>
	 * If the indicator is not naked this event is a no-op, for all the other
	 * object also this event can be safely ignored.
	 * 
	 * <p>
	 * As this event may be time consuming the progress indicator is passed
	 * because the user may want to stop the processing prematurely
	 * 
	 * @param aMonitor
	 *            the monitor used to check if the user wants to stop the
	 *            processing
	 */
	public void preWarmUpFinishedEvent(IProgressMonitor aMonitor);

	/**
	 * Signals that the real time queue has gone down a certain size. Listeners
	 * are then able to resume normal processing.
	 * 
	 * @param currentSize
	 *            the upper limit of the current size of the queue.
	 */
	public void realTimeQueueAlertDown(int currentSize);

	/**
	 * this method is a hint to the consumers that the queue is becoming too
	 * much long, the listener should do its best to process a tick as fast as
	 * possibile, because real time ticks are of course not discarded but they
	 * may be in the queue waiting to be processed.
	 * 
	 * <p>
	 * Listeners are currently not able to choose the frequency of alerts
	 * 
	 * @param currentSize
	 *            the current size of the queue.
	 */
	public void realTimeQueueAlertUp(int currentSize);
}
