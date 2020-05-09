package com.mfg.chart.ui.interactive;

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
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
import com.mfg.chart.backend.opengl.MFGChartCustomization;
import com.mfg.chart.backend.opengl.PriceChartCanvas_OpenGL;
import com.mfg.chart.commands.DiscardLineFirstAnchorHandler;
import com.mfg.chart.commands.TrendLinesSettingsHandler;
import com.mfg.chart.model.IPriceModel;
import com.mfg.chart.profiles.Profile;
import com.mfg.chart.ui.MouseCursor;
import com.mfg.common.DfsSymbol;
import com.mfg.utils.ImageUtils;

public class TrendLinesTool extends InteractiveTool implements IAnchorTool {

	private static final ChartPoint NULL_DELTA = new ChartPoint(0, 0, 0, 0);
	private static final String CONTEXT_ID = "com.mfg.chart.contexts.chartView.trendLines";
	private Line _drawLine;
	private Settings _defaultSettings;
	private Line _pointingLine;
	private List<Line> _lines;
	private boolean _dragging;
	private Line _recoverLine;
	private boolean _paintNewLine;
	private SnappingMode _snappingMode;
	NumberFormat _decimal = NumberFormat.getInstance();
	private ChartPoint _offset = NULL_DELTA;
	private double _priceValue;
	private String _currency;

	public static class Settings {
		private static final String K_MIRROR = "mirror";
		private static final String K_SHAPE_WIDTH = "shapeWidth";
		private static final String K_SHAPE_TYPE = "shapeType";
		private static final String K_LINE_WIDTH = "lineWidth";
		private static final String K_LINE_TYPE = "lineType";
		private static final String K_COLOR = "color";
		public float[] color;
		public int lineType;
		public int lineWidth;
		public int shapeType;
		public int shapeWidth;
		public boolean mirror;

		public Settings() {
			color = COLOR_YELLOW;
			lineType = 0;
			lineWidth = 1;
			shapeType = 2;
			shapeWidth = 1;
			mirror = false;
		}

		public void fillProfile(Profile p) {
			p.putFloatArray(K_COLOR, color);
			p.putInt(K_LINE_TYPE, lineType);
			p.putInt(K_LINE_WIDTH, lineWidth);
			p.putInt(K_SHAPE_TYPE, shapeType);
			p.putInt(K_SHAPE_WIDTH, shapeWidth);
			p.putBoolean(K_MIRROR, mirror);
		}

		public void updateFromProfile(Profile p) {
			color = p.getFloatArray(K_COLOR, COLOR_YELLOW);
			lineType = p.getInt(K_LINE_TYPE, 0);
			lineWidth = p.getInt(K_LINE_WIDTH, 1);
			shapeType = p.getInt(K_SHAPE_TYPE, 2);
			shapeWidth = p.getInt(K_SHAPE_WIDTH, 1);
			mirror = p.getBoolean(K_MIRROR, false);
		}

		@Override
		public Settings clone() {
			Settings s = new Settings();
			Profile p = new Profile();
			fillProfile(p);
			s.updateFromProfile(p);
			return s;
		}
	}

	public static class Line {
		public ChartPoint start;
		public ChartPoint stop;
		public Settings settings = new Settings();
	}

	public TrendLinesTool(Chart chart) {
		super("Trend Lines", chart, null);
		_lines = new ArrayList<>();
		_defaultSettings = new Settings();
		_defaultSettings.updateFromProfile(getDefault());
		_drawLine = new Line();
		_drawLine.settings = _defaultSettings;

		int tickScale = chart.getModel().getPriceModel().getTickScale();
		_decimal.setMinimumFractionDigits(tickScale);
		_decimal.setMaximumFractionDigits(tickScale);
		_priceValue = 12.5;
		_currency = DfsSymbol.CURRENCY_USD;
	}

	public double getPriceValue() {
		return _priceValue;
	}

	public void setPriceValue(double priceValue) {
		_priceValue = priceValue;
	}

	public String getCurrency() {
		return _currency;
	}

	public void setCurrency(String currency) {
		_currency = currency;
	}

	@Override
	protected List<Profile> createProfilePresets() {
		Profile p = new Profile("Profile 1");
		Settings s = new Settings();
		s.fillProfile(p);
		return Arrays.asList(p);
	}

	public Settings getDefaultSettings() {
		return _defaultSettings;
	}

	@Override
	public String getProfileKeySet() {
		return "trendLinesTool";
	}

	@Override
	public String getContextId() {
		return CONTEXT_ID;
	}

	@Override
	public String getKeywords() {
		return "trend line";
	}

	public double x(double plotX) {
		return getChart().getXRange().screenValue(
				plotX,
				getChart().getPlotScreenWidth()
						- getChart().getPlotLeftMargin());
	}

	public double y(double plotY) {
		return getChart().getXRange().screenValue(
				plotY,
				getChart().getPlotScreenHeight()
						- getChart().getPlotBottomMargin());
	}

	@Override
	public boolean mouseReleased(ChartMouseEvent e) {
		_recoverLine = null;
		_paintNewLine = false;

		if (e.getButton() == ChartMouseEvent.RIGHT_BUTTON) {
			if (_pointingLine == null) {
				discardFirstAnchor();
			} else {
				editLine(_pointingLine);
			}
		} else {
			if (_dragging) {
				return false;
			}

			if (_drawLine.start == null) {
				if (_pointingLine == null) {
					_paintNewLine = true;
				} else {
					_paintNewLine = false;
					ChartPoint pos = e.getPosition();
					double d1 = distance(_pointingLine.start, pos);
					double d2 = distance(_pointingLine.stop, pos);
					if (d1 < 40 || d2 < 40) {
						ChartPoint newStart;
						ChartPoint ref;
						if (d1 < d2) {
							newStart = _pointingLine.stop;
							ref = _pointingLine.start;
						} else {
							newStart = _pointingLine.start;
							ref = _pointingLine.stop;
						}
						_offset = e.getPosition().delta(ref);
						_drawLine.start = newStart;
						_drawLine.stop = ref;
						_recoverLine = _pointingLine;
						_lines.remove(_pointingLine);
						_pointingLine = null;
					} else {
						_paintNewLine = true;
					}
				}
				if (_paintNewLine) {
					_drawLine.settings = _defaultSettings;
					_drawLine.start = e.getPosition();
					_offset = NULL_DELTA;
				}
			} else {
				_drawLine.stop = e.getPosition().delta(_offset);
				addLine();
			}
			repaint();
		}
		return false;
	}

	private void addLine() {

		Settings keepSettings = _drawLine.settings.clone();

		_lines.add(_drawLine);

		_drawLine = new Line();
		_drawLine.settings = keepSettings;
		_drawLine.start = null;
		_drawLine.stop = null;
	}

	private void editLine(Line line) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		getChart().openSettingsWindow(shell, this, line);
		repaint();
	}

	@Override
	public boolean mouseMoved(ChartMouseEvent e) {
		_dragging = false;
		ChartPoint pos = e.getPosition();

		ChartPoint p1 = _drawLine.stop;
		ChartPoint p2 = _drawLine.start;

		PriceChartCanvas_OpenGL canvas = (PriceChartCanvas_OpenGL) getChart()
				.getCanvas();
		boolean shift = canvas.getConnection().isShiftPressed();

		if (shift && p1 != null && p2 != null && !_paintNewLine) {
			double dpx = pos.getPlotX() - p1.getPlotX();
			double dpy = pos.getPlotY() - p1.getPlotY();
			int dsx = pos.getScreenX() - p1.getScreenX();
			int dsy = pos.getScreenY() - p1.getScreenY();

			ChartPoint p3 = new ChartPoint(
			// compute with delta
					p2.getScreenX() + dsx,

					p2.getScreenY() + dsy,

					p2.getPlotX() + dpx,

					p2.getPlotY() + dpy);
			_drawLine.start = p3.delta(_offset);

		}
		_drawLine.stop = pos.delta(_offset);

		_pointingLine = null;

		double min = Double.MAX_VALUE;
		for (Line line : _lines) {
			double d = Math.min(distance(line.start, pos),
					distance(line.stop, pos));
			if (d < min && d < 40) {
				_pointingLine = line;
				min = d;
			}
		}

		repaint();
		return false;
	}

	private double distance(ChartPoint a, ChartPoint b) {
		return Point2D.distance(x(a.getPlotX()), y(a.getPlotY()),
				x(b.getPlotX()), y(b.getPlotY()));
	}

	@Override
	public boolean mouseDragged(ChartMouseEvent e) {
		_dragging = true;
		return super.mouseDragged(e);
	}

	@Override
	public void paintOnPlotMatrix(GL2 gl, int w, int h) {
		for (Line line : _lines) {
			drawMirrorLine(gl, line, line.settings.color);
		}

		if (_drawLine.start == null && _pointingLine != null) {
			drawMirrorLine(gl, _pointingLine, COLOR_CYAN);
		} else {
			drawMirrorLine(gl, _drawLine, _drawLine.settings.color);
		}
	}

	private void drawMirrorLine(GL2 gl, Line line, float[] color) {
		if (line.start != null && line.stop != null) {
			drawLine(gl, line, color, false, _priceValue);
			if (line.settings.mirror) {
				Line l = new Line();
				l.start = line.stop;
				double dx = line.stop.getPlotX() - line.start.getPlotX();
				double x2 = line.stop.getPlotX() + dx;
				double y2 = line.start.getPlotY();
				l.stop = new ChartPoint(0, 0, x2, y2);
				drawLine(gl, l, color, true, _priceValue);
			}
		}
	}

	private void drawLine(GL2 gl, Line line, float[] color, boolean mirror,
			double tickValue) {
		ChartPoint a = line.start;
		ChartPoint b = line.stop;
		if (a != null && b != null) {
			PlotRange xrange = getChart().getXRange();

			double x1 = a.getPlotX();
			double y1 = a.getPlotY();
			double x2 = b.getPlotX();
			double y2 = b.getPlotY();
			double m = (y2 - y1) / (x2 - x1);
			double n = y2 - m * x2;
			double x3 = x2 < x1 ? xrange.lower : xrange.upper;
			double y3 = m * x3 + n;
			double x0;
			double y0;
			if (mirror) {
				x0 = x1;
				y0 = y1;
			} else {
				x0 = x2 < x1 ? xrange.upper : xrange.lower;
				y0 = m * x0 + n;
			}

			Settings s = line.settings;
			gl.glPushAttrib(GL.GL_LINES);

			if (s.lineType != STIPPLE_FACTOR_NULL) {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(s.lineType, STIPPLE_PATTERN);
			}
			gl.glLineWidth(s.lineWidth);

			gl.glColor4fv(color, 0);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex2d(x0, y0);
			gl.glVertex2d(x3, y3);
			gl.glEnd();

			if (!mirror) {
				BitmapData data = SHAPES[s.shapeWidth][s.shapeType];
				gl.glRasterPos2d(x1, y1);
				gl.glBitmap(data.width, data.height, data.x, data.y, 0, 0,
						data.bitmap, 0);

				MFGChartCustomization custom = getChart().getCustom();
				_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "    P="
						+ custom.formatYTick(y1));

				gl.glRasterPos2d(x2, y2);
				gl.glBitmap(data.width, data.height, data.x, data.y, 0, 0,
						data.bitmap, 0);

				IPriceModel priceModel = getChart().getModel().getPriceModel();
				double pow = Math.pow(10, priceModel.getTickScale());
				double tickSize = priceModel.getTickSize();
				tickSize = tickSize / pow;
				float delta = (float) Math.abs(y2 - y1);
				double result = delta / pow * tickValue / tickSize;

				String V_str = _decimal.format(result);

				V_str = _currency == DfsSymbol.CURRENCY_EUR ? V_str + "e"
						: "$" + V_str;
				String D_str = custom.formatYTick(delta);

				_glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "    P="
						+ custom.formatYTick(y2) + ", D=" + D_str + ", V="
						+ V_str);
			}

			if (s.lineType != STIPPLE_FACTOR_NULL) {
				gl.glDisable(GL2.GL_LINE_STIPPLE);
			}

			gl.glPopAttrib();

		}
	}

	@Override
	public MouseCursor getMouseCursor() {
		if (_dragging || _drawLine.start == null && _pointingLine == null) {
			return null;
		}
		return MouseCursor.DEFAULT;
	}

	@Override
	public boolean discardFirstAnchor() {
		_drawLine.start = null;
		_drawLine.stop = null;
		if (_recoverLine != null) {
			_lines.add(_recoverLine);
			_recoverLine = null;
		}
		repaint();
		return false;
	}

	@Override
	public void deletePointedAnchor() {
		if (_pointingLine != null) {
			_lines.remove(_pointingLine);
			_pointingLine = null;
			repaint();
		}
	}

	@Override
	public void fillMenu(IMenuManager menu) {
		MenuManager menu2 = new MenuManager("Trend Lines",
				ImageUtils.getBundledImageDescriptor(ChartPlugin.PLUGIN_ID,
						"icons/trend-tool.png"), "trendlines");
		menu.add(menu2);
		menu2.add(new ToolAction(DiscardLineFirstAnchorHandler.CMD_ID) {
			@Override
			public void run() {
				DiscardLineFirstAnchorHandler.execute(getChart());
			}
		});
		menu2.add(new ToolAction(TrendLinesSettingsHandler.CMD_ID) {
			@Override
			public void run() {
				TrendLinesSettingsHandler.execute(getChart());
			}
		});
	}

	@Override
	public void selected() {
		super.selected();
		_snappingMode = getChart().glChart.getSettings().getSnappingMode();
		getChart().glChart.getSettings().setSnappingMode(SnappingMode.SNAP_XY);
	}

	@Override
	public void unselected() {
		super.unselected();
		getChart().glChart.getSettings().setSnappingMode(_snappingMode);
	}
}
