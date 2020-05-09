/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.symbols.jobs;

import static com.mfg.utils.Utils.debug_var;
import static java.lang.System.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.mfg.mdb.runtime.DBSynchronizer;
import org.mfg.mdb.runtime.MDBSession;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.common.DFSException;
import com.mfg.common.QueueTick;
import com.mfg.common.TEAException;
import com.mfg.dm.EStartOutput;
import com.mfg.dm.IDataProvider;
import com.mfg.dm.TickAdapter;
import com.mfg.dm.TickDataSource;
import com.mfg.dm.symbols.HistoricalDataInfo;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDB.Appender;
import com.mfg.inputdb.prices.mdb.PriceMDB.Record;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.interfaces.configurations.BaseConfiguration;
import com.mfg.logger.LoggerPlugin;
import com.mfg.logger.application.IAppLogger;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.Utils;
import com.mfg.utils.collections.TimeMap;
import com.mfg.utils.jobs.MFGJob;
import com.mfg.utils.lic.LicenseUtil;
import com.mfg.widget.arc.strategy.LayeredIndicator;
import com.mfg.widget.arc.strategy.LayeredIndicator.ELayeredStatus;

/**
 * @author arian
 * 
 */
public abstract class SymbolJob<T extends SymbolConfiguration<?, ?>> extends
		MFGJob {

	public static boolean canRunConfiguration(Object configuration) {
		boolean notRunning = !isConfigurationRunning(configuration);
		if (notRunning) {
			// In case of inputs, they should be started at the same time
			// the symbol, so, if the symbol is running, then the input cannot
			// be started.
			if (configuration instanceof InputConfiguration) {
				return canRunConfiguration(PersistInterfacesPlugin.getDefault()
						.findById(
								((InputConfiguration) configuration).getInfo()
										.getSymbolId()));
			} else if (configuration instanceof TradingConfiguration) {
				InputConfiguration input = SymbolsPlugin
						.getDefault()
						.getInputsStorage()
						.findById(
								((TradingConfiguration) configuration)
										.getInfo().getInputConfiguratioId());
				return isConfigurationRunning(input)
						|| !isConfigurationRunning(PersistInterfacesPlugin
								.getDefault().findById(
										input.getInfo().getSymbolId()));
			}
		}
		return notRunning;
	}

	/**
	 * @return the instance
	 */
	public static SymbolJobManager getManager() {
		if (_instance == null) {
			_instance = new SymbolJobManager();
		}
		return _instance;
	}

	public static TradingPipe getRunningTradingPipe(
			TradingConfiguration configuration) {
		Job[] jobs = Job.getJobManager().find(configuration);
		for (Job job : jobs) {
			if (job instanceof SymbolJob<?>) {
				SymbolJob<?> symbolJob = (SymbolJob<?>) job;
				for (TradingPipe pipe : symbolJob.getTradingPipes()) {
					if (pipe.getConfiguration() == configuration
							&& pipe.isRunning()) {
						return pipe;
					}
				}
			}
		}
		return null;
	}

	/**
	 * @param pipe
	 * @return
	 */
	private static boolean hasLongOpenPositions(TradingPipe pipe) {
		return pipe.getPortfolio().getAccount().getLongStatistics()
				.getQuantity() != 0;
	}

	/**
	 * @param pipe
	 * @return
	 */
	private static boolean hasShortOpenPositions(TradingPipe pipe) {
		return pipe.getPortfolio().getAccount().getShortStatistics()
				.getQuantity() != 0;
	}

	public static boolean isConfigurationRunning(Object configuration) {
		Job[] jobs = Job.getJobManager().find(configuration);
		if (jobs.length == 0) {
			return false;
		}
		if (configuration instanceof TradingConfiguration) {
			SymbolJob<?> job = (SymbolJob<?>) jobs[0];
			TradingPipe pipe = job
					.getTradingPipe((TradingConfiguration) configuration);
			return pipe != null && pipe.isRunning();
		}
		return true;
	}

	/**
	 * Check if the there is any trading associated with this configuration.
	 * 
	 * @param configuration
	 * @return
	 */
	public static boolean isConfigurationTrading(Object configuration) {
		Job[] jobs = Job.getJobManager().find(configuration);
		if (jobs.length > 0) {
			SymbolJob<?> job = (SymbolJob<?>) jobs[0];
			for (TradingPipe pipe : job.getTradingPipes()) {
				if (pipe.isRunning()) {
					return true;
				}
			}
		}
		return false;
	}

	public static void runConfigurations(Collection<Object> list,
			Object startConfiguration) {
		runConfigurations(list, startConfiguration, false);
	}

	public static void runConfigurations(Collection<Object> list,
			Object startConfiguration, boolean fullSpeedWarmup) {
		if (!list.isEmpty()) {
			IAdapterManager manager1 = Platform.getAdapterManager();

			List<SymbolJobConfig<?>> configs = SymbolJobConfig
					.createFromConfigurations(list, startConfiguration);

			for (SymbolJobConfig<?> config : configs) {
				config.setRunWarmupFullSpeed(fullSpeedWarmup);
				if (isConfigurationRunning(config.getSymbol())) {
					// we cannot run a second symbol job, but we can run a
					// second trading
					for (InputConfiguration input : config.getInputsToRun()) {
						// to run a trading, it should be attached to a running
						// input
						Job[] jobs = Job.getJobManager().find(input);
						if (jobs.length == 1) {
							SymbolJob<?> job = (SymbolJob<?>) jobs[0];
							List<TradingConfiguration> tradings = config
									.getTradingsToRun(input);
							for (TradingConfiguration trading : tradings) {
								TradingPipe pipe = job.getTradingPipe(trading);
								try {
									if (pipe == null) {
										// create a new pipe and run it
										job.startNewTradingPipe(trading);

									} else {
										if (pipe.isRunning()) {
											// invalid pipe to run
										} else {
											pipe.restart();
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
									throw new RuntimeException(e);
								}
							}
						}
					}
				} else {
					ISymbolJobFactory factory = (ISymbolJobFactory) manager1
							.getAdapter(config.getSymbol(),
									ISymbolJobFactory.class);
					SymbolJob<?> job = factory.createSymbolJob(config);
					job.schedule();
				}
			}
		}
	}

	public static void stopConfiguration(Object configuration) {
		Job[] jobs = Job.getJobManager().find(configuration);
		if (configuration instanceof TradingConfiguration) {
			// stop the trading pipe
			for (Job j : jobs) {
				SymbolJob<?> job = (SymbolJob<?>) j;
				for (TradingPipe pipe : job.getTradingPipes()) {
					if (pipe.getConfiguration() == configuration) {
						if (pipe.isRunning()) {
							try {
								pipe.stop();
							} catch (TEAException e) {
								e.printStackTrace();
							}
						}
						break;
					}
				}
			}
		} else if (configuration instanceof InputConfiguration) {
			// stop the input pipe
			for (Job j : jobs) {
				SymbolJob<?> job = (SymbolJob<?>) j;
				for (InputPipe pipe : job.getInputPipes()) {
					if (pipe.getConfiguration() == configuration) {
						pipe.stop();
						break;
					}
				}
				if (job.getInputPipes().isEmpty()) {
					job.cancel();
				}
			}
		} else {
			// stop the job
			for (Job job : jobs) {
				job.cancel();
			}
		}
	}

	public static void stopConfigurationDataRequest(
			final Object configurationPar) {
		Object configuration = configurationPar;
		if (configuration instanceof TradingConfiguration) {
			configuration = SymbolsPlugin
					.getDefault()
					.getInputsStorage()
					.findById(
							((TradingConfiguration) configuration).getInfo()
									.getInputConfiguratioId());
		}
		Job[] jobs = Job.getJobManager().find(configuration);
		// stop the job
		for (Job job : jobs) {
			job.cancel();
		}
	}

	/**
	 * @param list
	 */
	public static void stopConfigurations(List<Object> list) {
		Set<Object> tradings = new HashSet<>();
		for (Object obj : list) {
			if (obj instanceof TradingConfiguration) {
				tradings.add(obj);
			}
		}

		boolean deny = false;

		for (Object obj : list) {
			if (!(obj instanceof TradingConfiguration)) {
				Job[] jobs = Job.getJobManager().find(obj);
				if (jobs.length > 0) {
					SymbolJob<?> job = (SymbolJob<?>) jobs[0];
					for (TradingPipe pipe : job.getTradingPipes()) {
						if (!tradings.contains(pipe.getConfiguration())
								&& pipe.isRunning()
								&& (hasLongOpenPositions(pipe) || hasShortOpenPositions(pipe))) {
							deny = true;
							break;
						}
					}
				}
			}
		}
		if (deny) {
			MessageDialog
					.openInformation(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell(), "Stop",
							"There is a trading session open.  You need to close it before stopping data");

		} else {
			for (Object obj : list) {
				stopConfiguration(obj);
			}
		}
	}

	protected PriceMDBSession _mdbSession;

	protected IDataProvider _dataProvider;

	private PriceMDB[] _symbolPricesMDB;

	private PriceMDB.Appender[] _symbolPricesAppenders;

	List<InputPipe> _inputPipes;

	private final IAppLogger _logger;

	List<TradingPipe> _tradingPipes;

	private final SymbolJobConfig<T> _jobConfig;

	protected int _dataLayersCount;

	private TickDataSource[] _dataSourceList;

	protected IProgressMonitor _monitor;

	private long _startTime;

	private boolean _warmupCompleted;

	protected int _warmedUpLayers;

	private StepDefinition _stepDefinition;

	private static SymbolJobManager _instance;

	public SymbolJob(SymbolJobConfig<T> jobConfig1) throws Exception {
		super("Request Data for " + jobConfig1.getSymbol().getName());

		String error = LicenseUtil.isValidLicense();
		if (error != null) {
			throw new LicenseKeyException(error);
		}

		this._jobConfig = jobConfig1;

		_logger = LoggerPlugin.getDefault().getAppLogger(
				"com.mfg.symbols.inputs.logger", getName());

		_warmupCompleted = false;

		_dataProvider = getDataProvider();

		initializeDataSource();
		createSession();
		connectTickDataSources();
		createInputPipes();
		createTradingPipes();
		connectWarmupListener();
	}

	public SymbolJobConfig<T> getJobConfig() {
		return _jobConfig;
	}

	@Override
	public boolean belongsTo(Object family) {
		if (family.equals(_jobConfig.getSymbol())
				|| family == _jobConfig.getSymbol().getUUID()) {
			return true;
		}
		for (InputPipe pipe : _inputPipes) {
			if (pipe.belongsTo(family)) {
				return true;
			}
		}
		for (TradingPipe pipe : _tradingPipes) {
			if (pipe.belongsTo(family)) {
				return true;
			}
		}
		return super.belongsTo(family);
	}

	private void createInputPipes() {
		_inputPipes = new ArrayList<>();
		InputConfiguration[] inputsToRun = _jobConfig.getInputsToRun();
		for (InputConfiguration inputToRun : inputsToRun) {
			try {
				InputPipe inputPipe = new InputPipe(this, inputToRun, _logger);
				_inputPipes.add(inputPipe);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void createSession() throws IOException, FileNotFoundException {
		String relPath = "ChartDatabases/" + getSymbolConfiguration().getName()
				+ "-" + getSymbolConfiguration().getUUID() + "-"
				+ System.currentTimeMillis();
		File dbRoot = new File(Platform.getInstanceLocation().getURL()
				.getFile(), relPath);
		getSymbolConfiguration().getInfo().setDatabasePath(relPath);
		MDBSession.delete(dbRoot);
		Assert.isTrue(!dbRoot.exists());

		_mdbSession = new PriceMDBSession(
				this._jobConfig.getSymbol().getName(), dbRoot,
				SessionMode.READ_WRITE, true, new DBSynchronizer());

		TickDataSource layeredDs = getDataSource();
		_dataLayersCount = layeredDs.getLayersSize();
		_dataSourceList = new TickDataSource[_dataLayersCount];
		_symbolPricesMDB = new PriceMDB[_dataLayersCount];
		_symbolPricesAppenders = new Appender[_dataLayersCount];

		for (int dataLayer = 0; dataLayer < _dataLayersCount; dataLayer++) {
			if (_mdbSession.getMode() != SessionMode.MEMORY) {
				new File(_mdbSession.getRoot(), "layer-" + dataLayer).mkdirs();
			}
			_symbolPricesMDB[dataLayer] = _mdbSession
					.connectTo_PriceMDB(dataLayer);
			_symbolPricesAppenders[dataLayer] = _symbolPricesMDB[dataLayer]
					.appender();
			_dataSourceList[dataLayer] = layeredDs;
		}

		_mdbSession.setDataLayersCount(_dataLayersCount);
		int[] scales = new int[_dataLayersCount];
		for (int layer = 0; layer < _dataLayersCount; layer++) {
			scales[layer] = getDataLayerScale(layer);
		}
		_mdbSession.setDataLayerScales(scales);
		_mdbSession.saveProperties();
	}

	private void createTradingPipes() throws IOException {
		_tradingPipes = new ArrayList<>();
		for (InputPipe inputPipe : _inputPipes) {
			List<TradingConfiguration> tradingsToRun = _jobConfig
					.getTradingsToRun(inputPipe.getConfiguration());
			synchronized (_tradingPipes) {
				for (TradingConfiguration trading : tradingsToRun) {
					TradingPipe pipe = new TradingPipe(inputPipe, trading);
					_tradingPipes.add(pipe);
				}
			}
		}
	}

	/**
	 * @throws IOException
	 */
	public void dispose() throws IOException {
		_mdbSession.getSynchronizer().close(new Runnable() {

			@Override
			public void run() {
				// out.println("*** Closing");
				try {
					for (TradingPipe pipe : _tradingPipes) {
						pipe.close();
					}
					for (InputPipe inputPipe : _inputPipes) {
						inputPipe.close();
					}

					_mdbSession.closeAndDelete();
				} catch (IOException | TimeoutException e) {
					e.printStackTrace();
				}
				// out.println("--- Closed");
			}
		});
	}

	/**
	 * Get the series scale:
	 * <ul>
	 * <li>0: Show all.</li>
	 * <li>1: Ignore prices.</li>
	 * <li>N: Ignore indicator scales below N.</li>
	 * </ul>
	 * 
	 * @param layer
	 * 
	 * @return the layer filter.
	 */
	@SuppressWarnings("static-method")
	// Used on inner classes.
	protected int getDataLayerScale(int layer) {
		// by default it returns 'Show All' filter.
		return 0;
	}

	/**
	 * @return the layersCount
	 */
	public int getDataLayersCount() {
		return _dataLayersCount;
	}

	public abstract IDataProvider getDataProvider();

	public abstract TickDataSource getDataSource();

	public HistoricalDataInfo getHistoricalDataInfoToRun() {
		BaseConfiguration<?> historicalDataContainer = _jobConfig
				.getStartConfiguration();
		if (historicalDataContainer instanceof TradingConfiguration) {
			historicalDataContainer = SymbolsPlugin
					.getDefault()
					.getInputsStorage()
					.findById(
							((TradingConfiguration) historicalDataContainer)
									.getInfo().getInputConfiguratioId());
		}

		HistoricalDataInfo historicalDataInfo = null;

		if (historicalDataContainer instanceof SymbolConfiguration) {
			historicalDataInfo = ((SymbolConfiguration<?, ?>) historicalDataContainer)
					.getInfo().getHistoricalDataInfo();
		} else if (historicalDataContainer instanceof InputConfiguration) {
			historicalDataInfo = ((InputConfiguration) historicalDataContainer)
					.getInfo().getHistoricalDataInfo();
		}
		Assert.isNotNull(historicalDataInfo);

		_logger.logComment(
				"Read historical data settings from %s",
				SymbolsPlugin.getDefault().getFullConfigurationName(
						historicalDataContainer));

		return historicalDataInfo;
	}

	/**
	 * @param input
	 * @return
	 */
	public InputPipe getInputPipe(InputConfiguration input) {
		for (InputPipe pipe : _inputPipes) {
			if (pipe.getConfiguration() == input) {
				return pipe;
			}
		}
		return null;
	}

	/**
	 * @return the inputPipes
	 */
	public List<InputPipe> getInputPipes() {
		return _inputPipes;
	}

	/**
	 * @return the logger
	 */
	public IAppLogger getLogger() {
		return _logger;
	}

	/**
	 * @return the mdbSession
	 */
	public PriceMDBSession getMdbSession() {
		return _mdbSession;
	}

	public IProgressMonitor getMonitor() {
		return _monitor;
	}

	/**
	 * @return the configuration
	 */
	public T getSymbolConfiguration() {
		return _jobConfig.getSymbol();
	}

	public TradingPipe getTradingPipe(TradingConfiguration trading) {
		for (TradingPipe pipe : _tradingPipes) {
			if (pipe.getConfiguration() == trading) {
				return pipe;
			}
		}
		return null;
	}

	/**
	 * @return the tradingPipes
	 */
	public List<TradingPipe> getTradingPipes() {
		return _tradingPipes;
	}

	/**
	 * Return the configuration if there is only one, else return null.
	 * 
	 * @return
	 */
	public InputConfiguration getUniqueInputConfiguration() {
		InputConfiguration[] inputs = _jobConfig.getInputs();
		return inputs != null && inputs.length == 1 ? inputs[0] : null;
	}

	/**
	 * Return the configuration if there is only one, else return null.
	 * 
	 * @return
	 */
	public TradingConfiguration getUniqueTradingConfiguration() {
		TradingConfiguration[] tradings = _jobConfig.getTradings();
		return tradings != null && tradings.length == 1 ? tradings[0] : null;
	}

	public StepDefinition getStepDefinition() {
		return _stepDefinition;
	}

	/**
	 * Custom symbol-jobs must override this method to create the custom data
	 * source.
	 * 
	 * @throws DFSException
	 */
	protected abstract void initializeDataSource() throws Exception;

	/**
	 * @param args
	 */
	@SuppressWarnings("static-method")
	// Used on inner classes.
	protected void log(String msg, Object... args) {
		out.println(msg);
	}

	protected void onDataSourceNewTick(IProgressMonitor monitor, QueueTick qt,
			int dataLayer) {
		if (!monitor.isCanceled()) {
			int scale = getDataLayerScale(dataLayer);
			if (scale == 0) {
				try {
					// save prices only if it is included in the series scale.
					Appender appender = _symbolPricesAppenders[dataLayer];
					Record r = new PriceMDB.Record();
					r.update(qt);
					appender.append_ref_unsafe(r);
					TimeMap timeMap = _mdbSession.getTimeMap(dataLayer);
					timeMap.put((int) r.time, qt.getPhysicalTime());
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}

	protected void onDataSourceVolumeUpdate(int dataLayer, int fakeTime,
			int volume) {
		PriceMDB mdb = _symbolPricesAppenders[dataLayer].getMDB();
		try {
			mdb.replace_volume(fakeTime, volume);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param dataLayer
	 */
	void onDataSourceStarted(IProgressMonitor monitor, int tick, int scale,
			int dataLayer) {
		if (!monitor.isCanceled()) {
			_mdbSession.setTickSize(tick);
			_mdbSession.setTickScale(scale);
			try {
				_mdbSession.saveProperties();
			} catch (Exception e) {
				e.printStackTrace();
				log("Error: " + e.getMessage());
				throw new RuntimeException(e);
			}
			_stepDefinition = new StepDefinition(scale, tick);

			for (TradingPipe pipe : _tradingPipes) {
				pipe.setTick(_stepDefinition);
				PortfolioStrategy portfolio = pipe.getPortfolio();
				portfolio.setTick(_stepDefinition);
				portfolio.begin(tick);
			}
		}
	}

	protected void onDataSourceWarmUpFinished(int dataLayer, long lastTime,
			boolean allLayersWarmedUp) {
		{
			long t = System.currentTimeMillis() - _startTime;
			Utils.debug_var(876655, "Warm up finished: "
					+ TimeUnit.MILLISECONDS.toSeconds(t) + " seconds");
		}

		_mdbSession.getStartRealtimes()[dataLayer] = Long.valueOf(lastTime);

		if (allLayersWarmedUp) {
			_warmupCompleted = true;
			for (InputPipe pipe : _inputPipes) {
				LayeredIndicator indicator = pipe.getIndicator();
				indicator.setStatus(ELayeredStatus.MERGE_INDICATORS, 0);
			}

			getManager().fireSymbolJobChange(SymbolJob.this, null,
					SymbolJobManager.WARMING_UP_FINISHED);
		}
	}

	public boolean isWarmupCompleted() {
		return _warmupCompleted;
	}

	void removeInputPipe(InputPipe inputPipe) {
		ArrayList<InputPipe> list = new ArrayList<>(_inputPipes);
		list.remove(inputPipe);
		_inputPipes = list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		_startTime = System.currentTimeMillis();
		_monitor = monitor;
		if (this.getThread() != null) {
			this.getThread().setName("JOB running thread for " + this);
		}

		for (TradingPipe pipe : getTradingPipes()) {
			getManager().fireSymbolJobChange(this, pipe,
					SymbolJobManager.TRADING_START);
		}

		IStatus status;
		try {
			if (_dataProvider.switchOn()) {
				status = startDataSource(monitor);
			} else {
				log("Cannot switch ON provider...");
				status = new Status(IStatus.ERROR, SymbolsPlugin.PLUGIN_ID,
						"Cannot switch ON provider...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getClass().getSimpleName() + ": "
					+ e.getMessage();
			log(message);
			status = new Status(IStatus.ERROR, SymbolsPlugin.PLUGIN_ID,
					message, e);
			return status;
		}

		for (TradingPipe pipe : getTradingPipes()) {
			pipe.setClosing(true);
		}

		for (TradingPipe pipe : getTradingPipes()) {
			try {
				if (pipe.isRunning()) {
					pipe.stop();
				}
			} catch (TEAException e) {
				e.printStackTrace();
				status = new Status(IStatus.ERROR, SymbolsPlugin.PLUGIN_ID,
						"Cannot stop the trading pipe.");
			}
		}

		for (InputPipe pipe : getInputPipes()) {
			pipe.stop();
		}

		return status;
	}

	private void connectTickDataSources() {
		for (int i = 0; i < _dataLayersCount; i++) {
			final int dataLayer = i;
			TickAdapter tickAdapter = new TickAdapter() {
				@Override
				public void onVolumeUpdate(int fakeTime, int volume) {
					onDataSourceVolumeUpdate(dataLayer, fakeTime, volume);
				}

				@Override
				public void onNewTick(QueueTick qt) {
					onDataSourceNewTick(_monitor, qt, dataLayer);
				}

				@Override
				public void onStarting(int tick, int scale) {
					onDataSourceStarted(_monitor, tick, scale, dataLayer);
				}

				@Override
				public void onTemporaryTick(QueueTick qt) {
					// TODO Auto-generated method stub
					super.onTemporaryTick(qt);
				}

			};
			TickDataSource ds = _dataSourceList[dataLayer];
			ds.addTickListener(dataLayer, tickAdapter);
		}
	}

	private void connectWarmupListener() {
		for (int i = 0; i < _dataLayersCount; i++) {
			final int dataLayer = i;
			TickAdapter tickAdapter = new TickAdapter() {
				long _lastTime = 0;

				@Override
				public void onNewTick(QueueTick qt) {
					_lastTime = qt.getFakeTime();
				}

				@Override
				public void onWarmUpFinished() {
					_warmedUpLayers++;
					boolean all = _warmedUpLayers == _dataLayersCount;
					out.println(all + " " + _warmedUpLayers + "/"
							+ _dataLayersCount);
					onDataSourceWarmUpFinished(dataLayer, _lastTime, all);
				}
			};
			TickDataSource ds = _dataSourceList[dataLayer];
			ds.addTickListener(dataLayer, tickAdapter);
		}
	}

	private IStatus startDataSource(IProgressMonitor monitor)
			throws DFSException {
		TickDataSource dataSource = getDataSource();
		IStatus status = Status.OK_STATUS;

		if (status.isOK()) {
			EStartOutput startResult = dataSource.start(monitor);

			String errDetail = "";
			if (startResult == EStartOutput.START_OK) {
				/*
				 * Here the job is suspended until the monitor is cancelled.
				 */
				dataSource.kickTheCan(monitor);
			} else {
				if (!dataSource.hasBeenAborted()) {
					debug_var(269168, "Failed to start the data source");
					log("Failed to start the data source");
				} else {
					log("This data source has been aborted");
					debug_var(432096, "This data source has been aborted");
					startResult = EStartOutput.START_KO;
				}
				errDetail = dataSource.abortedReason();
			}

			boolean switchOFF_OK = true;

			_dataProvider.switchOff();

			if (switchOFF_OK && startResult != EStartOutput.START_KO) {
				if (startResult == EStartOutput.START_INTERRUPTED_BY_USER) {
					log("The start was interrupted by the user!");
					debug_var(219921, "The start was interrupted by the user!");
				}
				status = Status.OK_STATUS;
			} else if (switchOFF_OK && startResult == EStartOutput.START_KO) {
				log("Cannot get starting data... aborted.");

				// I have aborted the job, but maybe the abort has been
				// requested by the user
				if (errDetail.compareTo("normal stopping") == 0) {
					status = Status.OK_STATUS;
				} else {
					status = new Status(IStatus.ERROR, SymbolsPlugin.PLUGIN_ID,
							"Cannot get data. Reason: " + errDetail);
				}
			} else {
				log("Generic error in starting data provider...");
				status = new Status(IStatus.ERROR, SymbolsPlugin.PLUGIN_ID,
						"Generic error in starting data provider... see log");
			}
		}
		return status;
	}

	private void startNewTradingPipe(TradingConfiguration trading)
			throws IOException {
		for (InputPipe inputPipe : _inputPipes) {
			if (inputPipe.getConfiguration().getUUID()
					.equals(trading.getInfo().getInputConfiguratioId())) {
				TradingPipe tradingPipe = new TradingPipe(inputPipe, trading);
				synchronized (_tradingPipes) {
					_tradingPipes.add(tradingPipe);
				}
				tradingPipe.restart();
			}
		}
	}
}
