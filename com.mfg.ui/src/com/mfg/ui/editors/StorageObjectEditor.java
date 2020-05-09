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
package com.mfg.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.persist.interfaces.IStorageObject;

/**
 * @author arian
 * 
 */
public abstract class StorageObjectEditor extends FormEditor implements
		PropertyChangeListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		updatePartFromImput();
		getEditorInput().setGetEditorActivePage(
				(p) -> Integer.valueOf(getActivePage()));
		getEditorInput().getStorageObject().addPropertyChangeListener(
				IStorageObject.PROP_NAME, this);
	}

	private void updatePartFromImput() {
		setPartName(getEditorInput().getName());
		setTitleToolTip(getEditorInput().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public StorageObjectEditorInput<?> getEditorInput() {
		return (StorageObjectEditorInput<?>) super.getEditorInput();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Adding a comment to avoid empty block warning.
	}

	@Override
	public void doSaveAs() {
		// Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Changed the the "name" property of the storage object
		updatePartFromImput();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#dispose()
	 */
	@Override
	public void dispose() {
		getEditorInput().getStorageObject().removePropertyChangeListener(
				IStorageObject.PROP_NAME, this);
		super.dispose();
	}
}
