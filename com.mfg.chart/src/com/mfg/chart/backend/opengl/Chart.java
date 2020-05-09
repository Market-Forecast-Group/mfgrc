/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */
/**
 *
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.chart.backend.opengl;

//import static java.lang.System.out;

import static java.lang.System.out;

import java.awt.geom.Point2D.Double;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.mfg.mdb.runtime.DBSynchronizer;
import org.mfg.opengl.chart.GLChart;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.Plot;
import org.mfg.opengl.chart.PlotRange;
import org.mfg.opengl.chart.Settings;
import org.mfg.opengl.chart.SnappingMode;
import org.mfg.opengl.chart.interactive.ChartMouseEvent;
import org.mfg.opengl.chart.interactive.ChartPoint;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.mfg.chart.ChartPlugin;
import com.mfg.chart.layers.EquityLayer;
import com.mfg.chart.layers.IChartLayer;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.PriceLayer;
import com.mfg.chart.layers.ProbabilityLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.layers.SyntheticLayer;
import com.mfg.chart.layers.TradingLayer;
import com.mfg.chart.model.ChartModelException;
import com.mfg.chart.model.ChartModel_MDB;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IDataLayerModel;
import com.mfg.chart.model.IPivotCollection;
import com.mfg.chart.model.IPivotModel;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.model.IPriceModel.LayerProjection;
import com.mfg.chart.model.IScaledIndicatorModel;
import com.mfg.chart.model.ITradingModel;
import com.mfg.chart.model.PhysicalPriceModel_MDB;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.AutoRangeType;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.IChartCanvas;
import com.mfg.chart.ui.MouseCursor;
import com.mfg.chart.ui.ScrollingMode;
import com.mfg.chart.ui.TimeOfTheDay;
import com.mfg.chart.ui.osd.tools.InteractiveToolFactory;
import com.mfg.chart.ui.settings.ProfiledObject;
import com.mfg.chart.ui.settings.global.ChartSettingsDialog;
import com.mfg.chart.ui.views.AbstractChartView;
import com.mfg.chart.ui.views.ChartConfig;

public class Chart implements IGLConstantsMFG, IDataLayerModel {

	public static class MainSettings extends Settings {
		private static final String K_AUTO_RANGE_TYPE = "autoRangeType";
		private static final String K_AUTO_RANGE_ENABLED = "autoRangeEnabled";
		public boolean autoRangeEnabled;
		public AutoRangeType autoRangeType;

		public MainSettings() {
			autoRangeEnabled = true;
			autoRangeType = AutoRangeType.AUTORANGE_PRICES;
		}

		public void fillProfile(Profile p) {
			p.putBoolean(K_AUTO_RANGE_ENABLED, autoRangeEnabled);
			p.putString(K_AUTO_RANGE_TYPE, autoRangeType.name());

			p.putFloatArray(PREF_GL_CHART_BG_COLOR, getBgColor());
			p.putFloatArray(PREF_GL_CHART_GRID_COLOR, getGridColor());
			p.putFloatArray(PREF_GL_CHART_TEXT_COLOR, getTextColor());
			p.putFloatArray(PREF_GL_CHART_CROSSHAIR_COLOR, getCrosshairColor());
			p.putInt(PREF_GL_CHART_CROSSHAIR_STIPPLE_FACTOR,
					getCrosshairStippleFactor());
			p.putInt(PREF_GL_CHART_CROSSHAIR_STIPPLE_PATTERN,
					getCrosshairStipplePattern());
			p.putInt(PREF_GL_CHART_GRID_STIPPLE_FACTOR, getGridStippleFactor());
			p.putInt(PREF_GL_CHART_GRID_STIPPLE_PATTERN,
					getGridStipplePattern());

			p.putFloat(PREF_GL_CHART_GRID_WIDTH, getGridWidth());
			p.putFloat(PREF_GL_CHART_CROSSHAIR_WIDTH, getCrosshairWidth());

			p.putString(PREF_GL_CHART_SNAPPING_OVER_PRICES, getSnappingMode()
					.name());
		}

		public void updateFromProfile(Profile p) {
			autoRangeEnabled = p.getBoolean(K_AUTO_RANGE_ENABLED, true);
			autoRangeType = AutoRangeType.valueOf(p.getString(
					K_AUTO_RANGE_TYPE, AutoRangeType.AUTORANGE_PRICES.name()));

			setBgColor(p.getFloatArray(PREF_GL_CHART_BG_COLOR, COLOR_BLACK));
			setGridColor(p.getFloatArray(PREF_GL_CHART_GRID_COLOR,
					COLOR_DARK_GRAY));
			setTextColor(p.getFloatArray(PREF_GL_CHART_TEXT_COLOR, COLOR_WHITE));
			setCrosshairColor(p.getFloatArray(PREF_GL_CHART_CROSSHAIR_COLOR,
					COLOR_CYAN));

			setCrosshairStippleFactor(p.getInt(
					PREF_GL_CHART_CROSSHAIR_STIPPLE_FACTOR, STIPPLE_FACTOR_1));
			setCrosshairStipplePattern((short) p.getInt(
					PREF_GL_CHART_CROSSHAIR_STIPPLE_PATTERN, STIPPLE_PATTERN));
			setGridStippleFactor(p.getInt(PREF_GL_CHART_GRID_STIPPLE_FACTOR,
					STIPPLE_FACTOR_3));
			setGridStipplePattern((short) p.getInt(
					PREF_GL_CHART_GRID_STIPPLE_PATTERN, STIPPLE_PATTERN));

			setGridWidth(p.getFloat(PREF_GL_CHART_GRID_WIDTH, 1.5f));
			setCrosshairWidth(p.getFloat(PREF_GL_CHART_CROSSHAIR_WIDTH, 1.5f));

			setSnappingMode(SnappingMode.valueOf(p.getString(
					PREF_GL_CHART_SNAPPING_OVER_PRICES,
					SnappingMode.SNAP_Y.name())));

		}
	}

	public enum TimeOfTheDayLabelMode {
		NEVER_SHOW_LABELS("Never Show Labels"), ALWAYS_SHOW_LABELS(
				"Always Show Labels"), ONLY_SHOW_LABELS_CLOSE_TO_THE_CROSSHAIR(
				"Only Show Labels Close To The Crosshair");
		private String _str;

		private TimeOfTheDayLabelMode(String str) {
			this._str = str;
		}

		@Override
		public String toString() {
			return _str;
		}
	}

	public static class TimeOfTheDaySettings {
		private static final String K_TIMES_OF_THE_DAY_LABEL_MODE = "timesOfTheDay.labelMode";
		private static final String K_TIMES_OF_THE_DAY_MAX_NUMBER = "timesOfTheDay.maxNumber";
		private static final String K_TIMES_OF_THE_DAY_VISIBLE = "timesOfTheDay.visible";
		private TimeOfTheDay[] _timesOfTheDay;
		private int _maxNumberOfTimesOfTheDay;
		private TimeOfTheDayLabelMode _timeOfTheDayLabelMode;
		private boolean _visible;

		public TimeOfTheDaySettings() {
			_timesOfTheDay = new TimeOfTheDay[0];
			_maxNumberOfTimesOfTheDay = 5;
			_timeOfTheDayLabelMode = TimeOfTheDayLabelMode.ONLY_SHOW_LABELS_CLOSE_TO_THE_CROSSHAIR;
			_visible = true;
		}

		/**
		 * @return the timeOfTheDayLabelMode
		 */
		public TimeOfTheDayLabelMode getTimeOfTheDayLabelMode() {
			return _timeOfTheDayLabelMode;
		}

		/**
		 * @param timeOfTheDayLabelMode
		 *            the timeOfTheDayLabelMode to set
		 */
		public void setTimeOfTheDayLabelMode(
				TimeOfTheDayLabelMode timeOfTheDayLabelMode) {
			this._timeOfTheDayLabelMode = timeOfTheDayLabelMode;
		}

		/**
		 * @return the maxNumberOfTimesOfTheDay
		 */
		public int getMaxNumberOfTimesOfTheDay() {
			return _maxNumberOfTimesOfTheDay;
		}

		/**
		 * @param maxNumberOfTimesOfTheDay
		 *            the maxNumberOfTimesOfTheDay to set
		 */
		public void setMaxNumberOfTimesOfTheDay(int maxNumberOfTimesOfTheDay) {
			this._maxNumberOfTimesOfTheDay = maxNumberOfTimesOfTheDay;
		}

		/**
		 * @return the timesOfTheDay
		 */
		public TimeOfTheDay[] getTimesOfTheDay() {
			return _timesOfTheDay;
		}

		/**
		 * @param timesOfTheDay
		 *            the timesOfTheDay to set
		 */
		public void setTimesOfTheDay(TimeOfTheDay[] timesOfTheDay) {
			this._timesOfTheDay = timesOfTheDay;
		}

		public boolean isVisible() {
			return _visible;
		}

		public void setVisible(boolean visible) {
			_visible = visible;
		}

		public void fillProfile(Profile p) {
			TimeOfTheDay.write(p, _timesOfTheDay);
			p.putInt(K_TIMES_OF_THE_DAY_MAX_NUMBER, _maxNumberOfTimesOfTheDay);
			p.putString(K_TIMES_OF_THE_DAY_LABEL_MODE,
					_timeOfTheDayLabelMode.name());
			p.putBoolean(K_TIMES_OF_THE_DAY_VISIBLE, _visible);
		}

		public void updateFromProfile(Profile p) {
			_timesOfTheDay = TimeOfTheDay.read(p);
			_maxNumberOfTimesOfTheDay = p.getInt(K_TIMES_OF_THE_DAY_MAX_NUMBER,
					5);
			_timeOfTheDayLabelMode = TimeOfTheDayLabelMode
					.valueOf(p
							.getString(
									K_TIMES_OF_THE_DAY_LABEL_MODE,
									TimeOfTheDayLabelMode.ONLY_SHOW_LABELS_CLOSE_TO_THE_CROSSHAIR
											.name()));
			_visible = p.getBoolean(K_TIMES_OF_THE_DAY_VISIBLE, true);
		}

	}

	public GLChart glChart;
	private Plot plot;
	private FeedbackMessages feedbackMessages;
	private int _tickScale;
	private MFGChartCustomization _custom;
	private Object _lastSettingsDlgSelection;
	private ProfiledObject _mainSettingsProfiledObject;
	private TimeOfTheDaySettings _timeOfTheDaySettings;
	private ProfiledObject _timeOfTheDayProfiledObject;
	private MainSettings _mainSettings;
	private SyntheticLayer _syntheticLayer;
	private PlotRange _lastXRange;

	public Chart(final String chartName, final IChartModel model,
			final ChartType type, Object content) {
		this._model = model;
		model.setRangeModel(this);
		this._type = type;
		this._chartName = chartName;
		this._content = content;
		_disposed = false;
		_token = 0;
		_scrollingMode = ScrollingMode.NONE;
		_lastLowerTime = -1;
		_lastUpperTime = -1;

		_rangeChanged = new CopyOnWriteArrayList<>();
		_disposeListeners = new CopyOnWriteArrayList<>();
		_mirrors = new ArrayList<>();
		_historyNode = null;

		initialize();

		addRangeChangedAction(new Runnable() {

			@Override
			public void run() {
				domainRangeChanged();
			}
		});

		_prefsListener = new IPropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				updatePreferences(ChartPlugin.getDefault().getPreferenceStore());
			}
		};
		if (ChartPlugin.getDefault() != null) {
			ChartPlugin.getDefault().getPreferenceStore()
					.addPropertyChangeListener(_prefsListener);
		}

		// we do not want to zoom all always! see ChartView.setContent(obj,
		// range)
		// zoomOutAll();

		if (_model instanceof ChartModel_MDB) {
			_sync = ((ChartModel_MDB) _model).getPriceSession()
					.getSynchronizer();
			_repaintRun = new Runnable() {

				@Override
				public void run() {
					repaint();
				}
			};
		}
		_tickScale = 0;

	}

	void initSettings() {
		{
			_mainSettingsProfiledObject = new ProfiledObject() {

				@Override
				protected List<Profile> createProfilePresets() {
					Profile p = new Profile("Profile 1");
					MainSettings s = new MainSettings();
					MFGChartCustomization c = new MFGChartCustomization(
							Chart.this);
					s.fillProfile(p);
					c.fillProfile(p);
					return Arrays.asList(p);
				}

				@Override
				public String getProfileKeySet() {
					return "chartMainSettings";
				}
			};
			_mainSettings = new MainSettings();
			getGLChart().setSettings(_mainSettings);
			_mainSettings.updateFromProfile(_mainSettingsProfiledObject
					.getProfile());
		}

		{
			_timeOfTheDayProfiledObject = new ProfiledObject() {

				@Override
				protected List<Profile> createProfilePresets() {
					Profile p = new Profile("Profile 1");
					return Arrays.asList(p);
				}

				@Override
				public String getProfileKeySet() {
					return "timesOftheDay";
				}
			};
			_timeOfTheDaySettings = new TimeOfTheDaySettings();
			_timeOfTheDaySettings.updateFromProfile(_timeOfTheDayProfiledObject
					.getProfile());
		}
	}

	public ProfiledObject getMainSettingsProfiledObject() {
		return _mainSettingsProfiledObject;
	}

	public MainSettings getMainSettings() {
		return _mainSettings;
	}

	public void setMainSettings(MainSettings mainSettings) {
		_mainSettings = mainSettings;
		getGLChart().setSettings(mainSettings);
	}

	public InteractiveTool[] getTools() {
		return glChart.getTools();
	}

	@SuppressWarnings("unchecked")
	public <T extends InteractiveTool> T getTool(Class<T> cls) {
		for (InteractiveTool t : getTools()) {
			if (t.getClass() == cls) {
				return (T) t;
			}
		}
		return null;
	}

	public MFGChartCustomization getCustom() {
		return _custom;
	}

	/**
	 * @return the feedbackMessages
	 */
	public FeedbackMessages getFeedbackMessages() {
		return feedbackMessages;
	}

	protected void initialize() {
		glChart = new GLChart();
		_custom = new MFGChartCustomization(this);
		glChart.setCustom(_custom);
		IPriceModel priceModel = getModel().getPriceModel();
		glChart.yTickSize = priceModel.getTickSize();
		setTickScale(priceModel.getTickScale());
		plot = glChart.plot;
		plot.xrange = new PlotRange(0, 10);
		plot.yrange = new PlotRange(0, 10);

		List<InteractiveTool> list = new ArrayList<>();

		// it is null when the chart is run as a stand-along app.
		if (ChartPlugin.getDefault() != null) {
			InteractiveToolFactory[] interactiveToolFactories = ChartPlugin
					.getDefault().getInteractiveToolFactories();
			for (InteractiveToolFactory factory : interactiveToolFactories) {
				InteractiveTool tool = factory.createTool(this);
				if (tool != null) {
					tool.init(glChart);
					list.add(tool);
				}
			}
		}
		glChart.setTools(list.toArray(new InteractiveTool[list.size()]));

		initSettings();

		ChartType type = getType();
		ChartType chartType = type;
		_autoDataLayer = true;

		if (chartType.hasPrices()) {
			_priceLayer = new PriceLayer(this);
		}

		if (chartType.hasChannels() || chartType == ChartType.SYNTHETIC) {
			IScaledIndicatorModel indicatorModel = _model
					.getScaledIndicatorModel();
			int firstScale = indicatorModel.getFirstScale();
			_indicatorLayer = new IndicatorLayer(this, firstScale);

		}

		if (chartType == ChartType.SYNTHETIC) {
			_syntheticLayer = new SyntheticLayer(this);
		}

		if (chartType.hasExecutions()) {
			_tradingLayer = new TradingLayer(this);
		}

		if (chartType.hasEquity()) {
			_equityLayer = new EquityLayer(this);
		}

		// if the chart is running as plugin
		if (ChartPlugin.getDefault() != null) {
			updatePreferences(ChartPlugin.getDefault().getPreferenceStore());
		}

		if (getModel().getDataLayerCount() == 1) {
			setAutoDataLayer(false);
		}

		feedbackMessages = new FeedbackMessages(this);
		glChart.addOSDDrawable(feedbackMessages);

		IDataset snappingDataset;
		switch (type) {
		case SYNTHETIC:
			snappingDataset = getSyntheticLayer().getCrossSnappingDataset();
			break;
		case EQUITY:
			snappingDataset = getEquityLayer().getCrossSnappingDataset();
			break;
		// $CASES-OMITTED$
		default:
			snappingDataset = getPriceLayer().getCrossSnappingDataset();
			break;
		}
		glChart.setCrossSnappingDataset(snappingDataset);
	}

	public TimeOfTheDaySettings getTimeOfTheDaySettings() {
		return _timeOfTheDaySettings;
	}

	public void setTimeOfTheDaySettings(
			TimeOfTheDaySettings timeOfTheDaySettings) {
		_timeOfTheDaySettings = timeOfTheDaySettings;
	}

	public ProfiledObject getTimeOfTheDayProfiledObject() {
		return _timeOfTheDayProfiledObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.backend.opengl.PriceChart_OpenGL#setShowCross(boolean)
	 */

	public void setShowCross(final boolean show) {
		glChart.drawCrosshair = show;
	}

	public void addDataset(final IDataset ds, final ISeriesPainter paniter) {
		plot.addDataset(ds, paniter);
	}

	public void setXRange(final PlotRange range) {
		plot.xrange = range;
	}

	public PlotRange getXRange() {
		return plot.xrange;
	}

	public void setYRange(final PlotRange range) {
		plot.yrange = range;
	}

	public PlotRange getYRange() {
		return plot.yrange;
	}

	public GLChart getGLChart() {
		return glChart;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param mouseButton
	 */
	public void mouseUp(final int x, int y, final int width, final int height,
			int mouseButton) {
		_lastDragX = -1;
		_lastDragY = -1;
		_dragging = false;

		boolean doclick = true;

		if (isSelecting()) {
			glChart.setRangeSelected(false);

			double lower = glChart.getLowerXSelection();
			double upper = glChart.getUpperXSelection();
			if (upper < lower) {
				final double a = lower;
				lower = upper;
				upper = a;
			}
			double len = upper - lower;
			int len2 = getXRange().screenWidth(len, getPlotScreenWidth());

			if (len2 > 20) {
				if (upper - lower > 2) {
					PlotRange yrange = new PlotRange(
							glChart.getLowerYSelection(),
							glChart.getUpperYSelection());
					if (yrange.getLength() < 0) {
						yrange = new PlotRange(yrange.upper, yrange.lower);
					}
					setYRange(yrange);
					setXRangeAsZoomOperation(new PlotRange(lower, upper));
					fireRangeChanged();
				} else {
					repaint();
				}
				doclick = false;
			}
		}

		if (doclick) {
			if (glChart.getSelectedTool() != null) {
				glChart.getSelectedTool().mouseReleased(
						new ChartMouseEvent(x, y, glChart, mouseButton));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.backend.opengl.PriceChart_OpenGL#mouseMoved(int, int)
	 */

	public void mouseMoved(final int x, final int y) {
		_lastDragX = x;
		_lastDragY = y;

		glChart.setCrosshair(x, y);

		InteractiveTool tool = glChart.getSelectedTool();

		if (tool != null) {
			double xplot = glChart.convertScreenToPlot_X(x);
			double yplot = glChart.convertScreenToPlot_Y(y);
			ChartMouseEvent e = new ChartMouseEvent(new ChartPoint(x, y, xplot,
					yplot), 0, -1);
			tool.mouseMoved(e);
			if (_view != null) {
				// put focus on the chart because the tool probably if doing
				// something
				IWorkbenchPage page = _view.getViewSite().getPage();
				if (page.getActivePart() != _view) {
					out.println("activate chart " + _view.getPartName());
					page.activate(_view);
				}
			}
		}
		_token++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.backend.opengl.PriceChart_OpenGL#mouseDragged(int,
	 * int, int, int)
	 */

	public void mouseDragged(final int x, final int y, final int width,
			final int height, boolean ctrlPressed, int mouseButton) {
		if (isStatic()) {
			return;
		}

		boolean doit = glChart.getSelectedTool() == null
				|| !glChart.getSelectedTool().mouseDragged(
						new ChartMouseEvent(x, y, glChart, mouseButton));
		if (doit) {
			_dragging = true;
			if (!isSelecting()) {
				boolean fire = false;
				if (!ctrlPressed) {
					if (_lastDragX != -1) {
						final int dx = _lastDragX - x;
						final PlotRange range = getXRange();
						final double amount = range.plotWidth(dx, width);
						if (amount < 0 && !_fixedTimeLower || amount > 0
								&& !_fixedTimeUpper) {
							PlotRange newRange = new PlotRange(range.lower
									+ amount, range.upper + amount);
							setXRange(newRange);
							fire = true;
						}
					}
				}
				_lastDragX = x;
				if (ctrlPressed || !isAutoRangeEnabled()) {
					if (_lastDragY != -1) {
						final int dy = _lastDragY - y;
						final PlotRange range = getYRange();
						final double amount = range.plotWidth(dy, height);
						PlotRange newRange = new PlotRange(
								range.lower + amount, range.upper + amount);
						setYRange(newRange);
						fire = true;
					}
				}
				_lastDragY = y;
				if (fire) {
					if (ctrlPressed) {
						setAutoRangeEnabled(false);
					}
					fireRangeChanged();
				}
			}

			glChart.setCrosshair(x, y);

			glChart.setUpperXSelection(glChart.convertScreenToPlot_X(x));
			glChart.setUpperYSelection(glChart.convertScreenToPlot_Y(y));

			if (isSelecting()) {
				repaint();
			}
		}
		_token++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.backend.opengl.PriceChart_OpenGL#isSelecting()
	 */

	public boolean isSelecting() {
		return glChart.isRangeSelected();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param button
	 */
	public void mouseDown(final int x, final int y, final int width,
			final int height, final int button) {
		boolean doit = glChart.getSelectedTool() == null
				|| !glChart.getSelectedTool().mousePressed(
						new ChartMouseEvent(x, y, glChart, button));
		if (doit) {
			double plotX = glChart.convertScreenToPlot_X(x);
			double plotY = glChart.convertScreenToPlot_Y(y);
			glChart.setRangeSelected(button == 3);
			glChart.setLowerXSelection(plotX);
			glChart.setUpperXSelection(plotX);
			glChart.setLowerYSelection(plotY);
			glChart.setUpperYSelection(plotY);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.backend.opengl.PriceChart_OpenGL#getDesiredMouseCursorAt
	 * (int, int)
	 */

	public MouseCursor getDesiredMouseCursorAt(final int x, int y) {
		MouseCursor cursor = glChart.getSelectedTool() == null ? null : glChart
				.getSelectedTool().getMouseCursor();
		if (cursor == null) {
			if (x <= glChart.xMargin || y <= glChart.yMargin) {
				cursor = MouseCursor.DEFAULT;
			} else {
				cursor = MouseCursor.CROSSHAIR;
			}
		}
		return cursor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.backend.opengl.PriceChart_OpenGL#getLeftMargin()
	 */

	public int getPlotLeftMargin() {
		return glChart.xMargin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.backend.opengl.PriceChart_OpenGL#getBottomMargin()
	 */

	public int getPlotBottomMargin() {
		return glChart.yMargin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.backend.opengl.PriceChart_OpenGL#getScreenLength()
	 */

	public int getPlotScreenWidth() {
		return glChart.plot.screenWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.backend.opengl.PriceChart_OpenGL#getPlotScreenHeight()
	 */

	public int getPlotScreenHeight() {
		return glChart.plot.screenHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.backend.opengl.PriceChart_OpenGL#setCrosshairInPlot(double,
	 * double)
	 */

	public void setCrosshairInPlot(double x, double y) {
		glChart.setCrosshairInPlot(x, y);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	@SuppressWarnings("static-method")
	public String getTooltip(int x, int y) {
		return null;
	}

	public void setTickScale(final int tickScale) {
		this._tickScale = tickScale;
		Format f;
		if (tickScale == 0) {
			f = NumberFormat.getInstance();
		} else {
			final NumberFormat decimal = NumberFormat.getInstance();
			decimal.setMinimumFractionDigits(tickScale);
			decimal.setMaximumFractionDigits(tickScale);
			f = new Format() {

				private static final long serialVersionUID = 1L;

				@Override
				public StringBuffer format(Object obj, StringBuffer toAppendTo,
						FieldPosition pos) {
					java.lang.Double d = java.lang.Double
							.valueOf(((Number) obj).doubleValue());
					d = java.lang.Double.valueOf(d.doubleValue()
							/ Math.pow(10, tickScale));
					return decimal.format(d, toAppendTo, pos);
				}

				@Override
				public Object parseObject(String source, ParsePosition pos) {
					throw new UnsupportedOperationException(
							"This format is just to format chart prices, not to parse.");
				}

			};
		}
		_custom.setFormatYValues(f);
	}

	public int getTickScale() {
		return _tickScale;
	}

	public static String getSnappingName(SnappingMode mode) {
		switch (mode) {
		case DO_NOT_SNAP:
			return "Does not Snap";
		case SNAP_XY:
			return "Snaps Over Prices";
		case SNAP_Y:
			return "Snaps To The Closest Tick";
		}
		return null;
	}

	public void shiftUp(int percent) {
		setAutoRangeEnabled(false);
		PlotRange range = getYRange();
		double amount = range.getLength() / percent;
		range = range.getMovedTo(range.getMiddle() + amount);
		setYRange(range);
		fireRangeChanged();
	}

	public int openSettingsWindow(Shell shell, Object selection, Object context) {
		ChartSettingsDialog dlg = new ChartSettingsDialog(shell);
		if (selection != null) {
			_lastSettingsDlgSelection = selection;
		}
		dlg.setLastSelection(_lastSettingsDlgSelection);
		dlg.setChart(this);
		dlg.setContext(context);
		int result = dlg.open();
		_lastSettingsDlgSelection = dlg.getLastSelection();
		return result;
	}

	private static final String PREF_GL_CHART_CROSSHAIR_WIDTH = "glChart.crosshairWidth";
	private static final String PREF_GL_CHART_GRID_WIDTH = "glChart.gridWidth";
	private static final String PREF_GL_CHART_TEXT_COLOR = "glChart.textColor";
	private static final String PREF_GL_CHART_SNAPPING_OVER_PRICES = "glChart.snappingOverPrices";
	private static final String PREF_GL_CHART_GRID_STIPPLE_PATTERN = "glChart.gridStipplePattern";
	private static final String PREF_GL_CHART_GRID_STIPPLE_FACTOR = "glChart.gridStippleFactor";
	private static final String PREF_GL_CHART_CROSSHAIR_STIPPLE_PATTERN = "glChart.crosshairStipplePattern";
	private static final String PREF_GL_CHART_CROSSHAIR_STIPPLE_FACTOR = "glChart.crosshairStippleFactor";
	public static final String PREF_GL_CHART_CROSSHAIR_COLOR = "glChart.crosshairColor";
	public static final String PREF_GL_CHART_GRID_COLOR = "glChart.gridColor";
	public static final String PREF_GL_CHART_BG_COLOR = "glChart.bgColor";

	public boolean isAutoRangeEnabled() {
		return _mainSettings.autoRangeEnabled;
	}

	public void setAutoRangeEnabled(boolean enabled) {
		_mainSettings.autoRangeEnabled = enabled;
	}

	public AutoRangeType getAutoRangeType() {
		return _mainSettings.autoRangeType;
	}

	public static final int MIN_PRICES_IN_SCREEN = 10;

	private int _lastDragX = -1;
	private final IChartModel _model;
	private final ChartType _type;
	private final List<Runnable> _rangeChanged;
	private final List<Runnable> _disposeListeners;
	private IChartCanvas _canvas;
	private final String _chartName;
	private int _lastDragY;
	private final IPropertyChangeListener _prefsListener;
	private boolean _dragging = false;
	protected EquityLayer _equityLayer;
	private PriceLayer _priceLayer;
	private IndicatorLayer _indicatorLayer;
	private TradingLayer _tradingLayer;
	private int _dataLayer;
	private boolean _autoDataLayer;
	private double RANGE_BLANK_PERCENT;
	private double START_SCROLLING_PERCENT;
	private double STOP_SCROLLING_PERCENT;
	private boolean _fixedTimeUpper;
	private boolean _fixedTimeLower;
	private ScrollingMode _scrollingMode;
	private final Object _content;
	private boolean _disposed;
	protected long _token;
	private List<Chart> _mirrors;
	private Chart _masterChart;

	private long _lastLowerTime;

	private long _lastUpperTime;

	private boolean _leftBlank;

	private boolean _rightBlank;

	private DBSynchronizer _sync;

	private Runnable _repaintRun;

	class RangeNode {
		PlotRange xrange;
		PlotRange yrange;
		int dataLayer;
		RangeNode next;
		RangeNode prev;

		@Override
		public String toString() {
			return "RangeNode [xrange=" + xrange + ", yrange=" + yrange + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.hashCode();
			result = prime * result + dataLayer;
			result = prime * result
					+ ((xrange == null) ? 0 : xrange.hashCode());
			result = prime * result
					+ ((yrange == null) ? 0 : yrange.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			RangeNode other = (RangeNode) obj;
			if (dataLayer != other.dataLayer)
				return false;
			if (xrange == null) {
				if (other.xrange != null)
					return false;
			} else if (!xrange.equals(other.xrange))
				return false;
			if (yrange == null) {
				if (other.yrange != null)
					return false;
			} else if (!yrange.equals(other.yrange))
				return false;
			return true;
		}
	}

	private RangeNode _historyNode;
	private AbstractChartView _view;

	public Object getContent() {
		return _content;
	}

	protected void updatePreferences(final IPreferenceStore store) {
		RANGE_BLANK_PERCENT = store
				.getDouble(ChartPlugin.PREFERENCES_ZOOM_OUT_ALL_BLANK_PERCENT);
		START_SCROLLING_PERCENT = store
				.getInt(ChartPlugin.PREFERENCES_START_SCROLLING_PERCENT);
		STOP_SCROLLING_PERCENT = store
				.getInt(ChartPlugin.PREFERENCES_STOP_SCROLLING_PERCENT);

		if (_priceLayer != null) {
			_priceLayer.updatePreferences(store);
		}

		if (_indicatorLayer != null) {
			_indicatorLayer.updatePreferences(store);
		}

		if (_tradingLayer != null) {
			_tradingLayer.updatePreferences(store);
		}

		if (_equityLayer != null) {
			_equityLayer.updatePreferences(store);
		}
	}

	public void dispose() {
		_disposed = true;
		ChartPlugin.getDefault().getPreferenceStore()
				.removePropertyChangeListener(_prefsListener);
		for (Runnable action : _disposeListeners) {
			action.run();
		}
	}

	public boolean isDisposed() {
		return _disposed;
	}

	public void setCanvas(final IChartCanvas canvas) {
		this._canvas = canvas;
	}

	public IChartCanvas getCanvas() {
		return _canvas;
	}

	/**
	 * @return the dragging
	 */
	public boolean isDragging() {
		return _dragging;
	}

	public ScrollingMode getScrollingMode() {
		return _scrollingMode;
	}

	public void setScrollingMode(ScrollingMode scrollingMode) {
		_scrollingMode = scrollingMode;
	}

	public void repaint() {
		try {
			if (_canvas != null) {
				_canvas.repaintCanvas();

				updateMirrorsCrosshair();
			}
		} catch (ChartModelException e) {
			e.printStackTrace();
		}
	}

	public void syncRepaint() {
		if (_sync != null) {
			_sync.operation(_repaintRun);
		} else {
			// if the model is not based on MDB, the sync is null, so execute
			// the repaint without sync.
			repaint();
		}
	}

	public SyntheticLayer getSyntheticLayer() {
		return _syntheticLayer;
	}

	/**
	 * @return the indicatorPart
	 */
	public IndicatorLayer getIndicatorLayer() {
		return _indicatorLayer;
	}

	/**
	 * @return the priceLayer
	 */
	public PriceLayer getPriceLayer() {
		return _priceLayer;
	}

	/**
	 * @return the executionLayer
	 */
	public TradingLayer getTradingLayer() {
		return _tradingLayer;
	}

	/**
	 * @return the equityLayer
	 */
	public EquityLayer getEquityLayer() {
		return _equityLayer;
	}

	/**
	 * @return the chartName
	 */
	public String getChartName() {
		return _chartName;
	}

	public long getToken() {
		long modelToken = getModel().getToken();
		return _token + modelToken;
	}

	public void addMirror(Chart mirror) {
		mirror._masterChart = this;
		ArrayList<Chart> list = new ArrayList<>(_mirrors);
		list.add(mirror);
		_mirrors = list;

		if (mirror.getType() == ChartType.EQUITY) {
			mirror.getModel().getTradingModel().setEquityShowIndex(false);
		}
	}

	public void removeMirror(Chart mirror) {
		ArrayList<Chart> list = new ArrayList<>(_mirrors);
		list.remove(mirror);
		_mirrors = list;
		mirror._masterChart = null;
	}

	public List<Chart> getMirrors() {
		return _mirrors;
	}

	public Chart getMasterChart() {
		return _masterChart;
	}

	public void breakLink() {
		if (_masterChart != null) {
			_masterChart.removeMirror(this);
			_masterChart = null;

			if (getType() == ChartType.EQUITY) {
				getModel().getTradingModel().setEquityShowIndex(true);
				zoomOutAll(false);
			}
		}
	}

	public void addRangeChangedAction(final Runnable action) {
		_rangeChanged.add(action);
	}

	public void removeRangeChangeAction(Runnable action) {
		_rangeChanged.remove(action);
	}

	public void fireRangeChanged() {
		_token++;
		for (final Runnable action : _rangeChanged) {
			action.run();
		}
	}

	public void addDisposeListener(final Runnable action) {
		_disposeListeners.add(action);
	}

	public void removeDisposeListener(Runnable action) {
		_disposeListeners.remove(action);
	}

	public void undo() {
		if (_historyNode != null) {
			RangeNode node = _historyNode.prev;
			if (node != null) {
				recoverHistory(node);
				_historyNode = node;
			}
		}
	}

	public void redo() {
		if (_historyNode != null) {
			RangeNode node = _historyNode.next;
			if (node != null) {
				_historyNode = node;
				recoverHistory(node);
			}
		}
	}

	public void clearHistory() {
		_historyNode = null;
	}

	private void recordHistory() {
		RangeNode node = new RangeNode();
		node.xrange = new PlotRange(getXRange());
		node.yrange = new PlotRange(getYRange());
		node.dataLayer = _dataLayer;

		if (_historyNode == null) {
			// out.println("record " + node);
			_historyNode = node;
		} else if (!_historyNode.equals(node)) {
			node.prev = _historyNode;
			_historyNode.next = node;
			_historyNode = node;
			// out.println("record " + node);
		}
	}

	private void recoverHistory(RangeNode node) {
		// out.println("recover " + node);
		setXRange(node.xrange);
		setYRange(node.yrange);
		_dataLayer = node.dataLayer;
		update(isAutoRangeEnabled(), false, false);
		syncRepaint();
	}

	public void zoomOutAll(boolean fire) {
		if (isStatic()) {
			return;
		}

		if (isAutoDataLayer()) {
			_dataLayer = getHighestLayer();
		}
		PlotRange range = getDataTimeRange(_dataLayer);
		double margin = RANGE_BLANK_PERCENT / 100.0 * range.getLength();
		range = new PlotRange(range.lower - margin, range.upper + margin);
		setXRange(range);
		if (fire) {
			fireRangeChanged();
		}
	}

	private boolean isStatic() {
		if (getType() == ChartType.SYNTHETIC) {
			return true;
		}
		return false;
	}

	private int getHighestLayer() {
		int count = getModel().getDataLayerCount();
		for (int layer = count - 1; layer >= 0; layer--) {
			if (getModel().getPriceModel().getDataLayerPricesCount(layer) > 1) {
				return layer;
			}
		}
		return 0;
	}

	private int getLowestLayer() {
		int count = getModel().getDataLayerCount();
		for (int layer = 0; layer < count; layer++) {
			if (getModel().getPriceModel().getDataLayerPricesCount(layer) > 1) {
				return layer;
			}
		}
		return 0;
	}

	private PlotRange getDataTimeRange(int layer) {
		PlotRange range;
		if (getType().hasEquity()) {
			ITradingModel model = getModel().getTradingModel();
			double lower = model.getEquityLowerTime();
			double upper = model.getEquityUpperTime();
			range = new PlotRange(lower, upper);
		} else {

			IPriceModel priceModel = _model.getPriceModel();

			long lower = priceModel.getDataLayerLowerDisplayTime(layer);
			long upper = priceModel.getDataLayerUpperDisplayTime(layer);
			range = new PlotRange(lower, upper);
		}
		return range;
	}

	protected void domainRangeChanged() {
		update();
	}

	public void update() {
		update(isAutoRangeEnabled());
	}

	public void update(final boolean autorange) {
		update(autorange, true);
	}

	public void update(final boolean autorange, boolean doRepaint) {
		update(autorange, doRepaint, true);
	}

	public synchronized void update(final boolean autorange, boolean doRepaint,
			boolean recordHistory) {
		try {
			if (isAutoDataLayer()) {
				switchDataLayerAndRestoreResolution();
			}

			fixTimeRange();

			// first update the indicator layer because the prices needs to know
			// the first visible scale.
			if (_indicatorLayer != null) {
				_indicatorLayer.updateDataset();
			}

			if (_syntheticLayer != null) {
				_syntheticLayer.updateDataset();
			}

			if (_priceLayer != null) {
				_priceLayer.updateDataset();
			}

			if (_equityLayer != null) {
				_equityLayer.updateDataset();
			}

			if (_equityLayer != null) {
				_equityLayer.updateDataset();
			}

			if (_tradingLayer != null) {
				_tradingLayer.updateDataset();
			}

			if (autorange) {
				autorange();
			} else {
				if (getType() != ChartType.EQUITY) {
					if (getYRange().lower < 0) {
						getYRange().lower = 0;
					}
				}
			}

			if (Thread.currentThread() == Display.getDefault().getThread()) {
				updateMirrors();
			} else {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						updateMirrors();
					}
				});

			}

			if (doRepaint) {
				repaint();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		_lastLowerTime = (long) getXRange().lower;
		_lastUpperTime = (long) getXRange().upper;

		if (recordHistory) {
			recordHistory();
		}

	}

	private void switchDataLayerAndRestoreResolution() {
		if (_lastLowerTime != -1) {
			IPriceModel priceModel = getModel().getPriceModel();
			long resolution1 = priceModel.getPricesDistance(_dataLayer,
					_lastLowerTime, _lastUpperTime);

			int oldDataLayer = _dataLayer;

			switchDataLayer();

			if (_dataLayer > oldDataLayer) {
				if (_leftBlank && _rightBlank) {
					// nothing yet
				} else if (_leftBlank) {
					// expands the range to the left
					long lower1 = (long) getXRange().lower;
					long upper = (long) getXRange().upper;
					long resolution2 = priceModel.getPricesDistance(_dataLayer,
							lower1, upper);
					if (resolution1 != resolution2) {
						long lower2 = priceModel.getDisplayTimeOffset(
								_dataLayer, upper, -resolution1);
						getXRange().lower = lower2;
					}
				} else if (_rightBlank) {
					// expands the range to the right
					long lower = (long) getXRange().lower;
					long upper1 = (long) getXRange().upper;
					long resolution2 = priceModel.getPricesDistance(_dataLayer,
							lower, upper1);
					if (resolution1 != resolution2) {
						long upper2 = priceModel.getDisplayTimeOffset(
								_dataLayer, lower, resolution1);
						getXRange().upper = upper2;
					}
				}
			}

		} else {
			switchDataLayer();
		}
	}

	void updateMirrors() {
		PlotRange range = getXRange();
		IPriceModel priceModel = getModel().getPriceModel();
		for (Chart mirror : _mirrors) {
			long a;
			long b;
			IPriceModel mirPriceModel = mirror.getModel().getPriceModel();

			boolean samePriceModel = mirror.getModel().getPriceModel()
					.getClass() == getModel().getPriceModel().getClass();

			boolean sameLayerMode = mirror.isAutoDataLayer()
					&& isAutoDataLayer();

			if (sameLayerMode) {
				mirror._dataLayer = _dataLayer;
			}

			int mirLayer = mirror.getDataLayer();
			if (samePriceModel && _dataLayer == mirLayer) {
				a = (long) getXRange().lower;
				b = (long) getXRange().upper;
			} else {
				long lowerDate = priceModel.getPhysicalTime_from_FakeTime(
						_dataLayer, (long) range.lower);
				long upperDate = priceModel.getPhysicalTime_from_FakeTime(
						_dataLayer, (long) range.upper);

				if (mirPriceModel instanceof PhysicalPriceModel_MDB) {
					a = lowerDate
							- mirPriceModel.getLowerPhysicalTime(_dataLayer);
					b = upperDate
							- mirPriceModel.getLowerPhysicalTime(_dataLayer);
				} else {
					a = mirPriceModel.getDisplayTime_from_PhysicalTime(
							_dataLayer, lowerDate);
					b = mirPriceModel.getDisplayTime_from_PhysicalTime(
							_dataLayer, upperDate);
				}
			}

			// set range and update

			mirror.setXRange(new PlotRange(a, b));
			mirror.update(mirror.isAutoRangeEnabled());
		}
	}

	private void updateMirrorsCrosshair() {
		// crosshair

		for (Chart mirror : _mirrors) {

			Chart glThis = this;
			Chart glMirror = mirror;
			Double point = glThis.glChart.getCrosshairInPlot();
			if (glMirror.getType() == ChartType.EQUITY) {
				ITradingModel model = glMirror.getModel().getTradingModel();
				double total = model.getEquityCloseTotal((long) point.getX());
				point.setLocation(point.getX(), total);
			}
			glMirror.glChart.setCrosshairInPlot(point);
			glMirror.repaint();
		}
	}

	private void autorange() {
		IChartLayer layer = null;
		ChartType type = getType();
		if (type == ChartType.SYNTHETIC) {
			layer = _syntheticLayer;
		} else if (type == ChartType.EQUITY) {
			Assert.isNotNull(_equityLayer);
			layer = _equityLayer;
		} else {
			if (getAutoRangeType() == AutoRangeType.AUTORANGE_PROBS
					&& type.hasProbs()) {
				LinkedList<ScaleLayer> scales = _indicatorLayer.getLayers();
				for (int i = scales.size() - 1; i > 0; i--) {
					ScaleLayer scale = scales.get(i);
					ProbabilityLayer probsLayer = scale.getProbsLayer();
					if (probsLayer.isEnabled()
							&& probsLayer.isVisible()
							&& probsLayer.getAutorangeDataset().getItemCount(0) > 0) {
						layer = probsLayer;
						break;
					}
				}
			}
			if (layer == null) {
				layer = _priceLayer;
			}
		}

		layer.autorange();

		for (InteractiveTool tool : getTools()) {
			if (tool == glChart.getSelectedTool() || tool.isAlwaysPaint()) {
				tool.autorange();
			}
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param count
	 */
	public void mouseScrolled(final int x, final int y, int count) {
		zoom(x, count < 0 ? -1 : 1);
	}

	/**
	 * @param x
	 * @param amount
	 */
	public void zoom(int x, final int amount) {
		if (isStatic()) {
			return;
		}

		if (_scrollingMode == ScrollingMode.SCROLLING) {
			zoom(amount);
			return;
		}

		PlotRange xrange = getXRange();
		double zoom;
		double len = xrange.getLength();
		ChartPlugin plugin = ChartPlugin.getDefault();
		double percent = (plugin == null ? 50 : plugin.getPreferenceStore()
				.getInt(ChartPlugin.PREFERENCES_ZOOM_WHEEL_PERCENT))
				/ (double) 100;
		if (amount > 0) {
			zoom = len * percent;
		} else {
			zoom = len / percent;
		}

		double x2 = x - getPlotLeftMargin();
		double leftPercent = x2 / getPlotScreenWidth();

		double time = xrange.lower + (leftPercent * len);

		double leftZoomLen = leftPercent * zoom;
		double rightZoomLen = zoom - leftZoomLen;

		double lower = time - leftZoomLen;
		double upper = time + rightZoomLen;

		PlotRange dataRange = getDataTimeRange(_dataLayer);

		double margin = RANGE_BLANK_PERCENT / 100.0 * dataRange.getLength();

		lower = Math.max(lower, dataRange.lower - margin);
		upper = Math.min(upper, dataRange.upper + margin);

		boolean zoomIn = amount > 0;

		zoomLayer((long) lower, (long) upper, zoomIn);
	}

	private void zoomLayer(long lower, long upper, boolean zoomIn) {
		if (isStatic()) {
			return;
		}

		long count = getModel().getPriceModel().getPricesDistance(_dataLayer,
				lower, upper);
		if (count > 0 && upper - lower > 0) {
			setXRangeAsZoomOperation(new PlotRange(lower, upper));
			fireRangeChanged();
		} else {
			if (isAutoDataLayer()) {
				if (zoomIn) {
					PlotRange range = getXRange();
					setXRange(new PlotRange(lower, upper));
					if (_dataLayer > 0) {
						for (int layer = _dataLayer - 1; layer >= 0; layer--) {
							LayerProjection projection = getModel()
									.getPriceModel().getLayerProjection(
											_dataLayer, getLowerRangeTime(),
											getUpperRangeTime(), layer);
							if (projection != null && !projection.isOffData()
									&& projection.getTimeLength() > 0) {
								_dataLayer = layer;
								setXRangeAsZoomOperation(new PlotRange(
										projection.getLowerDisplayTime(),
										projection.getUpperDisplayTime()));
								range = null;
								fireRangeChanged();
								break;
							}
						}
					}
					if (range != null) {
						// restore range
						setXRange(range);
					}
				}
			}
		}

	}

	/**
	 * Zoom the chart.
	 * 
	 * @param amount
	 */
	public void zoom(final int amount) {
		if (isStatic()) {
			return;
		}
		PlotRange xrange = getXRange();
		double percent = ChartPlugin.getDefault().getPreferenceStore()
				.getInt(ChartPlugin.PREFERENCES_ZOOM_WHEEL_PERCENT)
				/ (double) 100;
		if (amount != 0) {
			if (_scrollingMode == ScrollingMode.SCROLLING) {
				double zoomout = percent * xrange.getLength();
				rtScrollChartToEnd(true, amount * zoomout);
			} else {

				double zoom;
				if (amount > 0) {
					zoom = xrange.getLength() * percent;
				} else {
					zoom = xrange.getLength() / percent;
				}
				double lower = xrange.getMiddle() - zoom / 2;
				double upper = xrange.getMiddle() + zoom / 2;

				PlotRange dataRange = getDataTimeRange(_dataLayer);
				double margin = RANGE_BLANK_PERCENT / 100.0
						* dataRange.getLength();

				lower = Math.max(lower, dataRange.lower - margin);
				upper = Math.min(upper, dataRange.upper + margin);

				zoomLayer((long) lower, (long) upper, amount > 0);
			}
		}
	}

	/**
	 * @param range
	 */
	protected void setXRangeAsZoomOperation(PlotRange range) {
		setXRange(range);
	}

	public IChartModel getModel() {
		return _model;
	}

	public ChartType getType() {
		return _type;
	}

	public void keyPressed(final char character) {
		InteractiveTool tool = glChart.getSelectedTool();
		if (tool != null) {
			if (!tool.keyPressed(Character.valueOf(character))) {
				return;
			}
		}
	}

	public void expandPriceRange(final double factor) {
		setAutoRangeEnabled(false);
		final PlotRange range = getYRange();
		final double margin = range.getLength() * factor;
		setYRange(new PlotRange(range.lower - margin, range.upper + margin));
		fireRangeChanged();
	}

	public void shift(final double factor) {
		final PlotRange range = getXRange();
		final double len = range.getLength();
		final double delta = factor * len;
		final double lower = range.lower + delta;
		final double upper = range.upper + delta;

		if (factor < 0 && !_fixedTimeLower || factor > 0 && !_fixedTimeUpper) {
			setXRange(new PlotRange(lower, upper));
			fireRangeChanged();
		}
	}

	public void scrollToStart() {
		// int newLayer = getHighestLayer();
		// if (newLayer != dataLayer) {
		// IPriceModel priceModel = getModel().getPriceModel();
		// LayerProjection proj = priceModel.getLayerProjection(dataLayer,
		// getLowerRangeTime(), getUpperRangeTime(), dataLayer);
		// long dateLen = proj.getDateLength();
		// PlotRange range = getDataTimeRange(newLayer);
		// long lowerDate = priceModel.getNearPhysicalTime(newLayer,
		// (long) range.lower);
		// long upperDate = lowerDate + dateLen;
		// long upper = priceModel.getFakeTime(newLayer, upperDate);
		// if (upper - range.lower < 2) {
		// upper += 2;
		// }
		// setXRange(new PlotRange(range.lower, upper));
		// dataLayer = newLayer;
		// }

		final PlotRange range = getDataTimeRange(_dataLayer);
		scrollToStart(range);
	}

	/**
	 * @param fullRange
	 */
	private void scrollToStart(final PlotRange fullRange) {
		double lower = fullRange.lower;

		final PlotRange screenRan = getXRange();

		double screenLen = screenRan.getLength();

		double margin = screenLen * RANGE_BLANK_PERCENT / 100;
		lower = Math.min(screenRan.lower - margin, fullRange.lower - margin);
		PlotRange range = new PlotRange(lower, lower + screenLen);

		setXRange(range);

		fireRangeChanged();
	}

	public void rtScrollChartToEnd(boolean fire, double zoomOut) {
		int lowestLayer = getLowestLayer();

		final PlotRange xrange = getXRange();
		// this is used with the O key, when you zoom-out but the chart is a
		// scrolling mode
		xrange.lower += zoomOut;

		if (isAutoDataLayer() && _dataLayer != lowestLayer
				|| getType().hasEquity()) {
			scrollToEnd();
		} else {

			final double len = xrange.getLength();
			final long lastTime = getModel().getPriceModel()
					.getUpperDisplayTime(_dataLayer);
			double startScrollingTime = xrange.upper
					- (START_SCROLLING_PERCENT / 100.0) * len;
			double stopScrollingShift = len * STOP_SCROLLING_PERCENT / 100.0;

			if (lastTime > startScrollingTime || lastTime < xrange.lower) {
				setXRange(new PlotRange(lastTime - stopScrollingShift, lastTime
						+ len - stopScrollingShift));
			}

			if (fire) {
				fireRangeChanged();
			}
		}
	}

	public void rtScrollChartToEnd(boolean fire) {
		rtScrollChartToEnd(fire, 0);
	}

	/**
	 * The user select scroll to end.
	 */
	public void scrollToEnd() {
		// On 12/9/2013 9:58 AM, Giulio Rugarli wrote:
		//
		// Hi Arian,
		//
		// I was checking the issue
		// "Right 10% blank for higher layers not working" and I noticed that if
		// we are in layer 2 and press the End key, we switch to layer 1 AND we
		// do NOT see 10% right blank.
		//
		// Question: Are we supposed to switch to layer 1 OR we should remainon
		// layer 2 anyhow? If this is the right behaviour, we should at least
		// leave the 10%.
		//
		// I think that this should however not happen because it would be
		// inconsistent with the behavior of the Start key which moves to the
		// beginning of the layer which we are in. So if we are in layer 2 and
		// press End key, we should move to the end of layer 2 showing 10% of
		// layer 1 at the right.
		//
		// If we press AGAIN End key, I think that we should go at that point at
		// the end of layer 1.
		//
		final PlotRange range = getDataTimeRange(_dataLayer);
		scrollToEnd(range);
	}

	/**
	 * Internal method used to scroll to the end of the given range.
	 * 
	 * @param range
	 */
	private void scrollToEnd(final PlotRange fullRange) {
		double upper = fullRange.upper;

		final PlotRange screenRan = getXRange();

		double screenLen = screenRan.getLength();

		double margin = screenLen * RANGE_BLANK_PERCENT / 100;

		upper = Math.max(upper + margin, screenRan.upper + margin);
		PlotRange range = new PlotRange(upper - screenLen, upper);

		setXRange(range);

		fireRangeChanged();
	}

	public void scrollToPoint(long time, double price) {
		double halfTime = getXRange().getLength() / 2;
		double halfPrice = getYRange().getLength() / 2;

		setXRange(new PlotRange(time - halfTime, time + halfTime));
		setYRange(new PlotRange(price - halfPrice, price + halfPrice));

		setCrosshairInPlot(time, price);
		fireRangeChanged();
	}

	/**
	 * Put useful information about the chart in the current range.
	 * 
	 * @param info
	 */
	@SuppressWarnings("boxing")
	public void putInfo(Map<String, Object> info) {
		long lower = (long) getXRange().lower;
		long upper = (long) getXRange().upper;

		IPriceModel priceModel = getModel().getPriceModel();
		DateFormat formatter = DateFormat.getDateTimeInstance();

		info.put("Chart Type", getType());
		info.put("Range Lower", lower);
		info.put("Range Upper", upper);

		info.put("Range Lower Fake Time", priceModel
				.getLowerDisplayTime_from_DisplayTime(_dataLayer, lower));
		info.put("Range Upper Fake Time", priceModel
				.getUpperDisplayTime_from_DisplayTime(_dataLayer, upper));
		long time = priceModel.getLowerPhysicalTime_from_DisplayTime(
				_dataLayer, lower);
		info.put("Range Lower Physical Time", formatter.format(new Date(time)));
		time = priceModel.getUpperPhysicalTime_from_DisplayTime(_dataLayer,
				upper);
		info.put("Range Upper Physical Time", formatter.format(new Date(time)));
		info.put("Visible Data Layer", _dataLayer + 1);

		info.put("DATA LAYER (LOWER/UPPER)", "(FAKE/PHYSICAL) TIME");

		for (int layer = 0; layer < getModel().getDataLayerCount(); layer++) {
			long fake = priceModel.getDataLayerLowerDisplayTime(layer);
			long date = priceModel
					.getPhysicalTime_from_DisplayTime(layer, fake);
			info.put((layer + 1) + ": Lower",
					fake + "/" + formatter.format(new Date(date)));

			fake = priceModel.getDataLayerUpperDisplayTime(layer);
			date = priceModel.getPhysicalTime_from_DisplayTime(layer, fake);
			info.put((layer + 1) + ": Upper ",
					fake + "/" + formatter.format(new Date(date)));
		}

		if (_indicatorLayer != null && _indicatorLayer.isFiltersEnabled()) {
			info.put("DATA LAYER / LEVEL", "# PIVOTS / MAX");

			for (int layer = 0; layer < getModel().getDataLayerCount(); layer++) {
				info.put("Layer " + (layer + 1), "");
				IScaledIndicatorModel indicatorModel = getModel()
						.getScaledIndicatorModel();

				LayerProjection projection = priceModel.getLayerProjection(
						_dataLayer, getLowerRangeTime(), getUpperRangeTime(),
						layer);

				for (ScaleLayer scaleLayer : _indicatorLayer.getScales()) {
					int level = scaleLayer.getLevel();
					IPivotModel pivotModel = indicatorModel
							.getPivotModel(level);
					int pivots = projection == null ? 0 : pivotModel
							.countNegPivots(layer,
									projection.getLowerDisplayTime(),
									projection.getUpperDisplayTime());
					info.put((layer + 1) + "/" + level, pivots + "/"
							+ scaleLayer.getFilterNumber());
				}
			}
		}

		LayerProjection actualProj = priceModel.getLayerProjection(_dataLayer,
				getLowerRangeTime(), getUpperRangeTime(), _dataLayer);
		info.put("LAYER/PERCENT", "");
		for (int layer = 0; layer < getModel().getDataLayerCount(); layer++) {
			LayerProjection proj = priceModel.getLayerProjection(_dataLayer,
					actualProj, layer);
			double percent = proj == null ? 0 : (double) proj.getDateLength()
					/ actualProj.getDateLength() * 100;
			info.put("Percent Layer " + (layer + 1), percent);
		}
	}

	public long getLowerRangeTime() {
		return (long) getXRange().lower;
	}

	public long getUpperRangeTime() {
		return (long) getXRange().upper;
	}

	@Override
	public boolean isAutoDataLayer() {
		return _autoDataLayer;
	}

	@Override
	public void setAutoDataLayer(boolean autoDataLayer) {
		this._autoDataLayer = autoDataLayer;
	}

	public void configure(ChartConfig chartConfig) {
		_autoDataLayer = chartConfig.isAutoDataLayer();
		_dataLayer = chartConfig.getDataLayer();
		setXRange(chartConfig.getRange());
		update(true);
	}

	@Override
	public int getDataLayer() {
		return _dataLayer;
	}

	@Override
	public String setDataLayer(int newDataLayer) {
		String error = null;

		IPriceModel priceModel = _model.getPriceModel();

		LayerProjection projection = priceModel.getLayerProjection(
				this._dataLayer, getLowerRangeTime(), getUpperRangeTime(),
				newDataLayer);
		if (projection == null || projection.isOffData()
				&& (!projection.isHighestTime() || newDataLayer <= _dataLayer)
				|| priceModel.getDataLayerPricesCount(newDataLayer) == 0) {
			error = "Layer " + (newDataLayer + 1)
					+ " does not have data in this range";
		} else {
			if (projection.isOffData()) {
				// It is trying to switch from a lower a layer to an upper layer
				// but the upper layer does not have data.
				// In this case we should duplicate the last point and data.
				error = "No higher level data present. The last data is shown.";
			}

			this._dataLayer = newDataLayer;
			PlotRange range = null;

			if (projection.isHighestTime() && getType().hasChannels()) {
				IPivotModel pivotsModel = getModel().getScaledIndicatorModel()
						.getPivotModel(
								getModel().getScaledIndicatorModel()
										.getFirstScale());
				int count = pivotsModel.getPivotsCount(_dataLayer);
				if (count > 0) {
					int index = Math.max(0, count - 50 - 1);
					IPivotCollection lowerPivot = pivotsModel.getPivotAtIndex(
							_dataLayer, index);
					IPivotCollection lastPivot = pivotsModel.getPivotAtIndex(
							_dataLayer, count - 1);
					long lowerTime = lowerPivot.getTime(0);
					long upperTime = lastPivot.getTime(0);
					if (lowerTime < projection.getLowerDisplayTime()) {
						double margin = RANGE_BLANK_PERCENT / 100.0
								* (upperTime - lowerTime);
						range = new PlotRange(lowerTime, upperTime + margin);
						error = "No higher level data present. Shifting to the last 50 pivots.";
					}
				}
			}

			if (range == null) {
				if (projection.getTimeLength() > 4) {
					range = new PlotRange(projection.getLowerDisplayTime(),
							projection.getUpperDisplayTime());
				} else {
					range = new PlotRange(projection.getLowerDisplayTime() - 4,
							projection.getLowerDisplayTime() + 4);
				}
			}
			setXRange(range);
			fireRangeChanged();
		}
		return error;
	}

	private void switchDataLayer() {
		_leftBlank = false;
		_rightBlank = false;

		int oldDataLayer = _dataLayer;
		PlotRange oldRange = getXRange();

		PlotRange newRange = null;

		long minPercent = 75;
		long screenLen = (long) getXRange().getLength();

		IPriceModel priceModel = _model.getPriceModel();
		int layerCount = getModel().getDataLayerCount();

		LayerProjection actualProj = priceModel.getLayerProjection(_dataLayer,
				getLowerRangeTime(), getUpperRangeTime(), _dataLayer);

		if (actualProj != null) { // if there is data

			_leftBlank = actualProj.isLowestTime();
			_rightBlank = actualProj.isHighestTime();

			if (actualProj.isLowestTime()) { // scrolled to left
				double actualPercent = (double) actualProj.getTimeLength()
						/ screenLen * 100;
				if (actualPercent < minPercent) {
					// has to switch to the higher layer
					int newLayer = _dataLayer + 1;
					if (newLayer < layerCount) {
						LayerProjection newProj = priceModel
								.getLayerProjection(_dataLayer, actualProj,
										newLayer);
						if (newProj != null) {
							long lower = newProj.getLowerDisplayTime();
							long upper = newProj.getUpperDisplayTime();
							long len = upper - lower;
							if (len < 2) {
								lower -= 2;
							}
							_dataLayer = newLayer;
							newRange = new PlotRange(lower, upper);
						}
					}
				}
			} else {
				// look for the first layer with the right percent
				for (int newLayer = 0; newLayer < layerCount; newLayer++) {
					LayerProjection proj = priceModel.getLayerProjection(
							_dataLayer, actualProj, newLayer);
					// if there is data in this layer
					if (proj != null && proj.getDateLength() > 0) {
						double percent = (double) proj.getDateLength()
								/ actualProj.getDateLength() * 100;
						// if this is fine to switch
						if (percent >= minPercent) {
							// if is someone else then switch
							if (newLayer != _dataLayer) {
								_dataLayer = newLayer;
								newRange = new PlotRange(
										proj.getLowerDisplayTime(),
										proj.getUpperDisplayTime());
								if (proj.isHighestTime()) {
									// add 10% blank percent
									double margin = newRange.getLength()
											* RANGE_BLANK_PERCENT / 100;
									double len = newRange.getLength();
									newRange.upper += margin;
									newRange.lower = newRange.upper - len;
								}
							}
							break;
						}
					}
				}
			}
		}

		boolean scrolledToRight = actualProj != null
				&& actualProj.isHighestTime();

		if (newRange != null) {
			setXRange(newRange);
		}

		// check if switch because the number of pivots
		if (_indicatorLayer != null && _indicatorLayer.isFiltersEnabled()
				&& _dataLayer < getHighestLayer()) {
			ScaleLayer lastScale = _indicatorLayer.getScales().getLast();

			int countObjects = lastScale.getZzLayer().countObjects();
			if (countObjects > lastScale.getFilterNumber()) {
				int newLayer = _dataLayer + 1;

				// DEBUG:
				// ((PriceChart_OpenGL) this).getFeedbackMessages().showMessage(
				// "Too many pivots, switch from layer " + (dataLayer + 1)
				// + " to " + (newLayer + 1));

				actualProj = priceModel.getLayerProjection(_dataLayer,
						getLowerRangeTime(), getUpperRangeTime(), _dataLayer);
				if (actualProj != null) {
					LayerProjection proj = priceModel.getLayerProjection(
							_dataLayer, actualProj, newLayer);
					if (proj != null) {
						_dataLayer = newLayer;
						long lower = proj.getLowerDisplayTime();
						long upper = proj.getUpperDisplayTime();
						if (upper - lower < 2) {
							lower -= 2;
							upper += 2;
						}

						if (scrolledToRight) {
							double margin = proj.getTimeLength()
									* RANGE_BLANK_PERCENT / 100;
							upper += margin;
						}
						newRange = new PlotRange(lower, upper);

						setXRange(newRange);
					}
				}
			}
		}

		if (_dataLayer == oldDataLayer) {
			setXRange(oldRange);
		}
	}

	protected void fixTimeRange() {
		_fixedTimeLower = false;
		_fixedTimeUpper = false;

		PlotRange range = getXRange();
		boolean doFix = true;

		if (isAutoDataLayer()) {
			doFix = false;

			int lowestLayer = getLowestLayer();
			int highestLayer = getHighestLayer();
			long lowest = (long) getDataTimeRange(highestLayer).lower;
			long highest = (long) getDataTimeRange(lowestLayer).upper;

			if (_dataLayer == lowestLayer && range.contains(highest)) {
				doFix = true;
			} else if (_dataLayer == highestLayer && range.contains(lowest)) {
				doFix = true;
			}
		}

		final PlotRange full = getDataTimeRange(_dataLayer);

		if (doFix) {
			boolean doFixLower = (!isAutoDataLayer() || _dataLayer == getHighestLayer())
					&& range.lower <= full.lower;

			boolean doFixUpper = _scrollingMode == ScrollingMode.NONE
					&& full.upper <= range.upper;

			if (doFixLower || doFixUpper) {
				final double len = range.getLength();
				int minPricesInScreen = getType().hasEquity() ? 1
						: MIN_PRICES_IN_SCREEN;
				double margin = Math.max(minPricesInScreen,
						range.getLength() * 0.1);

				double lower = range.lower;
				double upper = range.upper;

				if (doFixLower) {
					if (full.lower - margin > lower) {
						lower = full.lower - margin;
						_fixedTimeLower = true;
					}

					if (lower + minPricesInScreen > full.upper) {
						lower = full.upper - margin;
						_fixedTimeLower = true;
					}
				}

				if (doFixUpper) {
					if (full.upper + margin < upper) {
						upper = full.upper + margin;
						_fixedTimeUpper = true;
					}

					if (upper - minPricesInScreen < full.lower) {
						upper = full.lower + margin;
						_fixedTimeUpper = true;
					}
				}

				if (_fixedTimeLower && !_fixedTimeUpper) {
					upper = lower + len;
				}
				if (_fixedTimeUpper && !_fixedTimeLower) {
					lower = upper - len;
				}

				final PlotRange fix = new PlotRange(lower, upper);
				setXRange(fix);
			}
		}

		// do not accept a range with less than 10 prices
		if (range.getLength() < MIN_PRICES_IN_SCREEN) {
			boolean continueFix = true;
			// check if the lower layer will accept a switch, if not, then fix
			if (isAutoDataLayer() && _dataLayer > 0) {
				IPriceModel priceModel = getModel().getPriceModel();
				LayerProjection proj = priceModel.getLayerProjection(
						_dataLayer, (long) range.lower, (long) range.upper,
						_dataLayer - 1);
				if (proj == null || proj.getTimeLength() < 5) {
					// out.println("do fix, lower layer does not have enough prices");
				} else {
					continueFix = false;
					// out.println("ignore fix, lower layer does not have enough prices");
				}
			}

			// do the fix
			if (continueFix) {
				// out.println("do fix");
				if (_lastXRange == null
						|| _lastXRange.getLength() < MIN_PRICES_IN_SCREEN) {
					int half = MIN_PRICES_IN_SCREEN / 2;
					setXRange(new PlotRange(range.getMiddle() - half,
							range.getMiddle() + half));
				} else {
					// last range was better
					setXRange(_lastXRange);
				}
			}
		}

		_lastXRange = getXRange();
	}

	public void reloadDefaultProfile() {
		_mainSettings.updateFromProfile(_mainSettingsProfiledObject
				.getDefault());
		_timeOfTheDaySettings.updateFromProfile(_timeOfTheDayProfiledObject
				.getDefault());
		_custom.updateFromProfile(_mainSettingsProfiledObject.getDefault());
		if (_priceLayer != null) {
			_priceLayer.reloadDefaultProfile();
		}
		if (_indicatorLayer != null) {
			_indicatorLayer.reloadDefaultProfile();
		}
	}

	public void setView(AbstractChartView view) {
		_view = view;
	}

	public AbstractChartView getView() {
		return _view;
	}

}
