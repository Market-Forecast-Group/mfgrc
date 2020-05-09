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

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.mfg.opengl.chart.GLChartCanvas;

import com.mfg.chart.ui.IChartUtils;
import com.mfg.chart.ui.IChartCanvas;
import com.mfg.chart.ui.SWTComposite_PriceChart_Connection;

public class PriceChartCanvas_OpenGL extends GLChartCanvas implements
		IChartCanvas {

	SWTComposite_PriceChart_Connection _connection;
	Chart _chart;

	public PriceChartCanvas_OpenGL(final Composite parent, final GLData glData,
			final Chart chart) {
		super(parent, glData, chart.getGLChart());
		chart.setCanvas(this);
		_connection = IChartUtils.connect_SWTComposite_PriceChart(this, chart);

		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				_connection.close();
			}
		});

		DefaultToolTip tooltip = new DefaultToolTip(this) {

			@Override
			protected boolean shouldCreateToolTip(Event event) {
				boolean b = getText(event) != null;
				return b;
			}

			@Override
			protected String getText(Event event) {
				int y = ((Composite) event.widget).getBounds().height - event.y;
				String text = _chart == null ? null : _chart.getTooltip(
						event.x, y);
				return text;
			}
		};
		tooltip.setShift(new Point(0, 20));
	}

	@Override
	public void repaintCanvas() {
		glRepaint();
	}

	public void setChart(final Chart chart) {
		final PriceChartCanvas_OpenGL thisObject = this;

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				_chart = chart;
				_connection.close();
				chart.setCanvas(thisObject);
				replaceChart(getGLData(), chart.getGLChart());
				_connection = IChartUtils.connect_SWTComposite_PriceChart(
						thisObject, chart);

			}

		});

	}

	/**
	 * @return the chart
	 */
	public Chart getChart() {
		return _chart;
	}
	
	public SWTComposite_PriceChart_Connection getConnection() {
		return _connection;
	}
}
