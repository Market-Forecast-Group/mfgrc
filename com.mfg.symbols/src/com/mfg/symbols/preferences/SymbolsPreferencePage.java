package com.mfg.symbols.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mfg.symbols.SymbolsPlugin;

public class SymbolsPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public SymbolsPreferencePage() {
		super(GRID);
		setPreferenceStore(SymbolsPlugin.getDefault().getPreferenceStore());
		setDescription("Symbols and Trading");
	}

	@Override
	public void init(IWorkbench workbench) {
		// Adding a comment to avoid empty block warning.
	}

	@Override
	protected void createFieldEditors() {
		IntegerFieldEditor editor;
		addField(editor = new IntegerFieldEditor(
				SymbolsPlugin.PREF_MAX_NUM_SNAPSHOTS,
				"Max Number Of Snapshots", getFieldEditorParent()));
		editor.setValidRange(1, 50);

		addField(new BooleanFieldEditor(SymbolsPlugin.EMBEDDED_TEA,
				"connect to embedded TEA?", getFieldEditorParent()));
	}

}
