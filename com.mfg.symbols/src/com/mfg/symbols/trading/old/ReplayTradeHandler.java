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
package com.mfg.symbols.trading.old;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;

import com.mfg.chart.commands.AbstractChartViewHanlder;
import com.mfg.chart.ui.views.AbstractChartView;

public class ReplayTradeHandler extends AbstractChartViewHanlder {

	@Override
	protected Object execute(AbstractChartView view, ExecutionEvent event) {
		MessageDialog.openInformation(view.getSite().getShell(), "Replay",
				"Empty Replay Action");
		return null;
	}

}
