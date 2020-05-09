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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.mfg.mdb.runtime.MDBSession;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.common.QueueTick;
import com.mfg.common.TEAException;
import com.mfg.dfs.conn.DfsDataProvider;
import com.mfg.dm.TickAdapter;
import com.mfg.dm.TickDataRequest;
import com.mfg.dm.TickDataSource;
import com.mfg.dm.symbols.SymbolData2;
import com.mfg.logger.ILogger;
import com.mfg.logger.application.IAppLogger;
import com.mfg.logger.memory.MemoryLoggerManager;
import com.mfg.strategy.FinalStrategy;
import com.mfg.strategy.IStrategyFactory;
import com.mfg.strategy.IStrategyFactory.CreateStrategyArgs;
import com.mfg.strategy.IStrategySettings;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.configurations.SymbolConfigurationInfo;
import com.mfg.symbols.inputs.configurations.InputConfigurationInfo;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfigurationInfo;
import com.mfg.symbols.trading.ui.chart.TradingRecorder;
import com.mfg.tea.conn.IVirtualBroker;
import com.mfg.tradingdb.mdb.TradingMDBSession;
import com.mfg.utils.StepDefinition;
import com.mfg.utils.U;

/**
 * 
 * The trading pipe is a ITEAListener, because acts as the interface between the
 * portfolio and the TEA.
 * 
 * 
 * @author arian
 * 
 */
public class TradingPipe {

	private final class TradingPipeTickListener extends TickAdapter {

		public TradingPipeTickListener() {
			//
		}

		@Override
		public void onNewTick(QueueTick qt) {
			// if the pipe is stopped then the portfolio is null
			if (_portfolio != null) {
				_portfolio.newTick(qt);
			}
		}

		@Override
		public void onStarting(int tick, int scale) {

			try {
				_start(tick);
			} catch (Exception e) {
				_aborted = true;
				getInputPipe().getSymbolJob().cancel();
				U.debug_var(283471,
						"EXCEPTION IN onStarting... I will abort the input pipe: ");
				e.printStackTrace();
			}
			_portfolio.begin(tick);
		}

		@Override
		public void onStopping() {
			if (!_aborted) {
				_portfolio.stopTrading();
			}

		}

		@Override
		public void onWarmUpFinished() {
			_portfolio.endWarmUp();
		}
	}

	private final TradingConfiguration _configuration;
	private final SymbolJob<?> _symbolJob;
	TradingMDBSession _mdbSession;
	private final TradingConfigurationInfo _info;
	private final IAppLogger _logger;
	private final SymbolConfigurationInfo<? extends SymbolData2> _symbolInfo;
	PortfolioStrategy _portfolio;
	private IVirtualBroker _broker;
	private TradingRecorder _executionRecorder;
	private final InputPipe _inputPipe;
	private final SymbolConfiguration<? extends SymbolData2, SymbolConfigurationInfo<? extends SymbolData2>> _symbolConfig;
	private boolean _running;
	private boolean _closing;
	private TradingPipeTickListener _pipeListener;
	private int _tick;

	private StepDefinition _stepDef;

	/**
	 * The trading pipe can be aborted if the TEA cannot be started, in that
	 * case the broker does not exist and the portfolio is aborted too.
	 */
	boolean _aborted = false;

	@SuppressWarnings("unchecked")
	public TradingPipe(InputPipe aInputPipe, TradingConfiguration aConfiguration)
			throws IOException {
		super();
		this._configuration = aConfiguration;
		this._symbolJob = aInputPipe.getSymbolJob();
		this._inputPipe = aInputPipe;
		_running = true;

		_logger = _symbolJob.getLogger();
		_info = aConfiguration.getInfo();
		_symbolConfig = (SymbolConfiguration<? extends SymbolData2, SymbolConfigurationInfo<? extends SymbolData2>>) _symbolJob
				.getSymbolConfiguration();
		_symbolInfo = _symbolConfig.getInfo();

		createStrategy();
		createTradingDatabase();

	}

	private String _getShellId() {
		return this.getConfiguration().getUUID().toString();
	}

	private String _getTradedSymbol() {
		/*
		 * The traded symbol is determined by the configuration and it does not
		 * change.
		 */
		return this._inputPipe.getSymbolJob().getDataSource().getRequest()
				.getSymbol().getSymbol();
	}

	/**
	 * Starts the trading pipe.
	 * 
	 * <p>
	 * If the broker is a simulated broker then this will subscribe to the
	 * virtual symbol.
	 * 
	 * @param tick
	 * @throws TEAException
	 */
	void _start(int tick) throws TEAException {
		_tick = tick;
		createBroker(tick);
		/*
		 * For a virtual broker connected to a real broker the start may be a
		 * no-op, because it does not have to connect to a symbol (it gets the
		 * data from the market...).
		 */
		this._broker.start();
		// try {
		//
		// } catch (TEAException e) {
		// e.printStackTrace();
		// throw new RuntimeException(e);
		// }
	}

	/**
	 * @param family
	 * @return
	 */
	public boolean belongsTo(Object family) {
		return family.equals(_configuration)
				|| _configuration.getUUID() == family;
	}

	void close() throws IOException, TimeoutException {
		_running = false;
		_mdbSession.closeAndDelete();
	}

	@SuppressWarnings("boxing")
	private void createBroker(int tick2) throws TEAException {

		String virtualSymbol = this._inputPipe.getSymbolJob().getDataSource()
				.getDataSourceId();

		String tradedSymbol = _getTradedSymbol();

		boolean isRealTimeRequest = this._inputPipe.getSymbolJob()
				.getDataSource().getRequest().isRealTime();

		boolean paperTrading = _info.isDoPaperTrading();

		String shellId = _getShellId();

		TickDataRequest aRequest = this._inputPipe.getSymbolJob()
				.getDataSource().getRequest();

		// int tick =
		// inputPipe.getSymbolJob().getDataSource().getRequest()._symbol
		// .getTick();

		int tickValue = _inputPipe.getSymbolJob().getDataSource().getRequest()._symbol
				.getTickValue();

		U.debug_var(294285, "shell id is ", shellId, " tick ", tick2,
				" tick value ", tickValue);

		_broker = TEAGateway
				.instance(
						((DfsDataProvider) this._inputPipe.getSymbolJob()._dataProvider)
								.getDfs()).createVirtualBroker(virtualSymbol,
						tradedSymbol, _portfolio, isRealTimeRequest,
						paperTrading, shellId, aRequest, tick2, tickValue);

		_portfolio.setBroker(_broker);
	}

	private void createStrategy() {
		String factoryId = _info.getStrategyFactoryId();
		IStrategyFactory factory = SymbolsPlugin.getDefault()
				.getStrategyFactory(factoryId);
		Assert.isNotNull(factory);

		IStrategySettings settings = _info.getStrategySettings(factoryId);
		CreateStrategyArgs args = new CreateStrategyArgs(settings, _logger);
		FinalStrategy strategy = factory.createStrategy(args);

		_logger.logComment("Create strategy %s=%s.", factory.getName(),
				strategy);

		InputConfigurationInfo inputInfo = _inputPipe.getConfiguration()
				.getInfo();

		// String tradedSymbol = _getTradedSymbol();
		//
		// String shellId = _getShellId();

		_portfolio = new PortfolioStrategy(_symbolInfo.getSymbol()
				.getTickValue(), inputInfo.isUsingProbabilities(),
				inputInfo.getProbabilityName(), _inputPipe.getSymbolJob()
						.getDataSource(), _configuration);

		_portfolio.addStrategy(strategy);

		MemoryLoggerManager loggerManager = new MemoryLoggerManager(
				"Strategy Log - " + getConfiguration().getName(), true);
		ILogger tradingLogger = loggerManager.createLogger();

		_portfolio.setLogger(tradingLogger);
		/*
		 * The indicator for the strategy is only the range indicator.
		 */
		_portfolio.setIndicator(_inputPipe.getIndicator().getLayers().get(0));
		// Integer tickSize = symbolInfo.getSymbol().getTickSize();
		// logger.logComment("Set to portfolio initial tick size=%d.",
		// tickSize);

		_pipeListener = new TradingPipeTickListener();

		_inputPipe.getSymbolJob().getDataSource()
				.addTickListener(TickDataSource.REAL_TIME_LAYER, _pipeListener);
	}

	private void createTradingDatabase() throws IOException {
		String relPath = "ChartDatabases/" + _configuration.getName() + "-"
				+ _configuration.getUUID() + "-" + System.currentTimeMillis();
		_info.setDatabasePath(relPath);
		File root = new File(Platform.getInstanceLocation().getURL().getFile(),
				relPath);
		MDBSession.delete(root);
		Assert.isTrue(!root.exists());

		boolean temporal = true;
		_mdbSession = new TradingMDBSession(_configuration.getName(), root,
				SessionMode.READ_WRITE, temporal);
		_mdbSession.saveProperties();

		initRecorders();
	}

	public TradingConfiguration getConfiguration() {
		return _configuration;
	}

	/**
	 * @return the inputPipe
	 */
	public InputPipe getInputPipe() {
		return _inputPipe;
	}

	/**
	 * @return the mdbSession
	 */
	public TradingMDBSession getMdbSession() {
		return _mdbSession;
	}

	/**
	 * Return the strategy portfolio, null if the trading was stoppped.
	 * 
	 * @return the portfolio
	 */
	public PortfolioStrategy getPortfolio() {
		return _portfolio;
	}

	/**
	 * @return the symbolJob
	 */
	public SymbolJob<?> getSymbolJob() {
		return _symbolJob;
	}

	private void initRecorders() throws IOException {
		if (_executionRecorder != null) {
			_executionRecorder.close();
		}
		SymbolData2 symbol = getSymbolJob().getSymbolConfiguration().getInfo()
				.getSymbol();
		_executionRecorder = new TradingRecorder(getSymbolJob().getMdbSession()
				.getTimeMap(0), _mdbSession, _portfolio.getIndicator()
				.getChscalelevels(), _portfolio.getTick(),
				symbol.getTickValue(), _inputPipe.getIndicator().getParamBean()
						.isProbLinesPercentValueEnabled());
		_portfolio.addPositionListener(_executionRecorder);
		_portfolio.addProbabilitiesDealerListener(_executionRecorder);
	}

	public boolean isClosing() {
		return _closing;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return _running;
	}

	/**
	 * Used to restart a trading pipe that was stopped before. It will create a
	 * new strategy and broker based on the user configuration. You can start
	 * trading with a simulated broker, stop it, and start it again with a real
	 * broker, the only thing should remain in common is the trading database.
	 * TOD O: Something that we have to consider, is that we cannot restart a
	 * trading if yet it is executing an stop routine.
	 * 
	 * @throws IOException
	 * @throws TEAException
	 */
	public void restart() throws IOException {
		_running = true;
		_closing = false;

		// recreate the portfolio
		createStrategy();
		setTick(getSymbolJob().getStepDefinition());
		_tick = _stepDef.getStepInteger();

		_portfolio.setTick(_stepDef);
		_portfolio.begin(_tick);
		if (!_symbolJob.getDataSource().isInWarmUp()) {
			_portfolio.endWarmUp();
		}

		// recreate the broker
		try {
			createBroker(_tick);
			this._broker.start();
		} catch (TEAException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		// instead of recreate the db, we just reconnect the recorders
		initRecorders();

		SymbolJob.getManager().fireSymbolJobChange(_symbolJob, this,
				SymbolJobManager.TRADING_RESTARTED);
	}

	public void setClosing(boolean aClosing) {
		this._closing = aClosing;
	}

	public void setTick(StepDefinition stepDef) {
		_executionRecorder.setTick(stepDef);
		_stepDef = stepDef;
	}

	/**
	 * Stop broker and strategies. Keep the database alive until the symbol job
	 * is done. The strategy and broker are set to null after the stop.
	 * 
	 * @throws TEAException
	 */
	public void stop() throws TEAException {
		Assert.isTrue(_running);

		_running = false;
		_portfolio.stop();

		_inputPipe.getSymbolJob().getDataSource()
				.removeTickListener(0, _pipeListener);

		_portfolio.removePositionListener(_executionRecorder);
		_portfolio.removeProbabilitiesDealerListener(_executionRecorder);

		if (!_aborted) {
			_broker.stop();
			_broker = null;
			_logger.logComment("Traiding %s stopped by user",
					_configuration.getName());
		} else {
			_logger.logComment("Traiding %s aborted", _configuration.getName());
		}
		_portfolio = null;
		SymbolJob.getManager().fireSymbolJobChange(_symbolJob, this,
				SymbolJobManager.TRADING_STOPPED);
	}
}
