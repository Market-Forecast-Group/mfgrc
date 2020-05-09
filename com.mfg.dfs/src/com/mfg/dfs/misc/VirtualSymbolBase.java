package com.mfg.dfs.misc;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.DFSException;
import com.mfg.common.DFSQuote;
import com.mfg.common.DFSVolumeUpdateEvent;
import com.mfg.common.DFSWarmUpFinishedEvent;
import com.mfg.common.QueueTick;
import com.mfg.dm.MonitorCancelledException;
import com.mfg.dm.TickDataRequest;
import com.mfg.dm.filters.CacheExpander;
import com.mfg.dm.filters.ICacheExpanderListener;
import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.utils.U;
import com.mfg.utils.jobs.ProgressMonitorAdapter;

/**
 * The base class for the virtual symbols in DFS.
 * 
 * <p>
 * They may be virtual symbols which are inside dfs or they may be CSV files
 * which are parsed and given here.
 * 
 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
 * 
 */
public abstract class VirtualSymbolBase implements IVirtualSymbol,
		ICacheExpanderListener {

	/*
	 * Here common stuff for all the virtual symbols.
	 */

	/**
	 * The expanders are numbered as the layers, so at position zero there is
	 * <b>always</b> layer zero, etc...
	 */
	protected ArrayList<CacheExpander> _expanders = new ArrayList<>();

	/**
	 * This vector holds all the flags for the warm up. It records if the real
	 * time has started for the given layer.
	 */
	protected boolean[] _realTimeStartedForLayer;

	/**
	 * Each virtual symbol starts with this prefix which is used to know if the
	 * given symbol is virtual or not.
	 */
	private static final String VIRTUAL_SYMBOL_PREFIX = "<V>_";

	/**
	 * returns true if this is a virtual symbol.
	 * 
	 * @param symbol
	 * @return
	 */
	public static boolean isVirtual(String symbol) {
		return symbol.startsWith(VirtualSymbolBase.VIRTUAL_SYMBOL_PREFIX);
	}

	protected final String _id;

	protected final MultiServer _server;

	protected IDelayControl _delayControl;

	/**
	 * The request for this virtual symbol.
	 */
	protected final TickDataRequest _request;

	/**
	 * A server side monitor. This monitor takes orders from the client holder
	 * of this virtual symbol.
	 */
	protected final IProgressMonitor _monitor;

	/**
	 * The thread which keeps the symbol alive.
	 */
	private Thread _virtSymbolThread;

	/**
	 * Every virtual symbol is unique, because also if it is the same symbol and
	 * it has the same request, it is created in another time and needs to be
	 * distinguishable from the others.
	 */
	private static AtomicInteger _nextId = new AtomicInteger(
			(int) (System.currentTimeMillis() % Integer.MAX_VALUE));

	public VirtualSymbolBase(MultiServer aServer, TickDataRequest aRequest) {

		/*
		 * I remove all the commas because this id may be sent in the socket and
		 * in the socket parameters are separated by commas.
		 */
		String safeSymbol = aRequest.getLocalSymbol().replaceAll(",", "_");

		_id = VIRTUAL_SYMBOL_PREFIX + safeSymbol + "_" + aRequest.getHashId()
				+ "_" + _nextId.incrementAndGet();

		_server = aServer;

		_request = aRequest;

		/*
		 * The server side monitor is an object which does not have a GUI mask,
		 * it is only an object that can be cancelled by the client, maybe
		 * remotely.
		 */
		_monitor = new ProgressMonitorAdapter();

		/*
		 * I create the warm up array which is used then to know whether a given
		 * layer is still in warm up or not. Usually the layers are expanded
		 * from range to daily (if not merged).
		 */
		_realTimeStartedForLayer = new boolean[aRequest.getLayersSize()];

	}

	protected abstract void _virtualSymbolThread();

	protected void baseWarmUpNotify(int aLayer) {
		/*
		 * The warm up must be received once for every layer.
		 */
		assert (_realTimeStartedForLayer[aLayer] == false);

		DFSWarmUpFinishedEvent wufe = new DFSWarmUpFinishedEvent(_id, aLayer);
		_server.onNewSymbolEvent(wufe);

		_realTimeStartedForLayer[aLayer] = true;
	}

	@Override
	public final void endOfExpansion(int aLayer) {
		/*
		 * this means that the historical part of the expansion has been
		 * reached, this is the forced end for warm up, if it has not yet been
		 * sent.
		 */
		if (!_realTimeStartedForLayer[aLayer]) {
			notifyEndWarmUp(aLayer, _monitor);
			_realTimeStartedForLayer[aLayer] = true;
		}

	}

	@Override
	public final void fastForward() {
		_delayControl.getModel()
				.setState(DataSpeedControlState.FAST_FORWARDING);
		synchronized (_delayControl.getModel()) {
			_delayControl.getModel().notify();
		}
	}

	@Override
	public final boolean finalNotifiyTick(int aLayer, QueueTick aTick)
			throws MonitorCancelledException {
		if (_monitor.isCanceled()) {
			throw new MonitorCancelledException();
		}
		DFSQuote quote = new DFSQuote(aTick, _id, aLayer,
				!_realTimeStartedForLayer[aLayer], true);
		_server.onNewSymbolEvent(quote);
		/*
		 * The return is always false because the warm up is decided by the
		 * DelayedCacheExpander.
		 */
		return false;
	}

	@Override
	public final void fullSpeedUntil(int limitTime) {
		_delayControl.getModel().setTimeToRun(limitTime);
		synchronized (_delayControl.getModel()) {
			_delayControl.getModel().notify();
		}
	}

	protected void genericSendTick(int aLayer, QueueTick aTick, boolean isFinal)
			throws MonitorCancelledException {
		if (_monitor.isCanceled()) {
			throw new MonitorCancelledException();
		}
		DFSQuote quote = new DFSQuote(aTick, _id, aLayer,
				!_realTimeStartedForLayer[aLayer], isFinal);
		_server.onNewSymbolEvent(quote);
	}

	@Override
	public final String getId() {
		return _id;
	}

	@Override
	public final TickDataRequest getRequest() {
		return _request;
	}

	@Override
	public final void notifyEndWarmUp(int aLayer, IProgressMonitor _monitor1) {
		baseWarmUpNotify(aLayer);
	}

	@Override
	public final void onVolumeUpdate(int aFakeTime, int aVolume)
			throws MonitorCancelledException {
		if (_monitor.isCanceled()) {
			throw new MonitorCancelledException();
		}

		DFSVolumeUpdateEvent volUpd = new DFSVolumeUpdateEvent(_id, aFakeTime,
				aVolume);
		_server.onNewSymbolEvent(volUpd);
	}

	@Override
	public final void passNotFinalTick(int aLayer, QueueTick aTick)
			throws MonitorCancelledException {
		if (_monitor.isCanceled()) {
			throw new MonitorCancelledException();
		}
		DFSQuote quote = new DFSQuote(aTick, _id, aLayer,
				!_realTimeStartedForLayer[aLayer], false);
		_server.onNewSymbolEvent(quote);
	}

	@Override
	public final void pause() {
		_delayControl.getModel().setState(DataSpeedControlState.PAUSED);
	}

	@Override
	public final void play() {
		if (_delayControl == null) {
			return;
		}
		_delayControl.getModel().setState(DataSpeedControlState.PLAYING);
		synchronized (_delayControl.getModel()) {
			_delayControl.getModel().notify();
		}
	}

	/**
	 * @param delay
	 *            the delay in milliseconds for this virtual symbol. *
	 */
	@Override
	public final void setDelay(long delay) {
		_delayControl.getModel().setDelay(delay);
	}

	/**
	 * Starts the virtual symbol giving to the outside the merged ticks which
	 * have been expaned in this realm.
	 * 
	 * <p>
	 * The expansion is done server side.
	 * 
	 * @throws DFSException
	 */
	@Override
	public final void start() {

		_virtSymbolThread = new Thread(new Runnable() {

			@Override
			public void run() {
				_virtualSymbolThread();
			}
		});

		_virtSymbolThread.setName("virtual symbol " + this._id);
		_virtSymbolThread.start();

	}

	@Override
	public final void step() {
		if (_delayControl.getModel().getState() == DataSpeedControlState.PAUSED) {
			synchronized (_delayControl.getModel()) {
				/*
				 * no change in state.
				 */
				_delayControl.getModel().notifyAll();
			}
		}

	}

	@Override
	public final void stop() {

		U.debug_var(178391, "Stopping virtual symbol ", _id);
		/*
		 * When the virtual symbol stops the internal monitor should be
		 * signaled.
		 */
		_monitor.setCanceled(true);

		if (_virtSymbolThread == null) {
			U.debug_var(920193,
					"The virtual symbol thread is null... maybe has been already stopped.");
			return;
		}

		this._virtSymbolThread.interrupt();

		try {
			_virtSymbolThread.join();
		} catch (InterruptedException e) {
			U.debug_var(382955,
					"interrupted while waiting the virtual symbol thread ",
					_virtSymbolThread.getName(), " I am ", Thread
							.currentThread().getName());
		}

		/*
		 * Setting it to null should catch the double close bug, a virtual
		 * symbol should be closed once by the MultiServer class. If not
		 * something is wrong.
		 */
		_virtSymbolThread = null;
	}
}
