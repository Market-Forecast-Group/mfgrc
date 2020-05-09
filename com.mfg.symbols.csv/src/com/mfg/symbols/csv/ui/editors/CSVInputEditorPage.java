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
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.chart.ui.actions.ShowObjectInChart;
import com.mfg.connector.csv.CSVHistoricalDataInfo;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfigurationInfo;
import com.mfg.symbols.inputs.persistence.InputsStorage;
import com.mfg.symbols.inputs.ui.adapters.SyntheticInput;
import com.mfg.symbols.inputs.ui.editors.InputTabComposite;
import com.mfg.symbols.ui.editors.AbstractSymbolEditor;
import com.mfg.ui.editors.EditorUtils;
import com.mfg.utils.DataBindingUtils;

/**
 * @author arian
 * 
 */
public class CSVInputEditorPage extends FormPage {
	private DataBindingContext m_bindingContext;
	private String formTitle;
	private CTabFolder tabFolder;
	private CTabItem inputTab;
	private CTabItem tbtmHistoricalData;

	private Section sctnCommands;
	private Composite commandsComposite;
	private InputTabComposite inputTabComposite;
	private CSVHistoricalDataInfoComposite historicalDataInfoComposite;
	private final CSVInputEditorPage self = this;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public CSVInputEditorPage(String id, String title) {
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
	public CSVInputEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText(formTitle == null ? "Input" : formTitle);
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		GridLayout gridLayout = new GridLayout(1, true);
		managedForm.getForm().getBody().setLayout(gridLayout);

		sctnCommands = managedForm.getToolkit().createSection(body,
				ExpandableComposite.TITLE_BAR);
		sctnCommands.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnCommands);
		sctnCommands.setText("Commands");

		commandsComposite = managedForm.getToolkit().createComposite(
				managedForm.getForm().getBody(), SWT.NONE);
		commandsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		managedForm.getToolkit().paintBordersFor(commandsComposite);
		commandsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new CTabFolder(managedForm.getForm().getBody(), SWT.BORDER);
		GridData gd_tabFolder = new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 1);
		gd_tabFolder.widthHint = 100;
		tabFolder.setLayoutData(gd_tabFolder);
		managedForm.getToolkit().adapt(tabFolder);
		managedForm.getToolkit().paintBordersFor(tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		tbtmHistoricalData = new CTabItem(tabFolder, SWT.NONE);
		tbtmHistoricalData.setText("Historical Data");

		Composite compositeHistoricalData = managedForm.getToolkit()
				.createComposite(tabFolder);
		tbtmHistoricalData.setControl(compositeHistoricalData);
		compositeHistoricalData.setLayout(new GridLayout(1, true));

		historicalDataInfoComposite = new CSVHistoricalDataInfoComposite(
				compositeHistoricalData, SWT.NONE);
		historicalDataInfoComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));
		managedForm.getToolkit().adapt(historicalDataInfoComposite);
		managedForm.getToolkit().paintBordersFor(historicalDataInfoComposite);

		inputTab = new CTabItem(tabFolder, SWT.NONE);
		inputTab.setText("Inputs");

		inputTabComposite = new InputTabComposite(tabFolder, SWT.NONE);
		inputTab.setControl(inputTabComposite);
		managedForm.getToolkit().paintBordersFor(inputTabComposite);

		afterCreateWidgets();
		m_bindingContext = initDataBindings();

		afterInitBindings();
	}

	private void afterInitBindings() {
		final InputsStorage inputsStorage = SymbolsPlugin.getDefault()
				.getInputsStorage();
		EditorUtils.registerBindingListenersToUpdateWorkspace(
				commandsComposite, inputsStorage, m_bindingContext,
				historicalDataInfoComposite.getDataBindingContext());
		DataBindingUtils.decorateBindings(m_bindingContext,
				historicalDataInfoComposite.getDataBindingContext());
	}

	public void setFormTitle(String title) {
		formTitle = title;
	}

	/**
	 * 
	 */
	private void afterCreateWidgets() {

		addActions(getManagedForm());
		tabFolder.setSelection(tbtmHistoricalData);

		getEditor().createCommandsSection(commandsComposite,
				getConfiguration(), null);

		initInputTab();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#getEditor()
	 */
	@Override
	public AbstractSymbolEditor getEditor() {
		return (AbstractSymbolEditor) super.getEditor();
	}

	private void addActions(IManagedForm form) {
		IToolBarManager manager = form.getForm().getToolBarManager();
		manager.add(new ShowObjectInChart(getConfiguration()) {
			@Override
			protected Menu fillMenu(Menu menu1) {
				super.fillMenu(menu1);

				MenuItem item = new MenuItem(menu1, SWT.PUSH);
				item.setText("Show in Synthetic Chart");
				item.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						showObject(new SyntheticInput(getConfiguration()));
					}

				});
				return menu1;
			}
		});
		manager.update(true);
		form.getForm().getForm().setToolBarVerticalAlignment(SWT.RIGHT);
	}

	public InputConfigurationInfo getInfo() {
		return getConfiguration().getInfo();
	}

	public CSVHistoricalDataInfo getHistoricalDataInfo() {
		return (CSVHistoricalDataInfo) getInfo().getHistoricalDataInfo();
	}

	/**
	 * @return
	 */
	public InputConfiguration getConfiguration() {
		return EditorUtils.getStorageObject(this);
	}

	/**
	 * 
	 */
	private void initInputTab() {
		inputTabComposite.setInfo(getInfo());
		inputTabComposite.initWidgets();

		EditorUtils.registerBindingListenersToUpdateWorkspace(
				inputTabComposite, SymbolsPlugin.getDefault()
						.getInputsStorage(), inputTabComposite
						.getBindingContext(), inputTabComposite
						.getIndicatorSettingsComposite()
						.getDataBindingContext());
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

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
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
