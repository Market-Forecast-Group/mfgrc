/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.widget.probabilities;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.ActionFactory;

import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;

public class ImportDistributionAction extends Action {

	public ImportDistributionAction() {
		init();
	}

	protected void init() {
		setText("Import Probability Distribution");
		setId(ActionFactory.IMPORT.getId());
		setActionDefinitionId(ActionFactory.IMPORT.getId());
		setImageDescriptor(WidgetPlugin.getImageDescriptor("/icons/import.gif"));
		setEnabled(true);
	}

	@Override
	public void run() {
		Shell shell = Display.getDefault().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.prob" });
		dialog.setFilterNames(new String[] { "Probability Distribution" });
		dialog.setText("Import Probability Distribution");
		String path = dialog.open();
		if (path == null)
			return;
		ProbabilitiesManager probabilitiesManager = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		DistributionsContainer d = ProbabilitiesManager
				.loadDistributionContainer(path);
		probabilitiesManager.setDistributionsContainer(d);
	}
}
