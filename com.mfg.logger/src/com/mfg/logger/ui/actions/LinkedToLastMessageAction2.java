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
package com.mfg.logger.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.mfg.logger.ui.LogViewerManager;

/**
 * @author arian
 * 
 */
public class LinkedToLastMessageAction2 extends Action {
	private final LogViewerManager manager;

	public LinkedToLastMessageAction2(LogViewerManager aManager) {
		super();
		this.manager = aManager;
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setImageDescriptor(ImageDescriptor.createFromImage(sharedImages
				.getImage(ISharedImages.IMG_ELCL_SYNCED)));
		setChecked(aManager.isLinkedToLastMessage());
		setText("Linked to Last Message");
		setToolTipText("Linked to Last Message");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		boolean linked = !manager.isLinkedToLastMessage();
		manager.setLinkedToLastMessage(linked);
		setChecked(linked);
		manager.scrollToIndex(0);
	}
}
