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
package com.mfg.symbols.ui.widgets;

import java.util.Comparator;

import com.mfg.persist.interfaces.IStorageObject;
import com.mfg.symbols.SymbolsPlugin;

/**
 * @author arian
 * 
 */
public class ConfigurationFullnameComparator implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 instanceof IStorageObject && o2 instanceof IStorageObject) {
			String name1 = SymbolsPlugin.getDefault().getFullConfigurationName(
					(IStorageObject) o1);
			String name2 = SymbolsPlugin.getDefault().getFullConfigurationName(
					(IStorageObject) o2);
			return name1.compareTo(name2);
		}
		return 0;
	}

}
