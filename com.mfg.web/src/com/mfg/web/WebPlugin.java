package com.mfg.web;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class WebPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.web"; //$NON-NLS-1$

	// The shared instance
	private static WebPlugin plugin;

	private WebServer _server;

	private WebServerJob _job;

	/**
	 * The constructor
	 */
	public WebPlugin() {
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
	public static WebPlugin getDefault() {
		return plugin;
	}

	/**
	 * Start or stop the server. Only one server at the same time can run.
	 */
	public synchronized void switchServer() {
		if (_job == null) {
			_job = new WebServerJob();
			_job.schedule();
		} else {
			_job.cancel();
			_job = null;
		}
	}

	public boolean isServerRunning() {
		return _server != null && _server.isRunning();
	}

	public WebServer getServer() {
		if (_server == null) {
			_server = new WebServer();
		}
		return _server;
	}

}
