package com.mfg.dfs.conn;

import com.mfg.common.DFSException;
import com.mfg.common.DFSStoppingSubscriptionEvent;
import com.mfg.common.DFSSubscriptionStartEvent;
import com.mfg.common.DFSSymbolEvent;
import com.mfg.common.DFSWarmUpFinishedEvent;
import com.mfg.common.IDataSource;
import com.mfg.common.ISymbolListener;
import com.mfg.utils.U;
import com.mfg.utils.concurrent.LazyWriteArrayList;
import com.mfg.utils.concurrent.LazyWriteArrayList.RunnableItem;

public abstract class BaseDataSource implements IDataSource, ISymbolListener {

	/**
	 * In case of a remove data source the stopping coudl cause a deadlock
	 * because the thread holds this object in the {@link #stop()} method and
	 * the remote server may want to send some more pushes using the
	 * {@link #onNewSymbolEvent(DFSSymbolEvent)}, which is itself synchronized.
	 * 
	 * <p>
	 * So the solution may be to give to the base data source the possibility to
	 * have a flag that signals the impending shutdown
	 */
	private boolean _startedShutdown;

	@Override
	public synchronized final void stop() throws DFSException {

		_startedShutdown = true;

		_notifyStopping();

		/*
		 * do the real stop and then remove itself from the service.
		 */
		_stopImpl();

		_baseService.removeStoppedDataSource(this);
	}

	/**
	 * Notifies all the listeners that this data source is going to be stopped.
	 * This is important for the listeners that have connected to this data
	 * source afterwards, for example the market simulator inside a broker.
	 */
	private void _notifyStopping() {
		DFSStoppingSubscriptionEvent sse = new DFSStoppingSubscriptionEvent(
				_symbol);
		NewQuoteRunnable code = new NewQuoteRunnable(sse);
		_listeners.iterateCode(code);
	}

	protected abstract void _stopImpl() throws DFSException;

	/**
	 * There is only one base service for each application, either local or
	 * remote. This is used to notify when the data source stops.
	 */
	private static BaseService _baseService = null;

	private static final class NewQuoteRunnable extends
			RunnableItem<ISymbolListener> {

		private final DFSSymbolEvent _event;

		NewQuoteRunnable(DFSSymbolEvent anEvent) {
			_event = anEvent;
		}

		@Override
		public void run(ISymbolListener aItem) {
			aItem.onNewSymbolEvent(_event);
		}

	}

	//

	/**
	 * Every data source is tied to a particular symbol. This is the virtual
	 * symbol unique identifier.
	 */
	protected final String _symbol;

	@Override
	public final String getId() {
		return _symbol;
	}

	/**
	 * This is the created listener. Every data source has bound to a created
	 * listener which is the first. A data source without listeners cannot be.
	 */
	private final LazyWriteArrayList<ISymbolListener> _listeners = new LazyWriteArrayList<>();

	/**
	 * This stores the start event used to tell the other listeners the data
	 * source data.
	 */
	private DFSSubscriptionStartEvent _startEvent;

	/**
	 * The data source starts in warm up, but then if listeners add to it it
	 * will tell them if the warm up is finished.
	 */
	private boolean _realTimeStartedForLayer[];

	/**
	 * @param layersCount
	 *            how many layers are present in this data source.
	 */
	public BaseDataSource(String aSymbol, ISymbolListener aListener,
			int layersCount) {
		_symbol = aSymbol;
		_listeners.add(aListener);

		_realTimeStartedForLayer = new boolean[layersCount];
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Important: this method should not be synchronized because it may cause a
	 * deadlock, see the comment on the field {@link #_startedShutdown}
	 */
	@Override
	public final void onNewSymbolEvent(DFSSymbolEvent anEvent) {
		if (_startedShutdown) {
			U.debug_var(382515, "in shutdown> ignored event ", anEvent);
			return;
		}
		/*
		 * This will acquire the lock on the object.
		 */
		_onNewSymbolEvent(anEvent);
	}

	private synchronized final void _onNewSymbolEvent(DFSSymbolEvent anEvent) {

		if (anEvent instanceof DFSSubscriptionStartEvent) {
			/*
			 * I store the start event because other listeners may append to
			 * this data source.
			 */
			_startEvent = (DFSSubscriptionStartEvent) anEvent;
		} else if (anEvent instanceof DFSWarmUpFinishedEvent) {
			DFSWarmUpFinishedEvent wufe = (DFSWarmUpFinishedEvent) anEvent;
			_realTimeStartedForLayer[wufe.layer] = true;
		}

		NewQuoteRunnable nqr = new NewQuoteRunnable(anEvent);
		_listeners.iterateCode(nqr);
	}

	@Override
	public final void addListener(ISymbolListener aListener, boolean addFirst) {
		if (_startEvent != null) {
			aListener.onNewSymbolEvent(_startEvent);
		}

		for (int i = 0; i < _realTimeStartedForLayer.length; ++i) {
			if (_realTimeStartedForLayer[i]) {
				aListener.onNewSymbolEvent(new DFSWarmUpFinishedEvent(_symbol,
						i));
			}
		}

		if (addFirst) {
			_listeners.addFirst(aListener);
		} else {
			_listeners.add(aListener);
		}

	}

	@Override
	public final void removeListener(ISymbolListener aListener) {
		_listeners.remove(aListener);
	}

	/**
	 * Sets the service which is shared by all the data sources.
	 * 
	 * <p>
	 * It is an error to set the service twice with a different server.
	 * 
	 * @param baseService
	 */
	public static void setService(BaseService baseService) {
		if (_baseService != null && baseService != null) {
			throw new IllegalStateException();
		}
		_baseService = baseService;
	}

}
