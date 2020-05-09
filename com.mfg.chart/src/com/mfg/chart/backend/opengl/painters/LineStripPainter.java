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

package com.mfg.chart.backend.opengl.painters;

import static javax.media.opengl.GL.GL_LINE_STRIP;

import javax.media.opengl.GL2;

import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.layers.FinalLayer;

/**
 * @author arian
 * 
 */
public class LineStripPainter implements ISeriesPainter {
	private final FinalLayer layer;

	public LineStripPainter(FinalLayer layer1) {
		this.layer = layer1;
	}

	/**
	 * @param series 
	 * @return the stippleFactor
	 */
	public int getFactor(int series) {
		return layer.getLayerStippleFactor();
	}

	/**
	 * @return the color
	 */
	public float[] getColor() {
		return layer.getLayerColor();
	}

	/**
	 * @param series  
	 */
	public float getWidth(int series) {
		return layer.getLayerWidth();
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

		gl.glColor4fv(getColor(), 0);

		for (int series = 0; series < ds.getSeriesCount(); series++) {
			int factor = getFactor(series);

			final boolean isStipple = factor != STIPPLE_FACTOR_NULL;

			gl.glPushAttrib(GL2.GL_LINE_BIT);
			gl.glLineWidth(getWidth(series));

			if (isStipple) {
				gl.glEnable(GL2.GL_LINE_STIPPLE);
				gl.glLineStipple(factor, STIPPLE_PATTERN);
			}

			gl.glBegin(GL_LINE_STRIP);

			for (int item = 0; item < ds.getItemCount(series); item++) {
				final double x = ds.getX(series, item);
				final double y = ds.getY(series, item);

				gl.glVertex2d(x, y);
			}
			gl.glEnd();

			gl.glPopAttrib();

			if (isStipple) {
				gl.glDisable(GL2.GL_LINE_STIPPLE);
			}

		}

	}
}
