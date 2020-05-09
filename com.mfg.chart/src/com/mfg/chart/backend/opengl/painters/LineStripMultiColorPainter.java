/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 *
 * @version $Revision$: $Date$:
 * $Id$:
 */

package com.mfg.chart.backend.opengl.painters;

import static javax.media.opengl.GL.GL_LINE_STRIP;

import javax.media.opengl.GL2;

import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

/**
 * @author arian
 * 
 */
public class LineStripMultiColorPainter implements ISeriesPainter {
	private short stipple;
	private int factor;

	public LineStripMultiColorPainter(final int factor1, final short stipple1) {
		this.stipple = stipple1;
		this.factor = factor1;
	}

	public LineStripMultiColorPainter() {
		this(STIPPLE_FACTOR_NULL, STIPPLE_PATTERN);
	}

	/**
	 * @param series
	 * @param item
	 * @return
	 */
	@SuppressWarnings("static-method")
	// Overloaded on inner classes.
	public float[] getColor(final int series, final int item) {
		return COLOR_WHITE;
	}

	/**
	 * @return the stipple
	 */
	public short getStipple() {
		return stipple;
	}

	/**
	 * @param stipple1
	 *            the stipple to set
	 */
	public void setStipple(final short stipple1) {
		this.stipple = stipple1;
	}

	/**
	 * @return the stippleFactor
	 */
	public int getFactor() {
		return factor;
	}

	/**
	 * @param stippleFactor
	 *            the stippleFactor to set
	 */
	public void setFactor(final int stippleFactor) {
		factor = stippleFactor;
	}

	public void setStipple(final int factor1, final short stipple1) {
		setStipple(stipple1);
		setFactor(factor1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.chart.ISeriesPainter#paint(javax.media.opengl.GL,
	 * org.mfg.opengl.chart.IDataset, org.mfg.opengl.chart.PlotRange,
	 * org.mfg.opengl.chart.PlotRange)
	 */
	@Override
	public void paint(final GL2 gl, final IDataset ds, final PlotRange xrange,
			final PlotRange yrange) {
		final boolean isStipple = factor != STIPPLE_FACTOR_NULL;

		if (isStipple) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glLineStipple(factor, stipple);
		}

		for (int series = 0; series < ds.getSeriesCount(); series++) {

			gl.glBegin(GL_LINE_STRIP);

			for (int item = 0; item < ds.getItemCount(series); item++) {

				gl.glColor4fv(getColor(series, item), 0);

				final double x = ds.getX(series, item);
				final double y = ds.getY(series, item);

				gl.glVertex2d(x, y);
			}
			gl.glEnd();
		}
		if (isStipple) {
			gl.glDisable(GL2.GL_LINE_STIPPLE);
		}
	}

}
