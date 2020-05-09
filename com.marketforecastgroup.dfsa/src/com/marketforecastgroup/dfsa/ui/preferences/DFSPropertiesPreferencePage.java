package com.marketforecastgroup.dfsa.ui.preferences;

import static com.marketforecastgroup.dfsa.DFSAPlugin.DAYS_TO_OVERRIDE;
import static com.marketforecastgroup.dfsa.DFSAPlugin.HOURS_TO_OVERRIDE;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.marketforecastgroup.dfsa.DFSAPlugin;

/**
 * This class represents a preference page to configure what ESignalBridge host
 * JDFSA will connects.
 * 
 * @author Karell
 */

public class DFSPropertiesPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public DFSPropertiesPreferencePage() {
		super(GRID);
		setTitle("DFS Properties");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		addField(new IntegerFieldEditor(DAYS_TO_OVERRIDE, "Days to override",
				getFieldEditorParent()));
		addField(new IntegerFieldEditor(HOURS_TO_OVERRIDE, "Hours to override",
				getFieldEditorParent()));
	}

	@Override
	public void init(final IWorkbench workbench) {
		final IPreferenceStore preferenceStore = DFSAPlugin.getDefault()
				.getPreferenceStore();
		setPreferenceStore(preferenceStore);

	}

}
