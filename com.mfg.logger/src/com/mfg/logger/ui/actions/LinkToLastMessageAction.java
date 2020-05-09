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

public class LinkToLastMessageAction extends Action {

	private final LogViewerAdapter adapter;

	public LinkToLastMessageAction(LogViewerAdapter aAdapter) {
		super("Link to last message");
		this.adapter = aAdapter;
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setImageDescriptor(ImageDescriptor.createFromImage(sharedImages
				.getImage(ISharedImages.IMG_ELCL_SYNCED)));
		setChecked(aAdapter.isLinkedToLastMessage());
	}

	@Override
	public void run() {
		adapter.setLinkedToLastMessage(isChecked());
	}

}
