/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */

package com.mfg.plstats.charts;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.IChartModel;
import com.mfg.chart.ui.ChartType;
import com.mfg.chart.ui.views.AbstractRTChartView;
import com.mfg.interfaces.symbols.IIndicatorConfiguration;
import com.mfg.plstats.PLStatsPlugin;
import com.mfg.plstats.persist.PLStatsIndicatorConfiguration;
import com.mfg.plstats.persist.PLStatsIndicatorStorage;
import com.mfg.plstats.ui.actions.OpenIndicatorChartAction;

/**
 * @author arian
 * 
 */
public class IndicatorChartView extends AbstractRTChartView {
	public static final String VIEW_ID = "com.mfg.plstats.charts.IndicatorChartView";
	private final static String MEMENTO_CONFIGURATION_KEY = "configurationKey";

	private IIndicatorConfiguration configuration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.RTChartView#getChartType()
	 */
	@Override
	protected ChartType getChartType() {
		return ChartType.INDICATOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.views.RTChartView#createPartControl(org.eclipse.swt.
	 * widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		setPartName("Indicator Chart (Unbounded)");
		PLStatsPlugin.getDefault().getIndicatorManager().addChartView(this);
		if (configuration != null) {
			setConfiguration(configuration);

			if (!restoreInitialChartValues()) {
				getChart().zoomOutAll(true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.views.RTChartView#fillMenuBar(org.eclipse.jface.action
	 * .IMenuManager)
	 */
	@Override
	protected void fillMenuBar(IMenuManager manager) {
		super.fillMenuBar(manager);
		manager.add(new Separator());
		OpenIndicatorChartAction action = new OpenIndicatorChartAction(
				configuration) {
			{
				setText("Open New Chart");
			}

			@Override
			public IIndicatorConfiguration getConfiguration() {
				return IndicatorChartView.this.getConfiguration();
			}
		};
		manager.add(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		PLStatsPlugin.getDefault().getIndicatorManager().removeChartView(this);
		super.dispose();
	}

	public void setConfiguration(IIndicatorConfiguration newConfiguration) {
		setPartName("Indicator Chart "
				+ (newConfiguration == null ? "(Unbounded)" : "- "
						+ newConfiguration.getName()));

		if (newConfiguration == null) {
			close();
		}

		configuration = newConfiguration;

		IChartModel model = configuration == null ? IChartModel.EMPTY
				: createModel();

		setChart(new Chart(getPartName(), model,
				ChartType.INDICATOR, newConfiguration));

		if (newConfiguration != null && !isChartThreadRunning()) {
			restartChartThread();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.ChartView#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		memento.putString(MEMENTO_CONFIGURATION_KEY,
				getConfiguration() == null ? null : getConfiguration()
						.getUUID().toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.views.ChartView#init(org.eclipse.ui.IViewSite,
	 * org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		if (memento != null) {
			String uuid = memento.getString(MEMENTO_CONFIGURATION_KEY);
			PLStatsIndicatorStorage storage = PLStatsPlugin.getDefault()
					.getIndicatorStorage();
			PLStatsIndicatorConfiguration config = storage.findById(uuid);
			configuration = config.getIndicator();
		}
	}

	/**
	 * @return the configuration
	 */
	public IIndicatorConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @return
	 */
	private IChartModel createModel() {
		IChartModel model = PLStatsPlugin.getDefault().getIndicatorManager()
				.createModel(configuration);
		return model;
	}
}
