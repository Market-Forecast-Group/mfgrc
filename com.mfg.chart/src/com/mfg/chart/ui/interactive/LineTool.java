package com.mfg.chart.ui.interactive;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.mfg.chart.commands.LinesGeneralSettingsHandler;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.IndicatorLayer.PivotReference;
import com.mfg.chart.layers.PriceLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.model.IRealTimeZZModel;
import com.mfg.chart.model.PriceModel_MDB;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.MouseCursor;
import com.mfg.utils.ImageUtils;
import com.mfg.widget.arc.strategy.StraightLineFreeIndicator;

public class LineTool extends InteractiveTool implements IAnchorTool {
	private static final String PROFILE_KEY_SET = "LineTool";
	private static final String KEY_USE_USE_PIVOTS_FOR_ANCHOR_POINTS = "usePivotsForAnchorPoints";
	public static final String CONTEXT_ID = "com.mfg.chart.contexts.chartView.lineTool";
	private static final String KEY_REALTIME = "realtime";
	private static final String KEY_COLOR = "color";
	private static final String KEY_LINE_WIDTH = "lineWidth";
	private static final String KEY_LINE_TYPE = "lineType";

	public static class Line {
		private static final String K_MIRROR = "mirror";
		public PivotReference anchor1;
		public PivotReference anchor2;
		public double top;
		public double bottom;
		public boolean realtime;
		public int lineWidth;
		public int lineType;
		public float[] color;
		public boolean usePivotsForAnchorPoints;
		public boolean mirror;

		public Line(PivotReference aPoint1, PivotReference aPoint2) {
			super();
			this.anchor1 = aPoint1;
			this.anchor2 = aPoint2;
			this.realtime = false;
			color = COLOR_YELLOW;
			lineWidth = 1;
			lineType = 0;
			usePivotsForAnchorPoints = true;
			mirror = false;
		}

		public boolean isWellFormed() {
			return this.anchor1 != null && this.anchor2 != null
					&& this.anchor1.getTime() < this.anchor2.getTime();
		}

		public int getMaxLevel() {
			return Math.max(anchor1.getLevel(), anchor2.getLevel());
		}

		@Override
		public Line clone() {
			Line l = new Line(null, null);
			l.top = top;
			l.bottom = bottom;
			l.color = color;
			l.realtime = realtime;
			l.anchor1 = anchor1;
			l.anchor2 = anchor1;
			l.lineWidth = lineWidth;
			l.lineType = lineType;
			l.usePivotsForAnchorPoints = usePivotsForAnchorPoints;
			l.mirror = mirror;
			return l;
		}

		public void updateFromProfile(Profile p) {
			color = p.getFloatArray(KEY_COLOR, COLOR_YELLOW);
			realtime = p.getBoolean(KEY_REALTIME, false);
			lineWidth = p.getInt(KEY_LINE_WIDTH, 1);
			lineType = p.getInt(KEY_LINE_TYPE, 0);
			usePivotsForAnchorPoints = p.getBoolean(
					KEY_USE_USE_PIVOTS_FOR_ANCHOR_POINTS, true);
			mirror = p.getBoolean(K_MIRROR, false);
		}

		public void fillProfile(Profile p) {
			p.putFloatArray(KEY_COLOR, color);
			p.putBoolean(KEY_REALTIME, realtime);
			p.putInt(KEY_LINE_WIDTH, lineWidth);
			p.putInt(KEY_LINE_TYPE, lineType);
			p.putBoolean(KEY_USE_USE_PIVOTS_FOR_ANCHOR_POINTS,
					usePivotsForAnchorPoints);
			p.putBoolean(K_MIRROR, mirror);
		}

		public void update(Line line) {
			color = line.color;
			realtime = line.realtime;
			lineWidth = line.lineWidth;
			lineType = line.lineType;
			usePivotsForAnchorPoints = line.usePivotsForAnchorPoints;
			mirror = line.mirror;
		}
	}

	private PivotReference _pointingAnchor;
	private PivotReference _anchor1;
	private final BitmapData _anchorBmp;
	private boolean _dragging;
	private boolean _pointingLeft;
	private ChartPoint _pointingPosition;
	private boolean _editOn;
	private final List<Line> _lines;
	private final Line _defaultLine;

	public LineTool(Chart chart) {
		super("Line", chart, BITMAP_ANCHOR_LINES_TOOL_ICON);
		setTooltip("Line Tool (L): paint lines.");
		_anchorBmp = BITMAP_ANCHOR_LINES_TOOL_ICON;
		_editOn = true;
		_lines = new ArrayList<>();
		_defaultLine = new Line(null, null);
		Profile p = getProfilesManager().getDefault(getProfileKeySet());
		_defaultLine.updateFromProfile(p);
	}

	@Override
	protected void migrateProfile(Profile profile) {
		if (!profile.containsKey(KEY_LINE_TYPE)) {
			profile.putInt(KEY_LINE_TYPE, 0);
		}
		if (!profile.containsKey(KEY_USE_USE_PIVOTS_FOR_ANCHOR_POINTS)) {
			profile.putBoolean(KEY_USE_USE_PIVOTS_FOR_ANCHOR_POINTS, true);
		}
		super.migrateProfile(profile);
	}

	@Override
	protected List<Profile> createProfilePresets() {
		Profile p = new Profile("Preset 1");
		Line l = new Line(null, null);
		l.fillProfile(p);
		return Arrays.asList(p);
	}

	@Override
	public void selected() {
		super.selected();
		getChart().glChart.getSettings().setSnappingMode(SnappingMode.SNAP_XY);
	}

	@Override
	public String getContextId() {
		return CONTEXT_ID;
	}

	public Line getDefaultLine() {
		return _defaultLine;
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
		if (_dragging || !_editOn) {
			return false;
		}

		if (e.getButton() == ChartMouseEvent.RIGHT_BUTTON) {
			if (_anchor1 == null) {
				if (_pointingAnchor != null) {
					FindResult result = findLines(_pointingAnchor);
					if (result.founded) {
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
		Line line = new Line(_anchor1, _pointingAnchor);
		line.update(_defaultLine);

		try {
			updateLineDistances(line);
			_lines.add(line);
			_anchor1 = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void updateLineDistances(Line line) throws IOException {
		PriceModel_MDB priceModel = (PriceModel_MDB) getChart().getModel()
				.getPriceModel();
		StraightLineFreeIndicator ind = new StraightLineFreeIndicator(
				priceModel.getMDB(getChart().getDataLayer()),
				(int) line.anchor1.getTime());
		long upper = getChart().getModel().getPriceModel()
				.getDataLayerUpperDisplayTime(getChart().getDataLayer());
		ind.setRightAnchor((int) Math.min(line.anchor2.getTime(), upper));
		line.top = ind.getTopDistance();
		line.bottom = ind.getBottomDistance();
	}

	private Line editLines(Line line) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		getChart().openSettingsWindow(shell, this, line);
		return null;
	}

	private FindResult findLines(PivotReference anchor) {
		FindResult result = new FindResult();
		for (Line lines : _lines) {
			boolean match1 = matchAnchor(lines.anchor1, anchor);
			boolean match2 = matchAnchor(lines.anchor2, anchor);
			boolean match = match1 || match2;
			boolean visible = isVisible(lines);
			if (visible && match) {
				if (result.line == null
						|| lines.getMaxLevel() > result.line.getMaxLevel()) {
					result.line = lines;
					result.anchor = match1 ? lines.anchor1 : lines.anchor1;
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

	private boolean isVisible(Line line) {
		boolean visible1 = isVisible(line.anchor1.getLevel());
		boolean visible2 = isVisible(line.anchor2.getLevel());
		return visible1 && visible2;
	}

	@Override
	public boolean mouseMoved(ChartMouseEvent e) {
		_dragging = false;
		_pointingPosition = e.getPosition();

		if (_editOn) {
			updatePointingState(e.getPosition());

			repaint();
		}

		return false;
	}

	public void swapEditMode() {
		_editOn = !_editOn;
		repaint();
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
				if (_defaultLine.usePivotsForAnchorPoints) {
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
		if (_editOn) {
			paintEditAnchors(gl, w, h);
		}

		if (_anchor1 != null && _pointingAnchor != null) {
			Line l = new Line(_anchor1, new PivotReference(
					_pointingAnchor.getTime(), _pointingAnchor.getPrice(), 0));
			if (l.isWellFormed()) {
				l.update(_defaultLine);
				try {
					updateLineDistances(l);
					paintLine(l, gl);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Line line : _lines) {
			List<Integer> scales = Arrays.asList(Integer.valueOf(-1
					* line.anchor1.getLevel()));
			paintAnchor(line.anchor1, scales, _defaultLine.color,
					_defaultLine.lineWidth, gl);

			PivotReference anchor2 = line.anchor2;
			scales = Arrays.asList(Integer.valueOf(anchor2.getLevel()));
			paintAnchor(anchor2, scales, line.color, _defaultLine.lineWidth, gl);

			if (line.realtime) {
				IRealTimeZZModel model = getChart().getModel()
						.getScaledIndicatorModel()
						.getRealTimeZZModel(line.getMaxLevel());
				long x = model.getTime2(getChart().getDataLayer());
				double y = model.getPrice2(getChart().getDataLayer());

				Line l = new Line(line.anchor1, new PivotReference(x, (long) y,
						0));
				if (l.isWellFormed()) {
					try {
						updateLineDistances(l);
						l.color = line.color;
						paintLine(l, gl);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				paintLine(line, gl);
			}
		}
	}

	@Override
	public void deletePointedAnchor() {
		if (_pointingAnchor != null) {
			Line toDel = null;
			for (Line l : _lines) {
				if (l.anchor1.samePosition(_pointingAnchor)
						|| l.anchor2.samePosition(_pointingAnchor)) {
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

	private void paintLine(Line line, GL2 gl) {
		if (line.isWellFormed()) {
			PivotReference anchor1 = line.anchor1;
			PivotReference anchor2 = line.anchor2;
			float[] color = line.color;

			long x1 = anchor1.getTime();
			long y1 = anchor1.getPrice();
			double x2 = anchor2.getTime();
			double y2 = anchor2.getPrice();

			paintLine(line, x1, y1, x2, y2, color, gl);

			if (line.mirror) {
				double x3 = x2 + (x2 - x1);
				double y3 = y1;

				paintLine(line, x2, y2, x3, y3, color, gl);
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
	private void paintLine(Line line, double x1, double y1, double x2,
			double y2, float[] color, GL2 gl) {
		if (x2 > x1) {
			Chart chart = getChart();
			double xlen = x2 - x1;
			double ylen = y2 - y1;
			double m = ylen / xlen;
			long x3 = chart.getModel().getPriceModel()
					.getDataLayerUpperDisplayTime(chart.getDataLayer());
			double y3 = y1 + m * (x3 - x1);

			if (line.lineType != 0) {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(line.lineType, STIPPLE_PATTERN);
			}

			for (double offset : new double[] { 0, line.top, -line.bottom }) {
				gl.glPushAttrib(GL2.GL_LINE_BIT);
				gl.glLineWidth(line.lineWidth);
				gl.glColor4fv(color, 0);
				gl.glBegin(GL.GL_LINE_STRIP);
				gl.glVertex2d(x1, y1 + offset);
				gl.glVertex2d(x3, y3 + offset);
				gl.glEnd();
				gl.glPopAttrib();
			}

			if (line.lineType != 0) {
				gl.glDisable(GL2.GL_LINE_STIPPLE);
			}
		}
	}

	private void paintEditAnchors(GL2 gl, int w, int h) {
		if (isAnchor1Hidden()) {
			paintAnchor1IsHidden(gl, w, h);
		} else {

			if (_pointingLeft) {
				paintPointingLeft(gl, w, h);
			} else if (_pointingAnchor != null) {
				boolean firstAnchor = _anchor1 == null;
				paintPivotSelection(_pointingAnchor, firstAnchor,
						_defaultLine.color, _defaultLine.lineWidth, gl, w, h);
			}

			// paint first anchor
			if (_anchor1 != null && isVisible(_anchor1.getLevel())) {
				paintPivotSelection(_anchor1, true, _defaultLine.color,
						_defaultLine.lineWidth, gl, w, h);
			}
		}
	}

	private boolean isAnchor1Hidden() {
		return _anchor1 != null && !isVisible(_anchor1.getLevel());
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

	private void paintPivotSelection(PivotReference pivot, boolean firstAnchor,
			float[] color, int anchorLineWidth, GL2 gl, int w, int h) {
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

		int scaleInfo = (firstAnchor ? -1 : 1) * pivot.getLevel();
		List<Integer> scales = Arrays.asList(Integer.valueOf(scaleInfo));
		paintAnchor(pivot, scales, color, anchorLineWidth, gl);
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

	/**
	 * 
	 * @param pivot
	 * @param overlapingScales
	 * @param color
	 * @param lineWidth
	 * @param gl
	 */
	private void paintAnchor(PivotReference pivot,
			List<Integer> overlapingScales, float[] color, int lineWidth, GL2 gl) {

		final long x = pivot.getTime();
		final long y = pivot.getPrice();

		PlotRange xr = getChart().getXRange();
		PlotRange yr = getChart().getYRange();

		gl.glColor4fv(color, 0);
		gl.glRasterPos2i((int) x, (int) y);
		gl.glBitmap(_anchorBmp.width, _anchorBmp.height, 0, 0, 0, 0,
				_anchorBmp.bitmap, 0);

		double xlen = xr.plotWidth(23, _glChart.plot.screenWidth);
		double ylen = yr.plotWidth(15, _glChart.plot.screenHeight);

		double bmpLen = yr.plotWidth(_anchorBmp.height,
				_glChart.plot.screenHeight);

		int y2 = (int) (y - (overlapingScales.size() * ylen) / 2 + bmpLen / 2);

		for (int scaleInfo : overlapingScales) {
			boolean left = scaleInfo < 0;
			int x2 = (int) (x + xlen);
			String str = (left ? "> " : "< ")
					+ Integer.toString(Math.abs(scaleInfo));
			gl.glRasterPos2i(x2, y2);
			_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, str);
			y2 += ylen;
		}
	}

	@Override
	public MouseCursor getMouseCursor() {
		// return _dragging || _pointingAnchor == null ? super.getMouseCursor()
		// : MouseCursor.DEFAULT;
		return _dragging ? super.getMouseCursor() : MouseCursor.HIDDEN;
	}

	@Override
	public void unselected() {
		_pointingAnchor = null;
		_anchor1 = null;
		super.unselected();
	}

	@Override
	public String getProfileKeySet() {
		return PROFILE_KEY_SET;
	}

	@Override
	public String getKeywords() {
		return "real time realtime line color Thickness type use pivots anchor point";
	}

	@Override
	public void fillMenu(IMenuManager menu) {
		MenuManager menu2 = new MenuManager("Lines",
				ImageUtils.getBundledImageDescriptor(ChartPlugin.PLUGIN_ID,
						"icons/LT_16.png"), "lines");
		menu.add(menu2);
		menu2.add(new ToolAction(DiscardLineFirstAnchorHandler.CMD_ID) {
			@Override
			public void run() {
				DiscardLineFirstAnchorHandler.execute(getChart());
			}
		});
		menu2.add(new ToolAction(LinesGeneralSettingsHandler.CMD_ID) {
			@Override
			public void run() {
				LinesGeneralSettingsHandler.execute(getChart());
			}
		});
	}
}
