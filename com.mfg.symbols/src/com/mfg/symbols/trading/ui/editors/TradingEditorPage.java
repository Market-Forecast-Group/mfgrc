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

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.mfg.dm.symbols.HistoricalDataInfo;
import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;
import com.mfg.strategy.AbstractStrategyFactory;
import com.mfg.strategy.IStrategyFactory;
import com.mfg.strategy.IStrategyFactory.CreateTradingPageActionsArgs;
import com.mfg.strategy.IStrategySettings;
import com.mfg.strategy.ui.IdToStrategyFactoryConverter;
import com.mfg.strategy.ui.StrategyFactoryLabelProvider;
import com.mfg.strategy.ui.StrategyFactoryToIdConverter;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.inputs.configurations.InputConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfigurationInfo;
import com.mfg.symbols.trading.ui.actions.ChangeConfigurationSetAction;
import com.mfg.symbols.trading.ui.actions.OpenTradingViewAction;
import com.mfg.symbols.trading.ui.actions.ShowTradingInChartAction;
import com.mfg.symbols.trading.ui.commands.OpenDashboardHandler;
import com.mfg.symbols.trading.ui.views.AccountManagerView2;
import com.mfg.symbols.trading.ui.views.ITradingView;
import com.mfg.symbols.ui.ConfigurationSetsManager;
import com.mfg.symbols.ui.editors.AbstractSymbolEditor;
import com.mfg.ui.editors.EditorUtils;
import com.mfg.utils.DataBindingUtils;

/**
 * @author arian
 * 
 */
public class TradingEditorPage extends FormPage {
	private DataBindingContext m_bindingContext;

	/**
	 * 
	 */
	private static final String PROP_INFO = "info";
	private static final String PROP_CONFIGURATION = "configuration";
	private final String formTitle;
	private final TradingEditorPage self = this;
	TradingConfiguration configuration;
	private TradingConfigurationInfo info;

	private OpenTradingViewAction openAccountManager;
	private ChangeConfigurationSetAction changeConfigurationSetAction;

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public TradingEditorPage(String id, String title) {
		super(id, title);
		formTitle = title;
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
	public TradingEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		formTitle = title;
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
		managedForm.getForm().getBody().setLayout(new GridLayout(1, false));

		Section sctnCommands = managedForm.getToolkit().createSection(
				managedForm.getForm().getBody(), ExpandableComposite.TITLE_BAR);
		sctnCommands.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnCommands);
		sctnCommands.setText("Commands");

		_commandsComposite = managedForm.getToolkit().createComposite(
				sctnCommands, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(_commandsComposite);
		sctnCommands.setClient(_commandsComposite);
		_commandsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		_tradingCommandsComposite = new TradingCommandsComposite(
				_commandsComposite, SWT.NONE);
		managedForm.getToolkit().adapt(_tradingCommandsComposite);
		managedForm.getToolkit().paintBordersFor(_tradingCommandsComposite);

		Section sctnStrategy = managedForm.getToolkit().createSection(
				managedForm.getForm().getBody(), ExpandableComposite.TITLE_BAR);
		sctnStrategy.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		managedForm.getToolkit().paintBordersFor(sctnStrategy);
		sctnStrategy.setText("Strategy");

		Composite composite = managedForm.getToolkit().createComposite(
				sctnStrategy, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite);
		sctnStrategy.setClient(composite);
		composite.setLayout(new GridLayout(2, false));

		managedForm.getToolkit().createLabel(composite, "Strategy", SWT.NONE);

		strategyComboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = strategyComboViewer.getCombo();
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1);
		gd_combo.widthHint = 160;
		combo.setLayoutData(gd_combo);
		managedForm.getToolkit().paintBordersFor(combo);
		strategyComboViewer.setContentProvider(new ArrayContentProvider());
		strategyComboViewer
				.setLabelProvider(new StrategyFactoryLabelProvider());

		btnDoPaperTrading = new Button(composite, SWT.CHECK);
		btnDoPaperTrading.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		managedForm.getToolkit().adapt(btnDoPaperTrading, true, true);
		btnDoPaperTrading.setText("Do paper trading");

		Section sctnSettings = managedForm.getToolkit().createSection(
				managedForm.getForm().getBody(), ExpandableComposite.TITLE_BAR);
		sctnSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		managedForm.getToolkit().paintBordersFor(sctnSettings);
		sctnSettings.setText("Settings");

		Composite composite_2 = managedForm.getToolkit().createComposite(
				sctnSettings, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(composite_2);
		sctnSettings.setClient(composite_2);
		GridLayout gl_composite_2 = new GridLayout(1, false);
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		composite_2.setLayout(gl_composite_2);

		settingsTabFolder = new CTabFolder(composite_2, SWT.BORDER);
		GridData gd_settingsTabFolder = new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1);
		gd_settingsTabFolder.widthHint = 100;
		settingsTabFolder.setLayoutData(gd_settingsTabFolder);
		settingsTabFolder.setSimple(false);
		managedForm.getToolkit().adapt(settingsTabFolder);
		managedForm.getToolkit().paintBordersFor(settingsTabFolder);
		settingsTabFolder.setSelectionBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		tabStrategySettings = new CTabItem(settingsTabFolder, SWT.NONE);
		tabStrategySettings.setText("Strategy Settings");

		Composite composite_4 = managedForm.getToolkit().createComposite(
				settingsTabFolder, SWT.NONE);
		tabStrategySettings.setControl(composite_4);
		managedForm.getToolkit().paintBordersFor(composite_4);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));

		strategyTabFolder = new CTabFolder(composite_4, SWT.BORDER);
		managedForm.getToolkit().adapt(strategyTabFolder);
		managedForm.getToolkit().paintBordersFor(strategyTabFolder);
		strategyTabFolder.setSelectionBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		afterWidgetsCreate();

		m_bindingContext = initDataBindings();

		afterInitBindings();
	}

	private void afterInitBindings() {
		DataBindingUtils.decorateBindings(m_bindingContext);
	}

	/**
	 * 
	 */
	private void afterWidgetsCreate() {
		// -- strategy --
		AbstractStrategyFactory[] strategyFactories = SymbolsPlugin
				.getDefault().getStrategyFactories();
		AbstractStrategyFactory[] factories = strategyFactories;
		strategyComboViewer.setInput(factories);

		// -- strategies --
		{
			strategyComboViewer
					.addSelectionChangedListener(new ISelectionChangedListener() {

						@Override
						public void selectionChanged(SelectionChangedEvent event) {
							StructuredSelection sel = (StructuredSelection) event
									.getSelection();
							IStrategyFactory factory = (IStrategyFactory) sel
									.getFirstElement();
							String id = factory.getId();
							CTabItem tab = (CTabItem) strategyTabMap.get(id);
							if (tab != null) {
								settingsTabFolder
										.setSelection(tabStrategySettings);
								strategyTabFolder.setSelection(tab);
								createSelectedStrategyTab();
							}
							// addActions(getManagedForm(), factory);
						}
					});
			strategyTabMap = new HashMap<>();
			for (IStrategyFactory factory : strategyFactories) {
				IStrategySettings settings = getInfo().getStrategySettings(
						factory.getId());
				if (settings != null) {
					CTabItem tab = new CTabItem(strategyTabFolder, SWT.None);
					tab.setText(factory.getName());
					strategyTabMap.put(factory.getId(), tab);
					strategyTabMap.put(tab, factory);
				}
			}
			strategyTabFolder.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					createSelectedStrategyTab();
				}
			});
			CTabItem tab = (CTabItem) strategyTabMap.get(getInfo()
					.getStrategyFactoryId());
			if (tab == null) {
				if (strategyTabFolder.getTabList().length > 0) {
					strategyTabFolder.setSelection(0);
					createSelectedStrategyTab();
				}
			} else {
				strategyTabFolder.setSelection(tab);
				createSelectedStrategyTab();
			}
		}

		// -- commands --

		_tradingCommandsComposite.setConfiguration(getConfiguration());

		//
		// AbstractSymbolEditor editor = (AbstractSymbolEditor) getEditor();
		// UUID inputId = configuration.getInfo().getInputConfiguratioId();
		// InputConfiguration input = SymbolsPlugin.getDefault()
		// .getInputsStorage().findById(inputId);
		// editor.createCommandsSectionForInput(commandsComposite, input,
		// configuration);

		// image
		registerUpdateImageListeners();

		// ---
		createActions();
		addActions(getManagedForm(), factories);

		// --- name listener

		final PropertyChangeListener nameListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Display.getCurrent().asyncExec(new Runnable() {

					@Override
					public void run() {
						AbstractSymbolEditor editor = (AbstractSymbolEditor) getEditor();
						editor.setPageText(getIndex(), getConfiguration()
								.getName());
					}
				});
			}
		};
		getConfiguration().addPropertyChangeListener(IStorageObject.PROP_NAME,
				nameListener);
		_commandsComposite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				getConfiguration().removePropertyChangeListener(
						IStorageObject.PROP_NAME, nameListener);
			}
		});

		// UIUtils.updateLayout(UIUtils.getRootParent(commandsComposite));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose() {
		if (changeConfigurationSetAction != null) {
			changeConfigurationSetAction.dispose();
		}
		super.dispose();
	}

	/**
	 * 
	 */
	private void registerUpdateImageListeners() {
		final PropertyChangeListener configSetListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (PlatformUI.isWorkbenchRunning()) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							AbstractSymbolEditor editor = (AbstractSymbolEditor) getEditor();
							SymbolsPlugin.getDefault().getSetsManager();
							editor.setPageImage(getIndex(),
									ConfigurationSetsManager
											.getImage(configuration.getInfo()
													.getConfigurationSet()));
						}
					});
				}
			}
		};
		this._commandsComposite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				configuration.getInfo().removePropertyChangeListener(
						TradingConfigurationInfo.PROP_CONFIGURATION_SET,
						configSetListener);
			}
		});
		configuration.getInfo().addPropertyChangeListener(
				TradingConfigurationInfo.PROP_CONFIGURATION_SET,
				configSetListener);
	}

	private void createActions() {
		openAccountManager = new OpenTradingViewAction(configuration,
				AccountManagerView2.VIEW_ID, "Account Manager",
				SymbolsPlugin.PLUGIN_ID, SymbolsPlugin.STRATEGY_LOG_IMAGE_PATH);
		changeConfigurationSetAction = new ChangeConfigurationSetAction(
				new ITradingView() {

					@Override
					public void setConfigurationSet(int configurationSet) {
						getConfiguration().getInfo().setConfigurationSet(
								configurationSet);
					}

					@Override
					public void setConfiguration(
							TradingConfiguration aConfiguration) {
						// Documenting empty method to avoid warning.
					}

					@Override
					public IViewPart getPart() {
						return null;
					}

					@Override
					public int getConfigurationSet() {
						return this.getConfiguration().getInfo()
								.getConfigurationSet();
					}

					@Override
					public TradingConfiguration getConfiguration() {
						return TradingEditorPage.this.getConfiguration();
					}
				}, false);
	}

	void addActions(IManagedForm form, IStrategyFactory[] factories) {
		IToolBarManager manager = form.getForm().getToolBarManager();
		manager.removeAll();
		manager.add(changeConfigurationSetAction);
		manager.add(new Separator());

		for (IStrategyFactory factory : factories) {
			Action[] actions = factory
					.createTradingPageActions(new CreateTradingPageActionsArgs(
							configuration));
			if (actions != null) {
				for (Action action : actions) {
					manager.add(action);
				}
				if (actions.length > 0) {
					manager.add(new Separator());
				}
			}
		}
		manager.add(OpenDashboardHandler.createAction(configuration));
		manager.add(openAccountManager);
		manager.add(new ShowTradingInChartAction(configuration));

		((AbstractSymbolEditor) getEditor()).addExtraActions(manager);

		manager.update(true);
		form.getForm().getForm().setToolBarVerticalAlignment(SWT.RIGHT);
	}

	/**
	 * @return the info
	 */
	public TradingConfigurationInfo getInfo() {
		return info;
	}

	/**
	 * @param aInfo
	 *            the info to set
	 */
	public void setInfo(TradingConfigurationInfo aInfo) {
		this.info = aInfo;
		firePropertyChange(PROP_INFO);
		setDoPaperTrading(aInfo.isDoPaperTrading());
	}

	public boolean isDoPaperTrading() {
		return getInfo().isDoPaperTrading();
	}

	public void setDoPaperTrading(boolean doPaperTrading) {
		getInfo().setDoPaperTrading(doPaperTrading);
		firePropertyChange("doPaperTrading");
	}

	HistoricalDataInfo getHistoricalInfo() {
		InputConfiguration input = (InputConfiguration) PersistInterfacesPlugin
				.getDefault().findById(getInfo().getInputConfiguratioId());
		HistoricalDataInfo histInfo = input.getInfo().getHistoricalDataInfo();
		return histInfo;
	}

	/**
	 * @return the configuration
	 */
	public TradingConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param aConfiguration
	 *            the configuration to set
	 */
	public void setConfiguration(TradingConfiguration aConfiguration) {
		this.configuration = aConfiguration;
		firePropertyChange(PROP_CONFIGURATION);
		setInfo(aConfiguration.getInfo());
	}

	private transient final PropertyChangeSupport support = new PropertyChangeSupport(
			this);
	private ComboViewer strategyComboViewer;
	private Button btnDoPaperTrading;
	Map<Object, Object> brokerTabMap;
	Map<Object, Object> strategyTabMap;
	private Composite _commandsComposite;
	CTabFolder strategyTabFolder;
	CTabFolder settingsTabFolder;
	CTabItem tabStrategySettings;
	private TradingCommandsComposite _tradingCommandsComposite;

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

	/**
	 * @param tab
	 */
	void createSelectedStrategyTab() {
		CTabItem tab = strategyTabFolder.getSelection();
		if (tab.getControl() == null) {
			long t = currentTimeMillis();
			IStrategyFactory factory = (IStrategyFactory) strategyTabMap
					.get(tab);
			out.println("create strategy tab for " + factory.getName());
			IStrategySettings settings = getInfo().getStrategySettings(
					factory.getId());
			Composite parent = new Composite(strategyTabFolder, SWT.NONE);
			parent.setLayout(new FillLayout());

			IStrategyFactory.CreateSettingsEditorArgs arg = new IStrategyFactory.CreateSettingsEditorArgs(
					parent, settings);
			factory.createSettingsEditor(arg);
			EditorUtils.registerBindingListenersToUpdateWorkspace(
					strategyTabFolder, SymbolsPlugin.getDefault()
							.getTradingStorage(), arg.getBindings());
			tab.setControl(parent);
			out.println(currentTimeMillis() - t + "ms");
		}
	}

	public class DoPaperTradingValidator implements IValidator {

		@Override
		public IStatus validate(Object value) {
			Boolean b = (Boolean) value;
			HistoricalDataInfo histInfo = getHistoricalInfo();
			if (histInfo.allowPaperTrading() && histInfo.forceDoPaperTrading()
					&& !b.booleanValue()) {
				return ValidationStatus
						.error("The current data request mode requires to do paper trading");
			}
			return Status.OK_STATUS;
		}
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue strategyComboViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(strategyComboViewer);
		IObservableValue selfInfostrategyFactoryIdObserveValue = BeansObservables
				.observeValue(self, "info.strategyFactoryId");
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new StrategyFactoryToIdConverter());
		UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
		strategy_3.setConverter(new IdToStrategyFactoryConverter());
		bindingContext.bindValue(strategyComboViewerObserveSingleSelection,
				selfInfostrategyFactoryIdObserveValue, strategy, strategy_3);
		//
		IObservableValue observeSelectionBtnDoPaperTradingObserveWidget = WidgetProperties
				.selection().observe(btnDoPaperTrading);
		IObservableValue doPaperTradingSelfObserveValue = BeanProperties.value(
				"doPaperTrading").observe(self);
		UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setBeforeSetValidator(new DoPaperTradingValidator());
		bindingContext.bindValue(
				observeSelectionBtnDoPaperTradingObserveWidget,
				doPaperTradingSelfObserveValue, strategy_1, null);
		//
		return bindingContext;
	}
}
