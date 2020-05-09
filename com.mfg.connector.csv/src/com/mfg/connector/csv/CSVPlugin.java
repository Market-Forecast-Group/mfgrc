package com.mfg.connector.csv;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.dm.IDataProvider;

public class CSVPlugin extends AbstractUIPlugin {

	private static BundleContext context;
	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.connector.csv"; //$NON-NLS-1$

	// The shared instance
	private static CSVPlugin plugin;
	private static FileDialog openFileDialog;
	private String csvFile;
	private final CsvDataProvider fDataProvider = new CsvDataProvider();

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context1) throws Exception {
		super.start(context1);
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
	public void stop(BundleContext context1) throws Exception {
		plugin = null;
		super.stop(context1);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CSVPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns string from plug-in's resource bundle
	 * 
	 * @generated
	 */
	public static String getString(String key) {
		return Platform.getResourceString(getDefault().getBundle(), "%" + key); //$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	public void logError(String error) {
		logError(error, null);
	}

	/**
	 * @generated
	 */
	public void logError(String inError, Throwable throwable) {
		String error = inError;
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
	public void logInfo(String message) {
		logInfo(message, null);
	}

	/**
	 * @generated
	 */
	public void logInfo(String inMessage, Throwable throwable) {
		String message = inMessage;
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

	/**
	 * @param filterExtensions
	 */
	public static String showFileOpenDialog(String[] filterExtensions) {
		if (openFileDialog == null) {
			openFileDialog = new FileDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.OPEN);
		}

		String[] extensions = new String[1];
		String allFiles = new String();
		String namesText = "";
		for (int i = 0; i < filterExtensions.length; i++) {
			allFiles = allFiles + filterExtensions[i] + ";";
			if (i == filterExtensions.length - 1) {
				allFiles = allFiles + filterExtensions[0] + ";";
			}
			extensions[0] = allFiles;
			if (namesText.length() > 0) {
				namesText = namesText + "," + filterExtensions[i];
			} else {
				namesText = namesText + filterExtensions[i];
			}
		}
		String[] names = new String[] { "All Files(" + namesText + ")." };
		openFileDialog.setFilterExtensions(extensions);
		openFileDialog.setFilterNames(names);
		return openFileDialog.open();
	}

	public String getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(String csvFile1) {
		this.csvFile = csvFile1;
	}

	public IDataProvider getDataProvider() {
		return fDataProvider;
	}

}
