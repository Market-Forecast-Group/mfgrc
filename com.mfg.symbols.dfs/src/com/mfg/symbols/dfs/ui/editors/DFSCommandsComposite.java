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
package com.mfg.symbols.dfs.ui.editors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.ResourceManager;

import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.configurations.DFSConfigurationInfo;
import com.mfg.utils.DataBindingUtils;

/**
 * @author arian
 * 
 */
public class DFSCommandsComposite extends Composite {
	private final DataBindingContext m_bindingContext;

	/**
	 * 
	 */
	private static final String PROP_INFO = "info";
	/**
	 * 
	 */
	private static final String PROP_CONFIGURATION = "configuration";
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private DFSConfiguration configuration;
	private DFSConfigurationInfo info;
	private final DFSCommandsComposite self = this;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DFSCommandsComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(3, false));

		btnStartData = toolkit.createButton(this, "", SWT.NONE);
		btnStartData.setImage(ResourceManager.getPluginImage("com.mfg.symbols",
				"icons/play.gif"));

		btnStopData = toolkit.createButton(this, "", SWT.NONE);
		btnStopData.setImage(ResourceManager.getPluginImage("com.mfg.symbols",
				"icons/stop.gif"));
		new Label(this, SWT.NONE).setText("");

		btnStartTrading = new Button(this, SWT.CHECK);
		btnStartTrading.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 3, 1));
		toolkit.adapt(btnStartTrading, true, true);
		btnStartTrading.setText("Start Trading");
		m_bindingContext = initDataBindings();
		afterInitBindings();

	}

	private void afterInitBindings() {
		DataBindingUtils.disposeBindingContextAtControlDispose(this,
				m_bindingContext);
	}

	/**
	 * @return the m_bindingContext
	 */
	public DataBindingContext getBindingContext() {
		return m_bindingContext;
	}

	/**
	 * @return the configuration
	 */
	public DFSConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param aConfiguration
	 *            the configuration to set
	 */
	public void setConfiguration(DFSConfiguration aConfiguration) {
		firePropertyChange(PROP_CONFIGURATION);
		this.configuration = aConfiguration;
	}

	/**
	 * @return the info
	 */
	public DFSConfigurationInfo getInfo() {
		return info;
	}

	/**
	 * @param aInfo
	 *            the info to set
	 */
	public void setInfo(DFSConfigurationInfo aInfo) {
		this.info = aInfo;
		firePropertyChange(PROP_INFO);
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private final Button btnStartTrading;
	private final Button btnStartData;
	private final Button btnStopData;

	/**
	 * @return the btnStartData
	 */
	public Button getStartDataButton() {
		return btnStartData;
	}

	/**
	 * @return the btnStartTrading
	 */
	public Button getStartTradingButton() {
		return btnStartTrading;
	}

	/**
	 * @return the btnStopData
	 */
	public Button getStopDataButton() {
		return btnStopData;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue btnStartTradingObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnStartTrading);
		IObservableValue selfInfostartTradingObserveValue = BeansObservables
				.observeValue(self, "info.startTrading");
		bindingContext.bindValue(btnStartTradingObserveSelectionObserveWidget,
				selfInfostartTradingObserveValue, null, null);
		//
		return bindingContext;
	}
}
