package com.mfg.strategy.manual;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.logger.ILogger;
import com.mfg.logger.ILoggerManager;
import com.mfg.logger.memory.MemoryLoggerManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class ManualStrategyPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.strategy.manual"; //$NON-NLS-1$
	public static final String TRADING_CONSOLE_IMAGE_PATH = "icons/trading-console.ico";

	// The shared instance
	private static ManualStrategyPlugin plugin;

	private final ILoggerManager logManager;

	/**
	 * The constructor
	 */
	public ManualStrategyPlugin() {
		logManager = new MemoryLoggerManager("Manual Strategy", true);
	}

	public ILogger getLogger() {
		return logManager.createLogger();
	}

	public ILoggerManager getLogManager() {
		return logManager;
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
	public static ManualStrategyPlugin getDefault() {
		return plugin;
	}

	public static ImageDescriptor getBundledImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public Image getBundledImage(String path) {
		Image image = getImageRegistry().get(path);
		if (image == null) {
			getImageRegistry().put(path, getBundledImageDescriptor(path));
			image = getImageRegistry().get(path);
		}
		return image;
	}

}
