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
package com.mfg.symbols.csv.jobs;

import static java.lang.System.out;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.mfg.common.CsvSymbol;
import com.mfg.common.MfgSymbol;
import com.mfg.connector.csv.CSVHistoricalDataInfo;
import com.mfg.connector.csv.CSVHistoricalDataInfo.GapFillingType;
import com.mfg.connector.csv.preferences.CSVPrefsPage;
import com.mfg.connector.dfs.DFSPlugin;
import com.mfg.dm.DataProviderParams;
import com.mfg.dm.IDataProvider;
import com.mfg.dm.SlotParams;
import com.mfg.dm.TickDataRequest;
import com.mfg.dm.TickDataSource;
import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.DataSpeedModel;
import com.mfg.dm.speedControl.IDelayControl;
import com.mfg.dm.speedControl.IDelayedDataSource;
import com.mfg.dm.speedControl.ThreadDelayControl;
import com.mfg.symbols.csv.CSVSymbolPlugin;
import com.mfg.symbols.csv.configurations.CSVConfiguration;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobConfig;
import com.mfg.utils.U;

/**
 * @author arian
 * 
 */
public class CSVJob extends SymbolJob<CSVConfiguration> {

	// private static final int PRESTARTING_BARS = 201;
	private TickDataRequest dataRequest;
	private IDelayedDataSource dataSource;
	DataSpeedModel _dataSpeedModel;

	public CSVJob(SymbolJobConfig<CSVConfiguration> config) throws Exception {
		super(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.jobs.SymbolJob#initializeDataSource()
	 */
	@Override
	protected void initializeDataSource() {
		CSVConfiguration configuration = getSymbolConfiguration();
		// CSVContractAdapter contract = new CSVContractAdapter(configuration);

		CSVHistoricalDataInfo historicalDataInfo = (CSVHistoricalDataInfo) getHistoricalDataInfoToRun();

		DataProviderParams historicalParams = new DataProviderParams();
		// just to avoid a NPE
		historicalParams.setSlots(new ArrayList<SlotParams>());
		historicalParams.addDefaultSlot();
		// --

		historicalParams.setGapFillingTypeSlidingWindow(historicalDataInfo
				.getGapFillingType() == GapFillingType.SLIDING_WINDOW);

		historicalParams.setExtraData(historicalDataInfo);
		historicalParams.setDp(historicalDataInfo.getDp());
		historicalParams.setXp(historicalDataInfo.getXp());

		File file;
		boolean tryAgain;
		do {
			String fname = configuration.getInfo().getSymbol().getFileName();
			String dir = CSVSymbolPlugin.getCSVFilesPath();
			file = new File(dir, fname);
			tryAgain = !file.exists();
			if (tryAgain) {
				tryAgain = true;
				Shell shell = Display.getDefault().getActiveShell();
				MessageDialog
						.openError(
								shell,
								"Error",
								"The file "
										+ file
										+ " does not exist. Please select a valid CSV Data Folder path.");
				PreferencesUtil.createPreferenceDialogOn(shell,
						CSVPrefsPage.ID, null, null).open();
			}
		} while (tryAgain);

		MfgSymbol symbol = new CsvSymbol(file.getAbsolutePath());

		int numberOfPrices = historicalDataInfo.getNumberOfPrices();

		/*
		 * the tick data request for a csv symbol is not real time, and this is
		 * OK. We cannot have a csv request which is real time.
		 */
		dataRequest = new TickDataRequest(symbol, historicalParams,
				numberOfPrices, false,
				historicalDataInfo.isFilterOutOfRangeTicks(),
				historicalDataInfo.getMinGapInTicks());

		dataSource = _dataProvider.createTickDataSource(dataRequest,
				configuration.getUUID());

		// speed control
		_dataSpeedModel = new DataSpeedModel();
		dataSource.setDelayControl(new ThreadDelayControl(_dataSpeedModel));
		_dataSpeedModel.addPropertyChangeListener(DataSpeedModel.PROP_STATE,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (_dataSpeedModel.getState() == DataSpeedControlState.STOPPED) {
							if (getMonitor() != null) {
								getMonitor().setCanceled(true);
							}
						}
					}
				});
	}

	@Override
	public TickDataSource getDataSource() {
		return (TickDataSource) dataSource;
	}

	@Override
	public IDataProvider getDataProvider() {
		// return CSVPlugin.getDefault().getDataProvider();
		return DFSPlugin.getDefault().getDataProvider();
	}

	@Override
	protected void canceling() {
		IDelayControl control = dataSource.getDelayControl();
		if (control != null) {
			DataSpeedModel model = control.getModel();
			model.setState(DataSpeedControlState.STOPPED);
			out.println("Notify speed control on job cancellation.");
			// Maybe the datasource thread is waiting for the
			// speed-control-model.
			// So we notify the model to make the datasource adavance to the
			// end.
			synchronized (model) {
				model.notifyAll();
			}
		}
		U.debug_var(392340, "trying to cancel the job ", this,
				" asking to cancel ", dataSource);
		((TickDataSource) this.dataSource).canceling();
		super.canceling();
	}

	@Override
	protected void onDataSourceWarmUpFinished(int dataLayer, long lastTime,
			boolean allLayersWarmedUp) {
		super.onDataSourceWarmUpFinished(dataLayer, lastTime, allLayersWarmedUp);
		if (allLayersWarmedUp) {
			if (_dataSpeedModel.getState() == DataSpeedControlState.FAST_FORWARDING) {
				_dataSpeedModel.setState(DataSpeedControlState.PLAYING);
			}
		}
	}

	/**
	 * @return the dataSpeedModel
	 */
	public DataSpeedModel getDataSpeedModel() {
		return _dataSpeedModel;
	}
}
