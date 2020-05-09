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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.mfg.opengl.widgets.GWBitmapButton;
import org.mfg.opengl.widgets.GWButton;
import org.mfg.opengl.widgets.GWHorizontalLayout;
import org.mfg.opengl.widgets.GWLabel;
import org.mfg.opengl.widgets.GWSeparator;
import org.mfg.opengl.widgets.GWidget;
import org.mfg.opengl.widgets.IGWColorModel;
import org.mfg.opengl.widgets.IGWSelectionModel;

import com.jogamp.opengl.util.gl2.GLUT;
import com.mfg.chart.backend.opengl.IGLConstantsMFG;
import com.mfg.chart.layers.IChartLayer;
import com.mfg.chart.layers.IndicatorLayer;
import com.mfg.chart.layers.MergedLayer;
import com.mfg.chart.layers.PriceLayer;
import com.mfg.chart.layers.ScaleLayer;
import com.mfg.chart.backend.opengl.Chart;

/**
 * @author arian
 * 
 */
class GlobalColorModel extends AbstractLayerModel implements IGWColorModel {

	/**
	 * @param layer
	 */
	public GlobalColorModel(final IChartLayer layer) {
		super(layer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWColorModel#getBackground()
	 */
	@Override
	public float[] getBackground() {
		return COLOR_BLACK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mfg.opengl.widgets.IGWColorModel#getForeground()
	 */
	@Override
	public float[] getForeground() {
		return getLayer().isEnabled() ? COLOR_GRAY : COLOR_DARK_GRAY;
	}

}

public class GlobalScaleElementChartMenu extends GWidget implements
		IGLConstantsMFG {

	private final Chart chart;

	/**
	 * @param parent
	 */
	public GlobalScaleElementChartMenu(final GWidget parent,
			final IndicatorLayer indicatorLayer) {
		super(parent, new GWHorizontalLayout(10, true));
		this.chart = indicatorLayer.getChart();
		getBorder().setVisible(false);
		getBorder().reset(0, 0, 0, 0);

		addLevelLabel();

		final MergedLayer<GlobalScaleElementLayer> globalLayer = indicatorLayer
				.getGlobalLayer();
		final List<IChartLayer> layers = new ArrayList<>(
				globalLayer.getLayers());
		layers.add(globalLayer);

		for (final IChartLayer layer : layers) {
			new GWSeparator(this, true).setExtraLong(5);

			addVisibilityButton(layer);
		}

		addFilterButton(indicatorLayer);

		addScale1PricesCompressionLabel();
	}

	/**
	 * @param indicatorLayer
	 */
	private void addFilterButton(final IndicatorLayer indicatorLayer) {
		GWBitmapButton btn = new GWBitmapButton(this, BITMAP_BIG_FILTER) {
			@Override
			public String getTooltip() {
				return isSelected() ? "Disable Filters" : "Enable Filters";
			}
		};
		btn.setSelectionModel(new IGWSelectionModel() {

			@Override
			public void setSelected(boolean selected) {
				indicatorLayer.setFiltersEnabled(true);
				indicatorLayer.getChart().update(
						indicatorLayer.getChart().isAutoRangeEnabled());
			}

			@Override
			public boolean isSelected() {
				return indicatorLayer.isFiltersEnabled();
			}
		});
		btn.getBorder().reset(5, 5, 0, 0);
	}

	/**
	 * @param layer
	 */
	public void addVisibilityButton(final IChartLayer layer) {
		GWBitmapButton enableButton = new GWBitmapButton(this, BITMAP_ON,
				BITMAP_OFF, BITMAP_ON_WIDTH, BITMAP_ON_HEIGHT) {
			@Override
			public String getTooltip() {
				return (isSelected() ? "Hide " : "Show ") + layer.getName();
			}
		};
		enableButton.setSelectionModel(new IGWSelectionModel() {

			@Override
			public void setSelected(boolean selected) {
				layer.setEnabled(selected);
				layer.getChart().update(layer.getChart().isAutoRangeEnabled());
			}

			@Override
			public boolean isSelected() {
				return layer.isEnabled();
			}
		});
		enableButton.getBorder().reset(5, 5, 0, 0);
	}

	private void addScale1PricesCompressionLabel() {
		final PriceLayer scaleLayer = chart.getPriceLayer();
		GWButton button = new GWButton(this, "888") {
			{
				getBorder().setVisible(false);
			}

			@Override
			public String getText() {
				return Integer.toString(scaleLayer
						.getMaxNumberOfPricesToShowAsZZ1());
			}

			@Override
			public boolean isSelected() {
				return false;
			}

			@Override
			public String getTooltip() {
				return "Max number of prices to show when they are used for ZZ scale 1.";
			}

			@Override
			public void mouseClicked(int x, int y) {
				final ListDialog dialog = new ListDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell());
				dialog.setLabelProvider(new LabelProvider());
				dialog.setContentProvider(new ArrayContentProvider());
				int DFN = ScaleLayer.DEFAULT_FILTER_NUMBER;
				dialog.setInput(new Object[] { Integer.valueOf(1 * DFN), Integer.valueOf(2 * DFN), Integer.valueOf(3 * DFN),
						Integer.valueOf(5 * DFN), Integer.valueOf(6 * DFN), Integer.valueOf(7 * DFN), Integer.valueOf(8 * DFN), Integer.valueOf(9 * DFN), Integer.valueOf(10 * DFN),
								Integer.valueOf(11 * DFN), Integer.valueOf(12 * DFN), Integer.valueOf(13 * DFN), Integer.valueOf(14 * DFN) });
				dialog.setInitialSelections(new Integer[] { Integer.valueOf(scaleLayer
						.getMaxNumberOfPricesToShowAsZZ1()) });
				dialog.setTitle("Prices Compression Number");
				dialog.setMessage("Select the max number of prices to show when they are used for ZZ scale 1.");

				if (dialog.open() == Window.OK) {
					int number = ((Integer) dialog.getResult()[0]).intValue();
					scaleLayer.setMaxNumberOfPricesToShowAsZZ1(number);
					scaleLayer.getChart().fireRangeChanged();
				}
			}

			@Override
			public int getBestHeight() {
				return 10;
			}

			@Override
			public int getBestWidth() {
				return 15;
			}

		};
		button.setFont(GLUT.BITMAP_HELVETICA_12);

	}

	private void addLevelLabel() {
		new GWLabel(this, "*") {
			@Override
			public int getBestWidth() {
				return 15;
			}
		}.getBorder().setVisible(false);
	}
}
