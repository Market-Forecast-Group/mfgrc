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
package com.mfg.chart.profiles;

import org.eclipse.jface.viewers.LabelProvider;

import com.mfg.chart.ChartPlugin;

/**
 * @author arian
 * 
 */
public class ProfileLabelProvider extends LabelProvider {

	private static ProfileLabelProvider instance = new ProfileLabelProvider();

	public static ProfileLabelProvider getDefault() {
		return instance;
	}

	@Override
	public String getText(final Object element) {
		final Profile profile = (Profile) element;
		String name = profile.getName();
		String defaultProfileName = ChartPlugin.getDefault()
				.getPreferenceStore()
				.getString(ChartPlugin.PREFERENCES_PROFILES_DEFAULT);
		if (name.equals(defaultProfileName)) {
			name += " (default)";
		}
		return name;
	}
}
