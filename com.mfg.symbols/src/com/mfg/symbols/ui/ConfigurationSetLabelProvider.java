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
package com.mfg.symbols.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.mfg.symbols.SymbolsPlugin;

/**
 * @author arian
 * 
 */
public class ConfigurationSetLabelProvider extends LabelProvider {
	@Override
	public Image getImage(Object element) {
		SymbolsPlugin.getDefault().getSetsManager();
		Image img = ConfigurationSetsManager.getImage(((Integer) element)
				.intValue());
		return img;
	}

	@Override
	public String getText(Object element) {
		return "Set " + element;
	}
}
