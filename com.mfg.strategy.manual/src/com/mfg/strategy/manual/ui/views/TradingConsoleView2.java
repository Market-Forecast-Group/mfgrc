package com.mfg.strategy.manual.ui.views;

import static java.lang.System.out;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mfg.broker.IOrderMfg.EXECUTION_TYPE;
import com.mfg.dm.TickDataSource;
import com.mfg.interfaces.trading.IStrategy;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.WorkspaceStorageAdapter;
import com.mfg.strategy.AutoStop;
import com.mfg.strategy.ChildToExit;
import com.mfg.strategy.EntryExitOrderType;
import com.mfg.strategy.FinalStrategy;
import com.mfg.strategy.IManualStrategy;
import com.mfg.strategy.ManualStrategySettings;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.builder.StrategyBuilderPlugin;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.strategy.builder.persistence.StrategyBuilderStorage;
import com.mfg.strategy.builder.ui.AutomaticStrategySettingsComposite.StrategyInfoLabelProvider;
import com.mfg.strategy.builder.utils.ObjectsJSONFileIO;
import com.mfg.strategy.manual.ManualStrategy;
import com.mfg.strategy.manual.ManualStrategyPlugin;
import com.mfg.strategy.manual.TradingConsoleWindowEnvironment;
import com.mfg.strategy.manual.TrailingStatus;
import com.mfg.strategy.manual.WindowCommand;
import com.mfg.strategy.manual.interfaces.IAccountStatus;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.jobs.ISymbolJobChangeListener;
import com.mfg.symbols.jobs.InputPipeChangeEvent;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobChangeEvent;
import com.mfg.symbols.jobs.TradingPipe;
import com.mfg.symbols.jobs.TradingPipeChangeEvent;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.persistence.TradingStorage;
import com.mfg.symbols.trading.ui.actions.ChangeConfigurationSetAction;
import com.mfg.symbols.trading.ui.actions.DisconnectTradingViewAction;
import com.mfg.symbols.trading.ui.views.ITradingView;
import com.mfg.utils.DataBindingUtils;
import com.mfg.utils.U;
import com.mfg.utils.ui.ShowHideLayout;
import com.mfg.utils.ui.UIUtils;

public class TradingConsoleView2 extends ViewPart implements ITradingView,
		ISymbolJobChangeListener {

	/**
	 * 
	 */
	private static final String KEY_SETTINGS_VISIBLE = "settingsVisible";

	public static final String VIEW_ID = "com.mfg.strategy.manual.views.tradingConsole";

	private static final String KEY_TRADE_CONFIG_ID = "tradingConfiguration";

	private static List<StrategyInfo> getCompletePatterns() {
		List<StrategyInfo> patterns = StrategyBuilderPlugin.getDefault()
				.getStrategiesStorage().getObjects();
		List<StrategyInfo> res = new ArrayList<>();
		for (StrategyInfo info : patterns) {
			EventsCanvasModel strategyModel = ObjectsJSONFileIO.getInstance()
					.readModelFromJSON(info.getPatternJSON());
			EventGeneral pattern = strategyModel.exportMe();
			if (pattern.isNotPure())
				res.add(info);
		}
		return res;
	}

	private static List<StrategyInfo> getPurePatterns(boolean entry) {
		List<StrategyInfo> patterns = StrategyBuilderPlugin.getDefault()
				.getStrategiesStorage().getObjects();
		List<StrategyInfo> res = new ArrayList<>();
		for (StrategyInfo info : patterns) {
			EventsCanvasModel strategyModel = ObjectsJSONFileIO.getInstance()
					.readModelFromJSON(info.getPatternJSON());
			EventGeneral pattern = strategyModel.exportMe();
			if (pattern.isPure(entry))
				res.add(info);
		}
		return res;
	}

	private static StrategyInfo getSelected(StructuredViewer combo) {
		StrategyInfo info;
		ISelection selection = combo.getSelection();
		if (selection.isEmpty()) {
			info = null;
		} else {
			info = (StrategyInfo) ((IStructuredSelection) selection)
					.getFirstElement();
		}
		return info;
	}

	DataBindingContext m_bindingContext;
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());
	private Text txtNewText;
	private Text text;
	private Text txtNewText_1;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text txtLevelLong;
	private Text txtLevelShort;
	private Composite leftSideComposite;
	private Composite manualSettingsComposite;
	Composite parentComposite;
	private Action settingsAction;
	private Composite showHideAutoStrategiesComposite;
	TradingPipe tradingPipe;
	ManualStrategy _manualStrategy;
	ManualStrategySettings settings;
	private final TradingConsoleView2 self = this;
	private Spinner spinner;
	private Button btnUseOrderConfirmation;
	private ComboViewer entryExitOrderTypeViewer;
	private ComboViewer entryExitChildToExitViewer;
	private ComboViewer stopLossAutoStopViewer;
	private ComboViewer takeProfitAutoStopViewer;
	private ComboViewer stopLossStopTypeViewer;
	private ComboViewer takeProfitStopTypeViewer;

	private Button btnThScale;

	private TradingConsoleWindowEnvironment _winEnv;

	private Runnable _stateListener;

	private Button _btnRcShort;

	private PortfolioStrategy _portfolioStrategy;

	private Button _btnUseAutoStrategy;

	private TradingConfiguration _configuration;

	private ChangeConfigurationSetAction _changeConfigurationSetAction;

	private ComboViewer _comboViewerEntry;

	private ComboViewer _comboViewerExit;

	private ComboViewer _comboViewerComplete;

	private boolean _autotrading;

	private FinalStrategy _autoStrategy;

	private Button _btnEntryAndExit;

	private Button _btnEntry;

	private Button _btnExitOnly;

	private String _startText;

	private Hyperlink _btnStartStopAutoStrategy;

	private String _stopText;

	private Spinner _entryExitTHScaleSpinner;

	private Spinner _numberOfTicksSpinner;

	private Spinner _numOfTicksSpinner;

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	private Button btnCloseAll;

	private Label lblLongQuantity;

	private Label lblShortQuantity;

	private Button btnOpenLong;

	private Button btnOpenShort;

	private Button btnCloseLong;

	private Button btnCloseShort;

	private Button btnAvgLong;

	private Button btnAvgShort;

	private Button btnStopAndReverseLong;

	private Button btnStopAndReverseShort;

	private Button btnRcLong;

	private Button btnClLong;

	private Button btnClShort;

	private Button btnScLong;

	private Button btnScShort;

	private Button btnCancelTrailLong;

	private Button btnCloseAllLong;

	private Button btnCancelTrailShort;

	private Button btnCloseAllShort;

	private Button btnCancelLongPending;

	private Button btnCancelShortPending;

	private Button btnCancelAllPending;

	private Boolean initialSettingsVisible;

	private String initialConfigurationId;

	private int _confSet;

	public TradingConsoleView2() {
		settings = new ManualStrategySettings();
	}

	private void addActions() {
		IToolBarManager manager = getViewSite().getActionBars()
				.getToolBarManager();
		manager.add(_changeConfigurationSetAction);
		manager.add(settingsAction);
		getViewSite().getActionBars().getMenuManager()
				.add(new DisconnectTradingViewAction());
	}

	/**
	 * @param disposeListener
	 */
	private void addDisposeListener(DisposeListener disposeListener) {
		// nothing

	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	private void afterCreatedWidgets() {
		createActions();
		addActions();

		// combos
		entryExitOrderTypeViewer.setInput(EntryExitOrderType.values());
		entryExitChildToExitViewer.setInput(ChildToExit.values());
		stopLossAutoStopViewer.setInput(AutoStop.values());
		stopLossStopTypeViewer.setInput(new Object[] { EXECUTION_TYPE.STOP,
				EXECUTION_TYPE.STOP_LIMIT });
		takeProfitAutoStopViewer.setInput(AutoStop.values());
		takeProfitStopTypeViewer.setInput(new Object[] { EXECUTION_TYPE.LIMIT,
				EXECUTION_TYPE.MIT });

		updateSettingsVisibility();

		if (initialConfigurationId != null) {
			TradingStorage storage = SymbolsPlugin.getDefault()
					.getTradingStorage();
			_configuration = storage.findById(initialConfigurationId);
		}
		setConfiguration(_configuration);

		UIUtils.updateLayout(UIUtils.getRootParent(parentComposite));
		connectTheStrategiesWorkspace();
	}

	private void afterInitDataBindings() {
		DataBindingUtils.disposeBindingContextAtControlDispose(btnAvgLong,
				m_bindingContext);
	}

	protected void avgLongClicked() {
		_winEnv.executeCommand(WindowCommand.AVG_LONG,
				createSettingsToCommand());
	}

	protected void avgShortClicked() {
		_winEnv.executeCommand(WindowCommand.AVG_SHORT,
				createSettingsToCommand());
	}

	/**
	 * 
	 */
	protected void cancelAllPending() {
		_winEnv.executeCommand(WindowCommand.CANCEL_ALL_PENDING, settings);
	}

	/**
	 * 
	 */
	protected void cancelLongPending() {
		_winEnv.executeCommand(WindowCommand.CANCEL_LONG_PENDING, settings);
	}

	/**
	 * 
	 */
	protected void cancelShortPending() {
		_winEnv.executeCommand(WindowCommand.CANCEL_SHORT_PENDING, settings);
	}

	protected void cancelTrailLongClicked() {
		_winEnv.executeCommand(WindowCommand.CANCEL_TRAIL_LONG,
				createSettingsToCommand());
	}

	protected void cancelTrailShortClicked() {
		_winEnv.executeCommand(WindowCommand.CANCEL_TRAIL_SHORT,
				createSettingsToCommand());
	}

	protected void clLongClicked() {
		_winEnv.executeCommand(WindowCommand.CL_LONG, createSettingsToCommand());
	}

	protected void closeAllClicked() {
		_winEnv.executeCommand(WindowCommand.CLOSE_ALL,
				createSettingsToCommand());
	}

	protected void closeAllLongClicked() {
		_winEnv.executeCommand(WindowCommand.CLOSE_ALL_LONG,
				createSettingsToCommand());
	}

	/**
	 * 
	 */
	protected void closeAllShortClicked() {
		_winEnv.executeCommand(WindowCommand.CLOSE_ALL_SHORT,
				createSettingsToCommand());
	}

	protected void closeLongClicked() {
		_winEnv.executeCommand(WindowCommand.CLOSE_LONG,
				createSettingsToCommand());
	}

	protected void closeShortClicked() {
		_winEnv.executeCommand(WindowCommand.CLOSE_SHORT,
				createSettingsToCommand());
	}

	protected void clShortClicked() {
		_winEnv.executeCommand(WindowCommand.CL_SHORT,
				createSettingsToCommand());
	}

	private void connectTheStrategiesWorkspace() {
		fillPatterns();
		final WorkspaceStorageAdapter listener = new WorkspaceStorageAdapter() {
			@Override
			public void storageChanged(IWorkspaceStorage storage) {
				fillPatterns();
				m_bindingContext.updateTargets();
			}
		};
		final StrategyBuilderStorage storage = StrategyBuilderPlugin
				.getDefault().getStrategiesStorage();
		storage.addStorageListener(listener);
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				storage.removeStorageListener(listener);
			}
		});
	}

	/**
	 * 
	 */
	private void createActions() {
		settingsAction = new Action("Settings", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				updateSettingsVisibility();
			}
		};
		settingsAction.setImageDescriptor(ManualStrategyPlugin
				.getBundledImageDescriptor("icons/configure.gif"));
		settingsAction.setChecked(initialSettingsVisible != null
				&& initialSettingsVisible.booleanValue());

		_changeConfigurationSetAction = new ChangeConfigurationSetAction(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		formToolkit.adapt(scrolledComposite);
		formToolkit.paintBordersFor(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		parentComposite = formToolkit.createComposite(scrolledComposite,
				SWT.NONE);
		formToolkit.paintBordersFor(parentComposite);
		parentComposite.setLayout(new GridLayout(2, false));

		leftSideComposite = new Composite(parentComposite, SWT.NONE);
		leftSideComposite.setLayout(new ShowHideLayout());
		leftSideComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1));
		formToolkit.adapt(leftSideComposite);
		formToolkit.paintBordersFor(leftSideComposite);

		manualSettingsComposite = new Composite(leftSideComposite, SWT.NONE);
		GridLayout gl_manualSettingsComposite = new GridLayout(1, false);
		gl_manualSettingsComposite.verticalSpacing = 0;
		gl_manualSettingsComposite.marginWidth = 0;
		gl_manualSettingsComposite.marginHeight = 0;
		manualSettingsComposite.setLayout(gl_manualSettingsComposite);
		formToolkit.adapt(manualSettingsComposite);
		formToolkit.paintBordersFor(manualSettingsComposite);

		Section sctnBasicSettings = formToolkit.createSection(
				manualSettingsComposite, ExpandableComposite.TWISTIE
						| ExpandableComposite.TITLE_BAR);
		sctnBasicSettings.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 1, 1));
		formToolkit.paintBordersFor(sctnBasicSettings);
		sctnBasicSettings.setText("Basic Settings");
		sctnBasicSettings.setExpanded(true);

		Composite composite_1 = formToolkit.createComposite(sctnBasicSettings,
				SWT.NONE);
		formToolkit.paintBordersFor(composite_1);
		sctnBasicSettings.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, false));

		formToolkit.createLabel(composite_1, "Quantity", SWT.NONE);

		spinner = new Spinner(composite_1, SWT.BORDER);
		formToolkit.adapt(spinner);
		formToolkit.paintBordersFor(spinner);

		formToolkit.createLabel(composite_1, "Max Quantity", SWT.NONE);

		txtNewText = formToolkit.createText(composite_1, "New Text", SWT.NONE);
		txtNewText.setText("");
		txtNewText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		btnUseOrderConfirmation = new Button(composite_1, SWT.CHECK);
		btnUseOrderConfirmation.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		formToolkit.adapt(btnUseOrderConfirmation, true, true);
		btnUseOrderConfirmation.setText("Use Manual Order Confirmation");

		Section sctnEntryexit = formToolkit.createSection(
				manualSettingsComposite, ExpandableComposite.TWISTIE
						| ExpandableComposite.TITLE_BAR);
		sctnEntryexit.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 1, 1));
		formToolkit.paintBordersFor(sctnEntryexit);
		sctnEntryexit.setText("Entry/Exit");
		sctnEntryexit.setExpanded(true);

		Composite composite_2 = formToolkit.createComposite(sctnEntryexit,
				SWT.NONE);
		formToolkit.paintBordersFor(composite_2);
		sctnEntryexit.setClient(composite_2);
		composite_2.setLayout(new GridLayout(2, false));

		formToolkit.createLabel(composite_2, "Order Type", SWT.NONE);

		entryExitOrderTypeViewer = new ComboViewer(composite_2, SWT.READ_ONLY);
		Combo combo = entryExitOrderTypeViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		formToolkit.paintBordersFor(combo);
		entryExitOrderTypeViewer.setContentProvider(new ArrayContentProvider());
		entryExitOrderTypeViewer.setLabelProvider(new LabelProvider());

		formToolkit.createLabel(composite_2, "Limit Price", SWT.NONE);

		text = formToolkit.createText(composite_2, "New Text", SWT.NONE);
		text.setText("");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnThScale = new Button(composite_2, SWT.CHECK);
		formToolkit.adapt(btnThScale, true, true);
		btnThScale.setText("TH Scale");

		_entryExitTHScaleSpinner = new Spinner(composite_2, SWT.BORDER);
		formToolkit.adapt(_entryExitTHScaleSpinner);
		formToolkit.paintBordersFor(_entryExitTHScaleSpinner);

		formToolkit.createLabel(composite_2, "Child to Exit", SWT.NONE);

		entryExitChildToExitViewer = new ComboViewer(composite_2, SWT.READ_ONLY);
		entryExitChildToExitViewer.setUseHashlookup(true);
		Combo combo_1 = entryExitChildToExitViewer.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(combo_1);
		entryExitChildToExitViewer.setLabelProvider(new LabelProvider());
		entryExitChildToExitViewer
				.setContentProvider(new ArrayContentProvider());

		Section sctnStopLoss = formToolkit.createSection(
				manualSettingsComposite, ExpandableComposite.TWISTIE
						| ExpandableComposite.TITLE_BAR);
		sctnStopLoss.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
				false, 1, 1));
		formToolkit.paintBordersFor(sctnStopLoss);
		sctnStopLoss.setText("Stop Loss");
		sctnStopLoss.setExpanded(true);

		Composite composite_3 = formToolkit.createComposite(sctnStopLoss,
				SWT.NONE);
		formToolkit.paintBordersFor(composite_3);
		sctnStopLoss.setClient(composite_3);
		composite_3.setLayout(new GridLayout(2, false));

		formToolkit.createLabel(composite_3, "Auto Stop", SWT.NONE);

		stopLossAutoStopViewer = new ComboViewer(composite_3, SWT.READ_ONLY);
		stopLossAutoStopViewer.setUseHashlookup(true);
		Combo combo_2 = stopLossAutoStopViewer.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(combo_2);
		stopLossAutoStopViewer.setLabelProvider(new LabelProvider());
		stopLossAutoStopViewer.setContentProvider(new ArrayContentProvider());

		formToolkit.createLabel(composite_3, "Number of Ticks", SWT.NONE);

		_numberOfTicksSpinner = new Spinner(composite_3, SWT.BORDER);
		formToolkit.adapt(_numberOfTicksSpinner);
		formToolkit.paintBordersFor(_numberOfTicksSpinner);

		formToolkit.createLabel(composite_3, "Stop Type", SWT.NONE);

		stopLossStopTypeViewer = new ComboViewer(composite_3, SWT.READ_ONLY);
		stopLossStopTypeViewer.setUseHashlookup(true);
		Combo combo_3 = stopLossStopTypeViewer.getCombo();
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(combo_3);
		stopLossStopTypeViewer.setLabelProvider(new LabelProvider());
		stopLossStopTypeViewer.setContentProvider(new ArrayContentProvider());

		formToolkit.createLabel(composite_3, "Trigger Price", SWT.NONE);

		txtNewText_1 = formToolkit
				.createText(composite_3, "New Text", SWT.NONE);
		txtNewText_1.setText("");
		txtNewText_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		formToolkit.createLabel(composite_3, "Limit Price", SWT.NONE);

		text_1 = formToolkit.createText(composite_3, "New Text", SWT.NONE);
		text_1.setText("");
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Section sctnTakeProfit = formToolkit.createSection(
				manualSettingsComposite, ExpandableComposite.TWISTIE
						| ExpandableComposite.TITLE_BAR);
		sctnTakeProfit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				true, 1, 1));
		formToolkit.paintBordersFor(sctnTakeProfit);
		sctnTakeProfit.setText("Take Profit");
		sctnTakeProfit.setExpanded(true);

		Composite composite_4 = formToolkit.createComposite(sctnTakeProfit,
				SWT.NONE);
		formToolkit.paintBordersFor(composite_4);
		sctnTakeProfit.setClient(composite_4);
		composite_4.setLayout(new GridLayout(2, false));

		formToolkit.createLabel(composite_4, "Auto Stop", SWT.NONE);

		takeProfitAutoStopViewer = new ComboViewer(composite_4, SWT.READ_ONLY);
		takeProfitAutoStopViewer.setUseHashlookup(true);
		Combo combo_4 = takeProfitAutoStopViewer.getCombo();
		combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(combo_4);
		takeProfitAutoStopViewer.setLabelProvider(new LabelProvider());
		takeProfitAutoStopViewer.setContentProvider(new ArrayContentProvider());

		formToolkit.createLabel(composite_4, "Number of Ticks", SWT.NONE);

		_numOfTicksSpinner = new Spinner(composite_4, SWT.BORDER);
		formToolkit.adapt(_numOfTicksSpinner);
		formToolkit.paintBordersFor(_numOfTicksSpinner);

		formToolkit.createLabel(composite_4, "Stop Type", SWT.NONE);

		takeProfitStopTypeViewer = new ComboViewer(composite_4, SWT.READ_ONLY);
		takeProfitStopTypeViewer.setUseHashlookup(true);
		Combo combo_5 = takeProfitStopTypeViewer.getCombo();
		combo_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(combo_5);
		takeProfitStopTypeViewer.setLabelProvider(new LabelProvider());
		takeProfitStopTypeViewer.setContentProvider(new ArrayContentProvider());

		formToolkit.createLabel(composite_4, "Trigger Price", SWT.NONE);

		text_2 = formToolkit.createText(composite_4, "New Text", SWT.NONE);
		text_2.setText("");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		formToolkit.createLabel(composite_4, "Limit Price", SWT.NONE);

		text_3 = formToolkit.createText(composite_4, "New Text", SWT.NONE);
		text_3.setText("");
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));

		Composite rightSideComposite = formToolkit.createComposite(
				parentComposite, SWT.NONE);
		rightSideComposite.setLayout(new GridLayout(2, true));
		rightSideComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
				false, 1, 1));
		formToolkit.paintBordersFor(rightSideComposite);

		btnCloseAll = formToolkit.createButton(rightSideComposite, "Close All",
				SWT.NONE);
		btnCloseAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));
		btnCloseAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				closeAllClicked();
			}
		});

		btnCancelAllPending = formToolkit.createButton(rightSideComposite,
				"Cancel All Pending", SWT.NONE);
		btnCancelAllPending.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 2, 1));
		btnCancelAllPending.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelAllPending();
			}
		});
		lblLongQuantity = formToolkit.createLabel(rightSideComposite,
				"Quantity: 0", SWT.NONE);

		lblShortQuantity = formToolkit.createLabel(rightSideComposite,
				"Quantity: 0", SWT.NONE);

		btnOpenLong = formToolkit.createButton(rightSideComposite, "Open Long",
				SWT.NONE);
		btnOpenLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openLongClicked();
			}
		});
		btnOpenLong.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,
				1, 1));
		btnOpenLong.setBounds(0, 0, 68, 23);

		btnOpenShort = formToolkit.createButton(rightSideComposite,
				"Open Short", SWT.NONE);
		btnOpenShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnOpenShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openShortClicked();
			}
		});

		btnCancelLongPending = formToolkit.createButton(rightSideComposite,
				"Cancel Long Pending", SWT.NONE);
		btnCancelLongPending.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelLongPending();
			}
		});
		btnCancelLongPending.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		btnCancelShortPending = formToolkit.createButton(rightSideComposite,
				"Cancel Short Pending", SWT.NONE);
		btnCancelShortPending.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelShortPending();
			}
		});
		btnCancelShortPending.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));

		btnCloseLong = formToolkit.createButton(rightSideComposite,
				"Close Long", SWT.NONE);
		btnCloseLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				closeLongClicked();
			}
		});
		btnCloseLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		btnCloseShort = formToolkit.createButton(rightSideComposite,
				"Close Short", SWT.NONE);
		btnCloseShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnCloseShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				closeShortClicked();
			}
		});

		btnAvgLong = formToolkit.createButton(rightSideComposite, "Avg Long",
				SWT.NONE);
		btnAvgLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnAvgLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				avgLongClicked();
			}
		});

		btnAvgShort = formToolkit.createButton(rightSideComposite, "Avg Short",
				SWT.NONE);
		btnAvgShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnAvgShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				avgShortClicked();
			}
		});

		btnStopAndReverseLong = formToolkit.createButton(rightSideComposite,
				"Stop And Reverse", SWT.NONE);
		btnStopAndReverseLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		btnStopAndReverseLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sarLongClicked();
			}
		});

		btnStopAndReverseShort = formToolkit.createButton(rightSideComposite,
				"Stop And Reverse", SWT.NONE);
		btnStopAndReverseShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		btnStopAndReverseShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sarShortClicked();
			}
		});

		btnRcLong = formToolkit.createButton(rightSideComposite, "RC",
				SWT.TOGGLE);
		btnRcLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnRcLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rcLongClicked();
			}
		});

		_btnRcShort = formToolkit.createButton(rightSideComposite, "RC",
				SWT.TOGGLE);
		_btnRcShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		_btnRcShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rcShortClicked();
			}
		});

		btnClLong = formToolkit.createButton(rightSideComposite, "CL",
				SWT.TOGGLE);
		btnClLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnClLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clLongClicked();
			}
		});

		btnClShort = formToolkit.createButton(rightSideComposite, "CL",
				SWT.TOGGLE);
		btnClShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnClShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clShortClicked();
			}
		});

		btnScLong = formToolkit.createButton(rightSideComposite, "SC",
				SWT.TOGGLE);
		btnScLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnScLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scLongClicked();
			}
		});

		btnScShort = formToolkit.createButton(rightSideComposite, "SC",
				SWT.TOGGLE);
		btnScShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnScShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scShortClicked();
			}
		});

		Composite composite_6 = formToolkit.createComposite(rightSideComposite,
				SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		formToolkit.paintBordersFor(composite_6);
		composite_6.setLayout(new GridLayout(3, false));

		Button btnDecrLevelLong = formToolkit.createButton(composite_6, "",
				SWT.NONE);
		btnDecrLevelLong.setImage(ResourceManager.getPluginImage(
				"com.mfg.strategy.manual", "icons/backward_nav.gif"));
		btnDecrLevelLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				decrLevelLongClicked();
			}
		});

		txtLevelLong = formToolkit.createText(composite_6, "New Text",
				SWT.CENTER);
		txtLevelLong.setText("3");
		txtLevelLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Button btnIncrLevelLong = formToolkit.createButton(composite_6, "",
				SWT.NONE);
		btnIncrLevelLong.setImage(ResourceManager.getPluginImage(
				"com.mfg.strategy.manual", "icons/forward_nav.gif"));
		btnIncrLevelLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				incrLevelLongClicked();
			}
		});

		Composite composite_7 = formToolkit.createComposite(rightSideComposite,
				SWT.NONE);
		composite_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		formToolkit.paintBordersFor(composite_7);
		composite_7.setLayout(new GridLayout(3, false));

		Button btnDecrLevelShort = formToolkit.createButton(composite_7, "",
				SWT.NONE);
		btnDecrLevelShort.setImage(ResourceManager.getPluginImage(
				"com.mfg.strategy.manual", "icons/backward_nav.gif"));
		btnDecrLevelShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				decrLevelShortClicked();
			}
		});

		txtLevelShort = formToolkit.createText(composite_7, "New Text",
				SWT.CENTER);
		txtLevelShort.setText("3");
		txtLevelShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Button btnIncrLevelShort = formToolkit.createButton(composite_7, "",
				SWT.NONE);
		btnIncrLevelShort.setImage(ResourceManager.getPluginImage(
				"com.mfg.strategy.manual", "icons/forward_nav.gif"));
		btnIncrLevelShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				incrLevelShortClicked();
			}
		});

		btnCancelTrailLong = formToolkit.createButton(rightSideComposite,
				"Cancel Trail", SWT.NONE);
		btnCancelTrailLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		btnCancelTrailLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelTrailLongClicked();
			}
		});

		btnCancelTrailShort = formToolkit.createButton(rightSideComposite,
				"Cancel Trail", SWT.NONE);
		btnCancelTrailShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		btnCancelTrailShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelTrailShortClicked();
			}
		});

		btnCloseAllLong = formToolkit.createButton(rightSideComposite,
				"Close All Long", SWT.NONE);
		btnCloseAllLong.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnCloseAllLong.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				closeAllLongClicked();
			}
		});

		btnCloseAllShort = formToolkit.createButton(rightSideComposite,
				"Close All Short", SWT.NONE);
		btnCloseAllShort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		btnCloseAllShort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				closeAllShortClicked();
			}
		});
		new Label(rightSideComposite, SWT.NONE).setText("");
		new Label(rightSideComposite, SWT.NONE).setText("");

		showHideAutoStrategiesComposite = new Composite(rightSideComposite,
				SWT.NONE);
		showHideAutoStrategiesComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, false, false, 2, 1));
		formToolkit.adapt(showHideAutoStrategiesComposite);
		formToolkit.paintBordersFor(showHideAutoStrategiesComposite);
		showHideAutoStrategiesComposite.setLayout(new ShowHideLayout());

		Section sctnAutoStrategies = formToolkit.createSection(
				showHideAutoStrategiesComposite, ExpandableComposite.TITLE_BAR);
		sctnAutoStrategies.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false, 2, 1));
		formToolkit.paintBordersFor(sctnAutoStrategies);
		sctnAutoStrategies.setText("Auto Strategies");
		sctnAutoStrategies.setExpanded(true);

		Composite composite_8 = formToolkit.createComposite(sctnAutoStrategies,
				SWT.NONE);
		formToolkit.paintBordersFor(composite_8);
		sctnAutoStrategies.setClient(composite_8);
		composite_8.setLayout(new GridLayout(2, false));

		_btnEntryAndExit = new Button(composite_8, SWT.CHECK);
		formToolkit.adapt(_btnEntryAndExit, true, true);
		_btnEntryAndExit.setText("Entry and Exit");

		_comboViewerComplete = new ComboViewer(composite_8, SWT.READ_ONLY);
		Combo combo_6 = _comboViewerComplete.getCombo();
		combo_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(combo_6);
		_comboViewerComplete.setLabelProvider(new StrategyInfoLabelProvider());
		_comboViewerComplete.setContentProvider(new ArrayContentProvider());

		_btnEntry = new Button(composite_8, SWT.CHECK);
		formToolkit.adapt(_btnEntry, true, true);
		_btnEntry.setText("Entry Only");

		_comboViewerEntry = new ComboViewer(composite_8, SWT.READ_ONLY);
		Combo combo_7 = _comboViewerEntry.getCombo();
		combo_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(combo_7);
		_comboViewerEntry.setLabelProvider(new StrategyInfoLabelProvider());
		_comboViewerEntry.setContentProvider(new ArrayContentProvider());

		_btnExitOnly = new Button(composite_8, SWT.CHECK);
		formToolkit.adapt(_btnExitOnly, true, true);
		_btnExitOnly.setText("Exit Only");

		_comboViewerExit = new ComboViewer(composite_8, SWT.READ_ONLY);
		Combo combo_8 = _comboViewerExit.getCombo();
		combo_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		formToolkit.paintBordersFor(combo_8);
		_comboViewerExit.setLabelProvider(new StrategyInfoLabelProvider());
		_comboViewerExit.setContentProvider(new ArrayContentProvider());

		_btnUseAutoStrategy = new Button(composite_8, SWT.CHECK);
		_btnUseAutoStrategy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 2, 1));
		formToolkit.adapt(_btnUseAutoStrategy, true, true);
		_btnUseAutoStrategy.setText("Use Auto Strategy Order Confirmation");

		Button btnGenerateOnlyWarnings = new Button(composite_8, SWT.CHECK);
		btnGenerateOnlyWarnings.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		formToolkit.adapt(btnGenerateOnlyWarnings, true, true);
		btnGenerateOnlyWarnings.setText("Generate ONLY Warnings and NO Orders");

		Label lblCommand = formToolkit.createLabel(composite_8, "Command",
				SWT.NONE);
		lblCommand.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
		new Label(composite_8, SWT.NONE).setText("");

		Composite composite_9 = formToolkit
				.createCompositeSeparator(composite_8);
		GridData gd_composite_9 = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1);
		gd_composite_9.heightHint = 3;
		composite_9.setLayoutData(gd_composite_9);
		formToolkit.paintBordersFor(composite_9);

		_startText = "Start Auto Strategy";
		_stopText = "Stop Auto Strategy";

		_btnStartStopAutoStrategy = formToolkit.createHyperlink(composite_8,
				_startText, SWT.NONE);
		_btnStartStopAutoStrategy.setLayoutData(new GridData(SWT.LEFT,
				SWT.CENTER, false, false, 2, 1));
		formToolkit.paintBordersFor(_btnStartStopAutoStrategy);
		scrolledComposite.setContent(parentComposite);
		scrolledComposite.setMinSize(parentComposite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		_btnStartStopAutoStrategy
				.addHyperlinkListener(new IHyperlinkListener() {

					@Override
					public void linkActivated(HyperlinkEvent e) {
						stopStartAutoStrategy();
					}

					@Override
					public void linkEntered(HyperlinkEvent e) {
						// Documenting empty method to avoid warning.
					}

					@Override
					public void linkExited(HyperlinkEvent e) {
						// Documenting empty method to avoid warning.
					}
				});
		afterCreatedWidgets();

		m_bindingContext = initDataBindings();

		afterInitDataBindings();
	}

	private ManualStrategySettings createSettingsToCommand() {
		ManualStrategySettings aSettings = _winEnv.getSettings();
		aSettings.setLongTrailingLevel(getLongTrailingLevel());
		aSettings.setShortTrailingLevel(getShortTrailingLevel());
		return aSettings;
	}

	protected void decrLevelLongClicked() {
		int level = getLongTrailingLevel();
		level = Math.max(1, level - 1);
		txtLevelLong.setText(Integer.toString(level));
		updateTrailingStop();
	}

	protected void decrLevelShortClicked() {
		int level = getShortTrailingLevel();
		level = Math.max(1, level - 1);
		txtLevelShort.setText(Integer.toString(level));
		updateTrailingStop();
	}

	private void disableView() {
		getSite().getShell().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				UIUtils.enableAll(parentComposite, false);
			}
		});

	}

	@Override
	public void dispose() {
		if (this._winEnv != null) {
			this._winEnv.removeStateListener(_stateListener);
		}
		if (tradingPipe != null) {
			SymbolJob.getManager().removeJobChangeListener(this);
		}
		_changeConfigurationSetAction.dispose();
		super.dispose();
	}

	private void fillCompletePatterns() {
		List<StrategyInfo> patterns = getCompletePatterns();
		_comboViewerComplete.setInput(patterns);
	}

	private void fillEntryPatterns() {
		List<StrategyInfo> patterns = getPurePatterns(true);
		_comboViewerEntry.setInput(patterns);
	}

	private void fillExitPatterns() {
		List<StrategyInfo> patterns = getPurePatterns(false);
		_comboViewerExit.setInput(patterns);
	}

	void fillPatterns() {
		fillEntryPatterns();
		fillExitPatterns();
		fillCompletePatterns();
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	private FinalStrategy getAutoStaretgy() {
		StrategyInfo info = null;
		if (_btnEntry.getSelection()) {
			info = getSelected(_comboViewerEntry);
		} else if (_btnExitOnly.getSelection()) {
			info = getSelected(_comboViewerExit);
		} else if (_btnEntryAndExit.getSelection()) {
			info = getSelected(_comboViewerComplete);
		} else {
			return null;
		}
		StrategyBuilderPlugin.getDefault();
		return StrategyBuilderPlugin.createStrategyFromInfo(info, null);
	}

	/**
	 * @return the configuration
	 */
	@Override
	public TradingConfiguration getConfiguration() {
		return _configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.trading.ui.views.ITradingView#getConfigurationSet()
	 */
	@Override
	public int getConfigurationSet() {
		return _configuration == null ? _confSet : (_confSet = _configuration
				.getInfo().getConfigurationSet());
	}

	private int getLongTrailingLevel() {
		return Integer.parseInt(txtLevelLong.getText().trim());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.trading.ui.views.ITradingView#getPart()
	 */
	@Override
	public IViewPart getPart() {
		return this;
	}

	public ManualStrategySettings getSettings() {
		return settings;
	}

	private int getShortTrailingLevel() {
		return Integer.parseInt(txtLevelShort.getText().trim());
	}

	public TradingPipe getTradingJob() {
		return tradingPipe;
	}

	protected void incrLevelLongClicked() {
		int level = getLongTrailingLevel();
		level = Math.min(_winEnv.getStrategy().getScaleCount(), level + 1);
		txtLevelLong.setText(Integer.toString(level));
		updateTrailingStop();
	}

	protected void incrLevelShortClicked() {
		int level = getShortTrailingLevel();
		level = Math.min(_winEnv.getStrategy().getScaleCount(), level + 1);
		txtLevelShort.setText(Integer.toString(level));
		updateTrailingStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite,
	 * org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if (memento != null) {
			initialSettingsVisible = memento.getBoolean(KEY_SETTINGS_VISIBLE);
			initialConfigurationId = memento.getString(KEY_TRADE_CONFIG_ID);
		}
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue spinnerObserveSelectionObserveWidget = SWTObservables
				.observeSelection(spinner);
		IObservableValue selfSettingsbasicQuantityObserveValue = BeansObservables
				.observeValue(self, "settings.basicQuantity");
		bindingContext.bindValue(spinnerObserveSelectionObserveWidget,
				selfSettingsbasicQuantityObserveValue, null, null);
		//
		IObservableValue txtNewTextObserveTextObserveWidget = SWTObservables
				.observeText(txtNewText, SWT.Modify);
		IObservableValue selfSettingsbasicMaxQuantityObserveValue = BeansObservables
				.observeValue(self, "settings.basicMaxQuantity");
		bindingContext.bindValue(txtNewTextObserveTextObserveWidget,
				selfSettingsbasicMaxQuantityObserveValue, null, null);
		//
		IObservableValue btnUseOrderConfirmationObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnUseOrderConfirmation);
		IObservableValue selfSettingsbasicUseManualOrderConfirmationObserveValue = BeansObservables
				.observeValue(self, "settings.basicUseManualOrderConfirmation");
		bindingContext.bindValue(
				btnUseOrderConfirmationObserveSelectionObserveWidget,
				selfSettingsbasicUseManualOrderConfirmationObserveValue, null,
				null);
		//
		IObservableValue entryExitOrderTypeViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(entryExitOrderTypeViewer);
		IObservableValue selfSettingsentryExitOrderTypeObserveValue = BeansObservables
				.observeValue(self, "settings.entryExitOrderType");
		bindingContext.bindValue(
				entryExitOrderTypeViewerObserveSingleSelection,
				selfSettingsentryExitOrderTypeObserveValue, null, null);
		//
		IObservableValue entryExitChildToExitViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(entryExitChildToExitViewer);
		IObservableValue selfSettingsentryExitChildToExitObserveValue = BeansObservables
				.observeValue(self, "settings.entryExitChildToExit");
		bindingContext.bindValue(
				entryExitChildToExitViewerObserveSingleSelection,
				selfSettingsentryExitChildToExitObserveValue, null, null);
		//
		IObservableValue textObserveTextObserveWidget = SWTObservables
				.observeText(text, SWT.Modify);
		IObservableValue selfSettingsentryExitLimitPriceObserveValue = BeansObservables
				.observeValue(self, "settings.entryExitLimitPrice");
		bindingContext.bindValue(textObserveTextObserveWidget,
				selfSettingsentryExitLimitPriceObserveValue, null, null);
		//
		IObservableValue spinner_1ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(_entryExitTHScaleSpinner);
		IObservableValue selfSettingsentryExitTHScaleObserveValue = BeansObservables
				.observeValue(self, "settings.entryExitTHScale");
		bindingContext.bindValue(spinner_1ObserveSelectionObserveWidget,
				selfSettingsentryExitTHScaleObserveValue, null, null);
		//
		IObservableValue btnThScaleObserveSelectionObserveWidget = SWTObservables
				.observeSelection(btnThScale);
		IObservableValue selfSettingsselectedEntryExitTHScaleObserveValue = BeansObservables
				.observeValue(self, "settings.selectedEntryExitTHScale");
		bindingContext.bindValue(btnThScaleObserveSelectionObserveWidget,
				selfSettingsselectedEntryExitTHScaleObserveValue, null, null);
		//
		IObservableValue stopLossAutoStopViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(stopLossAutoStopViewer);
		IObservableValue selfSettingsstopLossSettingsautoStopObserveValue = BeansObservables
				.observeValue(self, "settings.stopLossSettings.autoStop");
		bindingContext.bindValue(stopLossAutoStopViewerObserveSingleSelection,
				selfSettingsstopLossSettingsautoStopObserveValue, null, null);
		//
		IObservableValue spinner_2ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(_numberOfTicksSpinner);
		IObservableValue selfSettingsstopLossSettingsnumberOfTicksObserveValue = BeansObservables
				.observeValue(self, "settings.stopLossSettings.numberOfTicks");
		bindingContext.bindValue(spinner_2ObserveSelectionObserveWidget,
				selfSettingsstopLossSettingsnumberOfTicksObserveValue, null,
				null);
		//
		IObservableValue stopLossStopTypeViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(stopLossStopTypeViewer);
		IObservableValue selfSettingsstopLossSettingsstopTypeObserveValue = BeansObservables
				.observeValue(self, "settings.stopLossSettings.stopType");
		bindingContext.bindValue(stopLossStopTypeViewerObserveSingleSelection,
				selfSettingsstopLossSettingsstopTypeObserveValue, null, null);
		//
		IObservableValue txtNewText_1ObserveTextObserveWidget = SWTObservables
				.observeText(txtNewText_1, SWT.Modify);
		IObservableValue selfSettingsstopLossSettingstriggerPriceObserveValue = BeansObservables
				.observeValue(self, "settings.stopLossSettings.triggerPrice");
		bindingContext.bindValue(txtNewText_1ObserveTextObserveWidget,
				selfSettingsstopLossSettingstriggerPriceObserveValue, null,
				null);
		//
		IObservableValue text_1ObserveTextObserveWidget = SWTObservables
				.observeText(text_1, SWT.Modify);
		IObservableValue selfSettingsstopLossSettingslimitPriceObserveValue = BeansObservables
				.observeValue(self, "settings.stopLossSettings.limitPrice");
		bindingContext.bindValue(text_1ObserveTextObserveWidget,
				selfSettingsstopLossSettingslimitPriceObserveValue, null, null);
		//
		IObservableValue takeProfitAutoStopViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(takeProfitAutoStopViewer);
		IObservableValue selfSettingstakeProfitSettingsautoStopObserveValue = BeansObservables
				.observeValue(self, "settings.takeProfitSettings.autoStop");
		bindingContext.bindValue(
				takeProfitAutoStopViewerObserveSingleSelection,
				selfSettingstakeProfitSettingsautoStopObserveValue, null, null);
		//
		IObservableValue spinner_3ObserveSelectionObserveWidget = SWTObservables
				.observeSelection(_numOfTicksSpinner);
		IObservableValue selfSettingstakeProfitSettingsnumberOfTicksObserveValue = BeansObservables
				.observeValue(self, "settings.takeProfitSettings.numberOfTicks");
		bindingContext.bindValue(spinner_3ObserveSelectionObserveWidget,
				selfSettingstakeProfitSettingsnumberOfTicksObserveValue, null,
				null);
		//
		IObservableValue takeProfitStopTypeViewerObserveSingleSelection = ViewersObservables
				.observeSingleSelection(takeProfitStopTypeViewer);
		IObservableValue selfSettingstakeProfitSettingsstopTypeObserveValue = BeansObservables
				.observeValue(self, "settings.takeProfitSettings.stopType");
		bindingContext.bindValue(
				takeProfitStopTypeViewerObserveSingleSelection,
				selfSettingstakeProfitSettingsstopTypeObserveValue, null, null);
		//
		IObservableValue text_2ObserveTextObserveWidget = SWTObservables
				.observeText(text_2, SWT.Modify);
		IObservableValue selfSettingstakeProfitSettingstriggerPriceObserveValue = BeansObservables
				.observeValue(self, "settings.takeProfitSettings.triggerPrice");
		bindingContext.bindValue(text_2ObserveTextObserveWidget,
				selfSettingstakeProfitSettingstriggerPriceObserveValue, null,
				null);
		//
		IObservableValue text_3ObserveTextObserveWidget = SWTObservables
				.observeText(text_3, SWT.Modify);
		IObservableValue selfSettingstakeProfitSettingslimitPriceObserveValue = BeansObservables
				.observeValue(self, "settings.takeProfitSettings.limitPrice");
		bindingContext.bindValue(text_3ObserveTextObserveWidget,
				selfSettingstakeProfitSettingslimitPriceObserveValue, null,
				null);
		//
		IObservableValue btnUseAutoStrategyObserveSelectionObserveWidget = SWTObservables
				.observeSelection(_btnUseAutoStrategy);
		IObservableValue selfWaitingForConfirmationObserveValue = BeansObservables
				.observeValue(self, "waitingForConfirmation");
		bindingContext.bindValue(
				btnUseAutoStrategyObserveSelectionObserveWidget,
				selfWaitingForConfirmationObserveValue, null, null);
		//

		return bindingContext;
	}

	@SuppressWarnings("static-method")
	public boolean isWaitingForConfirmation() {
		return false;
	}

	protected void openLongClicked() {
		_winEnv.executeCommand(WindowCommand.OPEN_LONG,
				createSettingsToCommand());
	}

	protected void openShortClicked() {
		_winEnv.executeCommand(WindowCommand.OPEN_SHORT,
				createSettingsToCommand());
	}

	protected void rcLongClicked() {
		_winEnv.executeCommand(WindowCommand.RC_LONG, createSettingsToCommand());
	}

	protected void rcShortClicked() {
		_winEnv.executeCommand(WindowCommand.RC_SHORT,
				createSettingsToCommand());
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	protected void sarLongClicked() {
		_winEnv.executeCommand(WindowCommand.SAR_LONG,
				createSettingsToCommand());
	}

	protected void sarShortClicked() {
		_winEnv.executeCommand(WindowCommand.SAR_SHORT,
				createSettingsToCommand());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putBoolean(KEY_SETTINGS_VISIBLE, settingsAction.isChecked());
		memento.putString(KEY_TRADE_CONFIG_ID, _configuration == null ? null
				: _configuration.getUUID().toString());
	}

	// public void setWaitingForConfirmation(boolean aWaitingForConfirmation) {
	// if (portfolioStrategy != null)
	// portfolioStrategy
	// .setWaitingForConfirmation(aWaitingForConfirmation);
	// }

	protected void scLongClicked() {
		_winEnv.executeCommand(WindowCommand.SC_LONG, createSettingsToCommand());
	}

	protected void scShortClicked() {
		_winEnv.executeCommand(WindowCommand.SC_SHORT,
				createSettingsToCommand());
	}

	/**
	 * @param conf
	 *            the configuration to set
	 */
	@Override
	public void setConfiguration(TradingConfiguration conf) {
		out.println("set configuration " + conf);
		this._configuration = conf;
		setConfigurationSet(conf == null ? _confSet : conf.getInfo()
				.getConfigurationSet());
		TradingPipe pipe = SymbolJob.getRunningTradingPipe(conf);
		setTradingPipe(pipe);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.trading.ui.views.ITradingView#setConfigurationSet(int)
	 */
	@Override
	public void setConfigurationSet(int aConfigurationSet) {
		this._confSet = aConfigurationSet;
		_changeConfigurationSetAction.updateIcon();
	}

	@Override
	public void setFocus() {
		parentComposite.setFocus();
	}

	public void setSettings(ManualStrategySettings aSettings) {
		this.settings = aSettings;
		firePropertyChange("settings");
	}

	private void setTradingPipe(TradingPipe pipe) {
		if (tradingPipe != null) {
			tradingPipe.getSymbolJob().removeJobChangeListener(this);
		}

		this.tradingPipe = pipe;
		if (pipe == null) {
			_manualStrategy = null;
			_portfolioStrategy = null;
			setWindowEnvironment(null);
			disableView();
			updatePartName();
		} else {
			_configuration = pipe.getConfiguration();// pipe == null ? null :
														// pipe.getConfiguration();

			TickDataSource dataSource = tradingPipe.getSymbolJob()
					.getDataSource();
			final boolean inWarmUp = dataSource.isInWarmUp();

			if (inWarmUp) {
				SymbolJob.getManager().addJobChangeListener(this);
			}

			updatePartName();

			if (_configuration == null) {
				_manualStrategy = null;
			} else {
				_portfolioStrategy = pipe.getPortfolio();
				for (IStrategy s : _portfolioStrategy.getStrategies()) {
					if (s instanceof IManualStrategy) {
						_manualStrategy = (ManualStrategy) s;
						break;
					}
				}
			}
			getSite().getShell().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					if (_manualStrategy == null) {
						UIUtils.enableAll(parentComposite, false);
					} else {
						UIUtils.enableAll(parentComposite, !inWarmUp);
						setSettings(_manualStrategy.getManualStrategySettings());
						final TradingConsoleWindowEnvironment aWinEnv = new TradingConsoleWindowEnvironment(
								_manualStrategy, settings);
						_manualStrategy.getStrategyEnvironment()
								.addStateChangedListener(new Runnable() {

									@Override
									public void run() {
										aWinEnv.fireStateChanged();
									}
								});
						setWindowEnvironment(aWinEnv);
					}
				}
			});
		}
	}

	synchronized void setWindowEnvironment(
			TradingConsoleWindowEnvironment aWinEnv) {
		if (this._winEnv != null && _stateListener != null) {
			this._winEnv.removeStateListener(_stateListener);
		}

		this._winEnv = aWinEnv;

		if (aWinEnv != null) {
			_stateListener = new Runnable() {

				@Override
				public void run() {
					updateFromEnvironment();
				}
			};
			aWinEnv.addStateListener(_stateListener);

			updateFromEnvironment();
		}
	}

	protected void stopStartAutoStrategy() {
		PortfolioStrategy p = tradingPipe.getPortfolio();
		_autotrading = !_autotrading;
		if (_autotrading) {
			_btnStartStopAutoStrategy.setText(_stopText);
			_autoStrategy = getAutoStaretgy();
			p.addStrategyOnTheRun(_autoStrategy);
		} else {
			_btnStartStopAutoStrategy.setText(_startText);
			p.removeStrategy(_autoStrategy);
			_autoStrategy = null;
		}
	}

	void updateAccount() {
		Assert.isNotNull(this._winEnv);

		IAccountStatus status = _winEnv.getStrategy().getAccountStatus();

		if (status == null) {
			U.debug_var(109151, "Status is null...");
		}
		lblLongQuantity.setText("Quantity: "
				+ ((status == null) ? "0 (null)" : Long.toString(status
						.getLongQuantity())));
		// we put the positive value of the short quantity
		lblShortQuantity.setText("Quantity: "
				+ ((status == null) ? "0 (null)" : Long.toString(Math
						.abs(status.getShortQuantity()))));
	}

	protected synchronized void updateFromEnvironment() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!PlatformUI.getWorkbench().isClosing()) {
					synchronized (TradingConsoleView2.this) {
						updateTrailingStop();
						updateAccount();
						updatePostionButtons();
						updatePendingButtons();
					}
				}
			}
		});

	}

	/**
	 * 
	 */
	void updatePartName() {
		Display.getDefault().asyncExec(new Runnable() {

			@SuppressWarnings("synthetic-access")
			// Use of inherit method.
			@Override
			public void run() {
				boolean iswarmuping = tradingPipe != null
						&& tradingPipe.getSymbolJob().getDataSource()
								.isInWarmUp();
				String warmUpStr = iswarmuping ? "In Warm Up - " : "";
				setPartName("Trading Console "
						+ (_configuration == null ? "" : "(" + warmUpStr
								+ _configuration.getName() + ")"));
			}
		});
	}

	void updatePendingButtons() {
		Assert.isNotNull(this._winEnv);

		IAccountStatus status = _winEnv.getStrategy().getAccountStatus();

		btnCancelLongPending.setEnabled(status.hasLongPendingOrders());
		btnCancelShortPending.setEnabled(status.hasShortPendingOrders());
		btnCancelAllPending.setEnabled(status.hasLongPendingOrders()
				|| status.hasShortPendingOrders());
	}

	void updatePostionButtons() {
		Assert.isNotNull(this._winEnv);

		IAccountStatus status = _winEnv.getStrategy().getAccountStatus();

		boolean longEnabled = status.getLongQuantity() != 0;
		boolean longPendingOrders = status.hasLongPendingOrders();
		btnCloseLong.setEnabled(longEnabled);
		btnCloseAllLong.setEnabled(longEnabled || longPendingOrders);
		btnStopAndReverseLong.setEnabled(longEnabled);
		btnAvgLong.setEnabled(longEnabled);

		boolean shortEnabled = status.getShortQuantity() != 0;
		boolean shortPendingOrders = status.hasShortPendingOrders();
		btnCloseShort.setEnabled(shortEnabled);
		btnCloseAllShort.setEnabled(shortEnabled || shortPendingOrders);
		btnStopAndReverseShort.setEnabled(shortEnabled);
		btnAvgShort.setEnabled(shortEnabled);

		btnCloseAll.setEnabled(longEnabled || shortEnabled || longPendingOrders
				|| shortPendingOrders);
	}

	/**
	 * 
	 */
	public void updateSettingsVisibility() {
		boolean visible = settingsAction.isChecked();

		ShowHideLayout layout = (ShowHideLayout) leftSideComposite.getLayout();
		layout.setVisible(visible);
		layout = (ShowHideLayout) showHideAutoStrategiesComposite.getLayout();
		layout.setVisible(visible);

		UIUtils.updateLayout(parentComposite);
		UIUtils.updateDetachedViewBounds(TradingConsoleView2.this,
				parentComposite);
	}

	synchronized void updateTrailingStop() {
		try {
			Assert.isNotNull(_winEnv);

			// long
			int level = getLongTrailingLevel();
			ManualStrategy env = this._winEnv.getStrategy();
			TrailingStatus status = env.getTrailingStatus(level);
			btnRcLong.setSelection(status.isLongRC());
			btnClLong.setSelection(status.isLongCL());
			btnScLong.setSelection(status.isLongSC());
			btnCancelTrailLong.setEnabled(status.isLongRC()
					|| status.isLongCL() || status.isLongSC());

			// short
			level = getShortTrailingLevel();
			status = env.getTrailingStatus(level);
			_btnRcShort.setSelection(status.isShortRC());
			btnClShort.setSelection(status.isShortCL());
			btnScShort.setSelection(status.isShortSC());
			btnCancelTrailShort.setEnabled(status.isShortRC()
					|| status.isShortCL() || status.isShortSC());

		} catch (NumberFormatException e) {
			// Documenting empty method to avoid warning.
		}
	}

	@Override
	public void aboutToRun(IJobChangeEvent event) {
		// nothing
	}

	@Override
	public void awake(IJobChangeEvent event) {
		// nothing
	}

	@Override
	public void done(IJobChangeEvent event) {
		// nothing
	}

	@Override
	public void running(IJobChangeEvent event) {
		// nothing
	}

	@Override
	public void scheduled(IJobChangeEvent event) {
		// nothing
	}

	@Override
	public void sleeping(IJobChangeEvent event) {
		// nothing
	}

	@Override
	public void tradingStarted(TradingPipeChangeEvent event) {
		// nothing
	}

	@Override
	public void tradingStopped(TradingPipeChangeEvent event) {
		// nothing
	}

	@Override
	public void tradingPaused(TradingPipeChangeEvent event) {
		// nothing
	}

	@Override
	public void tradingRestarted(TradingPipeChangeEvent event) {
		// nothing
	}

	@Override
	public void warmingUpFinished(SymbolJobChangeEvent event) {
		if (tradingPipe != null) {
			// we do not need to listen the data anymore.
			// TODO: I can't do it because a concurrent modification
			// tradingPipe.getSymbolJob().getDataSource().removeTickListener(this);
			if (!parentComposite.isDisposed()) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (_manualStrategy != null) {
							UIUtils.enableAll(parentComposite, true);
							updateFromEnvironment();
						}
						updatePartName();
					}
				});
			}
		}
	}

	@Override
	public void inputStopped(InputPipeChangeEvent event) {
		// nothing
	}
}
