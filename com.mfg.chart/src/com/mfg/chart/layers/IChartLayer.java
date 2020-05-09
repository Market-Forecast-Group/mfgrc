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

import org.eclipse.jface.preference.IPreferenceStore;
import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.backend.opengl.IGLConstantsMFG;

/**
 * @author arian
 * 
 */
public interface IChartLayer extends IGLConstantsMFG {

	/**
	 * Update the data of the layer.
	 */
	public abstract void updateDataset();

	/**
	 * Clear the datasets
	 */
	public void clearDatasets();

	/**
	 * The autorange-dataset is used by the chart to perform autorange. All the
	 * points (in the current time-range) of the returned dataset will be showed
	 * in the screen.
	 * 
	 * @return
	 */
	public abstract IDataset getAutorangeDataset();

	/**
	 * Get the layer visibility
	 * 
	 * @return is visible?
	 */
	public abstract boolean isVisible();

	/**
	 * Set the layer visibility.
	 * 
	 * @param visible
	 *            is visible?
	 */
	public abstract void setVisible(boolean visible);

	/**
	 * The owner chart.
	 * 
	 * @return the chart
	 */
	public abstract Chart getChart();

	/**
	 * The layer name. Commonly used in the chart menu.
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Performs the layer autorange
	 */
	public abstract void autorange();

	/**
	 * You can enable or disable a layer. Disabled layers will not be showed and
	 * will not be affected for the chart menu.
	 * 
	 * @return
	 */
	public boolean isEnabled();

	/**
	 * Enable or disable the layer.
	 * 
	 * @see #isEnabled()
	 * @param enabled
	 */
	public void setEnabled(boolean enabled);

	public void updatePreferences(IPreferenceStore store);
}
