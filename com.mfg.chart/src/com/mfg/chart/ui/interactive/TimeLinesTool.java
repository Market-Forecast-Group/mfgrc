package com.mfg.chart.ui.interactive;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.PlotRange;
import org.mfg.opengl.chart.SnappingMode;
import org.mfg.opengl.chart.interactive.ChartMouseEvent;
import org.mfg.opengl.chart.interactive.ChartPoint;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.chart.ChartPlugin;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.commands.DiscardLineFirstAnchorHandler;
import com.mfg.chart.commands.TimeLineGeneralSettingsHandler;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.IndicatorLayer.PivotReference;
import com.mfg.chart.layers.PriceLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.MouseCursor;
import com.mfg.chart.ui.interactive.TimeLinesTool.TimeLines.RatioInfo;
import com.mfg.utils.ImageUtils;

public class TimeLinesTool extends InteractiveTool implements IAnchorTool {

	public static final String PROFILE_SET_KEY = "TimeLinesTool";
	public static final String CONTEXT_ID = "com.mfg.chart.contexts.chartView.timeLinesTool";
	private static final int RATIOS_COUNT = 10;
	private static final String KEY_USE_PIVOTS_FOR_ANCHOR_POINTS = "usePivotsForAnchorPoints";
	private static final String KEY_ANCHOR_LINE_TYPE = "anchorLineType";
	private static final String KEY_ANCHOR_LINE_WIDTH = "anchorLineWidth";
	private static final String KEY_ANCHOR_COLOR = "anchorColor";
	public static final float[] DEFAULT_ANCHOR_COLOR = COLOR_WHITE;
	public static final float DEFAULT_RATIO = 1;

	private PivotReference _pointingAnchor;
	private PivotReference _anchor1;
	private final List<TimeLines> _linesList;
	private final BitmapData _anchorBmp;
	private boolean _dragging;
	private boolean _pointingLeft;
	private ChartPoint _pointingPosition;
	private Settings _defaultSettings;

	public static class TimeLines implements Comparable<TimeLines>, Cloneable {

		public static class RatioInfo {
			private float _ratio;
			private float[] _color;
			private boolean _selected;
			private int _lineWidth;
			private int _lineType;

			public RatioInfo(float ratio, float[] color, boolean selected,
					int lineWidth, int lineType) {
				super();
				_ratio = ratio;
				_color = color;
				_selected = selected;
				_lineWidth = lineWidth;
				_lineType = lineType;
			}

			public int getLineType() {
				return _lineType;
			}

			public void setLineType(int lineType) {
				_lineType = lineType;
			}

			public int getLineWidth() {
				return _lineWidth;
			}

			public void setLineWidth(int lineWidth) {
				_lineWidth = lineWidth;
			}

			public boolean isSelected() {
				return _selected;
			}

			public void setSelected(boolean selected) {
				_selected = selected;
			}

			public float getRatio() {
				return _ratio;
			}

			public void setRatio(float ratio) {
				_ratio = ratio;
			}

			public float[] getColor() {
				return _color;
			}

			public void setColor(float[] color) {
				_color = color;
			}

			@Override
			public RatioInfo clone() {
				return new RatioInfo(_ratio, _color, _selected, _lineWidth,
						_lineType);
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + Arrays.hashCode(_color);
				result = prime * result + Float.floatToIntBits(_ratio);
				result = prime * result + (_selected ? 1231 : 1237);
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				RatioInfo other = (RatioInfo) obj;
				if (!Arrays.equals(_color, other._color))
					return false;
				if (Float.floatToIntBits(_ratio) != Float
						.floatToIntBits(other._ratio))
					return false;
				if (_selected != other._selected)
					return false;
				return true;
			}
		}

		final PivotReference _anchor1;
		final PivotReference _anchor2;
		private Settings _settings;

		public TimeLines(PivotReference anchor1, PivotReference anchor2,
				Settings settings) {
			_anchor1 = anchor1;
			_anchor2 = anchor2;
			_settings = settings;
		}

		public PivotReference getAnchor1() {
			return _anchor1;
		}

		public PivotReference getAnchor2() {
			return _anchor2;
		}

		public int getMaxLevel() {
			return Math.max(_anchor1.getLevel(), _anchor2.getLevel());
		}

		@Override
		public int compareTo(TimeLines o) {
			return -Integer.valueOf(getMaxLevel()).compareTo(
					Integer.valueOf(o.getMaxLevel()));
		}

		public String key() {
			return _anchor1.key() + "-" + _anchor2.key();
		}

		public long getRatioLineTime(RatioInfo info) {
			long len = _anchor2.getTime() - _anchor1.getTime();
			return (long) (_anchor2.getTime() + len * info.getRatio());
		}

		public Settings getSettings() {
			return _settings;
		}

		public void setSettings(Settings settings) {
			_settings = settings;
		}

	}

	public TimeLinesTool(Chart chart) {
		super("Time Lines", chart, BITMAP_ANCHOR_LINES_TOOL_ICON);
		setTooltip("Anchor Lines Tool (E): paint Time Lines.");
		_linesList = new ArrayList<>();
		_anchorBmp = BITMAP_ANCHOR_LINES_TOOL_ICON;

		// get the default settings
		_defaultSettings = new Settings();
		Profile p = getProfilesManager().getDefault(getProfileKeySet());
		_defaultSettings.updateFromProfile(p);
	}

	@Override
	protected List<Profile> createProfilePresets() {
		Settings s = new Settings();
		RatioInfo[] ratios = s.getRatios();

		ratios[0].setColor(COLOR_BLUE);
		ratios[0].setRatio(1);
		ratios[0].setSelected(true);

		ratios[1].setColor(COLOR_GREEN);
		ratios[1].setRatio(0.618f);
		ratios[1].setSelected(true);

		ratios[2].setColor(COLOR_BLUE);
		ratios[2].setRatio(0.5f);
		ratios[2].setSelected(true);

		ratios[3].setColor(COLOR_BLUE);
		ratios[3].setRatio(2);
		ratios[3].setSelected(true);

		ratios[4].setColor(COLOR_GREEN);
		ratios[4].setRatio(1.618f);
		ratios[4].setSelected(true);

		ratios[5].setColor(COLOR_CYAN);
		ratios[5].setRatio(1.8f);
		ratios[5].setSelected(true);

		ratios[6].setColor(COLOR_CYAN);
		ratios[6].setRatio(0.559f);
		ratios[6].setSelected(true);

		Profile p = new Profile("Preset 1");
		s.fillProfile(p);

		return Arrays.asList(p);
	}

	@Override
	protected void migrateProfile(Profile profile) {
		if (!profile.containsKey(KEY_ANCHOR_LINE_TYPE)) {
			profile.putInt(KEY_ANCHOR_LINE_TYPE, 0);
		}

		if (!profile.containsKey(KEY_ANCHOR_COLOR)) {
			profile.putBoolean(KEY_USE_PIVOTS_FOR_ANCHOR_POINTS, true);
		}

		for (int i = 0; i < RATIOS_COUNT; i++) {
			String key = "ratios." + i + ".ratioLineType";
			if (!profile.containsKey(key)) {
				profile.putInt(key, 0);
			}
		}
	}

	@Override
	public void selected() {
		super.selected();
		getChart().glChart.getSettings().setSnappingMode(SnappingMode.SNAP_XY);
	}

	public Settings getDefaultSettings() {
		return _defaultSettings;
	}

	public void setDefaultSettings(Settings defaultSettings) {
		_defaultSettings = defaultSettings;
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
		public TimeLines lines;
		public PivotReference anchor;
		public boolean founded = false;
		public boolean firstAnchor;
	}

	private FindResult findLines(PivotReference anchor) {
		FindResult result = new FindResult();
		for (TimeLines lines : _linesList) {
			boolean match1 = matchAnchor(lines.getAnchor1(), anchor);
			boolean match2 = matchAnchor(lines.getAnchor2(), anchor);
			boolean match = match1 || match2;
			boolean visible = isVisible(lines);
			if (visible && match) {
				if (result.lines == null
						|| lines.getMaxLevel() > result.lines.getMaxLevel()) {
					result.lines = lines;
					result.anchor = match1 ? lines.getAnchor1() : lines
							.getAnchor2();
					result.firstAnchor = match1;
					result.founded = true;
				}
			}
		}
		return result;
	}

	private static boolean matchAnchor(PivotReference setAnchor,
			PivotReference pointingAnchor) {
		return setAnchor.samePosition(pointingAnchor);
	}

	@Override
	public boolean mouseReleased(ChartMouseEvent e) {
		if (_dragging) {
			return false;
		}

		updatePointingState(e.getPosition());

		if (e.getButton() == ChartMouseEvent.RIGHT_BUTTON) {
			if (_anchor1 == null) {
				if (_pointingAnchor != null
						&& !_pointingAnchor.samePosition(_anchor1)) {
					TimeLines toDel = null;

					FindResult result = findLines(_pointingAnchor);

					if (result.founded) {
						toDel = editLines(result.lines);
						if (toDel != null) {
							_linesList.remove(toDel);
							repaint();
						}
					}
				}
			} else {
				discardFirstAnchor();
			}
		} else {
			if (_pointingAnchor != null) {
				if (_anchor1 == null) {
					_anchor1 = _pointingAnchor;
				} else {
					addNewLine();
				}
			}
		}

		updatePointingState(e.getPosition());

		repaint();

		return false;
	}

	void addNewLine() {
		TimeLines lines = new TimeLines(_anchor1, _pointingAnchor,
				_defaultSettings.clone());
		_linesList.add(lines);
		Collections.sort(_linesList);
		_anchor1 = null;
	}

	private TimeLines editLines(TimeLines lines) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		getChart().openSettingsWindow(shell, this, lines);
		return null;
	}

	@Override
	public boolean mouseMoved(ChartMouseEvent e) {
		_dragging = false;
		_pointingPosition = e.getPosition();

		updatePointingState(e.getPosition());

		repaint();

		return false;
	}

	@Override
	public boolean discardFirstAnchor() {
		if (_anchor1 == null) {
			return true;
		}
		_anchor1 = null;
		repaint();
		return false;
	}

	@Override
	public void deletePointedAnchor() {
		if (_pointingAnchor != null) {
			FindResult result = findLines(_pointingAnchor);
			if (result.founded) {
				TimeLines lines = result.lines;
				if (MessageDialog.openConfirm(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "Delete",
						"Do you wants to delete this line ("
								+ lines.getAnchor1().getLevel() + ", "
								+ lines.getAnchor2().getLevel() + ")?")) {
					_linesList.remove(lines);
					repaint();
				}
			}
		}
	}

	private void updatePointingState(ChartPoint pos) {
		_pointingPosition = pos;

		if (isAnchor1Hidden()) {
			_pointingAnchor = null;
			_pointingLeft = false;
		} else {
			long x = (long) pos.getPlotX();
			double y = pos.getPlotY();

			_pointingLeft = _anchor1 != null && x < _anchor1.getTime();

			if (_pointingLeft) {
				_pointingAnchor = null;
			} else {
				IndicatorLayer indicator = getChart().getIndicatorLayer();
				PivotReference pivot;
				if (_defaultSettings.isUsePivotsForAnchorPoints()) {
					pivot = indicator.findVisiblePivot(x, (long) y);
				} else {
					PriceLayer priceLayer = getChart().getPriceLayer();
					Point2D p = priceLayer.findPoint(x, (long) y);
					if (p == null) {
						pivot = null;
					} else {
						pivot = PivotReference.fromPoint(p, getChart()
								.getDataLayer());
					}
				}

				if (pivot != null && _anchor1 != null
						&& pivot.samePosition(_anchor1)) {
					_pointingAnchor = null;
				} else {
					_pointingAnchor = pivot;
				}
			}
		}
	}

	@Override
	public void paintOnPlotMatrix(GL2 gl, int w, int h) {
		Map<String, List<Integer>> overlapingAnchorsMap = computeOverlapingAnchors();

		paintEditAnchors(overlapingAnchorsMap, gl, w, h);

		paintLines(overlapingAnchorsMap, gl);
	}

	private void paintEditAnchors(
			Map<String, List<Integer>> overlapingAnchorsMap, GL2 gl, int w,
			int h) {
		if (isAnchor1Hidden()) {
			paintAnchor1IsHidden(gl, w, h);
		} else {

			if (_pointingLeft) {
				paintPointingLeft(gl, w, h);
			} else if (_pointingAnchor != null) {
				FindResult result = findLines(_pointingAnchor);

				boolean firstAnchor = _anchor1 == null;

				if (result.founded) {
					Settings settings = result.lines.getSettings();
					paintPivotSelection(result.anchor, result.firstAnchor,
							overlapingAnchorsMap, settings.getAnchorColor(),
							settings.getAnchorLineWidth(),
							settings.getAnchorLineType(), gl, w, h);
				} else {
					paintPivotSelection(_pointingAnchor, firstAnchor,
							overlapingAnchorsMap,
							_defaultSettings.getAnchorColor(),
							_defaultSettings.getAnchorLineWidth(),
							_defaultSettings.getAnchorLineType(), gl, w, h);
				}
			}

			// paint first anchor
			if (_anchor1 != null && isVisible(_anchor1.getLevel())) {
				paintPivotSelection(_anchor1, true, overlapingAnchorsMap,
						_defaultSettings.getAnchorColor(),
						_defaultSettings.getAnchorLineWidth(),
						_defaultSettings.getAnchorLineType(), gl, w, h);
			}

			if (_anchor1 != null && _pointingAnchor != null) {
				TimeLines temp = new TimeLines(_anchor1, _pointingAnchor,
						_defaultSettings);
				paintRatioLines(temp, gl);
			}

		}
	}

	private boolean isAnchor1Hidden() {
		return _anchor1 != null && !isVisible(_anchor1.getLevel());
	}

	private void paintLines(Map<String, List<Integer>> overlapingAnchorsMap,
			GL2 gl) {
		PlotRange xr = getChart().getXRange();

		for (TimeLines line : _linesList) {
			if (isVisible(line)) {
				float[] color = line.getSettings().getAnchorColor();
				int anchorLineWidth = line.getSettings().getAnchorLineWidth();
				int anchorLineType = line.getSettings().getAnchorLineType();
				PivotReference anchor = line.getAnchor1();

				if (xr.contains(anchor.getTime())) {
					List<Integer> overlaping = overlapingAnchorsMap.get(anchor
							.key());
					paintAnchorLine(anchor, overlaping, color, anchorLineWidth,
							anchorLineType, gl);
				}

				anchor = line.getAnchor2();
				if (xr.contains(anchor.getTime())) {
					List<Integer> overlaping = overlapingAnchorsMap.get(anchor
							.key());
					paintAnchorLine(anchor, overlaping, color, anchorLineWidth,
							anchorLineType, gl);
				}

				paintRatioLines(line, gl);
			}
		}
	}

	private Map<String, List<Integer>> computeOverlapingAnchors() {
		Map<String, List<Integer>> overlapingAnchorsMap = new HashMap<>();

		for (TimeLines line : _linesList) {
			if (isVisible(line)) {

				updateOverlapingAnchorMap(overlapingAnchorsMap,
						line.getAnchor1(), true);

				updateOverlapingAnchorMap(overlapingAnchorsMap,
						line.getAnchor2(), false);
			}
		}
		return overlapingAnchorsMap;
	}

	private void updateOverlapingAnchorMap(
			Map<String, List<Integer>> countAnchors, PivotReference anchor,
			boolean firstAnchor) {
		if (!getChart().getXRange().contains(anchor.getTime())) {
			return;
		}

		String key;
		key = anchor.key();
		List<Integer> list = countAnchors.get(key);
		if (list == null) {
			list = new ArrayList<>();
			countAnchors.put(key, list);
		}
		list.add(Integer.valueOf((firstAnchor ? -1 : 1) * anchor.getLevel()));
	}

	private boolean isVisible(TimeLines line) {
		boolean visible1 = isVisible(line.getAnchor1().getLevel());
		boolean visible2 = isVisible(line.getAnchor2().getLevel());
		return visible1 && visible2;
	}

	private boolean isVisible(int level) {
		IndicatorLayer ind = getChart().getIndicatorLayer();
		for (ScaleLayer scale : ind.getScales()) {
			if (scale.getZzLayer().isVisible() && scale.getLevel() <= level) {
				return true;
			}
		}
		return false;
	}

	private void paintRatioLines(TimeLines lines, GL2 gl) {
		Chart chart = getChart();
		PlotRange yr = chart.getYRange();
		PlotRange xr = chart.getXRange();

		for (RatioInfo info : lines.getSettings().getRatios()) {
			if (info.isSelected()) {
				long x = lines.getRatioLineTime(info);
				long y = lines.getAnchor2().getPrice();

				if (info.getLineType() != 0) {
					gl.glEnable(GL2.GL_LINE_STIPPLE);
					gl.glLineStipple(info.getLineType(), STIPPLE_PATTERN);
				}

				gl.glPushAttrib(GL2.GL_LINE_BIT);
				gl.glLineWidth(info.getLineWidth());
				gl.glColor4fv(info.getColor(), 0);
				gl.glBegin(GL.GL_LINE_STRIP);

				gl.glVertex2d(x, yr.upper);
				gl.glVertex2d(x, yr.lower);

				gl.glEnd();
				gl.glPopAttrib();

				if (info.getLineType() != 0) {
					gl.glDisable(GL2.GL_LINE_STIPPLE);
				}

				int x2 = (int) (x + (xr.plotWidth(20,
						chart.glChart.plot.screenWidth)));
				String str = Float.toString(info.getRatio());

				gl.glRasterPos2i(x2, (int) y);
				_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);
			}
		}

	}

	private void paintPivotSelection(PivotReference pivot, boolean firstAnchor,
			Map<String, List<Integer>> overlapingAnchorsMap, float[] color,
			int anchorLineWidth, int anchorLineType, GL2 gl, int w, int h) {
		long x = pivot.getTime();
		long y = pivot.getPrice();

		int len = pivot == _pointingAnchor ? 18 : 20;
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

		List<Integer> scalesInMap = overlapingAnchorsMap.get(pivot.key());
		int scaleInfo = (firstAnchor ? -1 : 1) * pivot.getLevel();
		List<Integer> scales = scalesInMap == null ? Arrays.asList(Integer
				.valueOf(scaleInfo)) : scalesInMap;
		paintAnchorLine(pivot, scales, color, anchorLineWidth, anchorLineType,
				gl);
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

		long x = (long) _pointingPosition.getPlotX();
		long y = (long) _pointingPosition.getPlotY();

		double xlen = getChart().getXRange().plotWidth(20, w);
		double ylen = getChart().getYRange().plotWidth(5, h);

		gl.glColor4fv(COLOR_RED, 0);

		gl.glRasterPos2i((int) (x + xlen), (int) y);
		gl.glBitmap(_anchorBmp.width, _anchorBmp.height, 0, 0, 0, 0,
				_anchorBmp.bitmap, 0);

		String str = "ANCHOR 2 < ANCHOR 1";
		int x2 = (int) (x - xlen * 8);

		gl.glRasterPos2i(x2, (int) (y + ylen));
		_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);
	}

	private void paintAnchor1IsHidden(GL2 gl, int w, int h) {
		if (_pointingPosition == null) {
			return;
		}

		long x = (long) _pointingPosition.getPlotX();
		long y = (long) _pointingPosition.getPlotY();

		double xlen = getChart().getXRange().plotWidth(20, w);
		double ylen = getChart().getYRange().plotWidth(5, h);

		gl.glColor4fv(COLOR_RED, 0);

		gl.glRasterPos2i((int) (x + xlen), (int) y);
		gl.glBitmap(_anchorBmp.width, _anchorBmp.height, 0, 0, 0, 0,
				_anchorBmp.bitmap, 0);

		String str = "ANCHOR 1 IS HIDDEN IN SCALE " + _anchor1.getLevel();
		int x2 = (int) (x - xlen * 10);

		gl.glRasterPos2i(x2, (int) (y + ylen));
		_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);
	}

	private void paintAnchorLine(PivotReference pivot,
			List<Integer> overlapingScales, float[] color, int lineWidth,
			int lineType, GL2 gl) {

		final long x = pivot.getTime();
		final long y = pivot.getPrice();

		PlotRange xr = getChart().getXRange();
		PlotRange yr = getChart().getYRange();

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		if (lineType != 0) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(lineType, STIPPLE_PATTERN);
		}
		gl.glLineWidth(lineWidth);
		gl.glColor4fv(color, 0);
		gl.glBegin(GL.GL_LINE_STRIP);

		gl.glVertex2d(x, yr.upper);
		gl.glVertex2d(x, yr.lower);
		gl.glEnd();

		gl.glPopAttrib();

		if (lineType != 0) {
			gl.glDisable(GL2.GL_LINE_STIPPLE);
		}

		gl.glRasterPos2i((int) x, (int) y);
		gl.glBitmap(_anchorBmp.width, _anchorBmp.height, 0, 0, 0, 0,
				_anchorBmp.bitmap, 0);

		double xlen = xr.plotWidth(23, _glChart.plot.screenWidth);
		double ylen = yr.plotWidth(15, _glChart.plot.screenHeight);

		double bmpLen = yr.plotWidth(_anchorBmp.height,
				_glChart.plot.screenHeight);

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

	@Override
	public MouseCursor getMouseCursor() {
		return _dragging || _pointingAnchor == null ? super.getMouseCursor()
				: MouseCursor.DEFAULT;
	}

	public static class Settings implements Cloneable {

		private float[] _anchorColor;
		private int _anchorLineType;
		private RatioInfo[] _ratios;
		private int _anchorLineWidth;
		private boolean _usePivotsForAnchorPoints;

		public Settings() {
			_ratios = new RatioInfo[RATIOS_COUNT];

			for (int i = 0; i < _ratios.length; i++) {
				_ratios[i] = new RatioInfo(DEFAULT_RATIO, DEFAULT_ANCHOR_COLOR,
						i == 0, 1, 0);
			}

			_anchorColor = COLOR_WHITE;
			_anchorLineWidth = 1;
			_anchorLineType = 0;
			_usePivotsForAnchorPoints = true;
		}

		public boolean isUsePivotsForAnchorPoints() {
			return _usePivotsForAnchorPoints;
		}

		public void setUsePivotsForAnchorPoints(boolean usePivotsForAnchorPoints) {
			_usePivotsForAnchorPoints = usePivotsForAnchorPoints;
		}

		public int getAnchorLineType() {
			return _anchorLineType;
		}

		public void setAnchorLineType(int anchorLineType) {
			_anchorLineType = anchorLineType;
		}

		public int getAnchorLineWidth() {
			return _anchorLineWidth;
		}

		public void setAnchorLineWidth(int anchorLineWidth) {
			_anchorLineWidth = anchorLineWidth;
		}

		public float[] getAnchorColor() {
			return _anchorColor;
		}

		public void setAnchorColor(float[] anchorColor) {
			this._anchorColor = anchorColor;
		}

		public RatioInfo[] getRatios() {
			return _ratios;
		}

		public void setRatios(RatioInfo[] ratios) {
			_ratios = ratios;
		}

		@Override
		public Settings clone() {
			Settings s = new Settings();
			s._ratios = new RatioInfo[_ratios.length];
			for (int i = 0; i < _ratios.length; i++) {
				s._ratios[i] = _ratios[i].clone();
			}
			s._anchorColor = Arrays.copyOf(_anchorColor, _anchorColor.length);
			s._anchorLineWidth = _anchorLineWidth;
			s._anchorLineType = _anchorLineType;
			s._usePivotsForAnchorPoints = _usePivotsForAnchorPoints;
			return s;
		}

		public void updateFromProfile(Profile profile) {
			_anchorColor = profile.getFloatArray(KEY_ANCHOR_COLOR,
					DEFAULT_ANCHOR_COLOR);
			_anchorLineWidth = profile.getInt(KEY_ANCHOR_LINE_WIDTH, 1);
			_anchorLineType = profile.getInt(KEY_ANCHOR_LINE_TYPE, 0);
			_usePivotsForAnchorPoints = profile.getBoolean(
					KEY_USE_PIVOTS_FOR_ANCHOR_POINTS, true);

			_ratios = new RatioInfo[RATIOS_COUNT];
			for (int i = 0; i < _ratios.length; i++) {
				String key = "ratios." + i;
				float ratio = profile.getFloat(key + ".ratio", DEFAULT_RATIO);
				float[] color = profile.getFloatArray(key + ".color",
						COLOR_BLUE);
				boolean selected = profile
						.getBoolean(key + ".selected", i == 0);
				int lineWidth = profile.getInt(key + ".ratioLineWidth", 1);
				int lineType = profile.getInt(key + ".ratioLineType", 0);
				_ratios[i] = new RatioInfo(ratio, color, selected, lineWidth,
						lineType);
			}
		}

		public void fillProfile(Profile profile) {
			profile.putFloatArray(KEY_ANCHOR_COLOR, _anchorColor);
			profile.putInt(KEY_ANCHOR_LINE_WIDTH, _anchorLineWidth);
			profile.putInt(KEY_ANCHOR_LINE_TYPE, _anchorLineType);
			profile.putBoolean(KEY_USE_PIVOTS_FOR_ANCHOR_POINTS,
					_usePivotsForAnchorPoints);

			for (int i = 0; i < _ratios.length; i++) {
				String key = "ratios." + i;
				RatioInfo ratio = _ratios[i];
				profile.putFloat(key + ".ratio", ratio.getRatio());
				profile.putFloatArray(key + ".color", ratio.getColor());
				profile.putBoolean(key + ".selected", ratio.isSelected());
				profile.putInt(key + ".ratioLineWidth", ratio.getLineWidth());
				profile.putInt(key + ".ratioLineType", ratio.getLineType());
			}
		}

		public void updateFromSettings(Settings settings) {
			Profile p = new Profile();
			settings.fillProfile(p);
			updateFromProfile(p);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(_anchorColor);
			result = prime * result + _anchorLineType;
			result = prime * result + _anchorLineWidth;
			result = prime * result + Arrays.hashCode(_ratios);
			result = prime * result + (_usePivotsForAnchorPoints ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Settings other = (Settings) obj;
			if (!Arrays.equals(_anchorColor, other._anchorColor))
				return false;
			if (_anchorLineType != other._anchorLineType)
				return false;
			if (_anchorLineWidth != other._anchorLineWidth)
				return false;
			if (!Arrays.equals(_ratios, other._ratios))
				return false;
			if (_usePivotsForAnchorPoints != other._usePivotsForAnchorPoints)
				return false;
			return true;
		}

	}

	@Override
	public String getProfileKeySet() {
		return PROFILE_SET_KEY;
	}

	@Override
	public void unselected() {
		_pointingAnchor = null;
		_anchor1 = null;
		super.unselected();
	}

	@Override
	public String getKeywords() {
		return "anchor color width type ratio use pivot anchor point";
	}

	@Override
	public void fillMenu(IMenuManager menu) {
		IMenuManager menu2 = new MenuManager("Time Lines", ImageUtils.getBundledImageDescriptor(ChartPlugin.PLUGIN_ID,
				"icons/TL2_16.png"), "timelines");
		menu.add(menu2);

		menu2.add(new ToolAction(DiscardLineFirstAnchorHandler.CMD_ID) {
			@Override
			public void run() {
				DiscardLineFirstAnchorHandler.execute(getChart());
			}
		});
		menu2.add(new ToolAction(TimeLineGeneralSettingsHandler.CMD_ID) {
			@Override
			public void run() {
				TimeLineGeneralSettingsHandler.execute(getChart());
			}
		});

	}
}
