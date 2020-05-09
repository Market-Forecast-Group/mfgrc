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

import java.util.UUID;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

import com.mfg.persist.interfaces.IStorageObject;

/**
 * @author arian
 * 
 */
public class Editable implements IEditable {
	private String _editorId;
	private IEditorInput _input;

	public Editable(String editorId, IEditorInput input) {
		super();
		this._editorId = editorId;
		this._input = input;
	}

	public String getEditorId() {
		return _editorId;
	}

	public void setEditorId(String editorId) {
		this._editorId = editorId;
	}

	public IEditorInput getInput() {
		return _input;
	}

	public void setInput(IEditorInput input) {
		this._input = input;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mfg.ui.editors.IEditable#openEditor()
	 */
	@Override
	public IEditorPart openEditor() throws PartInitException {
		// first, look if there is an open editor
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (final IWorkbenchWindow window : windows) {
			IEditorReference[] refs = window.getActivePage()
					.getEditorReferences();
			for (IEditorReference ref : refs) {
				if (ref.getId().equals(_editorId)
						&& ref.getEditorInput().equals(_input)) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// nothing
							}
							window.getShell().forceFocus();
						}
					});
					return ref.getEditor(true);
				}
			}
		}

		// else, open the editor in the active window
		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		IEditorPart editor = activeWindow.getActivePage().openEditor(_input,
				_editorId);

		if (_input instanceof StorageObjectEditorInput<?>) {
			IStorageObject obj = ((StorageObjectEditorInput<?>) _input)
					.getStorageObject();
			UUID uuid = obj.getUUID();

			if (editor instanceof FormEditor) {
				((FormEditor) editor).setActivePage(uuid.toString());
			}
		}
		return editor;
	}

}
