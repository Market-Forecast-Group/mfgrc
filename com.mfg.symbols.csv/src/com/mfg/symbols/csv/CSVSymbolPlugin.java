package com.mfg.symbols.csv;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.connector.csv.CSVPlugin;
import com.mfg.connector.csv.preferences.CSVPrefsPage;
import com.mfg.symbols.csv.persistence.CSVStorage;

/**
 * The activator class controls the plug-in life cycle
 */
public class CSVSymbolPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.symbols.csv"; //$NON-NLS-1$

	public static final String CSV_SYMBOL_IMAGE_PATH = "icons/symbol csv.ico";
	@Deprecated
	public static final String CSV_SYMBOL_CONFIG_IMAGE_PATH = "icons/symbol-csv-config.ico";

	private CSVStorage configrationsStorage;

	// The shared instance
	private static CSVSymbolPlugin plugin;

	/**
	 * The constructor
	 */
	public CSVSymbolPlugin() {
	}

	public CSVStorage getCSVStorage() {
		if (configrationsStorage == null) {
			configrationsStorage = new CSVStorage();
		}
		return configrationsStorage;
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

	public static String getCSVFilesPath() {
		String dir = CSVPlugin.getDefault().getPreferenceStore()
				.getString(CSVPrefsPage.PREFERENCE_CSV_DATA_FOLDER);
		return dir;
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
	public static CSVSymbolPlugin getDefault() {
		return plugin;
	}
}
