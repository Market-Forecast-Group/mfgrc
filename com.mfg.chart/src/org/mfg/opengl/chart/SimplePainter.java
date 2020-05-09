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

import static javax.media.opengl.GL.GL_LINE_STRIP;

import javax.media.opengl.GL2;

public class SimplePainter implements ISeriesPainter {

	private final float[] color;

	public SimplePainter(final float[] color1) {
		this.color = color1;
	}

	/**
	 * @param series
	 */
	public float[] getColor(final int series) {
		return color;
	}

	@Override
	public void paint(final GL2 gl, final IDataset ds, final PlotRange xrange,
			final PlotRange yrange) {

		gl.glPushAttrib(GL2.GL_LINE_BIT);

		gl.glLineWidth(getLineWidth());

		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glLineStipple(getStippleFactor(), STIPPLE_PATTERN);

		for (int series = 0; series < ds.getSeriesCount(); series++) {

			gl.glBegin(GL_LINE_STRIP);
			gl.glColor4fv(getColor(series), 0);

			for (int item = 0; item < ds.getItemCount(series); item++) {
				final double x = ds.getX(series, item);
				final double y = ds.getY(series, item);

				gl.glVertex2d(x, y);
			}
			gl.glEnd();
		}
		
		gl.glDisable(GL2.GL_LINE_STIPPLE);
		gl.glPopAttrib();
	}

	@SuppressWarnings("static-method")
	public float getLineWidth() {
		return 1;
	}

	@SuppressWarnings("static-method")
	public int getStippleFactor() {
		return STIPPLE_FACTOR_NULL;
	}
}
