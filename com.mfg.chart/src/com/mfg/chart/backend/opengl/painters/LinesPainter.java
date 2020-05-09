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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.layers.IColoredLayer;
import com.mfg.chart.layers.IStippledLayer;

/**
 * @author arian
 * 
 */
public class LinesPainter implements ISeriesPainter {
	private final IColoredLayer layer;

	public LinesPainter(final IColoredLayer layer1) {
		this.layer = layer1;
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

		int factor = STIPPLE_FACTOR_NULL;

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		if (layer instanceof IStippledLayer) {
			IStippledLayer stippledLayer = (IStippledLayer) layer;
			factor = stippledLayer.getLayerStippleFactor();
			gl.glLineWidth(stippledLayer.getLayerWidth());
		}

		if (factor != STIPPLE_FACTOR_NULL) {
			gl.glEnable(GL2.GL_LINE_STIPPLE);
		}

		for (int series = 0; series < ds.getSeriesCount(); series++) {

			gl.glBegin(GL.GL_LINES);

			gl.glColor4fv(layer.getLayerColor(), 0);

			for (int item = 0; item < ds.getItemCount(series); item++) {
				final double x = ds.getX(series, item);
				final double y = ds.getY(series, item);

				gl.glVertex2d(x, y);
			}
			gl.glEnd();
		}

		gl.glPopAttrib();

		if (factor != STIPPLE_FACTOR_NULL) {
			gl.glDisable(GL2.GL_LINE_STIPPLE);
		}
	}

}
