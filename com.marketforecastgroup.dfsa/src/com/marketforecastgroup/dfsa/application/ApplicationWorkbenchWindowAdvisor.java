package com.marketforecastgroup.dfsa.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.marketforecastgroup.dfsa.DFSAPlugin;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public static final String TITLE = "Data Feed and Storage Agent";

	public static IWorkbenchWindowConfigurer configurer;

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer1) {
		super(configurer1);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer1) {
		return new ApplicationActionBarAdvisor(configurer1);
	}

	@Override
	public void preWindowOpen() {
		configurer = getWindowConfigurer();
		configurer.setShowCoolBar(true);
		configurer.setShowMenuBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowFastViewBars(true);
		// configurer.setShellStyle(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX);
		configurer.setTitle(TITLE); //$NON-NLS-1$
	}

	@Override
	public void postWindowOpen() {
		// Shell shell = configurer.getWindow().getShell();
		// Display display = shell.getDisplay();
		// Monitor primary = display.getPrimaryMonitor();
		// Rectangle bounds = primary.getBounds();
		// Rectangle rect = shell.getBounds();
		//
		// int x = bounds.x + (bounds.width - rect.width) / 2;
		// int y = bounds.y + (bounds.height - rect.height) / 2;
		//
		// shell.setLocation(x, y);
		// shell.setSize(880, 620);

		PersistInterfacesPlugin.getDefault().setWorkspaceDirty(false);
	}

	@Override
	public boolean preWindowShellClose() {
		int returnCode;
		if (PersistInterfacesPlugin.getDefault().isWorkspaceDirty()) {
			MessageBox dialog = new MessageBox(getWindowConfigurer()
					.getWindow().getShell(), SWT.ICON_QUESTION | SWT.YES
					| SWT.NO | SWT.CANCEL);
			dialog.setMessage("Do you want to save last changes?");
			returnCode = dialog.open();
			if (returnCode == SWT.YES) {
				DFSAPlugin.saveWorkspace();
			}
		} else {
			MessageBox dialog = new MessageBox(getWindowConfigurer()
					.getWindow().getShell(), SWT.ICON_QUESTION | SWT.OK
					| SWT.CANCEL);
			dialog.setMessage("Do you want to close the application?");
			returnCode = dialog.open();
		}
		return returnCode != SWT.CANCEL;
	}
}
