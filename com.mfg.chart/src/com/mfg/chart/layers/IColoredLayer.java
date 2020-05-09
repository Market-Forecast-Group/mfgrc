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

/**
 * A layer with color.
 *
 * @author arian
 *
 */
public interface IColoredLayer extends IChartLayer {
	public float[] getLayerColor();

	public float[] getDefaultLayerColor();

	public void setLayerColor(float[] color);
}
