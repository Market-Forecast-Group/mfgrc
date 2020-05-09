package com.mfg.utils.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.mfg.utils.UtilsPlugin;

public class PerspectiveTrakerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public PerspectiveTrakerPreferencePage() {
		super(GRID);
		setTitle("Perspective Traker");
		setPreferenceStore(UtilsPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor perspectiveTraker = new BooleanFieldEditor(
				UtilsPlugin.PERSPECTIVE_TRAKER_ENABLED, "PerspectiveTraker",
				getFieldEditorParent());
		addField(perspectiveTraker);
	}

	@Override
	public void init(IWorkbench workbench) {
		//Adding a comment to avoid empty block warning.
	}

	@Override
	public boolean performOk() {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Perspective Traker", " Changes were applied, they will take effect after restarting.");
		return super.performOk();
	}
	
}
