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

import org.mfg.opengl.widgets.IGWSelectionModel;

import com.mfg.chart.layers.IChartLayer;

/**
 * @author arian
 *
 */
public class LayerEnabledSelectionModel extends AbstractLayerModel implements IGWSelectionModel {

	/**
	 * @param layer
	 */
	public LayerEnabledSelectionModel(final IChartLayer layer) {
		super(layer);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWSelectionModel#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return getLayer().isEnabled();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see org.mfg.opengl.widgets.IGWSelectionModel#setSelected(boolean)
	 */
	@Override
	public void setSelected(final boolean selected) {
		final IChartLayer layer = getLayer();
		layer.setEnabled(selected);
		layer.getChart().fireRangeChanged();
	}

}
