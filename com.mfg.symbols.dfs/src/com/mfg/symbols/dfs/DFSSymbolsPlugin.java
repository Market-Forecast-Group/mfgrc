package com.mfg.symbols.dfs;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.symbols.dfs.persistence.DFSProfileStorage;
import com.mfg.symbols.dfs.persistence.DFSStorage;

/**
 * The activator class controls the plug-in life cycle
 */
public class DFSSymbolsPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.symbols.dfs"; //$NON-NLS-1$

	// The shared instance
	private static DFSSymbolsPlugin plugin;

	private DFSProfileStorage _profileStorage;

	private DFSStorage _storage;

	/**
	 * The constructor
	 */
	public DFSSymbolsPlugin() {
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
		getPreferenceStore().setDefault("com.mfg.symbols.dfs.prop1", 10);
		getPreferenceStore().setDefault("com.mfg.symbols.dfs.prop2",
				"some string");
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
	public static DFSSymbolsPlugin getDefault() {
		return plugin;
	}

	public DFSStorage getDFSStorage() {
		if (_storage == null) {
			_storage = new DFSStorage();
		}
		return _storage;
	}

	public DFSProfileStorage getProfileStorage() {
		if (_profileStorage == null) {
			_profileStorage = new DFSProfileStorage();
		}
		return _profileStorage;
	}

}
