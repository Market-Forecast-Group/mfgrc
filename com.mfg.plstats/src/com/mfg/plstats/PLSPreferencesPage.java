package com.mfg.plstats;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PLSPreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PLSPreferencesPage() {
		setTitle("Pre-Learning Statistics");
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(PLStatsPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		//Adding some comment to avoid empty block warning.
	}

}
