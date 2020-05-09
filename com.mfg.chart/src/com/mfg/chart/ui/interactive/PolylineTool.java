package com.mfg.chart.ui.interactive;

import static java.lang.System.out;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.xml.bind.JAXBException;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.mfg.opengl.BitmapData;
import org.mfg.opengl.IGLConstants;
import org.mfg.opengl.chart.PlotRange;
import org.mfg.opengl.chart.Settings;
import org.mfg.opengl.chart.SnappingMode;
import org.mfg.opengl.chart.interactive.ChartMouseEvent;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.PriceChartCanvas_OpenGL;
import com.mfg.chart.commands.DiscardLineFirstAnchorHandler;
import com.mfg.chart.commands.PolylineDrawPathHandler;
import com.mfg.chart.commands.PolylineGeneralSettignsHandler;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.IndicatorLayer.PivotReference;
import com.mfg.chart.layers.PriceLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.model.IRealTimeZZModel;
import com.mfg.chart.model.PriceModel_MDB;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.profiles.ProfileSet;
import com.mfg.chart.ui.MouseCursor;
import com.mfg.inputdb.prices.mdb.PriceMDB;
import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.utils.ImageUtils;
import com.mfg.utils.Utils;
import com.mfg.widget.arc.math.geom.PolyEvaluator;
import com.mfg.widget.arc.strategy.IFreehandIndicator;

public class PolylineTool extends InteractiveTool implements IAnchorTool {
	public static final String KEY_USE_PIVOTS_FOR_ANCHOR_POINTS = "usePivotsForAnchorPoints";

	private static final String KEY_PLOTING_NUMBER_OF_POINTS = "plotingNumberOfPoints";

	private static final String PREF_POLYLINE_TOOL_GENERAL_SETTINGS = "PolylineTool.generalSettings";

	private static final String PROFILE_KEY_SET = "PolylineTool";

	public static final String CONTEXT_ID = "com.mfg.chart.contexts.chartView.polylinesTool";

	public static class Line extends Polyline {
		private static PriceMDBSession getSession(Chart chart) {
			PriceModel_MDB priceModel = (PriceModel_MDB) chart.getModel()
					.getPriceModel();
			int dataLayer = chart.getDataLayer();
			PriceMDB mdb = priceModel.getMDB(dataLayer);
			return mdb.getSession();
		}

		public void setAnchor1(Chart chart, PivotReference anchor) {
			if (chart == null) {
				super.setAnchor1(null, 0, null);
				return;
			}

			super.setAnchor1(getSession(chart), chart.getDataLayer(), anchor);
		}

		public void setAnchor2(Chart chart, PivotReference anchor) {
			if (chart == null) {
				super.setAnchor2(null, 0, 0, 0, 0, null);
				return;
			}

			IChartModel model = chart.getModel();
			int dataLayer = chart.getDataLayer();
			PivotReference anchor1 = getAnchor1();
			if (anchor1 == null) {
				super.setAnchor2(getSession(chart), chart.getDataLayer(), 0, 0,
						0, anchor);
			} else {
				int level = anchor1.getLevel();
				IRealTimeZZModel rtModel = model.getScaledIndicatorModel()
						.getRealTimeZZModel(level);
				long rtPivotTime = rtModel.getTime2(dataLayer);
				long rtPivotPrice = (long) rtModel.getPrice2(dataLayer);
				long lastTime = model.getPriceModel().getLastTime(dataLayer);
				super.setAnchor2(getSession(chart), chart.getDataLayer(),
						rtPivotTime, rtPivotPrice, lastTime, anchor);
			}
		}

		public void setRightAnchor(Chart chart, double x) {
			super.setRightAnchor(getSession(chart), chart.getDataLayer(), x);
		}
	}

	public enum EquationType {
		AVG("Linear Regression"), POLY_2("Polyline 2�"), POLY_3("Polyline 3�"), POLY_4(
				"Polyline 4�");

		private String _name;

		private EquationType(String name) {
			_name = name;
		}

		@Override
		public String toString() {
			return _name;
		}
	}

	class TrackPoint {
		double top;
		Point2D center;
		double bottom;
	}

	private final Map<EquationType, List<TrackPoint>> _trackLines;
	private boolean _dragging;
	private boolean _pointingLeft;
	private Point2D.Double _pointingPosition;
	private final List<Line> _lines;
	private final Line _paintingLine;
	private boolean _drawPath;

	private SnappingMode _lastSnappingMode;

	private boolean _usePivotsForAnchorPoints;

	private static int _plotingNumberOfPoints;

	public static final float[][] DEFAULT_COLORS = { IGLConstants.COLOR_CYAN,
			IGLConstants.COLOR_YELLOW, IGLConstants.COLOR_GREEN,
			IGLConstants.COLOR_BLUE };

	public PolylineTool(Chart chart) {
		super("Polyline", chart, BITMAP_ANCHOR_LINES_TOOL_ICON);
		setTooltip("Polylines Tool (?): paint Polylines.");
		_lines = new ArrayList<>();
		_trackLines = new HashMap<>();
		_drawPath = false;

		resetTrackLines();

		Profile defProfile = getProfilesManager()
				.getDefault(getProfileKeySet());
		_paintingLine = new Line();
		_paintingLine.updateFromProfile(defProfile);
		_usePivotsForAnchorPoints = defProfile.getBoolean(
				KEY_USE_PIVOTS_FOR_ANCHOR_POINTS, true);

		readGlobalPrefs();
	}

	@Override
	protected List<Profile> createProfilePresets() {
		List<Profile> plist = new ArrayList<>();
		for (int i = 1; i <= 7; i++) {
			String name = "Preset " + i;
			Profile p = new Profile(name);

			// line settings
			Line l = new Line();
			l.types.clear();
			if (i <= 4) {
				int j = i - 1;
				EquationType t = EquationType.values()[j];
				l.types.add(t);
			} else {
				for (int j = 0; j < i - 3; j++) {
					EquationType t = EquationType.values()[j];
					l.types.add(t);
				}
			}
			l.fillProfile(p);

			// other settings
			p.putBoolean(KEY_USE_PIVOTS_FOR_ANCHOR_POINTS, true);

			plist.add(p);
		}

		return plist;
	}

	@Override
	protected void migrateProfile(Profile profile) {
		if (!profile.containsKey(KEY_USE_PIVOTS_FOR_ANCHOR_POINTS)) {
			profile.putBoolean(KEY_USE_PIVOTS_FOR_ANCHOR_POINTS, true);
		}

		// realtime property is not used anymore.
		profile.removeKey("realtime");

		for (EquationType type : EquationType.values()) {
			String tname = type.name();

			String key = "HAS_TYPE_" + tname;
			if (!profile.containsKey(key)) {
				profile.putBoolean(key, false);
			}

			String colorKey = "COLOR_" + tname;
			if (!profile.containsKey(colorKey)) {
				profile.putFloatArray(colorKey, DEFAULT_COLORS[type.ordinal()]);
			}

			String widthKey = "LINE_WIDTH_" + tname;
			if (!profile.containsKey(widthKey)) {
				profile.putInt(widthKey, 1);
			}

			String lineTypeKey = "LINE_TYPE_" + tname;
			if (!profile.containsKey(lineTypeKey)) {
				profile.putInt(lineTypeKey, 0);
			}
		}

		super.migrateProfile(profile);
	}

	public boolean isDrawPath() {
		return _drawPath;
	}

	public boolean isUsePivotsForAnchorPoints() {
		return _usePivotsForAnchorPoints;
	}

	public void setUsePivotsForAnchorPoints(boolean usePivotsForAnchorPoints) {
		_usePivotsForAnchorPoints = usePivotsForAnchorPoints;
	}

	public void setDrawPath(boolean drawPath) {
		_drawPath = drawPath;
	}

	public void setCombination(int combination) {
		ProfileSet list = getProfilesManager()
				.getProfileSet(getProfileKeySet());
		Profile p = list.findProfile("Preset " + combination);
		out.println("Set profile " + p);
		if (p != null) {
			_paintingLine.updateFromProfile(p);
			setProfile(p);
		}
	}

	public static int getPlotingNumberOfPoints() {
		return _plotingNumberOfPoints;
	}

	public Line getDefaultLine() {
		return _paintingLine;
	}

	public static void setPlotingNumberOfPoints(int plotingNumberOfPoints) {
		_plotingNumberOfPoints = plotingNumberOfPoints;
	}

	@Override
	public String getContextId() {
		return CONTEXT_ID;
	}

	@Override
	public boolean mouseDragged(ChartMouseEvent e) {
		_dragging = true;
		return false;
	}

	static class FindResult {
		public PivotReference anchor;
		public boolean founded = false;
		public boolean firstAnchor;
		public Line line;
	}

	@Override
	public boolean mouseReleased(ChartMouseEvent e) {
		if (_dragging) {
			return false;
		}

		updatePointingState();

		if (e.getButton() == ChartMouseEvent.RIGHT_BUTTON) {
			if (_paintingLine.getAnchor1() == null) {
				if (_paintingLine.getAnchor2() != null) {
					FindResult result = findLines(_paintingLine.getAnchor2());
					Utils.debug_id(534355,
							"Find line at " + _paintingLine.getAnchor2() + " "
									+ result.founded);
					if (result.founded && result.firstAnchor
							|| !result.line.realtime) {
						Line toDel = editLines(result.line);
						if (toDel != null) {
							_lines.remove(toDel);
						}
					}
				}
			} else {
				discardFirstAnchor();
			}
		} else {
			placeFirstAnchor();
		}

		updatePointingState();

		repaint();

		return false;
	}

	private void placeFirstAnchor() {
		if (_paintingLine.getAnchor1() == null) {

			// check for the real-time mode
			PriceChartCanvas_OpenGL canvas = (PriceChartCanvas_OpenGL) getChart()
					.getCanvas();
			boolean shift = canvas.getConnection().isShiftPressed();
			if (shift) {
				PivotReference anchor2;
				// show popup to select the real time mode
				PolylineRealtimeModeDialog dlg = new PolylineRealtimeModeDialog(
						Display.getDefault().getActiveShell());
				int result = dlg.open();
				if (result == 0) {
					// the user canceled the line
					anchor2 = null;
				} else {
					// anchor2 is always updated with the pointing anchor,
					// so we use that to place the first anchor
					anchor2 = _paintingLine.getAnchor2();

					// in case to follow the rt-pivot:
					if (result == PolylineRealtimeModeDialog.RT_PIVOT) {
						// find all matching pivots
						List<PivotReference> list = getChart()
								.getIndicatorLayer().findMatchingPivots(
										anchor2.getTime(), anchor2.getPrice());

						// if there are more than 1 matching pivot, select one
						// of them
						if (list.size() > 1) {
							PivotReference pivot = selectPivot(list);
							// if the user cancel to select a pivot, then the
							// line is not added (anchor2 will get a null
							// value).
							anchor2 = pivot;
						}
					}
				}
				_paintingLine.realtimeFollowPivot = true;

				if (anchor2 != null) {
					_paintingLine.setAnchor1(getChart(), anchor2);
					Line line = addNewLine();
					line.realtime = true;
					line.realtimeFollowPivot = result == PolylineRealtimeModeDialog.RT_PIVOT;
				}
			} else {
				// anchor2 is always updated with the pointing anchor,
				// so we use that to place the first anchor
				_paintingLine.realtime = false;
				_paintingLine.realtimeFollowPivot = false;
				_paintingLine
						.setAnchor1(getChart(), _paintingLine.getAnchor2());
			}

		} else {
			addNewLine();
		}
	}

	private Line addNewLine() {
		Line line = new Line();
		line.updateFromLine(_paintingLine);
		line.setAnchor1(getChart(), _paintingLine.getAnchor1());
		line.setAnchor2(getChart(), _paintingLine.getAnchor2());
		_lines.add(line);
		_paintingLine.setAnchor1(null, null);
		_paintingLine.setAnchor2(null, null);
		resetTrackLines();

		return line;
	}

	private static PivotReference selectPivot(List<PivotReference> list) {
		ListDialog dlg = new ListDialog(Display.getDefault().getActiveShell());
		dlg.setContentProvider(new ArrayContentProvider());
		dlg.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return "Scale "
						+ Integer.toString(((PivotReference) element)
								.getLevel());
			}
		});
		dlg.setTitle("New Polyline");
		dlg.setMessage("Select the anchor level");
		dlg.setInput(list);
		if (dlg.open() == Window.OK) {
			return (PivotReference) dlg.getResult()[0];
		}
		return null;
	}

	void resetTrackLines() {
		for (EquationType t : EquationType.values()) {
			_trackLines.put(t, new ArrayList<TrackPoint>());
		}
	}

	@Override
	public void selected() {
		super.selected();
		// save the snapping mode
		Settings settings = getChart().glChart.getSettings();
		_lastSnappingMode = settings.getSnappingMode();
		// set snapping mode to prices
		settings.setSnappingMode(SnappingMode.SNAP_XY);
	}

	@Override
	public void unselected() {
		_paintingLine.setAnchor2(null, null);
		_paintingLine.setAnchor1(null, null);

		// recover the snapping mode
		Settings settings = getChart().glChart.getSettings();
		if (settings.getSnappingMode() == SnappingMode.SNAP_XY) {
			settings.setSnappingMode(_lastSnappingMode);
		}
		super.unselected();
	}

	private FindResult findLines(PivotReference anchor) {
		FindResult result = new FindResult();
		for (Line lines : _lines) {
			boolean match1 = matchAnchor(lines.getAnchor1(), anchor);
			boolean match2 = matchAnchor(lines.getAnchor2(), anchor);
			boolean match = match1 || match2;
			boolean visible = isVisible(lines);
			if (visible && match) {
				if (result.line == null
						|| lines.getMaxLevel() > result.line.getMaxLevel()) {
					result.line = lines;
					result.anchor = match1 ? lines.getAnchor1() : lines
							.getAnchor1();
					result.firstAnchor = match1;
					result.founded = true;
				}
			}
		}
		return result;
	}

	private boolean isVisible(Line line) {
		boolean visible1 = isVisible(line.getAnchor1().getLevel());
		boolean visible2 = isVisible(line.getAnchor2().getLevel());
		return visible1 && visible2;
	}

	private boolean isVisible(int level) {
		if (_usePivotsForAnchorPoints) {
			IndicatorLayer ind = getChart().getIndicatorLayer();
			for (ScaleLayer scale : ind.getScales()) {
				if (scale.getZzLayer().isVisible() && scale.getLevel() <= level) {
					return true;
				}
			}
		} else {
			// in case of prices look for the data layer.
			return getChart().getDataLayer() == level;
		}
		return false;
	}

	private static boolean matchAnchor(PivotReference setAnchor,
			PivotReference pointingAnchor) {
		return setAnchor.samePosition(pointingAnchor);
	}

	private Line editLines(Line line) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		getChart().openSettingsWindow(shell, this, line);
		// reset values of real-time
		line.setAnchor2(getChart(), line.getAnchor2());
		return null;
	}

	public static void writeGlobalPrefs() {
		Profile p = new Profile();
		p.putInt(KEY_PLOTING_NUMBER_OF_POINTS, _plotingNumberOfPoints);
		try {
			getPrefs().putValue(PREF_POLYLINE_TOOL_GENERAL_SETTINGS, p.toXML());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private static void readGlobalPrefs() {
		{
			String xml = getPrefs().getString(
					PREF_POLYLINE_TOOL_GENERAL_SETTINGS);

			try {
				Profile p = xml == null || xml.trim().length() == 0 ? new Profile()
						: Profile.fromXML(xml);
				_plotingNumberOfPoints = p.getInt(KEY_PLOTING_NUMBER_OF_POINTS,
						300);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}

	private static IPreferenceStore getPrefs() {
		return ChartPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public boolean mouseMoved(ChartMouseEvent e) {
		_dragging = false;
		_pointingPosition = getChart().glChart.getCrosshairInPlot();

		updatePointingState();

		repaint();

		return false;
	}

	@Override
	public boolean discardFirstAnchor() {
		if (_paintingLine.getAnchor1() == null) {
			return true;
		}
		_paintingLine.setAnchor1(null, null);
		_paintingLine.setAnchor2(null, null);
		resetTrackLines();
		repaint();
		return false;
	}

	private void updatePointingState() {
		Double pos = getChart().glChart.getCrosshairInPlot();
		_pointingPosition = pos;

		if (isAnchor1Hidden()) {
			_paintingLine.setAnchor2(null, null);
			_pointingLeft = false;
		} else {
			long x = (long) pos.getX();
			double y = pos.getY();

			_pointingLeft = _paintingLine.getAnchor1() != null
					&& x < _paintingLine.getAnchor1().getTime();

			if (_pointingLeft) {
				_paintingLine.setAnchor2(null, null);
			} else {
				IndicatorLayer indLayer = getChart().getIndicatorLayer();
				PivotReference pivot;
				if (_usePivotsForAnchorPoints) {
					pivot = indLayer.findVisiblePivot(x, (long) y);
				} else {
					PriceLayer priceLayer = getChart().getPriceLayer();
					Point2D.Double p = priceLayer.findPoint(x, (long) y);
					if (p == null) {
						pivot = null;
					} else {
						pivot = PivotReference.fromPoint(p, getChart()
								.getDataLayer());
					}
				}
				_paintingLine.setAnchor2(getChart(), pivot);
			}
		}
	}

	@Override
	public void paintOnPlotMatrix(GL2 gl, int w, int h) {

		// recompute real time values
		Chart chart = getChart();
		if (_paintingLine.realtime) {
			_paintingLine.setAnchor2(chart, _paintingLine.getAnchor2());
		}

		for (Line line : _lines) {
			if (line.realtime) {
				line.setAnchor2(chart, line.getAnchor2());
			}
		}

		paintEditAnchors(gl, w, h);
		if (_paintingLine.getAnchor1() != null
				&& _paintingLine.getAnchor2() != null) {
			for (EquationType type : _paintingLine.types) {
				paintEcuation(chart, _paintingLine, type, gl);

				// paint track
				if (_drawPath) {
					if (_pointingPosition.getX() >= _paintingLine.getAnchor1()
							.getTime()) {
						List<TrackPoint> track = new ArrayList<>(
								_trackLines.get(type));
						if (track.isEmpty()) {
							TrackPoint p = new TrackPoint();
							p.center = (Point2D) _pointingPosition.clone();
							p.top = p.bottom = p.center.getY();
							track.add(p);
							_trackLines.put(type, track);
						} else {
							List<TrackPoint> track2 = new ArrayList<>();
							for (TrackPoint p : track) {
								if (p.center.getX() < _pointingPosition.getX()) {
									track2.add(p);
								}
							}
							track = track2;
							_trackLines.put(type, track);

							double x2 = _pointingPosition.getX();
							double lastX = track.get(track.size() - 1).center
									.getX();
							if (x2 > lastX) {
								// double x1 = track.get(0).center.getX();
								_paintingLine.setRightAnchor(chart, x2);
								IFreehandIndicator ind = _paintingLine
										.getIndicator(type);
								double y = PolyEvaluator.evaluate(
										ind.getCenterLineCoefficients(), x2);
								TrackPoint p = new TrackPoint();
								p.center = new Double(x2, y);
								p.top = y + _paintingLine.getTopDistance(type);
								p.bottom = y
										- _paintingLine.getBottomDistance(type);
								track.add(p);
							}
						}
						paintTrack(track, _paintingLine.colorsMap.get(type),
								_paintingLine.widthMap.get(type).intValue(), gl);
					}
				}
				// -- end paint track
			}
		}

		for (Line line : _lines) {
			List<Integer> scales = Arrays.asList(Integer.valueOf(-1
					* line.getAnchor1().getLevel()));
			paintAnchor(chart, line.getAnchor1(), scales, COLOR_YELLOW, gl);

			scales = Arrays.asList(Integer
					.valueOf(line.getAnchor2().getLevel()));
			if (!line.realtime || line.realtime && line.realtimeFollowPivot) {
				paintAnchor(chart, line.getAnchor2(), scales, COLOR_YELLOW, gl);
			}

			if (_drawPath) {
				for (EquationType type : line.types) {
					List<TrackPoint> track = line.trackLinesMap.get(type);
					float[] color = line.colorsMap.get(type);
					int lineWidth = line.widthMap.get(type).intValue();
					paintTrack(track, color, lineWidth, gl);
				}
			}

			for (EquationType type : line.types) {
				paintEcuation(chart, line, type, gl);
			}
		}
	}

	private static void paintTrack(List<TrackPoint> track, float[] color,
			int lineWidth, GL2 gl) {

		for (double shiftY : new double[] { 0 /*
											 * , -indicator.getBottomDistance(),
											 * indicator.getTopDistance()
											 */}) {
			gl.glPushAttrib(GL2.GL_LINE_BIT);
			gl.glLineWidth(lineWidth);
			gl.glColor4fv(color, 0);
			gl.glBegin(GL.GL_LINE_STRIP);

			for (TrackPoint p : track) {
				double x = p.center.getX();
				double y = p.top;
				gl.glVertex2d(x, y + shiftY);
			}
			gl.glEnd();

			gl.glBegin(GL.GL_LINE_STRIP);

			for (TrackPoint p : track) {
				double x = p.center.getX();
				double y = p.center.getY();
				gl.glVertex2d(x, y + shiftY);
			}
			gl.glEnd();

			gl.glBegin(GL.GL_LINE_STRIP);

			for (TrackPoint p : track) {
				double x = p.center.getX();
				double y = p.bottom;
				gl.glVertex2d(x, y + shiftY);
			}
			gl.glEnd();

			gl.glPopAttrib();
		}

	}

	@Override
	public void deletePointedAnchor() {
		if (_paintingLine.getAnchor2() != null) {
			Line toDel = null;
			for (Line l : _lines) {
				if (l.getAnchor1().samePosition(_paintingLine.getAnchor2())
						|| l.getAnchor2().samePosition(
								_paintingLine.getAnchor2())) {
					toDel = l;
					break;
				}
			}
			if (toDel != null) {
				if (MessageDialog.openConfirm(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "Delete Line",
						"Do you want to delete the line?")) {
					_lines.remove(toDel);
				}
				repaint();
			}
		}
	}

	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param color
	 * @param gl
	 */
	public static void paintEcuation(Chart chart, Polyline line,
			EquationType type, GL2 gl) {
		double x1 = line.getAnchor1().getTime();
		double x2 = line.realtime ? line.getRealtimeX() : line.getAnchor2()
				.getTime();
		float[] color = line.colorsMap.get(type);
		int lineWidth = line.widthMap.get(type).intValue();
		int lineType = line.lineTypeMap.get(type).intValue();
		boolean mirror = line.mirrorMap.get(type).booleanValue();
		if (x2 > x1) {
			IFreehandIndicator freeIndicator = line.getIndicator(type);
			double[] params = freeIndicator.getCenterLineCoefficients();
			if (params != null) {
				if (lineType != 0) {
					gl.glEnable(GL2.GL_LINE_STIPPLE);
					gl.glLineStipple(lineType, STIPPLE_PATTERN);
				}
				double bottom = line.getBottomDistance(type);
				double top = line.getTopDistance(type);
				for (double shiftY : new double[] { 0, -bottom, top }) {
					gl.glPushAttrib(GL2.GL_LINE_BIT);
					gl.glLineWidth(lineWidth);

					// color
					float[] color2 = color;
					boolean rt = line.realtime || line.realtimeFollowPivot;
					if (rt) {
						boolean touch = shiftY == top && line.isTouchTop(type)
								|| shiftY == -bottom
								&& line.isTouchBottom(type);
						if (touch) {
							color2 = COLOR_RED;
						}
					}
					// --

					gl.glColor4fv(color2, 0);
					gl.glBegin(GL.GL_LINE_STRIP);

					int x4;
					if (mirror) {
						x4 = (int) x2;
					} else {
						x4 = (int) chart.getXRange().upper;
					}
					double len = x4 - x1;
					double step = len / _plotingNumberOfPoints;
					if (step < 1) {
						step = 1;
					}

					for (double offset = 0; offset < len + step; offset += step) {
						double x = x1 + offset;
						if (x > x4) {
							x = x4;
						}

						double y = PolyEvaluator.evaluate(params, x);
						gl.glVertex2d(x, y + shiftY);
					}
					gl.glEnd();

					if (mirror) {
						// mirror X
						gl.glBegin(GL.GL_LINE_STRIP);
						for (double offset = 0; offset < len + step; offset += step) {
							double x = x1 + offset;
							if (x > x4) {
								x = x4;
							}
							double y = PolyEvaluator.evaluate(params, x);
							gl.glVertex2d(x2 + len - offset, y + shiftY);
						}
						gl.glEnd();

						// extend
						double len2 = chart.getXRange().upper - x2 + len;
						double step2 = len2 / _plotingNumberOfPoints;
						if (step2 < 1) {
							step2 = 1;
						}
						gl.glBegin(GL.GL_LINE_STRIP);
						for (double offset = 0; offset < len2 + step2; offset += step2) {
							double x = x1 - offset;
							double y = PolyEvaluator.evaluate(params, x);
							gl.glVertex2d(x2 + len + offset, y + shiftY);
						}
						gl.glEnd();

						// mirror Y

						double centerY = PolyEvaluator.evaluate(params, x1
								+ len);

						gl.glBegin(GL.GL_LINE_STRIP);
						for (double offset = 0; offset < len; offset += step) {
							double x = x1 + offset;
							if (x > x4) {
								x = x4;
							}
							double y = centerY * 2
									- PolyEvaluator.evaluate(params, x);
							gl.glVertex2d(x2 + len - offset, y + shiftY);
						}
						gl.glEnd();

						// extend

						gl.glBegin(GL.GL_LINE_STRIP);
						for (double offset = 0; offset < len2; offset += step2) {
							double x = x1 - offset;
							double y = centerY * 2
									- PolyEvaluator.evaluate(params, x);
							gl.glVertex2d(x2 + len + offset, y + shiftY);
						}
						gl.glEnd();
					}

					gl.glPopAttrib();
				}

				// paint free

				if (lineType != 0) {
					gl.glDisable(GL2.GL_LINE_STIPPLE);
				}
			}
		}
	}

	private void paintEditAnchors(GL2 gl, int w, int h) {
		if (isAnchor1Hidden()) {
			paintAnchor1IsHidden(gl, w, h);
		} else {

			if (_pointingLeft) {
				paintPointingLeft(gl, w, h);
			} else if (_paintingLine.getAnchor2() != null) {
				boolean firstAnchor = _paintingLine.getAnchor1() == null;
				paintPivotSelection(_paintingLine.getAnchor2(), firstAnchor,
						COLOR_CYAN, gl, w, h);
			}

			// paint first anchor
			if (_paintingLine.getAnchor1() != null
					&& isVisible(_paintingLine.getAnchor1().getLevel())) {
				paintPivotSelection(_paintingLine.getAnchor1(), true,
						COLOR_CYAN, gl, w, h);
			}
		}
	}

	private boolean isAnchor1Hidden() {
		return _paintingLine.getAnchor1() != null
				&& !isVisible(_paintingLine.getAnchor1().getLevel());
	}

	private void paintPivotSelection(PivotReference pivot, boolean firstAnchor,
			float[] color, GL2 gl, int w, int h) {
		long x = pivot.getTime();
		long y = pivot.getPrice();

		int len = pivot == _paintingLine.getAnchor2() ? 18 : 20;
		double xlen = getChart().getXRange().plotWidth(len, w);
		double ylen = getChart().getYRange().plotWidth(len, h);

		gl.glColor4fv(color, 0);
		gl.glBegin(GL.GL_LINE_STRIP);

		gl.glVertex2d(x - xlen, y - ylen);
		gl.glVertex2d(x + xlen, y - ylen);
		gl.glVertex2d(x + xlen, y + ylen);
		gl.glVertex2d(x - xlen, y + ylen);
		gl.glVertex2d(x - xlen, y - ylen);
		gl.glEnd();

		int scaleInfo = (firstAnchor ? -1 : 1) * pivot.getLevel();
		List<Integer> scales = Arrays.asList(Integer.valueOf(scaleInfo));
		paintAnchor(getChart(), pivot, scales, color, gl);
	}

	/**
	 * 
	 * @param pivot
	 * @param gl
	 * @param w
	 * @param h
	 */
	private void paintPointingLeft(GL2 gl, int w, int h) {
		if (_pointingPosition == null) {
			return;
		}

		long x = (long) _pointingPosition.getX();
		long y = (long) _pointingPosition.getY();

		double xlen = getChart().getXRange().plotWidth(20, w);
		double ylen = getChart().getYRange().plotWidth(5, h);

		gl.glColor4fv(COLOR_RED, 0);

		gl.glRasterPos2i((int) (x + xlen), (int) y);
		BitmapData anchorBmp = BITMAP_ANCHOR_LINES_TOOL_ICON;
		gl.glBitmap(anchorBmp.width, anchorBmp.height, 0, 0, 0, 0,
				anchorBmp.bitmap, 0);

		String str = "ANCHOR 2 < ANCHOR 1";
		int x2 = (int) (x - xlen * 8);

		gl.glRasterPos2i(x2, (int) (y + ylen));
		_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);
	}

	private void paintAnchor1IsHidden(GL2 gl, int w, int h) {
		if (_pointingPosition == null) {
			return;
		}

		long x = (long) _pointingPosition.getX();
		long y = (long) _pointingPosition.getY();

		double xlen = getChart().getXRange().plotWidth(20, w);
		double ylen = getChart().getYRange().plotWidth(5, h);

		gl.glColor4fv(COLOR_RED, 0);

		BitmapData anchorBmp = BITMAP_ANCHOR_LINES_TOOL_ICON;
		gl.glRasterPos2i((int) (x + xlen), (int) y);
		gl.glBitmap(anchorBmp.width, anchorBmp.height, 0, 0, 0, 0,
				anchorBmp.bitmap, 0);

		String str = "ANCHOR 1 IS HIDDEN IN SCALE "
				+ _paintingLine.getAnchor1().getLevel();
		int x2 = (int) (x - xlen * 10);

		gl.glRasterPos2i(x2, (int) (y + ylen));
		_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);
	}

	/**
	 * 
	 * @param pivot
	 * @param overlapingScales
	 * @param color
	 * @param gl
	 */
	public static void paintAnchor(Chart chart, PivotReference pivot,
			List<Integer> overlapingScales, float[] color, GL2 gl) {

		final long x = pivot.getTime();
		final long y = pivot.getPrice();

		PlotRange xr = chart.getXRange();
		PlotRange yr = chart.getYRange();

		// gl.glPushAttrib(GL2.GL_LINE_BIT);
		// gl.glLineWidth(lineWidth);
		gl.glColor4fv(color, 0);
		// gl.glBegin(GL.GL_LINE_STRIP);
		//
		// gl.glVertex2d(x, yr.upper);
		// gl.glVertex2d(x, yr.lower);
		// gl.glEnd();
		//
		// gl.glPopAttrib();

		gl.glRasterPos2i((int) x, (int) y);
		BitmapData anchorBmp = BITMAP_ANCHOR_LINES_TOOL_ICON;
		gl.glBitmap(anchorBmp.width, anchorBmp.height, 0, 0, 0, 0,
				anchorBmp.bitmap, 0);

		double xlen = xr.plotWidth(23, chart.glChart.plot.screenWidth);
		double ylen = yr.plotWidth(15, chart.glChart.plot.screenHeight);

		double bmpLen = yr.plotWidth(anchorBmp.height,
				chart.glChart.plot.screenHeight);

		if (overlapingScales != null) {
			int y2 = (int) (y - (overlapingScales.size() * ylen) / 2 + bmpLen / 2);
			for (int scaleInfo : overlapingScales) {
				int x2 = (int) (x + xlen);
				String str = (scaleInfo < 0 ? "> " : "< ")
						+ Integer.toString(Math.abs(scaleInfo));
				gl.glRasterPos2i(x2, y2);
				_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);
				y2 += ylen;
			}
		}
	}

	@Override
	public MouseCursor getMouseCursor() {
		return _dragging || _paintingLine.getAnchor2() == null ? super
				.getMouseCursor() : MouseCursor.DEFAULT;
	}

	@Override
	public String getProfileKeySet() {
		return PROFILE_KEY_SET;
	}

	@Override
	public String getKeywords() {
		return " regression line 2 3 4 line width type color use pivot anchor point real time realtime number";
	}

	@Override
	public void fillMenu(IMenuManager menu) {
		MenuManager menu2 = new MenuManager("Polyline",
				ImageUtils.getBundledImageDescriptor(ChartPlugin.PLUGIN_ID,
						"icons/PL_16.png"), "polyline");
		menu.add(menu2);
		menu2.add(new ToolAction(PolylineDrawPathHandler.CMD_ID) {
			@Override
			public void run() {
				PolylineDrawPathHandler.execute(getChart());
			}
		});
		menu2.add(new ToolAction(DiscardLineFirstAnchorHandler.CMD_ID) {
			@Override
			public void run() {
				DiscardLineFirstAnchorHandler.execute(getChart());
			}
		});
		menu2.add(new ToolAction(PolylineGeneralSettignsHandler.CMD_ID) {
			@Override
			public void run() {
				PolylineGeneralSettignsHandler.execute(getChart());
			}
		});
	}
}
