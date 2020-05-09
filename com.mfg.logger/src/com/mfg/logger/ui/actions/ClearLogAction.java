/**
 * 
 * (C) Copyright 2011 - MFG <http://www.marketforecastgroup.com/>
 * All rights reserved. This program and the accompanying materials
 * are proprietary to Giulio Rugarli.
 * 
 * $Id: $
 *
 * @author <a href="mailto:boniatillo@gmail.com">Arian Fornaris</a>, MFG
 * 
 * @version $Revision: $ $Date: $
 */

package com.mfg.logger.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.mfg.logger.ui.LogViewerAdapter;

public class ClearLogAction extends Action {
	private final LogViewerAdapter adapter;

	public ClearLogAction(LogViewerAdapter aAdapter) {
		super("Clear Table");
		this.adapter = aAdapter;
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setImageDescriptor(ImageDescriptor.createFromImage(sharedImages
				.getImage(ISharedImages.IMG_ELCL_REMOVEALL)));
	}

	@Override
	public void run() {
		adapter.clearTable();
	}
}
