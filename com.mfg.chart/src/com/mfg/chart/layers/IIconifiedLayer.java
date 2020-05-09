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
 * @author arian
 * 
 */
public interface IIconifiedLayer extends IChartLayer {
	// TODO: Is better to use a BitmapData object.
	public byte[] getIconBitmap();

	public int getIconWidth();

	public int getIconHeight();
}
