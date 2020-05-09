/*
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 *
 * $Id: $
 */
/**
 * @author <a href="mailto:ramzy.arfawi@gmail.com">Ramzy ARFAWI</a>, MFG
 *
 * @version $Revision: $ $Date: $
 */

package com.mfg.broker;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class BrokerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.mfg.broker"; //$NON-NLS-1$

	public static final String PREF_BROKER_LIST = "brokerList";

	public static final String PREF_BROKER_ACCOUNTS = "brokerAccounts";

	// The shared instance
	private static BrokerPlugin plugin;

	/**
	 * The constructor
	 */
	public BrokerPlugin() {
	}

	/**
	 * Get the accounts defined in preferences.
	 * 
	 * @param preferenceKey
	 *            The preference key. One of:
	 *            {@link BrokerPlugin#PREFERENCES_ACCOUNTS_LONG_AND_SHORT},
	 *            {@link BrokerPlugin#PREFERENCES_ACCOUNTS_ONLY_LONG} and
	 *            {@link BrokerPlugin#PREFERENCES_ACCOUNTS_ONLY_SHORT}
	 * @return
	 */
	public String[] getPreferenceStringList(String preferenceKey) {
		return parsePreferenceStringList(getPreferenceStore().getString(
				preferenceKey));
	}

	/**
	 * @param stringList
	 * @return
	 */
	public static String[] parsePreferenceStringList(String stringList) {
		return stringList.trim().split("\n");
	}

	/**
	 * @param items
	 * @return
	 */
	public static String createAccountList(String[] items) {
		StringBuilder sb = new StringBuilder();
		for (String str : items) {
			sb.append(str + "\n");
		}
		return sb.toString().trim();
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

		IPreferenceStore store = getPreferenceStore();
		store.setDefault(PREF_BROKER_ACCOUNTS, "Account1");
		store.setDefault(PREF_BROKER_LIST, "Broker1");
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
	public static BrokerPlugin getDefault() {
		return plugin;
	}
}
