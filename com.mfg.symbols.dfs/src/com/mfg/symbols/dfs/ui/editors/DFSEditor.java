package com.mfg.symbols.dfs.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormPage;

import com.mfg.common.DFSException;
import com.mfg.connector.dfs.DFSHistoricalDataInfo;
import com.mfg.connector.dfs.DFSHistoricalDataInfo.RequestMode;
import com.mfg.connector.dfs.IDFSRunnable;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.DataSpeedModel;
import com.mfg.dm.speedControl.SpeedComposite3;
import com.mfg.symbols.dfs.DFSSymbolsPlugin;
import com.mfg.symbols.dfs.actions.OpenDFSSimulatorControlAction;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.jobs.DFSJob;
import com.mfg.symbols.dfs.persistence.DFSStorage;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.ui.editors.CreateCommandsRunnable;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobConfig;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.ui.editors.AbstractSymbolEditor;

public class DFSEditor extends AbstractSymbolEditor {

	private final class DFSCreateCommandsRunnable extends
			CreateCommandsRunnable<DFSConfiguration> {
		private final TradingConfiguration _tradingConfiguration;
		private final InputConfiguration _inputConfiguration;
		SpeedComposite3 dataSpeedComp;

		DFSCreateCommandsRunnable(AbstractSymbolEditor aEditor,
				InputConfiguration inputConfiguration,
				TradingConfiguration tradingConfiguration, Composite aParent,
				TradingConfiguration tradingConfiguration2,
				InputConfiguration inputConfiguration2) {
			super(aEditor, inputConfiguration, tradingConfiguration, aParent);
			_tradingConfiguration = tradingConfiguration2;
			_inputConfiguration = inputConfiguration2;
		}

		@Override
		protected void updateCommandsWidgetsForJob(SymbolJob<?> job,
				boolean runningJobInThisTab) {
			DataSpeedModel model = runningJobInThisTab ? ((DFSJob) job)
					.getDataSpeedModel() : DataSpeedModel.DISABLED;
			dataSpeedComp.setModel(model);
			DFSHistoricalDataInfo info = (DFSHistoricalDataInfo) job
					.getHistoricalDataInfoToRun();
			boolean enabledSpeedButtons = info.getRequestMode() == RequestMode.DATABASE;
			dataSpeedComp.setEnableSpeedButtons(enabledSpeedButtons);
		}

		@Override
		protected void updateCommandsWidgetsToInitialState() {
			dataSpeedComp.setModel(DataSpeedModel.INITIAL_MODEL);
		}

		@Override
		protected Job createJob(boolean aStartTrading) {
			try {
				// TODO: we say that always that you start the job from the
				// input or trading tab, it will start the trading.
				boolean startTrading = aStartTrading
						|| _tradingConfiguration != null;
				TradingConfiguration[] tradings;
				if (startTrading) {
					tradings = _tradingConfiguration == null ? null
							: new TradingConfiguration[] { _tradingConfiguration };
				} else {
					tradings = new TradingConfiguration[0];
				}
				InputConfiguration[] inputs = _inputConfiguration == null ? null
						: new InputConfiguration[] { _inputConfiguration };
				SymbolJobConfig<DFSConfiguration> config = new SymbolJobConfig<>(
						getConfiguration(), inputs, tradings,
						_inputConfiguration == null ? getConfiguration()
								: _inputConfiguration);

				DFSJob job = new DFSJob(config);

				job.getHistoricalDataInfoToRun();
				return job;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		@Override
		protected boolean isJobClass(Job job) {
			return job instanceof DFSJob;
		}

		@Override
		protected void createCommandsWidgets(Composite aParent) {
			// CSVCommandsComposite comp = new CSVCommandsComposite(parent,
			// SWT.NONE);
			// comp.setConfiguration(getConfiguration());
			// dataSpeedComp = comp.getSpeedComposite();
			dataSpeedComp = new SpeedComposite3(aParent, SWT.NONE);
			dataSpeedComp.getPlayButton().addSelectionListener(
					new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							// check if it is the case it click the play
							// button to pause the job and not to start it.
							if (dataSpeedComp.getModel().getState() == DataSpeedControlState.INITIAL) {
								// boolean startTrading = getConfiguration()
								// .getInfo().isStartTrading();
								startJob(false);
							}
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							// Documenting empty method to avoid warning.
						}
					});
			dataSpeedComp.setStopVeto(new Runnable() {

				@Override
				public void run() {
					if (SymbolJob.isConfigurationTrading(getConfiguration())) {
						throw new IllegalArgumentException(
								"There is a trading session open.  You need to close it before stopping data");
					}
				}
			});

		}

		public SpeedComposite3 getDataSpeedComp() {
			return dataSpeedComp;
		}
	}

	public static final String EDITOR_ID = "com.mfg.symbols.dfs.ui.editor";
	public static final String MATURITY_STATS_KEY = "maturityStatsNode";

	private DFSEditorPage mainPage;
	protected IDFS _dfs;

	@Override
	protected void createPages() {
		DFSStorage storage = DFSSymbolsPlugin.getDefault().getDFSStorage();
		storage.runWhenReady(new IDFSRunnable() {

			@Override
			public void run(IDFS dfs) throws DFSException {
				_dfs = dfs;
				Display.getDefault().syncExec(DFSEditor.this::addDFSPages);
			}

			@Override
			public void notReady() {
				addBusyPage();
			}
		});
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		DFSHistoricalDataInfo info = (DFSHistoricalDataInfo) getConfiguration()
				.getInfo().getHistoricalDataInfo();
		info.setUsed(true);
	}

	@Override
	protected void addPages() {
		try {
			mainPage = new DFSEditorPage(this, getMainPageId(),
					getConfiguration().getName());
			addPage(mainPage);
			super.addPages();
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public DFSConfiguration getConfiguration() {
		return (DFSConfiguration) getEditorInput().getStorageObject();
	}

	@Override
	protected FormPage createInputPage(final InputConfiguration inputConfig) {
		DFSHistoricalDataInfo info = (DFSHistoricalDataInfo) inputConfig
				.getInfo().getHistoricalDataInfo();
		info.setUsed(true);
		return new DFSInputEditorPage(this, inputConfig.getUUID().toString(),
				inputConfig.getName()) {
			@Override
			public InputConfiguration getConfiguration() {
				return inputConfig;
			}
		};
	}

	@Override
	public void addExtraActions(IToolBarManager manager) {
		Assert.isNotNull(_dfs);
		if (_dfs.getController() != null) {
			manager.add(new OpenDFSSimulatorControlAction());
		}
	}

	@Override
	public Object createCommandsSection(final Composite parent,
			final InputConfiguration inputConfiguration,
			final TradingConfiguration tradingConfiguration) {
		DFSCreateCommandsRunnable create = new DFSCreateCommandsRunnable(this,
				inputConfiguration, tradingConfiguration, parent,
				tradingConfiguration, inputConfiguration);
		create.run();
		return create.getDataSpeedComp();
	}

	void addDFSPages() {
		if (getPageCount() > 0) {
			removePage(0);
		}
		addPages();
	}

	void addBusyPage() {
		try {
			addPage(new WaitingDFSPage(DFSEditor.this, "dfsWaiting",
					"Waiting DFS"));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public MaturityStats getMaturityStats() {
		return getStorage().lookupMaturityStats(getConfiguration());
	}

	private static DFSStorage getStorage() {
		return DFSSymbolsPlugin.getDefault().getDFSStorage();
	}

}
