package com.mfg.application;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.mfg.utils.Utils;
import com.mfg.utils.jobs.IMFGJob;
import com.mfg.utils.jobs.MFGJob;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "com.mfg.application.perspective"; //$NON-NLS-1$

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void postStartup() {
		final IWorkbenchWindow win = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (win == null) {
			PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {

				@Override
				public void windowOpened(IWorkbenchWindow window) {
					// registerListenersToWindow(window);
				}

				@Override
				public void windowDeactivated(IWorkbenchWindow window) {
					//
				}

				@Override
				public void windowClosed(IWorkbenchWindow window) {
					//
				}

				@Override
				public void windowActivated(IWorkbenchWindow window) {
					//
				}
			});
		} else {
			// registerListenersToWindow(win);
		}
	}

	static final Map<String, List<MPart>> _mapParts = new HashMap<>();
	static final Map<String, IEditorReference[]> _mapRefs = new HashMap<>();
	static final Map<String, IEditorPart> _mapActive = new HashMap<>();

	static void registerListenersToWindow(final IWorkbenchWindow win) {
		Utils.debug_var(523623,
				"Register perspective-editor listeners to window " + win);

		win.addPerspectiveListener(new PerspectiveAdapter() {

			{
				IWorkbenchPage page = win.getActivePage();
				String id = page.getPerspective().getId();
				init(page, id);
			}

			@Override
			public void perspectiveChanged(IWorkbenchPage page,
					IPerspectiveDescriptor perspective, String changeId) {
				if (changeId == IWorkbenchPage.CHANGE_EDITOR_OPEN
						|| changeId == IWorkbenchPage.CHANGE_EDITOR_CLOSE) {
					init(page, perspective.getId());
				}
			}

			private List<MPart> getEditorParts(IWorkbenchPage page) {
				List<MPart> list = new ArrayList<>();
				EPartService serv = (EPartService) page.getWorkbenchWindow()
						.getService(EPartService.class);

				for (MPart p : serv.getParts()) {
					if (p.getElementId().contains("editor")) {
						list.add(p);
					}
				}

				return list;
			}

			void init(IWorkbenchPage page, String id) {
				IEditorReference[] editors = page.getEditorReferences();
				_mapRefs.put(id, editors);
				_mapParts.put(id, getEditorParts(page));
				_mapActive.put(id, page.getActiveEditor());
			}

			@Override
			public void perspectiveActivated(IWorkbenchPage page,
					IPerspectiveDescriptor perspective) {
				EPartService partServ = (EPartService) PlatformUI
						.getWorkbench().getActiveWorkbenchWindow()
						.getService(EPartService.class);

				String perspId = perspective.getId();

				// IEditorReference[] editors = page.getEditorReferences();
				// for (IEditorReference ref : editors) {
				// page.setPartState(ref, IWorkbenchPage.STATE_MINIMIZED);
				// }

				for (MPart p : getEditorParts(page)) {
					p.getTags().add(IPresentationEngine.MINIMIZED);
				}

				List<MPart> parts = _mapParts.get(perspId);
				if (parts != null) {
					for (MPart p : parts) {
						p.setVisible(true);
						out.println("show " + p.getObject());
						partServ.showPart(p, PartState.CREATE);
					}
				}

				IEditorPart editor = _mapActive.get(perspId);
				if (editor != null) {
					page.activate(editor);
				}

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui
	 * .application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#preShutdown()
	 */
	@Override
	public boolean preShutdown() {
		final Job[] mfgJobs = Job.getJobManager().find(IMFGJob.class);

		if (mfgJobs.length > 0) {
			if (MessageDialog
					.openConfirm(
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
							"Application Close",
							"One or more Jobs are still running. Do you want to close the application anyway?")) {

				Job[] refreshingJobs = Job.getJobManager().find(
						"com.mfg.jobs.refreshingGuiJob");

				out.println("Closing refreshing jobs...");

				for (Job job : refreshingJobs) {
					job.cancel();
					try {
						job.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				out.println("Closed refreshing jobs.");

				Job cancelJob = new Job("Canceling jobs") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask("Canceling jobs", mfgJobs.length);
						for (Job job : mfgJobs) {
							out.println("Canceling job " + job.getName());
							if (job instanceof MFGJob) {
								((MFGJob) job).setCanceledByAppShutdown(true);
							}
							job.cancel();
							try {
								job.join();
							} catch (InterruptedException e) {
								//
							}
							monitor.worked(1);
						}
						monitor.done();
						return Status.OK_STATUS;
					}
				};
				cancelJob.setSystem(true);
				cancelJob.schedule();
				try {
					cancelJob.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} else {
				return false;
			}
		}

		return true;
	}
}
