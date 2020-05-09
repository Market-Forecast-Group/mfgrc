package com.mfg.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class UtilsPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "com.mfg.utils";

	public static final String PREFERENCES_STD_DEBUG = "utils.std.debug";
	public static final String PERSPECTIVE_TRAKER_ENABLED = "PERSPECTIVE_TRAKER_ENABLED";

	// This is not used any more.
	public static final String PREFERENCES_CREATE_LOG_FILE = "utils.std.log_file";
	public static UtilsPlugin plugin;

	public static ImageDescriptor getBundledImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns an image for the image file at the given plug-in relative path.
	 * Client do not need to dispose this image. Images will be disposed
	 * automatically.
	 * 
	 * @param path
	 *            the path
	 * @return image instance
	 */
	public Image getBundledImage(String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, getBundledImageDescriptor(path));
			image = getImageRegistry().get(path);
		}
		return image;
	}

	/**
	 * @return the plugin
	 */
	public static UtilsPlugin getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		getPreferenceStore().setDefault(PREFERENCES_STD_DEBUG, true);
		getPreferenceStore().setDefault(PERSPECTIVE_TRAKER_ENABLED, true);
		getPreferenceStore().setDefault(PREFERENCES_CREATE_LOG_FILE, false);

		Utils.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

		Utils.stop();
	}
}
