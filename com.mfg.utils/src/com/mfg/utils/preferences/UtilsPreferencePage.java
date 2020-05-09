package com.mfg.utils.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mfg.utils.UtilsPlugin;

public class UtilsPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public UtilsPreferencePage() {
		super(GRID);
		setPreferenceStore(UtilsPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		//Adding a comment to avoid empty block warning.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(UtilsPlugin.PREFERENCES_STD_DEBUG,
				"Show debug message in std output", getFieldEditorParent()));
		// addField(new BooleanFieldEditor(
		// UtilsPlugin.PREFERENCES_CREATE_LOG_FILE,
		// "Create a log file (in logs dir)", getFieldEditorParent()));
	}

}
