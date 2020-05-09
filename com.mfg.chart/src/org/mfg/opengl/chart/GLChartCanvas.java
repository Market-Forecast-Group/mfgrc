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

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class GLChartCanvas extends GLCanvas {

	private Listener _resizeListener;
	private PaintListener _paintListener;
	GLContext _glContext;
	private GLChart _glChart;

	public GLChartCanvas(final Composite parent, final GLData glData,
			final GLChart chart) {
		super(parent, SWT.NO_BACKGROUND, glData);
		replaceChart(glData, chart);

		addDisposeListener(new DisposeListener() {

			@SuppressWarnings("synthetic-access")
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				destroyContext();
			}
		});
	}

	/**
	 * @param glData
	 */
	public void replaceChart(final GLData glData, final GLChart chart) {
		_glChart = chart;
		if (_glContext != null) {
			removeListener(SWT.Resize, _resizeListener);
			removePaintListener(_paintListener);
			destroyContext();
		}

		setCurrent();
		_glContext = GLDrawableFactory.getDesktopFactory()
				.createExternalGLContext();
		final GL2 gl = _glContext.getGL().getGL2();
		chart.init(gl);

		// fix the viewport when the user resizes the window
		_resizeListener = new Listener() {

			@Override
			public void handleEvent(final Event event) {
				reshapeChart(chart);
			}
		};

		// draw the triangle when the OS tells us that any part of the window
		// needs drawing
		_paintListener = new PaintListener() {
			@Override
			public void paintControl(final PaintEvent paintevent) {
				glRepaint();
			}
		};
		addPaintListener(_paintListener);
		addListener(SWT.Resize, _resizeListener);

		reshapeChart(chart);
	}

	/**
	 * @param _glChart
	 */
	public void glRepaint() {
		if (!isDisposed() && isVisible()) {
			setCurrent();
			final Rectangle clientArea = getClientArea();
			_glContext.makeCurrent();
			GL2 gl2 = _glContext.getGL().getGL2();
			_glChart.display(gl2, clientArea.width, clientArea.height);
			swapBuffers();
			_glContext.release();
		}
	}

	/**
	 * @param chart
	 */
	void reshapeChart(final GLChart chart) {
		final Rectangle clientArea = getClientArea();
		setCurrent();
		_glContext.makeCurrent();
		chart.reshape(_glContext.getGL().getGL2(), clientArea.width,
				clientArea.height);
		_glContext.release();
	}

	private void destroyContext() {
		setCurrent();
		_glContext.makeCurrent();
		_glContext.release();
		_glContext.destroy();
	}
}
