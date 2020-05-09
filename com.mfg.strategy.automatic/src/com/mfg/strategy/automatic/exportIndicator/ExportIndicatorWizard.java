package com.mfg.strategy.automatic.exportIndicator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.mfg.utils.Utils;

public class ExportIndicatorWizard extends Wizard implements IExportWizard {

	private IndicatorExportingSettings page;
	private IndicatorExportingConfiguration configuration;

	public ExportIndicatorWizard() {
		setWindowTitle("Export Indicator Wizard");
	}

	@Override
	public void addPages() {
		page = new IndicatorExportingSettings();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		boolean ready = this.page.getConfiguration().isReady();
		if (ready)
			doTheExporting(this.page.getConfiguration());
		return ready;
	}

	@SuppressWarnings("unused")
	// Setting StrategyExportIndicator
	private void doTheExporting(IndicatorExportingConfiguration aConfiguration) {
		new StrategyExportIndicator(aConfiguration);
		Utils.debug_var(12345, aConfiguration);
		this.configuration = aConfiguration;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	public IndicatorExportingConfiguration getConfiguration() {
		return configuration;
	}

}
