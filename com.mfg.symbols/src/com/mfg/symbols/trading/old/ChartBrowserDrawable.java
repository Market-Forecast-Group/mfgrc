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
 */
package com.mfg.symbols.trading.old;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.mfg.opengl.IGLDrawable;

import com.mfg.chart.backend.opengl.IGLConstantsMFG;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.IChartBrowser;

/**
 * @author arian
 * 
 */
public class ChartBrowserDrawable implements IGLDrawable, IGLConstantsMFG {

	private final IChartBrowser browser;
	private final Chart chart;

	public ChartBrowserDrawable(IChartBrowser aBbrowser, Chart aChart) {
		this.browser = aBbrowser;
		this.chart = aChart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.IGLDrawable#init(org.mfg.opengl.GL2)
	 */
	@Override
	public void init(GL2 gl) {
		//Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.IGLDrawable#reshape(org.mfg.opengl.GL2, int, int)
	 */
	@Override
	public void reshape(GL2 gl, int width, int height) {
		//Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.IGLDrawable#display(org.mfg.opengl.GL2, int, int)
	 */
	@Override
	public void display(GL2 gl, int width, int height) {
		if (browser.isActive()) {
			long time = browser.getCurrentTime();
			double price = browser.getCurrentPrice();

			double w = chart.getXRange().plotWidth(20,
					chart.glChart.plot.screenWidth);
			double h = chart.getYRange().plotWidth(20,
					chart.glChart.plot.screenHeight);
			gl.glColor4fv(COLOR_BLUE, 0);
			gl.glLineWidth(3);
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(STIPPLE_FACTOR_3, STIPPLE_PATTERN);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d(time - w, price - h);
			gl.glVertex2d(time + w, price - h);
			gl.glVertex2d(time + w, price + h);
			gl.glVertex2d(time - w, price + h);
			gl.glEnd();
			gl.glDisable(GL2.GL_LINE_STIPPLE);
			gl.glLineWidth(1.5f);
		}
	}

}
