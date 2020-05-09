package com.mfg.widget;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mfg.inputdb.prices.mdb.PriceMDBSession;
import com.mfg.utils.PropertiesEx;
import com.mfg.widget.arc.gui.IndicatorParamBean;
import com.mfg.widget.arc.strategy.MultiscaleIndicator;
import com.mfg.widget.probabilities.PLStatsProbabilitiesStorage;

/**
 * The activator class controls the plug-in life cycle
 */
public class WidgetPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.widget"; //$NON-NLS-1$

	// The shared instance
	private static WidgetPlugin plugin;

	/**
	 * The constructor
	 */
	public WidgetPlugin() {
		probabilitiesManager = new ProbabilitiesManager();
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
	 * Creates a new real-time indicator.
	 * 
	 * @param params
	 * @param sessionInfo
	 * @param snapshotEnabled
	 * @param b
	 * @return
	 */
	public static MultiscaleIndicator getIndicator(IndicatorParamBean params,
			PriceMDBSession priceSession) {
		params.setProperties(new PropertiesEx());
		return new MultiscaleIndicator(params, priceSession, 0);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static WidgetPlugin getDefault() {
		return plugin;
	}

	/**
	 * @param aProbabilitiesManager
	 *            the probabilitiesManager to set
	 */
	public void setProbabilitiesManager(
			ProbabilitiesManager aProbabilitiesManager) {
		this.probabilitiesManager = aProbabilitiesManager;
	}

	/**
	 * @return the probabilitiesManager
	 */
	public ProbabilitiesManager getProbabilitiesManager() {
		return probabilitiesManager;
	}

	private ProbabilitiesManager probabilitiesManager;

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private PLStatsProbabilitiesStorage _probsStorage;

	public PLStatsProbabilitiesStorage getProbsStorage() {
		if (_probsStorage == null) {
			_probsStorage = new PLStatsProbabilitiesStorage();
		}
		return _probsStorage;
	}

}
