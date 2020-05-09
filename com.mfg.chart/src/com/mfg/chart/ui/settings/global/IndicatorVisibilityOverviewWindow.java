package com.mfg.chart.ui.settings.global;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import com.mfg.chart.backend.opengl.Chart;

public class IndicatorVisibilityOverviewWindow extends Shell {

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	@SuppressWarnings("unused")
	public IndicatorVisibilityOverviewWindow(Chart chart, Display display) {
		super(display, isLinux() ? SWT.SHELL_TRIM : SWT.SHELL_TRIM | SWT.ON_TOP);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		IndicatorVisibilityOverview indicatorVisibilityOverview = new IndicatorVisibilityOverview(
				chart, this);
		createContents();
	}

	private static boolean isLinux() {
		return System.getProperty("os.name").equals("Linux");
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Indicator Visibility Overview");
		setSize(450, 300);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
