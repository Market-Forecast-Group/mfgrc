package com.marketforescastgroup.logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LogViewPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.marketforescastgroup.logger"; //$NON-NLS-1$

	public static final String LOG_DIRECTORY = "LOG_DIRECTORY";

	public static final String LOG_DEFAULT_PATH = System
			.getProperty("user.home") + "/.jdfsa/";

	// The shared instance
	private static LogViewPlugin plugin;

	/**
	 * The constructor
	 */
	public LogViewPlugin() {
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

		String log = getPreferenceStore().getString(
				LogViewPlugin.LOG_DIRECTORY);
		if (log.length() == 0) {
			getPreferenceStore().setValue(LogViewPlugin.LOG_DIRECTORY,
					LogViewPlugin.LOG_DEFAULT_PATH);
			getPreferenceStore().setDefault(LogViewPlugin.LOG_DIRECTORY,
					LogViewPlugin.LOG_DEFAULT_PATH);
		}
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
	public static LogViewPlugin getDefault() {
		return plugin;
	}

	
}
