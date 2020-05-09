
package com.mfg.chart.model;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ChartModelPlugin implements BundleActivator {
	public static final String LOGGER_COMPONENT_ID = "com.mfg.chart.model.sessionComponent";
	private static BundleContext context;


	static BundleContext getContext() {
		return context;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		ChartModelPlugin.context = bundleContext;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		ChartModelPlugin.context = null;
	}

}
