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
package com.mfg.chart.commands;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

import com.mfg.chart.backend.opengl.Chart;
import com.mfg.chart.ui.views.AbstractChartView;

/**
 * @author arian
 * 
 */
// this should be modified to use the DataLayerAction implementation.
@Deprecated
public class SwitchLayerHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		try {
			String id = event.getCommand().getId();
			boolean up = id.endsWith("Up");
			boolean down = id.endsWith("Down");
			boolean range = id.endsWith("Range");
			boolean min = id.endsWith("Minute");
			// boolean day = id.endsWith("Daily");

			Chart chart = view.getChart();
			int layer = chart.getDataLayer();
			int newLayer;
			if (up || down) {
				newLayer = layer + (up ? 1 : -1);
			} else {
				if (range) {
					newLayer = 0;
				} else if (min) {
					newLayer = 1;
				} else {
					newLayer = 2;
				}

				// State s = event.getCommand().getState(
				// RegistryToggleState.STATE_ID);
				// if (s == null) {
				// s = new State();
				// s.setValue(new Boolean(false));
				// event.getCommand()
				// .addState(RegistryToggleState.STATE_ID, s);
				// }

				HandlerUtil.toggleCommandState(event.getCommand());
				State s = event.getCommand().getState(
						RegistryToggleState.STATE_ID);
				Boolean sel = (Boolean) s.getValue();
				if (!sel.booleanValue()) {
					newLayer = -1;
				}
			}

			if (newLayer == -1) {
				chart.setAutoDataLayer(true);
			} else if (newLayer >= 0
					&& newLayer < chart.getModel().getDataLayerCount()) {
				chart.setAutoDataLayer(false);
				chart.setDataLayer(newLayer);
			}

			ICommandService serv = (ICommandService) HandlerUtil
					.getActivePart(event).getSite()
					.getService(ICommandService.class);
			String[] layers_id = new String[] { "Range", "Minute", "Daily" };
			for (int i = 0; i < 3; i++) {
				String id2 = "com.mfg.chart.commands.switchLayer_"
						+ layers_id[i];
				if (i != chart.getDataLayer() || chart.isAutoDataLayer()) {
					Command cmd = serv.getCommand(id2);
					State s2 = cmd.getState(RegistryToggleState.STATE_ID);
					s2.setValue(Boolean.TRUE);
					s2.setValue(Boolean.FALSE);
				}
			}

		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
