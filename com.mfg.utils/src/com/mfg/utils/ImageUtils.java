/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision$: $Date$:
 * $Id$:
 */
package com.mfg.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author arian
 * 
 */
public class ImageUtils {
	private ImageUtils() {
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path. Client do not need to dispose this image. Images will be
	 * disposed automatically.
	 * 
	 * @param path
	 *            the path
	 * @return image instance
	 */
	public static ImageDescriptor getBundledImageDescriptor(String pluginId,
			String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, path);
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
	public static Image getBundledImage(AbstractUIPlugin plugin, String path) {
		Image image = plugin.getImageRegistry().get(path);
		if (image == null) {
			String pluginId = plugin.getBundle().getSymbolicName();
			plugin.getImageRegistry().put(path,
					getBundledImageDescriptor(pluginId, path));
			image = plugin.getImageRegistry().get(path);
		}
		return image;
	}
}
