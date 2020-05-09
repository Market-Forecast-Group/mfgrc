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

package com.mfg.chart.layers;

import org.mfg.opengl.IGLConstants;
import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.backend.opengl.Chart;

/**
 * A final layer has certain behavior and features:
 * <ul>
 * <li>It has a color</li>
 * <li>It has datasets. So you must to implements the method
 * {@link #clearDatasets()} used for hide the layer</li>
 * <li>Implementors of the method {@link #updateDataset()} just need fill the
 * datasets</li>
 * <li>When the layer is enabled, the filter is enabled too</li>
 * <ul/>
 * 
 * @author arian
 * 
 */
public abstract class FinalLayer extends AbstractLayer implements
		IColoredLayer, IStippledLayer, IIconifiedLayer {

	public static final String PREF_LAYER_STIPPLE_FACTOR = ".stippleFactor";
	public static final String PREF_LAYER_STIPPLE_PATTERN = ".stipplePattern";
	public static final String PREF_LAYER_COLOR = ".color";

	private final int _iconWidth;
	private final int _iconHeight;
	private final byte[] _iconBmp;
	private float[] _layerColor;
	private int _stippleFactor;
	private float _width;

	public FinalLayer(final String name, final Chart chart) {
		this(name, chart, null, 0, 0);
	}

	public FinalLayer(final String name, final Chart chart,
			final byte[] iconBmp, final int iconWidth, final int iconHeight) {
		super(name, chart);
		this._iconBmp = iconBmp;
		this._iconWidth = iconWidth;
		this._iconHeight = iconHeight;
		_layerColor = IGLConstants.COLOR_BLUE;
		_stippleFactor = getDefaultLayerStippleFactor();
		_width = getDefaultLayerWidth();
	}

	public FinalLayer(final String name, final Chart chart,
			final byte[] iconBmp_16x16) {
		this(name, chart, iconBmp_16x16, 16, 16);
	}

	/**
	 * Fill the datasets
	 */
	@Override
	public abstract void updateDataset();

	@Override
	public abstract IDataset getAutorangeDataset();

	@Override
	public byte[] getIconBitmap() {
		return _iconBmp;
	}

	@Override
	public int getIconWidth() {
		return _iconWidth;
	}

	@Override
	public int getIconHeight() {
		return _iconHeight;
	}

	@Override
	public float[] getLayerColor() {
		return _layerColor;
	}

	@Override
	public void setLayerColor(final float[] color) {
		_layerColor = color;
	}

	@Override
	public abstract float[] getDefaultLayerColor();

	@Override
	public int getLayerStippleFactor() {
		return _stippleFactor;
	}

	@Override
	public void setLayerStippleFactor(int factor) {
		_stippleFactor = factor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#getLayerWidth()
	 */
	@Override
	public float getLayerWidth() {
		return _width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IStippledLayer#setLayerWidth(float)
	 */
	@Override
	public void setLayerWidth(float width) {
		this._width = width;
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
	 * If it is set to visible, the method {@link #updateDataset()} will be
	 * called, else {@link #clearDatasets()}. If the layer is not enabled the
	 * method does not do anything.
	 */
	@Override
	public void setVisible(final boolean visible) {
		setVisible(visible, true);
	}

	public void setVisible(boolean visible, boolean updateDatasets) {
		if (updateDatasets) {
			if (isEnabled()) {
				super.setVisible(visible);

				if (visible) {
					updateDataset();
				} else {
					clearDatasets();
				}
			}
		} else {
			if (isEnabled()) {
				super.setVisible(visible);
			}
		}
	}

	@Override
	public String getLayerPreferenceKey() {
		return getName();
	}

}
