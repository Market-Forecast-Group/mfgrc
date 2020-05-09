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
package com.mfg.symbols.trading.ui.adapters;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.commands.ChartAction;
import com.mfg.chart.commands.SelectToolAction;
import com.mfg.chart.layers.TradingLayer;
import com.mfg.chart.layers.TradingLayer.TradingSettings;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.PhysicalTradingModel_MDB;
import com.mfg.chart.model.TradingModel_MDB;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.interactive.TrendLinesTool;
import com.mfg.chart.ui.views.ChartConfig;
import com.mfg.chart.ui.views.ChartContentAdapter;
import com.mfg.chart.ui.views.IChartContentAdapter;
import com.mfg.chart.ui.views.IChartView;
import com.mfg.dm.symbols.SymbolData2;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.ui.adapters.InputChartAdapter;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.TradingPipe;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.old.PendingOrdersModel;
import com.mfg.symbols.trading.persistence.TradingStorage;
import com.mfg.symbols.trading.ui.actions.ChangeConfigurationSetAction;
import com.mfg.symbols.ui.ConfigurationSetsManager;
import com.mfg.utils.ImageUtils;
import com.mfg.utils.collections.TimeMap;

/**
 * @author arian
 * 
 */
public class TradingChartAdapater extends InputChartAdapter {

	private final class ShowClosedTradesAction extends ChartAction {
		public ShowClosedTradesAction(ChartContentAdapter adapter) {
			super(adapter, "com.mfg.symbols.commands.showClosedTrades",
					ImageUtils.getBundledImageDescriptor("com.mfg.symbols",
							"icons/trading-tool.png"), IAction.AS_CHECK_BOX);
			setChecked(_chart.getTradingLayer().getSettings().showClosedPosition);
		}

		@Override
		public void run() {
			TradingSettings settings = _chart.getTradingLayer().getSettings();
			settings.showClosedPosition = !settings.showClosedPosition;
			setChecked(settings.showClosedPosition);
		}
	}

	private final class SelectTradeToolAction extends SelectToolAction {

		public SelectTradeToolAction(ChartContentAdapter adapter) {
			super(adapter, "com.mfg.symbols.commands.tradeTool", ImageUtils
					.getBundledImageDescriptor(ChartPlugin.PLUGIN_ID,
							"icons/order-tool.png"),
					"com.mfg.strategy.manual.ui.chart.tools.TradingChartTool",
					true);
		}

	}

	private static final String MEMENTO_HAS_EQUITY = "tradingChartAdapter.hasEquity";
	private final TradingConfiguration tradingConfiguration;
	private TradingPipe tradingPipe;
	private SymbolJob<?> job;
	private PropertyChangeListener _tradingSetListener;
	private boolean _swapToEquityOnConfigure;

	/**
	 * @param configuration
	 * @param chartType
	 */
	public TradingChartAdapater(TradingConfiguration configuration,
			ChartType chartType) {
		super(SymbolsPlugin.getDefault().getInputsStorage()
				.findById(configuration.getInfo().getInputConfiguratioId()),
				chartType);
		this.tradingConfiguration = configuration;
	}

	public TradingChartAdapater(TradingConfiguration configuration) {
		this(configuration, ChartType.TRADING);

		_tradingSetListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() == getTradingConfiguration()) {
					updateViewIcon();
				}
			}
		};
		getTradingStoragePropertySupport().addPropertyChangeListener(
				TradingStorage.PROP_CONFIGURATION_SET, _tradingSetListener);
	}

	void updateViewIcon() {
		SymbolsPlugin.getDefault().getSetsManager();
		Image img = ConfigurationSetsManager.getImage(tradingConfiguration
				.getInfo().getConfigurationSet());
		getChartView().setTitleImage(img);
	}

	@Override
	public void configure(IChartView chartView, ChartConfig chartConfig) {
		super.configure(chartView, chartConfig);
		configureName(chartView);
		updateViewIcon();

		if (_swapToEquityOnConfigure && getType() != ChartType.EQUITY) {
			swapTradingAndEquity(chartView);
		}
	}

	@Override
	protected void fillToolbar(IToolBarManager manager) {
		super.fillToolbar(manager);
		boolean empty = _chart.getModel() == IChartModel.EMPTY;
		if (!empty) {
			manager.add(new Separator());

			if (!getType().hasEquity()) {
				addActions(manager, new ShowClosedTradesAction(this),
						new SelectTradeToolAction(this));
			}

			manager.add(new ChangeConfigurationSetAction(
					new ChartViewToITradingViewAdapter(getChartView())));
		}
	}

	@Override
	public void initState(IMemento memento) {
		super.initState(memento);
		Boolean b = memento.getBoolean(MEMENTO_HAS_EQUITY);
		_swapToEquityOnConfigure = b != null && b.booleanValue();
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putBoolean(MEMENTO_HAS_EQUITY, getType().hasEquity());
	}

	@Override
	public void init(boolean usePhysicalTime, IChartContentAdapter oldAdapter) {
		super.init(usePhysicalTime, oldAdapter);
		if (oldAdapter != null) {
			_swapToEquityOnConfigure = oldAdapter.getType().hasEquity();
		}
	}

	private void configureName(IChartView chartView) {
		InputConfiguration input = SymbolsPlugin
				.getDefault()
				.getInputsStorage()
				.findById(
						tradingConfiguration.getInfo().getInputConfiguratioId());
		IStorageObject symbol = PersistInterfacesPlugin.getDefault().findById(
				input.getInfo().getSymbolId());
		String name = symbol.getName() + " / " + input.getName() + " / "
				+ tradingConfiguration.getName();
		chartView
				.setPartName(name + (getType().hasEquity() ? " (Equity)" : ""));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.inputs.ui.adapters.InputChartAdapter#dispose(com.mfg.
	 * chart.ui.views.ChartView)
	 */
	@Override
	public void dispose(IChartView chartView) {
		ChartViewToITradingViewAdapter.setChartViewConfigurationSet(chartView,
				null);

		getTradingStoragePropertySupport().removePropertyChangeListener(
				TradingStorage.PROP_CONFIGURATION_SET, _tradingSetListener);

		super.dispose(chartView);
	}

	private static PropertyChangeSupport getTradingStoragePropertySupport() {
		return SymbolsPlugin.getDefault().getTradingStorage()
				.getPropertySupport();
	}

	@Override
	public String getChartName() {
		return tradingConfiguration.getName();
	}

	public TradingConfiguration getTradingConfiguration() {
		return tradingConfiguration;
	}

	@Override
	public Object getChartContent() {
		return tradingConfiguration;
	}

	@Override
	protected void connectToJob(SymbolJob<?> aJob) {
		Chart chart = null;
		for (TradingPipe pipe : aJob.getTradingPipes()) {
			if (pipe.getConfiguration() == tradingConfiguration
					&& !pipe.isClosing()) {
				tradingPipe = pipe;
				this.job = aJob;
				super.connectToJob(aJob);
				TradingLayer executionLayer = _chart.getTradingLayer();
				// the equity adapter does not have execution layer.
				if (executionLayer != null) {
					executionLayer.getSettings().showClosedPosition = false;
				}
				chart = _chart;
				break;
			}
		}
		// if there is not chart, create a new one
		if (chart == null) {
			chart = new Chart(getChartName(), IChartModel.EMPTY, getType(),
					tradingConfiguration);
			getChartView().setChart(chart);
		}
		// update the line tool price value and currency
		TrendLinesTool tool = chart.getTool(TrendLinesTool.class);
		SymbolData2 symbolData = aJob.getSymbolConfiguration().getInfo()
				.getSymbol();
		double priceValue = symbolData.getRealTickValue();
		tool.setPriceValue(priceValue);
		tool.setCurrency(symbolData.getCurrency());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.inputs.ui.adapters.InputChartAdapter#createChartModel()
	 */
	@Override
	protected IChartModel createChartModel() {
		IChartModel model = super.createChartModel();
		if (model instanceof ChartModel_MDB) {
			ChartModel_MDB mdbModel = (ChartModel_MDB) model;
			if (tradingPipe != null) {

				TradingModel_MDB executionModel;
				if (isPhysicalTimeChart()) {
					TimeMap timeMap = mdbModel.getPriceSession().getTimeMap(0);
					executionModel = new PhysicalTradingModel_MDB(timeMap,
							tradingPipe.getMdbSession(),
							mdbModel.getPriceModel());
				} else {
					executionModel = new TradingModel_MDB(
							tradingPipe.getMdbSession(),
							mdbModel.getPriceModel());
				}
				mdbModel.setExecutionModel(executionModel);
				mdbModel.setPendingOrdersModel(new PendingOrdersModel(
						tradingPipe.getPortfolio()));
			}
		}
		return model;
	}

	public void swapTradingAndEquity(IChartView view) {
		ChartType newType;
		if (getType() == ChartType.TRADING) {
			newType = ChartType.EQUITY;
		} else {
			newType = ChartType.TRADING;
		}
		setType(newType);
		configureName(view);

		if (job != null) {
			dispose(view);
			connectToJob(job);
			getAnimator().start();
		}

		deactivateContexts(getContextService());
		activateContexts(getContextService());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.ChartContentAdapter#getLastTime()
	 */
	@Override
	protected long getLastTime() {
		return getType() == ChartType.EQUITY ? _chart.getModel()
				.getTradingModel().getEquityUpperTime() : super.getLastTime();
	}
}
