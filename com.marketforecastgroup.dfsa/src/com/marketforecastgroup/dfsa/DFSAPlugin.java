package com.marketforecastgroup.dfsa;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.marketforescastgroup.logger.LogManager;
import com.mfg.dfs.conn.DfsCacheRepo;
import com.mfg.dfs.conn.IDFS;
import com.mfg.dfs.data.HistoryTable;
import com.mfg.dfs.misc.Service;
import com.mfg.persist.interfaces.PersistInterfacesPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class DFSAPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.marketforecastgroup.dfsa"; //$NON-NLS-1$

	public static final String DAYS_TO_OVERRIDE = "DAYS_TO_OVERRIDE";
	public static final String HOURS_TO_OVERRIDE = "HOURS_TO_OVERRIDE";

	// The shared instance
	private static DFSAPlugin plugin;

	public static DfsCacheRepo getCacheRepo(IDFS dfs) {
		Service s = (Service) dfs;
		return s.getModel().getCache();
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
		getPreferenceStore().setDefault(HOURS_TO_OVERRIDE, 24);
		getPreferenceStore().setDefault(DAYS_TO_OVERRIDE, 7);

		HistoryTable.setDaysToOverlap(getPreferenceStore().getInt(
				DAYS_TO_OVERRIDE));
		HistoryTable.setHoursToOverlap(getPreferenceStore().getInt(
				HOURS_TO_OVERRIDE));

		LogManager.getInstance().INFO(
				"System is started @ "
						+ (Platform.getInstanceLocation() == null ? "null"
								: Platform.getInstanceLocation().getURL()
										.getFile()));
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
	public static DFSAPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static ImageDescriptor getBundledImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public Image getBundledImage(final String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, getBundledImageDescriptor(path));
			image = getImageRegistry().get(path);
		}
		return image;
	}

	public static void saveWorkspace() {
		PersistInterfacesPlugin.getDefault().saveWorkspace();
	}
}