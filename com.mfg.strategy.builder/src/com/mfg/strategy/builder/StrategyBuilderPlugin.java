/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:gardero@gmail.com">Enrique Matos</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.strategy.builder;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.logger.application.IAppLogger;
import com.mfg.strategy.automatic.EventsPatternStrategy;
import com.mfg.strategy.automatic.eventPatterns.EventGeneral;
import com.mfg.strategy.automatic.eventPatterns.EventSortedCollection;
import com.mfg.strategy.automatic.eventPatterns.ManualEntryEvent;
import com.mfg.strategy.automatic.probabilities.EventsPatternProbabilitiesStrategy;
import com.mfg.strategy.builder.model.EventsCanvasModel;
import com.mfg.strategy.builder.model.StrategyInfo;
import com.mfg.strategy.builder.persistence.StrategyBuilderStorage;
import com.mfg.strategy.builder.utils.ObjectsJSONFileIO;
import com.mfg.utils.Utils;
import com.mfg.widget.ProbabilitiesManager;
import com.mfg.widget.WidgetPlugin;
import com.mfg.widget.probabilities.DistributionsContainer;

/**
 * The activator class controls the plug-in life cycle
 */
public class StrategyBuilderPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.strategy.builder"; //$NON-NLS-1$

	// The shared instance
	private static StrategyBuilderPlugin plugin;

	private StrategyBuilderStorage strategyStorage;


	/**
	 * The constructor
	 */
	public StrategyBuilderPlugin() {
	}


	/**
	 * @return
	 */
	public StrategyBuilderStorage getStrategiesStorage() {
		if (strategyStorage == null) {
			strategyStorage = new StrategyBuilderStorage();
		}
		return strategyStorage;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	public static StrategyBuilderPlugin getDefault() {
		return plugin;
	}


	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
	public void logError(String errorPar, Throwable throwable) {
		String error = errorPar;
		if (error == null && throwable != null) {
			error = throwable.getMessage();
		}
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, error, throwable));
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
	public void logInfo(String messagePar, Throwable throwable) {
		String message = messagePar;
		if (message == null && throwable != null) {
			message = throwable.getMessage();
		}
		getLog().log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, message, throwable));
	}


	/**
	 * Returns an image for the image file at the given plug-in relative path. Client do not need to dispose this image. Images will be disposed
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
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getBundledImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}


	public static EventsPatternStrategy createStrategyFromInfo(StrategyInfo info, IAppLogger logger) {
		EventsPatternStrategy result;

		EventsCanvasModel strategyModel = ObjectsJSONFileIO.getInstance()
				.readModelFromJSON(info.getPatternJSON());
		strategyModel.setName(info.getName());
		
		EventGeneral pattern = strategyModel.exportMe();
		if (pattern.isPure(false)) {
			EventSortedCollection pattern1 = new EventSortedCollection();
			pattern1.addEvent(new ManualEntryEvent());
			pattern1.addEvent(pattern);
			pattern = pattern1;
		}
		if (!strategyModel.isProbabilistic()) {
			if (logger != null)
				logger.logComment("Running without Probabilities");
			Utils.debug_var(12343, "---- Running without Probabilities");
			result = new EventsPatternStrategy(pattern);
			result.setStrategyName(info.getName());
		} else {
			WidgetPlugin
					.getDefault().getProbabilitiesManager();
			DistributionsContainer distributionContainer = ProbabilitiesManager.loadDistributionContainer(strategyModel.getProbabilityName());
			if (logger != null)
				logger.logComment("Running with Probabilities");
			Utils.debug_var(12344, "++++ Running with Probabilities");
			EventsPatternProbabilitiesStrategy probPattern = new EventsPatternProbabilitiesStrategy(pattern);
			probPattern.setDistribution(distributionContainer);
			probPattern.setStrategyName(info.getName());
			result = probPattern;
		}
		assignNodeIDs(result.getEventPattern(), -1);
		return result;
	}


	private static int assignNodeIDs(EventGeneral result, int id) {
		int id2 = id + 1;
		result.setNodeID(id2);
		for (EventGeneral event : result.getChildren()) {
			id2 = assignNodeIDs(event, id2);
		}
		return id2;
	}

}
