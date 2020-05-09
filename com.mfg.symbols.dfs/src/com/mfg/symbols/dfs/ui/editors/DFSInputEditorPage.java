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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.chart.ui.actions.ShowObjectInChart;
import com.mfg.connector.dfs.DFSHistoricalDataInfo;
import com.mfg.dfs.data.MaturityStats;
import com.mfg.dm.speedControl.SpeedComposite3;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.dfs.configurations.DFSConfiguration;
import com.mfg.symbols.dfs.ui.DFSHistoricalDataInfoComposite;
import com.mfg.symbols.dfs.ui.DFSSlotsComposite;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.inputs.configurations.InputConfigurationInfo;
import com.mfg.symbols.inputs.ui.adapters.SyntheticInput;
import com.mfg.symbols.inputs.ui.editors.InputTabComposite;
import com.mfg.symbols.ui.editors.AbstractSymbolEditor;
import com.mfg.ui.editors.EditorUtils;
import com.mfg.utils.DataBindingUtils;

/**
 * @author arian
 * 
 */
public class DFSInputEditorPage extends FormPage {
	private DataBindingContext m_bindingContext;
	private String formTitle;
	private CTabFolder tabFolder;
	private CTabItem inputTab;
	private CTabItem tbtmHistoricalData;

	private Section sctnCommands;
	private InputTabComposite inputTabComposite;
	private DFSHistoricalDataInfoComposite historicalDataInfoComposite;
	private DFSSlotsComposite historicalDataSlotsComposite;
	private Section sctnDataType;
	private Composite commandsComposite;

	/**
	 * Create the form page.
	 * 
	 * @param title
	 */
	public DFSInputEditorPage(String id, String title) {
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
	public DFSInputEditorPage(FormEditor editor, String id, String title) {
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
		FillLayout fillLayout = new FillLayout();
		managedForm.getForm().getBody().setLayout(fillLayout);

		ScrolledComposite scrolledComposite = new ScrolledComposite(managedForm
				.getForm().getBody(), SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		managedForm.getToolkit().adapt(scrolledComposite);
		managedForm.getToolkit().paintBordersFor(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite content = managedForm.getToolkit().createComposite(
				scrolledComposite, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(content);
		scrolledComposite.setContent(content);
		content.setLayout(new GridLayout(1, false));

		sctnCommands = managedForm.getToolkit().createSection(content,
				ExpandableComposite.TITLE_BAR);
		sctnCommands.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnCommands);
		sctnCommands.setText("Commands");

		commandsComposite = managedForm.getToolkit().createComposite(content,
				SWT.NONE);
		commandsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		managedForm.getToolkit().paintBordersFor(commandsComposite);
		commandsComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		tabFolder = new CTabFolder(content, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true,
				1, 1));
		managedForm.getToolkit().adapt(tabFolder);
		managedForm.getToolkit().paintBordersFor(tabFolder);

		tbtmHistoricalData = new CTabItem(tabFolder, SWT.NONE);
		tbtmHistoricalData.setText("Historical Data");

		Composite compositeHistoricalData = managedForm.getToolkit()
				.createComposite(tabFolder);
		tbtmHistoricalData.setControl(compositeHistoricalData);
		compositeHistoricalData.setLayout(new GridLayout(2, false));

		historicalDataInfoComposite = new DFSHistoricalDataInfoComposite(
				compositeHistoricalData, SWT.NONE);
		historicalDataInfoComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, false, true, 1, 2));
		managedForm.getToolkit().adapt(historicalDataInfoComposite);
		managedForm.getToolkit().paintBordersFor(historicalDataInfoComposite);

		sctnDataType = managedForm.getToolkit().createSection(
				compositeHistoricalData, ExpandableComposite.TITLE_BAR);
		sctnDataType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnDataType);
		sctnDataType.setText("Data Type");

		historicalDataSlotsComposite = new DFSSlotsComposite(
				compositeHistoricalData, SWT.NONE);
		historicalDataSlotsComposite.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		historicalDataSlotsComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, false, true, 1, 1));
		managedForm.getToolkit().adapt(historicalDataSlotsComposite);
		managedForm.getToolkit().paintBordersFor(historicalDataSlotsComposite);

		inputTab = new CTabItem(tabFolder, SWT.NONE);
		inputTab.setText("Inputs");

		inputTabComposite = new InputTabComposite(tabFolder, SWT.NONE);
		inputTab.setControl(inputTabComposite);
		managedForm.getToolkit().paintBordersFor(inputTabComposite);

		/*$hide$*/ afterCreateWidgets();
		m_bindingContext = initDataBindings();

		/*$hide$*/ afterInitBindings();
	}

	private void afterInitBindings() {
		EditorUtils.registerBindingListenersToUpdateWorkspace(
				commandsComposite, SymbolsPlugin.getDefault()
						.getInputsStorage(), m_bindingContext,
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
		try {
			final DFSEditor editor = (DFSEditor) getEditor();

			MaturityStats maturityStats = editor.getMaturityStats();

			DFSConfiguration symbolConfig = editor.getConfiguration();

			final InputConfiguration inputConf = getConfiguration();

			DFSHistoricalDataInfo histInfo = getHistoricalDataInfo();
			historicalDataSlotsComposite.setInfo(symbolConfig, histInfo,
					maturityStats, editor);
			createHistoricalDataTab();

			addActions(getManagedForm());
			tabFolder.setSelection(tbtmHistoricalData);

			SpeedComposite3 speedControl = (SpeedComposite3) getEditor()
					.createCommandsSection(commandsComposite, inputConf, null);
			historicalDataInfoComposite.init(histInfo, speedControl);

			initInputTab();

			final PropertyChangeListener nameListener = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Display.getCurrent().asyncExec(new Runnable() {

						@Override
						public void run() {
							editor.setPageText(getIndex(), inputConf.getName());
						}
					});
				}
			};
			inputConf.addPropertyChangeListener(IStorageObject.PROP_NAME,
					nameListener);
			commandsComposite.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					inputConf.removePropertyChangeListener(
							IStorageObject.PROP_NAME, nameListener);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void createHistoricalDataTab() {
		// Adding a comment to avoid empty block warning.
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

		getEditor().addExtraActions(manager);

		manager.update(true);
		form.getForm().getForm().setToolBarVerticalAlignment(SWT.RIGHT);
	}

	public InputConfigurationInfo getInfo() {
		return getConfiguration().getInfo();
	}

	public DFSHistoricalDataInfo getHistoricalDataInfo() {
		return (DFSHistoricalDataInfo) getInfo().getHistoricalDataInfo();
	}

	/**
	 * @return
	 */
	public InputConfiguration getConfiguration() {
		return EditorUtils.getStorageObject(this);
	}

	
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
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

	protected static DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
