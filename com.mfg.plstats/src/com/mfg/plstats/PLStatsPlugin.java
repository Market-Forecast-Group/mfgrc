package com.mfg.plstats;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.plstats.persist.PLStatsCSVStorage;
import com.mfg.plstats.persist.PLStatsIndicatorStorage;

/**
 * The activator class controls the plug-in life cycle
 */
public class PLStatsPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.plstats"; //$NON-NLS-1$
	public static final String PROBABILITIES_LOG_ID = "com.mfg.plstats.probabilitiesLog";

	// The shared instance
	private static PLStatsPlugin plugin;

	private IndicatorManager manager;
	private PLStatsCSVStorage _csvStorage;
	private PLStatsIndicatorStorage _indicatorStorage;

	// private DistributionsStorage distributionsStorage;

	/**
	 * The constructor
	 */
	public PLStatsPlugin() {
	}

	public PLStatsCSVStorage getCSVStorage() {
		if (_csvStorage == null) {
			_csvStorage = new PLStatsCSVStorage();
		}
		return _csvStorage;
	}

	public PLStatsIndicatorStorage getIndicatorStorage() {
		if (_indicatorStorage == null) {
			_indicatorStorage = new PLStatsIndicatorStorage();
		}
		return _indicatorStorage;
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
	public static PLStatsPlugin getDefault() {
		return plugin;
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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getBundledImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public IndicatorManager getIndicatorManager() {
		if (manager == null) {
			manager = new IndicatorManager();
		}
		return manager;
	}
}
