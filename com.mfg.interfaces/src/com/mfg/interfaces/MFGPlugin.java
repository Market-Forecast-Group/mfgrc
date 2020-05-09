package com.mfg.interfaces;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.thoughtworks.xstream.XStream;

/**
 * The activator class controls the plug-in life cycle
 */
public class MFGPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.interfaces"; //$NON-NLS-1$

	// The shared instance
	private static MFGPlugin plugin;

	private XStream xs;

	private Map<String, String> allEditors;

	/**
	 * The constructor
	 */
	public MFGPlugin() {
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
	public static MFGPlugin getDefault() {
		return plugin;
	}

	public Map<String, String> getAllEditors() {
		return allEditors;
	}

	@SuppressWarnings("unchecked")
	public void restoreEditors() {
		try {
			URL url = Platform.getInstanceLocation().getURL();
			final File file = new File(url.getFile(), "Editors.xml");
			if (!file.exists()) {
				return;
			}

			try (FileReader fis = new FileReader(file)) {
				if (fis.ready()) {
					allEditors = (Map<String, String>) xs.fromXML(fis);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
