/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:jgasmi@gmail.com">Jamel Gasmi</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.dm;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.mfg.utils.PropertiesEx;

/**
 * The activator class controls the plug-in life cycle
 */
public class DMPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "com.mfg.dm";

	// public static final String ENABLED_CACHE_EXPANDER =
	// "CACHE_EXPANDER_ENABLED";

	// public static final String FILL_GAP_FILTER_ENHANCEMENT =
	// "FILL_GAP_FILTER_ENHANCEMENT";

	private static DMPlugin plugin;

	private PropertiesEx fProperties = new PropertiesEx();

	/**
	 * The constructor
	 */
	public DMPlugin() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// getPreferenceStore().setDefault(FILL_GAP_FILTER_ENHANCEMENT, 1);

	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static DMPlugin getDefault() {
		return plugin;
	}

	/**
	 * You should use this only to use the data providers out of the Eclipse
	 * platform.
	 * 
	 * @author arian
	 * @param plugin1
	 *            the plugin to set
	 */
	public static void setDefault(DMPlugin plugin1) {
		DMPlugin.plugin = plugin1;
		plugin1.fProperties = new PropertiesEx();
		plugin1.fProperties.setProperty("PrintBarFiles", "false");
		plugin1.fProperties.setProperty("PrintRawTicks", "false");
		plugin1.fProperties.setProperty("SaveUnfinishedBars", "false");
		plugin1.fProperties.setProperty("WarmUpStatisticsWritten", "false");
		plugin1.fProperties.setProperty("SaveWarmUpSeries", "false");
		plugin1.fProperties.setProperty("LoadSavedWarmUp", "false");
	}

	public static Display getStandardDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}

	public static String getString(final String key) {
		return Platform.getResourceString(getDefault().getBundle(), "%" + key); //$NON-NLS-1$
	}

	public static ImageDescriptor getImageDescriptor(
			final IConfigurationElement element, final String attr) {
		final Bundle bundle = Platform.getBundle(element.getContributor()
				.getName());
		final String iconPath = element.getAttribute(attr);
		if (iconPath != null) {
			final URL iconURL = FileLocator.find(bundle, new Path(iconPath),
					null);
			if (iconURL != null) {
				return ImageDescriptor.createFromURL(iconURL);
			}
		}
		return null;
	}

	/**
	 * @generated
	 */
	public void logError(final String error) {
		logError(error, null);
	}

	/**
	 * @generated
	 */
	public void logError(String aError, final Throwable throwable) {
		String error = aError;
		if (error == null && throwable != null) {
			error = throwable.getMessage();
		}
		getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, error,
						throwable));
	}

	/**
	 * @generated
	 */
	public void logInfo(final String message) {
		logInfo(message, null);
	}

	/**
	 * @generated
	 */
	public void logInfo(String aMessage, final Throwable throwable) {
		String message = aMessage;
		if (message == null && throwable != null) {
			message = throwable.getMessage();
		}
		getLog().log(
				new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, message,
						throwable));
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
	public Image getBundledImage(final String path) {
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
	public static ImageDescriptor getBundledImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
