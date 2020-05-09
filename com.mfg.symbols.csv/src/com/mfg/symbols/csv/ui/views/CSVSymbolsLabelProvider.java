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
package com.mfg.symbols.csv.ui.views;

import org.eclipse.swt.graphics.Image;

import com.mfg.symbols.SymbolsPlugin;
import com.mfg.symbols.csv.CSVSymbolPlugin;
import com.mfg.symbols.csv.configurations.CSVConfiguration;
import com.mfg.symbols.csv.configurations.CSVSymbolData2;
import com.mfg.symbols.ui.widgets.SymbolsLabelProvider;
import com.mfg.utils.ImageUtils;

/**
 * @author arian
 * 
 */
public class CSVSymbolsLabelProvider extends SymbolsLabelProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mfg.symbols.ui.widgets.SymbolsLabelProvider#getImage(java.lang.Object
	 * )
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof CSVSymbolData2) {
			return ImageUtils.getBundledImage(CSVSymbolPlugin.getDefault(),
					CSVSymbolPlugin.CSV_SYMBOL_IMAGE_PATH);
		} else if (element instanceof CSVConfiguration) {
			return ImageUtils.getBundledImage(SymbolsPlugin.getDefault(),
					SymbolsPlugin.SYMBOL_CONFIG_IMAGE_PATH);
		}
		return super.getImage(element);
	}
}
