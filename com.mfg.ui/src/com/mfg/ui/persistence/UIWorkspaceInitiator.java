package com.mfg.ui.persistence;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.persist.interfaces.IWorkspaceStorage;
import com.mfg.persist.interfaces.IWorkspaceStorageInitiator;
import com.mfg.persist.interfaces.IWorkspaceStorageListener;
import com.mfg.persist.interfaces.RemoveException;
import com.mfg.ui.editors.StorageObjectEditorInput;

public class UIWorkspaceInitiator implements IWorkspaceStorageInitiator,
		IWorkspaceStorageListener {

	public UIWorkspaceInitiator() {
	}

	@Override
	public void intitialize(IWorkspaceStorage storage) {
		storage.addStorageListener(this);
	}

	@Override
	public void objectAdded(IWorkspaceStorage sotarage, Object obj) {
		// nothing
	}

	@Override
	public void listAdded(IWorkspaceStorage storage, List<? extends Object> list) {
		// nothing
	}

	@Override
	public void objectAboutToRemove(IWorkspaceStorage storage, Object obj)
			throws RemoveException {
		checkForRunningJobs(obj);
	}

	/**
	 * @param obj
	 * @throws RemoveException
	 */
	private static void checkForRunningJobs(Object obj) throws RemoveException {
		Job[] jobs = Job.getJobManager().find(obj);
		if (jobs.length > 0) {
			throw new RemoveException(String.format(
					"Cannot remove %s because it is used by a running job",
					((IStorageObject) obj).getName()));
		}
	}

	@Override
	public void objectRemoved(IWorkspaceStorage storage, Object obj) {
		IEditorReference[] refs = getEditorsFor(obj);
		for (IEditorReference ref : refs) {
			ref.getPage().closeEditor(ref.getEditor(true), false);
		}
	}

	private static IEditorReference[] getEditorsFor(Object obj) {
		List<IEditorReference> list = new ArrayList<>();
		for (IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			IWorkbenchPage page = window.getActivePage();
			IEditorReference[] refs = page.getEditorReferences();
			for (IEditorReference ref : refs) {
				IEditorInput input;
				try {
					input = ref.getEditorInput();
					if (input instanceof StorageObjectEditorInput<?>) {
						if (((StorageObjectEditorInput<?>) input)
								.getStorageObject() == obj) {
							list.add(ref);
						}
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
		return list.toArray(new IEditorReference[list.size()]);
	}

	@Override
	public void objectModified(IWorkspaceStorage storage, Object obj) {
		// nothing
	}

	@Override
	public void listRemoved(IWorkspaceStorage storage,
			List<? extends Object> list) {
		// nothing
	}

	@Override
	public void storageChanged(IWorkspaceStorage storage) {
		// nothing
	}

}
