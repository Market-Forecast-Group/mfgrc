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
package com.mfg.symbols.csv.ui.editors;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormPage;

import com.mfg.dm.speedControl.DataSpeedControlState;
import com.mfg.dm.speedControl.DataSpeedModel;
import com.mfg.dm.speedControl.SpeedComposite3;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.csv.configurations.CSVConfiguration;
import com.mfg.symbols.csv.jobs.CSVJob;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.ui.editors.CreateCommandsRunnable;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobConfig;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.ui.editors.AbstractSymbolEditor;
import com.mfg.utils.ImageUtils;

/**
 * @author arian
 * 
 */
public class CSVEditor extends AbstractSymbolEditor {

	public static final String EDITOR_ID = "com.mfg.symbols.csv.ui.editors.CSVSymbolConfigurationEditor"; //$NON-NLS-1$

	public CSVEditor() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		try {
			int i = addPage(new CSVEditorPage(this, getMainPageId(),
					getConfiguration().getName()));
			setPageImage(i, ImageUtils.getBundledImage(
					SymbolsPlugin.getDefault(),
					SymbolsPlugin.SYMBOL_CONFIG_IMAGE_PATH));
			super.addPages();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected FormPage createInputPage(final InputConfiguration inputConfig) {
		return new CSVInputEditorPage(this, inputConfig.getUUID().toString(),
				inputConfig.getName()) {
			@Override
			public InputConfiguration getConfiguration() {
				return inputConfig;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.ui.editors.AbstractSymbolEditor#createCommandsSectionForInput
	 * (org.eclipse.swt.widgets.Composite,
	 * com.mfg.inputs.configurations.InputConfiguration)
	 */
	@Override
	public Object createCommandsSection(final Composite parent,
			final InputConfiguration inputConfiguration,
			final TradingConfiguration tradingConfiguration) {
		new CreateCommandsRunnable<CSVConfiguration>(this, inputConfiguration,
				tradingConfiguration, parent) {
			SpeedComposite3 _dataSpeedComp;

			@Override
			protected void updateCommandsWidgetsForJob(SymbolJob<?> job,
					boolean runningJobInThisTab) {
				DataSpeedModel model = runningJobInThisTab ? ((CSVJob) job)
						.getDataSpeedModel() : DataSpeedModel.DISABLED;
				_dataSpeedComp.setModel(model);
			}

			@Override
			protected void updateCommandsWidgetsToInitialState() {
				_dataSpeedComp.setModel(DataSpeedModel.INITIAL_MODEL);
			}

			@Override
			protected Job createJob(boolean aStartTrading) {
				try {
					// TODO: we say that always that you start the job from the
					// input or trading tab, it will start the trading.
					boolean startTrading = aStartTrading

					|| tradingConfiguration != null;
					TradingConfiguration[] tradings;
					if (startTrading) {
						tradings = tradingConfiguration == null ? null
								: new TradingConfiguration[] { tradingConfiguration };
					} else {
						tradings = new TradingConfiguration[0];
					}
					InputConfiguration[] inputs = inputConfiguration == null ? null
							: new InputConfiguration[] { inputConfiguration };
					SymbolJobConfig<CSVConfiguration> config = new SymbolJobConfig<>(
							getConfiguration(), inputs, tradings,
							inputConfiguration == null ? getConfiguration()
									: inputConfiguration);
					_dataSpeedComp.setEnableSpeedButtons(true);
					return new CSVJob(config);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}

			@Override
			protected boolean isJobClass(Job job) {
				return job instanceof CSVJob;
			}

			@Override
			protected void createCommandsWidgets(Composite aParent) {
				// CSVCommandsComposite comp = new CSVCommandsComposite(parent,
				// SWT.NONE);
				// comp.setConfiguration(getConfiguration());
				// dataSpeedComp = comp.getSpeedComposite();
				_dataSpeedComp = new SpeedComposite3(aParent, SWT.NONE);
				_dataSpeedComp.getPlayButton().addSelectionListener(
						new SelectionListener() {

							@Override
							public void widgetSelected(SelectionEvent e) {
								// check if it is the case it click the play
								// button to pause the job and not to start it.
								if (_dataSpeedComp.getModel().getState() == DataSpeedControlState.INITIAL) {
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
				_dataSpeedComp.setStopVeto(new Runnable() {

					@Override
					public void run() {
						if (SymbolJob
								.isConfigurationTrading(getConfiguration())) {
							throw new IllegalArgumentException(
									"There is a trading session open.  You need to close it before stopping data");
						}
					}
				});

			}
		}.run();
		return null;
	}

	/**
	 * @return
	 */
	CSVConfiguration getConfiguration() {
		return (CSVConfiguration) getEditorInput().getStorageObject();
	}
}
