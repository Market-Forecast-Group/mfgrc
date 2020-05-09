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

package com.mfg.chart.ui.osd;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.IChartLayer;
import com.mfg.chart.layers.IIconifiedLayer;
import com.mfg.chart.layers.IStippledLayer;
import com.mfg.chart.layers.MergedLayer;

/**
 * @author arian
 * 
 */
public class GlobalScaleElementLayer extends MergedLayer<IChartLayer> implements
		IIconifiedLayer, IStippledLayer {

	/**
	 * @param name
	 * @param chart
	 */
	public GlobalScaleElementLayer(final String name,
			final Chart chart) {
		super(name, chart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IIconifiedLayer#getIconBitmap()
	 */
	@Override
	public byte[] getIconBitmap() {
		final IChartLayer first = getLayers().getFirst();
		if (first instanceof IIconifiedLayer) {
			return ((IIconifiedLayer) first).getIconBitmap();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IIconifiedLayer#getIconWidth()
	 */
	@Override
	public int getIconWidth() {
		return 16;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IIconifiedLayer#getIconHeight()
	 */
	@Override
	public int getIconHeight() {
		return 16;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#getLayerStippleFactor()
	 */
	@Override
	public int getLayerStippleFactor() {
		for (IChartLayer layer : getLayers()) {
			return ((IStippledLayer) layer).getLayerStippleFactor();
		}
		return STIPPLE_FACTOR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#setLayerStippleFactor(int)
	 */
	@Override
	public void setLayerStippleFactor(int factor) {
		for (IChartLayer layer : getLayers()) {
			((IStippledLayer) layer).setLayerStippleFactor(factor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#getDefaultLayerStippleFactor()
	 */
	@Override
	public int getDefaultLayerStippleFactor() {
		for (IChartLayer layer : getLayers()) {
			return ((IStippledLayer) layer).getDefaultLayerStippleFactor();
		}
		return STIPPLE_FACTOR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#getLayerWidth()
	 */
	@Override
	public float getLayerWidth() {
		for (IChartLayer layer : getLayers()) {
			return ((IStippledLayer) layer).getLayerWidth();
		}
		return getDefaultLayerWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#setLayerWidth(float)
	 */
	@Override
	public void setLayerWidth(float width) {
		for (IChartLayer layer : getLayers()) {
			((IStippledLayer) layer).setLayerWidth(width);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#getDefaultLayerWidth()
	 */
	@Override
	public float getDefaultLayerWidth() {
		return 1.5f;
	}

	/**
	 * @param visible
	 */
	public void setVisibleByUser(boolean visible) {
		for (IChartLayer layer : layers) {
			layer.setVisible(visible);
		}

	}
}
