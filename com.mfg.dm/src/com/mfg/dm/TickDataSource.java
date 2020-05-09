package com.mfg.dm;

import java.util.ConcurrentModificationException;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.mfg.common.DFSException;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.dm.speedControl.IDelayedDataSource;

/**
 * the base class for all the <b>tick</b> data sources, that is data sources
 * which produce ticks. The data sources can be <i>bar</i> data sources and
 * <b>tick</b> data sources.
 * 
 * <p>
 * The data source is then put in the Job to be part of a trading configuration.
 * 
 * @author Sergio
 * 
 */
public abstract class TickDataSource implements IDelayedDataSource {

	/**
	 * The real time layer is by definition the layer zero.
	 */
	public static final int REAL_TIME_LAYER = 0;

	/**
	 * The csv layer is by definition the layer zero (it is the same as the real
	 * time layer).
	 */
	public static final int CSV_LAYER = 0;

	protected final UUID fId;

	protected TickDataSource(UUID aId) {
		fId = aId;
	}

	/**
	 * 
	 * @return a human like message for the last abortion.
	 */
	public abstract String abortedReason();

	/**
	 * adds a tick listener to this player.
	 * 
	 * The tick listener should be added <i>before</i> the starting method has
	 * been called. Failure to do so may result in a
	 * {@link ConcurrentModificationException}.
	 * 
	 * @param aLayer
	 *            the layer to which you want to add the tick listener itself.
	 *            The real time layer is always layer zero. In case of csv files
	 *            only one layer is present and it is layer zero.
	 * @param aTickListener
	 *            the listener that you want to add.
	 */
	public abstract void addTickListener(int aLayer, ITickListener aTickListener);

	/**
	 * responds to the {@link Job#cancel()} method. The data source has a chance
	 * to stop.
	 */
	public abstract void canceling();

	@Override
	public abstract IDelayControl getDelayControl();

	/**
	 * returns the global unique identifier.
	 * 
	 * <p>
	 * This is the mfg global identifier which is created in the input pipe.
	 * 
	 * @return
	 */
	public UUID getId() {
		return fId;
	}

	/**
	 * returns the data source identifier, the string which is created by DFS,
	 * unique in dfs space. This is usually the virtual symbol's string.
	 * 
	 * @return the virtual symbol id.
	 */
	@SuppressWarnings("static-method")
	public String getDataSourceId() {
		return null;
	}

	public abstract int getLayersSize();

	public abstract TickDataRequest getRequest();

	/**
	 * returns the tick for this data source.
	 * 
	 * <p>
	 * This is a just a convenience method.
	 * 
	 * @return
	 */
	public abstract int getTick();

	public abstract boolean hasBeenAborted();

	/**
	 * The csv data source has a different way of dealing with warm up. This may
	 * mean that the
	 */
	protected boolean _inWarmUp = true;

	public final boolean isInWarmUp() {
		return _inWarmUp;
	}

	/**
	 * This is the <b>stealing</b> code. This function does not exit. It steals
	 * the thread until the monitor is cancelled.
	 * 
	 * <p>
	 * The fact is that the monitor is <i>unique</i> in the job, but the data
	 * source could have different <b>layers</b>. Each layer has its own
	 * listener.
	 * 
	 * <p>
	 * This method will start the real time data stream. This may have different
	 * meanings, because the data source may be a database data source so the
	 * distinction between historical and real time is arbitrary.
	 * 
	 * <p>
	 * Only in the case of a normal data source this method really starts the
	 * real time stream.
	 * 
	 * <p>
	 * Under the hood there may (or may not) be a subscription to a real symbol,
	 * but the client does not see it.
	 * 
	 * @param monitor
	 *            the <b>global</b> monitor (it is the same for all the layers).
	 * @throws DFSException
	 */
	public abstract void kickTheCan(IProgressMonitor monitor)
			throws DFSException;

	/**
	 * This is a helper method used only by the {@link LayeredDataSource}. It
	 * tells to a layer to start a fake real time thread if the
	 * {@link TickDataRequest} is not real time.
	 * 
	 * <p>
	 * This because in the {@link #kickTheCan(IProgressMonitor)} method the
	 * {@link CompositeDataSource} already does an expansion, but the kickTheCan
	 * is not called by the kick the can of the layered data source.
	 * 
	 * @param aMonitor
	 *            the monitor used to check if the data source is cancelled.
	 * @throws DFSException
	 */
	protected abstract void preKickTheCanHook(IProgressMonitor aMonitor)
			throws DFSException;

	/**
	 * Removes a tick listener from the player.
	 * 
	 * The tick listener should be removed after the player is stopped. Failure
	 * to do so may result in a {@link ConcurrentModificationException}.
	 * 
	 * @param aTickListener
	 *            the tick listener to be removed
	 */
	public abstract void removeTickListener(int aLayer,
			ITickListener aTickListener);

	@Override
	public abstract void setDelayControl(IDelayControl control);

	/**
	 * starts the data source.
	 * <p>
	 * This generically imply also the collection of the bars from the outside.
	 * This may take a bit of time if the data provider is a proxy and not
	 * local.
	 * 
	 * <p>
	 * For this reason this method has a parameter to be able to interrupt the
	 * request if it is taking too long.
	 * 
	 * <p>
	 * This method is synchronous.
	 * 
	 * @param monitor
	 *            an object used to notify the user of the progress and to pool
	 *            if the user wants to cancel the operation. If the monitor is
	 *            null a fake monitor which does not ever cancel is created.
	 * 
	 * @return the result of the start. The method should not throw any
	 *         exceptions.
	 * @throws DFSException
	 */
	public abstract EStartOutput start(IProgressMonitor monitor)
			throws DFSException;

	public abstract void stop() throws DFSException;

}
