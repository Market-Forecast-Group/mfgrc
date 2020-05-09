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
package com.mfg.symbols.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Display;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.commands.DataLayerAction;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.model.ITradingModel;
import com.mfg.chart.model.PhysicalPriceModel_MDB;
import com.mfg.chart.model.PriceModel_MDB;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.views.ChartContentAdapter;
import com.mfg.chart.ui.views.IChartContentAdapter;
import com.mfg.chart.ui.views.IChartView;
import com.mfg.common.QueueTick;
import com.mfg.dm.ITickListener;
import com.mfg.dm.TickAdapter;
import com.mfg.dm.TickDataSource;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.symbols.configurations.SymbolConfiguration;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.ui.chart.models.PhysicalTemporalPricesModel;
import com.mfg.symbols.ui.chart.models.TemporalPricesModel;

/**
 * @author arian
 * 
 */
public class SymbolChartAdapter extends ChartContentAdapter {

	private SymbolJob<?> _job;
	protected TemporalPricesModel _tempTickModel;
	private final SymbolConfiguration<?, ?> _configuration;
	private List<ITickListener> _listeners;

	public SymbolChartAdapter(final SymbolConfiguration<?, ?> configuration) {
		super(configuration.getName(), ChartType.FINANCIAL, true);
		this._configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.ChartContentAdapter#init(boolean)
	 */
	@Override
	public void init(boolean usePhysicalTime, IChartContentAdapter oldAdapter) {
		super.init(usePhysicalTime, oldAdapter);
		Job[] jobs = Job.getJobManager().find(_configuration);
		Assert.isTrue(jobs.length < 2);
		if (jobs.length == 1) {
			_job = (SymbolJob<?>) jobs[0];

			_tempTickModel = createTemporalPricesModel();
			_listeners = new ArrayList<>();

			TickDataSource ds = _job.getDataSource();
			for (int layer = 0; layer < ds.getLayersSize(); layer++) {
				ds.addTickListener(layer, createTickListener(layer));
			}

			createChartFromJob();
			_tempTickModel.setChart(_chart);
			// does not set the chart to the view because the view is not
			// configured yet.
		}
	}

	@Override
	protected void fillToolbar(IToolBarManager manager) {
		super.fillToolbar(manager);
		addActions(manager, DataLayerAction.createActions(this));
	}

	/**
	 * @return
	 */
	private TickAdapter createTickListener(final int layer) {
		TickAdapter listener = new TickAdapter() {
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
			public void onWarmUpFinished() {
				// nothing
			}

			@Override
			public void onNewTick(QueueTick qt) {
				_tempTickModel.onNewTick(layer, qt);
				updateChartOnTick();
			}

			@Override
			public void onTemporaryTick(QueueTick qt) {
				_tempTickModel.onTemporaryTick(layer, qt);
				updateChartOnTick();
			}
		};
		_listeners.add(listener);
		return listener;
	}

	/**
	 * @return
	 */
	protected TemporalPricesModel createTemporalPricesModel() {
		int dataLayersCount = _job.getDataLayersCount();
		return isPhysicalTimeChart() ? new PhysicalTemporalPricesModel(
				dataLayersCount) : new TemporalPricesModel(
				_job.getDataLayersCount());
	}

	/**
	 * @return the job
	 */
	public SymbolJob<?> getJob() {
		return _job;
	}

	private void createChartFromJob() {
		PriceMDBSession priceSession = _job.getMdbSession();
		IChartModel model;
		if (priceSession.isOpen()) {
			IPriceModel priceModel = createPriceModel(priceSession);
			model = new ChartModel_MDB(priceSession, null, null, priceModel,
					_tempTickModel, ITradingModel.EMPTY, isPhysicalTimeChart());
		} else {
			model = IChartModel.EMPTY;
		}
		_chart = createChart(getChartName(), model, getType(), _configuration);
	}

	protected IPriceModel createPriceModel(PriceMDBSession session) {
		return isPhysicalTimeChart() ? new PhysicalPriceModel_MDB(session)
				: new PriceModel_MDB(session);
	}

	@Override
	public void dispose(IChartView chartView) {
		if (_job != null) {
			if (!_listeners.isEmpty()) {
				TickDataSource ds = _job.getDataSource();

				for (ITickListener l : _listeners) {
					TickDataSource ds2 = ds;
					for (int i = 0; i < ds2.getLayersSize(); i++) {
						ds2.removeTickListener(i, l);
					}
				}

				_job = null;
			}
		}
		super.dispose(chartView);
	}
}
