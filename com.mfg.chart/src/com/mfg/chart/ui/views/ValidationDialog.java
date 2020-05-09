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
package com.mfg.chart.ui.views;

import java.util.Collections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.mfg.mdb.runtime.ValidatorError;

/**
 * @author arian
 * 
 */
@SuppressWarnings("rawtypes")
public class ValidationDialog extends Dialog {
	private ListViewer listViewer;
	private java.util.List<ValidatorError> errors = Collections.emptyList();

	class MyLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			ValidatorError err = (ValidatorError) element;
			return err.getMessage();
		}
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ValidationDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
	 * .Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Validation Errors");
	}

	public void setErrors(java.util.List<ValidatorError> errors1) {
		this.errors = errors1;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));

		listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		List list = listViewer.getList();
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listViewer.setLabelProvider(new MyLabelProvider());
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setInput(errors);
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(650, 300);
	}

}
