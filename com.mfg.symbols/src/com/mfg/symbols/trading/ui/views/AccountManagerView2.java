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
package com.mfg.symbols.trading.ui.views;

import static java.lang.System.out;

import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

import com.mfg.broker.IOrderMfg.EAccountRouting;
import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.PriceChartCanvas_OpenGL;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.chart.ui.views.IChartContentAdapter;
import com.mfg.chart.ui.views.IChartView;
import com.mfg.interfaces.trading.PositionEvent;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.LogRecord;
import com.mfg.logger.memory.MemoryLoggerManager;
import com.mfg.strategy.logger.TradeMessageWrapper;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.TradingPipe;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.adapters.EquityChartAdapter;
import com.mfg.symbols.trading.ui.adapters.TradingChartAdapater;
import com.mfg.utils.PartUtils;
import com.mfg.utils.concurrent.SimpleAnimator;
import com.mfg.utils.ui.actions.CopyStructuredSelectionAction;

/**
 * @author arian
 * 
 */
public class AccountManagerView2 extends ViewPart implements ITradingView,
		IChartView {

	/**
	 * 
	 */
	private static final String KEY_TRADING_CONFIGURATION = "tradingConfiguration";
	public static final String VIEW_ID = "com.mfg.symbols.trading.ui.views.AccountManagerView2"; //$NON-NLS-1$
	CTabFolder _tabFolder;
	private LongShort_AM_TabComposite _longTabComposite;
	private LongShort_AM_TabComposite _shortTabComposite;
	private TradingConfiguration _configuration;
	private TradingConfiguration _initialConfiguration;
	Lsc_AM_TabComposite _closeTradesTabComposite;
	Lso_AM_TabComposite _openTradesTabComposite;
	private AnalysisTabComposite2 _analysisTabComposite;
	private int configurationSet;
	private Composite _chartContainer;
	private PriceChartCanvas_OpenGL _chartComp;
	IChartContentAdapter _chartAdapter;
	private SimpleAnimator _animator;

	public AccountManagerView2() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		{
			_tabFolder = new CTabFolder(container, SWT.BORDER);
			_tabFolder.setSelection(0);
			_tabFolder.setSelectionBackground(Display.getCurrent()
					.getSystemColor(
							SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
			{
				CTabItem tbtmAnalysis = new CTabItem(_tabFolder, SWT.NONE);
				tbtmAnalysis.setText("Analysis");
				{
					_analysisTabComposite = new AnalysisTabComposite2(
							_tabFolder, SWT.NONE);
					tbtmAnalysis.setControl(_analysisTabComposite);
				}
			}
			{
				CTabItem tbtmLong = new CTabItem(_tabFolder, SWT.NONE);
				tbtmLong.setText("Long");
				{
					_longTabComposite = new LongShort_AM_TabComposite(
							_tabFolder, SWT.NONE);
					tbtmLong.setControl(_longTabComposite);
				}
			}
			{
				CTabItem tbtmShort = new CTabItem(_tabFolder, SWT.NONE);
				tbtmShort.setText("Short");
				{
					_shortTabComposite = new LongShort_AM_TabComposite(
							_tabFolder, SWT.NONE);
					tbtmShort.setControl(_shortTabComposite);
				}
			}
			{
				CTabItem tbtmOpenTrades = new CTabItem(_tabFolder, SWT.NONE);
				tbtmOpenTrades.setText("Open Trades");
				{
					_openTradesTabComposite = new Lso_AM_TabComposite(
							_tabFolder, SWT.NONE);
					tbtmOpenTrades.setControl(_openTradesTabComposite);
				}
			}
			{
				CTabItem tbtmCloseTrades = new CTabItem(_tabFolder, SWT.NONE);
				tbtmCloseTrades.setText("Close Trades");
				{
					_closeTradesTabComposite = new Lsc_AM_TabComposite(
							_tabFolder, SWT.NONE);
					tbtmCloseTrades.setControl(_closeTradesTabComposite);
				}
			}
			{
				CTabItem tbtmChart = new CTabItem(_tabFolder, SWT.NONE);
				tbtmChart.setText("Chart");
				{
					_chartContainer = new Composite(_tabFolder, SWT.NONE);
					tbtmChart.setControl(_chartContainer);
					_chartContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
				}
			}
		}

		createActions();
		initializeToolBar();
		initializeMenu();
		afterCreateWidgets();
	}

	/**
	 * 
	 */
	private void afterCreateWidgets() {
		_tabFolder.setSelection(0);

		// the chart tab

		if (ChartPlugin.getDefault() != null) { // condition required by WB.
			GLData glData = new GLData();
			glData.doubleBuffer = true;
			_chartComp = new PriceChartCanvas_OpenGL(_chartContainer, glData,
					new Chart("Equity", IChartModel.EMPTY, ChartType.EQUITY,
							null));
		}

		// the logger tabs

		class AccountFilter extends ViewerFilter {
			private final EAccountRouting routing;

			public AccountFilter(EAccountRouting aRouting) {
				this.routing = aRouting;
			}

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				LogRecord record = (LogRecord) element;
				TradeMessageWrapper msg = (TradeMessageWrapper) record
						.getMessage();
				EAccountRouting msgRouting = msg.getRoutedAccount();
				return msgRouting == null || msgRouting == routing;
			}
		}
		_longTabComposite
				.getLogViewerManager()
				.getViewer()
				.setFilters(
						new ViewerFilter[] { new AccountFilter(
								EAccountRouting.LONG_ACCOUNT) });
		_shortTabComposite
				.getLogViewerManager()
				.getViewer()
				.setFilters(
						new ViewerFilter[] { new AccountFilter(
								EAccountRouting.SHORT_ACCOUNT) });
		_tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelectionProvider selProvider = null;
				CTabItem item = _tabFolder.getSelection();
				Control control = item.getControl();
				if (control instanceof LongShort_AM_TabComposite) {
					LongShort_AM_TabComposite comp = (LongShort_AM_TabComposite) control;
					selProvider = comp.getLogViewerManager().getViewer();
				} else if (control == _openTradesTabComposite) {
					selProvider = _openTradesTabComposite.getTreeViewer();
				} else if (control == _closeTradesTabComposite) {
					selProvider = _closeTradesTabComposite.getTreeViewer();
				}
				getSite().setSelectionProvider(selProvider);
			}
		});
		getViewSite().getActionBars()
				.setGlobalActionHandler(ActionFactory.COPY.getId(),
						new CopyStructuredSelectionAction());

		IDoubleClickListener doubleClickListener = new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection sel = (StructuredSelection) event
						.getSelection();
				Object elem = sel.getFirstElement();
				out.println("double click on: class=" + elem.getClass()
						+ ", elem=" + elem);
				Long time = null;
				Double price = null;
				if (elem instanceof LogRecord) {
					elem = ((LogRecord) elem).getMessage();
				}
				if (elem instanceof TradeMessageWrapper) {
					TradeMessageWrapper msg = (TradeMessageWrapper) elem;
					time = Long.valueOf(msg.getFakeTime());
					price = Double.valueOf(msg.getPrice());
				}
				if (elem instanceof PositionEvent) {
					PositionEvent posEvent = (PositionEvent) elem;
					time = Long.valueOf(posEvent.getExecutionTime());
					price = Double.valueOf(posEvent.getExecutionPrice());
				}
				if (time != null && price != null) {
					TradingConfiguration config = getConfiguration();
					if (config != null) {
						List<ChartView> views = ChartView.findByContent(config);
						for (ChartView view : views) {
							view.getChart().scrollToPoint(time.longValue(),
									price.doubleValue());
						}
					}
				}
			}
		};

		for (StructuredViewer viewer : new StructuredViewer[] {
				_longTabComposite.getLogViewerManager().getViewer(),
				_shortTabComposite.getLogViewerManager().getViewer(),
				_openTradesTabComposite.getTreeViewer(),
				_closeTradesTabComposite.getTreeViewer() }) {
			viewer.addDoubleClickListener(doubleClickListener);
		}

		// animator
		_animator = new SimpleAnimator(new Runnable() {

			@Override
			public void run() {
				_openTradesTabComposite.getUpdateRunnable().run();
				_closeTradesTabComposite.getUpdateRunnable().run();
			}
		});
		_animator.start();

		setConfiguration(_initialConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		_animator.stop();
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		//
	}

	/**
	 * 
	 */
	void openEquityChart() {
		ChartView view = PartUtils.openView(ChartView.VIEW_ID, true);
		view.setContent(_configuration);
		TradingChartAdapater adapter = (TradingChartAdapater) view
				.getContentAdapter();
		adapter.swapTradingAndEquity(view);
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		// IMenuManager menuManager = getViewSite().getActionBars()
		// .getMenuManager();
	}

	@Override
	public void setFocus() {
		_tabFolder.setFocus();
	}

	/**
	 * @return the configuration
	 */
	@Override
	public TradingConfiguration getConfiguration() {
		return _configuration;
	}

	/**
	 * @param aConfiguration
	 *            the configuration to set
	 */
	@Override
	public void setConfiguration(final TradingConfiguration aConfiguration) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				setContent(aConfiguration);
			}
		});

	}

	/**
	 * @param aConfiguration
	 */
	private void updateTabsWithConfiguration(TradingConfiguration aConfiguration) {
		configurationSet = aConfiguration == null ? configurationSet
				: aConfiguration.getInfo().getConfigurationSet();

		TradingPipe pipe = SymbolJob.getRunningTradingPipe(aConfiguration);
		setTradingPipe(pipe);

		setPartName("Account Manager"
				+ (aConfiguration == null ? "" : " - "
						+ SymbolsPlugin.getDefault().getFullConfigurationName(
								aConfiguration)));
	}

	private void setTradingPipe(TradingPipe pipe) {
		ILoggerManager loggerManager;
		if (pipe == null) {
			loggerManager = new MemoryLoggerManager("Empty", true);
		} else {
			loggerManager = pipe.getPortfolio().getLogger().getManager();
		}

		_longTabComposite.getLogViewerManager().setLogManager(loggerManager);
		_shortTabComposite.getLogViewerManager().setLogManager(loggerManager);
		_openTradesTabComposite.setTradingPipe(pipe);
		_closeTradesTabComposite.setTradingPipe(pipe);

		_analysisTabComposite.setPortfolio(pipe == null ? null : pipe
				.getPortfolio());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putString(KEY_TRADING_CONFIGURATION,
				_configuration == null ? null : _configuration.getUUID()
						.toString());
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
			String id = memento.getString(KEY_TRADING_CONFIGURATION);
			if (id != null) {
				_initialConfiguration = SymbolsPlugin.getDefault()
						.getTradingStorage().findById(id);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.symbols.trading.ui.views.ITradingView#getConfigurationSet()
	 */
	@Override
	public int getConfigurationSet() {
		return _configuration == null ? configurationSet
				: (configurationSet = _configuration.getInfo()
						.getConfigurationSet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.trading.ui.views.ITradingView#setConfigurationSet(int)
	 */
	@Override
	public void setConfigurationSet(int aConfigurationSet) {
		this.configurationSet = aConfigurationSet;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartView#getChart()
	 */
	@Override
	public Chart getChart() {
		return _chartComp.getChart();
	}

	void addDefaultActions() {
		// IActionBars actionBars = getViewSite().getActionBars();
		// IToolBarManager toolbar = getViewSite().getActionBars()
		// .getToolBarManager();
		// actionBars.updateActionBars();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartView#setContent(java.lang.Object)
	 */
	@Override
	public synchronized void setContent(Object newContent) {
		if (newContent == null) {
			_configuration = null;
			if (_chartAdapter != null) {
				_chartAdapter.dispose(this);
			}
			_chartAdapter = null;
			setChart(new Chart("Empty", IChartModel.EMPTY, ChartType.EQUITY,
					newContent));
			getSite().getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					addDefaultActions();
				}
			});
		} else {
			_configuration = (TradingConfiguration) newContent;
			if (_chartAdapter != null) {
				_chartAdapter.dispose(this);
			}
			IChartContentAdapter oldAdapter = _chartAdapter;
			_chartAdapter = new EquityChartAdapter(_configuration);
			_chartAdapter.init(false, oldAdapter);
			Chart chart = _chartAdapter.getChart();
			setChart(chart);
			getSite().getShell().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					_chartAdapter.configure(AccountManagerView2.this, null);
					_chartAdapter.getChart().zoomOutAll(true);
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							addDefaultActions();
						}
					});
				}
			});
		}

		updateTabsWithConfiguration(_configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartView#getContent()
	 */
	@Override
	public Object getContent() {
		return _configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.views.IChartView#setChart(com.mfg.chart.backend.opengl
	 * .PriceChart_OpenGL)
	 */
	@Override
	public void setChart(Chart chart) {
		_chartComp.setChart(chart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartView#getContentAdapter()
	 */
	@Override
	public IChartContentAdapter getContentAdapter() {
		return _chartAdapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#setTitleImage(org.eclipse.swt.graphics
	 * .Image)
	 */
	@Override
	public void setTitleImage(Image titleImage) {
		super.setTitleImage(titleImage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#setPartName(java.lang.String)
	 */
	@Override
	public void setPartName(String partName) {
		super.setPartName(partName);
	}

}
