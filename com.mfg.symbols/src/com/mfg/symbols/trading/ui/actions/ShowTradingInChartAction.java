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
package com.mfg.symbols.trading.ui.actions;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.mfg.chart.ui.actions.ShowObjectInChart;
import com.mfg.chart.ui.views.ChartView;
import com.mfg.symbols.trading.configurations.TradingConfiguration;
import com.mfg.symbols.trading.ui.adapters.ChartViewToITradingViewAdapter;
import com.mfg.symbols.trading.ui.adapters.TradingChartAdapater;
import com.mfg.utils.PartUtils;

/**
 * @author arian
 * 
 */
public class ShowTradingInChartAction extends ShowObjectInChart {
	TradingConfiguration configuration;

	public ShowTradingInChartAction(TradingConfiguration aConfiguration) {
		super(aConfiguration);
		this.configuration = aConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.chart.ui.actions.ShowObjectInChart#getObject()
	 */
	@Override
	public Object getObject() {
		return getConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		TradingConfiguration config = getConfiguration();
		if (config != null) {
			List<ChartView> views = PartUtils.getOpenViews(ChartView.VIEW_ID);
			ChartView viewToOpen = null;

			// activate a chart with same configuration set
			for (ChartView view : views) {
				Integer set = ChartViewToITradingViewAdapter
						.getChartViewConfigurationSet(view);
				int thisSet = config.getInfo().getConfigurationSet();
				// match the same configuration set even if the chart is empty
				boolean match1 = set != null && set.intValue() == thisSet;
				// match the chart configuration set with this configuration set
				boolean match2 = view.getContent() instanceof TradingConfiguration
						&& ((TradingConfiguration) view.getContent()).getInfo()
								.getConfigurationSet() == thisSet;
				if (match1 || match2) {
					viewToOpen = view;
					viewToOpen.setContent(config);
					PartUtils.activatePart(viewToOpen);
				}
			}

			if (viewToOpen == null) {
				// activate chart with empty content
				for (ChartView view : views) {
					if (view.getContent() == null) {
						viewToOpen = view;
						PartUtils.activatePart(viewToOpen);
						viewToOpen.setContent(config);
						return;
					}
				}

				// open new view
				ChartView view = PartUtils.openView(ChartView.VIEW_ID, true);
				if (view != null) {
					view.setContent(config);
				}
			}
		}
	}

	/**
	 * @return the configuration
	 */
	public TradingConfiguration getConfiguration() {
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.chart.ui.actions.ShowObjectInChart#fillMenu(org.eclipse.swt.widgets
	 * .Menu)
	 */
	@Override
	protected Menu fillMenu(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Show in Equity Chart");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TradingConfiguration config = getConfiguration();
				List<ChartView> views = PartUtils
						.getOpenViews(ChartView.VIEW_ID);
				ChartView viewToOpen = null;
				// check for an empty chart
				for (ChartView view : views) {
					if (view.getContent() == null) {
						PartUtils.activatePart(view);
						view.setContent(config);
						viewToOpen = view;
						break;
					}
				}
				if (viewToOpen == null) {
					ChartView view = PartUtils
							.openView(ChartView.VIEW_ID, true);
					if (view != null) {
						view.setContent(config);
						viewToOpen = view;
					}
				}
				if (viewToOpen != null) {
					TradingChartAdapater adapter = (TradingChartAdapater) viewToOpen
							.getContentAdapter();
					adapter.swapTradingAndEquity(viewToOpen);
				}
			}

		});
		
		super.fillMenu(menu);
		return menu;
	}
}
