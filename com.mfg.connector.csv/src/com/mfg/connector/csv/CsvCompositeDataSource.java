package com.mfg.connector.csv;

import static com.mfg.utils.Utils.debug_var;
import static com.mfg.utils.Utils.sleep;
import static com.mfg.utils.Utils.waitForEverOn;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;

import com.mfg.common.CsvSymbol;
import com.mfg.common.DFSException;
import com.mfg.common.MfgSymbol;
import com.mfg.common.RealTick;
import com.mfg.common.Tick;
import com.mfg.connector.csv.reader.CsvFileReader;
import com.mfg.connector.csv.reader.CsvReaderParams;
import com.mfg.connector.csv.reader.DataSource1P;
import com.mfg.dm.CompositeDataSource;
import com.mfg.dm.DataProvider;
import com.mfg.dm.EStartOutput;
import com.mfg.dm.MonitorCancelledException;
import com.mfg.dm.TickDataRequest;
import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.utils.jobs.ProgressMonitorAdapter;

/**
 * This class has been created because a CsvData source is different, at least
 * for now it does not have a raw data source which is related to this, because
 * the file is already expanded (this should change, in any case).
 * 
 * @author Sergio
 */
public class CsvCompositeDataSource extends CompositeDataSource {

	// @Override
	// public boolean isInWarmUp() {
	// return _inWarmUp;
	// }

	/**
	 * This structure holds the notification... object. Every tick is filled
	 * with the tick and the boolean.
	 * 
	 * @author Pasqualino
	 * 
	 */
	private static class NotifyRecord {
		public Tick tickToNotify = null;

		@SuppressWarnings("unused")
		// to be deleted.
		public boolean isReal;

		public NotifyRecord() {

		}
	}

	private static final int DELTA_PRICES_FOR_STATS = 20000;

	private IDelayControl control = null;

	private DataSpeedControlState state = null;

	private DataSource1P dataSource;

	private final AtomicBoolean isReadyTick = new AtomicBoolean(false);

	public NotifyRecord notifyRecord = new NotifyRecord();

	private boolean stopped = false;

	private long startTime;

	private long lastTime;

	private Iterator<Tick> fCsvIterator;

	private int fNumPricesCsv;

	private int i_tick;

	private int _upperLimitForWarmUp;

	// 2

	/**
	 * Constructor used to build the data
	 * 
	 * @param dp
	 *            the data provider
	 */
	public CsvCompositeDataSource(MfgSymbol aSymbol) {
		super(new TickDataRequest(aSymbol));
		fNumPricesCsv = 0;
	}

	public CsvCompositeDataSource(final TickDataRequest cdr1,
			final DataProvider dp, UUID aId) {
		super(cdr1, dp, aId);

		fNumPricesCsv = cdr1.getNumberWarmupPrices();
	}

	private void _setComputedValues(int tick, int scale_from_the_source) {
		((CsvSymbol) _symbol).setComputedValues(tick, scale_from_the_source);
	}

	@Override
	public IDelayControl getDelayControl() {
		return control;
	}

	boolean isStopped() {
		return stopped
				|| control != null
				&& control.getModel().getState() == DataSpeedControlState.STOPPED;
	}

	@Override
	public void preKickTheCanHook(IProgressMonitor aMonitor)
			throws DFSException {

		IProgressMonitor monitor = aMonitor;

		debug_var(716411, "kick the CAN for csv: ", this._symbol);

		if (monitor == null) {
			monitor = new ProgressMonitorAdapter();
		}
		_currentMonitor = aMonitor;

		monitor.beginTask("giving " + dataSource.ticks.size() + " ticks for "
				+ this._symbol, dataSource.ticks.size());

		startTime = System.currentTimeMillis();
		lastTime = startTime;

		while (fCsvIterator.hasNext()) {
			Tick tk = fCsvIterator.next();
			if (!processCsvTick(tk, monitor)) {
				break;
			}
		}

		monitor.done();

		// now I have finished... I wait to stop
		for (;;) {
			sleep(500);
			if (isStopped() || monitor.isCanceled()) {
				if (control != null) {
					control.stop();
				}
				break;
			}
		}

		debug_var(983911, "Ending the ticks");
		notifyStopping();
		state = DataSpeedControlState.STOPPED;

		if (control != null) {
			control.stop();
		}

		debug_var(359295, "ending thread");

	}

	/**
	 * This is called by the data provider thread.
	 * 
	 * @throws MonitorCancelledException
	 */
	void notifyCsvTick() throws MonitorCancelledException {
		if (isReadyTick.get() == false) {
			return;
		}

		notifyTickBeforeFilters(new RealTick(notifyRecord.tickToNotify, true));

		isReadyTick.set(false);
		synchronized (isReadyTick) {
			isReadyTick.notify();
		}
	}

	/**
	 * In this method I will simulate the playing of the ticks
	 * 
	 * @throws DFSException
	 */
	public void playTicks(IProgressMonitor monitor) throws DFSException {
		try {
			_playTicks(monitor);
		} catch (MonitorCancelledException e) {
			e.printStackTrace();
			/*
			 * a simple return is sufficient, this exception is "safe".
			 */
		}
	}

	private void _playTicks(IProgressMonitor monitor) throws DFSException,
			MonitorCancelledException {
		_currentMonitor = monitor;
		if (isStopped() || dataSource == null) {
			debug_var(298654,
					"Starting the csv composite data source---------------");
			start(monitor);
		} else {
			if (_currentMonitor == null) {
				_currentMonitor = monitor;
			}
		}

		System.out.println("giving " + dataSource.ticks.size() + " ticks");
		if (monitor != null)
			monitor.beginTask("Giving ticks", dataSource.ticks.size());
		_inWarmUp = true;
		int numTick = 0;
		for (final Tick tk : dataSource.ticks) {

			if (monitor != null) {
				if (monitor.isCanceled()) {
					break;
				}
				monitor.worked(1);
			}

			try {
				preNotifyTick(tk.getPhysicalTime(), tk.getPrice());
			} catch (MonitorCancelledException e) {
				e.printStackTrace();
			}

			++numTick;
			if (_inWarmUp && (numTick > fNumPricesCsv)) {
				notifyEndWarmUp(0, monitor);
				_inWarmUp = false;
			}
		}

		if (_inWarmUp) {
			notifyEndWarmUp(0, monitor);
			_inWarmUp = false;
		}

		notifyStopping();
	}

	/**
	 * This method will process a tick either in the start method or in the
	 * kickthecan
	 * 
	 * @param tk
	 * @param i_tick
	 * @param monitor
	 * @return
	 * @throws DFSException
	 */
	private boolean processCsvTick(Tick tk, IProgressMonitor monitor)
			throws DFSException {
		if (i_tick % DELTA_PRICES_FOR_STATS == 0) {
			long now = System.currentTimeMillis();
			double delta_s = (now - startTime) / 1000.0;
			double delta_p = (now - lastTime) / 1000.0;
			debug_var(
					291825,
					this._symbol.getSymbol().substring(0,
							Math.min(10, this._symbol.getSymbol().length())),
					" : given ", Integer.valueOf(i_tick), " prices in ",
					Double.valueOf(delta_s), " secs avg ",
					Double.valueOf(i_tick / delta_s), " p/sec. Last batch in ",
					Double.valueOf(delta_p), " secs, avg ",
					Double.valueOf(DELTA_PRICES_FOR_STATS / delta_p));
			lastTime = now;
		}

		monitor.worked(1);

		final boolean is_real = true; // the csv ticks is always real.
		if (control == null) {
			sleep(500);
		} else {
			control.delay(monitor, i_tick);
			state = control.getModel().getState();
			if (state == DataSpeedControlState.STOPPED) {
				return false;
			}
		}

		notifyRecord.tickToNotify = tk;
		notifyRecord.isReal = is_real;

		final CsvDataProvider cdp = (CsvDataProvider) fDp;
		isReadyTick.set(true);

		synchronized (cdp.lock) {
			cdp.lock.notify();
		}

		synchronized (isReadyTick) {
			for (;;) {
				/*
				 * If this is false it means that the dp thread has consumed the
				 * tick.
				 */
				if (isReadyTick.get() == false) {
					break;
				}
				waitForEverOn(isReadyTick);
			}
		}

		if (monitor.isCanceled()) {
			debug_var(192525, "Requesting to stop by the user!");
			stop();
		}

		if (isStopped()) {
			debug_var(259611, "Stopping. Kick the can");
			return false; // no more
		}

		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.connector.csv.IDelayedDataRequest#setDelayControl(com.mfg.connector
	 * .csv.IDelayControl)
	 * 
	 * the control cannot be null!
	 */
	@Override
	public void setDelayControl(final IDelayControl control1) {

		debug_var(296169, "this ", Integer.valueOf(hashCode()),
				" setDelayControl ", Integer.valueOf(control1.hashCode()));
		this.control = control1;
		if (!stopped) {
			control1.getModel().setState(DataSpeedControlState.PLAYING);
		}

	}

	@Override
	public EStartOutput start_phase_one(IProgressMonitor monitor) {
		_currentMonitor = monitor;

		final CsvReaderParams crp = new CsvReaderParams();

		crp.csv_file_name = _symbol.getSymbol();
		int aTick;

		_upperLimitForWarmUp = Integer.MAX_VALUE;

		debug_var(291294, "NO RANDOM DATA SOURCE ", crp.csv_file_name);
		dataSource = new DataSource1P();
		dataSource = CsvFileReader.read_csv_file(crp, dataSource, false);

		debug_var(259616, "The computed tick is ",
				Integer.valueOf(dataSource.dsc.tick), " the scale is ",
				Integer.valueOf(dataSource.dsc.scale_from_the_source));
		aTick = dataSource.dsc.tick;
		_setComputedValues(dataSource.dsc.tick,
				dataSource.dsc.scale_from_the_source);

		fCsvIterator = this.dataSource.ticks.iterator();

		_upperLimitForWarmUp = Math.min(this.dataSource.ticks.size(),
				fNumPricesCsv);

		try {
			_createDummyExpanderForCsv(aTick);
		} catch (DFSException e) {
			e.printStackTrace();
			return EStartOutput.START_KO;
		}
		return EStartOutput.START_OK;
	}

	@Override
	public EStartOutput start_phase_two(IProgressMonitor monitor)
			throws DFSException {

		if (monitor != null) {
			monitor.beginTask("Feeding ticks", fNumPricesCsv);
		}

		i_tick = -1;
		for (int i = 0; i < _upperLimitForWarmUp; ++i) {
			Tick tk = fCsvIterator.next();
			if (!processCsvTick(tk, monitor)) {
				return EStartOutput.START_INTERRUPTED_BY_USER;
			}
		}

		if (fNumPricesCsv != 0) {
			try {
				notifyEndWarmUp(0, monitor);
			} catch (MonitorCancelledException e) {
				return EStartOutput.START_INTERRUPTED_BY_USER;
			}
			_inWarmUp = false;
		}

		return EStartOutput.START_OK;

	}

	@Override
	public void stop() throws DFSException {
		debug_var(281439, "Stop the CSV data source!");
		stopped = true;

		super.stop();

		synchronized (this) {
			notify();
		}
	}

}
