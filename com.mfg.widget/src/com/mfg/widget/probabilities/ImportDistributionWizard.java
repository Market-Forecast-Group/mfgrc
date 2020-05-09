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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;

public class ImportDistributionWizard extends Wizard implements IImportWizard {

	private ImportPage page;

	public ImportDistributionWizard() {
		super();
		setWindowTitle("Import Probability Distribution");
		addPage(page = new ImportPage());
	}

	private String fileName;
	IWorkbench workbench;
	IWorkbenchWindow window;

	@Override
	public boolean performFinish() {
		if (page.getFileNameText().isEmpty()) {
			page.setErrorMessage("Obligatory Field left Blank");
			return false;
		}
		fileName = page.getFileNameText();
		ProbabilitiesManager probabilitiesManager = WidgetPlugin.getDefault()
				.getProbabilitiesManager();
		DistributionsContainer d = ProbabilitiesManager
				.loadDistributionContainer(fileName);
		probabilitiesManager.setDistributionsContainer(d);
		whendone();
		return true;
	}

	public String getFileName() {
		return fileName;
	}

	public void whendone() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// open the perspective
				try {
					workbench.showPerspective(
							"com.mfg.plstats.ProbabilitiesPerpective", window);
				} catch (WorkbenchException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		workbench = PlatformUI.getWorkbench();
		window = workbench.getActiveWorkbenchWindow();
	}

}
