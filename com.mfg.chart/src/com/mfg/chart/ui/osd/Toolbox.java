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
package com.mfg.chart.ui.osd;

import org.mfg.opengl.widgets.GWBitmapButton;
import org.mfg.opengl.widgets.GWHelper;
import org.mfg.opengl.widgets.GWHorizontalLayout;
import org.mfg.opengl.widgets.GWidget;
import org.mfg.opengl.widgets.IGWSelectionModel;

import com.mfg.chart.backend.opengl.IGLConstantsMFG;
import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.AutoRangeType;

/**
 * @author arian
 * 
 */
public class Toolbox extends GWidget implements IGLConstantsMFG {

	private final Chart chart;

	/**
	 * @param parent
	 * @param aChart
	 */
	public Toolbox(GWidget parent, final Chart aChart) {
		super(parent);
		this.chart = aChart;
		getBorder().reset(5, 5, 5, 5);
		getBorder().setVisible(false);

		setLayout(new GWHorizontalLayout(5, false));
		GWBitmapButton autoRangeButton = new GWBitmapButton(this,
				BITMAP_AUTORANGE, new IGWSelectionModel() {

					@Override
					public void setSelected(boolean selected) {
						if (selected) {
							aChart.update(true);
						}
					}

					@Override
					public boolean isSelected() {
						return aChart.isAutoRangeEnabled();
					}

				}) {
			@Override
			public String getTooltip() {
				return (isSelected() ? "Disable" : "Enable")
						+ " autorange (Alt+A)";
			}
		};
		GWHelper.setBorderToHideWhenButtonIsNotSelected(autoRangeButton);

		if (aChart.getType().hasProbs()) {
			GWBitmapButton autoRangeWithPricesButton = new GWBitmapButton(this,
					BITMAP_PRICES, new IGWSelectionModel() {

						@Override
						public void setSelected(boolean selected) {
							setAutorangeType(AutoRangeType.AUTORANGE_PRICES);
						}

						@Override
						public boolean isSelected() {
							return aChart.getAutoRangeType() == AutoRangeType.AUTORANGE_PRICES;
						}
					}) {
				@Override
				public String getTooltip() {
					return (isSelected() ? "Disable" : "Enable")
							+ " autorange with Prices (X)";
				}
			};
			GWBitmapButton autoRangeWithProbsButton = new GWBitmapButton(this,
					BITMAP_PROBS, new IGWSelectionModel() {

						@Override
						public void setSelected(boolean selected) {
							setAutorangeType(AutoRangeType.AUTORANGE_PROBS);
						}

						@Override
						public boolean isSelected() {
							return aChart.getAutoRangeType() == AutoRangeType.AUTORANGE_PROBS;
						}
					}) {
				@Override
				public String getTooltip() {
					return (isSelected() ? "Disable" : "Enable")
							+ " autorange with Probabilities (X)";
				}
			};
			GWHelper.setBorderToHideWhenButtonIsNotSelected(
					autoRangeWithPricesButton, autoRangeWithProbsButton);
		}

	}

	void setAutorangeType(AutoRangeType type) {
		chart.getMainSettings().autoRangeType = type;
		chart.setAutoRangeEnabled(true);
		chart.update(true);
	}
}
