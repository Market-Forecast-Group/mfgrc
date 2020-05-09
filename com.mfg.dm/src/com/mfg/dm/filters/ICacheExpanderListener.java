package com.mfg.dm.filters;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.QueueTick;
import com.mfg.dm.CompositeDataSource;
import com.mfg.dm.MonitorCancelledException;

/**
 * This interface defines an object which is able to listen to the cache
 * expanders ticks which are sent expanding the cache.
 * 
 * <p>
 * There are two types of ticks, final and not final: these two types of ticks
 * are in reality sent during real time, because it is assumed that in
 * historical part <i>all</i> ticks are final.
 * 
 * <p>
 * The two known classes that implement this interface are one which can delay
 * the tick and the other is the {@link CompositeDataSource} itself
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public interface ICacheExpanderListener {

	/**
	 * Called when the expander has finished the <b>historical</b> part of the
	 * expansion. This is then translated to the "end of warm up" event if it
	 * needs to be.
	 * 
	 * @param aLayer
	 *            the layer that has finished the expansion, that means that it
	 *            has completed the cache.
	 * 
	 * @throws MonitorCancelledException
	 */
	public void endOfExpansion(int aLayer) throws MonitorCancelledException;

	/**
	 * Notifies a tick for a particular layer, this is the new method which will
	 * deprecate the {@link #finalNotifiyTick(QueueTick)}
	 * 
	 * @param aLayer
	 * @param aTick
	 * @return
	 * @throws MonitorCancelledException
	 * @throws EndOfWarmUpException
	 * 
	 * @return true if this tick marks the transition between the warm up period
	 *         and the real time. This true flag is returned only by the
	 *         {@link DelayableCacheExpanderListener}
	 */
	public boolean finalNotifiyTick(int aLayer, QueueTick aTick)
			throws MonitorCancelledException;

	/**
	 * notifies the end of warm up for the given layer.
	 * 
	 * @param aLayer
	 * @param _monitor
	 * @throws MonitorCancelledException
	 */
	public void notifyEndWarmUp(int aLayer, IProgressMonitor _monitor)
			throws MonitorCancelledException;

	/**
	 * Updates the volume, to be defined better...
	 * 
	 * <p>
	 * This method is passed when the {@link CacheExpander} (which is
	 * responsible for the filling of the gaps and the general structure of the
	 * prices stream) outputs two final prices.
	 * 
	 * <p>
	 * In this case the volume of the price <b>before</b> the first of the two
	 * final ticks is updated (because the volume needs to be shared between the
	 * two ticks with equal price).
	 * 
	 * 
	 * @param aFakeTime
	 * @param aVolume
	 * @throws MonitorCancelledException
	 */
	public void onVolumeUpdate(int aFakeTime, int aVolume)
			throws MonitorCancelledException;

	/**
	 * pass a not final tick. This is the method which will deprecate the
	 * {@link #passNotFinalTick(QueueTick)} one.
	 * 
	 * @param aLayer
	 * @param aTick
	 * @throws MonitorCancelledException
	 */
	public void passNotFinalTick(int aLayer, QueueTick aTick)
			throws MonitorCancelledException;
}
