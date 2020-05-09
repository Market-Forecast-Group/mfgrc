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

import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.IChartUtils;

/**
 * @author arian
 * 
 */
public class MergedLayer<T extends IChartLayer> extends AbstractLayer {
	protected final LinkedList<T> layers;
	private final LinkedHashMap<String, T> layersMap;

	public MergedLayer(final String name, final Chart chart) {
		super(name, chart);
		layers = new LinkedList<>();
		layersMap = new LinkedHashMap<>();
	}

	public void addLayer(final T layer) {
		layers.add(layer);
		layersMap.put(layer.getName(), layer);
	}

	/**
	 * @return the layers
	 */
	public LinkedList<T> getLayers() {
		return layers;
	}

	public T getLayer(final String name) {
		return layersMap.get(name);
	}

	public boolean containsLayer(final String name) {
		return layersMap.containsKey(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#update()
	 */
	@Override
	public void updateDataset() {
		for (final IChartLayer layer : layers) {
			if (layer.isEnabled()) {
				layer.updateDataset();
			}
		}
	}

	@Override
	public void clearDatasets() {
		for (final IChartLayer layer : layers) {
			if (layer.isEnabled()) {
				layer.clearDatasets();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#getAutorangeDataset()
	 */
	@Override
	public IDataset getAutorangeDataset() {
		return IChartUtils.EMPTY_DATASET;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractLayer#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		for (final IChartLayer layer : layers) {
			if (layer.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.AbstractLayer#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		for (final IChartLayer layer : layers) {
			layer.setEnabled(enabled);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#isVisible()
	 */
	@Override
	public boolean isVisible() {
		for (final IChartLayer layer : layers) {
			if (layer.isVisible() && layer.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean visible) {
		if (isEnabled()) {
			for (final IChartLayer layer : layers) {
				layer.setVisible(visible);
			}
		}
	}

	@Override
	public String getLayerPreferenceKey() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.layers.IChartLayer#updatePreferences(org.eclipse.jface.
	 * preference.IPreferenceStore)
	 */
	@Override
	public void updatePreferences(IPreferenceStore store) {
		for (IChartLayer layer : layers) {
			layer.updatePreferences(store);
		}
	}
}
