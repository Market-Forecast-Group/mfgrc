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
package com.mfg.symbols.trading.ui.editors;

import java.util.Arrays;
import java.util.UUID;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.ResourceManager;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobChangeAdapter;
import com.mfg.symbols.jobs.TradingPipeChangeEvent;
import com.mfg.symbols.trading.configurations.TradingConfiguration;

/**
 * @author arian
 * 
 */
public class TradingCommandsComposite extends Composite {

	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final Button btnStart;
	private final Button btnStop;
	private final Button btnStopTradingAndData;
	private TradingConfiguration _configuration;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public TradingCommandsComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(4, false));

		btnStart = toolkit.createButton(this, "", SWT.NONE);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startTrading();
			}
		});
		btnStart.setImage(ResourceManager.getPluginImage("com.mfg.symbols",
				"icons/play.gif"));
		toolkit.adapt(btnStart, true, true);

		btnStop = toolkit.createButton(this, "", SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopTrading();
			}
		});
		btnStop.setImage(ResourceManager.getPluginImage("com.mfg.symbols",
				"icons/stop.gif"));
		toolkit.adapt(btnStop, true, true);

		Label label = toolkit.createLabel(this, "", SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
				1);
		gd_label.widthHint = 10;
		label.setLayoutData(gd_label);

		btnStopTradingAndData = toolkit.createButton(this, "", SWT.NONE);
		btnStopTradingAndData.setImage(ResourceManager.getPluginImage(
				"com.mfg.symbols", "icons/stop.gif"));
		btnStopTradingAndData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopDataRequest();
			}
		});
		toolkit.adapt(btnStopTradingAndData, true, true);
		btnStopTradingAndData.setText("Trading && Data");
		afterCreateWidgets();
	}

	protected void stopDataRequest() {
		SymbolJob.stopConfigurationDataRequest(_configuration);
	}

	protected void stopTrading() {
		SymbolJob.stopConfiguration(_configuration);
	}

	protected void startTrading() {
		SymbolJob.runConfigurations(Arrays.asList((Object) getConfiguration()),
				getConfiguration());
	}

	private void afterCreateWidgets() {
		final SymbolJobChangeAdapter listener = new SymbolJobChangeAdapter() {
			@Override
			public void tradingChanged(TradingPipeChangeEvent event) {
				update();
			}

			@Override
			public void done(IJobChangeEvent event) {
				update();
			}

			@Override
			public void running(IJobChangeEvent event) {
				update();
			}
			
			private void update() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (!isDisposed()) {
							updateButtons();
						}
					}
				});
			}
		};
		SymbolJob.getManager().addJobChangeListener(listener);
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				SymbolJob.getManager().removeJobChangeListener(listener);
			}
		});
	}

	/**
	 * @return the configuration
	 */
	public TradingConfiguration getConfiguration() {
		return _configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(TradingConfiguration configuration) {
		_configuration = configuration;
		updateButtons();
	}

	void updateButtons() {
		if (_configuration != null) {
			boolean canRun = SymbolJob.canRunConfiguration(_configuration);
			boolean running = SymbolJob.isConfigurationRunning(_configuration);
			UUID inputId = _configuration.getInfo().getInputConfiguratioId();
			InputConfiguration input = SymbolsPlugin.getDefault()
					.getInputsStorage().findById(inputId);
			boolean runningInput = SymbolJob.isConfigurationRunning(input);
			btnStart.setEnabled(canRun);
			btnStop.setEnabled(running);
			btnStopTradingAndData.setEnabled(runningInput);
		}
	}

}
