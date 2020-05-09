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
package com.mfg.strategy.manual.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;
import com.mfg.strategy.AutoStop;
import com.mfg.strategy.ChildToExit;
import com.mfg.strategy.EntryExitOrderType;
import com.mfg.strategy.ManualStrategySettings;
import com.mfg.utils.ui.IEnumWithLabel;

/**
 * @author arian
 * 
 */
public class ManualStrategySettingsComposite extends Composite {

	/**
	 * 
	 */
	private static final String PROP_SETTINGS = "settings";

	private final DataBindingContext m_bindingContext;

	final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private final List<EntryExitOrderType> entryExitOrderTypes = Arrays
			.asList(EntryExitOrderType.values());
	private final List<ChildToExit> childToExistList = Arrays
			.asList(ChildToExit.values());
	private final List<AutoStop> autoStopList = Arrays
			.asList(AutoStop.values());
	private final List<EXECUTION_TYPE> stopLoss_stopTypes = Arrays.asList(
			EXECUTION_TYPE.STOP, EXECUTION_TYPE.STOP_LIMIT);
	private final List<EXECUTION_TYPE> takeProfit_stopTypes = Arrays.asList(
			EXECUTION_TYPE.LIMIT, EXECUTION_TYPE.MIT);

	private final Text text;
	private final Text text_1;
	private final Text text_2;
	private final Text text_5;
	private final Text text_6;
	private final Spinner spinner;
	private final Spinner spinner_1;
	private final ComboViewer comboViewer_2;
	private final Spinner spinner_3;
	private final ComboViewer comboViewer_3;
	private final ComboViewer comboViewer;
	private final Button btnThScale;
	private final Spinner spinner_2;
	private final ComboViewer comboViewer_1;
	private final ComboViewer comboViewer_5;
	private final Spinner spinner_4;
	private final ComboViewer comboViewer_4;

	private ManualStrategySettings settings;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ManualStrategySettingsComposite(Composite parent, int style,
			ManualStrategySettings aSettings) {
		super(parent, style);
		this.settings = aSettings;
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(2, true));

		Section sctnBasicSettings = toolkit.createSection(this,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnBasicSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		toolkit.paintBordersFor(sctnBasicSettings);
		sctnBasicSettings.setText("Basic Settings");
		sctnBasicSettings.setExpanded(true);

		Composite composite = toolkit.createComposite(sctnBasicSettings,
				SWT.NONE);
		toolkit.paintBordersFor(composite);
		sctnBasicSettings.setClient(composite);
		composite.setLayout(new GridLayout(2, false));

		toolkit.createLabel(composite, "Quantity", SWT.NONE);

		spinner = new Spinner(composite, SWT.BORDER);
		toolkit.adapt(spinner);
		toolkit.paintBordersFor(spinner);

		toolkit.createLabel(composite, "Max Quantity", SWT.NONE);

		spinner_1 = new Spinner(composite, SWT.BORDER);
		toolkit.adapt(spinner_1);
		toolkit.paintBordersFor(spinner_1);

		Section sctnEntryexit = toolkit.createSection(this,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnEntryexit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		toolkit.paintBordersFor(sctnEntryexit);
		sctnEntryexit.setText("Entry/Exit");
		sctnEntryexit.setExpanded(true);

		Composite composite_2 = toolkit
				.createComposite(sctnEntryexit, SWT.NONE);
		toolkit.paintBordersFor(composite_2);
		sctnEntryexit.setClient(composite_2);
		composite_2.setLayout(new GridLayout(2, false));

		toolkit.createLabel(composite_2, "Order Type", SWT.NONE);

		comboViewer = new ComboViewer(composite_2, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		toolkit.paintBordersFor(combo);

		toolkit.createLabel(composite_2, "Limit Price", SWT.NONE);

		text = toolkit.createText(composite_2, "New Text", SWT.NONE);
		text.setText("");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnThScale = new Button(composite_2, SWT.CHECK);
		toolkit.adapt(btnThScale, true, true);
		btnThScale.setText("TH Scale");

		spinner_2 = new Spinner(composite_2, SWT.BORDER);
		toolkit.adapt(spinner_2);
		toolkit.paintBordersFor(spinner_2);

		Label lblChildToExit = toolkit.createLabel(composite_2,
				"Child to Exit", SWT.NONE);
		lblChildToExit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));

		comboViewer_1 = new ComboViewer(composite_2, SWT.READ_ONLY);
		Combo combo_1 = comboViewer_1.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_1);

		Section sctnStopLoss = toolkit.createSection(this,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnStopLoss.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		toolkit.paintBordersFor(sctnStopLoss);
		sctnStopLoss.setText("Stop Loss");
		sctnStopLoss.setExpanded(true);

		Composite composite_1 = toolkit.createComposite(sctnStopLoss, SWT.NONE);
		toolkit.paintBordersFor(composite_1);
		sctnStopLoss.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, false));

		toolkit.createLabel(composite_1, "Auto Stop", SWT.NONE);

		comboViewer_2 = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo_2 = comboViewer_2.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_2);

		toolkit.createLabel(composite_1, "Number of Ticks", SWT.NONE);

		spinner_3 = new Spinner(composite_1, SWT.BORDER);
		toolkit.adapt(spinner_3);
		toolkit.paintBordersFor(spinner_3);

		toolkit.createLabel(composite_1, "Stop Type", SWT.NONE);

		comboViewer_3 = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo_3 = comboViewer_3.getCombo();
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_3);

		toolkit.createLabel(composite_1, "Trigger Price", SWT.NONE);

		text_1 = toolkit.createText(composite_1, "New Text", SWT.NONE);
		text_1.setText("");
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(composite_1, "Limit Price", SWT.NONE);

		text_2 = toolkit.createText(composite_1, "New Text", SWT.NONE);
		text_2.setText("");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Section sctnTakeProfit = toolkit.createSection(this,
				ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		sctnTakeProfit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		toolkit.paintBordersFor(sctnTakeProfit);
		sctnTakeProfit.setText("Take Profit");
		sctnTakeProfit.setExpanded(true);

		Composite composite_3 = toolkit.createComposite(sctnTakeProfit,
				SWT.NONE);
		toolkit.paintBordersFor(composite_3);
		sctnTakeProfit.setClient(composite_3);
		composite_3.setLayout(new GridLayout(2, false));

		toolkit.createLabel(composite_3, "Auto Stop", SWT.NONE);

		comboViewer_5 = new ComboViewer(composite_3, SWT.READ_ONLY);
		Combo combo_5 = comboViewer_5.getCombo();
		combo_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_5);

		toolkit.createLabel(composite_3, "Number of Ticks", SWT.NONE);

		spinner_4 = new Spinner(composite_3, SWT.BORDER);
		toolkit.adapt(spinner_4);
		toolkit.paintBordersFor(spinner_4);

		toolkit.createLabel(composite_3, "Stop Type", SWT.NONE);

		comboViewer_4 = new ComboViewer(composite_3, SWT.READ_ONLY);
		Combo combo_4 = comboViewer_4.getCombo();
		combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		toolkit.paintBordersFor(combo_4);

		toolkit.createLabel(composite_3, "Trigger Price", SWT.NONE);

		text_5 = toolkit.createText(composite_3, "New Text", SWT.NONE);
		text_5.setText("");
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		toolkit.createLabel(composite_3, "Limit Price", SWT.NONE);

		text_6 = toolkit.createText(composite_3, "New Text", SWT.NONE);
		text_6.setText("");
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		m_bindingContext = initDataBindings();
	}

	public ManualStrategySettings getSettings() {
		return settings;
	}

	/**
	 * @param aSettings
	 *            the settings to set
	 */
	public void setSettings(ManualStrategySettings aSettings) {
		this.settings = aSettings;
		firePropertyChange(PROP_SETTINGS);
	}

	public DataBindingContext getDataBindingContext() {
		return m_bindingContext;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue spinnerObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner);
		IObservableValue getSettingsBasicQuantityObserveValue = BeansObservables
				.observeValue(getSettings(), "basicQuantity");
		bindingContext.bindValue(spinnerObserveSelectionObserveWidget,
				getSettingsBasicQuantityObserveValue, null, null);
		//
		IObservableValue spinner_1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_1);
		IObservableValue getSettingsBasicMaxQuantityObserveValue = BeansObservables
				.observeValue(getSettings(), "basicMaxQuantity");
		bindingContext.bindValue(spinner_1ObserveSelectionObserveWidget,
				getSettingsBasicMaxQuantityObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		comboViewer_2.setContentProvider(listContentProvider);
		//
		IObservableMap observeMap = PojoObservables.observeMap(
				listContentProvider.getKnownElements(), IEnumWithLabel.class,
				"label");
		comboViewer_2.setLabelProvider(new ObservableMapLabelProvider(
				observeMap));
		//
		WritableList writableList = new WritableList(autoStopList,
				IEnumWithLabel.class);
		comboViewer_2.setInput(writableList);
		//
		IObservableValue comboViewer_2ObserveSingleSelection = ViewersObservables
				.observeSingleSelection(comboViewer_2);
		IObservableValue getSettingsStopLossSettingsautoStopObserveValue = BeansObservables
				.observeValue(getSettings(), "stopLossSettings.autoStop");
		bindingContext.bindValue(comboViewer_2ObserveSingleSelection,
				getSettingsStopLossSettingsautoStopObserveValue, null, null);
		//
		IObservableValue spinner_3ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_3);
		IObservableValue getSettingsStopLossSettingsnumberOfTicksObserveValue = BeansObservables
				.observeValue(getSettings(), "stopLossSettings.numberOfTicks");
		bindingContext.bindValue(spinner_3ObserveSelectionObserveWidget,
				getSettingsStopLossSettingsnumberOfTicksObserveValue, null,
				null);
		//
		ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		comboViewer_3.setContentProvider(listContentProvider_1);
		//
		IObservableMap observeMap_1 = PojoObservables.observeMap(
				listContentProvider_1.getKnownElements(), IEnumWithLabel.class,
				"label");
		comboViewer_3.setLabelProvider(new ObservableMapLabelProvider(
				observeMap_1));
		//
		WritableList writableList_1 = new WritableList(stopLoss_stopTypes,
				IEnumWithLabel.class);
		comboViewer_3.setInput(writableList_1);
		//
		IObservableValue comboViewer_3ObserveSingleSelection = ViewersObservables
				.observeSingleSelection(comboViewer_3);
		IObservableValue getSettingsStopLossSettingsstopTypeObserveValue = BeansObservables
				.observeValue(getSettings(), "stopLossSettings.stopType");
		bindingContext.bindValue(comboViewer_3ObserveSingleSelection,
				getSettingsStopLossSettingsstopTypeObserveValue, null, null);
		//
		IObservableValue text_1ObserveTextObserveWidget = SWTObservables
				.observeText(text_1, SWT.Modify);
		IObservableValue getSettingsStopLossSettingstriggerPriceObserveValue = BeansObservables
				.observeValue(getSettings(), "stopLossSettings.triggerPrice");
		bindingContext
				.bindValue(text_1ObserveTextObserveWidget,
						getSettingsStopLossSettingstriggerPriceObserveValue,
						null, null);
		//
		IObservableValue text_2ObserveTextObserveWidget = SWTObservables
				.observeText(text_2, SWT.Modify);
		IObservableValue getSettingsStopLossSettingslimitPriceObserveValue = BeansObservables
				.observeValue(getSettings(), "stopLossSettings.limitPrice");
		bindingContext.bindValue(text_2ObserveTextObserveWidget,
				getSettingsStopLossSettingslimitPriceObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider_2 = new ObservableListContentProvider();
		comboViewer.setContentProvider(listContentProvider_2);
		//
		IObservableMap observeMap_2 = PojoObservables.observeMap(
				listContentProvider_2.getKnownElements(), IEnumWithLabel.class,
				"label");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(
				observeMap_2));
		//
		WritableList writableList_2 = new WritableList(entryExitOrderTypes,
				IEnumWithLabel.class);
		comboViewer.setInput(writableList_2);
		//
		IObservableValue comboViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(comboViewer);
		IObservableValue getSettingsEntryExitOrderTypeObserveValue = BeansObservables
				.observeValue(getSettings(), "entryExitOrderType");
		bindingContext.bindValue(comboViewerObserveSingleSelection,
				getSettingsEntryExitOrderTypeObserveValue, null, null);
		//
		IObservableValue textObserveTextObserveWidget = SWTObservables
				.observeText(text, SWT.Modify);
		IObservableValue getSettingsEntryExitLimitPriceObserveValue = BeansObservables
				.observeValue(getSettings(), "entryExitLimitPrice");
		bindingContext.bindValue(textObserveTextObserveWidget,
				getSettingsEntryExitLimitPriceObserveValue, null, null);
		//
		IObservableValue btnThScaleObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnThScale);
		IObservableValue getSettingsSelectedEntryExitTHScaleObserveValue = BeansObservables
				.observeValue(getSettings(), "selectedEntryExitTHScale");
		bindingContext.bindValue(btnThScaleObserveSelectionObserveWidget,
				getSettingsSelectedEntryExitTHScaleObserveValue, null, null);
		//
		IObservableValue spinner_2ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_2);
		IObservableValue getSettingsEntryExitTHScaleObserveValue = BeansObservables
				.observeValue(getSettings(), "entryExitTHScale");
		bindingContext.bindValue(spinner_2ObserveSelectionObserveWidget,
				getSettingsEntryExitTHScaleObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider_3 = new ObservableListContentProvider();
		comboViewer_1.setContentProvider(listContentProvider_3);
		//
		IObservableMap observeMap_3 = PojoObservables.observeMap(
				listContentProvider_3.getKnownElements(), IEnumWithLabel.class,
				"label");
		comboViewer_1.setLabelProvider(new ObservableMapLabelProvider(
				observeMap_3));
		//
		WritableList writableList_3 = new WritableList(childToExistList,
				IEnumWithLabel.class);
		comboViewer_1.setInput(writableList_3);
		//
		IObservableValue comboViewer_1ObserveSingleSelection = ViewersObservables
				.observeSingleSelection(comboViewer_1);
		IObservableValue getSettingsEntryExitChildToExitObserveValue = BeansObservables
				.observeValue(getSettings(), "entryExitChildToExit");
		bindingContext.bindValue(comboViewer_1ObserveSingleSelection,
				getSettingsEntryExitChildToExitObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider_4 = new ObservableListContentProvider();
		comboViewer_5.setContentProvider(listContentProvider_4);
		//
		IObservableMap observeMap_4 = PojoObservables.observeMap(
				listContentProvider_4.getKnownElements(), IEnumWithLabel.class,
				"label");
		comboViewer_5.setLabelProvider(new ObservableMapLabelProvider(
				observeMap_4));
		//
		comboViewer_5.setInput(writableList);
		//
		IObservableValue comboViewer_5ObserveSingleSelection = ViewersObservables
				.observeSingleSelection(comboViewer_5);
		IObservableValue getSettingsTakeProfitSettingsautoStopObserveValue = BeansObservables
				.observeValue(getSettings(), "takeProfitSettings.autoStop");
		bindingContext.bindValue(comboViewer_5ObserveSingleSelection,
				getSettingsTakeProfitSettingsautoStopObserveValue, null, null);
		//
		IObservableValue spinner_4ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner_4);
		IObservableValue getSettingsTakeProfitSettingsnumberOfTicksObserveValue = BeansObservables
				.observeValue(getSettings(), "takeProfitSettings.numberOfTicks");
		bindingContext.bindValue(spinner_4ObserveSelectionObserveWidget,
				getSettingsTakeProfitSettingsnumberOfTicksObserveValue, null,
				null);
		//
		ObservableListContentProvider listContentProvider_5 = new ObservableListContentProvider();
		comboViewer_4.setContentProvider(listContentProvider_5);
		//
		IObservableMap observeMap_5 = PojoObservables.observeMap(
				listContentProvider_5.getKnownElements(), IEnumWithLabel.class,
				"label");
		comboViewer_4.setLabelProvider(new ObservableMapLabelProvider(
				observeMap_5));
		//
		WritableList writableList_4 = new WritableList(takeProfit_stopTypes,
				IEnumWithLabel.class);
		comboViewer_4.setInput(writableList_4);
		//
		IObservableValue comboViewer_4ObserveSingleSelection = ViewersObservables
				.observeSingleSelection(comboViewer_4);
		IObservableValue getSettingsTakeProfitSettingsstopTypeObserveValue = BeansObservables
				.observeValue(getSettings(), "takeProfitSettings.stopType");
		bindingContext.bindValue(comboViewer_4ObserveSingleSelection,
				getSettingsTakeProfitSettingsstopTypeObserveValue, null, null);
		//
		IObservableValue text_5ObserveTextObserveWidget = SWTObservables
				.observeText(text_5, SWT.Modify);
		IObservableValue getSettingsTakeProfitSettingstriggerPriceObserveValue = BeansObservables
				.observeValue(getSettings(), "takeProfitSettings.triggerPrice");
		bindingContext.bindValue(text_5ObserveTextObserveWidget,
				getSettingsTakeProfitSettingstriggerPriceObserveValue, null,
				null);
		//
		IObservableValue text_6ObserveTextObserveWidget = SWTObservables
				.observeText(text_6, SWT.Modify);
		IObservableValue getSettingsTakeProfitSettingslimitPriceObserveValue = BeansObservables
				.observeValue(getSettings(), "takeProfitSettings.limitPrice");
		bindingContext
				.bindValue(text_6ObserveTextObserveWidget,
						getSettingsTakeProfitSettingslimitPriceObserveValue,
						null, null);
		//
		return bindingContext;
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

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
}
