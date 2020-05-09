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

package org.mfg.opengl.chart;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.mfg.opengl.IGLDrawable;
import org.mfg.opengl.chart.interactive.InteractiveTool;

import com.jogamp.opengl.util.gl2.GLUT;

@SuppressWarnings("boxing")
public class GLChart implements IGLDrawable {
	public Plot plot;
	public int xMargin;
	public int yMargin;

	public double xTickSize;
	public double yTickSize;

	public boolean drawCrosshair;
	public int crossScreenX;
	public int crossScreenY;

	private double _lowerXSelection;
	private double _upperXSelection;
	private double _lowerYSelection;
	private double _upperYSelection;
	private boolean _rangeSelected;
	private boolean _osdVisible;

	private Settings _settings;

	private IGLChartCustomization _custom;
	private final List<IGLDrawable> _osdDrawables;
	private final List<IGLDrawable> _plotDrawables;

	private IDataset _crossSnappingDataset;

	private final GLUT _glut = new GLUT();

	private InteractiveTool _selectedTool;
	private InteractiveTool[] _tools;
	private double _crosshairInPlotX;
	private double _crosshairInPlotY;

	public GLChart() {
		plot = new Plot();
		xMargin = 80;
		yMargin = 20;
		xTickSize = 1;
		yTickSize = 25;
		drawCrosshair = true;
		_custom = new DefaultGLChartCustomization();
		_osdDrawables = new ArrayList<>();
		_plotDrawables = new ArrayList<>();
		setOsdVisible(true);
		setRangeSelected(false);
		_settings = new Settings();
		_selectedTool = null;
		_tools = new InteractiveTool[0];
	}

	/**
	 * @return the itool
	 */
	// TODO: Very wrong! it is something only for MFG concern! We should split
	// MFG logic from GLChart.
	public InteractiveTool getSelectedTool() {
		return _selectedTool;
	}

	/**
	 * @param tool
	 *            the tool to set
	 */
	public void setSelectedTool(InteractiveTool tool) {
		if (_selectedTool != null) {
			_selectedTool.unselected();
		}

		this._selectedTool = tool;

		if (tool != null) {
			tool.selected();
		}
	}

	public InteractiveTool[] getTools() {
		return _tools;
	}

	public void setTools(InteractiveTool[] tools) {
		_tools = tools;
	}

	/**
	 * @return the custom
	 */
	public IGLChartCustomization getCustom() {
		return _custom;
	}

	public Settings getSettings() {
		return _settings;
	}

	public void setSettings(Settings settings) {
		_settings = settings;
	}

	/**
	 * @param custom
	 *            the custom to set
	 */
	public void setCustom(IGLChartCustomization custom) {
		this._custom = custom;
	}

	@Override
	public void init(final GL2 gl) {
		// XXX:dw.setGL(new DebugGL(gl));

		// used to show the shapes (bitmap)
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

		// the selection polygon
		byte[] halftone = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55,
				(byte) 0x55, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55 };

		gl.glPolygonStipple(halftone, 0);
		// --

		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GLLightingFunc.GL_LIGHTING);
		gl.glShadeModel(GLLightingFunc.GL_FLAT);

		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST /* GL.GL_NICEST */);
	}

	@Override
	public void reshape(final GL2 gl, final int w, final int h) {
		plot.screenWidth = w;
		plot.screenHeight = h;

		if (isOsdVisible()) {
			for (final IGLDrawable dw : _osdDrawables) {
				dw.reshape(gl, w, h);
			}
		}
	}

	@Override
	public void display(final GL2 gl, final int width, final int height) {
		if (width != plot.screenWidth || height != plot.screenHeight) {
			reshape(gl, width, height);
		}

		// TODO: alpha (bgColor[3]) is not working here, I don't know why.
		// gl.glClearColor(bgColor[0], bgColor[1], bgColor[2], bgColor[3]);
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		// in the meantime I will paint a "background box"
		loadScreenMatrix(gl, width, height);

		gl.glColor4fv(_settings.getBgColor(), 0);
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glVertex2d(0, 0);
		gl.glVertex2d(width, 0);
		gl.glVertex2d(width, height);
		gl.glVertex2d(0, height);
		gl.glEnd();
		// ---

		paintGrid(gl, width, height);

		paintPlot(gl, width, height);

		if (isRangeSelected()) {
			paintSelection(gl, width, height);
		}

		paintDrawables(gl, width, height, _plotDrawables);

		boolean empty = plot.isEmpty();
		if (!empty) {
			if (drawCrosshair) {
				// TODO: maybe we should paint all in plot coordinates.
				loadPlotMatrix(gl, width, height);
				paintCrosshairAndTooltip(gl, width, height);
				loadScreenMatrix(gl, width, height);
			}

			if (_tools.length > 0) {
				loadScreenMatrix(gl, width, height);
				for (InteractiveTool tool : _tools) {
					if (tool.isAlwaysPaint() || tool == _selectedTool) {
						tool.paintOnScreenMatrix(gl, width, height);
					}
				}

				loadPlotMatrix(gl, width, height);
				for (InteractiveTool tool : _tools) {
					if (tool.isAlwaysPaint() || tool == _selectedTool) {
						tool.paintOnPlotMatrix(gl, width, height);
					}
				}
			}
		}

		if (isOsdVisible()) {
			loadScreenMatrix(gl, width, height);
			paintDrawables(gl, width, height, _osdDrawables);
		}

		gl.glFlush();
	}

	/**
	 * @param gl
	 * @param width
	 * @param heitgh
	 */
	private void paintSelection(final GL2 gl, final int width, final int heitgh) {
		gl.glEnable(GL2.GL_POLYGON_STIPPLE);
		gl.glColor4fv(COLOR_BLUE, 0);
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glVertex2d(getLowerXSelection(), getLowerYSelection());
		gl.glVertex2d(getLowerXSelection(), getUpperYSelection());
		gl.glVertex2d(getUpperXSelection(), getUpperYSelection());
		gl.glVertex2d(getUpperXSelection(), getLowerYSelection());
		gl.glEnd();
		gl.glDisable(GL2.GL_POLYGON_STIPPLE);
	}

	/**
	 * @param gl
	 * @param width
	 * @param height
	 * @param list
	 */
	@SuppressWarnings("static-method")
	protected void paintDrawables(final GL2 gl, final int width,
			final int height, List<IGLDrawable> list) {
		for (final IGLDrawable dw : list) {
			dw.display(gl, width, height);
		}
	}

	private void paintPlot(final GL2 gl, final int w, final int h) {
		loadPlotMatrix(gl, w, h);
		gl.glLineWidth(1.5f);
		plot.paint(gl);
	}

	private void loadPlotMatrix(final GL2 gl, final int w, final int h) {
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		final PlotRange xrang = plot.xrange;
		final PlotRange yrang = plot.yrange;
		gl.glOrtho(xrang.lower, xrang.upper, yrang.lower, yrang.upper, -1, 1);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glViewport(xMargin, yMargin, w - xMargin, h - yMargin);
	}

	protected void paintGrid(final GL2 gl, final int width, final int height) {
		loadScreenMatrix(gl, width, height);

		paintGridBorder(gl, width, height);

		paintTicks(gl, width, height);

		if (_custom != null) {
			_custom.paintExtraGrid(gl, width, height);
		}
	}

	private static void loadScreenMatrix(final GL2 gl, final int width,
			final int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, width, 0, height, -1, 1);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	private void paintGridBorder(final GL2 gl, final int width, final int height) {
		gl.glLineWidth(_settings.getGridWidth());
		gl.glColor4fv(_settings.getGridColor(), 0);
		gl.glBegin(GL.GL_LINE_STRIP);
		// gl.glVertex2i(width /*- axisXMargin*/, height /*-
		// this.axisYMargin*/);
		gl.glVertex2i(xMargin, height /*- this.axisYMargin*/);
		gl.glVertex2i(xMargin, yMargin);
		gl.glVertex2i(width /*- axisXMargin*/, yMargin);
		gl.glEnd();
	}

	public double convertScreenToPlot_X(int screenX) {
		return plot.xrange.lower
				+ plot.xrange.plotWidth(screenX - xMargin, plot.screenWidth
						- xMargin);
	}

	public double convertScreenToPlot_Y(int screenY) {
		return plot.yrange.lower
				+ plot.yrange.plotWidth(screenY - yMargin, plot.screenHeight
						- yMargin);
	}

	public int convertPlotToScreen_X(double plotX) {
		return xMargin
				+ plot.xrange.screenValue(plotX, plot.screenWidth - xMargin);
	}

	public int convertPlotToScreen_Y(double plotY) {
		return yMargin
				+ plot.yrange.screenValue(plotY, plot.screenHeight - yMargin);
	}

	/**
	 * 
	 * Paint crosshair and tooltip.
	 * 
	 * @param gl
	 * @param width
	 * @param height
	 */
	private void paintCrosshairAndTooltip(final GL2 gl, final int width,
			final int height) {

		double plotX = convertScreenToPlot_X(crossScreenX);
		double plotY = convertScreenToPlot_Y(crossScreenY);

		double closePlotX = plotX;
		double closePlotY = plotY;

		boolean showTooltip = false;

		for (int i = 0; i < _crossSnappingDataset.getSeriesCount(); i++) {
			if (_crossSnappingDataset.getItemCount(i) > 0) {
				showTooltip = true;
				break;
			}
		}

		// convert the cross to the close time

		// Giulio Rugarli:
		// 1) Crossair (CA) snaps exactly over price even though we position
		// ourselves away from it.
		// 2) CA snaps to the closest TICK this is the default mode.
		// 3) CA does NOT snaps over tick meaning that we could go wherever
		// we want which is useful for going close for instance to a line which
		// can be a trend line.

		if (_crossSnappingDataset != null && plot.xrange.getLength() != 0) {
			SnappingMode snappingMode = _settings.getSnappingMode();
			if (snappingMode == SnappingMode.SNAP_Y) {
				// rule 2
				// TODO: it uses long because MFG domain.
				long min = (long) plotY / (long) yTickSize * (long) yTickSize;
				long max = ((long) plotY / (long) yTickSize + 1)
						* (long) yTickSize;
				if (max - plotY < plotY - min) {
					closePlotY = max;
				} else {
					closePlotY = min;
				}
			} else {
				double min = Double.MAX_VALUE;
				if (snappingMode == SnappingMode.SNAP_XY) {
					// rule 3
					for (int series = 0; series < _crossSnappingDataset
							.getSeriesCount(); series++) {
						final int itemCount = _crossSnappingDataset
								.getItemCount(series);
						for (int item = 0; item < itemCount; item++) {
							double dsPlotX = _crossSnappingDataset.getX(series,
									item);
							double dsPlotY = _crossSnappingDataset.getY(series,
									item);

							final double diff = Math.abs(plotX - dsPlotX);
							if (diff < min) {
								closePlotX = dsPlotX;
								closePlotY = dsPlotY;
								min = diff;
							}
						}
					}
				}
			}
		}

		plotX = closePlotX;
		plotY = closePlotY;

		_crosshairInPlotX = plotX;
		_crosshairInPlotY = plotY;

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(_settings.getCrosshairWidth());
		int crosshairStippleFactor = _settings.getCrosshairStippleFactor();
		if (crosshairStippleFactor != STIPPLE_FACTOR_NULL) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(crosshairStippleFactor,
					_settings.getCrosshairStipplePattern());
		}

		gl.glColor4fv(_settings.getCrosshairColor(), 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2d(plot.xrange.lower, plotY);
		gl.glVertex2d(plot.xrange.upper, plotY);
		gl.glVertex2d(plotX, plot.yrange.lower);
		gl.glVertex2d(plotX, plot.yrange.upper);
		gl.glEnd();

		gl.glPopAttrib();

		// tool-tip
		final int font = GLUT.BITMAP_HELVETICA_12;

		double xScrToPlot = plot.xrange.getLength() / plot.screenWidth;
		double yScrToPlot = plot.yrange.getLength() / plot.screenHeight;

		if (showTooltip) {
			// paint x-tooltip
			final String tooltip = _custom.getXTooltip(plotX, plotY);
			if (tooltip != null) {
				double len = _glut.glutBitmapLength(font, tooltip + "  ")
						* xScrToPlot;
				double offs = plotX > plot.xrange.lower
						+ plot.xrange.getLength() / 2 ? -len - 20 * xScrToPlot
						: 0;
				double x = plotX + 10 * xScrToPlot + offs;
				double y = plot.yrange.upper - 20 * yScrToPlot;
				paintTooltip(gl, tooltip, font, len, x, y);
			}
		}

		if (showTooltip) {
			// paint y-tooltip
			final String tooltip = _custom.getYTooltip(plotX, plotY);
			if (tooltip != null) {
				double len = _glut.glutBitmapLength(font, tooltip) * xScrToPlot;
				double offs = plotY > plot.yrange.lower
						+ plot.yrange.getLength() / 2 ? -20 * yScrToPlot
						: 10 * yScrToPlot;
				double y = plotY + offs;

				double x = plot.xrange.lower + 10 * xScrToPlot;
				if (plotX < x + len + 10 * xScrToPlot) {
					x = plot.xrange.upper - 10 * xScrToPlot - len;
				}

				paintTooltip(gl, tooltip, font, len, x, y);
			}
		}
	}

	/**
	 * @param gl
	 * @param tooltip
	 * @param font
	 * @param stringLen
	 * @param x
	 * @param y
	 */
	private void paintTooltip(final GL2 gl, final String tooltip,
			final int font, double stringLen, double x, double y) {
		double xScrToPlot = plot.xrange.getLength() / plot.screenWidth;
		double yScrToPlot = plot.yrange.getLength() / plot.screenHeight;

		double xspace = xScrToPlot * 5;
		double yspace = yScrToPlot * 5;

		final double x1 = x - xspace;
		final double y1 = y - yspace;
		final double x2 = x + stringLen + xspace * 3;
		final double y2 = y + yspace * 3;

		// paint clear the background with the same bgColor
		// but first paint a black box, because alpha blending
		gl.glColor4fv(COLOR_BLACK, 0);
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glVertex2d(x1, y1);
		gl.glVertex2d(x2, y1);
		gl.glVertex2d(x2, y2);
		gl.glVertex2d(x1, y2);
		gl.glEnd();

		gl.glColor4fv(_settings.getBgColor(), 0);
		gl.glBegin(GL2GL3.GL_QUADS);
		gl.glVertex2d(x1, y1);
		gl.glVertex2d(x2, y1);
		gl.glVertex2d(x2, y2);
		gl.glVertex2d(x1, y2);
		gl.glEnd();
		// ---

		// paint the tooltip
		gl.glColor4fv(_settings.getTextColor(), 0);
		gl.glRasterPos2d(x, y);
		_glut.glutBitmapString(font, tooltip);
	}

	private void paintTicks(final GL2 gl, final int width, final int height) {
		gl.glPushAttrib(GL2.GL_LINE_BIT);

		gl.glLineWidth(_settings.getGridWidth());

		if (_settings.getGridStippleFactor() != STIPPLE_FACTOR_NULL) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(_settings.getGridStippleFactor(),
					_settings.getGridStipplePattern());
		}
		paintVerticalGridLines(gl, width, height);
		paintHorizontalGridLines(gl, width, height);

		gl.glPopAttrib();

	}

	private void paintVerticalGridLines(final GL2 gl, final int width,
			final int height) {
		PlotRange xrange = plot.xrange;

		// TODO: Very very ugly. This is a fix of an external bug.
		if (xrange.getLength() == 0) {
			xrange = new PlotRange(xrange.lower, xrange.lower + 10);

		}
		final int plotWidth = width - xMargin;

		final double nearTickSize = xrange.plotWidth(100, plotWidth);
		double i = Math.ceil(nearTickSize / xTickSize);
		final double bestTickSize = xTickSize * i;

		double startTick = 0;

		i = Math.ceil(xrange.lower / bestTickSize);
		startTick = i * bestTickSize;

		double tick = startTick;

		final GLUT glut = new GLUT();
		final int FONT = GLUT.BITMAP_HELVETICA_12;
		int y = yMargin - 15;

		gl.glColor4fv(_settings.getTextColor(), 0);

		int lastStrX = Integer.MIN_VALUE;
		while (tick < xrange.upper) {
			if (tick > xrange.upper) {
				break;
			}
			if (tick > xrange.lower) {
				final int x = xMargin + xrange.screenValue(tick, plotWidth);
				final String str = _custom.formatXTick(tick);
				int strWidth = glut.glutBitmapLength(FONT, str);
				int strX = x - strWidth / 2;
				if (strX > lastStrX + 10) {
					gl.glRasterPos2i(strX, y);
					glut.glutBitmapString(FONT, str);
					lastStrX = strX + strWidth;
				}
			}

			tick += bestTickSize;
		}

		tick = startTick;
		y = yMargin;

		gl.glColor4fv(_settings.getGridColor(), 0);
		gl.glBegin(GL.GL_LINES);

		while (tick < xrange.upper) {
			if (tick > xrange.upper) {
				break;
			}
			if (tick > xrange.lower) {
				final int x = xMargin + xrange.screenValue(tick, plotWidth);
				gl.glVertex2d(x, y);
				gl.glVertex2d(x, height);
			}

			tick += bestTickSize;
		}

		gl.glEnd();
	}

	private void paintHorizontalGridLines(final GL2 gl, final int width,
			final int height) {
		final PlotRange yrange = plot.yrange;
		final int plotHeight = height - yMargin;
		final double tickSize = yTickSize;
		List<Double> ticks = new LinkedList<>();
		{
			final double nearTickSize = yrange.plotWidth(50, plotHeight);
			double i = Math.ceil(nearTickSize / tickSize);
			final double bestTickSize = tickSize * i;

			double startTick = 0;

			i = Math.ceil(yrange.lower / bestTickSize);
			startTick = i * bestTickSize;

			double tick = startTick;
			while (tick < yrange.upper) {
				if (tick > yrange.upper) {
					break;
				}
				if (tick > yrange.lower) {
					ticks.add(tick);
				}
				tick += bestTickSize;
			}
			List<Double> extra = _custom.computeExtraYTicks(yrange);
			if (extra != null) {
				for (double etick : extra) {
					if (!ticks.contains(extra)) {
						ticks.add(etick);
					}
				}
			}
		}

		final GLUT glut = new GLUT();
		final int FONT = GLUT.BITMAP_HELVETICA_12;

		gl.glColor4fv(_settings.getTextColor(), 0);

		for (Double tick : ticks) {
			gl.glColor4fv(_settings.getTextColor(), 0);

			final int y = yMargin + yrange.screenValue(tick, plotHeight);
			final String str = _custom.formatYTick(tick);
			int strWidth = glut.glutBitmapLength(FONT, str);
			final int strX = xMargin - strWidth - 10;

			float[] tickBg = _custom.getYTickBackgroundColor(tick);
			if (tickBg != null) {
				gl.glColor4fv(tickBg, 0);
				gl.glBegin(GL2GL3.GL_QUADS);
				gl.glVertex2i(2, y - 2);
				gl.glVertex2i(xMargin - 2, y - 2);
				gl.glVertex2i(xMargin - 2, y + 12 - 2);
				gl.glVertex2i(2, y + 12 - 2);
				gl.glEnd();
				float[] tickFg = _custom.getYTickForegroundColor(tick);
				gl.glColor4fv(tickFg == null ? _settings.getTextColor()
						: tickFg, 0);
			}
			gl.glRasterPos2i(strX, y);
			glut.glutBitmapString(FONT, str);
		}

		gl.glBegin(GL.GL_LINES);

		for (double tick : ticks) {
			float[] color = _custom.getYTickGridLineColor(tick);
			if (color == null) {
				color = _settings.getGridColor();
			}
			gl.glColor4fv(color, 0);
			final int y = yMargin + yrange.screenValue(tick, plotHeight);
			gl.glVertex2d(xMargin, y);
			gl.glVertex2d(width, y);
		}

		gl.glEnd();
	}

	/**
	 * @param priceChart_OpenGL
	 */
	public void addOSDDrawable(final IGLDrawable dw) {
		_osdDrawables.add(dw);
	}

	public void addPlotDrawable(final IGLDrawable dw) {
		_plotDrawables.add(dw);
	}

	public void setCrosshairInPlot(final double x, final double y) {
		crossScreenX = convertPlotToScreen_X(x);
		crossScreenY = convertPlotToScreen_Y(y);
	}

	public void setCrosshairInPlot(Point2D.Double point) {
		setCrosshairInPlot(point.getX(), point.getY());
	}

	public Point2D.Double getCrosshairInPlot() {
		return new Point2D.Double(_crosshairInPlotX, _crosshairInPlotY);
	}

	/**
	 * @param x
	 * @param y
	 */
	public void setCrosshair(final int x, final int y) {
		crossScreenX = x;
		crossScreenY = y;
	}

	/**
	 * @return the crossSnappingDataset
	 */
	public IDataset getCrossSnappingDataset() {
		return _crossSnappingDataset;
	}

	/**
	 * @param crossSnappingDataset
	 *            the crossSnappingDataset to set
	 */
	public void setCrossSnappingDataset(final IDataset crossSnappingDataset) {
		this._crossSnappingDataset = crossSnappingDataset;
	}

	public double getLowerXSelection() {
		return _lowerXSelection;
	}

	public void setLowerXSelection(double lowerXSelection) {
		this._lowerXSelection = lowerXSelection;
	}

	public double getUpperXSelection() {
		return _upperXSelection;
	}

	public void setUpperXSelection(double upperXSelection) {
		this._upperXSelection = upperXSelection;
	}

	public double getLowerYSelection() {
		return _lowerYSelection;
	}

	public void setLowerYSelection(double lowerYSelection) {
		this._lowerYSelection = lowerYSelection;
	}

	public double getUpperYSelection() {
		return _upperYSelection;
	}

	public void setUpperYSelection(double upperYSelection) {
		this._upperYSelection = upperYSelection;
	}

	public boolean isRangeSelected() {
		return _rangeSelected;
	}

	public void setRangeSelected(boolean rangeSelected) {
		this._rangeSelected = rangeSelected;
	}

	public boolean isOsdVisible() {
		return _osdVisible;
	}

	public void setOsdVisible(boolean osdVisible) {
		this._osdVisible = osdVisible;
	}

}
