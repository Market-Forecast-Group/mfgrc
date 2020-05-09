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

import com.mfg.chart.ui.IChartUtils;

/**
 * @author arian
 * 
 */
public interface IElementScaleLayer extends IColoredLayer, IIconifiedLayer, IChartUtils {
	public int getLevel();

	public ScaleLayer getScale();
}
