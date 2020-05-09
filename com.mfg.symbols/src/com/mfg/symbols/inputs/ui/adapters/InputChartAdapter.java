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
package com.mfg.symbols.inputs.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.mfg.mdb.runtime.DBSynchronizer;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.commands.DataLayerAction;
import com.mfg.chart.model.ChannelModel_MDB;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.EmptyChartModel;
import com.mfg.chart.model.EmptyPriceModel;
import com.mfg.chart.model.IChannelModel;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IParallelRealTimeZZModel;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.model.ISyntheticModel;
import com.mfg.chart.model.ITemporalPricesModel;
import com.mfg.chart.model.ITradingModel;
import com.mfg.chart.model.PhysicalPriceModel_MDB;
import com.mfg.chart.model.PhysicalScaledIndicatorModel_MDB;
import com.mfg.chart.model.PriceModel_MDB;
import com.mfg.chart.model.ScaledIndicatorModel_MDB;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.views.ChartConfig;
import com.mfg.chart.ui.views.ChartContentAdapter;
import com.mfg.chart.ui.views.IChartContentAdapter;
import com.mfg.chart.ui.views.IChartView;
import com.mfg.common.QueueTick;
import com.mfg.dm.ITickListener;
import com.mfg.dm.TickAdapter;
import com.mfg.dm.TickDataSource;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.ui.chart.SyntheticModel;
import com.mfg.symbols.inputs.ui.views.ParallelRealTimeZZModel;
import com.mfg.symbols.inputs.ui.views.PhysicalParallelRealTimeZZModel;
import com.mfg.symbols.inputs.ui.views.PhysicalRealTimeZZModel;
import com.mfg.symbols.inputs.ui.views.PhysicalTrendLinesModel;
import com.mfg.symbols.inputs.ui.views.RealTimeChannelModel;
import com.mfg.symbols.inputs.ui.views.RealTimeZZModel;
import com.mfg.symbols.inputs.ui.views.TrendLinesModel;
import com.mfg.symbols.jobs.InputPipe;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.ui.chart.models.Channel2Model_MDB;
import com.mfg.symbols.ui.chart.models.PhysicalChannel2Model_MDB;
import com.mfg.symbols.ui.chart.models.PhysicalTemporalPricesModel;
import com.mfg.symbols.ui.chart.models.TemporalPricesModel;
import com.mfg.widget.arc.strategy.LayeredIndicator;

/**
 * @author arian
 * 
 */
public class InputChartAdapter extends ChartContentAdapter {

	private static final String[] RESET_COMMANDS = new String[] {
			"com.mfg.chart.commands.polylinesDrawPath",
			"com.mfg.chart.commands.parallelRtZZ" };
	TickDataSource _dataSource;
	InputPipe _inputPipe;
	private final InputConfiguration configuration;
	ITemporalPricesModel _tempModel;
	private List<ITickListener> listeners;

	public InputChartAdapter(InputConfiguration aConfiguration) {
		this(aConfiguration, ChartType.INDICATOR);
	}

	public InputChartAdapter(InputConfiguration aConfiguration,
			ChartType chartType) {
		super(aConfiguration.getName(), chartType, true);
		this.configuration = aConfiguration;
	}

	@Override
	public void init(boolean usePhysicalTime, IChartContentAdapter oldAdapter) {
		listeners = new ArrayList<>();
		super.init(usePhysicalTime, oldAdapter);
	}

	/**
	 * @return
	 */
	private TickAdapter createTickListener(final int layer) {
		final TemporalPricesModel tempModel = (TemporalPricesModel) (_tempModel != null
				&& _tempModel instanceof TemporalPricesModel ? _tempModel
				: null);
		TickAdapter l = new TickAdapter() {
			@Override
			public void onStarting(final int tick, final int scale) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						Chart chart = getChart();
						chart.glChart.yTickSize = tick;
						chart.setTickScale(scale);
						chart.repaint();
					}
				});
			}

			@Override
			public void onNewTick(QueueTick qt) {
				if (tempModel != null) {
					tempModel.onNewTick(layer, qt);
				}
				handleNewTickToUpdateChart();
			}

			@Override
			public void onTemporaryTick(QueueTick qt) {
				if (tempModel != null) {
					tempModel.onTemporaryTick(layer, qt);
				}
				handleNewTickToUpdateChart();
			}

		};
		listeners.add(l);
		return l;
	}

	protected void handleNewTickToUpdateChart() {
		updateChartOnTick();
	}

	/**
	 * @param pipe
	 */
	protected void connectToJob(SymbolJob<?> job) {
		InputPipe pipe = job.getInputPipe(configuration);
		if (pipe != null) {
			int dataLayersCount = job.getDataLayersCount();

			ChartType type = getType();
			boolean isIndicator = type == ChartType.INDICATOR
					|| type == ChartType.TRADING;

			if (isIndicator) {
				_tempModel = isPhysicalTimeChart() ? new PhysicalTemporalPricesModel(
						dataLayersCount) : new TemporalPricesModel(
						job.getDataLayersCount());
			} else {
				_tempModel = ITemporalPricesModel.EMPTY;
			}

			_inputPipe = pipe;
			_dataSource = _inputPipe.getSymbolJob().getDataSource();

			TickDataSource ds = job.getDataSource();
			for (int i = 0; i < ds.getLayersSize(); i++) {
				ds.addTickListener(i, createTickListener(i));
			}

			IChartModel chartModel = createChartModel();

			_chart = createChart(getChartName(), chartModel, type,
					getChartContent());

			if (_tempModel != null && _tempModel instanceof TemporalPricesModel) {
				((TemporalPricesModel) _tempModel).setChart(_chart);
			}

			IChartModel model = _chart.getModel();
			if (model instanceof ChartModel_MDB) {
				DBSynchronizer synchro = ((ChartModel_MDB) model)
						.getPriceSession().getSynchronizer();
				synchro.operation(new Runnable() {

					@Override
					public void run() {
						getChartView().setChart(getChart());
					}
				});
			} else {
				getChartView().setChart(_chart);
			}
		}
	}

	public Object getChartContent() {
		return configuration;
	}

	@Override
	public void dispose(IChartView chartView) {
		for (ITickListener l : listeners) {
			if (_dataSource != null) {
				for (int i = 0; i < _dataSource.getLayersSize(); i++) {
					_dataSource.removeTickListener(i, l);
				}
			}
		}
		super.dispose(chartView);
	}

	/**
	 * @return
	 */
	protected IChartModel createChartModel() {
		final IndicatorMDBSession indicatorSession = _inputPipe.getMdbSession();
		if (_inputPipe.isRunning() && indicatorSession.isOpen()) {
			if (getType() == ChartType.SYNTHETIC) {
				SymbolJob<?> job = _inputPipe.getSymbolJob();
				boolean completed = job.isWarmupCompleted();
				if (completed) {
					return createSyntheticModel();
				}
			} else {
				return createIndicatorModel();
			}
		}
		return IChartModel.EMPTY;

	}

	private IChartModel createSyntheticModel() {
		final IndicatorMDBSession indidactorSession = _inputPipe
				.getMdbSession();
		final PriceMDBSession priceSession = _inputPipe.getSymbolJob()
				.getMdbSession();

		LayeredIndicator indicator = _inputPipe.getIndicator();

		final ISyntheticModel synthModel = new SyntheticModel(priceSession,
				indicator);

		return new EmptyChartModel() {

			private IPriceModel _priceModel = new EmptyPriceModel() {
				@Override
				public double getTickSize() {
					return priceSession.getTickSize();
				}
			};

			@Override
			public IPriceModel getPriceModel() {
				return _priceModel;
			}

			@Override
			public ISyntheticModel getSyntheticModel() {
				return synthModel;
			}

			@Override
			public long getToken() {
				return synthModel.getHigherZZScale() * synthModel.getZZSwings()
						+ indidactorSession.getModificatonToken();
			}
		};
	}

	private IChartModel createIndicatorModel() {
		final IndicatorMDBSession indicatorSession = _inputPipe.getMdbSession();
		PriceMDBSession priceSession = _inputPipe.getSymbolJob()
				.getMdbSession();

		final boolean physicalTimeChart = isPhysicalTimeChart();
		final PriceModel_MDB priceModel = physicalTimeChart ? new PhysicalPriceModel_MDB(
				priceSession) : new PriceModel_MDB(priceSession);

		ChartModel_MDB chartModel = new ChartModel_MDB(priceSession,
				indicatorSession, null, priceModel, _tempModel,
				ITradingModel.EMPTY, physicalTimeChart) {

			@Override
			protected PhysicalScaledIndicatorModel_MDB createPhysicalIndicatorModel(
					IndicatorMDBSession indicatorSession2) {
				return new PhysicalScaledIndicatorModel_MDB(indicatorSession,
						this) {
					@Override
					protected ChannelModel_MDB createChannelModel(int level) {
						return new PhysicalChannel2Model_MDB(indicatorSession,
								level, getChartModel());
					}
				};
			}

			@Override
			protected ScaledIndicatorModel_MDB createIndicatorModel(
					IndicatorMDBSession aIndicatorSession) {
				return new ScaledIndicatorModel_MDB(aIndicatorSession, this) {
					@Override
					protected IChannelModel createChannelModel(int level) {

						return new Channel2Model_MDB(indicatorSession, level,
								getChartModel());
					}
				};
			}
		};

		LayeredIndicator layeredIndicator = _inputPipe.getIndicator();

		int levelsCount = layeredIndicator.getLayers().get(0).getParamBean()
				.getIndicatorNumberOfScales();

		for (int level = 1; level <= levelsCount; level++) {
			ScaledIndicatorModel_MDB scaleModel = chartModel
					.getScaledIndicatorModel();

			// realtime channels
			RealTimeChannelModel rtChannelModel = new RealTimeChannelModel(
					layeredIndicator, level, chartModel);
			scaleModel.setRealTimeChannelModel(level, rtChannelModel);

			// realtime ZZ
			RealTimeZZModel rtZZModel = physicalTimeChart ? new PhysicalRealTimeZZModel(
					layeredIndicator, level, chartModel) : new RealTimeZZModel(
					layeredIndicator, level, chartModel);
			scaleModel.setRealTimeZZModel(level, rtZZModel);

			// parallel RT ZZ
			IParallelRealTimeZZModel parallelRtZZModel = physicalTimeChart ? new PhysicalParallelRealTimeZZModel(
					layeredIndicator, level, chartModel)
					: new ParallelRealTimeZZModel(layeredIndicator, level,
							chartModel);
			scaleModel.setParallelRealTimeZZModel(level, parallelRtZZModel);

			// trend lines
			TrendLinesModel trendLinesModel = physicalTimeChart ? new PhysicalTrendLinesModel(
					layeredIndicator, chartModel) : new TrendLinesModel(
					layeredIndicator, chartModel);

			scaleModel.setTrendLinesModel(trendLinesModel);

		}
		return chartModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.views.ChartContentAdapter#configure(com.mfg.chart.ui
	 * .views.ChartView)
	 */
	@Override
	public void configure(IChartView chartView, ChartConfig chartConfig) {
		super.configure(chartView, chartConfig);

		IStorageObject symbol = PersistInterfacesPlugin.getDefault().findById(
				configuration.getInfo().getSymbolId());
		chartView.setPartName(configuration.getName() + " / "
				+ symbol.getName());

		Job[] jobs = Job.getJobManager().find(configuration);

		if (jobs.length > 0) {
			Assert.isTrue(jobs.length == 1,
					"Should be there only one symbol job per input.");
			SymbolJob<?> symbolJob = (SymbolJob<?>) jobs[0];

			connectToJob(symbolJob);
		}

		// update the Draw Path command status
		ICommandService service = (ICommandService) chartView.getViewSite()
				.getService(ICommandService.class);
		for (String id : RESET_COMMANDS) {
			Command cmd = service.getCommand(id);
			State s = new State();
			s.setValue(Boolean.FALSE);
			cmd.addState(RegistryToggleState.STATE_ID, s);
		}
	}

	public InputConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	protected void fillToolbar(IToolBarManager manager) {
		super.fillToolbar(manager);

		addActions(manager, DataLayerAction.createActions(this));
	}

	@Override
	public void fillMenuBar(IMenuManager menu) {
		super.fillMenuBar(menu);
		boolean empty = _chart.getModel() == IChartModel.EMPTY;
		ChartType type = _chart.getType();
		if (!empty && type == ChartType.INDICATOR) {
			menu.add(new Separator());
			menu.add(new Action("Synthetic Chart") {
				@Override
				public void run() {
					getChartView().setContent(
							new SyntheticInput(getConfiguration()));
				}
			});
		}
	}
}
