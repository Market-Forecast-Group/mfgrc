package com.mfg.logger;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.logger.application.AppLogLevel;
import com.mfg.logger.application.AppLogger;
import com.mfg.logger.application.AppLoggerManager;
import com.mfg.logger.application.IAppLogger;

/**
 * The activator class controls the plug-in life cycle
 */
public class LoggerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.logger"; //$NON-NLS-1$
	public static final String COMPONENT_PREFERENCES_EXTENSION_POINT = "com.mfg.logger.application.components"; //$NON-NLS-1$
	public static final String LOGGER_VIEWER_EXTENSION_POINT = "com.mfg.logger.ui.logViewer"; //$NON-NLS-1$

	public static ImageDescriptor getBundledImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	// The shared instance
	private static LoggerPlugin plugin;
	private AppLoggerManager appLogManager;
	private HashMap<String, IConfigurationElement> components;

	private LogRecordConverter fLogRecordConverter = new LogRecordConverter();

	/**
	 * The constructor
	 */
	public LoggerPlugin() {
	}

	public IAppLogger getAppLogger(String componentId, String source) {
		AppLogger logger = getAppLogManager().createLogger();
		logger.setComponentID(componentId);
		logger.setSource(source);
		return logger;
	}

	/**
	 * @param componentId
	 * @return
	 */
	public static String getComponentLogLevelPreferenceKey(String componentId) {
		return "com.mfg.logger@" + componentId;
	}

	public AppLoggerManager getAppLogManager() {
		if (appLogManager == null) {
			appLogManager = new AppLoggerManager("Application");
		}
		return appLogManager;
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

	/**
	 * 
	 */
	private void initLogLevels() {
		components = new HashMap<>();
		IConfigurationElement[] configElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						LoggerPlugin.COMPONENT_PREFERENCES_EXTENSION_POINT);

		IPreferenceStore store = getPreferenceStore();

		for (IConfigurationElement configElement : configElements) {
			if (configElement.getName().equals("component")) {
				String componentID = configElement.getAttribute("id");
				components.put(componentID, configElement);

				String levelPref = getComponentLogLevelPreferenceKey(componentID);

				store.setDefault(levelPref, AppLogLevel.COMMENT.getPriority());
			}
		}
	}

	private HashMap<String, IConfigurationElement> getComponents() {
		if (components == null) {
			initLogLevels();
		}
		return components;
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
	public static LoggerPlugin getDefault() {
		return plugin;
	}

	/**
	 * @param componentId
	 * @return
	 */
	public IConfigurationElement getLoggerComponentConfiguration(
			String componentId) {
		return getComponents().get(componentId);
	}

	public Collection<IConfigurationElement> getComponentConfigurations() {
		return getComponents().values();
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public LogRecordConverter getLogRecordConverter() {
		return fLogRecordConverter;
	}

	public void setLogRecordConverter(LogRecordConverter aConverter) {
		fLogRecordConverter = aConverter;
	}

	public void initLogRecordConverter(
			LogRecordConverter aProbabilitiesLogRecordConverter) {
		if (!fLogRecordConverter.getClass().equals(
				aProbabilitiesLogRecordConverter.getClass())) {
			fLogRecordConverter = aProbabilitiesLogRecordConverter;
		}
	}

}
