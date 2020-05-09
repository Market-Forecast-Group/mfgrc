package com.mfg.connector.csv.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mfg.connector.csv.CSVPlugin;

public class CSVPrefsPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	public static final String ID = "com.mfg.connector.csv.prefs";
	public static final String PREFERENCE_CSV_DATA_FOLDER = "csv.file.path";

	public CSVPrefsPage() {
		super();
		setTitle("CSV Data Provider");
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(CSVPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(PREFERENCE_CSV_DATA_FOLDER,
				"CSV Data Folder", getFieldEditorParent()));
	}
}
