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

import com.mfg.chart.layers.IChartLayer;

abstract class AbstractLayerModel {
	private final IChartLayer layer;


	public AbstractLayerModel(final IChartLayer layer1) {
		this.layer = layer1;
	}


	/**
	 * @return the layer
	 */
	public IChartLayer getLayer() {
		return layer;
	}

}