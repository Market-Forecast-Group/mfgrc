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
package com.mfg.ui.views;

import org.eclipse.jface.viewers.LabelProvider;

import com.mfg.persist.interfaces.IStorageObject;

/**
 * @author arian
 * 
 */
public class StorageObjectsLabelProvider extends LabelProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IStorageObject) {
			return ((IStorageObject) element).getName();
		}
		return super.getText(element);
	}
}
