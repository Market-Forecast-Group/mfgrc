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

package com.mfg.strategy.builder.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.strategy.builder.AutomaticStrategySettings;
import com.mfg.strategy.builder.StrategyBuilderPlugin;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.strategy.builder.persistence.StrategyBuilderStorage;

/**
 * @author arian
 * 
 */
public class AutomaticStrategySettingsComposite extends Composite {
	private final DataBindingContext m_bindingContext;

	public static class StrategyInfoLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object element) {
			return super.getImage(element);
		}


		@Override
		public String getText(Object element) {
			return ((StrategyInfo) element).getName();
		}
	}

	public static class StringInfoToUUIDConverter extends Converter {

		public StringInfoToUUIDConverter() {
			super(StrategyInfo.class, UUID.class);
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
		 */
		@Override
		public Object convert(Object fromObject) {
			return fromObject == null ? null : ((StrategyInfo) fromObject).getUUID();
		}

	}

	private static class UUIDToStartegyInfoConverter extends Converter {

		private final StrategyBuilderStorage storage;


		public UUIDToStartegyInfoConverter() {
			super(UUID.class, StrategyInfo.class);
			storage = StrategyBuilderPlugin.getDefault().getStrategiesStorage();
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.databinding.conversion.IConverter#convert(java.lang.Object)
		 */
		@Override
		public Object convert(Object fromObject) {
			return fromObject == null ? null : storage.findById(((UUID) fromObject));
		}

	}

	/**
	 * 
	 */
	private static final String PROP_SETTINGS = "settings";
	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final AutomaticStrategySettingsComposite self = this;
	private AutomaticStrategySettings settings;
	final ComboViewer patternComboViewer;


	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public AutomaticStrategySettingsComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(2, false));

		Label lblPattern = toolkit.createLabel(this, "Pattern", SWT.NONE);
		lblPattern.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		patternComboViewer = new ComboViewer(this, SWT.READ_ONLY);
		Combo combo = patternComboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolkit.paintBordersFor(combo);
		patternComboViewer.setLabelProvider(new StrategyInfoLabelProvider());
		patternComboViewer.setContentProvider(new ArrayContentProvider());

		afterCreateWidgets();
		m_bindingContext = initDataBindings();
	}


	/**
	 * 
	 */
	private void afterCreateWidgets() {
		patternComboViewer.setInput(getPatterns());
		final WorkspaceStorageAdapter listener = new WorkspaceStorageAdapter() {
			@Override
			public void storageChanged(IWorkspaceStorage storage) {
				patternComboViewer.setInput(getPatterns());
				patternBinding.updateModelToTarget();
			}
		};
		final StrategyBuilderStorage storage = StrategyBuilderPlugin.getDefault().getStrategiesStorage();
		storage.addStorageListener(listener);
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				storage.removeStorageListener(listener);
			}
		});
	}


	static List<StrategyInfo> getPatterns() {
		return StrategyBuilderPlugin.getDefault().getStrategiesStorage().getObjects();

	}


	/**
	 * @return the settings
	 */
	public AutomaticStrategySettings getSettings() {
		return settings;
	}


	/**
	 * @param aSettings
	 *            the settings to set
	 */
	public void setSettings(AutomaticStrategySettings aSettings) {
		this.settings = aSettings;
		firePropertyChange(PROP_SETTINGS);
	}


	/**
	 * @return the m_bindingContext
	 */
	public DataBindingContext getDataBindingContext() {
		return m_bindingContext;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(this);
	Binding patternBinding;


	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}


	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}


	public void addPropertyChangeListener(String property, PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}


	public void removePropertyChangeListener(String property, PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}


	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}


	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue patternComboViewerObserveSingleSelection = ViewersObservables.observeSingleSelection(patternComboViewer);
		IObservableValue selfSettingspatternFileObserveValue = BeansObservables.observeValue(self, "settings.strategyInfoId");
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringInfoToUUIDConverter());
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new UUIDToStartegyInfoConverter());
		patternBinding = bindingContext.bindValue(patternComboViewerObserveSingleSelection, selfSettingspatternFileObserveValue, strategy, strategy_1);
		//
		return bindingContext;
	}
}
