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

package com.mfg.chart.layers;

import org.mfg.opengl.BitmapData;

public abstract class FinalScaleElementLayer extends FinalLayer implements
		IElementScaleLayer {

	private final ScaleLayer scale;
	private final int level;
	private final String _key;

	public FinalScaleElementLayer(final String name, String key,
			final ScaleLayer scale1, BitmapData iconBmp_16x16) {
		this(name, key, scale1, iconBmp_16x16.bitmap);
	}

	public FinalScaleElementLayer(final String name, String key,
			final ScaleLayer scale1, final byte[] iconBmp_16x16) {
		super(name, scale1.getChart(), iconBmp_16x16);
		this.scale = scale1;
		level = scale1.getLevel();
		_key = key;
	}

	public FinalScaleElementLayer(final String name, String key,
			final ScaleLayer scale1) {
		this(name, key, scale1, (byte[]) null);
	}

	public String getKey() {
		return _key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractLayer#setVisibleOnLoadProfile(boolean)
	 */
	@Override
	protected void setVisibleOnLoadProfile(boolean visible) {
		super.setVisible(visible, false);
	}

	@Override
	public void setLayerColor(final float[] color) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float[] getLayerColor() {
		return getScale().getLayerColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IColoredLayer#getDefaultLayerColor()
	 */
	@Override
	public float[] getDefaultLayerColor() {
		return getScale().getDefaultLayerColor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#getDefaultStippleFactor()
	 */
	@Override
	public int getDefaultLayerStippleFactor() {
		return STIPPLE_FACTOR_NULL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IScaleLayer#getLevel()
	 */
	@Override
	public int getLevel() {
		return level;
	}

	/**
	 * @return the scale
	 */
	@Override
	public ScaleLayer getScale() {
		return scale;
	}

	@Override
	public String getLayerPreferenceKey() {
		return super.getLayerPreferenceKey() + ".level" + getScale().getLevel();
	}
}
