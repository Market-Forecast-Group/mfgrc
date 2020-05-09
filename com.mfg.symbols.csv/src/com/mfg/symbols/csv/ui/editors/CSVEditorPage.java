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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.chart.ui.actions.ShowObjectInChart;
import com.mfg.connector.csv.CSVHistoricalDataInfo;
import com.mfg.dm.symbols.SymbolData2;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.symbols.csv.CSVSymbolPlugin;
import com.mfg.symbols.csv.configurations.CSVConfiguration;
import com.mfg.symbols.csv.configurations.CSVConfigurationInfo;
import com.mfg.symbols.ui.databinding.RealTickSizeValidator;
import com.mfg.symbols.ui.editors.AbstractSymbolEditor;
import com.mfg.symbols.ui.widgets.EmptyTickFieldPainter;
import com.mfg.ui.editors.EditorUtils;
import com.mfg.ui.editors.StorageObjectEditorInput;
import com.mfg.utils.DataBindingUtils;
import com.mfg.utils.ui.bindings.StringToBigDecimalConverter;

/**
 * @author arian
 * 
 */
public class CSVEditorPage extends FormPage {
	private DataBindingContext m_bindingContext;
	private Text txtDataProvider;
	private Text textLocalSymbol;
	private Text textTickValue;
	private Section sctnCommands;
	private final CSVEditorPage self = this;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public CSVEditorPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public CSVEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	@SuppressWarnings("unused")
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("CSV Symbol Configuration");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		managedForm.getForm().getBody().setLayout(new GridLayout(1, false));

		sctnCommands = managedForm.getToolkit().createSection(
				managedForm.getForm().getBody(),
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		sctnCommands.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnCommands);
		sctnCommands.setText("Commands");

		commandsComposite = managedForm.getToolkit().createComposite(
				managedForm.getForm().getBody(), SWT.NONE);
		commandsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		commandsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(commandsComposite);

		btnStartTradings = new Button(managedForm.getForm().getBody(),
				SWT.CHECK);
		managedForm.getToolkit().adapt(btnStartTradings, true, true);
		btnStartTradings.setText("Start Trading");

		Composite composite_3 = managedForm.getToolkit()
				.createCompositeSeparator(managedForm.getForm().getBody());
		GridData gd_composite_3 = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_composite_3.heightHint = 2;
		composite_3.setLayoutData(gd_composite_3);
		managedForm.getToolkit().paintBordersFor(composite_3);

		ScrolledComposite scrolledComposite = new ScrolledComposite(managedForm
				.getForm().getBody(), SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		managedForm.getToolkit().adapt(scrolledComposite);
		managedForm.getToolkit().paintBordersFor(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite_2 = managedForm.getToolkit().createComposite(
				scrolledComposite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_2);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.marginWidth = 0;
		gl_composite_2.marginHeight = 0;
		composite_2.setLayout(gl_composite_2);

		Section sctnSymbol = managedForm.getToolkit().createSection(
				composite_2, ExpandableComposite.TITLE_BAR);
		sctnSymbol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnSymbol);
		sctnSymbol.setText("Symbol");
		sctnSymbol.setExpanded(true);

		Section sctnHistoricalData = managedForm.getToolkit().createSection(
				composite_2, ExpandableComposite.TITLE_BAR);
		sctnHistoricalData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnHistoricalData);
		sctnHistoricalData.setText("Historical Data");
		sctnHistoricalData.setExpanded(true);

		Composite composite = managedForm.getToolkit().createComposite(
				composite_2, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		managedForm.getToolkit().paintBordersFor(composite);
		composite.setLayout(new GridLayout(2, false));

		Label label = managedForm.getToolkit().createLabel(composite,
				"Data Provider", SWT.NONE);

		txtDataProvider = managedForm.getToolkit().createText(composite,
				"New Text", SWT.READ_ONLY);
		txtDataProvider.setText("CSV");
		txtDataProvider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		managedForm.getToolkit().createLabel(composite, "Name", SWT.NONE);

		textName = managedForm.getToolkit().createText(composite, "New Text",
				SWT.NONE);
		textName.setText("");
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		managedForm.getToolkit().createLabel(composite, "Local Symbol",
				SWT.NONE);

		textLocalSymbol = managedForm.getToolkit().createText(composite,
				"New Text", SWT.NONE);
		textLocalSymbol.setText("");
		textLocalSymbol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		managedForm.getToolkit().createLabel(composite, "Tick Size", SWT.NONE);

		textRealTickSize = managedForm.getToolkit().createText(composite,
				"New Text", SWT.NONE);
		textRealTickSize.setText("");
		textRealTickSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		btnAutomaticVerifyTick = new Button(composite, SWT.CHECK);
		btnAutomaticVerifyTick.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		managedForm.getToolkit().adapt(btnAutomaticVerifyTick, true, true);
		btnAutomaticVerifyTick.setText("Automatic Verify Tick Size");

		managedForm.getToolkit().createLabel(composite, "Tick Value", SWT.NONE);

		textTickValue = managedForm.getToolkit().createText(composite,
				"New Text", SWT.NONE);
		textTickValue.setText("");
		textTickValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		managedForm.getToolkit().createLabel(composite, "Currency", SWT.NONE);

		comboViewerCurrency = new ComboViewer(composite, SWT.NONE);
		Combo combo = comboViewerCurrency.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		managedForm.getToolkit().paintBordersFor(combo);

		historicalDataInfoComposite = new CSVHistoricalDataInfoComposite(
				composite_2, SWT.NONE);
		historicalDataInfoComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, false, false, 1, 1));
		managedForm.getToolkit().adapt(historicalDataInfoComposite);
		managedForm.getToolkit().paintBordersFor(historicalDataInfoComposite);
		scrolledComposite.setContent(composite_2);
		scrolledComposite.setMinSize(composite_2.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		comboViewerCurrency.setLabelProvider(new LabelProvider());
		comboViewerCurrency.setContentProvider(new ArrayContentProvider());

		afterCreateWidgets(managedForm);

		m_bindingContext = initDataBindings();

		afterCreateBindings();

	}

	private void afterCreateBindings() {
		EditorUtils.registerBindingListenersToUpdateWorkspace(
				commandsComposite, CSVSymbolPlugin.getDefault()
						.getCSVStorage(), m_bindingContext,
				historicalDataInfoComposite.getDataBindingContext());
		DataBindingUtils.decorateBindings(m_bindingContext,
				historicalDataInfoComposite.getDataBindingContext());
	}

	private void afterCreateWidgets(IManagedForm managedForm) {
		EmptyTickFieldPainter.addPainterToWidgets(textRealTickSize);
		comboViewerCurrency.setInput(SymbolData2.CURRENCIES);

		((AbstractSymbolEditor) getEditor()).createCommandsSection(
				commandsComposite, null, null);

		addActions(managedForm);

		final PropertyChangeListener nameListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Display.getCurrent().asyncExec(new Runnable() {

					@Override
					public void run() {
						CSVEditor editor = (CSVEditor) getEditor();
						editor.setPageText(getIndex(), getConfiguration()
								.getName());
					}
				});
			}
		};
		getConfiguration().addPropertyChangeListener(IStorageObject.PROP_NAME,
				nameListener);
		commandsComposite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				getConfiguration().removePropertyChangeListener(
						IStorageObject.PROP_NAME, nameListener);
			}
		});
	}

	/**
	 * @param form
	 */
	private void addActions(IManagedForm form) {
		IToolBarManager manager = form.getForm().getToolBarManager();
		manager.add(new ShowObjectInChart(getConfiguration()));
		manager.update(true);
		form.getForm().getForm().setToolBarVerticalAlignment(SWT.RIGHT);
	}

	public CSVConfigurationInfo getInfo() {
		return getConfiguration().getInfo();
	}

	public CSVConfiguration getConfiguration() {
		return (CSVConfiguration) ((StorageObjectEditorInput<?>) getEditorInput())
				.getStorageObject();
	}

	public CSVHistoricalDataInfo getHistoricalDataInfo() {
		return (CSVHistoricalDataInfo) getInfo().getHistoricalDataInfo();
	}

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private Text textName;
	private ComboViewer comboViewerCurrency;
	private Button btnAutomaticVerifyTick;
	private Text textRealTickSize;
	private Composite commandsComposite;
	private Button btnStartTradings;
	private CSVHistoricalDataInfoComposite historicalDataInfoComposite;

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
		IObservableValue textLocalSymbolObserveTextObserveWidget = SWTObservables
				.observeText(textLocalSymbol, SWT.Modify);
		IObservableValue getInfoSymbollocalSymbolObserveValue = BeansObservables
				.observeValue(self, "info.symbol.localSymbol");
		bindingContext.bindValue(textLocalSymbolObserveTextObserveWidget,
				getInfoSymbollocalSymbolObserveValue, null, null);
		//
		IObservableValue textTickValueObserveTextObserveWidget = SWTObservables
				.observeText(textTickValue, SWT.Modify);
		IObservableValue getInfoSymboltickValueObserveValue = BeansObservables
				.observeValue(self, "info.symbol.tickValue");
		bindingContext.bindValue(textTickValueObserveTextObserveWidget,
				getInfoSymboltickValueObserveValue, null, null);
		//
		IObservableValue textNameObserveTextObserveWidget = SWTObservables
				.observeText(textName, SWT.Modify);
		IObservableValue selfConfigurationnameObserveValue = BeansObservables
				.observeValue(self, "configuration.name");
		bindingContext.bindValue(textNameObserveTextObserveWidget,
				selfConfigurationnameObserveValue, null, null);
		//
		IObservableValue comboViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(comboViewerCurrency);
		IObservableValue selfInfosymbolcurrencyObserveValue = BeansObservables
				.observeValue(self, "info.symbol.currency");
		bindingContext.bindValue(comboViewerObserveSingleSelection,
				selfInfosymbolcurrencyObserveValue, null, null);
		//
		IObservableValue btnAutomaticVerifyTickObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnAutomaticVerifyTick);
		IObservableValue selfInfosymbolautoVerifyTickInfoObserveValue = BeansObservables
				.observeValue(self, "info.symbol.autoVerifyTickInfo");
		bindingContext.bindValue(
				btnAutomaticVerifyTickObserveSelectionObserveWidget,
				selfInfosymbolautoVerifyTickInfoObserveValue, null, null);
		//
		IObservableValue textRealTickSizeObserveTextObserveWidget = SWTObservables
				.observeText(textRealTickSize, SWT.Modify);
		IObservableValue selfInfosymbolrealTickSizeObserveValue = BeansObservables
				.observeValue(self, "info.symbol.realTickSize");
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StringToBigDecimalConverter());
		strategy.setAfterGetValidator(new RealTickSizeValidator());
		bindingContext.bindValue(textRealTickSizeObserveTextObserveWidget,
				selfInfosymbolrealTickSizeObserveValue, strategy, null);
		//
		IObservableValue btnStartTradingsObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnStartTradings);
		IObservableValue selfInfostartTradingObserveValue = BeansObservables
				.observeValue(self, "info.startTrading");
		bindingContext.bindValue(btnStartTradingsObserveSelectionObserveWidget,
				selfInfostartTradingObserveValue, null, null);
		//
		IObservableValue modelHistoricalDataInfoCompositeObserveValue = BeanProperties
				.value("model").observe(historicalDataInfoComposite);
		IObservableValue historicalDataInfoSelfObserveValue = BeanProperties
				.value("historicalDataInfo").observe(self);
		bindingContext.bindValue(modelHistoricalDataInfoCompositeObserveValue,
				historicalDataInfoSelfObserveValue, null, null);
		//
		return bindingContext;
	}
}
