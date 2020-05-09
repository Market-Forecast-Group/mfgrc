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

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

import com.mfg.logger.ui.views.ILogClient;
import com.mfg.logger.ui.views.ILogView;

/**
 * @author arian
 * 
 */
public class ShowLogClientsAction extends Action {
	private final ILogView view;
	private String dialogTitle;
	private String dialogMessage;

	public ShowLogClientsAction(ILogView aView) {
		super("Show Log Clients");
		this.view = aView;
		dialogTitle = "Show Log Clients";
		dialogMessage = "Log Clients";
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String aDialogTitle) {
		this.dialogTitle = aDialogTitle;
	}

	public String getDialogMessage() {
		return dialogMessage;
	}

	public void setDialogMessage(String aDialogMessage) {
		this.dialogMessage = aDialogMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		ListDialog dialog = new ListDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());

		dialog.setLabelProvider(new LabelProvider() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object
			 * )
			 */
			@Override
			public Image getImage(Object element) {
				return ((ILogClient) element).getImage();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return ((ILogClient) element).getName();
			}
		});
		dialog.setContentProvider(new ArrayContentProvider());
		dialog.setInput(getClients());
		dialog.setAddCancelButton(false);
		dialog.setTitle(getDialogTitle());
		dialog.setMessage(getDialogMessage());
		dialog.setInitialSelections(new Object[0]);
		dialog.open();
	}

	public List<ILogClient> getClients() {
		return view.getLoggerClients();
	}
}
