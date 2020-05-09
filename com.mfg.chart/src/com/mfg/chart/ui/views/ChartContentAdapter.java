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
package com.mfg.chart.ui.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.part.ViewPart;
import org.mfg.opengl.chart.PlotRange;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.Chart.TimeOfTheDaySettings;
import com.mfg.chart.commands.ChangeRangesHandler;
import com.mfg.chart.commands.ChartAction;
import com.mfg.chart.commands.ChartSettingsHandler;
import com.mfg.chart.commands.PolylineToolAction;
import com.mfg.chart.commands.ScrollingOnOffHandler;
import com.mfg.chart.commands.SelectToolAction;
import com.mfg.chart.commands.SwapFakePhysicalTimesHandler;
import com.mfg.chart.commands.TrendLinesToolAction;
import com.mfg.chart.layers.IndicatorLayer.ATLSettings;
import com.mfg.chart.layers.PriceLayer.PriceSettings;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.ScrollingMode;
import com.mfg.chart.ui.animation.ChartAnimator;
import com.mfg.chart.ui.interactive.ForecastingTool;
import com.mfg.chart.ui.interactive.HarmonicLinesTool;
import com.mfg.chart.ui.interactive.LineTool;
import com.mfg.chart.ui.interactive.TimeLinesTool;
import com.mfg.chart.ui.settings.global.IndicatorVisibilityOverviewWindow;
import com.mfg.inputdb.indicator.mdb.IndicatorMDBSession;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.tradingdb.mdb.TradingMDBSession;
import com.mfg.utils.ImageUtils;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public abstract class ChartContentAdapter implements IChartContentAdapter {
	private final class ShowIndicatorVisibilityWindowAction extends Action {
		public ShowIndicatorVisibilityWindowAction() {
			super("Indicator Visibility Window");
		}

		@Override
		public void run() {
			Display display = Display.getDefault();
			IndicatorVisibilityOverviewWindow w = new IndicatorVisibilityOverviewWindow(
					getChart(), display);
			w.pack();
			w.setLocation(100, 100);
			w.open();
		}
	}

	private final class ZoomingOutAction extends Action {
		public ZoomingOutAction() {
			super("Real-Time Scrolling (Zooming Out)");
		}

		@Override
		public void run() {
			_chart.setScrollingMode(_chart.getScrollingMode().swapZoomingOut());
		}
	}

	public static class MenuAction extends Action {
		public MenuAction(String id) {
			this(id, null, SWT.PUSH);
		}

		public MenuAction(String id, ImageDescriptor icon) {
			this(id, icon, SWT.PUSH);
		}

		public MenuAction(String id, ImageDescriptor icon, int style) {
			super(null, style);
			if (icon != null) {
				setImageDescriptor(icon);
			}
			if (id != null) {
				setActionDefinitionId(id);

				ICommandService cmdServ = (ICommandService) PlatformUI
						.getWorkbench().getService(ICommandService.class);
				Command cmd = cmdServ.getCommand(id);
				try {
					String name = cmd.getName();
					setText(name);
				} catch (NotDefinedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private final class ZoomOutAllAction extends Action {
		public ZoomOutAllAction() {
			super("Zoom Out All");
		}

		@Override
		public void run() {
			getChart().zoomOutAll(true);
		}
	}

	private final class DisconnectAction extends Action {
		public DisconnectAction() {
			super("Disconnect");
		}

		@Override
		public void run() {
			getChartView().setContent(null);
		}
	}

	private final class LinkToOtherChartAction extends Action {
		public LinkToOtherChartAction() {
			super("Link To Other Chart");
		}

		@Override
		public void run() {
			Chart chart = getChart();
			Chart masterChart = chart.getMasterChart();
			if (masterChart == null) {
				// make the link
				Shell shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();
				ListDialog dlg = new ListDialog(shell);
				dlg.setLabelProvider(new LabelProvider() {
					@Override
					public String getText(Object element) {
						ChartView v = (ChartView) element;
						return v.getPartName();
					}
				});
				dlg.setContentProvider(new ArrayContentProvider());
				dlg.setTitle("Link Chart");
				dlg.setMessage("Select target chart:");
				List<ChartView> list = PartUtils
						.getOpenViews(ChartView.VIEW_ID);
				if (list.isEmpty()) {
					MessageDialog.openInformation(shell, "Link Chart",
							"There are not any other chart where link to.");
				} else {
					list.remove(getChartView());
					dlg.setInput(list);
					if (dlg.open() == Window.OK) {
						final ChartView view2 = (ChartView) dlg.getResult()[0];

						String name = "Break link with " + view2.getPartName();
						setText(name);

						masterChart = view2.getChart();
						masterChart.addMirror(chart);
						masterChart.update(masterChart.isAutoRangeEnabled());
					}
				}
			} else {
				// break the link
				chart.breakLink();
				setText("Link To Chart...");
			}

		}
	}

	private final class ChartInfoAction extends Action {
		public ChartInfoAction() {
			super("Chart Info...");
		}

		@Override
		public void run() {
			ChartInfoDialog dialog = new ChartInfoDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell());
			dialog.setChart(getChart());
			dialog.open();
		}
	}

	private final class BrowseDataAction extends Action {
		BrowseDataAction() {
			super("Browse Data...");
		}

		@Override
		public void run() {
			try {
				IChartView view = getChartView();
				final IChartModel model = getChart().getModel();
				if (model instanceof ChartModel_MDB) {
					final ChartDBBrowserView view2 = (ChartDBBrowserView) view
							.getViewSite().getWorkbenchWindow().getActivePage()
							.showView(ChartDBBrowserView.VIEW_ID);
					ChartModel_MDB mdbModel = (ChartModel_MDB) model;

					PriceMDBSession priceSession = mdbModel.getPriceSession();
					IndicatorMDBSession indicatorSession = mdbModel
							.getIndicatorSession();
					TradingMDBSession tradingSession = mdbModel
							.getTradingSession();

					view2.setSession(priceSession, indicatorSession,
							tradingSession, view.getContent());
				} else {
					MessageDialog.openError(view.getViewSite().getShell(),
							"Invalid operation", "The chart is empty");
				}
			} catch (final PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	public static String CHART_CONTEXT = "com.mfg.chart.contexts.chartView";
	private static final String REALTIME_CHART_CONTEXT = "com.mfg.chart.contexts.chartView.realtime";
	private static final String INDICATOR_CHART_CONTEXT = "com.mfg.chart.contexts.chartView.indicator";
	private static final String TRADING_CHART_CONTEXT = "com.mfg.chart.contexts.chartView.trading";
	private static final String EQUITY_CHART_CONTEXT = "com.mfg.chart.contexts.chartView.equity";
	private String _chartName;
	private ChartType _type;
	IChartView _chartView;
	private final boolean _realtime;

	final int _maxDelay = ChartPlugin
			.getDefault()
			.getPreferenceStore()
			.getInt(ChartPlugin.PREFERENCES_REALTIME_UPDATE_ON_TICK_SLEEP_VALUE);
	protected Chart _chart;

	private IPropertyChangeListener _prefsListener;
	private boolean _isPhysicalTimeChart;
	private ChartAnimator _animator;
	private long _tickToken;
	private long _lastTickToken;
	private IContextActivation _indicatorChartToken;
	private IContextActivation _realtimeChartToken;
	private IContextActivation _tradingChartToken;
	private IContextActivation _chartContext;
	private IContextActivation _equityChartToken;
	private Map<String, ChartAction> _actionMap;

	public ChartContentAdapter(String chartName, ChartType type,
			boolean realtime) {
		this._chartName = chartName;
		this._type = type;
		this._realtime = realtime;
		_actionMap = new HashMap<>();
	}

	public Map<String, ChartAction> getActionMap() {
		return _actionMap;
	}

	protected void addActions(IContributionManager toolbar,
			ChartAction... actions) {
		if (actions != null) {
			for (ChartAction action : actions) {
				toolbar.add(action);
				_actionMap.put(action.getActionDefinitionId(), action);
			}
		}
	}

	@Override
	public void saveState(IMemento memento) {
		// nothing
	}

	@Override
	public void initState(IMemento initState) {
		// nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartContentAdapter#init(boolean,
	 * com.mfg.chart.ui.views.ChartConfig)
	 */
	@Override
	public void init(boolean usePhysicalTime, IChartContentAdapter oldAdapter) {
		_isPhysicalTimeChart = usePhysicalTime;

		_chart = createChart(_chartName, IChartModel.EMPTY, _type, null);
		_chart.zoomOutAll(false);

		final IPreferenceStore preferenceStore = ChartPlugin.getDefault()
				.getPreferenceStore();
		_prefsListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				preferencesChanged(preferenceStore);
			}
		};
		preferenceStore.addPropertyChangeListener(_prefsListener);
		preferencesChanged(preferenceStore);

		_animator = new ChartAnimator(this);
		_animator.start();
	}

	public boolean isPhysicalTimeChart() {
		return _isPhysicalTimeChart;
	}

	/**
	 * @param chartName
	 * @param model
	 * @param type
	 * @return
	 */
	protected Chart createChart(String chartName, IChartModel model,
			ChartType type, Object content) {
		if (_chart != null) {
			_chart.dispose();
		}
		Chart newChart = new Chart(chartName, model, type, content) {
			@Override
			protected void setXRangeAsZoomOperation(PlotRange range) {
				if (_chart.getScrollingMode() == ScrollingMode.ZOOMING_OUT) {
					_chart.setScrollingMode(ScrollingMode.NONE);
				}
				super.setXRangeAsZoomOperation(range);
			}
		};
		if (_realtime) {
			newChart.setScrollingMode(ScrollingMode.ZOOMING_OUT);
		}

		return newChart;
	}

	protected void preferencesChanged(IPreferenceStore preferenceStore) {
		if (_animator != null) {
			_animator.setFps(preferenceStore
					.getInt(ChartPlugin.PREFERENCES_FRAMES_PER_SECOND));
		}
	}

	public void updateChartOnTick() {
		_tickToken++;
	}

	protected long getLastTime() {
		int dataLayer = _chart.getDataLayer();
		IPriceModel model = _chart.getModel().getPriceModel();
		return model.getUpperDisplayTime(dataLayer);
	}

	public ChartAnimator getAnimator() {
		return _animator;
	}

	public void fillMenuBar(final IMenuManager menu) {
		ChartType type = getChart().getType();

		switch (type) {
		case FINANCIAL:
		case INDICATOR:
		case TRADING:
		case EQUITY:
			boolean empty = _chart.getModel() == IChartModel.EMPTY;
			boolean equity = type == ChartType.EQUITY;
			if (!empty) {
				if (!equity) {
					fillMenuBar_chartTools(menu);
					menu.add(new Separator());
				}

				menu.add(new BrowseDataAction());
				menu.add(new ChartInfoAction());

				menu.add(new Separator());

				menu.add(new ZoomOutAllAction());
				menu.add(new MenuAction(ScrollingOnOffHandler.CMD_ID) {
					@Override
					public void run() {
						ScrollingOnOffHandler.execute(getChart());
					}
				});
				menu.add(new ZoomingOutAction());
				menu.add(new MenuAction(ChangeRangesHandler.CMD_ID) {
					@Override
					public void run() {
						ChangeRangesHandler.execute(getChart());
					}
				});

				menu.add(new Separator());

				menu.add(new MenuAction(SwapFakePhysicalTimesHandler.CMD_ID) {
					@Override
					public void run() {
						SwapFakePhysicalTimesHandler.execute(getChartView());
					}
				});

				if (!equity) {
					menu.add(new Separator());
					menu.add(new ShowIndicatorVisibilityWindowAction());
				}

				menu.add(new Separator());
				menu.add(new MenuAction(ChartSettingsHandler.CMD_ID) {
					@Override
					public void run() {
						ChartSettingsHandler.execute(getChart());
					}
				});

				menu.add(new Separator());
			}

			menu.add(new LinkToOtherChartAction());
			menu.add(new DisconnectAction());

			break;
		case EMPTY:
		case SYNTHETIC:
			break;
		}
	}

	protected void fillMenuBar_chartTools(IMenuManager menu) {
		InteractiveTool[] tools = getChart().getTools();
		for (InteractiveTool tool : tools) {
			tool.fillMenu(menu);
		}
	}

	/**
	 * @return the realtime
	 */
	public boolean isRealtime() {
		return _realtime;
	}

	@Override
	public String getChartName() {
		return _chartName;
	}

	@SuppressWarnings("static-method")
	// Used on inner classes.
	public Image getChartIcon() {
		return ImageUtils.getBundledImage(ChartPlugin.getDefault(),
				ChartPlugin.CHART_ICON_PATH);
	}

	public void setChartName(String chartName) {
		this._chartName = chartName;
	}

	@Override
	public ChartType getType() {
		return _type;
	}

	public void setType(ChartType type) {
		this._type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.IChartContent#getChart()
	 */
	@Override
	public Chart getChart() {
		return _chart;
	}

	@Override
	public void configure(final IChartView chartView, ChartConfig config) {
		this._chartView = chartView;
		String name = getChartName();
		if (name != null) {
			chartView.setPartName(name);
		}
		Image chartIcon = getChartIcon();
		if (chartIcon != null) {
			chartView.setTitleImage(chartIcon);
		}

		IContextService service = getContextService();
		activateContexts(service);

		if (_realtime) {
			if (config == null) {
				_chart.setScrollingMode(ScrollingMode.ZOOMING_OUT);
			} else {
				_chart.setScrollingMode(config.getScrollingMode());
			}
		}
		chartView.setChart(_chart);

		// give some time to update the bar, at this moment probably the chart
		// is not created with the right model, because implementers should
		// configure by them self.
		Display.getCurrent().asyncExec(new Runnable() {

			@Override
			public void run() {
				IActionBars actionBars = chartView.getViewSite()
						.getActionBars();
				final IToolBarManager toolbar = actionBars.getToolBarManager();
				IMenuManager menu = actionBars.getMenuManager();

				toolbar.removeAll();
				menu.removeAll();

				fillToolbar(toolbar);
				fillMenuBar(menu);

				actionBars.updateActionBars();
			}
		});
	}

	/**
	 * Fill the tool bar.
	 * 
	 * @param manager
	 *            The tool bar manager.
	 */
	protected void fillToolbar(IToolBarManager manager) {
		if (_chart.getModel() != IChartModel.EMPTY) {

			// all non-equity charts
			ChartType type = getType();
			if (type != ChartType.EQUITY && type != ChartType.SYNTHETIC) {
				addActions(
						manager,
						// forecasting tool
						new SelectToolAction(this, null, ImageUtils
								.getBundledImageDescriptor("com.mfg.chart",
										"icons/forecasting-tool.png"),
								ForecastingTool.class, false) {
							@Override
							public void run() {
								super.run();
								ForecastingTool tool = getChart().getTool(
										ForecastingTool.class);
								tool.setAlwaysPaint(isChecked());
								getChart().update();
							}

						},
						// volume
						new ChartAction(this, "com.mfg.chart.commands.volume",
								ImageUtils.getBundledImageDescriptor(
										"com.mfg.chart", "icons/volume.png"),
								IAction.AS_CHECK_BOX) {
							@Override
							public void run() {
								PriceSettings settings = _chart.getPriceLayer()
										.getSettings();
								settings.showVolume = !settings.showVolume;
								setChecked(settings.showVolume);
								_chart.update();
							}
						},
						// harmonic lines
						new SelectToolAction(this,
								SelectToolAction.HARMONIC_LINES_CMD_ID,
								ImageUtils.getBundledImageDescriptor(
										"com.mfg.chart", "icons/HL_16.png"),
								HarmonicLinesTool.class) {
							@Override
							public void run() {
								super.run();
								HarmonicLinesTool tool = getChart().getTool(
										HarmonicLinesTool.class);
								tool.setAlwaysPaint(isChecked());
								tool.repaint();
							}
						},

						new ChartAction(this,
								"com.mfg.chart.commands.timesoftheday",
								ImageUtils.getBundledImageDescriptor(
										"com.mfg.chart", "icons/TOD.png"),
								IAction.AS_CHECK_BOX) {
							{
								setChecked(true);
							}

							@Override
							public void run() {
								TimeOfTheDaySettings s = _chart
										.getTimeOfTheDaySettings();
								s.setVisible(!s.isVisible());
								setChecked(s.isVisible());
								getChart().repaint();
							}
						});
			}

			// indicator charts

			if (_chart.getType().hasChannels()) {

				addActions(
						manager,

						// line tool
						new SelectToolAction(this,
								SelectToolAction.LINES_TOOL_CMD_ID, ImageUtils
										.getBundledImageDescriptor(
												"com.mfg.chart",
												"icons/LT_16.png"),
								LineTool.class),

						// poly-line
						new PolylineToolAction(this),

						new TrendLinesToolAction(this),

						// time line tool
						new SelectToolAction(this,
								SelectToolAction.TIME_LINES_CMD_ID, ImageUtils
										.getBundledImageDescriptor(
												"com.mfg.chart",
												"icons/TL2_16.png"),

								TimeLinesTool.class),
						// auto time lines
						new ChartAction(
								this,
								"com.mfg.chart.commands.showLayer.AutoTimeLines",
								ImageUtils.getBundledImageDescriptor(
										"com.mfg.chart", "icons/ATL2_16.png"),
								IAction.AS_CHECK_BOX) {
							@Override
							public void run() {
								ATLSettings s = getChart().getIndicatorLayer()
										.getAtlSettings();
								s.enabled = !s.enabled;
								setChecked(s.enabled);
							}
						});
			}

		}
	}

	protected void activateContexts(IContextService service) {
		if (service != null) {
			if (getType() == ChartType.EMPTY) {
				// nothing is activated
			} else {
				_chartContext = service.activateContext(CHART_CONTEXT);

				if (getType().hasChannels()) {
					_indicatorChartToken = service
							.activateContext(INDICATOR_CHART_CONTEXT);
				}

				if (getType().hasExecutions()) {
					_tradingChartToken = service
							.activateContext(TRADING_CHART_CONTEXT);
				}

				if (getType().hasEquity()) {
					_equityChartToken = service
							.activateContext(EQUITY_CHART_CONTEXT);
				}

				if (_realtime) {
					_realtimeChartToken = service
							.activateContext(REALTIME_CHART_CONTEXT);
				}
			}
		}
	}

	protected void deactivateContexts(IContextService service) {
		if (service != null) {
			if (_equityChartToken != null) {
				service.deactivateContext(_equityChartToken);
			}
			if (_chartContext != null) {
				service.deactivateContext(_chartContext);
			}

			if (_indicatorChartToken != null) {
				service.deactivateContext(_indicatorChartToken);
			}

			if (_tradingChartToken != null) {
				service.deactivateContext(_tradingChartToken);
			}

			if (_realtimeChartToken != null) {
				service.deactivateContext(_realtimeChartToken);
			}
		}
	}

	protected IContextService getContextService() {
		if (_chartView == null) {
			return null;
		}
		IWorkbenchPartSite site = ((ViewPart) _chartView).getSite();
		if (site == null) {
			return null;
		}
		IContextService service = (IContextService) site
				.getService(IContextService.class);
		return service;
	}

	public IChartView getChartView() {
		return _chartView;
	}

	@Override
	public void dispose(IChartView chartView) {
		deactivateContexts(getContextService());

		_chart.breakLink();
		_animator.stop();
		ChartPlugin.getDefault().getPreferenceStore()
				.removePropertyChangeListener(_prefsListener);
	}

	@Override
	public final void scrollChart() {
		if (_lastTickToken < _tickToken) {
			_lastTickToken = _tickToken;
			if (_chart != null && _chart.getModel().isAlive()
					&& !_chart.isDragging()) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						scrollChartImpl();
					}
				});
			}
		}
	}

	protected void scrollChartImpl() {
		ScrollingMode mode = _chart.getScrollingMode();
		switch (mode) {
		case SCROLLING:
			_chart.rtScrollChartToEnd(false);
			break;
		case ZOOMING_OUT:
			_chart.zoomOutAll(false);
			break;
		case NONE:
			break;
		}
	}

	@Override
	public void shuttingDown() {
		_animator.stop();
	}
}
