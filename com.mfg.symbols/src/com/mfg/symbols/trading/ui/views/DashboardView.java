package com.mfg.symbols.trading.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.mfg.broker.events.ITradeMessage;
import com.mfg.broker.events.TradeMessageType;
import com.mfg.logger.ILogRecord;
import com.mfg.logger.ILogger;
import com.mfg.logger.ILoggerListener;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.LogLevel;
import com.mfg.logger.LogRecord;
import com.mfg.strategy.IStrategyFactory;
import com.mfg.strategy.IStrategySettings;
import com.mfg.strategy.PortfolioStrategy;
import com.mfg.strategy.logger.TradeMessageWrapper;
import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.jobs.SymbolJob;
import com.mfg.symbols.jobs.SymbolJobChangeAdapter;
import com.mfg.symbols.jobs.SymbolJobChangeEvent;
import com.mfg.symbols.jobs.TradingPipe;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.configurations.TradingConfigurationInfo;
import com.mfg.symbols.trading.ui.actions.ChangeConfigurationSetAction;
import com.mfg.symbols.trading.ui.dashboard.DashboardCanvas;
import com.mfg.symbols.trading.ui.dashboard.FigureAdapter;
import com.mfg.symbols.trading.ui.dashboard.GaugeAdapter;
import com.mfg.symbols.trading.ui.dashboard.MeterAdapter;
import com.mfg.symbols.trading.ui.dashboard.PolylineAdapter;
import com.mfg.symbols.trading.ui.dashboard.ProgressBarAdapter;
import com.mfg.symbols.trading.ui.dashboard.StrategyAdapter;
import com.mfg.symbols.trading.ui.dashboard.TankAdapter;
import com.mfg.utils.Utils;

public class DashboardView extends ViewPart implements ITradingView,
		ILoggerListener {

	static final LogRecord FAKE_LOG_RECORD = new LogRecord(0, LogLevel.ANY, 0,
			"fake-source", new TradeMessageWrapper(new ITradeMessage() {

				@Override
				public void setTickScale(int scale) {
					//
				}

				@Override
				public TradeMessageType getType() {
					return new TradeMessageType("type");
				}

				@Override
				public int getTickScale() {
					return 0;
				}

				@Override
				public String getSource() {
					return "source";
				}

				@Override
				public String getEvent() {
					return "event";
				}
			}));
	private static final String MEMENTO_CONF_ID = "conf-id";
	public static final String ID = "com.mfg.symbols.trading.ui.views.DashboardView"; //$NON-NLS-1$
	private static final String MEMENTO_CANVAS_JSON = "canvas";
	DashboardCanvas _canvas;
	private Action _delAction;
	private Action _addGaugeAction;
	private Action _addTankAction;
	private Action _addMeterAction;
	private Action _addPolylineAction;
	private MenuManager _menu;
	private JSONObject _mementoObj;
	TradingConfiguration _configuration;
	private int _configSet;
	private String _initConfId;
	private ChangeConfigurationSetAction _configSetAction;
	private ILoggerManager _logManager;
	private Action _addStrategyAction;
	private PortfolioStrategy _portfolio;
	private PropertyChangeListener _confListener;
	private SymbolJob<?> _job;
	private SymbolJobChangeAdapter _warmupListener;

	public DashboardView() {
		_confListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						configureStrategyWidgets(false);
					}
				});
			}
		};
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));

		_canvas = new DashboardCanvas(parent);
		if (_mementoObj != null) {
			try {
				JSONObject json = _mementoObj.getJSONObject("canvas");
				_canvas.load(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		createActions();
		initializeToolBar();
		initializeMenu();
		initializeContextMenu();

		if (_initConfId != null) {
			TradingConfiguration conf = SymbolsPlugin.getDefault()
					.getTradingStorage().findById(_initConfId);
			if (conf != null) {
				setConfiguration(conf);
			}
		}
	}

	protected void updateMenu() {
		IFigure fig = _canvas.getSelection();
		_delAction.setEnabled(fig != null);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if (memento != null) {
			String json = memento.getString("canvas");
			if (json != null) {
				try {
					_mementoObj = new JSONObject(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			String confId = memento.getString(MEMENTO_CONF_ID);
			if (confId != null) {
				_initConfId = confId;
			}
		}
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putString(MEMENTO_CONF_ID, _configuration == null ? null
				: _configuration.getUUID().toString());

		JSONStringer s = new JSONStringer();
		try {
			s.object();
			s.key("canvas");
			s.object();
			_canvas.toJSON(s);
			s.endObject();
			s.endObject();
			memento.putString(MEMENTO_CANVAS_JSON, s.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		abstract class AddAction extends Action {
			public AddAction(String name) {
				super("Add " + name);
				setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
						.getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
			}

			public abstract FigureAdapter<?> createAdapter(
					DashboardCanvas canvas);

			public Rectangle getBounds() {
				return null;
			}

			@Override
			public void run() {
				_canvas.addFigure(createAdapter(_canvas), getBounds());
			}
		}

		_addGaugeAction = new AddAction("Gauge") {

			@Override
			public FigureAdapter<?> createAdapter(DashboardCanvas canvas) {
				return new GaugeAdapter(canvas);
			}
		};
		_addTankAction = new AddAction("Tank") {
			@Override
			public FigureAdapter<?> createAdapter(DashboardCanvas canvas) {
				return new TankAdapter(canvas);
			}
		};
		_addMeterAction = new AddAction("Meter") {

			@Override
			public FigureAdapter<?> createAdapter(DashboardCanvas canvas) {
				return new MeterAdapter(canvas);
			}
		};

		_addMeterAction = new AddAction("Progress Bar") {

			@Override
			public FigureAdapter<?> createAdapter(DashboardCanvas canvas) {
				return new ProgressBarAdapter(canvas);
			}

			@Override
			public Rectangle getBounds() {
				return new Rectangle(20, 20, 300, 100);
			}

		};

		_addStrategyAction = new AddAction("Strategy") {

			@Override
			public FigureAdapter<?> createAdapter(DashboardCanvas canvas) {
				return new StrategyAdapter(canvas);
			}

			@Override
			public void run() {
				super.run();
				configureStrategyWidgets(false);
			}
		};
		_addPolylineAction = new AddAction("Polyline") {

			@Override
			public FigureAdapter<?> createAdapter(DashboardCanvas canvas) {
				PolylineAdapter adapter = new PolylineAdapter(canvas);
				if (_configuration != null) {
					adapter.setConfiguration(_configuration);
				}
				return adapter;
			}
		};

		_delAction = new Action("Delete") {
			@Override
			public void run() {
				_canvas.removeSelection();
			}
		};
		_delAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
		_delAction.setDisabledImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE_DISABLED));

		// --
		_configSetAction = new ChangeConfigurationSetAction(this, true);
	}

	private void initializeContextMenu() {
		_menu = new MenuManager();
		_menu.add(_addGaugeAction);
		_menu.add(_addTankAction);
		_menu.add(_addMeterAction);
		_menu.add(new Separator());
		_menu.add(_addStrategyAction);
		_menu.add(new Separator());
		_menu.add(_addPolylineAction);
		_menu.add(new Separator());
		_menu.add(_delAction);

		// menu.add(_delAction);

		_menu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				updateMenu();
			}
		});

		_canvas.setMenu(_menu.createContextMenu(_canvas));
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbar = getViewSite().getActionBars()
				.getToolBarManager();
		toolbar.add(_configSetAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		@SuppressWarnings("unused")
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();

	}

	@Override
	public void setFocus() {
		_canvas.setFocus();
	}

	@Override
	public TradingConfiguration getConfiguration() {
		return _configuration;
	}

	@Override
	public void setConfiguration(TradingConfiguration configuration) {
		if (_warmupListener == null) {
			_warmupListener = new SymbolJobChangeAdapter() {
				@Override
				public void warmingUpFinished(SymbolJobChangeEvent event) {
					try {
						Utils.debug_id(8863352,
								"Dashboard: Warming up finished");
						// send fake event to put the dashboard in black
						logged(null, FAKE_LOG_RECORD);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			SymbolJob.getManager().addJobChangeListener(_warmupListener);
		}
		_job = null;
		if (_configuration != null) {
			removeConfListener();
		}

		_configuration = configuration;
		if (configuration != null) {
			IStrategySettings settings = getSettings(configuration);
			if (settings == null) {
				_configuration = null;
			} else {
				settings.addPropertyChangeListener(_confListener);
				configuration.getInfo().addPropertyChangeListener(
						TradingConfigurationInfo.PROP_STRATEGY_FACTORY_ID,
						_confListener);
				Job[] jobs = Job.getJobManager().find(configuration);
				_job = jobs.length == 0 ? null : (SymbolJob<?>) jobs[0];
			}
		}

		if (Display.getDefault().getThread() == Thread.currentThread()) {
			configure();
		} else {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					configure();
				}
			});
		}
	}

	private void removeConfListener() {
		getSettings(_configuration).removePropertyChangeListener(_confListener);
		_configuration.getInfo().removePropertyChangeListener(
				TradingConfigurationInfo.PROP_STRATEGY_FACTORY_ID,
				_confListener);
	}

	private static IStrategySettings getSettings(
			TradingConfiguration configuration) {
		return configuration.getInfo().getStrategySettings(
				configuration.getInfo().getStrategyFactoryId());
	}

	void configure() {
		String name = _configuration == null ? "Dashboard" : "Dashboard ("
				+ SymbolsPlugin.getDefault().getFullConfigurationName(
						_configuration) + ")";
		setPartName(name);

		if (_logManager != null) {
			_logManager.removeLoggerListener(this);
		}

		if (_configuration != null) {
			TradingPipe pipe = SymbolJob.getRunningTradingPipe(_configuration);
			if (pipe != null) {
				_portfolio = pipe.getPortfolio();
				_logManager = _portfolio.getLogger().getManager();
				_logManager.addLoggerListener(this);
			}
		}
		configureStrategyWidgets(false);
		configurePolylineWidgets();
	}

	private void configurePolylineWidgets() {
		Collection<FigureAdapter<?>> adapters = _canvas.getFigureAdapters();
		for (FigureAdapter<?> adapter : adapters) {
			if (adapter instanceof PolylineAdapter) {
				PolylineAdapter adapter2 = (PolylineAdapter) adapter;
				adapter2.setConfiguration(_configuration);
			}
		}
	}

	void configureStrategyWidgets(boolean closedAccount) {
		IDashboardWidgetProvider dstrategy = null;
		if (_configuration != null) {
			IStrategyFactory factory = SymbolsPlugin.getDefault()
					.getStrategyFactory(
							_configuration.getInfo().getStrategyFactoryId());
			dstrategy = factory.createDashboardWidget(_configuration);
		}

		Collection<FigureAdapter<?>> adapters = _canvas.getFigureAdapters();
		for (FigureAdapter<?> adapter : adapters) {
			if (adapter instanceof StrategyAdapter) {
				StrategyAdapter sadapter = (StrategyAdapter) adapter;
				sadapter.show(dstrategy);
				if (closedAccount && dstrategy != null) {
					dstrategy.repaint(sadapter.getFigure().getLongFig(), null,
							true, null);
					dstrategy.repaint(sadapter.getFigure().getShortFig(), null,
							true, null);
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (_logManager != null) {
			_logManager.removeLoggerListener(this);
		}
		if (_warmupListener != null) {
			SymbolJob.getManager().removeJobChangeListener(_warmupListener);
		}
		if (_configuration != null) {
			removeConfListener();
		}
		super.dispose();
	}

	@Override
	public int getConfigurationSet() {
		return _configSet;
	}

	@Override
	public void setConfigurationSet(int configurationSet) {
		_configSet = configurationSet;
	}

	@Override
	public IViewPart getPart() {
		return this;
	}

	@Override
	public void logged(ILogger logger, final ILogRecord record) {
		if (_job != null && _job.getDataSource().isInWarmUp()) {
			return;
		}
		Object msg1 = record.getMessage();
		if (msg1 instanceof TradeMessageWrapper) {
			TradeMessageWrapper msg2 = (TradeMessageWrapper) msg1;
			ITradeMessage msg3 = msg2.getTradeMessage();
			_canvas.handleLogMessage(msg3, _portfolio);
		}
	}

	@Override
	public void begin(ILogger logger, String msg) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				configureStrategyWidgets(true);
			}
		});
	}

}
