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

import javax.media.opengl.GL2;

import org.mfg.opengl.BitmapData;
import org.mfg.opengl.chart.IDataset;
import org.mfg.opengl.chart.ISeriesPainter;
import org.mfg.opengl.chart.PlotRange;

import com.mfg.chart.backend.opengl.IGLConstantsMFG;

/**
 * @author arian
 *
 */
public abstract class AbstractBitmapPainter implements ISeriesPainter,
		IGLConstantsMFG {

	private final float[] color;

	public AbstractBitmapPainter(final float[] color1) {
		this.color = color1;
	}

	public abstract BitmapData getBitmap(IDataset ds, int series, int item);

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.chart.ISeriesPainter#paint(javax.media.opengl.GL,
	 * org.mfg.opengl.chart.IDataset, org.mfg.opengl.chart.PlotRange,
	 * org.mfg.opengl.chart.PlotRange)
	 */
	@Override
	public void paint(final GL2 gl, final IDataset ds, final PlotRange xrange, final PlotRange yrange) {
		gl.glColor4fv(color, 0);
		for (int series = 0; series < ds.getSeriesCount(); series++) {
			for (int item = 0; item < ds.getItemCount(series); item++) {
				final double x = ds.getX(series, item);
				final double y = ds.getY(series, item);
				final BitmapData data = getBitmap(ds, series, item);
				if (data != null) {
					gl.glRasterPos2d(x, y);
					gl.glBitmap(data.width, data.height, data.x, data.y, 0, 0,
							data.bitmap, 0);
				}
			}
		}
	}

}
