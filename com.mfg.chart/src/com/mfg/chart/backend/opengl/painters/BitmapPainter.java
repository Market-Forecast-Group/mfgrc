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

import com.mfg.chart.layers.IColoredLayer;

/**
 * @author arian
 * 
 */
public class BitmapPainter implements ISeriesPainter {

	private final BitmapData _data;
	private final IColoredLayer _layer;
	private float[] color;

	public BitmapPainter(IColoredLayer layer1, final BitmapData bmp) {
		super();
		_data = bmp;
		this._layer = layer1;
	}

	public BitmapPainter(float[] color1, final BitmapData bmp) {
		this.color = color1;
		_data = bmp;
		_layer = null;
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
		for (int series = 0; series < ds.getSeriesCount(); series++) {
			for (int item = 0; item < ds.getItemCount(series); item++) {
				float[] c = getColor(series, item);
				gl.glColor4fv(c, 0);

				final double x = ds.getX(series, item);
				final double y = ds.getY(series, item);
				gl.glRasterPos2d(x, y);
				BitmapData data = getBitmap();
				gl.glBitmap(data.width, data.height, data.x, data.y, 0, 0,
						data.bitmap, 0);
			}
		}
	}

	protected BitmapData getBitmap() {
		return _data;
	}

	@SuppressWarnings("unused")
	protected float[] getColor(int series, int item) {
		return color == null ? _layer.getLayerColor() : color;
	}
}
