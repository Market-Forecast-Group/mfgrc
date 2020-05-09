package com.mfg.persist.interfaces;

import static java.lang.System.out;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The activator class controls the plug-in life cycle
 */
public class PersistInterfacesPlugin extends AbstractUIPlugin {

	/**
	 * 
	 */
	public static final String PROP_WORKSPACE_DIRTY = "workspaceDirty";
	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.persist.interfaces"; //$NON-NLS-1$
	private static final String WORKSPACE_STORAGE_REF_ID = "com.mfg.persist.workspaceStorageReference"; //$NON-NLS-1$
	public static final String PREF_LAST_OPENED_WORKSPACE = "lastOpenedWorkspace"; //$NON-NLS-1$
	public static final String STORAGE_INITIATOR_ID = "com.mfg.persist.workspaceStorageInitiator"; //$NON-NLS-1$

	// The shared instance
	private static PersistInterfacesPlugin plugin;

	private boolean _workspaceDirty = false;
	Set<SimpleStorage<?>> _loadedStorages;
	private HashMap<UUID, WeakReference<IStorageObject>> _objMap;
	private List<IWorkspaceStorageReference> _storageReferences;

	/**
	 * The constructor
	 */
	public PersistInterfacesPlugin() {
		_loadedStorages = new HashSet<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		plugin.getPreferenceStore().setDefault(
				PREF_LAST_OPENED_WORKSPACE,
				new File(getDefaultWorkspacesDir(), "Workspace 1")
						.getAbsolutePath());
	}

	public void setWorkspaceDirty(boolean dirty) {
		this._workspaceDirty = dirty;
		firePropertyChange(PROP_WORKSPACE_DIRTY);
	}

	/**
	 * @return the workspaceDirty
	 */
	public boolean isWorkspaceDirty() {
		return _workspaceDirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PersistInterfacesPlugin getDefault() {
		return plugin;
	}

	/**
	 * @return
	 */
	public static File getDefaultWorkspacesDir() {
		return new File(Platform.getInstallLocation().getURL().getFile());
	}

	/**
	 * Instead use {@link #getStorageRefrences()} to get all the storages or
	 * {@link #getLoadedStorages()} to get only the loaded storages.
	 * 
	 * @return
	 */
	@Deprecated
	public List<SimpleStorage<?>> getStorages() {
		for (IWorkspaceStorageReference ref : getStorageRefrences()) {
			ref.getStorage();
		}
		return new ArrayList<>(_loadedStorages);
	}

	public Set<SimpleStorage<?>> getLoadedStorages() {
		return _loadedStorages;
	}

	private void persistStorages() {
		File workspace = new File(getCurrentWorkspacePath());
		workspace.mkdirs();

		for (SimpleStorage<?> s : _loadedStorages) {
			try {
				s.saveAll(workspace);
				s.storageSaved();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<IWorkspaceStorageReference> getStorageRefrences() {
		if (_storageReferences == null) {
			_storageReferences = new ArrayList<>();
			IConfigurationElement[] configElements = Platform
					.getExtensionRegistry().getConfigurationElementsFor(
							WORKSPACE_STORAGE_REF_ID);
			try {
				for (IConfigurationElement configElement : configElements) {
					final Object obj = configElement
							.createExecutableExtension("class");
					if (obj instanceof IWorkspaceStorageReference) {
						IWorkspaceStorageReference ref = (IWorkspaceStorageReference) obj;
						_storageReferences.add(ref);
					}
				}
			} catch (CoreException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		}
		return _storageReferences;
	}

	/**
	 * @param storageId
	 * @return
	 */
	public IWorkspaceStorage getStorage(String storageId) {
		for (IWorkspaceStorageReference ref : getStorageRefrences()) {
			if (ref.getStorageId().equals(storageId)) {

				return ref.getStorage();
			}
		}
		return null;
	}

	public IStorageObject findById(UUID uuid) {
		if (_objMap == null) {
			_objMap = new HashMap<>();
		}
		WeakReference<IStorageObject> ref = _objMap.get(uuid);
		if (ref == null) {
			for (Object storage : _loadedStorages) {
				if (storage instanceof SimpleStorage) {
					IStorageObject obj = ((SimpleStorage<?>) storage)
							.findById(uuid);
					if (obj != null) {
						_objMap.put(uuid, new WeakReference<>(obj));
						return obj;
					}
				}
			}
		} else {
			IStorageObject obj = ref.get();
			if (obj == null) {
				_objMap.remove(uuid);
			}
			return obj;
		}
		return null;
	}

	/**
	 * 
	 * @param fileToSwitch
	 * @param copyCurrentObjects
	 * @param userJob
	 * @param monitor
	 */
	public void loadWorkspace(File fileToSwitch,
			final boolean copyCurrentObjects, boolean userJob,
			IProgressMonitor monitor) {
		final File file = fileToSwitch;
		getDefault().setCurrentWorkspacePath(file.getPath());

		if (copyCurrentObjects) {
			file.mkdirs();
			persistStorages();
		}

		if (_loadedStorages != null) {
			for (SimpleStorage<?> s : _loadedStorages) {
				s.reload();
			}
		}
		getDefault().setWorkspaceDirty(false);
	}

	public void loadLastWorkspace() {
		IPreferenceStore prefs = PersistInterfacesPlugin.getDefault()
				.getPreferenceStore();
		File file = new File(
				prefs.getString(PersistInterfacesPlugin.PREF_LAST_OPENED_WORKSPACE));
		if (!file.exists()) {
			file = new File(
					prefs.getDefaultString(PersistInterfacesPlugin.PREF_LAST_OPENED_WORKSPACE));
		}
		loadWorkspace(file, false, false, null);
	}

	public boolean openSaveWorksapceDialog() {
		int returnCode;
		if (PersistInterfacesPlugin.getDefault().isWorkspaceDirty()) {
			MessageBox dialog = new MessageBox(getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION
					| SWT.YES | SWT.NO | SWT.CANCEL);
			dialog.setMessage("Save " + getCurrentWorkspacePath() + "?");
			returnCode = dialog.open();
			if (returnCode == SWT.YES) {
				persistStorages();
			}
		} else {
			MessageBox dialog = new MessageBox(getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION
					| SWT.OK | SWT.CANCEL);
			dialog.setMessage("Do you want to close the application?");
			returnCode = dialog.open();
		}
		return returnCode != SWT.CANCEL;
	}

	/**
	 * 
	 * @param fileName
	 */
	public void importFile(final String fileName) {
		// TODO:
	}

	/**
	 * 
	 * @param fileName
	 */
	public void exportFile(final String fileName) {
		// TODO:
	}

	private String workSpacePath;

	public String getCurrentWorkspacePath() {
		return workSpacePath;
	}

	public void setCurrentWorkspacePath(String aWorkSpacePath) {
		workSpacePath = aWorkSpacePath;
	}

	private final PropertyChangeSupport support = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener l) {
		support.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		support.removePropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.addPropertyChangeListener(property, l);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener l) {
		support.removePropertyChangeListener(property, l);
	}

	public void firePropertyChange(String property) {
		support.firePropertyChange(property, true, false);
	}

	public void saveWorkspace() {
		persistStorages();
		setWorkspaceDirty(false);
		try {
			Platform.getPreferencesService().getRootNode().flush();
			IWorkbench workbench = PlatformUI.getWorkbench();
			Class<? extends IWorkbench> cls = workbench.getClass();
			try {
				out.println("Save workbench");
				Method m = cls.getDeclaredMethod("persist", boolean.class);
				m.setAccessible(true);
				m.invoke(workbench, Boolean.FALSE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

}
