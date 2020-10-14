package com.mfg.application;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.mfg.persist.interfaces.PersistInterfacesPlugin;

public class Application implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		Location instanceLoc = Platform.getInstanceLocation();

		try {
			boolean remember = WorkspaceLauncher.isRememberWorkspace();

			String lastUsedWs = WorkspaceLauncher
					.getLastSetWorkspaceDirectory();

			if (remember && (lastUsedWs == null || lastUsedWs.length() == 0)) {
				remember = false;
			}
			if (remember) {
				String ret = WorkspaceLauncher.checkWorkspaceDirectory(Display
						.getDefault().getActiveShell(), lastUsedWs, false,
						false);
				if (ret != null) {
					remember = false;
				}

			}

			if (!remember) {
				WorkspaceLauncher pwd = new WorkspaceLauncher(false, null);
				int pick = pwd.open();

				if (pick == Window.CANCEL) {
					if (pwd.getSelectedWorkspaceLocation() == null) {
						try {
							PlatformUI.getWorkbench().close();
						} catch (Exception err) {
							return IApplication.EXIT_OK;
						}
					}
				} else {
					instanceLoc.set(
							new URL("file", null, pwd
									.getSelectedWorkspaceLocation()), true);
				}
			} else {
				instanceLoc.set(new URL("file", null, lastUsedWs), true);
			}

			if (!instanceLoc.isSet()) {
				MessageDialog.openError(display.getActiveShell(), "ERROR",
						"Workspace in use, choose a different one.");
				WorkspaceLauncher.setRememberWorkspace(false);
				start(context);
				return IApplication.EXIT_OK;
			}

			loadPersistenceXMLWorkspace(instanceLoc);

			int returnCode = PlatformUI.createAndRunWorkbench(display,
					new ApplicationWorkbenchAdvisor());

			if (returnCode == PlatformUI.RETURN_RESTART) {

				return IApplication.EXIT_RESTART;
			}

			return IApplication.EXIT_OK;

		} catch (Exception err) {
			err.printStackTrace();
			MessageDialog
					.openError(
							display.getActiveShell(),
							"ERROR",
							"Cannot change the location once it is set. Append the argument -data @noDefault to the run configurations Arguments(Application will exit now).");
		} finally {
			if (!display.isDisposed()) {
				display.dispose();
			}
		}

		return IApplication.EXIT_OK;
	}

	/**
	 * @param instanceLoc
	 */
	public static void loadPersistenceXMLWorkspace(Location instanceLoc) {
		// This is to load the persistence.xml file (by Arian)
		if (instanceLoc != null) {
			PersistInterfacesPlugin.getDefault()
					.loadWorkspace(new File(instanceLoc.getURL().getFile()),
							false, true, null);
		}
		// --
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
