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

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.layers.IChartLayer;
import com.mfg.chart.layers.IElementScaleLayer;
import com.mfg.chart.layers.ScaleLayer;

class LayerVisibilitySelectionModel extends AbstractLayerModel implements
		IGWSelectionModel {

	/**
	 * @param layer
	 */
	public LayerVisibilitySelectionModel(final IChartLayer layer) {
		super(layer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWSelectionModel#isSelected()
	 */
	@Override
	public boolean isSelected() {
		return getLayer().isVisible();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWSelectionModel#setSelected(boolean)
	 */
	@Override
	public void setSelected(final boolean selected) {
		final IChartLayer layer = getLayer();
		if (layer instanceof IElementScaleLayer) {
			layer.setVisible(selected);
		} else if (layer instanceof ScaleLayer) {
			if (selected) {
				// layer.restoreDefaults();
			} else {
				((ScaleLayer) layer).setVisible(false);
			}
		}
		Chart chart = layer.getChart();
		chart.getIndicatorLayer().setVisibleByUser(layer, selected);
		chart.update(chart.isAutoRangeEnabled());
	}
}
