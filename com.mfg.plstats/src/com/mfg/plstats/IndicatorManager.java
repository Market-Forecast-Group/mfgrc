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

package com.mfg.plstats;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mfg.mdb.runtime.SessionMode;

import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChartModel;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.interfaces.indicator.IIndicator;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.plstats.charts.IndicatorChartView;
import com.mfg.plstats.jobs.CreateIndicatorJob;
import com.mfg.plstats.jobs.ExportIndicatorJob;
import com.mfg.plstats.jobs.FreezeIndicatorJob;
import com.mfg.plstats.jobs.ProbabilitiesJob;
import com.mfg.plstats.persist.PLStatsIndicatorConfiguration;
import com.mfg.plstats.ui.actions.CreateIndexAction;
import com.mfg.plstats.ui.actions.CreateIndicatorAction;
import com.mfg.plstats.ui.actions.CreateProbabilitiesAction;
import com.mfg.plstats.ui.actions.ExportIndicatorAction;
import com.mfg.plstats.ui.actions.IIndicatorActions;
import com.mfg.plstats.ui.actions.LoadIndicatorAction;
import com.mfg.plstats.ui.actions.OpenIndicatorChartAction;
import com.mfg.utils.io.IO;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.arc.strategy.FrozenWidget;

/**
 * @author arian
 * 
 */
public class IndicatorManager {

	final Map<IIndicatorConfiguration, CreateIndicatorJob> configCreateJobMap = new HashMap<>();
	final Map<IIndicatorConfiguration, FreezeIndicatorJob> configFreezeJobMap = new HashMap<>();
	private final Map<IIndicatorConfiguration, FrozenWidget> configFrozenMap = new HashMap<>();
	private final Map<IIndicatorConfiguration, SessionInfo> configSessionMap = new HashMap<>();
	final Map<IIndicatorConfiguration, ProbabilitiesJob> configProbJobMap = new HashMap<>();
	final Map<IIndicatorConfiguration, ExportIndicatorJob> exportJobMap = new HashMap<>();

	private List<IndicatorChartView> chartViews = new ArrayList<>();

	class IndicatorActions implements IIndicatorActions {
		private final CreateIndicatorAction createIndicatorAction;
		private final LoadIndicatorAction loadIndicatorAction;
		private final OpenIndicatorChartAction openIndicatorChartAction;
		private final JobChangeAdapter jobListener;
		final CreateProbabilitiesAction createProbabilitiesAction;
		private final ExportIndicatorAction exportIndicatorAction;
		private final CreateIndexAction createIndexAction;

		public IndicatorActions(final IIndicatorConfiguration config) {
			createIndicatorAction = new CreateIndicatorAction(config);
			createProbabilitiesAction = new CreateProbabilitiesAction(config);
			loadIndicatorAction = new LoadIndicatorAction(config);
			createIndexAction = new CreateIndexAction(config);
			openIndicatorChartAction = new OpenIndicatorChartAction(config);
			exportIndicatorAction = new ExportIndicatorAction(config);

			updateActions(config);

			config.getProbabilitiesSettings().addPropertyChangeListener(
					new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent aEvt) {
							createProbabilitiesAction.updateText();
						}
					});

			jobListener = new JobChangeAdapter() {

				private void update(IJobChangeEvent event) {
					if (acceptEvent(event)) {
						updateActions(config);
					}
				}

				@Override
				public void aboutToRun(IJobChangeEvent event) {
					update(event);
				}

				@Override
				public void running(IJobChangeEvent event) {
					update(event);
				}

				@Override
				public void done(IJobChangeEvent event) {
					update(event);
				}

				private boolean acceptEvent(IJobChangeEvent event) {
					Job job = event.getJob();
					if (job instanceof CreateIndicatorJob) {
						return ((CreateIndicatorJob) job).getConfiguration() == config;
					}

					if (job instanceof FreezeIndicatorJob) {
						return ((FreezeIndicatorJob) job).getConfiguration() == config;
					}

					if (job instanceof ProbabilitiesJob) {
						return ((ProbabilitiesJob) job).getConfiguration() == config;
					}

					return false;
				}

			};
			Job.getJobManager().addJobChangeListener(jobListener);
		}

		void updateActions(final IIndicatorConfiguration config) {
			CreateIndicatorJob indicatorJob = configCreateJobMap.get(config);

			createIndicatorAction.setEnabled(true);

			File dbDir = getIndicatorDatabaseDir(config);

			boolean dbExists = dbDir.exists();
			createIndicatorAction
					.setText(dbExists ? "Create Indicator (Update)"
							: "Create Indicator");
			loadIndicatorAction.setEnabled(dbExists);
			openIndicatorChartAction.setEnabled(dbExists);

			if (indicatorJob != null) {
				if (indicatorJob.getState() != Job.NONE) {
					createIndicatorAction.setEnabled(false);
				}
				boolean createIndicator_doneOk = indicatorJob.getResult() != null
						&& indicatorJob.getResult().isOK();
				loadIndicatorAction.setEnabled(createIndicator_doneOk);
				createIndicatorAction.setText("Create Indicator (Update)");
			}
			FreezeIndicatorJob freezeJob = configFreezeJobMap.get(config);

			if (freezeJob != null && freezeJob.getResult() != null
					&& freezeJob.getResult().isOK()) {
				loadIndicatorAction.setText("Load Indicator (Update)");
				createProbabilitiesAction.updateEnabled();
				exportIndicatorAction.updateEnabled();
				createIndexAction.updateEnabled();
			}
			ProbabilitiesJob probJob = configProbJobMap.get(config);
			if (probJob != null) {
				if (probJob.getResult() != null && probJob.getResult().isOK()
						&& probJob.getResult().getCode() != IStatus.CANCEL) {
					createProbabilitiesAction.updateText();
					CreateProbabilitiesAction.whendone();
				}
			}
			ExportIndicatorJob exportJob = exportJobMap.get(config);
			if (exportJob != null) {
				if (exportJob.getResult() != null
						&& exportJob.getResult().isOK()
						&& exportJob.getResult().getCode() != IStatus.CANCEL) {
					exportIndicatorAction.updateText();
					exportIndicatorAction.whendone();
				}
			}

		}

		@Override
		public Action getCreateIndicatorAction() {
			return createIndicatorAction;
		}

		@Override
		public Action getCreateProbabilitiesAction() {
			return createProbabilitiesAction;
		}

		@Override
		public Action getExportIndicatorAction() {
			return exportIndicatorAction;
		}

		@Override
		public Action getLoadIndicatorAction() {
			return loadIndicatorAction;
		}

		@Override
		public Action getIndexAction() {
			return createIndexAction;
		}

		@Override
		public Action getOpenIndicatorChartAction() {
			return openIndicatorChartAction;
		}

		@Override
		public void dispose() {
			Job.getJobManager().removeJobChangeListener(jobListener);
		}
	}

	public static File getIndicatorDatabaseDir(
			IIndicatorConfiguration configuration) {
		String path = PersistInterfacesPlugin.getDefault()
				.getCurrentWorkspacePath();
		return new File(path + "/PreLearningDatabases", configuration.getUUID()
				.toString());
	}

	public IIndicatorActions createIndicatorActions(
			IIndicatorConfiguration configuration) {
		return new IndicatorActions(configuration);
	}

	public IIndicator getFrozenIndicator(IIndicatorConfiguration configuration) {
		FrozenWidget frozen = configFrozenMap.get(configuration);
		return frozen;
	}

	public void setFrozenIndicator(IIndicatorConfiguration configuration,
			FrozenWidget frozenIndicagtor) {
		configFrozenMap.put(configuration, frozenIndicagtor);
	}

	public CreateIndicatorJob startCreateIndicatorJob(
			final IIndicatorConfiguration configuration) {
		final CreateIndicatorJob createJob = new CreateIndicatorJob(
				configuration, this);
		configCreateJobMap.put(configuration, createJob);
		createJob.schedule();

		return createJob;
	}

	public void startLoadIndicatorJob(
			final IIndicatorConfiguration configuration) {
		SessionInfo session = getOrCreateMDBSession(configuration);

		if (session != null) {
			final FreezeIndicatorJob job = new FreezeIndicatorJob(
					configuration, this);
			configFreezeJobMap.put(configuration, job);
			job.schedule();
		}
	}

	/**
	 * Create a new MDB session. If is needed the database is created.
	 * 
	 * @param configuration
	 * @param dbRoot
	 * @return
	 * @throws IOException
	 */
	public SessionInfo createNewSession(
			final IIndicatorConfiguration configuration, File dbRoot) {
		try {
			PriceMDBSession priceSession = new PriceMDBSession("PriceSession-"
					+ configuration.getName(), new File(dbRoot, "prices"));
			IndicatorMDBSession indicatorSession = new IndicatorMDBSession(
					"IndicatorSession-" + configuration.getName(), new File(
							dbRoot, "indicator"), SessionMode.READ_WRITE);
			priceSession.setDataLayersCount(1);
			priceSession.setDataLayerScales(new int[] { 0 });
			SessionInfo info = new SessionInfo(priceSession, indicatorSession);
			configSessionMap.put(configuration, info);
			return info;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get or create a new MDB session. Returns null if there is not a database.
	 * 
	 * @param configuration
	 * @return
	 */
	public SessionInfo getOrCreateMDBSession(
			IIndicatorConfiguration configuration) {
		SessionInfo info = getCreatedMDBSession(configuration);

		if (info == null) {
			File dbDir = getIndicatorDatabaseDir(configuration);
			if (dbDir.exists()) {
				info = createNewSession(configuration, dbDir);
			}
		}

		return info;
	}

	/**
	 * Get a created-already MDB session associated to a configuration. Maybe
	 * returns null.
	 * 
	 * @param configuration
	 * @return
	 */
	public SessionInfo getCreatedMDBSession(
			IIndicatorConfiguration configuration) {
		SessionInfo info = configSessionMap.get(configuration);
		return info;
	}

	public ProbabilitiesJob startCreateProbabilitiesJob(
			final IIndicatorConfiguration configuration) {
		ProbabilitiesJob probsJob = new ProbabilitiesJob(configuration);
		configProbJobMap.put(configuration, probsJob);
		probsJob.schedule();
		return probsJob;
	}

	public ExportIndicatorJob startExportIndicatorJob(
			final IIndicatorConfiguration configuration) {
		ExportIndicatorJob probsJob = new ExportIndicatorJob(configuration);
		exportJobMap.put(configuration, probsJob);
		probsJob.schedule();
		return probsJob;
	}

	/**
	 * @param aConfiguration
	 *            Used in inner classes
	 */
	public static void startCreateIndexJob(
			IIndicatorConfiguration aConfiguration) {
		System.out.println("Implement the indexing");
	}

	public List<IndicatorChartView> getChartViews() {
		return chartViews;
	}

	/**
	 * @param configuration
	 */
	public IndicatorChartView openIndicatorChart(
			IIndicatorConfiguration configuration, final boolean openNew) {
		IndicatorChartView chartView;
		try {
			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			// openNew = openNew || chartViews.isEmpty();
			if (openNew || chartViews.isEmpty()) {
				chartView = (IndicatorChartView) activePage.showView(
						IndicatorChartView.VIEW_ID,
						Long.toString(System.currentTimeMillis()),
						IWorkbenchPage.VIEW_CREATE);
			} else {
				chartView = chartViews.get(0);
			}
			openIndicatorChart(chartView, configuration);
			return chartView;
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param chartView
	 * @param configuration
	 */
	public static void openIndicatorChart(IndicatorChartView chartView,
			IIndicatorConfiguration configuration) {
		chartView.setConfiguration(configuration);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.activate(chartView);
	}

	/**
	 * @param configuration
	 *            Used in inner classes.
	 */
	public void closeIndicatorChartsFor(IIndicatorConfiguration configuration) {
		ArrayList<IndicatorChartView> list = new ArrayList<>(getChartViews());
		for (IndicatorChartView view : list) {
			view.setConfiguration(null);
		}
	}

	/**
	 * @param configuration
	 * @return
	 */
	public IChartModel createModel(IIndicatorConfiguration configuration) {
		if (configuration != null) {
			SessionInfo info = getOrCreateMDBSession(configuration);

			File dbDir = getIndicatorDatabaseDir(configuration);

			if (info == null && dbDir.exists()) {
				info = createNewSession(configuration, dbDir);
			}

			if (info != null) {
				return new ChartModel_MDB(info.getPriceSession(),
						info.getIndicatorSession(), null);
			}
		}

		return IChartModel.EMPTY;
	}

	/**
	 * @param chartView
	 */
	public void addChartView(IndicatorChartView chartView) {
		ArrayList<IndicatorChartView> list = new ArrayList<>(chartViews);
		list.add(chartView);
		chartViews = list;
	}

	/**
	 * @param indicatorChartView
	 */
	public void removeChartView(IndicatorChartView indicatorChartView) {
		chartViews.remove(indicatorChartView);
	}

	public boolean deleteIndicator(IIndicatorConfiguration configuration) {
		// --- Enrique's code:
		WidgetPlugin.getDefault().getProbabilitiesManager()
				.getDistributionsStorate().remove(configuration);
		// ---

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		CreateIndicatorJob job = configCreateJobMap.get(configuration);

		if (job != null && job.getState() == Job.RUNNING) {
			MessageDialog.openInformation(shell, "Delete Indicator",
					"You cannot delete an in-progress indicator.");
			return false;
		}
		boolean deleted = false;
		SessionInfo session = getCreatedMDBSession(configuration);

		if (session == null) {
			File dbDir = getIndicatorDatabaseDir(configuration);
			try {
				IO.deleteFile(dbDir);
				deleted = true;
			} catch (IOException e) {
				e.printStackTrace();
				MessageDialog.openError(shell, "Delete Indicator",
						"Cannot delete the database: \n" + e.getMessage());
			}
		} else {
			try {
				closeIndicatorChartsFor(configuration);
				session.getPriceSession().closeAndDelete();
				session.getIndicatorSession().closeAndDelete();
				configSessionMap.remove(configuration);
				deleted = true;
			} catch (IOException | TimeoutException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		if (deleted) {
			configCreateJobMap.remove(configuration);
			configFreezeJobMap.remove(configuration);
			configFrozenMap.remove(configuration);
			try {
				PLStatsPlugin.getDefault().getIndicatorStorage()
						.remove((PLStatsIndicatorConfiguration) configuration);
			} catch (RemoveException e) {
				e.printStackTrace();
			}
		}
		return deleted;
	}

	public void cancelIndicatorJob(CreateIndicatorJob job) {
		final IIndicatorConfiguration configuration = job.getConfiguration();
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				closeIndicatorChartsFor(configuration);
			}
		});

		try {
			job.getSession().getPriceSession().closeAndDelete();
			job.getSession().getIndicatorSession().closeAndDelete();
			configSessionMap.remove(configuration);
			configCreateJobMap.remove(configuration);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}

	}

}
