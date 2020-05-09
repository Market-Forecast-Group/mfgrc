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

import org.mfg.opengl.widgets.IGWColorModel;

import com.mfg.chart.layers.IColoredLayer;

class LayerVisibilityColorModel extends AbstractLayerModel implements IGWColorModel {

	private final boolean showScaleColor;


	/**
	 * @param layer
	 */
	public LayerVisibilityColorModel(final IColoredLayer layer, final boolean showScaleColor1) {
		super(layer);
		this.showScaleColor = showScaleColor1;
	}


	public LayerVisibilityColorModel(final IColoredLayer layer) {
		this(layer, true);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.mfg.chart.ui.osd.AbstractLayerModel#getLayer()
	 */
	@Override
	public IColoredLayer getLayer() {
		return (IColoredLayer) super.getLayer();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWColorModel#getBackground()
	 */
	@Override
	public float[] getBackground() {
		return COLOR_BLACK;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWColorModel#getForeground()
	 */
	@Override
	public float[] getForeground() {
		final IColoredLayer layer = getLayer();
		return layer.isEnabled() ? (showScaleColor ? layer.getLayerColor() : COLOR_GRAY) : COLOR_DARK_GRAY;
	}
}
