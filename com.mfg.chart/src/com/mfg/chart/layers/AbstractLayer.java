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

import org.eclipse.jface.preference.IPreferenceStore;
import org.mfg.opengl.chart.IDataset;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.IChartUtils;

public abstract class AbstractLayer implements IChartLayer {
	public static final String PREF_VISIBLE = ".visible";
	public static final String PREF_ENABLED = ".enabled";

	protected Chart _chart;
	private boolean _visible;
	private final String _name;
	private boolean _enabled;

	public AbstractLayer(final String name, final Chart chart) {
		this._chart = chart;
		_visible = true;
		this._name = name;
		_enabled = true;
	}

	/**
	 * @return the enabled
	 */
	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		if (!enabled) {
			setVisible(false);
		}
		this._enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#update()
	 */
	@Override
	public abstract void updateDataset();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#getAutorangeDataset()
	 */
	@Override
	public abstract IDataset getAutorangeDataset();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return _visible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean visible) {
		if (isEnabled()) {
			this._visible = visible;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#getChart()
	 */
	@Override
	public Chart getChart() {
		return _chart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#getName()
	 */
	@Override
	public String getName() {
		return _name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.layers.IChartLayer#autorange()
	 */
	@Override
	public void autorange() {
		IChartUtils.autorange(_chart, getAutorangeDataset());
	}

	/**
	 * <p>
	 * This method is created to allow implementers of this class a more
	 * detailed behavior of the visibility of the layer when it is updated
	 * because a profile update.
	 * </p>
	 * 
	 * @param visible
	 */
	protected void setVisibleOnLoadProfile(boolean visible) {
		setVisible(visible);
	}

	public abstract String getLayerPreferenceKey();

	@Override
	public void updatePreferences(IPreferenceStore store) {
		//
	}
}
