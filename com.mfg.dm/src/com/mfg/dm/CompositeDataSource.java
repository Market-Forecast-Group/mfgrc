package com.mfg.dm;

import static com.mfg.utils.Utils.debug_var;

import java.util.ArrayList;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.DFSException;
import com.mfg.common.MfgSymbol;
import com.mfg.common.QueueTick;
import com.mfg.common.RealTick;
import com.mfg.dm.filters.CacheExpander;
import com.mfg.dm.filters.CacheExpander.EGapFillingMethod;
import com.mfg.dm.filters.DelayableCacheExpanderListener;
import com.mfg.dm.filters.ICacheExpanderListener;
import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.utils.U;
import com.mfg.utils.concurrent.LazyWriteArrayList;
import com.mfg.utils.jobs.ProgressMonitorAdapter;

/**
 * The composite data source is a data source which can give ticks (post
 * processed) and also the indicator's data.
 * 
 * This should be equivalent to the subscription class, which was used in the
 * old application
 * 
 * The class is able to store the ticks which come from the warm up algorithm
 * and to store the real time ticks as soon as they come.
 */
public class CompositeDataSource extends TickDataSource implements
		ICacheExpanderListener {

	private static final class NotifyEndOfWarmUp extends
			LazyWriteArrayList.RunnableItem<ITickListener> {

		private IProgressMonitor _monitor;

		public NotifyEndOfWarmUp(IProgressMonitor aMonitor) {
			_monitor = aMonitor;
		}

		@Override
		public void run(ITickListener aItem) {
			aItem.preWarmUpFinishedEvent(_monitor);
			aItem.onWarmUpFinished();
		}

	}

	/**
	 * The functor responsible to notify a new tick.
	 * 
	 * @author Pasqualino Ferrentino <lino.ferrentino@gmail.com>
	 * 
	 */
	private static final class NotifyNewTickRunnable extends
			LazyWriteArrayList.RunnableItem<ITickListener> {

		private final QueueTick _qt;

		public NotifyNewTickRunnable(QueueTick qt) {
			_qt = qt;
		}

		@Override
		public void run(ITickListener aItem) {
			aItem.onNewTick(_qt);
		}

	}

	private static final class VolumeUpdateRunnable extends
			LazyWriteArrayList.RunnableItem<ITickListener> {

		private final int _fakeTime;
		private final int _volume;

		public VolumeUpdateRunnable(int aFakeTime, int aVolume) {
			_fakeTime = aFakeTime;
			_volume = aVolume;
		}

		@Override
		public void run(ITickListener aItem) {
			aItem.onVolumeUpdate(_fakeTime, _volume);
		}

	}

	private static final class NotifyNotFinalTickRunnable extends
			LazyWriteArrayList.RunnableItem<ITickListener> {

		private final QueueTick _qt;

		public NotifyNotFinalTickRunnable(QueueTick qt) {
			_qt = qt;
		}

		@Override
		public void run(ITickListener aItem) {
			aItem.onTemporaryTick(_qt);
		}

	}

	private static final class NotifyOnStartingRunnable extends
			LazyWriteArrayList.RunnableItem<ITickListener> {

		// private MfgSymbol _symbol;

		private int _tick;
		private int _scale;

		public NotifyOnStartingRunnable(int aTick, int aScale) {
			// _symbol = aSymbol;
			_tick = aTick;
			_scale = aScale;
		}

		@Override
		public void run(ITickListener aItem) {
			aItem.onStarting(_tick, _scale);
		}

	}

	private static final class NotifyQueueAlert extends
			LazyWriteArrayList.RunnableItem<ITickListener> {

		private final boolean _up;

		private final int _size;

		public NotifyQueueAlert(boolean up, int aSize) {
			_up = up;
			_size = aSize;
		}

		@Override
		public void run(ITickListener aItem) {
			if (_up) {
				aItem.realTimeQueueAlertUp(_size);
			} else {
				aItem.realTimeQueueAlertDown(_size);
			}

		}

	}

	private static final class NotifyStopping extends
			LazyWriteArrayList.RunnableItem<ITickListener> {

		public NotifyStopping() {
			//
		}

		@Override
		public void run(ITickListener aItem) {
			aItem.onStopping();
		}

	}

	/**
	 * As the layers are fixed at construction time I can have a simple array of
	 * listeners, each array list has the listeners for a particular layer.
	 * 
	 * <p>
	 * The array list of listers is moved from the {@link CompositeDataSource}
	 * 
	 */
	private LazyWriteArrayList<ITickListener> _listeners[];

	/**
	 * The current monitor which makes this request stoppable.
	 * 
	 */
	protected IProgressMonitor _currentMonitor;

	/**
	 * The request is here because I have only one request.
	 */
	protected final TickDataRequest _request;

	private String fAbortedReason;

	protected final IDataProvider fDp;

	/**
	 * This is a temporary array used to get the expanders
	 */
	protected final ArrayList<CacheExpander> _expanders = new ArrayList<>();

	protected MfgSymbol _symbol;

	private final boolean hasBeenAborted = false;

	private long _seed;

	boolean _subscribed;

	private ICacheExpanderListener _delayableListener;

	protected IDelayControl _delayControl;

	private Thread _historicalTicksThread;

	/**
	 * This constructor is used only by the CsvCompositeDataSource. It gives no
	 * data provider and no uuid, because it is usually a thrown away request,
	 * in which the ticks are given using the
	 * {@link #playTicks(IProgressMonitor)} method and not the
	 * {@link #kickTheCan(IProgressMonitor)}.
	 * 
	 * @param aRequest
	 *            the request used to build the data source
	 * 
	 * 
	 */
	protected CompositeDataSource(TickDataRequest aRequest) {
		super(null);
		_request = aRequest;
		this._symbol = aRequest.getSymbol();
		this.fDp = null;
		_initLayeredDataSourcePart(null, aRequest);
	}

	public CompositeDataSource(TickDataRequest cdr1, IDataProvider dp, UUID aId) {
		super(aId);
		_request = cdr1;
		_initLayeredDataSourcePart(aId, cdr1);
		this.fDp = dp;
		this._symbol = cdr1 != null ? cdr1.getSymbol() : null;
	}

	/**
	 * Just a convenience method for the Csv Composite data source
	 * 
	 * @param tick
	 * 
	 * @throws DFSException
	 */
	protected void _createDummyExpanderForCsv(int tick) throws DFSException {
		_expanders.clear();

		int gap1 = 0;
		int gap2 = 0;

		EGapFillingMethod method = EGapFillingMethod.PRICE_MULTIPLIER;
		double aXp = 0.25;
		double aDp = 0.25;

		/*
		 * if the tick data request is null I can have the possibility to
		 * request the normal data for this request.
		 */
		if (_request != null) {

			gap1 = _request.getRequests().get(0).getGap1();
			gap2 = _request.getRequests().get(0).getGap2();

			aXp = _request.getXp();
			aDp = _request.getDp();

			boolean isUseWindow = this._request.isGapFillingUsingWindow();

			if (isUseWindow) {
				method = EGapFillingMethod.UPPER_CACHE;
			} else {
				method = EGapFillingMethod.PRICE_MULTIPLIER;
			}
		}

		int dummyLayer = 0;
		CacheExpander ce = new CacheExpander(null, null, method, gap1, gap2,
				tick, _seed, aXp, aDp, false/* do not filter lonely ticks */,
				0, this, dummyLayer, true /* do not truncate the layers. */,
				false /* no real time */);
		_expanders.add(ce);
		try {
			ce.expand();
		} catch (MonitorCancelledException e) {
			e.printStackTrace();
			stop();
		}
	}

	/**
	 * this one is called whenever the composite data source has finished the
	 * start (warm up) so it will close or purge the old bars from the data
	 * source
	 * 
	 * @param forced
	 *            if true the end start will dispose of the bars even if the
	 *            request is database.
	 * 
	 * @throws DFSException
	 */
	// @Override
	protected void _endStart(boolean forced) throws DFSException {

		if (!_request.isRealTime() && !forced) {
			return; // nothing to do
		}
	}

	/**
	 * expands the expanders of this data source.
	 * 
	 * <P>
	 * The expanders should be already prepared.
	 * 
	 * @param aMonitor
	 *            unused *to be cancelled*
	 * 
	 * @throws DFSException
	 * @throws MonitorCancelledException
	 */
	void _expand(IProgressMonitor aMonitor) throws DFSException,
			MonitorCancelledException {
		/*
		 * Now the base expander (the daily) is in the last position and the
		 * real time (usually the range) in front. To warm up I simply have to
		 * expand the last (the daily). The expander will automatically call the
		 * other (upper) expander, in sequence.
		 * 
		 * When the tick data request is not merged then I have to expand from
		 * the range, instead.
		 */
		if (_request.isMerged()) {
			_expanders.get(_expanders.size() - 1).expand();
		} else {
			_expanders.get(0).expand();
		}

	}

	/**
	 * 
	 * @param aId
	 *            the identification of this data source (it has meaning only
	 *            for the GUI part)
	 * @param cdr1
	 */
	public void _initLayeredDataSourcePart(UUID aId, TickDataRequest cdr1) {
		setRequest(_request);
	}

	protected void _notifyStarting() {
		MfgSymbol symbol = this._request.getSymbol();
		_notifyStartingPars(symbol.getTick(), symbol.getScale());
	}

	protected void _notifyStartingPars(int aTick, int aScale) {
		NotifyOnStartingRunnable nosr = new NotifyOnStartingRunnable(aTick,
				aScale);
		for (LazyWriteArrayList<ITickListener> aList : _listeners) {
			aList.iterateCode(nosr);
		}

	}

	private void _sendPreKickHook(IProgressMonitor monitor) throws DFSException {
		preKickTheCanHook(monitor);
	}

	private EStartOutput _startPrivate(IProgressMonitor monitor)
			throws DFSException {
		// I send the message to all the data sources
		EStartOutput eso = EStartOutput.START_OK;
		try {
			eso = start_phase_one(monitor);
		} catch (DFSException e) {
			e.printStackTrace();
			return EStartOutput.START_KO;
		}
		if (eso != EStartOutput.START_OK) {
			debug_var(101029, "The layered data source ", fId, "is stopped");
			stop();
			return eso;
		}

		_notifyStarting();

		try {
			eso = start_phase_two(monitor);
		} catch (DFSException e) {
			e.printStackTrace();
			eso = EStartOutput.START_KO;
		}
		if (eso != EStartOutput.START_OK) {
			debug_var(736728, "The layered data source ", fId,
					"is stopped, I stop");
			stop();
			return eso;
		}

		return EStartOutput.START_OK;
	}

	/**
	 * @param monitor
	 */
	@SuppressWarnings("static-method")
	protected EStartOutput start_phase_two(IProgressMonitor monitor)
			throws DFSException {
		throw new DFSException("Need to override this");
	}

	/**
	 * @param monitor
	 */
	@SuppressWarnings("static-method")
	protected EStartOutput start_phase_one(IProgressMonitor monitor)
			throws DFSException {
		throw new DFSException("Need to override this");
	}

	@Override
	public String abortedReason() {
		return fAbortedReason;
	}

	/**
	 * Adds a layer tick listener for a certain layer.
	 * 
	 * @param _layer
	 * @param aTickListener
	 */
	@Override
	public void addTickListener(int _layer, ITickListener aTickListener) {
		_listeners[_layer].add(aTickListener);
	}

	@Override
	public void canceling() {
		U.debug_var(392943,
				"Composite data source asked to stop from outside ", this);

		synchronized (_currentMonitor) {
			_currentMonitor.setCanceled(true);
			_currentMonitor.notify();
		}

	}

	/**
	 * checks the data source if no new ticks have come. This means that the
	 * data source will tell the listeners that no tick has come.
	 * 
	 * <p>
	 * This method is called by the
	 * {@linkplain LayeredDataSource#kickTheCan(IProgressMonitor)} method.
	 * 
	 * @throws DFSException
	 * 
	 */
	// @Override
	protected void checkNoTick() throws DFSException {
		//
	}

	@Override
	public final void endOfExpansion(int aLayer)
			throws MonitorCancelledException {
		/*
		 * If I receive this message it means that I am not delayed. This is the
		 * real end of warmup.
		 */
		for (int i = 0; i < getLayersSize(); ++i) {
			notifyEndWarmUp(i, _currentMonitor);
		}

	}

	@Override
	public final boolean finalNotifiyTick(int aLayer, QueueTick aTick)
			throws MonitorCancelledException {
		if (_currentMonitor.isCanceled()) {
			throw new MonitorCancelledException();
		}
		NotifyNewTickRunnable nntr = new NotifyNewTickRunnable(aTick);
		_listeners[aLayer].iterateCode(nntr);
		return false;
	}

	@Override
	public IDelayControl getDelayControl() {
		if (_delayableListener == null) {
			return _delayControl;
		}
		return ((DelayableCacheExpanderListener) _delayableListener)
				.getDelayControl();
	}

	@Override
	public int getLayersSize() {
		return _request.getLayersSize();
	}

	@Override
	public final TickDataRequest getRequest() {
		return _request;
	}

	@Override
	public int getTick() {
		return _request.getTick();
	}

	@Override
	public boolean hasBeenAborted() {
		return this.hasBeenAborted;
	}

	@Override
	public void kickTheCan(IProgressMonitor aMonitor) throws DFSException {

		try {
			IProgressMonitor monitor = aMonitor;

			if (monitor == null) {
				monitor = new ProgressMonitorAdapter();
			}

			monitor.beginTask(
					"Giving Real time ticks for " + _request.getLayersSize()
							+ " layers of data", IProgressMonitor.UNKNOWN);

			/*
			 * This call is important because if the data source is not real
			 * time then every layer will start a fake kick the can loop which
			 * will collect the delayed ticks from the CacheExpander.
			 */
			_sendPreKickHook(monitor);

			for (;;) {

				synchronized (_listeners) {
					_listeners.wait(3000);
				}
				if (monitor.isCanceled()) {
					// stop();
					break;
				}

				checkNoTick();
			}
		} catch (InterruptedException e) {
			//
		} catch (DFSException e1) {
			e1.printStackTrace();
		} finally {
			notifyStopping();
			stop();
		}

	}

	@Override
	public final void notifyEndWarmUp(int aLayer, IProgressMonitor _monitor)
			throws MonitorCancelledException {
		_inWarmUp = false;
		NotifyEndOfWarmUp neowu = new NotifyEndOfWarmUp(_monitor);
		_listeners[aLayer].iterateCode(neowu);
		// for (LazyWriteArrayList<ITickListener> aList : _listeners) {
		// aList.iterateCode(neowu);
		// }
		if (_monitor.isCanceled()) {
			throw new MonitorCancelledException();
		}
	}

	public final void notifyQueueAlertDown(int aSize) {
		NotifyQueueAlert nqa = new NotifyQueueAlert(false, aSize);
		/*
		 * The queue alert interests only the real time layer, which by
		 * definition is layer zero
		 */
		_listeners[0].iterateCode(nqa);
	}

	public final void notifyQueueAlertUp(int aSize) {
		NotifyQueueAlert nqa = new NotifyQueueAlert(true, aSize);
		_listeners[0].iterateCode(nqa);
	}

	protected final void notifyStopping() {
		NotifyStopping ns = new NotifyStopping();
		for (LazyWriteArrayList<ITickListener> aList : _listeners) {
			aList.iterateCode(ns);
		}
	}

	/**
	 * This function will simply get the tick. The tick IS REAL and will pass it
	 * to the filters and fill the gaps machines before finalizing it.
	 * 
	 * @param tk1
	 *            the tick which is passed. This is REAL.
	 * 
	 * @param isReal
	 *            true if this tick is real (if it comes from warm up then it
	 *            could be not real...)
	 * @throws MonitorCancelledException
	 */
	@SuppressWarnings({ "static-method", "unused" })
	protected void notifyTickBeforeFilters(RealTick aTick)
			throws MonitorCancelledException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onVolumeUpdate(int aFakeTime, int aVolume)
			throws MonitorCancelledException {
		if (_currentMonitor.isCanceled()) {
			throw new MonitorCancelledException();
		}
		/*
		 * The volume update is only for the real time layer.
		 */
		VolumeUpdateRunnable vur = new VolumeUpdateRunnable(aFakeTime, aVolume);
		_listeners[REAL_TIME_LAYER].iterateCode(vur);

	}

	@Override
	public final void passNotFinalTick(int aLayer, QueueTick aTick)
			throws MonitorCancelledException {
		if (_currentMonitor.isCanceled()) {
			throw new MonitorCancelledException();
		}
		NotifyNotFinalTickRunnable nnftr = new NotifyNotFinalTickRunnable(aTick);
		_listeners[aLayer].iterateCode(nnftr);
	}

	/**
	 * This method will start the kick the can loop in another thread, if the
	 * request is not real time
	 * 
	 * @throws DFSException
	 */
	@Override
	protected void preKickTheCanHook(final IProgressMonitor monitor)
			throws DFSException {
		_currentMonitor = monitor;

		if (!this._request.isRealTime()) {
			_historicalTicksThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						_expand(monitor);
						/*
						 * At the end I end the start.
						 */
						_endStart(true);
					} catch (DFSException e) {
						e.printStackTrace();
					} catch (MonitorCancelledException e) {
						// this is normal, end of thread.
						U.debug_var(293893, "end normal of CDS " + this);
					}

				}
			});

			_historicalTicksThread.setName("Historical thread for cds "
					+ this.fId);
			_historicalTicksThread.start();
		}

	}

	/**
	 * notifies a tick as soon as it comes from the real time provider.
	 * 
	 * @param time
	 *            the time of this tick (physical as the fake time is sent
	 *            after)
	 * 
	 * @param price
	 *            the price for this tick.
	 * @throws MonitorCancelledException
	 */
	protected void preNotifyTick(long time, int price)
			throws MonitorCancelledException {
		RealTick tk = new RealTick(time, price, true);
		notifyTickBeforeFilters(tk);
	}

	@Override
	public void removeTickListener(int _layer, ITickListener aTickListener) {
		_listeners[_layer].remove(aTickListener);
	}

	@Override
	public void setDelayControl(IDelayControl control) {
		if (_delayableListener == null) {
			_delayControl = control;
			return;
		}

		((DelayableCacheExpanderListener) _delayableListener)
				.setDelayControl(control);

		control.getModel().setState(DataSpeedControlState.PLAYING);
	}

	/**
	 * This method is really only useful because we have to know the number of
	 * slots.
	 * 
	 * @param dataRequest1
	 *            the request. It has the layers from daily to range.
	 */
	@SuppressWarnings("unchecked")
	private void setRequest(TickDataRequest dataRequest1) {
		/*
		 * If the datarequest is null this comes from the csv file
		 */
		int numLayers = dataRequest1.getRequests().size();

		/*
		 * A request with zero layers is not supported.
		 */
		assert (numLayers > 0);

		_listeners = new LazyWriteArrayList[numLayers];

		for (int i = 0; i < _listeners.length; ++i) {
			_listeners[i] = new LazyWriteArrayList<>();
		}
	}

	/**
	 * to start a layered data source you have to start all the composite data
	 * sources which are inside it.
	 * 
	 * <p>
	 * The datasources are started in two phases, this because the normal start
	 * does also the join and in this case I have to wait until the data sources
	 * are joined, and the request is sequential (instead I want a parallel
	 * request).
	 * 
	 * @throws DFSException
	 */
	@Override
	public EStartOutput start(IProgressMonitor monitor) throws DFSException {

		_currentMonitor = monitor;

		try {
			return _startPrivate(monitor);
		} finally {
			_endStart(false);
		}
	}

	@Override
	public void stop() throws DFSException {

		debug_var(959155, "Stopped the data source ", this);

		/*
		 * The subscription stops automatically because it receives a
		 * MonitorCancelledException in its own thread, but I should cancel it!
		 */
		this._currentMonitor.setCanceled(true);

	}

}
