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

package com.mfg.persist.interfaces;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author arian
 * 
 */
public class SaveWorkspaceAction extends Action {
	private static final SaveWorkspaceAction instance = new SaveWorkspaceAction();

	/**
	 * 
	 */
	public SaveWorkspaceAction() {
		super("Save Workspace");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));

		PersistInterfacesPlugin.getDefault().addPropertyChangeListener(
				PersistInterfacesPlugin.PROP_WORKSPACE_DIRTY,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						SaveWorkspaceAction.this
								.setEnabled(PersistInterfacesPlugin
										.getDefault().isWorkspaceDirty());
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		PersistInterfacesPlugin.getDefault().saveWorkspace();
	}

	/**
	 * @return
	 */
	public static SaveWorkspaceAction getDefault() {
		return instance;
	}
}
