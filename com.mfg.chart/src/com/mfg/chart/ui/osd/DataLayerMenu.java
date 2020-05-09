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

import org.mfg.opengl.widgets.GWButton;
import org.mfg.opengl.widgets.GWColorModel;
import org.mfg.opengl.widgets.GWHorizontalLayout;
import org.mfg.opengl.widgets.GWidget;
import org.mfg.opengl.widgets.IGWSelectionModel;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.model.IDataLayerModel;

/**
 * @author arian
 * 
 */
public class DataLayerMenu extends GWidget {

	float[] _color;

	public DataLayerMenu(GWidget parent, final Chart chart) {
		super(parent);
		getBorder().setVisible(false);
		getBorder().reset(5, 5, 5, 5);
		setLayout(new GWHorizontalLayout(5, true));

		_color = new float[] { 0, 1, 0.67f, 1 };

		int count = chart.getModel().getDataLayerCount();
		for (int i = 0; i < count; i++) {
			final int dataLayer = i;
			final GWButton btn = new GWButton(this, Integer.toString(i + 1));
			btn.setSelectionModel(new IGWSelectionModel() {

				@Override
				public void setSelected(boolean selected) {
					IDataLayerModel model = chart.getModel()
							.getDataLayerModel();
					boolean auto = model.isAutoDataLayer();
					if (btn.isSelected()) {
						model.setAutoDataLayer(!auto);
					} else {
						model.setAutoDataLayer(false);
						model.setDataLayer(dataLayer);
					}
				}

				@Override
				public boolean isSelected() {
					int curLayer = chart.getModel().getDataLayerModel()
							.getDataLayer();
					IDataLayerModel model = chart.getModel()
							.getDataLayerModel();
					boolean auto = model.isAutoDataLayer();
					boolean sel = dataLayer == curLayer;
					btn.setTooltip(sel ? (auto ? "Disable automatic switching"
							: "Enable automatic switching")
							: "Switch to Data Layer " + (dataLayer + 1)
									+ " and disable automatic switching");
					return sel;
				}

			});

			btn.setColorModel(new GWColorModel() {

				@Override
				public float[] getForeground() {
					boolean auto = chart.getModel().getDataLayerModel()
							.isAutoDataLayer();
					if (btn.isSelected() && !auto) {
						return _color;
					}
					return super.getForeground();
				}
			});
		}
	}
}
