package com.marketforecastgroup.dfsa.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class MaturityEditor extends FormEditor {

	public static String EDITOR_ID = "com.marketforecastgroup.dfsa.ui.editors.maturity";

	@Override
	protected void addPages() {
		try {
			addPage(new MaturityEditorPage(this, "id", getEditorInput().getName()));
			setPartName(getEditorInput().getName());
		} catch (PartInitException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		//Adding a comment to avoid empty block warning

	}

	@Override
	public void doSaveAs() {
		//Adding a comment to avoid empty block warning
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
