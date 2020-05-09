package com.mfg.dfs;

import static com.mfg.utils.Utils.debug_var;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.dfs.cache.MfgMdbSession;
import com.mfg.dfs.conn.ServerFactory;

public class DFSCorePlugin extends AbstractUIPlugin {

	// public static final String DFS_LOCAL_ROOT = "com.mfg.dfs.dfs_local_root";
	private static BundleContext context;
	private static DFSCorePlugin _instance;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		DFSCorePlugin.context = bundleContext;
		_instance = this;
		MfgMdbSession.waitForTheRoot();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		DFSCorePlugin.context = null;
		ServerFactory.disposingServer();
		debug_var(839902, "Final disposing of the dfs server...");
	}

	public static DFSCorePlugin getDefault() {
		return _instance;
	}

}
