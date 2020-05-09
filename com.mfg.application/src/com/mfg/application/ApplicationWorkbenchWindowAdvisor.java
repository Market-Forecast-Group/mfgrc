package com.mfg.application;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.mfg.persist.interfaces.PersistInterfacesPlugin;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	/**
	 * 
	 */
	public static final String APP_TITLE = "MFG RC [1.0 beta]";
	IWorkbenchWindowConfigurer _configurer;

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		_configurer = getWindowConfigurer();
		_configurer.setShowCoolBar(true);
		_configurer.setShowStatusLine(true);
		_configurer.setShowPerspectiveBar(true);
		// _configurer.setShowFastViewBars(true);
		_configurer.setShowProgressIndicator(true);
		_configurer.setShowMenuBar(true);
		_configurer.setTitle(APP_TITLE);

		IPreferenceStore store = PlatformUI.getPreferenceStore();
		store.setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,
				false);
		store.setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR,
				IWorkbenchPreferenceConstants.TOP_RIGHT);
		store.setValue(
				IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR,
				true);
		store.setDefault(
				IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS,
				"com.mfg.application.perspective,com.mfg.strategy.builder.perspective,com.marketforecastgroup.dfsa.ui.perspective");

	}

	@Override
	public void postWindowOpen() {
		Shell shell = _configurer.getWindow().getShell();
		shell.setMaximized(true);
		shell.setText("MFG RC [1.0 beta] - "
				+ PersistInterfacesPlugin.getDefault()
						.getCurrentWorkspacePath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowShellClose()
	 */
	@Override
	public boolean preWindowShellClose() {
		boolean close = PersistInterfacesPlugin.getDefault()
				.openSaveWorksapceDialog();
		return close;
	}
}
